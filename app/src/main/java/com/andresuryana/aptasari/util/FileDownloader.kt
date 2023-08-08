package com.andresuryana.aptasari.util

import android.os.Handler
import android.os.Looper
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLDecoder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FileDownloader(private val filesDir: File) {

    private var executor: ExecutorService? = null
    private val handler = Handler(Looper.getMainLooper())

    fun downloadFiles(links: List<String>, callback: DownloadCallback) {
        try {
            // Init executor
            executor = Executors.newFixedThreadPool(links.size)

            // Create a CountDownLatch with the count equal to the number of links
            val latch = CountDownLatch(links.size)

            // Run executors to execute task in the background thread
            executor?.execute {
                val files = mutableListOf<File>()
                // Loop through links
                links.forEach { link ->
                    // Get and download file
                    val filename = getFilenameFromUrl(link)

                    // Download progress
                    val file = downloadFile(link, filename, callback)
                    if (file != null) {
                        files.add(file)
                    }

                    // Decrease the CountDownLatch count after each download
                    latch.countDown()
                }

                // Wait for all downloads to complete
                latch.await()

                // Handle on complete
                handler.post {
                    callback.onDownloadCompleted(files)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onDownloadError(e.message)
        } finally {
            executor?.shutdown()
        }
    }

    fun cancelDownload() {
        executor?.shutdown()
    }

    private fun downloadFile(link: String, filename: String, callback: DownloadCallback): File? {
        return try {
            // Prepare connection
            val url = URL(link)
            val connection = url.openConnection()
            connection.connect()

            // File stream
            val inputStream = BufferedInputStream(url.openStream())
            val file = File(filesDir, filename)
            val outputStream = FileOutputStream(file)

            // Content data
            val contentLength = connection.contentLength
            val data = ByteArray(1024)
            var downloadedBytes = 0L
            var length: Int

            // Download data
            while (inputStream.read(data).also { length = it } != -1) {
                outputStream.write(data, 0, length)
                downloadedBytes += length

                // Calculate progress
                val progress = ((downloadedBytes.toDouble() / contentLength) * 100).toLong()

                // Update progress
                callback.onDownloadProgress(progress, filename)
            }

            // Resource cleaning
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onDownloadError(e.message)
            null
        }
    }

    interface DownloadCallback {
        fun onDownloadProgress(progress: Long, filename: String)
        fun onDownloadCompleted(files: List<File>)
        fun onDownloadError(message: String?)
    }

    companion object {
        fun getFilenameFromUrl(url: String): String {
            var decodedUrl = URLDecoder.decode(url, "UTF-8")
            if (decodedUrl.contains("?")) {
                decodedUrl = decodedUrl.substringBeforeLast("?")
            }
            return decodedUrl.substringAfterLast("/")
        }
    }
}