package com.girlkun.models.map.sieuhang;

import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.dhvt.BossDHVT;
import com.girlkun.models.player.Player;

/**
 *
 * @author Dev Duy
 */
public class ClonePlayer extends BossDHVT{
    public ClonePlayer(Player player, BossData data, int id) throws Exception {
        super(id, data,5000);
        this.playerAtt = player;
    }
}
