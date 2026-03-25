package me.zerog.tets2huclicker.player;

import static me.zerog.tets2huclicker.utils.ProgressManager.getDatastore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zerog.tets2huclicker.mob.Mob;
import me.zerog.tets2huclicker.utils.ActionUtils;
import me.zerog.tets2huclicker.utils.DataStoreSingleton;
import me.zerog.tets2huclicker.utils.Executable;
import me.zerog.tets2huclicker.utils.ServerCommunicator;

public class ServerPlayer{
    //Server communication
    private static ServerCommunicator<Void, HashMap> communicator;
    private static ServerCommunicator.ReqParams authParams = new ServerCommunicator.ReqParams("http://10.0.2.2:8080/api/auth");
    private static ServerCommunicator.ReqParams gameParams = new ServerCommunicator.ReqParams("http://10.0.2.2:8080/api/game");
    private static HashMap<String, Object> communicationResponse = new HashMap<>();

    //Player data
    private static Player player;
    private static DataStoreSingleton datastore;

    //Authorization
    private static final String LOGIN = "user_login", PASSWORD = "user_password", MAIL = "user_mail";

    //Server-received data
    private static long mobSeed;
    private static List<Mob> mobs;

    //Additional updates
    private static Executable<Void, Void> updatePlayerInfo;

    public static void init(AppCompatActivity activity){
        if(datastore == null){
            datastore = getDatastore(activity);
        }

        communicator = new ServerCommunicator<>(
                authParams, HashMap.class,
                null,
                null
        );

        signIn();
    }

    //TODO Ability to signUp only if there is no associated account
    public static void signUp(String name, String password, String email, @Nullable Executable<HashMap<String, String>, Void> postResponseAction, @Nullable Executable<Exception, Void> postFailAction){
        long time = System.currentTimeMillis();
        authParams.addJSONBodyValue("timestamp", String.valueOf(time));

        communicator.setPostExecuteSuccess(map -> {
            ActionUtils.addAction(datastore, ActionUtils.init(time));

            fillResponse(map);
            if(postResponseAction != null){
                postResponseAction.execute(map);
                signIn();
            }
        });
        communicator.setPostExecuteFail(response -> {
            if(postFailAction != null){
                postFailAction.execute(response);
            }
        });

        authParams.addJSONBodyValue("username", name);
        authParams.addJSONBodyValue("password", password);
        authParams.addJSONBodyValue("email", email);

        communicator.run(authParams.withPostfix("/signup", ServerCommunicator.ReqMethod.POST));
    }

    public static void signIn(){
        signIn(null, null);
    }

    public static void signIn(@Nullable String login, @Nullable String password, @Nullable Executable<HashMap<String, Object>, Void> postSuccessAction, @Nullable Executable<Exception, Void> postFailAction){
        if(!datastore.hasKey(LOGIN) && login != null && password != null && !login.isEmpty() && !password.isEmpty()){
            datastore.setValue(LOGIN, login);
            datastore.setValue(PASSWORD, password);
        }
        signIn(postSuccessAction, postFailAction);
    }

    public static void signIn(@Nullable Executable<HashMap<String, Object>, Void> postSuccessAction, @Nullable Executable<Exception, Void> postFailAction){
        //If user exists
        if(datastore.hasKey(LOGIN)){
            authParams.addJSONBodyValue("username", datastore.getStringValue(LOGIN)); //Login
            authParams.addJSONBodyValue("password", datastore.getStringValue(PASSWORD)); //Password

            //Fill player related fields and call outer method
            communicator.setPostExecuteSuccess(map -> {
                fillResponse(map);
                buildPlayer(map);
                if(postSuccessAction != null){
                    postSuccessAction.execute(map);
                }
                if(updatePlayerInfo != null){
                    updatePlayerInfo.execute(null);
                }
            });
            communicator.setPostExecuteFail(response -> {
                if(postFailAction != null){
                    postFailAction.execute(response);
                }
            });

            authParams.addJSONBodyValue("actions", new Gson().toJson(ActionUtils.getActions(datastore)));

            //Send communication
            communicator.run(authParams.withPostfix("/signin"));
        }
    }

    public static void sendKillBossRequest(){
        ActionUtils.addAction(datastore, ActionUtils.killBoss(player.getLocationLevel()));
        communicator.clearActions();
        ServerCommunicator.ReqParams copy = gameParams.withPostfix("/boss");
        copy.addJSONBodyValue("location_level", player.getLocationLevel()+"");
        copy.addJSONBodyValue("timestamp", System.currentTimeMillis()+"");
        copy.setMethod(ServerCommunicator.ReqMethod.GET);

        communicator.run(copy);
    }

