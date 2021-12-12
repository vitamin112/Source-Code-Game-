package real;

/**
 *
 * @author Dũng Trần
 */


import io.Message;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import server.Server;
import server.util;

public class ItemData {

    static Server server = Server.getInstance();

    public short id;
    public byte type;
    public byte nclass;
    public byte skill;
    public byte gender;
    public String name;
    public String description;
    public byte level;
    public short iconID;
    public short part;
    public boolean isUpToUp;
    public boolean isExpires;
    public long seconds_expires;
    public int saleCoinLock;
    public ArrayList<Option> itemoption;
    public ArrayList<Option> option1;
    public ArrayList<Option> option2;
    public ArrayList<Option> option3;
    public static ArrayList<ItemData> entrys = new ArrayList<>();

    public static boolean isTypeBody(int id) {
        for (ItemData entry : entrys) {
            if (entry.id == id) {
                return entry.type >= 0 && entry.type <= 15;
            }
        }
        return false;
    }

    public static boolean isTypeUIME(int typeUI) {
        return typeUI == 5 || typeUI == 3 || typeUI == 4 || typeUI == 39;
    }

    public static boolean isTypeUIShop(int typeUI) {
        return typeUI == 20 || typeUI == 21 || typeUI == 22 || typeUI == 23 || typeUI == 24 || typeUI == 25 || typeUI == 26 || typeUI == 27 || typeUI == 28 || typeUI == 29 || typeUI == 16 || typeUI == 17 || typeUI == 18 || typeUI == 19 || typeUI == 2 || typeUI == 6 || typeUI == 8 || typeUI == 34;
    }

    public static boolean isTypeUIShopLock(int typeUI) {
        return typeUI == 7 || typeUI == 9;
    }

    public static boolean isTypeUIStore(int typeUI) {
        return typeUI == 14;
    }

    public static boolean isTypeUIBook(int typeUI) {
        return typeUI == 15;
    }

    public static boolean isTypeUIFashion(int typeUI) {
        return typeUI == 32;
    }

    public static boolean isTypeUIClanShop(int typeUI) {
        return typeUI == 34;
    }

    public static boolean isTypeMounts(int id) {
        for (ItemData entry : entrys) {
            if (entry.id == id) {
                return entry.type >= 29 && entry.type <= 33;
            }
        }
        return false;
    }

    public static boolean isTypeNgocKham(int id) {
        for (ItemData entry : entrys) {
            if (entry.id == id) {
                return entry.type == 34;
            }
        }
        return false;
    }

    public static int ThinhLuyenParam(int id, int tl) {
        switch (id) {
            case 76:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 50 : 60 : 70 : 90 : 130 : 180 : 250 : 350 : 550;
            case 77:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 40 : 60 : 80 : 100 : 120 : 140 : 200 : 220 : 590;
            case 75:
            case 78:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 25 : 30 : 35 : 40 : 50 : 60 : 80 : 115 : 165;
            case 79:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 1 : 1 : 1 : 1 : 5 : 5 : 5 : 5 : 5;
            case 80:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 5 : 10 : 15 : 20 : 25 : 30 : 35 : 40 : 45;
            case 84:
            case 86:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 10 : 20 : 30 : 40 : 50 : 100 : 120 : 150 : 200;
            case 85:
                return 1;
            case 82:
            case 83:
            case 87:
            case 88:
            case 89:
            case 90:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 50 : 60 : 80 : 100 : 125 : 300 : 350 : 400 : 500;
            case 94:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 5 : 10 : 15 : 20 : 25 : 30 : 35 : 40 : 60;
            case 81:
            case 91:
            case 92:
            case 95:
            case 96:
            case 97:
                return (tl != 8) ? (tl != 7) ? (tl != 6) ? (tl != 5) ? (tl != 4) ? (tl != 3) ? (tl != 2) ? (tl != 1) ? (tl != 0) ? 0 : 1 : 5 : 10 : 15 : 20 : 25 : 30 : 40 : 60;
        }
        return 0;
    }

