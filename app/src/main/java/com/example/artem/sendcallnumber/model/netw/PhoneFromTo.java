package com.example.artem.sendcallnumber.model.netw;

/**
 * Created by artem on 10.06.17.
 */

public class PhoneFromTo {
    String FromNumber,ToNumber;
    long DateTime;

    public String getFromNumber() {
        return FromNumber;
    }

    public void setFromNumber(String fromNumber) {
        FromNumber = fromNumber;
    }

    public String getToNumber() {
        return ToNumber;
    }

    public void setToNumber(String toNumber) {
        ToNumber = toNumber;
    }

    public long getDateTime() {
        return DateTime;
    }

    public void setDateTime(long dateTime) {
        DateTime = dateTime;
    }
}
