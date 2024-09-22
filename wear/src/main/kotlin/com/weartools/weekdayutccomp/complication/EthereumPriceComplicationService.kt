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

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.graphics.drawable.Icon.createWithResource
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationText
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.weartools.weekdayutccomp.R.drawable
import com.weartools.weekdayutccomp.preferences.UserPreferences
import com.weartools.weekdayutccomp.preferences.UserPreferencesRepository
import com.weartools.weekdayutccomp.receiver.ComplicationTapBroadcastReceiver
import com.weartools.weekdayutccomp.receiver.ComplicationToggleArgs
import com.weartools.weekdayutccomp.utils.CryptoHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class EthereumPriceComplicationService : SuspendingComplicationDataSourceService() {

    @Inject
    lateinit var dataStore: DataStore<UserPreferences>
    private val preferences by lazy { UserPreferencesRepository(dataStore).getPreferences() }

    private var price = 0f
    private var highPrice = 0f
    private var lowPrice = 0f
    private var shortPattern = "#.##K"
    private var longPattern = "$#,###"

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = "2.45K").build(),
                    contentDescription = ComplicationText.EMPTY)
                    .setMonochromaticImage(MonochromaticImage.Builder(image = createWithResource(this, drawable.ic_ethereum)).build())
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = "$2,450").build(),
                    contentDescription = ComplicationText.EMPTY)
                    .setTitle(PlainComplicationText.Builder(text = "ETH").build())
                    .setMonochromaticImage(MonochromaticImage.Builder(createWithResource(this, drawable.ic_ethereum)).build())
                    .build()
            }
            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = 75F,
                    min = 0F,
                    max = 100F,
                    contentDescription = ComplicationText.EMPTY)
                    .setText(PlainComplicationText.Builder(text = "2.45K").build())
                    .setMonochromaticImage(MonochromaticImage.Builder(createWithResource(this, drawable.ic_ethereum)).build())
                    .build()
            }
            else -> {null}
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val args = ComplicationToggleArgs(providerComponent = ComponentName(this, javaClass), complicationInstanceId = request.complicationInstanceId)
        val complicationPendingIntent = ComplicationTapBroadcastReceiver.getToggleIntent(context = this, args = args)

        //GET CURRENT PRICE
        val ethereumPrice = CryptoHelper.fetchEthereumPrice()
        if (ethereumPrice != null){
            price = ethereumPrice.last
            highPrice = ethereumPrice.high
            lowPrice = ethereumPrice.low
            dataStore.updateData { it.copy(priceETH = ethereumPrice.last) }
            shortPattern = "#.##K"
            longPattern = "$#,###"
        }
        else {
            price = preferences.first().priceETH
            shortPattern = "#.##K!"
            longPattern = "$#,###!"
        }

        val priceString =
            if (price >= 1000.00) { DecimalFormat(shortPattern).apply { RoundingMode.HALF_UP }.format(price/1000.0) }
            else { DecimalFormat(shortPattern).format(price) }

        return when (request.complicationType) {

            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = priceString).build(),
                    contentDescription = PlainComplicationText.Builder(text = "ETH").build())
                    .setMonochromaticImage(MonochromaticImage.Builder(createWithResource(this, drawable.ic_ethereum)).build())
                    .setTapAction(complicationPendingIntent)
                    .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = DecimalFormat(longPattern).format(price.toInt())).build(),
                    contentDescription = PlainComplicationText.Builder(text = "ETH").build())
                    .setTitle(PlainComplicationText.Builder(text = "ETH").build())
                    .setMonochromaticImage(MonochromaticImage.Builder(createWithResource(this, drawable.ic_ethereum)).build())
                    .setTapAction(complicationPendingIntent)
                    .build()
            }
            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = if (ethereumPrice == null) lowPrice else price,
                    min = lowPrice,
                    max =  highPrice,
                    contentDescription = PlainComplicationText.Builder(text = "ETH").build())
                    .setText(PlainComplicationText.Builder(text = priceString).build())
                    .setMonochromaticImage(MonochromaticImage.Builder(createWithResource(this, drawable.ic_ethereum)).build())
                    .setTapAction(complicationPendingIntent)
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

