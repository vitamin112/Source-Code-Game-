package real;

/**
 *
 * @author Dũng Trần
 */

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import server.SQLManager;
import server.Server;
import server.Service;
import server.util;

public class cloneChar extends Body {
    
    public Char _char = null;
    public int percendame = 0;
    
    public cloneChar(Char n) {
        try {
            this.seNinja(n);
            this._char = n;
            this.id = -n.id-100000;
            this.ItemBody = new Item[16];
            this.ItemMounts = new Item[5];
            this.KSkill = new byte[3];
            this.OSkill = new byte[5];
            for (byte i = 0; i < this.KSkill.length; i++) {
                this.KSkill[i] = -1;
            }
            for (byte i = 0; i < this.OSkill.length; i++) {
                this.OSkill[i] = -1;
            }
            this.isHuman = false;
            this.isNhanban = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static cloneChar getClone(Char n) {
        try {
            synchronized (Server.LOCK_MYSQL) {
                ResultSet red = SQLManager.stat.executeQuery("SELECT * FROM `clone_ninja` WHERE `name`LIKE'" + n.name + "';");
                cloneChar cl;
                if (red.first()) {
                    cl = new cloneChar(n);
                    cl.id = red.getInt("id");
                    cl.speed = red.getByte("speed");
                    cl.nclass = red.getByte("class");
                    cl.ppoint = red.getShort("ppoint");
                    cl.potential0 = red.getShort("potential0");
                    cl.potential1 = red.getShort("potential1");
                    cl.potential2 = red.getInt("potential2");
                    cl.potential3 = red.getInt("potential3");
                    cl.spoint = red.getShort("spoint");
                    JSONArray jar = (JSONArray)JSONValue.parse(red.getString("skill"));
                    if (jar != null) {
                        byte b; for (b = 0; b < jar.size(); b = (byte)(b + 1)) {
                            JSONObject job = (JSONObject)jar.get(b);
                            Skill skill = new Skill();
                            skill.id = Byte.parseByte(job.get("id").toString());
                            skill.point = Byte.parseByte(job.get("point").toString());
                            cl.skill.add(skill);
                        }
                    } 
                    JSONArray jarr2 = (JSONArray)JSONValue.parse(red.getString("KSkill"));
                    cl.KSkill = new byte[jarr2.size()];
                    byte j;
                    for (j = 0; j < cl.KSkill.length; j = (byte)(j + 1)) {
                        cl.KSkill[j] = Byte.parseByte(jarr2.get(j).toString());
                    }
                    jarr2 = (JSONArray)JSONValue.parse(red.getString("OSkill"));
                    cl.OSkill = new byte[jarr2.size()];
                    for (j = 0; j < cl.OSkill.length; j = (byte)(j + 1)) {
                        cl.OSkill[j] = Byte.parseByte(jarr2.get(j).toString());
                    }
                    cl.CSkill = (short)Byte.parseByte(red.getString("CSkill"));
                    cl.level = red.getShort("level");
                    cl.exp = red.getLong("exp");
                    cl.expdown = red.getLong("expdown");
                    cl.pk = red.getByte("pk");
                    cl.ItemBody = new Item[16];
                    jar = (JSONArray)JSONValue.parse(red.getString("ItemBody"));
                    if (jar != null) {
                        for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
                            JSONObject job = (JSONObject)jar.get(j);
                            byte index = Byte.parseByte(job.get("index").toString());
                            cl.ItemBody[index] = ItemData.parseItem(jar.get(j).toString());
                        }
                    }
                    cl.ItemMounts = new Item[5];
                    jar = (JSONArray)JSONValue.parse(red.getString("ItemMounts"));
                    if (jar != null) {
                        for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
                            JSONObject job = (JSONObject)jar.get(j);
                            byte index = Byte.parseByte(job.get("index").toString());
                            cl.ItemMounts[index] = ItemData.parseItem(jar.get(j).toString());
                        }
                    }
                    jar = (JSONArray)JSONValue.parse(red.getString("effect"));
                    for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
                        JSONArray jar2 = (JSONArray)jar.get(j);
                        int effid = Integer.parseInt(jar2.get(0).toString());
                        byte efftype = Byte.parseByte(jar2.get(1).toString());
                        long efftime = Long.parseLong(jar2.get(2).toString());
                        int param = Integer.parseInt(jar2.get(3).toString());
                        Effect eff = new Effect(effid, param);
                        eff.timeStart = 0;
                        eff.timeLength = (int)((eff.timeRemove = efftime) - System.currentTimeMillis());
                        eff = new Effect(effid, 0, (int)efftime, param);
                        cl.veff.add(eff);
                    } 
                    return cl;
                } else {
                    red.close();
                    SQLManager.stat.executeUpdate("INSERT INTO clone_ninja(`id`,`name`,`ItemBody`,`ItemMounts`) VALUES ("+(-10000000-n.id)+",'"+n.name+"','[]','[]');");
                    cl = new cloneChar(n);
                    cl.id = -10000000-n.id;
                    cl.speed = 10;
                    cl.exp = 14000000;
                    cl.setLevel_Exp(cl.exp, true);
                    cl.ItemBody[1] = ItemData.itemDefault(194);
                    Skill skill = new Skill();
                    cl.skill.add(skill);
                    return cl;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void refresh() {
        synchronized (this) {
            this.hp = this.getMaxHP();
            this.mp = this.getMaxMP();
            this.x = (short) util.nextInt(this._char.x-30, this._char.x+30);
            this.y = this._char.y;
            this.isDie = false;
        }
    }
    
    public void move(short x, short y) {
        synchronized(this) {
            this.x = x;
            this.y = y;
            _char.place.move(this.id, x, y);
        }
    }
    
    public void off() {
        synchronized(this) {
            this._char.timeRemoveClone = -1;
            this.isDie = true;
            _char.place.removeMessage(this.id);
        }
    }
    
    public void open(long time, int percentdame) {
        synchronized (this) {
            if (!this.isDie) {
                _char.place.removeMessage(this.id);
            }
            _char.timeRemoveClone = time;
            this.percendame = percentdame;
            this.refresh();
            for (short i = 0; i < _char.place.players.size(); i++) {
                Service.sendclonechar(_char.p, _char.place.players.get(i));
            }
        }
    }
    
    
    public void flush() {
        JSONArray jarr = new JSONArray();
        try {
            synchronized (Server.LOCK_MYSQL) {
                String sqlSET = "`class`=" + this.nclass + ",`ppoint`=" + this.ppoint + ",`potential0`=" + this.potential0 + ",`potential1`=" + this.potential1 + ",`potential2`=" + this.potential2 + ",`potential3`=" + this.potential3 + ",`spoint`=" + this.spoint + ",`level`=" + this.level + ",`exp`=" + this.exp + ",`expdown`=" + this.expdown+",`pk`="+this.pk+"";
                jarr.clear();
                for (Skill skill : this.skill) {
                    jarr.add(SkillData.ObjectSkill(skill));
                }
                sqlSET = sqlSET + ",`skill`='" + jarr.toJSONString() + "'";
                jarr.clear();
                for (byte oid : this.KSkill) {
                    jarr.add(oid);
                }
                sqlSET = sqlSET + ",`KSkill`='" + jarr.toJSONString() + "'";
                jarr.clear();
                
                for (byte oid : this.OSkill) {
                    jarr.add(oid);
                }
                sqlSET = sqlSET + ",`OSkill`='" + jarr.toJSONString() + "',`CSkill`=" + this.CSkill + "";
                jarr.clear();
                byte j;
                for (j = 0; j < this.ItemBody.length; j++) {
                    Item item = this.ItemBody[j];
                    if (item != null) {
                        jarr.add(ItemData.ObjectItem(item, j));
                    } 
                } 
                sqlSET = sqlSET + ",`ItemBody`='" + jarr.toJSONString() + "'";
                jarr.clear();
                for (j = 0; j < this.ItemMounts.length; j++) {
                    Item item = this.ItemMounts[j];
                    if (item != null) {
                        jarr.add(ItemData.ObjectItem(item, j));
                    }
                }
                sqlSET = sqlSET + ",`ItemMounts`='" + jarr.toJSONString() + "'";
                jarr.clear();
                byte i;
                for (i = 0; i < this.veff.size(); i = (byte)(i + 1)) {
                    if (((Effect)this.veff.get(i)).template.type == 0 || ((Effect)this.veff.get(i)).template.type == 18 || ((Effect)this.veff.get(i)).template.type == 25) {
                        JSONArray jarr2 = new JSONArray();
                        jarr2.add(((Effect)this.veff.get(i)).template.id);
                        if (((Effect)this.veff.get(i)).template.id == 36 || ((Effect)this.veff.get(i)).template.id == 42 || ((Effect)this.veff.get(i)).template.id == 37 || ((Effect)this.veff.get(i)).template.id == 38 || ((Effect)this.veff.get(i)).template.id == 39) {
                           jarr2.add(1);
                           jarr2.add(((Effect)this.veff.get(i)).timeRemove);
                        } else {
                            jarr2.add(0);
                            jarr2.add(((Effect)this.veff.get(i)).timeRemove - System.currentTimeMillis());
                        }
                        jarr2.add(((Effect)this.veff.get(i)).param);
                        jarr.add(jarr2);
                    }
                } 
                sqlSET = sqlSET + ",`effect`='" + jarr.toJSONString() + "'";
                jarr.clear();
                SQLManager.stat.executeUpdate("UPDATE `clone_ninja` SET " + sqlSET + " WHERE `id`=" + this.id + " LIMIT 1;");
            } 
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
    
}
