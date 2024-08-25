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
package com.weartools.weekdayutccomp.complication

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.weartools.weekdayutccomp.R
import android.content.ContentValues.TAG

class DayAndWeekComplicationService : SuspendingComplicationDataSourceService() {

    private fun openScreen(): PendingIntent? {

        val calendarIntent = Intent()
        calendarIntent.action = Intent.ACTION_MAIN
        calendarIntent.addCategory(Intent.CATEGORY_APP_CALENDAR)

        return PendingIntent.getActivity(
            this, 0, calendarIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {

            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = "${getString(R.string.weekW)}34").build(),
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.doy_comp_name)).build())
                    .setTitle(PlainComplicationText.Builder(text = "${getString(R.string.dayD)}254").build())
                    .setTapAction(null)
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = "${getString(R.string.weekWlong)} 34").build(),
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.doy_comp_name)).build())
                    .setTitle(PlainComplicationText.Builder(text = "${getString(R.string.dayDlong)} 254").build())
                    .setTapAction(null)
                    .build()
            }

            else -> { null }
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        return when (request.complicationType) {

            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = TimeFormatComplicationText.Builder(format = "'${getString(R.string.weekW)}'w").build(),
                    contentDescription = PlainComplicationText
                        .Builder(text = getString(R.string.doy_comp_name))
                        .build())
                    .setTitle(TimeFormatComplicationText.Builder(format = "'${getString(R.string.dayD)}'D").build())
                    .setTapAction(openScreen())
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = TimeFormatComplicationText.Builder(format = "'${getString(R.string.weekWlong)}' w").build(),
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.doy_comp_name)).build())
                    .setTitle(TimeFormatComplicationText.Builder(format = "'${getString(R.string.dayDlong)}' D").build())
                    .setTapAction(openScreen())
                    .build()
            }

            else -> {
                if (Log.isLoggable(TAG, Log.WARN)) {
                    Log.w(TAG, "Unexpected complication type ${request.complicationType}")
                }
                null
            }

        }
    }
}

