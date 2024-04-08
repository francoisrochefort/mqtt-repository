package com.example.test1234.data.hmi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hmis")
data class Hmi(
    @PrimaryKey
    val id: String,
    val operator: String,
    val company: String,
    val tell : String,
    val granted: Boolean = false,

    // Toggled to false whenever a permission is granted or denied/revoked
    val ringing: Boolean = true
) {

}