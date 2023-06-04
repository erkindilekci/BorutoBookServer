package com.erkindilekci

import com.erkindilekci.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureKoin()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureDefaultHeader()
    configureStatusPage()
}
