package org.meatball.lunch

import org.meatball.lunch.app.SlackApplication
import org.meatball.lunch.env.writePidToFile

fun main() {
    val pid = ProcessHandle.current().pid()
    writePidToFile(pid)

    SlackApplication().run()
}