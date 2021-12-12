package real;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import java.io.IOException;
import java.util.ArrayList;

public class Party {
    
    public int master = -1;
    public boolean lock = false;
    public ArrayList<Char> ninjas = new ArrayList<>();
    public ArrayList<Integer> pt = new ArrayList<>();
    public int id;
    public Cave cave;
    public static int basid;
    
    
    protected Party(Char n) {
        master = n.id;
        id = basid++;
    }
    
    protected void addPartyAccept(Char n) {
        ninjas.add(n);
        n.party = this;
        refreshTeam();
    }
    
    public void sendMessage(Message m) {
        for (byte i = 0; i < ninjas.size(); i++) {
            ninjas.get(i).p.conn.sendMessage(m);
        }
    }
    
    public Char getNinja(int id) {
        synchronized (this) {
            for (byte i = 0; i < ninjas.size(); i++) {
                if (ninjas.get(i).id == id) {
                    return ninjas.get(i);
                }
            }
            return null;
        }
    }
    
    public void openCave(Cave cave, String name) {
        synchronized (this) {
            this.cave = cave;
            for (byte i = 0; i < ninjas.size(); i++) {
                ninjas.get(i).p.sendAddchatYellow(name+" đã mở cửa hang động");
            }
        }
    }
    
    protected void addParty(Player p1, Player p2) throws IOException {
        if (ninjas.size() > 5) {
            p1.sendAddchatYellow("Số lượng thành viên đã tối đa");
            return;
        }
        pt.add(p2.conn.id);
        Message m = new Message(79);
        m.writer().writeInt(p1.c.id);
        m.writer().writeUTF(p1.c.name);
        m.writer().flush();
        p2.conn.sendMessage(m);
        m.cleanup();
    }
    
    public void changeTeamLeader(int index) {
        Char n1 = getNinja(master);
        synchronized (this) {
            Char n2 = ninjas.get(index);
            if (n1 != null && n1 != n2) {
                ninjas.set(index, n1);
                ninjas.set(0, n2);
                master = n2.id;
                for (byte i = 0; i < ninjas.size(); i++) {
                    ninjas.get(i).p.sendAddchatYellow(n2.name+" đã được lên làm nhóm trưởng");
                }
                this.refreshTeam();
            }
        }
    }
    
    public void moveMember(int index) {
        synchronized (this) {
            try {
                Char n = ninjas.remove(index);
                n.party = null;
                for (byte i = 0; i < ninjas.size(); i++) {
                    ninjas.get(i).p.sendAddchatYellow(n.name+" đã bị đuổi ra khỏi nhóm");
                }
                this.refreshTeam();
                Message m = new Message(83);
                m.writer().flush();
                n.p.conn.sendMessage(m);
                m.cleanup();
                n.p.sendAddchatYellow("Bạn đã bị đuổi ra khỏi nhóm");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void exitParty(Char n) {
        synchronized (this) {
            if (ninjas.contains(n)) {
                try {
                    Message m = new Message(83);
                    m.writer().flush();
                    n.p.conn.sendMessage(m);
                    m.cleanup();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ninjas.remove(n);
                if (ninjas.size() > 0) {
                    this.refreshTeam();
                    for (byte i = 0; i < ninjas.size(); i++) {
                        ninjas.get(i).p.sendAddchatYellow(n.name+" đã rời khỏi nhóm");
                    }
                    if (n.id == master) {
                        master = ninjas.get(0).id;
                        for (byte i = 0; i < ninjas.size(); i++) {
                            ninjas.get(i).p.sendAddchatYellow(ninjas.get(0).name+" đã đã được lên làm nhóm trưởng");
                        }
                    }
                } else {
                    master = -1;
                    ninjas.clear();
                    pt.clear();
                }
                n.get().party = null;
            }
        }
    }
    
    public void refreshTeam() {
        try {
            Message m = new Message(82);
            m.writer().writeBoolean(lock);
            for (byte i = 0; i < ninjas.size(); i++) {
                m.writer().writeInt(ninjas.get(i).get().id);
                m.writer().writeByte(ninjas.get(i).get().nclass);
                m.writer().writeUTF(ninjas.get(i).name);
            }
            m.writer().flush();
            for (byte i = 0; i < ninjas.size(); i++) {
                sendMessage(m);
            }
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
