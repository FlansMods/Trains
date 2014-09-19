package com.flansmod.client.model;

import com.flansmod.client.tmt.ModelRendererTurbo;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.trains.EntityCoach;

import net.minecraft.client.model.ModelBase;

public class ModelCoach extends ModelDriveable
{
	/** bogieModel[0] is the front bogie, bogieModel[1] is the rear bogie */
	public ModelRendererTurbo[][] bogieModel = new ModelRendererTurbo[2][0];
	/** Array indices are [front = 0, rear = 1][axleNum][modelPartNum] */ 
	public ModelRendererTurbo[][][] bogieWheelModel = new ModelRendererTurbo[2][0][0];
	
	@Override
	public void render(EntityDriveable driveable, float f1)
	{
		render(0.0625F, (EntityCoach)driveable, f1);
	}
	
    public void render(float f5, EntityCoach coach, float f)
    {    	
		//Rendering the body
        if(coach.isPartIntact(EnumDriveablePart.core))
        {
	        for(int i = 0; i < bodyModel.length; i++)
	        {
				bodyModel[i].render(f5);
	        }	
        }
    }

	public void renderBogie(EntityCoach coach, int i, float f1) 
	{
		float f5 = 0.0625F;
		for(int j = 0; j < bogieModel[i].length; j++)
		{
			bogieModel[i][j].render(f5);
		}
		
		for(int j = 0; j < bogieWheelModel[i].length; j++)
		{
			for(int k = 0; k < bogieWheelModel[i][j].length; k++)
			{
				bogieWheelModel[i][j][k].rotateAngleX = coach.axleAngle;
				bogieWheelModel[i][j][k].render(f5);
			}
		}
	}
}
