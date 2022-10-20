package com.egyptfwd.loadapp

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.egyptfwd.loadapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var selectedURL: String
    private lateinit var selectedFileName: String

    private lateinit var downloadStatus: String

    private val notificationID = 0

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
            CHANNEL_ID,
            "Downloaded Files"
        )

        binding.contentMain.customButton.setOnClickListener {

            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.cancelNotifications()
            if (binding.contentMain.contentMainRadioGp
                    .checkedRadioButtonId == -1
            ) {
                Toast.makeText(
                    this,
                    "Select a button",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                when (binding.contentMain.contentMainRadioGp
                    .indexOfChild(findViewById(
                        binding.contentMain.contentMainRadioGp
                            .checkedRadioButtonId
                    ))) {
                    0 -> {
                        selectedURL = GlideURL
                        selectedFileName = "Glide Library"
                    }
                    1 -> {
                        selectedURL = ProjectURL
                        selectedFileName = "Load App Starter Code"
                    }
                    else -> {
                        selectedURL = RetrofitURL
                        selectedFileName = "Retrofit Library"
                    }
                }
                binding.contentMain.customButton.buttonState = ButtonState.Loading
                download(selectedURL)

            }

        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val action = intent?.action

            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))
                    val downloadManager =
                        context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            downloadStatus = if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                "Success"
                            } else {
                                "Fail"
                            }
                        }
                    }

                    Log.i("details", downloadStatus)
                }
            }
            binding.contentMain.customButton.buttonState=ButtonState.Completed
            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification(
                selectedFileName,
                downloadStatus,
                applicationContext
            )


        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun download(selectedURL: String) {
        val request =
            DownloadManager.Request(Uri.parse(selectedURL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
//                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GlideURL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"

        private const val ProjectURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"

        private const val RetrofitURL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private const val CHANNEL_ID = "channelId"
    }



    @SuppressLint("UnspecifiedImmutableFlag")
    fun NotificationManager.sendNotification(
        fileName: String,
        status: String,
        applicationContext: Context
    ) {

        val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        contentIntent.apply {
            putExtra("fileName", fileName)
            putExtra("status", status)
        }

        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        action = NotificationCompat.Action.Builder(
            0,
            "Check the status",
            pendingIntent
        ).build()

        val builder = NotificationCompat.Builder(
            applicationContext,
            "channelId"
        )
            .setSmallIcon(R.drawable.ic_assistant_black)
            .setContentTitle(fileName)
            .setContentText(status)
            .setContentIntent(pendingIntent)
            .addAction(action)
            .setAutoCancel(true)

        notify(notificationID, builder.build())
    }

    private fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = resources.getString(R.string.notification_description)
            val notificationManager = applicationContext.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
