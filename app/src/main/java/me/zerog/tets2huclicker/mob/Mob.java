package me.zerog.tets2huclicker.mob;

import androidx.annotation.Nullable;

import java.util.Random;

public class Mob {

    private MobType type;
    private int maxHealth;
    private int currHealth;
    private int locationLevel;
    private final int LEVEL_HP_MULT = 10;
    private boolean isAlive = true;

    public Mob(int locationLevel){
        createMob(genType(), genHealth(locationLevel), locationLevel);
    }

    public Mob(int maxHealth, int locationLevel) {
        createMob(genType(), maxHealth, locationLevel);
    }


    public Mob(MobType type, int maxHealth, int locationLevel) {
        createMob(type, maxHealth, locationLevel);
    }

    private void createMob(MobType type, int maxHealth, int locationLevel){
        if(locationLevel <= 0){
            this.locationLevel = 1;
            locationLevel = 1;
        }
        if(maxHealth <= 0){
            maxHealth = 10;
            this.maxHealth = 10;
        }
        this.type = type;
        this.maxHealth = maxHealth;
        this.locationLevel = locationLevel;
        this.currHealth = maxHealth;
        isAlive = true;
    }

    private int genHealth(int locationLevel){
        if(locationLevel <=0) {
            locationLevel = 1;
            this.locationLevel = 1;
        }

        return LEVEL_HP_MULT * locationLevel;
    }

    private void kill(Player killer){
        this.isAlive = false;

        if(killer != null){
            killer.addExp((int) (locationLevel * killer.getExpMult()));
            killer.addMoney((int) (locationLevel * killer.getMoneyMult()));
        }
    }

    public void respawn(){
        createMob(genType(), genHealth(locationLevel), locationLevel);
    }

    public void respawn(int newLevel){
        createMob(genType(), genHealth(locationLevel), newLevel);
    }

    public void incLevel(){
        locationLevel++;
    }

    public void incLevel(int amount){
        locationLevel += amount;
    }

    public boolean isAlive(){
        return isAlive;
    }

    private MobType genType(){
        return MobType.values()[new Random().nextInt(MobType.values().length)];
    }

    /**
     *
     * @param damage
     * @return Returns leftover health
     */
    public int damage(int damage, @Nullable Player attacker){
        if(this.currHealth > damage){
            currHealth -= damage;
            return currHealth;
        }else {
            kill(attacker);
            return 0;
        }
    }

    public int damage(int damage, boolean respawnIfDead, @Nullable Player attacker){
        if(this.currHealth > damage){
            currHealth -= damage;
            return currHealth;
        }else {
            kill(attacker);
            respawn();
            return 0;
        }
    }

    public int damage(int damage, boolean respawnIfDead, int currLevel, @Nullable Player attacker){
        if(this.currHealth > damage){
            currHealth -= damage;
            return currHealth;
        }else {
            kill(attacker);
            respawn(currLevel);
            return 0;
        }
    }

    public int getCurrHealth() {
        return currHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getName(){
        return this.type.getName();
    }

    enum MobType {
        RUMIA("Rumia"),
        CIRNO("Cirno the Wise"),
        DAIYOUSEI("Daiyousei"),
        STAR("Star Sapphire"),
        LUNA("Luna Child"),
        FAIRY("Fairy");

        public String name;

        MobType(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }
    }
}
