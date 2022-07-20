package com.hausding.challenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hausding.challenge.model.api.CoinDataCache
import com.hausding.challenge.model.api.CoinDefaults
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

/**
 * Test class for cache tests
 */
@RunWith(AndroidJUnit4::class)
class CacheTest {
    /**
     * Check that only existing cache can be loaded
     */
    @Test
    fun testCacheCreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteFile(CoinDefaults.CACHE_FILE_NAME)
        Assert.assertNull(CoinDataCache.loadCache(context))
        CoinDataCache.storeCache(
            context,
            CoinDataCache(
                LocalDateTime.now(),
                listOf(),
                listOf(),
                listOf(),
                "",
                "",
                14
            )
        )
        Assert.assertNotNull(CoinDataCache.loadCache(context))
        context.deleteFile(CoinDefaults.CACHE_FILE_NAME)
        Assert.assertNull(CoinDataCache.loadCache(context))
    }
}