package xzr.perfmon;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    static SharedPreferences sharedPreferences;

    static final String skip_first_screen = "skip_first_screen";
    static final boolean default_skip_first_screen = false;

    static final String delay = "refreshing_delay";
    static final int default_delay = 1000;

    static final String reverse_current = "reverse_current";
    static final boolean reverse_current_default = false;

    static final String show_cpufreq = "show_cpufreq";
    static final boolean show_cpufreq_default = true;

    static final String show_cpuload = "show_cpuload";
    static final boolean show_cpuload_default = true;

    static final String show_gpufreq = "show_gpufreq";
    static final boolean show_gpufreq_default = true;

    static final String show_gpuload = "show_gpuload";
    static final boolean show_gpuload_default = true;

    static final String show_cpubw = "show_cpubw";
    static final boolean show_cpubw_default = true;

    static final String show_mincpubw = "show_mincpubw";
    static final boolean show_mincpubw_default = false;

    static final String show_m4m = "show_m4m";
    static final boolean show_m4m_default = true;

    static final String show_thermal = "show_thermal";
    static final boolean show_thermal_default = true;

    static final String show_mem = "show_mem";
    static final boolean show_mem_default = true;

    static final String show_current = "show_current";
    static final boolean show_current_default = true;

    static final String show_gpubw = "show_gpubw";
    static final boolean show_gpubw_default = true;

    static final String show_llcbw = "show_llcbw";
    static final boolean show_llcbw_default = true;

    static final String show_fps = "show_fps";
    static final boolean show_fps_default = true;

    static final String horizon_mode = "horizon_mode";
    static final boolean horizon_mode_default = true;

    static final String flod_cpu = "horizon_mode";
    static final boolean flod_cpu_default = true;

    static final String front_size = "front_size";
    static final float front_size_default = 0.7f;

    static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("main", 0);
    }
}
