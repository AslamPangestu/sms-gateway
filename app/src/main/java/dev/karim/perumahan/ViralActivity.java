package dev.karim.perumahan;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.karim.perumahan.model.Counter;
import dev.karim.perumahan.model.Viral;
import dev.karim.perumahan.network.CustomRequest;
import dev.karim.perumahan.network.NetworkResponse;
import dev.karim.perumahan.network.Networks;
import dev.karim.perumahan.network.Route;
import retrofit2.Response;

import android.text.InputType;

import static dev.karim.perumahan.BuildConfig.PASSKEY;
import static dev.karim.perumahan.BuildConfig.USERKEY;

public class ViralActivity extends AppCompatActivity {

    //Layout
    @BindView(R.id.main_viral) LinearLayout mainLayout;
    @BindView(R.id.submit) Button btnSubmit;
    @BindView(R.id.add_receiver) Button btnAddReceiver;
    @BindView(R.id.message) EditText etMessage;
    @BindView(R.id.sender) EditText etSender;
    @BindView(R.id.max_quota) TextView tvMaxQuota;
    @BindView(R.id.quota_counter) TextView tvQuota;
    @BindView(R.id.warning_quota) TextView tvWarning;

    //generic textView
    private TextView tvLog;

    //Content
    private String sender;
    private String message;

    //Counter View
    private int nEtReceiver = 0;
    private int countView = 0;

    //counter logic
    private int maxReceiver = 20;
    private int tmp, currentReceiver, currentMonth, lastMonth;
    private int pos = 3;
    private static int ID = 1;

    private List<EditText> texts = new ArrayList<>();
    private String[] newReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viral);

        ButterKnife.bind(this);
        Calendar calendar = Calendar.getInstance();

        tvMaxQuota.setText(String.valueOf(maxReceiver));
        getStatus(ID);
        currentMonth = calendar.get(Calendar.MONTH);

        btnAddReceiver.setOnClickListener(v -> {
            addReceiver();
            currentReceiver++;
            Log.d("Current",currentReceiver+"");
            tvQuota.setText(String.valueOf(currentReceiver));

            if (nEtReceiver == 10) {
                btnAddReceiver.setVisibility(View.GONE);
            }
            if (currentReceiver == maxReceiver){
                btnAddReceiver.setVisibility(View.GONE);
                tvWarning.setVisibility(View.VISIBLE);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (countView == texts.size()) {
                for (int j = 0; j < texts.size(); j++) {
                    mainLayout.removeView(tvLog);
                }
            }
            sender = etSender.getText().toString();
            message = etMessage.getText().toString();
            newReceive = new String[texts.size()];
            for (int i = 0; i < texts.size(); i++) {
                if (texts.size() == 0){
                        Toast.makeText(getApplicationContext(),"Maaf, penerima tidak ada",Toast.LENGTH_LONG).show();
                }
                newReceive[i] = texts.get(i).getText().toString();
                getViral(newReceive[i], message,sender);
                countView++;
            }
            removeReceiver();
        });
        putStatus(ID,currentReceiver,currentMonth,lastMonth);
    }

    private void getViral(final String receiver, final String isiPesan, final String sender) {
        CustomRequest.request(Networks.viralRequest().getViral(USERKEY, PASSKEY, receiver, isiPesan), new NetworkResponse<Viral>() {
            @Override
            public void onSuccess(@NonNull Response<Viral> res) {
                if (res.body() == null) return;
                String status = res.body().getMessage().getText();
                String receiver = res.body().getMessage().getTo();
                addLog(status,receiver,isiPesan);
            }

            @Override
            public void onError(Throwable error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void getStatus(final int id) {
        CustomRequest.request(Networks.counterRequest().getCounter(id), new NetworkResponse<Counter>() {
            @Override public void onSuccess(@NonNull Response<Counter> res) {
                if (res.body() == null) return;
                currentReceiver = res.body().getCounter();
                currentMonth = res.body().getCurMonth();
                lastMonth = res.body().getLastMonth();
                tvQuota.setText(String.valueOf(currentReceiver));
            }

            @Override public void onError(Throwable error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void putStatus(final int id, int counter, int currentMonth, int lastMonth){
        CustomRequest.request(Networks.counterRequest().putCounter(id, counter, currentMonth, lastMonth), new NetworkResponse<Counter>() {
            @Override
            public void onSuccess(@NonNull Response<Counter> res) {
                if (res.isSuccessful()){
                    if (res.body() == null) return;
                    Log.d("Status","Counter : "+res.body().getCounter()+
                            ", Current Month : "+res.body().getCurMonth()+
                            ", Last Month : "+res.body().getLastMonth());
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void addReceiver() {

        EditText etReceiver = new EditText(this);
        texts.add(etReceiver);
        etReceiver.setHint("Masukkan no penerima");
        etReceiver.setId(nEtReceiver);
        etReceiver.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        etReceiver.setLayoutParams(params);
        nEtReceiver++;

        mainLayout.addView(etReceiver, pos);
        pos++;
    }

    private void removeReceiver(){
        texts.clear();
        mainLayout.removeViews(3,nEtReceiver);
        if (nEtReceiver == 10){
            btnAddReceiver.setVisibility(View.VISIBLE);
        }
        pos = 3;
        nEtReceiver = 0;
    }

    private void addLog(String status, String receiver, String message) {
        tvLog = new TextView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLog.setLayoutParams(params);
        tvLog.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColor));
        tvLog.setText(sender + status +
                " mengirim ke " + receiver +
                " dengan pesan " + message);
        mainLayout.addView(tvLog);
    }

}
