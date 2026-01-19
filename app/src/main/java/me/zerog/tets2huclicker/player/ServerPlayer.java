package me.zerog.tets2huclicker.player;

import static me.zerog.tets2huclicker.utils.ProgressManager.getDatastore;

import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.zerog.tets2huclicker.Player;

public class ServerPlayer extends AsyncTask<Integer, Void, Player> {

    private static final String PLAYER_ID = "P_ID";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://10.0.2.2:8080/players/";

    private static Player online_player;

    public static Player getPlayer(){
        return online_player;
    }

    public static int getPlayerID(AppCompatActivity activity){
        return getDatastore(activity).getOrDefault(PLAYER_ID, 1);
    }

    //TODO Post on server
    public static void saveProgressOnServer(Player player){

    }

    public static void resetGlobalPlayer(){
        online_player = new Player();
        //TODO Reset player on server
    }


    //TODO Load mobType and leftoverHP from server
    @Nullable
    public static Player loadProgressFromServer(int player_id){
        new ServerPlayer().execute(player_id);
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
            online_player = Player.copyOf(player);
            super.onPostExecute(player);
        }
    }
}
