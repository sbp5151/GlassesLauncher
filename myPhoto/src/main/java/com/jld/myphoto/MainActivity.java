package com.jld.myphoto;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements BaseActivity.MyKeyListener {

    private ViewPager mViewPager;
    private TextView mNum;
    private String all_num;
    private String current_num = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD卡异常", Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.activity_main);
        setKeyListener(this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNum = (TextView) findViewById(R.id.num_text);
        mNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        mViewPager.setOffscreenPageLimit(0);
        ArrayList<Uri> photoFiles = getPhotoFiles();
        all_num = photoFiles.size() + "";
        Log.d(TAG, "all_num:" + all_num);
        if (photoFiles.size() == 0)
            mNum.setText(0 + "/" + all_num);
        else
            mNum.setText(1 + "/" + all_num);

        mViewPager.setAdapter(new MyPagerAdapter(this, photoFiles));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "position:" + position);
                mNum.setText((position + 1) + "/" + all_num);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    public ArrayList<Uri> getPhotoFiles() {
        ArrayList<Uri> photos = new ArrayList<>();
        String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GlassPhoto";
        Log.d(TAG, "Path:" + Path);
        File files = new File(Path);
        if (files.isDirectory()) {
            for (File file : files.listFiles()) {
                String absolutePath = file.getAbsolutePath();
                if (absolutePath.endsWith(".jpg")) {
                    photos.add(Uri.fromFile(file));
                }
            }
        }
        return photos;
    }


    @Override
    public void singleClick() {

    }

    @Override
    public void doubleClick() {
        onBackPressed();
    }

    @Override
    public void leftGlide() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

    @Override
    public void rightGlide() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }
}
