package com.dabigjoe.obsidianAPI.render.part.bend;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.bend.BendHelper;
import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

public class Bend {

    private Vertex centreOfBend;
    private BendPart bendPart;
	
    public final PartObj parent;
    public final PartObj child;
    
    //The number of bend parents the bend is made up of.
    //Min 5, max 40
    protected static final int bendSplit = 1;
    
    public Bend(PartObj parent, PartObj child, GroupObject bendGroupObject) {
    	this.parent = parent;
    	this.child = child;
        this.centreOfBend = new Vertex(-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2));
    	this.bendPart = new BendPart(child, bendGroupObject);    	
    }
    
    public void move()
    {
        //Get all parents that need compensating for.
        List<PartObj> parents = new ArrayList<PartObj>();
        PartObj p = parent;
        while (p.hasParent())
        {
            p = p.getParent();
            parents.add(0, p);
        }

        //Compensate for all parents.
        for (PartObj q : parents)
            q.move();
    }
    
    public void render()
    {
        GL11.glPushMatrix();
        move();
        bendPart.render(child.createRotationMatrixFromAngles(), centreOfBend);        
        GL11.glPopMatrix();
    }
    
}
