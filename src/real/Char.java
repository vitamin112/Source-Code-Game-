package real;

/**
 *
 * @author Dũng Trần
 */


import boardGame.Place;
import io.Message;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import server.SQLManager;
import server.Server;
import server.util;

public class Char extends Body {
    
    public int typeSolo;
    public byte maxBPL = 0;
    public byte maxBBH = 0;
    public byte maxSKN = 0;
    public byte maxSTN = 0;
    public Player p = null;
    public Place place = null;
    public String name = null;
    public ClanMember clan = null;
    public byte taskId = 0;
    public byte gender = -1;
    public int xu = 0;
    public int xuBox = 0;
    public int yen = 0;
    public byte maxluggage = 30;
    protected byte levelBag = 0;
    public Item[] ItemBag = null;
    public Item[] ItemBox = null;
    protected String friend = "[]";
    public int mapType = 0;
    public int mapLTD = 22;
    public int mapid = 22;
    public int mobAtk = -1;
    public long eff5buff = 0L;
    public byte type = 0;
    protected boolean isTrade = false;
    protected int rqTradeId;
    protected int tradeId;
    protected int tradeCoin = 0;
    protected long tradeDelay = 0L;
    protected byte tradeLock = -1;
    protected ArrayList<Byte> tradeIdItem = new ArrayList<>();
    public byte denbu = 0;
    public Date newlogin = null;
    public boolean ddClan = false;
    public boolean ddLogin = false;
    public int caveID = -1;
    public int nCave = 1;
    public int pointCave = 0;
    public int useCave = 1;
    protected int bagCaveMax = 0;
    protected short itemIDCaveMax = -1;
    public int requestclan = -1;
    public long deleyRequestClan = 0L;
    public long delayEffect = 0L;
    public long timeRemoveClone = -1;
    public int typemenu;
    public cloneChar clone = null;
    public int pointCT = 0;
    public int typeCT = 0;
    public byte rewardedCT = 0;
    public int xuLoiDai; // Lôi đài
    public int ldgtID = -1;
    public int ldgtNum = -1; // LDGT
    
    private Char() {
        seNinja(this);
    }
    
