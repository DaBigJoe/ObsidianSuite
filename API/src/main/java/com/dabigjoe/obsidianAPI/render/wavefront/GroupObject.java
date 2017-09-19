package com.dabigjoe.obsidianAPI.render.wavefront;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GroupObject
{
    public String name;
    public ArrayList<Face> faces = new ArrayList<Face>();
    public int glDrawingMode;

    public GroupObject()
    {
        this("");
    }

    public GroupObject(String name)
    {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode)
    {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @SideOnly(Side.CLIENT)
    public void render()
    {
        if (faces.size() > 0)
        {
            Tessellator tessellator = Tessellator.getInstance();
            render(tessellator.getBuffer());
            //tessellator.draw();
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(BufferBuilder renderer)
    {
        if (faces.size() > 0)
        {
            for (Face face : faces)
            {
                face.render(renderer);
            }
        }
    }
    
    public List<Vertex> getIntersectingVertices(GroupObject obj) {
    	List<Vertex> intersectingVertices = new ArrayList<Vertex>();
    	List<Vertex> allVertices1 = getAllVertices();
    	List<Vertex> allVertices2 = obj.getAllVertices();
    	for(Vertex v : allVertices1) {
        	for(Vertex w : allVertices2) {
        		if(v.isEquivalent(w)) {
        			System.out.println(v);
        			intersectingVertices.add(v);
        		}
        	}
    	}
    	return intersectingVertices;
    }
    
    private List<Vertex> getAllVertices() {
    	List<Vertex> allVertices = new ArrayList<Vertex>();
    	for(Face f : faces) {
    		for(Vertex v : f.vertices) {
    			//Check for duplicates and add if unique.
    			boolean add = true;
    			for(Vertex w : allVertices) {
    				if(v.isEquivalent(w))
    					add = false;
    			}
    			if(add)
    				allVertices.add(v);
    		}
    	}
    	return allVertices;
    }
}