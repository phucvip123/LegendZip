package com.arriety.models.map.bdkb;

import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.list_boss.phoban.TrungUyXanhLo;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dev Duy
 */
public class BanDoService {

    private static BanDoService I;

    public static BanDoService gI() {
        if (BanDoService.I == null) {
            BanDoService.I = new BanDoService();
        }
        return BanDoService.I;
    }

    public List<BanDo> banDos;

    private BanDoService() {
        this.banDos = new ArrayList<>();
        for (int i = 0; i < BanDo.AVAILABLE; i++) {
            this.banDos.add(new BanDo(i));
        }
    }

    public void addMapBanDo(int id, Zone zone) {
        this.banDos.get(id).getZones().add(zone);
    }

    public void openBDKB(Player pl, int level) {
        if (pl.clan == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (InventoryServiceNew.gI().findItemBag(pl, 611) == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy bản đồ kho báu");
            return;
        }
        if (pl.countBDKB >= 3) {
            Service.gI().sendThongBao(pl, "Bạn đã đạt giới hạn lượt đi trong ngày");
            return;
        }
        BanDo bd = null;
        for (BanDo dt : this.banDos) {
            if (dt.getClan() == null) {
                bd = dt;
                break;
            }
        }
        if (bd == null) {
            Service.getInstance().sendThongBao(pl, "Bản đồ đã đầy, hãy quay lại vào lúc khác!");
            return;
        }
        InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 611), 1);
        InventoryServiceNew.gI().sendItemBags(pl);
        bd.openBDKB(pl, level);
        ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        try {
            new TrungUyXanhLo(pl.clan.bando.getMapById(137),pl.clan.bando.level);
        } catch (Exception e) {
        }
    }

    public void joinBDKB(Player pl) {
        if (pl.clan == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (pl.clan.bando != null) {
            if (!pl.firstJoinBDKB) {
                pl.countBDKB++;
                pl.firstJoinBDKB = true;
            }
            pl.lastimeJoinBDKB = System.currentTimeMillis();
            ChangeMapService.gI().goToDBKB(pl);
            ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        }
    }
    private void kickOutOfMapBdkb(Player player) {
        if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            player.clan.bando = null;
            Service.gI().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }
    }

    private void ketthucbdkb(Player player) {
        List<Player> playersMap = player.zone.getPlayers();
        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            kickOutOfMapBdkb(pl);
        }
    }
    public void update(Player player) {
        if (player.zone == null || !MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            return;
        }
        try {
                long now = System.currentTimeMillis();
                if (!(now >= player.clan.lastTimeOpenBDKB && now <= (player.clan.lastTimeOpenBDKB + BanDo.TIME_BAN_DO))) {
                    ketthucbdkb(player);
                }
            } catch (Exception ignored) {
            }
    }
}
