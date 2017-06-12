package com.example.artem.sendcallnumber.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.artem.sendcallnumber.model.AppState;
import com.example.artem.sendcallnumber.model.IncommingCall;
import com.example.artem.sendcallnumber.model.db.HelperFactory;
import com.example.artem.sendcallnumber.model.netw.ApiClient;
import com.example.artem.sendcallnumber.model.netw.PhoneFromTo;
import com.example.artem.sendcallnumber.view.ListActivity;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by artem on 09.06.17.
 */

public class IncomingCallReceiver extends BroadcastReceiver {
    private static String lastState = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras()!= null) {
            String phone_state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.e("phone state",phone_state+"  "+lastState);
            if(!lastState.equals(phone_state)) {
                lastState = phone_state;
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(phone_state)) {
                    String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.e("INCOM_CALL", "WORK !!!!!!!!!  " + number + "  - " + phone_state);
                    if(AppState.getInstance().isStoreLocal()){
                        IncommingCall call = new IncommingCall();
                        call.setPhone_number(number);
                        call.setTime(new Date().getTime());
                        try {
                            List<IncommingCall> search = HelperFactory.getInstans().getDao(IncommingCall.class).queryForEq("phone_number",number);
                            if(search == null || search.size() ==0)
                                HelperFactory.getInstans().getDao(IncommingCall.class).create(call);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if(AppState.getInstance().isUseService()){
                        Observable.just(number)
                                .subscribeOn(Schedulers.io())
                                .map(numb -> {
                                    PhoneFromTo phoneFromTo = new PhoneFromTo();
                                    phoneFromTo.setFromNumber(AppState.getInstance().getMyPhoneNumber());
                                    phoneFromTo.setToNumber(numb);

                                    Response response = null;
                                    try {
                                        response = ApiClient.getInstance().send(phoneFromTo).execute();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if(response!=null && response.isSuccessful()){

                                        return true;
                                    }

                                    return false;

                                })
                                .subscribe(aBoolean -> {

                                },throwable -> {
                                        });
                    }
                }
            }
        }
    }
}
