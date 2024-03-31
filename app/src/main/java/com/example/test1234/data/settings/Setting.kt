package com.example.test1234.data.settings

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.test1234.R
import java.security.InvalidParameterException

@Entity(tableName = "settings")
data class Setting(
    @PrimaryKey
    val id: Id,
    val type: Type,
    @ColumnInfo(name = "int_value")
    val intValue: Int?,
    @ColumnInfo(name = "string_value")
    val stringValue: String?
) {
    enum class Id(val value: Int) {
        MqttServerUri(value = 1)
    }

    enum class Type(val id: Int) {
        IntValue(id = 1),
        StringValue(id = 2)
    }

    @get:Ignore
    val description: Int @StringRes get() = when(id) {
        Id.MqttServerUri -> R.string.data_setting_mqtt_server_uri
        else -> throw InvalidParameterException()
    }
}