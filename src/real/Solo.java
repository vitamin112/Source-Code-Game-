package real;

/**
 *
 * @author Dũng Trần
 */

import io.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Solo extends Thread implements Runnable {

    public int timeCount = 300;
    public Soloer sl_1 = new Soloer();
    public Soloer sl_2 = new Soloer();
    public int checkWin = 0;

    public void showTyThi() throws IOException {
        Message ms = new Message(66);
        DataOutputStream ds = ms.writer();
        ds.writeInt(sl_1.player.c.id);

        ds.flush();
        sl_2.player.c.place.sendMyMessage(sl_2.player, ms);

        ds.writeInt(sl_2.player.c.id);
        ds.flush();
        sl_1.player.c.place.sendMyMessage(sl_1.player, ms);

        ms.cleanup();
    }

    public void endSolo() throws IOException {

        sl_1.player.c.typeSolo = 0;
        sl_2.player.c.typeSolo = 0;

        Message ms = new Message(67);
        DataOutputStream ds = ms.writer();

        if (checkWin == 2) {
            ds.writeInt(sl_1.player.c.id);
            ds.writeInt(sl_2.player.c.id);
            ds.writeInt(sl_2.player.c.hp);
        } else if (checkWin == 1) {
            ds.writeInt(sl_2.player.c.id);
            ds.writeInt(sl_1.player.c.id);
            ds.writeInt(sl_1.player.c.hp);
        } else {
            ds.writeInt(sl_2.player.c.id);
            ds.writeInt(sl_1.player.c.id);
        }

        ds.flush();
        sl_1.player.c.place.sendMessage(ms);
        sl_2.player.c.place.sendMessage(ms);
        ms.cleanup();

    }

    public void run() {
        try {
            showTyThi();
            while (timeCount > 0) {

                if (sl_1.player.c.typeSolo == 1 && sl_2.player.c.typeSolo == 1) {
                    if (sl_2.player.c.isDie) {
                        checkWin = 1;
                        endSolo();

                        Thread.sleep(400);

                        sl_2.player.c.isDie = false;
                        sl_2.player.c.hp = sl_2.player.c.getMaxHP();
                        sl_2.player.c.mp = sl_2.player.c.getMaxMP();

                        Message m = new Message(-10);
                        sl_2.player.conn.sendMessage(m);
                        Message m2 = new Message(88);
                        m2.writer().writeInt(sl_2.player.c.id);
                        m2.writer().writeShort(sl_2.player.c.x);
                        m2.writer().writeShort(sl_2.player.c.y);
                        m2.writer().flush();
                        m2.cleanup();
                        sl_2.player.c.place.sendToMap(m2);
                        return;
                    } else if (sl_1.player.c.isDie) {
                        checkWin = 2;
                        endSolo();

                        Thread.sleep(400);

                        sl_1.player.c.isDie = false;
                        sl_1.player.c.hp = sl_1.player.c.getMaxHP();
                        sl_1.player.c.mp = sl_1.player.c.getMaxMP();

                        Message m = new Message(-10);
                        sl_1.player.conn.sendMessage(m);
                        Message m2 = new Message(88);
                        m2.writer().writeInt(sl_1.player.c.id);
                        m2.writer().writeShort(sl_1.player.c.x);
                        m2.writer().writeShort(sl_1.player.c.y);
                        m2.writer().flush();
                        m2.cleanup();
                        sl_1.player.c.place.sendToMap(m2);
                        return;
                    }
                } else {
                    endSolo();
                    return;
                }

                timeCount--;
                Thread.sleep(200);
            }
            endSolo();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Solo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public class Soloer {

        public Player player;
    }
}
