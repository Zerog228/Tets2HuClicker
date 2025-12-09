package me.zerog.tets2huclicker.utils;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.zerog.tets2huclicker.MainActivity;
import me.zerog.tets2huclicker.Player;

public class ProgressManager extends AsyncTask<Integer, Void, Player>{

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://10.0.2.2:8080/players/";
    private static Player online_player, offline_player, selected_player;
    private static DataStoreSingleton datastore;

    private static final String NAME = "P_NAME", LEVEL = "P_LEVEL", XP = "P_XP", MONEY = "P_MONEY", HP = "P_HP", UPGRADES = "P_UPGRADES";

    //TODO DATA SAVING
    public static void saveProgressOnLocal(MainActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        datastore.setStringValue("SOME_SETTING", "bar");
    }

    public static void loadProgressFromLocal(MainActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }

        String player_name = datastore.getOrDefault(NAME, "Reimu");
        int level = datastore.getOrDefault(LEVEL, 1);
        int xp = datastore.getOrDefault(XP, 1);
        int money = datastore.getOrDefault(MONEY, 1);
        int hp = datastore.getOrDefault(HP, 1);

        String upgrades = datastore.getOrDefault(UPGRADES, Player.upgradesToString());

        offline_player = new Player(player_name, level, xp, money, hp, Player.stringToUpgrades(upgrades));
    }

    public static void saveProgressOnServer(Player player){

    }

    public static Player loadProgressFromServer(int player_id){
        if(online_player == null){
            ProgressManager.online_player = new Player();
        }
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

    public static Player getOnlinePlayer(){
        if(online_player == null){
            ProgressManager.online_player = new Player();
        }
        return online_player;
    }

    public static Player getOfflinePlayer(){
        //TODO Offline player getter
        return new Player();
    }

    public static Player getSelectedPlayer(){
        if(selected_player == null){
            selected_player = new Player();
        }
        return selected_player;
    }

    public static void selectPlayer(Player selected){
        selected_player = selected;
    }
}
