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
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.system.exitProcess

/**
 * Bootstrap object to use when initializing Kanata.
 */
object Bootstrap {
    private val logger by logging<Bootstrap>()

    @JvmStatic
    fun main(args: Array<String>) {
        Thread.currentThread().name = "Kanata-MainThread"
        ConfigProvider.load()

        if (args.isNotEmpty() && args[0] == "components") {
            logger.info("Finding component tree from Instatus...")

            val provider = ConfigProvider.config.getProvider()
            val kanata = Kanata()

            runBlocking {
                val pages = kanata.httpClient.get<JsonArray>("https://api.instatus.com/v1/pages") {
                    header("Authorization", "Bearer ${provider.apiKey}")
                }

                logger.info("Found ${pages.size} pages available!")
                for (page in pages) {
                    logger.info("-> ${page.jsonObject["name"]} -> ${page.jsonObject["id"]}")
                }

                logger.info("Now retrieving component IDs!")
                for (page in pages) {
                    val components = kanata.httpClient.get<JsonArray>("https://api.instatus.com/v1/${page.jsonObject["id"]!!.jsonPrimitive.content}/components") {
                        header("Authorization", "Bearer ${provider.apiKey}")
                    }

                    logger.info("Page ${page.jsonObject["name"]} has a total of ${components.size} components.")
                    for (component in components) {
                        logger.info("-> ${page.jsonObject["name"]}: ${component.jsonObject["name"]} -> ${component.jsonObject["id"]}")
                    }
                }
            }

            logger.info("Hoped it help you to map out components for listening to status updates!")
            kanata.httpClient.close()
            exitProcess(1)
        }

        val banner = File("./assets/banner.txt")
        val lines = banner.readText().split("\n")

        for (line in lines) {
            val print = line
                .replace("{{VERSION}}", KanataInfo.VERSION)
                .replace("{{COMMIT_HASH}}", KanataInfo.COMMIT_HASH)
                .replace("{{BUILD_DATE}}", KanataInfo.BUILD_DATE)

            println(print)
        }

        logger.info("Bootstrapping Kanata...")
        val kanata = Kanata()
        kanata.launch()
    }
}
