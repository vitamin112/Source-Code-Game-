package real;

/**
 *
 * @author Dũng Trần
 */


import java.util.ArrayList;

public class Item {

    public short id = -1;
    public boolean isLock = false;
    public byte upgrade = 0;
    public boolean isExpires = false;
    public int quantity = 1;
    public long expires = -1;
    public int saleCoinLock = 0;
    public int buyCoin = 0;
    public int buyCoinLock = 0;
    public int buyGold = 0;
    public byte sys = 0;
    public ArrayList<Option> options = new ArrayList<>();
    
    @Override
    public Item clone() {
        Item item = new Item();
        item.id = this.id;
        item.isLock = this.isLock;
        item.upgrade = this.upgrade;
        item.isExpires = this.isExpires;
        item.quantity = this.quantity;
        item.expires = this.expires;
        item.saleCoinLock = this.saleCoinLock;
        item.buyCoin = this.buyCoin;
        item.buyCoinLock = this.buyCoinLock;
        item.buyGold = this.buyGold;
        item.sys = this.sys;
        for (int i = 0; i < this.options.size(); i++) {
            item.options.add(new Option(this.options.get(i).id,this.options.get(i).param));
        }
        return item;
    }
    
    public int getUpMax() {
        ItemData data = ItemData.ItemDataId(this.id);
        if (data.level >= 1 && data.level < 20) {
            return 4;
        }
        if (data.level >= 20 && data.level < 40) {
            return 8;
        }
        if (data.level >= 40 && data.level < 50) {
            return 12;
        }
        if (data.level >= 50 && data.level < 60) {
            return 14;
        }
        return 16;
    }
    
    public void upgradeNext(byte next) {
        this.upgrade += next;
        if (this.options != null) {
            for (short i = 0; i < this.options.size(); i++) {
                Option itemOption = this.options.get(i);
                if (itemOption.id == 6 || itemOption.id == 7) {
                    itemOption.param += (15 * next);
                } else if (itemOption.id == 8 || itemOption.id == 9 || itemOption.id == 19) {
                    itemOption.param += (10 * next);
                } else if (itemOption.id == 10 || itemOption.id == 11 || itemOption.id == 12 || itemOption.id == 13 || itemOption.id == 14 || itemOption.id == 15 || itemOption.id == 17 || itemOption.id == 18 || itemOption.id == 20) {
                    itemOption.param += (5 * next);
                } else if (itemOption.id == 21 || itemOption.id == 22 || itemOption.id == 23 || itemOption.id == 24 || itemOption.id == 25 || itemOption.id == 26) {
                    itemOption.param += (150 * next);
                } else if (itemOption.id == 16) {
                    itemOption.param += (3 * next);
                }
            }
	}
    }
    
    public int getOptionShopMin(int opid, int param) {
        if (opid == 0 || opid == 1 || opid == 21 || opid == 22 || opid == 23 || opid == 24 || opid == 25 || opid == 26) {
            return (param - 50 + 1);
        } else if (opid == 6 || opid == 7 || opid == 8 || opid == 9 || opid == 19) {
            return (param - 10 + 1);
        } else if (opid == 2 || opid == 3 || opid == 4 || opid == 5 || opid == 10 || opid == 11 || opid == 12 || opid == 13 || opid == 14 || opid == 15 || opid == 17 || opid == 18 || opid == 20) {
            return (param - 5 + 1);
        } else if (opid == 16) {
            return (param - 3 + 1);
	}
        return param;
    }
}
