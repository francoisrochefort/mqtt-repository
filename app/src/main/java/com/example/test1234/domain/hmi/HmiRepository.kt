package com.example.test1234.domain.hmi

import com.example.test1234.data.hmi.Hmi
import com.example.test1234.data.hmi.HmiDao

class HmiRepository(
    private val hmiDao: HmiDao
) {
    fun listAll() = hmiDao.listAll()
    fun getByHmi(id: String) = hmiDao.getById(id = id)
    fun add(hmi: Hmi) = hmiDao.add(hmi = hmi)
    fun mute(hmi: Hmi) = hmiDao.add(hmi = hmi.copy(ringing = false))
    fun unmute(hmi: Hmi) = hmiDao.add(hmi = hmi.copy(ringing = true))
    fun grantPermission(hmi: Hmi) = hmiDao.add(hmi = hmi.copy(granted = true, ringing = false))
    fun revokePermission(hmi: Hmi) = hmiDao.add(hmi = hmi.copy(granted = false, ringing = false))
    fun delete(hmi: Hmi) = hmiDao.delete(hmi = hmi)
}