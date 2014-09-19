package com.flansmod.client.trains;

import org.lwjgl.opengl.GL11;

import com.flansmod.client.FlansModResourceHandler;
import com.flansmod.common.FlansMod;
import com.flansmod.common.trains.D4;
import com.flansmod.common.trains.ItemTrack;
import com.flansmod.common.trains.Track;
import com.flansmod.common.trains.TrackPiece;
import com.flansmod.common.trains.TrainSpace;
import com.flansmod.common.vector.Vector3i;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

public class TrackRenderer implements IItemRenderer
{
	public TrackRenderer()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event)
	{
		//Get the world
		World world = Minecraft.getMinecraft().theWorld;
		if(world == null || FlansMod.trainHandler == null)
			return;
		TrainSpace trainSpace = FlansMod.trainHandler.trainSpaces.get(world);
		if(trainSpace == null)
			return;
		//Get the camera frustrum for clipping
        EntityLivingBase camera = Minecraft.getMinecraft().renderViewEntity;
        double x = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * event.partialTicks;
        double y = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * event.partialTicks;
        double z = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * event.partialTicks;
        Frustrum frustrum = new Frustrum();
        frustrum.setPosition(x, y, z);
        
        //Push
        GL11.glPushMatrix();
        //Setup lighting
        Minecraft.getMinecraft().entityRenderer.enableLightmap((double)0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        
        GL11.glTranslatef(-(float)x, -(float)y, -(float)z);
        
		for(Track track : trainSpace.tracks.values())
		{
			if(frustrum.isBoundingBoxInFrustum(track.getBoundingBox()))
				renderTrack(event.context, world, track);
		}
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ItemStack playerItemStack = player.getCurrentEquippedItem();
		if(playerItemStack != null && playerItemStack.getItem() instanceof ItemTrack)
		{
			ItemTrack trackItem = (ItemTrack)playerItemStack.getItem();
			TrackPiece piece = trackItem.getTrackPiece(playerItemStack);
			if(piece != null)
			{
				D4 orientation = piece.orientation;
				Vector3i corner = trackItem.getCorner(world, player, playerItemStack, piece);
				if(corner != null)
				{
					renderTrack(event.context, world, corner.x, corner.y, corner.z, piece, new boolean[piece.numSwitches]);
					renderBeddingOverlay(event.context, world, corner.x, corner.y, corner.z, piece);
				}
			}
		}
		
		//Reset Lighting
		Minecraft.getMinecraft().entityRenderer.disableLightmap((double)0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//Pop
		GL11.glPopMatrix();
	}
	
	private void renderTrack(RenderGlobal renderer, World world, Track track)
	{
		renderTrack(renderer, world, track.x, track.y, track.z, track.piece, track.switchStates);
	}
	
	private void renderTrack(RenderGlobal renderer, World world, int x, int y, int z, TrackPiece piece, boolean[] switchStates)
	{
		GL11.glPushMatrix();
		
		Minecraft.getMinecraft().renderEngine.bindTexture(getTexture(piece));
		GL11.glColor3f(1F, 1F, 1F);
				
		if(world != null)
		{
	        int i = MathHelper.floor_double(x + (float)(piece.size.x / 2));
	        int j = MathHelper.floor_double(y);
	        int k = MathHelper.floor_double(z + (float)(piece.size.z / 2));
	        int r = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
	        int p = r % 65536;
	        int q = r / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)p, (float)q);
		}

		GL11.glTranslatef(x + (float)(piece.size.x) / 2F, y, z + (float)(piece.size.z) / 2F);
		
        switch(piece.orientation)
        {
        case s : case rs : case rrs : case rrrs : GL11.glScalef(-1F, 1F, 1F); GL11.glCullFace(GL11.GL_FRONT); break;
        }
		
        switch(piece.orientation)
        {
        case rrr : case rrrs : GL11.glRotatef(270F, 0F, 1F, 0F); break;
        case rr : case rrs : GL11.glRotatef(180F, 0F, 1F, 0F); break;
        case r : case rs : GL11.glRotatef(90F, 0F, 1F, 0F); break;
        }

        if(piece.model != null)
        	piece.model.render(switchStates);
        
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        
        if(FlansMod.DEBUG)
        {
	        GL11.glPushMatrix();
	        GL11.glDisable(GL11.GL_DEPTH_TEST);
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glTranslatef(x, y, z);
	        for(int i = 0; i < piece.numRoutes; i++)
	        {
	        	GL11.glLineWidth(5F);
	        	GL11.glPointSize(10F);
	        	
	        	TrackPiece.Route route = piece.routes[i];
	        	
	            GL11.glColor3f(1F, 0F, 0F);
	            
	            for(int j = 0; j < piece.numSwitches; j++)
	            {
	            	TrackPiece.Switch trackSwitch = piece.switches[j];
	            	if(trackSwitch.offRouteID == i)
	            		GL11.glColor3f(switchStates[j] ? 1F : 0F, 0F, 0F);
	            	if(trackSwitch.onRouteID == i)
	            		GL11.glColor3f(!switchStates[j] ? 1F : 0F, 0F, 0F);
	            }
	    	    
	    	    /*
	            GL11.glBegin(GL11.GL_LINE_STRIP);
	    	    GL11.glVertex3f(route.entries[0].point.x, route.entries[0].point.y, route.entries[0].point.z);
	    	    for(int j = 0; j < route.numNodes; j++)
	    	    {
	    	    	GL11.glVertex3f(route.nodes[j].x, route.nodes[j].y, route.nodes[j].z);
	    	    }
	    	    GL11.glVertex3f(route.entries[1].point.x, route.entries[1].point.y, route.entries[1].point.z);
	    	    GL11.glEnd();
	    	    */
	            
	    	    GL11.glBegin(GL11.GL_LINES);
	    	    
	    	    //Entry point
	    	    GL11.glVertex3f(route.entries[0].point.x, route.entries[0].point.y, route.entries[0].point.z);
    	    	GL11.glVertex3f(route.entries[0].point.x + (float)Math.cos(route.yaw[0] * 3.14159265F / 180F), route.entries[0].point.y, route.entries[0].point.z + (float)Math.sin(route.yaw[0] * 3.14159265F / 180F));
	    	    //Inner nodes
	    	    for(int j = 0; j < route.numNodes; j++)
	    	    {
	    	    	GL11.glVertex3f(route.nodes[j].x, route.nodes[j].y, route.nodes[j].z);
	    	    	GL11.glVertex3f(route.nodes[j].x + (float)Math.cos(route.yaw[j + 1] * 3.14159265F / 180F), route.nodes[j].y, route.nodes[j].z + (float)Math.sin(route.yaw[j + 1] * 3.14159265F / 180F));
	    	    }
	    	    //Exit point
	    	    GL11.glVertex3f(route.entries[1].point.x, route.entries[1].point.y, route.entries[1].point.z);
    	    	GL11.glVertex3f(route.entries[1].point.x + (float)Math.cos(route.yaw[route.numNodes + 1] * 3.14159265F / 180F), route.entries[1].point.y, route.entries[1].point.z + (float)Math.sin(route.yaw[route.numNodes + 1] * 3.14159265F / 180F));

	    	    GL11.glEnd();
	    	    
	    	    GL11.glColor3f(route.entries[0].diagonal ? 0F : 1F, 0F, 1F);
	    	    
	    	    
	    	    GL11.glBegin(GL11.GL_POINTS);
	    	    GL11.glVertex3f(route.entries[0].point.x, route.entries[0].point.y, route.entries[0].point.z);
	    	    GL11.glEnd();
	    	    
	    	    GL11.glColor3f(route.entries[1].diagonal ? 0F : 1F, 0F, 1F);
	    	    
	    	    GL11.glBegin(GL11.GL_POINTS);
	    	    GL11.glVertex3f(route.entries[1].point.x, route.entries[1].point.y, route.entries[1].point.z);
	    	    GL11.glEnd();
	    	    
	        }
	        
	        GL11.glColor3f(0F, 1F, 0F);
        	GL11.glBegin(GL11.GL_POINTS);
	        for(int i = 0; i < piece.numSwitches; i++)
	        {
		    	GL11.glVertex3f(piece.switches[i].position.x + 0.5F, piece.switches[i].position.y, piece.switches[i].position.z + 0.5F);
	        }
	        GL11.glEnd();
	        
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glPopMatrix();
        }
	}
	
	private void renderBeddingOverlay(RenderGlobal renderer, World world, int x, int y, int z, TrackPiece piece)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for(int i = x; i < x + piece.size.x; i++)
		{
			int j = y - 1;
			for(int k = z; k < z + piece.size.z; k++)
			{
				if(piece.needBedding[i - x][k - z])
				{
					if(ItemTrack.isBedding(world, i, j, k))
					{
						if(ItemTrack.isFreeBedding(world, i, j, k))
							GL11.glColor4f(0F, 1F, 0F, 0.3F);
						else GL11.glColor4f(1F, 1F, 0F, 0.3F);
					}
					else GL11.glColor4f(1F, 0F, 0F, 0.3F);
					renderAABB(AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1));
				}
			}
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	protected ResourceLocation getTexture(TrackPiece piece) 
	{
		return FlansModResourceHandler.getTexture(piece);
	}
	
    public static void renderAABB(AxisAlignedBB par0AxisAlignedBB)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.minX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.minZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.maxY, par0AxisAlignedBB.maxZ);
        tessellator.addVertex(par0AxisAlignedBB.maxX, par0AxisAlignedBB.minY, par0AxisAlignedBB.maxZ);
        tessellator.draw();
    }

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) 
	{
		switch(type)
		{
		case EQUIPPED : case EQUIPPED_FIRST_PERSON :  case INVENTORY : case ENTITY : return true;
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) 
	{
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) 
	{
		if(!(item.getItem() instanceof ItemTrack))
			return;
		
		TrackPiece piece = ((ItemTrack)item.getItem()).getTrackPiece(item);
		if(piece == null)
			return;
		
		switch(type)
		{
			case EQUIPPED : 
			{
				GL11.glPushMatrix();
				
				float maxScale = Math.max(piece.size.x, Math.max(piece.size.y, piece.size.z));
				
				GL11.glTranslatef(0.5F, 0.5F, 0F);
				
				GL11.glRotatef(-90F, 1F, 0F, 0F);
				GL11.glScalef(1F / maxScale, 1F / maxScale, 1F / maxScale);
							
				renderTrack(Minecraft.getMinecraft().renderGlobal, null, -piece.size.x / 2, -piece.size.y / 2, -piece.size.z / 2, piece, new boolean[piece.numSwitches]);
				
				GL11.glPopMatrix();
				
				break;
			}
			case EQUIPPED_FIRST_PERSON : 
			{
				GL11.glPushMatrix();
				
				float maxScale = Math.max(piece.size.x, Math.max(piece.size.y, piece.size.z));

				EntityPlayer player = (EntityPlayer)data[1];
				
				GL11.glTranslatef(1F, 0.5F, 0F);
				
				GL11.glRotatef(-90F, 1F, 0F, 0F);
				GL11.glRotatef(90F, 0F, 0F, 1F);
				GL11.glRotatef(player.rotationYaw, 0F, 1F, 0F);
				GL11.glScalef(1F / maxScale, 1F / maxScale, 1F / maxScale);
							
				renderTrack(Minecraft.getMinecraft().renderGlobal, null, -piece.size.x / 2, -piece.size.y / 2, -piece.size.z / 2, piece, new boolean[piece.numSwitches]);
				
				GL11.glPopMatrix();
				
				break;
			}
			case INVENTORY :
			{
				GL11.glPushMatrix();
				
				float maxScale = Math.max(piece.size.x, Math.max(piece.size.y, piece.size.z));
				
				GL11.glTranslatef(8F, 8F, 0F);
				
				GL11.glRotatef(-90F, 1F, 0F, 0F);
				//GL11.glRotatef(180F, 0F, 0F, 1F);
				GL11.glScalef(16F / maxScale, 16F / maxScale, 16F / maxScale);
							
				renderTrack(Minecraft.getMinecraft().renderGlobal, null, -piece.size.x / 2, -piece.size.y / 2, -piece.size.z / 2, piece, new boolean[piece.numSwitches]);
				
				GL11.glPopMatrix();
				
				break;
			}
			case ENTITY :
			{
				GL11.glPushMatrix();
				
				float maxScale = Math.max(piece.size.x, Math.max(piece.size.y, piece.size.z));
				
				EntityItem entity = (EntityItem)data[1];
				
				GL11.glTranslatef(0F, 0.4F + (float)Math.sin(entity.age / 8F) / 10F, 0F);

				GL11.glRotatef(entity.age * 3F, 0F, 1F, 0F);
				GL11.glRotatef(-90F, 1F, 0F, 0F);
				
				GL11.glScalef(1F / maxScale, 1F / maxScale, 1F / maxScale);
							
				renderTrack(Minecraft.getMinecraft().renderGlobal, null, -piece.size.x / 2, -piece.size.y / 2, -piece.size.z / 2, piece, new boolean[piece.numSwitches]);
				
				GL11.glPopMatrix();
				
				break;
			}
		}
	}
}
