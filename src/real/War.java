package real;

/**
 *
 * @author Dũng Trần
 */

import java.io.IOException;
import java.util.ArrayList;

public class War {
    public boolean status;
    public int pointBlack;
    public int pointWhite;
    public int win;
    public ArrayList<Char> ninjas = new ArrayList<>();

    public War() {
        this.pointBlack = 0;
        this.pointWhite = 0;
        this.win = 0;
    }

    public void updatePoint(Char c,int point) {
        synchronized (this) {
            c.pointCT += point;
            c.p.setPointCT(c.pointCT);
        }
    }

    public void rest(Char c) throws IOException {
        while (c.typeCT > 3) {
            c.changePk(c,(byte)0);
            c.place.leave(c.p);
            Map ma = server.Manager.getMapid(27);
            for (byte k = 0; k < ma.area.length; k++) {
                if (ma.area[k].numplayers < ma.template.maxplayers) {
                    ma.area[k].EnterMap0(c);
                    break;
                }
            }
        }
    }
}
