## 跨进程通信可能遇到问题

在 AIDL 中客户端向服务端注册一个回调方法时，服务端要考虑客户端是否意外退出（客户端由于错误应用 Crash，或者被 Kill 掉了），服务端还不知道，去回调客户端，出现错误。

在进程间通讯过程当中，极可能出现一个进程死亡的状况。若是这时活着的一方不知道另外一方已经死了就会出现问题。那咱们如何在 A 进程中获取 B 进程的存活状态呢？

android 给咱们提供了解决方式，那就是 Binder 的 `linkToDeath` 和 `unlinkToDeath` 方法，
linkToDeath 方法须要传入一个 `DeathRecipient` 对象，`DeathRecipient` 类里面有个 `binderDied` 方法，
当 binder 对象的所在进程死亡，binderDied 方法就会被执行，咱们就能够在 binderDied 方法里面作一些异常处理，释放资源等操作了。

`RemoteCallbackList` 很好的解决了 IPC 过程当中可能出现的某一方进程 Crash，引发另外一方 Exception 的问题。


