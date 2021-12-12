package io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import real.PlayerManager;
import real.Player;
import server.GameScr;
import server.LogHistory;
import server.Server;
import server.util;

public class Session extends Thread {

    private static int baseId = 0;
    private final static String KEY = "D";
    public int id;
    private boolean connected = false;
    
    private boolean getKeyComplete = false;

    private ArrayList<Message> sendDatas = new ArrayList<>();
    private byte curR, curW;

    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;

    ISessionHandler messageHandler;

    private final Object LOCK = new Object();

    public Player player = null;

    public int outdelay = 50;
    private byte type;
    public byte zoomLevel;
    private boolean isGPS;
    private int width;
    private int height;
    private boolean isQwert;
    private boolean isTouch;
    private String plastfrom;
    private byte languageId;
    private int provider;
    private String agent;

    private Server server = Server.getInstance();
	private String versionARM;
    private LogHistory LogHistory = new LogHistory(this.getClass());
	
    public Session(Socket socket, ISessionHandler handler) {
        id = baseId++;
        try {
            setSocket(socket);
            messageHandler = handler;
            connected = true;
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    private void setSocket(Socket socket) throws IOException {
        this.socket = socket;

        if (socket != null) {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        }
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(connected) {
                        while(connected && sendDatas.size() > 0) {
                            Message m = sendDatas.remove(0);
                            if (m != null)
                                doSendMessage(m);
                        }
                        synchronized (LOCK) {
                            try {
                                LOCK.wait(10);
                            } catch (InterruptedException e) {}
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            while (connected) {
                Message message = readMessage();
                if (message != null) {
                    util.Debug(this +" do message "+message.getCommand()+" size "+message.reader().available());
                    messageHandler.processMessage(this, message);
                    message.cleanup();
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
        }
        disconnect();
        dis = null;
    }

    private Message readMessage() throws Exception {
        byte cmd = dis.readByte();
        if (cmd != -27) {
            cmd = readKey(cmd);
        }
        int size;
        if (cmd != -27) {
            byte b1 = dis.readByte();
            byte b2 = dis.readByte();
            size = (readKey(b1) & 255) << 8 | readKey(b2) & 255;
        } else {
            size = dis.readUnsignedShort();
        }
        byte data[] = new byte[size];
        int len = 0;
        int byteRead = 0;
        while (len != -1 && byteRead < size) {
            len = dis.read(data, byteRead, size - byteRead);
            if (len > 0) {
                byteRead += len;
            }
        }
        if (cmd != -27) {
            for (int i = 0; i < data.length; i++) {
                data[i] = readKey(data[i]);
            }
        }
        Message msg = new Message(cmd, data);
        return msg;
    }

    protected synchronized void doSendMessage(Message m) throws IOException {
        try {
            byte[] data = m.getData();
            if (data != null) {
                byte b = m.getCommand();
                int size = data.length;
                if (size > 65535)
                    b = -32;
                if (getKeyComplete) {
                    dos.writeByte(writeKey(b));
                } else {
                    dos.writeByte(b);
                }
                if (b == -32) {
                    b = m.getCommand();
                    if (getKeyComplete)
                        dos.writeByte(writeKey(b));
                    else
                        dos.writeByte(b);
                    int byte1 = writeKey((byte) (size >> 24));
                    dos.writeByte(byte1);
                    int byte2 = writeKey((byte) (size >> 16));
                    dos.writeByte(byte2);
                    int byte3 = writeKey((byte) (size >> 8));
                    dos.writeByte(byte3);
                    int byte4 = writeKey((byte) (size & 255));
                    dos.writeByte(byte4);
                } else if (getKeyComplete) {
                    int byte1 = writeKey((byte) (size >> 8));
                    dos.writeByte(byte1);
                    int byte2 = writeKey((byte) (size & 255));
                    dos.writeByte(byte2);
                } else {
                    int byte1 = (byte) (size & 65280);
                    dos.writeByte(byte1);
                    int byte2 = (byte) (size & 255);
                    dos.writeByte(byte2);
                }
                if (getKeyComplete) {
                    for (int i = 0; i < size; i++) {
                        data[i] = writeKey(data[i]);
                    }
                }
                dos.write(data);
                util.Debug("do mss "+b+" size "+size);
            }
            dos.flush();
        } catch(IOException e) {
            disconnect();
            System.out.println("Error write message from client " + id);
        }
    }
    
    public void sendMessage(Message m) {
        if (connected) {
            sendDatas.add(m);
        }
    }

    private byte readKey(byte b) {
        byte i = (byte) ((KEY.getBytes()[curR++] & 255) ^ (b & 255));
        if (curR >= KEY.getBytes().length) {
            curR %= KEY.getBytes().length;
        }
        return i;
    }

    private byte writeKey(byte b) {
        byte i = (byte) ((KEY.getBytes()[curW++] & 255) ^ (b & 255));
        if (curW >= KEY.getBytes().length) {
            curW %= KEY.getBytes().length;
        }
        return i;
    }

    public void hansakeMessage() throws IOException {
        Message m = new Message(-27);
        m.writer().writeByte(KEY.getBytes().length);
        m.writer().writeByte(KEY.getBytes()[0]);
        for (int i = 1; i < KEY.getBytes().length; i++) {
            m.writer().writeByte(KEY.getBytes()[i] ^ KEY.getBytes()[i - 1]);
        }
        m.writer().flush();
        doSendMessage(m);
        m.cleanup();
        getKeyComplete = true;
    }

    public void sendMessageLog(String str) {
        try {
            Message m = new Message(-26);
            m.writer().writeUTF(str);
            m.writer().flush();
            sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

    @Override
    public String toString() {
        return "Conn:" + id;
    }
    
    public void setConnect(Message m) throws IOException {
        type = m.reader().readByte();
        zoomLevel = m.reader().readByte();
        isGPS = m.reader().readBoolean();
        width = m.reader().readInt();
        height = m.reader().readInt();
        isQwert = m.reader().readBoolean();
        isTouch = m.reader().readBoolean();
        plastfrom = m.reader().readUTF();
        m.reader().readInt();
        m.reader().readByte();
        languageId = m.reader().readByte();
        provider = m.reader().readInt();
        agent = m.reader().readUTF();
        m.cleanup();
        util.Debug("Connection type "+type+" zoomlevel "+zoomLevel+" width "+width+" height "+height);
    }

    public void loginGame(Message m) throws Exception {
        String uname = util.strSQL(m.reader().readUTF());
        String passw = util.strSQL(m.reader().readUTF());
        String version = m.reader().readUTF();
        String t1 = m.reader().readUTF();
        String packages = m.reader().readUTF();
        String random = m.reader().readUTF();
        byte sv = m.reader().readByte();
        LogHistory.log(String.format("ID: %d - tai khoan: %s - mat khau: %s - phien ban: %s - so hieu dang nhap: %s - so may chu: %d", id, uname, passw, version, random, sv));
        this.versionARM = version;
        m.cleanup();
        int songuoi = PlayerManager.getInstance().conns_size();
        if (songuoi > 100) {
            this.sendMessageLog("Máy chủ hiện đã đủ số lượng người chơi [100/100], vui lòng thử lại sau ít phút");
        } else {
        Player p = Player.login(this, uname, passw);
        if (p != null) {
            player = p;
            outdelay = 0;
            // Load Player
            server.manager.getPackMessage(p);
            p.selectNhanVat(null);
        } 
        else {
        this.sendMessageLog("Đăng nhập thất bại, vui lòng đóng và đăng nhập lại!");
        }
    }
    }
    
    public void disconnect() {
        if (connected) {
            connected = false;
            //System.out.println("Session:" + id + " disconnect");
            try {
                if (socket != null) {
                    socket.close();
                //    PlayerManager.getInstance().removeClient(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            messageHandler.onDisconnected(this);
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
    }

}