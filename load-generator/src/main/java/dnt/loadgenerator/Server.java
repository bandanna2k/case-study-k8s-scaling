package dnt.loadgenerator;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements AutoCloseable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final Vertx vertx;

    public Server(Vertx vertx)
    {
        this.vertx = vertx;
    }

    @Override
    public void close()
    {
        vertx.close();
    }

    public static void main(String[] args)
    {
        new Server(VertxFactory.newVertx())
                .start();
    }

    private void start()
    {
        vertx.deployVerticle(new LoadGenerationVerticle(vertx))
                .onSuccess(deploymentId ->
                {
                    LOGGER.info("Verticle deployed successfully: " + deploymentId);
                })
                .onFailure(throwable -> {
                    LOGGER.error("Failed to deploy verticle", throwable);
                    System.exit(1);
                });

        // Keep main thread alive
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}