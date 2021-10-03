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

/**
 * Returns the configuration for using [Instatus](https://instatus.com)
 */
@Serializable
data class InstatusConfig(
    /**
     * Returns a map of components by `pod: componentId` from Instatus. Kanata
     * will print out the components by `component name -> component id` so you
     * can configure this.
     */
    val components: Map<String, String> = mapOf(),

    /**
     * Returns the webhook URL to use, you can find more here:
     * https://instatus.com/help/monitoring/custom-service-webhook
     */
    val webhookUrl: String? = null,

    /**
     * Returns the API key, this is required!
     */
    val apiKey: String
)
