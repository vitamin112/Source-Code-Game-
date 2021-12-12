package server;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import io.Session;

public class GameCanvas {
    
    
    protected static void addInfoDlg(Session session, String s) {
        Message msg = null;
        try {
            msg = Service.messageNotMap((byte) -86);
            msg.writer().writeUTF(s);
            session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    protected static void startOKDlg(Session session, String info) {
        Message msg = null;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(info);
            session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void addEffect(Session session, byte b, int vId, short id, int timelive, int miliSecondWait, boolean isHead) {
        Message msg = null;
        try {
            msg = new Message(125);
            msg.writer().writeByte(0);
            msg.writer().writeByte(b);
            if (b == 1) {
                msg.writer().writeByte(vId);
            } else {
                msg.writer().writeInt(vId);
            }
            msg.writer().writeShort(id);
            msg.writer().writeInt(timelive);
            msg.writer().writeByte(miliSecondWait);
            msg.writer().writeByte((isHead?1:0));
            msg.writer().flush();
            session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void getImgEffect(Session session, short id) {
        Message msg = null;
        try {
            byte[] ab = GameScr.loadFile("res/Effect/x"+session.zoomLevel+"/ImgEffect/ImgEffect "+id+".png").toByteArray();
            if (ab != null) {
                msg = new Message(125);
                msg.writer().writeByte(1);
                msg.writer().writeByte(id);
                msg.writer().writeInt(ab.length);
                msg.writer().write(ab);
                msg.writer().flush();
                session.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }
    
    public static void getDataEffect(Session session, short id) {
        Message msg = null;
        try {
            byte[] ab = GameScr.loadFile("res/Effect/x"+session.zoomLevel+"/DataEffect/"+id).toByteArray();
            if (ab != null) {
                msg = new Message(125);
                msg.writer().write(ab);
                msg.writer().flush();
                session.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (msg != null)
                msg.cleanup();
        }
    }

    public static void addEffect(Session conn, byte b, int id, int i, int i0, int i1, boolean b0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
