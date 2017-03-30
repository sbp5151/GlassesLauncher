package com.jld.glassplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PlayerActivity extends BaseActivity implements BaseActivity.MyKeyListener {
    public static final String TAG = "PlayerActivity";
    private SurfaceView mSurfaceView;
    private String mPlay_url;
    private MediaPlayer mMediaPlayer;
    private ProgressBar mProgressBar;
    private ImageView mPlay;
    private static final int ADD_TIME = 0x01;
    private int all_time ;
    private int current_time = 0;
    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what){
                case ADD_TIME:
                    mHandler.sendEmptyMessageDelayed(ADD_TIME,200);
                    if(mMediaPlayer!=null){
                        current_time = mMediaPlayer.getCurrentPosition();
                        mNum.setText(timeFormatter(current_time)+"/"+timeFormatter(all_time));
                    }
                    break;
            }
        }
    };
    private TextView mNum;
    private Display mCurrDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_player);
        setKeyListener(this);
        Intent intent = getIntent();
        mPlay_url = intent.getStringExtra(MyPagerAdapter.PLAY_URL);
        Log.d(TAG,"mPlay_url:"+mPlay_url);

        initView();
        initPlay();
    }
    public void initView(){
        /**
         * SurfaceView
         */
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        /**
         * SurfaceHolder
         */

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mPlay = (ImageView) findViewById(R.id.play);
        mNum = (TextView) findViewById(R.id.num_text);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick");
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                    mPlay.setVisibility(View.VISIBLE);
                }else{
                    mMediaPlayer.start();
                    mPlay.setVisibility(View.GONE);
                }
            }
        });
    }

    public void initPlay(){
        /**
         * MediaPlayer
         */
        mMediaPlayer = new MediaPlayer();
        try {
            //设置播放资源
            mMediaPlayer.setDataSource(mPlay_url);
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.addCallback(mCallback);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            all_time = mMediaPlayer.getDuration();
            Log.d(TAG,"all_time:"+all_time);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"IOException:"+e.toString());
            Toast.makeText(this,getString(R.string.play_error),Toast.LENGTH_SHORT).show();
            finish();
        }
        //取得当前Display对象
        mCurrDisplay = this.getWindowManager().getDefaultDisplay();
    }
    MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Log.d(TAG,"onCompletion:播放结束");

            /**
             * 播放完成
             */
            //释放
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mHandler.removeMessages(ADD_TIME);
            current_time = 0;
            finish();
        }
    };
    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            Log.d(TAG,"onPrepared:准备完成开始播放");
//            /**
//             * 准备完成，开始播放
//             */
//            int videoHeight = mediaPlayer.getVideoHeight();
//            int videoWidth = mediaPlayer.getVideoWidth();
//            //如果video宽度或者高度超过当前屏幕则缩放
//            if(videoHeight>mCurrDisplay.getHeight()||videoWidth>mCurrDisplay.getWidth())
//                mSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(mCurrDisplay.getWidth(), mCurrDisplay.getHeight()));
            mProgressBar.setVisibility(View.INVISIBLE);
            mMediaPlayer.start();
            mHandler.sendEmptyMessageDelayed(ADD_TIME,200);
            mMediaPlayer.setLooping(false);
        }
    };
    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG,"surfaceCreated:Surface创建");
            // 当SurfaceView中的Surface被创建的时候被调用
            //在这里我们指定MediaPlayer在当前的Surface中进行播放
            mMediaPlayer.setDisplay(surfaceHolder);
            //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了


        }
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG,"surfaceChanged：Surface改变");
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d(TAG,"surfaceDestroyed：Surface销毁");
            surfaceHolder.removeCallback(mCallback);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Override
    public void singleClick() {

        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            mPlay.setVisibility(View.VISIBLE);
        }else{
            mMediaPlayer.start();
            mPlay.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 将时间毫秒转换为视频格式
     * @param time
     * @return
     */
    private String timeFormatter(int time){

        String forTime = "";
        int timeS = 0;
        String timeS2;
        int timeM = 0;
        String timeM2;
        int timeH = 0;
        String timeH2;

        timeS = time/1000;
        if(timeS>60){
            timeM = timeS/60;
            timeS = timeS%60;
            if(timeM>60){
                timeH = timeM/60;
                timeM = timeM%60;

            }
        }
        if(timeS<10)
            timeS2 = "0"+timeS;
        else
            timeS2 = timeS+"";

        if(timeM<10)
            timeM2 = "0"+timeM;
        else
            timeM2 = timeM+"";

        if(timeH<10)
            timeH2 = "0"+timeH;
        else
            timeH2 = timeH+"";

        if(timeH>0)
            forTime = timeH2+":"+timeM2+":"+timeS2;
        else
            forTime = timeM2+":"+timeS2;

        return forTime;
    }
    @Override
    public void doubleClick() {
        mMediaPlayer.stop();
        onBackPressed();
    }

    @Override
    public void leftGlide() {

    }

    @Override
    public void rightGlide() {

    }
}
