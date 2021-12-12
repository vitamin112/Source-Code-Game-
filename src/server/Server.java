package server;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import io.Session;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import real.ClanManager;
import real.Map;
import real.MapTemplate;
import real.PlayerManager;
import real.RealController;
import server.LogHistory;

public class Server {

    private static Server instance = null;
    private ServerSocket listenSocket = null;
    public static boolean start = false;
    public ByteArrayOutputStream[] cache = new ByteArrayOutputStream[4];
    private LogHistory LogHistory = new LogHistory(this.getClass()); // Xuất IP đăng nhập ra log

    public Manager manager;
    public MenuController menu;
    public ServerController controllerManager;
    public Controller serverMessageHandler;

    public Map[] maps;
    public static final Object LOCK_MYSQL = new Object();
    private static final int[] hoursRefreshBoss = new int[]{0,3,6,9,12,15,18,20,22};
    private static final boolean[] isRefreshBoss = new boolean[]{false,false,false,false,false,false,false,false,false};
    private static final short[] mapBossVDMQ = new short[]{141,142,143};
    private static final short[] mapBossLangCo = new short[]{134,135,136,137,138};
    private static final short[] mapBoss45 = new short[]{14,15,16,34,35,52,68};
    private static final short[] mapBoss55 = new short[]{44,67};
    private static final short[] mapBoss65 = new short[]{24,41,45,59};
    private static final short[] mapBoss75 = new short[]{18,36,54};
    private static final short[] mapBossTG = new short[]{23};
    
    private static boolean running = true;
    
