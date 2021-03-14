package org.mcnative.runtime.common.utils.positioning;

import org.mcnative.runtime.api.utils.positioning.Position;
import org.mcnative.runtime.api.utils.positioning.Vector;

public class DefaultPosition extends DefaultVector implements Position {

    private float yaw;
    private float pitch;

    public DefaultPosition() {
        this.yaw = 0;
        this.pitch = 0;
    }

    public DefaultPosition(double x, double y, double z, float yaw, float pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public float getYaw() {
        return yaw;
    }

    @Override
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public Vector getDirection() {
        Vector vector = new DefaultVector();

        double rotX = this.getYaw();
        double rotY = this.getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

        return vector;
    }

    @Override
    public Position setDirection(Vector vector) {
        final double _2PI = 2 * Math.PI;
        final double x = vector.getX();
        final double z = vector.getZ();

        if (x == 0 && z == 0) {
            pitch = vector.getY() > 0 ? -90 : 90;
            return this;
        }

        double theta = Math.atan2(-x, z);
        yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

        double x2 = x*x;
        double z2 = z*z;
        double xz = Math.sqrt(x2 + z2);
        pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));
        return this;
    }

    @Override
    public Position clone() {
        return new DefaultPosition(getX(),getY(),getZ(),yaw,pitch);
    }
}
