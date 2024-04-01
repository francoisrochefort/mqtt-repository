package com.example.test1234.data.settings

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Query("SELECT string_value FROM settings WHERE id = :id")
    fun getStringValue(id: Setting.PrimaryKeys) : Flow<String>
}