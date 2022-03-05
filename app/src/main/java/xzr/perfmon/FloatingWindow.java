package xzr.perfmon;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

import static xzr.perfmon.RefreshingDateThread.adrenofreq;
import static xzr.perfmon.RefreshingDateThread.adrenoload;
import static xzr.perfmon.RefreshingDateThread.cpubw;
import static xzr.perfmon.RefreshingDateThread.cpufreq;
import static xzr.perfmon.RefreshingDateThread.cpuload;
import static xzr.perfmon.RefreshingDateThread.cpunum;
import static xzr.perfmon.RefreshingDateThread.current;
import static xzr.perfmon.RefreshingDateThread.fps;
import static xzr.perfmon.RefreshingDateThread.gpubw;
import static xzr.perfmon.RefreshingDateThread.llcbw;
import static xzr.perfmon.RefreshingDateThread.m4m;
import static xzr.perfmon.RefreshingDateThread.maxtemp;
import static xzr.perfmon.RefreshingDateThread.memusage;
import static xzr.perfmon.RefreshingDateThread.mincpubw;

public class FloatingWindow extends Service {
    static String TAG = "FloatingWindow";

    public static boolean do_exit = true;
    static WindowManager.LayoutParams params;
    static WindowManager windowManager;
    static ArrayList<TextView> textViews;
    static Handler ui_refresher;

    static boolean show_cpufreq_now;
    static boolean show_cpuload_now;
    static boolean show_gpufreq_now;
    static boolean show_gpuload_now;
    static boolean show_cpubw_now;
    static boolean show_mincpubw_now;
    static boolean show_m4m_now;
    static boolean show_thermal_now;
    static boolean show_mem_now;
    static boolean show_current_now;
    static boolean show_gpubw_now;
    static boolean show_llcbw_now;
    static boolean show_fps_now;

    static float front_size;
    private FlexboxLayout main;

