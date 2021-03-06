package com.example.artem.sendcallnumber.model;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by artem on 09.06.17.
 */

public class AppState {
    private static final String SILEND = "SILEND";
    private static final String USE = "USE";
    private static final String STORE = "STORE";
    private static final String MY_NUMBER = "MY_NUMBER";
    private static final String END_POINT = "END_POINT";
    private static final String MY_NUMBER2 = "MY_NUMBER2";
    private boolean silendMode,useService,storeLocal;
    private String endPoint,myPhoneNumber,myPhoneNumber2;
    private static final AppState ourInstance = new AppState();
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    public static AppState getInstance() {
        return ourInstance;
    }

    private AppState() {
    }

    public boolean isSilendMode() {
        return silendMode;
    }

    public void setSilendMode(boolean silendMode) {
        this.silendMode = silendMode;
        editor.putBoolean(SILEND,silendMode).commit();

    }

    public boolean isUseService() {
        return useService;
    }

    public void setUseService(boolean useService) {
        this.useService = useService;
        editor.putBoolean(USE,useService);
    }

    public boolean isStoreLocal() {
        return storeLocal;
    }

    public void setStoreLocal(boolean storeLocal) {
        this.storeLocal = storeLocal;
        editor.putBoolean(STORE,storeLocal).commit();

    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        editor.putString(END_POINT,endPoint);

    }

    public String getMyPhoneNumber() {
        return myPhoneNumber;
    }

    public void setMyPhoneNumber(String myPhoneNumber) {
        this.myPhoneNumber = myPhoneNumber;
        editor.putString(MY_NUMBER,myPhoneNumber);

    }

    public String getMyPhoneNumber2() {
        return myPhoneNumber2;
    }

    public void setMyPhoneNumber2(String myPhoneNumber2) {
        this.myPhoneNumber2 = myPhoneNumber2;
        editor.putString(MY_NUMBER2,myPhoneNumber2);

    }

    public void setContext(Context context) {
        shared = context.getSharedPreferences("APP_STATE", Context.MODE_PRIVATE);
        editor = shared.edit();
        silendMode = shared.getBoolean(SILEND,false);
        storeLocal = shared.getBoolean(STORE,false);
        useService = shared.getBoolean(USE,false);
        myPhoneNumber = shared.getString(MY_NUMBER,"");
        myPhoneNumber2 = shared.getString(MY_NUMBER2,"");
        endPoint = shared.getString(END_POINT,"http://aaea7b71.ngrok.io/");

        if(myPhoneNumber.isEmpty()) {
            try {
                TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                setMyPhoneNumber(tMgr.getLine1Number());
            }catch (Exception ex){}

        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
            List<SubscriptionInfo> sil = mSubscriptionManager.getActiveSubscriptionInfoList();
            if(sil!=null && sil.size()>1){
                setMyPhoneNumber2(sil.get(1).getNumber());
            }
        }
    }

    public void commitChanges() {
        editor.commit();
    }
}
