/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.skripsi.guidingblock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

import com.skripsi.guidingblock.Classifier.Recognition;

public class RecognitionScoreView extends View implements ResultsView {
    private static final float TEXT_SIZE_DIP = 24; //ukuran text hasil pengenalan gambar
    private List<Recognition> results; //array menyimpan hasil pengenalan gambar
    private final float textSizePx; //float untuk fixing text size
    private final Paint fgPaint; //memanggil fugnsi Paint

    public RecognitionScoreView(final Context context, final AttributeSet set) {
        super(context, set);

        //textSizePx dengan menerapkan tipe ukuran berdasarkan "DIP", kemudian size text sesuai dengan TEXT_SIZE_DIP
        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        fgPaint = new Paint(); //inisialiasai fgPaint dari Paint di class ini
        fgPaint.setTextSize(textSizePx); //fgPaint menerapkan isi text dan size text dari textSizePx
    }

    //constructor
    @Override
    public void setResults(final List<Recognition> results) {
        this.results = results;
        postInvalidate();
    }

    //fungsi untuk menggambar text
    @Override
    public void onDraw(final Canvas canvas) {

        fgPaint.setColor(Color.BLACK); //menerapkan warna pada fgPaint dengan warna hitam

        //jika result yg berupa array tidak kosong maka akan menjalankan proses dibawah
        if (results != null && results.size() > 0) {
            int y = (int) (fgPaint.getTextSize() * 1.4f);
            final Recognition recog = results.get(0);
            final int x = (int)(canvas.getWidth() - fgPaint.measureText(recog.getTitle())) / 2;
            canvas.drawText(recog.getTitle(), x, y, fgPaint);
        }
    }
}
