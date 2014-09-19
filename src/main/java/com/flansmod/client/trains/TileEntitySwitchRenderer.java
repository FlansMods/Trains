package com.flansmod.client.trains;

import org.lwjgl.opengl.GL11;

import com.flansmod.client.FlansModResourceHandler;
import com.flansmod.common.trains.BlockTrackSwitch;
import com.flansmod.common.trains.SwitchType;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class TileEntitySwitchRenderer extends TileEntitySpecialRenderer 
{
/*
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) 
	{
		if(block instanceof BlockTrackSwitch)
		{
			SwitchType type = ((BlockTrackSwitch)block).type;
			//Minecraft.getMinecraft().renderEngine.bindTexture(FlansModResourceHandler.getTexture(type));
			if(type.model != null)
				type.model.render(false);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) 
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, z);
		if(block instanceof BlockTrackSwitch)
		{
			SwitchType type = ((BlockTrackSwitch)block).type;
			//Minecraft.getMinecraft().renderEngine.bindTexture(FlansModResourceHandler.getTexture(type));
			if(type.model != null)
				type.model.render(((BlockTrackSwitch)block).getSwitchState(world, x, y, z));
		}
		GL11.glPopMatrix();
		return false;
	}*/

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float var8) 
	{
		GL11.glPushMatrix();
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		GL11.glTranslated(x, y, z);
		SwitchType type = ((BlockTrackSwitch)(te.blockType)).type;
		bindTexture(FlansModResourceHandler.getTexture(type));
		if(type.model != null)
			type.model.render(((BlockTrackSwitch)te.blockType).getSwitchState(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord));
		GL11.glPopMatrix();

	}

}
