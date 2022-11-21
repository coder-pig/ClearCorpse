package cn.coderpig.clearcorpse

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

/**
 * @Author CoderPig
 * @Desc:
 * @CreateDate: Create in 2022/11/19 17:15
 */

/**
 * 检测无障碍服务是否开启
 * */
fun Context.isAccessibilitySettingsOn(clazz: Class<out AccessibilityService?>): Boolean {
    var accessibilityEnabled = false    // 判断设备的无障碍功能是否可用
    try {
        accessibilityEnabled = Settings.Secure.getInt(
            applicationContext.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        ) == 1
    } catch (e: Settings.SettingNotFoundException) {
        e.printStackTrace()
    }
    val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
    if (accessibilityEnabled) {
        // 获取启用的无障碍服务
        val settingValue: String? = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        if (settingValue != null) {
            // 遍历判断是否包含我们的服务
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessibilityService = mStringColonSplitter.next()
                if (accessibilityService.equals(
                        "${packageName}/${clazz.canonicalName}",
                        ignoreCase = true
                    )
                ) return true

            }
        }
    }
    return false
}

@SuppressLint("UseCompatLoadingForDrawables")
fun Context.getDrawableRes(resId: Int): Drawable =
    applicationContext.resources.getDrawable(resId, null)

fun Context.getStringRes(resId: Int): String = applicationContext.resources.getString(resId, "")

fun Context.shortToast(msg: String) =
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

fun Context.longToast(msg: String) =
    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()


const val TAG = "清除微信🧟好友"
fun logD(content: String) {
    Log.d(TAG, content)
}

fun AccessibilityNodeInfo.getNodeById(id: String): AccessibilityNodeInfo? {
    var node: AccessibilityNodeInfo? = null
    findAccessibilityNodeInfosByViewId(id).let {
        if (!it.isNullOrEmpty()) node = it[0]
    }
    return node
}

fun AccessibilityNodeInfo.getNodesById(id: String): ArrayList<AccessibilityNodeInfo> {
    val nodes = arrayListOf<AccessibilityNodeInfo>()
    findAccessibilityNodeInfosByViewId(id)?.forEach { nodes.add(it) }
    return nodes
}

fun AccessibilityNodeInfo.getNodeByText(text: String): AccessibilityNodeInfo? {
    var node: AccessibilityNodeInfo? = null
    findAccessibilityNodeInfosByText(text).let {
        if (!it.isNullOrEmpty()) node = it[0]
    }
    return node
}

fun AccessibilityNodeInfo.getNodesByText(text: String): ArrayList<AccessibilityNodeInfo> {
    val nodes = arrayListOf<AccessibilityNodeInfo>()
    findAccessibilityNodeInfosByText(text)?.forEach { nodes.add(it) }
    return nodes
}


fun AccessibilityNodeInfo?.text(): String {
    return this?.text?.toString() ?: ""
}


/**
 * 点击，迭代能点击的父节点
 * */
fun AccessibilityNodeInfo?.click() {
    if (this == null) return
    var clickNode = this
    while (true) {
        if (clickNode!!.isClickable) {
            logD("点击的节点：${clickNode}")
            clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return
        } else {
            clickNode = clickNode.parent
            if (clickNode == null) return
        }
    }
}

// 长按
fun AccessibilityNodeInfo.longClick() =
    performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)

// 向下滑动一下
fun AccessibilityNodeInfo.scrollForward() =
    performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)

// 向上滑动一下
fun AccessibilityNodeInfo.scrollBackward() =
    performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)

// 填充文本
fun AccessibilityNodeInfo.input(content: String) = performAction(
    AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content)
    }
)

fun sleep(second: Int) {
    Thread.sleep((second * 500).toLong())
}
