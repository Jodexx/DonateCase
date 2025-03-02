package com.jodexindustries.donatecase.api.data.storage;

import com.jodexindustries.donatecase.api.DCAPI;
import com.jodexindustries.donatecase.api.tools.NumberUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

@Accessors(fluent = true)
@Data
public class CaseLocation implements Cloneable, TypeSerializer<CaseLocation> {

    private String world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public CaseLocation() {}

    public CaseLocation(double x, double y, double z) {
        this(null, x, y, z);
    }

    public CaseLocation(@Nullable String world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public CaseLocation(@Nullable String world, double x, double y, double z, float pitch, float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @Nullable
    public CaseWorld getWorld() {
        return this.world == null ? null : DCAPI.getInstance().getPlatform().getWorld(world);
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 19 * hash + (world != null ? world.hashCode() : 0);
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    @NotNull
    public CaseLocation add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Get the distance between this location and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the location's magnitude. NaN will
     * be returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other location
     * @return the distance
     * @throws IllegalArgumentException for differing worlds
     */
    public double distance(@NotNull CaseLocation o) {
        return Math.sqrt(distanceSquared(o));
    }

    /**
     * Get the squared distance between this location and another.
     *
     * @param o The other location
     * @return the distance
     * @throws IllegalArgumentException for differing worlds
     */
    public double distanceSquared(@NotNull CaseLocation o) {
        if (o.getWorld() == null || getWorld() == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null world");
        } else if (!o.getWorld().equals(getWorld())) {
            throw new IllegalArgumentException("Cannot measure distance between " + getWorld() + " and " + o.getWorld());
        }

        return NumberUtils.square(x - o.x) + NumberUtils.square(y - o.y) + NumberUtils.square(z - o.z);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        CaseLocation other = (CaseLocation) object;

        return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(other.x) &&
                Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y) &&
                Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z) &&
                Objects.equals(world, other.world);
    }

    @Override
    public CaseLocation clone() {
        try {
            return (CaseLocation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public CaseLocation deserialize(Type type, ConfigurationNode node) {
        CaseLocation location = new CaseLocation();
        location.x = node.node("x").getDouble();
        location.y = node.node("y").getDouble();
        location.z = node.node("z").getDouble();
        location.pitch = node.node("pitch").getFloat();
        location.yaw = node.node("yaw").getFloat();
        location.world = node.node("world").getString();
        return location;
    }

    @Override
    public void serialize(Type type, @org.checkerframework.checker.nullness.qual.Nullable CaseLocation obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.node("x").set(obj.x);
        node.node("y").set(obj.y);
        node.node("z").set(obj.z);
        node.node("pitch").set(obj.pitch);
        node.node("yaw").set(obj.yaw);

        CaseWorld world = obj.getWorld();
        if (world != null) node.node("world").set(world.name());
    }

    @Override
    public @org.checkerframework.checker.nullness.qual.Nullable CaseLocation emptyValue(Type specificType, ConfigurationOptions options) {
        return new CaseLocation();
    }

    @Override
    public String toString() {
        return String.format("Position: %f %f %f", x, y, z);
    }

}
