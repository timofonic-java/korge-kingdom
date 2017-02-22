package com.games.soywiz.korgekingdom

import com.soywiz.korio.async.EventLoop
import com.soywiz.korio.async.spawnAndForget
import com.soywiz.korio.vfs.ResourcesVfs
import io.vertx.core.Vertx
import java.util.*

fun main(args: Array<String>) = EventLoop.main {
    val vertx = Vertx.vertx()
    val resources = ResourcesVfs

    vertx.createHttpServer()
            .websocketHandler { ws ->
                spawnAndForget {
                    login(Client(ws))
                }
            }
            .requestHandler { req ->
                spawnAndForget {
                    req.response().end(resources["ws.html"].readString())
                }
            }
            .listen(8080);
}


suspend fun login(client: Client) {
    val uuid = UUID.randomUUID().toString()
    client.send(LoginChallenge(uuid))
    val req = client.wait(LoginRequest::class.java)
    println(req)
}