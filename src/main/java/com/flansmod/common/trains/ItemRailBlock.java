package com.flansmod.common.trains;

import com.flansmod.common.FlansMod;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Only the bedding block will use this class. Tracks will be handled separately by their own item */
public class ItemRailBlock extends ItemBlock 
{	
	private Block block;
	
	public ItemRailBlock(Block block) 
	{
		super(block);
		this.block = block;
	}

	@Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float f1, float f2, float f3)
    {
        Block blockHit = world.getBlock(x, y, z);

        if(blockHit == Blocks.snow && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if(blockHit != Blocks.vine && blockHit != Blocks.tallgrass && blockHit != Blocks.deadbush
                && (blockHit == null || !blockHit.isReplaceable(world, x, y, z)))
        {
        	switch(side)
        	{
        	case 0 : y--; break;
        	case 1 : y++; break;
        	case 2 : z--; break;
        	case 3 : z++; break;
        	case 4 : x--; break;
        	case 5 : x++; break;
        	}
        }

        if(stack.stackSize <= 0)
            return false;

        if(!player.canPlayerEdit(x, y, z, side, stack))
            return false;
        
        if(y > 252)
            return false;
        
        if(world.canPlaceEntityOnSide(block, x, y + 0, z, false, side, player, stack)
		&& world.canPlaceEntityOnSide(FlansMod.railBlock, x, y + 1, z, false, side, player, stack)
		&& world.canPlaceEntityOnSide(FlansMod.railBlock, x, y + 2, z, false, side, player, stack)
		&& world.canPlaceEntityOnSide(FlansMod.railBlock, x, y + 3, z, false, side, player, stack))
        {
            int k1 = block.onBlockPlaced(world, x, y, z, side, f1, f2, f3, 0);

            if(placeBlockAt(stack, player, world, x, y + 0, z, side, f1, f2, f3, 0))
            {
                world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                stack.stackSize--;
            }

            return true;
        }
        
        return false;
    }
	
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, Block block, int metadata)
    {
       if (!world.setBlock(x, y, z, block, metadata, 3))
       {
           return false;
       }

       if (world.getBlock(x, y, z) == block)
       {
           block.onBlockPlacedBy(world, x, y, z, player, stack);
           block.onPostBlockPlaced(world, x, y, z, metadata);
       }

       return true;
    }
}
