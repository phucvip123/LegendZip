package com.arriety.models.map.khigas;

import com.girlkun.jdbc.daos.GodGK;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.map.Zone;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.server.Client;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.func.ChangeMapService;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Dev Duy
 */
@Data
public class KhiGas {

    public static final int N_PLAYER_CLAN = 0;
    //số người đứng cùng khu
    public static final int AVAILABLE = 10;
    public static final int TIME_KHI_GAS = 1800000;

    private final int id;
    private final List<Zone> zones;
    private Clan clan;

    private long lastTimeOpen;
    public int levelkg;

    public KhiGas(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public void openKG(Player player, int level) {
        this.lastTimeOpen = System.currentTimeMillis();
        this.clan = player.clan;
        this.levelkg = level;
        player.clan.khigas = this;
        player.clan.playerOpenKG = player.name;
        player.clan.lastTimeOpenKG = this.lastTimeOpen;
        //Khởi tạo quái, boss
        this.init(level);
        player.clan.members.stream().filter(m -> m != null && m.id != player.id).forEach(m -> {
            Player pl = GodGK.loadById(m.id);
            if (pl != null) {
                pl.firstJoinKG = false;
                if (Client.gI().getPlayer(pl.id) == null) {
                    PlayerDAO.updatePlayer(pl);
                }
            }
        });
        player.firstJoinKG = true;
        player.countKG++;
        player.lastimeJoinKG = System.currentTimeMillis();
        ChangeMapService.gI().goToKG(player);
        ItemTimeService.gI().sendTextKhiGas(player);
    }
   
   private void init(int level){
        long totalDame = 0;
        long totalHp = 0;
        for(Player pl : this.clan.membersInGame){
            totalDame += pl.nPoint.dame;
            totalHp += pl.nPoint.hpMax;
        }
        //Hồi sinh quái
        for(Zone zone : this.zones){
            for(Mob mob : zone.mobs){
                if ((totalHp / 40)*(level/10) <= 0 || (totalDame * 3)*(level/10) <= 0){
                    mob.point.dame = 10000;
                    mob.point.maxHp = 1000000;
                    mob.hoiSinh();
                }else{
                    mob.point.dame = (int) (totalHp / 40)*(level/10);
                    mob.point.maxHp = (int) (totalDame * 3)*(level/10);
                    mob.hoiSinh();
                }
            }
        }
    }
}

