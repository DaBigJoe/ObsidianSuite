package com.dabigjoe.obsidianAPI.render.part.bend;

import java.util.ArrayList;
import java.util.List;

import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

public class BendHelper
{

	/**
	 * Align one list of vertices with another
	 * @param fixed Vertices to align to
	 * @param vertices Vertices to align
	 * @return List of aligned vertices. Given list is also changed.
	 */
    public static List<Vertex> alignVertices(List<Vertex> fixed, List<Vertex> vertices)
    {
        List<Vertex> alignedVertices = new ArrayList<Vertex>();
        for (Vertex v : fixed)
            alignedVertices.add(getClosestVertex(v, vertices));
        vertices.clear();
        vertices.addAll(alignedVertices);
        return alignedVertices;
    }
    
    /**
     * Order vertices so that they are in a 'square' order:
     * 0---1
     * |   |
     * 3---2
     * Changes given list and also returns new list.
     */
    public static List<Vertex> orderVertices(List<Vertex> vertices) {
    	List<Vertex> orderedVertices = new ArrayList<Vertex>();
    	List<Vertex> stillToOrder = new ArrayList<Vertex>();
    	stillToOrder.addAll(vertices);
    	Vertex ref = vertices.get(0);
    	stillToOrder.remove(ref);
    	orderedVertices.add(ref);
    	while(!stillToOrder.isEmpty()) {
        	ref = getClosestVertex(ref, stillToOrder);
        	stillToOrder.remove(ref);
        	orderedVertices.add(ref);
    	} 
        vertices.clear();
        vertices.addAll(orderedVertices);
    	return orderedVertices;
    }

    private static Vertex getClosestVertex(Vertex v, List<Vertex> vertices) {
    	Double min = null;
    	Vertex closest = null;
    	for(Vertex w : vertices) {
    		double f = v.distanceTo(w);
    		if(min == null || f < min) {
    			min = f;
    			closest = w;
    		}
    	}
    	return closest;
	}

    /**
     * Rotate a vertex by a given rotation around a given rotation point.
     */
    public static void rotateVertex(Vertex in, float[] rotationMatrix, Vertex rotationPoint)
    {    	    	
        float[] vector = new float[] {in.x - rotationPoint.x, in.y - rotationPoint.y, in.z - rotationPoint.z};

        in.x = vector[0] * rotationMatrix[0] + vector[1] * rotationMatrix[3] + vector[2] * rotationMatrix[6] + rotationPoint.x;
        in.y = vector[0] * rotationMatrix[1] + vector[1] * rotationMatrix[4] + vector[2] * rotationMatrix[7] + rotationPoint.y;
        in.z = vector[0] * rotationMatrix[2] + vector[1] * rotationMatrix[5] + vector[2] * rotationMatrix[8] + rotationPoint.z;
    }

}

