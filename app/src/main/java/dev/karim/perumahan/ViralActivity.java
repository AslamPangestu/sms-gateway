package dev.karim.perumahan;

import android.support.annotation.NonNull;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.karim.perumahan.model.Counter;
import dev.karim.perumahan.model.Message;
import dev.karim.perumahan.model.Viral;
import dev.karim.perumahan.network.CustomRequest;
import dev.karim.perumahan.network.NetworkResponse;
import dev.karim.perumahan.network.Networks;
import retrofit2.Response;

import android.text.InputType;

import static dev.karim.perumahan.BuildConfig.PASSKEY;
import static dev.karim.perumahan.BuildConfig.USERKEY;

public class ViralActivity extends AppCompatActivity {

    //Layout
    @BindView(R.id.main_viral) LinearLayout mainLayout;
    @BindView(R.id.submit) Button btnSubmit;
    @BindView(R.id.add_receiver) Button btnAddReceiver;
    @BindView(R.id.sender) EditText etSender;
    @BindView(R.id.max_quota) TextView tvMaxQuota;
    @BindView(R.id.quota_counter) TextView tvQuota;
    @BindView(R.id.warning_quota) TextView tvWarning;
    @BindView(R.id.viral_content) TextView tvContent;
    @BindView(R.id.sender_tv) TextView tvSender;
    @BindView(R.id.message_title) TextView tvMessageTitle;
    @BindView(R.id.message) TextView tvMessage;
    @BindView(R.id.receiver_title) TextView tvReceiverTitle;
    @BindView(R.id.quota) TextView tvQuotaTitle;

    //Content
    private String sender;
    private String message;

    //Counter View
    private int nEtReceiver = 0;

    //counter logic
    private int maxReceiver = 20;
    private int currentReceiver;
    private int pos = 5;
    private static int ID = 1;

    private List<EditText> texts = new ArrayList<>();
    private String[] newReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viral);
        getSupportActionBar().setTitle(getResources().getText(R.string.viral_title));

        ButterKnife.bind(this);

        layoutText();
        getCounter(ID);
        getMessage(ID);

        btnSubmit.setOnClickListener(v -> {
            sender = etSender.getText().toString();
            newReceive = new String[texts.size()];
            for (int i = 0; i < texts.size(); i++) {
                if (texts.size() == 0){
                        Toast.makeText(getApplicationContext(),"Maaf, penerima tidak ada",Toast.LENGTH_LONG).show();
                }
                newReceive[i] = texts.get(i).getText().toString();
                getViral(newReceive[i], message,sender);
            }
            removeReceiver();
        });
    }

    private void layoutText(){
        tvContent.setText(getResources().getText(R.string.viral_content));
        tvSender.setText(getResources().getText(R.string.sender));
        etSender.setHint(getResources().getText(R.string.hint_sender));
        tvMessageTitle.setText(getResources().getText(R.string.message));
        tvReceiverTitle.setText(getResources().getText(R.string.receiver));
        btnAddReceiver.setText(getResources().getText(R.string.add_receiver));
        btnSubmit.setText(getResources().getText(R.string.submit));
        tvQuotaTitle.setText(getResources().getText(R.string.quota));
        tvMaxQuota.setText(String.valueOf(maxReceiver));
    }

    private void getViral(final String receiver, final String isiPesan, final String sender) {
        CustomRequest.request(Networks.viralRequest().getViral(USERKEY, PASSKEY, receiver, isiPesan), new NetworkResponse<Viral>() {
            @Override
            public void onSuccess(@NonNull Response<Viral> res) {
                if (res.body() == null) return;
                String status = res.body().getMessage().getText();
                String receiver = res.body().getMessage().getTo();
                Log.d("Status","Status : "+status+"; Penerima : "+receiver);
                Toast.makeText(getApplicationContext(),"Status : "+status+"; Penerima : "+receiver,Toast.LENGTH_LONG).show();
//                addLog(status,receiver,isiPesan);
            }

            @Override
            public void onError(Throwable error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void getCounter(final int id) {
        CustomRequest.request(Networks.perumahanRequest().getCounter(id), new NetworkResponse<Counter>() {
            @Override public void onSuccess(@NonNull Response<Counter> res) {
                if (res.body() == null) return;
                currentReceiver = res.body().getCounter();
                tvQuota.setText(String.valueOf(currentReceiver));
                addReceiver();
            }

            @Override public void onError(Throwable error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void getMessage(final int id){
        CustomRequest.request(Networks.perumahanRequest().getMessage(id), new NetworkResponse<Message>() {
            @Override
            public void onSuccess(@NonNull Response<Message> res) {
                if (res.body() == null) return;
                message = res.body().getMessage();
                tvMessage.setText(message);
            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }

    private void addReceiver(){
        btnAddReceiver.setOnClickListener(v -> {
            newReceiver();
            currentReceiver++;
            tvQuota.setText(String.valueOf(currentReceiver));

            if (nEtReceiver == 10) {
                btnAddReceiver.setVisibility(View.GONE);
                tvWarning.setText(getResources().getText(R.string.max_add_receiver));
                tvWarning.setVisibility(View.VISIBLE);
            }
            if (currentReceiver >= maxReceiver){
                btnAddReceiver.setVisibility(View.GONE);
                tvWarning.setText(getResources().getText(R.string.max_receiver));
                tvWarning.setVisibility(View.VISIBLE);
            }
            putCounter(ID,currentReceiver);
        });
    }

    private void putCounter(final int id, int counter){
        CustomRequest.request(Networks.perumahanRequest().putCounter(id, counter), new NetworkResponse<Counter>() {
            @Override
            public void onSuccess(@NonNull Response<Counter> res) {
                if (res.isSuccessful()){
                    if (res.body() == null) return;
                    Log.d("Counter","Counter : "+res.body().getCounter());
                }
            }

            @Override
            public void onError(Throwable error) {
                Log.e("Error", error.getMessage());
            }
        });
    }

    private void newReceiver() {

        EditText etReceiver = new EditText(this);
        texts.add(etReceiver);
        etReceiver.setHint(getResources().getText(R.string.hint_receiver));
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

}
