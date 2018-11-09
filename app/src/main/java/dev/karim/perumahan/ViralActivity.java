package dev.karim.perumahan;

import android.content.SharedPreferences;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.karim.perumahan.model.Viral;
import dev.karim.perumahan.network.NetworksNotif;
import dev.karim.perumahan.network.RoutesNotif;
import dev.karim.perumahan.util.CacheManager;
import retrofit2.Call;
import retrofit2.Callback;
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

    EditText etReceiver;
    TextView tvLog;

    //Content
    String sender;
    String message;

    //Counter
    int nEtReceiver = 0;
    String maxReceiver = "20";
    String currentReceiver;
    int currentQuota;
    int countView = 0;
    int pos = 3;

    List<EditText> texts = new ArrayList<>();
    String[] newReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viral);

        ButterKnife.bind(this);

        tvQuota = findViewById(R.id.quota_counter);

        btnAddReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReceiver();
                if (nEtReceiver == 10) {
                    btnAddReceiver.setVisibility(View.GONE);
                }
                if (currentReceiver.equals(maxReceiver)){
                    btnAddReceiver.setVisibility(View.GONE);
                    tvWarning.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                currentQuota += texts.size();
                removeReceiver();
            }
        });
    }

    private void getViral(final String receiver, final String isiPesan, final String sender) {
        RoutesNotif routesNotif = NetworksNotif.viralRequest().create(RoutesNotif.class);
        Call<Viral> viralCall = routesNotif.getViral(USERKEY, PASSKEY, receiver, isiPesan);
        viralCall.enqueue(new Callback<Viral>() {
            @Override
            public void onResponse(Call<Viral> call, Response<Viral> response) {
                if(response.isSuccessful()){
                    String status = response.body().getMessage().getText();
                    String receiver = response.body().getMessage().getTo();
                    addLog(status,receiver,isiPesan);
                    Toast.makeText(getApplicationContext(), "Success Send Message", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Failed Send Message", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Viral> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(getApplicationContext(), "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                }
                else {
                    Toast.makeText(getApplicationContext(), "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                }
            }
        });
    }

    private void addReceiver() {

        etReceiver = new EditText(this);
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

    private void setCurrentQuota(SharedPreferences pref1, boolean firstRun, int currentQuota){
        tvQuota.setText(currentReceiver);
    }
}
