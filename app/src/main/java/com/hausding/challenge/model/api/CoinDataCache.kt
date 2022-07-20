package com.hausding.challenge.model.api

import android.content.Context
import android.widget.Toast
import com.hausding.challenge.R
import com.hausding.challenge.model.LocalDateTimeAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.IOException
import java.time.LocalDateTime

/**
 * Data class for coin data cache
 * @property syncDate Date of last sync
 * @property coins List of coins
 * @property currencies List of currencies
 * @property rates List of conversion rates
 * @property selectedCoin Last selected coin id
 * @property selectedCurrency Last selected currency id
 * @property selectedDuration Last selected duration till oldest data set in days
 */
@JsonClass(generateAdapter = true)
data class CoinDataCache(
    val syncDate: LocalDateTime,
    val coins: List<Coin>,
    val currencies: List<Currency>,
    val rates: List<ConversionRate>,
    val selectedCoin: String,
    val selectedCurrency: String,
    val selectedDuration: Int
) {
    companion object {
        private val JSON_ADAPTER = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(LocalDateTimeAdapter())
            .build()
            .adapter(CoinDataCache::class.java)

        /**
         * Function for loading cache from app data folder
         * @param context Context object for file functions and error toast display
         */
        fun loadCache(context: Context): CoinDataCache? {
            try {
                return JSON_ADAPTER.fromJson(context.openFileInput(CoinDefaults.CACHE_FILE_NAME).bufferedReader().use { it.readText() })
            } catch(ex: FileNotFoundException) {
                //No cache
            } catch (ex: JsonDataException) {
                context.deleteFile(CoinDefaults.CACHE_FILE_NAME)
                MainScope().launch {
                    Toast.makeText(context, R.string.toast_error_cache_corrupted, Toast.LENGTH_SHORT).show()

                }

            } catch (ex: IOException) {
                MainScope().launch {
                    Toast.makeText(context, R.string.toast_error_cache_access, Toast.LENGTH_SHORT).show()
                }
            }
            return null
        }

        /**
         * Function for storing cache into app data folder
         * @param context Context object for file functions and error toast display
         * @param cache CoinDataCache object
         */
        fun storeCache(context: Context, cache: CoinDataCache) {
            try {
                val output = JSON_ADAPTER.toJson(cache)
                context.openFileOutput(CoinDefaults.CACHE_FILE_NAME, Context.MODE_PRIVATE).bufferedWriter().use { it.write(output) }
            } catch (ex: Exception) {
                Toast.makeText(context, R.string.toast_error_cache_storage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}