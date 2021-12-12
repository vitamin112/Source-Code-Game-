package real;

import java.util.ArrayList;
import server.GameCanvas;
import server.GameScr;

/**
 *
 * @author Dũng Trần
 */

public class Body {
    
    public Char c;
    public int id = 0;
    public byte head = -1;
    protected byte speed = 4;
    public byte nclass = 0;
    public int level = 1;
    public long exp = 1;
    public long expdown = 0;
    public byte pk = 0;
    public byte typepk = 0;
    public short ppoint = 0;
    public short potential0 = 15;
    public short potential1 = 5;
    public int potential2 = 5;
    public int potential3 = 5;
    public short spoint = 0;
    public boolean isDie = false;
    public ArrayList<Skill> skill = new ArrayList<>();
    public byte[] KSkill = null;
    public byte[] OSkill = null;
    public short CSkill = -1;
    public Item[] ItemBody = null;
    public Item[] ItemMounts = null;
    public boolean isHuman;
    public boolean isNhanban;
    public long CSkilldelay = 0;
    public Mob mobMe = null;
    public short x = 0;
    public short y = 0;
    public int hp = 0;
    public int mp = 0;
    
    public Party party = null;
    public byte exptype = 1;
    public byte limitKyNangSo, limitTiemNangSo, banhPhongLoi, banhBangHoa;
    public ArrayList<Effect> veff = new ArrayList<>();
    
    public void seNinja(Char c) {
        this.c = c;
    }
    
    public short partHead() {
        if (this.ItemBody[11] == null) {
            return this.c.head;
        }
        if (this.ItemBody[11].id == 745) {
            return 264;
        }
        
        if (this.ItemBody[11] != null) {
            return (ItemData.ItemDataId((this.ItemBody[11]).id)).part;
        }
        return c.head;
    }
    
    public short Weapon() {
        if (this.ItemBody[1] != null) {
            return (ItemData.ItemDataId((this.ItemBody[1]).id)).part;
        }
        for (byte j = 0; j < this.c.get().ItemBody.length; j++) {
                Item item = this.c.get().ItemBody[j];
               if (item != null && item.id == 799) {
             for (int k = 0; k < this.c.place.players.size(); k++) {
              GameCanvas.addEffect(this.c.place.players.get(k).conn, (byte) 0, this.c.id, (byte) 44, 0, 0, false);
           }
         }
         }
        return -1;
    }
    
    public short Body() {
        if (this.partHead() == 258) {
            return 259;
        }
        if (this.partHead() == 264) {
            return 265;
        }
        if (this.ItemBody[2] != null) {
            return (ItemData.ItemDataId((this.ItemBody[2]).id)).part;
        }
        return -1;
    }
    
    public short Leg() {
        
        if (this.partHead() == 258) {
            return 260;
        }
        if (this.partHead() == 264) {
            return 266;
        }
        if (this.ItemBody[6] != null) {
            return (ItemData.ItemDataId((this.ItemBody[6]).id)).part;
        }
        return -1;
    }
    
    public void updatePk(int num) {
        this.pk += (byte) num;
        if (this.pk < 0)
            this.pk = 0;
        else if (this.pk > 20)
            this.pk = 20;
    }
    
    public int getMaxHP() {
        int hpmax = getPotential(2) * 10;
        hpmax += hpmax*(getPramItem(31)+getPramItem(61)+getPramSkill(17))/100;
        hpmax += getPramItem(6);
        hpmax += getPramItem(32);
        hpmax += getPramItem(77);
        hpmax += getPramItem(82);
        hpmax += getPramItem(82);
        hpmax += getPramItem(125);
        Effect effect = c.get().getEffId(27);
        if (effect != null){
            hpmax += effect.param;
        }
        if (hp > hpmax) {
            hp = hpmax;
        }
        return hpmax;
    }

    public synchronized void upHP(int hpup) {
        if (isDie)
            return;
        hp += hpup;
        if (hp > getMaxHP()) {
            hp = getMaxHP();
        }
        if (hp <= 0) {
            isDie = true;
            hp = 0;
        }

    }

    public int getMaxMP() {
        int mpmax = getPotential(3) * 10;
        mpmax += mpmax*(getPramItem(28)+getPramItem(60)+getPramSkill(18))/100;
        mpmax += getPramItem(7);
        mpmax += getPramItem(19);
        mpmax += getPramItem(29);
        mpmax += getPramItem(83);
        mpmax += getPramItem(117);
        if (mp > mpmax) {
            mp = mpmax;
        }
        return mpmax;
    }

    public synchronized void upMP(int mpup) {
        if (isDie)
            return;
        mp += mpup;
        if (mp > getMaxMP()) {
            mp = getMaxMP();
        } else if (mp < 0)
            mp = 0;
    }

    public int eff5buffHP() {
        int efHP = getPramItem(27);
        efHP += getPramItem(120);
        efHP += getPramItem(99);
        return efHP;
    }

    public int eff5buffMP() {
        int efMP = getPramItem(30);
        efMP += getPramItem(119);
        efMP += getPramItem(99);
        return efMP;
    }
    
