package me.zerog.tets2huclicker

import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import me.zerog.tets2huclicker.mob.Mob
import me.zerog.tets2huclicker.utils.ProgressManager

class MainActivity : AppCompatActivity() {

    //TODO Текст-вступление у персонажей
    //TODO Стартовый экран
    //TODO Экран улучшений
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        //Load player and mob
        ProgressManager.loadProgress(1);
        var mob = Mob(ProgressManager.getPlayer().level);

        mobIcon.setImageResource(mob.icon);
        mobIcon.setOnClickListener {
            if(mob.damage(1, ProgressManager.getPlayer().locationLevel, ProgressManager.getPlayer())){
                moneyField.setText(ProgressManager.getPlayer().money.toString());
                levelField.setText(ProgressManager.getPlayer().level.toString()+" ("+ProgressManager.getPlayer().exp+"/"+ProgressManager.getPlayer().levelUpCost()+")");
                expBar.progress = ProgressManager.getPlayer().exp;
                expBar.setMax(ProgressManager.getPlayer().levelUpCost());
                expBar.setMin(ProgressManager.getPlayer().levelUpCost(ProgressManager.getPlayer().level - 1));
                mobIcon.setImageResource(mob.icon);

                ProgressManager.getPlayer().increaseLocationLevel()
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
        expBar.setMax(ProgressManager.getPlayer().levelUpCost());
        moneyField.setText("0");
        levelField.setText("1");
    }
}