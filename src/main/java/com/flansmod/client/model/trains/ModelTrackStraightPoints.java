package com.flansmod.client.model.trains;

import com.flansmod.client.model.ModelTrack;
import com.flansmod.client.tmt.Coord2D;
import com.flansmod.client.tmt.ModelRendererTurbo;


public class ModelTrackStraightPoints extends ModelTrack 
{
	public ModelTrackStraightPoints()
	{
		int textureX = 64;
		int textureY = 64;
		
		int length = 6;
		int scaledLength = length * 16;
		int numSleepers = 6;
		int numTrackModelPieces = 9;
		
		trackModel = new ModelRendererTurbo[numTrackModelPieces + numSleepers];
		for(int i = 0; i < numTrackModelPieces; i++)
			trackModel[i] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		
		trackModel[0].addBox(-scaledLength / 2, 2, -9, 15, 2, 1);
		trackModel[1].addBox(-scaledLength / 2, 2, 8, scaledLength, 2, 1);
		trackModel[2].addBox(-scaledLength / 2 + 29, 2, -9, 38, 2, 1);		
		trackModel[3].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 61, -8, 0, 0), new Coord2D(-scaledLength / 2 + 63, -9, 0, 0), new Coord2D(scaledLength / 2 - 8, -9, 0, 0), new Coord2D(scaledLength / 2 - 8, -8, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);
		trackModel[4].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 59, -8, 0, 0), new Coord2D(-scaledLength / 2 + 59, -9, 0, 0), new Coord2D(-scaledLength / 2 + 73, -16, 0, 0), new Coord2D(-scaledLength / 2 + 75, -16, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		trackModel[5].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 21, 6, 0, 0), new Coord2D(-scaledLength / 2 + 21, 5, 0, 0), new Coord2D(-scaledLength / 2 + 40, 0, 0, 0), new Coord2D(-scaledLength / 2 + 40, 1, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		trackModel[6].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 40, 1, 0, 0), new Coord2D(-scaledLength / 2 + 40, 0, 0, 0), new Coord2D(-scaledLength / 2 + 55, -7, 0, 0), new Coord2D(-scaledLength / 2 + 57, -7, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		trackModel[7].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 7, -8, 0, 0), new Coord2D(-scaledLength / 2 + 7, -9, 0, 0), new Coord2D(-scaledLength / 2 + 21, -11, 0, 0), new Coord2D(-scaledLength / 2 + 21, -10, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		trackModel[8].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 21, -10, 0, 0), new Coord2D(-scaledLength / 2 + 21, -11, 0, 0), new Coord2D(-scaledLength / 2 + 37, -16, 0, 0), new Coord2D(-scaledLength / 2 + 39, -16, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);		
		

		
		for(int i = 0; i < numSleepers; i++)
		{
			boolean wide = i == 2 || i == 3 || i == 4;
			trackModel[numTrackModelPieces + i] = new ModelRendererTurbo(this, wide ? textureX - 4 : 0, wide ? 34 : 3, textureX, textureY);
			trackModel[numTrackModelPieces + i].addBox(-scaledLength / 2 + (float)scaledLength * ((float)i + 0.5F) / (float)numSleepers - 3F, 0, wide ? -16 : -12, 6, 2, wide ? 28 : 24);
		}
		
		pointsModel = new ModelRendererTurbo[1][2][2];
		
		pointsModel[0][0][0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		pointsModel[0][0][1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		pointsModel[0][0][0].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 7, -8, 0, 0), new Coord2D(-scaledLength / 2 + 13, -9, 0, 0), new Coord2D(-scaledLength / 2 + 21, -9, 0, 0), new Coord2D(-scaledLength / 2 + 21, -8, 0, 0) } , 
						2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		pointsModel[0][0][1].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 13, 7, 0, 0), new Coord2D(-scaledLength / 2 + 7, 7, 0, 0), new Coord2D(-scaledLength / 2 + 21, 5, 0, 0), new Coord2D(-scaledLength / 2 + 21, 6, 0, 0) } , 
						2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);	
		
		pointsModel[0][1][0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		pointsModel[0][1][1] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		pointsModel[0][1][0].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 7, -7, 0, 0), new Coord2D(-scaledLength / 2 + 13, -8, 0, 0), new Coord2D(-scaledLength / 2 + 21, -9, 0, 0), new Coord2D(-scaledLength / 2 + 21, -8, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		pointsModel[0][1][1].addShape3D(8F, 2F, 0F, new Coord2D[] { new Coord2D(-scaledLength / 2 + 13, 8, 0, 0), new Coord2D(-scaledLength / 2 + 9, 8, 0, 0), new Coord2D(-scaledLength / 2 + 21, 5, 0, 0), new Coord2D(-scaledLength / 2 + 21, 6, 0, 0) } , 
				2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);	
		
		
	}
}
