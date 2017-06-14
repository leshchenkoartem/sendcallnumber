package com.example.artem.sendcallnumber.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artem.sendcallnumber.R;
import com.example.artem.sendcallnumber.model.AppState;
import com.example.artem.sendcallnumber.model.db.data.IncommingCall;
import com.example.artem.sendcallnumber.model.db.helpers.HelperFactory;
import com.example.artem.sendcallnumber.model.netw.ApiClient;
import com.example.artem.sendcallnumber.model.netw.PhoneFromTo;
import com.example.artem.sendcallnumber.util.RoundedTransformationForPicasso;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    @BindView(R.id.sendBtn)
    Button sendBtn;
    @BindView(R.id.phoneList)
    RecyclerView phoneList;
    List<IncommingCall> incommingCalls;
    private List<IncommingCall> selectedChList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        getDataForList();
        phoneList.setHasFixedSize(true);
        phoneList.setLayoutManager(new LinearLayoutManager(this));
        phoneList.setAdapter(new Adapter());
    }

    private void getDataForList() {
        try {
            incommingCalls = HelperFactory.getInstans().getDao(IncommingCall.class).queryBuilder().orderBy("time", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
            incommingCalls = new ArrayList<>();
        }
    }

    @OnClick({R.id.cancelBtn, R.id.sendBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancelBtn:
                removeClick();
                break;
            case R.id.sendBtn:
                sendClick();
                break;
        }
    }

    private void removeClick() {
        Observable.from(selectedChList)
                //.subscribeOn(Schedulers.newThread())
                .map(incommingCall -> {
                    List l = null;
                    try {
                        l = HelperFactory.getInstans().getDao(IncommingCall.class).queryForEq("phone_number", incommingCall.getPhone_number());
                        HelperFactory.getInstans().getDao(IncommingCall.class).delete(l);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return true;
                })
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    getDataForList();
                    phoneList.getAdapter().notifyDataSetChanged();
                });

    }

    private void sendClick() {
        Observable.from(selectedChList)
                .subscribeOn(Schedulers.io())
                .map(incommingCall -> {
                    PhoneFromTo phoneFromTo = new PhoneFromTo();
                    phoneFromTo.setFromNumber(incommingCall.getPhone_number());
                    phoneFromTo.setToNumber(AppState.getInstance().getMyPhoneNumber());
                    phoneFromTo.setDateTime(incommingCall.getTime());


                    Response response = null;
                    try {
                        response = ApiClient.getInstance().send(phoneFromTo).execute();
                        if (response.isSuccessful())
                            return incommingCall;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    return null;

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                            if (s != null) {
                                try {
                                    List l = HelperFactory.getInstans().getDao(IncommingCall.class).queryForEq("phone_number", s.getPhone_number());
                                    HelperFactory.getInstans().getDao(IncommingCall.class).delete(l);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                getDataForList();
                                phoneList.getAdapter().notifyDataSetChanged();
                            }
                        }
                        , throwable -> {
                            Toast.makeText(ListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        });

    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_cell, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.phoneNumberTv.setText(incommingCalls.get(position).getPhone_number());
            String simID = incommingCalls.get(position).getSimId()>=0?"sim:"+incommingCalls.get(position).getSimId()+" ":"";
            holder.phoneInfoTv.setText(simID + incommingCalls.get(position).getPhone_type());
            if(incommingCalls.get(position).getPhoto_uri()!=null && incommingCalls.get(position).getPhoto_uri().length()>0){
                Picasso.with(holder.avaImg.getContext())
                        .load(Uri.parse((incommingCalls.get(position).getPhoto_uri())))
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit().centerCrop()
                        .transform(new RoundedTransformationForPicasso(50, 0))
                        .into(holder.avaImg);
            }else{
                Picasso.with(holder.avaImg.getContext())
                        .load(R.mipmap.ic_launcher)
                        .fit().centerCrop()
                        .transform(new RoundedTransformationForPicasso(50, 0))
                        .into(holder.avaImg);
            }
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy hh:mm");
            String date = format.format(new Date(incommingCalls.get(position).getTime()));
            holder.timeTv.setText(date);

            holder.checkBox.setChecked(false);
            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    selectedChList.add(incommingCalls.get(position));
                } else {
                    selectedChList.remove(incommingCalls.get(position));
                }

            });
        }

        @Override
        public int getItemCount() {
            return incommingCalls.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.checkBox)
            CheckBox checkBox;
            @BindView(R.id.avaImg)
            ImageView avaImg;
            @BindView(R.id.phoneNumberTv)
            TextView phoneNumberTv;
            @BindView(R.id.phoneInfoTv)
            TextView phoneInfoTv;
            @BindView(R.id.timeTv)
            TextView timeTv;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }


    }
}
