package me.zerog.tets2huclicker.player;

import androidx.appcompat.app.AppCompatActivity;

import me.zerog.tets2huclicker.Player;
import me.zerog.tets2huclicker.mob.Mob;
import me.zerog.tets2huclicker.utils.DataStoreSingleton;
import me.zerog.tets2huclicker.utils.ProgressManager;

public class LocalPlayer {
    private static Player player;
    private static DataStoreSingleton datastore;

    //ID's
    private static final String NAME = "P_NAME", LEVEL = "P_LEVEL", EXP = "P_EXP",
            MONEY = "P_MONEY", HP = "P_HP", UPGRADES = "P_UPGRADES", BOMBS = "P_BOMBS",
            LOCATION_LEVEL = "LOCATION_LEVEL", LAST_MOB_TYPE = "MOB_TYPE", MOB_HEALTH = "MOB_HEALTH";

    //Mob info
    private static Mob mob;
    private static boolean mob_first_time_loaded = false;
    private static int mob_leftover_health;
    private static String mob_type_name;

    public static void resetPlayer(AppCompatActivity activity){
        player = new Player();
        mob = new Mob(0);
        saveProgress(activity);
    }

    public static void init(AppCompatActivity activity){
        loadProgress(activity);
    }

    private static Mob genMob(){
        if(mob_first_time_loaded && mob_type_name != null && !mob_type_name.equals("Zun")){
            mob_first_time_loaded = false;
            mob = new Mob(mob_type_name, mob_leftover_health, player.getLocationLevel());
        }else {
            mob = new Mob(player.getLocationLevel());
        }
        return mob;
    }

    public static Mob getMob(){
        if(mob == null || mob.getCurrHealth() < 1){
            return genMob();
        }
        return mob;
    }

    public static void saveProgress(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        //Player data
        datastore.setValue(LOCATION_LEVEL, player.getLocationLevel());

        datastore.setValue(NAME, player.getName());
        datastore.setValue(LEVEL, player.getLevel());
        datastore.setValue(EXP, player.getExp());
        datastore.setValue(MONEY, player.getMoney());
        datastore.setValue(HP, player.getHealth());
        datastore.setValue(BOMBS, player.getBombs());

        datastore.setValue(UPGRADES, player.myUpgradesToString());

        //Mob data
        if(mob == null){
            System.err.println("Trying to save mob data while mob is not currently loaded. Probably because it's reset progress operation");
            mob = new Mob(0);
        }

        System.out.println("Saving progress. Mob - "+mob.getType());

        datastore.setValue(LAST_MOB_TYPE, mob.getType());
        datastore.setValue(MOB_HEALTH, mob.getCurrHealth());
    }

    public static void loadProgress(AppCompatActivity activity){
        if(datastore == null){
            datastore = ProgressManager.getDatastore(activity);
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

        player = new Player(player_name, location_level, level, xp, money, hp, Player.stringToUpgrades(upgrades), bombs);

        //System.out.println("Loading progress on local. Last mob - "+datastore.getOrDefault(LAST_MOB_TYPE, "Zun"));

        //Loading mob
        mob_leftover_health = datastore.getOrDefault(MOB_HEALTH, 100);
        mob_type_name = datastore.getOrDefault(LAST_MOB_TYPE, "Zun");
        mob = new Mob(mob_type_name, mob_leftover_health, location_level);
    }

    public static Player getPlayer(){
        return player;
    }

}
