package com.flansmod.client.model.trains;

import com.flansmod.client.tmt.ModelRendererTurbo;

public class ModelCoachLongStandard extends ModelBogiedCoach 
{
	public ModelCoachLongStandard(int textureX, int textureY, int numParts) 
	{
		//Make sure you pass in the texture size so the bogies know where to pick their texture from
		super(textureX, textureY);

		bodyModel = new ModelRendererTurbo[numParts];
		
		bodyModel[0] = new ModelRendererTurbo(this, 0, 32, textureX, textureY);
		bodyModel[0].addBox(-80F, 16F, -12F, 160, 1, 24);		
		
		bodyModel[1] = new ModelRendererTurbo(this, 0, 58, textureX, textureY);
		bodyModel[1].addBox(-80F, 17F, -12F, 160, 32, 1);
		
		bodyModel[2] = new ModelRendererTurbo(this, 0, 58, textureX, textureY);
		bodyModel[2].addBox(-80F, 17F, -12F, 160, 32, 1);
		bodyModel[2].rotateAngleY = pi;
	
		//Walls (with curved caps)
		//End wall 1
		bodyModel[3] = new ModelRendererTurbo(this, 368, 32, textureX, textureY);
		bodyModel[3].addBox(-80F, 17F, -11F, 1, 32, 22);
		
		bodyModel[4] = new ModelRendererTurbo(this, 327, 60, textureX, textureY);
		bodyModel[4].addShapeBox(-80F, 49F, -11F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, -2F, -2F, /* 5 */ 0F, -2F, -2F, /* 6 */ 0F, 0F, 0F, /* 7 */ 0F, 0F, 0F);

		bodyModel[5] = new ModelRendererTurbo(this, 344, 60, textureX, textureY);
		bodyModel[5].addBox(-80F, 49F, -4F, 1, 6, 8);
		
		bodyModel[6] = new ModelRendererTurbo(this, 327, 74, textureX, textureY);
		bodyModel[6].addShapeBox(-80F, 49F, 4F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, 0F, 0F, /* 5 */ 0F, 0F, 0F, /* 6 */ 0F, -2F, -2F, /* 7 */ 0F, -2F, -2F);
		
		//End wall 2
		bodyModel[7] = new ModelRendererTurbo(this, 368, 32, textureX, textureY);
		bodyModel[7].addBox(-80F, 17F, -11F, 1, 32, 22);
		bodyModel[7].rotateAngleY = pi;
		
		bodyModel[8] = new ModelRendererTurbo(this, 327, 60, textureX, textureY);
		bodyModel[8].addShapeBox(-80F, 49F, -11F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, -2F, -2F, /* 5 */ 0F, -2F, -2F, /* 6 */ 0F, 0F, 0F, /* 7 */ 0F, 0F, 0F);
		bodyModel[8].rotateAngleY = pi;
		
		bodyModel[9] = new ModelRendererTurbo(this, 344, 60, textureX, textureY);
		bodyModel[9].addBox(-80F, 49F, -4F, 1, 6, 8);
		bodyModel[9].rotateAngleY = pi;
		
		bodyModel[10] = new ModelRendererTurbo(this, 327, 74, textureX, textureY);
		bodyModel[10].addShapeBox(-80F, 49F, 4F, 1, 6, 7, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, 0F, 0F, /* 5 */ 0F, 0F, 0F, /* 6 */ 0F, -2F, -2F, /* 7 */ 0F, -2F, -2F);
		bodyModel[10].rotateAngleY = pi;
		
		//Roof panels
		bodyModel[11] = new ModelRendererTurbo(this, 0, 93, textureX, textureY);
		bodyModel[11].addShapeBox(-80F, 49F, -12F, 160, 4, 1, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, 1F, -3F, /* 5 */ 0F, 1F, -3F, /* 6 */ 0F, 0F, 2F, /* 7 */ 0F, 0F, 2F);
		
		bodyModel[12] = new ModelRendererTurbo(this, 0, 99, textureX, textureY);
		bodyModel[12].addShapeBox(-80F, 53F, -9F, 160, 1, 5, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, -2F, 0F, /* 3 */ 0F, -2F, 0F, /* 4 */ 0F, 0F, 0F, /* 5 */ 0F, 0F, 0F, /* 6 */ 0F, 2F, 0F, /* 7 */ 0F, 2F, 0F);
		
		bodyModel[13] = new ModelRendererTurbo(this, 0, 106, textureX, textureY);
		bodyModel[13].addBox(-80F, 55F, -4F, 160, 1, 8);

		bodyModel[14] = new ModelRendererTurbo(this, 0, 99, textureX, textureY);
		bodyModel[14].addShapeBox(-80F, 53F, -9F, 160, 1, 5, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, -2F, 0F, /* 3 */ 0F, -2F, 0F, /* 4 */ 0F, 0F, 0F, /* 5 */ 0F, 0F, 0F, /* 6 */ 0F, 2F, 0F, /* 7 */ 0F, 2F, 0F);
		bodyModel[14].rotateAngleY = pi;
		
		bodyModel[15] = new ModelRendererTurbo(this, 0, 93, textureX, textureY);
		bodyModel[15].addShapeBox(-80F, 49F, -12F, 160, 4, 1, 0F, /* 0 */ 0F, 0F, 0F, /* 1 */ 0F, 0F, 0F, /* 2 */ 0F, 0F, 0F, /* 3 */ 0F, 0F, 0F, /* 4 */ 0F, 1F, -3F, /* 5 */ 0F, 1F, -3F, /* 6 */ 0F, 0F, 2F, /* 7 */ 0F, 0F, 2F);
		bodyModel[15].rotateAngleY = pi;
	}
}
