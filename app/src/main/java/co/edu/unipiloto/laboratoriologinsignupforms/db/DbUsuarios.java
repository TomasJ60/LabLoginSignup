package co.edu.unipiloto.laboratoriologinsignupforms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

public class DbUsuarios extends DbHelper{


    Context context;

    public DbUsuarios(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarUsuarios(String name, String username, String email, String address, String birthdate, String password, String userType, String gender){
        long id = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("username", username);
            values.put("email", email);
            values.put("address", address);
            values.put("birthdate", birthdate);
            values.put("password", password);
            values.put("userType", userType);
            values.put("gender", gender);

            id = db.insert(TABLE_USUARIOS, null, values);
        }catch(Exception ex){
            ex.toString();
        }
        return id;

    }

    public boolean checkLogin(String username, String password) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean loggen = false;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS + " WHERE username=? AND password=?", new String[]{username, password});
        if (cursor.moveToFirst()){
            loggen = true;
        }
        cursor.close();
        return  loggen;
    }

    public boolean isUserRegistered(String username, String email) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS + " WHERE username=? OR email=?", new String[]{username, email});

        boolean exists = cursor.moveToFirst();

        cursor.close();
        return exists;
    }


}
