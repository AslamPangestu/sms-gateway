package dev.karim.perumahan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_task_aslam, btn_task_karim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_task_aslam = (Button) findViewById(R.id.btn_task_aslam);
        btn_task_karim = (Button) findViewById(R.id.btn_task_karim);

        btn_task_aslam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ViralActivity.class);
                startActivity(i);
            }
        });

        btn_task_karim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TaskKarimActivity.class);
                startActivity(i);
            }
        });
    }
}
