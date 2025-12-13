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

public class ProgressManager extends AsyncTask<Integer, Void, Player>{

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://10.0.2.2:8080/players/";
    private static Player online_player, local_player, selected_player;
    private static DataStoreSingleton datastore;

    private static Mob mob;
    private static boolean mob_first_time_loaded = false;
    private static int mob_leftover_health;
    private static String mob_type_name;

    private static GameMode gameMode = GameMode.LOCAL;

    //ID's
    private static final String NAME = "P_NAME", LEVEL = "P_LEVEL", EXP = "P_EXP",
            MONEY = "P_MONEY", HP = "P_HP", UPGRADES = "P_UPGRADES", PLAYER_ID = "P_ID",
            LAST_MOB_TYPE = "MOB_TYPE", MOB_HEALTH = "MOB_HEALTH";

    public static int getPlayerID(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        return datastore.getOrDefault(PLAYER_ID, 1);
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
        selected_player = online_player;
    }

    public static void resetLocalPlayer(){
        local_player = new Player();
    }

    public static void resetGlobalPlayer(){
        online_player = new Player();
        //TODO Reset player on server
    }

    public static void saveProgressOnLocal(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        //Player data
        datastore.setValue(NAME, local_player.getName());
        datastore.setValue(LEVEL, local_player.getLevel());
        datastore.setValue(EXP, local_player.getExp());
        datastore.setValue(MONEY, local_player.getMoney());
        datastore.setValue(HP, local_player.getHealth());

        datastore.setValue(UPGRADES, local_player.myUpgradesToString());

        //Mob data
        datastore.setValue(LAST_MOB_TYPE, mob.getType());
        datastore.setValue(MOB_HEALTH, mob.getCurrHealth());
    }

    public static void loadProgressFromLocal(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        //Loading player
        String player_name = datastore.getOrDefault(NAME, Player.DEF_NAME);
        int level = datastore.getOrDefault(LEVEL, Player.DEF_LEVEL);
        int xp = datastore.getOrDefault(EXP, Player.DEF_EXP);
        int money = datastore.getOrDefault(MONEY, Player.DEF_MONEY);
        int hp = datastore.getOrDefault(HP, Player.DEF_HEALTH);
        String upgrades = datastore.getOrDefault(UPGRADES, Player.upgradesToString());

        local_player = new Player(player_name, level, xp, money, hp, Player.stringToUpgrades(upgrades));

        //Loading mob
        mob_leftover_health = datastore.getOrDefault(MOB_HEALTH, 100);
        mob_type_name = datastore.getOrDefault(LAST_MOB_TYPE, "Zun");
    }

    //TODO Post on server
    public static void saveProgressOnServer(Player player){

    }


    //TODO Load mobType and leftoverHP from server
    @Nullable
    public static Player loadProgressFromServer(int player_id){
        new ProgressManager().execute(player_id);
        return online_player;
    }

    private static String sendGET(int player_id){
        try{
            URL obj = new URL(GET_URL+player_id);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();

            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    //System.out.println("Response - "+response);
                    return response.toString();
                }
            } else {
                System.out.println("GET request did not work.");
            }
        }catch (Exception exception){
            System.out.println("Failed to send request");
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected Player doInBackground(Integer... integers) {
        for(int id : integers){
            Gson gson = new Gson();
            return gson.fromJson(sendGET(id), Player.class);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Player player) {
        if(player != null){;
            ProgressManager.online_player = Player.copyOf(player);
            super.onPostExecute(player);
        }
    }

    @Nullable
    public static Player getOnlinePlayer(){
        return online_player;
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

    public enum GameMode{
        LOCAL,
        GLOBAL
    }
}
