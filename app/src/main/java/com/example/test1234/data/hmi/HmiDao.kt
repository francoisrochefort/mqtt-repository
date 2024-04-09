package com.example.test1234.data.hmi

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HmiDao {

    @Query("SELECT * FROM hmis ORDER BY company, operator ASC")
    fun listAll() : Flow<List<Hmi>>

    @Query("SELECT * FROM hmis WHERE id = :id")
    fun getById(id: String) : Hmi?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(hmi: Hmi)

    @Delete
    fun delete(hmi: Hmi)
}