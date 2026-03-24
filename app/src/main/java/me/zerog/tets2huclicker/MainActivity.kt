package me.zerog.tets2huclicker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import me.zerog.tets2huclicker.utils.ProgressManager
import me.zerog.tets2huclicker.view.KInGameView
import me.zerog.tets2huclicker.view.KMainMenuView
import me.zerog.tets2huclicker.view.KShopView

class MainActivity : AppCompatActivity() {
    //TODO Yamaxanadu cheating indicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ProgressManager.init(this);

        var kMainMenuView : KMainMenuView = ViewModelProvider(this).get(KMainMenuView::class.java)
        var kInGameView : KInGameView = ViewModelProvider(this).get(KInGameView::class.java)
        var kShopView : KShopView = ViewModelProvider(this).get(KShopView::class.java);

        when(ProgressManager.getCurrentMenuType()){
            ProgressManager.CurrentMenuType.MAIN_MENU -> kMainMenuView.showMainMenuView(this)
            ProgressManager.CurrentMenuType.MAIN_GAME_SCREEN -> kInGameView.showInGameView(this)
            ProgressManager.CurrentMenuType.SHOP_SCREEN -> kShopView.showShopView(this)

            else -> kMainMenuView.showMainMenuView(this);
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if(ProgressManager.getGameMode() == ProgressManager.GameMode.LOCAL){
            ProgressManager.saveProgressOnLocal(this)
        }
    }
}