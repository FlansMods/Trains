package com.flansmod.client.model.trains;

import com.flansmod.client.model.ModelTrack;
import com.flansmod.client.tmt.ModelRendererTurbo;

public class ModelTrackStraight extends ModelTrack 
{
	public ModelTrackStraight(int length, int numSleepers)
	{
		int textureX = 64;
		int textureY = 64;
		
		int scaledLength = length * 16;
		
		trackModel = new ModelRendererTurbo[2 + numSleepers];
		trackModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		trackModel[1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		trackModel[0].addBox(-scaledLength / 2, 2, -9, scaledLength, 2, 1);
		trackModel[1].addBox(-scaledLength / 2, 2, 8, scaledLength, 2, 1);
		
		for(int i = 0; i < numSleepers; i++)
		{
			trackModel[2 + i] = new ModelRendererTurbo(this, 0, 3, textureX, textureY);
			trackModel[2 + i].addBox(-scaledLength / 2 + (float)scaledLength * ((float)i + 0.5F) / (float)numSleepers - 3F, 0, -12, 6, 2, 24);
		}
	}
}
