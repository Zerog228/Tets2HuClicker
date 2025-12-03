package me.zerog.tets2huclicker.view;

import androidx.lifecycle.ViewModel;

import me.zerog.tets2huclicker.Player;
import me.zerog.tets2huclicker.mob.Mob;
import me.zerog.tets2huclicker.utils.ProgressManager;

public class InGameView extends ViewModel {

    private Player player;
    private Mob mob;

    public Player createOnlinePlayer(int id) {
        this.player = ProgressManager.loadProgress(id);
        return player;
    }

    public Player getOnlinePlayer(int id) {
        if(player == null){
            return createOnlinePlayer(id);
        }
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Mob getMob() {
        if(mob == null){
            return createMob();
        }
        return mob;
    }

    //TODO Метод для восстановления моба от его хп и текстуры?
    public Mob createMob() {
        this.mob = new Mob(player.getLocationLevel());
        return mob;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
    }
}
