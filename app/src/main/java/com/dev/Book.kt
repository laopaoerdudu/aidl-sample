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

    // 默认生成的模板类的对象只支持为 in 的定向 tag 。
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

    // 如果要支持为 out 或者 inout 的定向 tag 的话，还需要自己定义 readFromParcel() 方法
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