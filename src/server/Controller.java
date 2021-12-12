package server;

/**
 *
 * @author Dũng Trần
 */

import io.ISessionHandler;
import io.Message;
import io.Session;
import real.ClanManager;
import real.Player;
import real.useItem;
import static real.useSkill.useSkill;

public class Controller implements ISessionHandler {

    Server server = Server.getInstance();

    @Override
    public void onConnectOK(Session conn) {

    }

    @Override
    public void onConnectionFail(Session conn) {

    }

    @Override
    public void onDisconnected(Session conn) {
        conn.outdelay = 5;
    }

    @Override
    public void processMessage(Session conn, Message message) {
        ServerController ctl = server.controllerManager;
        try {
            Player p = conn.player;
            switch (message.getCommand()) {
                case -30:
                    if (p != null) {
                        p.messageSubCommand(message);
                    }
                    break;
                case -29:
                    ctl.processGameMessage(conn, message);
                    break;
                case -28:
                    if (p != null) {
                        p.processGameMessage(message);
                    }
                    break;
                // Handsake
                case -27:
                    conn.hansakeMessage();
                    break;
                //Chat cong dong
                case -23:
                    if (p != null) {
                        p.c.place.Chat(p, message);
                    }
                    break;
                //Chat rieng
                case -22:
                    if (p != null) {
                        p.privateChat(message);
                    }
                    break;
                //Chat Kenh The Gioi
                case -21:
                    if (p != null) {
                        server.manager.chatKTG(p, message);
                    }
                    break;
                //Chat chung nhom
                case -20:
                    if (p != null && p.c != null) {
                        p.chatParty(message);
                    }
                    break;
                //Chat clan 
                case -19:
                    if (p != null && !p.c.isDie) {
                        ClanManager clan = ClanManager.getClanName(p.c.clan.clanName);
                        if (clan != null) {
                            clan.chat(p, message);
                        }
                    }
                    break;
                //Next map
                case -17:
                    if (p != null && !p.c.isDie) {
                        p.c.place.VGo(p, message);
                    }
                    break;
                //nhat vat pham
                case -14:
                    if (p != null && p.c != null && !p.c.isDie) {
                        p.c.place.pickItem(p, message);
                    }
                    break;
                //Bo item ra dat
                case -12:
                    if (p != null && !p.c.isDie) {
                        p.c.place.leaveItemBackground(p, message);
                    }
                    break;
                //hs
                case -10:
                    if (p != null && p.c.isDie && !p.c.isNhanban) {
                        p.c.place.wakeUpDieReturn(p);
                    }
                    break;
                //Quay ve
                case -9:
                    if (p != null && p.c.isDie && !p.c.isNhanban) {
                        p.c.place.DieReturn(p);
                    }
                    break;
                //Di chuyen
                case 1:
                    if (p != null && p.c != null && !p.c.isDie) {
                        p.c.place.moveMessage(p, message);
                    }
                    break;
                //pk boss
                case 4:
                    if (p != null && !p.c.isDie) {
                        p.c.place.FightMob2(p, message);
                    }
                    break;
                //Dung item
                case 11:
                    if (p != null && !p.c.isDie) {
                        p.useItem(message);
                    }
                    break;
                //Dung item next map
                case 12:
                    if (p != null && p.c != null && !p.c.isDie) {
                        useItem.useItemChangeMap(p, message);
                    }
                    break;
                //Mua item
                case 13:
                    if (p != null && !p.c.isDie) {
                        GameScr.buyItemStore(p, message);
                    }
                    break;
                //Ban do
                case 14:
                    if (p != null) {
                        p.SellItemBag(message);
                    }
                    break;
                // cat trang bi
                case 15:
                    if (p != null) {
                        p.itemBodyToBag(message);
                    }
                    break;
                // Lay do trong ruong ra
                case 16:
                    if (p != null) {
                        p.itemBoxToBag(message);
                    }
                    break;
                // Cat do vo ruong
                case 17:
                    if (p != null) {
                        p.itemBagToBox(message);
                    }
                    break;
                // luyen da xu
                case 19:
                    if (p != null) {
                        GameScr.crystalCollect(p, message, true);
                    }
                    break;
                // luyen da yen
                case 20:
                    if (p != null) {
                        GameScr.crystalCollect(p, message, false);
                    }
                    break;
                // dap do
                case 21:
                    if (p != null) {
                        GameScr.UpGrade(p, message);
                    }
                    break;
                // tach do
                case 22:
                    if (p != null) {
                        GameScr.Split(p, message);
                    }
                    break;
                //Chon khu vuc
                case 28:
                    if (p != null && !p.c.isDie) {
                        p.c.place.selectUIZone(p, message);
                    }
                    break;
                //send menu
                case 29:
                    if (p != null) {
                        server.menu.sendMenu(p, message);
                    }
                    break;
                //Mo doi khu vuc
                case 36:
                    if (p != null && !p.c.isDie) {
                        p.c.place.openUIZone(p);
                    }
                    break;
                //Mo npc
                case 40:
                    if (p != null && !p.c.isDie) {
                        server.menu.openUINpc(p, message);
                    }
                    break;
                //Dung ki nang
                case 41:
                    if (p != null && !p.c.isDie) {
                        useSkill(p, message);
                    }
                    break;
                //Xem info item
                case 42:
                    if (p != null) {
                        p.requestItemInfo(message);
                    }
                    break;
                //Giao Dich
                case 43:
                    if (p != null && !p.c.isDie) {
                        p.requestTrade(message);
                    }
                    break;
                //Bat dau giao dich
                case 44:
                    if (p != null && !p.c.isDie) {
                        p.startTrade(message);
                    }
                    break;
                //Khoa giao dich
                case 45:
                    if (p != null) {
                        p.lockTrade(message);
                    }
                    break;
                //Dong y giao dich
                case 46:
                    if (p != null) {
                        p.agreeTrade();
                    }
                    break;
                //select menu npc
                case 47:
                    if (p != null) {
                        p.c.place.selectMenuNpc(p, message);
                    }
                    break;
                //colse y giao dich
                case 56:
                    if (p != null) {
                        p.closeTrade();
                    }
                    break;
                //Dong giao dich
                case 57:
                    if (p != null) {
                        p.closeLoad();
                    }
                    break;
                //Ket ban
                case 59:
                    if (p != null) {
                        p.addFriend(message);
                    }
                    break;
                //up bos
                case 60:
                    if (p != null && !p.c.isDie) {
                        p.c.place.FightMob(p, message);
                    }
                    break;
                //pk
                case 61:
                    if (p != null && !p.c.isDie) {
                        p.c.place.FightNinja(p, message);
                    }
                    break;
                // tỉ thí
                case 65: {
                    if (p != null && !p.c.isDie) {
                        p.requestSolo(message);
                    }
                    break;
                }
                case 66: {
                    if (p != null && !p.c.isDie) {
                        p.startSolo(message);
                    }
                    break;
                }
                case 67: {
                    if (p != null && !p.c.isDie) {
                        p.endSolo(message);
                    }
                    break;
                }
                // party
                case 79:
                    if (p != null) {
                        p.addParty(message);
                    }
                    break;
//                Dong y vao nhom
                case 80:
                    if (p != null) {
                        p.addPartyAccept(message);
                    }
                    break;
//                Thoat
                case 83:
                    if (p != null && p.c != null && p.c.get().party != null) {
                        p.c.get().party.exitParty(p.c);
                    }
                    break;
                // nhap
                case 92:
                    if (p != null) {
                        Draw.Draw(p, message);
                    }
                    break;
                //view info players
                case 93:
                    if (p != null) {
                        p.viewPlayerMessage(message);
                    }
                    break;
                //view option players
                case 94:
                    if (p != null) {
                        p.viewOptionPlayers(message);
                    }
                    break;
                case 108:
                    if (p != null) {
                        p.itemMonToBag(message);
                    }
                    break;
                case 110: {
                    if (p != null) {
                        GameScr.LuyenThach(p, message);
                        break;
                    }
                    break;
                }
                case 111:
                    if (p != null) {
                        GameScr.TinhLuyen(p, message);
                    }
                    break;
                case 112:
                    if (p != null) {
                        GameScr.DichChuyen(p, message);
                    }
                    break;
                case 124: {
                    if (p != null) {
                        GameScr.luyenNgoc(p, message);
                        break;
                    }
                    break;
                }
                case 125:
                    if (p != null) {
                        byte b = message.reader().readByte();
                        if (b == 1) {
                            GameCanvas.getImgEffect(p.conn, message.reader().readShort());
                        } else if (b == 2) {
                            GameCanvas.getDataEffect(p.conn, message.reader().readShort());
                        }
                    }
                    break;
            }
            message.cleanup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
