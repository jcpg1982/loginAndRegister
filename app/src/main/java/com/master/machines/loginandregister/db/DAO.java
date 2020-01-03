package com.master.machines.loginandregister.db;

import android.database.Cursor;

import com.master.machines.loginandregister.model.User;

public class DAO {

    public static boolean addUser(DataBaseUser dbu, User user) {
        try {
            dbu.getWritableDatabase().execSQL("INSERT INTO user (u_photo,u_name,u_lastName,u_addres)" +
                    "VALUES ('" + user.getPhoto() + "','" + user.getName() + "','" + user.getLastName() + "','" + user.getAddress() + "')");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Cursor getUser(DataBaseUser dbu) {
        try {
            Cursor fila = dbu.getReadableDatabase().rawQuery("SELECT * FROM user", null);
            if (fila.moveToFirst()) {
                return fila;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean updateUser(DataBaseUser dbu, User user) {
        try {
            dbu.getWritableDatabase().execSQL("UPDATE user set " +
                    "u_photo='" + user.getPhoto() + "'," +
                    "u_name='" + user.getName() + "'," +
                    "u_lastName='" + user.getLastName() + "'," +
                    "u_addres='" + user.getAddress() + "' " +
                    "WHERE id = " + user.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteUser(DataBaseUser dbu, User user) {
        try {
            dbu.getWritableDatabase().execSQL("DELETE FROM usuario WHERE id = " + user.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
