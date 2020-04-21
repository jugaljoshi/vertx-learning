private Single<HttpResponse<JsonObject>> fetchTemperature(int port) {
  return webClient
    .get(port, "localhost", "/")
    .expect(ResponsePredicate.SC_SUCCESS)
    .as(BodyCodec.jsonObject())
    .rxSend();
}


private Single<JsonObject> collectTemperatures() {
  Single<HttpResponse<JsonObject>> r1 = fetchTemperature(3000);
  Single<HttpResponse<JsonObject>> r2 = fetchTemperature(3001);
  Single<HttpResponse<JsonObject>> r3 = fetchTemperature(3002);

  return Single.zip(r1, r2, r3, (j1, j2, j3) -> {
    JsonArray array = new JsonArray()
      .add(j1.body())
      .add(j2.body())
      .add(j3.body());
    return new JsonObject().put("data", array);
  });
}

private void handleRequest(HttpServerRequest request) {
  Single<JsonObject> data = collectTemperatures();
  sendToSnapshot(data).subscribe(json -> {
    request.response()
      .putHeader("Content-Type", "application/json")
      .end(json.encode());
  }, err -> {
    logger.error("Something went wrong", err);
    request.response().setStatusCode(500).end();
  });
}

private Single<JsonObject> sendToSnapshot(Single<JsonObject> data) {
  return data.flatMap(json -> webClient
    .post(4000, "localhost", "")
    .expect(ResponsePredicate.SC_SUCCESS)
    .rxSendJsonObject(json)
    .flatMap(resp -> Single.just(json)));
}


//This method introduces the flatMap operator which is well-known to the functional 
//programming enthusiasts. Do not worry if flatMap sounds cryptic to you: 
//in the case of composing sequential asynchronous operations you can just read "flatmap" as "and then".



