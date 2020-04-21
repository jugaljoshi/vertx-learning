public Future retrieve() {
    Future future = Future.future();
    vertx.fileSystem().readFile("fileName", ar -> {
        if (ar.failed()) {
            future.failed(ar.cause());
        } else {
            future.complete(ar.result().toString());
        }
    });
    return future;
}

retrieve().setHandler(ar -> {
  if (ar.failed()) {
    // Handle the failure, the exception is 
    // retrieved using ar.cause()
    Throwable cause = ar.cause();
    // ...
   } else {
    // Made it, the result is in ar.result()
    int r = ar.result();
    // ...
   }
});

=======================

retrieve()
  .compose(this::anotherAsyncMethod)
  .setHandler(ar -> {
    // ar.result is the final result
    // if any stage fails, ar.cause is 
    // the thrown exception
  });
  
========================

// CompositeFuture is a companion class simplifying the drastically concurrent composition. 
// all is not the only operator provided, you can use join, any

Future future1 = retrieve();
Future future2 = anotherAsyncMethod();
CompositeFuture.all(future1, future2)
  .setHandler(ar -> {
    // called when either all future have completed
    // successfully (success), 
    // or one failed (failure)
});

===========================. RxListner rxListen


private Completable createHttpServer(JsonObject config,
  Router router) {
  return vertx
    .createHttpServer()
    .requestHandler(router::accept)
    .rxListen(config.getInteger("HTTP_PORT", 8080))
    .toCompletable();
}






