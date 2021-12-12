package real;

/**
 *
 * @author Dũng Trần
 */

import java.util.ArrayList;
import java.util.HashMap;
import server.Server;

public class Cave {
    
    public int caveID;
    public Map[] map;
    public long time;
    public int level = 0;
    public byte finsh = 0;
    public int x = -1;
    
    public ArrayList<Char> ninjas = new ArrayList<>();
    
    private static int idbase;
    
    private boolean rest = false;
    
    Server server = Server.getInstance();
    
    public Cave(int x) {
        this.x = x;
        this.caveID = idbase++;
        time = System.currentTimeMillis()+(1000*60*60);
        if (x == 3) {
            this.map = new Map[3];
        } else if (x == 4) {
            this.map = new Map[4];
        } else if (x == 5) {
            this.map = new Map[5];
        } else if (x == 6) {
            this.map = new Map[3];
        } else if (x == 7) {
            this.map = new Map[4];
        } else if (x == 9) {
            this.map = new Map[3];
        }
        this.initMap(x);
        for (byte i = 0; i < this.map.length; i++) {
            this.map[i].timeMap = time;
        }
        caves.put(this.caveID, this);
    }
    
    private void initMap(int x) {
        switch (x) {
            case 3:
                this.map[0] = new Map(91, this);
                this.map[1] = new Map(92, this);
                this.map[2] = new Map(93, this);
                break;
            case 4:
                this.map[0] = new Map(94, this);
                this.map[1] = new Map(95, this);
                this.map[2] = new Map(96, this);
                this.map[3] = new Map(97, this);
                break;
            case 5:
                this.map[0] = new Map(105, this);
                this.map[1] = new Map(106, this);
                this.map[2] = new Map(107, this);
                this.map[3] = new Map(108, this);
                this.map[4] = new Map(109, this);
                break;
            case 6:
                this.map[0] = new Map(114, this);
                this.map[1] = new Map(115, this);
                this.map[2] = new Map(116, this);
                break;
            case 7:
                this.map[0] = new Map(125, this);
                this.map[1] = new Map(126, this);
                this.map[2] = new Map(127, this);
                this.map[3] = new Map(128, this);
                break;
            case 9:
                this.map[0] = new Map(157, this);
                this.map[1] = new Map(158, this);
                this.map[2] = new Map(159, this);
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
    
    public void rest() {
        if (!rest) {
            rest = true;
            synchronized (this) {
                while (ninjas.size() > 0) {
                    Char nj = ninjas.get(0);
                    nj.place.leave(nj.p);
                    nj.p.restCave();
                    Map ma = server.manager.getMapid(nj.mapLTD);
                    for (byte k = 0; k < ma.area.length; k++) {
                        if (ma.area[k].numplayers < ma.template.maxplayers) {
                            ma.area[k].EnterMap0(nj);
                            break;
                        }
                    }
                }
            }
            for (byte i = 0; i < map.length; i++) {
                map[i].close();
            }
            synchronized (caves) {
                caves.remove(this.caveID);
            }
        }
    }
    
    public void finsh() {
        synchronized (this) {
            this.level++;
            if (x != 6) {
                this.time = System.currentTimeMillis()+(1000*10);
                for (byte u = 0; u < map.length; u++) {
                    map[u].timeMap = time;
                }
            }
            if (this.x != 6 || this.finsh == 0) {
                this.finsh++;
                for (byte i = 0; i < ninjas.size(); i++) {
                    Char nj = ninjas.get(i);
                    nj.p.setTimeMap((int)(this.time-System.currentTimeMillis())/1000);
                    nj.p.sendAddchatYellow("Hoàn thành hang động");
                    if (nj.party != null && nj.party.cave != null) {
                        nj.party.cave = null;
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
            level++;
            if (level < map.length) {
                for (byte i = 0; i < ninjas.size(); i++) {
                    Char nj = ninjas.get(i);
                    nj.p.sendAddchatYellow(map[level].template.name+" đã được mở");
                }
            }
        }
    }
    
    public final static HashMap<Integer, Cave> caves = new HashMap<>();
}