    public Body get() {
        Body b = this;
        if (this.isNhanban) {
            b = this.clone;
        }
        return b;
    }
/*     */   
/*     */   public byte getBagNull() {
/* 145 */     byte num = 0;
/* 146 */     for (byte i = 0; i < this.ItemBag.length; i = (byte)(i + 1)) {
/* 147 */       if (this.ItemBag[i] == null) {
/* 148 */         num = (byte)(num + 1);
/*     */       }
/*     */     } 
/* 151 */     return num;
/*     */   }
/*     */   
/*     */   public byte getBoxNull() {
/* 155 */     byte num = 0;
/* 156 */     for (byte i = 0; i < this.ItemBox.length; i = (byte)(i + 1)) {
/* 157 */       if (this.ItemBox[i] == null) {
/* 158 */         num = (byte)(num + 1);
/*     */       }
/*     */     } 
/* 161 */     return num;
/*     */   }
/*     */   
/*     */   public Item getIndexBag(int index) {
/* 165 */     if (index < this.ItemBag.length && index >= 0) {
/* 166 */       return this.ItemBag[index];
/*     */     }
/* 168 */     return null;
/*     */   }
/*     */   
/*     */   public Item getIndexBox(int index) {
/* 172 */     if (index < this.ItemBox.length && index >= 0) {
/* 173 */       return this.ItemBox[index];
/*     */     }
/* 175 */     return null;
/*     */   }
/*     */   
/*     */   public int quantityItemyTotal(int id) {
/* 179 */     int quantity = 0;
/* 180 */     for (byte i = 0; i < this.ItemBag.length; i = (byte)(i + 1)) {
/* 181 */       Item item = this.ItemBag[i];
/* 182 */       if (item != null && item.id == id)
/*     */       {
/*     */         
/* 185 */         quantity += item.quantity; } 
/*     */     } 
/* 187 */     return quantity;
/*     */   }
/*     */   
/*     */   protected Item getItemIdBag(int id) {
/* 191 */     for (byte i = 0; i < this.ItemBag.length; ) {
/* 192 */       Item item = this.ItemBag[i];
/* 193 */       if (item == null || item.id != id) {
/*     */         i = (byte)(i + 1); continue;
/*     */       } 
/* 196 */       return item;
/*     */     } 
/* 198 */     return null;
/*     */   }
/*     */   
/*     */   public byte getIndexBagid(int id, boolean lock) {
/* 202 */     for (byte i = 0; i < this.ItemBag.length; ) {
/* 203 */       Item item = this.ItemBag[i];
/* 204 */       if (item == null || item.id != id || item.isLock != lock) {
/*     */         i = (byte)(i + 1); continue;
/*     */       } 
/* 207 */       return i;
/*     */     } 
/* 209 */     return -1;
/*     */   }
/*     */   
/*     */   public byte getIndexBoxid(int id, boolean lock) {
/* 213 */     for (byte i = 0; i < this.ItemBox.length; ) {
/* 214 */       Item item = this.ItemBox[i];
/* 215 */       if (item == null || item.id != id || item.isLock != lock) {
/*     */         i = (byte)(i + 1); continue;
/*     */       } 
/* 218 */       return i;
/*     */     } 
/* 220 */     return -1;
/*     */   }
/*     */   
/*     */   protected byte getIndexBagItem(int id, boolean lock) {
/* 224 */     for (byte i = 0; i < this.ItemBag.length; ) {
/* 225 */       Item item = this.ItemBag[i];
/* 226 */       if (item == null || item.id != id || item.isLock != lock) {
/*     */         i = (byte)(i + 1); continue;
/*     */       } 
/* 229 */       return i;
/*     */     } 
/* 231 */     return -1;
/*     */   }
/*     */   
/*     */   public byte getIndexBagNotItem() {
/* 235 */     for (byte i = 0; i < this.ItemBag.length; i = (byte)(i + 1)) {
/* 236 */       Item item = this.ItemBag[i];
/* 237 */       if (item == null) {
/* 238 */         return i;
/*     */       }
/*     */     } 
/* 241 */     return -1;
/*     */   }
/*     */   
/*     */   protected byte getIndexBoxNotItem() {
/* 245 */     for (byte i = 0; i < this.ItemBox.length; i = (byte)(i + 1)) {
/* 246 */       Item item = this.ItemBox[i];
/* 247 */       if (item == null) {
/* 248 */         return i;
/*     */       }
/*     */     } 
/* 251 */     return -1;
/*     */   }
/*     */   
/*     */   protected void setXPLoadSkill(long exp) throws IOException {
/* 255 */     get().exp = exp;
/* 256 */     Message m = new Message(-30);
/* 257 */     m.writer().writeByte(-124);
/* 258 */     m.writer().writeByte(get().speed);
/* 259 */     m.writer().writeInt(get().getMaxHP());
/* 260 */     m.writer().writeInt(get().getMaxMP());
/* 261 */     m.writer().writeLong(get().exp);
/* 262 */     m.writer().writeShort(get().spoint);
/* 263 */     m.writer().writeShort(get().ppoint);
/* 264 */     m.writer().writeShort(get().potential0);
/* 265 */     m.writer().writeShort(get().potential1);
/* 266 */     m.writer().writeInt(get().potential2);
/* 267 */     m.writer().writeInt(get().potential3);
/* 268 */     m.writer().flush();
/* 269 */     this.p.conn.sendMessage(m);
/* 270 */     m.cleanup();
/*     */   }
/*     */   
/*     */   protected void sortBag() throws IOException {
/*     */     byte i;
/* 275 */     for (i = 0; i < this.ItemBag.length; i = (byte)(i + 1)) {
/* 276 */       if (this.ItemBag[i] != null && !(this.ItemBag[i]).isExpires && (ItemData.ItemDataId((this.ItemBag[i]).id)).isUpToUp) {
/* 277 */         for (byte j = (byte)(i + 1); j < this.ItemBag.length; j = (byte)(j + 1)) {
/* 278 */           if (this.ItemBag[j] != null && !(this.ItemBag[i]).isExpires && (this.ItemBag[j]).id == (this.ItemBag[i]).id && (this.ItemBag[j]).isLock == (this.ItemBag[i]).isLock) {
/* 279 */             (this.ItemBag[i]).quantity += (this.ItemBag[j]).quantity;
/* 280 */             this.ItemBag[j] = null;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 286 */     for (i = 0; i < this.ItemBag.length; i = (byte)(i + 1)) {
/* 287 */       if (this.ItemBag[i] == null) {
/* 288 */         for (byte j = (byte)(i + 1); j < this.ItemBag.length; j = (byte)(j + 1)) {
/* 289 */           if (this.ItemBag[j] != null) {
/* 290 */             this.ItemBag[i] = this.ItemBag[j];
/* 291 */             this.ItemBag[j] = null;
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 297 */     Message m = new Message(-30);
/* 298 */     m.writer().writeByte(-107);
/* 299 */     m.writer().flush();
/* 300 */     this.p.conn.sendMessage(m);
/* 301 */     m.cleanup();
/*     */   }
/*     */   
/*     */   protected void sortBox() throws IOException {
/*     */     byte i;
/* 306 */     for (i = 0; i < this.ItemBox.length; i = (byte)(i + 1)) {
/* 307 */       if (this.ItemBox[i] != null && !(this.ItemBox[i]).isExpires && (ItemData.ItemDataId((this.ItemBox[i]).id)).isUpToUp) {
/* 308 */         for (byte j = (byte)(i + 1); j < this.ItemBox.length; j = (byte)(j + 1)) {
/* 309 */           if (this.ItemBox[j] != null && !(this.ItemBox[i]).isExpires && (this.ItemBox[j]).id == (this.ItemBox[i]).id && (this.ItemBox[j]).isLock == (this.ItemBox[i]).isLock) {
/* 310 */             (this.ItemBox[i]).quantity += (this.ItemBox[j]).quantity;
/* 311 */             this.ItemBox[j] = null;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 317 */     for (i = 0; i < this.ItemBox.length; i = (byte)(i + 1)) {
/* 318 */       if (this.ItemBox[i] == null) {
/* 319 */         for (byte j = (byte)(i + 1); j < this.ItemBox.length; j = (byte)(j + 1)) {
/* 320 */           if (this.ItemBox[j] != null) {
/* 321 */             this.ItemBox[i] = this.ItemBox[j];
/* 322 */             this.ItemBox[j] = null;
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 328 */     Message m = new Message(-30);
/* 329 */     m.writer().writeByte(-106);
/* 330 */     m.writer().flush();
/* 331 */     this.p.conn.sendMessage(m);
/* 332 */     m.cleanup();
/*     */   }
/*     */   
/*     */   public boolean addItemBag(Boolean uptoup, Item itemup) {
/*     */     try {
/* 337 */       byte index = getIndexBagid(itemup.id, itemup.isLock);
/* 338 */       if (uptoup.booleanValue() && !itemup.isExpires && (ItemData.ItemDataId(itemup.id)).isUpToUp && index != -1) {
/* 339 */         (this.ItemBag[index]).quantity += itemup.quantity;
/* 340 */         Message message = new Message(9);
/* 341 */         message.writer().writeByte(index);
/* 342 */         message.writer().writeShort(itemup.quantity);
/* 343 */         message.writer().flush();
/* 344 */         this.p.conn.sendMessage(message);
/* 345 */         message.cleanup();
/* 346 */         return true;
/*     */       } 
/* 348 */       index = getIndexBagNotItem();
/* 349 */       if (index == -1) {
/* 350 */         this.p.conn.sendMessageLog("Hành trang không đủ chỗ trống");
/* 351 */         return false;
/*     */       } 
/* 353 */       this.ItemBag[index] = itemup;
/* 354 */       Message m = new Message(8);
/* 355 */       m.writer().writeByte(index);
/* 356 */       m.writer().writeShort(itemup.id);
/* 357 */       m.writer().writeBoolean(itemup.isLock);
/* 358 */       if (ItemData.isTypeBody(itemup.id) || ItemData.isTypeNgocKham(itemup.id)) {
/* 359 */         m.writer().writeByte(itemup.upgrade);
/*     */       }
/* 361 */       m.writer().writeBoolean(itemup.isExpires);
/* 362 */       m.writer().writeShort(itemup.quantity);
/* 363 */       m.writer().flush();
/* 364 */       this.p.conn.sendMessage(m);
/* 365 */       return true;
/*     */     }
/* 367 */     catch (IOException iOException) {
/*     */       
/* 369 */       return false;
/*     */     } 
/*     */   }
/*     */   public void removeItemBags(int id, int quantity) {
/* 373 */     int num = 0; byte i;
/* 374 */     for (i = 0; i < this.ItemBag.length; i = (byte)(i + 1)) {
/* 375 */       Item item = this.ItemBag[i];
/* 376 */       if (item != null && item.id == id) {
/*     */ 
/*     */         
/* 379 */         if (num + item.quantity >= quantity) {
/* 380 */           removeItemBag(i, quantity - num);
/*     */           break;
/*     */         } 
/* 383 */         num += item.quantity;
/* 384 */         removeItemBag(i, item.quantity);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void removeItemBag(byte index, int quantity) {
/* 390 */     Item item = getIndexBag(index);
/*     */     try {
/* 392 */       item.quantity -= quantity;
/* 393 */       Message m = new Message(18);
/* 394 */       m.writer().writeByte(index);
/* 395 */       m.writer().writeShort(quantity);
/* 396 */       m.writer().flush();
/* 397 */       this.p.conn.sendMessage(m);
/* 398 */       m.cleanup();
/* 399 */       if (item.quantity <= 0) {
/* 400 */         this.ItemBag[index] = null;
/*     */       }
/* 402 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void removeItemBag(byte index) {
/* 407 */     Item item = getIndexBag(index);
/*     */     try {
/* 409 */       Message m = new Message(18);
/* 410 */       m.writer().writeByte(index);
/* 411 */       m.writer().writeShort(item.quantity);
/* 412 */       m.writer().flush();
/* 413 */       this.p.conn.sendMessage(m);
/* 414 */       m.cleanup();
/* 415 */       this.ItemBag[index] = null;
/* 416 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeItemBody(byte index) throws IOException {
/* 421 */     this.ItemBody[index] = null;
/* 422 */     if (index == 10)
/* 423 */       this.p.mobMeMessage(0, (byte)0); 
/* 424 */     Message m = new Message(-30);
/* 425 */     m.writer().writeByte(-80);
/* 426 */     m.writer().writeByte(index);
/* 427 */     m.writer().flush();
/* 428 */     this.p.conn.sendMessage(m);
/* 429 */     m.cleanup();
/*     */   }
/*     */   
/*     */   public void removeItemBox(byte index) throws IOException {
/* 433 */     this.ItemBox[index] = null;
/* 434 */     Message m = new Message(-30);
/* 435 */     m.writer().writeByte(-75);
/* 436 */     m.writer().writeByte(index);
/* 437 */     m.writer().flush();
/* 438 */     this.p.conn.sendMessage(m);
/* 439 */     m.cleanup();
/*     */   }
/*     */   
/*     */   public synchronized int upxu(long x) {
/* 443 */     long xunew = this.xu + x;
/* 444 */     if (xunew > 2000000000L) {
/* 445 */       x = (2000000000 - this.xu);
/* 446 */     } else if (xunew < -2000000000L) {
/* 447 */       x = (-2000000000 - this.xu);
/*     */     } 
/* 449 */     this.xu = (int)(this.xu + x);
/* 450 */     return (int)x;
/*     */   }
/*     */   
/*     */   public synchronized int upyen(long x) {
/* 454 */     long yennew = this.yen + x;
/* 455 */     if (yennew > 2000000000L) {
/* 456 */       x = (2000000000 - this.yen);
/* 457 */     } else if (yennew < -2000000000L) {
/* 458 */       x = (-2000000000 - this.yen);
/*     */     } 
/* 460 */     this.yen = (int)(this.yen + x);
/* 461 */     return (int)x;
/*     */   }
/*     */   
/*     */   public void upxuMessage(long x) {
/*     */     try {
/* 466 */       Message m = new Message(95);
/* 467 */       m.writer().writeInt(upxu(x));
/* 468 */       m.writer().flush();
/* 469 */       this.p.conn.sendMessage(m);
/* 470 */       m.cleanup();
/* 471 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public void upyenMessage(long x) {
/*     */     try {
/* 477 */       Message m = new Message(-8);
/* 478 */       m.writer().writeInt(upyen(x));
/* 479 */       m.writer().flush();
/* 480 */       this.p.conn.sendMessage(m);
/* 481 */       m.cleanup();
/* 482 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   /*     */ 
/*     */   
/*     */   protected static Char setup(Player p, String name) {
/*     */     try {
/* 516 */       synchronized (Server.LOCK_MYSQL) {
/* 517 */         ResultSet red = SQLManager.stat.executeQuery("SELECT * FROM `ninja` WHERE `name`LIKE'" + name + "';");
/* 518 */         if (red != null && red.first()) {
/* 519 */           Char nja = new Char();
/* 520 */           nja.p = p;
/* 521 */           nja.id = red.getInt("id");
/* 522 */           nja.name = red.getString("name");
/* 523 */           nja.gender = red.getByte("gender");
/* 524 */           nja.head = red.getByte("head");
/* 525 */           nja.speed = red.getByte("speed");
/* 526 */           nja.nclass = red.getByte("class");
/* 527 */           nja.ppoint = red.getShort("ppoint");
/* 528 */           nja.potential0 = red.getShort("potential0");
/* 529 */           nja.potential1 = red.getShort("potential1");
/* 530 */           nja.potential2 = red.getInt("potential2");
/* 531 */           nja.potential3 = red.getInt("potential3");
/* 532 */           nja.spoint = red.getShort("spoint");
                    // Bánh kỹ năng
                    nja.maxBPL = red.getByte("maxBPL");
                    nja.maxSKN = red.getByte("maxSKN");
                    nja.maxSTN = red.getByte("maxSTN");
                    nja.maxBBH = red.getByte("maxBBH");
/* 533 */           JSONArray jar = (JSONArray)JSONValue.parse(red.getString("skill"));
/* 534 */           if (jar != null) {
/* 535 */             byte b; for (b = 0; b < jar.size(); b = (byte)(b + 1)) {
/* 536 */               JSONObject job = (JSONObject)jar.get(b);
/* 537 */               Skill skill = new Skill();
/* 538 */               skill.id = Byte.parseByte(job.get("id").toString());
/* 539 */               skill.point = Byte.parseByte(job.get("point").toString());
/* 540 */               nja.skill.add(skill);
/*     */             } 
/*     */           } 
/* 543 */           JSONArray jarr2 = (JSONArray)JSONValue.parse(red.getString("KSkill"));
/* 544 */           nja.KSkill = new byte[jarr2.size()]; byte j;
/* 545 */           for (j = 0; j < nja.KSkill.length; j = (byte)(j + 1)) {
/* 546 */             nja.KSkill[j] = Byte.parseByte(jarr2.get(j).toString());
/*     */           }
/* 548 */           jarr2 = (JSONArray)JSONValue.parse(red.getString("OSkill"));
/* 549 */           nja.OSkill = new byte[jarr2.size()];
/* 550 */           for (j = 0; j < nja.OSkill.length; j = (byte)(j + 1)) {
/* 551 */             nja.OSkill[j] = Byte.parseByte(jarr2.get(j).toString());
/*     */           }
/* 553 */           nja.CSkill = (short)Byte.parseByte(red.getString("CSkill"));
/* 554 */           nja.level = red.getShort("level");
/* 555 */           nja.exp = red.getLong("exp");
/* 556 */           nja.expdown = red.getLong("expdown");
/* 557 */           nja.pk = red.getByte("pk");
/* 558 */           nja.xu = red.getInt("xu");
/* 559 */           nja.xuBox = red.getInt("xuBox");
/* 560 */           nja.yen = red.getInt("yen");
/* 561 */           nja.maxluggage = red.getByte("maxluggage");
/* 562 */           nja.levelBag = red.getByte("levelBag");
/* 563 */           nja.ItemBag = new Item[nja.maxluggage];
/* 564 */           jar = (JSONArray)JSONValue.parse(red.getString("ItemBag"));
/* 565 */           if (jar != null) {
/* 566 */             for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
/* 567 */               JSONObject job = (JSONObject)jar.get(j);
/* 568 */               byte index = Byte.parseByte(job.get("index").toString());
/* 569 */               nja.ItemBag[index] = ItemData.parseItem(jar.get(j).toString());
/*     */             } 
/*     */           }
/* 572 */           nja.ItemBox = new Item[30];
/* 573 */           jar = (JSONArray)JSONValue.parse(red.getString("ItemBox"));
/* 574 */           if (jar != null) {
/* 575 */             for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
/* 576 */               JSONObject job = (JSONObject)jar.get(j);
/* 577 */               byte index = Byte.parseByte(job.get("index").toString());
/* 578 */               nja.ItemBox[index] = ItemData.parseItem(jar.get(j).toString());
/*     */             } 
/*     */           }
/* 581 */           nja.ItemBody = new Item[16];
/* 582 */           jar = (JSONArray)JSONValue.parse(red.getString("ItemBody"));
/* 583 */           if (jar != null) {
/* 584 */             for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
/* 585 */               JSONObject job = (JSONObject)jar.get(j);
/* 586 */               byte index = Byte.parseByte(job.get("index").toString());
/* 587 */               nja.ItemBody[index] = ItemData.parseItem(jar.get(j).toString());
/*     */             } 
/*     */           }
/* 590 */           nja.ItemMounts = new Item[5];
/* 591 */           jar = (JSONArray)JSONValue.parse(red.getString("ItemMounts"));
/* 592 */           if (jar != null) {
/* 593 */             for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
/* 594 */               JSONObject job = (JSONObject)jar.get(j);
/* 595 */               byte index = Byte.parseByte(job.get("index").toString());
/* 596 */               nja.ItemMounts[index] = ItemData.parseItem(jar.get(j).toString());
/*     */             } 
/*     */           }
/* 599 */           nja.friend = red.getString("friend");
/* 600 */           jar = (JSONArray)JSONValue.parse(red.getString("site"));
/* 601 */           nja.mapid = util.UnsignedByte((byte)Integer.parseInt(jar.get(0).toString()));
/* 602 */           nja.x = Short.parseShort(jar.get(1).toString());
/* 603 */           nja.y = Short.parseShort(jar.get(2).toString());
                    nja.mapLTD = Short.parseShort(jar.get(3).toString());
                    nja.mapType = Short.parseShort(jar.get(4).toString());
/* 604 */           jar = (JSONArray)JSONValue.parse(red.getString("effect"));
/* 605 */           for (j = 0; j < jar.size(); j = (byte)(j + 1)) {
/* 606 */             JSONArray jar2 = (JSONArray)jar.get(j);
/* 607 */             int effid = Integer.parseInt(jar2.get(0).toString());
/* 608 */             byte efftype = Byte.parseByte(jar2.get(1).toString());
/* 609 */             long efftime = Long.parseLong(jar2.get(2).toString());
/* 610 */             int param = Integer.parseInt(jar2.get(3).toString());
/*     */ 
/*     */             
/* 613 */             Effect eff = new Effect(effid, param);
/* 614 */             eff.timeStart = 0;
/* 615 */             eff.timeLength = (int)((eff.timeRemove = efftime) - System.currentTimeMillis());
/*     */             
/* 617 */             eff = new Effect(effid, 0, (int)efftime, param);
/*     */             
/* 619 */             nja.veff.add(eff);
/*     */           } 
/* 621 */           jar = (JSONArray)JSONValue.parse(red.getString("clan"));
/* 622 */           if (jar == null || jar.size() != 2) {
/* 623 */             nja.clan = new ClanMember("", nja);
/*     */           } else {
/* 625 */             String clanName = jar.get(0).toString();
/* 626 */             ClanManager clan = ClanManager.getClanName(clanName);
/* 627 */             if (clan == null || clan.getMem(name) == null) {
/* 628 */               nja.clan = new ClanMember("", nja);
/*     */             } else {
/* 630 */               nja.clan = clan.getMem(name);
/* 631 */               nja.clan.nClass = nja.nclass;
/* 632 */               nja.clan.clevel = nja.level;
/*     */             } 
/* 634 */             nja.clan.pointClan = Integer.parseInt(jar.get(1).toString());
/*     */           } 
/* 636 */           nja.denbu = red.getByte("denbu");
/* 637 */           nja.newlogin = util.getDate(red.getString("newlogin"));
/* 638 */           nja.ddClan = red.getBoolean("ddClan");
                    nja.ddLogin = red.getBoolean("ddLogin");
                    nja.caveID = red.getInt("caveID");
                    nja.nCave = red.getInt("nCave");
                    nja.pointCave = red.getInt("pointCave");
                    nja.useCave = red.getInt("useCave");
                    nja.bagCaveMax = red.getInt("bagCaveMax");
                    nja.itemIDCaveMax = red.getShort("itemIDCaveMax");
                    nja.exptype = red.getByte("exptype");
                    nja.pointCT = red.getInt("pointCT");
                    nja.typeCT = red.getInt("typeCT");
                    nja.rewardedCT = red.getByte("rewardedCT");
                    nja.isHuman = true;
                    nja.isNhanban = false;
/* 639 */           return nja;
/*     */         } 
/*     */       } 
/* 642 */     } catch (SQLException|NumberFormatException ex) {
/* 643 */       ex.printStackTrace();
/*     */     } 
/* 645 */     return null;
/*     */   }
/*     */   
/*     */   public void flush() {
/* 649 */     JSONArray jarr = new JSONArray();
/*     */     try {
/* 651 */       synchronized (Server.LOCK_MYSQL) {
/* 652 */         jarr.add(this.mapid);
/* 653 */         jarr.add(this.x);
/* 654 */         jarr.add(this.y);
/* 652 */         jarr.add(this.mapLTD);
/* 652 */         jarr.add(this.mapType);
/* 655 */         String sqlSET = "`taskId`=" + this.taskId + ",`class`=" + this.nclass + ",`ppoint`=" + this.ppoint + ",`potential0`=" + this.potential0 + ",`potential1`=" + this.potential1 + ",`potential2`=" + this.potential2 + ",`potential3`=" + this.potential3 + ",`spoint`=" + this.spoint + ",`level`=" + this.level + ",`exp`=" + this.exp + ",`expdown`=" + this.expdown + ",`pk`=" + this.pk + ",`xu`=" + this.xu + ",`yen`=" + this.yen + ",`maxluggage`=" + this.maxluggage + ",`levelBag`=" + this.levelBag + ",`site`='" + jarr.toJSONString() + "',`friend`='" + this.friend + "'";
/* 656 */         jarr.clear();
/*     */         
/* 658 */         for (Skill skill : this.skill) {
/* 659 */           jarr.add(SkillData.ObjectSkill(skill));
/*     */         }
/* 661 */         sqlSET = sqlSET + ",`skill`='" + jarr.toJSONString() + "'";
/* 662 */         jarr.clear();
/*     */         
/* 664 */         for (byte oid : this.KSkill) {
/* 665 */           jarr.add(oid);
/*     */         }
/* 667 */         sqlSET = sqlSET + ",`KSkill`='" + jarr.toJSONString() + "'";
/* 668 */         jarr.clear();
/*     */         
/* 670 */         for (byte oid : this.OSkill) {
/* 671 */           jarr.add(Byte.valueOf(oid));
/*     */         }
/* 673 */         sqlSET = sqlSET + ",`OSkill`='" + jarr.toJSONString() + "',`CSkill`=" + this.CSkill + "";
/* 674 */         jarr.clear();
/*     */         byte j;
/* 676 */         for (j = 0; j < this.ItemBag.length; j = (byte)(j + 1)) {
/* 677 */           Item item = this.ItemBag[j];
/* 678 */           if (item != null)
/*     */           {
/*     */             
/* 681 */             jarr.add(ItemData.ObjectItem(item, j)); } 
/*     */         } 
/* 683 */         sqlSET = sqlSET + ",`ItemBag`='" + jarr.toJSONString() + "'";
/* 684 */         jarr.clear();
/*     */         
/* 686 */         for (j = 0; j < this.ItemBox.length; j = (byte)(j + 1)) {
/* 687 */           Item item = this.ItemBox[j];
/* 688 */           if (item != null)
/*     */           {
/*     */             
/* 691 */             jarr.add(ItemData.ObjectItem(item, j)); } 
/*     */         } 
/* 693 */         sqlSET = sqlSET + ",`xuBox`=" + this.xuBox + ",`ItemBox`='" + jarr.toJSONString() + "'";
/* 694 */         jarr.clear();
/*     */         
/* 696 */         for (j = 0; j < this.ItemBody.length; j = (byte)(j + 1)) {
/* 697 */           Item item = this.ItemBody[j];
/* 698 */           if (item != null)
/*     */           {
/*     */             
/* 701 */             jarr.add(ItemData.ObjectItem(item, j)); } 
/*     */         } 
/* 703 */         sqlSET = sqlSET + ",`ItemBody`='" + jarr.toJSONString() + "'";
/* 704 */         jarr.clear();
/*     */         
/* 706 */         for (j = 0; j < this.ItemMounts.length; j = (byte)(j + 1)) {
/* 707 */           Item item = this.ItemMounts[j];
/* 708 */           if (item != null)
/*     */           {
/*     */             
/* 711 */             jarr.add(ItemData.ObjectItem(item, j)); } 
/*     */         } 
/* 713 */         sqlSET = sqlSET + ",`ItemMounts`='" + jarr.toJSONString() + "'";
/* 714 */         jarr.clear();
/*     */         byte i;
/* 716 */         for (i = 0; i < this.veff.size(); i = (byte)(i + 1)) {
/* 717 */           if (((Effect)this.veff.get(i)).template.type == 0 || ((Effect)this.veff.get(i)).template.type == 18 || ((Effect)this.veff.get(i)).template.type == 25) {
/* 718 */             JSONArray jarr2 = new JSONArray();
/* 719 */             jarr2.add(((Effect)this.veff.get(i)).template.id);
/* 720 */             if (((Effect)this.veff.get(i)).template.id == 36 || ((Effect)this.veff.get(i)).template.id == 42 || ((Effect)this.veff.get(i)).template.id == 37 || ((Effect)this.veff.get(i)).template.id == 38 || ((Effect)this.veff.get(i)).template.id == 39) {
/* 721 */               jarr2.add(1);
/* 722 */               jarr2.add(((Effect)this.veff.get(i)).timeRemove);
/*     */             } else {
/* 724 */               jarr2.add(0);
/* 725 */               jarr2.add(((Effect)this.veff.get(i)).timeRemove - System.currentTimeMillis());
/*     */             } 
/* 727 */             jarr2.add(((Effect)this.veff.get(i)).param);
/* 728 */             jarr.add(jarr2);
/*     */           } 
/*     */         } 
/* 731 */         sqlSET = sqlSET + ",`effect`='" + jarr.toJSONString() + "'";
/* 732 */         jarr.clear();
/*     */         
/* 734 */         jarr.add(this.clan.clanName);
/* 735 */         jarr.add(this.clan.pointClan);
                  sqlSET = sqlSET + ",`clan`='" + jarr.toJSONString() + "',`denbu`=" + this.denbu + ",`newlogin`='" + util.toDateString(this.newlogin) + "',`ddClan`=" + this.ddClan + ",`ddLogin`=" + this.ddLogin + ",`caveID`="+caveID+",`nCave`="+nCave+",`pointCave`="+pointCave+",`useCave`="+useCave+",`bagCaveMax`="+bagCaveMax+",`itemIDCaveMax`="+itemIDCaveMax+",`exptype`="+exptype+",`maxBPL`="+maxBPL+",`maxSKN`="+maxSKN+",`maxBBH`="+maxBBH+",`maxSTN`="+maxSTN+",`pointCT`=" + this.pointCT + ",`typeCT`=" + this.typeCT + ",`rewardedCT`=" + this.rewardedCT;
/* 737 */         SQLManager.stat.executeUpdate("UPDATE `ninja` SET " + sqlSET + " WHERE `id`=" + this.id + " LIMIT 1;");
/* 738 */         jarr.clear();
/*     */       } 
/* 740 */     } catch (SQLException e) {
/* 741 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void close() {
            if (this.party != null) {
            this.party.exitParty(this);
        }
/* 746 */     if (this.place != null)
/* 747 */       this.place.leave(this.p); 
/*     */   }

            public void changePk(Char c, byte type) throws IOException {
                    c.typepk = type;
                    c.typeCT = type;
                    Message m = new Message(-30);
                    m.writer().writeByte(-92);
                    m.writer().writeInt(c.id);
                    m.writer().writeByte(type);
                    c.place.sendMessage(m);
                    m.cleanup();
                }
/*     */
 }

