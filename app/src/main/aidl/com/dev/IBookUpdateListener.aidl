// IBookUpdateListener.aidl
package com.dev;
import com.dev.Book;

interface IBookUpdateListener {
    void onBookUpdate(in Book book);
}