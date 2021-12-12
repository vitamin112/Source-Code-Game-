package real;

/**
 *
 * @author Dũng Trần
 */


import java.util.ArrayList;

public class ItemSell {

    public int id;
    public byte type;
    public Item[] item;
    public static ArrayList<ItemSell> entrys = new ArrayList<>();

    public static ItemSell SellItemType(int type) {
        for (ItemSell entry : entrys) {
            if (entry.type == type) {
                return entry;
            }
        }
        return null;
    }

    public static Item getItemTypeIndex(int type, int index) {
        for (ItemSell entry : entrys) {
            if (entry.type == type && index >= 0 && index < entry.item.length) {
                return entry.item[index];
            }
        }
        return null;
    }
}
