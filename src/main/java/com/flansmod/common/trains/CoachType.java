package com.flansmod.common.trains;

import java.util.ArrayList;

import com.flansmod.client.model.ModelCoach;
import com.flansmod.client.model.ModelMecha;
import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.DriveableType;
import com.flansmod.common.driveables.Seat;
import com.flansmod.common.types.TypeFile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoachType extends DriveableType 
{
	public static ArrayList<CoachType> coaches = new ArrayList<CoachType>();
	
	@SideOnly(Side.CLIENT)
	public ModelCoach model;
		
	/** The length from the rotation point of one bogie to the rotation point of the other. If the train does not have bogies, this should be considered as the 
	 * origin of the coupling rod */
	public float length = 2F;
	/** The mass of the coach */
	public float mass = 1F;
	/** In this case, there are no seats, and right clicking on the coach will open the inventory window */
	public boolean inventoryOnly = false;
	/** Whether or not this coach is an engine */
	public boolean engineCoach = false;
	/** How powerful the coach's brakes are */
	public float brakeStrength = 1F;
	/** The distance between the centre of this train's bogie and the centre point of the coupling */
	public float couplingLength = 1F;
	
	
	public CoachType(TypeFile file) 
	{
		super(file);
		//Make sure the passenger arrays are set up first
		//Override this for trains to avoid numbering confusion. For passenger coaches (with no driver seat) we don't need to add a seat for the driver
		for(String line : file.lines)
		{
			if(line == null)
				break;
			if(line.startsWith("//"))
				continue;
			String[] split = line.split(" ");
			if(split.length < 2)
				continue;
			
			if(split[0].equals("NumSeats"))
			{
				numPassengers = Integer.parseInt(split[1]) - 1;
				seats = new Seat[numPassengers + 1];
			}
		}
		coaches.add(this);
	}

	@Override
	protected void read(String[] split, TypeFile file)
	{
		super.read(split, file);
		try
		{		
			if(FMLCommonHandler.instance().getSide().isClient() && split[0].toLowerCase().equals("model"))
				model = FlansMod.proxy.loadModel(split[1], shortName, ModelCoach.class);
			if(split[0].toLowerCase().equals("length"))
				length = Float.parseFloat(split[1]);
			
		} 
		catch (Exception e)
		{
			System.out.println("Reading track file failed : " + shortName);
			e.printStackTrace();
		}
	}
	
	public static CoachType getCoach(String find)
	{
		for(CoachType type : coaches)
		{
			if(type.shortName.equals(find))
				return type;
		}
		return null;
	}
	
	@Override
	public boolean hasEngine() 
	{
		return false;
	}
	
	/** To be overriden by subtypes for model reloading */
	@Override
	public void reloadModel()
	{
		model = FlansMod.proxy.loadModel(modelString, shortName, ModelCoach.class);
	}
}
