// BookManager.aidl
package com.dev;
import com.dev.Book;
import com.dev.IBookUpdateListener;

interface BookManager {
    List<Book> getBooks();
    Book addBookByIn(in Book book);
    Book addBookByOut(out Book book);
    Book addBookByInAndOut(inout Book book);
    void registerListener(IBookUpdateListener listener);
    void unregisterListener(IBookUpdateListener listener);
}