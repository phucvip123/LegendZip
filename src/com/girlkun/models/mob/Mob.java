package com.girlkun.models.mob;

import com.girlkun.consts.ConstMap;
import com.girlkun.consts.ConstMob;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;

import java.util.List;

import com.girlkun.models.map.Zone;
import com.arriety.models.map.bdkb.BanDoKhoBau;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.player.Location;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.reward.ItemMobReward;
import com.girlkun.models.reward.MobReward;
import com.girlkun.models.skill.PlayerSkill;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.server.ServerManager;
import com.girlkun.services.*;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.io.IOException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;

    public boolean isMobMe;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100;
    }

    private long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public boolean isSieuQuai() {
        return this.lvMob > 0;
    }
    public boolean isQuaiBay() {
        if((this.id>=7 && this.id <=12) || this.id == 21 || this.id == 25 || (this.id>=28 && this.id<=33)
            || this.id == 37 || this.id == 43 || this.id == 49 || this.id == 50 || this.id == 69 || this.id == 75 
            || this.id == 79  ){
            return true;
        }
        return false;
    }
   

    public synchronized void injured(Player plAtt, double damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (this.zone.map.mapId == 122) {
                plAtt.NguHanhSonPoint++;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            if (plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        if (plAtt.nPoint.multicationChuong > 0 && Util.canDoWithTime(plAtt.nPoint.lastTimeMultiChuong, PlayerSkill.TIME_MUTIL_CHUONG)) {
                            damage *= plAtt.nPoint.multicationChuong;
                            plAtt.nPoint.lastTimeMultiChuong = System.currentTimeMillis();
                        }

                }
            }
            this.point.hp -= damage;

            if (this.isDie()) {
                this.lvMob = 0;
                this.status = 0;
                this.sendMobDieAffterAttacked(plAtt, damage);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                this.lastTimeDie = System.currentTimeMillis();
                if (this.id == 13) {
                    this.zone.isbulon13Alive = false;
                }
                if (this.id == 14) {
                    this.zone.isbulon14Alive = false;
                }
                if (this.isSieuQuai()) {
                plAtt.achievement.plusCount(12);
                }
                if (this.isQuaiBay()) {
                plAtt.achievement.plusCount(6);
                }
            } else {
                this.sendMobStillAliveAffterAttacked(damage, plAtt != null ? plAtt.nPoint.isCrit : false);
            }
            if (plAtt != null) {
                Service.gI().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }
        }
    }

    public long getTiemNangForPlayer(Player pl, double dame) {
        int levelPlayer = Service.gI().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        double pDameHit = dame * 100 / point.getHpFull();
        long tiemNang = Util.maxLong(pDameHit * maxTiemNang / 100);
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = (int) pl.nPoint.calSucManhTiemNang(tiemNang);
        if (pl.zone.map.mapId == 122 || pl.zone.map.mapId == 123 || pl.zone.map.mapId == 124 || pl.zone.map.mapId == 135 || pl.zone.map.mapId == 136 || pl.zone.map.mapId == 137 || pl.zone.map.mapId == 138 ) {
            tiemNang *= 5;
        }
        return tiemNang;
    }

    public void update() {

        if (this.isDie() && !Maintenance.isRuning) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    if (this.zone.isTrungUyTrangAlive && this.tempId == 22 && this.zone.map.mapId == 59) {
                        if (Util.canDoWithTime(lastTimeDie, 5000)) {
                            if (this.id == 13) {
                                this.zone.isbulon13Alive = true;
                            }
                            if (this.id == 14) {
                                this.zone.isbulon14Alive = true;
                            }
                            this.hoiSinh();
                            this.sendMobHoiSinh();
                        }

                    }
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    if (this.tempId == 72 || this.tempId == 71) {//ro bot bao ve
                        if (System.currentTimeMillis() - this.lastTimeDie > 3000) {
                            try {
                                Message t = new Message(102);
                                t.writer().writeByte((this.tempId == 71 ? 7 : 6));
                                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                                t.cleanup();
                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                case ConstMap.MAP_KHI_GAS:
                    break;
                default:
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.randomSieuQuai();
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
            }
        }
        effectSkill.update();
        attackPlayer();
    }

    private void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0)) {

            if ((this.tempId == 72 || this.tempId == 71) && Util.canDoWithTime(lastTimeAttackPlayer, 300)) {
                List<Player> pl = getListPlayerCanAttack();
                if (!pl.isEmpty()) {
                    this.sendMobBossBdkbAttack(pl, this.point.getDameAttack());
                } else {
                    if (this.tempId == 71) {
                        Player plA = getPlayerCanAttack();
                        if (plA != null) {
                            try {
                                Message t = new Message(102);
                                t.writer().writeByte(5);
                                t.writer().writeByte(plA.location.x);
                                this.location.x = plA.location.x;
                                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                                t.cleanup();
                            } catch (IOException e) {
                            }
                        }

                    }
                }
                this.lastTimeAttackPlayer = System.currentTimeMillis();
            } else if (Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
                Player pl = getPlayerCanAttack();
                if (pl != null) {
                    this.mobAttackPlayer(pl);
                }
                this.lastTimeAttackPlayer = System.currentTimeMillis();
            }

        }
    }

    private void sendMobBossBdkbAttack(List<Player> players, long dame) {
        if (this.tempId == 72) {
            try {
                Message t = new Message(102);
                int action = Util.nextInt(0, 2);
                t.writer().writeByte(action);
                if (action != 1) {
                    this.location.x = players.get(Util.nextInt(0, players.size() - 1)).location.x;
                }
                t.writer().writeByte(players.size());
                for (Byte i = 0; i < players.size(); i++) {
                    t.writer().writeInt((int) players.get(i).id);
                    t.writer().writeInt((int) players.get(i).injured(null, (int) dame, false, true));
                }
                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                t.cleanup();
            } catch (IOException e) {
            }
        } else if (this.tempId == 71) {
            try {
                Message t = new Message(102);
                t.writer().writeByte(Util.getOne(3, 4));
                t.writer().writeByte(players.size());
                for (Byte i = 0; i < players.size(); i++) {
                    t.writer().writeInt((int) players.get(i).id);
                    t.writer().writeInt((int) players.get(i).injured(null, (int) dame, false, true));
                }
                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                t.cleanup();
            } catch (IOException e) {
            }
        }
    }

    private List<Player> getListPlayerCanAttack() {
        List<Player> plAttack = new ArrayList<>();
        int distance = (this.tempId == 71 ? 250 : 600);
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack.add(pl);
                    }
                }
            }
        } catch (Exception e) {
        }

        return plAttack;
    }

    private Player getPlayerCanAttack() {
        int distance = 100;
        Player plAttack = null;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isNewPet) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
        } catch (Exception e) {

        }
        return plAttack;
    }

    //**************************************************************************
    private void mobAttackPlayer(Player player) {
        double dameMob = this.point.getDameAttack();
        if (player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (this.isSieuQuai()) {
            dameMob = player.nPoint.hpMax / 10;
        }
        double dame = player.injured(null, dameMob, false, true);
        this.sendMobAttackMe(player, dame);
        this.sendMobAttackPlayer(player);
    }

    private void sendMobAttackMe(Player player, double dame) {
        if (!player.isPet && !player.isNewPet) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(Util.maxInt(dame)); //dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(Util.maxInt(player.nPoint.hp));
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void randomSieuQuai() {
        if (this.tempId != 0 && MapService.gI().isMapKhongCoSieuQuai(this.zone.map.mapId) && Util.nextInt(0, 150) < 1) {
            this.lvMob = 1;
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    private void sendMobHoiSinh() {
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob);
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //**************************************************************************
    private void sendMobDieAffterAttacked(Player plKill, double dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(Util.maxInt(dameHit));
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
        }
    }

    public void sendMobDieAfterMobMeAttacked(Player plKill, int dameHit) {
        this.status = 0;
        Message msg;
        try {
            if (this.id == 13) {
                this.zone.isbulon13Alive = false;
            }
            if (this.id == 14) {
                this.zone.isbulon14Alive = false;
            }
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(false); // crit

            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (IOException e) {
            Logger.logException(Mob.class, e);
        }
//        if (plKill.isPl()) {
//            if (TaskService.gI().IsTaskDoWithMemClan(plKill.playerTask.taskMain.id)) {
//                TaskService.gI().checkDoneTaskKillMob(plKill, this, true);
//            } else {
//                TaskService.gI().checkDoneTaskKillMob(plKill, this, false);
//            }
//
//        }
        this.lastTimeDie = System.currentTimeMillis();
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet && !player.isNewPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(player, item.itemMapId, true);
                    }
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                    }
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
//        nplayer
        List<ItemMap> itemReward = new ArrayList<>();
        try {
           if ((!player.isPet && player.getSession().actived && player.setClothes.setDHD == 5) || (player.isPet && ((Pet) player).master.getSession().actived && ((Pet) player).setClothes.setDHD == 5)) {
               byte random = 1;
               if (Util.isTrue(5, 100)) {
                   random = 2;
               }
               Item i = new Item((short) 861);
               //Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
               i.quantity = random;
               InventoryServiceNew.gI().addItemBag(player, i);
               InventoryServiceNew.gI().sendItemBags(player);
               Service.gI().sendThongBao(player, "Bạn vừa nhận được " + random + " hồng ngọc");
           }
            if (Util.isTrue(60.0f, 100)) {
                if (player.setClothes.godClothes && MapService.gI().isMapCold(player.zone.map)) {
                    ArrietyDrop.DropItemReWard(player, ArrietyDrop.list_thuc_an[Util.nextInt(0, (ArrietyDrop.list_thuc_an.length - 1))], 1, this.location.x, this.location.y);
                }
            } else {
                //Service.getInstance().sendThongBao(player, "Con quái này không có rồi!");
            }
                    //    if (Util.isTrue(1.0f, 100)) {
                    //        if (player.setClothes.thienSuClothers && MapService.gI().isMapCold(player.zone.map)) {
                    //            ArrietyDrop.DropItemReWardDoHuyDietKichHoat(player, 1, this.location.x, this.location.y);
                    //        }
                    //    } 

            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); //sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemReward;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        MobReward mobReward = Manager.MOB_REWARDS.get(this.tempId);
        if (mobReward == null) {
            return list;
        }final Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(11);    
                   
        List<ItemMobReward> items = mobReward.getItemReward();
        List<ItemMobReward> golds = mobReward.getGoldReward();
        if (!items.isEmpty()) {
            ItemMobReward item = items.get(Util.nextInt(0, items.size() - 1));
            ItemMap itemMap = item.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (!golds.isEmpty()) {
            ItemMobReward gold = golds.get(Util.nextInt(0, golds.size() - 1));
            ItemMap itemMap = gold.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (player.itemTime.isUseMayDo && Util.isTrue(21, 100) && this.tempId > 57 && this.tempId < 66) {
            list.add(new ItemMap(zone, 380, 1, x, player.location.y, player.id));
        }// vat phẩm rơi khi user maaáy dò adu hoa r o day ti code choa
        if (player.itemTime.isUseMayDo2 && Util.isTrue(1, 100) && this.tempId > 1 && this.tempId < 81) {
            list.add(new ItemMap(zone, 2036, 1, x, player.location.y, player.id));// cai nay sua sau nha
        } if (player.cFlag>=1 && Util.isTrue(100, 100) && this.tempId == 0 && hour!=1 && hour!=3 && hour!=5 && hour!=7 && hour!=9 && hour!=11 && hour!=13 && hour!=15 && hour!=17 && hour!=19 && hour!=21 && hour!=23) {    //up bí kíp
            list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));// cai nay sua sau nha
            if (Util.isTrue(50, 100) && this.tempId == 0) {    //up bí kíp
            list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));
            if (Util.isTrue(50, 100) && this.tempId == 0) {    //up bí kíp
            list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));
            if (Util.isTrue(50, 100) && this.tempId == 0) {    //up bí kíp
            list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));}}}
        }if (this.tempId>0 && this.zone.map.mapId>=156 && this.zone.map.mapId<=159 && player.fusion.typeFusion==ConstPlayer.HOP_THE_PORATA2){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, 2076, 1, x, player.location.y, player.id));}}
        if (this.tempId>0 && this.zone.map.mapId>=156 && this.zone.map.mapId<=159){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, 933, 1, x, player.location.y, player.id));}}
        if (this.tempId>0 && this.zone.map.mapId>=156 && this.zone.map.mapId<=159){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, 934, 1, x, player.location.y, player.id));}}
        if (this.tempId>0 && this.zone.map.mapId>=156 && this.zone.map.mapId<=159 && player.fusion.typeFusion==ConstPlayer.HOP_THE_PORATA3){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, 2077, 1, x, player.location.y, player.id));}}
        if (this.tempId>0 && this.zone.map.mapId>=156 && this.zone.map.mapId<=159 && player.fusion.typeFusion==ConstPlayer.HOP_THE_PORATA3){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, 2036, 1, x, player.location.y, player.id));}}
        if (this.tempId>0 && this.zone.map.mapId>=156 && this.zone.map.mapId<=159 && player.fusion.typeFusion==ConstPlayer.HOP_THE_PORATA4){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, 2036, 1, x, player.location.y, player.id));}}
        if (player.setClothes.setGod() && this.zone.map.mapId>=105 && this.zone.map.mapId<=111){
        if (Util.isTrue(10, 100)&&player.setClothes.setGod14()) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(663,667), 1, x, player.location.y, player.id));}
        }
        if (player.setClothes.setGod14() && this.zone.map.mapId== 155){
        if (Util.isTrue(5, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(1066,1070), 1, x, player.location.y, player.id));}
        }
        Item item = player.inventory.itemsBody.get(1);
        if (this.zone.map.mapId > 0){
            if(item.isNotNullItem()){
            if (item.template.id == 691){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(695,698), 1, x, player.location.y, player.id));}
        }else if (item.template.id != 691 && item.template.id != 692 && item.template.id != 693){
            if (Util.isTrue(0, 1))
            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
        }
        }
        }if (this.zone.map.mapId >= 0){
            if(item.isNotNullItem()){
            if (item.template.id == 692){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(695,698), 1, x, player.location.y, player.id));}
        }else if (item.template.id != 691 && item.template.id != 692 && item.template.id != 693){
            if (Util.isTrue(0, 1))
            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
        }
        }
        }if (this.zone.map.mapId > 0){
            if(item.isNotNullItem()){
            if (item.template.id == 693){
        if (Util.isTrue(10, 100)) {    //up bí kíp
            list.add(new ItemMap(zone, Util.nextInt(695,698), 1, x, player.location.y, player.id));}
        }else if (item.template.id != 691 && item.template.id != 692 && item.template.id != 693){
            if (Util.isTrue(0, 1))
            list.add(new ItemMap(zone, 76, 1, x, player.location.y, player.id));
        }
        }
        }
        //up hòm tiếp tế
        // if (this.zone.map.mapId == 250){
        //     if (Util.isTrue(15, 100)){
        //     list.add(new ItemMap(zone, 2000+player.gender, 1, x, player.location.y, player.id));
        // }}
        //up hồng ngọc + thỏi vàng map gohan nhật nguyệt
        if(this.zone.map.mapId == 250 || this.zone.map.mapId == 251){
            if(Util.isTrue(1, 100)){
                list.add(new ItemMap(zone,861,Util.nextInt(5,10),x,player.location.y,player.id));
            }
            if(Util.isTrue(1, 1000)){
                list.add(new ItemMap(zone,457,1,x,player.location.y,player.id));
            }
        }
        if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            int level = player.clan.banDoKhoBau.level;
            int slhn = Util.nextInt(1, 3) * (level / 10);
            if (Util.nextInt(0, 100) < 100) {

                list.add(new ItemMap(zone, 861, slhn, x, player.location.y, player.id));
            }
        }
        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(double dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(Util.maxInt(dameHit));
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
}
