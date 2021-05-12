package io.vlinx.chia.plotter

import io.vlinx.configutils.YAMLUtils
import io.vlinx.logging.Logger
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    if (args.isEmpty()) {
        System.err.println("Plotter <settings-file>")
        exitProcess(1)
    }
    try {
        val settingsFilePath = args[0]
        val settings = YAMLUtils.loadObject(settingsFilePath, Settings::class.java) as Settings
        if (!settings.logDir.isNullOrBlank()) {
            Logger.SET_LOG_DIR(settings.logDir)
        }

        Logger.INFO("${App.NAME} ${App.VERSION}")

        val plotter = Plotter(settings)
        plotter.start()
    } catch (e: Exception) {
        Logger.ERROR(e)
        exitProcess(1)
    }

}