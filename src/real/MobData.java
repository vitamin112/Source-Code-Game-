package real;

/**
 *
 * @author Dũng Trần
 */

import java.util.ArrayList;

public class MobData {
    public int id;
    public byte type;
    public String name;
    public int hp;
    public byte rangeMove;
    public byte speed;
    public short[] arrIdItem;
    
    public static ArrayList<MobData> entrys = new ArrayList<>();
    
    public static MobData getMob(int id) {
        for (MobData mob : entrys)
            if (mob.id == id)
                return mob;
        return null;
    }
    
//    public imgInfo[] getImg(byte lvZoom) {
////        if (lvZoom == 1)
//            return imginfo1;
////        if (lvZoom == 2)
////            return imginfo2;
////        if (lvZoom == 3)
////            return imginfo3;
////        if (lvZoom == 4)
////            return imginfo4;
////        return null;
//    }
}
