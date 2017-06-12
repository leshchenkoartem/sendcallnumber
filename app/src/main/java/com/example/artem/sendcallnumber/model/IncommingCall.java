package com.example.artem.sendcallnumber.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by artem on 09.06.17.
 */
@DatabaseTable(tableName = "IncommingCall")
public class IncommingCall {
    @DatabaseField(generatedId = true, index = true)
    int _id;
    @DatabaseField
    String phone_number;
    @DatabaseField
    long time;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
