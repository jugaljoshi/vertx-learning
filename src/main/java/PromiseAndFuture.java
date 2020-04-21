@Override
public void start(Promise<Void> promise) {
  vertx.createHttpServer()
    .requestHandler(this::handleRequest)
    .listen(8080)
    .onFailure(promise::fail)
    .onSuccess(ok -> System.out.println("http://localhost:8080/"));
}

==================== From a Vert.x future to a CompletionStage

CompletionStage<String> cs = promise.future().toCompletionStage();
cs
  .thenApply(String::toUpperCase)
  .thenApply(str -> "~~~ " + str)
  .whenComplete((str, err) -> {
    if (err == null) {
      System.out.println(str);
    } else {
      System.out.println("Oh... " + err.getMessage());
    }
  });
  
  
  ================= From a CompletionStage to a Vert.x future
  
  CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
  try {
    Thread.sleep(5000);
  } catch (InterruptedException e) {
    e.printStackTrace();
  }
  return "5 seconds have elapsed";
});

Future
  .fromCompletionStage(cf, vertx.getOrCreateContext())
  .onSuccess(System.out::println)
  .onFailure(Throwable::printStackTrace);
  
  
  
  =================== Single request with future
  
  private Future<JsonObject> fetchTemperature(int port) {
  return webClient
    .get(port, "localhost", "/")
    .expect(ResponsePredicate.SC_SUCCESS)
    .as(BodyCodec.jsonObject())
    .send()
    .map(HttpResponse::body);
}

==================== CompositeFuture example 

private void handleRequest(HttpServerRequest request) {
  CompositeFuture.all(
    fetchTemperature(3000),
    fetchTemperature(3001),
    fetchTemperature(3002))
    .flatMap(this::sendToSnapshot)
    .onSuccess(data -> request.response()
      .putHeader("Content-Type", "application/json")
      .end(data.encode()))
    .onFailure(err -> {
      logger.error("Something went wrong", err);
      request.response().setStatusCode(500).end();
    });
}

private Future<JsonObject> sendToSnapshot(CompositeFuture temps) {
  List<JsonObject> tempData = temps.list();
  JsonObject data = new JsonObject()
    .put("data", new JsonArray()
      .add(tempData.get(0))
      .add(tempData.get(1))
      .add(tempData.get(2)));
  return webClient
    .post(4000, "localhost", "/")
    .expect(ResponsePredicate.SC_SUCCESS)
    .sendJson(data)
    .map(response -> data);
}

=======================
  
  
  
  
  
  
