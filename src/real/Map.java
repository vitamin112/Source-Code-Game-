package real;

/**
 *
 * @author Dũng Trần
 */

import boardGame.Place;

public final class Map extends Thread {

    public static final int[] arrLang = new int[]{10,17,22,32,38,43,48};
    public static final int[] arrTruong = new int[]{1,27,72};
    
    public int id;
    public MapTemplate template;
    public Place[] area;
    public Cave cave;
    public War war;
    public long timeMap = -1;
    private boolean runing;
    public LDGT ldgt;
    public boolean LOIDAI() { // Lôi đài
        return (id >= 110 && id <= 111);
    }
    public Map(int id, Cave cave) {
        this.id = id;
        this.template = MapTemplate.arrTemplate[id];
        this.cave = cave;
        this.war = new War();
        this.area = new Place[MapTemplate.arrTemplate[id].numarea];
        for (byte i = 0; i < this.template.numarea; i++) {
            area[i] = new Place(this, i);
        }
        this.initMob();
        this.runing = true;
        this.start();
    }
    
    public void initMob() {
        for (byte j = 0; j < area.length; j++) {
            area[j].mobs.clear();
            int k = 0;
            for (short i = 0; i < this.template.arMobid.length; i++) {
                Mob m = new Mob(k, this.template.arMobid[i], this.template.arrMoblevel[i]);
                m.x = this.template.arrMobx[i];
                m.y = this.template.arrMoby[i];
                m.status = this.template.arrMobstatus[i];
                m.lvboss = this.template.arrLevelboss[i];
                if (m.lvboss == 3) {
                    if ( j%5==0) {
                        m.hp = m.hpmax *= 200;
                    } else {
                        m.lvboss = 0;
                    }
                } else if (m.lvboss == 2) {
                    m.hp = m.hpmax *= 100;
                } else if (m.lvboss == 1) {
                    m.hp = m.hpmax *= 10;
                }
                m.isboss = this.template.arrisboss[i];
                area[j].mobs.add(m);
                k++;
            }
        }
    }
    
public void refreshMobLDGT(int area) {
        for (int i = 0; i < this.area.length; i++) {
            if (i >= 30) {
                break;
            }
            if (i == area) {
                Place place = this.area[i];
                for (short j = 0; j < place.mobs.size(); j++) {
                    Mob mob = place.mobs.get(j);
                    if (mob.templates.id == 81) {
                        System.err.println(mob.templates.name);
                        place.refreshMobLDGT(mob.id);
                    }
                }
                break;
            }
        }
    }

public void refreshBoss(int area) {
        for (int i = 15; i < this.area.length; i++) {
            if (i >= 30)
                break;
            if (i == area) {
                Place place = this.area[i];
                for (short j = 0; j < place.mobs.size(); j++) {
                    Mob mob = place.mobs.get(j);
                    if (mob.status == 0 && mob.isboss) {
                        place.refreshMob(mob.id);
                        System.out.println(mob.templates.name);
                    }
                }
                break;
            }
        }
    }
    
    //public int getXHD() {
    //    if (id == 157 || id == 158 || id == 159)
    //        return 9;
    //    else if (id == 125 || id == 126 || id == 127 || id ==128)
    //        return 7;
    //    else if (id == 114 || id == 115 || id == 116)
    //        return 6;
    //    else if (id == 105 || id == 106 || id == 107 || id == 108 || id == 109)
    //        return 5;
    //    else if (id == 94 || id == 95 || id == 96 || id == 97)
    //        return 4;
    //    else if (id == 91 || id == 92 || id == 93)
    //        return 3;
    //    return -1;
    //}
    
    public int getXHD() {
        if (id == 80 || id == 81 || id == 82 || id == 83 || id == 84 || id == 85 || id == 86 || id == 87 || id == 88 || id == 89 || id == 90 || id == 91) {
            return 10;
        } else if (id == 157 || id == 158 || id == 159) {
            return 9;
        } else if (id == 125 || id == 126 || id == 127 || id == 128) {
            return 7;
        } else if (id == 114 || id == 115 || id == 116) {
            return 6;
        } else if (id == 105 || id == 106 || id == 107 || id == 108 || id == 109) {
            return 5;
        } else if (id == 94 || id == 95 || id == 96 || id == 97) {
            return 4;
        } else if (id == 91 || id == 92 || id == 93) {
            return 3;
        }
        return -1;
    }   
    
    public boolean LangCo() {
        return (id >= 132 && id <= 138);
    }
    
    public boolean VDMQ() {
        return (id >= 139 && id <= 148);
    }
    
    public boolean LDGT() {
        return (id >= 80 && id <= 91);
    }

    private void update() {
        for (byte i = 0; i < area.length; i++) {
            area[i].update();
        }
    }

    @Override
    public void run() {
        while (runing) {
            try {
                long l1 = System.currentTimeMillis();
                update();
                long l2 = System.currentTimeMillis() - l1;
                if (l2 < 500) {
                    try {
                        Thread.sleep((500-l2));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        runing = false;
        for (byte i = 0; i < area.length; i++) {
            area[i].close();
        }
    }
    // LDGT
    public Map(LDGT ldgt, int id) {
        this.id = id;
        this.template = MapTemplate.arrTemplate[id];
        this.ldgt = ldgt;
        this.area = new Place[MapTemplate.arrTemplate[id].numarea];
        for (byte i = 0; i < this.template.numarea; i++) {
            area[i] = new Place(this, i);
        }
        this.initMob();
        this.runing = true;
        this.start();
    }
}
