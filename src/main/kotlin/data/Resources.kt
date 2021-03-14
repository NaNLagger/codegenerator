package data

import java.io.BufferedReader
import java.io.InputStream

object Resources {

    fun getResourceContent(path: String): String {
        val inputStream: InputStream? = Resources::class.java.getResourceAsStream(path)
        return inputStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
    }
}