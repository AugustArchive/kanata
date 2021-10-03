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

package dev.floofy.services.kanata.kubernetes

import dev.floofy.services.kanata.data.ConfigProvider
import dev.floofy.services.kanata.utils.logging
import io.kubernetes.client.Metrics
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.Config

class MetricsClient {
    private val logger by logging<MetricsClient>()
    private val coreApi: CoreV1Api
    private val api: Metrics

    init {
        val config = Config.defaultClient()

        coreApi = CoreV1Api(config)
        api = Metrics(config)
    }

    fun init() {
        // TODO: watch pods for new updates?
        // val provider = ConfigProvider.config.getProvider()
        val pods = coreApi.listNamespacedPod(
            ConfigProvider.config.namespace,
            "",
            false,
            "",
            "",
            "",
            250,
            "",
            "",
            15,
            false
        )

        logger.info("Found ${pods.items.size} pods to watch over.")
    }
}