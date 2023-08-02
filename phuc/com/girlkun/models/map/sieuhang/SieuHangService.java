package com.girlkun.models.map.sieuhang;

import com.girlkun.jdbc.daos.GodGK;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dev Duy
 */
public class SieuHangService {

    private static SieuHangService i;

    public static SieuHangService gI() {
        if (i == null) {
            i = new SieuHangService();
        }
        return i;
    }

    public void startChallenge(Player player, int id) throws Exception {
        Player pl = GodGK.loadById(id);
        if (pl != null) {
            pl.nPoint.calPoint();
            int[][] skillTemp = new int[pl.playerSkill.skills.size()][3];
            for (byte i = 0; i < pl.playerSkill.skills.size(); i++) {
                Skill skill = pl.playerSkill.skills.get(i);
                if (skill.point > 0) {
                    skillTemp[i][0] = skill.template.id;
                    skillTemp[i][1] = skill.point;
                    skillTemp[i][2] = skill.IsSkillDam(skill.template.id) ? 300 : skill.coolDown;
                }
            }
            BossData data = BossData.builder()
                    .name(pl.name)
                    .gender(pl.gender)
                    .dame(pl.nPoint.dame)
                    .hp(new int[]{pl.nPoint.hpMax})
                    .outfit(new short[]{pl.getHead(), pl.getBody(), pl.getLeg(), -1, -1, -1})
                    .mapJoin(new int[]{113})
                    .skillTemp(skillTemp)
                    .secondsRest(BossesData.REST_5_S)
                    .build();
            ClonePlayer boss = new ClonePlayer(player, data, (int) pl.id);
            boss.rankSieuHang = pl.rankSieuHang;
            Zone zone = getMapChalllenge(113);
            if (zone != null) {
                ChangeMapService.gI().changeMap(player, zone, player.location.x, 360);
                Util.setTimeout(() -> {
                    SieuHang mc = new SieuHang();
                    mc.setPlayer(player);
                    mc.toTheNextRound(boss);
                    SieuHangManager.gI().add(mc);
                }, 500);
            }
        }
    }

    public void moveFast(Player pl, int x, int y) {
        Message msg;
        try {
            msg = new Message(58);
            msg.writer().writeInt((int) pl.id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeInt((int) pl.id);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendTypePK(Player player, Player boss) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 35);
            msg.writer().writeInt((int) boss.id);
            msg.writer().writeByte(3);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public Zone getMapChalllenge(int mapId) {
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        if (map.getNumOfBosses() < 1) {
            return map;
        }
        return null;
    }
}
