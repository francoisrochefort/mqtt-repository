package com.example.test1234.domain

import com.example.test1234.data.settings.Setting
import com.example.test1234.data.settings.SettingDao
import kotlinx.coroutines.flow.Flow

class SettingRepository(
    private val settingDao: SettingDao
) {
    val mqttServerUri: Flow<String> get() = settingDao.getStringValue(Setting.Id.MqttServerUri)
}