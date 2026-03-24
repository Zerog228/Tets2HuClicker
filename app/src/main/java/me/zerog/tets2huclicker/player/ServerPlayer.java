package me.zerog.tets2huclicker.player;

import static me.zerog.tets2huclicker.utils.ProgressManager.getDatastore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    private static Player player; //TODO Add 'CurrentQMoney' and 'CurrentQEXP' that based on current killed mob in the queue. On Save/Load calculate this value, save only location level and money/exp.
    private static DataStoreSingleton datastore;

    //Authorization
    private static final String LOGIN = "user_login", PASSWORD = "user_password", MAIL = "user_mail";

    //Server-received data
    private static long mobSeed;
    private static List<Mob> mobs;

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

    //TODO Post on server
    public static void saveProgressOnServer(Player player){

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
            });
            communicator.setPostExecuteFail(response -> {
                if(postFailAction != null){
                    postFailAction.execute(response);
                }
            });

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

        communicator.run(copy);
    }

    public static void sendUpgradeRequest(Player.Upgrade upgrade){
        ActionUtils.addAction(datastore, ActionUtils.upgrade(upgrade, player.getLocationLevel()));
        communicator.clearActions();
        ServerCommunicator.ReqParams copy = gameParams.withPostfix("/upgrade");
        copy.addJSONBodyValue("ability", upgrade.name());
        copy.addJSONBodyValue("location_level", player.getLocationLevel()+"");
        copy.addJSONBodyValue("timestamp", System.currentTimeMillis()+"");

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

    private static void buildPlayer(HashMap<String, Object> map){
        gameParams.addHeader("Authorization", "Bearer "+map.get("token"));

        try{
            mobSeed = (long) map.get("mob_seed");
            mobs = Mob.genMobs(8 * Mob.getLocationLevelsPerBoss(), mobSeed);
            List<Map<String, Object>> actions = (List<Map<String, Object>>) map.get("actions");
            Map<Integer, List<Player.Upgrade>> upgradeMap = mapUpgrades(actions);

            player = new Player();

            for(int i = 0; i < (int) map.get("location_level") - 1; i++){
                if(upgradeMap.containsKey(i)){
                    for(Player.Upgrade upgrade : upgradeMap.get(i)){
                        player.upgradeAbility(upgrade);
                    }
                }

                //Kill mob from this location
                mobs.get(i).kill(player);
            }

        }catch (Exception ignored){
            ignored.printStackTrace();
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
