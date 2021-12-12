package server;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import io.Session;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import real.ClanManager;
import real.Item;
import real.ItemData;
import real.Option;
import real.Player;
import real.ItemSell;
import real.MobData;
import real.Skill;
import real.SkillData;

public class GameScr {
    
    static Server server = Server.getInstance();
    static final int[] crystals = new int[]{1, 4, 16, 64, 256, 1024, 4096, 16384, 65536, 262144, 1048576, 3096576};
    static final int[] upClothe = new int[]{4, 9, 33, 132, 177, 256, 656, 2880, 3968, 6016, 13440, 54144, 71680, 108544, 225280, 1032192};
    static final int[] upAdorn = new int[]{6, 14, 50, 256, 320, 512, 1024, 5120, 6016, 9088, 19904, 86016, 108544, 166912, 360448, 1589248};
    static final int[] upWeapon = new int[]{18, 42, 132, 627, 864, 1360, 2816, 13824, 17792, 26880, 54016, 267264, 315392, 489472, 1032192, 4587520};
    static final int[] coinUpCrystals = new int[]{10, 40, 160, 640, 2560, 10240, 40960, 163840, 655360, 1310720, 3932160, 11796480};
    static final int[] coinUpClothes = new int[]{120, 270, 990, 3960, 5310, 7680, 19680, 86400, 119040, 180480, 403200, 1624320, 2150400, 3256320, 6758400, 10137600};
    static final int[] coinUpAdorns = new int[]{180, 420, 1500, 7680, 9600, 15360, 30720, 153600, 180480, 272640, 597120, 2580480, 3256320, 5007360, 10813440, 16220160};
    static final int[] coinUpWeapons = new int[]{540, 1260, 3960, 18810, 25920, 40800, 84480, 414720, 533760, 806400, 1620480, 8017920, 9461760, 14684160, 22026240, 33039360};
    static final int[] goldUps = new int[]{1, 2, 3, 4, 5, 10, 15, 20, 50, 100, 150, 200, 300, 400, 500, 600};
    static final int[] maxPercents = new int[]{80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5};
    private static final short[] ArridLuck = new short[]{3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 8, 9, 12, 12, 567, 275, 276, 277, 278, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 369, 370, 371, 372, 373, 374, 407, 408, 419, 443, 492, 493, 494, 495, 496, 497, 498, 499, 500, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510};
    private static final int[] ArryenLuck = new int[]{10000, 20000, 30000, 50000, 100000, 200000, 500000, 1000000, 5000000};
    private static final byte[] ArrdayLuck = new byte[]{3, 7, 15, 30};
    static final int[] NgocKham = new int[]{102, 115, 126, 105, 117, 114, 73, 114, 124, 73, 125, 119, 103, 125, 118, 120, 124, 114, 113, 116, 125, 117, 124, 119};// id ngọc
    static final int[] arroption = new int[]{10000, -1100, 12000, -330, 50000, -500, 10000, -200, 5000, -1000, 20000, -1000, 10000, -2000, 12000, -1000, 5000, -300, 1000, -1000, 50000, -2000, 6000, -1000};// id ngọc
    private static int[][] arrNgocKhamEXP = new int[][]{
        new int[2],
        new int[]{
            200,
            10
        },
        new int[]{
            500,
            20
        },
        new int[]{
            1000,
            50
        },
        new int[]{
            2000,
            100
        },
        new int[]{
            5000,
            200
        },
        new int[]{
            10000,
            500
        },
        new int[]{
            20000,
            1000
        },
        new int[]{
            50000,
            2000
        },
        new int[]{
            100000,
            5000
        },
        new int[]{
            100000,
            10000
        }
    };
    private static int[][] arrLuyenNgocEXP = new int[][]{
        new int[2],
        new int[]{
            200,
            0
        },
        new int[]{
            500,
            200
        },
        new int[]{
            1000,
            500
        },
        new int[]{
            2000,
            1000
        },
        new int[]{
            5000,
            2000
        },
        new int[]{
            10000,
            5000
        },
        new int[]{
            20000,
            10000
        },
        new int[]{
            50000,
            20000
        },
        new int[]{
            100000,
            50000
        },
        new int[]{
            110000,
            100000
        }
    };
    public static int[] coinGotngoc = new int[]{
        0,
        5000,
        40000,
        135000,
        320000,
        625000,
        1080000,
        1715000,
        2560000,
        3645000,
        5000000
    };

    public static boolean mapNotPK(int mapId) {
        return (mapId == 1 || mapId == 10 || mapId == 17 || mapId == 22 || mapId == 27 || mapId == 32 || mapId == 38 || mapId == 43 || mapId == 48 || mapId == 72 || mapId == 100 || mapId == 101 || mapId == 102 || mapId == 109 || mapId == 121 || mapId == 122 || mapId == 123 || mapId == 138);
    }

    public static byte KeepUpgrade(int upgrade) {
        if (upgrade >= 14) {
            return 14;
        } else if (upgrade >= 12) {
            return 12;
        } else if (upgrade >= 8) {
            return 8;
        } else if (upgrade >= 4) {
            return 4;
        } else {
            return (byte) (upgrade);
        }
    }

