package com.publicuhc.pluginframework.util;

import org.bukkit.Location;
import org.bukkit.World;

public class Coordinates {

    private double x,y,z;

    public Coordinates(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location asLocationForWorld(World world)
    {
        return new Location(world, x, y, z);
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }
}
