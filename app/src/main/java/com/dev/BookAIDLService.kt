package com.dev

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import kotlin.jvm.Throws

class BookAIDLService : Service() {
    private val bookList = mutableListOf<Book>()
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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("WWE", "BookAIDLService #onStartCommand invoke!")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("WWE", "BookAIDLService #onDestroy invoke!")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return bookBinder
    }
}