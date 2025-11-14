package com.jodexindustries.donatecase.api.armorstand;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * EulerAngle is used to represent 3 angles, one for each
 * axis (x, y, z). The angles are in radians
 */
@Getter
@ConfigSerializable
public class EulerAngle {

    /**
     * A EulerAngle with every axis set to 0
     */
    public static final EulerAngle ZERO = new EulerAngle(0, 0, 0);

    @Setting
    private double x;

    @Setting
    private double y;

    @Setting
    private double z;

    /**
     * Creates a EulerAngle with each axis set to the
     * passed angle in radians
     *
     * @param x the angle for the x-axis in radians
     * @param y the angle for the y-axis in radians
     * @param z the angle for the z-axis in radians
     */
    public EulerAngle(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EulerAngle() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /**
     * Return a EulerAngle which is the result of changing
     * the x-axis to the passed angle
     *
     * @param x the angle in radians
     * @return the resultant EulerAngle
     */
    @NotNull
    public EulerAngle x(double x) {
        this.x = x;
        return this;
    }

    /**
     * Return a EulerAngle which is the result of changing
     * the y-axis to the passed angle
     *
     * @param y the angle in radians
     * @return the resultant EulerAngle
     */
    @NotNull
    public EulerAngle y(double y) {
        this.y = y;
        return this;
    }

    /**
     * Return a EulerAngle which is the result of changing
     * the z-axis to the passed angle
     *
     * @param z the angle in radians
     * @return the resultant EulerAngle
     */
    @NotNull
    public EulerAngle z(double z) {
        this.z = z;
        return this;
    }

    /**
     * Creates a new EulerAngle which is the result of adding
     * the x, y, z components to this EulerAngle
     *
     * @param x the angle to add to the x-axis in radians
     * @param y the angle to add to the y-axis in radians
     * @param z the angle to add to the z-axis in radians
     * @return the resultant EulerAngle
     */
    @NotNull
    public EulerAngle add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Creates a new EulerAngle which is the result of subtracting
     * the x, y, z components to this EulerAngle
     *
     * @param x the angle to subtract to the x-axis in radians
     * @param y the angle to subtract to the y-axis in radians
     * @param z the angle to subtract to the z-axis in radians
     * @return the resultant EulerAngle
     */
    @NotNull
    public EulerAngle subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EulerAngle that = (EulerAngle) o;

        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Double.compare(that.z, z) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        return result;
    }

    @Override
    public String toString() {
        return "EulerAngle{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
