package real;

/**
 *
 * @author Dũng Trần
 */

import io.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Mob {
    
    public boolean isFire;
    public boolean isIce;
    public boolean isWind;
    public long timeFire;
    public long timeIce;
    public long timeWind;
    public int id;
    public byte sys;
    public int hp;
    public int level;
    public int hpmax;
    public short x;
    public short y;
    public byte status;
    public int lvboss;
    public boolean isboss;
    public boolean isDie;
    public boolean isRefresh = true;
    public long xpup;
    public long timeRefresh;
    public long timeFight;
    public MobData templates;
    private final HashMap<Integer, Integer> nFight;
    private final ArrayList<Char> sortFight;
    
    public Mob(int id,int idtemplate, int level) {
        this.id = id;
        this.templates = MobData.entrys.get(idtemplate);
        this.level = level;
        this.hp = hpmax = templates.hp;
        this.xpup = 100000;
        this.isDie = false;
        this.nFight = new HashMap<>();
        this.sortFight = new ArrayList<>();
    }
    
    public void updateHP(int num) {
        hp += num;
        if (hp <= 0) {            
            hp = 0;
            status = 0;
            isDie = true;
            if (isRefresh) {
                timeRefresh = System.currentTimeMillis()+ 5000;
            }
            if (isboss) {
                if (this.templates.id != 199 && this.templates.id != 200) {
                    this.isRefresh = false;
                    timeRefresh = -1;
                } else {
                    timeRefresh = 10000;
                }
            }
        }
    }
    
    public void ClearFight() {
        nFight.clear();
    }
    
    public int sortNinjaFight() {
        int idN = -1;
        int dameMax = 0;
        Iterator<Integer> itr = nFight.keySet().iterator();
        while (itr.hasNext()) {
            int value = itr.next();
            int dame = nFight.get(value);
            Session conn = PlayerManager.getInstance().getConn(value);
            if (conn == null || conn.player == null || conn.player.c == null || dame <= dameMax)
                continue;
            dameMax = nFight.get(value);
            idN = conn.player.c.id;
        }
        return idN;
    }
    
    public void Fight(int id,int dame) {
        if (!nFight.containsKey(id))
            nFight.put(id, dame);
        else {
            int damenew = nFight.get(id);
            damenew += dame;
            nFight.replace(id, damenew);
        }
    }
    
    public void removeFight(int id) {
        if (nFight.containsKey(id))
            nFight.remove(id);
    }
    
    public boolean isFight(int id) {
        return nFight.containsKey(id);
    }
    
    public boolean isDisable() {
        return false;
    }
    
    public boolean isDonteMove() {
        return false;
    }
}
