package com.naamini.tenakataapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.naamini.tenakataapp.dao.StudentDao;
import com.naamini.tenakataapp.model.Student;

/**
 * Created by Naamini Yonazi on 19/07/20
 */
@Database(entities = {Student.class}, version = 1, exportSchema = false)
public abstract class StudentDatabaseConfig extends RoomDatabase {
    private static StudentDatabaseConfig INSTANCE;
    public abstract StudentDao studentDao();

    public static StudentDatabaseConfig getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StudentDatabaseConfig.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StudentDatabaseConfig.class, "student_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };
}
