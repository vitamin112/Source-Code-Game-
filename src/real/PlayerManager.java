package real;

/**
 *
 * @author Dũng Trần
 */


import io.Message;
import io.Session;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerManager implements Runnable {
    protected PlayerManager() {
        new Thread(this).start();
    }

    protected static PlayerManager instance;

    public static PlayerManager getInstance() {
        if (instance == null)
            instance = new PlayerManager();
        return instance;
    }

    private boolean runing = true;
    
    private final ArrayList<Session> conns = new ArrayList<Session>();
    private final HashMap<Integer, Session> conns_id = new HashMap<>();

    private final HashMap<Integer, Player> players_id = new HashMap<Integer, Player>();
    private final HashMap<String, Player> players_uname = new HashMap<String, Player>();

    private final HashMap<Integer, Char> ninjas_id = new HashMap<Integer, Char>();
    private final HashMap<String, Char> ninjas_name = new HashMap<String, Char>();

    public void sendMessage(Message m) {
        synchronized (conns) {
            for (int i = conns.size()-1; i >= 0; i--)
                conns.get(i).sendMessage(m);
        }
    }
    
    public void NinjaMessage(Message m) {
        synchronized (conns) {
            for (int i = conns.size()-1; i >= 0; i--)
                if (conns.get(i).player != null && conns.get(i).player.c != null)
                    conns.get(i).sendMessage(m);
        }
    }
    
    public void put(Session conn) {
        if (!conns_id.containsValue(conn))
            conns_id.put(conn.id, conn);
        if (!conns.contains(conn))
            conns.add(conn);
    }
    
    public void put(Player p) {
        if (!players_id.containsKey(p.id))
            players_id.put(p.id, p);
        if (!players_uname.containsKey(p.username))
            players_uname.put(p.username, p);
    }
    
    public void put(Char n) {
        if (!ninjas_id.containsKey(n.id))
            ninjas_id.put(n.id, n);
        if (!ninjas_name.containsKey(n.name))
            ninjas_name.put(n.name, n);
    }
    
    private void remove(Session conn) {
        if (conns_id.containsKey(conn.id))
            conns_id.remove(conn.id);
        if (conns.contains(conn))
            conns.remove(conn);
        if (conn.player != null)
            remove(conn.player);
    }
    
    private void remove(Player p) {
        if (players_id.containsKey(p.id))
            players_id.remove(p.id);
        if (players_uname.containsKey(p.username))
            players_uname.remove(p.username);
        if (p.c != null)
            remove(p.c);
        p.close();
        p.flush();
    }
    
    private void remove(Char n) {
        if (ninjas_id.containsKey(n.id))
            ninjas_id.remove(n.id);
        if (ninjas_name.containsKey(n.name))
            ninjas_name.remove(n.name);
        n.close();
        n.flush();
        if (n.clone != null)
            n.clone.flush();
    }
    
    public Session getConn(int id) {
        return conns_id.get(id);
    }
    
    public Player getPlayer(int id) {
        return players_id.get(id);
    }
    
    public Player getPlayer(String uname) {
        return players_uname.get(uname);
    }
    
    public Char getNinja(int id) {
        return ninjas_id.get(id);
    }
    
    public Char getNinja(String name) {
        return ninjas_name.get(name);
    }

    public int conns_size() {
        return conns_id.size();
    }
    
    public int players_size() {
        return players_id.size();
    }
    
    public int ninja_size() {
        return ninjas_id.size();
    }

    public void kickSession(Session conn) {
        remove(conn);
        conn.disconnect();
    }
    
    public void Clear() {
        while(!conns.isEmpty())
            kickSession(conns.get(0));
    }
    
    private void update() {
        for (int i = conns.size()-1; i >= 0; i--) {
            Session conn = conns.get(i);
            if (conn.outdelay > 0) {
                conn.outdelay--;
                if (conn.outdelay == 0) {
                    kickSession(conn);
                }
            }
        }
    }
    // Xóa cache
    public void removeClient(Session cl) {
        synchronized (conns) {
            conns.remove(cl);
            //logger.log("Disconnect client: " + cl);
        }
    }
    
    @Override
    public void run() {
        while (runing) {
            long l1 = System.currentTimeMillis();
            update();
            // Synchronize time
            long l2 = System.currentTimeMillis() - l1;
            if (l2 < 200)
                try {
                    Thread.sleep(200 - l2);
                } catch (InterruptedException e) {
                }
        }
    }
    
    public void close() {
        runing = false;
        instance = null;
    }
}
