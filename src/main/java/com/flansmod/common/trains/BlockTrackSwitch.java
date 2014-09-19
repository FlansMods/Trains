package com.flansmod.common.trains;

import java.util.Arrays;
import java.util.Random;

import com.flansmod.common.FlansMod;
import com.flansmod.common.guns.boxes.ItemGunBox;
import com.flansmod.common.network.PacketTrackData;
import com.flansmod.common.vector.Vector3i;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrackSwitch extends BlockContainer 
{
	public static int RENDERID = RenderingRegistry.getNextAvailableRenderId();
	public SwitchType type;
	
	public BlockTrackSwitch(SwitchType t) 
	{
		super(Material.circuits);
		type = t;
		setCreativeTab(FlansMod.tabFlanTrains);
		GameRegistry.registerBlock(this, ItemSwitch.class, "switch." + type.shortName);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int i)
	{
		return new TileEntitySwitch();
	}
	
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    @Override
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return true;
    }
    
    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.doesBlockHaveSolidTopSurface(par1World, par2, par3 - 1, par4) || BlockFence.func_149825_a(par1World.getBlock(par2, par3 - 1, par4));
    }

	public boolean getSwitchState(IBlockAccess world, int x, int y, int z)
	{
		return world.getBlockMetadata(x, y, z) == 1;
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitPointOffsetX, float hitPointOffsetY, float hitPointOffsetZ)
    {
		//If this is a manual switch, switch it
		if(!world.isRemote && type.mode == EnumSwitchMode.MANUAL)
		{
			world.setBlock(x, y, z, this, 1 - world.getBlockMetadata(x, y, z), 3);
			checkSwitchState(world, x, y, z);
		}
        return true;
    }
	
	@Override
    public int tickRate(World world)
    {
        return 5;
    }
	
    public void checkSwitchState(World world, int x, int y, int z) 
    {
    	//Check all track pieces in the world
    	for(Track track : FlansMod.trainHandler.trainSpaces.get(world).tracks.values())
    	{
    		//Check each switch input point on that piece of track
    		for(int i = 0; i < track.piece.numSwitches; i++)
    		{
    			Vector3i switchPos = track.piece.switches[i].position;
    			//If the switch input point is this block, then set the switch to have the same state as this block
    			if(track.x + switchPos.x == x && track.y + switchPos.y == y && track.z + switchPos.z == z)	
    				if(track.setSwitchState(i, getSwitchState(world, x, y, z)))
    					FlansMod.packetHandler.sendToAllAround(new PacketTrackData(Arrays.asList(new Track[] {track})), new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, TrainSpace.trackingDistance));
    		}
    	}
    }
	
	@Override
	public int getRenderType()
	{
		return RENDERID;
	}

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
    }
}
