package xzr.perfmon;

import android.content.Context;

class Tools {
    static String format_ify_add_blank(String in) {
        StringBuilder s = new StringBuilder(in);
        for (int i = 0; i < 4 - in.length(); i++) {
            s.insert(0, "0");
        }
        return s.toString();
    }

    static String bool2text(boolean bool, Context context) {
        if (bool) {
            return context.getResources().getString(R.string.yes);
        }
        return context.getResources().getString(R.string.no);
    }
}
