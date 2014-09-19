package com.flansmod.client;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.flansmod.api.IControllable;
import com.flansmod.client.gui.GuiTeamScores;
import com.flansmod.client.gui.GuiTeamSelect;
import com.flansmod.client.model.GunAnimations;
import com.flansmod.common.FlansMod;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.network.PacketReload;
import com.flansmod.common.trains.ItemTrack;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class KeyInputHandler
{  
	//public static KeyBinding accelerateKey = new KeyBinding("Accelerate Key", Keyboard.KEY_W, "key.categories.movement");
	//public static KeyBinding decelerateKey = new KeyBinding("Decelerate Key", Keyboard.KEY_S, "key.categories.movement");
	//public static KeyBinding leftKey = new KeyBinding("Left Key", Keyboard.KEY_A, "key.categories.movement");
	//public static KeyBinding rightKey = new KeyBinding("Right Key", Keyboard.KEY_D, "key.categories.movement");
	//public static KeyBinding upKey = new KeyBinding("Up Key", Keyboard.KEY_SPACE, "key.categories.movement");
	public static KeyBinding downKey = new KeyBinding("Down key", Keyboard.KEY_LCONTROL, "key.categories.movement");
	//public static KeyBinding exitKey = new KeyBinding("Exit Key", Keyboard.KEY_LSHIFT, "key.categories.gameplay");
	public static KeyBinding inventoryKey = new KeyBinding("Inventory key", Keyboard.KEY_R, "key.categories.inventory");
	public static KeyBinding bombKey = new KeyBinding("Bomb Key", Keyboard.KEY_V, "key.categories.gameplay");
	public static KeyBinding gunKey = new KeyBinding("Gun Key", Keyboard.KEY_B, "key.categories.gameplay");
	public static KeyBinding controlSwitchKey = new KeyBinding("Control Switch key", Keyboard.KEY_C, "key.categories.gameplay");
	public static KeyBinding reloadKey = new KeyBinding("Reload key", Keyboard.KEY_R, "key.categories.gameplay");
	public static KeyBinding teamsMenuKey = new KeyBinding("Teams Menu Key", Keyboard.KEY_G, "key.categories.multiplayer");
	public static KeyBinding teamsScoresKey = new KeyBinding("Teams Scores Key", Keyboard.KEY_H, "key.categories.multiplayer");
	public static KeyBinding leftRollKey = new KeyBinding("Roll Left Key", Keyboard.KEY_Z, "key.categories.movement");
	public static KeyBinding rightRollKey = new KeyBinding("Roll Right Key", Keyboard.KEY_X, "key.categories.movement");
    public static KeyBinding gearKey = new KeyBinding("Gear Up / Down Key", Keyboard.KEY_L, "key.categories.movement");
    public static KeyBinding doorKey = new KeyBinding("Door Open / Close Key", Keyboard.KEY_K, "key.categories.movement");
    public static KeyBinding wingKey = new KeyBinding("Wing Reposition Key", Keyboard.KEY_J, "key.categories.movement");
    public static KeyBinding trimKey = new KeyBinding("Trim Key", Keyboard.KEY_O, "key.categories.movement");
    public static KeyBinding debugKey = new KeyBinding("Debug Key", Keyboard.KEY_F10, "key.categories.misc");
    public static KeyBinding reloadModelsKey = new KeyBinding("Reload Models Key", Keyboard.KEY_F9, "key.categories.misc");

	Minecraft mc;
	
	public KeyInputHandler()
	{
		//ClientRegistry.registerKeyBinding(accelerateKey);
		//ClientRegistry.registerKeyBinding(decelerateKey);
		//ClientRegistry.registerKeyBinding(leftKey);
		//ClientRegistry.registerKeyBinding(rightKey);
		//ClientRegistry.registerKeyBinding(upKey);
		ClientRegistry.registerKeyBinding(downKey);
		//ClientRegistry.registerKeyBinding(exitKey);
		ClientRegistry.registerKeyBinding(inventoryKey);
		ClientRegistry.registerKeyBinding(bombKey);
		ClientRegistry.registerKeyBinding(gunKey);
		ClientRegistry.registerKeyBinding(controlSwitchKey);
		ClientRegistry.registerKeyBinding(reloadKey);
		ClientRegistry.registerKeyBinding(teamsMenuKey);
		ClientRegistry.registerKeyBinding(teamsScoresKey);
		ClientRegistry.registerKeyBinding(leftRollKey);
		ClientRegistry.registerKeyBinding(rightRollKey);
		ClientRegistry.registerKeyBinding(gearKey);
		ClientRegistry.registerKeyBinding(doorKey);
		ClientRegistry.registerKeyBinding(wingKey);
		ClientRegistry.registerKeyBinding(trimKey);
		ClientRegistry.registerKeyBinding(debugKey);
		ClientRegistry.registerKeyBinding(reloadModelsKey);
		/*
		 *  TODO : Note. This information (hold key or single shot key) has been lost.
				true, // accelerate key
				true, // decelerate
				true, // left
				true, // right
				true, // up
				true, // down
				false, // exit
				false, // inventory
				true, // bomb
				true, // gun
				false, // control switch
				true, //left Roll
				true, //right Roll
                false, // gear
                false, // door
                false, //wing
                false, // trim button
				false, // teams menu
				false, // teams scores menu
				false, //reload
				false, //debug
				false //reloadModels
						});*/
		
		mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		if(FMLClientHandler.instance().isGUIOpen(GuiChat.class) || mc.currentScreen != null)
			return;
		
		EntityPlayer player = mc.thePlayer;
		Entity ridingEntity = player.ridingEntity;
		
		//Handle universal keys
		if(teamsMenuKey.isPressed())
		{
			mc.displayGuiScreen(new GuiTeamSelect());
			return;
		}
		if(teamsScoresKey.isPressed())
		{
			mc.displayGuiScreen(new GuiTeamScores());
			return;
		}
		if(reloadKey.isPressed() && FlansModClient.shootTime <= 0)
		{
			ItemStack currentItemstack = player.getCurrentEquippedItem();
			if(currentItemstack != null)
			{
				Item currentItem = currentItemstack.getItem();
				if(currentItem instanceof ItemGun)
					FlansMod.getPacketHandler().sendToServer(new PacketReload());
				if(currentItem instanceof ItemTrack)
					((ItemTrack)currentItem).rotateCurrentRail(player, mc.gameSettings.keyBindSneak.getIsKeyPressed());
			}
			return;
		}
		if(debugKey.isPressed())
		{
			FlansMod.DEBUG = !FlansMod.DEBUG;
		}
		if(reloadModelsKey.isPressed())
		{
			FlansModClient.reloadModels();
		}
		
		//Handle driving keys
		if(ridingEntity instanceof IControllable)
		{
			IControllable riding = (IControllable)ridingEntity;
			if(mc.gameSettings.keyBindForward.isPressed())//if(accelerateKey.isPressed())
				riding.pressKey(0, player);
			if(mc.gameSettings.keyBindBack.isPressed())//if(decelerateKey.isPressed())
				riding.pressKey(1, player);
			if(mc.gameSettings.keyBindLeft.isPressed())//if(leftKey.isPressed())
				riding.pressKey(2, player);
			if(mc.gameSettings.keyBindRight.isPressed())//if(rightKey.isPressed())
				riding.pressKey(3, player);
			if(mc.gameSettings.keyBindJump.isPressed())//if(upKey.isPressed())
				riding.pressKey(4, player);
			if(downKey.isPressed())
				riding.pressKey(5, player);
			if(mc.gameSettings.keyBindSneak.isPressed())//if(exitKey.isPressed())
				riding.pressKey(6, player);
			if(mc.gameSettings.keyBindInventory.isPressed() || inventoryKey.isPressed())
				riding.pressKey(7, player);
			if(bombKey.isPressed())
				riding.pressKey(8, player);
			if(gunKey.isPressed())
				riding.pressKey(9, player);
			if(controlSwitchKey.isPressed())
				riding.pressKey(10, player);
			if(leftRollKey.isPressed())
				riding.pressKey(11, player);
			if(rightRollKey.isPressed())
				riding.pressKey(12, player);
			if(gearKey.isPressed())
				riding.pressKey(13, player);
			if(doorKey.isPressed())
				riding.pressKey(14, player);
			if(wingKey.isPressed())
				riding.pressKey(15, player);
			if(trimKey.isPressed())
				riding.pressKey(16, player);
			
			/*
			for(KeyBinding key : mc.gameSettings.keyBindings )
			{
				if(key.isPressed())
				{
					key.pressed = true;
					key.pressTime = 1;
				}
			}
			*/
		}
	}
}
