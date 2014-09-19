package com.flansmod.client.model.trains;

import com.flansmod.client.model.ModelTrack;
import com.flansmod.client.tmt.ModelRendererTurbo;


public class ModelTrackStraight1 extends ModelTrack
{
	public ModelTrackStraight1() 
	{
		int textureX = 64;
		int textureY = 64;
				
		trackModel = new ModelRendererTurbo[3];
		trackModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		trackModel[1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		trackModel[0].addBox(-8, 2, -9, 16, 2, 1);
		trackModel[1].addBox(-8, 2, 8, 16, 2, 1);
		
		trackModel[2] = new ModelRendererTurbo(this, 0, 3, textureX, textureY);
		trackModel[2].addBox(-3F, 0, -12, 6, 2, 24);
	}
}
