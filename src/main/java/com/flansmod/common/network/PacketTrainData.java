package com.flansmod.common.network;

import java.util.ArrayList;

import com.flansmod.common.FlansMod;
import com.flansmod.common.trains.Coach;
import com.flansmod.common.trains.CoachType;
import com.flansmod.common.trains.TrackAlignedPosition;
import com.flansmod.common.trains.Train;
import com.flansmod.common.trains.TrainSpace;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketTrainData extends PacketBase 
{
	public ArrayList<Train> trainData;
	
	public PacketTrainData()
	{		
	}
	
	public PacketTrainData(ArrayList<Train> trains)
	{
		trainData = trains;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		data.writeInt(trainData.size());
		for(int i = 0; i < trainData.size(); i++)
		{
			Train train = trainData.get(i);
			data.writeInt(train.ID);
			data.writeInt(train.length);
			for(int j = 0; j < train.length; j++)
			{
				Coach coach = train.coaches[j];
				coach.bogiePositions[0].writeTo(data);
				coach.bogiePositions[1].writeTo(data);
				writeUTF(data, coach.type.shortName);
				data.writeInt(coach.ID);
			}
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		trainData = new ArrayList<Train>();
		int numTrains = data.readInt();
		for(int i = 0; i < numTrains; i++)
		{
			int trainID = data.readInt();
			int trainLength = data.readInt();
			Coach[] coaches = new Coach[trainLength];
			for(int j = 0; j < trainLength; j++)
			{
				TrackAlignedPosition front = new TrackAlignedPosition(data);
				TrackAlignedPosition back = new TrackAlignedPosition(data);
				CoachType type = CoachType.getCoach(readUTF(data));
				int coachID = data.readInt();
				coaches[j] = new Coach(type, front, back, coachID);
			}
			Train train = new Train(trainID, coaches);
			trainData.add(train);
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) 
	{
		for(Train train : trainData)
		{
			TrainSpace space = FlansMod.trainHandler.trainSpaces.get(clientPlayer.worldObj);
			if(space.trains.containsKey(Integer.valueOf(train.ID)))
			{
				space.trains.get(Integer.valueOf(train.ID)).receivedData(train);
			}
			else
			{
				train.setTrainSpace(space);
				space.trains.put(train.ID, train);
			}
		}
	}

}
