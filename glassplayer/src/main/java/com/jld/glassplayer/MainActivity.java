package com.jld.glassplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static com.jld.glassplayer.MyPagerAdapter.PLAY_URL;

public class MainActivity extends BaseActivity implements BaseActivity.MyKeyListener {

    public ViewPager mViewPager;
    private TextView mNum;
    private String all_num;
    private ArrayList<String> mPhotoFiles;
    private MyPagerAdapter mAdapter;
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setKeyListener(this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNum = (TextView) findViewById(R.id.num_text);
        mPhotoFiles = getPhotoFiles();
        all_num = mPhotoFiles.size() + "";
        if (mPhotoFiles.size() == 0)
            mNum.setText(0 + "/" + all_num);
        else
            mNum.setText(1 + "/" + all_num);
        mAdapter = new MyPagerAdapter(this, mPhotoFiles);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mNum.setText((position + 1) + "/" + all_num);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public ArrayList<String> getPhotoFiles() {
        ArrayList<String> viods = new ArrayList<>();
        String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GlassVideo";
        Log.d(TAG, "Path:" + Path);
        File files = new File(Path);
        if (files.isDirectory()) {
            for (File file : files.listFiles()) {
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith(".mp4")) {
                    viods.add(absolutePath);
                }
            }
        }
        Log.d(TAG, "viods:" + viods);
        return viods;
    }

    @Override
    public void singleClick() {
        Log.d(TAG, "singleClick:" + mViewPager.getCurrentItem());
        if(!TextUtils.isEmpty(mPhotoFiles.get(mViewPager.getCurrentItem()))){
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra(PLAY_URL,mPhotoFiles.get(mViewPager.getCurrentItem()));
            this.startActivity(intent);
        }
    }

    @Override
    public void doubleClick() {
        onBackPressed();
    }

    @Override
    public void leftGlide() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        Log.d(TAG,"leftGlide:"+mViewPager.getCurrentItem());
    }

    @Override
    public void rightGlide() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
        Log.d(TAG,"rightGlide:"+mViewPager.getCurrentItem());
    }
}
