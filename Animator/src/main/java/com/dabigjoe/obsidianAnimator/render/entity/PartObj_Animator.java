package com.dabigjoe.obsidianAnimator.render.entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAnimator.render.MathHelper;
import com.dabigjoe.obsidianAnimator.render.RayTrace;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * One partObj for each 'part' of the model.
 */
public class PartObj_Animator extends PartObj
{

	public PartObj_Animator(ModelObj_Animator modelObject, GroupObject groupObj)
	{
		super(modelObject, groupObj);
	}

	//----------------------------------------------------------------
	// 							 Selection
	//----------------------------------------------------------------

	/**
	 * Test to see if a ray insects with this part.
	 * @param p0 - Point on ray.
	 * @param p1 - Another point on ray.
	 * @return - Minimum distance from p0 to part, null if no intersect exists.
	 */
	public Double testRay()
	{		
		GL11.glPushMatrix();

		//Get all parents that need compensating for.
		List<PartObj> parents = new ArrayList<PartObj>();
		PartObj p = this;
		parents.add(p);
		while(p.hasParent())
		{
			p = p.getParent();
			parents.add(0, p);
		}

		//Compensate for all parents. TODO remove compensate Part rotation method
		for(PartObj q : parents)
			q.move();

		Double min = null;
		for(Face f : groupObj.faces)
		{
			Double d = MathHelper.rayIntersectsFace(RayTrace.getRayTrace(), f);
			if(d != null && (min == null || d < min))
				min = d;
		}
		
		GL11.glPopMatrix();
		return min;	
	}

	//------------------------------------------
	//         Rendering and Rotating
	//------------------------------------------

	/**
	 * Stores the current texture coordinates in default texture coords.
	 * This is required in case a bend is removed, then the texture coords can be restored.
	 * XXX
	 */
	public void setDefaultTCsToCurrentTCs()
	{
		for(Face f : groupObj.faces)
		{
			if(f.textureCoordinates == null)
			{
				f.textureCoordinates = new TextureCoordinate[3];
				for(int i = 0; i < 3; i++)
				{
					f.textureCoordinates[i] = new TextureCoordinate(0, 0);
				}
			}   

			int size = f.textureCoordinates.length;
			TextureCoordinate[] coordsToStore = new TextureCoordinate[size];
			for(int i = 0; i < size; i++)
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);

			defaultTextureCoords.put(f, coordsToStore);
		}
	}

	/**
	 * Change the texture coordinates and texture if the part is highlighted.
	 */
	@Override
	public void updateTextureCoordinates(Entity entity, boolean bindTexture)
	{		
		boolean mainHighlight = ((ModelObj_Animator) modelObj).isMainHighlight(this);
		boolean otherHighlight = ((ModelObj_Animator) modelObj).isPartHighlighted(this);
		boolean useHighlightCoords = true;
		ResourceLocation texture;
		if(mainHighlight)
			texture = ModelObj_Animator.pinkResLoc;
		else if(otherHighlight)
			texture = ModelObj_Animator.whiteResLoc;
		else
		{
			texture = modelObj.getTexture(entity);
			useHighlightCoords = false;
		}

		if(bindTexture)
			Minecraft.getMinecraft().getTextureManager().bindTexture(texture);		

		for(Face f : groupObj.faces)
		{
			if(useHighlightCoords)
			{
				TextureCoordinate[] highlightCoords;
				if(f.vertices.length == 3)
				{
					highlightCoords = new TextureCoordinate[]{
							new TextureCoordinate(0.0F, 0.0F), 
							new TextureCoordinate(0.5F, 0.0F), 
							new TextureCoordinate(0.0F, 0.5F)};
				}
				else
				{
					highlightCoords = new TextureCoordinate[]{
							new TextureCoordinate(0.0F, 0.0F), 
							new TextureCoordinate(0.5F, 0.0F), 
							new TextureCoordinate(0.0F, 0.5F),
							new TextureCoordinate(0.0F, 0.0F)};
				}
				f.textureCoordinates = highlightCoords;
			}
			else
				f.textureCoordinates = defaultTextureCoords.get(f);
		}
	}
}
