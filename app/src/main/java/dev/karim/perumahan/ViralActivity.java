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

import dev.karim.perumahan.model.Viral;
import dev.karim.perumahan.network.NetworksNotif;
import dev.karim.perumahan.network.RoutesNotif;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.InputType;

import static dev.karim.perumahan.BuildConfig.PASSKEY;
import static dev.karim.perumahan.BuildConfig.USERKEY;

public class ViralActivity extends AppCompatActivity {

    //Layout
    Button btnSubmit, btnAddReceiver;
    EditText etSender, etReceiver, etMessage;
    TextView tvLog,tvQuota,tvMaxQuota;
    LinearLayout mainLayout;

    private static final String CURRENT_RECEIVER = "current_receiver";

    String sender;
    String message;

    //counter
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

        mainLayout = findViewById(R.id.main_viral);
        etSender = findViewById(R.id.sender);
        etMessage = findViewById(R.id.message);
        btnSubmit = findViewById(R.id.submit);
        btnAddReceiver = findViewById(R.id.add_receiver);
        tvQuota = findViewById(R.id.quota_counter);
        tvMaxQuota = findViewById(R.id.max_quota);

        tvMaxQuota.setText(maxReceiver);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences(CURRENT_RECEIVER, MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        setCurrentQuota(pref, editor, currentQuota);

        btnAddReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReceiver();
                if (nEtReceiver == 10 || currentReceiver.equals(maxReceiver)) {
                    btnAddReceiver.setVisibility(View.GONE);
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
                if (currentReceiver.equals(maxReceiver)){
                    Toast.makeText(getApplicationContext(),"Maaf, sudah mencapai kuota bulanan",Toast.LENGTH_LONG).show();
                }else {
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
                    setCurrentQuota(pref, editor, currentQuota);
                    removeReceiver();
                }
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

    private void setCurrentQuota(SharedPreferences pref, SharedPreferences.Editor editor, int currentQuota){
        editor.putInt(CURRENT_RECEIVER,currentQuota);
        editor.apply();
        currentReceiver = String.valueOf( pref.getInt(CURRENT_RECEIVER,0));
        tvQuota.setText(currentReceiver);
    }
}
