package com.example.carrental.AdminJcode;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.example.carrental.DatabaseHelper;
import com.example.carrental.RegistrationActivity;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class UserDbOperations {

    public Context context;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    public UserDbOperations(Context cntx)
    {
        this.context = cntx;
        //db related
        mDBHelper = new DatabaseHelper(context);
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (
                SQLException mSQLException) {
            throw mSQLException;
        }
    }



    //method to search for a user in DB, return a listoflist [[userType,userSummary,username],[userType,userSummary,username]]
    public List<ArrayList<String>> SearchAllUser(String lastname){

       /* List<ArrayList<String>> Allusers = new ArrayList<ArrayList<String>>();
        for(int i=0;i<10;i++) {
            ArrayList<String> eachuser = new ArrayList<String>();
            eachuser.add("Admin");
            eachuser.add("oxk5567\n4699200738\nOmkar Kyatham\nkyathamomkar@gmail.com");
            eachuser.add("oxk5567"+i);
            Allusers.add(eachuser);
        }  */
        return FindUsersinDB(lastname);
    }


    public List<ArrayList<String>> FindUsersinDB(String identifier)
    {
        List<ArrayList<String>> Allusers = new ArrayList<ArrayList<String>>();
        Allusers.clear();
        Cursor cursor = mDb.rawQuery("select username FROM user", null);
        if (cursor.getCount() > 0) {
            String query = "Select * from user where last_name like '%"+identifier+"%'";
            cursor = mDb.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                 //no user
                cursor.close();

            } else {
                cursor.moveToFirst();
                try {
                   do {
                       ArrayList<String> eachuser = new ArrayList<String>();
                       eachuser.add(cursor.getString(cursor.getColumnIndex("role")));
                       eachuser.add(cursor.getString(cursor.getColumnIndex("username")) + "\n" +
                               cursor.getString(cursor.getColumnIndex("uta_id")) + "\n" +
                               cursor.getString(cursor.getColumnIndex("first_name")) + " " +
                               cursor.getString(cursor.getColumnIndex("last_name")) + "\n" +
                               cursor.getString(cursor.getColumnIndex("email")));
                       eachuser.add(cursor.getString(cursor.getColumnIndex("username")));
                       Allusers.add(eachuser);
                   }
                   while (cursor.moveToNext());
                } finally {
                    cursor.close();
                }
            }
        }
        return Allusers;
    }

    public List<ArrayList<String>> SearchVehicles(int capacity, String startDate, String endDate) {
        String sql = "SELECT * FROM car\n" +
                "WHERE car_name NOT IN (SELECT car_name FROM car_reservation WHERE start_date > '" + startDate + "' AND end_date <'" + endDate + "') " +
                "AND capacity >= " + capacity + "ORDER BY weekly_rate ASC";

        return FindVehiclesInDB(sql);
    }


    public List<ArrayList<String>> FindVehiclesInDB(String sql)
    {
        List<ArrayList<String>> allVehicles = new ArrayList<ArrayList<String>>();
        allVehicles.clear();
        Cursor cursor = mDb.rawQuery("select car_name FROM car", null);
        if (cursor.getCount() > 0) {
            String query = sql;
            cursor = mDb.rawQuery(query, null);
            if (cursor.getCount() <= 0) {
                //no user
                cursor.close();

            } else {
                cursor.moveToFirst();
                try {
                    do {
                        ArrayList<String> eachVehicle = new ArrayList<String>();
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("car_name")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("car_number")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("capacity")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("weekday_rate")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("weekend_rate")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("weekly_rate")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("gps_rate")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("onstar_rate")));
                        eachVehicle.add(cursor.getString(cursor.getColumnIndex("siriusxm_rate")));
                        allVehicles.add(eachVehicle);
                    }
                    while (cursor.moveToNext());
                } finally {
                    cursor.close();
                }
            }
        }
        return allVehicles;
    }


}
