package com.example.artem.sendcallnumber.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.artem.sendcallnumber.R;
import com.example.artem.sendcallnumber.model.AppState;
import com.example.artem.sendcallnumber.model.db.data.IncommingCall;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.silent_mode_chb)
    CheckBox silentModeChb;
    @BindView(R.id.storelocal_chb)
    CheckBox storelocalChb;
    @BindView(R.id.point_edit)
    EditText pointEdit;
    @BindView(R.id.phone_edit)
    EditText phoneEdit;
    @BindView(R.id.useservice_chb)
    CheckBox useserviceChb;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;

    boolean b;
    @BindView(R.id.phone_edit2)
    EditText phoneEdit2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        silentModeChb.setChecked(AppState.getInstance().isSilendMode());
        storelocalChb.setChecked(AppState.getInstance().isStoreLocal());
        useserviceChb.setChecked(AppState.getInstance().isUseService());

        pointEdit.setText(AppState.getInstance().getEndPoint());
        phoneEdit.setText(AppState.getInstance().getMyPhoneNumber());
        String phone2 = AppState.getInstance().getMyPhoneNumber2();
        if(phone2!=null && !phone2.isEmpty()){
            phoneEdit2.setVisibility(View.VISIBLE);
            phoneEdit2.setText(phone2);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!b) {
            b = true;
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
            }
        }
    }
    @OnClick(R.id.button)
    public void onBClicked(View view) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PHONE_STATE2");
        intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER,"321");
        intent.putExtra(TelephonyManager.EXTRA_STATE,TelephonyManager.EXTRA_STATE_RINGING);
        sendBroadcast(intent);
    }
    @OnClick({R.id.silent_mode_chb, R.id.storelocal_chb, R.id.point_edit, R.id.phone_edit, R.id.useservice_chb, R.id.cancelBtn, R.id.saveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.silent_mode_chb:
                break;
            case R.id.storelocal_chb:
                break;
            case R.id.point_edit:
                break;
            case R.id.phone_edit:
                break;
            case R.id.useservice_chb:
                break;
            case R.id.cancelBtn:
                cancelClick();
                break;
            case R.id.saveBtn:
                saveClick();
                break;
        }
    }

    private void saveClick() {
        AppState.getInstance().setStoreLocal(storelocalChb.isChecked());
        AppState.getInstance().setSilendMode(silentModeChb.isChecked());
        AppState.getInstance().setUseService(useserviceChb.isChecked());
        AppState.getInstance().setEndPoint(pointEdit.getText().toString());
        AppState.getInstance().setMyPhoneNumber(phoneEdit.getText().toString());
        AppState.getInstance().commitChanges();
        finish();
    }

    private void cancelClick() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

    }



}
