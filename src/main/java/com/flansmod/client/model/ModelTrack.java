package com.flansmod.client.model;

import com.flansmod.client.tmt.ModelRendererTurbo;

import net.minecraft.client.model.ModelBase;

public class ModelTrack extends ModelBase 
{
	public ModelRendererTurbo[] trackModel = new ModelRendererTurbo[0];
	/** Each switch controls one set of points. The points each have an off (0) and on (1) model 
	 * The setup here is pointsModel[switchID][off = 0, on = 1][model parts] */
	public ModelRendererTurbo[][][] pointsModel = new ModelRendererTurbo[0][2][0];
	
	public ModelTrack()
	{
	}
	
	public void render(boolean[] switchStates)
	{
		float f5 = 0.0625F;
		for(ModelRendererTurbo model : trackModel)
		{
			model.render(f5);
		}
		
		for(int i = 0; i < pointsModel.length; i++)
		{
			ModelRendererTurbo[] stateToRender = pointsModel[i][0];
			if(i < switchStates.length && switchStates[i])
			{
				stateToRender = pointsModel[i][1];
			}
			for(ModelRendererTurbo model : stateToRender)
			{
				model.render(f5);
			}
		}
	}
}
