package com.flansmod.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandHandler;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;

import com.flansmod.common.driveables.EntityPlane;
import com.flansmod.common.driveables.EntitySeat;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.ItemPlane;
import com.flansmod.common.driveables.ItemVehicle;
import com.flansmod.common.driveables.PlaneType;
import com.flansmod.common.driveables.VehicleType;
import com.flansmod.common.driveables.mechas.EntityMecha;
import com.flansmod.common.driveables.mechas.ItemMecha;
import com.flansmod.common.driveables.mechas.ItemMechaAddon;
import com.flansmod.common.driveables.mechas.MechaItemType;
import com.flansmod.common.driveables.mechas.MechaType;
import com.flansmod.common.guns.AAGunType;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.EntityAAGun;
import com.flansmod.common.guns.EntityBullet;
import com.flansmod.common.guns.EntityGrenade;
import com.flansmod.common.guns.EntityMG;
import com.flansmod.common.guns.GrenadeType;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.ItemAAGun;
import com.flansmod.common.guns.ItemAttachment;
import com.flansmod.common.guns.ItemBullet;
import com.flansmod.common.guns.ItemGrenade;
import com.flansmod.common.guns.ItemGun;
import com.flansmod.common.guns.boxes.BlockGunBox;
import com.flansmod.common.guns.boxes.GunBoxType;
import com.flansmod.common.guns.boxes.ItemGunBox;
import com.flansmod.common.network.PacketHandler;
import com.flansmod.common.parts.ItemPart;
import com.flansmod.common.parts.PartType;
import com.flansmod.common.teams.ArmourType;
import com.flansmod.common.teams.BlockSpawner;
import com.flansmod.common.teams.ChunkLoadingHandler;
import com.flansmod.common.teams.CommandTeams;
import com.flansmod.common.teams.EntityFlag;
import com.flansmod.common.teams.EntityFlagpole;
import com.flansmod.common.teams.EntityGunItem;
import com.flansmod.common.teams.EntityTeamItem;
import com.flansmod.common.teams.ItemFlagpole;
import com.flansmod.common.teams.ItemOpStick;
import com.flansmod.common.teams.ItemTeamArmour;
import com.flansmod.common.teams.Team;
import com.flansmod.common.teams.TeamsManager;
import com.flansmod.common.teams.TileEntitySpawner;
import com.flansmod.common.tools.EntityParachute;
import com.flansmod.common.tools.ItemTool;
import com.flansmod.common.tools.ToolType;
import com.flansmod.common.trains.BlockBedding;
import com.flansmod.common.trains.BlockRail;
import com.flansmod.common.trains.BlockTrackSwitch;
import com.flansmod.common.trains.CoachType;
import com.flansmod.common.trains.EntityCoach;
import com.flansmod.common.trains.ItemCoach;
import com.flansmod.common.trains.ItemRailBlock;
import com.flansmod.common.trains.ItemTrack;
import com.flansmod.common.trains.SwitchType;
import com.flansmod.common.trains.TileEntitySwitch;
import com.flansmod.common.trains.TrainHandler;
import com.flansmod.common.types.EnumType;
import com.flansmod.common.types.InfoType;
import com.flansmod.common.types.TypeFile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = FlansMod.MODID, version = FlansMod.VERSION, acceptableRemoteVersions = "[4.2,4.3)")
public class FlansMod
{
	//Core mod stuff
	public static boolean DEBUG = false;
	public static final String MODID = "flansmod";
	public static final String VERSION = "4.2.3";
	@Instance(MODID)
	public static FlansMod INSTANCE;
	@SidedProxy(clientSide = "com.flansmod.client.ClientProxy", serverSide = "com.flansmod.common.CommonProxy")
	public static CommonProxy proxy;
	//A standardised ticker for all bits of the mod to call upon if they need one
	public static int ticker = 0;
	public static long lastTime;
	public static File flanDir;
	public static final float soundRange = 50F;
	public static final float driveableUpdateRange = 200F;
	
	/** The spectator team. Moved here to avoid a concurrent modification error */
	public static Team spectators = new Team("spectators", "Spectators", 0xffffff, '7');

	//Handlers
	public static final PacketHandler packetHandler = new PacketHandler();
	public static final PlayerHandler playerHandler = new PlayerHandler();
	public static final TeamsManager teamsManager = new TeamsManager();
	public static final CommonTickHandler tickHandler = new CommonTickHandler();
	public static final TrainHandler trainHandler = new TrainHandler();
	public static FlansHooks hooks = new FlansHooks();
	
