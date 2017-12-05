package com.yueding.chatrobot.Utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by yueding on 2017/12/1.
 */

public class YdUtil {

    /**
     * 打印Toast
     * @param toast
     * @param context
     */
    public static void showToast(final String toast, final Context context)
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

}
