package me.zerog.tets2huclicker.mob;

public class Boss extends Mob{
    public Boss(int locationLevel) {
        super(locationLevel);
    }

    public Boss(int maxHealth, int locationLevel) {
        super(maxHealth, locationLevel);
    }

    public Boss(MobType type, int maxHealth, int locationLevel) {
        super(type, maxHealth, locationLevel);
    }
}
