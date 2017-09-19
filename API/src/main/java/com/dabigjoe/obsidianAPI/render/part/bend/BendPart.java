package com.dabigjoe.obsidianAPI.render.part.bend;

import java.util.List;

import com.dabigjoe.obsidianAPI.render.bend.BendHelper;
import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;

public class BendPart extends GroupObject {
	
    protected List<TextureCoordinate[]> faceTextureCoords;
    private List<Vertex> verticesToRotate;

	public BendPart(PartObj child, GroupObject groupObject) {
		super("", 4);
		this.faces = groupObject.faces;
		this.verticesToRotate = groupObject.getIntersectingVertices(child.groupObj);	
	}

	public void render(float[] rotationMatrix, Vertex centreOfBend) {
		for(Vertex v : verticesToRotate)
			BendHelper.rotateVertex(v, rotationMatrix, centreOfBend);
		super.render();
		for(Vertex v : verticesToRotate)
			v.setToOriginalValues();
	}
    
    
	
}
