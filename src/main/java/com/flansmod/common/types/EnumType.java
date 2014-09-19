package com.flansmod.common.types;

import com.flansmod.common.driveables.EntityPlane;
import com.flansmod.common.driveables.EntityVehicle;
import com.flansmod.common.driveables.PlaneType;
import com.flansmod.common.driveables.VehicleType;
import com.flansmod.common.driveables.mechas.EntityMecha;
import com.flansmod.common.driveables.mechas.MechaItemType;
import com.flansmod.common.driveables.mechas.MechaType;
import com.flansmod.common.guns.AAGunType;
import com.flansmod.common.guns.AttachmentType;
import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.GrenadeType;
import com.flansmod.common.guns.GunType;
import com.flansmod.common.guns.boxes.GunBoxType;
import com.flansmod.common.parts.PartType;
import com.flansmod.common.teams.ArmourType;
import com.flansmod.common.teams.PlayerClass;
import com.flansmod.common.teams.Team;
import com.flansmod.common.tools.ToolType;
import com.flansmod.common.trains.CoachType;
import com.flansmod.common.trains.SwitchType;
import com.flansmod.common.trains.TrackPiece;

public enum EnumType 
{
	part("parts", PartType.class), 
	bullet("bullets", BulletType.class), attachment("attachments", AttachmentType.class), gun("guns", GunType.class),
	aa("aaguns", AAGunType.class), vehicle("vehicles", VehicleType.class), plane("planes", PlaneType.class),
	mechaItem("mechaItems", MechaItemType.class), mecha("mechas", MechaType.class),
	grenade("grenades", GrenadeType.class), tool("tools", ToolType.class),
	armour("armorFiles", ArmourType.class), playerClass("classes", PlayerClass.class), team("teams", Team.class),
	box("boxes", GunBoxType.class),
	track("tracks", TrackPiece.class), trackSwitch("switches", SwitchType.class), coach("coaches", CoachType.class);
	
	public String folderName;
	private Class<? extends InfoType> typeClass;
	
	private EnumType(String s, Class<? extends InfoType> typeClass)
	{
		folderName = s;
		this.typeClass = typeClass;
	}
	
	public static EnumType get(String s)
	{
		for(EnumType e : values())
		{
			if(e.folderName.equals(s))
				return e;
		}
		return null;
	}
	
	public Class<? extends InfoType> getTypeClass()
	{
		return typeClass;
	}

	public static EnumType getFromObject(Object o)
	{
		if(o instanceof EntityMecha || o instanceof MechaType) return mecha;
		if(o instanceof EntityPlane || o instanceof PlaneType) return plane;
		if(o instanceof EntityVehicle || o instanceof VehicleType) return vehicle;
		return null;
	}
}
