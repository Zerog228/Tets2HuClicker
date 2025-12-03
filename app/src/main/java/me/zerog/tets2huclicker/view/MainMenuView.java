package me.zerog.tets2huclicker.view;

import androidx.lifecycle.ViewModel;

public class MainMenuView extends ViewModel {

    private boolean in_menu = true;

    public boolean isInMenu() {
        return in_menu;
    }

    public void setInMenu(boolean in_menu) {
        this.in_menu = in_menu;
    }

    public void enterMenu(){
        this.in_menu = true;
    }

    public void exitMenu(){
        this.in_menu = false;
    }
}
