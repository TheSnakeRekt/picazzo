package com.example.picazzo.view;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;

public class PicazzoView extends View {

    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> pointMap;

    public PicazzoView(Context context,@NonNull AttributeSet atriSet) {
        super(context,atriSet);
        init();
    }

    void init(){
        paintScreen = new Paint();
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(15);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setStyle(Paint.Style.STROKE);

        pathMap = new HashMap<>();
        pointMap = new HashMap<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap,0,0, paintScreen);

        for(Integer key: pathMap.keySet()){
            canvas.drawPath(pathMap.get(key),paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int maskedAction = event.getActionMasked();
        final int actionIndex = event.getActionIndex();

        if(maskedAction == MotionEvent.ACTION_DOWN || maskedAction == MotionEvent.ACTION_POINTER_DOWN){
            touchStarted(event.getX(actionIndex),event.getY(actionIndex),event.getPointerId(actionIndex));
        }else if(maskedAction == MotionEvent.ACTION_UP ||maskedAction == MotionEvent.ACTION_POINTER_UP){
            touchEnd(event.getPointerId(actionIndex));
        }else{
            touchMoved(event);
        }

        invalidate();
        return true;
    }

    private void touchMoved(MotionEvent event) {

        for(int i = 0;i < event.getPointerCount(); i++){

            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if(pathMap.containsKey(pointerId)){
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = pointMap.get(pointerId);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if(deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE){
                    path.quadTo(point.x,point.y,(newX+point.x)/2,(newY+point.y)/2);

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }

        }
    }

    private void touchEnd(int pointerId) {

        Path path = pathMap.get(pointerId);
        canvas.drawPath(path,paintLine);
        path.reset();
    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path = null;
        Point point = null;

        if(pathMap.containsKey(pointerId)){
            path = pathMap.get(pointerId);
            point = pointMap.get(pointerId);
        }else{
            pathMap.put(pointerId,new Path());
            pointMap.put(pointerId,new Point());
        }

        if(path != null){
            path.moveTo(x,y);
        }

        if(point != null){
            point.x = (int) x;
            point.y =(int) y;
        }

    }


    public void clear(){
        pathMap.clear();
        pointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void save(){
        String filename = "Picazzo"+System.currentTimeMillis()+".jpg";

        File dir = getContext().getDir("ImageDir",Context.MODE_PRIVATE);

        File path = new File(dir,filename);



        try {
            final FileOutputStream outputStream = new FileOutputStream(path);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                }
            });

            thread.run();

            if(!thread.isAlive()){
                outputStream.flush();
                outputStream.close();

                Toast msg = Toast.makeText(getContext(),"Saved "+dir.getAbsolutePath(),Toast.LENGTH_LONG);
                msg.setGravity(Gravity.CENTER,msg.getXOffset() /2 , msg.getYOffset() /2);
                msg.show();
            }




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void setNewColor(int color){
        paintLine.setColor(color);
    }

    public int getColor(){
        return paintLine.getColor();
    }

    public void setNewLine(int width){
        paintLine.setStrokeWidth(width);
    }

    public int getLineWidth(){
        return (int) paintLine.getStrokeWidth();
    }

    public void erase(){}



}
