package com.jld.mylauncher;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by lz on 2016/10/21.
 */

public class ViewPageAdapter extends PagerAdapter {

    private List<AppInfoBean> list_view;
    private int mChildCount = 0;

    public ViewPageAdapter(List<AppInfoBean> list_view) {
        this.list_view = list_view;
    }

    @Override
    public int getCount() {
        if (list_view != null)
            return list_view.size();
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if ( mChildCount > 0){
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list_view.get(position).getView());//% list_view.size()
        return list_view.get(position).getView();//% list_view.size()
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView(list_view.get(position).getView());//% list_view.size()
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void setData(List<AppInfoBean> data){
        list_view = data;
        this.notifyDataSetChanged();
    }
}
