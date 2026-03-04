package me.zerog.tets2huclicker.player;

import static me.zerog.tets2huclicker.utils.ProgressManager.getDatastore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.zerog.tets2huclicker.mob.Mob;
import me.zerog.tets2huclicker.utils.DataStoreSingleton;
import me.zerog.tets2huclicker.utils.Executable;
import me.zerog.tets2huclicker.utils.ServerCommunicator;

public class ServerPlayer{
    //Server communication
    private static ServerCommunicator<Void, HashMap> communicator;
    private static ServerCommunicator.ReqParams params = new ServerCommunicator.ReqParams("http://10.0.2.2:8080/api/auth");
    private static HashMap<String, String> communicationResponce = new HashMap<>();

    //Player data
    private static Player player;
    private static List<Mob> mobsQueue = new ArrayList<>(); //TODO Maybe replace it with a Queue?
    private static DataStoreSingleton datastore;

    //Authorization
    private static final String LOGIN = "user_login", PASSWORD = "user_password", MAIL = "user_mail";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization", ACCESS_TOKEN_KEY = "accessToken", TOKEN_TYPE_KEY = "tokenType";
    private static String authToken;
    private static String authTokenType;

    public static void init(AppCompatActivity activity){
        if(datastore == null){
            datastore = getDatastore(activity);
        }

        communicator = new ServerCommunicator<>(
                params, HashMap.class,
                null,
                null
        );

        signIn();
    }

    //TODO Post on server
    public static void saveProgressOnServer(Player player){

    }

    public static void signUp(String name, String password, String email, @Nullable Executable<HashMap<String, String>, Void> postResponseAction, @Nullable Executable<Exception, Void> postFailAction){
        communicator.setPostExecuteSuccess(map -> {
            fillResponse(map);
            if(postResponseAction != null){
                postResponseAction.execute(map); //TODO If registered successfully -> save login + password. otherwise show alert
                signIn();
            }
        });
        communicator.setPostExecuteFail(response -> {
            if(postFailAction != null){
                postFailAction.execute(response);
            }
        });

        params.addJSONBodyValue("username", name);
        params.addJSONBodyValue("password", password);
        params.addJSONBodyValue("email", email);


        //Send communication
        //System.out.println("Sent /signup");
        //communicator.setParams(params); //Is this really needed?
        communicator.run(params.withPostfix("/signup", ServerCommunicator.ReqMethod.POST));
    }

    public static void signIn(){
        signIn(null, null);
    }

    public static void signIn(@Nullable String login, @Nullable String password, @Nullable Executable<HashMap<String, String>, Void> postSuccessAction, @Nullable Executable<Exception, Void> postFailAction){
        if(!datastore.hasKey(LOGIN) && login != null && password != null && !login.isEmpty() && !password.isEmpty()){
            datastore.setValue(LOGIN, login);
            datastore.setValue(PASSWORD, password);
        }
        signIn(postSuccessAction, postFailAction);
    }

    public static void signIn(@Nullable Executable<HashMap<String, String>, Void> postSuccessAction, @Nullable Executable<Exception, Void> postFailAction){
        //If user exists
        if(datastore.hasKey(LOGIN)){
            params.addJSONBodyValue("username", datastore.getStringValue(LOGIN)); //Login
            params.addJSONBodyValue("password", datastore.getStringValue(PASSWORD)); //Password

            //Fill player related fields and call outer method
            communicator.setPostExecuteSuccess(map -> {
                fillResponse(map);
                buildPlayer(map);
                if(postSuccessAction != null){
                    postSuccessAction.execute(map);
                }
            });
            communicator.setPostExecuteFail(response -> {
                if(postFailAction != null){
                    postFailAction.execute(response);
                }
            });

            //Send communication
            //communicator.setParams(params); //Is this really needed?
            communicator.run(params.withPostfix("/signin"));
        }
    }

    public static void savePlayerCredentials(String login, String password){
        datastore.setValue(LOGIN, login);
        datastore.setValue(PASSWORD, password);
    }

    public static String getLogin(){
        return datastore.getOrDefault(LOGIN, "");
    }

    public static String getPassword(){
        return datastore.getOrDefault(PASSWORD, "");
    }

    public static void resetPlayer(){
        player = new Player();
        //TODO Reset player on server
    }

    public static Mob getMob(){
        //TODO Gen mob
        return null;
    }

    private static void buildPlayer(HashMap<String, String> map){
        System.out.println(map);
        Gson gson = new Gson();
        try{
            //Player converted = ;
            player = Player.copyOf(gson.fromJson(map.toString(), Player.class)); //TODO Some values are not copied properly! Check which values and fix it
            player.setName(map.get("username"));
            player.setUpgrades(Player.stringToUpgrades(map.get("abilities_map")));
            //System.out.println(player);
            authToken = map.get(ACCESS_TOKEN_KEY);
            authTokenType = map.get(TOKEN_TYPE_KEY);
        }catch (Exception e){
            System.out.println("Failed to convert player from JSON!");
            e.printStackTrace();
        }
        //TODO build player from response. Нужна проверка на то, можно ли создать игрока из полученной информации
        //player = ...
    }

    private static void fillResponse(HashMap<String, String> response){
        //System.out.println("Filled response!");
        communicationResponce = new HashMap<String, String>(response);
    }

    public static Player getPlayer(){
        return player;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static String getAuthTokenType() {
        return authTokenType;
    }
}
