package com.example.artem.sendcallnumber.model.db.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;

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
    @DatabaseField
    int id = -1; // Androids REVIEW_DATAg
    @DatabaseField
    String phone_type;
    @DatabaseField(index = true)
    String name;
    @DatabaseField
    String photo_uri = "";
    @DatabaseField
    int simId;




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

    public String getPhone_type() {
        return phone_type;
    }

    public void setPhone_type(String phone_type) {
        this.phone_type = phone_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public int getSimId() {
        return simId;
    }

    public void setSimId(int simId) {
        this.simId = simId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" phone_number-"+phone_number);
        sb.append(" time-"+time);
        sb.append(" phone_type-"+phone_type);
        sb.append(" name-"+name);
        sb.append(" simID-"+simId);
        return sb.toString();
    }
}
