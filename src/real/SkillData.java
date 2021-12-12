package real;

/**
 *
 * @author Dũng Trần
 */

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class SkillData {

    public int id;
    public byte nclass;
    public String name = null;
    public byte maxPoint;
    public byte type;
    public short iconId;
    public String desc;
    public ArrayList<SkillTemplates> templates = new ArrayList<>();

    public static ArrayList<SkillData> entrys = new ArrayList<>();

    public static SkillTemplates Templates(byte id, byte point) {
        for (SkillData temp : entrys) {
            if (temp.id == id) {
                for (SkillTemplates data : temp.templates) {
                    if (data.point == point) {
                        return data;
                    }
                }
            }
        }
        return null;
    }

    public static SkillData Templates(int id) {
        for (SkillData temp : entrys) {
            if (temp.id == id) {
                return temp;
            }
        }
        return null;
    }

    public static JSONObject ObjectSkill(Skill skill) {
        JSONObject put = new JSONObject();
        put.put("id", skill.id);
        put.put("point", skill.point);
        return put;
    }
}
