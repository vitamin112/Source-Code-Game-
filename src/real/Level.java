package real;

/**
 *
 * @author Dũng Trần
 */

import java.util.ArrayList;

public class Level {
    public int level;
    public long exps;
    public short ppoint;
    public short spoint;
    
    public static long[] getLevelExp(long exp) {
        long num = exp;
        int i;
        for (i = 0; i < Level.entrys.size(); i++){
            if (num < Level.entrys.get(i).exps){
                break;
            }
            num -= Level.entrys.get(i).exps;
        }
        return new long[] {(long)i,num};
    }
    
    public static short totalpPoint(int level) {
        short ppoint = 0;
        for (short i = 0; i < Level.entrys.size(); i++) {
            if (Level.entrys.get(i).level <= level)
                ppoint += Level.entrys.get(i).ppoint;
        }
        return ppoint;
    }
    
    public static short totalsPoint(int level) {
        short spoint = 0;
        for (short i = 0; i < Level.entrys.size(); i++) {
            if (Level.entrys.get(i).level <= level)
                spoint += Level.entrys.get(i).spoint;
        }
        return spoint;
    }
    
    public static long getMaxExp(int level) {
        long num = 0L;
        for (int i = 0; i < level; i++){
            num += getLevel(i).exps;
        }
        return num;
    }
    
    public static Level getLevel(int level) {
        for (short i = 0; i < Level.entrys.size(); i++) {
            if (Level.entrys.get(i).level == level)
                return Level.entrys.get(i);
        }
        return null;
    }
    
    public static ArrayList<Level> entrys = new ArrayList<>();
}
