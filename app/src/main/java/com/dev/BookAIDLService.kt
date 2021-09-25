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
                    Log.e("WWE", "Server received { $book } from client by tag in")
                    if (!bookList.contains(_book)) {
                        bookList.add(_book.apply {
                            price = 100
                        })
                    }
                    Log.e("WWE", "Server get books are $bookList by tag in")
                } ?: Book()
            }
        }

        @Throws(RemoteException::class)
        override fun addBookByOut(book: Book?): Book {
            synchronized(this) {
                return book?.also { _book ->
                    Log.e("WWE", "Server received { $book } from client by tag out")
                    if (!bookList.contains(_book)) {
                        bookList.add(_book.apply {
                            price = 200
                        })
                    }
                    Log.e("WWE", "Server get books are $bookList by tag out")
                } ?: Book()
            }
        }

        @Throws(RemoteException::class)
        override fun addBookByInout(book: Book?): Book {
            synchronized(this) {
                return book?.also { _book ->
                    Log.e("WWE", "Server received { $book } from client by tag in and out")
                    if (!bookList.contains(_book)) {
                        bookList.add(_book.apply {
                            price = 300
                        })
                    }
                    Log.e("WWE", "Server get books are $bookList by tag in and out")
                } ?: Book()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("WWE", "BookAIDLService #onCreate")
        bookList.apply {
            add(Book().apply {
                name = "蜗居"
                price = 5
            })
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("WWE", "BookAIDLService #onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("WWE", "BookAIDLService #onDestroy")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return bookBinder
    }
}