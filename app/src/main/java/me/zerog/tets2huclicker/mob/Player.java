package me.zerog.tets2huclicker.mob;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int level;
    private int upgradePoints = 0;
    private int exp;
    private int money;
    private float moneyMult = 1;
    private float expMult = 1;
    private int health;
    private final List<Upgrades> upgrades = new ArrayList<>();

    private static final int LEVEL_INCREASE_COST_MULT = 3;


    public Player(int level, int exp, int money, int health) {
        if(level > 1){
            level = 1;
        }
        if(exp < 0){
            exp = 0;
        }
        if(health < 1){
            health = 1;
        }

        this.level = level;
        this.exp = exp;
        this.money = money;
        this.health = health;
    }

    public int getLevel() {
        return level;
    }

    private void addLevel(int amount){
        this.level += amount;
        this.upgradePoints += 1;
    }

    public int getExp() {
        return exp;
    }

    public float getExpPercent() {
        return ((float) exp) / levelUpCost();
    }

    public void addExp(int amount){
        //10
        if((this.exp + amount) - levelUpCost() > 0){
            //Вот тут ошибка
            addExp(-levelUpCost(), 1);
        }else {
            this.exp += amount;
        }
    }

    private void addExp(int amount, int accum_lvl){
        //10
        if((this.exp + amount) - levelUpCost(accum_lvl) > 0){
            //Вот тут ошибка
            addLevel(1);
            addExp(-levelUpCost(accum_lvl), accum_lvl);
        }else {
            this.exp += amount;
        }
        addLevel(accum_lvl);
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount){
        this.money += amount;
    }

    public boolean removeMoney(int amount){
        if(money >= amount){
            money -= amount;
            return true;
        }else {
            return false;
        }
    }

    public int getHealth() {
        return health;
    }

    public int levelUpCost(){
        return LEVEL_INCREASE_COST_MULT * level;
    }

    public int levelUpCost(int lvl){
        return LEVEL_INCREASE_COST_MULT * lvl;
    }

    public float getMoneyMult() {
        return moneyMult;
    }

    public float getExpMult() {
        return expMult;
    }

    enum Upgrades {
        LONGER_STICK, //Damage upgrade
        MORE_EXP,
        MORE_MONEY;
    };

}
