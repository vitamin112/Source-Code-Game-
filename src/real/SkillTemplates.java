package real;

/**
 *
 * @author Dũng Trần
 */

import java.util.ArrayList;

public class SkillTemplates {

    public short skillId;
    public byte point;
    public int level;
    public short manaUse;
    public int coolDown;
    public short dx;
    public short dy;
    public byte maxFight;
    public ArrayList<Option> options = new ArrayList<>();
}
