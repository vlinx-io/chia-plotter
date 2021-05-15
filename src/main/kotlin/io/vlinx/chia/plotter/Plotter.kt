package io.vlinx.chia.plotter

import io.vlinx.logging.Logger
import io.vlinx.processutils.ProcessUtils
import org.apache.commons.lang3.RandomStringUtils


/**
 * @author vlinx <vlinx@vlinx.io>
 * @create 2021-05-11
 * @version 1.0.0
 */

class Plotter(private val settings: Settings) {


    private var runningTasks = 0
    private var totalTasks = 0;

    fun start() {
        if (settings.taskCount < 1) {
            throw Exception("Task count is ${settings.taskCount}")
        }

        if (settings.tempFolders.isEmpty()) {
            throw Exception("No temp folder is specified")
        }

        if (settings.outputFolders.isEmpty()) {
            throw Exception("No output folder is specified")
        }

        var stopFlag = false;
        while (true) {
            while (runningTasks >= settings.taskCount) {
                Thread.sleep(1000)
            }

            if (totalTasks >= settings.taskCount && !settings.unstopped) {

                while (runningTasks > 0) {
                    Thread.sleep(1000)
                }

                stopFlag = true

            }

            if (stopFlag) {
                break
            }

            var taskId = "Task $totalTasks"

            val taskIndex = totalTasks % settings.taskCount
            val runnable = Runnable {
                try {
                    startTask(taskIndex, taskId)
                } catch (e: Exception) {
                    Logger.ERROR(taskId, e.message)
                }finally {
                    runningTasks--
                }
            }
            val thread = Thread(runnable)
            thread.isDaemon = true
            thread.start()
            runningTasks++
            totalTasks++

            if(totalTasks <= settings.taskCount) {
                if (settings.taskDelay > 0) {
                    Logger.INFO(taskId, "Wait ${settings.taskDelay}s to start another task")
                    Thread.sleep((settings.taskDelay * 1000).toLong())
                }
            }


        }


    }

    private fun startTask(taskIndex: Int, taskId: String) {
        Logger.INFO(taskId, "Start")
        val builder = StringBuilder()
        builder.append("/bin/bash -c 'source ${settings.chiaLocation}/activate && chia ")
        val tempFolder = settings.tempFolders[taskIndex % settings.tempFolders.size]
        val outputFolder = settings.outputFolders[taskIndex % settings.outputFolders.size]
        Logger.INFO(taskId, "Temp folder: $tempFolder")
        Logger.INFO(taskId, "Output folder: $outputFolder")
        builder.append("plots create -t $tempFolder -d $outputFolder ")

        val k = if (settings.k.size == 0) 0 else settings.k[taskIndex % settings.k.size]
        if (k > 0) {
            builder.append(" -k $k ")
        }

        val r = if (settings.threads.size == 0) 0 else settings.threads[taskIndex % settings.threads.size]
        if (r > 0) {
            builder.append(" -r $r ")
        }

        var b = if (settings.bufferSize.size == 0) 0 else settings.bufferSize[taskIndex % settings.bufferSize.size]
        if (b > 0) {
            builder.append(" -b $b ")
        }

        var u = if (settings.buckets.size == 0) 0 else settings.buckets[taskIndex % settings.buckets.size]
        if (u > 0) {
            builder.append(" -u $u")
        }

        if (settings.overrideK) {
            builder.append(" --override-k ")
        }

        builder.append("'")

        val command = builder.toString()
        Logger.INFO(taskId, command)

        ProcessUtils.run(command, true)
        Logger.INFO(taskId, "Completed")
    }


}