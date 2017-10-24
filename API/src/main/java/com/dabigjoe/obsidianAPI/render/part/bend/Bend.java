package com.dabigjoe.obsidianAPI.render.part.bend;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

import net.minecraft.entity.Entity;

public class Bend {

    private Vertex centreOfBend;
    protected List<BendPart> bendParts;
	
    public final PartObj parent;
    public final PartObj child;
    
    private List<Vertex> parentNearVertices, parentFarVertices, childNearVertices, childFarVertices;
    
    //The number of bend parents the bend is made up of.
    protected static final int bendSplit = 10;
    
    public Bend(PartObj parent, PartObj child, GroupObject bendGroupObject) {
    	this.parent = parent;
    	this.child = child;
        this.centreOfBend = new Vertex(-child.getRotationPoint(0), -child.getRotationPoint(1), -child.getRotationPoint(2));
    	this.parentNearVertices = parent.groupObj.getIntersectingVertices(bendGroupObject);
    	this.parentFarVertices = parent.groupObj.getNonIntersectingVertices(bendGroupObject);
    	this.childNearVertices = child.groupObj.getIntersectingVertices(bendGroupObject);
    	this.childFarVertices = child.groupObj.getNonIntersectingVertices(bendGroupObject);
    	this.bendParts = new ArrayList<BendPart>();   

    	BendHelper.orderVertices(parentFarVertices);
    	BendHelper.alignVertices(parentFarVertices, parentNearVertices);
    	BendHelper.alignVertices(parentFarVertices, childNearVertices);
    	BendHelper.alignVertices(parentFarVertices, childFarVertices);

    	initBendParts(bendGroupObject);
    }
    
    private void initBendParts(GroupObject bendGroupObject) {
    	float[] dx = new float[4], dy = new float[4], dz = new float[4];
    	for(int i = 0; i < 4; i++) {
    		Vertex parentVertex = parentNearVertices.get(i);
    		Vertex childVertex = childNearVertices.get(i);
        	dx[i] = (childVertex.x - parentVertex.x)/bendSplit;
        	dy[i] = (childVertex.y - parentVertex.y)/bendSplit;
        	dz[i] = (childVertex.z - parentVertex.z)/bendSplit;
    	}
    	
    	for(int i = 0; i < bendSplit; i++) {
			List<Vertex> nearVertices = new ArrayList<Vertex>();
			List<Vertex> farVertices = new ArrayList<Vertex>();
    		for(int j = 0; j < 4; j++) {
        		Vertex parentVertex = parentNearVertices.get(j);
    			nearVertices.add(new Vertex(parentVertex.x + dx[j]*i, parentVertex.y + dy[j]*i, parentVertex.z + dz[j]*i));
    			farVertices.add(new Vertex(parentVertex.x + dx[j]*(i+1), parentVertex.y + dy[j]*(i+1), parentVertex.z + dz[j]*(i+1)));
    		}
    		
    		createBendPart(bendGroupObject, nearVertices, farVertices);
    	}
    }
    
    protected void createBendPart(GroupObject bendGroupObject, List<Vertex> nearVertices, List<Vertex> farVertices) {
		bendParts.add(new BendPart(bendGroupObject, nearVertices, farVertices));
    }
    
    protected void move()
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
    
    public void render(Entity entity)
    {
        GL11.glPushMatrix();
        move();
        
        float[] rotMat = child.createRotationMatrixFromAngles();
        for(int i = 0; i < childNearVertices.size(); i++) {
        	BendHelper.rotateVertex(childNearVertices.get(i), rotMat, centreOfBend);
        	BendHelper.rotateVertex(childFarVertices.get(i), rotMat, centreOfBend);
        }
        BezierCurve[] curves = generateBezierCurves();	
        
        //for(BezierCurve curve : curves)
        //	curve.render();

    	for(int i = 0; i < bendSplit; i++) {
    		BendPart bendPart = bendParts.get(i); 
    		bendPart.update(curves, (float)i/(float)bendSplit, (float)(i+1)/(float)bendSplit);
    		renderBendPart(entity, bendPart, i);
    	}
    	
        for(int i = 0; i < childNearVertices.size(); i++) {
        	childNearVertices.get(i).setToOriginalValues();
        	childFarVertices.get(i).setToOriginalValues();
        	parentNearVertices.get(i).setToOriginalValues();
        	parentFarVertices.get(i).setToOriginalValues();
        }
     
        GL11.glPopMatrix();
    }
    
    /**
     * Render a bend part
     * @param entity - entity being rendered
     * @param bendPart - part to render
     * @param i - Index in bend
     */
    public void renderBendPart(Entity entity, BendPart bendPart, int i) {
        bendPart.render(entity, false, false, parent.modelObj);        
    }
    
    private BezierCurve[] generateBezierCurves()
    {
        BezierCurve[] curves = new BezierCurve[parentFarVertices.size()];
        for (int i = 0; i < parentFarVertices.size(); i++)
        {
            BezierCurve curve = new BezierCurve(parentFarVertices.get(i), parentNearVertices.get(i), childFarVertices.get(i), childNearVertices.get(i), centreOfBend);
            curves[i] = curve;
        }
        return curves;
    }
    
}
