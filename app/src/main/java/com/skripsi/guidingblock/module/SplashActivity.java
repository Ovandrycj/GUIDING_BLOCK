package com.skripsi.guidingblock.module;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.skripsi.guidingblock.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        //inisialisasi progress bar dari layout dengan nama progressBar
        progressBar = findViewById(R.id.progressbar);

        //Memanggil fungsi Handler, Handler merupakan fungsi untuk mengatur proses di activity
        Handler handler = new Handler();

        //handler menggunakan fungsi post delayed selama 2 detik, yang berarti selama 2 detik activity akan berjalan kemudian setelah itu terdapat intent ke MulaiActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MulaiActivity.class));
                SplashActivity.this.finish();
            }
        }, 2000);
    }
}