	//Items and creative tabs
	public static BlockFlansWorkbench workbench;
	public static BlockSpawner spawner;
	public static ItemOpStick opStick;
	public static ItemFlagpole flag;
	//Trains
	public static BlockBedding beddingBlock;
	public static BlockRail railBlock;
	public static Item trackItem;
	public static ArrayList<BlockTrackSwitch> trackSwitchBlocks = new ArrayList<BlockTrackSwitch>();
	public static ArrayList<ItemCoach> coachItems = new ArrayList<ItemCoach>();
	public static ArrayList<BlockGunBox> gunBoxBlocks = new ArrayList<BlockGunBox>();
	public static ArrayList<ItemBullet> bulletItems = new ArrayList<ItemBullet>();
	public static ArrayList<ItemGun> gunItems = new ArrayList<ItemGun>();
	public static ArrayList<ItemAttachment> attachmentItems = new  ArrayList<ItemAttachment>();
	public static ArrayList<ItemPart> partItems = new ArrayList<ItemPart>();
	public static ArrayList<ItemPlane> planeItems = new ArrayList<ItemPlane>();
	public static ArrayList<ItemVehicle> vehicleItems = new ArrayList<ItemVehicle>();
	public static ArrayList<ItemMechaAddon> mechaToolItems = new ArrayList<ItemMechaAddon>();
	public static ArrayList<ItemMecha> mechaItems = new ArrayList<ItemMecha>();
	public static ArrayList<ItemAAGun> aaGunItems = new ArrayList<ItemAAGun>();
	public static ArrayList<ItemGrenade> grenadeItems = new ArrayList<ItemGrenade>();
	public static ArrayList<ItemTool> toolItems = new ArrayList<ItemTool>();
	public static ArrayList<ItemTeamArmour> armourItems = new ArrayList<ItemTeamArmour>();
	public static CreativeTabFlan tabFlanGuns = new CreativeTabFlan(0), tabFlanDriveables = new CreativeTabFlan(1),
			tabFlanParts = new CreativeTabFlan(2), tabFlanTeams = new CreativeTabFlan(3), tabFlanMechas = new CreativeTabFlan(4), tabFlanTrains = new CreativeTabFlan(5);
	
	/** The mod pre-initialiser method */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		log("Preinitialising Flan's mod.");
		
		//TODO : Load properties
		//configuration = new Configuration(event.getSuggestedConfigurationFile());
		//loadProperties();
		
		flanDir = new File(event.getModConfigurationDirectory().getParentFile(), "/Flan/");
	
		if (!flanDir.exists())
		{
			log("Flan folder not found. Creating empty folder.");
			log("You should get some content packs and put them in the Flan folder.");
			flanDir.mkdirs();
			flanDir.mkdir();
		}
		
		//Set up mod blocks and items
		workbench = (BlockFlansWorkbench)(new BlockFlansWorkbench(1, 0).setBlockName("flansWorkbench").setBlockTextureName("flansWorkbench"));
		GameRegistry.registerBlock(workbench, ItemBlockManyNames.class, "flansWorkbench");
		GameRegistry.addRecipe(new ItemStack(workbench, 1, 0), "BBB", "III", "III", Character.valueOf('B'), Items.bowl, Character.valueOf('I'), Items.iron_ingot );
		GameRegistry.addRecipe(new ItemStack(workbench, 1, 1), "ICI", "III", Character.valueOf('C'), Items.cauldron, Character.valueOf('I'), Items.iron_ingot );
		opStick = new ItemOpStick();
		GameRegistry.registerItem(opStick, "opStick", MODID);
		flag = (ItemFlagpole)(new ItemFlagpole().setUnlocalizedName("flagpole"));
		GameRegistry.registerItem(flag, "flagpole", MODID);
		spawner = (BlockSpawner)(new BlockSpawner(Material.iron).setBlockName("teamsSpawner").setBlockUnbreakable().setResistance(1000000F));
		GameRegistry.registerBlock(spawner, ItemBlockManyNames.class, "teamsSpawner");
		GameRegistry.registerTileEntity(TileEntitySpawner.class, "teamsSpawner");	
		//Trains
		beddingBlock = (BlockBedding)(new BlockBedding().setBlockName("beddingBlock"));
		GameRegistry.registerBlock(beddingBlock, ItemRailBlock.class, "beddingBlock");
		railBlock = (BlockRail)(new BlockRail().setBlockName("railBlock"));
		GameRegistry.registerBlock(railBlock, "railBlock");
		trackItem = new ItemTrack();
		GameRegistry.registerItem(trackItem, "trackItem", MODID);
		GameRegistry.registerTileEntity(TileEntitySwitch.class, "trackSwitch");	
		
		proxy.registerRenderers();
		
		//Read content packs
		readContentPacks(event);
					
		//Do proxy loading
		proxy.load();
		//Force Minecraft to reload all resources in order to load content pack resources.
		proxy.forceReload();
						
