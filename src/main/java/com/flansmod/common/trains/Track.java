package com.flansmod.common.trains;

import java.util.ArrayList;
import java.util.Arrays;

import com.flansmod.common.FlansMod;
import com.flansmod.common.network.PacketTrackData;
import com.flansmod.common.trains.TrackPiece.Route;
import com.flansmod.common.trains.TrackPiece.TrainEntryPoint;

import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.util.AxisAlignedBB;

/** A track is a placed piece of track in the train space. 
 * Each track will reference a template TrackPiece. The orientation is contained within the TrackPiece */
public class Track 
{
	public static int nextFreeID = 0;
	/** The ID of this track peice, for client-server syncing */
	public int ID;
	/** The position of this Track in the train space */
	public int x, y, z;
	/** The TrackPiece template that this Track uses */
	public TrackPiece piece;
	
	public boolean[] switchStates;
	/** An array of neighbouring tracks per endpoint */
	public NeighbourTrack[] neighbours;
	
	public Track(TrackPiece p, int i, int j, int k, int ID)
	{
		this.ID = ID;
		if(ID >= nextFreeID)
			nextFreeID = ID + 1;
		piece = p;
		x = i;
		y = j; 
		z = k;
		switchStates = new boolean[p.numSwitches];
		neighbours = new NeighbourTrack[p.numEntries];
	}
	
	public Track(TrackPiece p, int i, int j, int k)
	{
		this(p, i, j, k, nextFreeID++);
	}
	
	public void checkIfNeighbour(Track track)
	{
		for(int i = 0; i < this.piece.numEntries; i++)
		{
			for(int j = 0; j < track.piece.numEntries; j++)
			{
				//If the two entry points match
				if(this.x + this.piece.entries[i].point.x == track.x + track.piece.entries[j].point.x
				&& this.y + this.piece.entries[i].point.y == track.y + track.piece.entries[j].point.y
				&& this.x + this.piece.entries[i].point.z == track.z + track.piece.entries[j].point.z)
				//&& this.piece.entries[i].diagonal == track.piece.entries[j].diagonal
				//&& (!this.piece.entries[i].diagonal || this.piece.entries[i].flipped == track.piece.entries[j].flipped))
				{
					//We have a neighbour. Add it to both tracks' lists
					this.neighbours[i] = new NeighbourTrack(track, j);
					track.neighbours[j] = new NeighbourTrack(this, i);
				}
			}
		}
	}
	
	public boolean setSwitchState(int i, boolean b)
	{
		boolean changed = b != switchStates[i];
		switchStates[i] = b;
		return changed;	
	}
		
	public AxisAlignedBB getBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(x, y, z, x + piece.size.x, y + piece.size.y, z + piece.size.z);
	}
	
	public boolean samePositionAndPiece(Track track)
	{
		return track.x == this.x && track.y == this.y && track.z == this.z && track.piece == this.piece;
	}
	
	public Route[] getRoutes()
	{
		return piece.getRoutes(switchStates);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Track)
		{
			Track track = (Track)o;
			if(samePositionAndPiece(track))
			{
				for(int i = 0; i < switchStates.length; i++)
					if(track.switchStates[i] != this.switchStates[i])
						return false;
				return true;
			}
		}
		return false;
	}
	
	public class NeighbourTrack
	{
		/** The neighbouring track */
		public Track track;
		/** The entry point within that track that we are connected to */
		public int entryPointID;
		
		public NeighbourTrack(Track track, int i)
		{
			this.track = track;
			entryPointID = i;
		}
	}
}
