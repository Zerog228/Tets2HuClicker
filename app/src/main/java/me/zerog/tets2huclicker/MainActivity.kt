package me.zerog.tets2huclicker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.mob.Mob
import me.zerog.tets2huclicker.view.InGameView
import me.zerog.tets2huclicker.view.MainMenuView

class MainActivity : AppCompatActivity() {
    //TODO Текст-вступление у персонажей
    //TODO Стартовый экран
    //TODO Экран улучшений
    //TODO If you are cheating, Yamaxanadu will appear in screen corner and will grow in size

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var mainMenuView : MainMenuView = ViewModelProvider(this).get(MainMenuView::class.java)
        var inGameView : InGameView = ViewModelProvider(this).get(InGameView::class.java)

        if(mainMenuView.isInMenu){
            MainMenuView(mainMenuView)
        }else{
            InGameView(inGameView)
        }
    }

    fun MainMenuView(view: MainMenuView){
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val select_local_user_button = findViewById<Button>(R.id.select_local_user_button);
        select_local_user_button.setOnClickListener {
            var inGameView : InGameView = ViewModelProvider(this).get(InGameView::class.java)
            view.exitMenu()
            InGameView(inGameView)
        }
    }

    fun InGameView(view : InGameView){
        var player : Player = view.getPlayer(0)
        var mob : Mob = view.mob;

        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_game)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val healthField = findViewById<TextView>(R.id.mobHealth);
        val nameField = findViewById<TextView>(R.id.mobName);
        val healthBar = findViewById<ProgressBar>(R.id.mobHealthProgressBar);

        val moneyField = findViewById<TextView>(R.id.money_field);
        val levelField = findViewById<TextView>(R.id.level_field);
        val expBar = findViewById<ProgressBar>(R.id.level_progress_bar);

        val mobIcon = findViewById<ImageView>(R.id.mob_image_view);
        mobIcon.setImageResource(mob.icon);

        //Button
        mobIcon.setOnClickListener {
            if(mob.damage(1, player.locationLevel, player)){
                moneyField.setText(player.money.toString());
                levelField.setText(player.level.toString()+" ("+player.exp+"/"+player.levelUpCost()+")");
                expBar.progress = player.exp;
                expBar.setMax(player.levelUpCost());
                expBar.setMin(player.levelUpCost(player.level - 1));
                mobIcon.setImageResource(mob.icon);

                player.increaseLocationLevel()
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
        expBar.setMax(player.levelUpCost());
        moneyField.setText(player.money.toString());
        levelField.setText(player.level.toString()+" ("+player.exp+"/"+player.levelUpCost()+")");
    }
}