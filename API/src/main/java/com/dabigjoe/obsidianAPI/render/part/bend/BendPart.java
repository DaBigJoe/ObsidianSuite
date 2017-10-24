package com.dabigjoe.obsidianAPI.render.part.bend;

import java.util.ArrayList;
import java.util.List;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class BendPart extends GroupObject {
	
    protected List<TextureCoordinate[]> faceTextureCoords;

	public BendPart(GroupObject bendGroupObject, List<Vertex> nearVertices, List<Vertex> farVertices) {
		super("", 4);
		this.faceTextureCoords = bendGroupObject.getRescaledTextureCoords(nearVertices, farVertices);
	}
	
    public void updateTextureCoordinates(Entity entity, boolean mainHighlight, boolean otherHighlight, ModelObj modelObj)
	{
        Minecraft.getMinecraft().getTextureManager().bindTexture(modelObj.getTexture(entity));

		for(int i = 0; i < faces.size(); i++)
		{
			Face f = faces.get(i);
			f.textureCoordinates = faceTextureCoords.get(i);
		}
	}
	
	public void update(BezierCurve[] curves, float t1, float t2) {
		this.faces.clear();
		List<Vertex> nearVertices = generateVertices(curves, t1);
		List<Vertex> farVertices = generateVertices(curves, t2);
		List<Vertex> allVertices = new ArrayList<>();
		allVertices.addAll(nearVertices);
		allVertices.addAll(farVertices);
        for (int i = 0; i < 4; i++)
        {
            int j = i == 3 ? 0 : i + 1;
            Vertex vA = nearVertices.get(i);
            Vertex vB = nearVertices.get(j);
            Vertex vC = farVertices.get(j);
            Vertex vD = farVertices.get(i);
            Face f = new Face(getCentre(allVertices));
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

	public void render(Entity entity, boolean mainHighlight, boolean otherHighlight, ModelObj modelObj) {
		updateTextureCoordinates(entity, mainHighlight, otherHighlight, modelObj);
		super.render();
	}
    
    public Vec3d getCentre(List<Vertex> allVertices) {
		double x=0, y=0, z=0;
		for(Vertex v : allVertices) {
			x += v.x;
			y += v.y;
			z += v.z;
		}
		double size = allVertices.size();
		return new Vec3d(x/size, y/size, z/size);
    }
	
}
