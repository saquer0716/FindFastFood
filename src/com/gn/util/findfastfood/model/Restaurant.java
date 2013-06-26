package com.gn.util.findfastfood.model;

import java.util.ArrayList;


public class Restaurant {
	public String name;
	public String vicinity;
	public Coordinate coordinate;
	
	public double distanceDrivingValue; 
	public double distanceWalkingValue;
	
	public String distanceDrivingText;
	public String distanceWalkingText;
	
	public ArrayList<Coordinate> stepsOfDriving;
	public ArrayList<Coordinate> stepsOfWalking;
}
