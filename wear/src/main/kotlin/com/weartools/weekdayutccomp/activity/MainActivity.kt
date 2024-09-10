/*
 * Copyright 2022 amoledwatchfaces™
 * support@amoledwatchfaces.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weartools.weekdayutccomp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.weartools.weekdayutccomp.MainViewModel
import com.weartools.weekdayutccomp.enums.Request
import com.weartools.weekdayutccomp.presentation.ComplicationsSuiteApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Update ViewModel state with initial intent extras
        viewModel.updateRequestState(intent)

        setContent {
            val open by viewModel.openRequestState.collectAsState()
            ComplicationsSuiteApp(open = open)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Update the ViewModel state when a new Intent is received
        viewModel.updateRequestState(intent)
    }
}

fun handleIntentExtras(intent: Intent): Request {
    return when {
        intent.hasExtra(EXTRA_SUNRISE_SUNSET) -> Request.SUNRISE_SUNSET
        intent.hasExtra(EXTRA_SUNRISE_SUNSET_OPEN_LOCATION) -> Request.SUNRISE_SUNSET_OPEN_LOCATION
        intent.hasExtra(EXTRA_CUSTOM_TEXT) -> Request.CUSTOM_TEXT
        intent.hasExtra(EXTRA_MOON_PHASE) -> Request.MOON_PHASE
        intent.hasExtra(EXTRA_WORLD_CLOCK) -> Request.WORLD_CLOCK
        else -> Request.MAIN
    }
}

const val EXTRA_SUNRISE_SUNSET = "com.weartools.weekdayutccomp.SUNRISE_SUNSET"
const val EXTRA_SUNRISE_SUNSET_OPEN_LOCATION = "com.weartools.weekdayutccomp.SUNRISE_SUNSET_OPEN_LOCATION"
const val EXTRA_CUSTOM_TEXT = "com.weartools.weekdayutccomp.CUSTOM_TEXT"
const val EXTRA_MOON_PHASE = "com.weartools.weekdayutccomp.MOON_PHASE"
const val EXTRA_WORLD_CLOCK = "com.weartools.weekdayutccomp.WORLD_CLOCK"

@Module
@InstallIn(SingletonComponent::class)
class LocationModule {
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
}