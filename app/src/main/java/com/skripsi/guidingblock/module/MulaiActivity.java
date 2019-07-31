package com.skripsi.guidingblock.module;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skripsi.guidingblock.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MulaiActivity extends AppCompatActivity {

    Button btn_about, btn_help;
    View btn_mulai;
    //variabel untuk permission device yang diperlukan
    private static final int PERMISSION_REQUEST_CODE = 1100;
    String[] appPermission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE
    };

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mulai);

        btn_mulai = findViewById(R.id.btn_mulai);
        btn_help = findViewById(R.id.btn_help);
        btn_about = findViewById(R.id.btn_about);

        btn_mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MulaiActivity.this, ClassifierActivity.class));
            }
        });
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MulaiActivity.this, AboutActivity.class));
            }
        });
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MulaiActivity.this, HelpActivity.class));
            }
        });

        if (checkAndRequestPermission()){

        }
    }

    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            finish();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    //fungsi untuk mendeteksi apakah permission semua telah disetujui atau belum
    public boolean checkAndRequestPermission(){
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String perm: appPermission){
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED){
                listPermissionNeeded.add(perm);
            }
        }

        //jika ada permission device yg belum disetujui maka akan mengajukan request permission lagi
        if (!listPermissionNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    //fungsi untuk hasil dari request permission device
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE){
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            for (int i=0; i<grantResults.length;i++){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount == 0){

            } else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()){
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)){
                        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
                        dialog.setTitle("Alert");
                        dialog.setMessage("This App need Camera and Storage Permission to work without and promles");
                        dialog.setPositiveButton("YES, Granted permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkAndRequestPermission();
                            }
                        });
                        dialog.setNegativeButton("No, Exit App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                System.exit(0);
                            }
                        });
                        dialog.setCancelable(false);
                        AlertDialog alert = dialog.create();
                        alert.show();

                    }
//                    else {
//                        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
//                        dialog.setTitle("Alert");
//                        dialog.setMessage("You have denied some permission. Allow all permission at [Setting] > [Permission]");
//                        dialog.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//                        dialog.setNegativeButton("No, Exit App", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                finish();
//                                System.exit(0);
//                            }
//                        });
//                        dialog.setCancelable(false);
//                        AlertDialog alert = dialog.create();
//                        alert.show();
//                    }
                }
            }
        }
    }

    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
