package com.example.test1234.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test1234.data.hmi.Hmi
import com.example.test1234.data.hmi.HmiDao
import com.example.test1234.data.settings.Setting
import com.example.test1234.data.settings.SettingDao

@Database(
    version = 1,
    entities = [
        Setting::class,
        Hmi::class
    ]
)

abstract class Db : RoomDatabase() {
    abstract val settingDao: SettingDao
    abstract val hmiDao: HmiDao
}