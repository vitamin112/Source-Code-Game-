package server;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.exit;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import real.Char;
import real.ClanManager;
import real.ClanMember;
import real.EffectData;
import real.Item;
import real.ItemData;
import real.ItemOptionTemplate;
import real.ItemSell;
import real.ItemTemplate;
import real.Level;
import real.Map;
import real.MapTemplate;
import real.MobData;
import real.Npc;
import real.Option;
import real.Player;
import real.PlayerManager;
import real.SkillData;
import real.SkillTemplates;
import real.Vgo;

public class Manager {

    int post;
    private String host;
    private String mysql_host;
    private String mysql_server;
    private String mysql_account;
    private String mysql_user;
    private String mysql_pass;
    private byte vsData;
    private byte vsMap;
    private byte vsSkill;
    public static byte vsItem;
    private byte[][] tasks;
    private byte[][] maptasks;
    static Server server = Server.getInstance();
    Rotationluck rotationluck[] = new Rotationluck[2];
    public static ItemOptionTemplate[] iOptionTemplates;
    public static ItemTemplate[] itemTemplates;

    
    public byte event = 0;

    public String[] NinjaS = new String[]{"Chưa vào lớp", "Ninja Kiếm", "Ninja Phi Tiêu", "Ninja Kinai", "Ninja Cung", "Ninja Đao", "Ninja Quạt"};

    public Manager() {
        loadConfigFile();
        rotationluck[0] = new Rotationluck("Vòng xoay vip", (byte) 0, (short) 120, 1000000, 50000000, 1000000000);
        rotationluck[1] = new Rotationluck("Vòng xoay thường", (byte) 1, (short) 120, 10000, 100000, 500000000);
        rotationluck[0].start();
        rotationluck[1].start();
        loadDataBase();
    }

    public static Map getMapid(int id) {
        synchronized (server.maps) {
            for (short i = 0; i < server.maps.length; i++) {
                Map map = server.maps[i];
                if (map != null && map.id == id) {
                    return map;
                }
            }
            return null;
        }
    }
    
    private void loadConfigFile() {
        byte[] ab = GameScr.loadFile("ninja.conf").toByteArray();
        if (ab == null) {
            System.out.println("Config file not found!");
            System.exit(0);
        }
        String data = new String(ab);
        HashMap<String, String> configMap = new HashMap<>();
        StringBuilder sbd = new StringBuilder();
        boolean bo = false;
        for (int i = 0; i <= data.length(); i++) {
            char es;
            if ((i == data.length()) || ((es = data.charAt(i)) == '\n')) {
                bo = false;
                String sbf = sbd.toString().trim();
                if (sbf != null && !sbf.equals("") && sbf.charAt(0) != '#') {
                    int j = sbf.indexOf(':');
                    if (j > 0) {
                        String key = sbf.substring(0, j).trim();
                        String value = sbf.substring(j + 1).trim();
                        configMap.put(key, value);
                        System.out.println("config: " + key + "-" + value);
                    }
                }
                sbd.setLength(0);
                continue;
            }
            if (es == '#') {
                bo = true;
            }
            if (!bo) {
                sbd.append(es);
            }
        }
        if (configMap.containsKey("debug")) {
            util.setDebug(Boolean.parseBoolean(configMap.get("debug")));
        } else {
            util.setDebug(false);
        }
        if (configMap.containsKey("host")) {
            host = configMap.get("host");
        } else {
            host = "localhost";
        }
        if (configMap.containsKey("post")) {
            post = Short.parseShort(configMap.get("post"));
        } else {
            post = 18888; //14444
        }
        if (configMap.containsKey("mysql-host")) {
            mysql_host = configMap.get("mysql-host");
        } else {
            mysql_host = "localhost";
        }
        if (configMap.containsKey("mysql-user")) {
            mysql_user = configMap.get("mysql-user");
        } else {
            mysql_user = "root";
        }
        if (configMap.containsKey("mysql-password")) {
            mysql_pass = configMap.get("mysql-password");
        } else {
            mysql_pass = "";
        }
        if (configMap.containsKey("mysql-server")) {
            mysql_server = configMap.get("mysql-server");
        } else {
            mysql_server = "nso_server";
        }
        if (configMap.containsKey("mysql-account")) {
            mysql_account = configMap.get("mysql-account");
        } else {
            mysql_account = "nso_account";
        }
        if (configMap.containsKey("version-Data")) {
            vsData = Byte.parseByte(configMap.get("version-Data"));
        } else {
            vsData = 55; // 54 55
        }
        if (configMap.containsKey("version-Map")) {
            vsMap = Byte.parseByte(configMap.get("version-Map"));
        } else {
            vsMap = 23; // 86 23
        }
        if (configMap.containsKey("version-Skill")) {
            vsSkill = Byte.parseByte(configMap.get("version-Skill"));
        } else {
            vsSkill = 16; // 10 16
        }
        if (configMap.containsKey("version-Item")) {
            vsItem = Byte.parseByte(configMap.get("version-Item"));
        } else {
            vsItem = 87; // 70 82 87
        }
        if (configMap.containsKey("version-Event")) {
            event = Byte.parseByte(configMap.get("version-Event"));
        } else {
            event = 1;
        }
    }

