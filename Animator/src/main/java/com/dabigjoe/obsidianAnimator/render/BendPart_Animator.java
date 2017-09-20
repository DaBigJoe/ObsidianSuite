package com.dabigjoe.obsidianAnimator.render;

import java.util.List;

import com.dabigjoe.obsidianAPI.render.ModelObj;
import com.dabigjoe.obsidianAPI.render.part.bend.BendPart;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.TextureCoordinate;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;
import com.dabigjoe.obsidianAnimator.render.entity.ModelObj_Animator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class BendPart_Animator extends BendPart
{

    public BendPart_Animator(GroupObject bendGroupObject, List<Vertex> nearVertices, List<Vertex> farVertices) {
		super(bendGroupObject, nearVertices, farVertices);
	}

	/**
     * Change the texture coordinates and texture if the part is highlighted.
     */
    @Override
    public void updateTextureCoordinates(Entity entity, boolean mainHighlight, boolean otherHighlight, ModelObj modelObj)
    {
        boolean useHighlightCoords = true;
        ResourceLocation texture;
        if (mainHighlight)
            texture = ModelObj_Animator.pinkResLoc;
        else if (otherHighlight)
            texture = ModelObj_Animator.whiteResLoc;
        else
        {
            texture = modelObj.getTexture(entity);
            useHighlightCoords = false;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        for (int i = 0; i < faces.size(); i++)
        {
            Face f = faces.get(i);
            if (useHighlightCoords) {
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
                f.textureCoordinates = faceTextureCoords.get(i);
        }
    }
}
