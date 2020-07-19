package com.naamini.tenakataapp;

import android.content.Context;
import android.os.AsyncTask;

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
//            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    /**
     * Populate the database in the background.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final StudentDao mDao;
//        String[] students = {"Naamini", "Frank", "You"};

        PopulateDbAsync(StudentDatabaseConfig db) {
            mDao = db.studentDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created

           /* mDao.deleteAll();
            for (int i = 0; i <= students.length - 1; i++) {
                student student = new student(students[i]);
                mDao.insert(student);
            }*/

            // If we have no students, then create the initial list of students
            /*if (mDao.getAnyStudent().length < 1) {
                for (int i = 0; i <= students.length - 1; i++) {
                    Student student = new Student(students[i]);
                    mDao.insert(student);
                }
            }*/
            return null;
        }
    }
}
