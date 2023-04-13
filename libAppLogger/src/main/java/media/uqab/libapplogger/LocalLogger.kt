package media.uqab.libapplogger

import java.io.File

class LocalLogger(val logFolder: File): AppLogger() {

    init {
        try {
            if (!logFolder.exists()) logFolder.mkdirs()
        } catch (e: Exception) {

        }
    }
}