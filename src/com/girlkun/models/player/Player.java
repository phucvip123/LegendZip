package com.girlkun.models.player;

import BoMong.BoMong;
import com.arriety.card.Card;
import com.girlkun.models.map.MapMaBu.MapMaBu;
import com.girlkun.models.skill.PlayerSkill;

import java.util.List;

import com.girlkun.models.clan.Clan;
import com.girlkun.models.intrinsic.IntrinsicPlayer;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.ItemTime;
import com.girlkun.models.npc.specialnpc.MagicTree;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.npc.specialnpc.MabuEgg;
import com.girlkun.models.mob.MobMe;
import com.girlkun.data.DataGame;
import com.girlkun.models.clan.ClanMember;
import com.girlkun.models.map.TrapMap;
import com.girlkun.models.map.Zone;
import com.arriety.models.map.bdkb.BanDoKhoBauService;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.arriety.models.map.doanhtrai.DoanhTraiService;
import com.arriety.models.map.khigas.KhiGasService;
//import com.arriety.models.map.khigas.KhiGasService;
import com.arriety.models.map.nguhanhson.nguhs;
import com.girlkun.models.matches.IPVP;
import com.girlkun.models.matches.TYPE_LOSE_PVP;
import com.girlkun.models.matches.TYPE_PVP;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.npc.specialnpc.BillEgg;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Manager;
import com.girlkun.services.Service;
import com.girlkun.server.io.MySession;
import com.girlkun.models.task.TaskPlayer;
import com.girlkun.network.io.Message;
import com.girlkun.server.Client;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.FriendAndEnemyService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.TaskService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.ChonAiDay;
import com.girlkun.services.func.CombineNew;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;

public class Player {

    public byte countBDKB;
    public boolean firstJoinBDKB;
    public long lastimeJoinBDKB;
    public int goldChallenge;
    public boolean receivedWoodChest;
    public int levelWoodChest;
    public List<String> textRuongGo = new ArrayList<>();

    public MySession session;

    public boolean beforeDispose;
    public byte role;

    public boolean isPet;
    public boolean isNewPet;
//    public boolean isNewPet1;
    public boolean isBoss;
    public int NguHanhSonPoint = 0;
    public IPVP pvp;
    public int pointPvp;
    public int khigas = 0;
    public byte maxTime = 30;
    public byte type = 0;

    public int mapIdBeforeLogout;
    public List<Zone> mapBlackBall;
    public List<Zone> mapMaBu;

    public Zone zone;
    public Zone mapBeforeCapsule;
    public List<Zone> mapCapsule;
    public Pet pet;
    public NewPet newpet;

    public MobMe mobMe;
    public Location location;
    public SetClothes setClothes;
    public EffectSkill effectSkill;
    public MabuEgg mabuEgg;
    public BillEgg billEgg;
    public TaskPlayer playerTask;
    public ItemTime itemTime;
    public Fusion fusion;
    public MagicTree magicTree;
    public IntrinsicPlayer playerIntrinsic;
    public Inventory inventory;
    public PlayerSkill playerSkill;
    public CombineNew combineNew;
    public IDMark iDMark;
    public Charms charms;
    public EffectSkin effectSkin;
    public Gift gift;
    public NPoint nPoint;
    public RewardBlackBall rewardBlackBall;
    public EffectFlagBag effectFlagBag;
    public FightMabu fightMabu;
    public SkillSpecial skillSpecial;
    
    
    public BoMong achievement;
    public Clan clan;
    public ClanMember clanMember;

    public List<Friend> friends;
    public List<Enemy> enemies;

    public long id;
    public String name;
    public byte gender;
    public boolean isNewMember;
    public short head;

    public boolean banv = false;
    public boolean muav = false;
    public long timeudbv = 0;
    public long timeudmv = 0;
    public long lasttimebanv;
    public long lasttimemuav;

    public byte typePk;

    public byte cFlag;

    public boolean haveTennisSpaceShip;

    public boolean justRevived;
    public long lastTimeRevived;

    public int violate;
    public byte totalPlayerViolate;
    public long timeChangeZone;
    public long lastTimeUseOption;

