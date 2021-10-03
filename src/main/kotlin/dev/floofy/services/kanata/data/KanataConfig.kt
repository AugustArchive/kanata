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

import kotlinx.serialization.Serializable
import java.lang.IllegalArgumentException

@Serializable
data class KanataConfig(
    val provider: ProviderConfig,
    val namespace: String,
    val port: Int = 22039,
    val host: String? = null,
    val dev: Boolean = false
) {
    fun getProvider(): InstatusConfig {
        if (provider.instatus != null) return provider.instatus

        throw IllegalArgumentException("Provider is not supported is at this time.")
    }
}

@Serializable
open class ProviderConfig(
    val instatus: InstatusConfig? = null
)
