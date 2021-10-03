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

import dev.floofy.services.kanata.utils.logging
import java.io.File

/**
 * Bootstrap object to use when initializing Kanata.
 */
object Bootstrap {
    private val logger by logging<Bootstrap>()

    @JvmStatic
    fun main(args: Array<String>) {
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
