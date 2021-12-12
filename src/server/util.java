package server;

/**
 *
 * @author Dũng Trần
 */

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class util {

    private static boolean debug = false;
    private final static Locale locale = new Locale("vi");
    private final static NumberFormat en = NumberFormat.getInstance(locale);
    private final static Random rand = new Random();
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat dateFormatWeek = new SimpleDateFormat("yyyy-MM-ww");
    private final static SimpleDateFormat dateFormatDay= new SimpleDateFormat("yyyy-MM-dd");
    
    public static Date getDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long TimeDay(int nDays) {
        return System.currentTimeMillis() + (nDays * 86400000L);
    }

    public static long TimeHours(int nHours) {
        return System.currentTimeMillis() + (+nHours * 3600000L);
    }

    public static long TimeMinutes(int nMinutes) {
        return System.currentTimeMillis() + (nMinutes * 60000L);
    }

    public static long TimeSeconds(long nSeconds) {
        return System.currentTimeMillis() + (nSeconds * 1000L);
    }

    public static long TimeMillis(long nMillis) {
        return System.currentTimeMillis() + nMillis;
    }

    public static Date DateDay(int nDays) {
        Date dat = new Date();
        dat.setTime(dat.getTime() + nDays * 86400000L);
        return dat;
    }

    public static String toDateString(Date date) {
        return dateFormat.format(date);
    }

    public static Date DateHours(int nHours) {
        Date dat = new Date();
        dat.setTime(dat.getTime() + nHours * 3600000L);
        return dat;
    }

    public static Date DateMinutes(int nMinutes) {
        Date dat = new Date();
        dat.setTime(dat.getTime() + nMinutes * 60000L);
        return dat;
    }

    public static Date DateSeconds(long nSeconds) {
        Date dat = new Date();
        dat.setTime(dat.getTime() + nSeconds * 1000L);
        return dat;
    }

    public static String getFormatNumber(long num) {
        return en.format(num);
    }
    
    public static boolean compare_Week(Date now, Date when) {
        try {
            Date date1 = dateFormatWeek.parse(dateFormatWeek.format(now));
            Date date2 = dateFormatWeek.parse(dateFormatWeek.format(when));
            if (date1.equals(date2))
                return false;
            else
                return !date1.before(date2);
        } catch (ParseException p) {
            p.printStackTrace();
        }
        return false;
    }
    
    public synchronized static boolean compare_Day(Date now, Date when) {
        try {
            Date date1 = dateFormatDay.parse(dateFormatDay.format(now));
            Date date2 = dateFormatDay.parse(dateFormatDay.format(when));
            if (date1.equals(date2))
                return false;
            else
                return !date1.before(date2);
        } catch (ParseException p) {
            p.printStackTrace();
        }
        return false;
    }

    public static boolean checkNumInt(String num) {
        return Pattern.compile("^[0-9]+$").matcher(num).find();
    }

    public static int UnsignedByte(byte b) {
        int ch = b;
        if (ch < 0) {
            return ch + 256;
        }
        return ch;
    }

    public static String parseString(String str, String wall) {
        return (!str.contains(wall)) ? null : str.substring(str.indexOf(wall) + 1);
    }

    public static boolean CheckString(String str, String c) {
        return Pattern.compile(c).matcher(str).find();
    }

    public static String strSQL(String str) {
        return str.replaceAll("['\"\\\\]", "\\\\$0");
    }

    public static int nextInt(int x1, int x2) {
        int to = x2;
        int from = x1;
        if (x2 < x1) {
            to = x1;
            from = x2;
        }
        return from + rand.nextInt((to + 1) - from);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }
    
    public static void setDebug(boolean v) {
        debug = v;
    }
    
    public static void Debug(String v) {
        if (debug)
            System.out.println(v);
    }
}
