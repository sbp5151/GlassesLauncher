package com.jld.glassplayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;

public class BaseActivity extends Activity {
    public static final String TAG = "BaseActivity";
    public int firstKeyDown = -1;
    public int curKeyDown = -1;
    public int curKeyUp = -1;
    public static final int SINGLE_KEY = 1;
    public static final int GLIDE_UP = 2;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;
            switch (what) {
                case SINGLE_KEY://单击
                    mHandler.removeMessages(SINGLE_KEY);
                    listener.singleClick();
                    break;
                case GLIDE_UP://滑动抬起
                    mHandler.removeMessages(GLIDE_UP);
                    glideEnd();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
//        //取消标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //取消状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "------onKeyDown:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_F1:
                return true;
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_F4:
            case KeyEvent.KEYCODE_F5:
            case KeyEvent.KEYCODE_F6:
                if (curKeyDown != keyCode) {
                    curKeyDown = keyCode;
                    if (firstKeyDown < 0) {//触摸开始
                        firstKeyDown = keyCode;
                    } else {
                        mHandler.removeMessages(GLIDE_UP);
                    }
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    long curTime;
    int doubleDelay = 400;//双击最大间隔时间
    int glideDelay = 120;//滑动最大间隔时间

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "------onKeyUp:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_F1:
                if (System.currentTimeMillis() - curTime < (doubleDelay - 20)) {//双击
                    mHandler.removeMessages(SINGLE_KEY);
                    listener.doubleClick();
                } else {//单击
                    mHandler.sendEmptyMessageDelayed(SINGLE_KEY, doubleDelay);
                }
                Log.d(TAG, "------time:" + (System.currentTimeMillis() - curTime));
                curTime = System.currentTimeMillis();
                return true;
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_F4:
            case KeyEvent.KEYCODE_F5:
            case KeyEvent.KEYCODE_F6:
                if (curKeyUp != keyCode)
                    curKeyUp = keyCode;
                mHandler.sendEmptyMessageDelayed(GLIDE_UP, glideDelay);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void glideEnd() {
        int touchNum = curKeyDown - firstKeyDown;
        if (Math.abs(touchNum) > 1) {//触摸有效
            if (touchNum > 0) {//右滑
                listener.rightGlide();
            } else {//左滑
                listener.leftGlide();
            }
        }
        firstKeyDown = -1;
        curKeyUp = -1;
        curKeyDown = -1;
    }

    MyKeyListener listener;

    protected void setKeyListener(MyKeyListener listener) {
        this.listener = listener;
    }

    protected interface MyKeyListener {
        //单击
        public void singleClick();

        //双击
        public void doubleClick();

        //左滑
        public void leftGlide();

        //右滑
        public void rightGlide();
    }
}
