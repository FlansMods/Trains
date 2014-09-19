package com.flansmod.common.trains;

import java.util.ArrayList;

import com.flansmod.client.model.ModelSwitch;
import com.flansmod.common.FlansMod;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.types.TypeFile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SwitchType extends InfoType 
{
	public static ArrayList<SwitchType> switches = new ArrayList<SwitchType>();
	
	@SideOnly(Side.CLIENT)
	public ModelSwitch model;
	public EnumSwitchMode mode;
	
	public SwitchType(TypeFile file) 
	{
		super(file);
	}
	
	@Override
	protected void read(String[] split, TypeFile file)
	{
		super.read(split, file);
		try
		{		
			if(FMLCommonHandler.instance().getSide().isClient() && split[0].toLowerCase().equals("model"))
				model = FlansMod.proxy.loadModel(split[1], shortName, ModelSwitch.class);
			if(split[0].toLowerCase().equals("texture"))
				texture = split[1];
			if(split[0].toLowerCase().equals("mode"))
				mode = EnumSwitchMode.get(split[1]);
			
		}
		catch(Exception e)
		{
			System.out.println("Reading switch file failed : " + shortName);
			e.printStackTrace();
		}
	}
	
	
	public SwitchType getSwitch(String s)
	{
		for(SwitchType sw : switches)
		{
			if(sw.shortName.equals(s))
				return sw;
		}
		return null;
	}

	@Override
	public void reloadModel() 
	{
		model = FlansMod.proxy.loadModel(modelString, shortName, ModelSwitch.class);
	}
}
