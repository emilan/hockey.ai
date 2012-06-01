//Playas only!
package core;

import java.io.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {
	int id;
	private int currentPos;
	private int currentRot;
	Vector location;
	BufferedImage reachMask;	
	PlayerPath path;
	Puck puck;
	Team team;
	
	public Player(int i, Team team, Puck puck){

		id=i;
		this.puck = puck;
		this.team = team;
		int teamId = team == Team.HOME ? 0 : 1;
		String fileName = String.format("src/resources/player%d%d.txt", teamId, id);
		
		try {
			path = new PlayerPath(fileName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if(!getReachMask(team))
		{
			//failed
		}		
	}
	protected void setState(int pos,int rot){
		this.currentRot=rot;
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
	public Vector getLocation(int pos)
	{
		return path.getCoordinate(pos);
	}
	private boolean getReachMask(Team team) {
		try{
			if(team == Team.HOME)
				reachMask = ImageIO.read(new File("src/resources/player0"+id+".png"));
			else
				reachMask = ImageIO.read(new File("src/resources/player1"+id+".png"));
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
		return true;
	}
		
	public double getAngleToPoint(Vector point){
		return Math.atan2(point.getX()-location.getX(),point.getY()-location.getY());
	}
	//Wrong!!??
	public double getDistanceToPoint(Point2D point){
		return Math.sqrt(point.getX()*point.getX()+point.getY()*point.getY());
	}
	public boolean canReachVector(Vector p){
		if (p.x > reachMask.getWidth()/2 || p.y > reachMask.getHeight()/2 || p.x < -reachMask.getWidth()/2 || p.y < -reachMask.getWidth()/2)
			return false;
		if(reachMask.getRGB((int)p.x + reachMask.getWidth()/2, (int)p.y + reachMask.getHeight()/2) != -16777216)
			return true;
		return false;
	}
	
	public String toString(){
		return "player "+id+"\tpos:"+this.getCurrentPos()+"\trot: "+this.getCurrentRot();
	}
}

