package dev.karim.perumahan;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dev.karim.perumahan.models.Notif;
import dev.karim.perumahan.models.ResultNotif;
import dev.karim.perumahan.models.Viral;
import dev.karim.perumahan.network.NetworksNotif;
import dev.karim.perumahan.network.RoutesNotif;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.InputType;

public class ViralActivity extends AppCompatActivity {

    private Button btnSubmit, btnAddReceiver;
    private EditText etSender, etReceiver;
    private TextView tvPreview, tvLog;
    private LinearLayout mainLayout;

    private static String USERKEY = "usernama";
    private static String PASSKEY = "password";
    public Integer rowid = 1;
    public String sender;
    public String message;

    public boolean isSubmit = false;
    public int nReceiver = 0;
    public int pos = 2;
    public List<EditText> texts = new ArrayList<>();
    public String[] newReceive;
    public int countView = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viral);

        mainLayout = findViewById(R.id.main_viral);

        etSender = findViewById(R.id.sender);
        tvPreview = findViewById(R.id.preview);
        btnSubmit = findViewById(R.id.submit);
        btnAddReceiver = findViewById(R.id.add_receiver);

        getNotif(0);

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
                isSubmit = true;
                sender = etSender.getText().toString();
                newReceive = new String[texts.size()];
                for (int i = 0; i < texts.size(); i++) {
                    newReceive[i] = texts.get(i).getText().toString();
                    Log.d("number", newReceive[i]);
                    getNotif(i);
                }
            }
        });
    }

    private void getNotif(final int counter) {

        RoutesNotif routesNotif = NetworksNotif.notifRequest().create(RoutesNotif.class);
        Call<ResultNotif> resultCall = routesNotif.getNotif(rowid);
        resultCall.enqueue(new Callback<ResultNotif>() {
            @Override
            public void onResponse(Call<ResultNotif> call, Response<ResultNotif> response) {
                Notif notif = response.body().getNotif();
                String isipesan = notif.getIsinotif();
                if (!isSubmit) {
                    tvPreview.setText(isipesan);
                } else {
                    message = "Dari " + etSender.getText() + "/n" + isipesan;
                    tvPreview.setText(message);
                    if (countView == texts.size()){
                        for (int j = 0; j < texts.size();j++){
                            mainLayout.removeView(tvLog);
                            Log.d("remove","remove view");
                        }
                    }
                    countView++;
                    getViral(newReceive[counter], counter);
                }
            }

            @Override
            public void onFailure(Call<ResultNotif> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getViral(final String receiver, final int pos) {
        RoutesNotif routesNotif = NetworksNotif.viralRequest().create(RoutesNotif.class);
        Call<Viral> viralCall = routesNotif.getViral(USERKEY, PASSKEY, receiver, message);
        viralCall.enqueue(new Callback<Viral>() {
            //pasti akan onFailure karena link bermasalah
            @Override
            public void onResponse(Call<Viral> call, Response<Viral> response) {
                Toast.makeText(getApplicationContext(), "Send Message", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Viral> call, Throwable t) {

                tvLog = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvLog.setLayoutParams(params);
                tvLog.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColor));
                tvLog.setText("https://reguler.zenziva.net/apps/smsapi.php" +
                        "?userkey=" + USERKEY +
                        "&passkey=" + PASSKEY +
                        "&nohp=" + newReceive[pos] +
                        "$pesan=" + message);
                mainLayout.addView(tvLog);
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
}
