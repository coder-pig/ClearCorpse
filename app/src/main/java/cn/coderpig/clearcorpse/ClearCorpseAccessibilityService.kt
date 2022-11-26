package cn.coderpig.clearcorpse

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.*
import android.view.accessibility.AccessibilityNodeInfo
import java.lang.StringBuilder

/**
 * @Author CoderPig
 * @Desc:
 * @CreateDate: Create in 2022/11/19 17:18
 */
class ClearCorpseAccessibilityService : AccessibilityService() {
    companion object {
        const val LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI"  // 首页
        const val CONTACT_INFO_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI"    // 联系人信息
        const val CHATTING_UI = "com.tencent.mm.ui.chatting.ChattingUI"    // 聊天页
        const val REMITTANCE_UI = "com.tencent.mm.plugin.remittance.ui.RemittanceUI" // 转账
        const val NO_FRIEND_DIALOG = "com.tencent.mm.ui.widget.dialog.f"   // 非好友对话框

        // 非还有对话框，text=你不是收款方好友，对方添加你为好友后才能发起转账, 我知道了
        const val CONTACT_LIST_ID = "js"
        const val CONTACT_ITEM_ID = "hg4"
        const val CONTACT_WX_NAME = "bq1"
        const val CHATTING_MORE_ID = "b3q"
        const val REMITTANCE_NICKNAME_ID = "inh"
        const val REMITTANCE_EDIT_ID = "lg_"
        const val REMITTANCE_CLICK_ID = "ffp"
    }

    private var mCurContact: Contact? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        logD("$event")
        when (event.eventType) {
            TYPE_WINDOW_STATE_CHANGED -> {
                when (event.className.toString()) {
                    LAUNCHER_UI -> {
                        event.source?.let { source -> source.getNodeByText("通讯录").click() }
                    }
                    CONTACT_INFO_UI -> {
                        event.source?.let { source ->
                            mCurContact!!.wxName = source.getNodeByText(wxNodeId(CONTACT_WX_NAME)).text()
                            mCurContact!!.wxNum = source.getNodeByText("微信号").text().replace("微信号:", "").trim()
                            gestureClick(source.getNodeByText("发消息")?.parent)
                        }
                    }
                    REMITTANCE_UI -> {
                        event.source?.let { source ->
                            sleep(200)
                            val nickName = source.getNodeById(wxNodeId(REMITTANCE_NICKNAME_ID))
                            if (nickName != null) {
                                if (nickName.text().indexOf("(") != -1 && nickName.text().indexOf(")") != -1) {
                                    logD("好友关系正常")
                                    repeat(5) {
                                        back()
                                        sleep(500)
                                    }
                                } else {
                                    logD("好友关系异常，假转账法确认关系")
                                    source.getNodeById(wxNodeId(REMITTANCE_EDIT_ID))?.input("0.01")
                                    source.getNodeById(wxNodeId(REMITTANCE_CLICK_ID))?.click()
                                }
                            }
                        }
                    }
                    NO_FRIEND_DIALOG -> {
                        event.source?.let { source ->
                            logD("好友关系：${event.text}")
                            source.getNodeByText("我知道了").click()
                            repeat(3) {
                                back()
                                sleep(500)
                            }
                        }
                    }
                }
            }
            TYPE_VIEW_CLICKED -> {
                if (event.text[0] == "通讯录") {
                    // 这里不能用event的getSource()，只能获取到发生改变的节点，需要调用getRootInActiveWindow()获得所有结点
                    rootInActiveWindow?.let { source ->
                        val contactList = source.getNodeById(wxNodeId(CONTACT_LIST_ID))
                        if (contactList != null) {
                            if (mCurContact == null) {
                                mCurContact = Contact()
                                contactList.getNodesById(wxNodeId(CONTACT_ITEM_ID))?.get(0).click()
                            } else {
                                val contactNodes = contactList.getNodesById(wxNodeId(CONTACT_ITEM_ID))
                                contactNodes?.forEachIndexed { index, node ->
                                    if (node.text() ==  mCurContact!!.wxName) {
                                        if (index + 1 >= contactNodes.size) {
                                            contactList.scrollBackward()
                                        } else {

                                        }
                                        contactList.getNodesById(wxNodeId(CONTACT_ITEM_ID))?.get(index + 1).click()
                                    }
                                }
                            }
                        } else {
                            logD("未能获取好友列表")
                        }
                    }
                }
                if (event.text[0] == "发消息") {
                    sleep(200)
                    rootInActiveWindow?.let { source ->
                        source.getNodeById(wxNodeId(CHATTING_MORE_ID)).click()
                    }
                }
                if (event.text[0] == "更多功能按钮，已折叠") {
                    sleep(100)
                    rootInActiveWindow?.let { source ->
                        gestureClick(source.getNodeByText("转账")?.parent)
                    }
                }
            }
            else -> logD("$event")
        }
    }


    override fun onInterrupt() {
    }


    data class Contact(
        var wxName: String? = null,
        var wxNum: String? = null,
        var status: String? = null
    )

}