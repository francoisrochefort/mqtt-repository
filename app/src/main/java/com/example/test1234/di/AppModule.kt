package com.example.test1234.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.test1234.domain.hmi.HmiRepository
import com.example.test1234.data.Db
import com.example.test1234.data.settings.Setting
import com.example.test1234.service.KevinServiceProxy
import com.example.test1234.domain.settings.SettingRepository
import com.example.test1234.service.KevinMqttProxy

class AppModule(
    private val appContext: Context
) {
    private val db by lazy {
        Room.databaseBuilder(appContext, Db::class.java,"test1234.db")
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {

                        // Insert the default MQTT Server Uri
                        db.execSQL(
                            """
                            INSERT INTO settings (
                                id, 
                                type, 
                                int_value, 
                                string_value
                            )
                            VALUES (?, ?, ?, ?)
                            """.trimIndent(),
                            arrayOf(
                                Setting.PrimaryKeys.MqttServerUri,
                                Setting.Type.StringValue.id,
                                null,
                                "tcp://167.114.3.107:1883"
                            )
                        )
                    }
                }
            )
            .build()
    }

    private val settingDao by lazy {
        db.settingDao
    }

    val settingRepository by lazy {
        SettingRepository(settingDao = settingDao)
    }

    private val hmiDao by lazy {
        db.hmiDao
    }

    val hmiRepository by lazy {
        HmiRepository(hmiDao = hmiDao)
    }

    val kevinMqttProxy by lazy {
        KevinMqttProxy()
    }

    val kevinServiceProxy by lazy {
        KevinServiceProxy(context = appContext)
    }
}