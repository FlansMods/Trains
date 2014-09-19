package com.flansmod.common.trains;

import com.flansmod.common.trains.TrackPiece.Route;
import com.flansmod.common.vector.Vector3i;

public class TrackGraph 
{
	private int size;
	private Node[] nodes;
	private byte[] edges;
	
	public TrackGraph()
	{
		size = 8;
		nodes = new Node[size];
		edges = new byte[size * size / 8];
	}
	
	public void addTrack(Track track)
	{
		Node[] entryPointNodes = new Node[track.piece.numEntries];
		
		//Add each entry point as a node
		for(int i = 0; i < track.piece.numEntries; i++)
		{
			Vector3i entryPoint = track.piece.entries[i].point;
			int x = entryPoint.x + track.x;
			int y = entryPoint.y + track.y;
			int z = entryPoint.z + track.z;
			
			int lowestFreeNodeID = size;
			
			//Search for this node in our set of nodes, but also find the lowest free node ID, in case our node does not appear
			for(int j = size - 1; j >= 0; j--)
			{
				Node node = nodes[j];
				if(node == null)
					lowestFreeNodeID = j;
				else if(node.x == x && node.y == y && node.z == z)
				{
					entryPointNodes[i] = node;
					break;
				}
			}
			
			//If we did not find our node in the existing node list, make a new node
			if(entryPointNodes[i] == null)
			{
				//If the list is full
				if(lowestFreeNodeID == size)
				{
					//Double the size of the list
					Node[] newNodes = new Node[size * 2];
					byte[] newEdges = new byte[size * size / 2];
					
					for(int j = 0; j < size; j++)
						newNodes[j] = nodes[j];
					
					for(int j = 0; j < size; j++)
						for(int k = 0; k < size / 8; k++)
							newEdges[j * size / 4 + k] = edges[j * size / 8 + k];
					
					nodes = newNodes;
					edges = newEdges;
					
					size *= 2;
				}
				
				//Create a new node
				nodes[lowestFreeNodeID] = new Node(lowestFreeNodeID, x, y, z);
				entryPointNodes[i] = nodes[lowestFreeNodeID];
			}
		}
		
		//Add routes
		Route[] routes = track.getRoutes();
		for(Route route : routes)
		{
			Node entryNode = entryPointNodes[route.entries[0].ID];
			Node exitNode = entryPointNodes[route.entries[1].ID];
			
			setEdge(entryNode, exitNode, true);
			setEdge(exitNode, entryNode, true);
		}
	}
	
	public void setEdge(Node node1, Node node2, boolean edge)
	{
		int byteID = size / 8 * node1.ID + node2.ID / 8;
		int bytePos = node2.ID % 8;
		if(edge)
			edges[byteID] = (byte)(edges[byteID] | (1 << bytePos));
		else
			edges[byteID] = (byte)(edges[byteID] & ~(1 << bytePos));
	}
	
	public boolean getEdge(Node node1, Node node2)
	{
		int byteID = size / 8 * node1.ID + node2.ID / 8;
		int bytePos = node2.ID % 8;
		return (edges[byteID] & (1 << bytePos)) == 1;
	}
	
	public class Node
	{
		public int ID, x, y, z;
		
		public Node(int ID, int x, int y, int z)
		{
			this.ID = ID;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(o instanceof Node)
			{
				Node n = (Node)o;
				return this.x == n.x && this.y == n.y && this.z == n.z;
			}
			return false;
		}
	}
}