    public short idNRNM = -1;
    public short idGo = -1;
    public long lastTimePickNRNM;
    public int goldNormar;
    public int goldVIP;
    public long lastTimeWin;
    public boolean isWin;
    public List<Card> Cards = new ArrayList<>();
    public short idAura = -1;

    public byte countKG;
    public boolean firstJoinKG;
    public long lastimeJoinKG;
    public long rankSieuHang;
    public long numKillSieuHang;
    
    public int bdkb_countPerDay;
    public long bdkb_lastTimeJoin;
    public boolean bdkb_isJoinBdkb;
    public boolean lockPK;
    public Timer timerDHVT;
    public Player _friendGiaoDich;

    public Player() {
        lastTimeUseOption = System.currentTimeMillis();
        location = new Location();
        nPoint = new NPoint(this);
        inventory = new Inventory();
        playerSkill = new PlayerSkill(this);
        setClothes = new SetClothes(this);
        effectSkill = new EffectSkill(this);
        fusion = new Fusion(this);
        playerIntrinsic = new IntrinsicPlayer();
        rewardBlackBall = new RewardBlackBall(this);
        effectFlagBag = new EffectFlagBag();
        fightMabu = new FightMabu(this);
        //----------------------------------------------------------------------
        iDMark = new IDMark();
        combineNew = new CombineNew();
        playerTask = new TaskPlayer();
        friends = new ArrayList<>();
        enemies = new ArrayList<>();
        itemTime = new ItemTime(this);
        charms = new Charms();
        gift = new Gift(this);
        effectSkin = new EffectSkin(this);
        skillSpecial = new SkillSpecial(this);
        achievement = new BoMong(this);
//        this.typePk = 5; //trạng thái pk toàn server
    }

