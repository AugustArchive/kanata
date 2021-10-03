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

package dev.floofy.services.kanata.types

import kotlinx.serialization.Serializable

/**
 * Returns the phase change of a Kubernetes pod.
 */
@Serializable
enum class PhaseStatus {
    /**
     * The pod is now running! If the phase was [PhaseStatus.NOT_FOUND],
     * then it will not report anything. If the phase was [PhaseStatus.TERMINATED],
     * then it'll report the pod is back online (since running -> terminated = report).
     */
    RUNNING,

    /**
     * If the pod was running and the host has terminated this pod, it'll
     * return this phase change and Kanata will report it to Instatus as a "Partial Outage",
     * if the pod has been terminated when the interval has checked on it at-least 15 times, it'll post
     * as a "Major Outage". If the pod has been terminated when the interval has checked on it
     * at-least 5-7 times, it'll post as a "Minor Outage".
     */
    TERMINATED,

    /**
     * The pod is initializing the container to run, this will post as an "Under Maintenance"
     * status, if the pod was running then initializing (due to deployment changes), it'll
     * not post it (since it'll get spammy with CI and such.)
     */
    INITIALIZING,

    /**
     * The default phase change.
     */
    NOT_FOUND,

    /**
     * The pod is receiving a huge spike in memory and/or CPU usage, the threshold
     * is completely customizable, and it'll report as "Degraded Performance" (hence the
     * enum member name)
     */
    DEGRADED_PERFORMANCE
}

/**
 * The status packet that represents the current status
 * of the Kubernetes pod.
 */
@Serializable
data class StatusPacket(
    /**
     * Returns the phase status, read the [documentation][PhaseStatus] for more information.
     */
    val phase: PhaseStatus,

    /**
     * Returns a message on why the [phase] it is at right now.
     */
    val message: String? = null
)
