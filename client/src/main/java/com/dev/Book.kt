package com.dev

import android.os.Parcel
import android.os.Parcelable

class Book() : Parcelable {
    var name: String? = null
    var price = 0

    constructor(parcel: Parcel) : this() {
        this.name = parcel.readString()
        this.price = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Book( name = $name, price = $price )"
    }

    fun readFromParcel(parcel: Parcel) {
        name = parcel.readString()
        price = parcel.readInt()
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}