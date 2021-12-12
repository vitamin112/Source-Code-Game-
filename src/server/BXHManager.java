package server;

/**
 *
 * @author Dũng Trần
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import real.ClanManager;
import real.ItemData;

public class BXHManager {

    public static class Entry {
        int index;
        String name;
        long[] nXH;
    }
    
    public static final ArrayList<Entry> bangXH[] = new ArrayList[6]; // XSMB chỉnh từ 5 -> 6
    public static final Timer t = new Timer(true);
    
    public static void init() {
        for(int i = 0; i < bangXH.length; i++)
            bangXH[i] = new ArrayList<>();
        System.out.println("Dong bo du lieu BXH thang cong!");
        for(int i = 0; i < bangXH.length; i++)
            initBXH(i);
        Calendar cl = GregorianCalendar.getInstance();
        Date d = new Date();cl.setTime(d);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.add(Calendar.DATE, 0);
       // t.schedule(new TimerTask() {
           // public void run() {
           //     for(int i = 0; i < bangXH.length; i++)
           //     bangXH[i] = new ArrayList<>();
           //     System.out.println("Lam moi BXH");
           //     for(int i = 0; i < bangXH.length; i++)
           //     initBXH(i);
           // }
       // }, cl.getTime(), 1000*60); // 1P làm mới 1 lần
    }
    
    public static void initBXH(int type) {
        ResultSet red;
        bangXH[type].clear();
        ArrayList<Entry> bxh = bangXH[type];
        switch(type) {
            case 0:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`yen`,`level` FROM `ninja` WHERE (`yen` > 0) ORDER BY `yen` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        int coin = red.getInt("yen");
                        int level = red.getInt("level");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = coin;
                        bXHE.nXH[1] = level;
                        bxh.add(bXHE);
                        i++;
                    }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`exp`,`level` FROM `ninja` WHERE (`exp` > 0) ORDER BY `exp` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        long exp = red.getLong("exp");
                        int level = red.getInt("level");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = exp;
                        bXHE.nXH[1] = level;
                        bxh.add(bXHE);
                        i++;
                        }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`level` FROM `clan` WHERE (`level` > 0) ORDER BY `level` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        int level = red.getInt("level");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[1];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = level;
                        bxh.add(bXHE);
                        i++;
                        }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`bagCaveMax`,`itemIDCaveMax` FROM `ninja` WHERE (`bagCaveMax` > 0) ORDER BY `bagCaveMax` DESC LIMIT 10;");
                    while(red.next()) {
                        String name = red.getString("name");
                        int cave = red.getInt("bagCaveMax");
                        short id = red.getShort("itemIDCaveMax");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = cave;
                        bXHE.nXH[1] = id;
                        bxh.add(bXHE);
                        i++;
                        }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 4: {
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `name`,`pointCT`,`typeCT` FROM `ninja` WHERE (`pointCT` > 0) ORDER BY `pointCT` DESC LIMIT 15;");
                    while (red.next()) {
                        final String name = red.getString("name");
                        final int pointCT = red.getInt("pointCT");
                        final int typeCT = red.getInt("typeCT");
                        final Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = name;
                        bXHE.index = i;
                        bXHE.nXH[0] = pointCT;
                        bXHE.nXH[1] = typeCT;
                        bxh.add(bXHE);
                        ++i;
                    }
                    red.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            // XSMB
            case 5:
                try {
                    int i = 1;
                    red = SQLManager.stat.executeQuery("SELECT `day`,`code`,`id` FROM `xoso` WHERE (`day` > 0) ORDER BY `day` DESC LIMIT 7;");
                    while(red.next()) {
                        String day = red.getString("day");
                        long code = red.getLong("code");
                        int id = red.getInt("id");
                        Entry bXHE = new Entry();
                        bXHE.nXH = new long[2];
                        bXHE.name = day;
                        bXHE.index = i;
                        bXHE.nXH[0] = code;
                        bXHE.nXH[1] = id;
                        bxh.add(bXHE);
                        i++;
                        }
                    red.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    
    public static final Entry[] getBangXH(int type) {
        ArrayList<Entry> bxh = bangXH[type];
        Entry[] bxhA = new Entry[bxh.size()];
        for(int i = 0; i < bxhA.length; i++)
            bxhA[i] = bxh.get(i);
        return bxhA;
    }
    
    public static String getStringBXH(int type) {
        String str = "";
        switch (type) {
            case 0:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        str += bxh.index+". "+bxh.name+": "+util.getFormatNumber(bxh.nXH[0])+" yên - cấp: "+bxh.nXH[1]+"\n";
                    }
                    break;
                }
                break;
            case 1:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        str += bxh.index+". "+bxh.name+": "+util.getFormatNumber(bxh.nXH[0])+" kinh nghiệm - cấp: "+bxh.nXH[1]+"\n";
                    }
                }
                break;
            case 2:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        ClanManager clan = ClanManager.getClanName(bxh.name);
                        if (clan != null)
                            str += bxh.index+". Gia tộc "+bxh.name+" trình độ cấp "+bxh.nXH[0]+" do "+clan.getmain_name()+" làm tộc trưởng, thành viên "+clan.members.size()+"/"+clan.getMemMax()+"\n";
                        else
                            str += bxh.index+". Gia tộc "+bxh.name+" trình độ cấp "+bxh.nXH[0]+" đã bị giải tán\n";
                    }
                }
                break;
            case 3:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        str += bxh.index+". "+bxh.name+" nhận được "+util.getFormatNumber(bxh.nXH[0])+" "+ItemData.ItemDataId((int)bxh.nXH[1]).name+"\n";
                    }
                }
                break;
            case 4: {
                if (BXHManager.bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                    break;
                }
                for (final Entry bxh : BXHManager.bangXH[type]) {
                    str = str + bxh.index + ". " + bxh.name + ": " + util.getFormatNumber(bxh.nXH[0]) + " điểm " + (bxh.nXH[1] == 4 ? "(Bạch)" :"(Hắc)") + "\nDanh hiệu: " + (bxh.nXH[0] < 4000 ? (bxh.nXH[0] < 1500 ? (bxh.nXH[0] < 600 ? (bxh.nXH[0] < 200 ? "Học Giả" : "Hạ Nhẫn") : "Trung Nhẫn") : "Thượng Nhẫn") : "Nhẫn Giả") + "\n";
                }
                break;
            }
            // XSMB
            case 5:
                if (bangXH[type].isEmpty()) {
                    str = "Chưa có thông tin";
                } else {
                    for (Entry bxh : bangXH[type]) {
                        str += bxh.index+". Ngày "+bxh.name+" - Số trúng thưởng: "+util.getFormatNumber(bxh.nXH[0])+"\n";
                    }
                }
                break;
        }
        return str;
    }
    
}
