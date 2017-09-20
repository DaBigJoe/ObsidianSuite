package com.dabigjoe.obsidianAPI.render.part.bend;

import java.util.ArrayList;
import java.util.List;

import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

public class BendHelper
{

    public static List<Vertex> alignVertices(List<Vertex> fixed, List<Vertex> vertices)
    {
        List<Vertex> alignedVertices = new ArrayList<Vertex>();
        for (Vertex v : fixed)
            alignedVertices.add(getClosestVertex(v, vertices));
        vertices.clear();
        vertices.addAll(alignedVertices);
        return alignedVertices;
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

