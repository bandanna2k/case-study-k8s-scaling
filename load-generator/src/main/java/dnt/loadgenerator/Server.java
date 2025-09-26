package dnt.loadgenerator;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static io.vertx.ext.auth.audit.impl.SecurityAuditLogger.LOGGER;

public class Server implements AutoCloseable
{
    private final Vertx vertx;

    public Server(Vertx vertx)
    {
        this.vertx = vertx;
    }

    public Future<HttpServer> start()
    {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        routeFrontEnd(router);

        return vertx.createHttpServer()
                .requestHandler(router)
                .listen(10200)
                .onSuccess(successfulHttpServer -> {
                    LOGGER.info("Server started on port " + successfulHttpServer.actualPort());
                })
                .onFailure(t -> LOGGER.error("Failed to start server", t));
    }

    private void routeFrontEnd(Router router)
    {
        router.route("/*").handler(StaticHandler.create("dist")
                        .setCachingEnabled(false)
                        .setIndexPage("index.html"));
    }

    @Override
    public void close()
    {
        vertx.close();
    }

    public static void main(String[] args)
    {
        try(Server server = new Server(VertxFactory.newVertx());
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)))
        {
            server.start();
            Thread.sleep(2_000);
            System.out.println("Press any to exit.");
            reader.readLine();
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}