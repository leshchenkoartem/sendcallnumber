package com.example.artem.sendcallnumber.controllers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.artem.sendcallnumber.model.AppState;
import com.example.artem.sendcallnumber.model.db.data.IncommingCall;
import com.example.artem.sendcallnumber.model.db.helpers.HelperFactory;
import com.example.artem.sendcallnumber.model.netw.ApiClient;
import com.example.artem.sendcallnumber.model.netw.PhoneFromTo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
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

                    if(AppState.getInstance().isUseService()){
                        sendToServer(number)
                                .flatMap(aBoolean -> {
                                    if(!aBoolean && AppState.getInstance().isStoreLocal())
                                        return storeLocal(number, context, intent);
                                    return Observable.just(true);
                                })
                                .subscribe(o -> {
                                    Log.e("REs",""+o);
                                });
                    }else if(AppState.getInstance().isStoreLocal()){
                        storeLocal(number, context, intent).subscribe();
                    }
                }
            }
        }
    }

    private Observable<IncommingCall> storeLocal(String number, Context context, Intent intent){
        return findContact(context,number)
                .subscribeOn(Schedulers.io())
                .map(call -> {
                    try {
                        Bundle bundle = intent.getExtras();
                        call.setSimId(bundle.getInt("simId", -1));
                    }catch (Exception ex){

                    }
                    Log.e("INCOM_CALL", "WORK !!!!!!!!!  " + call);

                    try {
                        List<IncommingCall> search = HelperFactory.getInstans().getDao(IncommingCall.class).queryForEq("phone_number",number);
                        if(search == null || search.size() ==0)
                            HelperFactory.getInstans().getDao(IncommingCall.class).create(call);
                        else {
                            search.get(0).setTime(new Date().getTime());
                            HelperFactory.getInstans().getDao(IncommingCall.class).update(search.get(0));
                        }


                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return call;
                });
    }

    private Observable<Boolean> sendToServer(String number){
        return Observable.just(number)
                .subscribeOn(Schedulers.io())
                .map(numb -> {
                    PhoneFromTo phoneFromTo = new PhoneFromTo();
                    phoneFromTo.setFromNumber(numb);
                    phoneFromTo.setToNumber(AppState.getInstance().getMyPhoneNumber());

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

                });
    }

    private Observable<IncommingCall> findContact(Context context, String iphone){
        return Observable.just(iphone)
                .subscribeOn(Schedulers.io())
                .map(phone -> {
                    IncommingCall incommingCall = new IncommingCall();
                    String select = ContactsContract.CommonDataKinds.Phone.NUMBER+" LIKE ?";
                    Cursor contactsContract = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.LABEL,
                                    ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI}
                            , select
                            , new String[] { "%"+phone }
                            , ContactsContract.CommonDataKinds.Phone.NUMBER + " ASC");
                    if (contactsContract.moveToFirst()) {
                        int id = contactsContract.getInt(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                        String name = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String number = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String photo = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        int type = contactsContract.getInt(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        String customLabel = contactsContract.getString(contactsContract.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                        CharSequence typeLabel = ContactsContract.CommonDataKinds.Email.getTypeLabel(context.getResources(), type, customLabel);
                        String phoneType = typeLabel.toString();
                        incommingCall.setPhone_number(number);
                        incommingCall.setTime(new Date().getTime());
                        incommingCall.setPhoto_uri(photo);
                        incommingCall.setName(name);
                        incommingCall.setPhone_type(phoneType);
                        return incommingCall;
                    }else {
                        incommingCall.setPhone_number(phone);
                        incommingCall.setTime(new Date().getTime());
                    }
                    return incommingCall;
                });



    }

}
