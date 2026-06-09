package me.zerog.tets2huclicker.utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import me.zerog.tets2huclicker.player.Player;
import me.zerog.tets2huclicker.mob.Mob;
import me.zerog.tets2huclicker.player.LocalPlayer;
import me.zerog.tets2huclicker.player.ServerPlayer;

//TODO Screen with local player is not resetting on progress reset
public class ProgressManager{
    private static final String PLAYER_ID = "P_ID";

    private static Player selected_player;

    private static DataStoreSingleton datastore;

    //App info
    private static GameMode gameMode = GameMode.LOCAL;
    private static CurrentMenuType currentMenuType = CurrentMenuType.MAIN_MENU;

    public static void init(AppCompatActivity activity){
        ServerPlayer.init(activity);
        LocalPlayer.init(activity);
    }

    public static DataStoreSingleton getDatastore(AppCompatActivity activity){
        if(datastore == null){
            datastore = DataStoreSingleton.getInstance(activity);
        }
        return datastore;
    }

    private static Mob genMob(){
        if(gameMode == GameMode.LOCAL){
            return LocalPlayer.getMob();
        }else{
            return ServerPlayer.getMob();
        }
    }

    public static Mob getMob(){
        return genMob();
    }

    @NotNull
    public static GameMode getGameMode() {
        return gameMode;
    }

    public static void setGameModeToLocal(){
        gameMode = GameMode.LOCAL;
        selected_player = LocalPlayer.getPlayer();
    }

    public static void setGameModeToGlobal(){
        gameMode = GameMode.GLOBAL;
        selected_player = ServerPlayer.getPlayer();
    }

    public static void resetLocalPlayer(AppCompatActivity activity){
        LocalPlayer.resetPlayer(activity);
    }

    public static void resetOnlinePlayer(Executable<Void, Void> success, Executable<Exception, String> fail){
        ServerPlayer.sendResetRequest(success, fail);
    }

    public static void saveProgressOnLocal(AppCompatActivity activity){
        LocalPlayer.saveProgress(activity);
    }

    public static void loadProgressFromLocal(AppCompatActivity activity){
        LocalPlayer.loadProgress(activity);
    }

    public static void loadProgressFormServer(AppCompatActivity activity){
        ServerPlayer.signIn();
    }

    @Nullable
    public static Player getOnlinePlayer(){
        return ServerPlayer.getPlayer();
    }

    public static Player getOfflinePlayer(){
        return LocalPlayer.getPlayer();
    }

    public static Player getSelectedPlayer(){
        if(selected_player == null){
            selected_player = new Player();
        }
        return selected_player;
    }

    private static int getPlayerID(AppCompatActivity activity){
        return getDatastore(activity).getOrDefault(PLAYER_ID, 1);
    }

    public static CurrentMenuType getCurrentMenuType(){
        return currentMenuType;
    }

    public static void setCurrentMenuType(CurrentMenuType type){
        currentMenuType = type;
    }

    public enum GameMode{
        LOCAL,
        GLOBAL
    }

    public enum CurrentMenuType{
        MAIN_MENU,
        MAIN_GAME_SCREEN,
        SHOP_SCREEN
    }
}
