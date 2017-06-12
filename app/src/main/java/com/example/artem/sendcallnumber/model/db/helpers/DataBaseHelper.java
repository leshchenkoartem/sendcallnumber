package com.example.artem.sendcallnumber.model.db.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.artem.sendcallnumber.model.db.data.IncommingCall;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "db_send_call";
    private static final int DATABASE_VERSION = 1;
    ConnectionSource connectionSource;

    public DataBaseHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, IncommingCall.class);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    @Override
    public void close() {
        super.close();
    }

}
