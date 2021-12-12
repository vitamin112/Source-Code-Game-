package server;

/**
 *
 * @author Dũng Trần
 */

import io.Session;
import io.Message;

public abstract class ServerController {

    public abstract void userLogin(Session conn, Message m);

    public abstract boolean userLogout(Session conn);

    public abstract void processGameMessage(Session conn, Message message);
}
