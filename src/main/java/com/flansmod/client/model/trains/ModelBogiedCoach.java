package com.flansmod.client.model.trains;

import com.flansmod.client.model.ModelCoach;
import com.flansmod.client.tmt.ModelRendererTurbo;

public abstract class ModelBogiedCoach extends ModelCoach 
{
	public ModelBogiedCoach(int textureX, int textureY)
	{
		bogieModel = new ModelRendererTurbo[2][7];
		bogieWheelModel = new ModelRendererTurbo[2][2][11];
		
		//For both bogies
		for(int i = 0; i < 2; i++)
		{	
			bogieModel[i][0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
			bogieModel[i][0].addBox(-16, 14, -10, 32, 2, 20);
			
			bogieModel[i][1] = new ModelRendererTurbo(this, 104, 0, textureX, textureY);
			bogieModel[i][1].addBox(-16, 6, -10, 32, 8, 3);
			
			bogieModel[i][2] = new ModelRendererTurbo(this, 104, 0, textureX, textureY);
			bogieModel[i][2].addBox(-16, 6, 7, 32, 8, 3);
			
			//Left buffer
			bogieModel[i][3] = new ModelRendererTurbo(this, 24, 0, textureX, textureY);
			bogieModel[i][3].addBox(16, 12, 5, 8, 2, 2);
			
			bogieModel[i][4] = new ModelRendererTurbo(this, 24, 0, textureX, textureY);
			bogieModel[i][4].addBox(24, 11, 4, 2, 4, 4);
			
			//Right buffer
			bogieModel[i][5] = new ModelRendererTurbo(this, 24, 0, textureX, textureY);
			bogieModel[i][5].addBox(16, 12, -7, 8, 2, 2);
			
			bogieModel[i][6] = new ModelRendererTurbo(this, 24, 0, textureX, textureY);
			bogieModel[i][6].addBox(24, 11, -8, 2, 4, 4);
			
			//For each axle
			for(int j = 0; j < 2; j++)
			{
				//Axle shaft
				bogieWheelModel[i][j][0] = new ModelRendererTurbo(this, 188, 0, textureX, textureY);
				bogieWheelModel[i][j][0].addBox(-1F, -1F, -8F, 2, 2, 16);
				
				//Wheel
				for(int k = 0; k < 2; k++)
				{
					bogieWheelModel[i][j][k == 0 ? 1 : 6] = new ModelRendererTurbo(this, 232, 0, textureX, textureY);
					bogieWheelModel[i][j][k == 0 ? 1 : 6].addBox(-2F, -4F, (k == 0 ? -9F : 8F), 4, 1, 1);

					bogieWheelModel[i][j][k == 0 ? 2 : 7] = new ModelRendererTurbo(this, 230, 3, textureX, textureY);
					bogieWheelModel[i][j][k == 0 ? 2 : 7].addBox(-3F, -3F, (k == 0 ? -9F : 8F), 6, 1, 1);
					
					bogieWheelModel[i][j][k == 0 ? 3 : 8] = new ModelRendererTurbo(this, 228, 6, textureX, textureY);
					bogieWheelModel[i][j][k == 0 ? 3 : 8].addBox(-4F, -2F, (k == 0 ? -9F : 8F), 8, 4, 1);
					
					bogieWheelModel[i][j][k == 0 ? 4 : 9] = new ModelRendererTurbo(this, 230, 12, textureX, textureY);
					bogieWheelModel[i][j][k == 0 ? 4 : 9].addBox(-3F, 2F, (k == 0 ? -9F : 8F), 6, 1, 1);

					bogieWheelModel[i][j][k == 0 ? 5 : 10] = new ModelRendererTurbo(this, 232, 15, textureX, textureY);
					bogieWheelModel[i][j][k == 0 ? 5 : 10].addBox(-2F, 3F, (k == 0 ? -9F : 8F), 4, 1, 1);

				}
			}
			
			//Set the position of each axle
			for(int k = 0; k < 11; k++)
			{
				bogieWheelModel[i][0][k].setRotationPoint(-10F, 8F, 0F);
				bogieWheelModel[i][1][k].setRotationPoint(10F, 8F, 0F);
			}
		}
		
		for(int j = 0; j < bogieModel[1].length; j++)
			bogieModel[1][j].rotateAngleY = tau / 2;
	}
}
