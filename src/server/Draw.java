package server;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import java.io.IOException;
import java.sql.SQLException;
import real.ClanManager;
import real.Char;
import real.Player;
import real.PlayerManager;

public class Draw {
    
    private static final Server server = Server.getInstance();
    
    public static void Draw(Player p, Message m) throws IOException, InterruptedException, SQLException {
        short menuId = m.reader().readShort();
        String str = m.reader().readUTF();
        m.cleanup();
        System.out.println("menuId "+menuId+" str "+str);
        byte b = -1;
        try {
            b = m.reader().readByte();
        } catch (IOException e) {}
        m.cleanup();
        switch (menuId) {
            case 1:
                if (p.c.quantityItemyTotal(279) > 0) {
                    Char c = PlayerManager.getInstance().getNinja(str);
                    if (c != null) { 
                        if (!c.place.map.LangCo() && c.place.map.getXHD() == -1) {
                            p.c.place.leave(p);
                            p.c.get().x = c.get().x;
                            p.c.get().y = c.get().y;
                            c.place.Enter(p);
                            return;
                        }
                    }
                    p.sendAddchatYellow("Ví trí người này không thể đi tới");
                }
                break;
            // Giftcode
            case 47: {
                p.Giftcode(str);
                break;
            }    
            case 50:
                ClanManager.createClan(p, str);
                break;
            case 51:
                p.passnew = "";
                p.passold = str;
                p.changePassword();
                server.menu.sendWrite(p, (short)52, "Nhập mật khẩu mới");
                break;
            case 52:
                p.passnew = str;
                p.changePassword();
                break;
            case 53: // Lôi đài
                Char n = PlayerManager.getInstance().getNinja(str);
                p.inviteLD(p, n.p);
                break;
            case 54:
                String xu = str.replaceAll(" ", "").trim();
                if (xu.length() > 10 || !util.checkNumInt(xu)) {
                    return;
                }
                int xutong = Integer.parseInt(xu);
                p.xuLoiDai(xutong);            
                break;
            // XSMB Player
            case 1406:
                if (str.equals("") || Integer.parseInt(str) < 0 || Integer.parseInt(str) > 99){
                    p.conn.sendMessageLog("Vui lòng nhập đúng số từ 0 - 99");
                    return;
                }else {
                    int numLucky = Integer.parseInt(str);
                    p.submitNumLucky(numLucky);
                }
                break;
            case 1405:
                if (str.equals("") || Integer.parseInt(str) < 1 || Integer.parseInt(str) > 100000){
                    p.conn.sendMessageLog("Số tiền cược không hợp lệ");
                    return;
                }else {
                    int coinLucky = Integer.parseInt(str);
                    p.submitCoinLucky(coinLucky);
                }
                break;
            case 100:
                String num = str.replaceAll(" ", "").trim();
                if (num.length() > 10 || !util.checkNumInt(num) || b < 0 || b >= server.manager.rotationluck.length) {
                    return;
                }
                int xujoin = Integer.parseInt(num);
                server.manager.rotationluck[b].joinLuck(p, xujoin);
                break;
            case 101:
                if (b < 0 || b >= server.manager.rotationluck.length) {
                    return;
                }
                server.manager.rotationluck[b].luckMessage(p);
                break;
            case 102:
                p.typemenu = 92;
                server.menu.doMenuArray(p, new String[]{"Vòng xoay vip", "Vòng xoay thường"});
                break;
        }
    }
}
