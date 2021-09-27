package com.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.Book
import com.dev.BookManager
import java.lang.StringBuilder
import kotlin.jvm.Throws

class AIDLActivity : AppCompatActivity(), View.OnClickListener {
    private var isConnected = false
    private lateinit var bookManager: BookManager
    private lateinit var tvDisplayBooks: TextView

    private val aidlConnection = object : ServiceConnection {

        @Throws(RemoteException::class)
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            isConnected = true

            // 如果是同一进程，那么就返回 Stub 对象本身 ( obj.queryLocalInterface(DESCRIPTOR) )
            // 否则如果是跨进程则返回 Stub 的代理内部类 Proxy
            bookManager = BookManager.Stub.asInterface(binder)
            Log.i("WWE", "Client #onServiceConnected get bookList -> ${bookManager.books}")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("WWE", "Client #onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aidl_layout)
        bindService()
        tvDisplayBooks = findViewById(R.id.tvDisplayBooks)
        findViewById<Button>(R.id.btnAddBookByIn).setOnClickListener(this)
        findViewById<Button>(R.id.btnAddBookByOut).setOnClickListener(this)
        findViewById<Button>(R.id.btnAddBookByInAndOut).setOnClickListener(this)
        findViewById<Button>(R.id.btnGetBooks).setOnClickListener(this)
    }

    private fun bindService() {
        bindService(
            Intent().apply {
                action = "com.dev.BookAIDLService"
                setPackage("com.dev")
            }, aidlConnection, BIND_AUTO_CREATE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isConnected) {
            isConnected = false
            unbindService(aidlConnection)
        }
    }

    override fun onClick(view: View?) {
        if (!isConnected) {
            bindService()
            Toast.makeText(this, "当前与服务端处于未连接状态，请稍后再试", Toast.LENGTH_SHORT).show()
            return
        }

        when (view?.id) {
            R.id.btnAddBookByIn -> {
                val book = Book().apply {
                    name = "九阴真经 add By in"
                    price = 10
                }
                bookManager.addBookByIn(book)
                Log.i("WWE", "Client transfer { $book } to server by In")
            }
            R.id.btnAddBookByOut -> {
                val book = Book().apply {
                    name = "祥龙18掌 add By out"
                    price = 100
                }
                bookManager.addBookByOut(book)
                Log.i("WWE", "Client transfer { $book } to server by Out")
            }
            R.id.btnAddBookByInAndOut -> {
                val book = Book().apply {
                    name = "打狗棒 add By in and out"
                    price = 1000
                }
                bookManager?.addBookByInAndOut(book)
                Log.i("WWE", "Client transfer { $book } to server by In and Out")
            }
            R.id.btnGetBooks -> {
                tvDisplayBooks.text = StringBuilder().apply {
                    bookManager.books.forEach { book ->
                        append("$book\n")
                    }
                }.toString()
            }
        }
    }
}