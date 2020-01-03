package com.master.machines.loginandregister.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseUser extends SQLiteOpenHelper {

    public DataBaseUser(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL("CREATE TABLE user" +
                    "(id INTEGER PRIMARY KEY not null unique," +
                    "u_photo INT NOT NULL," +
                    "u_name TEXT NOT NULL," +
                    "u_lastName TEXT NOT NULL," +
                    "u_addres TEXT NOT NULL)");
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (SQLException e) {
            throw e;
        } catch (Throwable th) {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }
}
