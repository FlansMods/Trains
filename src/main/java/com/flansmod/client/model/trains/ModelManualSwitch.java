package com.flansmod.client.model.trains;

import com.flansmod.client.model.ModelSwitch;
import com.flansmod.client.tmt.ModelRendererTurbo;

public class ModelManualSwitch extends ModelSwitch 
{
	public ModelManualSwitch()
	{
		int textureX = 64;
		int textureY = 32;
		
		baseModel = new ModelRendererTurbo[1];
		baseModel[0] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
		baseModel[0].addBox(4, 0, 4, 8, 4, 8);
		
		onModel = new ModelRendererTurbo[3];
		onModel[0] = new ModelRendererTurbo(this, 32, 0, textureX, textureY);
		onModel[0].addBox(-0.5F, -1, -0.5F, 1, 16, 1);
		onModel[0].setPosition(8, 2, 7);
		onModel[0].rotateAngleX = 0.3F;
		
		onModel[1] = new ModelRendererTurbo(this, 36, 0, textureX, textureY);
		onModel[1].addBox(-1, -1, -1, 2, 16, 2);
		onModel[1].setPosition(8, 2, 9);
		onModel[1].rotateAngleX = 0.3F;
		
		onModel[2] = new ModelRendererTurbo(this, 44, 0, textureX, textureY);
		onModel[2].addBox(-0.5F, 15, -0.5F, 1, 8, 1);
		onModel[2].setPosition(8, 2, 9);
		onModel[2].rotateAngleX = 0.3F;
		
		offModel = new ModelRendererTurbo[3];
		offModel[0] = new ModelRendererTurbo(this, 32, 0, textureX, textureY);
		offModel[0].addBox(-0.5F, -1, -0.5F, 1, 16, 1);
		offModel[0].setPosition(8, 2, 7);
		offModel[0].rotateAngleX = -0.3F;
		
		offModel[1] = new ModelRendererTurbo(this, 36, 0, textureX, textureY);
		offModel[1].addBox(-1, -1, -1, 2, 16, 2);
		offModel[1].setPosition(8, 2, 9);
		offModel[1].rotateAngleX = -0.3F;
		
		offModel[2] = new ModelRendererTurbo(this, 44, 0, textureX, textureY);
		offModel[2].addBox(-0.5F, 15, -0.5F, 1, 8, 1);
		offModel[2].setPosition(8, 2, 9);
		offModel[2].rotateAngleX = -0.3F;
	}
}
