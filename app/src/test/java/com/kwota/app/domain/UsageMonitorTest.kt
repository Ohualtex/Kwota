package com.kwota.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// UsageMonitor.nextThresholdStep saf mantığının birim testleri (DESIGN §5).
class UsageMonitorTest {

    private val step = 200L * 1024 * 1024 // 200 MB

    @Test
    fun `esik gecilmediyse null doner`() {
        val usage = 150L * 1024 * 1024 // 150 MB < 200 MB
        assertNull(UsageMonitor.nextThresholdStep(usage, step, lastNotifiedStep = 0))
    }

    @Test
    fun `ilk esik gecilince adim 1 doner`() {
        val usage = 210L * 1024 * 1024
        assertEquals(1, UsageMonitor.nextThresholdStep(usage, step, lastNotifiedStep = 0))
    }

    @Test
    fun `ayni adim tekrar bildirilmez`() {
        val usage = 210L * 1024 * 1024 // adım 1
        assertNull(UsageMonitor.nextThresholdStep(usage, step, lastNotifiedStep = 1))
    }

    @Test
    fun `birden cok adim birden gecilirse tek guncel adim doner`() {
        // 650 MB → adım 3; arada 1 ve 2 atlanmış olsa da tek sonuç (bildirim yağmuru önleme).
        val usage = 650L * 1024 * 1024
        assertEquals(3, UsageMonitor.nextThresholdStep(usage, step, lastNotifiedStep = 0))
    }
}