    public int speed() {
        int sp = speed;
        sp = sp*(100+getPramItem(16))/100;
        sp += getPramItem(93);
        return sp;
    }
    
    public int dameSide() {
        int si;
        int percent = getPramSkill(11)+getPramItem(94);
        Effect eff = c.get().getEffId(25);
        if (eff != null)
            percent += eff.param;
        eff = c.get().getEffId(17);
        if (eff != null)
            percent +=eff.param;
        eff = c.get().getEffId(19);
        if (eff != null)
            percent += 80+(eff.param*2);
        if (Side() == 1){
            si = getPotential(3);
            si += si*(getPramSkill(1)+getPramItem(9)+percent)/100;
            si += getPramItem(1);
        } else {
            si = getPotential(0);
            si += si*(getPramSkill(0)+getPramItem(8)+percent)/100;
            si += getPramItem(0);
        }
        si += getPramItem(38);
        return si;
    }
    
    public int dameSys() {
        int ds = 0;
        if (Sys() == 1) {
            ds = getPramSkill(2);
            ds += getPramItem(88);
            if (Side() == 1) {
                ds += getPramSkill(8);
                ds += getPramItem(22);
            } else {
                ds += getPramSkill(5);
                ds += getPramItem(21);
            }
        } else if (Sys() == 2) {
            ds = getPramSkill(3);
            ds += getPramItem(89);
            if (Side() == 1) {
                ds += getPramSkill(9);
                ds += getPramItem(24);
            } else {
                ds += getPramSkill(6);
                ds += getPramItem(23);
            }
        } else if (Sys() == 3) {
            ds = getPramSkill(4);
            ds += getPramItem(90);
            if (Side() == 1) {
                ds += getPramSkill(10);
                ds += getPramItem(26);
            } else {
                ds += getPramSkill(7);
                ds += getPramItem(25);
            }
        }
        return ds;
    }

    public int dameMax() {
        int dame = dameSide();
        dame += dameSys();
        dame += getPramItem(73);
        dame += getPramItem(74);
        dame += getPramItem(76);
        dame += getPramItem(87);
        if (dame < 0)
            dame = 0;
        return dame;
    }

    public int dameMin() {
        return dameMax() * 90 / 100;
    }
    
    public int dameDown() {
        int dwn = getPramItem(47);
        dwn += getPramItem(74);
        dwn += getPramItem(80);
        dwn += getPramItem(124);
        return dwn;
    }

    public int ResFire() {
        int bear = getPramItem(2);
        bear += getPramItem(11);
        bear += getPramItem(33);
        bear += getPramItem(70);
        bear += getPramItem(96);
        bear += getPramSkill(19);
        bear += getPramSkill(20);
        Effect eff = c.get().getEffId(19);
        if (eff != null)
            bear += eff.param;
        Effect eff1 = c.get().getEffId(26);
        if (eff1 != null)
            bear += eff1.param;
        return bear;
    }

    public int ResIce() {
        int bear = getPramItem(3);
        bear += getPramItem(12);
        bear += getPramItem(34);
        bear += getPramItem(71);
        bear += getPramItem(95);
        bear += getPramSkill(19);
        bear += getPramSkill(21);
        Effect eff = c.get().getEffId(19);
        if (eff != null)
            bear += eff.param;
        Effect eff1 = c.get().getEffId(26);
        if (eff1 != null)
            bear += eff1.param;
        return bear;
    }

    public int ResWind() {
        int bear  = getPramItem(4);
        bear += getPramItem(13);
        bear += getPramItem(35);
        bear += getPramItem(72);
        bear += getPramItem(97);
        bear += getPramSkill(19);
        bear += getPramSkill(22);
        Effect eff = c.get().getEffId(19);
        if (eff != null)
            bear += eff.param;
        Effect eff1 = c.get().getEffId(26);
        if (eff1 != null)
            bear += eff1.param;
        return bear;
    }

    public int Exactly() {
        int exa = getPotential(1);
        exa += getPramItem(10);
        exa += getPramItem(18);
        exa += getPramItem(75);
        exa += getPramItem(86);
        exa += getPramItem(116);
        exa += getPramSkill(12);
        Effect eff = c.get().getEffId(24);
        if (eff != null)
            exa += eff.param;
        return exa;
    }



    public int Miss() {
        int mi = getPotential(1) * 150 / 100;
        mi += getPramItem(5);
        mi += getPramItem(17);
        mi += getPramItem(62);
        mi += getPramItem(68);
        mi += getPramItem(78);
        mi += getPramItem(84);
        mi += getPramItem(115);
        mi += getPramSkill(13);
        mi += getPramSkill(31);
        Effect eff = c.get().getEffId(11);
        if (eff != null)
            mi += eff.param;
        return mi;
    }

    public int trueDame(){
        return getPramItem(113);
    }

    public int downFatalDame(){
        int downDMG = getPramItem(46);
        downDMG += getPramItem(79);
        downDMG += getPramItem(121);
        return downDMG;
    }

