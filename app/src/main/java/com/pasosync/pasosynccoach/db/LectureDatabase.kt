package com.pasosync.pasosynccoach.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pasosync.pasosynccoach.other.Constant.LECTURE_DATABASE_NAME


@Database(entities = [Lectures::class],
    version = 1)
abstract class LectureDatabase:RoomDatabase() {
    abstract fun getLectureDao():LectureDao
    companion object{

        //singleton object
        @Volatile //so that other thread can see if it change its instance
        private var instance:LectureDatabase?=null //checking null
        private val LOCK=Any()


        operator fun invoke(context: Context)= instance?: synchronized(LOCK){
            instance?:createDatabase(context).also{
                instance=it
            }
        }

        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,LectureDatabase::class.java
                ,LECTURE_DATABASE_NAME
            ).addMigrations().build()

    }

}