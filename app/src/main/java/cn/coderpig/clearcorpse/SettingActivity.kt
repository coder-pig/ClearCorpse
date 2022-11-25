package cn.coderpig.clearcorpse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class SettingActivity : AppCompatActivity() {
    private lateinit var mServiceStatusIv: ImageView
    private lateinit var mServiceStatusTv: TextView
    private lateinit var mToClearBt: Button
    private val mClickListener = View.OnClickListener {
        when (it.id) {
            R.id.iv_service_status -> {
                if (isAccessibilitySettingsOn(ClearCorpseAccessibilityService::class.java)) {
                    shortToast(getStringRes(R.string.service_is_enable_tips))
                } else {
                    longToast(getStringRes(R.string.service_enable_step_tips))
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            }
            R.id.bt_to_clear -> startActivity(Intent(this, ClearWxCorpseActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initView()
    }

    private fun initView() {
        mServiceStatusIv = findViewById(R.id.iv_service_status)
        mServiceStatusTv = findViewById(R.id.tv_service_status)
        mToClearBt = findViewById(R.id.bt_to_clear)
        mServiceStatusIv.setOnClickListener(mClickListener)
        mToClearBt.setOnClickListener(mClickListener)
    }

    override fun onResume() {
        super.onResume()
        if (isAccessibilitySettingsOn(ClearCorpseAccessibilityService::class.java)) {
            mServiceStatusIv.setImageDrawable(getDrawableRes(R.drawable.ic_service_enable))
            mServiceStatusTv.text = getStringRes(R.string.service_status_enable)
            mToClearBt.visibility = View.VISIBLE
        } else {
            mServiceStatusIv.setImageDrawable(getDrawableRes(R.drawable.ic_service_disable))
            mServiceStatusTv.text = getStringRes(R.string.service_status_disable)
            mToClearBt.visibility = View.GONE
        }
    }

}