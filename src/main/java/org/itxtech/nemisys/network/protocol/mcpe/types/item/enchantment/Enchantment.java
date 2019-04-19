package org.itxtech.nemisys.network.protocol.mcpe.types.item.enchantment;

public class Enchantment implements Cloneable {

    public static final int ID_PROTECTION_ALL = 0;
    public static final int ID_PROTECTION_FIRE = 1;
    public static final int ID_PROTECTION_FALL = 2;
    public static final int ID_PROTECTION_EXPLOSION = 3;
    public static final int ID_PROTECTION_PROJECTILE = 4;
    public static final int ID_THORNS = 5;
    public static final int ID_WATER_BREATHING = 6;
    public static final int ID_WATER_WALKER = 7;
    public static final int ID_WATER_WORKER = 8;
    public static final int ID_DAMAGE_ALL = 9;
    public static final int ID_DAMAGE_SMITE = 10;
    public static final int ID_DAMAGE_ARTHROPODS = 11;
    public static final int ID_KNOCKBACK = 12;
    public static final int ID_FIRE_ASPECT = 13;
    public static final int ID_LOOTING = 14;
    public static final int ID_EFFICIENCY = 15;
    public static final int ID_SILK_TOUCH = 16;
    public static final int ID_DURABILITY = 17;
    public static final int ID_FORTUNE_DIGGING = 18;
    public static final int ID_BOW_POWER = 19;
    public static final int ID_BOW_KNOCKBACK = 20;
    public static final int ID_BOW_FLAME = 21;
    public static final int ID_BOW_INFINITY = 22;
    public static final int ID_FORTUNE_FISHING = 23;
    public static final int ID_LURE = 24;
    public static final int ID_FROST_WALKER = 25;
    public static final int ID_MENDING = 26;
    public static final int ID_BINDING_CURSE = 27;
    public static final int ID_VANISHING_CURSE = 28;
    public static final int ID_TRIDENT_IMPALING = 29;
    public static final int ID_TRIDENT_RIPTIDE = 30;
    public static final int ID_TRIDENT_LOYALTY = 31;
    public static final int ID_TRIDENT_CHANNELING = 32;

    public final int id;

    protected int level = 1;

    public Enchantment(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public Enchantment setLevel(int level) {
        this.level = level;
        return this;
    }

    public int getId() {
        return id;
    }

    @Override
    protected Enchantment clone() {
        try {
            return (Enchantment) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
