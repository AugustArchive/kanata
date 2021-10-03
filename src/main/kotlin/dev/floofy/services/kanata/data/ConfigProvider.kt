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

package dev.floofy.services.kanata.data

import com.charleskorn.kaml.Yaml
import dev.floofy.services.kanata.utils.logging
import java.io.File
import kotlin.system.exitProcess

object ConfigProvider {
    private val logger by logging<ConfigProvider>()
    lateinit var config: KanataConfig

    fun load() {
        logger.info("Loading configuration...")

        val file = File("./config.yml")
        try {
            config = Yaml.default.decodeFromString(KanataConfig.serializer(), file.readText())
        } catch (e: Exception) {
            logger.error("Unknown exception has occured while serializing config:", e)
            exitProcess(1)
        }
    }
}
