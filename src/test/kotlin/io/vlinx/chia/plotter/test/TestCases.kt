package io.vlinx.chia.plotter.test

import io.vlinx.chia.plotter.Plotter
import io.vlinx.chia.plotter.Settings
import io.vlinx.configutils.YAMLUtils
import org.junit.Test

class TestCases {

    @Test
    fun testCreateSettingsFile() {
        val settings = Settings()
        YAMLUtils.saveObject(settings, "settings.yml")
    }

    @Test
    fun testPlotter() {
        val settings = Settings()
        settings.chiaLocation = "/Users/vlinx/Soft/chia-blockchain"
        settings.taskCount = 8
        settings.taskDelay = 0
        settings.tempFolders.add("/Users/vlinx/Test/chia/temp")
        settings.outputFolders.add("/Users/vlinx/Test/chia/output")
        settings.k.add(25)
        settings.bufferSize.add(1600)
        settings.overrideK = true
        val plotter = Plotter(settings)
        plotter.start()
    }

}