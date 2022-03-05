package xzr.perfmon;

import android.content.Context;

class Tools {
    static String bool2text(boolean bool, Context context) {
        if (bool) {
            return context.getResources().getString(R.string.yes);
        }
        return context.getResources().getString(R.string.no);
    }
}
