package me.zerog.tets2huclicker;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private String name;
    private int location_level = 1;
    private int level;
    private int upgrade_points = 0;
    private int exp;
    private int money;
    private int health;
    private final Map<Upgrade, Integer> upgrades;

    private static final int LEVEL_INCREASE_COST_MULT = 20;

    public Player(){
        this(1, 0, 0, 10);
    }

    public Player(int level, int exp, int money, int health) {
        this("Reimu", level, exp, money, health, new HashMap<>());
    }

    public Player(String name, int level, int exp, int money, int health) {
        this(name, level, exp, money, health, new HashMap<>());
    }

    public Player(int level, int exp, int money, int health, @Nullable Map<Upgrade, Integer> upgrades){
        this("Reimu", level, exp, money, health, upgrades);
    }

    public Player(String name, int level, int exp, int money, int health, @Nullable Map<Upgrade, Integer> upgrades){
        this.name = name;

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

        this.upgrades = new HashMap<>(Map.of(
                Upgrade.LONGER_STICK, 0,
                Upgrade.MORE_EXP, 0,
                Upgrade.MORE_MONEY, 0
        ));

        setUpgrades(upgrades);
    }

    public static Player copyOf(Player player){
        Player copy = new Player(player.name, player.level, player.exp, player.money, player.health, player.upgrades);
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
                if (removeMoney((upgrade_level + 1) * levelUpCost() + upgrade_level * upgradeType.getAdditionalCostPerLevel())) {
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

    enum Upgrade {
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
