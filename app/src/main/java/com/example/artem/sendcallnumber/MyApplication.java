package com.example.artem.sendcallnumber;

import android.app.Application;

import com.example.artem.sendcallnumber.model.AppState;
import com.example.artem.sendcallnumber.model.db.HelperFactory;

/**
 * Created by artem on 09.06.17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
        AppState.getInstance().setContext(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();

    }
}
