package com.flansmod.common.driveables.mechas;

import java.util.List;

import com.flansmod.common.FlansMod;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMechaAddon extends Item 
{
	public ItemMechaAddon(MechaItemType type1)
	{
		type = type1;
		setMaxStackSize(1);
		type.item = this;
		setCreativeTab(FlansMod.tabFlanMechas);
		GameRegistry.registerItem(this, type.shortName, FlansMod.MODID);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		if(type.description != null)
		{
			for(String s : type.description.split("_"))
				list.add(s);
		}
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

	public MechaItemType type;
}
