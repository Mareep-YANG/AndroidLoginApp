package cn.mareep.androidloginapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        // 数据库文件名
        private const val DATABASE_NAME = "login.db"

        // 数据库版本号
        private const val DATABASE_VERSION = 1

        // 表名
        const val TABLE_USERS = "users"

        // 列名
        const val COLUMN_ID = "_id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"  // 存储哈希后的密码
        const val COLUMN_CREATED_AT = "created_at"

        // 创建表的 SQL 语句
        private const val TABLE_CREATE = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建用户表
        db.execSQL(TABLE_CREATE)
        // 预埋默认测试账户
        insertDefaultUsers(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    /**
     * 预埋默认测试用户
     */
    private fun insertDefaultUsers(db: SQLiteDatabase) {
        // 默认账户: test@example.com / 12345678
        insertUser(db, "test@example.com", "12345678")
    }

    /**
     * 插入用户（内部方法，用于初始化）
     */
    private fun insertUser(db: SQLiteDatabase, email: String, password: String) {
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, hashPassword(password))
        }
        db.insert(TABLE_USERS, null, values)
    }

    /**
     * 验证用户登录
     * @return true 验证成功，false 验证失败
     */
    fun authenticate(email: String, password: String): Boolean {
        val db = readableDatabase
        val hashedPassword = hashPassword(password)

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, hashedPassword),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * 检查邮箱是否已存在
     */
    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /**
     * 注册新用户
     * @return true 注册成功，false 邮箱已存在
     */
    fun register(email: String, password: String): Boolean {
        if (isEmailExists(email)) {
            return false
        }
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, hashPassword(password))
        }
        return db.insert(TABLE_USERS, null, values) != -1L
    }

    /**
     * 密码哈希（使用 SHA-256）
     * 注意：生产环境应使用 bcrypt 等更安全的算法
     */
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}