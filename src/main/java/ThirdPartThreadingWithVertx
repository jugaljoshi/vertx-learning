public class ThirdPartThreadingWithVertx extends AbstractVerticle {
  private final Logger logger = LoggerFactory.getLogger(MixedThreading.class);

  @Override
  public void start() {
    Context context = vertx.getOrCreateContext();
    new Thread(() -> {
      try {
        run(context);
      } catch (InterruptedException e) {
        logger.error("Woops", e);
      }
    }).start();
  }

  private void run(Context context) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    logger.info("I am in a non-Vert.x thread");
    context.runOnContext(v -> {
      logger.info("I am on the event-loop");
      vertx.setTimer(1000, id -> {
        logger.info("This is the final countdown");
        latch.countDown();
      });
    });
    logger.info("Waiting on the countdown latch...");
    latch.await();
    logger.info("Bye!");
  }
}

//By passing a context obtained from a verticle, we are able to execute some code back on the event-loop 
// from some code running on a non-Vert.x thread.


INFO [Thread-3] MixedThreading - I am in a non-Vert.x thread
INFO [Thread-3] MixedThreading - Waiting on the countdown latch...
INFO [vert.x-eventloop-thread-0] MixedThreading - I am on the event-loop
INFO [vert.x-eventloop-thread-0] MixedThreading - This is the final countdown
INFO [Thread-3] MixedThreading - Bye!
