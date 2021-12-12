/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package real;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import server.Manager;
import server.SQLManager;
import server.Server;

/**
 *
 * @author Dũng Trần
 */
public class LDGT {

    public int ldgtID;
    public Map[] map;
    public long time;
    public int level = 0;
    public byte finsh = 0;
    public int x = -1;

    public ArrayList<Char> ninjas = new ArrayList<>();

    private static int idbase;

    private boolean rest = false;

    Server server = Server.getInstance();

    public LDGT(int x) {
        this.x = x;
        this.ldgtID = idbase++;
        if (level == 0) {
            time = System.currentTimeMillis() + (1000 * 60 * 10);
        }
        if (x == 3) {
            this.map = new Map[11];
        }
        this.initMap(x);
        for (byte i = 0; i < this.map.length; i++) {
            this.map[i].timeMap = time;
        }
        ldgt.put(this.ldgtID, this);
    }

    private void initMap(int x) {
        switch (x) {
            case 3:
                this.map[0] = new Map(this, 80);
                this.map[1] = new Map(this, 81);
                this.map[2] = new Map(this, 82);
                this.map[3] = new Map(this, 83);
                this.map[4] = new Map(this, 84);
                this.map[5] = new Map(this, 85);
                this.map[6] = new Map(this, 86);
                this.map[7] = new Map(this, 87);
                this.map[8] = new Map(this, 88);
                this.map[9] = new Map(this, 89);
                this.map[10] = new Map(this, 90);
                break;
        }
    }

    public void updateXP(long xp) {
        synchronized (this) {
            for (short i = 0; i < this.ninjas.size(); i++) {
                try {
                    this.ninjas.get(i).p.updateExp(xp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void updatePoint(int point) {
        synchronized (this) {
            for (short i = 0; i < this.ninjas.size(); i++) {
                this.ninjas.get(i).pointCave += point;
                this.ninjas.get(i).p.setPointPB(this.ninjas.get(i).pointCave);
            }
        }
    }

    public void rest() throws SQLException {
        if (!rest) {
            rest = true;
            synchronized (this) {
                int s = ninjas.size();
                int i = 0;
                while (s > 0) {
                    Char nj = ninjas.get(i);
                    Map ma = server.manager.getMapid(nj.mapLTD);
                    for (byte k = 0; k < ma.area.length; k++) {
                        if (ma.area[k].numplayers < ma.template.maxplayers) {
                            nj.place.leave(nj.p);
                            ma.area[k].EnterMap0(nj);
                            nj.ldgtNum = -1;
                            SQLManager.stat.executeUpdate("UPDATE `ninja` SET `ldgtNum`='" + nj.ldgtNum + "' WHERE `id`=" + nj.id + " LIMIT 1;");
                            break;
                        }
                    }
                    s -= 1;
                    i++;
                }
            }
            for (byte i = 0; i < map.length; i++) {
                map[i].close();
            }
            synchronized (ldgt) {
                ldgt.remove(this.ldgtID);
            }
        }
    }

    public void finsh() throws SQLException {
        synchronized (this) {
            this.level++;
            if (x == 3) {
                this.time = System.currentTimeMillis() + (1000 * 10);
                for (byte u = 0; u < map.length; u++) {
                    map[u].timeMap = time;
                }
            }

            System.err.println(this.finsh);
            if (this.finsh == 0) {
                this.finsh++;
                for (byte i = 0; i < ninjas.size(); i++) {
                    System.err.println(ninjas.size());
                    Char nj = ninjas.get(i);
                    ClanManager clan = null;
                    clan = ClanManager.getClanName(nj.clan.clanName);
                    clan.ldgt = null;
                    nj.p.setTimeMap((int) (this.time - System.currentTimeMillis()) / 1000);
                    nj.p.sendAddchatYellow("Hoàn thành lãnh địa gia tộc");
                    nj.ldgtNum = -1;
                    SQLManager.stat.executeUpdate("UPDATE `ninja` SET `ldgtNum`='" + nj.ldgtNum + "' WHERE `id`=" + nj.id + " LIMIT 1;");
                    if (nj.clan != null && nj.clan.ldgt != null) {
                        nj.clan.ldgt = null;
                    }
                    if (!nj.clan.clanName.isEmpty()) {
                        nj.p.upExpClan(10);
                    }
                }
            }
        }
    }

    public void openMap() {
        synchronized (this) {
            level += 3;
            if (level > map.length) {
                level = map.length - 1;
            }
            if (level < map.length) {
                for (byte i = 0; i < ninjas.size(); i++) {
                    Char nj = ninjas.get(i);
                    if (level != map.length - 1) {
                        nj.p.sendAddchatYellow(map[level - 2].template.name + ", " + map[level - 1].template.name + ", " + map[level].template.name + " đã được mở");
                    } else {
                        nj.p.sendAddchatYellow(map[level].template.name + " đã được mở");
                    }

                }
            }
        }
    }

    public final static HashMap<Integer, LDGT> ldgt = new HashMap<>();
}
