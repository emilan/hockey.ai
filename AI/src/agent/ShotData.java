package agent;

import core.Puck;
import core.Team;
import core.Vector;

/**
 * A class containing the data necessary to make a shot.
 * @author Bear
 */
public class ShotData {
	/**
	 * The position on the players path that is closest to the puck
	 */
	public int closestPos;
	/**
	 * The position on the players path from witch the player should shoot
	 */
	public int pos;
	/**
	 * The angle from witch the player should make his shoot
	 */
	public int fromAngle;
	/**
	 * If the player should turn clockwise when shooting or not, -1 if clockwise 1 if anti clockwise
	 */
	public int clockwise;
	/**
	 * Whether the shot can be made. If not pos is to far away from the puck
	 */
	public boolean possible; 
	/**
	 * Whether the puck has a larger or smaller y coordinate then the player, at the time of the shoot.
	 */
	//public boolean puckAbovePlayer; //TODO Set it in calculateData or remove it
	public ShotData()
	{
		
	}
	/**
	 * Calculates the data for the given pass or shot
	 * @param act
	 * The pass or shoot to make
	 * @param puck
	 * @param team
	 * @return returns this instance of ShotData now changed by the call
	 */
	public ShotData calculateData(Action act, Puck puck, Team team)
	{
		int i = 1;
		Vector from = puck.subtract(act.to.subtract(act.from).multiply(PlanningAI.PUCKRADIUS/act.to.subtract(act.from).norm()));
		double xDiff = Math.abs((act.player1.getLocation(0).getX()) - from.getX()); // -puck
		double yDiff = Math.abs((act.player1.getLocation(0).getY()) - from.getY()); // -puck
		double oldDiff = yDiff+xDiff;
		double diff = 0;
		for(; i < 255; i++)
		{	
			xDiff = Math.abs(act.player1.getLocation(i).getX() - from.getX()); // -puck
			yDiff = Math.abs(act.player1.getLocation(i).getY() - from.getY()); // -puck
			diff = yDiff+xDiff;
			if(diff >= oldDiff)
			{
				i--;
				break;
			}
			oldDiff = diff;
		}
		this.closestPos = i;
		System.out.println("First: " + i);
		
		// Variables to account for:
		// different teams
		// different players
		// puck.y over or under player path[i].y
		// moving puck in positive or negative x direction
		// (club is located slightly in front of player base)
		
		// - if the pass is close to 90 degrees in respect to the player path we may have to move the puck closer to the player path.
		// - if the distance from the puck to the path[i] is very small and we do not wish to pass close to 90 degrees
		//		we may have to move the puck slightly away from the path[i]
		
		// when oldDiff is smaller then 0 we need to inc i if home team and dec i if away team. 
		int traverse = 0;
		
		from = puck.subtract(act.to.subtract(act.from).multiply(PlanningAI.PUCKRADIUS/act.to.subtract(act.from).norm()));
		double far2 = act.to.subtract(from).normSquared();
		double close2 = from.subtract(act.player1.getLocation(i)).normSquared();
		double hyp2 = act.to.subtract(act.player1.getLocation(i)).normSquared();
		oldDiff = far2+close2-hyp2;
		if(team == Team.HOME)
			traverse = (oldDiff < 0)? 1 : -1;
		else
			traverse = (oldDiff < 0)? 1 : -1;
		oldDiff = Math.abs(oldDiff);
		for(; i > 0 && i < 255; i+=traverse)
		{	
			far2 = act.to.subtract(from).normSquared();
			close2 = from.subtract(act.player1.getLocation(i)).normSquared();
			hyp2 = act.to.subtract(act.player1.getLocation(i)).normSquared();
			diff = Math.abs(far2+close2-hyp2);
			if(diff >= oldDiff)
			{
				i-=traverse;
				break;
			}
			oldDiff = diff;
		}
		i = i==255 ? 255 : i;
		i = i==0 ? 0 : i;
		this.pos = i;
		System.out.println("Second: " + i);
		
		// set possible
		if(from.subtract(act.player1.getLocation(i)).norm() > PlanningAI.CLUBREACH) // add smaller than too so that the player is not to close??
			this.possible = false;
		else
			this.possible = true;
		// end possible
		// set clockwise and fromAngle
		Vector fromSubTo = act.from.subtract(act.to);
		if(fromSubTo.getX() < 0 && fromSubTo.getY() < 0) // to is right and below of from
		{
			if(puck.getY() > act.player1.getLocation(pos).getY()) // Puck is below of player
			{
				fromAngle = 96;
				clockwise = 1;
				
			}
			else
			{
				fromAngle = 96;
				clockwise = -1;
			}
		}
		else if(fromSubTo.getX() < 0 && fromSubTo.getY() >= 0) // to is right and above of from
		{
			if(puck.getY() > act.player1.getLocation(pos).getY()) // Puck is below of player
			{
				fromAngle = 32;
				clockwise = 1;
			}
			else
			{
				fromAngle = 32;
				clockwise = -1;
			}
		}
		else if(fromSubTo.getX() >= 0 && fromSubTo.getY() < 0) // to is left and below of from
		{
			if(puck.getY() > act.player1.getLocation(pos).getY()) // Puck is below of player
			{
				fromAngle = 160;
				clockwise = -1;
			}
			else
			{
				fromAngle = 160;
				clockwise = 1;
			}
		}
		else if(fromSubTo.getX() >= 0 && fromSubTo.getY() >= 0) // to is left and above of from
		{
			if(puck.getY() > act.player1.getLocation(pos).getY()) // Puck is below of player
			{
				fromAngle = 224;
				clockwise = -1;
			}
			else
			{
				fromAngle = 224;
				clockwise = 1;
			}
		}
		if(team.equals(Team.HOME))
		{
			fromAngle = (fromAngle + 128) % 256;
		}
		// end clockwise and fromAngle
		
		
		//acounting for backhand and forehand not being centered on the player
		if (clockwise == 1)	
		{
			pos -= 4;
		}
		else
			pos += 14;
		
		pos = pos < 0 ? 0: pos;
		pos = pos > 255 ? 255: pos;
		System.out.println("Final: " + pos);
		return this;
	}	
}