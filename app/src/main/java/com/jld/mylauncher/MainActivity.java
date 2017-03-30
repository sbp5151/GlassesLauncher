package com.jld.mylauncher;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements BaseActivity.MyKeyListener {

    public static final String TAG = "MainActivity";
    protected ViewPager viewPager;
    private List<AppInfoBean> dataView = new ArrayList<>();
    private ViewPageAdapter viewPageAdapter;
    private AppInstallReceiver appInstallReceiver;
    private static final int showTime = 2000;
    private TextView mTime;
    private ImageView mWifi;
    private ImageView mBattery;
    public static final int UPDATE_TIME = 1;
    public static final int UPDATE_WIFI = 2;
    public static final int UPDATE_BATTERY = 3;
    private int[] wifis = {R.mipmap.ic_setting_wifi_0, R.mipmap.ic_setting_wifi_1, R.mipmap.ic_setting_wifi_2, R.mipmap.ic_setting_wifi_3, R.mipmap.ic_setting_wifi_4};
    private int[] batterys = {R.mipmap.battery_1, R.mipmap.battery_2, R.mipmap.battery_3, R.mipmap.battery_4, R.mipmap.battery_5};
    public final int updateTime = 1200;
    public final int updateWifi = 3000;
    private int batteryNum = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case UPDATE_TIME:
                    String time = getTime();
                    mTime.setText(time);
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME, updateTime);
                    break;
                case UPDATE_WIFI:
                    int strength = getStrength();
                    if (strength > 0 && strength < 5) {
                        if (mWifi.getVisibility() == View.INVISIBLE)
                            mWifi.setVisibility(View.VISIBLE);
                        mWifi.setImageResource(wifis[strength]);
                    } else {
                        if (mWifi.getVisibility() == View.VISIBLE)
                            mWifi.setVisibility(View.INVISIBLE);
                    }
                    Log.d(TAG, "strength:" + strength);
                    mHandler.sendEmptyMessageDelayed(UPDATE_WIFI, updateWifi);
                    break;
                case UPDATE_BATTERY:
                    if (batteryNum >= 0 && batteryNum < 5)
                        mBattery.setImageResource(batterys[batteryNum]);
                    else if (batteryNum == 5) {
                        mBattery.setImageResource(batterys[4]);
                        break;
                    }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        appInstallReceiver = new AppInstallReceiver();
        registerReceiver(appInstallReceiver, intentFilter);
        //注册长按power键关机广播
        IntentFilter intentFilter1 = new IntentFilter("com.jld.mylauncher.power");
        ShowdownReceiver showdownReceiver = new ShowdownReceiver();
        registerReceiver(showdownReceiver,intentFilter1);
        Log.d(TAG, "------注册ShowdownReceiver:power");

        initView();
        registBatteryReceiver();
        setKeyListener(this);
        openBluetooth();
    }
    class ShowdownReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "------收到ShowdownReceiver:power");
            showDialog();
        }
    }
    /**
     * 自动打开蓝牙
     */
    private void openBluetooth() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        defaultAdapter.enable();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.list_pager);
        mTime = (TextView) findViewById(R.id.time);
        mWifi = (ImageView) findViewById(R.id.wifi);
        mBattery = (ImageView) findViewById(R.id.battery);
        mHandler.sendEmptyMessage(UPDATE_TIME);
        mHandler.sendEmptyMessage(UPDATE_WIFI);

        addAppData();
        viewPageAdapter = new ViewPageAdapter(dataView);
        viewPager.setAdapter(viewPageAdapter);
