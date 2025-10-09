package dnt.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerVerticle extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerVerticle.class);

    private final Vertx vertx;

    public ServerVerticle(final Vertx vertx)
    {
        this.vertx = vertx;

        final WebClientOptions clientOptions = new WebClientOptions();
        clientOptions.setConnectTimeout(1_000);
        clientOptions.setIdleTimeout(5_000);
    }

    @Override
    public void start(Promise<Void> startPromise)
    {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/v1/status").handler(this::getStatus);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(10201)
                .onSuccess(successfulHttpServer -> {
                    LOGGER.info("Server started on port " + successfulHttpServer.actualPort());
                    startPromise.complete();
                })
                .onFailure(t -> {
                    LOGGER.error("Failed to start server", t);
                    startPromise.fail("Failed to start server");
                });
    }

    private void getStatus(RoutingContext routingContext)
    {
        routingContext.json(new Status());
    }
}