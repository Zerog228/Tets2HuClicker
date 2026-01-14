package me.zerog.tets2huclicker;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Player {

    private String name;
    private int location_level = 1;
    private int level;
    private int upgrade_points = 0;
    private int exp;
    private int money;
    private int health;
    private int bombs;
    private final Map<Upgrade, Integer> upgrades = Arrays.stream(Upgrade.values()).collect(Collectors.toMap(value -> value, value -> 0));

    private int last_mob_leftover_hp = 100;
    private String last_mob_type = "Zun";

    private static final int LEVEL_INCREASE_COST_MULT = 20;

    public static final String DEF_NAME = "Reimu";
    public static final int DEF_LEVEL = 1, DEF_EXP = 0, DEF_MONEY = 0, DEF_HEALTH = 10, DEF_BOMBS = 3, DEF_LOCATION_LEVEL = 0;

    public Player(){
        this(DEF_LEVEL, DEF_EXP, DEF_MONEY, DEF_HEALTH);
    }

    public Player(int level, int exp, int money, int health) {
        this(DEF_NAME, DEF_LOCATION_LEVEL, level, exp, money, health, new HashMap<>(), 0);
    }

    public Player(String name, int level, int exp, int money, int health) {
        this(name, DEF_LOCATION_LEVEL, level, exp, money, health, new HashMap<>(), 0);
    }

    public Player(int level, int exp, int money, int health, @Nullable Map<Upgrade, Integer> upgrades){
        this(DEF_NAME, DEF_LOCATION_LEVEL, level, exp, money, health, upgrades, DEF_BOMBS);
    }

    public Player(String name, int location_level, int level, int exp, int money, int health, @Nullable Map<Upgrade, Integer> upgrades, int bombs){
        this.name = name;

        if(level > DEF_LEVEL){
            level = DEF_LEVEL;
        }
        if(exp < DEF_EXP){
            exp = DEF_EXP;
        }
        if(health < DEF_HEALTH){
            health = DEF_HEALTH;
        }

        this.level = level;
        this.exp = exp;
        this.money = money;
        this.health = health;

        this.bombs = bombs;

        setUpgrades(upgrades);
    }

    public static Player copyOf(Player player){
        Player copy = new Player(player.name, player.getLocationLevel(), player.level, player.exp, player.money, player.health, player.upgrades, player.getBombs());
        copy.location_level = player.location_level;
        copy.upgrade_points = player.upgrade_points;
        return copy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    private void addLevel(int amount){
        this.level += amount;
        this.upgrade_points++;
    }

    public int getExp() {
        return exp;
    }

    public float getExpPercent() {
        return ((float) exp) / levelUpCost();
    }


    public void addExp(int amount){
        exp += amount;
        while(exp >= levelUpCost()){
            addLevel(1);
        }
    }

    public void forceSetExpAndLevel(int level, int exp){
        this.level = level;
        this.exp = exp;
    }

    public void setUpgradePoints(int upgrade_points) {
        this.upgrade_points = upgrade_points;
    }

    public int getUpgradePoints() {
        return upgrade_points;
    }

    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    /**@return Returns Bombs left*/
    public int increaseBombs() {
        return ++bombs;
    }

    /**@return Returns Bombs left*/
    public int decreaseBombs() {
        return --bombs;
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
        return LEVEL_INCREASE_COST_MULT * level * level;
    }

    public int levelUpCost(int lvl){
        return LEVEL_INCREASE_COST_MULT * lvl * lvl;
    }

    public float getMoneyMult() {
        return 1 + (this.upgrades.get(Upgrade.MORE_MONEY) * Upgrade.MORE_MONEY.getAbilityPower());
    }

    public float getExpMult() {
        return 1 + (this.upgrades.get(Upgrade.MORE_EXP) * Upgrade.MORE_EXP.getAbilityPower());
    }

    public int getLocationLevel() {
        return location_level;
    }

    public void increaseLocationLevel(){
        this.location_level++;
    }

    public void setLocationLevel(int location_level) {
        this.location_level = location_level;
    }

    public boolean upgradeAbility(Upgrade upgradeType){
        try {
            int upgrade_level = upgrades.get(upgradeType);
            if(upgrade_level < upgradeType.getMaxLevel()){
                if (removeMoney((upgrade_level + 1) * upgradeType.getCost() + upgrade_level * upgradeType.getAdditionalCostPerLevel())) {
                    upgrades.put(upgradeType, ++upgrade_level);
                    return true;
                } else {
                    return false;
                }
            }else {
                return false;
            }
        }catch (Exception e){
            e.fillInStackTrace();
            return false;
        }
    }

    private void setUpgrades(Map<Upgrade, Integer> upgrades) {
        if (upgrades != null && !upgrades.isEmpty()) {
            for (Upgrade upgrade : this.upgrades.keySet()) {
                if (upgrades.containsKey(upgrade))
                    this.upgrades.put(upgrade, upgrades.get(upgrade));
            }
        }
    }

    public String myUpgradesToString(){
        return upgradesToString(upgrades);
    }

    //Returns default upgrade list
    public static String upgradesToString(){
        return upgradesToString(Arrays.stream(Upgrade.values()).collect(Collectors.toMap(value -> value, value -> 0)));
    }

    public static String upgradesToString(Map<Upgrade, Integer> upgrades){
        return new Gson().toJson(upgrades);

    }


    public static Map<Upgrade, Integer> stringToUpgrades(String upgrades){
        return new Gson().fromJson(upgrades, HashMap.class);
    }


    public enum Upgrade {
        LONGER_STICK(5, 15, 1, 15, 1f), //Damage upgrade
        MORE_EXP(3, 50, 1, 50, 0.1f),
        MORE_MONEY(3, 100, 1, 100, 0.1f);

        private int max_level, cost, cost_in_points, additional_cost_per_level;
        private float ability_power;

        Upgrade(int max_level, int cost, int cost_in_points, int additional_cost_per_level, float ability_power){
            this.max_level = max_level;
            this.cost = cost;
            this.cost_in_points = cost_in_points;
            this.additional_cost_per_level = additional_cost_per_level;

            this.ability_power = ability_power;
        }

        public int getMaxLevel() {
            return max_level;
        }

        public int getCost() {
            return cost;
        }

        public int getCostInPoints() {
            return cost_in_points;
        }

        public int getAdditionalCostPerLevel() {
            return additional_cost_per_level;
        }

        public float getAbilityPower() {
            return ability_power;
        }
    };

}
