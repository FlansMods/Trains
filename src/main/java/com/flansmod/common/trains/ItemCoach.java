package com.flansmod.common.trains;

import java.util.List;

import com.flansmod.client.debug.EntityDebugAABB;
import com.flansmod.client.debug.EntityDebugVector;
import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.DriveableData;
import com.flansmod.common.driveables.EntityPlane;
import com.flansmod.common.driveables.EnumDriveablePart;
import com.flansmod.common.parts.PartType;
import com.flansmod.common.types.EnumType;
import com.flansmod.common.vector.Vector3f;
import com.flansmod.common.vector.Vector3i;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemCoach extends Item 
{
	public CoachType type;
	
    public ItemCoach(CoachType type)
    {
        maxStackSize = 1;
		this.type = type;
		type.item = this;
		setCreativeTab(FlansMod.tabFlanTrains);
		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
    }
    
    @Override
	/** Make sure client and server side NBTtags update */
	public boolean getShareTag()
	{
		return true;
	}
	
	private NBTTagCompound getTagCompound(ItemStack stack, World world)
	{
		if(stack.stackTagCompound == null)
		{
			stack.stackTagCompound = new NBTTagCompound();
			stack.stackTagCompound.setString("Type", type.shortName);
		}
		return stack.stackTagCompound;
	}
	
	@Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
		//Raytracing
        float cosYaw = MathHelper.cos(-player.rotationYaw * 0.01745329F - 3.141593F);
        float sinYaw = MathHelper.sin(-player.rotationYaw * 0.01745329F - 3.141593F);
        float cosPitch = -MathHelper.cos(-player.rotationPitch * 0.01745329F);
        float sinPitch = MathHelper.sin(-player.rotationPitch * 0.01745329F);
        double length = player.capabilities.isCreativeMode ? 50D : 20D;
        Vec3 posVec = Vec3.createVectorHelper(player.posX, player.posY + 1.62D - (double)player.yOffset, player.posZ);        
        Vec3 lookVec = Vec3.createVectorHelper(sinYaw * cosPitch * length, sinPitch * length, cosYaw * cosPitch * length);
        lookVec = lookVec.addVector(player.posX, player.posY + 1.62D - (double)player.yOffset, player.posZ);
        
        TrainSpace space = FlansMod.trainHandler.trainSpaces.get(world);
        Track track = space.rayTrace(posVec, lookVec);
        
        if(world.isRemote && FlansMod.DEBUG && track != null)
        {
        	world.spawnEntityInWorld(new EntityDebugAABB(world, new Vector3f(track.x, track.y, track.z), new Vector3f(track.piece.size.x, track.piece.size.y, track.piece.size.z), 40, 0F, 1F, 0F));
        }
        
        if(track != null)
        {
            if(!world.isRemote)
            {
            	DriveableData data = getCoachData(itemstack, world);
            	if(data != null)
            	{
            		Coach coach = new Coach(space, type, track, itemstack.getItemDamage() == 0);
            		if(coach.bogiePositions[0] == null || coach.bogiePositions[1] == null)
            			return itemstack;
            		Train train = new Train(coach);
            		space.addTrain(train);
            		world.spawnEntityInWorld(new EntityCoach(world, data, coach));
            	}
            }
			if(!player.capabilities.isCreativeMode)
			{	
				itemstack.stackSize--;
			}
        }

        
        /*
        if(movingobjectposition.typeOfHit == MovingObjectType.BLOCK)
        {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            if(!world.isRemote)
            {
            	DriveableData data = getCoachData(itemstack, world);
            	//if(data != null)
            	//	world.spawnEntityInWorld(new EntityCoach(world, (double)i + 0.5F, (double)j + 2.5F, (double)k + 0.5F, entityplayer, type, data));
            }
			if(!entityplayer.capabilities.isCreativeMode)
			{	
				itemstack.stackSize--;
			}
        }
        */
        return itemstack;
    }
	
	public Entity spawnCoach(World world, double x, double y, double z, ItemStack stack)
    {
    	DriveableData data = getCoachData(stack, world);
    	if(data != null)
    	{
	    	//Entity entity = new EntityPlane(world, x, y, z, type, data);
	    	//if(!world.isRemote)
	        //{
			//	world.spawnEntityInWorld(entity);
	        //}
	    	//return entity;
    	}
    	return null;
    }
		
	public DriveableData getCoachData(ItemStack itemstack, World world)
    {
		return new DriveableData(getTagCompound(itemstack, world));
    }
		
    @Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
    	return type.colour;
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister icon) 
    {
    	itemIcon = icon.registerIcon("FlansMod:" + type.iconPath);
    }
    
    /** Make sure that creatively spawned coaches have nbt data */
    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List list)
    {
    	ItemStack coachStack = new ItemStack(item, 1, 0);
    	NBTTagCompound tags = new NBTTagCompound();
    	tags.setString("Type", type.shortName);
    	coachStack.stackTagCompound = tags;
        list.add(coachStack);
    }
}
