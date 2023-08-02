package com.girlkun.models.player;

/**
 *
 * @Stole By MITCHIKEN ZALO 0358689793 💖
 *
 */
public class Gift {

    private Player player;
    
    public Gift(Player player){
        this.player = player;
    }
    
    public boolean goldTanThu;
    public boolean gemTanThu;
    
    public void dispose(){
        this.player = null;
    }
    
}
