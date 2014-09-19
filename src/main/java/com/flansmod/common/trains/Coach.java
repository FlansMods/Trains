package com.flansmod.common.trains;

import com.flansmod.common.FlansMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A coach is any train engine, coach or wagon
 * It is stored in the TrainSpace, in its Train (can be a single coach train) */
public class Coach 
{
	/** The coach type */
	public CoachType type;
	/** The train space this coach is in */
	public TrainSpace space;
	/** The train this coach is a part of */
	public Train train;
	/** The next free coach ID */
	private static int nextFreeID;
	/** The coach number of this coach within the train */
	public int ID;
	
	/** The positions of the two bogies of this coach. Bogieless coaches still have bogies, but they are invisible 
	 * 0 is the bogie in front, 1 is the bogie behind */
	public TrackAlignedPosition[] bogiePositions = new TrackAlignedPosition[2];
	/** For smooth rendering */
	@SideOnly(Side.CLIENT)
	public TrackAlignedPosition[] prevBogiePositions = new TrackAlignedPosition[2];
	/** 0 : Coach in front, 1 : Coach behind */
	public Coach[] coupledTo = new Coach[2];
	private boolean foundTrain = false;
	
	private Coach(CoachType type, int ID)
	{
		this.type = type;
		this.ID = ID;
		if(ID < nextFreeID)
			nextFreeID = ID + 1;
	}
	
	public Coach(CoachType type, TrackAlignedPosition front, TrackAlignedPosition back, int coachID)
	{
		this(type, coachID);
		bogiePositions[0] = front;
		bogiePositions[1] = back;
		prevBogiePositions[0] = bogiePositions[0].clone();
		prevBogiePositions[1] = bogiePositions[1].clone();
	}

	public Coach(TrainSpace space, CoachType type, Track track, boolean flipped)
	{
		this(type, nextFreeID++);
		this.space = space;
		//If placing the coach on this piece of track needs only this piece of track, then we can cope easily enough
		if(track.piece.routes[0].totalLength >= type.length)
		{
			float scaledLength = type.length / track.piece.routes[0].totalLength;
			bogiePositions[flipped ? 1 : 0] = new TrackAlignedPosition(track, 0, 0.5F - scaledLength / 2F, flipped);
			bogiePositions[flipped ? 0 : 1] = new TrackAlignedPosition(track, 0, 0.5F + scaledLength / 2F, flipped);
		}
		else
		{
			//Panic
		}
		
		if(space.world.isRemote)
		{
			prevBogiePositions[0] = bogiePositions[0].clone();
			prevBogiePositions[1] = bogiePositions[1].clone();
		}
	}
	
	public void update()
	{
		if(space.world.isRemote)
		{
			prevBogiePositions[0] = bogiePositions[0].clone();
			prevBogiePositions[1] = bogiePositions[1].clone();
		}
	}

	public double getCoachX() 
	{
		return (bogiePositions[0].getX(space) + bogiePositions[1].getX(space)) / 2;
	}
	
	public double getCoachY() 
	{
		return (bogiePositions[0].getY(space) + bogiePositions[1].getY(space)) / 2;
	}
	
	public double getCoachZ() 
	{
		return (bogiePositions[0].getZ(space) + bogiePositions[1].getZ(space)) / 2;
	}

	public void setTrain(Train train) 
	{
		this.train = train;
		foundTrain = true;
	}
	
	public void setTrainSpace(TrainSpace space)
	{
		this.space = space;
	}

	public float getYaw() 
	{
		return (bogiePositions[0].getYaw(space) + bogiePositions[1].getYaw(space)) / 2;
	}
	
	public float getPitch() 
	{
		return (bogiePositions[0].getPitch(space) + bogiePositions[1].getPitch(space)) / 2;
	}
	
}
