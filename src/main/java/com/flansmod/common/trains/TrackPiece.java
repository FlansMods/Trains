package com.flansmod.common.trains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.flansmod.client.model.ModelTrack;
import com.flansmod.common.FlansMod;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.types.TypeFile;
import com.flansmod.common.vector.Vector3f;
import com.flansmod.common.vector.Vector3i;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A (soon to be loadable) Type class for track pieces 
 * These are templates for placed tracks */
public class TrackPiece extends InfoType
{
	/** List of track pieces, unoriented */
	public static ArrayList<TrackPiece> pieces = new ArrayList<TrackPiece>();
	/** List of track pieces, oriented */
	public static ArrayList<TrackPiece> allPieces = new ArrayList<TrackPiece>();

	/** A hash map containing 8 different track pieces which represent each of the possible orientations of this piece
	 * This map is in place to avoid repeated computation, since in general the number of Tracks will be much greater than the number of 
	 * TrackPieces, and this will be needed on every tick */
	public HashMap<D4, TrackPiece> orientations = new HashMap<D4, TrackPiece>();
	
	/** The orientation of this track piece */
	public D4 orientation;
	/** The dimensions of this piece of track */
	public Vector3i size;
	/** Whether each piece of track needs bedding or not. Starts from x-z origin (bottom left)
	 * This is flat bedding beneath the track. Any ramped bedding needs to be included in the track model */
	public boolean[][] needBedding;
	/** The number of entry points to this piece of track */
	public int numEntries;
	/** These are the entry (and exit) points of trains on this particular track piece
	 * Each one has an orientation, so you can have diagonal exits, such as for points */
	public TrainEntryPoint[] entries;
	/** The total number of routes through this TrackPiece */
	public int numRoutes;
	/** Possible routes for trains across this TrackPiece */
	public Route[] routes;
	/** The number of switches */
	public int numSwitches;
	/** The list of switches applying to this TrackPiece. Each route should only be specified by one switch. */
	public Switch[] switches;
	
	/** The model of this track piece */
	@SideOnly(Side.CLIENT)
	public ModelTrack model;
	
	public TrackPiece(TypeFile file)
	{
		super(file);
		orientation = D4.i;
		pieces.add(this);
		allPieces.add(this);
	}
	
	@Override
	/** Create 8 oriented pieces from this once loading is done */
	public void read(TypeFile file)
	{
		super.read(file);
		setupOrientations();
	}
	
