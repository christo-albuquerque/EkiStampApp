package com.ekistamp.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EkiStampEntity::class], version = 1, exportSchema = true)
abstract class EkiStampDatabase : RoomDatabase() {

    abstract fun ekiStampDao(): EkiStampDao

    companion object {
        fun create(context: Context): EkiStampDatabase =
            Room.databaseBuilder(context, EkiStampDatabase::class.java, "eki_stamps.db").build()
    }
}
