package dnt.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServerVerticle extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerVerticle.class);

    private final Vertx vertx;
    private final Metrics metrics;

    public ServerVerticle(final Vertx vertx)
    {
        this.vertx = vertx;
        this.metrics = new Metrics();

        final WebClientOptions clientOptions = new WebClientOptions();
        clientOptions.setConnectTimeout(1_000);
        clientOptions.setIdleTimeout(5_000);
    }

    @Override
    public void start(Promise<Void> startPromise)
    {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/v1/status").handler(routingContext -> {
            metrics.incrementRequests();
            getStatus(routingContext);
        });
        router.get("/metrics").handler(this::getRequestsPerSecond);
        router.get("/metrics/rps").handler(this::getMetrics);

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

    private void getMetrics(RoutingContext context) {
        context.json(Map.of("requestsPerSecond", String.format("%.1f", metrics.recalculateRequestsPerSecond())));
    }

    private void getRequestsPerSecond(RoutingContext context) {

        metrics.recalculateRequestsPerSecond();
        double rps = metrics.requestsPerSecond();
        String metrics = """
# HELP http_requests_per_second Current requests per second
# TYPE http_requests_per_second gauge
http_requests_per_second {rps}
                
# HELP http_requests_total Total requests
# TYPE http_requests_total counter
http_requests_total {request_count}
                """
                .replace("{rps}", String.format("%.1f", rps))
                .replace("{request_count}", String.valueOf(this.metrics.countOfRequests()));
        context.response().send(metrics);
    }

    private void getStatus(RoutingContext routingContext)
    {
        routingContext.json(new Status());
    }
}