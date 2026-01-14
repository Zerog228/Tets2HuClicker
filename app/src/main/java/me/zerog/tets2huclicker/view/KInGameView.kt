package me.zerog.tets2huclicker.view

import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.MainActivity
import me.zerog.tets2huclicker.Player
import me.zerog.tets2huclicker.R
import me.zerog.tets2huclicker.mob.Mob
import me.zerog.tets2huclicker.security.AntiCheat
import me.zerog.tets2huclicker.utils.ProgressManager
import me.zerog.tets2huclicker.utils.ProgressManager.getMob

class KInGameView() : ViewModel() {

    private var mob: Mob? = null

    fun showInGameView(activity : AppCompatActivity){
        ProgressManager.setCurrentMenuType(ProgressManager.CurrentMenuType.MAIN_GAME_SCREEN)

        var mob : Mob = getMob()

        var kShopView : KShopView = ViewModelProvider(activity).get(KShopView::class.java)

        activity.setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(R.id.main_game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val healthField = activity.findViewById<TextView>(R.id.mobHealth);
        val nameField = activity.findViewById<TextView>(R.id.mobName);
        val healthBar = activity.findViewById<ProgressBar>(R.id.mobHealthProgressBar);

        val moneyField = activity.findViewById<TextView>(R.id.money_field);
        val levelField = activity.findViewById<TextView>(R.id.level_field);
        val expBar = activity.findViewById<ProgressBar>(R.id.level_progress_bar);

        val mobIcon = activity.findViewById<ImageView>(R.id.mob_image_view);
        mobIcon.setImageResource(mob.icon);

        //TODO Image selection based on location level
        val layout = activity.findViewById<ConstraintLayout>(R.id.main_game)
        layout.setBackgroundResource(R.drawable.sdm_background)

        //TODO Bomb logic
        val bomb_button = activity.findViewById<ImageView>(R.id.bomb_image_view)
        bomb_button.setOnClickListener {

        }

        val shop_button = activity.findViewById<ImageView>(R.id.shop_image_view)
        shop_button.setOnClickListener {
            if(ProgressManager.getGameMode() == ProgressManager.GameMode.LOCAL){
                ProgressManager.saveProgressOnLocal(activity)
            }
            kShopView.showShopView(activity)
        }

        //Mob button
        mobIcon.setOnClickListener {
            //Anti-cheat
            AntiCheat.addStamp()

            if(mob.damage(1, ProgressManager.getSelectedPlayer().locationLevel, ProgressManager.getSelectedPlayer())){
                moneyField.setText(ProgressManager.getSelectedPlayer().money.toString());
                levelField.setText(getEXPText(ProgressManager.getSelectedPlayer()));
                expBar.progress = ProgressManager.getSelectedPlayer().exp;
                expBar.setMax(ProgressManager.getSelectedPlayer().levelUpCost());
                expBar.setMin(ProgressManager.getSelectedPlayer().levelUpCost(ProgressManager.getSelectedPlayer().level - 1));
                mobIcon.setImageResource(mob.icon);
                ProgressManager.getSelectedPlayer().increaseLocationLevel()

                ProgressManager.saveProgressOnLocal(activity)
            }

            healthField.setText(mob.currHealth.toString());
            nameField.setText(mob.name);

            healthBar.setMax(mob.maxHealth);
            healthBar.setProgress(mob.currHealth, true);
        }

        healthField.text = mob.currHealth.toString();
        nameField.text = mob.name;
        healthBar.setMin(0);
        healthBar.setMax(mob.maxHealth);
        healthBar.setProgress(mob.currHealth)

        expBar.setMin(0);
        expBar.setMax(ProgressManager.getSelectedPlayer().levelUpCost());
        moneyField.setText(ProgressManager.getSelectedPlayer().money.toString());
        levelField.setText(getEXPText(ProgressManager.getSelectedPlayer()));
    }

    fun getEXPText(player: Player) : String{
        return player.level.toString()+" ("+player.exp+"/"+player.levelUpCost()+")";
    }

}