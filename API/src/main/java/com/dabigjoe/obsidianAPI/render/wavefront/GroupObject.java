package com.dabigjoe.obsidianAPI.render.wavefront;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dabigjoe.obsidianAPI.render.part.bend.UVMap;

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
    
    /**
     * Return the list of vertices in this group object that are
     * also in the given group object. 
     */
    public List<Vertex> getIntersectingVertices(GroupObject obj) {
    	List<Vertex> intersectingVertices = new ArrayList<Vertex>();
    	List<Vertex> allVertices1 = getAllVertices();
    	List<Vertex> allVertices2 = obj.getAllVertices();
    	for(Vertex v : allVertices1) {
        	for(Vertex w : allVertices2) {
        		if(v.isEquivalent(w))
        			intersectingVertices.add(v);
        	}
    	}
    	return intersectingVertices;
    }
    
    /**
     * Return the list of vertices in this group object that are
     * also in the given group object. 
     */
    public List<Vertex> getNonIntersectingVertices(GroupObject obj) {
    	List<Vertex> nonIntersectingVertices = getAllVertices();
    	nonIntersectingVertices.removeAll(getIntersectingVertices(obj));
    	System.out.println(nonIntersectingVertices.size());
    	return nonIntersectingVertices;
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

	public List<TextureCoordinate[]> getRescaledTextureCoords(List<Vertex> nearVertices, List<Vertex> farVertices) {
		List<TextureCoordinate[]> rescaledTextCoords = new ArrayList<TextureCoordinate[]>();
		Map<Vertex, UVMap> mapsByNormal = new HashMap<Vertex, UVMap>();
		
		for(int i = 0; i < 4; i++) {
			Face f = faces.get(i+1);			
			UVMap map = new UVMap(f);
			mapsByNormal.put(f.faceNormal, map);
		}
		
		for(int i = 0; i < 4; i++) {
			int j = i == 3 ? 0 : i + 1;
			
			Vertex vA = nearVertices.get(i);
            Vertex vB = nearVertices.get(j);
            Vertex vC = farVertices.get(j);
            Vertex vD = farVertices.get(i);
            Face f = new Face();
            f.vertices = new Vertex[] {vA, vB, vC, vD};
            f.faceNormal = f.calculateFaceNormal();
			
			UVMap map = null;
			boolean inverted = false;
			for(Entry<Vertex, UVMap> entry : mapsByNormal.entrySet()) {
				if(entry.getKey().isEquivalent(f.faceNormal))
					map = entry.getValue();
				else if(entry.getKey().isEquivalent(new Vertex(-f.faceNormal.x, -f.faceNormal.y, -f.faceNormal.z))) {
					map = entry.getValue();
					inverted = true;
				}		
			}
			
			if(map != null) {
				TextureCoordinate[] tcs = new TextureCoordinate[4];
				for(int k = 0; k < 4; k++)
					tcs[k] = map.getCoord(f.vertices[k], inverted);
				if(inverted) {
					TextureCoordinate[] temp = new TextureCoordinate[]{tcs[1], tcs[0], tcs[3], tcs[2]};
					tcs = temp;
				}
				rescaledTextCoords.add(tcs);
			}
			else {
				System.err.println("Could not reslace texture coordinates for normal " + f.faceNormal.x + " " + f.faceNormal.y + " " + f.faceNormal.z);
				rescaledTextCoords.add(faces.get(i+1).textureCoordinates);
			}
			

			
			//For debugging incorrect texture coordinates.
//			for(int j = 0; j < 4; j++) {
//				TextureCoordinate tc = map.getCoord(f.vertices[j]);
//				if(tc.u != f.textureCoordinates[j].u && tc.v != f.textureCoordinates[j].v) {
//					System.out.println("Vertices");
//					for(int k = 0; k < 4; k++)
//						System.out.println(f.vertices[k].x + ", " + f.vertices[k].y + ", " + f.vertices[k].z);
//					System.out.println("Actual tcs");
//					for(int k = 0; k < 4; k++)
//						System.out.println(f.textureCoordinates[k].u + ", " + f.textureCoordinates[k].v);
//					System.out.println("Map tcs");
//					for(int k = 0; k < 4; k++) {
//						TextureCoordinate tc2 = map.getCoord(f.vertices[k]);
//						System.out.println(tc2.u + ", " + tc2.v);
//					}
//					System.out.println("Normal: " + f.faceNormal.x + ", " + f.faceNormal.y + ", " + + f.faceNormal.z);
//					break;
//				}
//				
//			}
		}
//		
//		rescaledTextCoords.clear();
//		for(int i = 0; i < 4; i++) {
//			Face f = faces.get(i+1);			
//			rescaledTextCoords.add(f.textureCoordinates);
//		}
		
		return rescaledTextCoords;
	}
    
}