package MCEntityAnimator.animation;

import java.util.ArrayList;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.parts.Part;

/**
 * An actual animation. Comprised of animation parts - sections for each part of the model.
 *
 */
public class AnimationSequence 
{

	private String animationName;
	ArrayList<AnimationPart> animations = new ArrayList<AnimationPart>();
	private float actionPoint = 0.0F;

	public AnimationSequence(String par0Str) 
	{
		this.animationName = par0Str;
	}
	
	public AnimationSequence(String entityName, NBTTagCompound compound) 
	{
		this.loadData(entityName, compound);
	}

	public String getName() 
	{
		return animationName;
	}

	public ArrayList<AnimationPart> getAnimations() 
	{
		return animations;
	}

	public void addAnimation(AnimationPart par0Animation)
	{
		animations.add(par0Animation);
	}

	public void setActionPoint(float time)
	{
		this.actionPoint = time;
	}

	public float getActionPoint()
	{
		return actionPoint;
	}

	/**
	 * Sets all the parts of a model to their rotation at a given time.
	 * The part with name = exceptionPartName will not be rotated.
	 */
	public void animateAll(float time, ModelObj entityModel, String exceptionPartName) 
	{
		for(Part part : entityModel.parts)
		{
			if(!part.getName().equals(exceptionPartName))
			{
				AnimationPart lastAnimation = getLastAnimation(part.getName());
				if(lastAnimation != null && time > lastAnimation.getEndTime())
					lastAnimation.animatePart(lastAnimation.getEndTime() - lastAnimation.getStartTime());
				else
					part.setToOriginalValues();
			}
		}
		for(AnimationPart s : this.animations)
		{
			if(!s.getPart().getName().equals(exceptionPartName) && time >= s.getStartTime() && time <= s.getEndTime())
				s.animatePart(time - s.getStartTime());
		}
	}

	public float getTotalTime() 
	{
		float max = 0.0F;
		for(AnimationPart animation : animations)
		{
			if(animation.getEndTime() > max)
			{
				max = animation.getEndTime();
			}
		}
		return max;
	}	

	/**
	 * Returns the time of the last frame for a given part.
	 */
	private AnimationPart getLastAnimation(String partName)
	{
		AnimationPart lastAnimation = null;
		for(AnimationPart s : this.animations)
		{
			if(s.getPart().getName().equals(partName))
				if(lastAnimation != null)
				{
					if(s.getEndTime() > lastAnimation.getEndTime())
						lastAnimation = s;
				}
				else
					lastAnimation = s;
		}
		return lastAnimation;
	}

	public NBTTagCompound getSaveData() 
	{
		NBTTagCompound sequenceData = new NBTTagCompound();
		NBTTagList animationList = new NBTTagList();
		for(AnimationPart animation : animations)
		{
			animationList.appendTag(animation.getSaveData());
		}
		sequenceData.setTag("Animations", animationList);
		sequenceData.setString("Name", animationName);
		sequenceData.setFloat("ActionPoint", actionPoint);
		return sequenceData;
	}

	public void loadData(String entityName, NBTTagCompound compound) 
	{
		NBTTagList segmentList = compound.getTagList("Animations", 10);
		for(int i = 0; i < segmentList.tagCount(); i++)
		{
			AnimationPart animation = new AnimationPart(entityName, segmentList.getCompoundTagAt(i));
			animations.add(animation);
		}		
		animationName = compound.getString("Name");
		actionPoint= compound.getFloat("ActionPoint");
	}

}
