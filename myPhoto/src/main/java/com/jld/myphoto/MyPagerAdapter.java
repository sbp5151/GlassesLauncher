package com.jld.myphoto;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * 项目名称：MyLauncher
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/2/16 16:22
 */
public class MyPagerAdapter extends PagerAdapter {


    private static final String TAG = "MyPagerAdapter";
    private Context mContext;
//    private final View mView;
//    private final ImageView mImageView;
    private ArrayList<Uri> mUris;
    private ArrayList<ImageView> imgs;
    private final ImageView mImageView0;
    private final ImageView mImageView1;
    private final ImageView mImageView2;
    private final ImageView mImageView3;

    public MyPagerAdapter(Context context, ArrayList<Uri> uris) {
        mUris = uris;
        mContext = context;
//        mView = LayoutInflater.from(mContext).inflate(R.layout.photo_item, null);
//        mImageView = (ImageView) mView.findViewById(R.id.photo_item);
        mImageView0 = new ImageView(mContext);
        mImageView1 = new ImageView(mContext);
        mImageView2 = new ImageView(mContext);
        mImageView3 = new ImageView(mContext);
    }

    @Override
    public int getCount() {
        return mUris.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       // super.destroyItem(container, position, object);
        Log.d(TAG,"destroyItem:"+position%4);
        if(position%4==0){
            container.removeView(mImageView0);
        }else if(position%4==1){
            container.removeView(mImageView1);
        }else if(position%4==2){
            container.removeView(mImageView2);
        }else{
            container.removeView(mImageView3);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG,"instantiateItem:"+position%4);
        if(position%4==0){
            mImageView0.setImageURI(mUris.get(position));
            container.addView(mImageView0);
            return mImageView0;
        }else if(position%4==1){
            mImageView1.setImageURI(mUris.get(position));
            container.addView(mImageView1);
            return mImageView1;
        }else if(position%4==2){
            mImageView2.setImageURI(mUris.get(position));
            container.addView(mImageView2);
            return mImageView2;
        }else{
            mImageView3.setImageURI(mUris.get(position));
            container.addView(mImageView3);
            return mImageView3;
        }
    }
}
