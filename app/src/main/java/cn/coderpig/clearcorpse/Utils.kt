package cn.coderpig.clearcorpse

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import java.lang.Exception
import java.lang.StringBuilder

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


/**
 * 跳转其它APP
 * @param packageName 跳转APP包名
 * @param activityName 跳转APP的Activity名
 * @param errorTips 跳转页面不存在时的提示
 * */
fun Context.startApp(packageName: String, activityName: String, errorTips: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName(packageName, activityName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    } catch (e: ActivityNotFoundException) {
        shortToast(errorTips)
    } catch (e: Exception) {
        e.message?.let { logD(it) }
    }
}

/**
 * 跳转其它APP
 * @param urlScheme URL Scheme请求字符串
 * @param errorTips 跳转页面不存在时的提示
 * */
fun Context.startApp(urlScheme: String, errorTips: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlScheme)))
    } catch (e: ActivityNotFoundException) {
        shortToast(errorTips)
    } catch (e: Exception) {
        e.message?.let { logD(it) }
    }
}

/**
 * 根据id查找单个节点
 * @param id 控件id
 * @return 对应id的节点
 * */
fun AccessibilityNodeInfo.getNodeById(id: String): AccessibilityNodeInfo? {
    var count = 0
    while (count < 10) {
        findAccessibilityNodeInfosByViewId(id).let {
            if (!it.isNullOrEmpty()) return it[0]
        }
        sleep(100)
        count++
    }
    return null
}

/**
 * 根据id查找多个节点
 * @param id 控件id
 * @return 对应id的节点列表
 * */
fun AccessibilityNodeInfo.getNodesById(id: String): List<AccessibilityNodeInfo>? {
    var count = 0
    while (count < 10) {
        findAccessibilityNodeInfosByViewId(id).let {
            if (!it.isNullOrEmpty()) return it
        }
        sleep(100)
        count++
    }
    return null
}

/**
 * 根据文本查找单个节点
 * @param text 匹配文本
 * @param allMatch 是否全匹配，默认false，contains()方式的匹配
 * @return 匹配文本的节点
 * */
fun AccessibilityNodeInfo.getNodeByText(
    text: String,
    allMatch: Boolean = false
): AccessibilityNodeInfo? {
    var count = 0
    while (count < 10) {
        findAccessibilityNodeInfosByText(text).let {
            if (!it.isNullOrEmpty()) {
                if (allMatch) {
                    it.forEach { node -> if (node.text == text) return node }
                } else {
                    return it[0]
                }
            }
            sleep(100)
            count++
        }
    }
    return null
}

/**
 * 根据文本查找多个节点
 * @param text 匹配文本
 * @param allMatch 是否全匹配，默认false，contains()方式的匹配
 * @return 匹配文本的节点列表
 * */
fun AccessibilityNodeInfo.getNodesByText(
    text: String,
    allMatch: Boolean = false
): List<AccessibilityNodeInfo>? {
    var count = 0
    while (count < 10) {
        findAccessibilityNodeInfosByText(text).let {
            if (!it.isNullOrEmpty()) {
                return if (allMatch) {
                    val tempList = arrayListOf<AccessibilityNodeInfo>()
                    it.forEach { node -> if (node.text == text) tempList.add(node) }
                    if (tempList.isEmpty()) null else tempList
                } else {
                    it
                }
            }
            sleep(100)
            count++
        }
    }
    return null
}

/**
 * 获取结点的文本
 * */
fun AccessibilityNodeInfo?.text(): String {
    return this?.text?.toString() ?: ""
}


/**
 * 点击，迭代能点击的父节点
 * */
fun AccessibilityNodeInfo?.click() {
    if (this == null) return
    if (this.isClickable) {
        this.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        return
    } else {
        this.parent.click()
    }
}

// 长按
fun AccessibilityNodeInfo?.longClick() {
    if (this == null) return
    if (this.isClickable) {
        this.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)
        return
    } else {
        this.parent.longClick()
    }
}

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

fun sleep(millisecond: Long) {
    Thread.sleep((millisecond))
}

// 遍历所有结点的方法
fun fullPrintNode(
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

/**
 * 遍历打印结点
 * */
fun AccessibilityNodeInfo?.fullPrintNode(
    tag: String,
    spaceCount: Int = 0
) {
    if (this == null) return
    val spaceSb = StringBuilder().apply { repeat(spaceCount) { append("  ") } }
    logD("$tag: $spaceSb$text | $viewIdResourceName | $className | Clickable: $isClickable")
    if (childCount == 0) return
    for (i in 0 until childCount) getChild(i).fullPrintNode(tag, spaceCount + 1)
}

const val WX_PKG_NAME = "com.tencent.mm"
fun wxNodeId(id: String)  = "$WX_PKG_NAME:id/$id"