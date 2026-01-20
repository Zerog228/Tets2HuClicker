package me.zerog.tets2huclicker.utils;

import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.zerog.tets2huclicker.Player;
import me.zerog.tets2huclicker.mob.Mob;
import me.zerog.tets2huclicker.player.ServerPlayer;

public class ProgressManager{
    private static Player local_player, selected_player;
    private static DataStoreSingleton datastore;

    private static Mob mob;
    private static boolean mob_first_time_loaded = false;
    private static int mob_leftover_health;
    private static String mob_type_name;

    private static GameMode gameMode = GameMode.LOCAL;
    private static CurrentMenuType currentMenuType = CurrentMenuType.MAIN_MENU;

    //ID's
    private static final String NAME = "P_NAME", LEVEL = "P_LEVEL", EXP = "P_EXP",
            MONEY = "P_MONEY", HP = "P_HP", UPGRADES = "P_UPGRADES", PLAYER_ID = "P_ID", BOMBS = "P_BOMBS",
            LOCATION_LEVEL = "LOCATION_LEVEL",
            LAST_MOB_TYPE = "MOB_TYPE", MOB_HEALTH = "MOB_HEALTH";

    public static DataStoreSingleton getDatastore(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }
        return datastore;
    }

    public static Mob genMob(){
        if(mob_first_time_loaded && mob_type_name != null && !mob_type_name.equals("Zun")){
            mob_first_time_loaded = false;
            mob = new Mob(mob_type_name, mob_leftover_health, local_player.getLocationLevel());
        }else {
            mob = new Mob(selected_player.getLocationLevel());
        }
        return mob;
    }

    public static Mob getMob(){
        if(mob == null || mob.getCurrHealth() < 1){
            return genMob();
        }
        return mob;
    }

    @NotNull
    public static GameMode getGameMode() {
        return gameMode;
    }

    public static void setGameModeToLocal(){
        gameMode = GameMode.LOCAL;
        selected_player = local_player;
    }

    public static void setGameModeToGlobal(){
        gameMode = GameMode.GLOBAL;
        selected_player = ServerPlayer.getPlayer();
    }

    public static void resetLocalPlayer(AppCompatActivity activity){
        local_player = new Player();
        mob = new Mob(0);
        saveProgressOnLocal(activity);
    }

    public static void resetGlobalPlayer(){
        ServerPlayer.resetGlobalPlayer();
    }

    public static void saveProgressOnLocal(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        //Player data
        datastore.setValue(LOCATION_LEVEL, local_player.getLocationLevel());

        datastore.setValue(NAME, local_player.getName());
        datastore.setValue(LEVEL, local_player.getLevel());
        datastore.setValue(EXP, local_player.getExp());
        datastore.setValue(MONEY, local_player.getMoney());
        datastore.setValue(HP, local_player.getHealth());
        datastore.setValue(BOMBS, local_player.getBombs());

        datastore.setValue(UPGRADES, local_player.myUpgradesToString());

        //Mob data
        if(mob == null){
            System.err.println("Trying to save mob data while mob is not currently loaded. Probably because it's reset progress operation");
            mob = new Mob(0);
        }

        System.out.println("Saving progress. Mob - "+mob.getType());

        datastore.setValue(LAST_MOB_TYPE, mob.getType());
        datastore.setValue(MOB_HEALTH, mob.getCurrHealth());
    }

    public static void loadProgressFromLocal(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        //Loading player
        int location_level = datastore.getOrDefault(LOCATION_LEVEL, 0);

        String player_name = datastore.getOrDefault(NAME, Player.DEF_NAME);
        int level = datastore.getOrDefault(LEVEL, Player.DEF_LEVEL);
        int xp = datastore.getOrDefault(EXP, Player.DEF_EXP);
        int money = datastore.getOrDefault(MONEY, Player.DEF_MONEY);
        int hp = datastore.getOrDefault(HP, Player.DEF_HEALTH);
        int bombs = datastore.getOrDefault(BOMBS, Player.DEF_BOMBS);
        String upgrades = datastore.getOrDefault(UPGRADES, Player.upgradesToString());

        local_player = new Player(player_name, location_level, level, xp, money, hp, Player.stringToUpgrades(upgrades), bombs);

        System.out.println("Loading progress on local. Last mob - "+datastore.getOrDefault(LAST_MOB_TYPE, "Zun"));

        //Loading mob
        mob_leftover_health = datastore.getOrDefault(MOB_HEALTH, 100);
        mob_type_name = datastore.getOrDefault(LAST_MOB_TYPE, "Zun");
        mob = new Mob(mob_type_name, mob_leftover_health, location_level);
    }

    @Nullable
    public static Player getOnlinePlayer(){
        return ServerPlayer.getPlayer();
    }

    @Nullable
    public static Player getOfflinePlayer(){
        return local_player;
    }

    public static Player getSelectedPlayer(){
        if(selected_player == null){
            selected_player = new Player();
        }
        return selected_player;
    }

    public static CurrentMenuType getCurrentMenuType() {
        return currentMenuType;
    }

    public static void setCurrentMenuType(CurrentMenuType currentMenuType) {
        ProgressManager.currentMenuType = currentMenuType;
    }

    public enum GameMode{
        LOCAL,
        GLOBAL
    }

    public enum CurrentMenuType{
        MAIN_MENU,
        MAIN_GAME_SCREEN,
        SHOP_SCREEN
    }
}
