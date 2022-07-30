## `asInterface(android.os.IBinder obj)`

```
public static com.dev.BookManager asInterface(android.os.IBinder obj)
    {
      if ((obj == null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin != null) && (iin instanceof com.dev.BookManager))) {
        return ((com.dev.BookManager)iin);
      }
      return new com.dev.BookManager.Stub.Proxy(obj);
    }
```

## `onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)`

`onTransact(...)` 方法运行在服务端的 Binder 线程池中， 客户端发起跨进程请求时，远程请求会通过系统底层封装后交给此方法来处理。
如果此方法返回 false，那么客户端的请求就会失败。

- code : 确定客户端请求的目标方法标识符

>TRANSACTION_getBooks, TRANSACTION_addBookByIn ...

- data : 如果目标方法有参数的话，就从 `data` 参数中取出目标方法中的参数

- reply : 当目标方法执行完毕后，如果目标方法有返回值，就在 `reply` 中写入返回值

- flag : Additional operation flags. Either 0 for a normal RPC, or FLAG_ONEWAY for a one-way RPC.

`onTransact(...)` 就是负责数据的读写，以及结果的返回。

## 定向 tag 的结论

AIDL 中的定向 tag 表示了在跨进程通信中数据的流向。

其中 in 表示数据只能由客户端流向服务端；
in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；

out 表示数据只能由服务端流向客户端；
out 的话表现为服务端将会接收到那个对象的的空对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动；

而 inout 则表示数据可在服务端与客户端之间双向流通；
inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，并且客户端将会同步服务端对该对象的任何变动。

因为这些是跨进程操作，必须将数据序列化传输。

---

Proxy -> client -> mRemote.transact(...)

Stub -> Server -> onTransact(...)



































