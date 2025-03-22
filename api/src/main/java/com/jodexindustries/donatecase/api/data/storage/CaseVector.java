package com.jodexindustries.donatecase.api.data.storage;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import com.jodexindustries.donatecase.api.tools.NumberUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class CaseVector implements Cloneable {
    
    private static final Random random = new Random();

    /**
     * Threshold for fuzzy equals().
     */
    private static final double epsilon = 0.000001;

    /**
     * -- GETTER --
     *  Gets the X component.
     *
     */
    protected double x;
    /**
     * -- GETTER --
     *  Gets the Y component.
     *
     */
    protected double y;
    /**
     * -- GETTER --
     *  Gets the Z component.
     *
     */
    protected double z;

    /**
     * Construct the vector with all components as 0.
     */
    public CaseVector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /**
     * Construct the vector with provided integer components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public CaseVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct the vector with provided double components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public CaseVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct the vector with provided float components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public CaseVector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds a vector to this one
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public CaseVector add(@NotNull CaseVector vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    /**
     * Subtracts a vector from this one.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public CaseVector subtract(@NotNull CaseVector vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    /**
     * Multiplies the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public CaseVector multiply(@NotNull CaseVector vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        return this;
    }

    /**
     * Divides the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public CaseVector divide(@NotNull CaseVector vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
        return this;
    }

    /**
     * Copies another vector
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public CaseVector copy(@NotNull CaseVector vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        return this;
    }

    /**
     * Gets the magnitude of the vector, defined as sqrt(x^2+y^2+z^2). The
     * value of this method is not cached and uses a costly square-root
     * function, so do not repeatedly call this method to get the vector's
     * magnitude. NaN will be returned if the inner result of the sqrt()
     * function overflows, which will be caused if the length is too long.
     *
     * @return the magnitude
     */
    public double length() {
        return Math.sqrt(NumberUtils.square(x) + NumberUtils.square(y) + NumberUtils.square(z));
    }

    /**
     * Gets the magnitude of the vector squared.
     *
     * @return the magnitude
     */
    public double lengthSquared() {
        return NumberUtils.square(x) + NumberUtils.square(y) + NumberUtils.square(z);
    }

    /**
     * Get the distance between this vector and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the vector's magnitude. NaN will be
     * returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other vector
     * @return the distance
     */
    public double distance(@NotNull CaseVector o) {
        return Math.sqrt(NumberUtils.square(x - o.x) + NumberUtils.square(y - o.y) + NumberUtils.square(z - o.z));
    }

    /**
     * Get the squared distance between this vector and another.
     *
     * @param o The other vector
     * @return the distance
     */
    public double distanceSquared(@NotNull CaseVector o) {
        return NumberUtils.square(x - o.x) + NumberUtils.square(y - o.y) + NumberUtils.square(z - o.z);
    }

    /**
     * Gets the angle between this vector and another in radians.
     *
     * @param other The other vector
     * @return angle in radians
     */
    public float angle(@NotNull CaseVector other) {
        double dot = Doubles.constrainToRange(dot(other) / (length() * other.length()), -1.0, 1.0);

        return (float) Math.acos(dot);
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     *
     * @param other The other vector
     * @return this same vector (now a midpoint)
     */
    @NotNull
    public CaseVector midpoint(@NotNull CaseVector other) {
        x = (x + other.x) / 2;
        y = (y + other.y) / 2;
        z = (z + other.z) / 2;
        return this;
    }

    /**
     * Gets a new midpoint vector between this vector and another.
     *
     * @param other The other vector
     * @return a new midpoint vector
     */
    @NotNull
    public CaseVector getMidpoint(@NotNull CaseVector other) {
        double x = (this.x + other.x) / 2;
        double y = (this.y + other.y) / 2;
        double z = (this.z + other.z) / 2;
        return new CaseVector(x, y, z);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public CaseVector multiply(int m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public CaseVector multiply(double m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public CaseVector multiply(float m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    /**
     * Calculates the dot product of this vector with another. The dot product
     * is defined as x1*x2+y1*y2+z1*z2. The returned value is a scalar.
     *
     * @param other The other vector
     * @return dot product
     */
    public double dot(@NotNull CaseVector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Calculates the cross product of this vector with another. The cross
     * product is defined as:
     * <ul>
     * <li>x = y1 * z2 - y2 * z1
     * <li>y = z1 * x2 - z2 * x1
     * <li>z = x1 * y2 - x2 * y1
     * </ul>
     *
     * @param o The other vector
     * @return the same vector
     */
    @NotNull
    public CaseVector crossProduct(@NotNull CaseVector o) {
        double newX = y * o.z - o.y * z;
        double newY = z * o.x - o.z * x;
        double newZ = x * o.y - o.x * y;

        x = newX;
        y = newY;
        z = newZ;
        return this;
    }

    /**
     * Calculates the cross product of this vector with another without mutating
     * the original. The cross product is defined as:
     * <ul>
     * <li>x = y1 * z2 - y2 * z1
     * <li>y = z1 * x2 - z2 * x1
     * <li>z = x1 * y2 - x2 * y1
     * </ul>
     *
     * @param o The other vector
     * @return a new vector
     */
    @NotNull
    public CaseVector getCrossProduct(@NotNull CaseVector o) {
        double x = this.y * o.z - o.y * this.z;
        double y = this.z * o.x - o.z * this.x;
        double z = this.x * o.y - o.x * this.y;
        return new CaseVector(x, y, z);
    }

    /**
     * Converts this vector to a unit vector (a vector with length of 1).
     *
     * @return the same vector
     */
    @NotNull
    public CaseVector normalize() {
        double length = length();

        x /= length;
        y /= length;
        z /= length;

        return this;
    }

    /**
     * Zero this vector's components.
     *
     * @return the same vector
     */
    @NotNull
    public CaseVector zero() {
        x = 0;
        y = 0;
        z = 0;
        return this;
    }

    /**
     * Converts each component of value <code>-0.0</code> to <code>0.0</code>.
     *
     * @return This vector.
     */
    @NotNull
    CaseVector normalizeZeros() {
        if (x == -0.0D) x = 0.0D;
        if (y == -0.0D) y = 0.0D;
        if (z == -0.0D) z = 0.0D;
        return this;
    }

    /**
     * Returns whether this vector is in an axis-aligned bounding box.
     * <p>
     * The minimum and maximum vectors given must be truly the minimum and
     * maximum X, Y and Z components.
     *
     * @param min Minimum vector
     * @param max Maximum vector
     * @return whether this vector is in the AABB
     */
    public boolean isInAABB(@NotNull CaseVector min, @NotNull CaseVector max) {
        return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
    }

    /**
     * Returns whether this vector is within a sphere.
     *
     * @param origin Sphere origin.
     * @param radius Sphere radius
     * @return whether this vector is in the sphere
     */
    public boolean isInSphere(@NotNull CaseVector origin, double radius) {
        return (NumberUtils.square(origin.x - x) + NumberUtils.square(origin.y - y) + NumberUtils.square(origin.z - z)) <= NumberUtils.square(radius);
    }

    /**
     * Returns if a vector is normalized
     *
     * @return whether the vector is normalised
     */
    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - 1) < epsilon;
    }

    /**
     * Rotates the vector around the x axis.
     * <p>
     * This piece of math is based on the standard rotation matrix for vectors
     * in three dimensional space. This matrix can be found here:
     * <a href="https://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">Rotation
     * Matrix</a>.
     *
     * @param angle the angle to rotate the vector about. This angle is passed
     * in radians
     * @return the same vector
     */
    @NotNull
    public CaseVector rotateAroundX(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double y = angleCos * y() - angleSin * z();
        double z = angleSin * y() + angleCos * z();
        return y(y).z(z);
    }

    /**
     * Rotates the vector around the y axis.
     * <p>
     * This piece of math is based on the standard rotation matrix for vectors
     * in three dimensional space. This matrix can be found here:
     * <a href="https://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">Rotation
     * Matrix</a>.
     *
     * @param angle the angle to rotate the vector about. This angle is passed
     * in radians
     * @return the same vector
     */
    @NotNull
    public CaseVector rotateAroundY(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * x() + angleSin * z();
        double z = -angleSin * x() + angleCos * z();
        return x(x).z(z);
    }

    /**
     * Rotates the vector around the z axis
     * <p>
     * This piece of math is based on the standard rotation matrix for vectors
     * in three dimensional space. This matrix can be found here:
     * <a href="https://en.wikipedia.org/wiki/Rotation_matrix#Basic_rotations">Rotation
     * Matrix</a>.
     *
     * @param angle the angle to rotate the vector about. This angle is passed
     * in radians
     * @return the same vector
     */
    @NotNull
    public CaseVector rotateAroundZ(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);

        double x = angleCos * x() - angleSin * y();
        double y = angleSin * x() + angleCos * y();
        return x(x).y(y);
    }

    /**
     * Rotates the vector around a given arbitrary axis in 3 dimensional space.
     *
     * <p>
     * Rotation will follow the general Right-Hand-Rule, which means rotation
     * will be counterclockwise when the axis is pointing towards the observer.
     * <p>
     * This method will always make sure the provided axis is a unit vector, to
     * not modify the length of the vector when rotating. If you are experienced
     * with the scaling of a non-unit axis vector, you can use
     * {@link CaseVector#rotateAroundNonUnitAxis(CaseVector, double)}.
     *
     * @param axis the axis to rotate the vector around. If the passed vector is
     * not of length 1, it gets copied and normalized before using it for the
     * rotation. Please use {@link CaseVector#normalize()} on the instance before
     * passing it to this method
     * @param angle the angle to rotate the vector around the axis
     * @return the same vector
     * @throws IllegalArgumentException if the provided axis vector instance is
     * null
     */
    @NotNull
    public CaseVector rotateAroundAxis(@NotNull CaseVector axis, double angle) throws IllegalArgumentException {
        Preconditions.checkArgument(axis != null, "The provided axis vector was null");

        return rotateAroundNonUnitAxis(axis.isNormalized() ? axis : axis.clone().normalize(), angle);
    }

    /**
     * Rotates the vector around a given arbitrary axis in 3 dimensional space.
     *
     * <p>
     * Rotation will follow the general Right-Hand-Rule, which means rotation
     * will be counterclockwise when the axis is pointing towards the observer.
     * <p>
     * Note that the vector length will change accordingly to the axis vector
     * length. If the provided axis is not a unit vector, the rotated vector
     * will not have its previous length. The scaled length of the resulting
     * vector will be related to the axis vector. If you are not perfectly sure
     * about the scaling of the vector, use
     * {@link CaseVector#rotateAroundAxis(CaseVector, double)}
     *
     * @param axis the axis to rotate the vector around.
     * @param angle the angle to rotate the vector around the axis
     * @return the same vector
     * @throws IllegalArgumentException if the provided axis vector instance is
     * null
     */
    @NotNull
    public CaseVector rotateAroundNonUnitAxis(@NotNull CaseVector axis, double angle) throws IllegalArgumentException {
        Preconditions.checkArgument(axis != null, "The provided axis vector was null");

        double x = x(), y = y(), z = z();
        double x2 = axis.x(), y2 = axis.y(), z2 = axis.z();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = this.dot(axis);

        double xPrime = x2 * dotProduct * (1d - cosTheta)
                + x * cosTheta
                + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * (1d - cosTheta)
                + y * cosTheta
                + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * (1d - cosTheta)
                + z * cosTheta
                + (-y2 * x + x2 * y) * sinTheta;

        return x(xPrime).y(yPrime).z(zPrime);
    }

    /**
     * Gets the floored value of the X component, indicating the block that
     * this vector is contained with.
     *
     * @return block X
     */
    public int blockX() {
        return NumberUtils.floor(x);
    }

    /**
     * Gets the floored value of the Y component, indicating the block that
     * this vector is contained with.
     *
     * @return block y
     */
    public int blockY() {
        return NumberUtils.floor(y);
    }

    /**
     * Gets the floored value of the Z component, indicating the block that
     * this vector is contained with.
     *
     * @return block z
     */
    public int blockZ() {
        return NumberUtils.floor(z);
    }

    /**
     * Checks to see if two objects are equal.
     * <p>
     * Only two Vectors can ever return true. This method uses a fuzzy match
     * to account for floating point errors. The epsilon can be retrieved
     * with epsilon.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CaseVector)) {
            return false;
        }

        CaseVector other = (CaseVector) obj;

        return Math.abs(x - other.x) < epsilon && Math.abs(y - other.y) < epsilon && Math.abs(z - other.z) < epsilon && (this.getClass().equals(obj.getClass()));
    }

    /**
     * Returns a hash code for this vector
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;

        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        return hash;
    }

    /**
     * Get a new vector.
     *
     * @return vector
     */
    @NotNull
    @Override
    public CaseVector clone() {
        try {
            return (CaseVector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    /**
     * Returns this vector's components as x,y,z.
     */
    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

    /**
     * Gets a Location version of this vector with yaw and pitch being 0.
     *
     * @param world The world to link the location to.
     * @return the location
     */
    @NotNull
    public CaseLocation toLocation(@NotNull String world) {
        return new CaseLocation(world, x, y, z);
    }

    /**
     * Gets a Location version of this vector.
     *
     * @param world The world to link the location to.
     * @param yaw The desired yaw.
     * @param pitch The desired pitch.
     * @return the location
     */
    @NotNull
    public CaseLocation toLocation(@NotNull String world, float yaw, float pitch) {
        return new CaseLocation(world, x, y, z, yaw, pitch);
    }

    /**
     * Gets the minimum components of two vectors.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return minimum
     */
    @NotNull
    public static CaseVector getMinimum(@NotNull CaseVector v1, @NotNull CaseVector v2) {
        return new CaseVector(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
    }

    /**
     * Gets the maximum components of two vectors.
     *
     * @param v1 The first vector.
     * @param v2 The second vector.
     * @return maximum
     */
    @NotNull
    public static CaseVector getMaximum(@NotNull CaseVector v1, @NotNull CaseVector v2) {
        return new CaseVector(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
    }

    /**
     * Gets a random vector with components having a random value between 0
     * and 1.
     *
     * @return A random vector.
     */
    @NotNull
    public static CaseVector getRandom() {
        return new CaseVector(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }
}