    public int Fatal() {
        int fat = getPramItem(14);
        fat += getPramItem(37);
        fat += getPramItem(69);
        fat += getPramItem(92);
        fat += getPramItem(114);
        fat += getPramSkill(14);
        return fat;
    }
    
    public int FantalDame() {
        int pfd = getPramItem(105);
        return pfd;
    }
    
    public int percentFantalDame() {
        int pfd = getPramItem(39);
        pfd += getPramItem(67);
        pfd += getPramSkill(65);
        return pfd;
    }

    public int ReactDame() {
        int reactd = getPramItem(15);
        reactd += getPramItem(91);
        reactd += getPramItem(126);
        return reactd;
    }

    public int sysUp() {
        int su = 0;
        return su;
    }

    public int sysDown() {
        int sd = 0;
        return sd;
    }
    
    public int percentFire2() {
        int pf = getPramSkill(24);
        return pf;
    }
    
    public int percentFire4() {
        int pf = getPramSkill(34);
        return pf;
    }  
    
    public int percentIce1_5() {
        int pi = getPramSkill(25);
        return pi;
    }
    
    public int percentWind1() {
        int pw = getPramSkill(26);
        return pw;
    }
    
    public int percentWind2() {
        int pw = getPramSkill(36);
        return pw;
    }

    public int getPotential(int i) {
        int potential = 0;
        if (i == 0) {
            potential = potential0;
        } else if (i == 1) {
            potential = potential1;
        } else if (i == 2) {
            potential = potential2;
        } else if (i == 3) {
            potential = potential3;
        }
        potential = potential*(100+getPramItem(58))/100;
        potential += getPramItem(57);
        return potential;
    }
    
    public int getPramItem(int id) {
        if (c.get() == null) {
            return 0;
        }
        int param = 0;
        for (Item body : c.get().ItemBody) {
            if (body == null) {
                continue;
            }
            for (Option option : body.options) {
                if (option.id == id && !ItemData.isUpgradeHide(option.id, body.upgrade)) {
                    param += option.param;
                }
            }
        }
        for (Item mounts : c.get().ItemMounts) {
            if (mounts == null) {
                continue;
            }
            for (Option option : mounts.options) {
                if (option.id == id) {
                    param += option.param;
                }
            }
        }
        return param;
    }

    public int getPramSkill(final int id) {
        if (this.c.get() == null) {
            return 0;
        }
        int param = 0;
        for (short i = 0; i < this.c.get().skill.size(); ++i) {
            final Skill sk = this.c.get().skill.get(i);
            final SkillData data = SkillData.Templates(sk.id);
            if (data.type == 0 || data.type == 2 || sk.id == this.CSkill) {
                final SkillTemplates temp = SkillData.Templates(sk.id, sk.point);
                for (int j = 0; j < temp.options.size(); ++j) {
                    final Option option = temp.options.get(j);
                    if (option.id == id) {
                        param += option.param;
                    }
                }
            }
        }
        return param;
    }
    
    public Effect getEffId(int effid) {
        for (byte i = 0; i < this.veff.size(); i = (byte)(i + 1)) {
            if (effid == ((Effect)this.veff.get(i)).template.id)
                return this.veff.get(i); 
        } 
        return null;
    }
    
    public Effect getEffType(byte efftype) {
        for (byte i = 0; i < this.veff.size(); i = (byte)(i + 1)) {
            if (efftype == ((Effect)this.veff.get(i)).template.type)
                return this.veff.get(i);
        }
        return null;
    }
    
    public Skill getSkill(int id) {
        for (Skill skl : this.skill) {
            if (skl.id == id) {
                return skl;
            }
        } 
        return null;
    }
    
    public void setLevel_Exp(long exp, boolean value) {
        long[] levelExp = Level.getLevelExp(exp);
        if (value) {
            this.level = (int)levelExp[0];
        }
    }
    
    public void upDie() {
        synchronized (this) {
            this.hp = 0;
            this.isDie = true;
            try {
                if (!c.isNhanban) {
                    c.place.sendDie(c);
                }
            } catch (Exception e){}
        }
    }
    
    public int fullTL() {
        int tl = 0;
        boolean ad = false;
        for (byte i = 0; i < 10; i = (byte)(i + 1)) {
            int tl2 = 0;
            Item item = this.ItemBody[i];
            if (item == null)
                return 0;  short j;
                for (j = 0; j < item.options.size(); j = (short)(j + 1)) {
                    Option op = item.options.get(j);
                    if (op.id == 85) {
                        tl2 = op.param;
                        break;
                    } 
                    if (j == item.options.size() - 1)
                        return 0; 
                } 
                if (!ad) {
                    tl = tl2;
                    ad = true;
                }
                if (tl > tl2)
                    tl = tl2;
        }
        return tl;
    }
    
    private byte Sys() {
        return GameScr.SysClass(nclass);
    }
    
    private byte Side() {
        return GameScr.SideClass(nclass);
    }

    boolean hasItemId(int i) { 
        return true; 
    }
}
