// BookManager.aidl
package com.dev;
import com.dev.Book;

interface BookManager {
    List<Book> getBooks();
    Book addBookByIn(in Book book);
    Book addBookByOut(out Book book);
    Book addBookByInAndOut(inout Book book);
}