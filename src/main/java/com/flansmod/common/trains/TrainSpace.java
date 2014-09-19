package com.flansmod.common.trains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.flansmod.common.FlansMod;
import com.flansmod.common.PlayerData;
import com.flansmod.common.driveables.EntityDriveable;
import com.flansmod.common.guns.EntityBullet;
import com.flansmod.common.network.PacketTrackData;
import com.flansmod.common.network.PacketTrainData;
import com.flansmod.common.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/** The train space holds all tracks and trains and simulates them when it can 
 * Any appearances of trains in the world are projections from the train space
 * Any interaction with trains and the world will come through the train space
 * When trains need to interact with world that is simply not there, they will
 * pause and wait for that bit of world to load. Hence, whenever the world has
 * changes relevant to the train space, they must be updated here */
public class TrainSpace 
{
	public World world;
	public HashMap<Integer, Train> trains = new HashMap<Integer, Train>();
	//public ArrayList<Track> tracks = new ArrayList<Track>();
	public HashMap<Integer, Track> tracks = new HashMap<Integer, Track>();
	public TrackGraph graph;
	public static double trackingDistanceSquared = 10000D;
	public static double trackingDistance = 100D;
	private final int trackTrackerUpdateInterval = 20;
	
	public TrainSpace(World w)
	{
		world = w;
		graph = new TrackGraph();
	}
	
	public void tick()
	{		
		//On the server only
		if(!world.isRemote && FlansMod.ticker % trackTrackerUpdateInterval == 0)
		{
			//Update players' tracked track
			for(Object obj : world.playerEntities)
			{
				EntityPlayerMP player = (EntityPlayerMP)obj;
				PlayerData data = FlansMod.playerHandler.getPlayerData(player, Side.SERVER);
				
				//Tracks
				ArrayList<Track> trackToTrack = new ArrayList<Track>();
				
				for(Track track : tracks.values())
				{
					//If track is in range
					if(player.getDistanceSq(track.x, track.y, track.z) < trackingDistanceSquared)
					{
						trackToTrack.add(track);
					}
				}
				
				//Do not resend data regarding already tracked track
				trackToTrack.removeAll(data.trackedTrack);
				
				FlansMod.getPacketHandler().sendTo(new PacketTrackData(trackToTrack), player);
				
				//Trains
				ArrayList<Train> trainsToTrack = new ArrayList<Train>();
				
				for(Train train : trains.values())
				{
					//If track is in range
					boolean inRange = true;
					for(Coach coach : train.coaches)
					{
						if(player.getDistanceSq(coach.getCoachX(), coach.getCoachY(), coach.getCoachZ()) > trackingDistanceSquared)
							inRange = false;
					}
					if(inRange)
						trainsToTrack.add(train);
				}
				
				//Do not resend data regarding already tracked track
				trainsToTrack.removeAll(data.trackedTrains);
				
				FlansMod.getPacketHandler().sendTo(new PacketTrainData(trainsToTrack), player);
			}				
		}
		
		for(Train train : trains.values())
		{
			train.update();
		}
	}
	

	public void addTrain(Train train) 
	{
		trains.put(train.ID, train);
	}
	
	/** @return Whether this track could be added */
	public boolean addTrack(Track track)
	{
		tracks.put(Integer.valueOf(track.ID), track);
		graph.addTrack(track);
		for(Track track2 : tracks.values())
			track2.checkIfNeighbour(track);
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void addTracks(List<Track> trackData)
	{
		for(Track track : trackData)
		{
			Track track2 = tracks.get(Integer.valueOf(track.ID)); 
			if(track2 == null)
			{
				tracks.put(track.ID, track);
			}
			else
			{
				track2.switchStates = track.switchStates;
			}
		}
	}
	
	/** Raytracing method */
	public Track rayTrace(Vec3 origin, Vec3 endPoint)
	{
		Track nearestTrack = null;
		double distanceToTrack = Float.POSITIVE_INFINITY;
		
		for(Track track : tracks.values())
		{
			Vec3 hitVec = hitTrack(track, origin, endPoint);
			if(hitVec == null)
				continue;
			
			Vec3 hitVecInTrackCoords = Vec3.createVectorHelper(track.x, track.y, track.z).subtract(hitVec);
			int x = MathHelper.floor_double(hitVecInTrackCoords.xCoord);
			int z = MathHelper.floor_double(hitVecInTrackCoords.zCoord);
			if(x < 0 || x >= track.piece.size.x || z < 0 || z >= track.piece.size.z || !track.piece.needBedding[x][z])
				continue;
			
			double hitDist = hitVec.subtract(origin).lengthVector();
			if(hitDist < distanceToTrack)
			{
				nearestTrack = track;
				distanceToTrack = hitDist;
			}
		}
		
		return nearestTrack;
	}
	
	/** Raytrace for a single track piece */
	public Vec3 hitTrack(Track track, Vec3 origin, Vec3 endPoint)
	{
		MovingObjectPosition hit = AxisAlignedBB.getBoundingBox(track.x, track.y, track.z, track.x + track.piece.size.x, track.y + track.piece.size.y, track.z + track.piece.size.z).calculateIntercept(origin, endPoint);
		
		if(hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			return hit.hitVec;
		}
		
		return null;
	}
}
