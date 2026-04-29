package me.zerog.tets2huclicker.mob;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.zerog.tets2huclicker.player.Player;
import me.zerog.tets2huclicker.R;
import me.zerog.tets2huclicker.player.ServerPlayer;
import me.zerog.tets2huclicker.utils.ProgressManager;

import static me.zerog.tets2huclicker.utils.IntercontinentalMobInfo.*;

public class Mob {

    private MobType type;
    private int maxHealth;
    private int currHealth;
    private int locationLevel;
    private final int LEVEL_HP_MULT = 10;
    private static final int LOCATION_LEVELS_PER_BOSS = 20;
    private boolean isAlive = true;

    public Mob(int locationLevel){
        createMob(genType(locationLevel), genHealth(locationLevel), locationLevel);
    }

    public Mob(MobType mobType, int locationLevel){
        createMob(mobType, genHealth(locationLevel, mobType), locationLevel);
    }

    public Mob(int left_health, int locationLevel) {
        createMob(genType(locationLevel), left_health, locationLevel);
    }


    public Mob(String type, int left_health, int locationLevel) {
        try{
            createMob(MobType.valueOf(type), left_health, locationLevel);
        }catch (Exception ignored){
            createMob(genType(locationLevel), genHealth(locationLevel), locationLevel);
        }
    }

    private int getTrueLocLevel(){
        return (int) (locationLevel / LOCATION_LEVELS_PER_BOSS) + 1;
    }

    private void createMob(MobType type, int left_health, int locationLevel){
        if(locationLevel <= 0){
            this.locationLevel = 1;
            locationLevel = 1;
        }
        if(left_health <= 0){
            left_health = 1;
        }
        this.type = type;
        this.maxHealth = genHealth(locationLevel, type);
        this.locationLevel = locationLevel;
        this.currHealth = left_health;
        isAlive = true;
    }

    private int genHealth(int locationLevel){
        if(locationLevel <= 0) {
            this.locationLevel = 1;
        }

        return (int) (LEVEL_HP_MULT * getTrueLocLevel() * type.getHpMult());
    }

    private int genHealth(int locationLevel, MobType type){
        if(locationLevel <= 0) {
            this.locationLevel = 1;
        }

        return (int) (LEVEL_HP_MULT * getTrueLocLevel() * type.getHpMult());
    }

    public void kill(Player killer, boolean sendRequest){
        this.isAlive = false;

        if(killer != null){
            killer.addExp((int) (killer.getExpMult() * getTrueLocLevel() * type.getExpMult()));
            killer.addMoney((int) (killer.getMoneyMult() * getTrueLocLevel() * type.getMoneyMult()));
        }

        //Server-side shenanigans
        if(ProgressManager.getGameMode() == ProgressManager.GameMode.GLOBAL){
            if(type.isBoss() && sendRequest){
                ServerPlayer.sendKillBossRequest();
            }

            //Reset progress on final boss
            if(type == MobType.MIMA){
                ServerPlayer.sendResetRequest(
                        null,
                        fail -> {
                            ProgressManager.setCurrentMenuType(ProgressManager.CurrentMenuType.MAIN_MENU);
                            return null;
                        }
                );
            }
        }
    }

    public void respawn(int locationLevel){
        createMob(genType(locationLevel), genHealth(locationLevel), locationLevel);
    }

    public boolean isAlive(){
        return isAlive;
    }

    public String getType(){
        return type.name();
    }

    private MobType genType(int locationLevel){
        //Check if boss
        if(locationLevel % LOCATION_LEVELS_PER_BOSS == 0){
            type = getBoss(locationLevel);
            return type;
        }

        //Indoor or Outdoor. First 3 bosses will be outdoor, other 4 - indoor
        if(locationLevel < LOCATION_LEVELS_PER_BOSS * 3){
            type = getOutdoorEnemies().get(new Random().nextInt(getOutdoorEnemies().size()));
            return type;
        }else if(locationLevel < LOCATION_LEVELS_PER_BOSS * 7){
            type = getIndoorEnemies().get(new Random().nextInt(getIndoorEnemies().size()));
            return type;
        }

        //If none passes
        type = MobType.values()[new Random().nextInt(MobType.values().length)];
        return type;
    }

    /**
     *
     * @param damage
     * @return Returns if died
     */
    public boolean damage(int damage, @Nullable Player attacker, boolean sendRequest){
        if(this.currHealth > damage){
            currHealth -= damage;
            return false;
        }else {
            kill(attacker, sendRequest);
            return true;
        }
    }

