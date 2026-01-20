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
import me.zerog.tets2huclicker.utils.ProgressManager

class KShopView : ViewModel() {

    fun showShopView(activity : AppCompatActivity){
        activity.setContentView(R.layout.activity_shop)
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(R.id.shop_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ProgressManager.setCurrentMenuType(ProgressManager.CurrentMenuType.SHOP_SCREEN)
        var kInGameView : KInGameView = ViewModelProvider(activity).get(KInGameView::class.java)

        val return_button = activity.findViewById<ImageView>(R.id.return_to_battle_image_view);
        return_button.setOnClickListener {
            kInGameView.showInGameView(activity)
        }

        var money_left_text = activity.findViewById<TextView>(R.id.money_left_text_view);
        money_left_text.setText(ProgressManager.getSelectedPlayer().money.toString());

        //First upgrade
        val first_upgrade_text = activity.findViewById<TextView>(R.id.first_upgrade_text_view);
        val first_upgrade_button = activity.findViewById<ImageView>(R.id.first_upgr_image_view);
        first_upgrade_button.setOnClickListener {

        }

        //Second upgrade
        val second_upgrade_text = activity.findViewById<TextView>(R.id.second_upgrade_text_view);
        val second_upgrade_button = activity.findViewById<ImageView>(R.id.second_upgr_image_view);
        second_upgrade_button.setOnClickListener {

        }

        //Third upgrade
        val third_upgrade_text = activity.findViewById<TextView>(R.id.third_upgrade_text_view);
        val third_upgrade_button = activity.findViewById<ImageView>(R.id.third_upgr_image_view);
        third_upgrade_button.setOnClickListener {

        }
    }
}