	@Override
	protected void read(String[] split, TypeFile file)
	{
		super.read(split, file);
		try
		{		
			if(FMLCommonHandler.instance().getSide().isClient() && split[0].toLowerCase().equals("model"))
				model = FlansMod.proxy.loadModel(split[1], shortName, ModelTrack.class);
			if(split[0].toLowerCase().equals("texture"))
				texture = split[1];
			if(split[0].toLowerCase().equals("size"))
			{
				size = new Vector3i(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
				needBedding = new boolean[size.x][size.z];
			}
			if(split[0].toLowerCase().equals("bedding"))
			{
				for(int j = 0; j < size.z; j++)
				{
					String line = null;
					line = file.readLine();
					if(line == null || line.startsWith("//"))
					{
						j--;
						continue;
					}
					for(int i = 0; i < size.x; i++)
					{
						needBedding[i][size.z - 1 - j] = line.charAt(i) != ' '; 
					}
				}
			}
			if(split[0].toLowerCase().equals("numentries"))
			{
				numEntries = Integer.parseInt(split[1]);
				entries = new TrainEntryPoint[numEntries];
			}
			if(split[0].toLowerCase().equals("trainentry"))
				entries[Integer.parseInt(split[1])] = new TrainEntryPoint(Integer.parseInt(split[1]), new Vector3i(Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4])), Boolean.parseBoolean(split[5].toLowerCase()));
			if(split[0].toLowerCase().equals("numroutes"))
			{
				numRoutes = Integer.parseInt(split[1]);
				routes = new Route[numRoutes];
			}
			if(split[0].toLowerCase().equals("route"))
			{
				int ID = Integer.parseInt(split[1]);
				int numNodes = Integer.parseInt(split[2]);
				Vector3f[] nodes = new Vector3f[numNodes];
				TrainEntryPoint[] entryPoints = new TrainEntryPoint[2];
				for(int j = 0; j < numNodes + 2; j++)
				{
					String line = null;
					line = file.readLine();
					if(line == null || line.startsWith("//"))
					{
						j--;
						continue;
					}
					String[] splitLine = line.split(" ");
					if(splitLine[0].toLowerCase().equals("entrypoint"))
						entryPoints[0] = entries[Integer.parseInt(splitLine[1])];
					if(splitLine[0].toLowerCase().equals("node"))
						nodes[Integer.parseInt(splitLine[1])] = new Vector3f(Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]), Float.parseFloat(splitLine[4]));
					if(splitLine[0].toLowerCase().equals("exitpoint"))
						entryPoints[1] = entries[Integer.parseInt(splitLine[1])];
				}
				routes[ID] = new Route(ID, nodes, entryPoints, this);
			}
			if(split[0].toLowerCase().equals("numswitches"))
			{
				numSwitches = Integer.parseInt(split[1]);
				switches = new Switch[numSwitches];
			}
			if(split[0].toLowerCase().equals("switch"))
				switches[Integer.parseInt(split[1])] = new Switch(Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[6]));
				
		} catch (Exception e)
		{
			System.out.println("Reading track file failed : " + shortName);
			e.printStackTrace();
		}
	}
	
	/** Given the switch states of a piece of this track, determine which routes are valid */
	public Route[] getRoutes(boolean[] switchStates)
	{
		Route[] validRoutes = new Route[numRoutes - numSwitches];
		int nextRoute = 0;
		for(Route route : routes)
		{
			boolean routeIsValid = true;
			for(int i = 0; i < numSwitches; i++)
			{
				if(switches[i].offRouteID == route.ID && switchStates[i])
					routeIsValid = false;
				if(switches[i].onRouteID == route.ID && !switchStates[i])
					routeIsValid = false;
			}
			if(routeIsValid)
			{
				validRoutes[nextRoute++] = route;
			}
		}
		return validRoutes;
	}
	
	/** To be overriden by subtypes for model reloading */
	public void reloadModel()
	{
		model = FlansMod.proxy.loadModel(modelString, shortName, ModelTrack.class);
	}
	
	private void setupOrientations()
	{
		//Fill the orientation map by rotating and reflecting this piece several times
		orientations.put(D4.i, this);
		orientations.put(D4.r, rotate(this));
		orientations.put(D4.rr, rotate(orientations.get(D4.r)));
		orientations.put(D4.rrr, rotate(orientations.get(D4.rr)));
		orientations.put(D4.s, reflect(this));
		orientations.put(D4.rs, reflect(orientations.get(D4.r)));
		orientations.put(D4.rrs, reflect(orientations.get(D4.rr)));
		orientations.put(D4.rrrs, reflect(orientations.get(D4.rrr)));
	}
	
	private TrackPiece(TrackPiece piece)
	{
		super(piece.contentPack);
		//Copy orientations so that any piece may reference any other.
		orientations = piece.orientations;
		name = piece.name;
		shortName = piece.shortName;
		model = piece.model;
		texture = piece.texture;
		modelString = piece.modelString;
		numSwitches = piece.numSwitches;
		switches = piece.switches;
		allPieces.add(this);
	}
		
	/** Takes a TrackPiece and returns a new one (without references) rotated 90 degrees clockwise */
	private static TrackPiece rotate(TrackPiece oldPiece)
	{
		//Create a new piece with a reference to the orientation map
		TrackPiece newPiece = new TrackPiece(oldPiece);
		//Set the orientation of this new track piece
		newPiece.orientation = D4.compose(oldPiece.orientation, D4.r);
		//The new size will have x and z swapped
		newPiece.size = new Vector3i(oldPiece.size.z, oldPiece.size.y, oldPiece.size.x);
		//Create the new bedding array
		newPiece.needBedding = new boolean[newPiece.size.x][newPiece.size.z];
		for(int i = 0; i < newPiece.size.x; i++)
		{
			for(int j = 0; j < newPiece.size.z; j++)
			{
				newPiece.needBedding[i][j] = oldPiece.needBedding[(newPiece.size.z - 1) - j][i];
			}
		}
		//Create the new entry point array
		newPiece.numEntries = oldPiece.numEntries;
		newPiece.entries = new TrainEntryPoint[newPiece.numEntries];
		for(int i = 0; i < newPiece.numEntries; i++)
		{
			TrainEntryPoint oldEntry = oldPiece.entries[i];
			newPiece.entries[i] = new TrainEntryPoint(i, new Vector3i(oldEntry.point.z, oldEntry.point.y, newPiece.size.z - oldEntry.point.x), oldEntry.diagonal);
			newPiece.entries[i].flipped = oldEntry.flipped;
		}
		//Create the new set of routes
		newPiece.numRoutes = oldPiece.numRoutes;
		newPiece.routes = new Route[newPiece.numRoutes];
		for(int i = 0; i < newPiece.numRoutes; i++)
		{
			Route oldRoute = oldPiece.routes[i];
			Vector3f[] newNodes = new Vector3f[oldRoute.numNodes];
			for(int j = 0; j < oldRoute.numNodes; j++)
			{
				Vector3f oldNode = oldRoute.nodes[j];
				newNodes[j] = new Vector3f(oldNode.z, oldNode.y, newPiece.size.z - oldNode.x);
			}
			newPiece.routes[i] = new Route(i, newNodes, new TrainEntryPoint[] { newPiece.entries[oldRoute.entries[0].ID], newPiece.entries[oldRoute.entries[1].ID] }, newPiece);
		}
		//Create the new set of switches
		newPiece.numSwitches = oldPiece.numSwitches;
		newPiece.switches = new Switch[newPiece.numSwitches];
		for(int i = 0; i < newPiece.numSwitches; i++)
		{
			Switch oldSwitch = oldPiece.switches[i];
			newPiece.switches[i] = new Switch(oldSwitch.position.z, oldSwitch.position.y, newPiece.size.z - oldSwitch.position.x - 1, oldSwitch.onRouteID, oldSwitch.offRouteID);
		}
		//Return the new, rotated piece
		return newPiece;
	}
	
	/** Takes a track piece and returns a new one (without references) reflected in the x axis */
	private static TrackPiece reflect(TrackPiece oldPiece)
	{
		//Create a new piece with a reference to the orientation map
		TrackPiece newPiece = new TrackPiece(oldPiece);
		//Set the orientation of this new track piece
		newPiece.orientation = D4.compose(oldPiece.orientation, D4.s);
		//The new size will be exactly the same
		newPiece.size = new Vector3i(oldPiece.size.x, oldPiece.size.y, oldPiece.size.z);
		//Create the new bedding array
		newPiece.needBedding = new boolean[newPiece.size.x][newPiece.size.z];
		for(int i = 0; i < newPiece.size.x; i++)
		{
			for(int j = 0; j < newPiece.size.z; j++)
			{
				newPiece.needBedding[i][j] = oldPiece.needBedding[(newPiece.size.x - 1) - i][j];
			}
		}
		//Create the new entry point array
		newPiece.numEntries = oldPiece.numEntries;
		newPiece.entries = new TrainEntryPoint[newPiece.numEntries];
		for(int i = 0; i < newPiece.numEntries; i++)
		{
			TrainEntryPoint oldEntry = oldPiece.entries[i];
			newPiece.entries[i] = new TrainEntryPoint(i, new Vector3i(newPiece.size.x - oldEntry.point.x, oldEntry.point.y, oldEntry.point.z), oldEntry.diagonal);
			newPiece.entries[i].flipped = !oldEntry.flipped;
		}
		//Create the new set of routes
		newPiece.numRoutes = oldPiece.numRoutes;
		newPiece.routes = new Route[newPiece.numRoutes];
		for(int i = 0; i < newPiece.numRoutes; i++)
		{
			Route oldRoute = oldPiece.routes[i];
			Vector3f[] newNodes = new Vector3f[oldRoute.numNodes];
			for(int j = 0; j < oldRoute.numNodes; j++)
			{
				Vector3f oldNode = oldRoute.nodes[j];
				newNodes[j] = new Vector3f(newPiece.size.x - oldNode.x, oldNode.y, oldNode.z);
			}
			newPiece.routes[i] = new Route(i, newNodes, new TrainEntryPoint[] { newPiece.entries[oldRoute.entries[0].ID], newPiece.entries[oldRoute.entries[1].ID] }, newPiece);
		}		
		//Create the new set of switches
		newPiece.numSwitches = oldPiece.numSwitches;
		newPiece.switches = new Switch[newPiece.numSwitches];
		for(int i = 0; i < newPiece.numSwitches; i++)
		{
			Switch oldSwitch = oldPiece.switches[i];
			newPiece.switches[i] = new Switch(newPiece.size.x - oldSwitch.position.x - 1, oldSwitch.position.y, oldSwitch.position.z, oldSwitch.onRouteID, oldSwitch.offRouteID);
		}
		//Return the new, reflected piece
		return newPiece;
	}
	
	@Override
	public String toString()
	{
		return shortName + " " + orientation.name();
	}
	
	/** This class represents an entry point into a TrackPiece for a train. 
	 * It is simply a point on the track piece and a boolean to represent its diagonality */
	public static class TrainEntryPoint
	{
		public int ID;
		public Vector3i point;
		/** This means that this entry point is at 45 degrees to normal */
		public boolean diagonal;
		/** This indicates that the diagonal entrance is slanted the opposite way to normal */
		public boolean flipped;
		
		public TrainEntryPoint(int i, Vector3i v, boolean d)
		{
			ID = i;
			point = v;
			diagonal = d;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof TrainEntryPoint)
			{
				TrainEntryPoint otherPoint = (TrainEntryPoint)obj;
				if(point == otherPoint.point && diagonal == otherPoint.diagonal && flipped == otherPoint.flipped)
					return true;
			}
			return super.equals(obj);
		}
	}
	
	public static class Route
	{
		/** The ID of this route. Used for switching */
		public int ID;
		/** The number of track nodes. The trains move from node to node, so there should be enough of these to make for smooth train movement
		 * Hence, straights need none and curves should have more depending on their curvature */
		public int numNodes;
		/** The nodes. These positions are relative to the bottom left corner of the piece of track */
		public Vector3f[] nodes;
		/** The entry points that this track connects. The ordering is
		 * entries[0], nodes[0], ... nodes[numNodes - 1], entries[1] */
		public TrainEntryPoint[] entries;
		/** The length of each edge of the route */
		public float[] lengths;
		/** The total length of the route */
		public float totalLength;
		
		/** The yaw value of bogies resting on each node */
		public float[] yaw;
		
		public Route(int i, Vector3f[] n, TrainEntryPoint[] e, TrackPiece piece)
		{
			ID = i;
			numNodes = n.length;
			nodes = n;
			entries = e;
			
			//Calculate the length of each segment on the route
			lengths = new float[numNodes + 1];
			totalLength = 0;
			for(int j = 0; j < numNodes + 1; j++)
			{
				lengths[j] = getDistance(getXCoordOfNode(j + 1) - getXCoordOfNode(j), getYCoordOfNode(j + 1) - getYCoordOfNode(j), getZCoordOfNode(j + 1) - getZCoordOfNode(j));
				totalLength += lengths[j];
			}
			
			//Calculate train yaw at each node
			yaw = new float[numNodes + 2];
			
			//For endpoints look at the entry / exit angle
			yaw[0] = calculateYawOfEntryPoint(entries[0], piece);
			yaw[numNodes + 1] = calculateYawOfEntryPoint(entries[1], piece) - 180F;
			
			//For other nodes, calculate it by looking at the two adjacent edges
			for(int j = 1; j < numNodes + 1; j++)
			{
				double yawOfPrevEdge = Math.atan2(getZCoordOfNode(j) - getZCoordOfNode(j - 1), getXCoordOfNode(j) - getXCoordOfNode(j - 1)) * 180F / 3.14159265F;
				double yawOfNextEdge = Math.atan2(getZCoordOfNode(j + 1) - getZCoordOfNode(j), getXCoordOfNode(j + 1) - getXCoordOfNode(j)) * 180F / 3.14159265F;
				
				for(; yawOfNextEdge - yawOfPrevEdge > 180F; yawOfNextEdge -= 360F);
				for(; yawOfNextEdge - yawOfPrevEdge <= -180F; yawOfNextEdge += 360F);
				
				yaw[j] = (float)(yawOfPrevEdge + yawOfNextEdge) / 2F;
			}
		}
		
		private float calculateYawOfEntryPoint(TrainEntryPoint entry, TrackPiece piece)
		{
			float yaw = 0F;
			if(entry.diagonal)
				yaw = entry.flipped ? -60F : 60F;
				
			//Work out which side the entry point is on and rotate accordingly
			if(entry.point.z == piece.size.z)
				yaw -= 90F;
			else if(entry.point.z == 0)
				yaw += 90F;
			else if(entry.point.x == piece.size.x)
				yaw += 180F;

			return yaw;
		}
		
		private float getXCoordOfNode(int i)
		{
			if(i == 0)
				return entries[0].point.x;
			if(i == numNodes + 1)
				return entries[1].point.x;
			return nodes[i - 1].x;
		}
		
		private float getYCoordOfNode(int i)
		{
			if(i == 0)
				return entries[0].point.y;
			if(i == numNodes + 1)
				return entries[1].point.y;
			return nodes[i - 1].y;
		}
		
		private float getZCoordOfNode(int i)
		{
			if(i == 0)
				return entries[0].point.z;
			if(i == numNodes + 1)
				return entries[1].point.z;
			return nodes[i - 1].z;
		}
		
		private float getDistance(float x, float y, float z)
		{
			return (float) Math.sqrt(x * x + y * y + z * z);
		}

		public float getX(float lambda) 
		{
			float distance = lambda * totalLength;
			for(int i = 0; i < numNodes + 1; i++)
			{
				if(distance < lengths[i])
				{
					float mu = distance / lengths[i];
					return mu * getXCoordOfNode(i + 1) + (1 - mu) * getXCoordOfNode(i);
				}
				distance -= lengths[i];
			}
			return getXCoordOfNode(numNodes + 1);
		}

		public float getY(float lambda) 
		{
			float distance = lambda * totalLength;
			for(int i = 0; i < numNodes + 1; i++)
			{
				if(distance < lengths[i])
				{
					float mu = distance / lengths[i];
					return mu * getYCoordOfNode(i + 1) + (1 - mu) * getYCoordOfNode(i);
				}
				distance -= lengths[i];
			}
			return getYCoordOfNode(numNodes + 1);
		}

		public float getZ(float lambda) 
		{
			float distance = lambda * totalLength;
			for(int i = 0; i < numNodes + 1; i++)
			{
				if(distance < lengths[i])
				{
					float mu = distance / lengths[i];
					return mu * getZCoordOfNode(i + 1) + (1 - mu) * getZCoordOfNode(i);
				}
				distance -= lengths[i];
			}
			return getZCoordOfNode(numNodes + 1);
		}
		
		private float getYawAtNode(int i)
		{
			return yaw[i];
		}
		
		public float getYaw(float lambda)
		{
			float distance = lambda * totalLength;
			for(int i = 0; i < numNodes + 1; i++)
			{
				if(distance < lengths[i])
				{
					float mu = distance / lengths[i];
					return mu * getYawAtNode(i + 1) + (1 - mu) * getYawAtNode(i);
				}
				distance -= lengths[i];
			}
			return getYawAtNode(numNodes + 1);
		}
	}
	
	public static class Switch
	{
		/** This is the point a switch controller must be placed at */
		public Vector3i position;
		/** The route to take when this switch is powered on */
		public int onRouteID;
		/** The route to take when this switch is powered off */
		public int offRouteID;
		
		public Switch(int x, int y, int z, int i, int j)
		{
			position = new Vector3i(x, y, z);
			onRouteID = i; 
			offRouteID = j;
		}
	}
	
	/** For obtaining a track piece from the given shortName */
	public static TrackPiece getPiece(String s)
	{
		for(TrackPiece piece : pieces)
		{
			if(piece.shortName.equals(s))
				return piece;
		}
		return null;
	}
}
