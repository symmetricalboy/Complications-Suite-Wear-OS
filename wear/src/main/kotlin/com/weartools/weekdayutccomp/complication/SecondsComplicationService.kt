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
import android.provider.AlarmClock
import android.util.Log
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.weartools.weekdayutccomp.R
import android.content.ContentValues.TAG
import android.graphics.drawable.Icon.createWithResource

class SecondsComplicationService : SuspendingComplicationDataSourceService() {

    private fun openScreen(): PendingIntent? {

        val mClockIntent = Intent(AlarmClock.ACTION_SET_TIMER)

        return PendingIntent.getActivity(
            this, 0, mClockIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {

            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = "30").build(),
                    contentDescription = ComplicationText.EMPTY)
                    .setTitle(PlainComplicationText.Builder(text = getString(R.string.sec_short_title)).build())
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = getString(R.string.sec_comp_name)).build(),
                    contentDescription = ComplicationText.EMPTY)
                    .setTitle(PlainComplicationText.Builder(text = "30").build())
                    .build()
            }

            else -> { null }
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {

        return when (request.complicationType) {

            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = TimeFormatComplicationText.Builder(format = "ss").build(),
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.sec_short_title)).build())
                    .setTitle(PlainComplicationText.Builder(text = getString(R.string.sec_short_title)).build())
                    .setTapAction(openScreen())
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = getString(R.string.sec_comp_name)).build(),
                    contentDescription = PlainComplicationText.Builder(text = getString(R.string.sec_short_title)).build())
                    .setMonochromaticImage(MonochromaticImage.Builder(image = createWithResource(this, R.drawable.ic_seconds)).build())
                    .setTitle(TimeFormatComplicationText.Builder(format = "ss").build())
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

