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

import java.util.*

object KanataInfo {
    val VERSION: String
    val COMMIT_HASH: String
    val BUILD_DATE: String

    init {
        val stream = this::class.java.getResourceAsStream("/metadata.properties")
        val props = Properties().apply { load(stream) }

        VERSION = props.getProperty("app.version")
        COMMIT_HASH = props.getProperty("app.commit")
        BUILD_DATE = props.getProperty("app.build.date")
    }
}
