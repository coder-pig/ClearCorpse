package cn.coderpig.clearcorpse

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author CoderPig
 * @Desc:
 * @CreateDate: Create in 2022/11/19 17:30
 */
class ClearWxCorpseActivity : AppCompatActivity() {
    private lateinit var mAdapter: FriendAdapter
    private val mData = arrayListOf(
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试",
        "测试"
    )
    private lateinit var mReTestBt: Button
    private lateinit var mContentRv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clear_wx_corpse)
        mReTestBt = findViewById(R.id.bt_retest)
        mContentRv = findViewById(R.id.rv_content)
        initView()
    }

    private fun initView() {
        mContentRv.apply {
            layoutManager = LinearLayoutManager(this@ClearWxCorpseActivity)
            mAdapter = FriendAdapter(this@ClearWxCorpseActivity, mData)
            adapter = mAdapter
        }
        mReTestBt.setOnClickListener {
            startApp("com.tencent.mm", "com.tencent.mm.ui.LauncherUI", "未安装微信")
        }
    }
}


class FriendAdapter(private val context: Context, private val data: ArrayList<String>) :
    RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_friend_status, parent, false)
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data[position].let {
            holder.apply {
                val splitList = it.split("^")
                if (splitList.size == 2) {
                    posTv.text = "${position + 1}"
                    nameTv.text = "${splitList[0]}"
                    statusTv.text = "${splitList[1]}"
                }
            }
        }
    }

    override fun getItemCount() = data.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posTv: TextView = itemView.findViewById(R.id.tv_friend_pos)
        val nameTv: TextView = itemView.findViewById(R.id.tv_friend_name)
        val statusTv: TextView = itemView.findViewById(R.id.tv_friend_status)
    }

}