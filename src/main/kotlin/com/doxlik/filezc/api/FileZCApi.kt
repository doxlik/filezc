package com.doxlik.filezc.api

import io.netty.handler.codec.http.HttpHeaderNames
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.netty.DisposableServer
import reactor.netty.http.server.HttpServer
import reactor.netty.resources.LoopResources
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.concurrent.thread

@Component
class ServerApplicationRunner() {

    private var server: DisposableServer? = null

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        thread {
            server = HttpServer.create().port(8080)
                .runOn(LoopResources.create("my-loop-sources"), true)
                .route { routes ->
                    routes
                        .get("/thread-name") { req, res ->
                            res.sendString(Mono.just(Thread.currentThread().name + "\n"))
                        }
                        .get("/hello-world") {req, res ->
                            res.sendString(Mono.just("Hello, World!"))
                        }.get("/file/{name}") { req, res ->
                            val name = req.param("name") ?: ""
                            val path: Path = Paths.get("data", name)

                            if (!Files.exists(path) || Files.isDirectory(path)) {
                                res.status(404).sendString(Mono.just("404 Not Found"))
                            } else {
                                res.status(200)
                                    .header(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream")
                                    .sendFile(path)
                            }
                        }
                }
                .bindNow()

            System.err.println("Server started")
            server?.onDispose()?.block()
        }
    }


    @EventListener(ContextClosedEvent::class)
    fun stop() {
        System.err.println("Stoping server...")
        server?.disposeNow()
        System.err.println("Server stoped")
    }
}