    public static void sendUpgradeRequest(Player.Upgrade upgrade){
        ActionUtils.addAction(datastore, ActionUtils.upgrade(upgrade, player.getLocationLevel()));
        communicator.clearActions();
        ServerCommunicator.ReqParams copy = gameParams.withPostfix("/upgrade");
        copy.addJSONBodyValue("ability", upgrade.name());
        copy.addJSONBodyValue("location_level", player.getLocationLevel()+"");
        copy.addJSONBodyValue("timestamp", System.currentTimeMillis()+"");
        copy.setMethod(ServerCommunicator.ReqMethod.GET);

        communicator.run(copy);
    }

    public static void sendSaveRequest(){
        sendSaveRequest(null, null);
    }

    public static void sendSaveRequest(Executable<Void, Void> success, Executable<Exception, String> fail){
        ServerCommunicator.ReqParams copy = gameParams.withPostfix("/save");
        communicator.clearActions();
        communicator.setPostExecuteSuccess(response -> {
            if(success != null){
                success.execute(null);
            }
        });
        communicator.setPostExecuteFail(exception -> {
            if(fail != null){
                fail.execute(exception);
            }
        });
        copy.setMethod(ServerCommunicator.ReqMethod.GET);

        copy.addJSONBodyValue("location_level", String.valueOf(player.getLocationLevel()));
        copy.addJSONBodyValue("actions", new Gson().toJson(ActionUtils.getActions(datastore)));

        communicator.run(copy);
    }

    public static void sendResetRequest(){
        player = new Player();
        //TODO Reset player on server
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

    public static Mob getMob(){
        return mobs.get(player.getLocationLevel() - 1);
    }

    public static String getPlayerInfo(){
        if(player != null){
            return player.getName() + " " + player.getExp() + "e " + player.getMoney() + "$ " + player.getLocationLevel() + "_ll";
        }else{
            return "Player not found!";
        }
    }

    public static void setUpdatePlayerInfo(Executable<Void, Void> updater){
        updatePlayerInfo = updater;
    }

    private static void buildPlayer(HashMap<String, Object> map){
        gameParams.addHeader("Authorization", "Bearer "+map.get("token"));
        try{
            mobSeed = Long.parseLong((String) map.get("mob_seed"));
            mobs = Mob.genMobs(8 * Mob.getLocationLevelsPerBoss(), mobSeed);
            List<Map<String, Object>> actions = (List<Map<String, Object>>) map.get("actions");
            Map<Integer, List<Player.Upgrade>> upgradeMap = mapUpgrades(actions);
            ActionUtils.syncActions(datastore, actions);
            player = new Player();
            System.out.println("Got level: "+map.get("location_level"));

            for(int i = 0; i < Integer.parseInt((String) map.get("location_level")) - 1; i++){
                if(upgradeMap.containsKey(i)){
                    for(Player.Upgrade upgrade : upgradeMap.get(i)){
                        player.upgradeAbility(upgrade);
                    }
                }

                //Kill mob from this location
                mobs.get(i).kill(player, false);
                player.increaseLocationLevel();
            }
            System.out.println(getPlayerInfo());

        }catch (Exception ignored){
            ignored.printStackTrace();
        }

        if(updatePlayerInfo != null){
            updatePlayerInfo.execute(null);
        }
    }

    private static void fillResponse(HashMap<String, Object> response){
        //System.out.println("Filled response!");
        communicationResponse = new HashMap<>(response);
    }

    public static Player getPlayer(){
        return player;
    }

    private static Map<Integer, List<Player.Upgrade>> mapUpgrades(List<Map<String, Object>> actions){
        Map<Integer, List<Player.Upgrade>> upgradeMap = new HashMap<>();
        actions.forEach(map -> {
            try{
                if(((String) map.get("action")).equals(ActionUtils.Type.UPGRADE.name())){
                    if(upgradeMap.containsKey((int) map.get("location") - 1)){
                        List<Player.Upgrade> upgrades = new ArrayList<>(upgradeMap.get((int) map.get("location") - 1));
                        upgrades.add(Player.Upgrade.valueOf((String) map.get("info")));
                        upgradeMap.put((int) map.get("location") - 1, upgrades);
                    }else{
                        upgradeMap.put((int) map.get("location") - 1, Collections.singletonList(Player.Upgrade.valueOf((String) map.get("info"))));
                    }
                }
            }catch (Exception ignored){
                ignored.printStackTrace();
            }
        });
        return upgradeMap;
    }
}
