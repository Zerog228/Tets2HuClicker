package me.zerog.tets2huclicker.view

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.R
import me.zerog.tets2huclicker.player.Player
import me.zerog.tets2huclicker.utils.ProgressManager
import kotlin.math.cos

class KShopView : ViewModel() {

    fun showShopView(activity : AppCompatActivity){
        activity.setContentView(R.layout.activity_shop)
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(R.id.shop_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ProgressManager.setCurrentMenuType(ProgressManager.CurrentMenuType.SHOP_SCREEN) //TODO NullPointerException? (setText() on widget)
        var kInGameView : KInGameView = ViewModelProvider(activity).get(KInGameView::class.java)

        val return_button = activity.findViewById<ImageView>(R.id.return_to_battle_image_view);
        return_button.setOnClickListener {
            kInGameView.showInGameView(activity)
        }

        var money_left_text = activity.findViewById<TextView>(R.id.money_left_text_view);
        money_left_text.setText(ProgressManager.getSelectedPlayer().money.toString());

        //First upgrade
        val first_upgrade_text = activity.findViewById<TextView>(R.id.first_upgrade_text_view);
        first_upgrade_text.setText(getUpgradeCost(Player.Upgrade.LONGER_STICK))
        val first_upgrade_button = activity.findViewById<ImageView>(R.id.first_upgr_image_view);
        first_upgrade_button.setOnClickListener {
            ProgressManager.getSelectedPlayer().upgradeAbility(Player.Upgrade.LONGER_STICK)

            money_left_text.setText(ProgressManager.getSelectedPlayer().money.toString())
            first_upgrade_text.setText(getUpgradeCost(Player.Upgrade.LONGER_STICK))
        }

        //Second upgrade
        val second_upgrade_text = activity.findViewById<TextView>(R.id.second_upgrade_text_view);
        second_upgrade_text.setText(getUpgradeCost(Player.Upgrade.MORE_EXP))
        val second_upgrade_button = activity.findViewById<ImageView>(R.id.second_upgr_image_view);
        second_upgrade_button.setOnClickListener {
            ProgressManager.getSelectedPlayer().upgradeAbility(Player.Upgrade.MORE_EXP)

            money_left_text.setText(ProgressManager.getSelectedPlayer().money.toString())
            second_upgrade_text.setText(getUpgradeCost(Player.Upgrade.MORE_EXP))
        }

        //Third upgrade
        val third_upgrade_text = activity.findViewById<TextView>(R.id.third_upgrade_text_view);
        third_upgrade_text.setText(getUpgradeCost(Player.Upgrade.MORE_MONEY))
        val third_upgrade_button = activity.findViewById<ImageView>(R.id.third_upgr_image_view);
        third_upgrade_button.setOnClickListener {
            ProgressManager.getSelectedPlayer().upgradeAbility(Player.Upgrade.MORE_MONEY)

            money_left_text.setText(ProgressManager.getSelectedPlayer().money.toString())
            third_upgrade_text.setText(getUpgradeCost(Player.Upgrade.MORE_MONEY))
        }
    }

    fun getUpgradeCost(upgrade: Player.Upgrade) : String {
        var level = ProgressManager.getSelectedPlayer().getAbilityLevel(upgrade)

        if(level <= upgrade.maxLevel){
            var cost = upgrade.calcCost(level)
            return cost.toString();
        }else{
            return "Max level!";
        }
    }
}