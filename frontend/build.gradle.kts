import com.github.gradle.node.yarn.task.YarnTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.github.node-gradle.node") version "3.0.1"
}

node {
    version.set("18.19.1")
    yarnVersion.set("1.22.22")
    download.set(true)
    workDir.set(file("${project.projectDir}/.gradle/nodejs"))
    yarnWorkDir.set(file("${project.projectDir}/.gradle/yarn"))
    nodeProjectDir.set(file("${project.projectDir}/node_modules"))
}

tasks {
    val installDependencies by registering(YarnTask::class) {
        args.set(listOf("install"))
    }

    val buildFrontend by registering(YarnTask::class) {
        dependsOn(installDependencies)
        args.set(listOf("build"))
    }

    val startFrontend by registering(YarnTask::class) {
        dependsOn(buildFrontend)
        doLast {
            runFrontendTask("start")
        }
    }

    val devFrontend by registering {
        dependsOn(installDependencies)
        doLast {
            runFrontendTask("dev")
        }
    }
}

fun runFrontendTask(taskName: String) {
    val processBuilder = ProcessBuilder("yarn", taskName)
    processBuilder.directory(file("${project.projectDir}"))
    val process = processBuilder.start()

    val logFile = File("${project.projectDir}/shutdown.log")

    Runtime.getRuntime().addShutdownHook(Thread {
        try {
            appendLog(logFile, "Attempting to stop process...")
            appendLog(
                logFile, "Main process: PID=${process.pid()} Command=${
                    process.info().commandLine().orElse("Unknown")
                }"
            )

            killProcessTree(process, logFile)
            appendLog(logFile, "Process stopped successfully.")
        } catch (e: Exception) {
            appendLog(logFile, "Error during shutdown: ${e.message}")
        }
    })

    val reader = BufferedReader(InputStreamReader(process.inputStream))
    reader.lines().forEach { println(it) }

    val exitCode = process.waitFor()
    if (exitCode != 0) {
        throw GradleException("yarn $taskName finished with non-zero exit code: $exitCode")
    }
}

fun killProcessTree(process: Process, logFile: File) {
    val processHandle = process.toHandle()
    val children = processHandle.children().toList()

    appendLog(logFile, "Killing child processes...")
    children.forEach { child ->
        appendLog(
            logFile, "Child process: PID=${child.pid()} Command=${
                child.info().commandLine().orElse("Unknown")
            }"
        )
        child.destroy()
    }

    appendLog(logFile, "Attempting to identify and kill next-server process...")
    try {
        // We need to manually find and kill the next-server process because it's not a child of the main process
        val nextServerPid = findNextServerProcessPid()
        if (nextServerPid != null) {
            appendLog(logFile, "Found next-server process: PID=$nextServerPid")
            try {
                val killCommand = listOf("kill", "-9", nextServerPid.toString())
                val killProcess = ProcessBuilder(killCommand)
                    .redirectErrorStream(true)
                    .start()

                val killOutput = BufferedReader(InputStreamReader(killProcess.inputStream)).readText()
                appendLog(logFile, "Kill command output: $killOutput")

                val exitCode = killProcess.waitFor()
                if (exitCode == 0) {
                    appendLog(logFile, "next-server process killed successfully.")
                } else {
                    appendLog(logFile, "Failed to kill next-server process. Exit code: $exitCode")
                }
            } catch (e: Exception) {
                appendLog(logFile, "Error killing next-server process: ${e.message}")
            }
        } else {
            appendLog(logFile, "next-server process not found.")
        }
    } catch (e: Exception) {
        appendLog(logFile, "Error identifying/killing next-server: ${e.message}")
    }

    appendLog(logFile, "Killing main process...")
    process.destroy()

    children.forEach { child ->
        child.onExit().join()
    }
    processHandle.onExit().join()

    appendLog(logFile, "All processes terminated.")
}

fun appendLog(logFile: File, message: String) {
    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    logFile.appendText("[$timestamp] $message\n")
}

fun findNextServerProcessPid(): Long? {
    val processBuilder = ProcessBuilder("pgrep", "-f", "next-server")
    val process = processBuilder.start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val pids = reader.readLines().mapNotNull { it.toLongOrNull() }
    return pids.firstOrNull()
}