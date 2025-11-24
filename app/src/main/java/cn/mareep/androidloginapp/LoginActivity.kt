package cn.mareep.androidloginapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {

    // 数据库帮助类
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 把 layout xml 布局文件加载到 Activity 中
        setContentView(R.layout.activity_login)

        // 初始化数据库（首次调用时会创建表并预埋默认用户）
        dbHelper = DatabaseHelper(this)

        // 输入框
        val emailEditText: EditText = findViewById(R.id.editTextTextEmailAddress)
        val passwordEditText: EditText = findViewById(R.id.editTextTextPassword)
        // 按钮
        val loginButton: Button = findViewById(R.id.button)
        val wechatLoginButton: Button = findViewById(R.id.button_wechat_login)
        val appleLoginButton: Button = findViewById(R.id.button_apple_login)
        // 密码输入监听
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordEditText.error = validatePassword(s.toString())
            }
        })

        // 邮箱输入监听
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailEditText.error = validateEmail(s.toString())
            }
        })

        // 登录按钮点击事件
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // 先验证输入格式
            val emailError = validateEmail(email)
            val passwordError = validatePassword(password)

            if (email.isEmpty()) {
                emailEditText.error = "请输入邮箱"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "请输入密码"
                return@setOnClickListener
            }
            if (emailError != null) {
                emailEditText.error = emailError
                return@setOnClickListener
            }
            if (passwordError != null) {
                passwordEditText.error = passwordError
                return@setOnClickListener
            }

            // 验证用户名密码
            if (dbHelper.authenticate(email, password)) {
                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show()
                 val intent = Intent(this, ProfileActivity::class.java)
                 startActivity(intent)
            } else {
                Toast.makeText(this, "邮箱或密码错误", Toast.LENGTH_SHORT).show()
            }
        }
        wechatLoginButton.setOnClickListener {
            Toast.makeText(this,"微信登录", Toast.LENGTH_SHORT).show()
        }
        appleLoginButton.setOnClickListener {
            Toast.makeText(this,"Apple登录", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭数据库连接
        dbHelper.close()
    }


    // 验证密码格式
    private fun validatePassword(password: String): String? {
        if (!password.isEmpty()) {
            if (password.length < 8) {
                return "密码长度不能少于8位"
            }
            return null
        }
        return null
    }

    // 验证邮箱格式
    private fun validateEmail(email: String): String? {
        val emailRegex =
            "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$".toRegex()
        return if (email.isNotEmpty() && !email.matches(emailRegex)) {
            "请输入正确的邮箱格式"
        } else {
            null
        }
    }
}