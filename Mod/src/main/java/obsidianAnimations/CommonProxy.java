package obsidianAnimations;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import obsidianAPI.animation.wrapper.FunctionAnimationWrapper;
import obsidianAPI.animation.wrapper.FunctionAnimationWrapper.IsActiveFunction;
import obsidianAPI.registry.AnimationRegistry;

public class CommonProxy
{	
	public void init() 
	{	
		ModEntities.registerEntities();
		registerRendering();
	}

	public void registerRendering() {}

	public void registerAnimations()
	{
		AnimationRegistry.init();
//		AnimationRegistry.registerEntity(EntityDummyPlayer.class, "dummy");
//		AnimationRegistry.registerAnimation("dummy", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"));
		
		AnimationRegistry.registerEntity(EntityPlayer.class, "player");
		
		IsActiveFunction returnTrue = (swingTime, swingMax, clock, lookX, lookY, f5, entity) -> { return true; };

		ResourceLocation playerWalk = new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba");

		try {
			FunctionAnimationWrapper playerWalkWrapper = new FunctionAnimationWrapper(playerWalk, 50, returnTrue);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		AnimationRegistry.registerAnimation("player", "WalkF", new ResourceLocation("mod_obsidian_animations:animations/player/WalkF.oba"));
//		AnimationRegistry.registerAnimation("player", "SprintF", new ResourceLocation("mod_obsidian_animations:animations/player/SprintF.oba"));	
//		AnimationRegistry.registerAnimation("player", "CrouchF", new ResourceLocation("mod_obsidian_animations:animations/player/CrouchF.oba"));	
//		AnimationRegistry.registerAnimation("player", "Jump", new ResourceLocation("mod_obsidian_animations:animations/player/Jump.oba"));	
//		AnimationRegistry.registerAnimation("player", "MovementTest", new ResourceLocation("mod_obsidian_animations:animations/player/MovementTest.oba"));	
//		AnimationRegistry.registerAnimation("player", "Idle", new ResourceLocation("mod_obsidian_animations:animations/player/Idle.oba"));
	}


}



