package com.arriety.models.map.doanhtrai;

import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import java.util.ArrayList;
import java.util.List;


public class DoanhTraiService {

    private static DoanhTraiService I;

    public static DoanhTraiService gI() {
        if (DoanhTraiService.I == null) {
            DoanhTraiService.I = new DoanhTraiService();
        }
        return DoanhTraiService.I;
    }

    public List<DoanhTrai> doanhTrais;

    private DoanhTraiService() {
        this.doanhTrais = new ArrayList<>();
        for (int i = 0; i < DoanhTrai.AVAILABLE; i++) {
            this.doanhTrais.add(new DoanhTrai(i));
        }
    }

    public void addMapDoanhTrai(int id, Zone zone) {
        this.doanhTrais.get(id).getZones().add(zone);
    }

    public void joinDoanhTrai(Player pl) {
        if (pl.clan == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (pl.clan.doanhTrai != null) {
            ChangeMapService.gI().changeMapInYard(pl, 53, -1, 60);
            return;
        }
        DoanhTrai doanhTrai = null;
        for (DoanhTrai dt : this.doanhTrais) {
            if (dt.getClan() == null) {
                doanhTrai = dt;
                break;
            }
        }
        if (doanhTrai == null) {
            Service.gI().sendThongBao(pl, "Doanh trại đã đầy, hãy quay lại vào lúc khác!");
            return;
        }
        doanhTrai.openDoanhTrai(pl);
        ItemTimeService.gI().sendTextDoanhTrai(pl);
    }
    private void kickOutOfMapDoanhTRai(Player player) {
        if (MapService.gI().isMapDoanhTrai(player.zone.map.mapId)) {
            player.clan.doanhTrai = null;
            Service.gI().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }
    }

    private void ketthucDoanhTrai(Player player) {
        List<Player> playersMap = player.zone.getPlayers();
        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            kickOutOfMapDoanhTRai(pl);
        }
    }
    public void update(Player player) {
        if (player.zone == null || !MapService.gI().isMapDoanhTrai(player.zone.map.mapId)) {
            return;
        }
        try {
                long now = System.currentTimeMillis();
                if (!(now >= player.clan.lastTimeOpenDoanhTrai && now <= (player.clan.lastTimeOpenDoanhTrai + DoanhTrai.TIME_DOANH_TRAI))) {
                    ketthucDoanhTrai(player);
                }
            } catch (Exception ignored) {
            }
    }
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
