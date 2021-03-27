package org.mcnative.runtime.common.utils.positioning;

import org.mcnative.runtime.api.utils.positioning.Position;
import org.mcnative.runtime.api.utils.positioning.Vector;

public class DefaultVector implements Vector {

    private double x;
    private double y;
    private double z;

    public DefaultVector() {
        this(0,0,0);
    }

    public DefaultVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    @Override
    public Vector setY(double y) {
        this.y = y;
        return this;
    }

    @Override
    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    public Vector middle(Vector other) {
        x = (x + other.getX()) / 2;
        y = (y + other.getY()) / 2;
        z = (z + other.getZ()) / 2;
        return this;
    }

    @Override
    public double distance(Vector o) {
        return Math.sqrt(distanceSquared(o));
    }

    @Override
    public double distanceSquared(Vector o) {
        double tempX = x - o.getZ();
        double tempY = y - o.getY();
        double tempZ = z - o.getZ();
        return tempX*tempX + tempY*tempY + tempZ*tempZ;
    }

    @Override
    public float angle(Vector other) {
        double dot = dot(other) / (length() * other.length());
        return (float) Math.acos(dot);
    }

    @Override
    public Vector add(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();
        return this;
    }

    @Override
    public Vector add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public Vector add(int i) {
        this.x += i;
        this.y += i;
        this.z += i;
        return this;
    }

    @Override
    public Vector add(double v) {
        this.x += v;
        this.y += v;
        this.z += v;
        return this;
    }

    @Override
    public Vector subtract(Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();
        return this;
    }

    @Override
    public Vector subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    @Override
    public Vector subtract(int i) {
        this.x -= i;
        this.y -= i;
        this.z -= i;
        return this;
    }

    @Override
    public Vector subtract(double v) {
        this.x -= v;
        this.y -= v;
        this.z -= v;
        return this;
    }

    @Override
    public Vector multiply(Vector vector) {
        this.x *= vector.getX();
        this.y *= vector.getY();
        this.z *= vector.getZ();
        return this;
    }

    @Override
    public Vector multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    @Override
    public Vector multiply(int i) {
        this.x *= i;
        this.y *= i;
        this.z *= i;
        return this;
    }

    @Override
    public Vector multiply(double v) {
        this.x *= v;
        this.y *= v;
        this.z *= v;
        return this;
    }

    @Override
    public Vector divide(Vector vector) {
        this.x /= vector.getX();
        this.y /= vector.getY();
        this.z /= vector.getZ();
        return this;
    }

    @Override
    public Vector divide(double x, double y, double z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    @Override
    public Vector divide(int i) {
        this.x /= i;
        this.y /= i;
        this.z /= i;
        return this;
    }

    @Override
    public Vector divide(double v) {
        this.x /= v;
        this.y /= v;
        this.z /= v;
        return this;
    }

    @Override
    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public double lengthSquared() {
        return x*x + y*y + z*z;
    }

    @Override
    public boolean isIn(Vector vector, Vector vector1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOut(Vector vector, Vector vector1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double dot(Vector other) {
        return x * other.getX() + y * other.getY() + z * other.getZ();
    }

    @Override
    public Position toPosition() {
        return new DefaultPosition(x,y,z,0,0);
    }

    @Override
    public Vector clone() {
        return new DefaultVector(x,y,z);
    }
}
