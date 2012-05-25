package core;

import java.awt.geom.Point2D;
import java.io.IOException;

import util.Vector;

/**
 * Class for managing players in the own team
 * 
 * @author Anders Ryndel
 * 
 */
public class ControllablePlayer extends Player {
	AIBase parent;
	PrimitiveOrder order;

	private int targetPos = 0;
	private int transSpeed = 0;
	private int targetRot = 0;
	private int rotSpeed = 0;

	public int getTargetPos() {
		return targetPos;
	}

	public void setTargetPos(int targetPos) {
		this.targetPos = targetPos;
	}

	public int getTransSpeed() {
		return transSpeed;
	}

	public void setTransSpeed(int transSpeed) {
		this.transSpeed = transSpeed;
	}

	public int getTargetRot() {
		return targetRot;
	}

	public void setTargetRot(int targetRot) {
		this.targetRot = targetRot;
	}

	public int getRotSpeed() {
		return rotSpeed;
	}

	public void setRotSpeed(int rotSpeed) {
		this.rotSpeed = rotSpeed;
	}

	protected ControllablePlayer(int i, AIBase base) {
		super(i, base, base.getTeam());
		parent = base;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Causes the player to turn to face the puck
	 */
	public void facePuck() {
		lookAtAngle(getAngleToPuck());
	}

	/**
	 * Causes the player to turn and move into position to stop and take control
	 * over the puck
	 */
	long lastShot = 0;

	public PrimitiveOrder scootAndShoot() {
		Vector puckVec = new Vector(puck.getX(), puck.getY());
		int closePos = (int) path.getClosestPosition(puckVec);
		if (Math.abs(getTargetPos() - closePos) > 5) {
			targetPos = closePos;
			rotSpeed = 0;
			transSpeed = 50;
		} 
		else {
			Vector myLocation = getLocation();
			double distance = myLocation.subtract(puckVec).norm();
			if (distance < 60) {
				long time = System.currentTimeMillis();
				if (time - lastShot > 500) {
					Vector headingVector = getHeadingVector();
					Vector puckRelativeVector = puckVec.subtract(myLocation);
					double angle = 
						Math.atan2(puckRelativeVector.getX(), puckRelativeVector.getY()) 
						- Math.atan2(headingVector.getX(), headingVector.getY());
					while (angle < 0)
						angle += 2 * Math.PI;
					while (angle > 2 * Math.PI)
						angle -= 2 * Math.PI;
					
					if (angle < Math.PI)
						rotSpeed = -127;
					else rotSpeed = 127;
					
					lastShot = time;
				}
				else return null;
			}
			else return null;
		} 
		
		/*
		 * System.out.println(getCurrentPos()-closePos);
		 * if(Math.abs(getCurrentPos()-closePos)<40){
		 * 
		 * transSpeed=0; rotSpeed=125; targetRot=getCurrentRot()-20;
		 * if(targetRot<0){ targetRot+=360; } }
		 */
		return getOrder();
	}
	
	public Vector getHeadingVector() {
		int position = getCurrentPos();
		int maxIndex = Math.min(position + 2, 255);
		int minIndex = Math.max(position - 2, 0);
		
		return path.getCoordinate(maxIndex).subtract(path.getCoordinate(minIndex));
	}

	/**
	 * If the player is in control of the puck, this method will try to properly
	 * align the player to the puck and then turn to shoot it in the given
	 * direction.
	 * 
	 * @param angle
	 *            - the angle in which the player attempts to shoot at.
	 */
	public void shootAtAngle(double angle) {

	}

	/**
	 * If the player is in control of the puck, this method will try to properly
	 * align the player to the puck and then turn to shoot it in the given
	 * direction.
	 * 
	 * @param target
	 *            - the target which the player attempts to shoot at.
	 */
	public void shootAtPoint(Point2D target) {

	}

	/**
	 * If the player is in control of the puck, this method will try to properly
	 * align the player to the puck as to prepare a shot in the given direction.
	 * 
	 * @param angle
	 *            - the angle in which the player attempts to aim at.
	 */
	public void aimAtAngle(double angle) {

	}

	/**
	 * If the player is in control of the puck, this method will try to properly
	 * align the player to the puck as to prepare a shot in the given direction.
	 * 
	 * @param target
	 *            - the target which the player attempts to aim at.
	 */
	public void aimAtPoint(Point2D target) {

	}

	/**
	 * Makes the player turn to face the given angle.
	 * 
	 * @param angle
	 *            - the angle at which the player will face.
	 */
	public void lookAtAngle(double angle) {

	}

	/**
	 * Makes the player turn to face the given point.
	 * 
	 * @param target
	 *            - the point which the player will face.
	 */
	public void lookAtPoint(Point2D target) {

	}

	/**
	 * If the player is in control of the puck, it will try to move it to the
	 * given position.
	 * 
	 * @param pos
	 *            - the position that the player will attempt to reach with the
	 *            puck.
	 */
	public void moveWithPuck(int pos) {

	}

	/**
	 * @return pos - the how far the player is slided in it's path
	 */

	/**
	 * @return the current order of this player
	 */

	protected PrimitiveOrder getOrder() {
		return new PrimitiveOrder(id, transSpeed, targetPos, rotSpeed,
				targetRot);
	}

	public void sendOrder() throws IOException {
		parent.addOrder(getOrder());
		parent.send();
	}

	private long lastOrder = 0;
	public PrimitiveOrder actLikeGoalGuard() {
		Vector vecPuck = new Vector(puck.getX(), puck.getY());
		Vector vecGoal;
		if (team == Team.HOME)
			vecGoal = new Vector(-284, 0);
		else vecGoal = new Vector(284, 0);
		
		double x1 = vecGoal.getX();
		double y1 = vecGoal.getY();
		double x2 = vecPuck.getX();
		double y2 = vecPuck.getY();
		
		double x3 = path.getCoordinate(0).getX();
		double y3 = path.getCoordinate(0).getY();
		double x4 = path.getCoordinate(255).getX();
		double y4 = path.getCoordinate(255).getY();
		
		double x = (y1 - y3 + (y4 - y3) / (x4 - x3) * x3 - (y2 - y1) / (x2 - x1) * x1) / ((y4 - y3) / (x4 - x3) - (y2 - y1) / (x2 - x1));
		double y = (y2 - y1) / (x2 - x1) * (x - x1) + y1;
		
		Vector vecIntersection = new Vector(x, y);
		int minDistanceIndex = path.getClosestPosition(vecIntersection);
		
		long time = System.currentTimeMillis();
		
		Vector myLocation = getLocation();
		double distance = myLocation.subtract(vecPuck).norm();
		if (distance < 40) {
			if (time - lastShot > 100) {
				if ((team == Team.AWAY && vecPuck.getY() > 0) || 
					(team == Team.HOME && vecPuck.getY() < 0))
					rotSpeed = -127;
				else rotSpeed = 127;
				
				lastShot = time;
			}
			else return null;
		}
		else {		
			if (time - lastOrder < 100)
				return null;
			
			lastOrder = time;
			
			targetPos = minDistanceIndex;
			transSpeed = 50;
			targetRot = 0;
			rotSpeed = 50;
		}
		
		return getOrder();
	}

}
