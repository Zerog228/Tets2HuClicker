package me.zerog.tets2huclicker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.utils.DataStoreSingleton
import me.zerog.tets2huclicker.utils.ProgressManager
import me.zerog.tets2huclicker.view.KInGameView
import me.zerog.tets2huclicker.view.KMainMenuView

class MainActivity : AppCompatActivity() {
    //TODO Текст-вступление у персонажей
    //TODO Экран улучшений
    //TODO If you are cheating, Yamaxanadu will appear in screen corner and will grow in size
    //TODO Предистория?

    companion object{
        const val PLAYER_ID : Int = 1;
        //val progress_manager : ProgressManager = ProgressManager();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ProgressManager.loadProgressFromServer(PLAYER_ID)

        ProgressManager.saveProgressOnLocal(this)
        ProgressManager.loadProgressFromLocal(this)

        var kMainMenuView : KMainMenuView = ViewModelProvider(this).get(KMainMenuView::class.java)
        var kInGameView : KInGameView = ViewModelProvider(this).get(KInGameView::class.java)

        when(kMainMenuView.getMenu()){
            KMainMenuView.CurrentMenuType.MAIN_MENU -> kMainMenuView.showMainMenuView(this)
            KMainMenuView.CurrentMenuType.MAIN_GAME_SCREEN -> kInGameView.showInGameView(this)

            else -> kMainMenuView.showMainMenuView(this);
        }
    }
}