    public static byte SysClass(byte nclass) {
        switch (nclass) {
            case 1:
            case 2:
                return 1;
            case 3:
            case 4:
                return 2;
            case 5:
            case 6:
                return 3;
        }
        if (nclass == 6 || nclass == 5) {
            return 3;
        } else if (nclass == 4 || nclass == 3) {
            return 2;
        } else if (nclass == 2 || nclass == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public static byte SideClass(byte nclass) {
        if (nclass == 6 || nclass == 4 || nclass == 2) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void SendFile(Session session, int cmd, String url) throws IOException {
        byte[] ab = GameScr.loadFile(url).toByteArray();
        Message msg = new Message(cmd);
        msg.writer().write(ab);
        msg.writer().flush();
        session.sendMessage(msg);
        msg.cleanup();
    }

    public static void ItemStands(Player p) throws IOException {
        Message m = new Message(-28);
        m.writer().writeByte(-83);
        m.writer().writeByte(10);
        m.writer().writeByte(12);
        m.writer().writeByte(12);
        m.writer().writeByte(13);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static void sendSkill(Player p, String text) {
        try {
            byte[] arrSkill = null;
            int lent = 0;
            if (text.equals("KSkill")) {
                lent = p.c.get().KSkill.length;
                arrSkill = new byte[lent];
                System.arraycopy(p.c.get().KSkill, 0, arrSkill, 0, lent);
            }
            if (text.equals("OSkill")) {
                lent = p.c.get().OSkill.length;
                arrSkill = new byte[lent];
                System.arraycopy(p.c.get().OSkill, 0, arrSkill, 0, lent);
            }
            if (text.equals("CSkill")) {
                lent = 1;
                arrSkill = new byte[lent];
                arrSkill[0] = -1;
                Skill skill = p.c.get().getSkill(p.c.get().CSkill);
                if (skill != null) {
                    SkillData data = SkillData.Templates(skill.id);
                    if (data.type != 2) {
                        arrSkill[0] = (byte) skill.id;
                    }
                }
                if (arrSkill[0] == -1 && p.c.get().skill.size() > 0) {
                    arrSkill[0] = p.c.get().skill.get(0).id;
                }
            }
            if (arrSkill == null) {
                return;
            }
            Message m = new Message(-30);
            m.writer().writeByte(-65);
            m.writer().writeUTF(text);//pth
            m.writer().writeInt(lent);//size
            m.writer().write(arrSkill);//size
            m.writer().writeByte(0);//b8
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reciveImage(Player p, Message m) throws IOException {
        int id = m.reader().readInt();
        m.cleanup();
        byte[] ab;
        ByteArrayOutputStream a = loadFile("res/icon/" + p.conn.zoomLevel + "/" + id + ".png");
        if (a != null) {
            ab = a.toByteArray();
        } else {
            return;
        }
        m = new Message(-28);
        m.writer().writeByte(-115);
        m.writer().writeInt(id);
        m.writer().writeInt(ab.length);
        m.writer().write(ab);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static void reciveImageMOB(Player p, Message m) throws IOException {
        int id = m.reader().readUnsignedByte();
        m.cleanup();
        MobData mob = MobData.getMob(id);
        if (mob == null) {
            return;
        }
        util.Debug(mob.id + " Id mob " + id);
        byte[] ab;
        ByteArrayOutputStream a = loadFile("res/cache/mob/" + p.conn.zoomLevel + "/" + id);
        if (a != null) {
            ab = a.toByteArray();
        } else {
            return;
        }
        m = new Message(-28);
        
//        m.writer().writeByte(-108);
//        m.writer().writeShort(mob.id);//id
//        m.writer().writeByte(mob.typeFly);//type Fly
//        m.writer().writeByte(mob.nImg);//num image
//        for (int i = 0; i < mob.nImg; i++) {
//            byte[] ab = loadFile("res/assets/mob/"+p.conn.zoomLevel+"/"+id+"_"+i+".png").toByteArray();
//            m.writer().writeInt(ab.length);
//            m.writer().write(ab);
//        }

        m.writer().write(ab);
//        m.writer().writeBoolean((mob.flag==1));//flag
//        if (mob.flag == 1) {
//            m.writer().writeByte(mob.frameBossMove.length);
//            for (int i = 0; i < mob.frameBossMove.length; i++)
//                m.writer().writeByte(mob.frameBossMove[i]);
//            m.writer().writeByte(mob.frameBossAttack.length);
//            for (byte[] frameBossAttack : mob.frameBossAttack) {
//                m.writer().writeByte(frameBossAttack.length);
//                for (int j = 0; j < frameBossAttack.length; j++) {
//                    m.writer().writeByte(frameBossAttack[j]);
//                }
//            }
//        }
//        m.writer().writeInt(mob.info);//imginfo
//        if (mob.info > 0) {
//            m.writer().writeByte(mob.imginfo3.length);
//            for (imgInfo img : mob.imginfo3) {
//                m.writer().writeByte(img.id);
//                m.writer().writeByte(img.x0);
//                m.writer().writeByte(img.y0);
//                m.writer().writeByte(img.w);
//                m.writer().writeByte(img.h);
//            }
//            m.writer().writeShort(mob.frameBoss.length);
//            for (FrameBoss frameBos : mob.frameBoss) {
//                m.writer().writeByte(frameBos.dx.length);
//                for(int i = 0; i < frameBos.dx.length; i++) {
//                    m.writer().writeShort(frameBos.dx[i]);
//                    m.writer().writeShort(frameBos.dy[i]);
//                    m.writer().writeByte(frameBos.idImg[i]);
//                }
//            }
//            m.writer().writeShort(0);//null
//        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static ByteArrayOutputStream loadFile(String url) {
        FileInputStream openFileInput;
        try {
            openFileInput = new FileInputStream(url);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[1024];
            while (true) {
                int read = openFileInput.read(bArr);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            openFileInput.close();
            return byteArrayOutputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveFile(String url, byte[] data) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ItemInfo(Player p, Message m) throws IOException {
        byte type = m.reader().readByte();
        m.cleanup();
        util.Debug("Item info type " + type);
        Item[] arrItem = null;
        switch (type) {
            case 2:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 4:
                arrItem = p.c.ItemBox;
                break;
            case 6:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 7:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 8:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 9:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 14:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 15:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 16:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 17:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 18:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 19:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 20:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 21:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 22:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 23:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 24:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 25:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 26:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 27:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 28:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 29:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 32:
                arrItem = ItemSell.SellItemType(type).item;
                break;
            case 34:
                arrItem = ItemSell.SellItemType(type).item;
                break;
        }
        if (arrItem == null) {
            return;
        }
        if (type == 4) {
            m = new Message(31);
            m.writer().writeInt(p.c.xuBox);//xu
            m.writer().writeByte(arrItem.length);//lent
            for (Item item : arrItem) {
                if (item != null) {
                    m.writer().writeShort(item.id);//id
                    m.writer().writeBoolean(item.isLock);//is lock
                    if (ItemData.isTypeBody(item.id) || ItemData.isTypeNgocKham(item.id)) {
                        m.writer().writeByte(item.upgrade);//id
                    }
                    m.writer().writeBoolean(item.isExpires);
                    m.writer().writeShort(item.quantity);
                } else {
                    m.writer().writeShort(-1);
                }
            }
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } else {
            m = new Message(33);
            m.writer().writeByte(type);//type
            m.writer().writeByte(arrItem.length);//lent
            for (int i = 0; i < arrItem.length; i++) {
                m.writer().writeByte(i);//index
                m.writer().writeShort(arrItem[i].id);//id
            }
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        }
    }

    public static void buyItemStore(Player p, Message m) throws IOException {
        if (p.c.isNhanban) {
            p.conn.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        byte type = m.reader().readByte();
        byte index = m.reader().readByte();
        short num = 1;
        if (m.reader().available() > 0) {
            num = m.reader().readShort();
        }
        m.cleanup();
        Item sell = ItemSell.getItemTypeIndex(type, index);
        if (num <= 0 || sell == null) {
            return;
        }
        int buycoin = sell.buyCoin * num;
        int buycoinlock = sell.buyCoinLock * num;
        int buycoingold = sell.buyGold * num;
        if (buycoin < 0 || buycoinlock < 0 || buycoingold < 0) {
            return;
        }
        ItemData data = ItemData.ItemDataId(sell.id);
        if (type == 34 && num > 0) {
            ClanManager clan = ClanManager.getClanName(p.c.clan.clanName);
            if (clan == null) {
                p.conn.sendMessageLog("Bạn cần có gia tộc");
            } else if (p.c.clan.typeclan < 3) {
                p.conn.sendMessageLog("Chỉ có tộc trưởng hoặc tôc phó mới được phép mua");
            } else if ((sell.id == 423 && clan.itemLevel < 1) || (sell.id == 424 && clan.itemLevel < 2) || (sell.id == 425 && clan.itemLevel < 3) || (sell.id == 426 && clan.itemLevel < 4) || (sell.id == 427 && clan.itemLevel < 5)) {
                p.conn.sendMessageLog("Cần khai mở gia tộc để mua vật phẩm này");
            } else {
                if (buycoin > clan.coin) {
                    p.conn.sendMessageLog("Ngân sách gia tộc không đủ");
                    return;
                }
                if (sell.id >= 423 && sell.id <= 427) {
                    Item item = sell.clone();
                    item.quantity = num;
                    for (short i = 0; i < item.options.size(); i++) {
                        item.options.get(i).param = util.nextInt(item.getOptionShopMin(item.options.get(i).id, item.options.get(i).param), item.options.get(i).param);
                    }
                    clan.addItem(item);
                    clan.updateCoin(-buycoin);
                    m = new Message(13);
                    m.writer().writeInt(p.c.xu);//xu
                    m.writer().writeInt(p.c.yen);//yen
                    m.writer().writeInt(p.luong);//luong
                    m.writer().flush();
                    p.conn.sendMessage(m);
                    m.cleanup();
                    m = new Message(-24);
                    m.writer().writeUTF("Gia tộc nhận được " + data.name);
                    m.writer().flush();
                    clan.sendMessage(m);
                    m.cleanup();
                } else {
                    p.conn.sendMessageLog("Chưa hỗ trợ");
                }
            }
        } else {
            if ((!data.isUpToUp && p.c.getBagNull() >= num) || (data.isUpToUp && p.c.getIndexBagid(sell.id, sell.isLock) != -1) || (data.isUpToUp && p.c.getBagNull() > 0)) {
                if (p.c.xu < buycoin) {
                    p.conn.sendMessageLog("Không đủ xu");
                    return;
                }
                if (p.c.yen < buycoinlock) {
                    p.conn.sendMessageLog("Không đủ yên");
                    return;
                }
                if (p.luong < buycoingold) {
                    p.conn.sendMessageLog("Không đủ lượng");
                    return;
                }
                p.c.upxu(-buycoin);
                p.c.upyen(-buycoinlock);
                p.upluong(-buycoingold);
                for (int i = 0; i < num; i++) {
                    Item item = new Item();
                    item.id = sell.id;
                    if (sell.isLock) {
                        item.isLock = true;
                    }
                    item.sys = sell.sys;
                    if (sell.isExpires) {
                        item.isExpires = true;
                        item.expires = util.TimeMillis(sell.expires);
                    }
                    item.saleCoinLock = sell.saleCoinLock;
                    for (Option Option : sell.options) {
                        int idOp = Option.id;
                        int par = util.nextInt(item.getOptionShopMin(idOp, Option.param), Option.param);
                        Option option = new Option(idOp, par);
                        item.options.add(option);
                    }
                    if (data.isUpToUp) {
                        item.quantity = num;
                        p.c.addItemBag(true, item);
                        break;
                    } else {
                        p.c.addItemBag(false, item);
                    }
                }
                m = new Message(13);
                m.writer().writeInt(p.c.xu);//xu
                m.writer().writeInt(p.c.yen);//yen
                m.writer().writeInt(p.luong);//luong
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
            } else {
                p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
            }
        }
    }

    public static void doConvertUpgrade(Player p, Message m) throws IOException {
        byte index1 = m.reader().readByte();
        byte index2 = m.reader().readByte();
        byte index3 = m.reader().readByte();
        m.cleanup();
        Item item1 = p.c.getIndexBag(index1);
        Item item2 = p.c.getIndexBag(index2);
        Item item3 = p.c.getIndexBag(index3);
        if (item1 != null && item2 != null && item3 != null) {
            if (!ItemData.isTypeBody(item1.id) || !ItemData.isTypeBody(item2.id) || (item3.id != 269 && item3.id != 270 && item3.id != 271)) {
                p.conn.sendMessageLog("Chỉ chọn trang bị và Chuyển hóa");
                return;
            }
            ItemData data1 = ItemData.ItemDataId(item1.id);
            ItemData data2 = ItemData.ItemDataId(item2.id);
            if (item1.upgrade == 0 || item2.upgrade > 0 || (item3.id == 269 && item1.upgrade > 10) || (item3.id == 270 && item1.upgrade > 13)) {
                p.conn.sendMessageLog("Vật phẩm chuyển hóa không hợp lệ");
                return;
            }
            if (data1.level > data2.level || data1.type != data2.type) {
                p.conn.sendMessageLog("Chỉ được chuyển hóa trang bị cùng loại và cùng cấp trở lên");
                return;
            }
            item1.isLock = true;
            item2.isLock = true;
            byte upgrade = item1.upgrade;
            item1.upgradeNext((byte) -item1.upgrade);
            item2.upgradeNext(upgrade);
            m = new Message(-28);
            m.writer().writeByte(-88);
            m.writer().writeByte(index1);
            m.writer().writeByte(item1.upgrade);
            m.writer().writeByte(index2);
            m.writer().writeByte(item2.upgrade);
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
            p.c.removeItemBag(index3, 1);
        }
    }

    public static void crystalCollect(Player p, Message m, boolean isCoin) throws IOException {
        if (p.c.isNhanban) {
            p.conn.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        if (m.reader().available() > 28) {
            util.Debug("Lơn hơn 28");
            return;
        }
        if (p.c.getBagNull() == 0) {
            p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        int crys = 0;
        byte[] arrItem = new byte[m.reader().available()];
        for (byte i = 0; i < arrItem.length; i++) {
            arrItem[i] = -1;
            byte index = m.reader().readByte();
            Item item = p.c.getIndexBag(index);
            if (item == null) {
                continue;
            }
            ItemData data = ItemData.ItemDataId(item.id);
            if (data.type == 26 && item.id < 12) {
                arrItem[i] = index;
                crys += crystals[item.id];
            } else {
                p.conn.sendMessageLog("Chỉ có thể dùng đá dưới 12 để nâng cấp");
                return;
            }
        }
        short id = 0;
        if (id > 11) {
            id = 11;
        }
        for (byte i = 0; i < crystals.length; i++) {
            if (crys > crystals[i]) {
                id = (short) (i + 1);
            }
        }
        int percen = crys * 100 / crystals[id];
        if (percen < 45) {
            p.conn.sendMessageLog("Tỷ lệ phải từ 45% trở lên");
            return;
        }
        if (isCoin) {
            if (coinUpCrystals[id] > p.c.xu) {
                return;
            }
            p.c.upxu(-coinUpCrystals[id]);
        } else {
            if (coinUpCrystals[id] > p.c.xu + p.c.yen) {
                return;
            }
            if (p.c.yen >= coinUpCrystals[id]) {
                p.c.upyen(-coinUpCrystals[id]);
            } else {
                int coin = coinUpCrystals[id] - p.c.yen;
                p.c.upyen(-p.c.yen);
                p.c.upxu(-coin);
            }
        }
        boolean suc = false;
        Item item = new Item();
        if (util.nextInt(1, 100) <= percen) {
            suc = true;
            item.id = id;
        } else {
            item.id = (short) (id - 1);
        }
        item.isLock = true;
        byte index = p.c.getIndexBagNotItem();
        p.c.ItemBag[index] = item;
        for (byte i = 0; i < arrItem.length; i++) {
            if (arrItem[i] != -1) {
                p.c.ItemBag[arrItem[i]] = null;
            }
        }
        m = new Message(isCoin ? 19 : 20);
        m.writer().writeByte(suc ? 1 : 0);
        m.writer().writeByte(index);
        m.writer().writeShort(item.id);
        m.writer().writeBoolean(item.isLock);
        m.writer().writeBoolean(item.isExpires);
        if (isCoin) {
            m.writer().writeInt(p.c.xu);
        } else {
            m.writer().writeInt(p.c.yen);
            m.writer().writeInt(p.c.xu);
        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static void UpGrade(Player p, Message m) throws IOException {
        if (p.c.isNhanban) {
            p.conn.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        byte type = m.reader().readByte();
        byte index = m.reader().readByte();
        Item item = p.c.getIndexBag(index);
        if (item == null || m.reader().available() > 18) {
            return;
        }
        if (item.upgrade >= item.getUpMax()) {
            p.conn.sendMessageLog("Trang bị đã đạt cấp tối đa");
            return;
        }
        byte[] arrItem = new byte[m.reader().available()];
        int crys = 0;
        boolean keep = false;
        boolean da = false;
        for (byte i = 0; i < arrItem.length; i++) {
            arrItem[i] = -1;
            byte index2 = m.reader().readByte();
            Item item2 = p.c.getIndexBag(index2);
            if (item2 == null) {
                continue;
            }
            ItemData data = ItemData.ItemDataId(item2.id);
            if (data.type == 26) {
                arrItem[i] = index2;
                crys += crystals[item2.id];
                da = true;
            } else if (data.type == 28) {
                arrItem[i] = index2;
                if (item2.id == 242 && item.upgrade < 8) {
                    keep = true;
                } else if (item2.id == 284 && item.upgrade < 12) {
                    keep = true;
                } else if (item2.id == 285 && item.upgrade < 14) {
                    keep = true;
                } else if (item2.id == 475) {
                    keep = true;
                } else {
                    p.conn.sendMessageLog("Bảo hiểm không hợp lệ");
                    return;
                }
            } else {
                p.conn.sendMessageLog("Chỉ có thể chọn đá và bảo hiểm");
                return;
            }
        }
        ItemData data = ItemData.ItemDataId(item.id);
        int coins;
        int gold = 0;
        int percen;
        if (arrItem.length == 0 || data.type > 10) {
            return;
        }
        if (!da) {
            p.conn.sendMessageLog("Hãy chọn thêm đá");
            return;
        }
        if (data.type == 1) {
            coins = coinUpWeapons[item.upgrade];
            percen = crys * 100 / upWeapon[item.upgrade];
            if (percen > maxPercents[item.upgrade]) {
                percen = maxPercents[item.upgrade];
            }
        } else if (data.type % 2 == 0) {
            coins = coinUpClothes[item.upgrade];
            percen = crys * 100 / upClothe[item.upgrade];
            if (percen > maxPercents[item.upgrade]) {
                percen = maxPercents[item.upgrade];
            }
        } else {
            coins = coinUpAdorns[item.upgrade];
            percen = crys * 100 / upAdorn[item.upgrade];
            if (percen > maxPercents[item.upgrade]) {
                percen = maxPercents[item.upgrade];
            }
        }
        if (type == 1) {
            percen = (int) ((double) percen * 1.5);
            gold = GameScr.goldUps[item.upgrade];
        }
        if (coins > p.c.yen + p.c.xu || gold > p.luong) {
            return;
        }
        for (byte i = 0; i < arrItem.length; i++) {
            if (arrItem[i] != -1) {
                p.c.ItemBag[arrItem[i]] = null;
            }
        }
        p.upluong(-gold);
        if (coins <= p.c.yen) {
            p.c.upyen(-coins);
        } //
        else if (coins >= p.c.yen) {
            int coin = coins - p.c.yen;
            p.c.upyen(-p.c.yen);
            p.c.upxu(-coin);
        }
        boolean suc = util.nextInt(1, 100) <= percen;
        m.cleanup();
        item.isLock = true;
        util.Debug("type " + type + " index " + index + " percen " + percen);
        if (suc) {
            item.upgradeNext((byte) 1);
        } else if (!keep) {
            item.upgradeNext((byte) -(item.upgrade - KeepUpgrade(item.upgrade)));
        }
        m = new Message(21);
        m.writer().writeByte(suc ? 1 : 0);
        m.writer().writeInt(p.luong);
        m.writer().writeInt(p.c.xu);
        m.writer().writeInt(p.c.yen);
        m.writer().writeByte(item.upgrade);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static void Split(Player p, Message m) throws IOException {
        if (p.c.isNhanban) {
            p.conn.sendMessageLog("Bạn đang trong chế độ thứ thân không thể dùng được chức năng này");
            return;
        }
        byte index = m.reader().readByte();
        Item item = p.c.getIndexBag(index);
        if (item == null || item.upgrade <= 0) {
            return;
        }
        ItemData data = ItemData.ItemDataId(item.id);
        if (data.type > 10) {
            return;
        }
        int num = 0;
        if (data.type == 1) {
            for (byte i = 0; i < item.upgrade; i++) {
                num += GameScr.upWeapon[i];
            }
        } else if (data.type % 2 == 0) {
            for (byte i = 0; i < item.upgrade; i++) {
                num += GameScr.upClothe[i];
            }
        } else {
            for (byte i = 0; i < item.upgrade; i++) {
                num += GameScr.upAdorn[i];
            }
        }
        num /= 2;
        int num2 = 0;
        Item[] arrItem = new Item[24];
        for (int n = GameScr.crystals.length - 1; n >= 0; n--) {
            if (num >= GameScr.crystals[n]) {
                arrItem[num2] = new Item();
                arrItem[num2].id = ((short) n);
                arrItem[num2].isLock = item.isLock;
                num -= GameScr.crystals[n];
                n++;
                num2++;
            }
        }
        if (num2 > p.c.getBagNull()) {
            p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        byte[] arrIndex = new byte[arrItem.length];
        for (byte i = 0; i < arrItem.length; i++) {
            if (arrItem[i] == null) {
                continue;
            }
            byte index2 = p.c.getIndexBagNotItem();
            p.c.ItemBag[index2] = arrItem[i];
            arrIndex[i] = index2;
        }
        item.upgradeNext((byte) -item.upgrade);
        m = new Message(22);
        m.writer().writeByte(num2);
        for (byte i = 0; i < num2; i++) {
            if (arrItem[i] == null) {
                continue;
            }
            m.writer().writeByte(arrIndex[i]);
            m.writer().writeShort(arrItem[i].id);
        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static void LuckValue(Player p, Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        if (index < 0 || index > 8) {
            index = 0;
        }
        if (p.c.getBagNull() == 0) {
            p.conn.sendMessageLog("Hành trang không đủ chỗ trống.");
            return;
        } else if (p.c.quantityItemyTotal(340) == 0) {
            p.conn.sendMessageLog("Cần có phiếu may mắn.");
            return;
        }
        p.c.removeItemBags(340, 1);
        short id = ArridLuck[util.nextInt(ArridLuck.length)];
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
        if (id == 523 || id == 419) {
            item.isExpires = true;
            item.expires = util.TimeDay(ArrdayLuck[util.nextInt(ArrdayLuck.length)]);
        }
        if (data.type != 19) {
            p.c.addItemBag(true, item);
        } else {
            item.quantity = ArryenLuck[util.nextInt(ArryenLuck.length)];
            p.c.upyenMessage(item.quantity);
            p.sendAddchatYellow("Bạn nhận được " + item.quantity + " Yên");
        }
        if (item.quantity >= 500000 || id == 8 || id == 9 || id == 11 || id == 343 || id == 344 || id == 345 || id == 346 || id == 403 || id == 404 || id == 405 || id == 406 || id == 407 || id == 408 || id == 419) {
            server.manager.chatKTG(p.c.name + " tham gia thẻ bài bí mật nhận được" + ((item.quantity > 1) ? " " + item.quantity + " " : " ") + data.name);
        }
        m = new Message(-28);
        m.writer().writeByte(-72);
        for (byte i = 0; i < 9; i++) {
            if (i == index) {
                m.writer().writeShort(id);
            } else {
                m.writer().writeShort(ArridLuck[util.nextInt(ArridLuck.length)]);
            }
        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public static void TinhLuyen(Player p, Message m) throws IOException {
        byte index = m.reader().readByte();
        Item it = p.c.getIndexBag(index);
        if (it == null) {
            return;
        }
        ItemData data = ItemData.ItemDataId(it.id);
        int tl = -1;
        for (byte i = 0; i < it.options.size(); i++) {
            if (it.options.get(i).id == 85) {
                tl = it.options.get(i).param;
                if (tl >= 9) {
                    p.conn.sendMessageLog("Vật phẩm đã được tinh luyên tối đa");
                    return;
                }
            }
        }
        if (tl == -1) {
            p.conn.sendMessageLog("Vật phẩm không dùng cho tinh luyện");
            return;
        }
        int ttts = 0;
        int tttt = 0;
        int tttc = 0;
        byte[] arit = new byte[m.reader().available()];
        for (byte i = 0; i < arit.length; i++) {
            byte ind = m.reader().readByte();
            Item item = p.c.getIndexBag(ind);
            if (item == null) {
                return;
            }
            if (item.id == 455 || item.id == 456 || item.id == 457) {
                arit[i] = ind;
                if (item.id == 455) {
                    ttts++;
                } else if (item.id == 456) {
                    tttt++;
                } else if (item.id == 457) {
                    tttc++;
                }
            } else {
                p.conn.sendMessageLog("Vật phẩm không dùng cho tinh luyện");
                return;
            }
        }
        int percent = 0;
        int yen = 0;
        switch (tl) {
            case 0:
                percent = 60;
                yen = 150000;
                if (ttts != 3 || tttt != 0 || tttc != 0) {
                    p.conn.sendMessageLog("Tinh luyện 1 cần dùng 3 Tử tinh thạch sơ");
                    return;
                }
                break;
            case 1:
                percent = 45;
                yen = 247500;
                if (ttts != 5 || tttt != 0 || tttc != 0) {
                    p.conn.sendMessageLog("Tinh luyện 2 cần dùng 5 Tử tinh thạch sơ");
                    return;
                }
                break;
            case 2:
                percent = 34;
                yen = 408375;
                if (ttts != 9 || tttt != 0 || tttc != 0) {
                    p.conn.sendMessageLog("Tinh luyện 3 cần dùng 9 Tử tinh thạch sơ");
                    return;
                }
                break;
            case 3:
                percent = 26;
                yen = 673819;
                if (ttts != 0 || tttt != 4 || tttc != 0) {
                    p.conn.sendMessageLog("Tinh luyện 4 cần dùng 4 Tử tinh thạch trung");
                    return;
                }
                break;
            case 4:
                percent = 20;
                yen = 1111801;
                if (ttts != 0 || tttt != 7 || tttc != 0) {
                    p.conn.sendMessageLog("Tinh luyện 5 cần dùng 7 Tử tinh thạch trung");
                    return;
                }
                break;
            case 5:
                percent = 15;
                yen = 2056832;
                if (ttts != 0 || tttt != 10 || tttc != 0) {
                    p.conn.sendMessageLog("Tinh luyện 6 cần dùng 10 Tử tinh thạch trung");
                    return;
                }
                break;
            case 6:
                percent = 11;
                yen = 4010922;
                if (ttts != 0 || tttt != 0 || tttc != 5) {
                    p.conn.sendMessageLog("Tinh luyện 7 cần dùng 5 Tử tinh thạch cao");
                    return;
                }
                break;
            case 7:
                percent = 8;
                yen = 7420021;
                if (ttts != 0 || tttt != 0 || tttc != 7) {
                    p.conn.sendMessageLog("Tinh luyện 8 cần dùng 7 Tử tinh thạch cao");
                    return;
                }
                break;
            case 8:
                percent = 6;
                yen = 12243035;
                if (ttts != 0 || tttt != 0 || tttc != 9) {
                    p.conn.sendMessageLog("Tinh luyện 9 cần dùng 9 Tử tinh thạch cao");
                    return;
                }
                break;
        }
        if (yen > p.c.yen) {
            p.conn.sendMessageLog("Không đủ yên tinh luyện");
            return;
        }
        p.endLoad(true);
        p.c.upyenMessage(-yen);
        if (percent >= util.nextInt(100)) {
            for (byte i = 0; i < it.options.size(); i++) {
                it.options.get(i).param += ItemData.ThinhLuyenParam(it.options.get(i).id, tl);
            }
            p.requestItemInfoMessage(it, index, 3);
            p.sendAddchatYellow("Tinh luyện thành công!");
        } else {

            p.sendAddchatYellow("Tinh luyện thất bại!");
        }
        for (byte i = 0; i < arit.length; i++) {
            p.c.removeItemBag(arit[i], 1);
        }
    }

    public static void DichChuyen(Player p, Message m) throws IOException {
        byte index = m.reader().readByte();
        Item item = p.c.getIndexBag(index);
        if (item != null && ItemData.isTypeBody(item.id) && item.upgrade > 11) {
            for (byte i = 0; i < item.options.size(); i++) {
                if (item.options.get(i).id == 85) {
                    p.conn.sendMessageLog("Vật phẩm đã được dịch chuyển");
                    return;
                }
            }
            byte[] arrIndex = new byte[20];
            for (byte i = 0; i < arrIndex.length; i++) {
                byte index2 = m.reader().readByte();
                Item item2 = p.c.getIndexBag(index2);
                if (item2 != null && item2.id == 454) {
                    arrIndex[i] = index2;
                } else {
                    return;
                }
            }
            p.endLoad(true);
            ItemData data = ItemData.ItemDataId(item.id);
            item.options.add(new Option(85, 0));
            switch (data.type) {
                case 0: {
                    if (item.sys == 1) {
                        item.options.add(new Option(96, 10));
                    } else if (item.sys == 2) {
                        item.options.add(new Option(95, 10));
                    } else if (item.sys == 3) {
                        item.options.add(new Option(97, 10));
                    }
                    item.options.add(new Option(79, 5));
                    break;
                }
                case 1: {
                    item.options.add(new Option(87, util.nextInt(800, 1200)));
                    item.options.add(new Option(87 + item.sys, util.nextInt(500, 800)));
                    break;
                }
                case 2: {
                    item.options.add(new Option(80, 50));
                    item.options.add(new Option(91, 10));
                    break;
                }
                case 3: {
                    item.options.add(new Option(81, 5));
                    item.options.add(new Option(79, 5));
                    break;
                }
                case 4: {
                    item.options.add(new Option(86, 120));
                    item.options.add(new Option(94, util.nextInt(90, 150)));
                    break;
                }
                case 5: {
                    if (item.sys == 1) {
                        item.options.add(new Option(96, 5));
                    } else if (item.sys == 2) {
                        item.options.add(new Option(95, 5));
                    } else if (item.sys == 3) {
                        item.options.add(new Option(97, 5));
                    }
                    item.options.add(new Option(92, 10));
                    break;
                }
                case 6: {
                    item.options.add(new Option(83, util.nextInt(600, 800)));
                    item.options.add(new Option(82, util.nextInt(600, 800)));
                    break;
                }
                case 7: {
                    if (item.sys == 1) {
                        item.options.add(new Option(96, 5));
                    } else if (item.sys == 2) {
                        item.options.add(new Option(95, 5));
                    } else if (item.sys == 3) {
                        item.options.add(new Option(97, 5));
                    }
                    item.options.add(new Option(87 + item.sys, util.nextInt(500, 700)));
                    break;
                }
                case 8: {
                    item.options.add(new Option(82, util.nextInt(600, 800)));
                    item.options.add(new Option(84, util.nextInt(90, 100)));
                    break;
                }
                case 9: {
                    item.options.add(new Option(84, util.nextInt(100, 130)));
                    item.options.add(new Option(83, util.nextInt(600, 800)));
                    break;
                }
            }
            for (byte i = 0; i < arrIndex.length; i++) {
                p.c.removeItemBag(arrIndex[i], 1);
            }
            p.sendAddchatYellow("Đã dịch chuyển trang bị");
            p.requestItemInfoMessage(item, index, 3);
        }
        util.Debug(index + " " + item.id);
    }

    public static void LuyenThach(Player p, Message m) throws IOException {
        byte[] arrItem = new byte[m.reader().available()];

        Item item = null;
        int checkTTS = 0;
        int checkTTT = 0;

        p.endLoad(true);

        if (arrItem.length == 4) {
            for (byte i = 0; i < arrItem.length; i++) {
                byte index2 = m.reader().readByte();
                item = p.c.getIndexBag(index2);
                if (item.id == 455) {
                    checkTTS++;
                    checkTTT = 0;
                } else if (item.id == 456) {
                    checkTTT++;
                    checkTTS = 0;
                }
                p.c.removeItemBag(index2, 1);
            }
            if (checkTTS > 0) {
                p.c.addItemBag(false, ItemData.itemDefault(456));
            } else if (checkTTT > 0) {
                p.c.addItemBag(false, ItemData.itemDefault(457));
            }
            return;

        } else if (arrItem.length == 9) {
            for (byte i = 0; i < arrItem.length; i++) {
                byte index2 = m.reader().readByte();
                if (i == 0) {
                    item = p.c.getIndexBag(index2);
                }
                p.c.removeItemBag(index2, 1);
            }

            if (item.id == 455) {
                p.c.addItemBag(false, ItemData.itemDefault(456));
            } else if (item.id == 456) {
                p.c.addItemBag(false, ItemData.itemDefault(457));
            }
            return;
        }

    }

    public static void luyenNgoc(Player p, Message m) throws IOException {
        byte index = m.reader().readByte();
        switch ((int) index) {
            case 0:
                byte indexItem = m.reader().readByte();
                Item item = p.c.getIndexBag(indexItem);
                int exp = 0;
                if (item != null) {
                    ItemData data = ItemData.ItemDataId(item.id);
                    int bach = data.type;
                    byte[] arrItem = new byte[m.reader().available()];
                    byte indexItemSub = m.reader().readByte();
                    arrItem[0] = indexItemSub;
                    Item itemSub = p.c.getIndexBag(indexItemSub);
                    if (checkTonTaiNgoc(itemSub, item)) {
                        p.conn.sendMessageLog("Không thể khảm cùng 1 loại ngọc trên 1 vật phẩm");
                        return;
                    }
                    if (p.c.yen < 50000000) {
                        p.conn.sendMessageLog("Yên đâu");
                        return;
                    }
                    int loai = 0;
                    int loai1 = 0;
                    int loai2 = 0;
                    int indextemp = 0;
                    int yenThaoNgoc = 0;
                    int isKhamNgoc = 0;
                    for (byte i = 1; i < arrItem.length; i++) {
                        arrItem[i] = m.reader().readByte();
                    }
                    for (byte i = 0; i < arrItem.length; i++) {
                        p.c.removeItemBag(arrItem[i], 1);
                    }
                    for (int i = 0; i < item.options.size(); i++) {
                        if (item.options.get(i).id == 122) {
                            yenThaoNgoc = item.options.get(i).param;
                            item.options.remove(i);
                            isKhamNgoc++;
                        }
                    }
                    switch (data.type) {
                        case 0:
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        case 1:
                            loai = 106;
                            loai1 = 107;
                            loai2 = 108;
                            break;
                        case 2:
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        case 3:
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                        case 4:
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        case 5:
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                        case 6:
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        case 7:
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                        case 8:
                            loai = 107;
                            loai1 = 106;
                            loai2 = 108;
                            break;
                        case 9:
                            loai = 108;
                            loai1 = 106;
                            loai2 = 107;
                            break;
                    }
                    switch ((int) itemSub.id) {
                        case 655:
                            indextemp = 0;
                            int[] temp = new int[]{106, 107, 108};
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp[j]) {
                                            item.options.add(new Option(112, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp[j]) {
                                            item.options.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 2, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 3, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp[j] == 107) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp[j]) {
                                            item.options.add(new Option(112, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp[j]) {
                                            item.options.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 5, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 6, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp[j] == 108) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp[j]) {
                                            item.options.add(new Option(112, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp[j]) {
                                            item.options.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 8, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 9, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.options.size(); i++) {
                                if (itemSub.options.get(i).id == 104) {
                                    item.options.add(new Option(indextemp - 10, itemSub.options.get(i).param));
                                } else if (itemSub.options.get(i).id == 123) {
                                    item.options.add(new Option(indextemp - 11, itemSub.options.get(i).param));
                                    yenThaoNgoc += itemSub.options.get(i).param * 2;
                                }
                            }
                            item.options.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.options.add(new Option(indextemp - 13, itemSub.saleCoinLock));
                            break;
                        case 654:
                            indextemp = -15;
                            int[] temp1 = new int[]{106, 107, 108};
                            for (int j = 0; j < temp1.length; j++) {
                                if (temp1[j] == 106) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp1[j]) {
                                            item.options.add(new Option(111, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp1[j]) {
                                            item.options.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 2, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 3, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp1[j] == 107) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp1[j]) {
                                            item.options.add(new Option(111, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp1[j]) {
                                            item.options.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 5, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 6, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp1[j] == 108) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp1[j]) {
                                            item.options.add(new Option(111, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp1[j]) {
                                            item.options.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 8, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 9, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.options.size(); i++) {
                                if (itemSub.options.get(i).id == 104) {
                                    item.options.add(new Option(indextemp - 10, itemSub.options.get(i).param));
                                } else if (itemSub.options.get(i).id == 123) {
                                    item.options.add(new Option(indextemp - 11, itemSub.options.get(i).param));
                                    yenThaoNgoc += itemSub.options.get(i).param * 2;
                                }
                            }
                            item.options.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.options.add(new Option(indextemp - 13, itemSub.saleCoinLock));
                            break;
                        case 653:
                            indextemp = -30;
                            int[] temp2 = new int[]{106, 107, 108};
                            for (int j = 0; j < temp2.length; j++) {
                                if (temp2[j] == 106) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp2[j]) {
                                            item.options.add(new Option(110, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp2[j]) {
                                            item.options.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 2, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 3, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp2[j] == 107) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp2[j]) {
                                            item.options.add(new Option(110, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp2[j]) {
                                            item.options.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 5, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 6, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp2[j] == 108) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp2[j]) {
                                            item.options.add(new Option(110, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp2[j]) {
                                            item.options.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 8, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 9, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.options.size(); i++) {
                                if (itemSub.options.get(i).id == 104) {
                                    item.options.add(new Option(indextemp - 10, itemSub.options.get(i).param));
                                } else if (itemSub.options.get(i).id == 123) {
                                    item.options.add(new Option(indextemp - 11, itemSub.options.get(i).param));
                                    yenThaoNgoc += itemSub.options.get(i).param * 2;
                                }
                            }
                            item.options.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.options.add(new Option(indextemp - 13, itemSub.saleCoinLock));
                            break;
                        case 652:
                            indextemp = -45;
                            int[] temp3 = new int[]{106, 107, 108};
                            for (int j = 0; j < temp3.length; j++) {
                                if (temp3[j] == 106) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp3[j]) {
                                            item.options.add(new Option(109, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp3[j]) {
                                            item.options.add(new Option(indextemp - 1, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 2, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 3, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp3[j] == 107) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp3[j]) {
                                            item.options.add(new Option(109, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp3[j]) {
                                            item.options.add(new Option(indextemp - 4, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 5, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 6, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                                if (temp3[j] == 108) {
                                    for (int i = 0; i < itemSub.options.size(); i++) {
                                        if (itemSub.options.get(i).id == loai && itemSub.options.get(i).id == temp3[j]) {
                                            item.options.add(new Option(109, 0));
                                            item.options.add(new Option(itemSub.options.get(i + 1).id, itemSub.options.get(i + 1).param));
                                            item.options.add(new Option(itemSub.options.get(i + 2).id, itemSub.options.get(i + 2).param));
                                        } else if (itemSub.options.get(i).id == temp3[j]) {
                                            item.options.add(new Option(indextemp - 7, Integer.parseInt(itemSub.upgrade + "" + itemSub.options.get(i).id)));
                                            item.options.add(new Option(indextemp - 8, Integer.parseInt(itemSub.options.get(i + 1).param + "" + itemSub.options.get(i + 1).id)));
                                            item.options.add(new Option(indextemp - 9, Integer.parseInt(itemSub.options.get(i + 2).param + "" + itemSub.options.get(i + 2).id)));
                                        }
                                    }
                                }
                            }

                            for (int i = 0; i < itemSub.options.size(); i++) {
                                if (itemSub.options.get(i).id == 104) {
                                    item.options.add(new Option(indextemp - 10, itemSub.options.get(i).param));
                                } else if (itemSub.options.get(i).id == 123) {
                                    item.options.add(new Option(indextemp - 11, itemSub.options.get(i).param));
                                    yenThaoNgoc += itemSub.options.get(i).param * 2;
                                }
                            }
                            item.options.add(new Option(indextemp - 12, itemSub.upgrade));
                            item.options.add(new Option(indextemp - 13, itemSub.saleCoinLock));

                            break;

                    }
                    item.options.add(new Option(122, yenThaoNgoc));
                    int money = 0;
                    for (int i = 0; i < itemSub.options.size(); i++) {
                        if (itemSub.options.get(i).id == 123) {
                            money = itemSub.options.get(i).param;
                        }
                    }

                    int type = 3;
                    int money2 = p.c.yen - money;
                    if (money <= p.c.yen) {
                        p.c.upyen(-money);
                    } //
                    else if (money >= p.c.yen) {
                        int coin = money - p.c.yen;
                        if (coin > p.c.xu) {
                            p.conn.sendMessageLog("Không đủ xu và yên nha");
                            return;
                        }
                        p.c.upyen(-p.c.yen);
                        p.c.upxu(-coin);
                    }
                    util.Debug("money2" + money2);
                    m = new Message(21);
                    m.writer().writeByte(5);
                    m.writer().writeInt(p.luong);
                    m.writer().writeInt(p.c.xu);
                    m.writer().writeInt(money2);
                    m.writer().writeByte(item.upgrade);
                    m.writer().flush();
                    p.conn.sendMessage(m);
                    m.cleanup();
                    m = new Message(42);
                    m.writer().writeByte(3);
                    m.writer().writeByte(indexItem);
                    m.writer().writeLong(item.expires);
                    if (ItemData.isTypeUIME(type)) {
                        m.writer().writeInt(item.saleCoinLock);
                    }
                    if (ItemData.isTypeUIShop(type) || ItemData.isTypeUIShopLock(type) || ItemData.isTypeMounts(type) || ItemData.isTypeUIStore(type) || ItemData.isTypeUIBook(type) || ItemData.isTypeUIFashion(type) || ItemData.isTypeUIClanShop(type)) {
                        m.writer().writeInt(item.buyCoin);
                        m.writer().writeInt(item.buyCoinLock);
                        m.writer().writeInt(item.buyGold);
                    }
                    if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
                        m.writer().writeByte(item.sys);//thuoc tinh
                        if (item.options != null) {
                            for (Option Option : item.options) {
                                if (Option.id > 0) {
                                    m.writer().writeByte(Option.id);
                                    m.writer().writeInt(Option.param);
                                }

                            }
                        }
                    }
                    m.writer().flush();
                    p.conn.sendMessage(m);
                    m.cleanup();
                }
                break;
            case 1:
                byte indexItemLuyenNgoc = m.reader().readByte();
                Item itemLuyenNgoc = p.c.getIndexBag(indexItemLuyenNgoc);
                int expLuyenNgoc = 0;
                int totalExp = 0;
                if (itemLuyenNgoc != null) {
                    if (itemLuyenNgoc.upgrade >= 10) {
                        p.conn.sendMessageLog("Ngọc đã maximum rồi em ơi!");
                        return;
                    }
                    for (byte i = 0; i < itemLuyenNgoc.options.size(); i++) {
                        if (itemLuyenNgoc.options.get(i).id == 104) {
                            expLuyenNgoc = itemLuyenNgoc.options.get(i).param;
                        }
                    }
                    byte[] arrIndex = new byte[m.reader().available()];
                    int exp2 = 0;
                    for (byte i = 0; i < arrIndex.length; i++) {
                        byte index2 = m.reader().readByte();
                        Item item2 = p.c.getIndexBag(index2);
                        if (item2 != null) {
                            exp2 += arrNgocKhamEXP[item2.upgrade][1];
                        }
                        arrIndex[i] = index2;
                    }
                    totalExp = expLuyenNgoc + exp2;
                    for (byte i = 0; i < arrIndex.length; i++) {
                        p.c.removeItemBag(arrIndex[i], 1);
                    }
                }
                int upgrade = itemLuyenNgoc.upgrade;
                int isupgrade = 0;
                int chenhlech = 0;
                for (byte i = 1; i < arrLuyenNgocEXP.length; i++) {
                    if (totalExp > arrLuyenNgocEXP[i][1] && totalExp < arrLuyenNgocEXP[i][0]) {
                        if (upgrade < i) {
                            chenhlech = i - upgrade;
                            upgrade = i;
                            isupgrade++;

                            totalExp = totalExp - arrLuyenNgocEXP[i][1];
                        } else {
                            totalExp = totalExp;
                        }

                        break;
                    }

                }

                itemLuyenNgoc.upgrade = (byte) upgrade;
                int type = 3;
                m = new Message(124);
                m.writer().writeByte(1);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.c.xu);
                m.writer().writeInt(p.c.yen);
                m.writer().writeByte(upgrade);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                m = new Message(42);
                m.writer().writeByte(3);
                m.writer().writeByte(indexItemLuyenNgoc);
                m.writer().writeLong(itemLuyenNgoc.expires);
                if (ItemData.isTypeUIME(type)) {
                    m.writer().writeInt(itemLuyenNgoc.saleCoinLock);
                }
                if (ItemData.isTypeUIShop(type) || ItemData.isTypeUIShopLock(type) || ItemData.isTypeMounts(type) || ItemData.isTypeUIStore(type) || ItemData.isTypeUIBook(type) || ItemData.isTypeUIFashion(type) || ItemData.isTypeUIClanShop(type)) {
                    m.writer().writeInt(itemLuyenNgoc.buyCoin);
                    m.writer().writeInt(itemLuyenNgoc.buyCoinLock);
                    m.writer().writeInt(itemLuyenNgoc.buyGold);
                }
                if (ItemData.isTypeBody(itemLuyenNgoc.id) || ItemData.isTypeMounts(itemLuyenNgoc.id) || ItemData.isTypeNgocKham(itemLuyenNgoc.id)) {
                    m.writer().writeByte(itemLuyenNgoc.sys);//thuoc tinh
                    int i = 0;
                    if (itemLuyenNgoc.options != null) {
                        for (Option Option : itemLuyenNgoc.options) {
                            m.writer().writeByte(Option.id);
                            if (Option.id == 104) {
                                m.writer().writeInt(totalExp);
                                itemLuyenNgoc.options.get(i).param = totalExp;
                            } else {
                                if (isupgrade == 1) {
                                    if (Option.id == 112 || Option.id == 105 || Option.id == 117 || Option.id == 73 || Option.id == 125 || Option.id == 131) {
                                        int value = 0;
                                        if (itemLuyenNgoc.options.get(i).param > 0) {
                                            value = itemLuyenNgoc.options.get(i).param + 1000 * chenhlech;
                                        } else if (itemLuyenNgoc.options.get(i).param < 0) {
                                            value = itemLuyenNgoc.options.get(i).param - 1000 * chenhlech;
                                        }

                                        itemLuyenNgoc.options.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else if (Option.id == 123) {
                                        int value = itemLuyenNgoc.options.get(i).param * 2 * chenhlech;
                                        itemLuyenNgoc.options.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else if (Option.id == 114) {
                                        int value = itemLuyenNgoc.options.get(i).param + 10 * chenhlech;
                                        itemLuyenNgoc.options.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else if (Option.id == 115 || Option.id == 124 || Option.id == 119 || Option.id == 120 || Option.id == 116 || Option.id == 113) {
                                        int value = itemLuyenNgoc.options.get(i).param + 100 * chenhlech;
                                        itemLuyenNgoc.options.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else {
                                        m.writer().writeInt(Option.param);
                                    }
                                } else {
                                    m.writer().writeInt(Option.param);
                                }
                            }
                            i++;

                        }
                    }
                }
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                p.sendAddchatYellow("Luyện ngọc thành công");
                break;
            case 2:
                byte indexItemGotNgoc = m.reader().readByte();
                Item itemGotNgoc = p.c.getIndexBag(indexItemGotNgoc);
                int expGotNgoc = 0;

                int money2 = p.c.xu - coinGotngoc[itemGotNgoc.upgrade];
                if (coinGotngoc[itemGotNgoc.upgrade] <= p.c.xu) {
                    p.c.upxu(-coinGotngoc[itemGotNgoc.upgrade]);
                } //
                else if (coinGotngoc[itemGotNgoc.upgrade] >= p.c.xu) {
                    int coin = coinGotngoc[itemGotNgoc.upgrade] - p.c.xu;
                    if (coin > p.c.yen) {
                        p.conn.sendMessageLog("Không đủ xu và yên nha");
                        return;
                    }
                    p.c.upxu(-p.c.xu);
                    p.c.upyen(-coin);
                }

                int typeGotNgoc = 3;
                m = new Message(124);
                m.writer().writeByte(2);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.c.xu);
                m.writer().writeInt(p.c.yen);
                m.writer().writeByte(itemGotNgoc.upgrade);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                m = new Message(42);
                m.writer().writeByte(3);
                m.writer().writeByte(indexItemGotNgoc);
                m.writer().writeLong(itemGotNgoc.expires);
                if (ItemData.isTypeUIME(typeGotNgoc)) {
                    m.writer().writeInt(itemGotNgoc.saleCoinLock);
                }
                if (ItemData.isTypeUIShop(typeGotNgoc) || ItemData.isTypeUIShopLock(typeGotNgoc) || ItemData.isTypeMounts(typeGotNgoc) || ItemData.isTypeUIStore(typeGotNgoc) || ItemData.isTypeUIBook(typeGotNgoc) || ItemData.isTypeUIFashion(typeGotNgoc) || ItemData.isTypeUIClanShop(typeGotNgoc)) {
                    m.writer().writeInt(itemGotNgoc.buyCoin);
                    m.writer().writeInt(itemGotNgoc.buyCoinLock);
                    m.writer().writeInt(itemGotNgoc.buyGold);
                }
                if (ItemData.isTypeBody(itemGotNgoc.id) || ItemData.isTypeMounts(itemGotNgoc.id) || ItemData.isTypeNgocKham(itemGotNgoc.id)) {
                    m.writer().writeByte(itemGotNgoc.sys);//thuoc tinh
                    int i = 0;
                    if (itemGotNgoc.options != null) {
                        for (Option Option : itemGotNgoc.options) {
                            m.writer().writeByte(Option.id);
                            if (Option.id == 104) {
                                m.writer().writeInt(Option.param);
                            } else {
                                if (Option.id == 112 || Option.id == 115 || Option.id == 126 || Option.id == 105 || Option.id == 117 || Option.id == 114 || Option.id == 73 || Option.id == 80 || Option.id == 119 || Option.id == 124 || Option.id == 125 || Option.id == 131 || Option.id == 116 || Option.id == 81) {
                                    if (itemGotNgoc.options.get(i).param < 0) {
                                        int value = 0;
                                        if (itemGotNgoc.options.get(i).param < -200) {
                                            value = itemGotNgoc.options.get(i).param + 200;
                                        } else {
                                            value = -1;
                                        }

                                        itemGotNgoc.options.get(i).param = value;
                                        m.writer().writeInt(value);
                                    } else {
                                        m.writer().writeInt(Option.param);
                                    }

                                } else {
                                    m.writer().writeInt(Option.param);
                                }

                            }
                            i++;

                        }
                    }
                }
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                p.conn.sendMessageLog("Ngọc đã được gọt");
                break;
            case 3:
                byte indexItemThaoNgoc = m.reader().readByte();
                Item itemThaoNgoc = p.c.getIndexBag(indexItemThaoNgoc);
                ItemData data = ItemData.ItemDataId(itemThaoNgoc.id);
                int expThaoNgoc = 0;
                Item itemNgoc = new Item();
                itemNgoc.isLock = true;
                int loai = 0;
                int loai1 = 0;
                int loai2 = 0;
                int indextemp = 0;
                switch (data.type) {
                    case 1:
                        loai = 106;
                        loai1 = 107;
                        loai2 = 108;
                        break;
                    case 2:
                        loai = 107;
                        loai1 = 106;
                        loai2 = 108;
                        break;
                    default:
                        loai = 108;
                        loai1 = 106;
                        loai2 = 107;
                        break;
                }
                int[] temp = new int[]{106, 107, 108};
                int a = 0;
                for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                    if (itemThaoNgoc.options.get(i).id == 109 || itemThaoNgoc.options.get(i).id == 110 || itemThaoNgoc.options.get(i).id == 111 || itemThaoNgoc.options.get(i).id == 112) {
                        a++;
                    }
                }
                if (a == 0) {
                    p.conn.sendMessageLog("Vật phẩm có ngọc đâu mà tháo đại ca");
                    return;
                }
                int yenThaoNgoc = 0;
                for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                    if (itemThaoNgoc.options.get(i).id == 122) {
                        yenThaoNgoc = itemThaoNgoc.options.get(i).param;
                        itemThaoNgoc.options.remove(i);
                    }
                }
                if (yenThaoNgoc <= p.c.yen) {
                    p.c.upyen(-yenThaoNgoc);
                } //
                else if (yenThaoNgoc >= p.c.yen) {
                    int coin = yenThaoNgoc - p.c.yen;
                    if (coin > p.c.xu) {
                        p.conn.sendMessageLog("Không đủ xu và yên nha");
                        return;
                    }
                    p.c.upyen(-p.c.yen);
                    p.c.upxu(-coin);
                }

                int[] tempa = new int[a];
                a = 0;
                for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                    if (itemThaoNgoc.options.get(i).id == 109 || itemThaoNgoc.options.get(i).id == 110 || itemThaoNgoc.options.get(i).id == 111 || itemThaoNgoc.options.get(i).id == 112) {
                        tempa[a] = itemThaoNgoc.options.get(i).id;
                        a++;
                    }
                }
                int a2 = 0;
                for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                    if (itemThaoNgoc.options.get(i).id == 109 || itemThaoNgoc.options.get(i).id == 110 || itemThaoNgoc.options.get(i).id == 111 || itemThaoNgoc.options.get(i).id == 112 || itemThaoNgoc.options.get(i).id < 0) {
                        a2++;
                    }
                }
                int[] tempIndex = new int[a2];
                int[] tempIndexNagative = new int[a2];
                a2 = 0;
                for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                    if (itemThaoNgoc.options.get(i).id == 109 || itemThaoNgoc.options.get(i).id == 110 || itemThaoNgoc.options.get(i).id == 111 || itemThaoNgoc.options.get(i).id == 112 || itemThaoNgoc.options.get(i).id < 0) {
                        tempIndex[a2] = i;
                        tempIndexNagative[a2] = itemThaoNgoc.options.get(i).id;
                        a2++;
                    }
                }
                for (int ai = 0; ai < tempa.length; ai++) {
                    itemNgoc = new Item();
                    itemNgoc.isLock = false;
                    switch (tempa[ai]) {
                        case 112:
                            indextemp = 0;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 112) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 1) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 112) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 4) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 112) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 7) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                if (itemThaoNgoc.options.get(i).id == indextemp - 10) {
                                    itemNgoc.options.add(new Option(104, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 11) {
                                    itemNgoc.options.add(new Option(123, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.options.get(i).param;
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 13) {
                                    itemNgoc.saleCoinLock = itemThaoNgoc.options.get(i).param;
                                }
                            }
                            itemNgoc.id = 655;
                            p.c.addItemBag(false, itemNgoc);
                            break;
                        case 111:
                            indextemp = -15;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 111) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 1) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 111) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 4) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 111) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 7) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                if (itemThaoNgoc.options.get(i).id == indextemp - 10) {
                                    itemNgoc.options.add(new Option(104, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 11) {
                                    itemNgoc.options.add(new Option(123, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.options.get(i).param;
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 13) {
                                    itemNgoc.saleCoinLock = itemThaoNgoc.options.get(i).param;
                                }
                            }
                            itemNgoc.id = 654;
                            p.c.addItemBag(false, itemNgoc);
                            break;
                        case 110:
                            indextemp = -30;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 110) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 1) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 110) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 4) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 110) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 7) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                if (itemThaoNgoc.options.get(i).id == indextemp - 10) {
                                    itemNgoc.options.add(new Option(104, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 11) {
                                    itemNgoc.options.add(new Option(123, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.options.get(i).param;
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 13) {
                                    itemNgoc.saleCoinLock = itemThaoNgoc.options.get(i).param;
                                }
                            }
                            itemNgoc.id = 653;
                            p.c.addItemBag(false, itemNgoc);
                            break;
                        case 109:
                            indextemp = -45;
                            for (int j = 0; j < temp.length; j++) {
                                if (temp[j] == 106 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 109) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 106) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 1) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 107 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 109) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 107) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 4) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                                if (temp[j] == 108 && temp[j] == loai) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == 109) {
                                            itemNgoc.options.add(new Option(loai, 0));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 1).id, itemThaoNgoc.options.get(i + 1).param));
                                            itemNgoc.options.add(new Option(itemThaoNgoc.options.get(i + 2).id, itemThaoNgoc.options.get(i + 2).param));
                                        }
                                    }
                                } else if (temp[j] == 108) {
                                    for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                        if (itemThaoNgoc.options.get(i).id == indextemp - 7) {
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 1).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 1).param).length() - 3))));
                                            itemNgoc.options.add(new Option(Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length())), Integer.valueOf(String.valueOf(itemThaoNgoc.options.get(i + 2).param).substring(0, String.valueOf(itemThaoNgoc.options.get(i + 2).param).length() - 3))));
                                        }
                                    }
                                }
                            }
                            for (int i = 0; i < itemThaoNgoc.options.size(); i++) {
                                if (itemThaoNgoc.options.get(i).id == indextemp - 10) {
                                    itemNgoc.options.add(new Option(104, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 11) {
                                    itemNgoc.options.add(new Option(123, itemThaoNgoc.options.get(i).param));
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 12) {
                                    itemNgoc.upgrade = (byte) itemThaoNgoc.options.get(i).param;
                                } else if (itemThaoNgoc.options.get(i).id == indextemp - 13) {
                                    itemNgoc.saleCoinLock = itemThaoNgoc.options.get(i).param;
                                }
                            }
                            itemNgoc.id = 652;
                            p.c.addItemBag(false, itemNgoc);
                            break;
                    }
                }
                for (int i = tempIndex.length - 1; i >= 0; i--) {
                    if (tempIndexNagative[i] == 109 || tempIndexNagative[i] == 110 || tempIndexNagative[i] == 111 || tempIndexNagative[i] == 112) {
                        itemThaoNgoc.options.remove(tempIndex[i] + 2);
                        itemThaoNgoc.options.remove(tempIndex[i] + 1);
                        itemThaoNgoc.options.remove(tempIndex[i]);
                    } else {
                        itemThaoNgoc.options.remove(tempIndex[i]);
                    }
                }

                int typeThaoNgoc = 3;
                m = new Message(124);
                m.writer().writeByte(3);
                m.writer().writeInt(p.luong);
                m.writer().writeInt(p.c.xu);
                m.writer().writeInt(p.c.yen);
                m.writer().writeByte(itemThaoNgoc.upgrade);
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
                m = new Message(42);
                m.writer().writeByte(3);
                m.writer().writeByte(indexItemThaoNgoc);
                m.writer().writeLong(itemThaoNgoc.expires);
                if (ItemData.isTypeUIME(typeThaoNgoc)) {
                    m.writer().writeInt(itemThaoNgoc.saleCoinLock);
                }
                if (ItemData.isTypeUIShop(typeThaoNgoc) || ItemData.isTypeUIShopLock(typeThaoNgoc) || ItemData.isTypeMounts(typeThaoNgoc) || ItemData.isTypeUIStore(typeThaoNgoc) || ItemData.isTypeUIBook(typeThaoNgoc) || ItemData.isTypeUIFashion(typeThaoNgoc) || ItemData.isTypeUIClanShop(typeThaoNgoc)) {
                    m.writer().writeInt(itemThaoNgoc.buyCoin);
                    m.writer().writeInt(itemThaoNgoc.buyCoinLock);
                    m.writer().writeInt(itemThaoNgoc.buyGold);
                }
                if (ItemData.isTypeBody(itemThaoNgoc.id) || ItemData.isTypeMounts(itemThaoNgoc.id) || ItemData.isTypeNgocKham(itemThaoNgoc.id)) {
                    m.writer().writeByte(itemThaoNgoc.sys);//thuoc tinh
                    int i = 0;
                    if (itemThaoNgoc.options != null) {
                        for (Option Option : itemThaoNgoc.options) {
                            m.writer().writeByte(Option.id);
                            m.writer().writeInt(Option.param);
                            i++;

                        }
                    }
                }
                m.writer().flush();
                p.conn.sendMessage(m);
                m.cleanup();
//                    p.conn.sendMessageLog("Tháo ngọc thành công");
                break;

        }
    }

    private static boolean checkTonTaiNgoc(Item itemsub, Item item) {
        switch (itemsub.id) {
            case 655:
                for (int i = 0; i < item.options.size(); i++) {
                    if (item.options.get(i).id == 112) {
                        return true;
                    }
                }
                break;
            case 654:
                for (int i = 0; i < item.options.size(); i++) {
                    if (item.options.get(i).id == 111) {
                        return true;
                    }
                }
                break;
            case 653:
                for (int i = 0; i < item.options.size(); i++) {
                    if (item.options.get(i).id == 110) {
                        return true;
                    }
                }
                break;
            case 652:
                for (int i = 0; i < item.options.size(); i++) {
                    if (item.options.get(i).id == 109) {
                        return true;
                    }
                }
                break;
        }
        return false;

    }

}
