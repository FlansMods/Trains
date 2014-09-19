package com.flansmod.common.trains;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.flansmod.common.FlansMod;
import com.flansmod.common.RotatedAxes;
import com.flansmod.common.driveables.DriveableData;
import com.flansmod.common.driveables.DriveablePart;
import com.flansmod.common.driveables.DriveableType;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.driveables.PlaneType;
import com.flansmod.common.vector.Vector3f;

import cpw.mods.fml.common.network.ByteBufUtils;

public class EntityCoach extends EntityDriveable 
{
	public int trainID;
	public int coachID;
	public boolean foundCoach = false;
	public Coach coach;
	public float axleAngle;
	private boolean foundTrain = false;
	
	public EntityCoach(World world) 
	{
		super(world);
	}
	
	public EntityCoach(World world, DriveableData data, Coach coach)
	{
		super(world, coach.type, data);
		this.coach = coach;
		this.coachID = coach.ID;
		this.trainID = coach.train.ID;
		setPosition(coach.getCoachX(), coach.getCoachY(), coach.getCoachZ());
		//setAngles(coach.getCoachYaw(), coach.getCoachPitch());
		initType(coach.type, false);
	}
		
	public CoachType getCoachType()
	{
		return CoachType.getCoach(driveableType);
	}

	@Override
	public void onMouseMoved(int deltaX, int deltaY) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean pressKey(int key, EntityPlayer player) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void dropItemsOnPartDeath(Vector3f midpoint, DriveablePart part) 
	{
	}

	@Override
	public boolean hasMouseControlMode() 
	{
		return false;
	}

	@Override
	public String getBombInventoryName() 
	{
		return "";
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		TrainSpace space = FlansMod.trainHandler.trainSpaces.get(worldObj);
		
		if(!foundCoach)
		{
			Train train = space.trains.get(trainID);
			if(train == null)
				return;
			coach = train.getCoachWithID(coachID);
			if(coach == null)
				return;
			else foundCoach = true;
		}		
		
		setPosition(coach.getCoachX(), coach.getCoachY(), coach.getCoachZ());
		axes.setAngles(coach.getYaw(), coach.getPitch(), 0F);
	}
	

	
	@Override
	public void writeSpawnData(ByteBuf data)
	{
		super.writeSpawnData(data);
		if(coach == null)
		{
			data.writeInt(0);
			data.writeInt(0);
		}
		else
		{
			data.writeInt(coach.train.ID);
			data.writeInt(coach.ID);
		}
	}
	
	@Override
	public void readSpawnData(ByteBuf data)
	{
		super.readSpawnData(data);
		trainID = data.readInt();
		coachID = data.readInt();
	}
	
	@Override
    protected void writeEntityToNBT(NBTTagCompound tag)
    {
		super.writeEntityToNBT(tag);
		tag.setInteger("trainID", trainID);
		tag.setInteger("coachID", coachID);
    }

	@Override
    protected void readEntityFromNBT(NBTTagCompound tag)
    {
		super.readEntityFromNBT(tag);
		trainID = tag.getInteger("trainID");
		coachID = tag.getInteger("coachID");
		
		Train train = FlansMod.trainHandler.trainSpaces.get(worldObj).trains.get(Integer.valueOf(trainID));
		if(train == null)
			setDead();
		else
		{
			coach = train.getCoachWithID(coachID);
		}
		if(coach == null)
			setDead();
    }
}
