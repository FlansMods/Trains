package com.flansmod.common.network;

import java.util.ArrayList;
import java.util.List;

import com.flansmod.common.FlansMod;
import com.flansmod.common.trains.D4;
import com.flansmod.common.trains.Track;
import com.flansmod.common.trains.TrackPiece;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketTrackData extends PacketBase 
{
	public List<Track> trackData;
	
	public PacketTrackData()
	{
		trackData = new ArrayList<Track>();
	}
	
	public PacketTrackData(List<Track> list)
	{
		trackData = list;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		//Write the track index to file
		data.writeInt(TrackPiece.allPieces.size());
		for(int i = 0; i < TrackPiece.allPieces.size(); i++)
		{
			writeUTF(data, TrackPiece.allPieces.get(i).shortName);
			data.writeByte(TrackPiece.allPieces.get(i).orientation.ordinal());
		}
		
		//Write the track pieces to file
		data.writeInt(trackData.size());
		for(int i = 0; i < trackData.size(); i++)
		{
			Track track = trackData.get(i);
			data.writeInt(TrackPiece.allPieces.indexOf(track.piece));
			data.writeInt(track.x);
			data.writeInt(track.y);
			data.writeInt(track.z);
			data.writeInt(track.ID);
			
			data.writeByte(track.switchStates.length);
			for(int j = 0; j < track.switchStates.length; j++)
			{
				data.writeBoolean(track.switchStates[j]);
			}
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		//Read the track index from file
		int indexSize = data.readInt();
		TrackPiece[] index = new TrackPiece[indexSize];
		for(int i = 0; i < indexSize; i++)
		{
			String shortName = readUTF(data);
			D4 orientation = D4.values()[data.readByte()];
			index[i] = TrackPiece.getPiece(shortName).orientations.get(orientation);
		}
		
		//Read track pieces from file
		int numTracks = data.readInt();
		for(int i = 0; i < numTracks; i++)
		{
			Track track = new Track(index[data.readInt()], data.readInt(), data.readInt(), data.readInt(), data.readInt());
			int numSwitchStates = data.readByte();
			for(int j = 0; j < numSwitchStates; j++)
			{
				track.switchStates[j] = data.readBoolean();
			}
			trackData.add(track);
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
		//FlansMod.trainHandler.trainSpaces.get(clientPlayer.worldObj).tracks.addAll(trackData);
		FlansMod.trainHandler.trainSpaces.get(clientPlayer.worldObj).addTracks(trackData);
	}

}