		log("Preinitializing complete.");
	}
	
	/** The mod initialiser method */
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		log("Initialising Flan's Mod.");
				
		//Initialising handlers
		packetHandler.initialise();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new CommonGuiHandler());		
		
		// Recipes
		for (InfoType type : InfoType.infoTypes)
		{
			type.addRecipe();
		}
		log("Loaded recipes.");
		
		//Register teams mod entities
		EntityRegistry.registerGlobalEntityID(EntityFlagpole.class, "Flagpole", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityFlagpole.class, "Flagpole", 93, this, 40, 5, true);
		EntityRegistry.registerGlobalEntityID(EntityFlag.class, "Flag", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityFlag.class, "Flag", 94, this, 40, 5, true);
		EntityRegistry.registerGlobalEntityID(EntityTeamItem.class, "TeamsItem", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityTeamItem.class, "TeamsItem", 97, this, 100, 10000, true);
		EntityRegistry.registerGlobalEntityID(EntityGunItem.class, "GunItem", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityGunItem.class, "GunItem", 98, this, 100, 20, true);
		
		//Register driveables
		EntityRegistry.registerGlobalEntityID(EntityPlane.class, "Plane", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityPlane.class, "Plane", 90, this, 250, 15, false);
		EntityRegistry.registerGlobalEntityID(EntityVehicle.class, "Vehicle", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityVehicle.class, "Vehicle", 95, this, 250, 20, false);
		EntityRegistry.registerGlobalEntityID(EntitySeat.class, "Seat", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntitySeat.class, "Seat", 99, this, 250, 20, false);
		EntityRegistry.registerGlobalEntityID(EntityParachute.class, "Parachute", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityParachute.class, "Parachute", 101, this, 40, 20, false);
		EntityRegistry.registerGlobalEntityID(EntityMecha.class, "Mecha", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityMecha.class, "Mecha", 102, this, 250, 20, false);
		//EntityRegistry.registerGlobalEntityID(EntityCoach.class, "Coach", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityCoach.class, "Coach", 103, this, 250, 20, false);
		
		//Register bullets and grenades
		//EntityRegistry.registerGlobalEntityID(EntityBullet.class, "Bullet", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityBullet.class, "Bullet", 96, this, 40, 100, true);
		EntityRegistry.registerGlobalEntityID(EntityGrenade.class, "Grenade", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityGrenade.class, "Grenade", 100, this, 40, 100, true);

		
		//Register MGs and AA guns
		EntityRegistry.registerGlobalEntityID(EntityMG.class, "MG", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityMG.class, "MG", 91, this, 40, 5, true);
		EntityRegistry.registerGlobalEntityID(EntityAAGun.class, "AAGun", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityAAGun.class, "AAGun", 92, this, 40, 500, false);
		
		
		//Register the chunk loader 
		//TODO : Re-do chunk loading
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoadingHandler());
		
		log("Loading complete.");
	}
	
	/** The mod post-initialisation method */
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		packetHandler.postInitialise();
		
		hooks.hook();
		
		
		/* TODO : ICBM
		isICBMSentryLoaded = Loader.instance().isModLoaded("ICBM|Sentry");
		
		log("ICBM hooking complete.");
		*/
	}
	
	/** Teams command register method */
	@EventHandler
	public void registerCommand(FMLServerStartedEvent e)
	{
		CommandHandler handler = ((CommandHandler)FMLCommonHandler.instance().getSidedDelegate().getServer().getCommandManager());
		handler.registerCommand(new CommandTeams());
	}
	
	/** Reads type files from all content packs */
	private void getTypeFiles(List<File> contentPacks)
	{
		for (File contentPack : contentPacks)
		{
			if(contentPack.isDirectory())
			{				
				for(EnumType typeToCheckFor : EnumType.values())
				{
					File typesDir = new File(contentPack, "/" + typeToCheckFor.folderName + "/");
					if(!typesDir.exists())
						continue;
					for(File file : typesDir.listFiles())
					{
						try
						{
							BufferedReader reader = new BufferedReader(new FileReader(file));
							String[] splitName = file.getName().split("/");
							TypeFile typeFile = new TypeFile(typeToCheckFor, splitName[splitName.length - 1].split("\\.")[0]);
							for(;;)
							{
								String line = null;
								try
								{
									line = reader.readLine();
								} 
								catch (Exception e)
								{
									break;
								}
								if (line == null)
									break;
								typeFile.lines.add(line);
							}
							reader.close();
						}
						catch(FileNotFoundException e)
						{
							e.printStackTrace();
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
					}		
				}
			}
			else
			{
				try
				{
					ZipFile zip = new ZipFile(contentPack);
					ZipInputStream zipStream = new ZipInputStream(new FileInputStream(contentPack));
					BufferedReader reader = new BufferedReader(new InputStreamReader(zipStream));
					ZipEntry zipEntry = zipStream.getNextEntry();
					do
					{
						zipEntry = zipStream.getNextEntry();
						if(zipEntry == null)
							continue;
						TypeFile typeFile = null;
						for(EnumType type : EnumType.values())
						{
							if(zipEntry.getName().startsWith(type.folderName + "/") && zipEntry.getName().split(type.folderName + "/").length > 1 && zipEntry.getName().split(type.folderName + "/")[1].length() > 0)
							{
								String[] splitName = zipEntry.getName().split("/");
								typeFile = new TypeFile(type, splitName[splitName.length - 1].split("\\.")[0]);
							}
						}
						if(typeFile == null)
						{
							continue;
						}
						for(;;)
						{
							String line = null;
							try
							{
								line = reader.readLine();
							} 
							catch (Exception e)
							{
								break;
							}
							if (line == null)
								break;
							typeFile.lines.add(line);
						}
					}
					while(zipEntry != null);
					reader.close();
					zipStream.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/** Content pack reader method */
	private void readContentPacks(FMLPreInitializationEvent event)
	{
		// Icons, Skins, Models
		// Get the classloader in order to load the images
		ClassLoader classloader = (net.minecraft.server.MinecraftServer.class).getClassLoader();
		Method method = null;
		try
		{
			method = (java.net.URLClassLoader.class).getDeclaredMethod("addURL", new Class[]
			{ java.net.URL.class });
			method.setAccessible(true);
		} catch (Exception e)
		{
			log("Failed to get class loader. All content loading will now fail.");
			e.printStackTrace();
		}

		List<File> contentPacks = proxy.getContentList(method, classloader);
		
		if (!event.getSide().equals(Side.CLIENT))
		{
			//Gametypes (Server only)
			// TODO: gametype loader
		}
		
		getTypeFiles(contentPacks);
		
		for(EnumType type : EnumType.values())
		{
			Class<? extends InfoType> typeClass = type.getTypeClass();
			for(TypeFile typeFile : TypeFile.files.get(type))
			{
				try
				{
					InfoType infoType = (typeClass.getConstructor(TypeFile.class).newInstance(typeFile));
					infoType.read(typeFile);
					switch(type)
					{
					case bullet : bulletItems.add((ItemBullet)new ItemBullet(infoType).setUnlocalizedName(infoType.shortName)); break;
					case attachment : attachmentItems.add((ItemAttachment)new ItemAttachment((AttachmentType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case gun : gunItems.add((ItemGun)new ItemGun((GunType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case grenade : grenadeItems.add((ItemGrenade)new ItemGrenade((GrenadeType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case part : partItems.add((ItemPart)new ItemPart((PartType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case plane : planeItems.add((ItemPlane)new ItemPlane((PlaneType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case vehicle : vehicleItems.add((ItemVehicle)new ItemVehicle((VehicleType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case aa : aaGunItems.add((ItemAAGun)new ItemAAGun((AAGunType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case mechaItem : mechaToolItems.add((ItemMechaAddon)new ItemMechaAddon((MechaItemType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case mecha : mechaItems.add((ItemMecha)new ItemMecha((MechaType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case tool : toolItems.add((ItemTool)new ItemTool((ToolType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case box : gunBoxBlocks.add((BlockGunBox)new BlockGunBox((GunBoxType)infoType).setBlockName(infoType.shortName)); break;
					case armour : armourItems.add((ItemTeamArmour)new ItemTeamArmour((ArmourType)infoType).setUnlocalizedName(infoType.shortName)); break;
					case playerClass : break;
					case team : break;
					case track : break;
					case trackSwitch : trackSwitchBlocks.add((BlockTrackSwitch)(new BlockTrackSwitch((SwitchType)infoType).setBlockName(infoType.shortName))); break;
					case coach : coachItems.add((ItemCoach)new ItemCoach((CoachType)infoType).setUnlocalizedName(infoType.shortName)); break;
					default : log("Unrecognised type."); break;
					}
				}
				catch(Exception e)
				{
					log("Failed to add " + type.name() + " : " + typeFile.name);
					e.printStackTrace();
				}
			}
			log("Loaded " + type.folderName + ".");
		}		
		Team.spectators = spectators;
	}
	
	public static PacketHandler getPacketHandler()
	{
		return INSTANCE.packetHandler;
	}
	
	//TODO : Proper logger
	public static void log(String string) 
	{
		System.out.println("[Flan's Mod] " + string);
	}
}
