package me.zerog.tets2huclicker.view

import android.app.Dialog
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.Player
import me.zerog.tets2huclicker.R
import me.zerog.tets2huclicker.utils.ProgressManager


class KMainMenuView : ViewModel() {

    private var menu_type : CurrentMenuType = CurrentMenuType.MAIN_MENU;

    fun getMenu() : CurrentMenuType{
        return menu_type;
    }

    fun showMainMenuView(activity : AppCompatActivity){
        //var online_player : Player = ProgressManager.getOnlinePlayer();

        activity.setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(R.id.main_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var kInGameView : KInGameView = ViewModelProvider(activity).get(KInGameView::class.java)

        //Local user selection
        val select_local_user_button = activity.findViewById<Button>(R.id.select_local_user_button);
        val reset_local_user_button = activity.findViewById<Button>(R.id.reset_progress_local_button);
        val local_player_text_view = activity.findViewById<TextView>(R.id.local_player_text_view);
        local_player_text_view.setText(getPlayerString(ProgressManager.getOfflinePlayer()))

        //Select local player
        select_local_user_button.setOnClickListener {
            menu_type = CurrentMenuType.MAIN_GAME_SCREEN;
            ProgressManager.setGameModeToLocal()
            kInGameView.showInGameView(activity)
        }

        //Reset local player
        reset_local_user_button.setOnClickListener {
            deletePlayerDialog(activity, {
                ProgressManager.resetLocalPlayer()
            })
        }

        //Global user selection
        val select_online_player_button = activity.findViewById<Button>(R.id.select_online_player);
        val refresh_online_player_button = activity.findViewById<Button>(R.id.refresh_online_player);
        val reset_global_user_button = activity.findViewById<Button>(R.id.reset_progress_server_button);
        val global_player_text_view = activity.findViewById<TextView>(R.id.global_player_text_view);
        global_player_text_view.setText(getPlayerString(ProgressManager.getOnlinePlayer(), "Player not found! Try refreshing connection"))

        //refresh
        refresh_online_player_button.setOnClickListener {
            ProgressManager.loadProgressFromServer(ProgressManager.getPlayerID(activity))
            global_player_text_view.setText(getPlayerString(ProgressManager.getOnlinePlayer(), "Player not found! Try refreshing connection"))
        }

        //Online player selection
        select_online_player_button.setOnClickListener {
            if(ProgressManager.getOnlinePlayer() != null){
                menu_type = CurrentMenuType.MAIN_GAME_SCREEN;
                ProgressManager.setGameModeToGlobal();

                kInGameView.showInGameView(activity)
            }
        }

        //Delete user on server
        reset_global_user_button.setOnClickListener {
            deletePlayerDialog(activity, {
                ProgressManager.resetGlobalPlayer();
            })
        }
    }

    enum class CurrentMenuType(){
        MAIN_MENU,
        MAIN_GAME_SCREEN,
        SHOP_SCREEN
    }

    fun getPlayerString(player : Player?) : String{
        if(player != null) {
            return player.name + " " + player.exp + "e " + player.money + "$ " + player.locationLevel + "_ll"
        }else{
            return "Player not found!"
        }
    }

    fun getPlayerString(player : Player?, if_not_found : String) : String{
        if(player != null) {
            return player.name + " " + player.exp + "e " + player.money + "$ " + player.locationLevel + "_ll"
        }else{
            return if_not_found;
        }
    }

    fun deletePlayerDialog(activity: AppCompatActivity, deleteUser: () -> Unit){
        var alert = Dialog(activity)
        alert.setContentView(R.layout.player_deletion_view)
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);

        val confirm_player_deletion = alert.findViewById<Button>(R.id.confirm_deletion_button);
        confirm_player_deletion.setOnClickListener {
            deleteUser;
            alert.dismiss()
        }

        val reject_player_deletion = alert.findViewById<Button>(R.id.reject_deletion_button);
        reject_player_deletion.setOnClickListener { alert.dismiss() }

        alert.show();

    }
}