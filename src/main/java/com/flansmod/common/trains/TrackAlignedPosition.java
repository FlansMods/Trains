package com.flansmod.common.trains;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.flansmod.common.trains.TrackPiece.Route;

import io.netty.buffer.ByteBuf;

public class TrackAlignedPosition 
{
	/** The track we are on */
	public int trackID;
	/** The ID of the route we are travelling along on this Track */
	public int routeID;
	/** The progression along this route */
	public float lambda;
	/** True if facing backwards */
	public boolean backwards;
	
	public TrackAlignedPosition(int trackID, int routeID, float f, boolean flipped) 
	{
		this.trackID = trackID;
		this.routeID = routeID;
		this.lambda = f;
		this.backwards = flipped;
	}
	
	public TrackAlignedPosition(Track track, int routeID, float f, boolean flipped) 
	{
		this(track.ID, routeID, f, flipped);
	}
	
	public TrackAlignedPosition(ByteBuf data)
	{
		trackID = data.readInt();
		routeID = data.readInt();
		lambda = data.readFloat();
		backwards = data.readBoolean();
	}
	
	public TrackAlignedPosition(DataInputStream data) throws IOException
	{
		trackID = data.readInt();
		routeID = data.readInt();
		lambda = data.readFloat();
		backwards = data.readBoolean();
	}

	@Override
	public TrackAlignedPosition clone()
	{
		return new TrackAlignedPosition(trackID, routeID, lambda, backwards);
	}
	
	public void writeTo(ByteBuf data)
	{
		data.writeInt(trackID);
		data.writeInt(routeID);
		data.writeFloat(lambda);
		data.writeBoolean(backwards);
	}
	
	public void writeTo(DataOutputStream data) throws IOException
	{
		data.writeInt(trackID);
		data.writeInt(routeID);
		data.writeFloat(lambda);
		data.writeBoolean(backwards);
	}
	
	public Track getTrack(TrainSpace space)
	{
		return space.tracks.get(Integer.valueOf(trackID));
	}
	
	public float getYaw(TrainSpace space)
	{
		Track track = getTrack(space);
		return track.piece.routes[routeID].getYaw(lambda);
	}
	
	public float getPitch(TrainSpace space)
	{
		return 0F;
	}

	public double getX(TrainSpace space) 
	{
		Track track = getTrack(space);
		return track.x + (double)track.piece.routes[routeID].getX(lambda);
	}
	
	public double getY(TrainSpace space) 
	{
		Track track = getTrack(space);
		return track.y + (double)track.piece.routes[routeID].getY(lambda);
	}
	
	public double getZ(TrainSpace space) 
	{
		Track track = getTrack(space);
		return track.z + (double)track.piece.routes[routeID].getZ(lambda);
	}
	

}
