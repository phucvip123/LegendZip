package com.arriety.models.map.khigas;

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
 * @author Dev Duy Béo
 */
public class KhiGasService {

    private static KhiGasService I;

    public static KhiGasService gI() {
        if (KhiGasService.I == null) {
            KhiGasService.I = new KhiGasService();
        }
        return KhiGasService.I;
    }

    public List<KhiGas> khiGas;

    private KhiGasService() {
        this.khiGas = new ArrayList<>();
        for (int i = 0; i < KhiGas.AVAILABLE; i++) {
            this.khiGas.add(new KhiGas(i));
        }
    }

    public void addMapKhiGas(int id, Zone zone) {
        this.khiGas.get(id).getZones().add(zone);
    }

    public void openKG(Player pl, int level) {
        if (pl.clan == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (InventoryServiceNew.gI().findItemBag(pl, 611) == null) {
            Service.gI().sendThongBao(pl, "Không tìm thấy bản đồ kho báu");
            return;
        }
        if (pl.countKG >= 3) {
            Service.gI().sendThongBao(pl, "Bạn đã đạt giới hạn lượt đi trong ngày");
            return;
        }
        KhiGas kg = null;
        for (KhiGas dt : this.khiGas) {
            if (dt.getClan() == null) {
                kg = dt;
                break;
            }
        }
        if (kg == null) {
            Service.getInstance().sendThongBao(pl, "Khí Gas đã đầy, hãy quay lại vào lúc khác!");
            return;
        }
        InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 611), 1);
        InventoryServiceNew.gI().sendItemBags(pl);
        kg.openKG(pl, level);
        ItemTimeService.gI().sendTextKhiGas(pl);
        try {
            new TrungUyXanhLo(pl.clan.khigas.getMapById(147),pl.clan.khigas.levelkg);
        } catch (Exception e) {
        }
    }

    public void joinKG(Player pl) {
        if (pl.clan == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (pl.clan.khigas != null) {
            if (!pl.firstJoinKG) {
                pl.countKG++;
                pl.firstJoinKG = true;
            }
            pl.lastimeJoinKG = System.currentTimeMillis();
            ChangeMapService.gI().goToKG(pl);
            ItemTimeService.gI().sendTextKhiGas(pl);
        }
    }
//    private void kickOutOfMapKg(Player player) {
//        if (MapService.gI().isMapKhiGas(player.zone.map.mapId)) {
//            player.clan.khigas = null;
//            Service.gI().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
//            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
//        }
//    }

//    private void ketthuckg(Player player) {
//        List<Player> playersMap = player.zone.getPlayers();
//        for (int i = playersMap.size() - 1; i >= 0; i--) {
//            Player pl = playersMap.get(i);
//            kickOutOfMapKg(pl);
//        }
//    }
//    public void update(Player player) {
//        if (player.zone == null || !MapService.gI().isMapKhiGas(player.zone.map.mapId)) {
//            return;
//        }
//        try {
//                long now = System.currentTimeMillis();
//                if (!(now >= player.clan.lastTimeOpenKG && now <= (player.clan.lastTimeOpenKG + KhiGas.TIME_KHI_GAS))) {
//                    ketthuckg(player);
//                }
//            } catch (Exception ignored) {
//            }
//    }
}
