package com.flansmod.common;

import java.util.ArrayList;

import com.flansmod.common.guns.EntityGrenade;
import com.flansmod.common.guns.EntityMG;
import com.flansmod.common.teams.PlayerClass;
import com.flansmod.common.teams.Team;
import com.flansmod.common.trains.Track;
import com.flansmod.common.trains.Train;

public class PlayerData 
{
	public String username;
	public EntityMG mountingGun;
	public Team team;
	public PlayerClass playerClass;
	public PlayerClass newPlayerClass;
	public boolean isShooting;
	public int shootTime;
	public int shootClickDelay;
	public int spawnDelay;
	public double spawnX;
	public double spawnY;
	public double spawnZ;

	public float prevRotationRoll;
	public float rotationRoll;
	
	/** When remote explosives are thrown they are added to this list. When the player uses a remote, the first one from this list detonates */
	public ArrayList<EntityGrenade> remoteExplosives = new ArrayList<EntityGrenade>();
	
	/** Tracks what track the player is tracking */
	public ArrayList<Track> trackedTrack = new ArrayList<Track>();
	/** Tracks what trains the player is tracking */
	public ArrayList<Train> trackedTrains = new ArrayList<Train>();
	
	//For use by the currentGametype
	public int score;
	public int kills;
	public int deaths;
	public boolean out; //For Nerf gametypes

	//For my quick world edit hack thingy
	public int x1, y1, z1, x2, y2, z2;
	
	public PlayerData(String name) 
	{
		username = name;	
	}
	
	public void tick()
	{
		if(shootTime > 0)
			shootTime--;
		if(shootClickDelay > 0)
			shootClickDelay--;
		spawnDelay--;
	}
	
	public void setSpawn(double x, double y, double z, int t)
	{
		spawnX = x;
		spawnY = y;
		spawnZ = z;
		spawnDelay = t;
	}
	
	public PlayerClass getPlayerClass()
	{
		if(playerClass != newPlayerClass)
			playerClass = newPlayerClass;
		return playerClass;
	}

	public void resetScore() 
	{
		score = kills = deaths = 0;
	}
}
