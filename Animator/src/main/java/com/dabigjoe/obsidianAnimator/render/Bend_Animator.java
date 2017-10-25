package com.dabigjoe.obsidianAnimator.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAPI.render.part.bend.Bend;
import com.dabigjoe.obsidianAPI.render.part.bend.BendPart;
import com.dabigjoe.obsidianAPI.render.wavefront.Face;
import com.dabigjoe.obsidianAPI.render.wavefront.GroupObject;
import com.dabigjoe.obsidianAPI.render.wavefront.Vertex;
import com.dabigjoe.obsidianAnimator.render.entity.ModelObj_Animator;

import net.minecraft.entity.Entity;

public class Bend_Animator extends Bend
{
    public Bend_Animator(String name, PartObj parent, PartObj child, GroupObject bendGroupObject)
    {
        super(name, parent, child, bendGroupObject);
    }

    private List<BendPart> getParentBendParts()
    {
        List<BendPart> parentBendParts = new ArrayList<BendPart>();
        for (int i = 0; i < bendSplit / 2; i++)
            parentBendParts.add(bendParts.get(i));
        return parentBendParts;
    }

    private List<BendPart> getChildBendParts()
    {
        List<BendPart> childBendParts = new ArrayList<BendPart>();
        for (int i = bendSplit / 2; i < bendSplit; i++)
            childBendParts.add(bendParts.get(i));
        return childBendParts;
    }

    /**
     * Test to see if a ray insects with the parent part of the bend.
     */
    public Double testRayParent()
    {
        GL11.glPushMatrix();
        move();
        Double t = testRay(RayTrace.getRayTrace(), getParentBendParts());
        GL11.glPopMatrix();
        return t;
    }

    /**
     * Test to see if a ray insects with the child part of the bend.
     */
    public Double testRayChild()
    {
        GL11.glPushMatrix();
        move();
        Double t = testRay(RayTrace.getRayTrace(), getChildBendParts());
        GL11.glPopMatrix();
        return t;
    }

    /**
     * Test to see if a ray insects with the parts of the bend.
     *
     * @return - Minimum distance from p0 to part, null if no intersect exists.
     */
    private Double testRay(RayTrace ray, List<BendPart> bendParts)
    {
        Double min = null;
        for (BendPart bendPart : bendParts)
        {
            for (Face f : bendPart.faces)
            {
                Double d = MathHelper.rayIntersectsFace(ray, f);
                if (d != null && (min == null || d < min))
                    min = d;
            }
        }
        return min;
    }
    
    @Override
    protected void createBendPart(GroupObject bendGroupObject, List<Vertex> nearVertices, List<Vertex> farVertices) {
		bendParts.add(new BendPart_Animator(bendGroupObject, nearVertices, farVertices));
    }
    
    @Override
    public void renderBendPart(Entity entity, BendPart bendPart, int i) {
        boolean mainHighlight = false;
        boolean otherHighlight = false;
    	if (parent.modelObj instanceof ModelObj_Animator)
        {
            ModelObj_Animator parentModel = (ModelObj_Animator) parent.modelObj;
            ModelObj_Animator childModel = (ModelObj_Animator) child.modelObj;
            mainHighlight = i < bendSplit / 2 ? parentModel.isMainHighlight(parent) : childModel.isMainHighlight(child);
            otherHighlight = i < bendSplit / 2 ? parentModel.isPartHighlighted(parent) : childModel.isPartHighlighted(child);
        }
    	bendPart.render(entity, mainHighlight, otherHighlight, parent.modelObj);        
    }
}
