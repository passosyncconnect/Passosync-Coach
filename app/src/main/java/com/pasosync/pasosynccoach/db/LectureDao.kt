package com.pasosync.pasosynccoach.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LectureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectures(lectures: Lectures)


    @Query("SELECT * FROM lectures")
    fun getAllLectures(): LiveData<List<Lectures>>

    @Query("SELECT * FROM lectures ORDER BY id DESC LIMIT 3")
    fun getLatestLectures(): LiveData<List<Lectures>>
}