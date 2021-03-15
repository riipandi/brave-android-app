package com.celzero.bravedns.download

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.celzero.bravedns.receiver.ReceiverHelper
import com.celzero.bravedns.ui.HomeScreenActivity
import com.celzero.bravedns.util.Constants
import com.celzero.bravedns.util.Constants.Companion.LOG_TAG
import java.io.File

class FileHandleWorker(val context: Context, workerParameters: WorkerParameters)
        : Worker(context, workerParameters){

    override fun doWork(): Result {
        try{
            val response = copyFiles(context)

            val outputData = workDataOf(DownloadConstants.OUTPUT_FILES to response)

            return if(response) Result.success(outputData)
            else Result.retry()

        }catch (e : Exception){
            Log.w(LOG_TAG, "FileHandleWorker Error while moving files to canonical path ${e.message}")
        }
        return Result.failure()
    }

    private fun copyFiles(context : Context): Boolean {
        val timeStamp = ReceiverHelper.persistentState.tempBlocklistDownloadTime
        if (DownloadHelper.isLocalDownloadValid(context, timeStamp.toString())) {
            DownloadHelper.deleteFromCanonicalPath(context)
            if (HomeScreenActivity.GlobalVariable.DEBUG) Log.d(LOG_TAG, "AppDownloadManager Copy file Directory isLocalDownloadValid- true")
            val dir = File(DownloadHelper.getExternalFilePath(context, timeStamp.toString()))
            if (HomeScreenActivity.GlobalVariable.DEBUG) Log.d(LOG_TAG, "AppDownloadManager Copy file Directory- ${dir.path}")
            if (dir.isDirectory) {
                val children = dir.list()
                if (children != null && children.isNotEmpty()) {
                    for (i in children.indices) {
                        if (HomeScreenActivity.GlobalVariable.DEBUG) Log.d(LOG_TAG, "AppDownloadManager Copy file - ${children[i]}")
                        val from = File(dir, children[i])
                        if (HomeScreenActivity.GlobalVariable.DEBUG) Log.d(LOG_TAG, "AppDownloadManager Copy file from - ${from.path}")
                        val to = File("${context.filesDir.canonicalPath}/$timeStamp/${children[i]}")
                        Log.i(LOG_TAG, "AppDownloadManager Copy file to - ${to.path}")
                        from.copyTo(to, true)
                    }
                    val destinationDir = File("${context.filesDir.canonicalPath}/$timeStamp")
                    if (destinationDir.isDirectory) {
                        val child = destinationDir.list()
                        if (child?.size == Constants.LOCAL_BLOCKLIST_FILE_COUNT) {
                            //Update the shared pref values
                            ReceiverHelper.persistentState.localBlockListDownloadTime = timeStamp
                            ReceiverHelper.persistentState.localBlocklistEnabled = true
                            ReceiverHelper.persistentState.blockListFilesDownloaded = true
                            ReceiverHelper.persistentState.tempBlocklistDownloadTime = 0
                            DownloadHelper.deleteOldFiles(context)
                            return true
                        }
                    }

                }
            }
            return false
        }
        return false
    }


}