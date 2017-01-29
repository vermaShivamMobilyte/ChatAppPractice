package com.shivam.chatapppractice.utils;

import com.shivam.chatapppractice.model.Chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Shivam on 28-01-2017.
 */

public class AppUtils {

    public static final int MSG_TYPE_TXT = 0;
    public static final int MSG_TYPE_IMG = 1;
    public static final int MSG_TYPE_VID = 2;
    public static final int SEND_STATUS_SENT = 0;
    public static final int SEND_STATUS_RECEIVE = 1;
    public static final int SEND_STATUS_READ = 2;

    public static final int TYPE_SENDER = 0;
    public static final int TYPE_RECEIVER = 1;
    public static final int TYPE_FOOTER = 5;

    public static long getCurrentTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
    }

    public static String getlocalTime(long time) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return df.format(calendar.getTime());
    }

}
