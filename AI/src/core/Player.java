//Playas only!
package core;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.awt.geom.Point2D;
import java.awt.Point;
import util.*;

public class Player {
	int id;
	int currentPos;
	private int currentRot;
	Vector location;
	
	PlayerPath path;
	Puck puck;
	Team team;

	Player(int i,AIBase base,Team team){
		id=i;
		this.team = team;
		this.puck = base.getPuck();
		
		int teamId = team == Team.HOME ? 0 : 1;
		String fileName = String.format("src/resources/player%d%d.txt", teamId, id);
		
		try {
			path = new PlayerPath(fileName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void setState(int pos,int rot){
		this.currentRot=rot*360/255;
		this.currentPos=pos;
		this.location=path.getCoordinate(pos);
	}
	
	public int getCurrentRot() {
		return currentRot;
	}
	public int getCurrentPos() {
		return currentPos;
	}
	public Vector getLocation(){
		return location;
	}
	public int getId(){
		return id;
	}
	
	public double getAngleToPuck(){
		return getAngleToPoint(puck);
	}
	public double getAngleToPoint(Vector point){
		return Math.atan2(point.getX()-location.getX(),point.getY()-location.getY());
	}
	
	public double getDistanceToPoint(Point2D point){
		return Math.sqrt(point.getX()*point.getX()+point.getY()*point.getY());
	}
	//public double getDistanceToPuck(){
		//return getDistanceToPoint();
	//}
	
	/*public boolean canReachPuck(Puck p){
		return path.canPlayerReachPuck(p);
	}*/
	
	public String toString(){
		return "player "+id+"\tpos:"+this.getCurrentPos()+"\trot: "+this.getCurrentRot();
	}
	
	
}
