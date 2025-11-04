package me.zerog.tets2huclicker.mob;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.zerog.tets2huclicker.Player;
import me.zerog.tets2huclicker.R;

public class Mob {

    private MobType type;
    private int maxHealth;
    private int currHealth;
    private int locationLevel;
    private final int LEVEL_HP_MULT = 10;
    private final int LOCATION_LEVELS_PER_BOSS = 100;
    private boolean isAlive = true;

    public Mob(int locationLevel){
        createMob(genType(locationLevel), genHealth(locationLevel), locationLevel);
    }

    public Mob(int maxHealth, int locationLevel) {
        createMob(genType(locationLevel), maxHealth, locationLevel);
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
        if(locationLevel <= 0) {
            locationLevel = 1;
            this.locationLevel = 1;
        }

        return (int) (LEVEL_HP_MULT + locationLevel * 0.1);
    }

    private void kill(Player killer){
        this.isAlive = false;

        if(killer != null){
            killer.addExp((int) (killer.getExpMult() + locationLevel * 0.1));
            killer.addMoney((int) (killer.getMoneyMult() + locationLevel * 0.1));
        }
    }

    public void respawn(int locationLevel){
        createMob(genType(locationLevel), genHealth(locationLevel), locationLevel);
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

    private MobType genType(int locationLevel){
        //Check if boss
        if(locationLevel % LOCATION_LEVELS_PER_BOSS == 0){
            return getBoss(locationLevel);
        }

        //Indoor or Outdoor. First 3 bosses will be outdoor, other 4 - indoor
        if(locationLevel < LOCATION_LEVELS_PER_BOSS * 3){
            return getOutdoorEnemies().get(new Random().nextInt(getOutdoorEnemies().size()));
        }else if(locationLevel < LOCATION_LEVELS_PER_BOSS * 7){
            return getIndoorEnemies().get(new Random().nextInt(getIndoorEnemies().size()));
        }

        //If none passes
        return MobType.values()[new Random().nextInt(MobType.values().length)];
    }

    /**
     *
     * @param damage
     * @return Returns if died
     */
    public boolean damage(int damage, @Nullable Player attacker){
        if(this.currHealth > damage){
            currHealth -= damage;
            return false;
        }else {
            kill(attacker);
            return true;
        }
    }

    /**
     *
     * @param damage
     * @return Returns 'true' if died
     */
    public boolean damage(int damage, int locationLevel, @Nullable Player attacker){
        if(this.currHealth > damage){
            currHealth -= damage;
            return false;
        }else {
            kill(attacker);
            respawn(locationLevel);
            return true;
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

    public int getIcon(){
        return this.type.getIcon();
    }

    private List<MobType> getOutdoorEnemies(){
        return List.of(MobType.DAIYOUSEI, MobType.STAR, MobType.LUNA, MobType.SUNNY, MobType.FAIRY, MobType.KEDAMA);
    }

    private List<MobType> getIndoorEnemies(){
        return List.of(MobType.KEDAMA, MobType.FAIRY_MAID_ONE, MobType.FAIRY_MAID_TWO, MobType.FAIRY_MAID_THREE, MobType.KOAKUMA);
    }

    private List<MobType> getAllBosses(){
        return List.of(MobType.RUMIA, MobType.CIRNO, MobType.MEILING, MobType.PATCHOULI, MobType.SAKUYA, MobType.REMILIA, MobType.FLANDRE, MobType.MIMA);
    }

    private MobType getBoss(int locationLevel){
        if(locationLevel == LOCATION_LEVELS_PER_BOSS)
            return MobType.RUMIA;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 2)
            return MobType.CIRNO;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 3)
            return MobType.MEILING;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 4)
            return MobType.PATCHOULI;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 5)
            return MobType.SAKUYA;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 6)
            return MobType.REMILIA;

        if(locationLevel == LOCATION_LEVELS_PER_BOSS * 7)
            return MobType.FLANDRE;

        return MobType.KEDAMA;
    }


    enum MobType { //TODO Draw textures
        //Outdoor mobs
        DAIYOUSEI("Daiyousei", R.drawable.daiyousei),
        STAR("Star Sapphire", R.drawable.star),
        LUNA("Luna Child", R.drawable.luna),
        SUNNY("Sunny Milk", R.drawable.sunny),
        FAIRY("Fairy", R.drawable.fairy),
        WRIGGLE("Wriggle Nightbug", R.drawable.kedama),

        KEDAMA("Kedama", R.drawable.kedama),

        //Indoor mobs
        FAIRY_MAID_ONE("Maid Fairy", R.drawable.fairy),
        FAIRY_MAID_TWO("Maid Fairy", R.drawable.fairy),
        FAIRY_MAID_THREE("Maid Fairy", R.drawable.fairy),
        KOAKUMA("KOAKUMA", R.drawable.kedama),


        //Bosses
        RUMIA("Rumia", R.drawable.rumia),
        CIRNO("Cirno the Wise", R.drawable.cirno),
        MEILING("Hong Meiling", R.drawable.kedama),
        PATCHOULI("Patchouli Knowledge", R.drawable.kedama),
        SAKUYA("Sakuya Izayoi", R.drawable.kedama),
        REMILIA("Remilia Scarlet", R.drawable.kedama),
        FLANDRE("Flandre Scarlet", R.drawable.kedama),

        MIMA("Mima the Forgotten", R.drawable.kedama),

        ;

        public final String name;
        public float hp_mult, exp_mult, money_mult;
        public final int icon;

        MobType(String name, int icon){
            this.name = name;
            this.icon = icon;
        }

        public String getName(){
            return this.name;
        }

        public int getIcon() {
            return icon;
        }
    }
}
