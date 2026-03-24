package me.zerog.tets2huclicker.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zerog.tets2huclicker.player.Player;
import me.zerog.tets2huclicker.player.ServerPlayer;

public class ActionUtils {

    private static final String SEPARATOR = ":";
    private static final String ACTION_KEY = "action";

    //  Type : Info : Location : Timestamp
    public static String init(){
        return init(System.currentTimeMillis());
    }

    public static String init(long time){
        return Type.INIT.name() + SEPARATOR + "null" + SEPARATOR + 1 + SEPARATOR + time;
    }

    public static String upgrade(Player.Upgrade upgrade, int location_level){
        return Type.UPGRADE.name() + SEPARATOR + upgrade.name() + SEPARATOR + location_level + SEPARATOR + System.currentTimeMillis();
    }

    public static String killBoss(int location_level){
        return Type.KILL_BOSS.name() + SEPARATOR + "null" + SEPARATOR + location_level + SEPARATOR + System.currentTimeMillis();
    }

    public static void addAction(DataStoreSingleton dataStore, String action){
        int action_amount = dataStore.getOrDefault(ACTION_KEY+"_amount", 0);
        dataStore.setValue(ACTION_KEY+"_"+action_amount, action);
        dataStore.setValue(ACTION_KEY+"_amount", ++action_amount);
    }

    public static Map<Integer, String> getActionStrings(DataStoreSingleton dataStore){
        Map<Integer, String> actions = new HashMap<>();
        int action_amount = dataStore.getOrDefault(ACTION_KEY+"_amount", 0);
        for(int i = 0; i < action_amount; i++){
            actions.put(i, dataStore.getStringValue(ACTION_KEY+"_"+i));
        }
        return actions;
    }

    public static List<Map<String, Object>> getActions(DataStoreSingleton dataStore){
        List<Map<String, Object>> actions = new ArrayList<>();
        getActionStrings(dataStore).forEach((id, value) -> {
            Map<String, Object> action = new HashMap<>();
            String[] data = value.split(SEPARATOR);
            try{
                action.put("action", data[0]);
                action.put("info", data[1]);
                action.put("location", data[2]);
                action.put("clientTimestamp", data[3]);
            }catch (Exception ignored){
                action.put("action", "INIT");
                action.put("info", "null");
                action.put("location", ServerPlayer.getPlayer().getLocationLevel());
                action.put("clientTimestamp", System.currentTimeMillis());
            }
            actions.add(action);
        });

        return actions;
    }

    public enum Type{
        INIT,
        UPGRADE,
        KILL_BOSS
    }
}
