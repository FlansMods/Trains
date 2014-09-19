package com.flansmod.common.trains;

import java.util.List;

import com.flansmod.common.FlansMod;
import com.flansmod.common.network.PacketRotateHeldTrack;
import com.flansmod.common.vector.Vector3i;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemTrack extends Item 
{
	public ItemTrack() 
	{
		super();
		setCreativeTab(FlansMod.tabFlanTrains);
		setHasSubtypes(true);
	}
	
	@Override
	public boolean isFull3D()
	{
		return true;
	}
	
	@Override
	public boolean getShareTag()
	{
		return true;
	}

	/** Getter for the track piece this track represents */
	public TrackPiece getTrackPiece(ItemStack stack)
	{
		if(!stack.stackTagCompound.hasKey("Piece"))
			return null;
		TrackPiece piece = TrackPiece.getPiece(stack.stackTagCompound.getString("Piece"));
		return piece == null ? null : piece.orientations.get(D4.get(stack.stackTagCompound.getString("Sym")));
	}
	
	/** Setter for the track piece this item represents */
	public void setTrackPiece(ItemStack stack, TrackPiece piece)
	{
		if(stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();
		stack.stackTagCompound.setString("Piece", piece.shortName);
		stack.stackTagCompound.setString("Sym", piece.orientation.toString());
	}
	
    /** Make sure that all track pieces are available in the creative menu */
    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List list)
    {
    	for(TrackPiece piece : TrackPiece.pieces)
    	{
    		ItemStack trackStack = new ItemStack(this);
    		trackStack.stackTagCompound = new NBTTagCompound();
    		trackStack.stackTagCompound.setString("Piece", piece.shortName);
    		trackStack.stackTagCompound.setString("Sym", piece.orientation.toString());
    		list.add(trackStack);
    	}
    }
    
	@Override
    public String getUnlocalizedName(ItemStack stack)
    {
		TrackPiece piece = getTrackPiece(stack);
        return piece == null ? "None" : super.getUnlocalizedName() + (stack == null ? "" : ("." + piece.name));
    }
	
	@Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		Vector3i corner = getCorner(world, player, stack, getTrackPiece(stack));
		if(isSufficientBedding(world, corner.x, corner.y, corner.z, getTrackPiece(stack)) && FlansMod.trainHandler.addTrack(world, new Track(getTrackPiece(stack), corner.x, corner.y, corner.z)) && !player.capabilities.isCreativeMode)
		{
			stack.stackSize--;
		}
		return stack;
    }
	
	/** Find the lower left corner of the track position the player is looking at.
	 * The centre of their view should be where the centre of the track lies */
	public Vector3i getCorner(World world, EntityPlayer player, ItemStack stack, TrackPiece piece)
	{
		//Raytracing
        float cosYaw = MathHelper.cos(-player.rotationYaw * 0.01745329F - 3.141593F);
        float sinYaw = MathHelper.sin(-player.rotationYaw * 0.01745329F - 3.141593F);
        float cosPitch = -MathHelper.cos(-player.rotationPitch * 0.01745329F);
        float sinPitch = MathHelper.sin(-player.rotationPitch * 0.01745329F);
        double length = player.capabilities.isCreativeMode ? 50D : 20D;
        Vec3 posVec = Vec3.createVectorHelper(player.posX, player.posY + 1.62D - (double)player.yOffset, player.posZ);        
        Vec3 lookVec = posVec.addVector(sinYaw * cosPitch * length, sinPitch * length, cosYaw * cosPitch * length);
        MovingObjectPosition hit = world.rayTraceBlocks(posVec, lookVec, true);
        
        //Result check
        if(hit == null)
        {
            return null;
        }
        if(hit.typeOfHit == MovingObjectType.BLOCK)
        {
        	int i = MathHelper.floor_double(hit.hitVec.xCoord - (double)piece.size.x / 2D + 0.5D);
            //int i = movingobjectposition.blockX;
            int j = hit.blockY;
            //int k = movingobjectposition.blockZ;
            int k = MathHelper.floor_double(hit.hitVec.zCoord - (double)piece.size.z / 2D + 0.5D);
            return new Vector3i(i, j + 1, k);
        }
        
        return null;
	}
	
	public boolean isSufficientBedding(World world, int x, int y, int z, TrackPiece piece)
	{
		for(int i = 0; i < piece.size.x; i++)
		{
			int j = y - 1;
			for(int k = 0; k < piece.size.z; k++)
			{
				if(piece.needBedding[i][k] && !isFreeBedding(world, x + i, j, z + k))
					return false;
			}
		}
		return true;
	}
	
	public static boolean isFreeBedding(World world, int i, int j, int k)
	{
		if(!isBedding(world, i, j, k))
			return false;
		TrainSpace trainSpace = FlansMod.trainHandler.trainSpaces.get(world);
		//Iterate over tracks
		for(Track track : trainSpace.tracks.values())
		{
			//If they have overlapping outer bounds
			if(track.x <= i && i < track.x + track.piece.size.x && track.y <= j + 1 && j + 1 < track.y + track.piece.size.y && track.z <= k && k < track.z + track.piece.size.z)
			{
				//Check the inner bounds
				if(track.piece.needBedding[i - track.x][k - track.z])
					return false;
			}
		}
		return true;
	}
	
	public static boolean isBedding(World world, int i, int j, int k)
	{
		return world.getBlock(i, j, k) == FlansMod.beddingBlock;
	}

	/** When the player presses the rotate or reflect button, do so to the track they are holding */
	public static void rotateCurrentRail(EntityPlayer player, boolean reflect) 
	{
		if(player.worldObj.isRemote)
		{
			//Send packet
			FlansMod.getPacketHandler().sendToServer(new PacketRotateHeldTrack(reflect));
		}
		ItemStack playerItemStack = player.getCurrentEquippedItem();
		if(playerItemStack != null && playerItemStack.getItem() instanceof ItemTrack)
		{
			ItemTrack trackItem = (ItemTrack)playerItemStack.getItem();
			TrackPiece piece = trackItem.getTrackPiece(playerItemStack);
			D4 orientation = piece.orientation;
			if(reflect)
				orientation = D4.compose(orientation, D4.s);
			else orientation = D4.compose(orientation, D4.r);
			piece = piece.orientations.get(orientation);
			trackItem.setTrackPiece(playerItemStack, piece);
		}
	}
	
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
    }
}
