package dnt.loadgenerator;

import io.netty.util.concurrent.DefaultThreadFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.uritemplate.UriTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LoadGenerationVerticle extends AbstractVerticle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGenerationVerticle.class);

    private final WebClient client;

    private final AtomicLong passCount = new AtomicLong();
    private final AtomicLong failCount = new AtomicLong();
    private final Vertx vertx;

    private long delayMillis = 1000;
    private final ExecutorService executor;


    public LoadGenerationVerticle(final Vertx vertx)
    {
        this.vertx = vertx;

        final WebClientOptions clientOptions = new WebClientOptions();
        clientOptions.setConnectTimeout(1_000);
        clientOptions.setIdleTimeout(5_000);
        this.client = WebClient.create(vertx, clientOptions);

        executor = Executors.newThreadPerTaskExecutor(
                new DefaultThreadFactory("DefaultThreadFactory"));
    }

    @Override
    public void start(Promise<Void> startPromise)
    {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/v1/status").handler(this::getStatus);
        router.post("/v1/load").handler(this::updateLoadGenerator);
        routeFrontEnd(router);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(10200)
                .onSuccess(successfulHttpServer -> {
                    LOGGER.info("Server started on port " + successfulHttpServer.actualPort());
                    startPromise.complete();
                })
                .onFailure(t -> {
                    LOGGER.error("Failed to start server", t);
                    startPromise.fail("Failed to start server");
                });
        setTimer();
    }

    private void setTimer()
    {
        vertx.setTimer(delayMillis, aLong -> {
            if(failCount.get() <= 10)
            {
                executor.submit(this::call);
            }
            setTimer();
        });
    }

    private void getStatus(RoutingContext routingContext)
    {
        routingContext.json(getStatus());
    }

    private Status getStatus()
    {
        return new Status(passCount.get(), failCount.get());
    }

    private void updateLoadGenerator(RoutingContext context)
    {
        try {
            final JsonObject jsonObject = context.body().asJsonObject();
            delayMillis = jsonObject.getLong("delay");
            passCount.set(0);
            failCount.set(0);
            LOGGER.info("Load generation delay set to {}(ms) ", delayMillis);
            context.response().setStatusCode(200).end();
        }
        catch (Exception e) {
            LOGGER.error("Failed to update load generator", e);
            context.response().setStatusCode(500).end();
        }
    }

    private void routeFrontEnd(Router router)
    {
        router.route("/*").handler(StaticHandler.create("dist")
                .setCachingEnabled(false)
                .setIndexPage("index.html"));
    }

    private void call()
    {
        int port = 10201;
        String host = "case-study-service";
        client.get(port, host, "/v1/status")
                .send()
                .onSuccess(resp -> {
                    passCount.incrementAndGet();
                    handleResponse(resp, host, port);
                })
                .onFailure(t -> {
                    failCount.incrementAndGet();
                    LOGGER.error("Failed to request. {}:{} {}", host, port, t.getMessage());
                });
    }

    private static void handleResponse(final HttpResponse<Buffer> resp, String host, int port)
    {
        try {
            JsonObject jsonObject = resp.bodyAsJsonObject();
            LOGGER.info("Response: {}:{} {}", host, port, jsonObject);
        }
        catch (Exception e) {
            LOGGER.error("Failed to handle response: {}:{} {}", host, port, resp.bodyAsString());
        }
    }
}