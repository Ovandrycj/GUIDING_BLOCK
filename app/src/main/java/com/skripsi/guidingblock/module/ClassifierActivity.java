package com.skripsi.guidingblock.module;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Typeface;

import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.skripsi.guidingblock.CameraActivity;
import com.skripsi.guidingblock.Classifier;
import com.skripsi.guidingblock.MSCognitiveServicesClassifier;
import com.skripsi.guidingblock.OverlayView.DrawCallback;
import com.skripsi.guidingblock.R;
import com.skripsi.guidingblock.env.BorderedText;
import com.skripsi.guidingblock.env.Logger;

public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

    private Integer sensorOrientation;
    private MSCognitiveServicesClassifier classifier;

    private BorderedText borderedText;
    private MediaPlayer mp;
    private Context context = this;

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    private static final float TEXT_SIZE_DIP = 10;

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {

        // pengaturan untuk text hasil
        final float textSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        // inisialisasi MSCognitiveServicesClassifier class di activity ini menjadi classifier
        classifier = new MSCognitiveServicesClassifier(ClassifierActivity.this);

        //get width & height
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();


        //button proses
        Button button_about = findViewById(R.id.btn_about);
        button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClassifierActivity.this, AboutActivity.class));
            }
        });

        Button button_help = findViewById(R.id.btn_help);
        button_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClassifierActivity.this, HelpActivity.class));
            }
        });

        View button_berhenti = findViewById(R.id.btn_berhenti);
        button_berhenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClassifierActivity.this, MulaiActivity.class));
                finish();
            }
        });

        final Display display = getWindowManager().getDefaultDisplay();
        final int screenOrientation = display.getRotation();


        //menampilkan log untuk sensor orientation
        LOGGER.i("Sensor orientation: %d, Screen orientation: %d", rotation, screenOrientation);

        sensorOrientation = rotation + screenOrientation;

        //menampilkan log untuk size
        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        yuvBytes = new byte[3][];

        //perintah untuk render setiap mendeteksi object
        addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        renderDebug(canvas);
                    }
                });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(ClassifierActivity.this, MulaiActivity.class));
        ClassifierActivity.this.finish();
    }

    //proses yg akan dijalankan setiap render
    protected void processImageRGBbytes(int[] rgbBytes) {
        //pengaturan frame untuk pixel
        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);


        //di belakang layar sistem, proses identifikasi pola disetting akan dijalankan setiap 3 detik dengan handler
        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //untuk mendapatkan info waktu terjadi render
                                final long startTime = SystemClock.uptimeMillis();
                                Classifier.Recognition r = classifier.classifyImage(rgbFrameBitmap, sensorOrientation);
                                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                                //hasil identifikasi akan di simpan di arraylist results
                                final List<Classifier.Recognition> results = new ArrayList<>();

                                //jika hasil identifikasi memiliki tingkat akurasi 70& maka hasilnya akan disimpan di array results
                                if (r.getConfidence() > 0.7) {
                                    results.add(r);
                                }

                                //log untuk menampilkan hasil deteksi
                                LOGGER.i("Detect: %s", results);
                                if (resultsView == null) {
                                    resultsView = findViewById(R.id.results);
                                }


                                //Menghasilkan suara
                                if (LOGGER.result("Detect: %s",results).contains("Berhenti")){
                                    mp = MediaPlayer.create(context, R.raw.berhenti);
                                    try {

                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if (LOGGER.result("Detect: %s",results).contains("Belok Kanan")){
                                    mp = MediaPlayer.create(context, R.raw.kanan);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if (LOGGER.result("Detect: %s",results).contains("Belok Kiri")){
                                    mp = MediaPlayer.create(context, R.raw.kiri);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if (LOGGER.result("Detect: %s",results).contains("Lurus")){
                                    mp = MediaPlayer.create(context, R.raw.lurus);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if (LOGGER.result("Detect: %s",results).contains("Pertigaan")){
                                    mp = MediaPlayer.create(context, R.raw.pertigaankanan);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if (LOGGER.result("Detect: %s",results).contains("Perempatan")){
                                    mp = MediaPlayer.create(context, R.raw.perempatankanan);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if (LOGGER.result("Detect: %s",results).contains("Serong Kanan")){
                                    mp = MediaPlayer.create(context, R.raw.serongkanan);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if (LOGGER.result("Detect: %s",results).contains("Serong Kiri")){
                                    mp = MediaPlayer.create(context, R.raw.serongkiri);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else {
                                    mp = MediaPlayer.create(context, R.raw.gagal);
                                    try {
                                        if (!mp.isPlaying()){
                                            mp.start();
                                        }
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                resultsView.setResults(results);
                                requestRender();
                                computing = false;
                                if (postInferenceCallback != null) {
                                    postInferenceCallback.run();
                                }
                            }
                        }, 3000);

                    }
                });

    }

    @Override
    public void onSetDebug(boolean debug) {
    }

    private void renderDebug(final Canvas canvas) {
        if (!isDebug()) {
            return;
        }

        final Vector<String> lines = new Vector<String>();
        lines.add("Inference time: " + lastProcessingTimeMs + "ms");
        borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
    }
}

