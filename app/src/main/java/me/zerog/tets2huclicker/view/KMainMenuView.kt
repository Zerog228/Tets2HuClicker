package me.zerog.tets2huclicker.view

import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.MainActivity
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
        select_local_user_button.setOnClickListener {
            menu_type = CurrentMenuType.MAIN_GAME_SCREEN;
            ProgressManager.selectPlayer(ProgressManager.getOfflinePlayer())

            kInGameView.showInGameView(activity)
        }

        //Global user selection
        val select_online_player_button = activity.findViewById<Button>(R.id.select_online_player);
        val refresh_online_player_button = activity.findViewById<Button>(R.id.refresh_online_player);
        val global_player_text_view = activity.findViewById<TextView>(R.id.global_player_text_view);
        global_player_text_view.setText(getPlayerString(ProgressManager.getOnlinePlayer()))

        //refresh
        refresh_online_player_button.setOnClickListener {
            ProgressManager.loadProgressFromServer(MainActivity.PLAYER_ID)
            global_player_text_view.setText(getPlayerString(ProgressManager.getOnlinePlayer()))
        }

        //Online player selection
        //TODO Отображать ошибку если игрок не найден а не начального игрока
        select_online_player_button.setOnClickListener {
            menu_type = CurrentMenuType.MAIN_GAME_SCREEN;
            ProgressManager.selectPlayer(ProgressManager.getOnlinePlayer())

            kInGameView.showInGameView(activity)
        }
    }

    enum class CurrentMenuType(){
        MAIN_MENU,
        MAIN_GAME_SCREEN
    }

    fun getPlayerString(player : Player) : String{
        return player.name+" "+player.exp+"e "+player.money+"$ "+player.locationLevel+"_ll"
    }
}