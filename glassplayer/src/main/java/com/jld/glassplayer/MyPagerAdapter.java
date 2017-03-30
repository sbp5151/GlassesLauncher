package com.jld.glassplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
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
    private ArrayList<String> mUris;
    private ArrayList<ImageView> imgs;
    private final ImageView mImageView0;
    private final ImageView mImageView1;
    private final ImageView mImageView2;
    private final ImageView mImageView3;
    public final static String PLAY_URL = "play_url";

    public MyPagerAdapter(Context context, ArrayList<String> uris) {
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
        Log.d(TAG,"destroyItem:"+position);
        Log.d(TAG,"destroyItem%4:"+position%4);
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
    public Object instantiateItem(ViewGroup container, final int position) {
        MainActivity activity = (MainActivity)mContext;
        int currentItem = activity.mViewPager.getCurrentItem();
        Log.d(TAG,"currentItem:"+currentItem);
        Log.d(TAG,"position:"+position);
        Log.d(TAG,"instantiateItem:"+position%4);
        Log.d(TAG,"mUris.get(currentItem):"+mUris.get(currentItem));
        ImageView view;
        if(position%4==0){
            view = mImageView0;
        }else if(position%4==1){
            view = mImageView1;
        }else if(position%4==2){
            view = mImageView2;
        }else{
            view = mImageView3;
        }
        view.setImageBitmap(getVideoThumbnail(mUris.get(position)));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick:"+position);
                if(!TextUtils.isEmpty(mUris.get(position))){
//                    Log.d(TAG,"onClick:"+position);
//                    Intent intent = new Intent(mContext, PlayerActivity.class);
//                   intent.putExtra(PLAY_URL,mUris.get(position));
//                   mContext.startActivity(intent);
               }
            }
        });
        container.addView(view);
        return view;
    }

    /**
     * 获取视频2s时的截图
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(2000);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


}
