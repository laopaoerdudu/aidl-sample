package com.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev.Book
import com.dev.BookManager
import com.dev.IBookUpdateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import kotlin.jvm.Throws

class AIDLActivity : AppCompatActivity(), View.OnClickListener {
    private var isConnected = false
    private lateinit var bookManager: BookManager
    private lateinit var tvDisplayBooks: TextView

    private val bookUpdateListener = object : IBookUpdateListener.Stub() {
        override fun onBookUpdate(book: Book?) {
            mHandler.obtainMessage(100, book).sendToTarget()
        }
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                100 -> {
                    Log.i("WWE", "AIDLActivity -> handleMessage -> message.obj -> ${message.obj}")
                }
                else -> {
                    super.handleMessage(message)
                }
            }
        }
    }

    private val deathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            try {
                bookManager.unregisterListener(bookUpdateListener)
            } catch (ex: RemoteException) {
                ex.printStackTrace()
            }
            // 注销监听和回收资源
            bookManager.asBinder().unlinkToDeath(this, 0)
            bindService()
        }
    }

    private val aidlConnection = object : ServiceConnection {

        @Throws(RemoteException::class)
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            isConnected = true
            // 如果是同一进程，那么就返回 Stub 对象本身 ( obj.queryLocalInterface(DESCRIPTOR) )
            // 否则如果是跨进程则返回 Stub 的代理内部类 Proxy
            bookManager = BookManager.Stub.asInterface(binder)
            try {
                binder?.linkToDeath(deathRecipient, 0)
                bookManager.registerListener(bookUpdateListener)
            } catch (ex: RemoteException) {
                ex.printStackTrace()
            }
            createTask()
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
        try {
            bookManager.unregisterListener(bookUpdateListener)
        } catch (ex: RemoteException) {
            ex.printStackTrace()
        }
        if (isConnected) {
            isConnected = false
            unbindService(aidlConnection)
        }
    }

    override fun onClick(view: View?) {
        if (!isConnected) {
            Toast.makeText(this, "当前与服务端处于未连接状态，请稍后再试", Toast.LENGTH_SHORT).show()
            bindService()
            return
        }

        when (view?.id) {
            R.id.btnAddBookByIn -> {
                // 调用服务端的 `addBookByIn` 方法
                bookManager.addBookByIn(Book().apply {
                    name = "九阴真经 add By in"
                    price = 10
                })
            }
            R.id.btnAddBookByOut -> {
                bookManager.addBookByOut(Book().apply {
                    name = "祥龙18掌 add By out"
                    price = 100
                })
            }
            R.id.btnAddBookByInAndOut -> {
                bookManager?.addBookByInAndOut(Book().apply {
                    name = "打狗棒 add By in and out"
                    price = 1000
                })
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

    private fun createTask() = GlobalScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            try {
                bookManager.books
            } catch (ex: RemoteException) {
                ex.printStackTrace()
            }
        }.run {
            this as? List<Book>
            Log.i("WWE", "AIDLActivity -> handleTask() -> ${Thread.currentThread().name}")
        }
    }
}