package com.brisson.maxprocess.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.brisson.maxprocess.data.local.converter.ColorConverter
import com.brisson.maxprocess.data.local.converter.DateConverter
import com.brisson.maxprocess.data.local.converter.StringListConverter
import com.brisson.maxprocess.data.local.dao.ClientDao
import com.brisson.maxprocess.data.local.entity.ClientEntity

@Database(
    version = 1,
    entities = [ClientEntity::class],
)
@TypeConverters(DateConverter::class, StringListConverter::class, ColorConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
}
