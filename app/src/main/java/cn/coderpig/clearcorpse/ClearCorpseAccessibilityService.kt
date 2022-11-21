package cn.coderpig.clearcorpse

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
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
        const val REMITTANCE_UI = "com.tencent.mm.plugin.remittance.ui.RemittanceUI" // 转账
        const val PAY_DIALOG = "com.tencent.mm.ui.widget.dialog.j" // 支付对话框，text=微信支付
        const val NO_FRIEND_DIALOG = "com.tencent.mm.ui.widget.dialog.f"   // 非好友对话框

        // 非还有对话框，text=你不是收款方好友，对方添加你为好友后才能发起转账, 我知道了
        const val CONTACT_LIST_ID = "com.tencent.mm:id/js"
        const val CONTACT_ITEM_ID = "com.tencent.mm:id/hg4"
        const val CONTACT_FOOTER_ID = "com.tencent.mm:id/bmm "
        const val CONTACT_INFO_NICKNAME_ID = "com.tencent.mm:id/bq1"   // 昵称
        const val CONTACT_INFO_WX_NO_ID = "com.tencent.mm:id/bq9"   // 昵称
        const val CHATTING_MORE_ID = "com.tencent.mm:id/b3q"   // 昵称
    }

    private var mContactTempMap = hashMapOf<String, Contact>()
    private var mCurContact: Contact? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == TYPE_WINDOW_STATE_CHANGED) {
            when (event.className.toString()) {
                LAUNCHER_UI -> {
                    fullPrintNode("首页", event.source)
                    event.source?.let { source ->
                        source.getNodeByText("通讯录").click()
                        source.getNodeById(CONTACT_LIST_ID)?.apply {
                            getNodesById(CONTACT_ITEM_ID).forEach {
                                it.click()
                            }
                        }
                    }
                }
                CONTACT_INFO_UI -> {
                    fullPrintNode("联系人信息页", event.source)
                    event.source?.let { source ->
                        mCurContact = Contact()
                        mCurContact!!.nickName = source.getNodeById(CONTACT_INFO_NICKNAME_ID).text()
                        mCurContact!!.wxNum =
                            source.getNodeById(CONTACT_INFO_WX_NO_ID).text().replace("微信号:  ", "")
                        source.getNodeByText("发消息")?.parent?.click()
                        sleep(1)
                        rootInActiveWindow.apply { getNodeById(CHATTING_MORE_ID).click(); recycle() }
                        sleep(1)
                        fullPrintNode("聊天页", rootInActiveWindow)
                        rootInActiveWindow.apply { getNodeByText("转账").click(); recycle() }
                    }
                }
                REMITTANCE_UI -> {
                    fullPrintNode("转账页", event.source)
                }
                PAY_DIALOG -> {
                    fullPrintNode("支付对话框", event.source)
                }
                NO_FRIEND_DIALOG -> {
                    fullPrintNode("非好友对话框", event.source)
                }
            }
        } else {
            logD("$event")
            fullPrintNode("其他：", event.source)
        }
    }

    // 遍历所有结点的方法
    private fun fullPrintNode(
        tag: String,
        parentNode: AccessibilityNodeInfo?,
        spaceCount: Int = 0
    ): AccessibilityNodeInfo? {
        if (parentNode == null) return null
        val spaceSb = StringBuilder().apply { repeat(spaceCount) { append("  ") } }
        logD("$tag: $spaceSb${parentNode.text} → ${parentNode.viewIdResourceName} → ${parentNode.className} → Clickable: ${parentNode.isClickable}")
        if (parentNode.childCount == 0) return null
        for (i in 0 until parentNode.childCount) {
            fullPrintNode(tag, parentNode.getChild(i), spaceCount + 1)
        }
        return null
    }

    override fun onInterrupt() {
    }

    data class Contact(
        var nickName: String? = null,
        var wxNum: String? = null,
        var status: RelationStatus? = RelationStatus.NORMAL,
    )

    enum class RelationStatus {
        NORMAL, DELETED, BLACKLIST
    }
}