package com.flansmod.client.model.trains;

import com.flansmod.client.tmt.ModelRendererTurbo;

public class ModelCoachLongPassenger extends ModelCoachLongStandard
{
	public ModelCoachLongPassenger()
	{
		//Call super, making sure to specify texture sizes and how many model parts you want to add
		super(512, 128, 60);
		
		//The way ModelCoachLongStandard works is that you tell it how many model parts you want in total and it uses the first 16 model parts for itself, so continue from that, starting at 16.
		int textureX = 512;
		int textureY = 128;
		
		//Quarter length divider 1
		bodyModel[16] = new ModelRendererTurbo(this, 391, 32, textureX, textureY);
		bodyModel[16].addBox(-40.5F, 17F, -11F, 1, 32, 22);
		
		bodyModel[17] = new ModelRendererTurbo(this, 343, 88, textureX, textureY);
		bodyModel[17].addShapeBox(-40.5F, 49F, -11F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, -2F, -2F, /* 5 */ 0F, -2F, -2F, /* 6 */ 0F, 0F, 0F, /* 7 */ 0F, 0F, 0F);

		bodyModel[18] = new ModelRendererTurbo(this, 360, 88, textureX, textureY);
		bodyModel[18].addBox(-40.5F, 49F, -4F, 1, 6, 8);
		
		bodyModel[19] = new ModelRendererTurbo(this, 343, 102, textureX, textureY);
		bodyModel[19].addShapeBox(-40.5F, 49F, 4F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, 0F, 0F, /* 5 */ 0F, 0F, 0F, /* 6 */ 0F, -2F, -2F, /* 7 */ 0F, -2F, -2F);

		//Middle dividing wall
		bodyModel[20] = new ModelRendererTurbo(this, 391, 32, textureX, textureY);
		bodyModel[20].addBox(-0.5F, 17F, -11F, 1, 32, 22);
		
		bodyModel[21] = new ModelRendererTurbo(this, 343, 88, textureX, textureY);
		bodyModel[21].addShapeBox(-0.5F, 49F, -11F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, -2F, -2F, /* 5 */ 0F, -2F, -2F, /* 6 */ 0F, 0F, 0F, /* 7 */ 0F, 0F, 0F);

		bodyModel[22] = new ModelRendererTurbo(this, 360, 88, textureX, textureY);
		bodyModel[22].addBox(-0.5F, 49F, -4F, 1, 6, 8);
		
		bodyModel[23] = new ModelRendererTurbo(this, 343, 102, textureX, textureY);
		bodyModel[23].addShapeBox(-0.5F, 49F, 4F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, 0F, 0F, /* 5 */ 0F, 0F, 0F, /* 6 */ 0F, -2F, -2F, /* 7 */ 0F, -2F, -2F);

		//Quarter length divider 2
		bodyModel[24] = new ModelRendererTurbo(this, 391, 32, textureX, textureY);
		bodyModel[24].addBox(-40.5F, 17F, -11F, 1, 32, 22);
		bodyModel[24].rotateAngleY = pi;
		
		bodyModel[25] = new ModelRendererTurbo(this, 343, 88, textureX, textureY);
		bodyModel[25].addShapeBox(-40.5F, 49F, -11F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, -2F, -2F, /* 5 */ 0F, -2F, -2F, /* 6 */ 0F, 0F, 0F, /* 7 */ 0F, 0F, 0F);
		bodyModel[25].rotateAngleY = pi;
		
		bodyModel[26] = new ModelRendererTurbo(this, 360, 88, textureX, textureY);
		bodyModel[26].addBox(-40.5F, 49F, -4F, 1, 6, 8);
		bodyModel[26].rotateAngleY = pi;
		
		bodyModel[27] = new ModelRendererTurbo(this, 343, 102, textureX, textureY);
		bodyModel[27].addShapeBox(-40.5F, 49F, 4F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, 0F, 0F, /* 5 */ 0F, 0F, 0F, /* 6 */ 0F, -2F, -2F, /* 7 */ 0F, -2F, -2F);
		bodyModel[27].rotateAngleY = pi;
		
		//Seats
		createSeat(28, -79F, false, textureX, textureY);
		createSeat(32, -79F, true, textureX, textureY);
		createSeat(36, -39.5F, false, textureX, textureY);
		createSeat(40, -39.5F, true, textureX, textureY);
		createSeat(44, 0.5F, false, textureX, textureY);
		createSeat(48, 0.5F, true, textureX, textureY);
		createSeat(52, 40.5F, false, textureX, textureY);
		createSeat(56, 40.5F, true, textureX, textureY);
	}
	
	/** Seat creation method to save on repeated code */
	private void createSeat(int startIndex, float xPos, boolean rotate, int textureX, int textureY)
	{
		bodyModel[startIndex] = new ModelRendererTurbo(this, 368, 0, textureX, textureY);
		bodyModel[startIndex].addBox(xPos, 24F, -11F, 12, 1, 22);
		
		bodyModel[startIndex + 1] = new ModelRendererTurbo(this, 437, 0, textureX, textureY);
		bodyModel[startIndex + 1].addBox(xPos, 25F, -11F, 1, 14, 22);
		
		bodyModel[startIndex + 2] = new ModelRendererTurbo(this, 438, 37, textureX, textureY);
		bodyModel[startIndex + 2].addBox(xPos + 1F, 25F, -10F, 10, 1, 20);

		bodyModel[startIndex + 3] = new ModelRendererTurbo(this, 438, 37, textureX, textureY);
		bodyModel[startIndex + 3].addBox(xPos + 1F, 26F, -10F, 1, 12, 20);
		
		if(rotate)
			for(int i = 0; i < 4; i++)
				bodyModel[startIndex + i].rotateAngleY = pi;
	}
}
