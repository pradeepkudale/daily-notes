package org.pradale.dailynotes.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AppUtils {

    public static String generateTaskId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddDSSS");
        return String.format("%s-%s%s", "Task", sdf.format(new Date()), new Random().nextInt(9));
    }
}
