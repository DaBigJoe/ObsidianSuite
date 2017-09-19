package com.dabigjoe.obsidianAPI.render.wavefront;

public class Vertex
{
    public float x, y, z;
    private float originalX, originalY, originalZ;

    public Vertex(float x, float y)
    {
        this(x, y, 0F);
    }

    public Vertex(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    	this.originalX = x;
    	this.originalY = y;
    	this.originalZ = z;
    }
    
    public void setToOriginalValues() {
    	x = originalX;
    	y = originalY;
    	z = originalZ;
    }
    
    public double distanceTo(Vertex v) {
    	double dx = v.x - x;
    	double dy = v.y - y;
    	double dz = v.z - z;
    	return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }
    
    public boolean isEquivalent(Vertex v) {
    	return distanceTo(v) < 0.05F;
    }
    
}