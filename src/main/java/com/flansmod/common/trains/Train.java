package com.flansmod.common.trains;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A train is an abstract entity that travels trough the TrainSpace
 * It is compromised of coaches, some of which may be engines */
public class Train 
{
	public static int nextFreeID;
	
	/** Train ID. Unique identifier for client-server syncing */
	public int ID;
	/** The number of coaches in this train */
	public int length;
	/** The coaches in this train */
	public Coach[] coaches;
	
	public Train(int ID, Coach[] coaches)
	{
		this.ID = ID;
		if(ID >= nextFreeID)
			nextFreeID = ID + 1;
		length = coaches.length;
		this.coaches = coaches;
		for(Coach coach : coaches)
			coach.setTrain(this);
	}
	
	/** Constructor for placing a single uncoupled coach */
	public Train(int ID, Coach coach)
	{
		this(ID, new Coach[] { coach });
	}
	
	public Train(Coach coach)
	{
		this(nextFreeID++, coach);
	}

	public void update() 
	{
		for(Coach coach : coaches)
			coach.update();	
	}

	/** Called when the client receives new data for this train from the server. The incoming train data is stored in a throwaway Train */
	@SideOnly(Side.CLIENT)
	public void receivedData(Train train) 
	{
		//TODO
	}

	public Coach getCoachWithID(int coachID) 
	{
		for(Coach coach : coaches)
		{
			if(coach.ID == coachID)
				return coach;
		}
		return null;
	}

	public void setTrainSpace(TrainSpace space) 
	{
		for(Coach coach : coaches)
		{
			coach.setTrainSpace(space);
		}
	}
}