    private void loadDataBase() {
        SQLManager.create(mysql_host, mysql_server, mysql_user, mysql_pass);
        int i = 0;
        ResultSet res;
        try {
            res = SQLManager.stat.executeQuery("SELECT * FROM `tasks`;");
            if (res.last()) {
                tasks = new byte[res.getRow()][];
                maptasks = new byte[tasks.length][];
                res.beforeFirst();
            }
            while (res.next()) {
                JSONArray jarr = (JSONArray) JSONValue.parse(res.getString("tasks"));
                JSONArray jarr2 = (JSONArray) JSONValue.parse(res.getString("maptasks"));
                tasks[i] = new byte[jarr.size()];
                maptasks[i] = new byte[tasks.length];
                for (byte j = 0; j < jarr.size(); j++) {
                    tasks[i][j] = Byte.parseByte(jarr.get(j).toString());
                    maptasks[i][j] = Byte.parseByte(jarr2.get(j).toString());
                }
                i++;
            }
            res.close();
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `level`;");
            while (res.next()) {
                Level level = new Level();
                level.level = Integer.parseInt(res.getString("level"));
                level.exps = Long.parseLong(res.getString("exps"));
                level.ppoint = Short.parseShort(res.getString("ppoint"));
                level.spoint = Short.parseShort(res.getString("spoint"));
                Level.entrys.add(level);
                i++;
            }
            res.close();
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `effect`;");
            while (res.next()) {
                EffectData eff = new EffectData();
                eff.id = Byte.parseByte(res.getString("id"));
                eff.type = Byte.parseByte(res.getString("type"));
                eff.name = res.getString("name");
                eff.iconId = Short.parseShort(res.getString("iconId"));
                EffectData.entrys.add(eff);
                i++;
            }
            res.close();
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `Mob`;");
            while (res.next()) {
                MobData md = new MobData();
                md.id = Integer.parseInt(res.getString("id"));
                md.type = Byte.parseByte(res.getString("type"));
                md.name = res.getString("name");
                md.hp = Integer.parseInt(res.getString("hp"));
                md.rangeMove = Byte.parseByte(res.getString("rangeMove"));
                md.speed = Byte.parseByte(res.getString("speed"));
                JSONArray jarr = (JSONArray) JSONValue.parse(res.getString("item"));
                md.arrIdItem = new short[jarr.size()];
                for (int j = 0; j < jarr.size(); j++) {
                    md.arrIdItem[j] = Short.parseShort(jarr.get(j).toString());
                }
                MobData.entrys.add(md);
                i++;
            }
            res.close();
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `map`;");
            if (res.last()) {
                MapTemplate.arrTemplate = new MapTemplate[res.getRow()];
                res.beforeFirst();
            }
            while (res.next()) {
                MapTemplate temp = new MapTemplate();
                temp.id = res.getInt("id");
                temp.tileID = res.getByte("tileID");
                temp.bgID = res.getByte("bgID");
                temp.name = res.getString("name");
                temp.typeMap = res.getByte("typeMap");
                temp.maxplayers = res.getByte("maxplayer");
                temp.numarea = res.getByte("numzone");
                temp.x0 = res.getShort("x0");
                temp.y0 = res.getShort("y0");
                JSONArray jarr = (JSONArray) JSONValue.parse(res.getString("Vgo"));
                temp.vgo = new Vgo[jarr.size()];
                for (byte j = 0; j < jarr.size(); j++) {
                    temp.vgo[j] = new Vgo();
                    JSONArray jar2 = (JSONArray) JSONValue.parse(jarr.get(j).toString());
                    Vgo vg = temp.vgo[j];
                    vg.minX = Short.parseShort(jar2.get(0).toString());
                    vg.minY = Short.parseShort(jar2.get(1).toString());
                    vg.maxX = Short.parseShort(jar2.get(2).toString());
                    vg.maxY = Short.parseShort(jar2.get(3).toString());
                    vg.mapid = Short.parseShort(jar2.get(4).toString());
                    vg.goX = Short.parseShort(jar2.get(5).toString());
                    vg.goY = Short.parseShort(jar2.get(6).toString());
                }
                jarr = (JSONArray) JSONValue.parse(res.getString("Mob"));
                temp.arMobid = new short[jarr.size()];
                temp.arrMobx = new short[jarr.size()];
                temp.arrMoby = new short[jarr.size()];
                temp.arrMobstatus = new byte[jarr.size()];
                temp.arrMoblevel = new int[jarr.size()];
                temp.arrLevelboss = new byte[jarr.size()];
                temp.arrisboss = new boolean[jarr.size()];
                for (short j = 0; j < jarr.size(); j++) {
                    JSONArray entry = (JSONArray) jarr.get(j);
                    temp.arMobid[j] = Short.parseShort(entry.get(0).toString());
                    temp.arrMoblevel[j] = Integer.parseInt(entry.get(1).toString());
                    temp.arrMobx[j] = Short.parseShort(entry.get(2).toString());
                    temp.arrMoby[j] = Short.parseShort(entry.get(3).toString());
                    temp.arrMobstatus[j] = Byte.parseByte(entry.get(4).toString());
                    temp.arrLevelboss[j] = Byte.parseByte(entry.get(5).toString());
                    temp.arrisboss[j] = Boolean.parseBoolean(entry.get(6).toString());
                }
                jarr = (JSONArray) JSONValue.parse(res.getString("NPC"));
                temp.npc = new Npc[jarr.size()];
                for (byte j = 0; j < jarr.size(); j++) {
                    temp.npc[j] = new Npc();
                    JSONArray jar2 = (JSONArray) JSONValue.parse(jarr.get(j).toString());
                    Npc npc = temp.npc[j];
                    npc.type = Byte.parseByte(jar2.get(0).toString());
                    npc.x = Short.parseShort(jar2.get(1).toString());
                    npc.y = Short.parseShort(jar2.get(2).toString());
                    npc.id = Byte.parseByte(jar2.get(3).toString());
                }
                MapTemplate.arrTemplate[i] = temp;
                i++;
            }
            res.close();
            System.out.println(" ... ");
            try {
                res = SQLManager.stat.executeQuery("SELECT * FROM `optionitem`;");
                if (res.last()) {
                    iOptionTemplates = new ItemOptionTemplate[res.getRow()];
                    res.beforeFirst();
                }
                i = 0;
                while (res.next()) {
                    final ItemOptionTemplate iotemplate = new ItemOptionTemplate();
                    iotemplate.id = res.getInt("id");
                    iotemplate.name = res.getString("name");
                    iotemplate.type = res.getByte("type");
                    iOptionTemplates[i] = iotemplate;
                    ++i;
                }
                res.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            System.out.println("OK");
            try {
                res = SQLManager.stat.executeQuery("SELECT * FROM `item`;");
                if (res.last()) {
                    itemTemplates = new ItemTemplate[res.getRow()];
                    res.beforeFirst();
                }
                i = 0;
                while (res.next()) {
                    final ItemTemplate itemTemplate = new ItemTemplate();
                    itemTemplate.id = res.getShort("id");
                    itemTemplate.type = res.getByte("type");
                    itemTemplate.gender = res.getByte("gender");
                    itemTemplate.name = res.getString("name");
                    itemTemplate.description = res.getString("description");
                    itemTemplate.level = res.getInt("level");
                    itemTemplate.iconID = res.getShort("iconID");
                    itemTemplate.part = res.getShort("part");
                    itemTemplate.isUpToUp = res.getBoolean("isUpToUp");
                    itemTemplates[i] = itemTemplate;
                    ++i;
                }
                res.close();
            }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `item`;");
            while (res.next()) {
                ItemData item = new ItemData();
                item.id = Short.parseShort(res.getString("id"));
                item.type = Byte.parseByte(res.getString("type"));
                item.nclass = Byte.parseByte(res.getString("class"));
                item.skill = Byte.parseByte(res.getString("skill"));
                item.gender = Byte.parseByte(res.getString("gender"));
                item.name = res.getString("name");
                item.description = res.getString("description");
                item.level = Byte.parseByte(res.getString("level"));
                item.iconID = Short.parseShort(res.getString("iconID"));
                item.part = Short.parseShort(res.getString("part"));
                item.isUpToUp = Byte.parseByte(res.getString("isUpToUp")) == 1;
                item.isExpires = Byte.parseByte(res.getString("isExpires")) == 1;
                item.seconds_expires = Long.parseLong(res.getString("secondsExpires"));
                item.saleCoinLock = Integer.parseInt(res.getString("saleCoinLock"));
                item.itemoption = new ArrayList<>();
                JSONArray Option = (JSONArray) JSONValue.parse(res.getString("ItemOption"));
                for (int j = 0; j < Option.size(); j++) {
                    JSONObject job = (JSONObject) Option.get(j);
                    Option option = new Option(Integer.parseInt(job.get("id").toString()), Integer.parseInt(job.get("param").toString()));
                    item.itemoption.add(option);
                }
                item.option1 = new ArrayList<>();
                Option = (JSONArray) JSONValue.parse(res.getString("Option1"));
                for (int j = 0; j < Option.size(); j++) {
                    JSONObject job = (JSONObject) Option.get(j);
                    Option option = new Option(Integer.parseInt(job.get("id").toString()), Integer.parseInt(job.get("param").toString()));
                    item.option1.add(option);
                }
                item.option2 = new ArrayList<>();
                Option = (JSONArray) JSONValue.parse(res.getString("Option2"));
                for (int j = 0; j < Option.size(); j++) {
                    JSONObject job = (JSONObject) Option.get(j);
                    Option option = new Option(Integer.parseInt(job.get("id").toString()), Integer.parseInt(job.get("param").toString()));
                    item.option2.add(option);
                }
                item.option3 = new ArrayList<>();
                Option = (JSONArray) JSONValue.parse(res.getString("Option3"));
                for (int j = 0; j < Option.size(); j++) {
                    JSONObject job = (JSONObject) Option.get(j);
                    Option option = new Option(Integer.parseInt(job.get("id").toString()), Integer.parseInt(job.get("param").toString()));
                    item.option3.add(option);
                }
                ItemData.entrys.add(item);
                i++;
            }
            res.close();
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `skill`;");
            while (res.next()) {
                SkillData skill = new SkillData();
                skill.id = Short.parseShort(res.getString("id"));
                skill.nclass = Byte.parseByte(res.getString("class"));
                skill.name = res.getString("name");
                skill.maxPoint = Byte.parseByte(res.getString("maxPoint"));
                skill.type = Byte.parseByte(res.getString("type"));
                skill.iconId = Short.parseShort(res.getString("iconId"));
                skill.desc = res.getString("desc");
                JSONArray Skilltemplate = (JSONArray) JSONValue.parse(res.getString("SkillTemplates"));
                for (Object template : Skilltemplate) {
                    JSONObject job = (JSONObject) template;
                    SkillTemplates temp = new SkillTemplates();
                    temp.skillId = Short.parseShort(job.get("skillId").toString());
                    temp.point = Byte.parseByte(job.get("point").toString());
                    temp.level = Integer.parseInt(job.get("level").toString());
                    temp.manaUse = Short.parseShort(job.get("manaUse").toString());
                    temp.coolDown = Integer.parseInt(job.get("coolDown").toString());
                    temp.dx = Short.parseShort(job.get("dx").toString());
                    temp.dy = Short.parseShort(job.get("dy").toString());
                    temp.maxFight = Byte.parseByte(job.get("maxFight").toString());
                    JSONArray Option = (JSONArray) JSONValue.parse(job.get("options").toString());
                    for (Object option : Option) {
                        JSONObject job2 = (JSONObject) option;
                        Option op = new Option(Integer.parseInt(job2.get("id").toString()), Integer.parseInt(job2.get("param").toString()));
                        temp.options.add(op);
                    }
                    skill.templates.add(temp);
                }
                SkillData.entrys.add(skill);
                i++;
            }
            res.close();
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `ItemSell`;");
            while (res.next()) {
                ItemSell sell = new ItemSell();
                sell.id = Integer.parseInt(res.getString("id"));
                sell.type = Byte.parseByte(res.getString("type"));
                JSONArray jar = (JSONArray) JSONValue.parse(res.getString("ListItem"));
                if (jar != null) {
                    sell.item = new Item[jar.size()];
                    for (byte j = 0; j < jar.size(); j++) {
                        JSONObject job = (JSONObject) jar.get(j);
                        Item item = ItemData.parseItem(jar.get(j).toString());
                        item.buyCoin = Integer.parseInt(job.get("buyCoin").toString());
                        item.buyCoinLock = Integer.parseInt(job.get("buyCoinLock").toString());
                        item.buyGold = Integer.parseInt(job.get("buyGold").toString());
                        sell.item[j] = item;
                    }
                }
                ItemSell.entrys.add(sell);
                i++;
            }
            res.close();
        } catch (Exception e) {
            System.out.println("Error i:" + i);
            e.printStackTrace();
            exit(0);
        }
        SQLManager.close();
        SQLManager.create("localhost", mysql_account , "root", mysql_pass);
        loadGame();
    }
    
    private void loadGame() {
        int i = 0;
        ResultSet res;
        try {
            //load game
            i = 0;
            res = SQLManager.stat.executeQuery("SELECT * FROM `clan`;");
            while (res.next()) {
                ClanManager clan = new ClanManager();
                clan.id = Integer.parseInt(res.getString("id"));
                clan.name = res.getString("name");
                clan.exp = res.getInt("exp");
                clan.level = res.getInt("level");
                clan.itemLevel = res.getInt("itemLevel");
                clan.coin = res.getInt("coin");
                clan.reg_date = res.getString("reg_date");
                clan.log = res.getString("log");
                clan.alert = res.getString("alert");
                clan.use_card = res.getByte("use_card");
                clan.openDun = res.getByte("openDun");
                clan.debt = res.getByte("debt");
                JSONArray jar = (JSONArray) JSONValue.parse(res.getString("members"));
                if (jar != null) {
                    for (short j = 0; j < jar.size(); j++) {
                        JSONArray jar2 = (JSONArray) jar.get(j);
                        ClanMember mem = new ClanMember();
                        mem.charID = Integer.parseInt(jar2.get(0).toString());
                        mem.cName = jar2.get(1).toString();
                        mem.clanName = jar2.get(2).toString();
                        mem.typeclan = Byte.parseByte(jar2.get(3).toString());
                        mem.clevel = Integer.parseInt(jar2.get(4).toString());
                        mem.nClass = Byte.parseByte(jar2.get(5).toString());
                        mem.pointClan = Integer.parseInt(jar2.get(6).toString());
                        mem.pointClanWeek = Integer.parseInt(jar2.get(7).toString());
                        clan.members.add(mem);
                    }
                }
                jar = (JSONArray) JSONValue.parse(res.getString("items"));
                if (jar != null) {
                    for (byte j = 0; j < jar.size(); j++) {
                        clan.items.add(ItemData.parseItem(jar.get(j).toString()));
                    }
                }
                clan.week = res.getString("week");
                ClanManager.entrys.add(clan);
            }
            res.close();
            SQLManager.stat.executeUpdate("UPDATE `ninja` SET `caveID`=-1;");
        } catch (Exception e) {
            System.out.println("Error i:" + i);
            e.printStackTrace();
            exit(0);
        }
    }

    public static void saveFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void createItem(final Player p){
        Message m = new Message(-28);
        try{
            m.writer().writeByte(-119);
            m.writer().writeByte(Manager.vsItem);
            m.writer().writeByte(Manager.iOptionTemplates.length);
            for (short i = 0; i < Manager.iOptionTemplates.length; ++i) {
                m.writer().writeUTF(Manager.iOptionTemplates[i].name);
                m.writer().writeByte(Manager.iOptionTemplates[i].type);
            }
            m.writer().writeShort(Manager.itemTemplates.length);
            for (short j = 0; j < Manager.itemTemplates.length; ++j) {
                m.writer().writeByte(Manager.itemTemplates[j].type);
                m.writer().writeByte(Manager.itemTemplates[j].gender);
                m.writer().writeUTF(Manager.itemTemplates[j].name);
                m.writer().writeUTF(Manager.itemTemplates[j].description);
                m.writer().writeByte(Manager.itemTemplates[j].level);
                m.writer().writeShort(Manager.itemTemplates[j].iconID);
                m.writer().writeShort(Manager.itemTemplates[j].part);
                m.writer().writeBoolean(Manager.itemTemplates[j].isUpToUp);
            }
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }
    
    
    public void sendData(Player p) throws IOException {
        byte[] ab;
        Message m = new Message(-28);
        m.writer().writeByte(-122);
        m.writer().writeByte(vsData);
        ab = GameScr.loadFile("res/cache/data/nj_arrow").toByteArray();
        m.writer().writeInt(ab.length);
        m.writer().write(ab);
        ab = GameScr.loadFile("res/cache/data/nj_effect").toByteArray();
        m.writer().writeInt(ab.length);
        m.writer().write(ab);
        ab = GameScr.loadFile("res/cache/data/nj_image").toByteArray();
        m.writer().writeInt(ab.length);
        m.writer().write(ab);
        ab = GameScr.loadFile("res/cache/data/nj_part").toByteArray();
        m.writer().writeInt(ab.length);
        m.writer().write(ab);
        ab = GameScr.loadFile("res/cache/data/nj_skill").toByteArray();
        m.writer().writeInt(ab.length);
        m.writer().write(ab);
        //tasks
        m.writer().writeByte(tasks.length);
        for (byte i = 0; i < tasks.length; i++) {
            m.writer().writeByte(tasks[i].length);
            for(byte j = 0; j < tasks[i].length; j++) {
                m.writer().writeByte(tasks[i][j]);
                m.writer().writeByte(maptasks[i][j]);
            }
        }
        //xp lv
        m.writer().writeByte(Level.entrys.size());
        for (Level entry : Level.entrys) {
            m.writer().writeLong(entry.exps);
        }
        //crystals
        m.writer().writeByte(GameScr.crystals.length);
        for (byte i = 0; i < GameScr.crystals.length; i++) {
            m.writer().writeInt(GameScr.crystals[i]);
        }
        //upClothe
        m.writer().writeByte(GameScr.upClothe.length);
        for (byte i = 0; i < GameScr.upClothe.length; i++) {
            m.writer().writeInt(GameScr.upClothe[i]);
        }
        //upAdorn
        m.writer().writeByte(GameScr.upAdorn.length);
        for (byte i = 0; i < GameScr.upAdorn.length; i++) {
            m.writer().writeInt(GameScr.upAdorn[i]);
        }
        //upWeapon
        m.writer().writeByte(GameScr.upWeapon.length);
        for (byte i = 0; i < GameScr.upWeapon.length; i++) {
            m.writer().writeInt(GameScr.upWeapon[i]);
        }
        //coinUpCrystals
        m.writer().writeByte(GameScr.coinUpCrystals.length);
        for (byte i = 0; i < GameScr.coinUpCrystals.length; i++) {
            m.writer().writeInt(GameScr.coinUpCrystals[i]);
        }
        //coinUpClothes
        m.writer().writeByte(GameScr.coinUpClothes.length);
        for (byte i = 0; i < GameScr.coinUpClothes.length; i++) {
            m.writer().writeInt(GameScr.coinUpClothes[i]);
        }
        //coinUpAdorns
        m.writer().writeByte(GameScr.coinUpAdorns.length);
        for (byte i = 0; i < GameScr.coinUpAdorns.length; i++) {
            m.writer().writeInt(GameScr.coinUpAdorns[i]);
        }
        //coinUpWeapons
        m.writer().writeByte(GameScr.coinUpWeapons.length);
        for (byte i = 0; i < GameScr.coinUpWeapons.length; i++) {
            m.writer().writeInt(GameScr.coinUpWeapons[i]);
        }
        //goldUps
        m.writer().writeByte(GameScr.goldUps.length);
        for (byte i = 0; i < GameScr.goldUps.length; i++) {
            m.writer().writeInt(GameScr.goldUps[i]);
        }
        //maxPercents
        m.writer().writeByte(GameScr.maxPercents.length);
        for (byte i = 0; i < GameScr.maxPercents.length; i++) {
            m.writer().writeInt(GameScr.maxPercents[i]);
        }
        //effect
        m.writer().writeByte(EffectData.entrys.size());
        for (byte i = 0; i < EffectData.entrys.size(); i++) {
            m.writer().writeByte(EffectData.entrys.get(i).id);
            m.writer().writeByte(EffectData.entrys.get(i).type);
            m.writer().writeUTF(EffectData.entrys.get(i).name);
            m.writer().writeShort(EffectData.entrys.get(i).iconId);
        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }
  
  public void sendMap(Player p) throws IOException {
    Message m = new Message(-28);
    m.writer().writeByte(-121);
    m.writer().write(server.cache[1].toByteArray());
    m.writer().flush();
    p.conn.sendMessage(m);
    m.cleanup();
  }
  
  public void sendSkill(Player p) throws IOException {
    Message m = new Message(-28);
    m.writer().writeByte(-120);
    m.writer().write(server.cache[2].toByteArray());
    m.writer().flush();
    p.conn.sendMessage(m);
    m.cleanup();
  }
  
  public void getPackMessage(Player p) throws IOException {
        Message m = new Message(-28);
        m.writer().writeByte(-123);
        m.writer().writeByte(vsData);
        m.writer().writeByte(vsMap);
        m.writer().writeByte(vsSkill);
        m.writer().writeByte(vsItem);
        m.writer().writeByte(0);
        m.writer().writeByte(0);
        m.writer().writeByte(0);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static void chatKTG(String chat) throws IOException {
        Message m = new Message(-25);
        m.writer().writeUTF(chat);
        m.writer().flush();
        PlayerManager.getInstance().NinjaMessage(m);
        m.cleanup();
    }

    public void Infochat(String chat) throws IOException {
        Message m = new Message(-24);
        m.writer().writeUTF(chat);
        m.writer().flush();
        PlayerManager.getInstance().NinjaMessage(m);
        m.cleanup();
    }
    
    protected void stop() {
        
    }

    protected void chatKTG(Player p, Message m) throws IOException {
        String chat = m.reader().readUTF();
        m.cleanup();
        if (p.chatKTGdelay > System.currentTimeMillis()) {
            p.conn.sendMessageLog("Chờ sau " + ((p.chatKTGdelay - System.currentTimeMillis()) / 1000L) + "s.");
            return;
        }
        p.chatKTGdelay = System.currentTimeMillis() + 5000L;
        if (p.luong < 500) {
            p.conn.sendMessageLog("Bạn không có đủ lượng trên người.");
            return;
        }
        p.luongMessage(-5);
        serverChat(p.c.name, chat);
    }
    
    public static void serverChat(String name, String s) {
        try {
            Message m = new Message(-21);
            m.writer().writeUTF(name);
            m.writer().writeUTF(s);
            m.writer().flush();
            PlayerManager.getInstance().NinjaMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTB(Player p, String title, String s) throws IOException {
        Message m = new Message(53);
        m.writer().writeUTF(title);
        m.writer().writeUTF(s);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    protected void close() {
        for (int i = 0; i < rotationluck.length; i++) {
            rotationluck[i].close();
            rotationluck[i] = null;
        }
        rotationluck = null;
        for (int i = 0; i < server.maps.length; i++) {
            server.maps[i].close();
            server.maps[i] = null;
        }
        server.maps = null;
    }
}
