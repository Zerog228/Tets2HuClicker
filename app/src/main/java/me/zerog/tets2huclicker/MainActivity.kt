package me.zerog.tets2huclicker

import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import me.zerog.tets2huclicker.mob.Mob

class MainActivity : AppCompatActivity() {
    //Mob
    var locationLevel = 1;
    var player = Player(1, 0, 0, 10);
    var mob = Mob(locationLevel);

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
        mobIcon.setImageResource(mob.icon);

        healthField.text = mob.currHealth.toString();
        nameField.text = mob.name;
        healthBar.setMin(0);
        healthBar.setMax(mob.maxHealth);
        healthBar.setProgress(mob.currHealth)

        expBar.setMin(0);
        expBar.setMax(player.levelUpCost());
        moneyField.setText("0");
        levelField.setText("1");

        val mobButton = findViewById<Button>(R.id.mobClick);
        mobButton.setOnClickListener {
            if(mob.damage(1, locationLevel, player)){
                moneyField.setText(player.money.toString());
                levelField.setText(player.level.toString()+" ("+player.exp+"/"+player.levelUpCost()+")");
                expBar.progress = player.exp;
                expBar.setMax(player.levelUpCost());
                expBar.setMin(player.levelUpCost(player.level - 1));
                mobIcon.setImageResource(mob.icon);

                locationLevel++;
            }

            healthField.setText(mob.currHealth.toString());
            nameField.setText(mob.name);
            healthBar.setProgress(mob.currHealth, true);
            healthBar.setMax(mob.maxHealth);
        };
    }
}