    @SuppressLint("ClickableViewAccessibility")
    void init() {
        textViews = new ArrayList<>();
        front_size = SharedPreferencesUtil.sharedPreferences.getFloat(SharedPreferencesUtil.front_size, SharedPreferencesUtil.front_size_default);
        {
            show_cpufreq_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_cpufreq, SharedPreferencesUtil.show_cpufreq_default);
            show_cpuload_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_cpuload, SharedPreferencesUtil.show_cpuload_default);
            show_gpufreq_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_gpufreq, SharedPreferencesUtil.show_gpufreq_default);
            show_gpuload_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_gpuload, SharedPreferencesUtil.show_gpuload_default);
            show_cpubw_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_cpubw, SharedPreferencesUtil.show_cpubw_default);
            show_mincpubw_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_mincpubw, SharedPreferencesUtil.show_mincpubw_default);
            show_m4m_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_m4m, SharedPreferencesUtil.show_m4m_default);
            show_thermal_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_thermal, SharedPreferencesUtil.show_thermal_default);
            show_mem_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_mem, SharedPreferencesUtil.show_mem_default);
            show_current_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_current, SharedPreferencesUtil.show_current_default);
            show_gpubw_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_gpubw, SharedPreferencesUtil.show_gpubw_default);
            show_llcbw_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_llcbw, SharedPreferencesUtil.show_llcbw_default);
            show_fps_now = SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.show_fps, SharedPreferencesUtil.show_fps_default);
        }
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER;
        params.x = 10;
        params.y = 10;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        main = new FlexboxLayout(this);
        if (SharedPreferencesUtil.sharedPreferences.getBoolean(SharedPreferencesUtil.horizon_mode, SharedPreferencesUtil.horizon_mode_default)) {
            main.setFlexDirection(FlexDirection.ROW);
            main.setFlexWrap(FlexWrap.WRAP);
        } else {
            main.setFlexDirection(FlexDirection.COLUMN);
        }
        main.setBackgroundColor(getResources().getColor(R.color.floating_window_backgrouns));
        main.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()), 0, 0, 0);
        TextView close = new TextView(this);
        close.setText(R.string.close);
        close.setTextSize(TypedValue.COMPLEX_UNIT_PX, close.getTextSize() * front_size);
        close.setTextColor(getResources().getColor(R.color.white));
        main.addView(close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });
        close.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SharedPreferencesUtil.sharedPreferences.edit().putBoolean(SharedPreferencesUtil.skip_first_screen, false).apply();
                Toast.makeText(FloatingWindow.this, R.string.skip_first_screen_str_disabled, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        main.setOnTouchListener(new View.OnTouchListener() {
            private int x, y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        params.x = params.x + movedX;
                        params.y = params.y + movedY;
                        windowManager.updateViewLayout(main, params);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        windowManager.addView(main, params);
    }

    void monitor_init() {
        windowManager.updateViewLayout(main, params);
        ui_refresher = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                int i = 0;
                if (Support.support_cpufreq && show_cpufreq_now) {
                    int total_freq = 0, total_load = 0;
                    for (int j = 0; j < RefreshingDateThread.cpunum; j++) {
                        total_freq += cpufreq[j];
                        total_load += cpuload[j];
                    }
                    String text = "cpu " + Tools.format_ify_add_blank(total_freq / cpunum + "") + " Mhz  ";
                    if (Support.support_cpuload && show_cpuload_now) {
                        text += total_load / cpunum + "%";
                    }
                    addToMainView(windowManager, main, textViews, ++i, text);
                }
                if (Support.support_adrenofreq && show_gpufreq_now) {
                    String text = "gpu0 " + Tools.format_ify_add_blank(adrenofreq + "") + " Mhz  ";
                    if (show_gpuload_now)
                        text += adrenoload + "%";
                    addToMainView(windowManager, main, textViews, ++i, text);
                }
                if (Support.support_mincpubw && show_mincpubw_now) {
                    addToMainView(windowManager, main, textViews, ++i, "mincpubw " + mincpubw);
                }
                if (Support.support_cpubw && show_cpubw_now) {
                    addToMainView(windowManager, main, textViews, ++i, "cpubw " + cpubw);
                }
                if (Support.support_gpubw && show_gpubw_now) {
                    addToMainView(windowManager, main, textViews, ++i, "gpubw " + gpubw);
                }
                if (Support.support_llcbw && show_llcbw_now) {
                    addToMainView(windowManager, main, textViews, ++i, "llccbw " + llcbw);
                }
                if (Support.support_m4m & show_m4m_now) {
                    addToMainView(windowManager, main, textViews, ++i, "m4m " + m4m + " Mhz");
                }
                if (Support.support_temp && show_thermal_now) {
                    addToMainView(windowManager, main, textViews, ++i, getResources().getString(R.string.temp) + maxtemp + " ℃");
                }
                if (Support.support_mem && show_mem_now) {
                    addToMainView(windowManager, main, textViews, ++i, getResources().getString(R.string.mem) + memusage + "%");
                }
                if (Support.support_current && show_current_now) {
                    addToMainView(windowManager, main, textViews, ++i, getResources().getString(R.string.current) + current + " mA");
                }
                if (Support.support_fps && show_fps_now) {
                    addToMainView(windowManager, main, textViews, ++i, "fps " + fps);
                }
                return false;
            }
        });
        new RefreshingDateThread().start();
    }

    private void addToMainView(WindowManager windowManager, FlexboxLayout main, ArrayList<TextView> textViews, int i, String message) {
        if (textViews.size() < i) {
            textViews.add(new TextView(FloatingWindow.this));
            TextView t = textViews.get(textViews.size() - 1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            t.setTextColor(getResources().getColor(R.color.white));
            t.setLayoutParams(layoutParams);
            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, t.getTextSize() * front_size);
            main.addView(t);
            windowManager.updateViewLayout(main, params);
        }
        textViews.get(i - 1).setText(message);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        do_exit = false;
        init();
        monitor_init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Calling destroy service");
        do_exit = true;
        try {
            windowManager.removeView(main);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }
}
