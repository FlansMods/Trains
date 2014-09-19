package com.flansmod.common.trains;

import java.util.ArrayList;
import java.util.List;

import com.flansmod.common.FlansMod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBedding extends Block 
{
	private IIcon bedding;
	
	public BlockBedding() 
	{
		super(Material.ground);
		setBlockBounds(0, 0, 0, 1, 1, 1);
		setCreativeTab(FlansMod.tabFlanTrains);
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return false;
	}
	
	@Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
		TrainSpace space = FlansMod.trainHandler.trainSpaces.get(world);
		for(Track track : space.tracks.values())
		{
			for(int i = 0; i < track.piece.size.x; i++)
			{
				for(int j = 0; j < track.piece.size.z; j++)
				{
					if(track.piece.needBedding[i][j] && x == track.x + i && y == track.y - 1 && z == track.z + j)
						return false;
				}
			}
		}
		super.removedByPlayer(world, player, x, y, z);
		return true;
    }
				
	@Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) 
    {
		world.setBlockToAir(x, y + 1, z);
		world.setBlockToAir(x, y + 2, z);
		world.setBlockToAir(x, y + 3, z);
    }
	
	@Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return 0;
    }
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) 
	{
		world.setBlock(x, y + 1, z, FlansMod.railBlock, 1, 3);		
		world.setBlock(x, y + 2, z, FlansMod.railBlock, 2, 3);		
		world.setBlock(x, y + 3, z, FlansMod.railBlock, 3, 3);		
	}
	
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int i, int j)
    {
    	return bedding;
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
    	bedding = register.registerIcon("FlansMod:" + "railBedding");
    }
    	
	/*
	@Override
	public void addCreativeItems(ArrayList itemList)
	{
		itemList.add(new ItemStack(this));
	}
	*/
}
