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

package dev.floofy.services.kanata.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class SL4FJDelegate(private val kClass: KClass<*>): ReadOnlyProperty<Any, Logger> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Logger =
        LoggerFactory.getLogger(kClass.java)
}

inline fun <reified T> logging(): SL4FJDelegate = SL4FJDelegate(T::class)
