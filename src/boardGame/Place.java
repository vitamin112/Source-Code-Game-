 package boardGame;

 /**
 *
 * @author Dũng Trần
 */

import io.Message;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import real.Body;
import real.Effect;
import real.Item;
import real.ItemData;
import real.Level;
import real.Option;
import real.Map;
import real.Mob;
import real.Char;
import real.LDGT;
import real.Npc;
import real.Party;
import real.Player;
import real.Skill;
import real.SkillData;
import real.SkillTemplates;
import real.Vgo;
import server.BXHManager;
import server.GameCanvas;
import server.GameScr;
import server.Manager;
import server.SQLManager;
import server.Server;
import server.Service;
import server.util;

public class Place {

    public Map map;
    protected byte id;
    public byte numplayers = 0;
    private int numTA = 0;
    private int numTL = 0;
    protected int numMobDie = 0;
    public final ArrayList<Player> players = new ArrayList<>();
    public final ArrayList<Mob> mobs = new ArrayList<>();
    private final ArrayList<ItemMap> itemMap = new ArrayList<>();
    Server server = Server.getInstance();

    public Place(Map map, byte id) {
        this.map = map;
        this.id = id;
    }

    public void sendMessage(Message m) {
        try {
            for (int i = players.size() - 1; i >= 0; i--) {
                players.get(i).conn.sendMessage(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMyMessage(Player p, Message m) {
        for (int i = players.size() - 1; i >= 0; i--) {
            if (p.id != players.get(i).id) {
                players.get(i).conn.sendMessage(m);
            }
        }
    }

    public Mob getMob(int id) {
        for (short i = 0; i < mobs.size(); i++) {
            if (mobs.get(i).id == id) {
                return mobs.get(i);
            }
        }
        return null;
    }

    public ArrayList getArryListParty() {
        synchronized (this) {
            ArrayList<Party> partys = new ArrayList<>();
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                if (p.c.get().party != null) {
                    boolean co = true;
                    for (int j = 0; j < partys.size(); j++) {
                        if (p.c.get().party.id == partys.get(j).id) {
                            co = false;
                            break;
                        }
                    }
                    if (co) {
                        partys.add(p.c.get().party);
                    }
                }
            }
            return partys;
        }
    }

    public Char getNinja(int id) {
        synchronized (this) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).c.id == id) {
                    return players.get(i).c;
                }
            }
            return null;
        }
    }

    private short getItemMapNotId() {
        short itemmapid = 0;
        while (true) {
            boolean isset = false;
            for (int i = itemMap.size() - 1; i >= 0; i--) {
                if (itemMap.get(i).itemMapId == itemmapid) {
                    isset = true;
                }
            }
            if (!isset) {
                return itemmapid;
            }
            itemmapid++;
        }
    }

    public void leave(Player p) {
        synchronized (this) {
            if (map.cave != null && map.cave.ninjas.contains(p.c)) {
                map.cave.ninjas.remove(p.c);
            }
            if (players.contains(p)) {
                players.remove(p);
                removeMessage(p.c.id);
                removeMessage(p.c.clone.id);
                numplayers--;
            }
        }
    }

    public void changerTypePK(Player p, Message m) throws IOException {
        if (p.c.isNhanban) {
            p.sendAddchatYellow("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        byte pk = m.reader().readByte();
        m.cleanup();
        if (p.c.pk > 14) {
            p.sendAddchatYellow("Điểm hiếu chiến quá cao không thể thay đổi chế độ pk");
            return;
        }
        if (map.id >= 98 && map.id <= 104 && p.c.typepk == 4) {
            p.sendAddchatYellow("Không thể thay đổi chế độ PK");
            return;
        }
        if (pk < 0 || pk > 3) {
            return;
        }
        p.c.typepk = pk;
        m = new Message(-30);
        m.writer().writeByte(-92);
        m.writer().writeInt(p.c.id);
        m.writer().writeByte(pk);
        sendMessage(m);
        m.cleanup();
    }

    public void sendCoat(Body b, Player pdo) {
        try {
            if (b.ItemBody[12] == null) {
                return;
            }
            Message m = new Message(-30);
            m.writer().writeByte((72 - 128));
            m.writer().writeInt(b.id);
            m.writer().writeInt(b.hp);
            m.writer().writeInt(b.getMaxHP());
            m.writer().writeShort(b.ItemBody[12].id);
            m.writer().flush();
            pdo.conn.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGlove(Body b, Player pdo) {
        try {
            if (b.ItemBody[13] == null) {
                return;
            }
            Message m = new Message(-30);
            m.writer().writeByte((73 - 128));
            m.writer().writeInt(b.id);
            m.writer().writeInt(b.hp);
            m.writer().writeInt(b.getMaxHP());
            m.writer().writeShort(b.ItemBody[13].id);
            m.writer().flush();
            pdo.conn.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMounts(Body b, Player pdo) {
        try {
            Message m = new Message(-30);
            m.writer().writeByte(-54);
            m.writer().writeInt(b.id);//id ninja
            for (byte i = 0; i < 5; i++) {
                Item item = b.ItemMounts[i];
                if (item != null) {
                    m.writer().writeShort(item.id);
                    m.writer().writeByte(item.upgrade);//cap
                    m.writer().writeLong(item.expires);//het han
                    m.writer().writeByte(item.sys);//thuoc tinh
                    m.writer().writeByte(item.options.size());//lent option
                    for (Option Option : item.options) {
                        m.writer().writeByte(Option.id);
                        m.writer().writeInt(Option.param);
                    }
                } else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            pdo.conn.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Chat(Player p, Message m) throws IOException, InterruptedException {
        String chat = m.reader().readUTF();
        // Thông báo bảo trì
        if (chat.equals("taurus@baotri")) {
          int timeCount = 3;
            while (timeCount > 0) {
                Manager.serverChat("Thông báo Bảo trì", "Hệ thống sẽ bảo trì sau " + timeCount + " phút. Vui lòng thoát game trước thời gian bảo trì, để tránh mất vật phẩm và điểm kinh nghiệm. Xin cảm ơn!");
                timeCount--;
                Thread.sleep(60000);
            }
            if (timeCount == 0) {
                this.server.stop();
            }
            return;
      } else if (chat.equals("taurus@mat")) { // Mắt 10
           for (byte n = 0; n < 1; n++) {// Số lượng vật phẩm gửi
                                final Item itemup = ItemData.itemDefault(694,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.c.addItemBag(false, itemup);
                            } 
          return;
      } else if (chat.equals("taurus@aochoang")) { // Áo choàng 16
           for (byte n = 0; n < 1; n++) {// Số lượng vật phẩm gửi
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.c.addItemBag(false, itemup);
                            } 
          return;
      } else if (chat.equals("taurus@ptl")) { // Áo choàng 16
           for (byte n = 0; n < 1; n++) {// Số lượng vật phẩm gửi
                                final Item itemup = ItemData.itemDefault(545,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.c.addItemBag(false, itemup);
                            } 
          return;
      } else if (chat.equals("taurus@level")) {
           p.updateExp(Level.getMaxExp(130) - p.c.exp);
          return;
       } else if (chat.equals("taurus@gaytraitim")) {
           for (byte n = 0; n < 1; n++) {// Số lượng vật phẩm gửi
                                final Item itemup = ItemData.itemDefault(799,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.c.addItemBag(false, itemup);
                            } 
      } else {
        m.cleanup();
        m = new Message(-23);
        m.writer().writeInt(p.c.get().id);
        m.writer().writeUTF(chat);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    } 
    
    }

    public void EnterMap0(Char n) {
        n.clone.x = n.x = map.template.x0;
        n.clone.y = n.y = map.template.y0;
        n.mapid = map.id;
        try {
            Enter(n.p);
        } catch (IOException e) {
        }
    }

    public void Enter(Player p) throws IOException {
        synchronized (this) {
            players.add(p);
            p.c.place = this;
            numplayers++;
            p.c.mobAtk = -1;
            p.c.eff5buff = System.currentTimeMillis() + 5000L;
            if (map.cave != null) {
                map.cave.ninjas.add(p.c);
            }
            if (map.timeMap != -1 && map.cave != null) {
                p.setTimeMap((int) (map.cave.time - System.currentTimeMillis()) / 1000);
            }
            if (map.timeMap != -1 && map.ldgt != null) {
                p.setTimeMap((int) (map.ldgt.time - System.currentTimeMillis()) / 1000);
            }
            if (map.ldgt != null) {
                map.ldgt.ninjas.add(p.c);
            }
            Message m = new Message(57);
            m.writer().flush();
            p.conn.sendMessage(m);
            m = new Message(-18);
            m.writer().writeByte(map.id);//map id
            m.writer().writeByte(map.template.tileID);//tile id
            m.writer().writeByte(map.template.bgID);//bg id
            m.writer().writeByte(map.template.typeMap);//type map
            m.writer().writeUTF(map.template.name);//name map
            m.writer().writeByte(id);//zone
            m.writer().writeShort(p.c.get().x);//X
            m.writer().writeShort(p.c.get().y); // Y
            m.writer().writeByte(map.template.vgo.length);// vgo
            for (byte i = 0; i < map.template.vgo.length; i++) {
                m.writer().writeShort(map.template.vgo[i].minX);//x
                m.writer().writeShort(map.template.vgo[i].minY);//y
                m.writer().writeShort(map.template.vgo[i].maxX);//xnext
                m.writer().writeShort(map.template.vgo[i].maxY);//ynext
            }
            m.writer().writeByte(mobs.size());// mob
            for (short i = 0; i < mobs.size(); i++) {
                Mob mob = mobs.get(i);
                m.writer().writeBoolean(mob.isDisable());//isDisable
                m.writer().writeBoolean(mob.isDonteMove());//isDontMove
                m.writer().writeBoolean(mob.isFire);//isFire
                m.writer().writeBoolean(mob.isIce);//isIce
                m.writer().writeBoolean(mob.isWind);//isWind
                m.writer().writeByte(mob.templates.id);//id templates
                m.writer().writeByte(mob.sys);//sys
                m.writer().writeInt(mob.hp);//hp
                m.writer().writeByte(mob.level);//level
                m.writer().writeInt(mob.hpmax);//hp max
                m.writer().writeShort(mob.x);//x
                m.writer().writeShort(mob.y);//y
                m.writer().writeByte(mob.status);//status
                m.writer().writeByte(mob.lvboss);//level boss
                m.writer().writeBoolean(mob.isboss);//isBosss
            }
            m.writer().writeByte(0); // 
            for (int i = 0; i < 0; i++) {
                m.writer().writeUTF("khúc gỗ");//name
                m.writer().writeShort(1945);//x
                m.writer().writeShort(240);//y
            }
            m.writer().writeByte(map.template.npc.length);//numb npc
            for (Npc npc : map.template.npc) {
                m.writer().writeByte(npc.type); //type
                m.writer().writeShort(npc.x); //x
                m.writer().writeShort(npc.y); //y
                m.writer().writeByte(npc.id); //id
            }
            m.writer().writeByte(itemMap.size());// item map
            for (int i = 0; i < itemMap.size(); i++) {
                ItemMap im = itemMap.get(i);
                m.writer().writeShort(im.itemMapId); //item map id
                m.writer().writeShort(im.item.id); //id item
                m.writer().writeShort(im.x); //x
                m.writer().writeShort(im.y); //y
            }
            m.writer().writeUTF(map.template.name);//name zone
            m.writer().writeByte(0);// item
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
            //Send Info team to me
            for (int i = players.size() - 1; i >= 0; i--) {
                Player player = players.get(i);
                if (player.id != p.id) {
                    sendCharInfo(player, p);
                    sendCoat(player.c.get(), p);
                    sendGlove(player.c.get(), p);
                }
                if (!player.c.isNhanban && !player.c.clone.isDie) {
                    Service.sendclonechar(player, p);
                }
                sendMounts(player.c.get(), p);
            }
            //Send Info do team
            for (int i = players.size() - 1; i >= 0; i--) {
                Player player = players.get(i);
                if (player.id != p.id) {
                    sendCharInfo(p, player);
                    sendCoat(p.c.get(), player);
                    sendGlove(p.c.get(), player);
                    if (!player.c.isNhanban && p.c.timeRemoveClone > System.currentTimeMillis()) {
                        Service.sendclonechar(p, player);
                    }
                }
                sendMounts(p.c.get(), player);
            }
            if (p.c.level == 1) {
                p.updateExp(Level.getMaxExp(10));
                p.upluongMessage(70000L);
                p.c.upxuMessage(2000000L);
                p.c.upyenMessage(2000000L);
            }
            if (util.compare_Day(Date.from(Instant.now()), p.c.newlogin)) {
                p.c.pointCave = 0;
                p.c.nCave = 1;
                p.c.useCave = 5;
                p.c.ddClan = false;
		p.c.ddLogin = false;
                p.c.newlogin = Date.from(Instant.now());
            }
        }
    }

    public void VGo(Player p, Message m) throws IOException {
        m.cleanup();
        for (byte i = 0; i < map.template.vgo.length; i++) {
            Vgo vg = map.template.vgo[i];
            if (p.c.get().x + 100 >= vg.minX && p.c.get().x <= vg.maxX + 100 && p.c.get().y + 100 >= vg.minY && p.c.get().y <= vg.maxY + 100) {
                leave(p);
                int mapid;
                if (map.id == 138) {
                    mapid = new int[]{134, 135, 136, 137}[util.nextInt(4)];
                } else {
                    mapid = vg.mapid;
                }
                Map ma = Manager.getMapid(mapid);
                if (map.cave != null) {
                    for (byte j = 0; j < map.cave.map.length; j++) {
                        if (map.cave.map[j].id == mapid) {
                            ma = map.cave.map[j];
                        }
                    }
                }
                if (map.ldgt != null) {
                    for (byte j = 0; j < map.ldgt.map.length; j++) {
                        if (map.ldgt.map[j].id == mapid) {
                            ma = map.ldgt.map[j];
                        }
                    }
                }
                for (byte j = 0; j < ma.template.vgo.length; j++) {
                    Vgo vg2 = ma.template.vgo[j];
                    if (vg2.mapid == map.id) {
                        p.c.get().x = (short) (vg2.goX);
                        p.c.get().y = (short) (vg2.goY);
                    }
                }
                byte errornext = -1;
                for (byte n = 0; n < p.c.get().ItemMounts.length; n++) {
                    if (p.c.get().ItemMounts[n] != null && p.c.get().ItemMounts[n].isExpires && p.c.get().ItemMounts[n].expires < System.currentTimeMillis()) {
                        errornext = 1;
                    }
                }
                if (map.cave != null && map.getXHD() < 9 && map.cave.map.length > map.cave.level && map.cave.map[map.cave.level].id < mapid) {
                    errornext = 2;
                }
                if (map.ldgt != null && map.getXHD() < 11 && map.ldgt.map.length > map.ldgt.level && map.ldgt.map[map.ldgt.level].id < mapid) {
                    errornext = 2;
                }
                if (errornext == -1) {
                    for (byte j = 0; j < ma.area.length; j++) {
                        if (ma.area[j].numplayers < ma.template.maxplayers) {
                            if (map.id == 138) {
                                ma.area[j].EnterMap0(p.c);
                            } else {
                                p.c.mapid = mapid;
                                p.c.x = vg.goX;
                                p.c.y = vg.goY;
                                p.c.clone.x = p.c.x;
                                p.c.clone.y = p.c.y;
                                ma.area[j].Enter(p);
                            }
                            return;
                        }
                        if (j == ma.area.length - 1) {
                            errornext = 0;
                        }
                    }
                }
                Enter(p);
                switch (errornext) {
                    case 0:
                        p.conn.sendMessageLog("Bản đồ quá tải.");
                        return;
                    case 1:
                        p.conn.sendMessageLog("Trang bị thú cưới đã hết hạn. Vui lòng tháo ra để di chuển");
                        return;
                    case 2:
                        p.conn.sendMessageLog("Cửa " + ma.template.name + " vẫn chưa mở");
                        return;
                }
            }
        }
    }

    public void moveMessage(Player p, Message m) throws IOException {
        short x, y, xold, yold;
        if (p.c.get().getEffId(18) != null) {
            return;
        }
        xold = p.c.get().x;
        yold = p.c.get().y;
        x = m.reader().readShort();
        y = m.reader().readShort();
        p.c.x = x;
        p.c.y = y;
        if (p.c.isNhanban) {
            p.c.clone.x = x;
            p.c.clone.y = y;
        }
        m.cleanup();
        move(p.c.get().id, p.c.get().x, p.c.get().y);
        /*     m = new Message(-23);
       m.writer().writeInt(p.nj.id);
        m.writer().writeUTF("X="+p.nj.x+"\nY="+p.nj.y);
        m.writer().flush();
       sendMessage(m);
       m.cleanup();*/
    }

    public void move(int id, short x, short y) {
        try {
            Message m = new Message(1);
            m.writer().writeInt(id);
            m.writer().writeShort(x);
            m.writer().writeShort(y);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeItemMapMessage(short itemmapid) throws IOException {
        Message m = new Message(-15);
        m.writer().writeShort(itemmapid);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    public synchronized void pickItem(Player p, Message m) throws IOException {
        if (m.reader().available() == 0) {
            return;
        }
        short itemmapid = m.reader().readShort();
        m.cleanup();
        for (short i = 0; i < itemMap.size(); i++) {
            if (itemMap.get(i).itemMapId == itemmapid) {
                ItemMap itemmap = itemMap.get(i);
                Item item = itemmap.item;
                ItemData data = ItemData.ItemDataId(item.id);
                if (itemmap.master != -1 && itemmap.master != p.c.id) {
                    p.sendAddchatYellow("Vật phẩm của người khác.");
                    return;
                } else if (Math.abs(itemmap.x - p.c.get().x) > 50 || Math.abs(itemmap.y - p.c.get().y) > 30) {
                    p.sendAddchatYellow("Khoảng cách quá xa.");
                    return;
                } else if (data.type == 19 || p.c.getBagNull() > 0 || (p.c.getIndexBagid(item.id, item.isLock) != -1 && data.isUpToUp)) {
                    itemMap.remove(i);
                    m = new Message(-13);
                    m.writer().writeShort(itemmap.itemMapId);
                    m.writer().writeInt(p.c.get().id);
                    m.writer().flush();
                    sendMyMessage(p, m);
                    m.cleanup();
                    m = new Message(-14);
                    m.writer().writeShort(itemmap.itemMapId);
                    if (ItemData.ItemDataId(item.id).type == 19) {
                        p.c.upyen(item.quantity);
                        m.writer().writeShort(item.quantity);
                    }
                    m.writer().flush();
                    p.conn.sendMessage(m);
                    m.cleanup();
                    if (ItemData.ItemDataId(item.id).type != 19) {
                        p.c.addItemBag(true, itemmap.item);
                    }
                    break;
                } else {
                    p.conn.sendMessageLog("Hành trang không đủ chỗ trống.");
                }
            }
        }
    }

    public void leaveItemBackground(Player p, Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        Item itembag = p.c.getIndexBag(index);
        if (itembag == null || itembag.isLock) {
            return;
        }
        if (itemMap.size() > 100) {
            removeItemMapMessage(itemMap.remove(0).itemMapId);
        }
        short itemmapid = getItemMapNotId();
        ItemMap item = new ItemMap();
        item.x = p.c.get().x;
        item.y = p.c.get().y;
        item.itemMapId = itemmapid;
        item.item = itembag;
        itemMap.add(item);
        p.c.ItemBag[index] = null;
        m = new Message(-6);
        m.writer().writeInt(p.c.get().id);
        m.writer().writeShort(item.itemMapId);
        m.writer().writeShort(item.item.id);
        m.writer().writeShort(item.x);
        m.writer().writeShort(item.y);
        m.writer().flush();
        sendMyMessage(p, m);
        m.cleanup();
        m = new Message(-12);
        m.writer().writeByte(index);
        m.writer().writeShort(item.itemMapId);
        m.writer().writeShort(item.x);
        m.writer().writeShort(item.y);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void refreshMob(int mobid) {
        try {
            synchronized (this) {
                Mob mob = getMob(mobid);
                mob.ClearFight();
                mob.sys = (byte) util.nextInt(1, 3);
                if (map.cave == null && mob.lvboss != 3 && !mob.isboss) {
                    if (mob.lvboss > 0) {
                        mob.lvboss = 0;
                    }
                    if (mob.level >= 10 && 1 > util.nextInt(100) && numTA < 2 && numTL < 1) {
                        mob.lvboss = util.nextInt(1, 2);
                    }
                }
                if (map.cave != null && map.cave.finsh > 0 && map.getXHD() == 6) {
                    int hpup = mob.templates.hp * ((10 * map.cave.finsh) + 100) / 100;
                    mob.hp = mob.hpmax = hpup;
                } else {
                    mob.hp = mob.hpmax = mob.templates.hp;
                }
                if (mob.lvboss == 3) {
                    mob.hp = mob.hpmax *= 200;
                } else if (mob.lvboss == 2) {
                    numTL++;
                    mob.hp = mob.hpmax *= 100;
                } else if (mob.lvboss == 1) {
                    numTA++;
                    mob.hp = mob.hpmax *= 10;
                }
                mob.status = 5;
                mob.isDie = false;
                mob.timeRefresh = 0;
                Message m = new Message(-5);
                m.writer().writeByte(mob.id);
                m.writer().writeByte(mob.sys);
                m.writer().writeByte(mob.lvboss);
                m.writer().writeInt(mob.hpmax);
                m.writer().flush();
                sendMessage(m);
                m.cleanup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void attachedMob(int dame, int mobid, boolean fatal) throws IOException {
        Message m = new Message(-1);
        m.writer().writeByte(mobid);
        Mob mob = getMob(mobid);
        m.writer().writeInt(mob.hp);
        m.writer().writeInt(dame);
        m.writer().writeBoolean(fatal);//flag
        m.writer().writeByte(mob.lvboss);
        m.writer().writeInt(mob.hpmax);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    private void MobStartDie(int dame, int mobid, boolean fatal) throws IOException {
        Mob mob = getMob(mobid);
        Message m = new Message(-4);
        m.writer().writeByte(mobid);
        m.writer().writeInt(dame);
        m.writer().writeBoolean(fatal);//flag
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    private void sendXYPlayer(Player p) throws IOException {
        Message m = new Message(52);
        m.writer().writeShort(p.c.get().x);
        m.writer().writeShort(p.c.get().y);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    private void setXYPlayers(short x, short y, Player p1, Player p2) throws IOException {
        p1.c.get().x = p2.c.get().x = x;
        p1.c.get().y = p2.c.get().y = y;
        Message m = new Message(64);
        m.writer().writeInt(p1.c.get().id);
        m.writer().writeShort(p1.c.get().x);
        m.writer().writeShort(p1.c.get().y);
        m.writer().writeInt(p2.c.get().id);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    public void removeMessage(int id) {
        try {
            Message m = new Message(2);
            m.writer().writeInt(id);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCharInfo(Player p, Player p2) {
        try {
            Message m = new Message(3);
            m.writer().writeInt(p.c.get().id);//id ninja
            m.writer().writeUTF(p.c.clan.clanName);//clan name
            if (!p.c.clan.clanName.isEmpty()) {
                m.writer().writeByte(p.c.clan.typeclan);//type clan
            }
            m.writer().writeBoolean(false);//isInvisible
            m.writer().writeByte(p.c.get().typepk);// type pk
            m.writer().writeByte(p.c.get().nclass);// class
            m.writer().writeByte(p.c.gender);// gender
            m.writer().writeShort(p.c.get().partHead());//head
            m.writer().writeUTF(p.c.name);//name
            m.writer().writeInt(p.c.get().hp);//hp
            m.writer().writeInt(p.c.get().getMaxHP());//hp max
            m.writer().writeByte(p.c.get().level);//level
            m.writer().writeShort(p.c.get().Weapon());//vu khi
            m.writer().writeShort(p.c.get().Body());// body
            m.writer().writeShort(p.c.get().Leg());//leg
            m.writer().writeByte(-1);//mob
            m.writer().writeShort(p.c.get().x);// X
            m.writer().writeShort(p.c.get().y);// Y
            m.writer().writeShort(p.c.get().eff5buffHP());//eff5BuffHp
            m.writer().writeShort(p.c.get().eff5buffMP());//eff5BuffMP
            m.writer().writeByte(0);
            m.writer().writeBoolean(p.c.isHuman); // human
            m.writer().writeBoolean(p.c.isNhanban); // nhan ban
            m.writer().writeShort(p.c.get().partHead());
            m.writer().writeShort(p.c.get().Weapon());
            m.writer().writeShort(p.c.get().Body());
            m.writer().writeShort(p.c.get().Leg());
            m.writer().flush();
            p2.conn.sendMessage(m);
            m.cleanup();
            if (p.c.get().mobMe != null) {
                m = new Message(-30);
                m.writer().writeByte(-68);
                m.writer().writeInt(p.c.get().id);
                m.writer().writeByte(p.c.get().mobMe.templates.id);
                m.writer().writeByte(p.c.get().mobMe.isboss ? 1 : 0);
                m.writer().flush();
                p2.conn.sendMessage(m);
                m.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void FightMob2(Player p, Message m) throws IOException {
        int mobId = m.reader().readByte();
        m.cleanup();
        Mob mob = getMob(mobId);
        if (p.c.get().ItemBody[1] == null || mob == null || mob.isDie) {
            return;
        }
        Skill skill = p.c.get().getSkill(p.c.get().CSkill);
        if (skill == null) {
            return;
        }
        SkillTemplates data = SkillData.Templates(skill.id, skill.point);
        if (skill.coolDown > System.currentTimeMillis() || Math.abs(p.c.get().x - mob.x) > data.dx || Math.abs(p.c.get().y - mob.y) > data.dy || p.c.get().mp < data.manaUse) {
            return;
        }
        p.c.get().upMP(-data.manaUse);
        skill.coolDown = System.currentTimeMillis() + data.coolDown;
        Mob[] arMob = new Mob[10];
        arMob[0] = mob;
        byte n = 1;
        for (Mob mob2 : mobs) {
            if (mob2.isDie || mob.id == mob2.id || Math.abs(mob2.x - mob2.x) > data.dx || Math.abs(mob2.y - mob2.y) > data.dy) {
                continue;
            }
            if (data.maxFight > n) {
                arMob[n] = mob2;
                n++;
            } else {
                break;
            }
        }
        m = new Message(60);
        m.writer().writeInt(p.c.get().id);
        m.writer().writeByte(p.c.get().CSkill);
        for (byte i = 0; i < arMob.length; i++) {
            if (arMob[i] != null) {
                m.writer().writeByte(arMob[i].id);
            }
        }
        m.writer().flush();
        sendMyMessage(p, m);
        m.cleanup();
        long xpup = 0;
        for (byte i = 0; i < arMob.length; i++) {
            if (arMob[i] == null) {
                continue;
            }
            Mob mob3 = arMob[i];
            int dame = util.nextInt(p.c.get().dameMin(), p.c.get().dameMax());
            int oldhp = mob3.hp;
            if (dame <= 0) {
                dame = 1;
            }
            int fatal = p.c.get().Fatal();
            boolean isfatal = fatal > util.nextInt(1, 1000);
            if (isfatal) {
                dame *= 2;
            }
            xpup += mob3.xpup + dame;
            mob3.updateHP(-dame);
            attachedMob((oldhp - mob3.hp), mob3.id, isfatal);

        }
        p.updateExp(xpup);
    }

    public void selectUIZone(Player p, Message m) throws IOException {
        byte zoneid = m.reader().readByte();
        byte index = m.reader().readByte();
        m.cleanup();
        if (zoneid == id) {
            return;
        }
        Item item = null;
        try {
            item = p.c.ItemBag[index];
        } catch (Exception e) {
        }
        boolean isalpha = false;
        for (byte i = 0; i < map.template.npc.length; i++) {
            Npc npc = map.template.npc[i];
            if (npc.id == 13 && Math.abs(npc.x - p.c.get().x) < 50 && Math.abs(npc.y - p.c.get().y) < 50) {
                isalpha = true;
                break;
            }
        }
        if ((item != null && (item.id == 35 || item.id == 37)) || (isalpha)) {
            if (zoneid >= 0 && zoneid < map.area.length) {
                if (map.area[zoneid].numplayers < map.template.maxplayers) {
                    leave(p);
                    map.area[zoneid].Enter(p);
                    p.endLoad(true);
                    if (item != null && item.id != 37) {
                        p.c.removeItemBag(index);
                    }
                } else {
                    p.sendAddchatYellow("Khu vực này đã đầy.");
                    p.endLoad(true);
                }
            }
        }
        m = new Message(57);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void openUIZone(Player p) throws IOException {
        boolean isalpha = false;
        for (byte i = 0; i < map.template.npc.length; i++) {
            Npc npc = map.template.npc[i];
            if (npc.id == 13 && Math.abs(npc.x - p.c.get().x) < 50 && Math.abs(npc.y - p.c.get().y) < 50) {
                isalpha = true;
                break;
            }
        }
        if (p.c.quantityItemyTotal(37) > 0 || p.c.quantityItemyTotal(35) > 0 || isalpha) {
            Message m = new Message(36);
            m.writer().writeByte(map.area.length);//so khu
            for (byte j = 0; j < map.area.length; j++) {
                m.writer().writeByte(map.area[j].numplayers);//map.area[i].numplayers);//so nguoi
                m.writer().writeByte(map.area[j].getArryListParty().size());//grups
            }
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } else {
            p.c.get().upDie();
        }
    }

    public void chatNPC(Player p, Short idnpc, String chat) throws IOException {
        Message m = new Message(38);
        m.writer().writeShort(idnpc);//npcid
        m.writer().writeUTF(chat);//chat
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void selectMenuNpc(Player p, Message m) throws IOException {
        chatNPC(p, (short) m.reader().readByte(), m.reader().readByte() + "");
    }

    private ItemMap LeaveItem(short id, short x, short y) throws IOException {
        if (itemMap.size() > 100) {
            removeItemMapMessage(itemMap.remove(0).itemMapId);
        }
        Item item;
        ItemData data = ItemData.ItemDataId(id);
        if (data.type < 10) {
            if (data.type == 1) {
                item = ItemData.itemDefault(id);
                item.sys = GameScr.SysClass(data.nclass);
            } else {
                byte sys = (byte) util.nextInt(1, 3);
                item = ItemData.itemDefault(id, sys);
            }
        } else {
            item = ItemData.itemDefault(id);
        }
        ItemMap im = new ItemMap();
        im.itemMapId = getItemMapNotId();
        im.x = x;
        im.y = y;
        im.item = item;
        itemMap.add(im);
        Message m = new Message(6);
        m.writer().writeShort(im.itemMapId);
        m.writer().writeShort(item.id);
        m.writer().writeShort(im.x);
        m.writer().writeShort(im.y);
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
        return im;
    }

    public void PlayerAttack(Mob[] arrmob, Body b, int type) throws InterruptedException {
        for (int j = 0; j < this.players.size(); j++) {
            Service.PlayerAttack(this.players.get(j), arrmob, b);
        }
        Message m;
        long xpup = 0;
        for (byte i = 0; i < arrmob.length; i++) {
            Mob mob = arrmob[i];
            int dame = util.nextInt(b.dameMin(), b.dameMax());
            if (map.cave == null && mob.isboss && b.level - mob.level > 30) {
                dame = 0;
            }
            int fatal = b.Fatal();
            if (fatal > 1500) {
                fatal = 1500;
            }
            boolean flag = fatal > util.nextInt(1, 2000);
            if (flag) {
                dame *= 2;
                dame = dame * (100 + b.percentFantalDame()) / 100;
                dame += b.FantalDame();
            }
            if (dame <= 0) {
                dame = 1;
            }
            if (mob.isFire) {
                dame *= 2;
            }
            if (b.c.isNhanban) {
                dame = dame * b.c.clone.percendame / 100;
            }
            if (dame > 0) {
                mob.Fight(b.c.p.conn.id, dame);
                mob.updateHP(-dame);
            }

            int xpnew = dame / 20 * b.level; // EXP
            if (b.getEffType((byte) 18) != null) {
                xpnew *= b.getEffType((byte) 18).param;
            }
            if (mob.lvboss == 1) {
                xpnew *= 2;
            } else if (mob.lvboss == 2) {
                xpnew *= 3;
            } else if (mob.lvboss == 3) {
                xpnew /= 2;
            }
            if (map.LangCo()) {
                xpnew = xpnew * 120 / 100;
            } else if (map.VDMQ()) {
                xpnew = xpnew * 120 / 100;
            }
            if (b.level > 99) {
                xpnew /= 5;
            }
            if (map.cave != null || (b.level > 1 && Math.abs(b.level - b.level) <= 10)) {
                xpup += xpnew;
            }
            for (int j = 0; j < this.players.size(); j++) {
                Service.Mobstart(this.players.get(j), mob.id, mob.hp, dame, flag, mob.lvboss, mob.hpmax);
            }
            if (!mob.isDie) {
                if (b.percentFire2() >= util.nextInt(1, 100)) {
                    FireMobMessage(mob.id, 0);
                }
                if (b.percentFire4() >= util.nextInt(1, 100)) {
                    FireMobMessage(mob.id, 1);
                }
                if (b.percentIce1_5() >= util.nextInt(1, 100)) {
                    IceMobMessage(mob.id, 0);
                }
                if (b.percentWind1() >= util.nextInt(1, 100)) {
                    WindMobMessage(mob.id, 0);
                }
                if (b.percentWind2() >= util.nextInt(1, 100)) {
                    WindMobMessage(mob.id, 1);
                }
            }
        }
    }

    public void FightMob(Player p, Message m) throws IOException, InterruptedException, SQLException {
        if (p.c.get().CSkill == -1 && p.c.get().skill.size() > 0) {
            p.c.get().CSkill = p.c.get().skill.get(0).id;
        }
        Skill skill = p.c.get().getSkill(p.c.get().CSkill);
        if (skill == null) {
            return;
        }
        int mobId = m.reader().readUnsignedByte();
        synchronized (this) {
            Mob mob = getMob(mobId);
            Mob[] arMob = new Mob[10];
            arMob[0] = mob;
            if (mob == null || mob.isDie) {
                return;
            }
            if (p.c.get().ItemBody[1] == null) {
                p.sendAddchatYellow("Vũ khí không thích hợp");
                return;
            }
            p.removeEffect(15);
            p.removeEffect(16);
            SkillTemplates data = SkillData.Templates(skill.id, skill.point);
            if (p.c.get().mp < data.manaUse) {
                p.getMp();
                return;
            }
            if (skill.coolDown > System.currentTimeMillis() || Math.abs(p.c.get().x - mob.x) > 150 || Math.abs(p.c.get().y - mob.y) > 150) {
                return;
            }
            skill.coolDown = System.currentTimeMillis() + data.coolDown;
            p.c.mobAtk = mob.id;
            p.c.get().upMP(-data.manaUse);
            if (skill.id == 42) {
                p.c.get().x = mob.x;
                p.c.get().y = mob.y;
                this.sendXYPlayer(p);
            }
            int size = m.reader().available();
            byte n = 1;
            for (int i = 0; i < size; i++) {
                Mob mob2 = getMob(m.reader().readUnsignedByte());
                if (mob2.isDie || mob.id == mob2.id)// || Math.abs(mob.x - mob2.x) > data.dx || Math.abs(mob.y - mob2.y) > data.dy)
                {
                    continue;
                }
                if (data.maxFight > n) {
                    arMob[n] = mob2;
                    n++;
                } else {
                    break;
                }
            }
            m.cleanup();
            for (int j = 0; j < this.players.size(); j++) {
                Service.PlayerAttack(this.players.get(j), arMob, p.c.get());

                if (p.c.isHuman && !p.c.clone.isDie) {
                    Service.PlayerAttack(this.players.get(j), arMob, p.c.clone);
                    if (p.c.mobAtk != -1) {
                        for (byte k = 0; k < arMob.length; ++k) {
                            if (arMob[k] != null) {
//                              int dame = util.nextInt(p.c.get().dameMin(), p.c.get().dameMax()) * p.c.clone.percendame / 100;
                                int dame = util.nextInt(p.c.clone.dameMin(), p.c.clone.dameMax()) * p.c.clone.percendame / 100;
                                arMob[k].updateHP(-dame);
                                this.attachedMob(dame, arMob[k].id, false);
                            }
                        }
                    }
                }
            }
            long xpup = 0;
            for (byte i = 0; i < arMob.length; i++) {
                if (arMob[i] == null) {
                    continue;
                }
                Mob mob3 = arMob[i];
                int dame = util.nextInt(p.c.get().dameMin(), p.c.get().dameMax());
                if (map.cave == null && mob3.isboss && p.c.get().level - mob3.level > 30) {
                    dame = 0;
                }
                int mis = util.nextInt(15); // Quái né cao thấp thấp ít
                if (this.map.id == 82 && mis != 0) {
                    dame = 0;
                }
                int oldhp = mob3.hp;
                int fatal = p.c.get().Fatal();
                if (fatal > 1500) {
                    fatal = 1500;
                }
                boolean isfatal = fatal > util.nextInt(1, 2000);
                if (isfatal) {
                    dame *= 2;
                    dame = dame * (100 + p.c.get().percentFantalDame()) / 100;
                    dame += p.c.get().FantalDame();
                }
                if (dame <= 0) {
                    this.missMob(mob3.id);
                }
                if (mob3.isFire) {
                    dame *= 2;
                }
                if (p.c.isNhanban) {
                    dame = dame * p.c.clone.percendame / 100;
                }
                int xpnew = dame / 20 * p.c.get().level;
                if (p.c.get().getEffType((byte) 18) != null) {
                    xpnew *= p.c.get().getEffType((byte) 18).param;
                }
                if (mob3.lvboss == 1) {
                    xpnew *= 2;
                } else if (mob3.lvboss == 2) {
                    xpnew *= 3;
                } else if (mob3.lvboss == 3) {
                    xpnew /= 2;
                }
                if (map.LangCo()) {
                    xpnew = xpnew * 120 / 100;
                } else if (map.VDMQ()) {
                    xpnew = xpnew * 110 / 100;
                }
                if (p.c.get().level > 99) {
                    xpnew /= 5;
                }
                if (map.cave != null || (mob3.level > 1 && Math.abs(mob3.level - p.c.get().level) <= 10)) {
                    xpup += xpnew;
                }
                // Phải ăn thảo dược
                if (this.map.id == 90 && p.c.getEffId(23) == null) {
                    dame = 1;
                }
                mob3.updateHP(-dame);
               // Cửa phản đòn
                if (this.map.id == 83) {
                    p.c.get().upHP(-dame);
                    MobAtkMessage(mob.id, p.c, dame, 1, (short) -1, (byte) -1, (byte) -1);
                }
                if (dame > 0) {
                    mob3.Fight(p.conn.id, dame);
                }
                if (!mob3.isFire) {
                    if (p.c.get().percentFire2() >= util.nextInt(1, 100)) {
                        FireMobMessage(mob3.id, 0);
                    }
                    if (p.c.get().percentFire4() >= util.nextInt(1, 100)) {
                        FireMobMessage(mob3.id, 1);
                    }
                }
                if (!mob3.isIce) {
                    if (p.c.get().percentIce1_5() >= util.nextInt(1, 100)) {
                        IceMobMessage(mob3.id, 0);
                    }
                }
                if (!mob3.isWind) {
                    if (p.c.get().percentWind1() >= util.nextInt(1, 100)) {
                        WindMobMessage(mob3.id, 0);
                    }
                    if (p.c.get().percentWind2() >= util.nextInt(1, 100)) {
                        WindMobMessage(mob3.id, 1);
                    }
                }
                if (mob3.isDie && (mob3.templates.id == 98 || mob3.templates.id == 99) && map.id >= 98 && map.id <= 104){
                        server.manager.chatKTG("Long trụ bị " + p.c.name + " đánh sập, mang về lợi thế cho đồng minh");
                        p.c.pointCT = p.c.pointCT + 250;
                        map.war.updatePoint(p.c,250);
                        if (p.c.typeCT == 4){
                            map.war.pointWhite += 250;
                        }else {
                            map.war.pointBlack += 250;
                        }
                    }
                if (mob3.isDie) {
                    MobStartDie((oldhp - mob3.hp), mob3.id, isfatal);
                } else {
                    attachedMob((oldhp - mob3.hp), mob3.id, isfatal);
                }
                if (mob3.isDie && mob3.level > 1) {
                    this.numMobDie++;
                    if (map.cave != null) {
                        map.cave.updatePoint(1);
                    }
                    if (this.map.war != null && map.id >= 98 && map.id <= 104) {
                            if(mob3.isboss) {
                                map.war.updatePoint(p.c,300);
                                if (p.c.typeCT == 4){
                                    map.war.pointWhite += 300;
                                }else {
                                    map.war.pointBlack += 300;
                                }
                            } else if(mob3.lvboss == 2) {
                                map.war.updatePoint(p.c,50);
                                if (p.c.typeCT == 4){
                                    map.war.pointWhite += 50;
                                }else {
                                    map.war.pointBlack += 50;
                                }
                            }else if(mob3.lvboss == 1) {
                                map.war.updatePoint(p.c,10);
                                if (p.c.typeCT == 4){
                                    map.war.pointWhite += 10;
                                }else {
                                    map.war.pointBlack += 10;
                                }
                            } else {
                                map.war.updatePoint(p.c,1);
                                if (p.c.typeCT == 4){
                                    map.war.pointWhite += 1;
                                }else {
                                    map.war.pointBlack += 1;
                                }
                            }
                        }
                    int master = mob3.sortNinjaFight();
                    if (mob3.lvboss == 1) {
                        numTA--;
                        p.c.upyenMessage(100000);
                    } else if (mob3.lvboss == 2) {
                        numTL--;
                        p.c.upyenMessage(500000);
                    }
                    ItemMap im;
                    short[] arid;
                    short[] aridsk = null;
                    if (map.LangCo()) { // Tỉ lệ ra lượng yên xu ở quái
                        //p.upluongMessage(util.nextInt(1, 3));
                        p.c.upyenMessage(1000L);
                        p.c.upxuMessage(100);
                        arid = new short[]{648, 649, 450, 651, 545, 12, -1, -1, 778, -1, -1, -1, 455, -1, -1, -1, -1, 444, -1, -1, 695, -1, 778, -1, 450, -1, 695, -1, -1, -1, 695, -1, 778, -1, -1, 450, -1, -1, 451, -1, -1, 778, -1, -1, -1, -1, 451, -1, -1, -1, 778, -1, -1, -1, 452, -1, -1, -1, 778, -1, -1, -1, 452, -1, -1, 545, -1, -1, 695, -1, -1, -1, 453, -1, 453, -1, -1, 573, -1, 778, -1, 574, -1, -1, 575 - 1, -1, -1, 552, -1, 778, -1, 553, -1, -1, 554, -1, -1, 555, -1, -1, 556, -1, -1, 557, -1, 695, -1, 618, -1, 778, -1, -1, 619, -1, -1, 620, -1, -1, -1, 621, -1, -1, -1, -1, 622, -1, -1, 623, -1, -1, -1, -1, -1, 624, -1, -1, 625, -1, -1, 778, -1, -1, -1, -1, 626, -1, 778, -1, 627, -1, -1, 628, -1, -1, 629, -1, 778, -1, 630, -1, -1, 631, -1, -1, 778};
                    } else if (this.map.VDMQ()) {
                        //p.upluongMessage(1L);
                        p.c.upyenMessage(1000L);
                        p.c.upxuMessage(100);
                        arid = new short[]{648, 649, 450, 651, 545, 12, -1, 778, -1, -1, -1, 695, -1, 455, -1, 778, -1, -1, -1, 444, -1, -1, 695, -1, 778, -1, -1, -1, -1, 449, -1, -1, 778, -1, -1, -1, -1, -1, 449, -1, -1, 449, -1, -1, -1, -1, -1, 450, -1, -1, -1, -1, -1, 450, -1, -1, 451, -1, -1, -1, -1, 451, -1, -1, -1, -1, -1, 452, -1, -1, -1, -1, -1, -1, -1, -1, -1, 452, -1, -1, -1, -1, -1, -1, -1, -1, 453, 778, -1, -1, 695, -1, -1, 453, -1, -1, 573, -1, -1, -1, 778, -1, -1, -1, 574, -1, -1, 575 - 1, -1, -1, 695, 778,};
                    } else {
                        p.c.upyenMessage(500L);
                        p.c.upxuMessage(50);
                        int x = util.nextInt(0, 2);
                        if (x == 2) {
                            p.c.upyenMessage(1L);
                            //p.c.addItemBag(true, ItemData.itemDefault(251)); // Mảnh giấy vụn
                        }
                        arid = new short[]{12, -1, -1, -1, -1, -1, -1, 9, -1, -1, -1, -1, -1, -1, -1, -1, 695, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,};
                    }
                    int per = 100;
                    switch (server.manager.event) {
                        case 1:
                            per = 5;
                            aridsk = new short[]{428, 429, 430, 431};
                            break;
                        case 2:
                            per = 3;
                            aridsk = new short[]{292, 293, 294, 295, 296, 297};
                            break;
                    }
                    if (aridsk != null && util.nextInt(per) == 0) {
                        arid = aridsk;
                    }
                    if (map.VDMQ() && p.c.get().level >= 100 && util.nextInt(100) <= 15) {
                        if (mob3.lvboss == 1 || mob3.lvboss == 2) {
                            arid = new short[]{545};
                        }
                    }
                    int lent = util.nextInt(arid.length);
                    if ((map.LangCo() || Math.abs(mob3.level - p.c.get().level) <= 10) && arid[lent] != -1) {
                        im = LeaveItem(arid[lent], mob3.x, mob3.y);
                        int quantity = 1;
                        if (im.item.id == 12) {
                            quantity = util.nextInt(10000, 30000);
                        }
                        if (im.item.id == 455 || im.item.id == 456) {
                            im.item.isExpires = true;
                            im.item.expires = util.TimeDay(7);
                        } else if (im.item.id == 545) {
                            im.item.isExpires = true;
                            im.item.expires = util.TimeDay(1);
                        }
                        im.item.quantity = quantity;
                        im.master = master;
                    }
                    if (mob3.isboss) { // Boss bị tiêu diệt & vật phẩm rơi ra
                        if (map.cave == null) {
                            Manager.chatKTG(mob3.templates.name  + " Gầm lên giận dữ, Aaaa ... đợi ta hồi sinh, ta sẽ trả thù ngươi, " + p.c.name);
                        }
                        
                        if (map.VDMQ()) {
                            im = LeaveItem((short) 547, (short) util.nextInt(mob3.x - 30, mob3.x + 30), mob3.y);
                            im.master = master;
                        }
                        
                        int l = mob3.templates.arrIdItem.length;
                        if (l > 1) {
                            for (int j = 0; j < mob3.templates.arrIdItem[0]; j++) {
                                lent = util.nextInt(1, l - 1);
                                short idi = mob3.templates.arrIdItem[lent];
                                if (idi == -1) {
                                    continue;
                                }
                                im = LeaveItem(idi, (short) util.nextInt(mob3.x - 30, mob3.x + 30), mob3.y);
                                if (im.item.id == 12) {
                                    im.item.quantity = util.nextInt(10000, 30000);
                                }
                                im.master = master;
                            }
                        }
                        if (map.cave != null && map.getXHD() == 9) {
                            if ((map.id == 157 && map.cave.level == 0) || (map.id == 158 && map.cave.level == 1) || (map.id == 159 && map.cave.level == 2)) {
                                if (util.nextInt(3) < 3) {
                                    map.cave.updatePoint(mobs.size());
                                    for (short k = 0; k < mobs.size(); k++) {
                                        mobs.get(k).updateHP(-mobs.get(k).hpmax);
                                        mobs.get(k).isRefresh = false;
                                        for (short h = 0; h < players.size(); h++) {
                                            Service.setHPMob(players.get(h).c, mobs.get(k).id, 0);
                                        }
                                    }
                                    map.cave.level++;
                                }
                            }
                        }
                    }
                    if (map.cave != null) {
                        if (this.map.getXHD() < 9) {
                            mob3.isRefresh = false;
                            if (mobs.size() == this.numMobDie) {
                                if (map.getXHD() == 5) {
                                    if (map.id == 105) {
                                        map.cave.openMap();
                                        map.cave.openMap();
                                        map.cave.openMap();
                                    } else if (map.id == 106 || map.id == 107 || map.id == 108) {
                                        map.cave.finsh++;
                                        if (map.cave.finsh >= 3) {
                                            map.cave.openMap();
                                        }
                                    } else {
                                        map.cave.openMap();
                                    }
                                } else if (map.getXHD() == 6 && map.id == 116) {
                                    if (map.cave.finsh == 0) {
                                        map.cave.openMap();
                                    } else {
                                        map.cave.finsh++;
                                    }
                                    this.numMobDie = 0;
                                    for (short l = 0; l < mobs.size(); l++) {
                                        this.refreshMob(l);
                                    }
                                } else {
                                    map.cave.openMap();
                                }
                            }
                        }  
                    }
                    
                    if (map.LDGT() && this.map.getXHD() == 10) {
                        final LDGT ldgt = this.map.ldgt;
                        if (mob3.templates.id != 81) {
                            mob3.isRefresh = false;
                        }
                        if (mob3.templates.id == 209) {
                            arid = new short[]{260};
                            lent = util.nextInt(arid.length);
                            LeaveItem(arid[lent], mob3.x, mob3.y);
                        }
                        if (this.mobs.size() == this.numMobDie + 4) {
                            this.map.refreshBoss(0);
                        }
                        if (this.mobs.size() == this.numMobDie && this.map.id == 90) {
                            System.err.println("Hoàn thành");
                            map.ldgt.finsh();
                        }
                    }

                } else {
                }
            }
            if (xpup > 0) {
                if (map.cave != null) {
                    map.cave.updateXP(xpup * 150 / 100);
                } else {
                    if (p.c.isNhanban) {
                        xpup /= 4;
                    }
                    p.updateExp(xpup);
                    xpup /= 5;
                    if (p.c.get().party != null) {
                        for (int i = 0; i < players.size(); i++) {
                            Player p2 = players.get(i);
                            if (p2.c.id != p.c.id) {
                                if (p2.c.party == p.c.party && Math.abs(p2.c.level - p.c.level) <= 10) {
                                    p2.updateExp(xpup / 55 * 100);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void FightNinja(Player p, Message m) throws IOException, InterruptedException {
        if (p.c.typeSolo != 1 && (map.id == 22 || map.id == 17 || map.id == 10 || map.id == 27 || map.id == 32 || map.id == 38 || map.id == 43 || map.id == 48 || map.id == 72 || map.id == 138 || map.id == 1)) {
            return;
        }
        int idP = m.reader().readInt();

        m.cleanup();
        Char c = getNinja(idP);

        if (p.c.ItemBody[1] != null && c != null && ((p.c.typepk == 1 && c.typepk == 1) || p.c.typepk == 3 || c.typepk == 3 || (p.c.typeSolo == 1 && c.typeSolo == 1) || (p.c.get().typepk == 4 || c.get().typepk == 4) || (p.c.get().typepk == 5 || c.get().typepk == 5))) {
            if (p.c.CSkill == -1 && p.c.skill.size() > 0) {
                p.c.CSkill = p.c.skill.get(0).id;
            }
            Skill skill = p.c.getSkill(p.c.CSkill);
            if (skill == null || c.isDie) {
                return;
            }
            SkillTemplates data = SkillData.Templates(skill.id, skill.point);
            if (skill.coolDown > System.currentTimeMillis() || Math.abs(p.c.x - c.x) > data.dx || Math.abs(p.c.y - c.y) > data.dy || p.c.mp < data.manaUse) {
                return;
            }
            p.c.upMP(-data.manaUse);

            skill.coolDown = System.currentTimeMillis() + data.coolDown;
            ArrayList<Char> spread = new ArrayList<>();
            spread.add(c);

            for (Player pl : players) {
                if (pl.id != p.id) {
                    if (pl.c.isDie || c.id == p.c.id || pl.c.id == p.c.id || Math.abs(c.x - pl.c.x) > data.dx || Math.abs(c.y - pl.c.y) > data.dy) {
                        continue;
                    }
                    if (data.maxFight > spread.size()) {
                        if (pl.c.typepk == 3 || p.c.typepk == 3 || (p.c.typepk == 1 && pl.c.typepk == 1 || (p.c.typeSolo == 1 && c.typeSolo == 1)) || (p.c.get().typepk == 4 || c.get().typepk == 4) || (p.c.get().typepk == 5 || c.get().typepk == 5)) {
                            if (pl.id != p.id) {
                                spread.add(pl.c);
                            }
                        }
                    } else {
                        break;
                    }
                }
            }

            m = new Message(61);
            m.writer().writeInt(p.c.id);
            m.writer().writeByte(skill.id);
            for (byte i = 0; i < spread.size(); i++) {
                m.writer().writeInt(spread.get(i).id);
            }
            m.writer().flush();
            sendMyMessage(p, m);
            m.cleanup();

            for (Char c2 : spread) {
                if (c2.id != p.c.id) {
                    int dame = util.nextInt(p.c.dameMin(), p.c.dameMax()) / 10;
                    int oldhp = c2.hp;
                    dame -= c2.dameDown(); // Giảm sát thương
                    if (dame <= 0) {
                        dame = 1;
                    }
                    int miss = c2.Miss() - p.c.Exactly();
                    if (util.nextInt(10000) < miss || p.c.get().getEffType((byte)10) != null) {
                        dame = 0;
                    }
                    c2.upHP(-dame);

                    attached((oldhp - c2.hp), c2.id);

                    if (c2.isDie) {
                        
                        // Lôi đài
                        if (map.id == 111){
                            sendDie(c2);
                            server.manager.chatKTG("" + p.c.name + " vừa giết " + c.name + " trong lôi đài, chiến lực thật đáng sợ");
                            Thread.sleep(5000);
                            Map ma = Manager.getMapid(27);
                            for (Place area : ma.area) {
                                if (area.numplayers < ma.template.maxplayers) {
                                    area.EnterMap0(c2);
                                    c.changePk(c2,(byte) 0);
                                    c2.place.DieSolo(c2.c.p);
                                    c2.c.p.sendAddchatYellow("Bạn đã thua " + p.c.name);
                                    p.sendAddchatYellow("Bạn đã thắng " + c2.name);
                                    if (c2.xu >= c2.xuLoiDai){
                                        p.c.upxuMessage(p.c.xuLoiDai);
                                    }
                                    c2.upxuMessage(-(c2.xuLoiDai));
                                    area.EnterMap0(p.c);
                                    leave(p);
                                    leave(c.p);
                                    c.changePk(p.c,(byte) 0);
                                    return;
                                }
                            }
                        }
                        
                        if ((c2.typeCT == 4 || c2.typeCT == 5) && (map.id >= 98 && map.id <= 104)){
                                p.sendAddchatYellow("Bạn đánh trọng thương " + c2.name);
                                c2.p.sendAddchatYellow("Bạn bị " + p.c.name + " đánh trọng thương");
                                map.war.updatePoint(p.c,10);
                                if (p.c.typeCT == 4) {
                                    map.war.pointWhite = map.war.pointWhite + 10;
                                    System.out.println(map.war.pointWhite);
                                } else if (p.c.typeCT == 5){
                                    map.war.pointBlack = map.war.pointBlack + 10;
                                    System.out.println(map.war.pointBlack);
                                }
                            }
                        c2.p.closeTrade();
                        c2.type = 14;
                        waitDie(c2.p);
                        myDie(c2.p);
                    }
                }

            }
        }
    }
    
    public void DieSolo(Player p) throws IOException {
        leave(p);
        p.c.get().isDie = false;
        Map ma = Manager.getMapid(27);
        for (Place area : ma.area) {
            if (area.numplayers < ma.template.maxplayers) {
                area.EnterMap0(p.c);
                p.c.get().hp = p.c.get().getMaxHP();
                p.c.get().mp = p.c.get().getMaxMP();
                Message m = new Message(-30);
                m.writer().writeByte(-123);
                m.writer().writeInt(p.c.xu);
                m.writer().writeInt(p.c.yen);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.c.get().getMaxHP());
                m.writer().writeInt(p.c.get().getMaxMP());
                m.writer().writeByte(0);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                m = new Message(57);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                return;
            }
        }
    }

    private void myDie(Player p) throws IOException {
        if (p.c.exp > Level.getMaxExp(p.c.level - 1)) {
            Message m = new Message(-11);
            m.writer().writeByte(p.c.typepk);
            m.writer().writeShort(p.c.x);
            m.writer().writeShort(p.c.y);
            m.writer().writeLong(p.c.exp);
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } else {
            p.c.exp = Level.getMaxExp(p.c.level - 1);
            Message m = new Message(72);
            m.writer().writeByte(p.c.typepk);
            m.writer().writeShort(p.c.x);
            m.writer().writeShort(p.c.y);
            m.writer().writeLong(p.c.expdown);
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        }
    }

    private void waitDie(Player p) throws IOException {
        Message m = new Message(0);
        m.writer().writeInt(p.c.id);
        m.writer().writeByte(p.c.typepk);
        m.writer().writeShort(p.c.x);
        m.writer().writeShort(p.c.y);
        m.writer().flush();
        sendMyMessage(p, m);
        m.cleanup();
    }

    public void wakeUpDieReturn(Player p) throws IOException {
        if (!p.c.isDie || map.LangCo()) {
            return;
        }
        if (p.luong < 1) {
            p.conn.sendMessageLog("Bạn không có đủ 1 lượng!");
            return;
        }
        p.c.get().isDie = false;
        p.luongMessage(-1);
        p.c.get().hp = p.c.get().getMaxHP();
        p.c.get().mp = p.c.get().getMaxMP();
        p.liveFromDead();
    }

    public void sendDie(Char c) throws IOException {
        if (c.get().exp > Level.getMaxExp(c.get().level)) {
            Message m = new Message(-11);
            m.writer().writeByte(c.get().pk);
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
            m.writer().writeLong(c.get().exp);
            m.writer().flush();
            c.p.conn.sendMessage(m);
            m.cleanup();
        } else {
            c.get().exp = Level.getMaxExp(c.get().level);
            Message m = new Message(72);
            m.writer().writeByte(c.get().pk);
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
            m.writer().writeLong(c.get().expdown);
            m.writer().flush();
            c.p.conn.sendMessage(m);
            m.cleanup();
        }
        Message m = new Message(0);
        m.writer().writeInt(c.get().id);
        m.writer().writeByte(c.get().pk);
        m.writer().writeShort(c.get().x);
        m.writer().writeShort(c.get().y);
        m.writer().flush();
        sendMyMessage(c.p, m);
        m.cleanup();
    }

    public void DieReturn(Player p) throws IOException {
        leave(p);
        p.c.get().isDie = false;
        Map ma;
        if (map.cave != null) {
            ma = map.cave.map[0];
        } else {
            ma = Manager.getMapid(p.c.mapLTD);
        }
        if (map.id >= 98 && map.id <= 104) {
            if (p.c.typepk == 4) {
                ma = Manager.getMapid(98);
            }
            if (p.c.typepk == 5) {
                ma = Manager.getMapid(104);
            }
        }
        if (map.LDGT()) {
            ma = Manager.getMapid(80);
        }
        for (Place area : ma.area) {
            if (area.numplayers < ma.template.maxplayers) {
                area.EnterMap0(p.c);
                p.c.get().hp = p.c.get().getMaxHP();
                p.c.get().mp = p.c.get().getMaxMP();
                Message m = new Message(-30);
                m.writer().writeByte(-123);
                m.writer().writeInt(p.c.xu);
                m.writer().writeInt(p.c.yen);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.c.get().getMaxHP());
                m.writer().writeInt(p.c.get().getMaxMP());
                m.writer().writeByte(0);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                m = new Message(57);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                return;
            }
        }
    }

    private void attached(int dame, int nid) throws IOException {
        Char n = getNinja(nid);
        Message m = new Message(62);
        m.writer().writeInt(nid);
        m.writer().writeInt(n.hp); // HP
        m.writer().writeInt(dame); // DAME
        m.writer().writeInt(n.mp); // MP
        m.writer().writeInt(0);    // DAME2
        m.writer().flush();
        sendMessage(m);
        m.cleanup();
    }

    private void FireMobMessage(int mobid, int type) {
        try {
            Mob mob = getMob(mobid);
            switch (type) {
                case -1:
                    mob.isFire = false;
                    break;
                case 0:
                    mob.isFire = true;
                    mob.timeFire = System.currentTimeMillis() + 1000L;
                    break;
                case 1:
                    mob.isFire = true;
                    mob.timeFire = System.currentTimeMillis() + 1500L;
                    break;
                default:
                    break;
            }
            Message m = new Message(89);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isFire);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void IceMobMessage(int mobid, int type) {
        try {
            Mob mob = getMob(mobid);
            switch (type) {
                case -1:
                    mob.isIce = false;
                    break;
                case 0:
                    mob.isIce = true;
                    mob.timeIce = System.currentTimeMillis() + 1500L;
                    break;
                case 1:
                    mob.isIce = true;
                    mob.timeIce = System.currentTimeMillis() + 3000L;
                    break;
                default:
                    break;
            }
            Message m = new Message(90);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isIce);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WindMobMessage(int mobid, int type) {
        try {
            Mob mob = getMob(mobid);
            switch (type) {
                case -1:
                    mob.isWind = false;
                    break;
                case 0:
                    mob.isWind = true;
                    mob.timeWind = System.currentTimeMillis() + 1000L;
                    break;
                case 1:
                    mob.isWind = true;
                    mob.timeWind = System.currentTimeMillis() + 1500L;
                    break;
                default:
                    break;
            }
            Message m = new Message(91);
            m.writer().writeByte(mobid);
            m.writer().writeBoolean(mob.isWind);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMobAttached(int mobid) {
        synchronized (this) {
            try {
                Mob mob = getMob(mobid);
                if (mob.isIce || mob.isWind) {
                    return;
                }
                long tFight = System.currentTimeMillis() + 1500L;
                if (mob.isboss) {
                    tFight = System.currentTimeMillis() + 500L;
                }
                if (this.map.id == 81) {
                    tFight = System.currentTimeMillis() + 100L;
                }
                mob.timeFight = tFight;
                for (short i = 0; i < players.size(); i++) {
                    Player player = players.get(i);
                    if (player.c.get().isDie || player.c.get().getEffId(15) != null || player.c.get().getEffId(16) != null) {
                        continue;
                    }
                    short dx = 80;
                    short dy = 2;
                    if (mob.templates.type > 3) {
                        dy = 80;
                    }
                    if (mob.isboss) {
                        dx = 110;
                    }
                    if (mob.isFight(player.conn.id)) {
                        dx = 200;
                        dy = 160;
                    }
                    if (Math.abs(player.c.get().x - mob.x) < dx && Math.abs(player.c.get().y - mob.y) < dy) {
                        int dame = mob.level * mob.level / 4;
                        if (map.cave != null && map.cave.finsh > 0 && map.getXHD() == 6) {
                            int dup = dame * ((10 * map.cave.finsh) + 100) / 100;
                            dame = dup;
                        }
                        if (mob.lvboss == 1) {
                            dame *= 2;
                        } else if (mob.lvboss == 2) {
                            dame *= 3;
                        } else if (mob.lvboss == 3) {
                            dame *= 4;
                        }
                        if (mob.isboss) {
                            dame *= 10;
                        }
                        if (mob.sys == 1) {
                            dame -= player.c.get().ResFire();
                        } else if (mob.sys == 2) {
                            dame -= player.c.get().ResIce();
                        } else if (mob.sys == 3) {
                            dame -= player.c.get().ResWind();
                        }
                        dame -= player.c.get().dameDown();
                        dame = util.nextInt((dame * 90 / 100), dame);
                        if (dame <= 0) {
                            dame = 1;
                        }
                        int miss = player.c.get().Miss();
                        if (miss > util.nextInt(20000)) {
                            dame = 0;
                        }
                        int mpdown = 0;
                        if (player.c.get().hp * 100 / player.c.get().getMaxHP() > 10) {
                            Effect eff = player.c.get().getEffId(10);
                            if (eff != null) {
                                int mpold = player.c.get().mp;
                                player.c.get().upMP(-(dame * eff.param / 100));
                                dame -= mpdown = (mpold - player.c.get().mp);
                            }
                        }
                        player.c.get().upHP(-dame);
                        
int random = util.nextInt(2);
                        if ((player.c.mapid == 86 || player.c.mapid == 90) && random == 1 && player.c.get().getEffId(6) == null) {
                            player.setEffect(6, 0, 2000, 0);
                            Service.PlayerAddEfect(player, player.c, player.c.getEffId(6));
                        }
                        if ((player.c.mapid == 85 || player.c.mapid == 90) && random == 1 && player.c.get().getEffId(7) == null) {
                            player.setEffect(7, 0, 2000, 0);
                            Service.PlayerAddEfect(player, player.c, player.c.getEffId(7));
                        }
                        if ((player.c.mapid == 84 || player.c.mapid == 90) && random == 1 && player.c.get().getEffId(5) == null) {
                            player.setEffect(5, 0, 2000, 0);
                            Service.PlayerAddEfect(player, player.c, player.c.getEffId(5));
                            dame *= 2;
                        }
                        MobAtkMessage(mob.id, player.c, dame, mpdown, (short) -1, (byte) -1, (byte) -1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void MobAtkMessage(int mobid, Char n, int dame, int mpdown, short idskill_atk, byte typeatk, byte typetool) throws IOException {
        Message m = new Message(-3);
        m.writer().writeByte(mobid);
        m.writer().writeInt(dame);//-Hp;
        m.writer().writeInt(mpdown);//-mp
        m.writer().writeShort(idskill_atk);//idSkill_atk
        m.writer().writeByte(typeatk);//type atk
        m.writer().writeByte(typetool);//type tool
        m.writer().flush();
        n.p.conn.sendMessage(m);
        m.cleanup();
        m = new Message(-2);
        m.writer().writeByte(mobid);
        m.writer().writeInt(n.id);//id ninja
        m.writer().writeInt(dame);//-Hp;
        m.writer().writeInt(mpdown);//-mp
        m.writer().writeShort(idskill_atk);//idSkill_atk
        m.writer().writeByte(typeatk);//type atk
        m.writer().writeByte(typetool);//type tool
        m.writer().flush();
        sendMyMessage(n.p, m);
        if (n.isDie && !map.LangCo()) {
            sendDie(n);
        }
    }

    private void loadMobMeAtk(Char n) {
        n.mobMe.timeFight = System.currentTimeMillis() + 1000L;
        try {
            if (n.mobAtk != -1 && n.mobMe.templates.id >= 211 && n.mobMe.templates.id <= 217) {
                Mob mob = getMob(n.mobAtk);
                if (!mob.isDie) {
                    int dame = n.dameMax() * 20 / 100;
                    MobMeAtkMessage(n, mob.id, dame, (short) 40, (byte) 1, (byte) 1, (byte) 0);
                    mob.updateHP(-dame);
                    attachedMob(dame, mob.id, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void MobMeAtkMessage(Char n, int idatk, int dame, short idskill_atk, byte typeatk, byte typetool, byte type) throws IOException {
        Message m = new Message(87);
        m.writer().writeInt(n.id);
        m.writer().writeByte(idatk);
        m.writer().writeShort(idskill_atk);//idSkill_atk
        m.writer().writeByte(typeatk);//type atk
        m.writer().writeByte(typetool);//type tool
        m.writer().writeByte(type);//type
        if (type == 1) {
            m.writer().writeInt(idatk);//char atk
        }
        m.writer().flush();
        n.p.conn.sendMessage(m);
        m.cleanup();
    }

    public void openFindParty(Player p) {
        try {
            ArrayList<Party> partys = this.getArryListParty();
            Message m = new Message(-30);
            m.writer().writeByte(-77);
            for (int i = 0; i < partys.size(); i++) {
                Char n = partys.get(i).getNinja(partys.get(i).master);
                m.writer().writeByte(n.nclass);
                m.writer().writeByte(n.level);
                m.writer().writeUTF(n.name);
                m.writer().writeByte(partys.get(i).ninjas.size());
            }
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        synchronized (this) {
            try {
                final Calendar rightNow = Calendar.getInstance();
                final int hour = rightNow.get(11);
                final int min = rightNow.get(12);
                final int sec = rightNow.get(13);
                
                // Hiệu ứng quái vật
                for (int i = mobs.size() - 1; i >= 0; i--) {
                    Mob mob = mobs.get(i);
                    if (mob.timeRefresh > 0 && System.currentTimeMillis() >= mob.timeRefresh && mob.isRefresh) {
                        refreshMob(mob.id);
                    }
                    if (mob.isFire && System.currentTimeMillis() >= mob.timeFire) {
                        FireMobMessage(mob.id, -1);
                    }
                    if (mob.isIce && System.currentTimeMillis() >= mob.timeIce) {
                        IceMobMessage(mob.id, -1);
                    }
                    if (mob.isWind && System.currentTimeMillis() >= mob.timeWind) {
                        WindMobMessage(mob.id, -1);
                    }
                    if (!mob.isDie && mob.status != 0 && mob.level != 1 && System.currentTimeMillis() >= mob.timeFight) {
                        loadMobAttached(mob.id);
                    }
                }
                for (int i = players.size() - 1; i >= 0; i--) {
                    // Lôi đài
                    if (map.id == 111 && players.get(i).c.get().y < 280 && players.get(i).c.get().typepk == 0) {
                        EnterMap3(players.get(i).c);
                        players.get(i).c.place.leave(players.get(i));
                    } 
                    Player p = players.get(i);
                    // Hiệu ứng
                    for (int j = p.c.get().veff.size() - 1; j >= 0; j--) {
                        Effect eff = p.c.get().veff.get(j);
                        if (System.currentTimeMillis() >= eff.timeRemove) {
                            p.removeEffect(eff.template.id);
                            j--;
                        } else if (eff.template.type == 0 || eff.template.type == 12) {
                            p.c.get().upHP(eff.param);
                            p.c.get().upMP(eff.param);
                        } else if (eff.template.type == 4 || eff.template.type == 17) {
                            p.c.get().upHP(eff.param);
                        } else if (eff.template.type == 13) {
                            p.c.get().upHP(-(p.c.get().getMaxHP() * 3 / 100));
                            if (p.c.get().isDie) {
                                p.c.get().upDie();
                            }
                        }
                    }
                    // Hiệu ứng
                    if (p.c.eff5buffHP() > 0 || p.c.get().eff5buffMP() > 0) {
                        if (p.c.eff5buff <= System.currentTimeMillis()) {
                            p.c.eff5buff = System.currentTimeMillis() + 5000L;
                            p.c.get().upHP(p.c.get().eff5buffHP());
                            p.c.get().upMP(p.c.get().eff5buffMP());
                        }
                    }
                    
                    // Hiệu ứng hào quang
                    if (p.c.get().fullTL() >= 7 && System.currentTimeMillis() > p.c.delayEffect) {
                        p.c.delayEffect = System.currentTimeMillis() + 2500;
                        byte tl = 0;
                        switch (GameScr.SysClass(p.c.nclass)) {
                            case 1:
                                tl = 9;
                                break;
                            case 2:
                                tl = 3;
                                break;
                            case 3:
                                tl = 6;
                                break;
                        }
                        if (p.c.fullTL() >= 9) {
                            tl += 1;
                        }
                        if (p.c.fullTL() >= 7) {
                            tl += 0;
                        }
                        for (int j = 0; j < players.size(); j++) {
                            GameCanvas.addEffect(players.get(j).conn, (byte) 0, p.c.get().id, tl, 1, 1, false);
                        }
                    }
                    
                    /**
                    *
                    * @author Dũng Trần
                    */
                    
                    //Hiệu ứng khi bạn là Admin
                    //if (p.c.name.equals("admin") && System.currentTimeMillis() > p.c.delayEffect) {
                    //    p.c.delayEffect = System.currentTimeMillis() + 1000;
                    //for (int j = 0; j < players.size(); j++) {
                    //    GameCanvas.addEffect(players.get(j).conn, (byte) 0, p.c.get().id, (byte)30, 0, 0, false);
                    //}
                    //}
                    // Hiệu ứng Bạch Hạc Kiếm
                    //for (byte j = 0; j < p.c.get().ItemBody.length; j++) {
                    //        Item item = p.c.get().ItemBody[j];
                    //        if (item != null && item.id == 94) {
                    //        for (int k = 0; k < players.size(); k++) {
                    //        GameCanvas.addEffect(players.get(k).conn, (byte) 0, p.c.id, (byte) 44, 0, 0, false);
                    //            }
                    //        }
                    //}
                    
                    // Hiệu ứng khi cấp độ 100
                    //if (p.c.get().level >= 100 && System.currentTimeMillis() > p.c.delayEffect) {
                    //    p.c.delayEffect = System.currentTimeMillis() + 2500;
                    //for (int j = 0; j < players.size(); j++) {
                    //    GameCanvas.addEffect(players.get(j).conn, (byte) 0, p.c.get().id, (byte)25, 0, 0, false);
                    //}
                    //}

                    // Hiệu ứng pet tấn công
                    if (p.c.get().mobMe != null && p.c.get().mobMe.timeFight <= System.currentTimeMillis()) {
                        loadMobMeAtk(p.c);
                    }
                    // Xóa item túi hết hạn
                    for (byte j = 0; j < p.c.ItemBag.length; j++) {
                        Item item = p.c.ItemBag[j];
                        if (item == null || !item.isExpires) {
                            continue;
                        }
                        if (System.currentTimeMillis() >= item.expires) {
                            p.c.removeItemBag(j, item.quantity);
                        }
                    }
                    // Xóa item trang bị hết hạn
                    for (byte j = 0; j < p.c.get().ItemBody.length; j++) {
                        Item item = p.c.get().ItemBody[j];
                        if (item == null || !item.isExpires) {
                            continue;
                        }
                        if (System.currentTimeMillis() >= item.expires) {
                            p.c.removeItemBody(j);
                        }
                    }
                    // Xóa item rương hết hạn
                    for (byte j = 0; j < p.c.ItemBox.length; j++) {
                        Item item = p.c.ItemBox[j];
                        if (item == null || !item.isExpires) {
                            continue;
                        }
                        if (System.currentTimeMillis() >= item.expires) {
                            p.c.removeItemBox(j);
                        }
                    }
                    if (map.LangCo() && (p.c.isDie || p.c.expdown > 0)) {
                        DieReturn(p);
                    }
                    if (System.currentTimeMillis() > p.c.deleyRequestClan) {
                        p.c.requestclan = -1;
                    }
                    if (p.c.clone != null && !p.c.clone.isDie && (Math.abs(p.c.x - p.c.clone.x) > 80 || Math.abs(p.c.y - p.c.clone.y) > 30)) {
                        p.c.clone.move((short) util.nextInt(p.c.x - 35, p.c.x + 35), p.c.y);
                    }
                    if (!p.c.clone.isDie && System.currentTimeMillis() > p.c.timeRemoveClone) {
                        p.c.clone.off();
                    }
                    if (p.c.get().isDie) {
                        p.exitNhanBan(true);
                    }
                }
                // Xóa item map
                for (int i = 0; i < itemMap.size(); i++) {
                    ItemMap itm = itemMap.get(i);
                    if (System.currentTimeMillis() >= itm.removedelay) {
                        removeItemMapMessage(itm.itemMapId);
                        itemMap.remove(i);
                        i--;
                    } else if ((itm.removedelay - System.currentTimeMillis()) < 70000L && itm.master != -1) {
                        itm.master = -1;
                    }
                }
                if (map.cave != null && System.currentTimeMillis() > map.cave.time) {
                    map.cave.rest();
                }
                if (map.cave != null && map.cave.level == map.cave.map.length) {
                    map.cave.finsh();
                }
                if (map.ldgt != null && System.currentTimeMillis() > map.ldgt.time) {
                    map.ldgt.rest();
                }
                for (int k = 0; k < this.players.size(); ++k) {
                    final Player p = this.players.get(k);
                    if (map.id >= 98 && map.id <= 104 && (hour == 11 || hour == 21) && (min == 0) && sec == 0) {
                        Map ma = Manager.getMapid(p.c.mapLTD);
                        for (Place area : ma.area) {
                            if (area.numplayers < ma.template.maxplayers) {
                                p.c.place.leave(p);
                                area.EnterMap0(p.c);
                                p.sendAddchatYellow("Chiến trường đã kết thúc. Nhận thưởng tại NPC Rikudou.");
                                return;
                            }
                        }
                    }
                }
                for (int k = 0; k < this.players.size(); ++k) { // Tự động lưu dữ liệu 
                    final Player p = this.players.get(k);
                    if (p != null && min % 5 == 0 && sec == 1) {
                        p.flush();
                        if (p.c != null) {
                            p.c.flush();
                            if (p.c.clone != null) {
                                p.c.clone.flush();
                            }
                        }
                        System.out.println("Sao luu du lieu nguoi choi: " + p.c.name);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
    }
    
    public synchronized void sendToMap(final Message ms) throws IOException {
        for (final Player pl : players) {
            if (pl != null) {
                pl.conn.sendMessage(ms);
            }
        }
    }
    
    public String result(Char c) throws SQLException {
        String win = null;
        int pointWhite = 0;
        ResultSet red = SQLManager.stat.executeQuery("SELECT `pointCT` FROM `ninja` WHERE typeCT=4;");
        while (red.next()) {
            int a = red.getInt("pointCT");
            pointWhite = pointWhite + a;
        }
        int pointBlack = 0;
        ResultSet red2 = SQLManager.stat.executeQuery("SELECT `pointCT` FROM `ninja` WHERE typeCT=5;");
        while (red2.next()) {
            int b = red2.getInt("pointCT");
            pointBlack = pointBlack + b;
        }
        if (pointWhite == pointBlack) {
            win = "Hai phe hoà nhau";
        } else if (pointWhite > pointBlack) {
            win = "Bạch Giả giành chiến thắng";
            map.war.win = 4;
        } else {
            win = "Hắc Giả giành chiến thắng";
            map.war.win = 5;
        }
        String result = "Tích luỹ: " + c.pointCT + " điểm " + (c.typeCT == map.war.win ? "(thưởng)\n" : "\n") + win + "\nBạch Giả: " + pointWhite + " điểm\nHắc Giả: " + pointBlack + " điểm\n---------------\n" + BXHManager.getStringBXH(4);
        return result;
    }
    // Lôi đài 
    public void EnterMap3(Char n) {
        n.clone.x = n.x = 385;
        n.clone.y = n.y = 300;
        n.mapid = map.id;
        try {
            Enter(n.p);
        } catch (IOException e) {
        }
    }
    public void EnterMap1(Char n) {
        n.clone.x = n.x = 312;
        n.clone.y = n.y = 214;
        n.mapid = map.id;
        try {
            Enter(n.p);
        } catch (IOException e) {
        }
    }
   private void missMob(final int mobid) throws IOException {
        final Message m = new Message(51);
        m.writer().writeByte(mobid);
        final Mob mob = this.getMob(mobid);
        m.writer().writeInt(mob.hp);
        m.writer().flush();
        this.sendMessage(m);
        m.cleanup();
    } 

    public void rsMobLDGT() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void refreshMobLDGT(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