    //--------------------------------------------------------------------------
    public boolean isDie() {
        if (this.nPoint != null) {
            return this.nPoint.hp <= 0;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    public void setSession(MySession session) {
        this.session = session;
    }
    public void chat(String text) {
        Service.getInstance().chat(this, text);
    }
    public void chatHp(){
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String hp = decimalFormat.format(this.nPoint.hp);
        hp = hp.replaceAll(",","."); 
        String text = "|1|Hp hiện tại của "+this.name+" là: \n" + "|7|"+hp;  
        this.chat(text);  
    }

    public void sendMessage(Message msg) {
        if (this.session != null) {
            session.sendMessage(msg);
        }
    }

    public MySession getSession() {
        return this.session;
    }

    public boolean isPl() {
        return !isPet && !isBoss && !isNewPet;
    }

    public void update() {
        if (!this.beforeDispose) {
            try {
                if (!iDMark.isBan()) {

                    if (nPoint != null) {
                        nPoint.update();
                    }
                    if (fusion != null) {
                        fusion.update();
                    }
                    if (effectSkin != null) {
                        effectSkill.update();
                    }
                    if (mobMe != null) {
                        mobMe.update();
                    }
                    if (effectSkin != null) {
                        effectSkin.update();
                    }
                    if (pet != null) {
                        pet.update();
                    }
                    if (newpet != null) {
                        newpet.update();
                    }

                    if (magicTree != null) {
                        magicTree.update();
                    }
                    if (itemTime != null) {
                        itemTime.update();
                    }
                    if (itemTime != null) {
                        itemTime.update();
                        if (this.itemTime.isdkhi = false){
        //                    Service.gI().setNotMonkey(this);
        Service.gI().Send_Caitrang(this);
        Service.gI().point(this);
        PlayerService.gI().sendInfoHpMp(this);
        Service.gI().Send_Info_NV(this);
        Service.gI().sendInfoPlayerEatPea(this);
                        }
                    }
                    long now = System.currentTimeMillis();
                    if (banv && this != null && Util.canDoWithTime(lasttimebanv, 1000) && (now >= timeudbv + 1000)) {
                        banv(this);
                        timeudbv = System.currentTimeMillis();
                    }
                    if (muav && this != null && Util.canDoWithTime(lasttimemuav, 2000) && (now >= timeudmv + 10000)) {
                        muav(this);
                        timeudmv = System.currentTimeMillis();
                    }
                    KhiGasService.gI().update(this);
                    nguhs.gI().update(this);
                    BlackBallWar.gI().update(this);
                    MapMaBu.gI().update(this);
                    BanDoKhoBauService.gI().updatePlayer(this);
                    DoanhTraiService.gI().updatePlayer(this);
                    if (!isBoss && this.iDMark.isGotoFuture() && Util.canDoWithTime(this.iDMark.getLastTimeGoToFuture(), 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                        this.iDMark.setGotoFuture(false);
                    }
                    if (this.iDMark.isGoToBDKB() && Util.canDoWithTime(this.iDMark.getLastTimeGoToBDKB(), 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 135, -1, 35);
                        this.iDMark.setGoToBDKB(false);
                    }
                    if (this.zone != null) {
                        TrapMap trap = this.zone.isInTrap(this);
                        if (trap != null) {
                            trap.doPlayer(this);
                        }
                    }
                    if (this.isPl() && this.inventory.itemsBody.get(7) != null) {
                        Item it = this.inventory.itemsBody.get(7);
                        if (it != null && it.isNotNullItem() && this.newpet == null) {
                            PetService.Pet2(this, it.template.head, it.template.body, it.template.leg, it.template.name);
                            Service.getInstance().point(this);
                        }
                    } else if (this.isPl() && newpet != null && !this.inventory.itemsBody.get(7).isNotNullItem()) {
                        newpet.dispose();
                        newpet = null;
                    }
                    if (this.isPl() && isWin && this.zone.map.mapId == 51 && Util.canDoWithTime(lastTimeWin, 2000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 52, 0, -1);
                        isWin = false;
                    }
                    if (location.lastTimeplayerMove < System.currentTimeMillis() - 30 * 60 * 1000) {
                        Client.gI().kickSession(getSession());
                    }
                } else {
                    if (Util.canDoWithTime(iDMark.getLastTimeBan(), 5000)) {
                        Client.gI().kickSession(session);
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
                Logger.logException(Player.class, e, "Lỗi tại player: " + this.name);
            }
        }
    }

    //--------------------------------------------------------------------------
    /*
     * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất
     * {383, 384, 385}: ht porata xayda trái đất
     * {391, 392, 393}: ht namếc
     * {870, 871, 872}: ht c2 trái đất
     * {873, 874, 875}: ht c2 namếc
     * {867, 878, 869}: ht c2 xayda
     * {2033,2034,2035}: ht c3 td
     * {2030,2031,2032}: ht c3 nm   
     * {2027,2028,2029}: ht c3 xd*/
    private static final short[][] idOutfitFusion = {
        {380, 381, 382}, {383, 384, 385}, {391, 392, 393},
        {870, 871, 872}, {873, 874, 875}, {867, 868, 869},
        {2048, 2049, 2050}, {2051, 2052, 2053}, {2063, 2064, 2065},
        {2057, 2058, 2059}, {2060, 2061, 2062}, {2054, 2055, 2056},};

    public byte getEffFront() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        int levelAo = 0;
        Item.ItemOption optionLevelAo = null;
        int levelQuan = 0;
        Item.ItemOption optionLevelQuan = null;
        int levelGang = 0;
        Item.ItemOption optionLevelGang = null;
        int levelGiay = 0;
        Item.ItemOption optionLevelGiay = null;
        int levelNhan = 0;
        Item.ItemOption optionLevelNhan = null;
        Item itemAo = this.inventory.itemsBody.get(0);
        Item itemQuan = this.inventory.itemsBody.get(1);
        Item itemGang = this.inventory.itemsBody.get(2);
        Item itemGiay = this.inventory.itemsBody.get(3);
        Item itemNhan = this.inventory.itemsBody.get(4);
        for (Item.ItemOption io : itemAo.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelAo = io.param;
                optionLevelAo = io;
                break;
            }
        }
        for (Item.ItemOption io : itemQuan.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelQuan = io.param;
                optionLevelQuan = io;
                break;
            }
        }
        for (Item.ItemOption io : itemGang.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelGang = io.param;
                optionLevelGang = io;
                break;
            }
        }
        for (Item.ItemOption io : itemGiay.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelGiay = io.param;
                optionLevelGiay = io;
                break;
            }
        }
        for (Item.ItemOption io : itemNhan.itemOptions) {
            if (io.optionTemplate.id == 72) {
                levelNhan = io.param;
                optionLevelNhan = io;
                break;
            }
        }
        if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 8 && levelQuan >= 8 && levelGang >= 8 && levelGiay >= 8 && levelNhan >= 8) {
            return 8;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 7 && levelQuan >= 7 && levelGang >= 7 && levelGiay >= 7 && levelNhan >= 7) {
            return 7;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 6 && levelQuan >= 6 && levelGang >= 6 && levelGiay >= 6 && levelNhan >= 6) {
            return 6;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 5 && levelQuan >= 5 && levelGang >= 5 && levelGiay >= 5 && levelNhan >= 5) {
            return 5;
        } else if (optionLevelAo != null && optionLevelQuan != null && optionLevelGang != null && optionLevelGiay != null && optionLevelNhan != null
                && levelAo >= 4 && levelQuan >= 4 && levelGang >= 4 && levelGiay >= 4 && levelNhan >= 4) {
            return 4;
        } else {
            return -1;
        }
    }

    public short getHead() {
        if (nPoint.IsBiHoaDa) {
            return 454;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        }  if (effectSkill != null && itemTime.isdkhi) {
            return (short) 1437;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else {
            if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][0];
//                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][0];
//                }
                return idOutfitFusion[3 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][0];
//                }
                return idOutfitFusion[6 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][0];
//                }
                return idOutfitFusion[9 + this.gender][0];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int head = inventory.itemsBody.get(5).template.head;
            if (head != -1) {
                return (short) head;
            }
        }
        }
        return this.head;
    }

    public short getBody() {
        if (nPoint.IsBiHoaDa) {
            return 455;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        } if (effectSkill != null && itemTime.isdkhi) {
            return (short) 1438;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else {
            if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][1];
//                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][1];
//                }
                return idOutfitFusion[3 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][1];
//                }
                return idOutfitFusion[6 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][1];
//                }
                return idOutfitFusion[9 + this.gender][1];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int body = inventory.itemsBody.get(5).template.body;
            if (body != -1) {
                return (short) body;
            }
        }
        }
        if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.get(0).template.part;
        }
        return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
    }

    public short getLeg() {
        if (nPoint.IsBiHoaDa) {
            return 456;
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        } if (effectSkill != null && itemTime.isdkhi) {
            return (short) 1439;
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else {
            if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][2];
//                }
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][2];
//                }
                return idOutfitFusion[3 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][2];
//                }
                return idOutfitFusion[6 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
//                if (this.pet.typePet == 1) {
//                    return idOutfitFusion[3 + this.gender][2];
//                }
                return idOutfitFusion[9 + this.gender][2];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int leg = inventory.itemsBody.get(5).template.leg;
            if (leg != -1) {
                return (short) leg;
            }
        }
        }
        if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }
        return (short) (gender == 1 ? 60 : 58);
    }

    public byte getAura() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item item = this.inventory.itemsBody.get(5);
        if (!item.isNotNullItem()) {
            return 0;
        }
        if (item.template.id == 1281) {
            return 27;
        } else if (item.template.id == 1282) {
            return 26;
        } else if (item.template.id == 1284) {
            return 25;
        } else if (item.template.id == 1290) {
            return 24;
        }  else if (item.template.id == 1291) {
            return 23;
        } else if (item.template.id == 1295) {
            return 22;
        } else if (item.template.id == 1296) {
            return 21;
        } else if (item.template.id == 1297) {
            return 20;
        } else if (item.template.id == 1298) {
            return 17;
        } else if (item.template.id == 1300) {
            return 16;
        } else if (item.template.id == 1301) {
            return 15;
        } else if (item.template.id == 1302) {
            return 14;
        } else if (item.template.id == 1306) {
            return 13;
        }else if (item.template.id == 1308) {
            return 12;
        }else if (item.template.id == 1309) {
            return 11;
        }else if (item.template.id == 1310) {
            return 10;
        } else {
            return -1;
        }

    }

    public short getFlagBag() {
        if (this.iDMark.isHoldBlackBall()) {
            return 31;
        } else if (this.idNRNM >= 353 && this.idNRNM <= 359) {
            return 30;
        }
        if (this.inventory.itemsBody.size() == 11) {
            if (this.inventory.itemsBody.get(8).isNotNullItem()) {
                return this.inventory.itemsBody.get(8).template.part;
            }
        }
        if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
            return 28;
        }
        if (this.clan != null) {
            return (short) this.clan.imgId;
        }
        return -1;
    }

    public short getMount() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item item = this.inventory.itemsBody.get(9);
        if (!item.isNotNullItem()) {
            return -1;
        }
        if (item.template.type == 24) {
            if (item.template.gender == 3 || item.template.gender == this.gender) {
                return item.template.id;
            } else {
                return -1;
            }
        } else {
            if (item.template.id < 500) {
                return item.template.id;
            } else {
                return (short) DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
            }
        }

    }

    //--------------------------------------------------------------------------
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
       
        
        if (!this.isDie()) {
            if (this.pet != null && this.pet.status == Pet.PROTECT) {
                this.pet.playerAttack = plAtt;
            }
            boolean isSkillChuong = false;
            if (plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        if (this.nPoint.voHieuChuong > 0) {
                            com.girlkun.services.PlayerService.gI().hoiPhuc(this, 0, damage * this.nPoint.voHieuChuong / 100);
                            return 0;
                        }
                        isSkillChuong = true;
                }
            }
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 100)) {
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                damage = this.nPoint.hp - 1;
            }
            if (plAtt != null) {
                if (isSkillChuong && plAtt.nPoint.multicationChuong > 0 && Util.canDoWithTime(plAtt.nPoint.lastTimeMultiChuong, PlayerSkill.TIME_MUTIL_CHUONG)) {
                    damage *= plAtt.nPoint.multicationChuong;
                    plAtt.nPoint.lastTimeMultiChuong = System.currentTimeMillis();
                }
            }

            this.nPoint.subHP(damage);
            if (isDie()) {
                if (this.zone.map.mapId == 112) {
                    plAtt.pointPvp++;
                }
                setDie(plAtt);
            }
           if(this.nPoint.hp > Integer.MAX_VALUE) this.chatHp();
            return damage;
        } else {
            return 0;
        }
    }

    protected void setDie(Player plAtt) {
        //xóa phù
        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.gI().point(this);
        }
        //xóa tụ skill đặc biệt
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        //xóa hiệu ứng skill
        this.effectSkill.removeSkillEffectWhenDie();
        //
        nPoint.setHp(0);
        nPoint.setMp(0);
        //xóa trứng
        if (this.mobMe != null) {
            this.mobMe.mobMeDie();
        }
        Service.gI().charDie(this);
        //add kẻ thù
        if (!this.isPet && !this.isNewPet && !this.isBoss && plAtt != null && !plAtt.isPet && !plAtt.isNewPet && !plAtt.isBoss) {
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
            }
}
         if (this.isPl() && plAtt != null && plAtt.isPl()) {
                plAtt.achievement.plusCount(3);
            }
        //kết thúc pk
        if (this.pvp != null) {
            this.pvp.lose(this, TYPE_LOSE_PVP.DEAD);
        }
