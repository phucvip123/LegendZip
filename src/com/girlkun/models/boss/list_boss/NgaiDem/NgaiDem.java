/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.NgaiDem;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.SkillService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

import java.util.Random;

/**
 * @Stole By MITCHIKEN ZALO 0358689793
 */
public class NgaiDem extends Boss {

    public NgaiDem(Zone zone) throws Exception {
        super(BossID.NGAI_DEM, BossesData.NGAI_DEM);
        this.zone = zone;
    }
    public NgaiDem() throws Exception {
        super(BossID.NGAI_DEM, BossesData.NGAI_DEM);
    }
    
   
    
    @Override
    public void active() {
        super.active();
    }
    @Override
    public void joinMap() {
        super.joinMapByZone(zone);
        st = System.currentTimeMillis();

    }
    private long st;
   

    @Override
    public void reward(Player plKill) {
        int[] NRs = new int[]{17,16,15,18,19,20};
        //int[] binhCan = new int[]{1233,1234,1235};
        //int randomBC = new Random().nextInt(binhCan.length);
        int randomNR = new Random().nextInt(NRs.length);
        if (Util.isTrue(50, 100)) {
            if (Util.isTrue(0, 50)) {
                Service.gI().dropItemMap(this.zone, Util.manhTS(zone,1241, 1, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 674, 1, this.location.x, this.location.y, plKill.id));
        } else if (Util.isTrue(50, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
    }
 
    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if(this.nPoint.hp>=Integer.MAX_VALUE){
            this.chatHp();
        }
        if (!this.isDie()) {
            
            damage = this.nPoint.subDameInjureWithDeff(damage/1);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage/2;
            }
            damage = 20;
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
    @Override
  public void leaveMap(){
      super.leaveMap();
      BossManager.gI().removeBoss(this);
      super.dispose();
  }
}
