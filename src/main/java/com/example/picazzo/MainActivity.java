package com.example.picazzo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.picazzo.view.PicazzoView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private PicazzoView picazzoView;
    private AlertDialog.Builder currentDialog;
    private ImageView line;
    private SeekBar alpha;
    private SeekBar red;
    private SeekBar green;
    private SeekBar blue;
    private View colorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picazzoView = findViewById(R.id.picazzoId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuClearId: picazzoView.clear();
            break;
            case R.id.menuColorId: showColorDialog();
            break;
            case R.id.menuLineWidthId: showLineWidthDialog();
            break;
            case R.id.menuSaveId: picazzoView.save();
        }

        return super.onOptionsItemSelected(item);
    }


    public void showLineWidthDialog(){
        currentDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog,null);

        final SeekBar bar = view.findViewById(R.id.widthSeekBarId);
        Button btn = view.findViewById(R.id.dialogButtonId);
        line = view.findViewById(R.id.imageViewId);

        final AlertDialog dialog = currentDialog.setTitle("Line Width").setCancelable(true).setView(view).create();
        dialog.show();

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            Bitmap bitmap = Bitmap.createBitmap(400,100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                Paint p = new Paint();
                p.setColor(picazzoView.getColor());
                p.setStrokeCap(Paint.Cap.ROUND);
                p.setStrokeWidth(i);
                bitmap.eraseColor(Color.WHITE);
                canvas.drawLine(30, 50, 370, 50, p);
                line.setImageBitmap(bitmap);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picazzoView.setNewLine(bar.getProgress());
                dialog.dismiss();
            }
        });

    }

    public void showColorDialog(){
        currentDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog,null);
        int drawingColor = picazzoView.getColor();

        currentDialog.setView(view);

        alpha = view.findViewById(R.id.alphaSeekbarId);
        red = view.findViewById(R.id.rSeekbarId);
        green = view.findViewById(R.id.gSeekbarId);
        blue = view.findViewById(R.id.bSeekbarId);
        colorView = view.findViewById(R.id.colorViewId);

        alpha.setOnSeekBarChangeListener(colorChangeListener);
        red.setOnSeekBarChangeListener(colorChangeListener);
        green.setOnSeekBarChangeListener(colorChangeListener);
        blue.setOnSeekBarChangeListener(colorChangeListener);

        Button btn = view.findViewById(R.id.setColorButtonId);

        alpha.setProgress(Color.alpha(drawingColor));
        red.setProgress(Color.red(drawingColor));
        green.setProgress(Color.green(drawingColor));
        blue.setProgress(Color.blue(drawingColor));

        final AlertDialog dialog =  currentDialog.create();
        dialog.show();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picazzoView.setNewColor(Color.argb(alpha.getProgress(),red.getProgress(),green.getProgress(),blue.getProgress()));
                dialog.dismiss();
            }
        });

    }

    private SeekBar.OnSeekBarChangeListener colorChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            picazzoView.setBackgroundColor(Color.argb(alpha.getProgress(),red.getProgress(),green.getProgress(),blue.getProgress()));
            colorView.setBackgroundColor(Color.argb(alpha.getProgress(),red.getProgress(),green.getProgress(),blue.getProgress()));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


}
