package com.jld.mylauncher;

import android.content.pm.ResolveInfo;
import android.view.View;

/**
 * Created by lz on 2016/10/22.
 */

public class AppInfoBean {
    private final View view;
    private final ResolveInfo resolveInfo;
    public AppInfoBean(View view, ResolveInfo resolveInfo) {
        this.view = view;
        this.resolveInfo = resolveInfo;
    }

    public View getView() {
        return view;
    }

    public ResolveInfo getResolveInfo() {
        return resolveInfo;
    }
}
