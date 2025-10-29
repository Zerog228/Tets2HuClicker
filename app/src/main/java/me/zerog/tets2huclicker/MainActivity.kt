package me.zerog.tets2huclicker

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import me.zerog.tets2huclicker.mob.Mob
import me.zerog.tets2huclicker.mob.Player

class MainActivity : AppCompatActivity() {
    //Mob
    var locationLevel = 1;

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

        var mob = Mob(locationLevel);
        var player = Player(1, 0, 0, 10);

        healthField.text = mob.currHealth.toString();
        nameField.text = mob.name;
        healthBar.setMin(0);
        healthBar.setMax(mob.maxHealth);
        healthBar.setProgress(mob.currHealth)

        val mobButton = findViewById<Button>(R.id.mobClick);
        mobButton.setOnClickListener {
            mob.damage(1, true, locationLevel, player);

            healthField.setText(mob.currHealth.toString());
            nameField.setText(mob.name);
            healthBar.setProgress(mob.currHealth, true);
            healthBar.setMax(mob.maxHealth);
        };
    }
}