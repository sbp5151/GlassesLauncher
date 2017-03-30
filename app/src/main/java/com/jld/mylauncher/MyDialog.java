package com.jld.mylauncher;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.KeyEvent;

/**
 * 项目名称：MyLauncher
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/3/23 9:44
 */
public class MyDialog extends Dialog {
    DialogKeyListener mKeyListener;
    public MyDialog(@NonNull Context context,DialogKeyListener keyListener) {
        super(context);
        this.mKeyListener = keyListener;
    }

    public MyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected MyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(mKeyListener !=null)
            mKeyListener.dialogOnKeyDown(keyCode,event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if(mKeyListener !=null)
            mKeyListener.dialogOnKeyUp(keyCode,event);
        return super.onKeyUp(keyCode, event);
    }

    interface DialogKeyListener {
        void dialogOnKeyDown(int keyCode, @NonNull KeyEvent event);

        void dialogOnKeyUp(int keyCode, @NonNull KeyEvent event);
    }
}
