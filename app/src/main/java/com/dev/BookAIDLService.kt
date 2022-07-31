package com.dev

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log

class BookAIDLService : Service() {
    private val bookList = mutableListOf<Book>()

    // 它的内部自动实现了线程同步的功能，而且它的本质是一个 ArrayMap
    private val callbackList = RemoteCallbackList<IBookUpdateListener>()

    // Handler 并不能精准的做定时任务，因为 Handler 在发送和接收的过程中会有时间损耗
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            for (i in 0 until callbackList.beginBroadcast()) {
                try {
                    callbackList.getBroadcastItem(i).onBookUpdate(Book().apply {
                        this.name = "Kotlin best practice"
                        this.price = 78
                    })
                } catch (ex: RemoteException) {
                    ex.printStackTrace()
                }
            }
            callbackList.finishBroadcast()
        }
    }

    private var isCanceled = false
    private var count = 0

    /**
     * 接收客户端传过来的 Book 对象，并对其进行修改，然后把修改后的对象再传回去。
     */
    private val bookBinder = object : BookManager.Stub() {

        @Throws(RemoteException::class)
        override fun getBooks(): List<Book> {
            synchronized(this) {
                Log.e("WWE", "BookAIDLService #getBooks -> $bookList")
                return bookList
            }
        }

        @Throws(RemoteException::class)
        override fun addBookByIn(book: Book?): Book {
            synchronized(this) {
                return book?.also { _book ->
                    Log.e("WWE", "BookAIDLService #addBookByIn { $book }")
                    if (!bookList.contains(_book)) {
                        bookList.add(_book.apply {
                            price = 20
                        })
                    }
                } ?: Book()
            }
        }

        @Throws(RemoteException::class)
        override fun addBookByOut(book: Book?): Book {
            synchronized(this) {
                return book?.also { _book ->
                    Log.e("WWE", "BookAIDLService #addBookByOut { $book }")
                    if (!bookList.contains(_book)) {
                        bookList.add(_book.apply {
                            price = 200
                        })
                    }
                } ?: Book()
            }
        }

        @Throws(RemoteException::class)
        override fun addBookByInAndOut(book: Book?): Book {
            synchronized(this) {
                return book?.also { _book ->
                    Log.e("WWE", "BookAIDLService #addBookByInAndOut { $book }")
                    if (!bookList.contains(_book)) {
                        bookList.add(_book.apply {
                            price = 2000
                        })
                    }
                } ?: Book()
            }
        }

        @Throws(RemoteException::class)
        override fun registerListener(listener: IBookUpdateListener) {
            callbackList.register(listener)
        }

        @Throws(RemoteException::class)
        override fun unregisterListener(listener: IBookUpdateListener) {
            callbackList.unregister(listener)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("WWE", "BookAIDLService #onCreate invoke!")
        bookList.apply {
            add(Book().apply {
                name = "蛤蟆功"
                price = 10000
            })
        }

        Thread {
            while (!isCanceled) {
                try {
                    Thread.sleep(5000)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
                count++
                if (count == 5) {
                    isCanceled = true
                }
                mHandler.obtainMessage().sendToTarget()
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("WWE", "BookAIDLService #onStartCommand invoke!")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        isCanceled = true
        super.onDestroy()
        Log.e("WWE", "BookAIDLService #onDestroy invoke!")
    }

    override fun onBind(intent: Intent?): IBinder? {
        // 将 bookBinder 回传给客户端
        return bookBinder
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        Log.e("WWE", "BookAIDLService #unbindService invoke!")
    }
}