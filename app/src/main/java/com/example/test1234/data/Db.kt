package com.example.test1234.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.test1234.data.settings.Setting
import com.example.test1234.data.settings.SettingDao
import java.security.InvalidParameterException


@Database(
    version = 1,
    entities = [
        Setting::class
    ]
)
@TypeConverters(
    Db.TypeTypeConverter::class
)
abstract class Db : RoomDatabase() {
    abstract val settingDao: SettingDao

    // Type converters
    class TypeTypeConverter {

        private val Int.toType get() = when(this) {
            1 -> Setting.Type.IntValue
            2 -> Setting.Type.StringValue
            else -> throw InvalidParameterException("Invalid parameter $this")
        }

        @TypeConverter
        fun intToType(int: Int) = int.toType

        @TypeConverter
        fun typeToInt(type: Setting.Type) = type.id
    }
}