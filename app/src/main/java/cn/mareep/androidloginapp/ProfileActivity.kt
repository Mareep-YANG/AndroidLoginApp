package cn.mareep.androidloginapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class ProfileActivity : Activity() {

    // SharedPreferences 常量
    companion object {
        private const val PREF_NAME = "user_profile"
        private const val KEY_USERNAME = "username"
        private const val KEY_SIGNATURE = "signature"
    }

    private lateinit var textUsername: TextView
    private lateinit var textSignature: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 初始化 View
        textUsername = findViewById(R.id.text_username)
        textSignature = findViewById(R.id.text_signature)
        val layoutAvatar: LinearLayout = findViewById(R.id.layout_avatar)
        val layoutUsername: LinearLayout = findViewById(R.id.layout_username)
        val layoutSignature: LinearLayout = findViewById(R.id.layout_signature)
        val layoutPersonalInfo: LinearLayout = findViewById(R.id.layout_personal_info)
        val layoutFavorites: LinearLayout = findViewById(R.id.layout_favorites)
        val layoutHistory: LinearLayout = findViewById(R.id.layout_history)
        val layoutAboutUs: LinearLayout = findViewById(R.id.layout_about_us)
        val layoutFeedback: LinearLayout = findViewById(R.id.layout_feedback)
        val buttonLogout: Button = findViewById(R.id.button_logout)

        // 加载用户数据
        loadUserData()

        // 头像点击事件
        layoutAvatar.setOnClickListener {
            Toast.makeText(this, "头像: 点击更换头像", Toast.LENGTH_SHORT).show()
        }

        // 用户名点击事件
        layoutUsername.setOnClickListener {
            showEditDialog("用户名", textUsername.text.toString()) { newValue ->
                saveUsername(newValue)
                textUsername.text = newValue
                Toast.makeText(this, "用户名已更新: $newValue", Toast.LENGTH_SHORT).show()
            }
        }

        // 签名点击事件
        layoutSignature.setOnClickListener {
            showEditDialog("个性签名", textSignature.text.toString()) { newValue ->
                saveSignature(newValue)
                textSignature.text = newValue
                Toast.makeText(this, "签名已更新: $newValue", Toast.LENGTH_SHORT).show()
            }
        }

        // 个人信息点击事件
        layoutPersonalInfo.setOnClickListener {
            Toast.makeText(this, "个人信息: 查看详细个人资料", Toast.LENGTH_SHORT).show()
        }

        // 我的收藏点击事件
        layoutFavorites.setOnClickListener {
            Toast.makeText(this, "我的收藏: 查看收藏的内容", Toast.LENGTH_SHORT).show()
        }

        // 浏览历史点击事件
        layoutHistory.setOnClickListener {
            Toast.makeText(this, "浏览历史: 查看历史记录", Toast.LENGTH_SHORT).show()
        }

        // 关于我们点击事件
        layoutAboutUs.setOnClickListener {
            Toast.makeText(this, "关于我们: 应用版本 1.0.0", Toast.LENGTH_SHORT).show()
        }

        // 意见反馈点击事件
        layoutFeedback.setOnClickListener {
            Toast.makeText(this, "意见反馈: 提交您的宝贵意见", Toast.LENGTH_SHORT).show()
        }

        // 登出按钮点击事件
        buttonLogout.setOnClickListener {
            Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show()
            // 返回登录页面
            val intent = Intent(this, LoginActivity::class.java)
            // 清除任务栈，防止用户按返回键回到个人中心
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * 从 SharedPreferences 加载用户数据
     */
    private fun loadUserData() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = prefs.getString(KEY_USERNAME, getString(R.string.profile_default_username))
        val signature = prefs.getString(KEY_SIGNATURE, getString(R.string.profile_default_signature))

        textUsername.text = username
        textSignature.text = signature
    }

    /**
     * 保存用户名到 SharedPreferences
     */
    private fun saveUsername(username: String) {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    /**
     * 保存签名到 SharedPreferences
     */
    private fun saveSignature(signature: String) {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SIGNATURE, signature).apply()
    }

    /**
     * 显示编辑对话框
     */
    private fun showEditDialog(title: String, currentValue: String, onSave: (String) -> Unit) {
        val editText = EditText(this)
        editText.setText(currentValue)
        editText.setSelection(currentValue.length) // 光标移到末尾

        AlertDialog.Builder(this)
            .setTitle("编辑$title")
            .setView(editText)
            .setPositiveButton("保存") { _, _ ->
                val newValue = editText.text.toString().trim()
                if (newValue.isNotEmpty()) {
                    onSave(newValue)
                } else {
                    Toast.makeText(this, "$title 不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
