package com.flansmod.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.flansmod.client.gui.GuiTeamSelect;
import com.flansmod.common.FlansMod;
import com.flansmod.common.teams.PlayerClass;
import com.flansmod.common.teams.Team;
import com.flansmod.common.teams.TeamsManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketTeamSelect extends PacketBase 
{
	public boolean selectionPacket = false;
	public String selection;
	public boolean classChoicesPacket = false;
	public Team[] teams;
	public PlayerClass[] playerClasses;
	
	public PacketTeamSelect() {}

	public PacketTeamSelect(Team[] t)
	{
		selectionPacket = false;
		classChoicesPacket = false;
		teams = t;
	}
	
	public PacketTeamSelect(PlayerClass[] c)
	{
		selectionPacket = false;
		classChoicesPacket = true;
		playerClasses = c;
	}
		
	public PacketTeamSelect(String shortName, boolean classPacket)
	{
		selectionPacket = true;
		classChoicesPacket = classPacket;
		selection = shortName;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		data.writeBoolean(selectionPacket);
		data.writeBoolean(classChoicesPacket);

		//If it is a selection packet, then we need only send the selection
		if(selectionPacket)
		{
			writeUTF(data, selection);
		}
		//Otherwise, we must send the full list of teams or classes on offer
		else
		{
			if(classChoicesPacket)
			{
		    	data.writeByte(playerClasses.length);
		    	for(int i = 0; i < playerClasses.length; i++)
		    	{
		    		writeUTF(data, playerClasses[i].shortName);
		    	}
			}
			else
			{
		    	data.writeByte(teams.length);
		    	for(int i = 0; i < teams.length; i++)
		    	{
		    		writeUTF(data, teams[i] == null ? Team.spectators.shortName : teams[i].shortName);
		    	}
			}
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		selectionPacket = data.readBoolean();
		classChoicesPacket = data.readBoolean();
		
		if(selectionPacket)
		{
			selection = readUTF(data);
		}
		else
		{
			if(classChoicesPacket)
			{
				byte numClasses = data.readByte();
				playerClasses = new PlayerClass[numClasses];
				for(int i = 0; i < numClasses; i++)
				{
					playerClasses[i] = PlayerClass.getClass(readUTF(data));
				}
			}
			else
			{
				byte numTeams = data.readByte();
				teams = new Team[numTeams];
				for(int i = 0; i < numTeams; i++)
				{
					teams[i] = Team.getTeam(readUTF(data));
				}
			}
		}
	}

	/** Handle player responses to team / class selection packets */
	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		if(!selectionPacket)
		{
			FlansMod.log("Class / Team listing packet received on server. Rejecting.");
			return;
		}
		if(classChoicesPacket)
		{
			TeamsManager.getInstance().playerSelectedClass(playerEntity, selection);
		}
		else
		{
			TeamsManager.getInstance().playerSelectedTeam(playerEntity, selection);
		}
	}

	/** Handle a request from the server to display a team / class selection window */
	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) 
	{		
		if(selectionPacket)
		{
			FlansMod.log("Class / Team selection packet received on client. Rejecting.");
			return;
		}
		if(classChoicesPacket)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiTeamSelect(playerClasses));
		}
		else Minecraft.getMinecraft().displayGuiScreen(new GuiTeamSelect(teams));
	}
}
