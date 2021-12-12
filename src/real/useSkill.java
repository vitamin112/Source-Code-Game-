package real;

/**
 *
 * @author Dũng Trần
 */

import io.Message;
import java.io.IOException;

public class useSkill {
    
    public static void useSkill(Player p, Message m) throws IOException {
        short idSkill = m.reader().readShort();
        m.cleanup();
        Skill skill = p.c.get().getSkill(idSkill);
        if (skill != null && System.currentTimeMillis() > p.c.get().CSkilldelay) {
            SkillData data = SkillData.Templates(idSkill);
            if (data.type != 0) {
                p.c.get().CSkilldelay = System.currentTimeMillis() + 500;
                if (data.type == 2) {
                    useSkillBuff(p,idSkill);
                } else {
                    p.c.get().CSkill = idSkill;
                }
            }
        }
    }
    
    private static void useSkillBuff(Player p, int skilltemp) throws IOException {
        Skill skill = p.c.get().getSkill(skilltemp);
        SkillTemplates temp = SkillData.Templates(skill.id, skill.point);
        if (p.c.get().mp < temp.manaUse) {
            p.getMp();
            return;
        }
        if (skill.coolDown > System.currentTimeMillis())
            return;
        p.c.get().upMP(-temp.manaUse);
        skill.coolDown = System.currentTimeMillis() + temp.coolDown;
        int param = 0;
        switch (skilltemp) {
            case 6:
                p.setEffect(15, 0, p.c.get().getPramSkill(53)*1000, 0);
                break;
            case 13:
                p.setEffect(9, 0, 30000, p.c.get().getPramSkill(51));
                break;
            case 15:
                p.setEffect(16, 0, 5000, p.c.get().getPramSkill(52));
                break;
            case 31:
                p.setEffect(10, 0, 90000, p.c.get().getPramSkill(30));
                break;
            case 33:
                p.setEffect(17, 0, 5000, p.c.get().getPramSkill(56));
                break;
            case 47:
                param = p.c.get().getPramSkill(27);
                param += param*p.c.get().getPramSkill(66)/100;
                p.setEffect(8, 0, 5000, param);
                if (p.c.get().party != null)
                    for (int i = 0; i < p.c.place.players.size(); i++) {
                        Player p2 = p.c.place.players.get(i);
                        if (p2.c.id != p.c.id) {
                            Char n = p2.c;
                            if (n.party == p.c.get().party && Math.abs(p.c.get().x - n.x) <= temp.dx && Math.abs(p.c.get().y - n.y) <= temp.dy) {
                                n.p.setEffect(8, 0, 5000, p.c.get().getPramSkill(43)+(p.c.get().getPramSkill(43)*p.c.get().getPramSkill(66)/100));
                            }
                        }
                    }
                break;
            case 51:
                param = p.c.get().getPramSkill(45);
                param += p.c.get().getPramSkill(66);
                p.setEffect(19, 0, 90000, param);
                if (p.c.get().party != null)
                    for (int i = 0; i < p.c.place.players.size(); i++) {
                        Player p2 = p.c.place.players.get(i);
                        if (p2.c.id != p.c.id) {
                            Char n = p2.c;
                            if (n.party == p.c.get().party && Math.abs(p.c.x - n.x) <= temp.dx && Math.abs(p.c.y - n.y) <= temp.dy) {
                                n.p.setEffect(19, 0, 90000, param);
                            }
                        }
                    }
                break;
            case 52:
                p.setEffect(20, 0, p.c.get().getPramSkill(54)*1000, p.c.get().getPramSkill(66));
                if (p.c.get().party != null)
                    for (int i = 0; i < p.c.place.players.size(); i++) {
                        Player p2 = p.c.place.players.get(i);
                        if (p2.c.id != p.c.id) {
                            Char n = p2.c;
                            if (n.party == p.c.get().party && Math.abs(p.c.get().x - n.x) <= temp.dx && Math.abs(p.c.get().y - n.y) <= temp.dy) {
                                n.p.setEffect(20, 0, p.c.get().getPramSkill(54)*1000, p.c.get().getPramSkill(66));
                            }
                        }
                    }
                break;
            case 58:
                p.setEffect(11, 0, p.c.get().getPramSkill(64), 2000);
                break;
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
                if (p.c.timeRemoveClone > System.currentTimeMillis() || p.c.quantityItemyTotal(545) > 0) {
                    p.c.clone.open(System.currentTimeMillis()+(1000*60*p.c.getPramSkill(68)),p.c.getPramSkill(71));
                    if (p.c.quantityItemyTotal(545) > 0) {
                        p.c.removeItemBags(545, 1);
                    }
                } else {
                    p.sendAddchatYellow("Không có đủ "+ItemData.ItemDataId(545).name);
                }
                break;
        }
    }
    
    public static void useSkillSupport(Player p, int skilltemp, int type, Char n) throws IOException {
        Skill skill = p.c.get().getSkill(skilltemp);
        SkillTemplates temp = SkillData.Templates(skill.id, skill.point);
        switch (skilltemp) {
            case 49:
                if (n.isDie) {
                    n.p.liveFromDead();
                    n.p.setEffect(11, 0, 5000, p.c.get().getPramSkill(28));
                    break;
                }
        }
    }
    
    public static void buffLive(Player p, Message m) throws IOException {
        int idP = m.reader().readInt();
        Char nj = p.c.place.getNinja(idP);
        m.cleanup();
        Skill skill = p.c.get().getSkill(p.c.get().CSkill);
        if (nj != null && nj.isDie && skill.id == 49) {
            SkillTemplates temp = SkillData.Templates(skill.id, skill.point);
            if (skill.coolDown > System.currentTimeMillis() || Math.abs(p.c.get().x - nj.x) > temp.dx || Math.abs(p.c.get().y - nj.y) > temp.dy || p.c.get().mp < temp.manaUse) {
                return;
            }
            p.c.get().upMP(-temp.manaUse);
            skill.coolDown = System.currentTimeMillis() + temp.coolDown;
            nj.p.liveFromDead();
            nj.p.setEffect(11, 0, 5000, p.c.get().getPramSkill(28));
        }
    }
}
