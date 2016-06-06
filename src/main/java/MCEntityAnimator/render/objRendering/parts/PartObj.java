package MCEntityAnimator.render.objRendering.parts;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;

import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.render.objRendering.Bend;
import MCEntityAnimator.render.objRendering.ModelObj;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;

/**
 * One partObj for each 'part' of the model.
 * 
 */
public class PartObj extends Part
{
	private float[] rotationPoint;
	private boolean showModel;

	private HashMap<Face, TextureCoordinate[]> defaultTextureCoords;

	private boolean visible;
	private Bend bend = null;
	public GroupObject groupObj;
	private String displayName;

	public PartObj(ModelObj modelObject, GroupObject groupObj) 
	{
		super(modelObject, (groupObj.name.contains("_") ? groupObj.name.substring(0, groupObj.name.indexOf("_")) : groupObj.name).toLowerCase());
		this.groupObj = groupObj;
		this.displayName = getName();
		defaultTextureCoords = new HashMap<Face, TextureCoordinate[]>();
		updateDefaultTextureCoordinates();
		visible = true;
	}

	//------------------------------------------
	//  			Basics
	//------------------------------------------

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void setShowModel(boolean show)
	{
		showModel = show;
	}

	public boolean getShowModel()
	{
		return showModel;
	}

	public void setRotationPoint(float[] rot) 
	{
		rotationPoint = rot;
	}

	public float getRotationPoint(int i) 
	{
		return rotationPoint[i];
	}

	public float[] getRotationPoint()
	{
		return rotationPoint;
	}

	public void setVisible(boolean bool)
	{
		visible = bool;
	}

	public void setBend(Bend b)
	{
		bend = b;
	}

	public boolean hasBend()
	{
		return bend != null;
	}

	public void removeBend()
	{
		modelObj.removeBend(bend);
		bend.remove();
		bend = null;
	}

	//------------------------------------------
	//  	   Rendering and Rotating
	//------------------------------------------

	public void updateDefaultTextureCoordinates()
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

			TextureCoordinate[] coordsToStore = new TextureCoordinate[3];
			for(int i = 0; i < 3; i++)
			{
				coordsToStore[i] = new TextureCoordinate(f.textureCoordinates[i].u, f.textureCoordinates[i].v);
			}

			defaultTextureCoords.put(f, coordsToStore);
		}
	}

	public void updateTextureCoordinates(boolean highlight, boolean main)
	{
		float u = 0.0F;
		float v = 0.0F;
		if(highlight)
		{
			if(main)
			{
				u = 0.0F;
				v = 0.75F;
			}
			else
			{
				u = 0.75F;
				v = 0.0F;
			}
		}


		if(!modelObj.renderWithTexture)
		{
			for(Face f : groupObj.faces)
			{
				TextureCoordinate texCo = new TextureCoordinate(u, v);
				f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
			}
		}
		else
		{
			if(highlight)
			{
				for(Face f : groupObj.faces)
				{
					TextureCoordinate texCo = new TextureCoordinate(0.0F, 0.0F);
					f.textureCoordinates = new TextureCoordinate[]{texCo, texCo, texCo};
				}
			}
			else
			{
				for(Face f : groupObj.faces)
				{
					f.textureCoordinates = defaultTextureCoords.get(f);
				}
			}			
		}
	}

	public void render(Entity entity, boolean highlight, boolean main) 
	{
		updateTextureCoordinates(highlight, main);

		GL11.glPushMatrix();
		move(entity);
		if(visible)
		{
			groupObj.render();
		}
		GL11.glPopMatrix();
	}

	/**
	 * Allows the changing of Angles after a box has been rendered
	 */
	@SideOnly(Side.CLIENT)
	public void postRender(float p_78794_1_)
	{
		if (this.valueX == 0.0F && this.valueY == 0.0F && this.valueZ == 0.0F)
		{
			if (this.rotationPoint[0] != 0.0F || this.rotationPoint[1] != 0.0F || this.rotationPoint[2] != 0.0F)
			{
				GL11.glTranslatef(this.rotationPoint[0] * p_78794_1_, this.rotationPoint[1] * p_78794_1_, this.rotationPoint[2] * p_78794_1_);
			}
		}
		else
		{
			GL11.glTranslatef(this.rotationPoint[0] * p_78794_1_, this.rotationPoint[1] * p_78794_1_, this.rotationPoint[2] * p_78794_1_);

			if (this.valueZ != 0.0F)
			{
				GL11.glRotatef(this.valueZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
			}

			if (this.valueY != 0.0F)
			{
				GL11.glRotatef(this.valueY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
			}

			if (this.valueX != 0.0F)
			{
				GL11.glRotatef(this.valueX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
			}
		}
	}


	@Override
	public void move(Entity entity)
	{
		//Translate part to centre
		GL11.glTranslatef(-rotationPoint[0], -rotationPoint[1], -rotationPoint[2]);

		//Rotate
		GL11.glRotated((valueX - originalValues[0])/Math.PI*180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotated((valueY - originalValues[1])/Math.PI*180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotated((valueZ - originalValues[2])/Math.PI*180.0F, 0.0F, 0.0F, 1.0F);

		//Translate to original position
		GL11.glTranslatef(rotationPoint[0], rotationPoint[1], rotationPoint[2]);	

		//Do for children - rotation for parent compensated for!
		List<PartObj> children = AnimationData.getAnipar(modelObj.getEntityType()).getChildren(this);
		if(children != null)
		{
			for(PartObj child : children)
			{
				child.render(entity, modelObj.isPartHighlighted(child), modelObj.isMainHighlight(child));
			}	
		}
	}


}