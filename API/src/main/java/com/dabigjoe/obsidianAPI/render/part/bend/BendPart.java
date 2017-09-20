package com.dabigjoe.obsidianAPI.render.part.bend;

import java.util.ArrayList;
import java.util.List;

import com.dabigjoe.obsidianAPI.render.bend.BezierCurve;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

public class BendPart extends GroupObject {
	
    private List<TextureCoordinate[]> faceTextureCoords;

	public BendPart(GroupObject bendGroupObject, List<Vertex> nearVertices, List<Vertex> farVertices) {
		super("", 4);
		this.faceTextureCoords = bendGroupObject.getRescaledTextureCoords(nearVertices, farVertices);
		
		
//		for (int i = 0; i < 4; i++)
//        {
//            int j = i == 3 ? 0 : i + 1;
//            Vertex vA = nearVertices.get(i);
//            Vertex vB = nearVertices.get(j);
//            Vertex vC = farVertices.get(j);
//            Vertex vD = farVertices.get(i);
//            Face f = new Face();
//            f.vertices = new Vertex[] {vA, vB, vC, vD};
//            f.faceNormal = f.calculateFaceNormal();
//            faces.add(f);
//        }
//		
//		
//		
//		System.out.println("Face");
//		System.out.println(nearVertices.get(0).x + ", " + nearVertices.get(0).y + ", " + nearVertices.get(0).z);
//		System.out.println(nearVertices.get(1).x + ", " + nearVertices.get(1).y + ", " + nearVertices.get(1).z);
//		System.out.println(farVertices.get(1).x + ", " + farVertices.get(1).y + ", " + farVertices.get(1).z);
//		System.out.println(farVertices.get(0).x + ", " + farVertices.get(0).y + ", " + farVertices.get(0).z);
//		System.out.println("Normal");
//		Face f = new Face();
//        f.vertices = new Vertex[] {nearVertices.get(0), nearVertices.get(1), farVertices.get(1), farVertices.get(0)};
//		System.out.println(f.calculateFaceNormal().x + ", " + f.calculateFaceNormal().y + ", " + f.calculateFaceNormal().z);
//		System.out.println("Texture Coords");
//		for(int i = 0; i < faceTextureCoords.get(1).length; i++)
//			System.out.println(faceTextureCoords.get(1)[i].u + ", " + faceTextureCoords.get(1)[i].v);
	}
	
	public void update(BezierCurve[] curves, float t1, float t2) {
		//System.out.println(t1 + " " + t2);
		this.faces.clear();
		List<Vertex> nearVertices = generateVertices(curves, t1);
		List<Vertex> farVertices = generateVertices(curves, t2);
        for (int i = 0; i < 4; i++)
        {
            int j = i == 3 ? 0 : i + 1;

            Vertex vA = nearVertices.get(i);
            Vertex vB = nearVertices.get(j);
            Vertex vC = farVertices.get(j);
            Vertex vD = farVertices.get(i);
            Face f = new Face();
            f.vertices = new Vertex[] {vA, vB, vC, vD};
            f.faceNormal = f.calculateFaceNormal();
            f.textureCoordinates = faceTextureCoords.get(i);
            faces.add(f);
        }
	}
	
    private List<Vertex> generateVertices(BezierCurve[] curves, float t)
    {
    	List<Vertex> vertices = new ArrayList<Vertex>();
        for (int i = 0; i < curves.length; i++)
            vertices.add(curves[i].getVertexOnCurve(t));
        return vertices;
    }
	
}