    /**
     *
     * @param damage Amount of damage dealt to mob
     * @param locationLevel Next location for the mob to spawn
     * @return Returns 'true' if died, 'else' otherwise
     */
    public boolean damage(int damage, int locationLevel, @Nullable Player attacker, boolean sendRequest){
        if(this.currHealth > damage){
            currHealth -= damage;
            return false;
        }else {
            kill(attacker, sendRequest);
            respawn(locationLevel + 1);
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

    private static List<MobType> getOutdoorEnemies(){
        return List.of(MobType.DAIYOUSEI, MobType.STAR, MobType.LUNA, MobType.SUNNY, MobType.FAIRY, MobType.KEDAMA, MobType.KAGEROU);
    }

    private static List<MobType> getIndoorEnemies(){
        return List.of(MobType.KEDAMA, MobType.FAIRY_MAID_ONE, MobType.FAIRY_MAID_TWO, MobType.FAIRY_MAID_THREE, MobType.KOAKUMA);
    }

    private static List<MobType> getAllBosses(){
        return List.of(MobType.RUMIA, MobType.CIRNO, MobType.MEILING, MobType.PATCHOULI, MobType.SAKUYA, MobType.REMILIA, MobType.FLANDRE, MobType.MIMA);
    }

    private static MobType getBoss(int locationLevel){
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

    public static int getLocationLevelsPerBoss(){
        return LOCATION_LEVELS_PER_BOSS;
    }

    public static List<Mob> genMobs(int location_level, long seed){
        List<Mob> mobs = new ArrayList<>(location_level);
        Random rand = new Random(seed);
        for(int current_location = 1; current_location <= location_level; current_location++){
            //If boss
            if(current_location % LOCATION_LEVELS_PER_BOSS == 0){
                mobs.add(new Mob(getBoss(current_location), current_location));
                continue;
            }

            //Indoor or Outdoor. First 3 bosses will be outdoor, other 4 - indoor
            if(current_location < LOCATION_LEVELS_PER_BOSS * 3){
                mobs.add(new Mob(getOutdoorEnemies().get(rand.nextInt(getOutdoorEnemies().size())), current_location));
                continue;
            }else if(current_location < LOCATION_LEVELS_PER_BOSS * 7){
                mobs.add(new Mob(getIndoorEnemies().get(rand.nextInt(getIndoorEnemies().size())), current_location));
                continue;
            }

            //If none passes
            MobType type = MobType.values()[rand.nextInt(MobType.values().length)];
            mobs.add(new Mob(type, current_location));
            if(type == MobType.MIMA){
                return mobs;
            }
        }
        mobs.add(new Mob(MobType.MIMA, location_level + 1));

        return mobs;
    }

    enum MobType { //TODO Draw textures
        //Outdoor mobs
        DAIYOUSEI("Daiyousei", R.drawable.daiyousei),
        STAR("Star Sapphire", R.drawable.star),
        LUNA("Luna Child", R.drawable.luna),
        SUNNY("Sunny Milk", R.drawable.sunny),
        FAIRY("Fairy", R.drawable.fairy, FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        WRIGGLE("Wriggle Nightbug", R.drawable.kedama),
        KAGEROU("Kagerou Imaizumi", R.drawable.kagerou), //To add or not to add?

        //Both indoor and outdoor
        KEDAMA("Kedama", R.drawable.kedama, KEDAMA_HEALTH, KEDAMA_EXP, KEDAMA_MONEY),

        //Indoor mobs
        FAIRY_MAID_ONE("Maid Fairy", R.drawable.fairy, FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        FAIRY_MAID_TWO("Maid Fairy", R.drawable.fairy, FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        FAIRY_MAID_THREE("Maid Fairy", R.drawable.fairy, FAIRY_HEALTH, FAIRY_EXP, FAIRY_MONEY),
        KOAKUMA("Koakuma", R.drawable.kedama),
        KOISHI("Koishi", R.drawable.kedama, KOISHI_HEALTH, KOISHI_EXP, KOISHI_MONEY),
        SATORI("Satori", R.drawable.kedama, SATORI_HEALTH, SATORI_EXP, SATORI_MONEY),

        //Bosses
        RUMIA("Rumia", R.drawable.rumia, RUMIA_HEALTH, RUMIA_EXP, RUMIA_MONEY, true),
        CIRNO("Cirno the Wise", R.drawable.cirno, CIRNO_HEALTH, CIRNO_EXP, CIRNO_MONEY, true),
        MEILING("Hong Meiling", R.drawable.kedama, MEILING_HEALTH, MEILING_EXP, MEILING_MONEY, true),
        PATCHOULI("Patchouli Knowledge", R.drawable.kedama, PATCHOULI_HEALTH, PATCHOULI_EXP, PATCHOULI_MONEY, true),
        SAKUYA("Sakuya Izayoi", R.drawable.kedama, SAKUYA_HEALTH, SAKUYA_EXP, SAKUYA_MONEY, true),
        REMILIA("Remilia Scarlet", R.drawable.kedama, REMILIA_HEALTH, REMILIA_EXP, REMILIA_MONEY, true),
        FLANDRE("Flandre Scarlet", R.drawable.kedama, FLANDRE_HEALTH, FLANDRE_EXP, FLANDRE_MONEY, true),

        //Additional bosses
        MIMA("Mima the Forgotten", R.drawable.kedama, MIMA_HEALTH, MIMA_EXP, MIMA_MONEY, true),

        //NPC's
        NITORI("Nitori Kawashiro", R.drawable.nitori), //Merchant in the shop

        ;

        private final String name;
        private float hp_mult, exp_mult, money_mult;
        private final int icon;
        private final boolean boss;

        MobType(String name, int icon){
            this(name, icon, 1, 1, 1);
        }

        MobType(String name, int icon, float hp_mult, float exp_mult, float money_mult){
            this(name, icon, hp_mult, exp_mult, money_mult, false);
        }

        MobType(String name, int icon, float hp_mult, float exp_mult, float money_mult, boolean boss){
            this.name = name;
            this.icon = icon;
            this.hp_mult = hp_mult;
            this.exp_mult = exp_mult;
            this.money_mult = money_mult;

            this.boss = boss;
        }

        public String getName(){
            return this.name;
        }

        public int getIcon() {
            return icon;
        }

        public float getHpMult() {
            return hp_mult;
        }

        public float getExpMult() {
            return exp_mult;
        }

        public float getMoneyMult() {
            return money_mult;
        }

        public boolean isBoss(){
            return boss;
        }
    }
}
