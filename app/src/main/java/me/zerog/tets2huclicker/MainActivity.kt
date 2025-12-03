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
import me.zerog.tets2huclicker.view.KInGameView
import me.zerog.tets2huclicker.view.KMainMenuView
import me.zerog.tets2huclicker.view.MainMenuView

class MainActivity : AppCompatActivity() {
    //TODO Текст-вступление у персонажей
    //TODO Экран улучшений
    //TODO If you are cheating, Yamaxanadu will appear in screen corner and will grow in size
    //TODO Предистория?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var kMainMenuView : KMainMenuView = ViewModelProvider(this).get(KMainMenuView::class.java)
        var kInGameView : KInGameView = ViewModelProvider(this).get(KInGameView::class.java)
        //TODO Стартовый экран


        //TODO Сделать енамы для каждого возможного вида экрана и одну общую переменную. Отображать экраны в зависимости от енама
        if(kMainMenuView.in_menu){
            kMainMenuView.showMainMenuView(this)
        }else{
            kInGameView.showInGameView(this)
        }
    }
}