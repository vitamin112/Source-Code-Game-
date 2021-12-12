package real;

/**
 *
 * @author Dũng Trần
 */


import io.Message;
import io.Session;

import server.Server;

import server.ServerController;
import server.util;

public class RealController extends ServerController {

    Server server = Server.getInstance();

    @Override
    public void processGameMessage(Session conn, Message message) {
        try {
            byte b = message.reader().readByte();
            util.Debug("msg -29-> "+b);
            switch (b) {
                case -127:
                    if (conn.player == null) {
                        conn.loginGame(message);
                    }
                    break;
                case -125:
                    conn.setConnect(message);
                    break;
                //tach item
                case -85: {
                    ItemData.divedeItem(conn.player, message);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void userLogin(Session conn, Message m) {

    }

    @Override
    public boolean userLogout(Session conn) {

        return false;
    }

}
