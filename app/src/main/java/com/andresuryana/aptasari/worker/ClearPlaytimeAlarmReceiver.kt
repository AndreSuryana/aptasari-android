package com.andresuryana.aptasari.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andresuryana.aptasari.util.Ext.goAsync
import com.andresuryana.aptasari.worker.WorkerUtils.resetUserPlaytime

class ClearPlaytimeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) = goAsync {
        resetUserPlaytime(context)
    }
}