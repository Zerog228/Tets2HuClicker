package me.zerog.tets2huclicker.view

import android.app.AlertDialog
import android.app.Dialog
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.player.Player
import me.zerog.tets2huclicker.R
import me.zerog.tets2huclicker.player.ServerPlayer
import me.zerog.tets2huclicker.utils.Executable
import me.zerog.tets2huclicker.utils.ProgressManager
import java.lang.Exception


class KMainMenuView : ViewModel() {
    fun showMainMenuView(activity : AppCompatActivity){
        ProgressManager.setCurrentMenuType(ProgressManager.CurrentMenuType.MAIN_MENU)
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
            ProgressManager.setCurrentMenuType(ProgressManager.CurrentMenuType.MAIN_GAME_SCREEN)
            ProgressManager.setGameModeToLocal()
            kInGameView.showInGameView(activity)
        }

        //Reset local player
        reset_local_user_button.setOnClickListener {
            deletePlayerDialog(activity, {
                ProgressManager.resetLocalPlayer(activity)
            })
        }

        //Online player selection
        val select_online_player_button = activity.findViewById<Button>(R.id.select_online_player);
        val refresh_online_player_button = activity.findViewById<Button>(R.id.refresh_online_player);
        val reset_global_user_button = activity.findViewById<Button>(R.id.reset_progress_server_button);
        val global_player_text_view = activity.findViewById<TextView>(R.id.global_player_text_view);
        global_player_text_view.text = ServerPlayer.getPlayerInfo()

        //Online player selection logic
        select_online_player_button.setOnClickListener {
            if(ProgressManager.getOnlinePlayer() != null){
                ProgressManager.setCurrentMenuType(ProgressManager.CurrentMenuType.MAIN_GAME_SCREEN)
                ProgressManager.setGameModeToGlobal();

                kInGameView.showInGameView(activity)
            }
        }

        //Delete user on server
        reset_global_user_button.setOnClickListener {
            deletePlayerDialog(activity, {
                ProgressManager.resetOnlinePlayer(
                    {
                        activity.runOnUiThread {
                            var alert = AlertDialog.Builder(activity)
                                .setTitle("Deleted user")
                                .setMessage("Account was successfully deleted!")
                                .create();
                            alert.show()
                        }
                        global_player_text_view.text = ServerPlayer.getPlayerInfo();
                        null;
                    },
                    {
                        exception ->
                        activity.runOnUiThread {
                            var alert = AlertDialog.Builder(activity)
                                .setTitle("Error")
                                .setMessage(exception.message)
                                .create();
                            alert.show()
                        }
                        null;
                    }
                );
            })
        }

        //Alert dialog
        var loginAlert = AlertDialog.Builder(activity)
            .setTitle("Error on getting user")
            .setMessage("Error")
            .create();

        //User input fields
        val username_input_field = activity.findViewById<TextView>(R.id.name_input_field);
        val password_input_field = activity.findViewById<TextView>(R.id.password_input_field);
        val email_input_field = activity.findViewById<TextView>(R.id.email_input_field);
        val signupButton = activity.findViewById<Button>(R.id.signup_button);
        username_input_field.text = ServerPlayer.getLogin()
        password_input_field.text = ServerPlayer.getPassword()

        ServerPlayer.setUpdatePlayerInfo {
            if(!username_input_field.text.isEmpty()){
                ServerPlayer.getPlayer().name = username_input_field.text.toString();
            }
            global_player_text_view.text = ServerPlayer.getPlayerInfo()
            return@setUpdatePlayerInfo null
        }

        signupButton.setOnClickListener { //TODO Verify all the fields
            val successAction = Executable<HashMap<String, String>, Void> { in_ ->
                activity.runOnUiThread {
                    loginAlert.setTitle(in_?.get("message"))
                    loginAlert.setMessage(username_input_field.text)
                    loginAlert.show()
                }
                ServerPlayer.savePlayerCredentials(username_input_field.text.toString(), password_input_field.text.toString());
                ServerPlayer.signIn()
                null;
            };
            val failAction = Executable<Exception, Void> { in_ ->
                activity.runOnUiThread {
                    loginAlert.setMessage(in_?.message.toString())
                    loginAlert.show()
                }
                //in_?.printStackTrace()
                null;
            };
            ServerPlayer.signUp(username_input_field.text.toString(), password_input_field.text.toString(), email_input_field.text.toString(), successAction, failAction)
        }

        //Logging in
        val loginButton = activity.findViewById<Button>(R.id.login_button);
        loginButton.setOnClickListener {
            if(ServerPlayer.getLogin().isEmpty() && (username_input_field.text.toString().isEmpty() || password_input_field.text.toString().isEmpty())){
                loginAlert.setMessage("Fill credentials first!")
                loginAlert.show();
                return@setOnClickListener;
            }

            val successAction = Executable<HashMap<String, Any>, Void> {
                activity.runOnUiThread {
                    loginAlert.setTitle("Log-in info")
                    loginAlert.setMessage("Logged in!")
                    loginAlert.show()
                }
                ServerPlayer.getPlayer().name = username_input_field.text.toString();
                global_player_text_view.text = getPlayerString(ServerPlayer.getPlayer())
                null;
            };
            val failAction = Executable<Exception, Void> { in_ ->
                activity.runOnUiThread {
                    loginAlert.setMessage(in_?.message.toString())
                    loginAlert.show()
                }
                in_?.printStackTrace()
                null;
            }

            ServerPlayer.signIn(username_input_field.text.toString(), password_input_field.text.toString(), successAction, failAction)
        }

        //refresh
        refresh_online_player_button.setOnClickListener {
            ServerPlayer.signIn(username_input_field.text.toString(), password_input_field.text.toString(), {
                global_player_text_view.text = getPlayerString(ServerPlayer.getPlayer());
                null;
            }, null)
        }
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
            deleteUser();
            alert.dismiss()
        }

        val reject_player_deletion = alert.findViewById<Button>(R.id.reject_deletion_button);
        reject_player_deletion.setOnClickListener { alert.dismiss() }

        alert.show();

    }
}