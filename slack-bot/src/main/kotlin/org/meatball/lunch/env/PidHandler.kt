package org.meatball.lunch.env

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.writeText

private const val PID_FILE_NAME = "app.pid"
private const val PID_FILE_PATH = "./$PID_FILE_NAME"

fun writePidToFile(pid: Long) {
    val path = Paths.get(PID_FILE_PATH)
    if (Files.notExists(path)) {
        Files.createFile(path)
    }

    path.writeText(pid.toString())
}