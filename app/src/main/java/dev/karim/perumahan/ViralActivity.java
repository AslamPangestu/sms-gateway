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

    private Button btnSubmit, btnAddReceiver;
    private EditText etSender, etReceiver, etMessage;
    private TextView tvLog;
    private LinearLayout mainLayout;

    public String sender;
    public String message;

    public int nReceiver = 0;
    public int maxReceiver = 20;
    private static final String CURRENT_RECEIVER = "current_receiver";
    public int pos = 3;
    public List<EditText> texts = new ArrayList<>();
    public String[] newReceive;
    public int countView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viral);

        mainLayout = findViewById(R.id.main_viral);

        etSender = findViewById(R.id.sender);
        etMessage = findViewById(R.id.message);
        btnSubmit = findViewById(R.id.submit);
        btnAddReceiver = findViewById(R.id.add_receiver);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences(CURRENT_RECEIVER, MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        btnAddReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReceiver();
                if (nReceiver == 10) {
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
                if (pref.getInt(CURRENT_RECEIVER,nReceiver)==maxReceiver){
                    Toast.makeText(getApplicationContext(),"Maaf, sudah mencapai kuota bulanan",Toast.LENGTH_LONG).show();
                }else {
                    sender = etSender.getText().toString();
                    String isipesan = etMessage.getText().toString();
                    newReceive = new String[texts.size()];
                    for (int i = 0; i < texts.size(); i++) {
                        if (texts.size() == 0){
                            Toast.makeText(getApplicationContext(),"Maaf, penerima tidak ada",Toast.LENGTH_LONG).show();
                        }
                        newReceive[i] = texts.get(i).getText().toString();
                        getViral(newReceive[i], isipesan,sender);
                        countView++;
                    }
                    Log.d("receiver",nReceiver+"");
                    editor.putInt(CURRENT_RECEIVER,nReceiver);
                    editor.apply();
                    mainLayout.removeViews(3,nReceiver);
                    if (nReceiver == 10){
                        btnAddReceiver.setVisibility(View.VISIBLE);
                    }
                    pos = 3;
                    nReceiver = 0;
                    Log.d("Max Receiver",pref.getInt(CURRENT_RECEIVER,0)+"");
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
                    Toast.makeText(getApplicationContext(), "Failde Send Message", Toast.LENGTH_LONG).show();
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
        etReceiver.setId(nReceiver);
        etReceiver.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        etReceiver.setLayoutParams(params);
        nReceiver++;

        mainLayout.addView(etReceiver, pos);
        pos++;
    }

    private void addLog(String status, String receiver, String isiPesan) {

        tvLog = new TextView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLog.setLayoutParams(params);
        tvLog.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColor));
        tvLog.setText(sender + status +
                " mengirim ke " + receiver +
                " dengan pesan " + isiPesan);
        mainLayout.addView(tvLog);
    }
}
