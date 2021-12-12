package real;

/**
 *
 * @author Dũng Trần
 */

import boardGame.Place;
import server.SQLManager;
import io.Message;
import io.Session;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import server.GameCanvas;
import server.GameScr;
import server.Manager;
import server.Server;
import server.Service;
import server.util;

public class Player extends Actor {

    public Solo solo;
    public Solo.Soloer soloer;
    public int changePk;

    public String username = null;
    public String version = null;
    public Session conn = null;
    public Char c = null;

    public String passold = "";
    public String passnew = "";

    Server server = Server.getInstance();
    public String giftcode = ""; // Giftcode
    private int numLucky;
    private int coinLucky;
    // XSMB
    public int xoso;
    public int coinXS;
    public void cleanup() {
        conn = null;
    }

    public synchronized int upluong(long x) {
        long luongnew = (long) luong + x;
        if (luongnew > 2000000000) {
            x = 2000000000 - luong;
        } else if (luongnew < -2000000000) {
            x = -2000000000 - luong;
        }
        luong += x;
        return (int) x;
    }

    public static Player login(Session conn, String user, String pass) {
        Player p;
        try {
            synchronized (Server.LOCK_MYSQL) {
                ResultSet red = SQLManager.stat.executeQuery("SELECT * FROM `player` WHERE (`username`LIKE'" + user + "' AND `password`LIKE'" + pass + "');");
                if (red != null && red.first()) {
                    int iddb = red.getInt("id");
                    String username = red.getString("username");
                    int luong = red.getInt("luong");
                    int xoso = red.getInt("xoso");
                    int coinXS = red.getInt("coinXS");
                    byte lock = red.getByte("lock");
                    byte active = red.getByte("status");

                    if (lock != 0) {
                        if (lock == 1) {
                            conn.sendMessageLog("Tài khoản của bạn chưa được kích hoạt, vui lòng truy cập website http://nsol.6game.click để xác nhận kích hoạt tài khoản. Thông tin thêm truy cập vào nhóm Zalo https://zalo.me/g/alpxeh665");
                            return null;
                        }
                    }
                    JSONArray jarr = (JSONArray) JSONValue.parse(red.getString("ninja"));
                    p = PlayerManager.getInstance().getPlayer(user);
                    if (p != null) {
                        p.conn.sendMessageLog("Có người đăng nhập vào tài khoản của bạn");
                        PlayerManager.getInstance().kickSession(p.conn);
                        conn.sendMessageLog("Bạn đang đăng nhập tại máy khác. Hãy thử đăng nhập lại");
                        return null;
                    } else {
                        p = new Player();
                        p.conn = conn;
                        p.id = iddb;
                        p.username = username;
                        p.luong = luong;
                        // XSMB
                        p.xoso = xoso;
                        p.coinXS = coinXS;
                        for (byte i = 0; i < jarr.size(); i++) {
                            p.sortNinja[i] = jarr.get(i).toString();
                        }
                        PlayerManager.getInstance().put(p);
                        return p;
                    }
                } else {
                    conn.sendMessageLog("Thông tin tài khoản hoặc mật khẩu không chính xác");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void messageSubCommand(Message m) throws IOException {
        byte b = m.reader().readByte();
        util.Debug("Cmd -30->" + b);
        switch (b) {
            // Cộng điểm tiềm năng
            case -109:
                pluspPoint(m);
                break;
            // Cộng điểm kỹ năng
            case -108:
                plusSkillpoints(m);
                break;
            // Sắp xếp hành trang
            case -107:
                c.sortBag();
                break;
            // Sắp xếp rương
            case -106:
                c.sortBox();
                break;
            case -105:
                xuBagtoBox(m);
                break;
            // Rút xu
            case -104:
                xuBoxtoBag(m);
                break;
            //
            case -103:
                GameScr.ItemInfo(this, m);
                break;
            // Chế độ PK
            case -93:
                c.place.changerTypePK(this, m);
                break;
            // Tạo nhóm
            case -88:
                if (c != null) {
                    createParty();
                }
                break;
            // Thay đổi nhóm đội trưởng
            case -87:
                if (c != null) {
                    changeTeamLeaderParty(m);
                }
                break;
            // Thay đổi thành viên nhóm
            case -86:
                if (c != null) {
                    this.moveMemberParty(m);
                }
                break;
            // Xem bạn bè
            case -85:
                viewFriend();
                break;
            // Xóa bạn bè
            case -83:
                deleteFriend(m);
                break;
            // Buff HS
            case -79:
                useSkill.buffLive(this, m);
                break;
            // Tìm kiếm nhóm
            case -77:
                if (c != null && c.place != null) {
                    c.place.openFindParty(this);
                }
                break;
            // Chiêu thức
            case -67:
                pasteSkill(m);
                break;
            case -65:
                GameScr.sendSkill(this, m.reader().readUTF());
                break;
            // Mời gia tộc
            case -63:
                clanInvite(m);
                break;
            // Đồng ý
            case -62:
                acceptInviteClan(m);
                break;
            // Xin gia tộc
            case -61:
                clanPlease(m);
                break;
            // Đồng ý gia tộc
            case -60:
                acceptPleaseClan(m);
        }
    }

    private void sendInfo() throws IOException {
        c.hp = c.getMaxHP();
        c.mp = c.getMaxMP();
        Message m = new Message(-30);
        m.writer().writeByte(-127);
        m.writer().writeInt(c.id);// ID
        m.writer().writeUTF(c.clan.clanName);// Name Clan
        if (!c.clan.clanName.isEmpty()) {
            m.writer().writeByte(c.clan.typeclan);// Kiểu Clan
        }
        m.writer().writeByte((c.taskId = 50));// Nhiệm vụ
        m.writer().writeByte(c.gender);// Giới tính
        m.writer().writeShort(c.head);// Tóc
        m.writer().writeByte(c.speed());// Tốc độ
        m.writer().writeUTF(c.name);// Tên
        m.writer().writeByte(c.pk);//pk
        m.writer().writeByte(c.typepk);
        m.writer().writeInt(c.getMaxHP());
        m.writer().writeInt(c.hp);
        m.writer().writeInt(c.getMaxMP());
        m.writer().writeInt(c.mp);
        m.writer().writeLong(c.exp);
        m.writer().writeLong(c.expdown);
        m.writer().writeShort(c.eff5buffHP()); 
        m.writer().writeShort(c.eff5buffMP());
        m.writer().writeByte(c.nclass);
        m.writer().writeShort(c.ppoint);
        m.writer().writeShort(c.potential0);
        m.writer().writeShort(c.potential1);
        m.writer().writeInt(c.potential2);
        m.writer().writeInt(c.potential3);
        m.writer().writeShort(c.spoint);
        m.writer().writeByte(c.skill.size());
        for (short i = 0; i < c.skill.size(); i++) {
            Skill skill = c.skill.get(i);
            m.writer().writeShort(SkillData.Templates(skill.id, skill.point).skillId);//id skill
        }
        m.writer().writeInt(c.xu);
        m.writer().writeInt(c.yen);
        m.writer().writeInt(luong);
        m.writer().writeByte(c.maxluggage);
        for (byte i = 0; i < c.maxluggage; i++) {
            Item item = c.ItemBag[i];
            if (item != null) {
                m.writer().writeShort(item.id);
                m.writer().writeBoolean(item.isLock);
                if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
                    m.writer().writeByte(item.upgrade);
                }
                m.writer().writeBoolean(item.isExpires);
                m.writer().writeShort(item.quantity);
            } else {
                m.writer().writeShort(-1);
            }
        }
        // trang bi
        for (int i = 0; i < 16; i++) {
            Item item = c.ItemBody[i];
            if (item != null) {
                m.writer().writeShort(item.id);
                m.writer().writeByte(item.upgrade);
                m.writer().writeByte(item.sys);
            } else {
                m.writer().writeShort(-1);
            }
        }

        m.writer().writeBoolean(c.isHuman); 
        m.writer().writeBoolean(c.isNhanban);
        m.writer().writeShort(-1);
        m.writer().writeShort(-1);
        m.writer().writeShort(-1);
        m.writer().writeShort(-1);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
        this.getMobMe();
        c.clone = cloneChar.getClone(c);
        for (Map map : server.maps) {
            if (map.id != c.mapid) {
                continue;
            }
            boolean isturn = false; // Lôi đài
            if (map.getXHD() != -1 || map.VDMQ() || (map.id >= 98 && map.id <= 104 || map.LOIDAI())) {
                isturn = true;
                map = Manager.getMapid(c.mapLTD);
            }
            for (int i = 0; i < map.area.length; i++) {
                if (map.area[i].numplayers < map.template.maxplayers) {
                    if (!isturn) {
                        map.area[i].Enter(this);
                    } else {
                        map.area[i].EnterMap0(c);
                    }
                    for (byte n = 0; n < c.veff.size(); n++) {
                        this.addEffectMessage(c.veff.get(n));
                    }
                    return;
                }
            }
            map.area[util.nextInt(map.area.length)].Enter(this);
            for (byte n = 0; n < c.veff.size(); n++) {
                this.addEffectMessage(c.veff.get(n));
            }
        }
    }

    public void processGameMessage(Message m) throws Exception {
        byte msg = m.reader().readByte();
        util.Debug("-28->" + msg);
        ClanManager clan = null;
        switch (msg) {
            // Chọn nhân vật
            case -126:
                selectNhanVat(m);
                break;
            // Tạo nhân vật
            case -125:
                createNinja(m);
                break;
            // Load Data
            case -122:
                server.manager.sendData(this);
                break;
            // Load Map
            case -121:
                server.manager.sendMap(this);
                break;
            // Load Skill
            case -120:
                server.manager.sendSkill(this);
                break;
            // Load Item
            case -119:
                 server.manager.createItem(this);
                break;
            // Lấy icon ảnh
            case -115:
                GameScr.reciveImage(this, m);
                break;
            // Xem lịch sử gia tộc
            case -114:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.LogClan(this);
                }
                break;
            // Xem gia tộc
            case -113:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.requestClanInfo(this);
                }
            // Xem gia tộc
            case -112:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.requestClanMember(this);
                }
            //Kho clan
            case -111:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.requestClanItem(this);
                }
                break;
            case -108:
                GameScr.reciveImageMOB(this, m);
                break;
            case -101:
                selectNhanVat(null);
                break;
            // Thông báo gia tộc
            case -95:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.setAlert(this, m);
                }
                break;
            // Bổ nhiệm gia tộc
            case -94:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.changeClanType(this, m);
                }
                break;
            // Đuổi khỏi gia tộc
            case -93:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.moveOutClan(this, m);
                }
                break;
            // Rời gia tộc
            case -92:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.OutClan(this);
                }
                break;
            // Nâng cấp gia tộc
            case -91:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.clanUpLevel(this);
                }
                break;
            // Góp xu
            case -90:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.inputCoinClan(this, m);
                }
                break;
            // Chuyển hóa
            case -88:
                GameScr.doConvertUpgrade(this, m);
                break;
            // Tách vật phẩm
            case -85:
                ItemData.divedeItem(this, m);
                break;
            // Nhận rương
            case -82:
                this.rewardedCave();
                break;
            case -79: {
                this.rewardedCT();
                break;
            }
            // Lật hình
            case -72:
                GameScr.LuckValue(this, m);
                break;
            // Nâng cấp
            case -62:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.openItemLevel(this);
                }
                break;
            // Phát vật phẩm gia tộc
            case -61:
                clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null) {
                    clan.sendClanItem(this, m);
                }

        }
        m.cleanup();
    }

    public void Admission(byte nclass) throws IOException {
        switch (nclass) {
            case 1:
                c.addItemBag(true, (ItemData.itemDefault(94, true)));
                c.addItemBag(true, ItemData.itemDefault(40, true));
                break;
            case 2:
                c.addItemBag(true, ItemData.itemDefault(114, true));
                c.addItemBag(true, ItemData.itemDefault(49, true));
                break;
            case 3:
                c.addItemBag(true, ItemData.itemDefault(99, true));
                c.addItemBag(true, ItemData.itemDefault(58, true));
                break;
            case 4:
                c.addItemBag(true, ItemData.itemDefault(109, true));
                c.addItemBag(true, ItemData.itemDefault(67, true));
                break;
            case 5:
                c.addItemBag(true, ItemData.itemDefault(104, true));
                c.addItemBag(true, ItemData.itemDefault(76, true));
                break;
            case 6:
                c.addItemBag(true, ItemData.itemDefault(119, true));
                c.addItemBag(true, ItemData.itemDefault(85, true));
                break;
            default:
                break;
        }
        c.get().nclass = c.clan.nClass = nclass;
        c.get().skill.clear();
        c.get().upHP(c.get().getMaxHP());
        c.get().upMP(c.get().getMaxMP());
        c.get().spoint = Level.totalsPoint(c.get().level);
        c.get().ppoint = Level.totalpPoint(c.get().level);
        c.get().potential0 = 5;
        c.get().potential1 = 5;
        c.get().potential2 = 5;
        c.get().potential3 = 10;
        Message m = new Message(-30);
        m.writer().writeByte(-126);
        m.writer().writeByte(c.get().speed()); // Tốc độ
        m.writer().writeInt(c.get().getMaxHP()); // Máu cao nhất
        m.writer().writeInt(c.get().getMaxMP()); // Mana cao nhất
        m.writer().writeShort(c.get().potential1);
        m.writer().writeInt(c.get().potential2);
        m.writer().writeInt(c.get().potential3);
        m.writer().writeByte(c.get().nclass); // Lớp
        m.writer().writeShort(c.get().spoint);
        m.writer().writeShort(c.get().ppoint);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    private void plusSkillpoints(Message m) throws IOException {
        short sk = m.reader().readShort();
        byte point = m.reader().readByte();
        m.cleanup();
        Skill skill = c.get().getSkill(sk);
        if (skill == null || c.get().spoint <= 0 || point <= 0) {
            return;
        }
//        if (sk >= 67 && sk <= 72) {
//            conn.sendMessageLog("Không thể cộng điểm cho kĩ năng này");
//            return;
//        }
        SkillData data = SkillData.Templates(sk);
        if (skill.point + point > data.maxPoint) {
            conn.sendMessageLog("Cấp tối đa là " + data.maxPoint);
            return;
        }
//        if (data.templates.get(skill.point+point).level > nj.level) {
//            conn.sendMessageLog("Trình độ của bạn chưa đạt "+SkillData.Templates(skill.id, skill.point).level);
//            return;
//        }
        skill.point += point;
        c.get().spoint -= point;
        c.get().upHP(c.get().getMaxHP());
        c.get().upMP(c.get().getMaxMP());
        loadSkill();
    }

    void loadSkill() throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-125);
        m.writer().writeByte(c.get().speed());
        m.writer().writeInt(c.get().getMaxHP());
        m.writer().writeInt(c.get().getMaxMP());
        m.writer().writeShort(c.get().spoint);
        m.writer().writeByte(c.get().skill.size());
        for (short i = 0; i < c.get().skill.size(); i++) {
            Skill fs = c.get().skill.get(i);
            m.writer().writeShort(SkillData.Templates(fs.id, fs.point).skillId);
        }
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void getMp() throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-121);
        m.writer().writeInt(c.get().mp);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    private void pluspPoint(Message m) throws IOException {
        if (c.get().nclass == 0) {
            return;
        }
        byte num = m.reader().readByte();
        short quantity = m.reader().readShort();
        m.cleanup();
        if (quantity <= 0 || quantity > c.get().ppoint) {
            return;
        }
        switch (num) {
            case 0:
                c.get().potential0 += quantity;
                break;
            case 1:
                c.get().potential1 += quantity;
                break;
            case 2:
                c.get().potential2 += quantity;
                break;
            case 3:
                c.get().potential3 += quantity;
                break;
            default:
                return;
        }
        c.get().ppoint -= quantity;
        c.get().upHP(c.get().getMaxHP());
        c.get().upMP(c.get().getMaxMP());
        loadPpoint();
    }

   public void restPpoint() throws IOException {
         
        c.get().potential0 = 5;
        c.get().potential1 = 5;
        c.get().potential2 = 5;
        c.get().potential3 = 10;
        c.get().ppoint = (short) (Level.totalpPoint(c.get().level) + c.maxSTN*10 + c.maxBBH*10);
       
        loadPpoint();
    }
    
    public void restSpoint() throws IOException {
        for (Skill skill : c.get().skill) {
            if (skill.id != 0 && skill.id != 72)
                skill.point = 1;
        }
        c.get().spoint = (short) (Level.totalsPoint(c.get().level) + c.maxSKN + c.maxBPL);
        loadSkill();
    }


    public void loadPpoint() throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-109);
        m.writer().writeByte(c.get().speed());
        m.writer().writeInt(c.get().getMaxHP());
        m.writer().writeInt(c.get().getMaxMP());
        m.writer().writeShort(c.get().ppoint);
        m.writer().writeShort(c.get().potential0);
        m.writer().writeShort(c.get().potential1);
        m.writer().writeInt(c.get().potential2);
        m.writer().writeInt(c.get().potential3);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    private void xuBagtoBox(Message m) throws IOException {
        int xu = m.reader().readInt();
        if (xu <= 0 || xu > c.xu) {
            return;
        }
        if ((long) xu + c.xuBox > 2000000000) {
            conn.sendMessageLog("Bạn chỉ có thể cất thêm " + util.getFormatNumber(((long) xu + c.xu) - 2000000000));
            return;
        }
        c.xu -= xu;
        c.xuBox += xu;
        m = new Message(-30);
        m.writer().writeByte(-105);
        m.writer().writeInt(xu);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    private void xuBoxtoBag(Message m) throws IOException {
        int xu = m.reader().readInt();
        if (xu <= 0 || xu > c.xuBox) {
            return;
        }
        if ((long) xu + c.xu > 2000000000) {
            conn.sendMessageLog("Bạn chỉ có thể rút thêm " + util.getFormatNumber(((long) xu + c.xu) - 2000000000));
            return;
        }
        c.xu += xu;
        c.xuBox -= xu;
        m = new Message(-30);
        m.writer().writeByte(-104);
        m.writer().writeInt(xu);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void openBagLevel(byte index) throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-91);
        m.writer().writeByte(c.ItemBag.length);
        m.writer().writeByte(index);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void viewFriend() throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-85);
        JSONArray jar = (JSONArray) JSONValue.parse(c.friend);
        byte i = 0;
        byte turn = 0;
        while (jar.size() > 0) {
            if (i == jar.size()) {
                i = 0;
                turn++;
                if (turn == 3) {
                    break;
                }
            }
            JSONObject job = (JSONObject) jar.get(i);
            if (Boolean.parseBoolean(job.get("agree").toString())) {
                Char n = PlayerManager.getInstance().getNinja(job.get("name").toString());
                if (turn == 0 && n != null) {
                    m.writer().writeUTF(job.get("name").toString());
                    m.writer().writeByte(3);
                } else if (turn == 1 && n == null) {
                    m.writer().writeUTF(job.get("name").toString());
                    m.writer().writeByte(1);
                }
            } else if (turn == 2) {
                m.writer().writeUTF(job.get("name").toString());
                m.writer().writeByte(-1);
            }
            i++;
        }
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    protected void deleteFriend(Message m) throws IOException {
        String nF = m.reader().readUTF();
        m.cleanup();
        JSONArray jar = (JSONArray) JSONValue.parse(c.friend);
        for (int i = 0; i < jar.size(); i++) {
            JSONObject job = (JSONObject) jar.get(i);
            if (job.get("name").toString().equals(nF)) {
                jar.remove(i);
                c.friend = jar.toJSONString();
                m = new Message(-30);
                m.writer().writeByte(-83);
                m.writer().writeUTF(nF);
                m.writer().flush();
                conn.sendMessage(m);
                m.cleanup();
                return;
            }
        }
    }

    public void acceptInviteClan(Message m) throws IOException {
        int charMapid = m.reader().readInt();
        m.cleanup();
        if (c.clan.clanName.length() > 0) {
            conn.sendMessageLog("Bạn đã có gia tộc.");
            return;
        }
        Char n = c.place.getNinja(charMapid);
        if (n == null || n.requestclan != c.id) {
            sendAddchatYellow("Lời mời đã hết hạn.");
            return;
        }
        ClanManager clan = ClanManager.getClanName(n.clan.clanName);
        if (clan != null) {
            if (clan.members.size() >= clan.getMemMax()) {
                conn.sendMessageLog("Gia tộc đã đầy thành viên.");
            } else if (Math.abs(c.get().x - n.x) < 70 && Math.abs(c.get().x - n.x) < 50) {
                c.requestclan = -1;
                c.clan.clanName = clan.name;
                c.clan.typeclan = 0;
                clan.members.add(c.clan);
                setTypeClan(c.clan.typeclan);
            } else {
                this.sendAddchatYellow("Khoảng cách quá xa không thể chấp nhận lời mời vào gia tộc");
            }
        }
    }

    public void clanInvite(Message m) throws IOException {
        int charId = m.reader().readInt();
        m.cleanup();
        if (c.requestclan != -1) {
            conn.sendMessageLog("Bạn đã gửi lời mời tham gia gia tộc.");
            return;
        }
        ClanManager clan = ClanManager.getClanName(c.clan.clanName);
        if (clan != null && c.clan.typeclan > 1) {
            if (clan.members.size() < clan.getMemMax()) {
                Char n = c.place.getNinja(charId);
                if (n == null) {
                    return;
                }
                if (n.requestclan != -1) {
                    conn.sendMessageLog("Đối phương đang có lời mời vào giao tộc");
                } else if (n.clan.clanName.length() > 0) {
                    conn.sendMessageLog("Đối phương đã có gia tộc");
                } else if (Math.abs(c.get().x - n.x) < 70 && Math.abs(c.get().x - n.x) < 50) {
                    c.requestclan = n.id;
                    c.deleyRequestClan = System.currentTimeMillis() + 10000L;
                    m = new Message(-30);
                    m.writer().writeByte(-63);
                    m.writer().writeInt(c.get().id);
                    m.writer().writeUTF(c.clan.clanName);
                    m.writer().flush();
                    n.p.conn.sendMessage(m);
                    m.cleanup();
                } else {
                    this.sendAddchatYellow("Khoảng cách quá xa không thể gửi lời mời vào gia tộc");
                }
            } else {
                conn.sendMessageLog("Gia tộc đã tối đa thành viện tham gia");
            }
        }
    }

    public void setTypeClan(int type) throws IOException {
        c.clan.typeclan = (byte) type;
        Message m = new Message(-30);
        m.writer().writeByte(-62);
        m.writer().writeInt(c.id);
        m.writer().writeUTF(c.clan.clanName);
        m.writer().writeByte(c.clan.typeclan);
        m.writer().flush();
        c.place.sendMessage(m);
        m.cleanup();
    }

    public void clanPlease(Message m) throws IOException {
        int charID = m.reader().readInt();
        m.cleanup();
        if (c.clan.clanName.length() > 0) {
            conn.sendMessageLog("Bạn đã có gia tộc");
        } else {
            Char n = c.place.getNinja(charID);
            if (n == null || n.clan.typeclan < 2) {
                return;
            }
            ClanManager clan = ClanManager.getClanName(n.clan.clanName);
            if (clan == null) {
                return;
            }
            if (clan.members.size() >= clan.getMemMax()) {
                conn.sendMessageLog("Gia tộc đã đầy thành viên.");
            } else if (c.requestclan != -1) {
                conn.sendMessageLog("Bạn đã gửi yêu cầu gia nhập biêt đội");
            } else if (Math.abs(c.x - n.x) < 70 && Math.abs(c.x - n.x) < 50) {
                c.requestclan = n.id;
                c.deleyRequestClan = System.currentTimeMillis() + 15000L;
                m = new Message(-30);
                m.writer().writeByte(-61);
                m.writer().writeInt(c.get().id);
                m.writer().flush();
                n.p.conn.sendMessage(m);
                m.cleanup();

            } else {
                this.sendAddchatYellow("Khoảng cách quá xa không thể gửi yêu cầu vào gia tộc");
            }
        }
    }

    public void acceptPleaseClan(Message m) throws IOException {
        int charID = m.reader().readInt();
        m.cleanup();
        ClanManager clan = ClanManager.getClanName(c.clan.clanName);
        if (clan == null || c.clan.typeclan < 2) {
            return;
        }
        Char n = c.place.getNinja(charID);
        if (n == null || n.requestclan != c.id) {
            sendAddchatYellow("Lời mời đã hết hạn.");
            return;
        }
        if (clan.members.size() >= clan.getMemMax()) {
            conn.sendMessageLog("Gia tộc đã đầy thành viên.");
        } else if (n.clan.clanName.length() > 0) {
            conn.sendMessageLog("Đối phương đã có gia tộc.");
        } else if (Math.abs(c.get().x - n.x) < 70 && Math.abs(c.get().x - n.x) < 50) {
            n.requestclan = -1;
            n.clan.clanName = clan.name;
            n.clan.typeclan = 0;
            clan.members.add(n.clan);
            n.p.setTypeClan(n.clan.typeclan);
        } else {
            this.sendAddchatYellow("Khoảng cách quá xa không thể chấp nhận yêu cầu vào gia tộc");
        }
    }

    private void pasteSkill(Message m) throws IOException {
        String t1 = m.reader().readUTF();
        String t2 = m.reader().readUTF();
        short lent = m.reader().readShort();
//        System.out.println("load skill");
        switch (t1) {
            case "KSkill":
                for (byte i = 0; i < c.get().KSkill.length; i++) {
                    byte sid = m.reader().readByte();
                    if (sid == -1) {
                        continue;
                    }
                    Skill skill = c.get().getSkill(sid);
                    if (skill != null && SkillData.Templates(skill.id).type != 0) {
                        c.get().KSkill[i] = skill.id;
                    }
                }
                break;
            case "OSkill":
                for (byte i = 0; i < c.get().OSkill.length; i++) {
                    byte sid = m.reader().readByte();
                    if (sid == -1) {
                        continue;
                    }
                    Skill skill = c.get().getSkill(sid);
                    if (skill != null && SkillData.Templates(skill.id).type != 0) {
                        c.get().OSkill[i] = skill.id;
                    }
                }
                break;
        }
        m.cleanup();
    }

    public void upExpClan(int exp) {
        ClanManager clan = ClanManager.getClanName(c.clan.clanName);
        if (clan != null && clan.getMem(c.name) != null) {
            c.clan.pointClan += exp;
            c.clan.pointClanWeek += exp;
            clan.upExp(exp);
            this.sendAddchatYellow("Gia tộc của bạn nhận được " + exp + " kinh nghiệm");
        }
    }

    public void selectNhanVat(Message m) throws Exception {
        if (m != null && c == null) {
            String name = m.reader().readUTF();
            for (byte i = 0; i < sortNinja.length; i++) {
                if (name.equals(sortNinja[i])) {
                    c = Char.setup(this, sortNinja[i]);
                    if (c == null) {
                        continue;
                    }
                    PlayerManager.getInstance().put(c);
                    sendInfo();
                    int songuoi = PlayerManager.getInstance().conns_size() + 0;
                    server.manager.sendTB(this, "Tin Tức", "#1. Đăng nhập lại NPC Vua Hùng nhận quà tân thủ, vào nhóm Zalo để lấy mã giftcode, tham gia đặt cược xổ số tại NPC Sunoo, và còn nhiều tính năng khác anh em tự khám phá.\n#2. Nạp lượng để ủng hộ máy chủ, cổng nạp lượng duy nhất tại website http://nsol.6game.click.\n#3. Truy cập nhóm Zalo để cập nhật thông tin mới nhất https://zalo.me/g/alpxeh665.\n#4. Đang online: " + songuoi +".");
                    m = new Message(-23);
                    m.writer().writeInt(c.get().id);
                    m.writer().flush();
                    conn.sendMessage(m);
                    m.cleanup();
                    break;
                }
            }
            return;
        }
        m = new Message(-28);
        m.writer().writeByte(-126);
        //lent hero
        byte lent = 0;
        for (byte i = 0; i < sortNinja.length; i++) {
            if (sortNinja[i] != null) {
                lent++;
            }
        }
        m.writer().writeByte(lent);
        ResultSet red;
        for (byte i = 0; i < sortNinja.length; i++) {
            if (sortNinja[i] == null) {
                continue;
            }
            synchronized (Server.LOCK_MYSQL) {
                red = SQLManager.stat.executeQuery("SELECT `gender`,`name`,`class`,`level`,`head`,`ItemBody` FROM `ninja` WHERE `name`LIKE'" + sortNinja[i] + "';");
                if (red != null && red.first()) {
                    m.writer().writeByte(red.getByte("gender")); // gt
                    m.writer().writeUTF(red.getString("name")); // name
                    m.writer().writeUTF(server.manager.NinjaS[red.getByte("class")]); // lop
                    m.writer().writeByte(red.getInt("level"));// Level
                    short head = red.getByte("head");
                    short weapon = -1;
                    short body = -1;
                    short leg = -1;
                    JSONArray jar = (JSONArray) JSONValue.parse(red.getString("ItemBody"));

                    Item[] itembody = new Item[16];
                    if (jar != null) {
                        for (byte j = 0; j < jar.size(); j++) {
                            JSONObject job = (JSONObject) jar.get(j);
                            byte index = Byte.parseByte(job.get("index").toString());
                            itembody[index] = ItemData.parseItem(jar.get(j).toString());
                        }
                    }
                    if (itembody[11] != null) {
                        head = ItemData.ItemDataId(itembody[11].id).part;
                        if (itembody[11].id == 745) {
                            head = 264;
                        }
                    }
                    if (itembody[1] != null) {
                        weapon = ItemData.ItemDataId(itembody[1].id).part;
                    }
                    if (itembody[2] != null) {
                        body = ItemData.ItemDataId(itembody[2].id).part;
                    }
                    if (itembody[6] != null) {
                        leg = ItemData.ItemDataId(itembody[6].id).part;
                    }
                    if (head == 258 || head == 264) {
                        body = (short) (head + 1);
                        leg = (short) (head + 2);
                    }
                    m.writer().writeShort(head);
                    m.writer().writeShort(weapon);
                    m.writer().writeShort(body);
                    m.writer().writeShort(leg);
                }
            }
        }
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    private void createNinja(Message m) throws Exception {
        if (sortNinja[2] != null) {
            return;
        }
        String name = m.reader().readUTF().toLowerCase();
        byte gender = m.reader().readByte();
        byte head = m.reader().readByte();
        m.cleanup();
        if (!util.CheckString(name, "^[a-zA-Z0-9]+$") || name.length() < 5 || name.length() > 15) {
            conn.sendMessageLog("Tên nhân chỉ đồng ý các ký tự a-z,0-9 và chiều dài từ 5 đến 15 ký tự");
            return;
        }
        if (sortNinja[0] != null) { // 0 Tạo 1 nhân vật, 1 tạo 2 nhân vật
            conn.sendMessageLog("Để tránh nhiều tài khoản clone, Máy chủ giới hạn không tạo thêm nhân vật");
            return;
        }
        synchronized (Server.LOCK_MYSQL) {
            ResultSet red = SQLManager.stat.executeQuery("SELECT `id` FROM `ninja` WHERE `name`LIKE'" + name + "';");
            if (red != null && red.first()) {
                conn.sendMessageLog("Tên nhân vật đã tồn tại!");
            //} else
            //if (luong < 10000) { // Chi phí tạo nhân vật
            //conn.sendMessageLog("Tài khoản bạn không đủ 10.000 lượng, vui lòng nạp lượng tại trang chủ để tạo nhân vật");
                return;
                }
            // Kiểm tra xong   
            SQLManager.stat.executeUpdate("INSERT INTO ninja(`name`,`gender`,`head`,`ItemBag`,`ItemBox`,`ItemBody`,`ItemMounts`) VALUES (\"" + name + "\"," + gender + "," + head + ",'[]','[]','[]','[]');");
            for (byte i = 0; i < sortNinja.length; i++) {
                if (sortNinja[i] == null) {
                    sortNinja[i] = name;
                    break;
                }
            }
        }
        flush();
        selectNhanVat(null);
    }

    public void sendAddchatYellow(String str) {
        try {
            Message m = new Message(-24);
            m.writer().writeUTF(str);
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void privateChat(Message m) throws IOException {
        String name = m.reader().readUTF();
        String chat = m.reader().readUTF();
        Char n = PlayerManager.getInstance().getNinja(name);
        if (n == null || n.id == c.id) {
            return;
        }
        m = new Message(-22);
        m.writer().writeUTF(c.name);
        m.writer().writeUTF(chat);
        m.writer().flush();
        n.p.conn.sendMessage(m);
        m.cleanup();
    }

    public void luongMessage(long luongup) {
        upluong(luongup);
        try {
            Message m = new Message(-30);
            m.writer().writeByte(-72);
            m.writer().writeInt(luong);
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void upluongMessage(long luongup) {
        try {
            Message m = new Message(-30);
            m.writer().writeByte(-71);
            m.writer().writeInt(upluong(luongup));
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void useItem(Message m) throws IOException, InterruptedException {
        byte index = m.reader().readByte();
        m.cleanup();
        Item item = c.getIndexBag(index);
        if (item == null) {
            return;
        }
        useItem.uesItem(this, item, index);
    }

    private synchronized void setMoney(int sxu, int syen, int sluong) {
        c.xu = sxu;
        c.yen = syen;
        luong = sluong;
        try {
            Message m = new Message(13);
            m.writer().writeInt(c.xu);
            m.writer().writeInt(c.yen);
            m.writer().writeInt(luong);
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void SellItemBag(Message m) throws IOException {
        int index = m.reader().readUnsignedByte();
        int num = 1;
        if (m.reader().available() > 0) {
            num = m.reader().readInt();
        }
        m.cleanup();
        Item item = c.getIndexBag(index);
        if (item == null || (ItemData.ItemDataId(item.id).isUpToUp && (num <= 0 || num > item.quantity))) {
            return;
        }
        if (ItemData.ItemDataId(item.id).isUpToUp) {
            num = 1;
        }
        if (ItemData.isTypeBody(item.id) && item.upgrade > 0) {
            conn.sendMessageLog("Không thể bán trang bị còn nâng cấp");
            return;
        }
        ItemData data = ItemData.ItemDataId(item.id);
        if (data.type == 12) {
            conn.sendMessageLog("Vật phẩm quý giá bạn không thể bán được");
            return;
        }
        item.quantity -= num;
        if (item.quantity <= 0) {
            c.ItemBag[index] = null;
        }
        c.upyen(item.saleCoinLock * num);
        m = new Message(14);
        m.writer().writeByte(index);//vi tri
        m.writer().writeInt(c.yen);//yen
        m.writer().writeShort(num);//so luong
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void itemBodyToBag(Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        byte idItemBag = c.getIndexBagNotItem();
        if (idItemBag == -1) {
            this.conn.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        if (index < 0 && index >= c.get().ItemBody.length) {
            return;
        }
        Item itembody = c.get().ItemBody[index];
        c.ItemBag[idItemBag] = itembody;
        c.get().ItemBody[index] = null;
        if (itembody.id == 569) {
            removeEffect(36);
        }
        if (index == 10) {
            mobMeMessage(0, (byte) 0);
        }
        m = new Message(15);
        m.writer().writeByte(c.get().speed());//speed
        m.writer().writeInt(c.get().getMaxHP());//hp
        m.writer().writeInt(c.get().getMaxMP());//mp
        m.writer().writeShort(c.get().eff5buffHP());//hp
        m.writer().writeShort(c.get().eff5buffMP());//mp
        m.writer().writeByte(index);//vi tri trang bi
        m.writer().writeByte(idItemBag);//index ItemBag
        m.writer().writeShort(c.get().partHead());//head
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void itemBoxToBag(Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        Item item = c.getIndexBox(index);
        if (item == null) {
            return;
        }
        ItemData data = ItemData.ItemDataId(item.id);
        byte indexBag = c.getIndexBagid(item.id, item.isLock);
        if (!item.isExpires && data.isUpToUp && indexBag != -1) {
            c.ItemBox[index] = null;
            c.ItemBag[indexBag].quantity += item.quantity;
        } else if (c.getBagNull() > 0) {
            indexBag = c.getIndexBagNotItem();
            c.ItemBox[index] = null;
            c.ItemBag[indexBag] = item;
        } else {
            conn.sendMessageLog("Rương đồ không đủ chỗ trống");
            return;
        }
        m = new Message(16);
        m.writer().writeByte(index);
        m.writer().writeByte(indexBag);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void itemBagToBox(Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        Item item = c.getIndexBag(index);
        if (item == null) {
            return;
        }
        ItemData data = ItemData.ItemDataId(item.id);
        byte indexBox = c.getIndexBoxid(item.id, item.isLock);
        if (!item.isExpires && data.isUpToUp && indexBox != -1) {
            c.ItemBag[index] = null;
            c.ItemBox[indexBox].quantity += item.quantity;
        } else if (c.getBoxNull() > 0) {
            indexBox = c.getIndexBoxNotItem();
            c.ItemBag[index] = null;
            c.ItemBox[indexBox] = item;
        } else {
            conn.sendMessageLog("Rương đồ không đủ chỗ trống");
            return;
        }
        m = new Message(17);
        m.writer().writeByte(index);
        m.writer().writeByte(indexBox);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void requestItem(int typeUI) throws IOException {
        Message m = new Message(30);
        m.writer().writeByte(typeUI);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void requestItemInfoMessage(Item item, int index, int typeUI) throws IOException {
        Message m = new Message(42);
        m.writer().writeByte(typeUI);
        m.writer().writeByte(index);
        m.writer().writeLong(item.expires);
        if (ItemData.isTypeUIME(typeUI)) {
            m.writer().writeInt(item.saleCoinLock);
        }
        if (ItemData.isTypeUIShop(typeUI) || ItemData.isTypeUIShopLock(typeUI) || ItemData.isTypeMounts(typeUI) || ItemData.isTypeUIStore(typeUI) || ItemData.isTypeUIBook(typeUI) || ItemData.isTypeUIFashion(typeUI) || ItemData.isTypeUIClanShop(typeUI)) {
            m.writer().writeInt(item.buyCoin);
            m.writer().writeInt(item.buyCoinLock);
            m.writer().writeInt(item.buyGold);
        }
        if (ItemData.isTypeBody(item.id) || ItemData.isTypeMounts(item.id) || ItemData.isTypeNgocKham(item.id)) {
            m.writer().writeByte(item.sys);
            if (item.options != null) {
                for (Option Option : item.options) {
                    if (Option.id >= 0) {
                    m.writer().writeByte(Option.id);
                    m.writer().writeInt(Option.param);
                }
            }
        }
        }
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void requestItemInfo(Message m) throws IOException {
        byte type = m.reader().readByte();
        int index = m.reader().readUnsignedByte();
        util.Debug("type " + type + " index" + index);
        m.cleanup();
        Item item = null;
        switch (type) {
            //xem info vk
            case 2:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            //xem info hanh trang
            case 3:
                if (index < 0 || index >= c.maxluggage) {
                    return;
                }
                item = c.ItemBag[index];
                break;
            //xem info ruong do
            case 4:
                if (index < 0 || index >= 30) {
                    return;
                }
                item = c.ItemBox[index];
                break;
            //xem info trang bi
            case 5:
                if (index < 0 || index > 15) {
                    return;
                }
                item = c.get().ItemBody[index];
                break;
            //xem info thuc an
            case 8:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            //xem info thuc an khoa
            case 9:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            //xem info cua hang
            case 14:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            //xem info cua hang sach
            case 15:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 16:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 17:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 18:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 19:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 20:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 21:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 22:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 23:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 24:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 25:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 26:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 27:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 28:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 29:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            //xem info thoi trang
            case 32:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 34:
                item = ItemSell.getItemTypeIndex(type, index);
                break;
            case 39:
                ClanManager clan = ClanManager.getClanName(c.clan.clanName);
                if (clan != null && index >= 0 && index < clan.items.size()) {
                    item = clan.items.get(index);
                }
                break;
            //xem info thu cuoi
            case 41:
                if (index < 0 || index > 4) {
                    return;
                }
                item = c.ItemMounts[index];
                break;
        }
        if (item == null) {
            //util.Debug("null item");
            return;
        }
        this.requestItemInfoMessage(item, index, type);
    }

    public void requestTrade(Message m) throws IOException {
        int ids = m.reader().readInt();
        m.cleanup();
        Player p = c.place.getNinja(ids).p;
        if (p == null) {
            sendAddchatYellow("Người này không ở cùng khu hoặc đã offline.");
        } else if (Math.abs(c.get().x - p.c.get().x) > 100 || Math.abs(c.get().y - p.c.get().y) > 100) {
            sendAddchatYellow("Khoảng cách quá xa.");
        } else if (c.tradeDelay > System.currentTimeMillis()) {
            conn.sendMessageLog("Bạn còn " + ((c.tradeDelay - System.currentTimeMillis()) / 1000L) + "s để tiếp tục giao dịch.");
        } else if (c.rqTradeId > 0) {
            conn.sendMessageLog(p.c.name + " đang có yêu cầu giao dịch.");
        } else if (p.c.isTrade) {
            conn.sendMessageLog(p.c.name + " đang thực hiện giao dịch.");
        } else {
            c.tradeDelay = System.currentTimeMillis() + 30000L;
            p.c.rqTradeId = c.get().id;
            m = new Message(43);
            m.writer().writeInt(c.get().id);
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        }
    }

    public void startTrade(Message m) throws IOException {
        int ids = m.reader().readInt();
        m.cleanup();
        if (c.isTrade) {
            conn.sendMessageLog("Bạn đã có giao dịch.");
            return;
        }
        Player p = c.place.getNinja(ids).p;
        if (p == null) {
            sendAddchatYellow("Người này không ở cùng khu hoặc đã offline.");
        } else if (Math.abs(c.get().x - p.c.get().x) > 100 || Math.abs(c.get().y - p.c.get().y) > 100) {
            sendAddchatYellow("Khoảng cách quá xa.");
        } else if (p.c.isTrade) {
            conn.sendMessageLog(p.c.name + " đã có giao dịch.");
        } else {
            p.c.isTrade = true;
            p.c.tradeId = c.id;
            p.c.tradeLock = 0;
            c.isTrade = true;
            c.tradeId = p.c.id;
            c.tradeLock = 0;
            c.rqTradeId = 0;
            m = new Message(37);
            m.writer().writeUTF(p.c.name);//name trade
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
            m = new Message(37);
            m.writer().writeUTF(c.name);//name trade
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
            return;
        }
        c.rqTradeId = 0;
    }

    public void lockTrade(Message m) throws IOException {
        if (c.tradeLock == 0) {
            c.tradeLock++;
            Char n = c.place.getNinja(c.tradeId);
            if (n == null) {
                closeLoad();
                return;
            }
            int tradexu = m.reader().readInt();//xu;
            if (tradexu > 0 && tradexu <= c.xu) {
                c.tradeCoin = tradexu;
            }
            byte lent = m.reader().readByte();//item
            for (byte i = 0; i < lent; i++) {
                byte index = m.reader().readByte();//index item ItemBag
                Item item = c.getIndexBag(index);
                if (c.tradeIdItem.size() > 12) {
                    break;
                }
                if (item != null && !item.isLock) {
                    c.tradeIdItem.add(index);
                }

            }
            if (c.tradeIdItem.size() > n.getBagNull()) {
                closeLoad();
                return;
            }
            m.cleanup();
            m = new Message(45);
            m.writer().writeInt(c.tradeCoin);
            m.writer().writeByte(c.tradeIdItem.size());//item
            for (byte i = 0; i < c.tradeIdItem.size(); i++) {
                Item item = c.getIndexBag(c.tradeIdItem.get(i));
                if (item == null) {
                    continue;
                }
                m.writer().writeShort(item.id);//id
                if (ItemData.isTypeBody(item.id) || ItemData.isTypeNgocKham(item.id)) {
                    m.writer().writeByte(item.upgrade);//+0-16
                }
                m.writer().writeBoolean(item.isExpires);//hsd
                m.writer().writeShort(item.quantity);//so luong
            }
            m.writer().flush();
            n.p.conn.sendMessage(m);
            m.cleanup();
        }
    }

    public void agreeTrade() throws IOException {
        if (c.tradeLock == 1) {
            Char n = c.place.getNinja(c.tradeId);
            if (n == null) {
                closeLoad();
                return;
            }
            c.tradeLock++;
            Message m = new Message(46);
            m.writer().flush();
            n.p.conn.sendMessage(m);
            m.cleanup();
            if (n.tradeLock == 2) {
                m = new Message(57);
                m.writer().flush();
                conn.sendMessage(m);
                n.p.conn.sendMessage(m);
                m.cleanup();
                if (n.tradeCoin > 0) {
                    n.upxuMessage(-n.tradeCoin);
                    c.upxuMessage(+n.tradeCoin);
                }
                if (c.tradeCoin > 0) {
                    c.upxuMessage(-c.tradeCoin);
                    n.upxuMessage(+c.tradeCoin);
                }
                ArrayList<Item> item1 = new ArrayList<>();
                ArrayList<Item> item2 = new ArrayList<>();
                for (byte i = 0; i < n.tradeIdItem.size(); i++) {
                    Item item = n.p.c.getIndexBag(n.tradeIdItem.get(i));
                    if (item != null) {
                        item1.add(item);
                        n.removeItemBag(n.tradeIdItem.get(i));
                    }
                }
                for (byte i = 0; i < c.tradeIdItem.size(); i++) {
                    Item item = c.getIndexBag(c.tradeIdItem.get(i));
                    if (item != null) {
                        item2.add(item);
                        c.removeItemBag(c.tradeIdItem.get(i));
                    }
                }
                for (byte i = 0; i < item1.size(); i++) {
                    Item item = item1.get(i);
                    if (item != null) {
                        c.addItemBag(true, item);
                    }
                }
                for (byte i = 0; i < item2.size(); i++) {
                    Item item = item2.get(i);
                    if (item != null) {
                        n.addItemBag(true, item);
                    }
                }
                closeTrade();
                n.p.closeTrade();
            }
        }
    }

    public void closeTrade() throws IOException {
        if (c.isTrade) {
            c.isTrade = false;
            c.tradeCoin = 0;
            c.tradeIdItem.clear();
            c.tradeLock = -1;
            c.tradeDelay = 0;
            c.tradeId = 0;
            closeLoad();
        } else if (c.rqTradeId > 0) {
            c.rqTradeId = 0;
        }
        c.requestclan = -1;
    }

    public void closeLoad() throws IOException {
        if (c.isTrade) {
            Char n = PlayerManager.getInstance().getNinja(c.tradeId);
            if (n != null && n.p != null && n.isTrade) {
                closeTrade();
                n.p.closeTrade();
            }
            closeTrade();
            n.p.closeTrade();
        }
        final Message m = new Message(57);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }


    public void liveFromDead() throws IOException {
        c.hp = c.getMaxHP();
        c.mp = c.getMaxMP();
        c.isDie = false;
        Message m = new Message(-10);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
        m = new Message(88);
        m.writer().writeInt(c.id);
        m.writer().writeShort(c.x);
        m.writer().writeShort(c.y);
        m.writer().flush();
        c.place.sendMyMessage(this, m);
        m.cleanup();
    }

    public void viewPlayerMessage(Message m) throws IOException {
        String playername = m.reader().readUTF();
        m.cleanup();
        Char n;
        if (playername.equals(c.name)) {
            viewInfoPlayers(this);
            return;
        }
        if (playername.equals(c.name)) {
            n = c;
        } else {
            n = PlayerManager.getInstance().getNinja(playername);
        }
        if (n == null) {
            sendAddchatYellow("Hiện tại người chơi đã offline");
            return;
        }
        n.p.sendAddchatYellow(c.name + " đang đứng nhìn bạn");
        viewInfoPlayers(n.p);
    }

    public void viewInfoPlayers(Player p) throws IOException {
        Message n = new Message(101);
        n.writer().writeInt(2);
        n.writer().writeByte(1);
        n.writer().writeByte(3);
        n.writer().flush();
        conn.sendMessage(n);
        n.cleanup();
        Message m = new Message(93);
        m.writer().writeInt(p.c.get().id);
        m.writer().writeUTF(p.c.name);
        m.writer().writeShort(p.c.get().partHead());
        m.writer().writeByte(p.c.gender);// Giới tính
        m.writer().writeByte(p.c.get().nclass);// Lớp
        m.writer().writeByte(p.c.get().pk);// Điểm hiếu chiến
        m.writer().writeInt(p.c.get().hp);
        m.writer().writeInt(p.c.get().getMaxHP());
        m.writer().writeInt(p.c.get().mp);
        m.writer().writeInt(p.c.get().getMaxMP());
        m.writer().writeByte(p.c.get().speed());// Tộc độ
        m.writer().writeShort(p.c.get().ResFire());// Res Fire
        m.writer().writeShort(p.c.get().ResIce());// Res Ice
        m.writer().writeShort(p.c.get().ResWind());// Res Wind 
        m.writer().writeInt(p.c.get().dameMax());// Dame
        m.writer().writeInt(p.c.get().dameDown());// Dame down
        m.writer().writeShort(p.c.get().Exactly());// Exactly
        m.writer().writeShort(p.c.get().Miss());// Ne don
        m.writer().writeShort(p.c.get().Fatal());// Fatal
        m.writer().writeShort(p.c.get().ReactDame());// ReactDame
        m.writer().writeShort(p.c.get().sysUp());// sysUp
        m.writer().writeShort(p.c.get().sysDown());
        m.writer().writeByte(p.c.get().level);
        m.writer().writeShort(38);// Điểm uy danh
        m.writer().writeUTF(p.c.clan.clanName);
        if (!p.c.clan.clanName.isEmpty()) {
            m.writer().writeByte(p.c.clan.typeclan);
        }
        m.writer().writeShort(39);
        m.writer().writeShort(40);
        m.writer().writeShort(41);
        m.writer().writeShort(42);
        m.writer().writeShort(43);
        m.writer().writeShort(44);
        m.writer().writeShort(45);
        m.writer().writeShort(46);
        m.writer().writeShort(47);
        m.writer().writeShort(48);
        m.writer().writeShort(49);
        m.writer().writeByte(50);
        m.writer().writeByte(51);
        m.writer().writeByte(p.c.nCave);
        // Bánh kỹ năng
        m.writer().writeByte(p.c.maxSTN);
        m.writer().writeByte(p.c.maxSKN);
        for (Item body : p.c.get().ItemBody) {
            if (body != null) {
                m.writer().writeShort(body.id);
                m.writer().writeByte(body.upgrade);
                m.writer().writeByte(body.sys);
            }
        }
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void viewOptionPlayers(Message m) throws IOException {
        int pid = m.reader().readInt();
        byte index = m.reader().readByte();
        m.cleanup();
        Char n = PlayerManager.getInstance().getNinja(pid);
        if (n == null || index < 0 || index > 15) {
            return;
        }
        Item item = n.get().ItemBody[index];
        if (item != null) {
            m = new Message(94);
            m.writer().writeByte(index);
            m.writer().writeLong(item.expires);
            m.writer().writeInt(item.saleCoinLock);
            m.writer().writeByte(item.sys);
            for (short i = 0; i < item.options.size(); i++) {
                m.writer().writeByte(item.options.get(i).id);
                m.writer().writeInt(item.options.get(i).param);
            }
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        }
    }

    public void endLoad(boolean canvas) throws IOException {
        Message m = new Message(126);
        m.writer().writeByte((canvas ? 0 : -1));
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public void addFriend(Message m) throws IOException {
        String nF = m.reader().readUTF();
        m.cleanup();
        if (nF.equals(c.name)) {
            sendAddchatYellow("Không thể thêm chính bản thân vào danh sách bạn bè.");
            return;
        }
        Player p = PlayerManager.getInstance().getNinja(nF).p;
        if (p == null) {
            sendAddchatYellow("Hiện tại người chơi này không online.");
            return;
        }
        JSONArray jar = (JSONArray) JSONValue.parse(c.friend);
        JSONObject job = new JSONObject();
        for (Object jar1 : jar) {
            job = (JSONObject) jar1;
            if (nF.equals(job.get("name").toString())) {
                sendAddchatYellow(nF + " đã có tên trong danh sách bạn bè hoặc thù địch.");
                return;
            }
            job.clear();
        }
        boolean agree = false;
        JSONArray jarF = (JSONArray) JSONValue.parse(p.c.friend);
        for (int i = 0; i < jarF.size(); i++) {
            JSONObject jobF = (JSONObject) jarF.get(i);
            if (jobF.get("name").toString().equals(c.name)) {
                agree = true;
                jobF.put("agree", agree);
                jarF.set(i, jobF);
                p.c.friend = jarF.toJSONString();
                break;
            }
        }
        job.put("name", nF);
        job.put("agree", agree);
        jar.add(job);
        c.friend = jar.toJSONString();
        if (!agree) {
            m = new Message(59);
            m.writer().writeUTF(c.name);
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
            sendAddchatYellow("Bạn đã thêm " + nF + " vào danh sách bạn bè.");
        } else {
            p.sendAddchatYellow(c.name + " đã trở thành bạn bè hữu hảo.");
            sendAddchatYellow(nF + " đã trở thành bạn bè hữu hảo.");
        }
    }

    public void itemMonToBag(Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        byte indexItemBag = c.getIndexBagNotItem();
        if (indexItemBag == 0) {
            conn.sendMessageLog("Hành trang không đủ chỗ trống");
            return;
        }
        if (index > 4 || index < 0 || c.get().ItemMounts[index] == null) {
            return;
        }
        if (index == 4 && (c.get().ItemMounts[0] != null || c.get().ItemMounts[1] != null || c.get().ItemMounts[2] != null || c.get().ItemMounts[3] != null)) {
            conn.sendMessageLog("Cần phải tháo hết trang bị thú cưới ra trước");
            return;
        }
        c.ItemBag[indexItemBag] = c.get().ItemMounts[index];
        c.get().ItemMounts[index] = null;
        m = new Message(108);
        m.writer().writeByte(c.get().speed());
        m.writer().writeInt(c.get().getMaxHP());
        m.writer().writeInt(c.get().getMaxMP());
        m.writer().writeShort(c.get().eff5buffHP());
        m.writer().writeShort(c.get().eff5buffMP());
        m.writer().writeByte(index);
        m.writer().writeByte(indexItemBag);
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
        for (Player player : c.place.players) {
            c.place.sendMounts(this.c.get(), player);
        }
    }

    public void changePassword() {
        if (!util.CheckString(passnew + passold, "^[a-zA-Z0-9]+$") || passnew.length() < 1 || passnew.length() > 30) {
            conn.sendMessageLog("Mật khẩu chỉ đồng ý các ký tự a-z,0-9 và chiều dài từ 1 đến 30 ký tự");
            return;
        }
        try {
            ResultSet red = SQLManager.stat.executeQuery("SELECT `id` FROM `player` WHERE (`password`LIKE'" + passold + "' AND `id` = " + id + ");");
            if (red == null || !red.first()) {
                conn.sendMessageLog("Mật khẩu cũ không chính xác!");
                return;
            }
            synchronized (Server.LOCK_MYSQL) {
                SQLManager.stat.executeUpdate("UPDATE `player` SET `password`='" + passnew + "' WHERE `id`=" + id + " LIMIT 1;");
            }
            conn.sendMessageLog("Đã đổi mật khẩu thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        JSONArray jarr = new JSONArray();
        byte i = 0;
        try {
            synchronized (Server.LOCK_MYSQL) {
                if (c != null) {
                    c.flush();
                    String n = sortNinja[0];
                    sortNinja[0] = c.name;
                    for (byte k = 1; k < sortNinja.length; k++) {
                        if (sortNinja[k] != null && sortNinja[k].equals(c.name)) {
                            sortNinja[k] = n;
                        }
                    }
                }
                //player
                for (byte k = 0; k < sortNinja.length; k++) {
                    if (sortNinja[k] != null) {
                        jarr.add(sortNinja[k]);
                    }
                }
                SQLManager.stat.executeUpdate("UPDATE `player` SET `luong`=" + luong + ",`ninja`='" + jarr.toJSONString() + "' WHERE `id`=" + id + " LIMIT 1;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void close() {
    }

    public void openBookSkill(byte index, byte sid) throws IOException {
        if (c.get().getSkill(sid) != null) {
            sendAddchatYellow("Bạn đã học kĩ năng này rồi");
            return;
        }
        c.ItemBag[index] = null;
        Skill skill = new Skill();
        skill.id = sid;
        skill.point = 1;
        c.get().skill.add(skill);
        viewInfoPlayers(this);
        loadSkill();
        Message m = new Message(-30);
        m.writer().writeByte(-102);
        m.writer().writeByte(index);
        m.writer().writeShort(SkillData.Templates(skill.id, skill.point).skillId);//id Skill
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
    }

    public synchronized void updateExp(long xpup) throws IOException {
        if (c.get().exptype == 0) {
            return;
        }
        if (c.get().expdown > 0) {
            c.get().expdown -= xpup;
            Message m = new Message(71);
            m.writer().writeLong(xpup);//xpdown up
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } else {
            if (c.nclass > 0) {
                Skill skill = c.getSkill(66 + c.nclass);
                if (skill != null && xpup >= 500000 && !c.isNhanban && c.timeRemoveClone > System.currentTimeMillis()) {
                    SkillData data = SkillData.Templates(skill.id);
                    if (data.maxPoint > skill.point && util.nextInt(50 * skill.point) == 0) {
                        skill.point++;
                        this.sendAddchatYellow(data.name + " đã đạt cấp " + skill.point);
                        this.loadSkill();
                    }
                }
            }
            c.get().expdown = 0L;
            long xpold = c.get().exp;
            c.get().exp += xpup;
            int level = c.get().level;
            c.get().setLevel_Exp(c.get().exp, true);
            if (c.get().level > 130) {
                c.get().level = 130;
                c.get().exp = Level.getMaxExp(131) - 1;
                return;
            }
            if (level < c.get().level) {
                if (c.get().nclass != 0) {
                    for (int i = level + 1; i <= c.get().level; i++) {
                        c.get().ppoint += Level.getLevel(i).ppoint;
                        c.get().spoint += Level.getLevel(i).spoint;
                    }
                } else {
                    for (int i = level + 1; i <= c.get().level; i++) {
                        c.get().potential0 += 5;
                        c.get().potential1 += 2;
                        c.get().potential2 += 2;
                        c.get().potential3 += 2;
                    }
                }
            }
            Message m = new Message(5);
            m.writer().writeLong(xpup);//xp up
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
            if (level != c.get().level) {
                c.setXPLoadSkill(c.get().exp);
            }
            c.clan.clevel = c.get().level;
        }
    }

    public void setEffect(int id, int timeStart, int timeLength, int param) {
        try {
            EffectData data = EffectData.entrys.get(id);
            Effect eff = c.get().getEffType(data.type);
            if (eff == null) {
                eff = new Effect(id, timeStart, timeLength, param);
                c.get().veff.add(eff);
                addEffectMessage(eff);
            } else {
                eff.template = data;
                eff.timeLength = timeLength;
                eff.timeStart = timeStart;
                eff.param = param;
                eff.timeRemove = (System.currentTimeMillis() - eff.timeStart) + eff.timeLength;
                setEffectMessage(eff);
            }
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public void addEffectMessage(Effect eff) throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-101);
        m.writer().writeByte(eff.template.id);//id template
        m.writer().writeInt(eff.timeStart);//time start
        m.writer().writeInt((int) (eff.timeRemove - System.currentTimeMillis()));//time length
        m.writer().writeShort(eff.param);//param
        if (eff.template.type == 2 || eff.template.type == 3 || eff.template.type == 14) {
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
        }
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
        m = new Message(-30);
        m.writer().writeByte(-98);
        m.writer().writeInt(c.get().id);
        m.writer().writeByte(eff.template.id);//id template
        m.writer().writeInt(eff.timeStart);//time start
        m.writer().writeInt((int) (eff.timeRemove - System.currentTimeMillis()));//time length
        m.writer().writeShort(eff.param);//param
        if (eff.template.type == 2 || eff.template.type == 3 || eff.template.type == 14) {
            m.writer().writeShort(c.get().x);
            m.writer().writeShort(c.get().y);
        }
        m.writer().flush();
        c.place.sendMessage(m);
        m.cleanup();
    }

    private void setEffectMessage(Effect eff) throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-100);
        m.writer().writeByte(eff.template.id);//id template
        m.writer().writeInt(eff.timeStart);//time start
        m.writer().writeInt(eff.timeLength);//time length
        m.writer().writeShort(eff.param);//param
        m.writer().flush();
        conn.sendMessage(m);
        m.cleanup();
        m = new Message(-30);
        m.writer().writeByte(-97);
        m.writer().writeInt(c.get().id);//id template
        m.writer().writeByte(eff.template.id);//id template
        m.writer().writeInt(eff.timeStart);//time start
        m.writer().writeInt(eff.timeLength);//time length
        m.writer().writeShort(eff.param);//param
        m.writer().flush();
        c.place.sendMessage(m);
        m.cleanup();
    }

    public void removeEffect(int id) {
        try {
            for (byte i = 0; i < c.get().veff.size(); i++) {
                Effect eff = c.get().veff.get(i);
                if (eff != null && eff.template.id == id) {
                    c.get().veff.remove(eff);
                    removeEffectMessage(eff);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void removeEffectMessage(Effect eff) {
        try {
            Message m = new Message(-30);
            m.writer().writeByte(-99);
            m.writer().writeByte(eff.template.id);//id template
            if (eff.template.type == 0 || eff.template.type == 12) {
                m.writer().writeInt(c.get().hp);
                m.writer().writeInt(c.get().mp);
            } else if (eff.template.type == 4 || eff.template.type == 13 || eff.template.type == 17) {
                m.writer().writeInt(c.get().hp);
            } else if (eff.template.type == 23) {
                m.writer().writeInt(c.get().hp);
                m.writer().writeInt(c.get().getMaxHP());
            }
            m.writer().flush();
            conn.sendMessage(m);
            m.writer().flush();
            m.cleanup();
            m = new Message(-30);
            m.writer().writeByte(-96);
            m.writer().writeInt(c.get().id);
            m.writer().writeByte(eff.template.id);//id template
            if (eff.template.type == 0 || eff.template.type == 12) {
                m.writer().writeInt(c.get().hp);
                m.writer().writeInt(c.get().mp);
            } else if (eff.template.type == 11) {
                m.writer().writeShort(c.get().x);
                m.writer().writeShort(c.get().y);
            } else if (eff.template.type == 4 || eff.template.type == 13 || eff.template.type == 17) {
                m.writer().writeInt(c.get().hp);
            } else if (eff.template.type == 23) {
                m.writer().writeInt(c.get().hp);
                m.writer().writeInt(c.get().getMaxHP());
            }
            m.writer().flush();
            c.place.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean updateSysMounts() {
        Item item = c.get().ItemMounts[4];
        if (item == null) {
            return false;
        }
        if (item.upgrade < 99) {
            c.p.sendAddchatYellow("Thú cưới chưa đạt cấp độ tối đa");
            return false;
        } else if (item.sys < 4) {
            if (20 / (item.sys + 1) > util.nextInt(100)) {
                item.sys++;
                item.upgrade = 0;
                for (byte i = 0; i < item.options.size(); i++) {
                    Option op = item.options.get(i);
                    if (op.id == 65) {
                        op.param = 0;
                    } else if (op.id != 66) {
                        for (byte j = 0; j < useItem.arrOp.length; j++) {
                            if (useItem.arrOp[j] == op.id) {
                                op.param -= (useItem.arrParam[j] * 8);
                                break;
                            }
                        }
                    }
                }
                try {
                    loadMounts();
                } catch (IOException e) {
                }
                c.p.sendAddchatYellow("Nâng cấp thành công, thú cưới được tặng 1 sao");
            } else {
                c.p.sendAddchatYellow("Nâng cấp thất bại, hao phí 1 Chuyển tinh thạch");
            }
        } else {
            c.p.sendAddchatYellow("Không thể nâng thêm sao");
            return false;
        }
        return true;
    }

    public boolean updateXpMounts(int xpup, byte type) {
        Item item = c.get().ItemMounts[4];
        if (item == null) {
            c.p.sendAddchatYellow("Bạn cần có thú cưới");
            return false;
        } else if (item.isExpires) {
            return false;
        } else if (type == 0 && item.id != 443 && item.id != 523 && item.id != 524) {
            c.p.sendAddchatYellow("Chỉ sử dụng cho thú cưới");
            return false;
        } else if (type == 1 && item.id != 485 && item.id != 524) {
            c.p.sendAddchatYellow("Chỉ sử dụng cho xe máy");
            return false;
        } else if (type == 2 && item.id != 776 && item.id != 777) {
            c.p.sendAddchatYellow("Chỉ sử dụng cho trâu");
            return false;
        } else if (item.upgrade < 99) {
            boolean isuplv = false;
            for (byte i = 0; i < item.options.size(); i++) {
                Option op = item.options.get(i);
                if (op.id == 65) {
                    op.param += xpup;
                    if (op.param >= 1000) {
                        isuplv = true;
                        op.param = 0;
                    }
                    break;
                }
            }
            if (isuplv) {
                item.upgrade++;
                int lv = item.upgrade + 1;
                if (lv == 10 || lv == 20 || lv == 30 || lv == 40 || lv == 50 || lv == 60 || lv == 70 || lv == 80 || lv == 90) {
                    for (byte i = 0; i < item.options.size(); i++) {
                        Option op = item.options.get(i);
                        if (op.id != 65 && op.id != 66) {
                            for (byte k = 0; k < useItem.arrOp.length; k++) {
                                if (useItem.arrOp[k] == op.id) {
                                    op.param += useItem.arrParam[k];
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            try {
                loadMounts();
            } catch (IOException e) {
            }
        } else {
            c.p.sendAddchatYellow("Thú cưới đã đạt cấp tối đa");
            return false;
        }
        return true;
    }

    public void loadMounts() throws IOException {
        Message m = new Message(-30);
        m.writer().writeByte(-54);
        m.writer().writeInt(c.get().id);
        for (byte i = 0; i < c.get().ItemMounts.length; i++) {
            Item item = c.get().ItemMounts[i];
            if (item != null) {
                m.writer().writeShort(item.id);
                m.writer().writeByte(item.upgrade);
                m.writer().writeLong(item.expires);
                m.writer().writeByte(item.sys);
                m.writer().writeByte(item.options.size());
                for (byte j = 0; j < item.options.size(); j++) {
                    m.writer().writeByte(item.options.get(j).id);
                    m.writer().writeInt(item.options.get(j).param);
                }
            } else {
                m.writer().writeShort(-1);
            }
        }
        m.writer().flush();
        c.place.sendMessage(m);
        m.cleanup();
    }

    public boolean dungThucan(byte id, int param, int thoigian) {
        Effect eff = c.get().getEffType((byte) 0);
        if (c.get().pk > 14) {
            sendAddchatYellow("Điểm hiếu chiến quá cao không thể dùng được vật phẩm này");
            return false;
        }
        if (eff != null && eff.param > param) {
            this.sendAddchatYellow("Đã có hiệu quả thức ăn cao hơn");
            return false;
        }
        setEffect(id, 0, 1000 * thoigian, param);
        return true;
    }

    public boolean buffHP(int param) {
        Effect eff = c.get().getEffType((byte) 17);
        if (eff != null) {
            return false;
        }
        if (c.get().pk > 14) {
            sendAddchatYellow("Điểm hiếu chiến quá cao không thể dùng được vật phẩm này");
            return false;
        }
        if (c.get().hp >= c.get().getMaxHP()) {
            sendAddchatYellow("HP đã đầy");
            return false;
        }
        setEffect(21, 0, 3000, param);
        return true;
    }

    public boolean buffMP(int param) {
        if (c.get().pk > 14) {
            sendAddchatYellow("Điểm hiếu chiến quá cao không thể dùng được vật phẩm này");
            return false;
        }
        if (c.get().mp >= c.get().getMaxMP()) {
            sendAddchatYellow("MP đã đầy");
            try {
                getMp();
            } catch (IOException e) {
            }
            return false;
        }
        c.get().upMP(param);
        try {
            getMp();
        } catch (IOException e) {
        }
        return true;
    }

    public void mobMeMessage(int id, byte boss) {
        try {
            if (id > 0) {
                Mob mob = new Mob(-1, id, 0);
                mob.sys = 1;
                mob.status = 5;
                mob.hp = mob.hpmax = 0;
                mob.isboss = boss != 0;
                c.get().mobMe = mob;
            } else {
                c.get().mobMe = null;
            }
            Message m = new Message(-30);
            m.writer().writeByte(-69);
            m.writer().writeByte(id);
            m.writer().writeByte(boss);
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
            if (c.place == null) {
                return;
            }
            m = new Message(-30);
            m.writer().writeByte(-68);
            m.writer().writeInt(c.get().id);
            m.writer().writeByte(id);
            m.writer().writeByte(boss);
            m.writer().flush();
            c.place.sendMyMessage(this, m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTimeMap(int timeLength) {
        try {
            Message m = new Message(-30);
            m.writer().writeByte(-95);
            m.writer().writeInt(timeLength);//char atk
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPointPB(int point) {
        try {
            Message m = new Message(-28);
            m.writer().writeByte(-84);
            m.writer().writeShort(point);
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restCave() {
        try {
            Message m = new Message(-16);
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rewardedCave() {
        int num = c.pointCave / 10;
        if (num > 0) {
            if (c.getBagNull() == 0) {
                conn.sendMessageLog("Hành trang không đủ chỗ trống");
                return;
            }
            Item item;
            if (c.level < 50) {
                item = new Item();
                item.id = 272;
            } else if (c.level < 90) {
                item = new Item();
                item.id = 282;
            } else {
                item = new Item();
                item.id = 647;
            }
            item.quantity = num;
            c.addItemBag(true, item);
            c.pointCave = 0;
            if (c.bagCaveMax < num) {
                c.bagCaveMax = num;
                c.itemIDCaveMax = item.id;
            }
        }
    }

    public void chatParty(Message m) throws IOException {
        String text = m.reader().readUTF();
        m.cleanup();
        if (c.get().party != null) {
            m = new Message(-20);
            m.writer().writeUTF(c.name);
            m.writer().writeUTF(text);
            m.writer().flush();
            for (byte i = 0; i < c.get().party.ninjas.size(); i++) {
                c.get().party.ninjas.get(i).p.conn.sendMessage(m);
            }
            m.cleanup();
        }
    }

    public void addParty(Message m) throws IOException {
        String name = m.reader().readUTF();
        m.cleanup();
        Char n = PlayerManager.getInstance().getNinja(name);
        if (n != null) {
            if (n.get().party != null) {
                this.sendAddchatYellow("Đối phương đã có nhóm");
            } else if (c.get().party != null) {
                if (c.get().party.master != c.id) {
                    this.sendAddchatYellow("Bạn không phải nhóm trưởng");
                } else {
                    c.get().party.addParty(this, n.p);
                }
            } else {
                c.get().party = new Party(c);
                c.get().party.addPartyAccept(c);
                c.get().party.addParty(this, n.p);
            }
        }
    }

    public void addPartyAccept(Message m) throws IOException {
        int charId = m.reader().readInt();
        m.cleanup();
        if (c.party != null) {
            return;
        }
        Char n = PlayerManager.getInstance().getNinja(charId);
        if (n != null && n.party != null) {
            Party party = n.party;
            if (party.ninjas.size() > 5) {
                this.sendAddchatYellow("Nhóm đã đủ thành viên");
                return;
            }
            for (short i = 0; i < party.pt.size(); i++) {
                if (party.pt.get(i) == conn.id) {
                    party.pt.remove(i);
                    party.addPartyAccept(c);
                    party.refreshTeam();
                    return;
                }
            }
        } else {
            this.sendAddchatYellow("Nhóm này đã không tồn tại");
        }
    }

    public void moveMemberParty(Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        if (c.get().party != null && c.get().id == c.get().party.master && index >= 0 && index < c.get().party.ninjas.size()) {
            Char n = c.get().party.ninjas.get(index);
            if (n.id != c.id) {
                c.get().party.moveMember(index);
            }
        }
    }

    public void changeTeamLeaderParty(Message m) throws IOException {
        byte index = m.reader().readByte();
        m.cleanup();
        if (c.get().party != null && c.id == c.get().party.master && index >= 0 && index < c.get().party.ninjas.size()) {
            c.get().party.changeTeamLeader(index);
        }
    }

    private void createParty() {
        if (c.get().party == null) {
            Party party = new Party(c);
            party.addPartyAccept(c);
        }
    }

    public void getMobMe() {
        if (c.get().ItemBody[10] != null) {
            switch (c.get().ItemBody[10].id) {
                case 246:
                    mobMeMessage(70, (byte) 0);
                    break;
                case 419:
                    mobMeMessage(122, (byte) 0);
                    break;
                case 568:
                    mobMeMessage(205, (byte) 0);
                    break;
                case 569:
                    mobMeMessage(206, (byte) 0);
                    break;
                case 570:
                    mobMeMessage(207, (byte) 0);
                    break;
                case 571:
                    mobMeMessage(208, (byte) 0);
                    break;
                case 583:
                    mobMeMessage(211, (byte) 1);
                    break;
                case 584:
                    mobMeMessage(212, (byte) 1);
                    break;
                case 585:
                    mobMeMessage(213, (byte) 1);
                    break;
                case 586:
                    mobMeMessage(214, (byte) 1);
                    break;
                case 587:
                    mobMeMessage(215, (byte) 1);
                    break;
                case 588:
                    mobMeMessage(216, (byte) 1);
                    break;
                case 589:
                    mobMeMessage(217, (byte) 1);
                    break;
                case 742:
                    mobMeMessage(229, (byte) 1);
                    break;
                case 781:
                    mobMeMessage(235, (byte) 1);
                    break;
                default:
                    mobMeMessage(0, (byte) 0);
                    break;
            }
        } else {
            mobMeMessage(0, (byte) 0);
        }
    }

    public void toNhanBan() {
        if (!c.isNhanban) {
            synchronized (c) {
                if (c.party != null) {
                    c.party.exitParty(c);
                }
                for (byte n = 0; n < c.get().veff.size(); n++) {
                    removeEffectMessage(c.get().veff.get(n));
                }
                c.isNhanban = true;
                c.isHuman = false;
                c.clone.x = c.x;
                c.clone.y = c.y;
                c.place.removeMessage(c.clone.id);
                c.place.removeMessage(c.id);
                Service.CharViewInfo(this);
                GameScr.sendSkill(this, "KSkill");
                GameScr.sendSkill(this, "OSkill");
                GameScr.sendSkill(this, "CSkill");
                for (int i = c.place.players.size() - 1; i >= 0; i--) {
                    Player player = c.place.players.get(i);
                    if (player.id != id) {
                        c.place.sendCharInfo(this, player);
                        c.place.sendCoat(this.c.get(), player);
                        c.place.sendGlove(this.c.get(), player);
                    }
                    c.place.sendMounts(this.c.get(), player);
                }
            }
            PlayerManager.getInstance().put(c);
        }
    }

    public void exitNhanBan(boolean isdie) {
        if (c.isNhanban) {
            synchronized (c) {
                if (c.clone.party != null) {
                    c.clone.party.exitParty(c);
                }
                for (byte n = 0; n < c.get().veff.size(); n++) {
                    removeEffectMessage(c.get().veff.get(n));
                }
                c.isNhanban = false;
                c.isHuman = true;
                if (isdie) {
                    c.clone.isDie = isdie;
                } else {
                    c.clone.refresh();
                }
                c.x = c.clone.x;
                c.y = c.clone.y;
                c.place.removeMessage(c.clone.id);
                Service.CharViewInfo(this);
                GameScr.sendSkill(this, "KSkill");
                GameScr.sendSkill(this, "OSkill");
                GameScr.sendSkill(this, "CSkill");
                for (int i = c.place.players.size() - 1; i >= 0; i--) {
                    Player player = c.place.players.get(i);
                    if (player.id != id) {
                        c.place.sendCharInfo(this, player);
                        c.place.sendCoat(this.c.get(), player);
                        c.place.sendGlove(this.c.get(), player);
                    }
                    c.place.sendMounts(this.c.get(), player);
                }
                if (!isdie) {
                    for (short i = 0; i < c.place.players.size(); i++) {
                        Service.sendclonechar(c.p, c.place.players.get(i));
                    }
                }
            }
            PlayerManager.getInstance().put(c);
        }
    }

    public void requestSolo(Message m) throws IOException {
        int ids = m.reader().readInt();

        Player _p = c.place.getNinja(ids).p;

        m = new Message(65);
        m.writer().writeInt(c.id);
        m.writer().flush();
        _p.conn.sendMessage(m);
        m.cleanup();
    }

    public void startSolo(Message m) throws IOException {
        int pid = m.reader().readInt();

        Player _p = c.place.getNinja(pid).p;

        if (_p != null) {
            _p.solo = new Solo();
            this.solo = new Solo();

            this.c.typeSolo = 1;
            _p.c.typeSolo = 1;

            _p.soloer = this.solo.sl_1;
            _p.soloer.player = _p;

            this.soloer = this.solo.sl_2;
            this.soloer.player = this;

            this.solo.start();
        }
    }

    public void endSolo(Message m) throws IOException {
        solo.endSolo();
    }

//    public void acceptPk(Message m) throws IOException {
//        int id = m.reader().readInt();
//        m.cleanup();
//        Player p = c.place.getNinja(id).p;
//        p.setEffect(14, 0, 10000, 0);
//        this.setEffect(14, 0, 10000, 0);
//
//        Map ma = Manager.getMapid(111);
//        for (Place area : ma.area) {
//            if (area.numplayers < ma.template.maxplayers) {
//                p.c.place.leave(p);
//                area.EnterMap0(p.c);
//                c.place.leave(this);
//                area.EnterMap0(c);
//                return;
//            }
//        }
//        return;
//    }

    public void invitePk(Message m) throws IOException {
        int id = m.reader().readInt();
        m.cleanup();
        Player p = c.place.getNinja(id).p;
        m = new Message(99);
//            m = new Message(99);
        m.writer().writeInt(c.id);
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }
    
   public void fish() throws InterruptedException, IOException {
        
        if (c.y != 288  || c.mapid != 22){
            this.conn.sendMessageLog("Bạn không thể câu cá tại đây!");
            return;
        }
        sendAddchatYellow("Đang thả cần ... vui lòng chờ cá cắn câu");
        Thread.sleep(10000);
        int[] idC = {
            652,653,654,655,652,653,654,655, // Ngọc
            548,548,548,548,548,548,548,548,548,548, // Cần câu
            573,574,575,576,577,578,573,574,575,576,577,578,573,574,575,576,577,578, // EXP Thú cưỡi
            760,761,762,763,764,765,766,767,768,760,761,762,763,764,765,766,767,768,760,761,762,763,764,765,766,767,768, // Mảnh Đồ Jumoti
            733,734,735,736,737,738,739,740,741,733,734,735,736,737,738,739,740,741,733,734,735,736,737,738,739,740,741, // Mảnh Đồ Jarai
            648,649,650,651,648,649,650,651,648,649,650,651,648,649,650,651,648,649,650,651, // Huy chương 4 loại
            384,385 // Rương Bạch Ngân, Rương Huyền Bí
            };
        if (util.nextInt(100) > 60){
            updateExp(1000000);
        }else {
            Item it;
            int iD = idC[util.nextInt(idC.length)];
            if (iD == 652 || iD == 653 || iD == 654 || iD == 655){ // Ngọc
                it = ItemData.itemDefault(iD);
                it.upgrade = 1;
            } else {
                it = ItemData.itemDefault(iD);
                it.upgrade = 0;
            }
            if (ItemData.ItemDataId(iD).isUpToUp){
                c.addItemBag(true,it);
            } else {
                c.addItemBag(false,it);
            }
        }
        c.removeItemBags(548,1);
    }
   
    public void setPointCT(int point) {
        try {
            Message m = new Message(-28);
            m.writer().writeByte(-81);
            m.writer().writeShort(point);
            m.writer().flush();
            conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void rewardedCT() throws IOException {
        final int num = this.c.pointCT;
        if (num > 0 && this.c.rewardedCT == 0) {
            if (this.c.getBagNull() == 0) {
                this.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                return;
            }
            Item item;
            if (num > 199 && num < 600) {
                this.updateExp(5000000L);
                item = new Item();
                item.id = 3;
                item.quantity = 1;
                this.c.addItemBag(true, item);
            } else if (num > 599 && num < 1500) {
                this.updateExp(10000000L);
                item = new Item();
                item.id = 4;
                item.quantity = 1;
                this.c.addItemBag(true, item);
            } else if (num > 1499 && num < 4000) {
                this.updateExp(15000000L);
                item = new Item();
                item.id = 6;
                item.quantity = 2;
                this.c.addItemBag(true, item);
            } else if (num > 4000) {
                this.updateExp(30000000L);
                item = new Item();
                item.id = 6;
                item.quantity = 1;
                this.c.addItemBag(true, item);
                item = new Item();
                item.id = (short) util.nextInt(275, 278);
                item.quantity = 1;
                this.c.addItemBag(true, item);        
            }
            if (this.c.typeCT == this.c.place.map.war.win) {
                item = new Item();
                item.id = 4;
                item.quantity = 1;
                this.c.addItemBag(true, item);
            }
            this.c.rewardedCT = 1;
        }
    }
    // Lôi đài
    public void acceptPk(Message m) throws IOException, InterruptedException {
        int id = m.reader().readInt();
        idloidai2 = id;
        m.cleanup();
        Player p = c.place.getNinja(id).p;
        p.c.xuLoiDai = 0;
        Map ma = Manager.getMapid(110);
        for (Place area : ma.area) {
            if (area.numplayers < ma.template.maxplayers) {
                p.c.place.leave(p);
                area.EnterMap0(p.c);
                c.place.leave(this);
                area.EnterMap1(c);
                return;
            }
        }
        return;
    }

    public void giveUp() {
        Player p = c.place.getNinja(idloidai2).p;
        Map ma = Manager.getMapid(22);
        for (Place area : ma.area) {
            if (area.numplayers < ma.template.maxplayers) {
                p.c.place.leave(p);
                area.EnterMap0(p.c);
                p.conn.sendMessageLog("Đối thủ của bạn đã bỏ chạy");
                this.c.place.leave(this);
                area.EnterMap0(this.c);
                return;
            }
        }

    }

    public int idloidai;
    public int idloidai2;

    public void inviteLD(Player p1, Player p2) throws IOException {
        idloidai2 = p2.c.id;
        p1.c.xuLoiDai = 0;
        Message m = new Message(99);
        m.writer().writeInt(p1.c.id);
        m.writer().flush();
        p2.conn.sendMessage(m);
        m.cleanup();
    }

    public int xuLoiDai(int xu) throws IOException, InterruptedException {
        Player p = c.place.getNinja(idloidai2).p;
        if (xu > p.c.xu) {
            this.conn.sendMessageLog("Bạn không đủ xu.");
            return 0;
        }
        p.conn.sendMessageLog("" + this.c.name + " đặt cược " + xu +" ");
        p.c.xuLoiDai = xu;
        if (this.c.xuLoiDai == p.c.xuLoiDai && p.c.xuLoiDai != 0) {
            server.manager.chatKTG("" + p.c.name + " đang thách đấu với " + this.c.name + " tại lôi đài tử chiến, cược " + xu + " xu, hãy mau qua xem.");
            Thread.sleep(3000);
            Map ma = Manager.getMapid(111);
            for (Place area : ma.area) {
                if (area.numplayers < ma.template.maxplayers) {
                    p.c.place.leave(p);
                    area.EnterMap0(p.c);
                    this.c.place.leave(this);
                    area.EnterMap1(this.c);
                }
            }
            p.setEffect(14, 0, 10000, 0);
            this.setEffect(14, 0, 10000, 0);
            p.c.changePk(p.c, (byte) 5);
            this.c.changePk(this.c, (byte) 4);
        }
        return xu;
    }
// XSMB bên Draw

    public void submitNumLucky(int num) throws SQLException {
        ResultSet red = SQLManager.stat.executeQuery("SELECT `xoso` FROM `player` WHERE `username`='"+username+"' LIMIT 1;");
        red.first();
        int numLk = red.getInt("xoso");
        red.close();
        if (numLk != -1){
            conn.sendMessageLog("Không thể đặt thêm, hôm nay bạn đã đặt cược hết số lần rồi!");
            return;
        } else {
            SQLManager.stat.executeUpdate("UPDATE `player` SET `xoso`=" + num +" WHERE `id`=" + id + " ;");
            this.numLucky = num;
            server.menu.sendWrite(this,(short) 1405, "Nhập tiền cược");
            return;
        }
    }

    public void submitCoinLucky(int num) throws SQLException {
        ResultSet red = SQLManager.stat.executeQuery("SELECT `coinXS` FROM `player` WHERE `username`='"+username+"' LIMIT 1;");
        red.first();
        int coinLk = red.getInt("coinXS");
        red.close();
        if (coinLk != -1){
            conn.sendMessageLog("Không thể đặt thêm, hôm nay bạn đã đặt cược hết số lần rồi!");
            return;
        } else {
            SQLManager.stat.executeUpdate("UPDATE `player` SET `coinXS`=" + num +" WHERE `id`=" + id + " ;");
            this.coinLucky = num;
            this.upluongMessage(-num); // Trừ lượng khi đặt
            conn.sendMessageLog("Bạn đã đặt số " + this.numLucky + " với " + this.coinLucky + " lượng");

        }
        return;
    }
    // Giftcode
    public void Giftcode(String str) {
        try {
            synchronized (Server.LOCK_MYSQL) {
                final ResultSet red = SQLManager.stat.executeQuery("SELECT * FROM `giftcode` WHERE (`gift`LIKE'" + str + "');");
                if (red != null && red.first()) {
                    final int id = red.getInt("id");
                    final String gift = red.getString("gift");
                    final int type = red.getInt("type");
                    int count = red.getInt("count");
//                    Date date = util.getDate(red.getString("date"));
                    if (count < 1) {
                        this.conn.sendMessageLog("Số lần sử dụng mã quà tặng này đã hết");
                        return;
                    }
//                    if (util.compare_Sec(Date.from(Instant.now()), date)) {
//                        this.conn.sendMessageLog("Mã quà tặng này đã hết hạn. Vui lòng thử lại với mã quà tặng khác.");
//                        return;
//                    }
                    // Phần thưởng ứng với các type cài trong SQL
                    switch (type) {
                        case 0: { 
                            this.c.upyenMessage(1000000L);
                            break;
                        }
                        case 1: {
                            this.c.upxuMessage(1000000L);
                            break;
                        }
                        case 2: {
                            this.upluongMessage(1000000L);
                            break;
                        }
                    }
                    count -= 1;
                    SQLManager.stat.executeUpdate("UPDATE `giftcode` SET `count`='" + count + "'" + " WHERE `id`=" + id + " LIMIT 1;");
                } else {
                    this.conn.sendMessageLog("Mã quà tặng không đúng. Vui lòng kiểm tra lại.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
