package com.celzero.bravedns.service

import android.app.Activity

/*
 * Copyright 2020 RethinkDNS and its authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
interface AppUpdater {
    fun checkForAppUpdate(isUserInitiated:Boolean, activity: Activity, listener:InstallStateListener)
    fun completeUpdate()
    fun unregisterListener(listener: InstallStateListener)

    interface InstallStateListener {
        fun onStateUpdate(state:InstallState)
        fun onUpdateAvailable()
        fun onUpToDate()
        fun onUpdateCheckFailed()
    }
    data class InstallState(val status:InstallStatus)
    enum class InstallStatus {
        CANCELED, DOWNLOADED, DOWNLOADING, FAILED, INSTALLED, INSTALLING, PENDING, UNKNOWN
    }
}