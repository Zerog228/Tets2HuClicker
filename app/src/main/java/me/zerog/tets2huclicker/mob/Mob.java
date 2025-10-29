package me.zerog.tets2huclicker.mob;

import java.util.Random;

public class Mob {

    private MobType type;
    private int maxHealth;
    private int currHealth;
    private int currLevel;
    private final int LEVEL_HP_MULT = 10;
    private boolean isAlive = true;

    public Mob(int currLevel){
        createMob(genType(), genHealth(currLevel), currLevel);
    }

    public Mob(int maxHealth, int currLevel) {
        createMob(genType(), maxHealth, currLevel);
    }


    public Mob(MobType type, int maxHealth, int currLevel) {
        createMob(type, maxHealth, currLevel);
    }

    private void createMob(MobType type, int maxHealth, int currLevel){
        if(currLevel <= 0){
            this.currLevel = 1;
            currLevel = 1;
        }
        if(maxHealth <= 0){
            maxHealth = 10;
            this.maxHealth = 10;
        }
        this.type = type;
        this.maxHealth = maxHealth;
        this.currLevel = currLevel;
        this.currHealth = maxHealth;
        isAlive = true;
    }

    private int genHealth(int currLevel){
        if(currLevel <=0) {
            currLevel = 1;
            this.currLevel = 1;
        }

        return LEVEL_HP_MULT * currLevel;
    }

    private void kill(){
        this.isAlive = false;
    }

    public void respawn(){
        createMob(genType(), genHealth(currLevel), currLevel);
    }

    public void respawn(int newLevel){
        createMob(genType(), genHealth(currLevel), newLevel);
    }

    public void incLevel(){
        currLevel++;
    }

    public void incLevel(int amount){
        currLevel += amount;
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
    public int damage(int damage){
        if(this.currHealth > damage){
            currHealth -= damage;
            return currHealth;
        }else {
            kill();
            return 0;
        }
    }

    public int damage(int damage, boolean respawnIfDead){
        if(this.currHealth > damage){
            currHealth -= damage;
            return currHealth;
        }else {
            kill();
            respawn();
            return 0;
        }
    }

    public int damage(int damage, boolean respawnIfDead, int currLevel){
        if(this.currHealth > damage){
            currHealth -= damage;
            return currHealth;
        }else {
            kill();
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
