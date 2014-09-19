package com.flansmod.client.model.trains;

import com.flansmod.client.model.ModelTrack;
import com.flansmod.client.tmt.Coord2D;
import com.flansmod.client.tmt.ModelRendererTurbo;


public class ModelTrackCurve extends ModelTrack 
{
	public ModelTrackCurve(int trackRadius, int curveSegments)
	{
		//
		//--------------- Inner inside coords
		//  Inner track
		//--------------- Inner outside coords
		//
		//
		//
		//--------------- Outer inside coords
		//  Outer track
		//--------------- Outer outside coords
		//
		
		int textureX = 64;
		int textureY = 64;
		
		int trackSize = trackRadius + 1;
		int trackModelOffset = trackSize * 16 / 2;
		int trackWidth = 1;
		int innerTrackRadius = 16 * trackRadius - 9;
		int outerTrackRadius = 16 * trackRadius + 8;
		int sleeperRadius = 16 * trackRadius - 12;
		
		trackModel = new ModelRendererTurbo[curveSegments * 3];
		
		float anglePerSegment = 3.14159265F / (2F * curveSegments);

		//Inner Track
		for(int i = 0; i < curveSegments; i++)
		{
			float startAngle = i * anglePerSegment;
			float endAngle = startAngle + anglePerSegment;
			
			Coord2D startInnerCorner = new Coord2D(innerTrackRadius * Math.cos(startAngle), innerTrackRadius * Math.sin(startAngle), 0, 0);
			Coord2D endInnerCorner = new Coord2D(innerTrackRadius * Math.cos(endAngle), innerTrackRadius * Math.sin(endAngle), 0, 0);
			Coord2D endOuterCorner = new Coord2D((innerTrackRadius + 1) * Math.cos(endAngle), (innerTrackRadius + 1) * Math.sin(endAngle), 0, 0);
			Coord2D startOuterCorner = new Coord2D((innerTrackRadius + 1) * Math.cos(startAngle), (innerTrackRadius + 1) * Math.sin(startAngle), 0, 0);
			
			trackModel[i] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
			trackModel[i].addShape3D(-trackModelOffset, 2F, -trackModelOffset, new Coord2D[] { startInnerCorner, startOuterCorner, endOuterCorner, endInnerCorner } , 
					2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		}

		//Outer Track
		for(int i = 0; i < curveSegments; i++)
		{
			float startAngle = i * anglePerSegment;
			float endAngle = startAngle + anglePerSegment;
			
			Coord2D startInnerCorner = new Coord2D(outerTrackRadius * Math.cos(startAngle), outerTrackRadius * Math.sin(startAngle), 0, 0);
			Coord2D endInnerCorner = new Coord2D(outerTrackRadius * Math.cos(endAngle), outerTrackRadius * Math.sin(endAngle), 0, 0);
			Coord2D endOuterCorner = new Coord2D((outerTrackRadius + 1) * Math.cos(endAngle), (outerTrackRadius + 1) * Math.sin(endAngle), 0, 0);
			Coord2D startOuterCorner = new Coord2D((outerTrackRadius + 1) * Math.cos(startAngle), (outerTrackRadius + 1) * Math.sin(startAngle), 0, 0);
			
			trackModel[curveSegments + i] = new ModelRendererTurbo(this, 0, 0, textureX, textureY);
			trackModel[curveSegments + i].addShape3D(-trackModelOffset, 2F, -trackModelOffset, new Coord2D[] { startInnerCorner, startOuterCorner, endOuterCorner, endInnerCorner } , 
					2, 32, 1, 32, 2, ModelRendererTurbo.MR_TOP);			
		}
		
		//Sleepers
		for(int i = 0; i < curveSegments; i++)
		{
			float angle = ((float)i + 0.5F) * anglePerSegment;
			trackModel[curveSegments * 2 + i] = new ModelRendererTurbo(this, 0, 3, textureX, textureY);
			trackModel[curveSegments * 2 + i].addBox(-3, 0, sleeperRadius, 6, 2, 24);
			trackModel[curveSegments * 2 + i].setRotationPoint(-trackModelOffset, 0F, -trackModelOffset);
			trackModel[curveSegments * 2 + i].rotateAngleY = angle;
		}
	}
}
