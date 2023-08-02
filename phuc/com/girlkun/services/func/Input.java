package com.girlkun.services.func;

import com.girlkun.database.GirlkunDB;
import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Zone;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.network.session.ISession;
import com.girlkun.server.Client;
import com.girlkun.services.Service;
import com.girlkun.services.GiftService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NapThe;
//import com.girlkun.services.NapThe;
import com.girlkun.services.NpcService;
import com.girlkun.services.PlayerService;
import com.girlkun.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class Input {

    public static String LOAI_THE;
    public static String MENH_GIA;
    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int GIVE_IT = 507;
    public static final int CHOOSE_LEVEL_KG = 515;

    public static final int QUY_DOI_COIN = 508;
    public static final int QUY_DOI_HONG_NGOC = 509;

    public static final int TAI = 510;
    public static final int XIU = 511;
    public static final int SEND_ITEM = 512;
    public static final int SEND_ITEM_OP = 513;
    public static final int SEND_ITEM_SKH = 514;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;

    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case GIVE_IT:
                    String name = text[0];
                    int id = Integer.valueOf(text[1]);
                    int q = Integer.valueOf(text[2]);
                    if (Client.gI().getPlayer(name) != null) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        item.quantity = q;
                        InventoryServiceNew.gI().addItemBag(Client.gI().getPlayer(name), item);
                        InventoryServiceNew.gI().sendItemBags(Client.gI().getPlayer(name));
                        Service.gI().sendThongBao(Client.gI().getPlayer(name), "Nhận " + item.template.name + " từ " + player.name);

                    } else {
                        Service.gI().sendThongBao(player, "Không online");
                    }
                    break;

                case CHANGE_PASSWORD:
                    Service.gI().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    GiftService.gI().giftCode(player, text[0]);
                    break;
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban", "Kick"},
                                pl);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            GirlkunDB.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.gI().player(plChanged);
                            Service.gI().Send_Caitrang(plChanged);
                            Service.gI().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.gI().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.gI().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;
                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else {
                            Item theDoiTen = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.gI().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0];
                                GirlkunDB.executeUpdate("update player set name = ? where id = ?", player.name, player.id);
                                Service.gI().player(player);
                                Service.gI().Send_Caitrang(player);
                                Service.gI().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.gI().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;

                case TAI:
                    if (player != null) {
                        int sotvtai = Integer.valueOf(text[0]);
                        if (sotvtai > 5000) {
                            Service.getInstance().sendThongBao(player, "Tối đa 5000 thỏi vàng!!");
                            return;
                        }
                        if (sotvtai <= 0) {
                            Service.getInstance().sendThongBao(player, "Nu Nu ai cho mày bug!!");
                            return;
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                            Service.getInstance().sendThongBao(player, "Ít nhất 2 ô trống trong hành trang!!");
                            return;
                        }
                        Item tv1 = null;
                        for (Item item : player.inventory.itemsBag) {
                            if (item.isNotNullItem() && item.template.id == 457) {
                                tv1 = item;
                                break;
                            }
                        }
                        try {
                            if (tv1 != null && tv1.quantity >= sotvtai) {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, tv1, sotvtai);
                                InventoryServiceNew.gI().sendItemBags(player);
                                int TimeSeconds = 10;
                                Service.getInstance().sendThongBao(player, "Chờ 10 giây để biết kết quả.");
                                while (TimeSeconds > 0) {
                                    TimeSeconds--;
                                    Thread.sleep(1000);
                                }
                                int x = Util.nextInt(1, 6);
                                int y = Util.nextInt(1, 6);
                                int z = Util.nextInt(1, 6);
                                int tong = (x + y + z);
                                if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra là :"
                                                + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : "
                                                + sotvtai + " thỏi vàng vào Tài" + "\nKết quả : Xỉu" + "\nCòn cái nịt.");
                                        return;
                                    }
                                } else if (x == y && x == z) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvtai + " thỏi vàng vào Xỉu" + "\nKết quả : Tam hoa" + "\nCòn cái nịt.");
                                        return;
                                    }
                                } else if ((x + y + z) > 10) {

                                    if (player != null) {
                                        Item tvthang = ItemService.gI().createNewItem((short) 457);
                                        tvthang.quantity = (int) Math.round(sotvtai * 1.8);
                                        InventoryServiceNew.gI().addItemBag(player, tvthang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra : " + x + " "
                                                + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvtai
                                                + " thỏi vàng vào Tài" + "\nKết quả : Tài" + "\n\nVề bờ");
                                        return;
                                    }
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ thỏi vàng để chơi.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Service.getInstance().sendThongBao(player, "Lỗi.");
                        }
                    }
                case XIU:
                    if (player != null) {
                        int sotvxiu = Integer.valueOf(text[0]);
                        if (sotvxiu > 5000) {
                            Service.getInstance().sendThongBao(player, "Tối đa 5000 thỏi vàng!!");
                            return;
                        }
                        if (sotvxiu <= 0) {
                            Service.getInstance().sendThongBao(player, "Nu Nu ai cho mày bug!!");
                            return;
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                            Service.getInstance().sendThongBao(player, "Ít nhất 2 ô trống trong hành trang!!");
                            return;
                        }
                        Item tv2 = null;
                        for (Item item : player.inventory.itemsBag) {
                            if (item.isNotNullItem() && item.template.id == 457) {
                                tv2 = item;
                                break;
                            }
                        }
                        try {
                            if (tv2 != null && tv2.quantity >= sotvxiu) {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, tv2, sotvxiu);
                                InventoryServiceNew.gI().sendItemBags(player);
                                int TimeSeconds = 10;
                                Service.getInstance().sendThongBao(player, "Chờ 10 giây để biết kết quả.");
                                while (TimeSeconds > 0) {
                                    TimeSeconds--;
                                    Thread.sleep(1000);
                                }
                                int x = Util.nextInt(1, 6);
                                int y = Util.nextInt(1, 6);
                                int z = Util.nextInt(1, 6);
                                int tong = (x + y + z);
                                if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                    if (player != null) {
                                        Item tvthang = ItemService.gI().createNewItem((short) 457);
                                        tvthang.quantity = (int) Math.round(sotvxiu * 1.8);
                                        InventoryServiceNew.gI().addItemBag(player, tvthang);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra : " + x + " "
                                                + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvxiu
                                                + " thỏi vàng vào Xỉu" + "\nKết quả : Xỉu" + "\n\nVề bờ");
                                        return;
                                    }
                                } else if (x == y && x == z) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sotvxiu + " thỏi vàng vào Xỉu" + "\nKết quả : Tam hoa" + "\nCòn cái nịt.");
                                        return;
                                    }
                                } else if ((x + y + z) > 10) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra là :"
                                                + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : "
                                                + sotvxiu + " thỏi vàng vào Xỉu" + "\nKết quả : Tài" + "\nCòn cái nịt.");
                                        return;
                                    }
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ thỏi vàng để chơi.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Service.getInstance().sendThongBao(player, "Lỗi.");
                        }
                    }
                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc chắn muốn tới bản đồ kho báu cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                    break;
                case CHOOSE_LEVEL_KG:
                    int levelkg = Integer.parseInt(text[0]);
                    if (levelkg >= 1 && levelkg <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.THAN_VU_TRU, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_KG,
                                    "Con có chắc chắn muốn tới khí gas cấp độ " + levelkg + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, levelkg);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                    break;
                case NAP_THE:

                    NapThe.SendCard(player, LOAI_THE, MENH_GIA, text[0], text[1]);
                    break;
                case QUY_DOI_COIN:
                    int goldTrade = Integer.parseInt(text[0]);
                    int tlquydoi = 5;
                    int coindoi = (goldTrade * 1000) / tlquydoi;
                    if (goldTrade <= 0 || goldTrade >= 500) {
                        Service.gI().sendThongBao(player, "Quá giới hạn mỗi lần chỉ đổi được\n tối đa 500 TV");
                    } else if (player.getSession().coinBar >= coindoi) {
                        PlayerDAO.subcoinBar(player, coindoi);
                        Item thoiVang = ItemService.gI().createNewItem((short) 457, goldTrade);// x3
                        InventoryServiceNew.gI().addItemBag(player, thoiVang);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + goldTrade
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Số tiền của bạn là " + player.getSession().coinBar + " không đủ để quy "
                                + " đổi " + goldTrade + " thỏi vàng " + " " + "bạn cần thêm" + (coindoi - player.getSession().coinBar));
                    }
                    break;
                case QUY_DOI_HONG_NGOC:
                    int RubyTrade = Integer.parseInt(text[0]);
                    if (RubyTrade <= 0 || RubyTrade >= 100000) {
                        Service.gI().sendThongBao(player, "Quá giới hạn mỗi lần chỉ được tối đa 100000");
                    } else if (player.getSession().coinBar >= RubyTrade) {
                        PlayerDAO.subcoinBar(player, RubyTrade);
                        player.inventory.ruby += RubyTrade;
                        PlayerService.gI().sendInfoHpMpMoney(player);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + RubyTrade
                                + " Hồng Ngọc");
                    } else {
                        Service.gI().sendThongBao(player, "Số tiền của bạn là " + player.getSession().coinBar + " không đủ để quy "
                                + " đổi " + RubyTrade + " Hồng Ngọc " + " " + "bạn cần thêm" + (RubyTrade - player.getSession().coinBar));
                    }
                    break;
                case SEND_ITEM:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int quantityItemBuff = Integer.parseInt(text[2]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) quantityItemBuff, Inventory.LIMIT_GOLD);
                                txtBuff += quantityItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + quantityItemBuff, 2000000000);
                                txtBuff += quantityItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + quantityItemBuff, 2000000000);
                                txtBuff += quantityItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.quantity = quantityItemBuff;
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                                txtBuff += "x" + quantityItemBuff + " " + itemBuffTemplate.template.name + "\b";
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(pBuffItem, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case SEND_ITEM_OP:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionBuff = Integer.parseInt(text[2]);
                        int slOptionBuff = Integer.parseInt(text[3]);
                        int slItemBuff = Integer.parseInt(text[4]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, Inventory.LIMIT_GOLD);
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                //Item itemBuffTemplate = ItemBuff.getItem(idItemBuff);
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case SEND_ITEM_SKH:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionSKH = Integer.parseInt(text[2]);
                        int idOptionBuff = Integer.parseInt(text[3]);
                        int slOptionBuff = Integer.parseInt(text[4]);
                        int slItemBuff = Integer.parseInt(text[5]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, Inventory.LIMIT_GOLD);
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionSKH, 0));
                                if (idOptionSKH == 127) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(139, 0));
                                } else if (idOptionSKH == 128) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(140, 0));
                                } else if (idOptionSKH == 129) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(141, 0));
                                } else if (idOptionSKH == 130) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(142, 0));
                                } else if (idOptionSKH == 131) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(143, 0));
                                } else if (idOptionSKH == 132) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(144, 0));
                                } else if (idOptionSKH == 133) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(136, 0));
                                } else if (idOptionSKH == 134) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(137, 0));
                                } else if (idOptionSKH == 135) {
                                    itemBuffTemplate.itemOptions.add(new Item.ItemOption(138, 0));
                                }
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(30, 0));
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;

                    }
                    break;
            }
        } catch (Exception e) {
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Quên Mật Khẩu", new SubInput("Nhập mật khẩu đã quên", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Gift code ", new SubInput("Gift-code", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void TAI(Player pl) {
        createForm(pl, TAI, "Chọn số thỏi vàng đặt tài", new SubInput("Số thỏi vàng", ANY));
    }

    public void XIU(Player pl) {
        createForm(pl, XIU, "Chọn số thỏi vàng đặt tài", new SubInput("Số thỏi vàng", ANY));
    }

    public void createFormNapThe(Player pl, String loaiThe, String menhGia) {
        LOAI_THE = loaiThe;
        MENH_GIA = menhGia;
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Số Seri", ANY), new SubInput("Mã thẻ", ANY));
    }

    public void createFormQDTV(Player pl) {

        createForm(pl, QUY_DOI_COIN, "Quy đổi thỏi vàng, giới hạn đổi không quá 500 "
                + "\n10.000 Vnd = 50 Thỏi vàng "
                + "\n20.000 Vnd = 100 Thỏi vàng "
                + "\n50.000 Vnd = 250 Thỏi vàng "
                + "\n100.000 Vnd = 500 Thỏi vàng "
                + "\n200.000 Vnd = 1000 Thỏi vàng "
                + "\n500.000 Vnd = 2500 Thỏi vàng", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormQDHN(Player pl) {

        createForm(pl, QUY_DOI_HONG_NGOC, "Quy đổi hồng ngọc"
                + "\n10.000 Vnd = 10000 Hồng Ngọc "
                + "\n20.000 Vnd = 20000 Hồng Ngọc "
                + "\n50.000 Vnd = 50000 Hồng Ngọc "
                + "\n100.000 Vnd = 100000 Hồng Ngọc "
                + "\n200.000 Vnd = 200000 Hồng Ngọc "
                + "\n500.000 Vnd = 500000 Hồng Ngọc",
                 new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormChooseLevelKG(Player pl) {
        createForm(pl, CHOOSE_LEVEL_KG, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormSenditem(Player pl) {
        createForm(pl, SEND_ITEM, "SEND ITEM",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID item", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormSenditem1(Player pl) {
        createForm(pl, SEND_ITEM_OP, "SEND Vật Phẩm Option",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public void createFormSenditem2(Player pl) {
        createForm(pl, SEND_ITEM_SKH, "Buff SKH Option V2",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option SKH 127 > 135", NUMERIC),
                new SubInput("ID Option Bonus", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

}
