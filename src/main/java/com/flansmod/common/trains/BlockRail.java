package com.flansmod.common.trains;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Rail block. Has different uses dependent on metadata. Actual track data is stored in the Train Space 
 * 1 : Rail, 2 : Air (To stop you building blocks in the way of the rather tall trains), 
 * 3 : Air / Cables (For when electric trains are implemented) */
public class BlockRail extends BlockAir
{
	public BlockRail() 
	{
		super();
	}
	
	@Override
    public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
    public boolean isBlockSolid(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        return false;
    }
	
	@Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
    {
		
    }
	
    @Override
    public boolean canCollideCheck(int metadata, boolean boat)
    {
        return false;
    }
	
	@Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }
	
	@Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }
		
	@Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        return 0;
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int i, int j)
    {
    	return null;
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
    }
}