//        PVPServcice.gI().finishPVP(this, PVP.TYPE_DIE);
        BlackBallWar.gI().dropBlackBall(this);
    }

    //--------------------------------------------------------------------------
    public void setClanMember() {
        if (this.clanMember != null) {
            this.clanMember.powerPoint = this.nPoint.power;
            this.clanMember.head = this.getHead();
            this.clanMember.body = this.getBody();
            this.clanMember.leg = this.getLeg();
        }
    }

    public boolean isAdmin() {
        return this.session.isAdmin;
    }

    public void setJustRevivaled() {
        this.justRevived = true;
        this.lastTimeRevived = System.currentTimeMillis();
    }

    public void preparedToDispose() {

    }

    public void dispose() {
        if (pet != null) {
            pet.dispose();
            pet = null;
        }
        if (newpet != null) {
            newpet.dispose();
            newpet = null;
        }

        if (mapBlackBall != null) {
            mapBlackBall.clear();
            mapBlackBall = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapMaBu != null) {
            mapMaBu.clear();
            mapMaBu = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapCapsule != null) {
            mapCapsule.clear();
            mapCapsule = null;
        }
        if (mobMe != null) {
            mobMe.dispose();
            mobMe = null;
        }
        location = null;
        if (setClothes != null) {
            setClothes.dispose();
            setClothes = null;
        }
        if (effectSkill != null) {
            effectSkill.dispose();
            effectSkill = null;
        }
        if (mabuEgg != null) {
            mabuEgg.dispose();
            mabuEgg = null;
        }
        if (billEgg != null) {
            billEgg.dispose();
            billEgg = null;
        }
        if (playerTask != null) {
            playerTask.dispose();
            playerTask = null;
        }
        if (itemTime != null) {
            itemTime.dispose();
            itemTime = null;
        }
        if (fusion != null) {
            fusion.dispose();
            fusion = null;
        }
        if (magicTree != null) {
            magicTree.dispose();
            magicTree = null;
        }
        if (playerIntrinsic != null) {
            playerIntrinsic.dispose();
            playerIntrinsic = null;
        }
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (playerSkill != null) {
            playerSkill.dispose();
            playerSkill = null;
        }
        if (combineNew != null) {
            combineNew.dispose();
            combineNew = null;
        }
        if (iDMark != null) {
            iDMark.dispose();
            iDMark = null;
        }
        if (charms != null) {
            charms.dispose();
            charms = null;
        }
        if (effectSkin != null) {
            effectSkin.dispose();
            effectSkin = null;
        }
        if (gift != null) {
            gift.dispose();
            gift = null;
        }
        if (nPoint != null) {
            nPoint.dispose();
            nPoint = null;
        }
        if (rewardBlackBall != null) {
            rewardBlackBall.dispose();
            rewardBlackBall = null;
        }
        if (effectFlagBag != null) {
            effectFlagBag.dispose();
            effectFlagBag = null;
        }
        if (pvp != null) {
            pvp.dispose();
            pvp = null;
        }
        effectFlagBag = null;
        clan = null;
        clanMember = null;
        friends = null;
        enemies = null;
        session = null;
        name = null;
    }

    public void banv(Player player) {
        try {
            if (this.banv && player.inventory.gold <= 1000000000 && player != null) {
                Item tv = null;
                for (Item item : player.inventory.itemsBag) {
                    if (item.isNotNullItem() && item.template.id == 457) {
                        tv = item;
                        break;
                    }
                }
                if (tv != null) {
                    if (player.inventory.gold <= 9_000_000_000_000_000_000L) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, tv, 1);
                        player.inventory.gold += 500000000;
                        lasttimebanv = System.currentTimeMillis();
                        PlayerService.gI().sendInfoHpMpMoney(player);
                        InventoryServiceNew.gI().sendItemBags(player);
                    } else {
                        Service.getInstance().sendThongBao(player, "không được vượt quá giới hạn vàng");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "hết thỏi vàng rồi, đã tắt lệnh bán vàng");
                    this.banv = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void muav(Player player) {
        try {
            if (this.muav && player != null) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 1) {
                    if (player.inventory.gold >= 500000000) {
                        player.inventory.gold -= 500000000;
                        Item tv = ItemService.gI().createNewItem((short) 457);
                        InventoryServiceNew.gI().addItemBag(player, tv);
                        lasttimemuav = System.currentTimeMillis();
                        PlayerService.gI().sendInfoHpMpMoney(player);
                        InventoryServiceNew.gI().sendItemBags(player);
                    }
                } else {
                    this.muav = false;
                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang, đã tắt tự mua tv");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String percentGold(int type) {
        try {
            if (type == 0) {
                double percent = ((double) this.goldNormar / ChonAiDay.gI().goldNormar) * 100;
                return String.valueOf(Math.ceil(percent));
            } else if (type == 1) {
                double percent = ((double) this.goldVIP / ChonAiDay.gI().goldVip) * 100;
                return String.valueOf(Math.ceil(percent));
            }
        } catch (ArithmeticException e) {
            return "0";
        }
        return "0";
    }
}
