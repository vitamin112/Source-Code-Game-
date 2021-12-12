package server;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import java.io.IOException;
import java.util.ArrayList;
import real.Char;
import real.Player;
import real.PlayerManager;

public class Rotationluck extends Thread {

    public String title = null;
    public short time = 120;
    public int totalxu = 0;
    public int maxtotalxu;
    public short numplayers = 0;
    public byte type = 1;
    private final int minxu;
    private final int maxxu;
    private boolean open = true;

    private short settime = 0;
    private boolean start = false;
    private String winerinfo = "Chưa có thông tin";
    private boolean runing = true;
    private ArrayList<Players> players = new ArrayList<>();

    private class Players {

        String user = null;
        String name = null;
        int joinxu = 0;
    }

    protected Rotationluck(String title, byte type, short time, int minxu, int maxxu, int maxtotal) {
        this.title = title;
        this.type = type;
        this.settime = time;
        this.time = time;
        this.minxu = minxu;
        this.maxxu = maxxu;
        this.maxtotalxu = maxtotal;
    }

    protected int getJoinxu(String njname) {
        for (short i = 0; i < players.size(); i++) {
            if (players.get(i).name.equals(njname)) {
                return players.get(i).joinxu;
            }
        }
        return 0;
    }

    protected synchronized void joinLuck(Player p, int joinxu) throws IOException {
        if (!open || joinxu <= 0) {
            return;
        }
        if (joinxu > p.c.xu) {
            p.conn.sendMessageLog("Bạn không đủ xu.");
            return;
        }
        if (totalxu > maxtotalxu) {
            p.conn.sendMessageLog("Số lượng xu tối đa là " + util.getFormatNumber(maxtotalxu));
            return;
        }
        Players p2 = null;
        for (short i = 0; i < players.size(); i++) {
            if (players.get(i).name.equals(p.c.name)) {
                p2 = players.get(i);
                break;
            }
        }

        if (p2 == null && (joinxu > maxxu || joinxu < minxu)) {
            p.conn.sendMessageLog("Bạn chỉ có thể đặt cược từ " + util.getFormatNumber(minxu) + " đến " + util.getFormatNumber(maxxu) + "xu.");
            return;
        }
        if (p2 == null) {
            p2 = new Players();
            p2.user = p.username;
            p2.name = p.c.name;
            numplayers++;
            players.add(p2);
        }
        if (p2.joinxu + joinxu > maxxu) {
            p.conn.sendMessageLog("Bạn chỉ có thể đặt tối đa " + util.getFormatNumber(maxxu - p2.joinxu) + ".");
            return;
        }
        p2.joinxu += joinxu;
        p.c.upxuMessage(-joinxu);
        totalxu += joinxu;
        if (numplayers == 2 && !start) {
            begin();
            Char ns = PlayerManager.getInstance().getNinja(players.get(0).name);
            if (ns != null) {
                luckMessage(ns.p);
            }
        }
        luckMessage(p);
    }

    private void turned() throws Exception {
        //sort acs
        String tempuser, tempname;
        int tempxu;
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                if (players.get(i).joinxu < players.get(j).joinxu) {
                    tempuser = players.get(j).user;
                    tempname = players.get(j).name;
                    tempxu = players.get(j).joinxu;
                    players.get(j).user = players.get(i).user;
                    players.get(j).name = players.get(i).name;
                    players.get(j).joinxu = players.get(i).joinxu;
                    players.get(i).user = tempuser;
                    players.get(i).name = tempname;
                    players.get(i).joinxu = tempxu;
                }
            }
        }
        Players p = null;
        for (Players player : players) {
            if (percentWin(player.name) > util.nextInt(100)) {
                p = player;
                break;
            }
        }
        if (p == null) {
            p = players.get(util.nextInt(players.size()));
        }
        long xuwin = totalxu;
        if (numplayers > 1)
            xuwin = xuwin * 95 / 100;
        numplayers = 0;
        totalxu = 0;
        Char ns = PlayerManager.getInstance().getNinja(p.name);
        if (ns != null) {
            ns.upxuMessage(xuwin);
        } else {
            synchronized (Server.LOCK_MYSQL) {
                SQLManager.stat.executeUpdate("UPDATE `ninja` SET `xu`=`xu`+" + xuwin + " WHERE `name`='" + p.name + "';");
            }
        }
        Manager.serverChat("Thông báo Trò chơi", "Chúc mừng " + p.name.toUpperCase() + " đã chiến thắng " + util.getFormatNumber(xuwin) + " xu trong trò chơi Vòng xoay may mắn");
        winerinfo = "Người vừa chiến thắng:\n" + ((type == 0) ? "c" + util.nextInt(10) : "") + "" + p.name + "\nSố xu thắng: " + util.getFormatNumber(xuwin) + " xu\nSố xu tham gia: " + util.getFormatNumber(p.joinxu) + " xu";
        players.removeAll(players);
        Thread.sleep(1000L);
        time = settime;
        start = false;
        open = true;
    }

    private void begin() {
        time = settime;
        start = true;
    }

    protected float percentWin(String njname) {
        for (short i = 0; i < players.size(); i++) {
            if (players.get(i).name.equals(njname)) {
                return (((float) players.get(i).joinxu * 100) / (float) totalxu);
            }
        }
        return 0;
    }

    @Override
    public void run() {
        while (runing) {
            try {
                Thread.sleep(1000L);
                if (time > 0 && start) {
                    time--;
                    if (time == 0) {
                        turned();
                    } else if (time < 10) {
                        open = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Close Thread Lucky");
    }

    protected void luckMessage(Player p) throws IOException {
        Message m = new Message(53);
        m.writer().writeUTF("typemoi");//type
        m.writer().writeUTF(title);//title
        m.writer().writeShort(time);//time
        m.writer().writeUTF(util.getFormatNumber(totalxu) + " xu");//totalMoney
        m.writer().writeShort((short) percentWin(p.c.name));//percentWin
        m.writer().writeUTF((util.parseString("" + percentWin(p.c.name), ".") == null ? "" + 0 : util.parseString("" + percentWin(p.c.name), ".")));///percentWin2
        m.writer().writeShort(numplayers);//numPlayer
        m.writer().writeUTF(winerinfo);//winer info
        m.writer().writeByte(type);//type lucky
        m.writer().writeUTF(util.getFormatNumber(getJoinxu(p.c.name)));//my money
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    protected void close() {
        try {
            runing = false;
            if (numplayers > 0) {
                turned();
            }
            title = null;
            winerinfo = null;
            players = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
