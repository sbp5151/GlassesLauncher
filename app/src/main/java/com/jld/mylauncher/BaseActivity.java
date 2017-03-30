package com.jld.mylauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

public class BaseActivity extends Activity {
    public static final String TAG = "BaseActivity";
    public int firstKeyDown = -1;
    public int curKeyDown = -1;
    public int curKeyUp = -1;
    public static final int SINGLE_KEY = 1;
    public static final int GLIDE_UP = 2;
    public static final int POWER_OFF = 3;
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;
            switch (what) {
                case SINGLE_KEY://单击
                    mHandler.removeMessages(SINGLE_KEY);
                    if (mCancel != null && mCancel.isEnabled())
                        mCancel.callOnClick();
                    else if (mConfirm != null && mConfirm.isEnabled())
                        mConfirm.callOnClick();
                    else listener.singleClick();
                    break;
                case GLIDE_UP://滑动抬起
                    mHandler.removeMessages(GLIDE_UP);
                    glideEnd();
                    break;
                case POWER_OFF://长按关机
                    isSend = false;
                    // showDialog();
                    break;
            }
        }
    };

    private boolean isSend = false;
    private Button mCancel;
    private Button mConfirm;
    private MyDialog mDialog;

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
//            case KeyEvent.KEYCODE_POWER://电源键按下
//                if (!isSend) {
//                    mHandler.sendEmptyMessageDelayed(POWER_OFF, 2000);
//                    isSend = true;
//                }
//                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void showDialog() {
        mDialog = new MyDialog(this, new MyDialog.DialogKeyListener() {
            @Override
            public void dialogOnKeyDown(int keyCode, @NonNull KeyEvent event) {
                onKeyDown(keyCode, event);
            }

            @Override
            public void dialogOnKeyUp(int keyCode, @NonNull KeyEvent event) {
                onKeyUp(keyCode, event);
            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        Log.d(TAG, "------inflater:" + inflater);
        View view = inflater.inflate(R.layout.shutdown_dialog, null);
        mCancel = (Button) view.findViewById(R.id.shutdown_cancel);
        mConfirm = (Button) view.findViewById(R.id.shutdown_confirm);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                //Toast.makeText(BaseActivity.this, "关机", Toast.LENGTH_SHORT).show();
                //发送关机广播
                Intent sendShowDown = new Intent("com.jld.mylauncher.showdown");
                sendBroadcast(sendShowDown);
                // showdown();
            }
        });
        // 设置布局
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        mDialog.show();
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
//            case KeyEvent.KEYCODE_POWER://电源键抬起
//                mHandler.removeMessages(POWER_OFF);
//                isSend = false;
//                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void glideEnd() {
        int touchNum = curKeyDown - firstKeyDown;
        if (Math.abs(touchNum) > 1) {//触摸有效
            if (touchNum > 0) {//右滑
                if (mDialog != null && mDialog.isShowing() && !mCancel.isEnabled()) {
                    mConfirm.setEnabled(false);
                    mCancel.setEnabled(true);
                } else if (mDialog != null && !mDialog.isShowing())
                    listener.rightGlide();
            } else {//左滑
                if (mDialog != null && mDialog.isShowing() && !mConfirm.isEnabled()) {
                    mConfirm.setEnabled(true);
                    mCancel.setEnabled(false);
                } else if (mDialog != null && !mDialog.isShowing())
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

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
//        //取消标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //取消状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
