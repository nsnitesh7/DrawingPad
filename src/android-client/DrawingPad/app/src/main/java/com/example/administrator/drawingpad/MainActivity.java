package com.example.administrator.drawingpad;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity {

    Conexao conexao;
    DrawingView dv ;
    private Paint mPaint;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawingView(this);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        conexao = new Conexao();
        conexao.start();
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub
                    super.handleMessage(msg);
                    DrawPoint(msg.arg1,msg.arg2);
                }
            };

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath( mPath,  mPaint);

            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        public void DrawPoint(int x,int y )
        {
            mCanvas.drawPoint(x, y, mPaint);
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            DrawPoint(x,y);

            int brushType=1;
            String action = (Color.red(mPaint.getColor())+","+Color.green(mPaint.getColor())+","+Color.blue(mPaint.getColor()));
            action += ";"+brushType+";"+(int)mPaint.getStrokeWidth()+";"+x+","+y;
            conexao.atualizar(action);

/*
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
*/            return true;
        }
    }

    public class Conexao extends Thread {

        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;

        public Conexao() {

        }

        public void run() {
            try {
                socket = new Socket("192.168.0.107", 587);
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                while (socket.isConnected()) {
                    String s = input.readUTF();
                    String[] sub = s.split(";");
                    String[] subColor = sub[0].split(",");
                    int[] color = new int[3];
                    for (int i = 0; i < subColor.length; i++) {
                        color[i] = Integer.parseInt(subColor[i]);
                    }
                    //g.setColor(new Color(color[0], color[1], color[2]));
                    //int type = Integer.parseInt(sub[1]);
                    //int size = Integer.parseInt(sub[2]);
                    String[] subXY = sub[3].split(",");
                    int x = Integer.parseInt(subXY[0]);
                    int y = Integer.parseInt(subXY[1]);

                    Message msg = new Message();
                    msg.arg1=x;
                    msg.arg2=y;
                    handler.sendMessage(msg);
                /*
                if (type == 1) {

                    g.fillOval(x, y, size, size);
                } else {
                    g.fillRect(x, y, size, size);
                }*/

                }
            } catch (UnknownHostException ex) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void close(){
            try {
                socket.close();
                this.interrupt();
            } catch (IOException ex) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void atualizar(String action) {
            try {
                output.writeUTF(action);
            } catch (IOException ex) {
                Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}