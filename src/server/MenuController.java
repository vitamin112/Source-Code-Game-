package server;

/**
 *
 * @author Dũng Trần
 */

import boardGame.Place;
import io.Message;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import real.Cave;
import real.ClanManager;
import real.Item;
import real.ItemData;
import real.LDGT; // LDGT
import real.Level;
import real.Map;
import real.Player;

public class MenuController {

    Server server = Server.getInstance();
    public Message message;

    public void sendMenu(Player p, Message m) throws IOException, SQLException {
        byte b1 = m.reader().readByte();// ID NPC
        byte b2 = m.reader().readByte();// Lớp nút 1
        byte b3 = m.reader().readByte();// Lớp nút 2
        switch (p.typemenu) {
            //menu npc Katana
            // Lôi đài
            case 0:
                switch (p.c.mapid) {
                    case 110: {
                        if (b2 == 0) {
                            p.giveUp();
                            break;
                        } else if (b2 == 1 && p.c.mapid == 110) {
                            this.sendWrite(p, (short) 54, "Nhập xu đặt cược");
                            p.idloidai = p.c.id;
                            break;
                        }
                        break;
                    }
                }
                if (b2 == 0 && p.c.mapid != 22) {
                    p.requestItem(2);
                } else if (b2 == 1 && p.c.mapid != 110) {
                    if (b3 == 0) {
                        if (!p.c.clan.clanName.isEmpty()) {
                            p.c.place.chatNPC(p, (short) b1, "Hiện tại con đã có gia tộc không thể thành lập thêm được nữa.");
                        } else if (p.luong < 1500000) {
                            p.c.place.chatNPC(p, (short) b1, "Để thành lập gia tộc con cần phải cóc đủ 1.500.000 lượng trong người.");
                        } else {
                            this.sendWrite(p, (short) 50, "Tên gia tộc");
                        }
                    }
                    // LDGT
                    if (b3 == 1) {
                        LDGT ldgt = null;
                        ClanManager clan = null;
                        clan = ClanManager.getClanName(p.c.clan.clanName);
                        if ((p.c.clan.typeclan == 3 || p.c.clan.typeclan == 4) && clan.ldgtID == -1) {
                            ldgt = new LDGT(3);
                            clan.openLDGT(ldgt, p.c.name);
                            p.c.place.leave(p);
                            ldgt.map[0].area[0].EnterMap0(p.c);
                            Item itemup = ItemData.itemDefault(260);
                            p.c.addItemBag(false, itemup);
                            p.c.ldgtID = ldgt.ldgtID;
                            clan.ldgt = ldgt;
                            clan.ldgtID = ldgt.ldgtID;
                            ldgt = LDGT.ldgt.get(p.c.ldgtID);
                            if (p.c.ldgtNum == -1) {
                                clan.ldgt.ninjas.add(p.c);
                                p.c.ldgtNum = 0;
                            }
                            SQLManager.stat.executeUpdate("UPDATE `ninja` SET `ldgtNum`='" + p.c.ldgtNum + "' WHERE `id`=" + p.c.id + " LIMIT 1;");
                        } else if (p.c.clan != null && p.c.clan.clanName != "" && clan.ldgt != null) {
                            if (p.c.ldgtNum == -1) {
                                clan.ldgt.ninjas.add(p.c);
                                p.c.ldgtNum = 0;
                            }
                            ldgt = clan.ldgt;
                            p.c.ldgtID = ldgt.ldgtID;
                            ldgt = LDGT.ldgt.get(p.c.ldgtID);
                            p.c.place.leave(p);
                            ldgt.map[0].area[0].EnterMap0(p.c);
                            SQLManager.stat.executeUpdate("UPDATE `ninja` SET `ldgtNum`='" + p.c.ldgtNum + "' WHERE `id`=" + p.c.id + " LIMIT 1;");
                        } else {
                            p.conn.sendMessageLog("Bạn chưa có gia tộc hoặc lãnh địa gia tộc chưa mở");
                            break;
                        }
                        break;
                    }

                } else if (b2 == 2) {
                    if (p.c.isNhanban) {
                        p.conn.sendMessageLog("Chức năng này không dành cho phân thân");
                        return;
                    }
                    if (b3 == 0) {
                        Service.evaluateCave(p.c);
                    } else {
                        Cave cave = null;
                        if (p.c.caveID != -1) {
                            if (Cave.caves.containsKey(p.c.caveID)) {
                                cave = Cave.caves.get(p.c.caveID);
                                p.c.place.leave(p);
                                cave.map[0].area[0].EnterMap0(p.c);
                            }
                        } else if (p.c.party != null) {
                            if (p.c.party.cave == null && p.c.party.master != p.c.id) {
                                p.conn.sendMessageLog("Chỉ có nhóm trưởng mới được phép mở cửa hang động");
                                return;
                            }
                        }
                        if (cave == null) {
                            if (p.c.nCave <= 0) {
                                p.c.place.chatNPC(p, (short) b1, "Số lần vào hang động cảu con hôm nay đã hết hãy quay lại vào ngày mai.");
                                return;
                            }
                            if (b3 == 1) {
                                if (p.c.level < 30 || p.c.level > 39) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 30 || p.c.party.ninjas.get(i).level > 39) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null) {
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(3);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(3);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 2) {
                                if (p.c.level < 40 || p.c.level > 49) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 40 || p.c.party.ninjas.get(i).level > 49) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null) {
                                    if (p.c.party.cave == null) {
                                    cave = new Cave(4);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(4);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 3) {
                                if (p.c.level < 50 || p.c.level > 59) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 50 || p.c.party.ninjas.get(i).level > 59) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null) {
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(5);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(5);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 4) {
                                if (p.c.level < 60 || p.c.level > 69) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                } else if (p.c.party != null && p.c.party.ninjas.size() > 1) {
                                    p.conn.sendMessageLog("Hoạt động lần này chỉ được phép một mình");
                                    return;
                                }
                                cave = new Cave(6);
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 5) {
                                if (p.c.level < 70 || p.c.level > 89) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
}
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 70 || p.c.party.ninjas.get(i).level > 89) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null) {
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(7);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(7);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (b3 == 6) {
                                if (p.c.level < 90 || p.c.level > 130) {
                                    p.conn.sendMessageLog("Trình độ không phù hợp");
                                    return;
                                }
                                if (p.c.party != null) {
                                    synchronized (p.c.party.ninjas) {
                                        for (byte i = 0; i < p.c.party.ninjas.size(); i++) {
                                            if (p.c.party.ninjas.get(i).level < 90 || p.c.party.ninjas.get(i).level > 131) {
                                                p.conn.sendMessageLog("Thành viên trong nhóm trình độ không phù hợp");
                                                return;
                                            }
                                        }
                                    }
                                }
                                if (p.c.party != null) {
                                    if (p.c.party.cave == null) {
                                        cave = new Cave(9);
                                        p.c.party.openCave(cave, p.c.name);
                                    } else {
                                        cave = p.c.party.cave;
                                    }
                                } else {
                                    cave = new Cave(9);
                                }
                                p.c.caveID = cave.caveID;
                            }
                            if (cave != null) {
                                p.c.nCave--;
                                p.c.pointCave = 0;
                                p.c.place.leave(p);
                                cave.map[0].area[0].EnterMap0(p.c);
                            }
                        }
                        p.setPointPB(p.c.pointCave);
                    }
                // Lôi đài
                } else if (b2 == 3) {
                    switch (b3) {
                        case 0: {
                            Map ma = server.manager.getMapid(111);
                            for (Place area : ma.area) {
                                if (area.numplayers > 1) {
                                    p.conn.sendMessageLog("Vui lòng chờ cho trận đấu đang diễn ra kết thúc");
                                    return;
                                }
                            }
                            Map ma1 = server.manager.getMapid(110);
                            for (Place area : ma1.area) {
                                if (area.numplayers > 1) {
                                    p.conn.sendMessageLog("Vui lòng chờ cho trận đấu đang diễn ra kết thúc");
                                    return;
                                }
                            }
                            this.sendWrite(p, (short) 53, "Nhập tên đối thủ");
                            break;
                        }
                        
                        case 1: {
                            Map ma2 = server.manager.getMapid(111);
                            for (Place area : ma2.area) {
                                if (area.numplayers > 1) {
                                    p.c.place.leave(p);
                                    area.EnterMap3(p.c);
                                    return;
                                } else {
                                    p.conn.sendMessageLog("Hiện không có trận đấu nào đang diễn ra");
                                }
                                break;
                            }
                            break;
                        }

                    }
                    break;
                }
                break;
           
                //menu npc Furoya
            case 1:
                if (b2 == 0) {
                    if (b3 == 0) {
                        p.requestItem(21 - p.c.gender);
                    } else if (b3 == 1) {
                        p.requestItem(23 - p.c.gender);
                    } else if (b3 == 2) {
                        p.requestItem(25 - p.c.gender);
                    } else if (b3 == 3) {
                        p.requestItem(27 - p.c.gender);
                    } else if (b3 == 4) {
                        p.requestItem(29 - p.c.gender);
                    }
                }
                break;
                //menu npc Ameji
            case 2:
                if (b2 == 0) {
                    if (b3 == 0) {
                        p.requestItem(16);
                    } else if (b3 == 1) {
                        p.requestItem(17);
                    } else if (b3 == 2) {
                        p.requestItem(18);
                    } else if (b3 == 3) {
                        p.requestItem(19);
                    }
                }
                break;
                //menu npc Kiriko
            case 3:
                if (b2 == 0) {
                    p.requestItem(7);
                } else if (b2 == 1) {
                    p.requestItem(6);
                }
                break;
                //menu npc Tabemono
            case 4:
                switch (b2) {
                    case 0:
                        p.requestItem(9);
                        break;
                    case 1:
                        p.requestItem(8);
                        break;
                }
                break;
                //menu npc Kamakura
            case 5:
                switch (b2) {
                    case 0:
                        p.requestItem(4);
                        break;
                    case 1:
                        p.c.mapLTD = p.c.place.map.id;
                        p.c.place.chatNPC(p, (short) b1, "Lưu tọa độ thành công, khi kiệt sức con sẽ được khiêng về đây");
                        break;
                    case 2:
                        if (b3 == 0) {
                            if (p.c.isNhanban) {
                                p.conn.sendMessageLog("Chức năng này không dành cho phân thân");
                                return;
                            }
                            if (p.c.level < 60) {
                                p.conn.sendMessageLog("Chức năng yêu cầu trình độ 60");
                                return;
                            }
                            Map ma = server.manager.getMapid(139);
                            for (Place area : ma.area) {
                                if (area.numplayers < ma.template.maxplayers) {
                                    p.c.place.leave(p);
                                    area.EnterMap0(p.c);
                                    return;
                                }
                            }
                        }
                        break;
                }
                break;
                //menu npc Kenshinto
            case 6:
                switch (b2) {
                    case 0:
                        if (b3 == 0) {
                            p.requestItem(10);
                        } else if (b3 == 1) {
                            p.requestItem(31);
                        }
                        break;
                    case 1:
                        if (b3 == 0) {
                            p.requestItem(12);
                        } else if (b3 == 1) {
                            p.requestItem(11);
                        }
                        break;
                    case 2:
                        p.requestItem(13);
                        break;
                    case 3:
                        p.requestItem(33);
                        break;
                    case 4:
                        p.requestItem(46);
                        break;
                    case 5:
                        p.requestItem(47);
                        break;
                    case 6:
                        p.requestItem(49);
                        break;
                    case 7:
                        p.requestItem(50);
                        break;
                }
                break;
                //menu noc Umayaki
            case 7:
                if (b2 == 0) {
                } else if (b2 > 0 && b2 <= Map.arrLang.length) {
                    Map ma = Manager.getMapid(Map.arrLang[b2 - 1]);
                    for (Place area : ma.area) {
                        if (area.numplayers < ma.template.maxplayers) {
                            p.c.place.leave(p);
                            area.EnterMap0(p.c);
                            return;
                        }
                    }
                }
                break;
                //menu noc Umayaki
            case 8:
                if (b2 >= 0 && b2 < Map.arrTruong.length) {
                    Map ma = Manager.getMapid(Map.arrTruong[b2]);
                    for (Place area : ma.area) {
                        if (area.numplayers < ma.template.maxplayers) {
                            p.c.place.leave(p);
                            area.EnterMap0(p.c);
                            return;
                        }
                    }
                } else {
                    
                }
                break;
                //menu npc cô toyotomi
            case 9:
                if (b2 == 0) {
                    if (b3 == 0) {
                        server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                    } else if (b3 == 1) {
                        server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                    } else if (b3 == 2) {
                        server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                    } else if (b3 == 3) {
                        server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                    }
                }
                if (b2 == 1) {
                    if (p.c.get().nclass > 0) {
                        p.c.place.chatNPC(p, (short) b1, "Con đã vào lớp từ trước rồi mà");
                    } else if (p.c.get().ItemBody[1] != null) {
                        p.c.place.chatNPC(p, (short) b1, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                    } else if (p.c.getBagNull() < 3) {
                        p.c.place.chatNPC(p, (short) b1, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                    } else {
                        p.c.addItemBag(false, ItemData.itemDefault(420));
                        if (b3 == 0) {
                            p.Admission((byte) 1);
                        } else if (b3 == 1) {
                            p.Admission((byte) 2);
                        }
                        p.c.place.chatNPC(p, (short) b1, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                    }
                } else if (b2 == 2) {
                    if (p.c.get().nclass != 1 && p.c.get().nclass != 2) {
                        p.c.place.chatNPC(p, (short) b1, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                    } else {
                        if (b3 == 0) {
                            p.restPpoint();
                            p.c.place.chatNPC(p, (short) b1, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                        } else if (b3 == 1) {
                            p.restSpoint();
                            p.c.place.chatNPC(p, (short) b1, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                        }
                    }
                }
                break;
                //menu npc cô Ookamesama
            case 10:
                if (b2 == 0) {
                    if (b3 == 0) {
                        server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                    } else if (b3 == 1) {
                        server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                    } else if (b3 == 2) {
                        server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                    } else if (b3 == 3) {
                        server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                    }
                }
                if (b2 == 1) {
                    if (p.c.get().nclass > 0) {
                        p.c.place.chatNPC(p, (short) b1, "Con đã vào lớp từ trước rồi mà");
                    } else if (p.c.get().ItemBody[1] != null) {
                        p.c.place.chatNPC(p, (short) b1, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                    } else if (p.c.getBagNull() < 3) {
                        p.c.place.chatNPC(p, (short) b1, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                    } else {
                        p.c.addItemBag(false, ItemData.itemDefault(421));
                        if (b3 == 0) {
                            p.Admission((byte) 3);
                        } else if (b3 == 1) {
                            p.Admission((byte) 4);
                        }
                        p.c.place.chatNPC(p, (short) 9, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                    }
                } else if (b2 == 2) {
                    if (p.c.get().nclass != 3 && p.c.get().nclass != 4) {
                        p.c.place.chatNPC(p, (short) b1, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                    } else {
                        if (b3 == 0) {
                            p.restPpoint();
                            p.c.place.chatNPC(p, (short) b1, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                        } else if (b3 == 1) {
                            p.restSpoint();
                            p.c.place.chatNPC(p, (short) b1, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                        }
                    }
                }
                break;
                //menu npc thầy Kazeto
            case 11:
                if (b2 == 0) {
                    if (b3 == 0) {
                        server.manager.sendTB(p, "Top đại gia yên", BXHManager.getStringBXH(0));
                    } else if (b3 == 1) {
                        server.manager.sendTB(p, "Top cao thủ", BXHManager.getStringBXH(1));
                    } else if (b3 == 2) {
                        server.manager.sendTB(p, "Top gia tộc", BXHManager.getStringBXH(2));
                    } else if (b3 == 3) {
                        server.manager.sendTB(p, "Top hang động", BXHManager.getStringBXH(3));
                    }
                }
                if (b2 == 1) {
                    if (p.c.get().nclass > 0) {
                        p.c.place.chatNPC(p, (short) b1, "Con đã vào lớp từ trước rồi mà");
                    } else if (p.c.get().ItemBody[1] != null) {
                        p.c.place.chatNPC(p, (short) b1, "Con cần tháo vũ khí ra để đến đây nhập học nhé");
                    } else if (p.c.getBagNull() < 3) {
                        p.c.place.chatNPC(p, (short) b1, "Hành trang phải có đủ 2 ô để nhận đồ con nhé");
                    } else {
                        p.c.addItemBag(false, ItemData.itemDefault(422));
                        if (b3 == 0) {
                            p.Admission((byte) 5);
                        } else if (b3 == 1) {
                            p.Admission((byte) 6);
                        }
                        p.c.place.chatNPC(p, (short) b1, "Hãy chăm chỉ quay tay để lên cấp con nhé");
                    }
                } else if (b2 == 2) {
                    if (p.c.get().nclass != 5 && p.c.get().nclass != 6) {
                        p.c.place.chatNPC(p, (short) b1, "Con không phải học sinh trường này nên không thể tẩy điểm ở đây");
                    } else {
                        if (b3 == 0) {
                            p.restPpoint();
                            p.c.place.chatNPC(p, (short) b1, "Ta đã giúp con tẩy điểm tiềm năng, hãy sử dụng tốt điểm tiềm năng nhé");
                        } else if (b3 == 1) {
                            p.restSpoint();
                            p.c.place.chatNPC(p, (short) b1, "Ta đã giúp con tẩy điểm kĩ năng, hãy sử dụng tốt điểm kĩ năng nhé");
                        }
                    }
                }
                break;
                //menu npc Tajima
            case 12:
                if (b2 == 0) {
//
                } else if (b2 == 3) {
                    if (p.c.timeRemoveClone > System.currentTimeMillis()) {
                        p.toNhanBan();
                    }
                } else if (b2 == 4) {
                    if (!p.c.clone.isDie && p.c.timeRemoveClone > System.currentTimeMillis()) {
                        p.exitNhanBan(false);
                    }
                } else {
                    p.c.place.chatNPC(p, (short) b1, "Con đang thực hiện nhiệm vụ kiên trì diệt ác, hãy chọn Menu/Nhiệm vụ để biết mình đang làm đến đâu");
                }
                break;
            case 18:
                if( b2 == 0) {
                    p.typemenu = 151;
                    doMenuArray(p, new String[]{" Trò chơi Chẵn lẻ"});
                    break;
                }
                break;
            case 151 :{
                if( b2 == 0) {
                    if ( p.luong < 10000) {
                        p.c.place.chatNPC(p, (short) b1, "Không có tiền thì chăm chỉ làm ăn đi? Cờ bạc làm gì hả cháu");
                    }
                    else {
                        p.typemenu = 152;
                        p.c.place.chatNPC(p, (short)b1, "Nhớ đừng báo công an bắt bà nghe con, bà chơi vì đam mê thôi");
                        doMenuArray(p, new String[]{" Chẵn"," Lẻ"});
                    }
                }
            }
            break;
            
            case 152 :{
                if (b2 == 0) {
                    int[] items = new int[]{1,2,4,6};
                    Random Rand = new Random();
                    int k = items[Rand.nextInt(items.length)];
                    
                    if( k % 2 == 1) {
                        p.c.place.chatNPC(p, (short) b1, "Về Chẵn - May mắn đấy, làm ván nữa với bà không?");
                        p.upluongMessage(20000);
                        p.sendAddchatYellow("Bạn nhận được 20.000 lượng");
                        
                    } else if(k % 2 == 0) {
                        p.c.place.chatNPC(p, (short) b1, "Về Lẻ - Thua rồi, cho bà xin ít lượng nhé");
                        
                        p.upluongMessage(-10000);
                        p.sendAddchatYellow("Bạn bị Bà Rei thu 10000 lượng");
                    }
                    break;
                }
                if (b2 == 1)
                {
                    int[] items = new int[]{1,2,5,7};
                    Random Rand = new Random();
                    int k = items[Rand.nextInt(items.length)];
                    if (k % 2 == 0) {
                        p.c.place.chatNPC(p, (short) b1, " Về Lẻ - May mắn đấy, làm ván nữa với bà không?");
                        p.upluongMessage(20000);
                        p.sendAddchatYellow("Bạn nhận được 20000 lượng");
                    } else {
                        p.c.place.chatNPC(p, (short) b1, "Về Chẵn - Thua rồi, cho bà xin ít lượng nhé");
                        p.upluongMessage(-10000);
                        p.sendAddchatYellow("Bạn bị Bà Rei thu 10000 lượng");
                    }
                }
            }
            break;
            //Menu npc Kirin
            case 19:
                if (b2 == 0) {
                    if (p.c.exptype == 0) {
                        p.c.exptype = 1;
                        p.c.place.chatNPC(p, (short) b1, "Đã tắt không nhận kinh nghiệm");
                    } else {
                        p.c.exptype = 0;
                        p.c.place.chatNPC(p, (short) b1, "Đã bật không nhận kinh nghiệm");
                    }
                } else if (b2 == 1) {
                    p.passold = "";
                    this.sendWrite(p, (short) 51, "Nhập mật khẩu cũ");
                }
                break;
            // Menu Okanechan
            case 24:
                switch (b2) { // Giftcode
                        case 4: {
                            this.sendWrite(p, (short) 47, "Nhập mã quà tặng");
                            break;
                        }
                        
                    }
                    break;
                
            case 21:  // NPC Sunoo
                if (b2 == 0) {
                    if (p.c.quantityItemyTotal(428) < 3 || p.c.quantityItemyTotal(429) < 3) {
                        p.c.place.chatNPC(p, (short) b1, "Để làm được Cần câu vàng con cần 3 Tre & 3 Dây");
                        break;
                    } else if (p.luong < 500) {
                        p.c.place.chatNPC(p, (short) b1, "Trong túi con không có đủ 100 lượng ư, hãy kiếm thêm");
                        break;
                    } else {
                        p.c.place.chatNPC(p, (short) b1, "Vật phẩm Cần câu vàng đã được gửi vào túi");
                        p.c.addItemBag(true, ItemData.itemDefault(548));
                        p.c.removeItemBags(428, 1);
                        p.c.removeItemBags(429, 1);
                        p.upluongMessage(-500);
                        break;
                    }
                }
            // XSMB
                    if (b2 == 1) {
                    p.c.place.chatNPC(p, (short) b1, "Mỗi ngày con chỉ được đặt cược 01 con số, nên con hãy suy nghĩ thật kỹ trước khi đặt cược nhé");
                    p.typemenu = 200;
                    doMenuArray(p, new String[]{" Chấp nhận"});
                    break;
                }
            case 200 : {
                if( b2 == 0) {
                    if ( p.luong < 10000) { // Check min tiền
                        p.c.place.chatNPC(p, (short) b1, "Không có tiền thì chăm chỉ làm ăn đi? Đừng dính tới cờ bạc, ta nói thật đấy");
                        return;
                    }
                    else {
                        p.typemenu = 201;
                        //p.c.place.chatNPC(p, (short)b1, "Đừng tham lam quá nhé các cháu");
                        doMenuArray(p, new String[]{" Đặt số"," Xem kết quả"," Nhận thưởng"," Rút tiền về ATM"});
                    } break;
                } 
            }
            break;
            case 201 : {       
                        Calendar cd = Calendar.getInstance();
                        int gio = cd.get(Calendar.HOUR_OF_DAY);
                        if (b2 == 0) { // Sau khi xong chỉnh về 08 và 17
                            if (gio > 7 && gio < 17) { // Sau khi xong chỉnh về 07 và 17
                            p.c.place.chatNPC(p, (short) b1, "Đặt tùy ý từ 0 - 99, nếu con đoán đúng phần thưởng sẽ được x80 lần số tiền con bỏ ra");
                            this.sendWrite(p,(short) 1406, " Mời bạn đặt số");
                            return;
                            } else {
                            p.c.place.chatNPC(p, (short) b1, "Thời gian đặt cược XSMB từ 07 - 17 giờ hàng ngày, có kết quả vào lúc 18h50 con nhé");
                            } break;
                        }
                        
                if (b2 == 1) {
                    // Hiển thị kết quả XSMB 7 ngày gần nhất treo CURL SQL
                    server.manager.sendTB(p, "Kết quả XSMB", BXHManager.getStringBXH(5));
                    return;
                } else
                if (b2 == 2) {
                    String DATE_FORMAT_FILE = "yyyy-MM-dd";
                    SimpleDateFormat dateFormatFile = null;
                    dateFormatFile = new SimpleDateFormat(DATE_FORMAT_FILE);
                    Calendar calender = Calendar.getInstance();
                    Date date = calender.getTime();
                    Date dt = null;
                    int rs;
                    ResultSet red = SQLManager.stat.executeQuery("SELECT * FROM `xoso` WHERE `day`='"+dateFormatFile.format(date)+"' LIMIT 1;");
                    if (red.first()){
                        dt = red.getDate("day");
                        rs = red.getInt("code");
                    } else {
                        p.conn.sendMessageLog("Chưa thống kê \nkết quả ngày " + dateFormatFile.format(date) +" \nvui lòng quay lại sau");
                        return;
                    }
                    red.close();
                    if (dt.toString().equals(dateFormatFile.format(date).toString())) {
                        red = SQLManager.stat.executeQuery("SELECT `xoso` FROM `player` WHERE `username`='"+p.username+"' LIMIT 1;");
                        red.first();
                        int numXS = red.getInt("xoso");
                        red = SQLManager.stat.executeQuery("SELECT `coinXS` FROM `player` WHERE `username`='"+p.username+"' LIMIT 1;");
                        red.first();
                        int numcoinXS = red.getInt("coinXS");
                        // Check nếu trùng số đặt cược
                        if (numXS == rs){
                            p.conn.sendMessageLog("Chúc mừng con đã trúng thưởng số " + numXS);
                            server.manager.chatKTG("" + p.c.name + " nhân phẩm thượng thừa đã đoán trúng KQXS ngày " + dateFormatFile.format(date) +" \nvề số " + numXS + ".");
                            p.upluongMessage(numcoinXS * 80); // Tỉ lệ thưởng 1 ăn 80 đã test lệnh OK
                            // Reset để mai chơi
                            p.xoso = -1;
                            SQLManager.stat.executeUpdate("UPDATE `player` SET `xoso`='" + p.xoso +"' WHERE `id` ='" + p.id + "' LIMIT 1;");
                            p.coinXS = -1;
                            SQLManager.stat.executeUpdate("UPDATE `player` SET `coinXS`='" + p.coinXS +"' WHERE `id` ='" + p.id + "' LIMIT 1;");
                        } else {
                            // Nếu không trùng số sẽ báo nút này
                            p.conn.sendMessageLog("Rất tiếc, chúc con may mắn lần sau!");
                            // Reset để mai chơi
                            p.xoso = -1;
                            SQLManager.stat.executeUpdate("UPDATE `player` SET `xoso`='" + p.xoso +"' WHERE `id` ='" + p.id + "' LIMIT 1;");
                            p.coinXS = -1;
                            SQLManager.stat.executeUpdate("UPDATE `player` SET `coinXS`='" + p.coinXS +"' WHERE `id` ='" + p.id + "' LIMIT 1;");
                        }
                        // Check nếu đã nhấn nhận thưởng rồi sẽ báo nút này
                        if (numXS == -1){
                            p.conn.sendMessageLog("Con đã nhận kết quả rồi, ngày mai quay trở lại đặt cược tiếp tục nhé!");
                        }
                    } else {
                        p.conn.sendMessageLog("Lỗi không xác định!");
                    }
                    return; 
                } else
                if (b2 == 3) {
                    // Hàm rút tiền =))) 
                    p.c.place.chatNPC(p, (short) b1, "Ta nói thế thôi, chứ làm gì có tiền mà chả cho các con");
                    //this.sendWrite(p, (short) b1, "Nhập số tiền");
                }
            }
            break;
            // NPC Guriin
            case 22: {
                if (b2 != 0) {
                    break;
                }
                if (p.c.clan.clanName.isEmpty()) {
                    p.c.place.chatNPC(p, (short) b1, "Con cần phải có gia tộc thì mới có thể điểm danh được nhé");
                    break;
                }
                if (p.c.ddClan) {
                    p.c.place.chatNPC(p, (short) b1, "Hôm nay con đã điểm danh rồi nhé, hãy quay lại đây vào ngày mai");
                    break;
                }
                p.c.ddClan = true;
                final ClanManager clan = ClanManager.getClanName(p.c.clan.clanName);
                if (clan == null) {
                    p.c.place.chatNPC(p, (short) b1, "Gia tộc lỗi");
                    return;
                }
                p.upExpClan(util.nextInt(1, 10 + clan.level));
                p.upluongMessage(50 * clan.level);
                p.c.upyenMessage(500000 * clan.level);
                p.c.place.chatNPC(p, (short) b1, "Điểm danh mỗi ngày sẽ nhận được các phần quà giá trị");
                break;
            }
            // NPC Chiến trường
            case 25: {
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    if ((b2 == 2) && (hour == 10 || hour == 20)) {
                        switch (b3) {
                            case 0: {
                                if (p.c.typeCT == 5) {
                                    p.c.place.chatNPC(p, (short)b1, "Mi đang ở phe bạch giả, không thể đổi phe");
                                    return;
                                }
                                p.c.changePk(p.c, (byte) 4);
                                Map ma = Manager.getMapid(98);
                                for (Place area : ma.area) {
                                    if (area.numplayers < ma.template.maxplayers) {
                                        p.c.place.leave(p);
                                        area.EnterMap0(p.c);
                                        return;
                                    }
                                }
                                break;
                            }
                            case 1: {
                                if (p.c.typeCT == 4) {
                                    p.c.place.chatNPC(p, (short)b1, "Mi đang ở phe hắc giả, không thể đổi phe");
                                    return;
                                }
                                p.c.changePk(p.c, (byte) 5);
                                Map ma = Manager.getMapid(104);
                                for (Place area : ma.area) {
                                    if (area.numplayers < ma.template.maxplayers) {
                                        p.c.place.leave(p);
                                        area.EnterMap0(p.c);
                                        return;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                Service.rewardCT(p.c);
                                break;
                            }
                        }
                        break;
                    } else {
                        if (b2 == 2) {
                            switch (b3) {
                                case 2: {
                                    Service.rewardCT(p.c);
                                    break;
                                }
                            }
                        }
                        p.c.place.chatNPC(p, (short) b1, "Chưa đến giờ chiến trường.");
                        break;
                    }
                }
            //Menu npc Goosho
            case 26:
                if (b2 == 0) {
                    p.requestItem(14);
                    break;
                } else if (b2 == 1) {
                    p.requestItem(15);
                    break;
                } else if (b2 == 2) {
                    p.requestItem(32);
                } else if (b2 == 3) {
                    p.requestItem(34);
                }
                break;
            // LDGT    
            case 27: {
                ClanManager clan = null;
                clan = ClanManager.getClanName(p.c.clan.clanName);
                if (b2 == 0 && p.c.quantityItemyTotal(260) >= 1) {
                    p.c.removeItemBags(260, 1);
                    if (clan.ldgt.level == 0) {
                        p.c.place.map.ldgt.openMap();
                        clan.ldgt.time = System.currentTimeMillis() + (1000 * 60 * 60);
                        break;
                    }
                    p.c.place.rsMobLDGT();
                    clan.chiakhoa++;
                    if (clan.chiakhoa == 3) {
                        p.c.place.map.ldgt.openMap();
                        clan.chiakhoa = 0;
                        break;
                    }
                    break;
                } else {
                    p.c.place.chatNPC(p, (short) b1, "Mang chìa khóa đến đây.");
                    break;
                }
            }
                //Menu npc Rakkii
            case 30:
                switch (b2) {
                    case 0:
                        p.requestItem(38);
                        break;
                    case 2:
                        if (b3 == 0) {
                            server.manager.rotationluck[0].luckMessage(p);
                        } else if (b3 == 2) {
                            server.manager.sendTB(p, "Vòng xoay vip", "Tham gia đi xem luật lm gì");
                        }
                        break;
                    case 3:
                        if (b3 == 0) {
                            server.manager.rotationluck[1].luckMessage(p);
                        } else if (b3 == 2) {
                            server.manager.sendTB(p, "Vòng xoay thường", "Tham gia đi xem luật lm gì");
                        }
                        break;
                }
                break;
                //menu npc Kagai
            case 32:
                switch (b2) {
                    case 4:
                        if (b3 == 1) {
                            p.requestItem(44);
                        } else if (b3 == 2) {
                            p.requestItem(45);
                        }
                        break;
                }
                break;
            // Menu Shinwa   
            case 28:
                if (b2 == 1) {
                    p.c.place.chatNPC(p, (short) b1, "Tụt quần ra con");
                }
            break;
                //menu npc Tiên nữ
            case 33: {
                if (p.typemenu != 33) {
                    break;
                }
                switch (this.server.manager.event) {
                    case 1: {
                        switch (b2) {
                            case 0: {
                                if (p.c.quantityItemyTotal(428) < 1 || p.c.quantityItemyTotal(429) < 1 || p.c.quantityItemyTotal(430) < 1 || p.c.quantityItemyTotal(431) < 1 || p.c.xu < 10000 || p.c.yen < 10000) {
                                    p.c.place.chatNPC(p, (short) b1, "Hành trang của con không có đủ nguyên liệu hoặc thiếu 10K xu, 10K yên");
                                    break;
                                }
                                if (p.c.getBagNull() == 0) {
                                    p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    break;
                                }
                                final Item it = ItemData.itemDefault(434);
                                p.c.addItemBag(true, it);
                                p.c.upxuMessage(-10000);
                                p.c.upyenMessage(-10000);
                                p.c.removeItemBags(428, 1);
                                p.c.removeItemBags(429, 1);
                                p.c.removeItemBags(430, 1);
                                p.c.removeItemBags(431, 1);
                                break;
                            }
                            case 1: {
                                if (p.c.quantityItemyTotal(428) < 3 || p.c.quantityItemyTotal(429) < 3 || p.c.quantityItemyTotal(430) < 3 || p.c.quantityItemyTotal(431) < 3 || p.c.xu < 30000 || p.c.yen < 30000) {
                                    p.c.place.chatNPC(p, (short) b1, "Hành trang của con không có đủ nguyên liệu hoặc thiếu 30K xu, 30K lượng");
                                    break;
                                }
                                if (p.c.getBagNull() == 0) {
                                    p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
                                    break;
                                }
                                final Item it = ItemData.itemDefault(435);
                                p.c.addItemBag(true, it);
                                p.c.upxuMessage(-30000);
                                p.c.upyenMessage(-30000);
                                p.c.removeItemBags(428, 3);
                                p.c.removeItemBags(429, 3);
                                p.c.removeItemBags(430, 3);
                                p.c.removeItemBags(431, 3);
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        p.c.place.chatNPC(p, (short) b1, "Hiện sự kiện này chưa diễn ra");
                        break;
                    }
                }
                break;
            }
            // npc hùng vương
            case 36: {
                p.typemenu = ((b2 == 0) ? 155 : 156);
                doMenuArray(p, new String[]{"Đổi lượng", "Đổi ngọc", "Nâng cấp mắt", "Nâng cấp Faiyaa", "Nâng cấp Mizu",  "Nâng cấp Windo", "Đổi đồ Jirai", "Đổi đồ Jumito", "Đổi bí kíp", "Quà tân thủ"});
                break;
            }
            case 155: {
                switch (b2) {
                    case 0: {
                        p.typemenu = ((b2 == 0) ? 107 : 108);
                        doMenuArray(p, new String[]{"Đổi 50K lượng ra 500K yên", "Đổi 5M yên ra 500K xu", "Đổi 500K xu ra 10K lượng"});
                        break;
                    }
                    case 1: {
                        p.typemenu = ((b2 == 1) ? 109 : 110);
                        doMenuArray(p, new String[]{"Đổi huyền tinh ngọc", "Đổi huyết ngọc", "Đổi lam tinh ngọc", "Đổi lục ngọc", "Nâng ngọc"});
                        break;
                    }
                    case 2: {
                        p.typemenu = ((b2 == 2) ? 111 : 112);
                        doMenuArray(p, new String[]{"Nâng mắt 1", "Nâng mắt 2", "Nâng mắt 3", "Nâng mắt 4", "Nâng mắt 5", "Nâng mắt 6", "Nâng mắt 7", "Nâng mắt 8", "Nâng mắt 9", "Nâng mắt 10"});
                        break;
                    }
                    case 3: {
                        p.typemenu = ((b2 == 3) ? 113 : 114);
                        doMenuArray(p, new String[]{"Mua áo cấp 1", "Mua áo cấp 2", "Mua áo cấp 3", "Mua áo cấp 4", "Mua áo cấp 5", "Mua áo cấp 6", "Mua áo cấp 7", "Mua áo cấp 8", "Mua áo cấp 9", "Mua áo cấp 10", "Mua áo cấp 11", "Mua áo cấp 12", "Mua áo cấp 13", "Mua áo cấp 14", "Mua áo cấp 15", "Mua áo cấp 16"});
                        break;
                    }
                    case 4: {
                        p.typemenu = ((b2 == 4) ? 115 : 116);
                        doMenuArray(p, new String[]{"Mua áo cấp 1", "Mua áo cấp 2", "Mua áo cấp 3", "Mua áo cấp 4", "Mua áo cấp 5", "Mua áo cấp 6", "Mua áo cấp 7", "Mua áo cấp 8", "Mua áo cấp 9", "Mua áo cấp 10", "Mua áo cấp 11", "Mua áo cấp 12", "Mua áo cấp 13", "Mua áo cấp 14", "Mua áo cấp 15", "Mua áo cấp 16"});                        break;
                    }
                    case 5: {
                        p.typemenu = ((b2 == 5) ? 117 : 118);
                        doMenuArray(p, new String[]{"Mua áo cấp 1", "Mua áo cấp 2", "Mua áo cấp 3", "Mua áo cấp 4", "Mua áo cấp 5", "Mua áo cấp 6", "Mua áo cấp 7", "Mua áo cấp 8", "Mua áo cấp 9", "Mua áo cấp 10", "Mua áo cấp 11", "Mua áo cấp 12", "Mua áo cấp 13", "Mua áo cấp 14", "Mua áo cấp 15", "Mua áo cấp 16"});                        break;
                    }
                    case 6: {
                        p.typemenu = ((b2 == 6) ? 119 : 120);
                        doMenuArray(p, new String[]{"Đổi giày Jirai", "Đổi phù Jirai", "Đổi quần Jirai", "Đổi bội Jirai", "Đổi găng Jirai", "Đổi nhẫn Jirai", "Đổi áo Jirai", "Đổi dây chuyền Jirai", "Đổi nón Jirai", "Đổi mặt nạ Jirai"});
                        break;
                    }
                    case 7: {
                        p.typemenu = ((b2 == 7) ? 121 : 122);
                        doMenuArray(p, new String[]{"Đổi giày Jumito", "Đổi phù Jumito", "Đổi quần Jumito", "Đổi bội Jumito", "Đổi găng Jumito", "Đổi nhẫn Jumito", "Đổi áo Jumito", "Đổi dây chuyền Jumito", "Đổi nón Jumito", "Đổi mặt nạ Jumito"});
                        break;
                    }
                    case 8: {
                        p.typemenu = ((b2 == 8) ? 123 : 124);
                        doMenuArray(p, new String[]{"Bí kíp kiếm", "Bí kíp Tiêu", "Bí kíp Đao", "Bí kíp Quạt", "Bí kíp Kunia", "Bí kíp Cung"});
                        break;
                    }
                    case 9: {
                        p.typemenu = ((b2 == 9) ? 125 : 126);
                        doMenuArray(p, new String[]{"Điểm danh hàng ngày", "Quà tặng tân thủ"});
                        break;
                    }
                }
                break;
            }
            case 107: {
                switch (b2) {
                    case 0: {
                        if (p.luong > 50000) {
                            p.upluongMessage(-50000L);
                            p.c.upyenMessage(500000);
                            return;
                        }
                        p.c.place.chatNPC(p, (short) b1, "Có đủ lượng đâu mà đổi hả con");
                        break;
                    }
                    case 1: {
                        if (p.c.yen < 5000000) {
                            p.c.place.chatNPC(p, (short) b1, "Kiếm thêm yên rồi đổi con nhé");
                            return;
                        } else {
                            p.c.upyenMessage(-5000000);
                            p.c.upxuMessage(500000);
                            break;
                        }
                    }
                    case 2: {
                        if (p.c.xu < 500000) {
                            p.c.place.chatNPC(p, (short) b1, "Kiếm thêm xu rồi đổi con nhé");
                            return;
                        } else {
                            p.c.upxuMessage(-500000);
                            p.upluongMessage(10000);
                            break;
                        }
                    }
                }
                break;
            }
            case 109: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(648) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 Huy chương chiến công đồng");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            final Item itemup = ItemData.itemDefault(652);
                            p.upluongMessage(-5000);
                            itemup.upgrade = 1;
                            p.c.removeItemBags(648, 100);
                            p.c.addItemBag(false, itemup);
                            break;
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(649) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 Huy chương chiến công bạc");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            final Item itemup = ItemData.itemDefault(653);
                            p.upluongMessage(-10000);
                            itemup.upgrade = 1;
                            p.c.removeItemBags(649, 100);
                            p.c.addItemBag(false, itemup);
                            break;
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(650) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 Huy chương chiến công vàng");
                            break;
                        } else if (p.luong < 15000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 15000 lượng");
                            break;
                        } else {
                            final Item itemup = ItemData.itemDefault(654);
                            p.upluongMessage(-15000);
                            itemup.upgrade = 1;
                            p.c.removeItemBags(650, 100);
                            p.c.addItemBag(false, itemup);
                            break;
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(651) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 Huy chương chiến công bạch kim");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            final Item itemup = ItemData.itemDefault(655);
                            p.upluongMessage(-20000);
                            itemup.upgrade = 1;
                            p.c.removeItemBags(651, 100);
                            p.c.addItemBag(false, itemup);
                            break;
                        }
                    }
                }
                break;
            }
            case 111: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(695) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 1");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(695, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 1 rồi");
                                final Item itemup = ItemData.itemDefault(685,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 1;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(695, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(696) < 100 || p.c.quantityItemyTotal(685) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 2 và mắt 1");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(696, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 2 rồi");
                                final Item itemup = ItemData.itemDefault(686,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 2;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(696, 100);
                                p.c.removeItemBags(685, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(696) < 500 || p.c.quantityItemyTotal(686) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 500 đá danh vọng 2 và mắt 2");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(696, 500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 3 rồi");
                                final Item itemup = ItemData.itemDefault(687,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 3;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(697, 500);
                                p.c.removeItemBags(686, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(696) < 1000 || p.c.quantityItemyTotal(687) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1000 đá danh vọng 2 và mắt 3");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(696, 1000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 4 rồi");
                                final Item itemup = ItemData.itemDefault(688,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 4;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(696, 1000);
                                p.c.removeItemBags(687, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 4: {
                        if (p.c.quantityItemyTotal(696) < 1500 || p.c.quantityItemyTotal(688) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1500 đá danh vọng 2 và mắt 4");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(696, 1500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 5 rồi");
                                final Item itemup = ItemData.itemDefault(689,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 5;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(696, 1500);
                                p.c.removeItemBags(688, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 5: {
                        if (p.c.quantityItemyTotal(696) < 2000 || p.c.quantityItemyTotal(689) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 2000 đá danh vọng 2 và mắt 5");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 6 rồi");
                                final Item itemup = ItemData.itemDefault(690,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 6;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
                                p.c.removeItemBags(689, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 6: {
                        if (p.c.quantityItemyTotal(701) < 40 || p.c.quantityItemyTotal(690) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 30 đá danh vọng 7 và mắt 6");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 40);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 7 rồi");
                                final Item itemup = ItemData.itemDefault(691,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 7;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 40);
                                p.c.removeItemBags(690, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 7: {
                        if (p.c.quantityItemyTotal(702) < 50 || p.c.quantityItemyTotal(691) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 8 và mắt 7");
                            break;
                        } else if (p.luong < 15000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 15000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-15000);
                                p.c.removeItemBags(702, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 8 rồi");
                                final Item itemup = ItemData.itemDefault(692,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 8;
                                p.upluongMessage(-15000);
                                p.c.removeItemBags(702, 50);
                                p.c.removeItemBags(691, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 8: {
                        if (p.c.quantityItemyTotal(703) < 50 || p.c.quantityItemyTotal(692) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 9 và mắt 8");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 9 rồi");
                                final Item itemup = ItemData.itemDefault(693,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 9;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
                                p.c.removeItemBags(692, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 9: {
                        if (p.c.quantityItemyTotal(704) < 60 || p.c.quantityItemyTotal(693) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 60 đá danh vọng 10 và mắt 9");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mắt 10 rồi");
                                final Item itemup = ItemData.itemDefault(694,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 10;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
                                p.c.removeItemBags(693, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 113: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(695) < 100 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 1 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(695, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 1 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 1;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(695, 100);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(696) < 100 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 2 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 2 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 2;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 100);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(696) < 500 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 500 đá danh vọng 2 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 3 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 3;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(697, 500);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(696) < 1000 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1000 đá danh vọng 2 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 4 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 4;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1000);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 4: {
                        if (p.c.quantityItemyTotal(696) < 1500 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1500 đá danh vọng 2 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 5 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 5;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1500);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 5: {
                        if (p.c.quantityItemyTotal(696) < 2000 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 2000 đá danh vọng 2 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 6 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 6;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 6: {
                        if (p.c.quantityItemyTotal(701) < 30 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 30 đá danh vọng 7 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 30);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 7 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 7;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 30);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 7: {
                        if (p.c.quantityItemyTotal(702) < 50 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 8 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 120000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 120000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-120000);
                                p.c.removeItemBags(702, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 8 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 8;
                                p.upluongMessage(-120000);
                                p.c.removeItemBags(702, 50);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 8: {
                        if (p.c.quantityItemyTotal(703) < 50 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 9 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 9 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 9;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 9: {
                        if (p.c.quantityItemyTotal(704) < 60 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 60 đá danh vọng 10 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 10 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 10;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 10: {
                        if (p.c.quantityItemyTotal(704) < 80 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 60 đá danh vọng 10 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 80);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 11 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 11;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 80);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 11: {
                        if (p.c.quantityItemyTotal(704) < 100 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 10 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 12 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 12;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 100);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 12: {
                        if (p.c.quantityItemyTotal(704) < 120 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 120 đá danh vọng 10 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 120);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 13 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 13;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 120);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 13: {
                        if (p.c.quantityItemyTotal(704) < 150 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 150 đá danh vọng 10 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 150);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 14 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 14;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 150);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 14: {
                        if (p.c.quantityItemyTotal(704) < 170 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 170 đá danh vọng 10 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 170);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 15 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 15;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 170);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 15: {
                        if (p.c.quantityItemyTotal(704) < 200 || p.c.quantityItemyTotal(420) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 200 đá danh vọng 10 và 1 Faiyaa Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 200);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Faiyaa Yoroi 16 rồi");
                                final Item itemup = ItemData.itemDefault(420,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 200);
								p.c.removeItemBags(420, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }	
                }
                break;
            }
            case 115: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(695) < 100 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 1 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(695, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 1 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 1;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(695, 100);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(696) < 100 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 2 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 2 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 2;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 100);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(696) < 500 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 500 đá danh vọng 2 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 3 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 3;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(697, 500);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(696) < 1000 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1000 đá danh vọng 2 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 4 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 4;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1000);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 4: {
                        if (p.c.quantityItemyTotal(696) < 1500 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1500 đá danh vọng 2 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 5 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 5;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1500);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 5: {
                        if (p.c.quantityItemyTotal(696) < 2000 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 2000 đá danh vọng 2 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 6 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 6;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 6: {
                        if (p.c.quantityItemyTotal(701) < 30 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 30 đá danh vọng 7 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 30);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 7 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 7;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 30);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 7: {
                        if (p.c.quantityItemyTotal(702) < 50 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 8 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 120000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 120000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-120000);
                                p.c.removeItemBags(702, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 8 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 8;
                                p.upluongMessage(-120000);
                                p.c.removeItemBags(702, 50);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 8: {
                        if (p.c.quantityItemyTotal(703) < 50 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 9 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 9 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 9;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 9: {
                        if (p.c.quantityItemyTotal(704) < 60 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 60 đá danh vọng 10 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 10 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 10;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 10: {
                        if (p.c.quantityItemyTotal(704) < 80 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 60 đá danh vọng 10 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 80);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 11 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 11;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 80);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 11: {
                        if (p.c.quantityItemyTotal(704) < 100 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 10 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 12 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 12;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 100);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 12: {
                        if (p.c.quantityItemyTotal(704) < 120 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 120 đá danh vọng 10 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 120);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 13 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 13;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 120);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 13: {
                        if (p.c.quantityItemyTotal(704) < 150 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 150 đá danh vọng 10 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 150);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 14 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 14;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 150);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 14: {
                        if (p.c.quantityItemyTotal(704) < 170 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 170 đá danh vọng 10 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 170);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 15 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 15;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 170);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 15: {
                        if (p.c.quantityItemyTotal(704) < 200 || p.c.quantityItemyTotal(421) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 200 đá danh vọng 10 và 1 Mizu Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 200);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Mizu Yoroi 16 rồi");
                                final Item itemup = ItemData.itemDefault(421,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 200);
								p.c.removeItemBags(421, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }	
                }
                break;
            }
            case 117: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(695) < 100 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 1 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(695, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 1 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 1;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(695, 100);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(696) < 100 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 2 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 2 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 2;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 100);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(696) < 500 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 500 đá danh vọng 2 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 3 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 3;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(697, 500);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(696) < 1000 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1000 đá danh vọng 2 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 4 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 4;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1000);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 4: {
                        if (p.c.quantityItemyTotal(696) < 1500 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 1500 đá danh vọng 2 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1500);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 5 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 5;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(696, 1500);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 5: {
                        if (p.c.quantityItemyTotal(696) < 2000 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 2000 đá danh vọng 2 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 6 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 6;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(696, 2000);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 6: {
                        if (p.c.quantityItemyTotal(701) < 30 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 30 đá danh vọng 7 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 30);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 7 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 7;
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(701, 30);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 7: {
                        if (p.c.quantityItemyTotal(702) < 50 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 8 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 120000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 120000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-120000);
                                p.c.removeItemBags(702, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 8 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 8;
                                p.upluongMessage(-120000);
                                p.c.removeItemBags(702, 50);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 8: {
                        if (p.c.quantityItemyTotal(703) < 50 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 50 đá danh vọng 9 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 9 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 9;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(703, 50);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 9: {
                        if (p.c.quantityItemyTotal(704) < 60 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 60 đá danh vọng 10 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 10 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 10;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 60);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 10: {
                        if (p.c.quantityItemyTotal(704) < 80 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 60 đá danh vọng 10 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 80);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 11 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 11;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 80);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 11: {
                        if (p.c.quantityItemyTotal(704) < 100 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 đá danh vọng 10 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 12 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 12;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 100);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 12: {
                        if (p.c.quantityItemyTotal(704) < 120 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 120 đá danh vọng 10 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 120);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 13 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 13;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 120);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 13: {
                        if (p.c.quantityItemyTotal(704) < 150 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 150 đá danh vọng 10 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 150);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 14 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 14;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 150);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 14: {
                        if (p.c.quantityItemyTotal(704) < 170 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 170 đá danh vọng 10 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 170);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 15 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 15;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 170);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
					case 15: {
                        if (p.c.quantityItemyTotal(704) < 200 || p.c.quantityItemyTotal(422) < 1) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 200 đá danh vọng 10 và 1 Windo Yoroi");
                            break;
                        } else if (p.luong < 20000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 20000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 200);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Windo Yoroi 16 rồi");
                                final Item itemup = ItemData.itemDefault(422,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-20000);
                                p.c.removeItemBags(704, 200);
								p.c.removeItemBags(422, 1);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }	
                }
                break;
            }
            case 119: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(737) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh giày Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(737, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có giày Jirai rồi");
                                final Item itemup = ItemData.itemDefault(748,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(737, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(740) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh phù Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(740, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có phù Jirai rồi");
                                final Item itemup = ItemData.itemDefault(750,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(740, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(736) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh quần Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(736, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có quần Jirai rồi");
                                final Item itemup = ItemData.itemDefault(713,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(736, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(739) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh ngọc bội Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(739, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có bội Jirai rồi");
                                final Item itemup = ItemData.itemDefault(751,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(739, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 4: {
                        if (p.c.quantityItemyTotal(734) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh găng Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(734, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có găng Jirai rồi");
                                final Item itemup = ItemData.itemDefault(747,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(734, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 5: {
                        if (p.c.quantityItemyTotal(741) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh nhẫn Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(741, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có nhẫn Jirai rồi");
                                final Item itemup = ItemData.itemDefault(749,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(741, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 6: {
                        if (p.c.quantityItemyTotal(735) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh áo Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(735, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Jirai rồi");
                                final Item itemup = ItemData.itemDefault(712,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(735, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 7: {
                        if (p.c.quantityItemyTotal(738) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh dây chuyền Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(738, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có dây chuyền Jirai rồi");
                                final Item itemup = ItemData.itemDefault(752,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(738, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 8: {
                        if (p.c.quantityItemyTotal(733) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh nón Jirai");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(733, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có nón Jirai rồi");
                                final Item itemup = ItemData.itemDefault(746,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(733, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 9: {
                        if (p.c.quantityItemyTotal(684) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 cỏ bốn lá");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(684, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mặt nạ Jirai rồi");
                                final Item itemup = ItemData.itemDefault(711);
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(684, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 121: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(764) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh giày Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(764, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có giày Jumito rồi");
                                final Item itemup = ItemData.itemDefault(755,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(764, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(767) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh phù Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(767, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có phù Jumito rồi");
                                final Item itemup = ItemData.itemDefault(757,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(767, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(763) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh quần Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(763, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có quần Jumito rồi");
                                final Item itemup = ItemData.itemDefault(716,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(763, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(766) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh ngọc bội Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(766, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có bội Jumito rồi");
                                final Item itemup = ItemData.itemDefault(758,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(766, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 4: {
                        if (p.c.quantityItemyTotal(761) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh găng Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(761, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có găng Jumito rồi");
                                final Item itemup = ItemData.itemDefault(754,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(761, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 5: {
                        if (p.c.quantityItemyTotal(768) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh nhẫn Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(768, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có nhẫn Jumito rồi");
                                final Item itemup = ItemData.itemDefault(756,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(768, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 6: {
                        if (p.c.quantityItemyTotal(762) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh áo Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(762, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có áo Jumito rồi");
                                final Item itemup = ItemData.itemDefault(715,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(762, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 7: {
                        if (p.c.quantityItemyTotal(765) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh dây chuyền Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(765, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có dây chuyền Jumito rồi");
                                final Item itemup = ItemData.itemDefault(759,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(765, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 8: {
                        if (p.c.quantityItemyTotal(760) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 mảnh nón Jumito");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(760, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có nón Jumito rồi");
                                final Item itemup = ItemData.itemDefault(753,(byte) util.nextInt(1, 3));
                                itemup.upgrade = 16;
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(760, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 9: {
                        if (p.c.quantityItemyTotal(684) < 100) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 100 cỏ bốn lá");
                            break;
                        } else if (p.luong < 5000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 5000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(684, 100);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Ngon. Có mặt nạ Jumito rồi");
                                final Item itemup = ItemData.itemDefault(714);
                                p.upluongMessage(-5000);
                                p.c.removeItemBags(684, 100);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 123: {
                switch (b2) {
                    case 0: {
                        if (p.c.quantityItemyTotal(632) < 10) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10 Thái Dương Vô Cực Kiếm");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(632, 10);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Cuối cùng con cũng có bí kíp Kiếm rồi");
                                final Item itemup = ItemData.itemDefault(397);
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(632, 10);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 1: {
                        if (p.c.quantityItemyTotal(633) < 10) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10 Thái Dương Thiên Hỏa Tiêu");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(633, 10);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Cuối cùng con cũng có bí kíp Tiêu rồi");
                                final Item itemup = ItemData.itemDefault(398);
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(633, 10);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 2: {
                        if (p.c.quantityItemyTotal(636) < 10) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10 Thái Dương Chiến Lục Đao");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(636, 10);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Cuối cùng con cũng có bí kíp Đao rồi");
                                final Item itemup = ItemData.itemDefault(401);
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(636, 10);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 3: {
                        if (p.c.quantityItemyTotal(637) < 10) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10 Thái Dương Hoàng Phong Phiến");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(637, 10);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Cuối cùng con cũng có bí kíp Quạt rồi");
                                final Item itemup = ItemData.itemDefault(402);
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(637, 10);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 4: {
                        if (p.c.quantityItemyTotal(634) < 10) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10 Thái Dương Táng Hồn Dao");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(634, 10);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Cuối cùng con cũng có bí kíp Kunai rồi");
                                final Item itemup = ItemData.itemDefault(399);
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(634, 10);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                    case 5: {
                        if (p.c.quantityItemyTotal(635) < 10) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10 Thái Dương Băng Thần Cung");
                            break;
                        } else if (p.luong < 10000) {
                            p.c.place.chatNPC(p, (short) b1, "Cần 10000 lượng");
                            break;
                        } else {
                            int tl = util.nextInt(3);
                            if (tl != 1) {
                                p.c.place.chatNPC(p, (short) b1, "Số con đen như bản mặt con vậy");
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(635, 10);
                                break;
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Cuối cùng con cũng có bí kíp Cung rồi");
                                final Item itemup = ItemData.itemDefault(400);
                                p.upluongMessage(-10000);
                                p.c.removeItemBags(635, 10);
                                p.c.addItemBag(false, itemup);
                                break;
                            }
                        }
                    }
                }
                break;
            }
            case 125: {
                switch (b2) {
                    case 0: {
                        if (p.c.ddLogin) {
                            p.c.place.chatNPC(p, (short)b1, "Hôm nay con đã điểm danh rồi nhé, hãy quay lại đây vào ngày mai");
                        } else {
                            p.c.ddLogin = true;
                            p.c.upyenMessage(10000L);
                            p.c.upxuMessage(10000L);
                            p.upluongMessage(10000L);
                            p.c.place.chatNPC(p, Short.valueOf((short)b1), "Ngày mai con lại đến điểm danh nhé, ta vẫn đứng ở đây thôi");
                        } break;
                    }
                    
                    case 1: {
                        if (p.c.denbu == 2) {
                            p.c.place.chatNPC(p, (short) b1, "Hôm trước con đã nhận phần quà này rồi, nên bây giờ không thể nhận thêm lần nữa!");
                        } else {
                            if (p.c.getBagNull() < 1) {
                                p.c.place.chatNPC(p, (short) b1, "Hành trang không đủ chỗ trống");
                            } else {
                                p.c.place.chatNPC(p, (short) b1, "Nhớ rủ thêm bạn bè vào cùng chơi nha con!");
                                p.c.denbu = 2;
                                p.updateExp(Level.getMaxExp(35) - p.c.exp);
                                p.upluongMessage(50000);
                                Item it = new Item();
                                for (byte i = 0; i < 5; i++) {
                                    it = new Item();
                                    it.id = 384;
                                    it.isLock = true;
                                    p.c.addItemBag(true, it);
                                }
                                for (byte i = 0; i < 5; i++) {
                                    it = new Item();
                                    it.id = 385;
                                    it.isLock = true;
                                    p.c.addItemBag(true, it);
                                }
                            }
                        }  break;
                    }
                }
                break;
            }
            case 92:
                p.typemenu = ((b2 == 0) ? 93 : 94);
                doMenuArray(p, new String[]{"Thông tin", "Luật chơi"});
                break;
            case 93:
                if (b2 == 0) {
                    server.manager.rotationluck[0].luckMessage(p);
                } else if (b2 == 1) {
                    server.manager.sendTB(p, "Vòng xoay vip", "Được ăn cả, ngã ... ở đâu ta gấp đôi ở đó");
                }
                break;
            case 94:
                if (b2 == 0) {
                    server.manager.rotationluck[1].luckMessage(p);
                } else if (b2 == 1) {
                    server.manager.sendTB(p, "Vòng xoay thường", "Được ăn cả, ngã ... ở đâu ta gấp đôi ở đó");
                }
                break;
            case 95:
                break;
            case 120:
                if (b2 > 0 && b2 < 7) {
                    p.Admission(b2);
                }
                break;
            case 850: // Mảnh giấy vụn
            if( b2 == 0) {
                if (p.c.quantityItemyTotal(251) >= 10000) {
                    p.c.removeItemBags(251, 10000);
                    p.sendAddchatYellow("Bạn nhận được 1 sách tiềm năng");
                    
                    Item it = ItemData.itemDefault(253);
                    p.c.addItemBag(true, it);
                    return;
              } else
                if (p.c.quantityItemyTotal(251) < 10000) {
                    p.conn.sendMessageLog("Bạn chưa đủ 10000 giấy vụn để đổi sách");
                    return;
                }
            }
            else if( b2 == 1) {
                if (p.c.quantityItemyTotal(251) >= 10000) {
                    p.c.removeItemBags(251, 10000);
                    p.sendAddchatYellow("Bạn nhận được 1 sách kỹ năng");
                    
                    Item it = ItemData.itemDefault(252);
                    p.c.addItemBag(true, it);
                    return;
              } else
                if (p.c.quantityItemyTotal(251) < 10000) {
                    p.conn.sendMessageLog("Bạn chưa đủ 10000 giấy vụn để đổi sách");
                    return;
                }
           }
            break;
            default:
                p.c.place.chatNPC(p, (short) b1, "Chức năng này đang cập nhật");
                break;
                
        }
        m.cleanup();
        util.Debug("byte1 " + b1 + " byte2 " + b2 + " byte3 " + b3);
    }

    public void openUINpc(Player p, Message m) throws IOException {
        short idnpc = m.reader().readShort();//idnpc
        m.cleanup();
        p.c.typemenu = 0;
        p.typemenu = idnpc;
        if (idnpc == 33) {
            switch (server.manager.event) {
                case 1:
                    doMenuArray(p, new String[]{"Diều giấy", "Diều vải", "Nói chuyện"});
                    return;
                case 2:
                    doMenuArray(p, new String[]{"Hộp bánh thường", "Hộp bánh vip", "Bánh thập cẩm", "Bánh dẻo", "Đậu xanh", "Bánh pía", "Nói chuyện"});
                    return;
            }
        }
        m = new Message(40);
        
        if (idnpc == 19) {
         m.writer().writeUTF("Không nhận EXP");
         m.writer().writeUTF("Đổi mật khẩu");
      }

        if (idnpc == 22) {
         p.c.typemenu = 1;
         m.writer().writeUTF("Điểm danh gia tộc");
      }
      
        if (idnpc == 18) {
         p.c.typemenu = 1;
         m.writer().writeUTF(" Trò chơi Nhân phẩm");
      }
        
        if(idnpc == 36) { // Người viết Dũng Trần
            p.c.typemenu = 1;
            this.doMenuArray(p, new String[]{" Nói chuyện"});
            return;
         }
        if (idnpc == 21) { // NPC Sunoo
            m.writer().writeUTF(" Làm cần câu vàng");
            m.writer().writeUTF(" Chơi xổ số");
        }
        
        if (idnpc == 0 && p.c.mapid == 110) {
            doMenuArray(p, new String[]{"Thoát ra", "Đặt cược",});
            return;
        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void doMenuArray(Player p, String[] menu) throws IOException {
        Message m = new Message(63);
        for (byte i = 0; i < menu.length; i++) {
            m.writer().writeUTF(menu[i]);//menu
        }
        m.writer().flush();
        p.conn.sendMessage(m);
        m.cleanup();
    }

    public void sendWrite(Player p, short type, String title) {
        try {
            Message m = new Message(92);
            m.writer().writeUTF(title);
            m.writer().writeShort(type);
            m.writer().flush();
            p.conn.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
        }
    }
}
