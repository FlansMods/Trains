package com.flansmod.client.model;

import com.flansmod.client.tmt.ModelRendererTurbo;

import net.minecraft.client.model.ModelBase;

public class ModelSwitch extends ModelBase 
{
	public ModelRendererTurbo[] baseModel = new ModelRendererTurbo[0];
	public ModelRendererTurbo[] onModel = new ModelRendererTurbo[0];	
	public ModelRendererTurbo[] offModel = new ModelRendererTurbo[0];
	
	public void render(boolean on)
	{
		float f5 = 1F / 16F;
		
		for(ModelRendererTurbo model : baseModel)
			model.render(f5);
		
		for(ModelRendererTurbo model : (on ? onModel : offModel))
			model.render(f5);
	}
}
