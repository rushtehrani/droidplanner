package com.droidplanner.helpers.geoTools;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.droidplanner.drone.variables.waypoint;
import com.google.android.gms.maps.model.LatLng;

public class PathFillet {

	private static final double FILLET_MINIMAL_DISTANCE_FACTOR = 1.1;

	public static List<waypoint> filletPath(List<waypoint> list, double radius) {
		ArrayList<waypoint> filletedList = new ArrayList<waypoint>();
		filletedList.add(list.get(0));
		for (int i = 2; i < list.size(); i++) {
			processCorner(list, radius, filletedList, i);
		}
		filletedList.add(list.get(list.size() - 1));
		return filletedList;
	}

	private static void processCorner(List<waypoint> list, double radius,
			ArrayList<waypoint> filletedList, int i) {
		waypoint previus = list.get(i - 2);
		waypoint center = list.get(i - 1);
		waypoint after = list.get(i);

		double angle = getAngleBetweenLines(after.getCoord(),
				center.getCoord(), previus.getCoord());
		double distanceFromStart = Math.abs(radius
				/ (Math.tan(Math.toRadians(angle) / 2)));
		
		Log.d("FILLET", "iteration " + i + " distance "
				+ distanceFromStart + " angle " + angle);

		if (angle < 150 & isDistanceEnoughForFillet(previus, center, after, distanceFromStart)) {
			filletCorner(filletedList, distanceFromStart, previus, center, after);
		} else {
			filletedList.add(center);
		}
	}

	private static boolean isDistanceEnoughForFillet(waypoint previus,
			waypoint center, waypoint after, double distanceFromStart) {
		if (GeoTools.getDistance(previus.getCoord(), center.getCoord())*FILLET_MINIMAL_DISTANCE_FACTOR<distanceFromStart) {
			return false;
		}
		if (GeoTools.getDistance(after.getCoord(), center.getCoord())*FILLET_MINIMAL_DISTANCE_FACTOR<distanceFromStart){
			return false;
		}
		return true;
	}

	private static void filletCorner(ArrayList<waypoint> filletedList,
			double radius, waypoint previus, waypoint center,
			waypoint after) {
		LatLng filletPrevius = generatePointOnLine(center.getCoord(),
				previus.getCoord(), radius);
		LatLng filletAfter = generatePointOnLine(center.getCoord(),
				after.getCoord(), radius);

		filletedList
				.add(new waypoint(filletPrevius, center.getHeight()));
		filletedList.add(new waypoint(filletAfter, center.getHeight()));
	}

	private static double getAngleBetweenLines(LatLng start, LatLng center,
			LatLng end) {
		double angle = GeoTools.getHeadingFromCoordinates(start, center)
				- GeoTools.getHeadingFromCoordinates(end, center);
		if (angle > 180) {
			angle = 360 - angle;
		}
		return angle;
	}

	private static LatLng generatePointOnLine(LatLng start, LatLng end,
			double distanceFromStart) {
		double heading = GeoTools.getHeadingFromCoordinates(start, end);
		return GeoTools.newCoordFromBearingAndDistance(start, heading,
				distanceFromStart);
	}

}
