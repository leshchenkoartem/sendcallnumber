package com.example.artem.sendcallnumber.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.artem.sendcallnumber.R;
import com.example.artem.sendcallnumber.model.AppState;
import com.example.artem.sendcallnumber.model.IncommingCall;
import com.example.artem.sendcallnumber.model.db.HelperFactory;
import com.example.artem.sendcallnumber.model.netw.ApiClient;
import com.example.artem.sendcallnumber.model.netw.PhoneFromTo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private List<CheckBox> selectedChList = new ArrayList<>();

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
            incommingCalls = HelperFactory.getInstans().getDao(IncommingCall.class).queryBuilder().orderBy("time",false).query();
        } catch (SQLException e) {
            e.printStackTrace();
            incommingCalls = new ArrayList<>();
        }
    }

    @OnClick({R.id.cancelBtn, R.id.sendBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancelBtn:
                finish();
                break;
            case R.id.sendBtn:
                sendClick();
                break;
        }
    }

    private void sendClick() {
            Observable.from(selectedChList)
                    .subscribeOn(Schedulers.io())
                    .map(checkBox -> {
                        PhoneFromTo phoneFromTo = new PhoneFromTo();
                        phoneFromTo.setFromNumber(AppState.getInstance().getMyPhoneNumber());
                        phoneFromTo.setToNumber(checkBox.getText().toString());

                        Response response = null;
                        try {
                            response = ApiClient.getInstance().send(phoneFromTo).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(response!=null && response.isSuccessful()){
                                try {
                                    List l = HelperFactory.getInstans().getDao(IncommingCall.class).queryForEq("phone_number",checkBox.getText().toString());
                                    HelperFactory.getInstans().getDao(IncommingCall.class).delete(l);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }

                        return false;

                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aBoolean -> {
                        if(aBoolean) {
                            getDataForList();
                            phoneList.getAdapter().notifyDataSetChanged();
                        }}
                    ,throwable -> {
                        Toast.makeText(ListActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    });

    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_cell, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            holder.checkBox.setText(incommingCalls.get(position).getPhone_number());
            holder.checkBox.setChecked(false);
            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if(b){
                    selectedChList.add(holder.checkBox);
                }else{
                    selectedChList.remove(holder.checkBox);
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

            }
        }
    }
}
