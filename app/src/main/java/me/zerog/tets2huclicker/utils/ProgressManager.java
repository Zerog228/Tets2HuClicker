package me.zerog.tets2huclicker.utils;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

import me.zerog.tets2huclicker.MainActivity;
import me.zerog.tets2huclicker.Player;

public class ProgressManager extends AsyncTask<Integer, Void, Player>{

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://10.0.2.2:8080/players/";
    private static Player player;

    public static void saveProgress(Player player){

    }

    public static Player loadProgress(int player_id){
        if(player == null){
            ProgressManager.player = new Player(1, 0, 0, 10);
            new ProgressManager().execute(player_id);
        }
        return player;
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

                    return response.toString();
                }
            } else {
                //System.out.println("GET request did not work.");
            }
        }catch (Exception exception){
            //System.out.println("Failed to send request");
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
        if(player != null){
            ProgressManager.player = Player.copyOf(player);
            super.onPostExecute(player);
        }
    }

    public static Player getPlayer(){
        return player;
    }
}