    public static void divedeItem(Player p, Message m) throws IOException {
        byte index = m.reader().readByte();
        int quantity = m.reader().readInt();
        m.cleanup();
        if (p.c.getBagNull() == 0) {
            p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        Item item = p.c.getIndexBag(index);
        if (quantity > 0 && item != null && item.quantity > 1 && quantity <= item.quantity) {
            Item itemup = new Item();
            itemup.id = item.id;
            itemup.isLock = item.isLock;
            itemup.upgrade = item.upgrade;
            itemup.isExpires = item.isExpires;
            itemup.quantity = quantity;
            itemup.expires = item.expires;
//            p.c.removeItemBag(index, quantity);
            for (int i = 0; i <= quantity; i++) {
                p.c.removeItemBag(index, 1);
            }

            p.c.addItemBag(false, itemup);
        }
    }

    public static boolean isUpgradeHide(int id, byte upgrade) {
        if ((id == 27 || id == 30 || id == 60) && upgrade < 4) {
            return true;
        } else if ((id == 28 || id == 31 || id == 37 || id == 61) && upgrade < 8) {
            return true;
        } else if ((id == 29 || id == 32 || id == 38 || id == 62) && upgrade < 12) {
            return true;
        } else if ((id == 33 || id == 34 || id == 35 || id == 36 || id == 39) && upgrade < 14) {
            return true;
        } else if (((id >= 40 && id <= 46) || (id >= 48 && id <= 56)) && upgrade < 16) {
            return true;
        }
        return false;
    }

    public static Item itemDefault(int id) {
        return itemDefault(id, (byte) 0);
    }

    public static Item itemDefault(int id, boolean isLock) {
        Item item = itemDefault(id, (byte) 0);
        item.isLock = isLock;
        return item;
    }
    
    public static Item itemDefault(int id, byte sys) {
        Item item = new Item();
        item.id = (short) id;
        item.sys = sys;
        ItemData data = ItemDataId(id);
        if (data.isExpires) {
            item.isExpires = true;
            item.expires = util.TimeSeconds(data.seconds_expires);
        }
        item.saleCoinLock = data.saleCoinLock;
        if (sys == 0) {
            for (Option option : data.itemoption) {
                int idOp = option.id;
                int par = option.param;
                Option op = new Option(idOp, par);
                item.options.add(op);
            }
        } else if (sys == 1) {
            for (Option option : data.option1) {
                int idOp = option.id;
                int par = option.param;
                Option op = new Option(idOp, par);
                item.options.add(op);
            }
        } else if (sys == 2) {
            for (Option option : data.option2) {
                int idOp = option.id;
                int par = option.param;
                Option op = new Option(idOp, par);
                item.options.add(op);
            }
        } else if (sys == 3) {
            for (Option option : data.option3) {
                int idOp = option.id;
                int par = option.param;
                Option op = new Option(idOp, par);
                item.options.add(op);
            }
        }
        return item;
    }

    public static Item parseItem(String str) {
        Item item = new Item();
        JSONObject job = (JSONObject) JSONValue.parse(str);
        item.id = Short.parseShort(job.get("id").toString());
        item.isLock = Boolean.parseBoolean(job.get("isLock").toString());
        item.upgrade = Byte.parseByte(job.get("upgrade").toString());
        item.isExpires = Boolean.parseBoolean(job.get("isExpires").toString());
        item.quantity = Integer.parseInt(job.get("quantity").toString());
        if (item.isExpires) {
            item.expires = Long.parseLong(job.get("expires").toString());
        }
        if (item.id == 523 && !item.isExpires) {
            item.isExpires = true;
            item.expires = util.TimeDay(1);
        }
        item.sys = Byte.parseByte(job.get("sys").toString());
        item.saleCoinLock = Integer.parseInt(job.get("sale").toString());
        JSONArray Option = (JSONArray) JSONValue.parse(job.get("option").toString());
        for (Object Option1 : Option) {
            JSONObject job2 = (JSONObject) Option1;
            Option option = new Option(Integer.parseInt(job2.get("id").toString()), Integer.parseInt(job2.get("param").toString()));
            item.options.add(option);
        }
        return item;
    }

    public static JSONObject ObjectItem(Item item, int index) {
        JSONObject put = new JSONObject();
        put.put("index", index);
        put.put("id", item.id);
        put.put("isLock", item.isLock);
        put.put("upgrade", item.upgrade);
        put.put("isExpires", item.isExpires);
        put.put("quantity", item.quantity);
        if (item.isExpires) {
            put.put("expires", item.expires);
        }
        put.put("sys", item.sys);
        put.put("sale", item.saleCoinLock);
        JSONArray option = new JSONArray();
        for (Option Option : item.options) {
            JSONObject pa = new JSONObject();
            pa.put("id", Option.id);
            pa.put("param", Option.param);
            option.add(pa);
        }
        put.put("option", option);
        return put;
    }

    public static ItemData ItemDataId(int id) {
        for (ItemData entry : entrys) {
            if (entry.id == id) {
                return entry;
            }
        }
        return null;
    }
}
