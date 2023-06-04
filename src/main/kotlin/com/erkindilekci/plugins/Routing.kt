package com.erkindilekci.plugins

import com.erkindilekci.routes.getAllHeroes
import com.erkindilekci.routes.root
import com.erkindilekci.routes.searchHeroes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()
        searchHeroes()

//        static("/images") {
//            resources("images")
//        }

        staticResources("/images", "images")
    }
}
