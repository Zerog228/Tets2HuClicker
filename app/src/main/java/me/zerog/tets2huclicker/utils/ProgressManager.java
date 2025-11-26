package me.zerog.tets2huclicker.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

import me.zerog.tets2huclicker.Player;

public class ProgressManager extends AsyncTask<Integer, Void, Player>{

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "https://localhost:8080/players/";

    public static void saveProgress(Player player){

    }

    public static Player loadProgress(int player_id){
        new ProgressManager().execute(player_id);

        //TODO
        /*Executor executor = command -> sendGET(player_id);
        executor.execute(() -> sendGET(player_id));*/
        //sendGET(player_id);

        return new Player(1, 0, 0, 10);
    }

    private static void sendGET(int player_id){
        try{
            System.out.println("Tried to send request");
            URL obj = new URL(GET_URL+player_id);
            System.out.println("Our url - "+obj);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);

            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))){
                    String inputLine;


                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }

                System.out.println(response);
            } else {
                System.out.println("GET request did not work.");
            }
        }catch (Exception exception){
            System.out.println("Failed to send request");
            exception.printStackTrace();
        }

    }

    @Override
    protected Player doInBackground(Integer... integers) {
        for(int id : integers){
            sendGET(id);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Player player) {
        //super.onPostExecute(player);
        System.out.println("YEY!");
    }
}
