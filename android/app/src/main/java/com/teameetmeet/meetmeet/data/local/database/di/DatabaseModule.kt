package com.teameetmeet.meetmeet.data.local.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.teameetmeet.meetmeet.data.local.database.AppDatabase
import com.teameetmeet.meetmeet.data.local.database.dao.EventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDao(database: AppDatabase): EventDao {
        return database.eventEntityDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `Event` MODIFY COLUMN `notification` INT NOT NULL")
            }
        }

        return Room
            .databaseBuilder(context, AppDatabase::class.java, "meetmeet-local.db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}