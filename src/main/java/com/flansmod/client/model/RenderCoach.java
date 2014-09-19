package com.flansmod.client.model;

import org.lwjgl.opengl.GL11;

import com.flansmod.client.FlansModResourceHandler;
import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.DriveablePart;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.driveables.PilotGun;
import com.flansmod.common.trains.CoachType;
import com.flansmod.common.trains.EntityCoach;
import com.flansmod.common.trains.TrackAlignedPosition;
import com.flansmod.common.trains.TrainSpace;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class RenderCoach extends Render
{
	 public RenderCoach()
    {
        shadowSize = 0.5F;
    }

    public void render(EntityCoach coach, double d, double d1, double d2, float f, float f1)
    {
    	if(!coach.foundCoach)
    		return;
    	
    	bindEntityTexture(coach);
    	CoachType type = coach.getCoachType();
        GL11.glPushMatrix();
        {
	        GL11.glTranslatef((float)d, (float)d1, (float)d2);
	        
	        
	        renderBogie(coach, 0, f1);
	        renderBogie(coach, 1, f1);
	        
	        float dYaw = (coach.axes.getYaw() - coach.prevRotationYaw);
	        for(; dYaw > 180F; dYaw -= 360F) {}
	        for(; dYaw <= -180F; dYaw += 360F) {}
	        float dPitch = (coach.axes.getPitch() - coach.prevRotationPitch);
	        for(; dPitch > 180F; dPitch -= 360F) {}
	        for(; dPitch <= -180F; dPitch += 360F) {}
	        float dRoll = (coach.axes.getRoll() - coach.prevRotationRoll);
	        for(; dRoll > 180F; dRoll -= 360F) {}
	        for(; dRoll <= -180F; dRoll += 360F) {}
	        GL11.glRotatef(180F - coach.prevRotationYaw - dYaw * f1, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(coach.prevRotationPitch + dPitch * f1, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(coach.prevRotationRoll + dRoll * f1, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
	
			float modelScale = type.modelScale;
			GL11.glPushMatrix();
			{
				GL11.glScalef(modelScale, modelScale, modelScale);
				ModelCoach modCoach = (ModelCoach)type.model;
				if(modCoach != null)
					modCoach.render(coach, f1);
			}
			GL11.glPopMatrix();
			
			if(FlansMod.DEBUG)
			{
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1F, 0F, 0F, 0.3F);
				GL11.glScalef(1F, 1F, 1F);
				for(DriveablePart part : coach.getDriveableData().parts.values())
				{
					if(part.box == null)
						continue;
					
					renderAABB(AxisAlignedBB.getBoundingBox(part.box.x / 16F, part.box.y / 16F, part.box.z / 16F, (part.box.x + part.box.w) / 16F, (part.box.y + part.box.h) / 16F, (part.box.z + part.box.d) / 16F));
				}
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1F, 1F, 1F, 1F);
			}
        }
        GL11.glPopMatrix();
    }
    
    public void renderBogie(EntityCoach coach, int i, float f1)
    {
    	TrainSpace space = coach.coach.space;
    	CoachType type = coach.getCoachType();
    	TrackAlignedPosition bogiePosition = coach.coach.bogiePositions[i];
    	TrackAlignedPosition prevBogiePosition = coach.coach.prevBogiePositions[i];
    	
    	GL11.glPushMatrix();
        {
        	double x = prevBogiePosition.getX(space) + (bogiePosition.getX(space) - prevBogiePosition.getX(space)) * f1;
        	double y = prevBogiePosition.getY(space) + (bogiePosition.getY(space) - prevBogiePosition.getY(space)) * f1;
        	double z = prevBogiePosition.getZ(space) + (bogiePosition.getZ(space) - prevBogiePosition.getZ(space)) * f1;
        	GL11.glTranslated(x - coach.posX, y - coach.posY, z - coach.posZ);
        	
	        float dYaw = bogiePosition.getYaw(space) - prevBogiePosition.getYaw(space);
	        for(; dYaw > 180F; dYaw -= 360F) {}
	        for(; dYaw <= -180F; dYaw += 360F) {}
	        float dPitch = bogiePosition.getPitch(space) - prevBogiePosition.getPitch(space);
	        for(; dPitch > 180F; dPitch -= 360F) {}
	        for(; dPitch <= -180F; dPitch += 360F) {}
	        GL11.glRotatef(180F - prevBogiePosition.getYaw(space) - dYaw * f1, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(prevBogiePosition.getPitch(space) + dPitch * f1, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
	
			float modelScale = type.modelScale;
			GL11.glScalef(modelScale, modelScale, modelScale);
			ModelCoach modCoach = (ModelCoach)type.model;
			if(modCoach != null)
				modCoach.renderBogie(coach, i, f1);
        }
        GL11.glPopMatrix();
    }

    @Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        render((EntityCoach)entity, d, d1, d2, f, f1);
    }
    
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return FlansModResourceHandler.getTexture(((EntityCoach)entity).getCoachType());
		
	}
}