    public static Thread run = new Thread (new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while(running) {
                    synchronized (ClanManager.entrys) {
                        for (int i = ClanManager.entrys.size()-1; i >= 0; i--) {
                            ClanManager clan = ClanManager.entrys.get(i);
                            if (util.compare_Week(Date.from(Instant.now()),util.getDate(clan.week))) {
                                clan.payfeesClan();
                            }
                        }
                    }
                    Calendar rightNow = Calendar.getInstance();
                    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
                    final int min = rightNow.get(12);
                    final int sec = rightNow.get(13);
                    for (int i = 0; i < hoursRefreshBoss.length; i++) {
                        if (hoursRefreshBoss[i] == hour) {
                            if (!isRefreshBoss[i]) {
                                String textchat = "Thần thú đã suất hiện tại"; // Thông báo hiện boss
                                for (byte j = 0; j < util.nextInt(1,1); j++) {
                                    Map map = Manager.getMapid(mapBoss75[util.nextInt(mapBoss75.length)]);
                                    if (map != null) {
                                        int k = util.nextInt(14,29);
                                        map.refreshBoss(k);
                                        textchat += ", " + map.template.name + " khu " + k; // Xuất hiện ở khu nào
                                        isRefreshBoss[i] = true;
                                    }           
                                }
                                for (byte j = 0; j < util.nextInt(1,2); j++) {
                                    Map map = Manager.getMapid(mapBoss65[util.nextInt(mapBoss65.length)]);
                                    if (map != null) {
                                        int k = util.nextInt(14,29);
                                        map.refreshBoss(k);
                                        textchat += ", " + map.template.name + " khu " + k; // Xuất hiện ở khu nào
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < util.nextInt(1,2); j++) {
                                    Map map = Manager.getMapid(mapBoss55[util.nextInt(mapBoss55.length)]);
                                    if (map != null) {
                                        int k = util.nextInt(14,29);
                                        map.refreshBoss(k);
                                        textchat += ", " + map.template.name + " khu " + k; // Xuất hiện ở khu nào
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < util.nextInt(1,2); j++) {
                                    Map map = Manager.getMapid(mapBoss45[util.nextInt(mapBoss45.length)]);
                                    if (map != null) {
                                        int k = util.nextInt(14,29);
                                        map.refreshBoss(k);
                                        textchat += ", " + map.template.name + " khu " + k; // Xuất hiện ở khu nào
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < util.nextInt(1,2); j++) {
                                    Map map = Manager.getMapid(mapBossTG[util.nextInt(mapBossTG.length)]);
                                    if (map != null) {
                                        int k = util.nextInt(14,29);
                                        map.refreshBoss(k);
                                        textchat += ", " + map.template.name + " khu " + k; // Xuất hiện ở khu nào
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < mapBossVDMQ.length; j++) {
                                    Map map = Manager.getMapid(mapBossVDMQ[j]);
                                    if (map != null) {
                                        int k = util.nextInt(14,29);
                                        map.refreshBoss(k);
                                        textchat += ", " + map.template.name + " khu " + k; // Xuất hiện ở khu nào
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                for (byte j = 0; j < mapBossLangCo.length; j++) {
                                    Map map = Manager.getMapid(mapBossLangCo[j]);
                                    if (map != null) {
                                        int k = util.nextInt(7,14);
                                        map.refreshBoss(k);
                                        textchat += ", " + map.template.name + " khu " + k; // Xuất hiện ở khu nào
                                        isRefreshBoss[i] = true;
                                    }
                                }
                                Manager.chatKTG(textchat);     
                                if ((hour == 9 || hour == 19) && (min >= 55) && sec == 0) {
                                    Manager.chatKTG("Chiến trường sẽ bắt đầu sau " + (60 - min) + " phút nữa. Tham gia tại NPC Rikudou ở các trường.");
                                }
                                if ((hour == 9 || hour == 19) && (min % 15 == 0) && sec == 0) {
                                    SQLManager.stat.executeUpdate("UPDATE `ninja` SET `pointCT`='0',`typeCT`='0',`rewardedCT`='0' ");
                                }
                                if ((hour == 10 || hour == 20) && (min >= 55) && sec == 0) {
                                    Manager.chatKTG("Chiến trường sắp kết thúc! còn lại " + (60 - min) + " phút");
                                }
                                if (sec % 15 == 0) {
                                    BXHManager.init();
                                }
                            }
                        } else {
                            isRefreshBoss[i] = false;
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }));
    
    
    private void init() {
        manager = new Manager();
        menu = new MenuController();
        controllerManager = new RealController();
        serverMessageHandler = new Controller();
        // Tạo cache từ SQL
        Service.createCacheItem();
        // Load version phiên bản mới
        cache[1] = GameScr.loadFile("res/version/map");
        cache[2] = GameScr.loadFile("res/version/skill");
        cache[3] = GameScr.loadFile("res/version/item");
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
            instance.init();
            BXHManager.init();
            run.start();
        }
        return instance;
    }

    public static void main(String[] args) {
        start = true;
        getInstance().run();
    }

    public void run() {
        maps = new Map[MapTemplate.arrTemplate.length];
        for (short i = 0; i < maps.length; i++) {
            maps[i] = new Map(i, null);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Da tat may chu Taurus!");
                stop();
            }
        }));
        listenSocket = null;
        try {
            listenSocket = new ServerSocket(manager.post);
            System.out.println("Da mo cong ket noi " + manager.post +" voi may chu Taurus\n---------- Khoi Dong Server Taurus ----------");
            while (start) {
                Socket clientSocket = listenSocket.accept();
                Session conn = new Session(clientSocket, serverMessageHandler);
                PlayerManager.getInstance().put(conn);
                conn.start();
                System.out.println("So IP dang truy cap vao may chu Taurus: " + PlayerManager.getInstance().conns_size());
                // Xuất IP đăng nhập ra log
                InetSocketAddress socketAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
                String clientIpAddress = socketAddress.getAddress().getHostAddress();
                LogHistory.log2(clientIpAddress.toString());
            }
        } catch (BindException bindEx) {
            System.exit(0);
        } catch (IOException genEx) {
            genEx.printStackTrace();
        }
        try {
            if (listenSocket != null) {
                listenSocket.close();
            }
            System.out.println("Ngung cho phep IP truy cap vao may chu Taurus");
        } catch (Exception ioEx) {
        }
    }

    public void stop() {
        if (start) {
            start = false;
            try {
                listenSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //Kick all Player game
            PlayerManager.getInstance().Clear();
            ClanManager.close();
            manager.close();
            manager = null;
            PlayerManager.getInstance().close();
            menu = null;
            controllerManager = null;
            serverMessageHandler = null;
            SQLManager.close();
            System.gc();
        }
    }
}
