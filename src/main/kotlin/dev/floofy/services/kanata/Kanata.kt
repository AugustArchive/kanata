/**
 * Microservice to handle Kubernetes pod state changes and reports them in Instatus or Statuspages.
 * Copyright (c) 2021 Noel <cutie@floofy.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.floofy.services.kanata

import dev.floofy.services.kanata.data.ConfigProvider
import dev.floofy.services.kanata.utils.logging
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

class Kanata {
    lateinit var server: NettyApplicationEngine
    private val logger by logging<Kanata>()

    val httpClient: HttpClient = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
                addInterceptor {
                    val logger = LoggerFactory.getLogger("okhttp.interceptor.kanata.logging")
                    val request = it.request()
                    var response: okhttp3.Response

                    val time = measureTimeMillis {
                        response = it.proceed(request)
                    }

                    logger.info("${request.url} ${request.method} | ${response.code} - ${time}ms")
                    response
                }
            }
        }

        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }

        install(UserAgent) {
            agent = "Kanata (+https://github.com/auguwu/Kanata; v${KanataInfo.VERSION})"
        }
    }

    fun launch() {
        logger.info("Kanata is now launching into space! \uD83D\uDE80")

        ConfigProvider.load()
        val environment = applicationEngineEnvironment {
            this.developmentMode = ConfigProvider.config.dev
            this.log = LoggerFactory.getLogger("dev.floofy.services.kanata.ktor.KtorApp")

            connector {
                host = ConfigProvider.config.host ?: "0.0.0.0"
                port = ConfigProvider.config.port
            }

            module {
                routing {
                    get("/") {
                        call.respondText(
                            "{\"hello\":\"world\",\"version\":\"${KanataInfo.VERSION}\"}",
                            contentType = ContentType.Application.Json,
                            status = HttpStatusCode.OK
                        )
                    }

                    get("/status") {
                        call.respondText(
                            "{\"todo\":true}",
                            contentType = ContentType.Application.Json,
                            status = HttpStatusCode.OK
                        )
                    }
                }
            }
        }

        server = embeddedServer(Netty, environment)
        server.addShutdownHook {
            logger.info("Shutting down Kanata...")
            httpClient.close()
        }

        server.start(wait = true)
    }
}
