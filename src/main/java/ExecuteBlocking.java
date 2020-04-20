public class Offload extends AbstractVerticle {
  private final Logger logger = LoggerFactory.getLogger(Offload.class);

  @Override
  public void start() {
    vertx.setPeriodic(5000, id -> {
      logger.info("Tick");
      vertx.executeBlocking(this::blockingCode, this::resultHandler);
    });
  }

  private void blockingCode(Promise<String> promise) {
    logger.info("Blocking code running");
    try {
      Thread.sleep(4000);
      logger.info("Done!");
      promise.complete("Ok!");
    } catch (InterruptedException e) {
      promise.fail(e);
    }
  }

  private void resultHandler(AsyncResult<String> ar) {
    if (ar.succeeded()) {
      logger.info("Blocking code result: {}", ar.result());
    } else {
      logger.error("Woops", ar.cause());
    }
  }
}


// By default successive executeBlocking operations have their results being processed 
// in the same order as the calls to executeBlocking.

// There exists a variant of executeBlocking with an additional boolean parameter, 
// and when set to false, results are being made available as event-loop events as soon as they are available, 
// no matter what the order of executeBlocking calls was.
