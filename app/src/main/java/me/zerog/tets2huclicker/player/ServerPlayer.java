package me.zerog.tets2huclicker.player;

import static me.zerog.tets2huclicker.utils.ProgressManager.getDatastore;

import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.zerog.tets2huclicker.Player;
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
    private static final String LOGIN = "user_login", PASSWORD = "user_password", MAIL = "user_mail";
    private static AlertDialog alert;
    //Authorization
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization", ACCESS_TOKEN_KEY = "accessToken", TOKEN_TYPE_KEY = "tokenType";
    private static String authorizationHeader = "";

    //Other
    @Nullable
    private static Executable<Void, Void> postResponseAction;

    public static void init(AppCompatActivity activity){
        if(datastore == null){
            datastore = getDatastore(activity);
        }

        communicator = new ServerCommunicator<>(
                params, HashMap.class,
                null,
                null
        );

        alert = new AlertDialog.Builder(activity)
                .setTitle("No online account")
                .setMessage("You don't have online account")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.cancel, null)
                .setIconAttribute(android.R.attr.alertDialogIcon).create();

        signIn(false);
    }

    //TODO Post on server
    public static void saveProgressOnServer(Player player){

    }

    public static void signUp(String name, String password, String email, @Nullable Executable<HashMap<String, String>, Void> postResponseAction, @Nullable Executable<Exception, Void> postFailAction){
        communicator.setPostExecuteSuccess(map -> {
            fillResponse(map);
            if(postResponseAction != null){
                postResponseAction.execute(map); //TODO If registered successfully -> save login + password. otherwise show alert
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
        communicator.run(communicator.getParams().withPostfix("/signup", ServerCommunicator.ReqMethod.POST));
    }

    public static void signIn(boolean showAlert){
        //If user exists
        if(datastore.hasKey(LOGIN)){
            params.addJSONBodyValue("username", datastore.getStringValue(LOGIN)); //Login
            params.addJSONBodyValue("password", datastore.getStringValue(PASSWORD)); //Password

            //Fill player and call outer method
            communicator.setPostExecuteSuccess(map -> {
                fillResponse(map);
                buildPlayer();
                if(postResponseAction != null){
                    postResponseAction.execute();
                }
            });

            //Send communication
            communicator.run(communicator.getParams().withPostfix("/signin"));
        }else if(showAlert) {
            alert.show();
        }
    }

    public static void resetPlayer(){
        player = new Player();
        //TODO Reset player on server
    }

    public static Mob getMob(){
        //TODO Gen mob
        return null;
    }

    private static void buildPlayer(){
        //TODO build player from response. Нужна проверка на то, можно ли создать игрока из полученной информации
        //player = ...
    }

    private static void fillResponse(HashMap<String, String> response){
        System.out.println("Filled response!");
        communicationResponce = new HashMap<String, String>(response);
    }

    public static Player getPlayer(){
        return player;
    }

    public static void setPostResponseAction(@Nullable Executable<Void, Void> postResponseAction){
        ServerPlayer.postResponseAction = postResponseAction;
    }
}
