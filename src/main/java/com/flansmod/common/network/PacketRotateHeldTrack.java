package com.flansmod.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.flansmod.common.FlansMod;
import com.flansmod.common.trains.ItemTrack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;

public class PacketRotateHeldTrack extends PacketBase 
{
	public boolean reflect;
	
	public PacketRotateHeldTrack()
	{
		
	}
	
	public PacketRotateHeldTrack(boolean b)
	{
		reflect = b;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		data.writeBoolean(reflect);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		reflect = data.readBoolean();
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		ItemTrack.rotateCurrentRail(playerEntity, reflect);
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) 
	{
		FlansMod.log("Received rotate track packet on client. Skipping.");
	}
}
