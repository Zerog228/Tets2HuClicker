package me.zerog.tets2huclicker.view

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.R

class KMainMenuView : ViewModel() {

    var in_menu = true;

    fun isInMenu() : Boolean{
        return in_menu;
    }

    fun setInMenu(in_menu: Boolean) {
        this.in_menu = in_menu
    }

    fun enterMenu() {
        this.in_menu = true
    }

    fun exitMenu() {
        this.in_menu = false
    }

    fun showMainMenuView(activity : AppCompatActivity){
        activity.setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(activity.findViewById(R.id.main_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val select_local_user_button = activity.findViewById<Button>(R.id.select_local_user_button);
        select_local_user_button.setOnClickListener {
            var kInGameView : KInGameView = ViewModelProvider(activity).get(KInGameView::class.java)
            //var inGameView : InGameView = ViewModelProvider(activity).get(InGameView::class.java)
            exitMenu()
            kInGameView.showInGameView(activity)
            //inGameView(inGameView)
        }
    }
}