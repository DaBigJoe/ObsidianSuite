package com.dabigjoe.obsidianAnimator.gui.entitySetup;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.dabigjoe.obsidianAPI.render.part.PartObj;
import com.dabigjoe.obsidianAnimator.gui.entityRenderer.GuiEntityRenderer;

public class EntitySetupGui extends GuiEntityRenderer
{

	private EntitySetupController controller;

	public EntitySetupGui(String entityName, EntitySetupController controller) 
	{
		super(entityName);		
		this.controller = controller;
	}

	@Override 
	public void processRay()
	{
		if(controller.hoverCheckRequired())
			super.processRay();
	}


	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		if(par2 == Keyboard.KEY_ESCAPE)
			controller.close();
		else
			super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(int x, int y, int i) throws IOException 
	{
		super.mouseClicked(x, y, i);
		if(i == 1 && hoveredPart != null) {
			PartObj selectedPartObj = (PartObj) selectedPart;
			PartObj hoveredPartObj = (PartObj) hoveredPart;			
			if(selectedPart != hoveredPart) {
				if(isShiftKeyDown()) {
					controller.getEntityModel().addMerge(selectedPartObj, hoveredPartObj);
					controller.getEntityModel().runMerge();
					controller.refresh();
				}
				else if(isCtrlKeyDown()) {
					controller.attemptCreateBend(selectedPartObj, hoveredPartObj);
				}
				else 
					controller.attemptParent(selectedPartObj, hoveredPartObj);
			}
		}
	}
}