//        int currentItem = (dataView.size() - 1) / 2;//Integer.MAX_VALUE / 2;
        if (dataView.size() > 1)
            viewPager.setCurrentItem(2);
    }

    public String getTime() {
        String time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(System.currentTimeMillis());
        time = dateFormat.format(date);
        return time;
    }

    public int getStrength() {
        WifiManager wifiManager = (WifiManager) MainActivity.this
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
            return strength;
        }
        return 0;
    }

    /**
     * 注册电池监听广播
     */
    public void registBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        BatteryReceiver batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, intentFilter);
    }

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                //获取当前电量
                int level = intent.getIntExtra("level", 0);
                //电量的总刻度
                int scale = intent.getIntExtra("scale", 100);

                batteryNum = (level * 100 / scale) / 20;

                mHandler.sendEmptyMessage(UPDATE_BATTERY);
                Log.d(TAG, "ratio:" + batteryNum);
            }
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(appInstallReceiver);
        super.onDestroy();
    }

    private List<ResolveInfo> apps;

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //new ImageView(MainActivity.this);
        apps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    private void uninstall(ResolveInfo appResolveInfo) {
        String pkg = appResolveInfo.activityInfo.packageName;
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + pkg));
        MainActivity.this.startActivity(uninstallIntent);
    }


    private void addAppData() {
        loadApps();
        dataView.clear();
        for (ResolveInfo app : apps) {
            Log.d(TAG, "packageName" + app.activityInfo.packageName);
            if (app.activityInfo.packageName.startsWith("com.jld.")
                    || app.activityInfo.packageName.contains("FileBrower")
                    ) {
                if (app.activityInfo.packageName.equals(getApplication().getPackageName())) {
                    continue;
                }
                Log.d(TAG, "packageName------");

                View v = LayoutInflater.from(this).inflate(R.layout.fragment_page, null);
                ImageView iv = (ImageView) v.findViewById(R.id.iv_app_icon);
                TextView tv = (TextView) v.findViewById(R.id.tv_app_name);
                iv.setImageDrawable(app.activityInfo.loadIcon(getPackageManager()));
                tv.setText(app.activityInfo.loadLabel(getPackageManager()));
                setOnClickListener(iv, app);
                dataView.add(new AppInfoBean(v, app));
            }
        }
    }

    private void setOnClickListener(final ImageView iv, final ResolveInfo app) {
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((ImageView) v).setColorFilter(Color.parseColor("#60000000"));
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        ((ImageView) v).setColorFilter(Color.parseColor("#00000000"));
                        break;
                }
                return false;
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //该应用的包名
                String pkg = app.activityInfo.packageName;
                if (pkg.equals("FileBrower")) {
                    ToastUtil.showToast(MainActivity.this, "系统应用，无法打开", 3000);
                    return;
                }
                //应用的主activity类
                String cls = app.activityInfo.name;
                ComponentName componentName = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                startActivity(intent);
            }
        });
        iv.setOnLongClickListener(longClickListener);
    }

    private int appRemoveIndex = -1;
    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //v.setFocusable(false);
            appRemoveIndex = viewPager.getCurrentItem();
            if (appRemoveIndex != -1) {
                uninstall(dataView.get(appRemoveIndex).getResolveInfo());
            }
            return true;
        }
    };

    public void sure() {
        final int index = viewPager.getCurrentItem();
        if (index >= 0 && index < dataView.size()) {
            ResolveInfo mResolveInfo = dataView.get(index).getResolveInfo();
            final String pkg = mResolveInfo.activityInfo.packageName;
            final String cls = mResolveInfo.activityInfo.name;
            ComponentName componentName = new ComponentName(pkg, cls);
            Intent intent = new Intent();
            intent.setComponent(componentName);
            startActivity(intent);
        }
    }

    public void last() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() > 1 ? viewPager.getCurrentItem() - 1 : start());
    }

    public void next() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() < dataView.size() - 1 ? viewPager.getCurrentItem() + 1 : end());
    }

    private int start() {
        ToastUtil.showToast(getApplicationContext(), R.string.start, showTime);
        return 1;
    }

    private int end() {
        ToastUtil.showToast(getApplicationContext(), R.string.end, showTime);
        return dataView.size() - 1;
    }

    public void last(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() > 0 ? viewPager.getCurrentItem() - 1 : start());
    }

    public void next(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() < dataView.size() - 1 ? viewPager.getCurrentItem() + 1 : end());
    }

    @Override
    public void singleClick() {
        sure();
    }

    @Override
    public void doubleClick() {

    }

    @Override
    public void leftGlide() {
        last();
    }

    @Override
    public void rightGlide() {
        next();
    }

    public class AppInstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //PackageManager manager = context.getPackageManager();
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                //String packageName = intent.getData().getSchemeSpecificPart();
                addAppData();
                viewPageAdapter.setData(dataView);
                ToastUtil.showToast(context, R.string.install, showTime);
                //ResolveInfo resolveInfo = new ResolveInfo()
            }
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                //String packageName = intent.getData().getSchemeSpecificPart();
                //Log.d("tag","卸载成功-----> "+packageName);
                ToastUtil.showToast(context, R.string.uninstall, showTime);
                if (appRemoveIndex != -1) {
                    dataView.get(appRemoveIndex).getView().setVisibility(View.GONE);
                    dataView.remove(appRemoveIndex);
                    viewPageAdapter.setData(dataView);
                    appRemoveIndex = -1;
                }
            }
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                //String packageName = intent.getData().getSchemeSpecificPart();
                //Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
            }
        }
    }
}
