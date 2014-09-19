package com.flansmod.common.trains;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import com.flansmod.common.FlansMod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

public class TrainHandler 
{
	public HashMap<World, TrainSpace> trainSpaces = new HashMap<World, TrainSpace>();
	
	public TrainHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void worldData(WorldEvent event)
	{
		if(event instanceof WorldEvent.Load)
		{
			loadPerWorldData(event, event.world);
			savePerWorldData(event, event.world);
		}
		if(event instanceof WorldEvent.Save)
		{
			savePerWorldData(event, event.world);
		}
		if(event instanceof WorldEvent.Unload)
		{
			trainSpaces.remove(event.world);
		}
	}
	
	private void loadPerWorldData(WorldEvent event, World world)
	{
		TrainSpace trainSpace = new TrainSpace(world);
		trainSpaces.put(world, trainSpace);
		
		File worldDirectory = world.getSaveHandler().getWorldDirectory();
		File trainsDir = new File(worldDirectory, "trains/");
		File trainsDataFile = new File(trainsDir, world.provider.dimensionId + ".dat");
		
		try
		{
			//If there is no saved data, skip loading
			if(!trainsDir.exists() || !trainsDataFile.exists())
				return;
			//Open an input stream for reading
			DataInputStream in = new DataInputStream(new FileInputStream(trainsDataFile));
			
			//Read the track index from file
			int indexSize = in.readInt();
			TrackPiece[] index = new TrackPiece[indexSize];
			for(int i = 0; i < indexSize; i++)
			{
				String shortName = in.readUTF();
				D4 orientation = D4.values()[in.readByte()];
				index[i] = TrackPiece.getPiece(shortName).orientations.get(orientation);
			}
			
			//Read track pieces from file
			int numTracks = in.readInt();
			for(int i = 0; i < numTracks; i++)
			{
				Track track = new Track(index[in.readInt()], in.readInt(), in.readInt(), in.readInt(), in.readInt());
				int numSwitchStates = in.readByte();
				for(int j = 0; j < numSwitchStates; j++)
				{
					track.switchStates[j] = in.readBoolean();
				}
				trainSpace.addTrack(track);
			}
			
			//Read trains from file		
			int numTrains = in.readInt();
			for(int i = 0; i < numTrains; i++)
			{
				int trainID = in.readInt();
				int numCoaches = in.readInt();
				Coach[] coaches = new Coach[numCoaches];
				for(int j = 0; j < numCoaches; j++)
				{
					TrackAlignedPosition bogiePosition1 = new TrackAlignedPosition(in);
					TrackAlignedPosition bogiePosition2 = new TrackAlignedPosition(in);
					CoachType coachType = CoachType.getCoach(in.readUTF());
					int coachID = in.readInt();
					
					coaches[j] = new Coach(coachType, bogiePosition1, bogiePosition2, coachID);
					coaches[j].setTrainSpace(trainSpace);
				}
				
				trainSpace.trains.put(trainID, new Train(trainID, coaches));
			}
			
			in.close();
		}
		catch(Exception e)
		{
			FlansMod.log("Failed to load train data for world " + world.getWorldInfo().getWorldName() + ".");
			e.printStackTrace();
		}
	}
	
	private void savePerWorldData(WorldEvent event, World world)
	{
		File worldDirectory = world.getSaveHandler().getWorldDirectory();
		File trainsDir = new File(worldDirectory, "trains/");
		File trainsDataFile = new File(trainsDir, world.provider.dimensionId + ".dat");
		TrainSpace trainSpace = trainSpaces.get(world);
		try
		{
			//If there is no file, make one
			if(!trainsDir.exists())
				trainsDir.mkdir();
			if(!trainsDataFile.exists())
				trainsDataFile.createNewFile();
			//Open an output stream for writing
			DataOutputStream out = new DataOutputStream(new FileOutputStream(trainsDataFile));
			
			//Write the track index to file
			out.writeInt(TrackPiece.allPieces.size());
			for(int i = 0; i < TrackPiece.allPieces.size(); i++)
			{
				out.writeUTF(TrackPiece.allPieces.get(i).shortName);
				out.writeByte(TrackPiece.allPieces.get(i).orientation.ordinal());
			}
			
			//Write the track pieces to file
			out.writeInt(trainSpace.tracks.values().size());
			for(Track track : trainSpace.tracks.values())
			{
				out.writeInt(TrackPiece.allPieces.indexOf(track.piece));
				out.writeInt(track.x);
				out.writeInt(track.y);
				out.writeInt(track.z);
				out.writeInt(track.ID);
				
				out.writeByte(track.switchStates.length);
				for(int j = 0; j < track.switchStates.length; j++)
				{
					out.writeBoolean(track.switchStates[j]);
				}
			}
			
			//Write trains to file
			out.writeInt(trainSpace.trains.values().size());
			for(Train train : trainSpace.trains.values())
			{
				out.writeInt(train.ID);
				out.writeInt(train.length);
				for(int i = 0; i < train.length; i++)
				{
					Coach coach = train.coaches[i];
					coach.bogiePositions[0].writeTo(out);
					coach.bogiePositions[1].writeTo(out);
					out.writeUTF(coach.type.shortName);
					out.writeInt(coach.ID);
				}
			}
			
			out.close();
			
		}
		catch(Exception e)
		{
			FlansMod.log("Failed to save train data for world " + world.getWorldInfo().getWorldName() + ".");
			e.printStackTrace();
		}
	}
	
	public void tick()
	{
		for(TrainSpace space : trainSpaces.values())
		{
			space.tick();
		}
	}
	
	public boolean addTrack(World world, Track track)
	{
		return trainSpaces.get(world).addTrack(track);
	}
}
