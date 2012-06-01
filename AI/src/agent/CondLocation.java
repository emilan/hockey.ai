package agent;

import core.Player;
import core.Vector;

/**
 * A Condition representing where a player is located.
 * @author Björn Berntsson
 */
public class CondLocation extends Condition {
	Player player;
	Vector point;
	public CondLocation(Player player, Vector point, Action action) // Location
	{
		this.name = Condition.Name.Location;
		this.player = player;
		this.point = point;
		onAction = action;
	}
	@Override
	public boolean equals(Condition cond)
	{
		if(cond.name.equals(Condition.Name.Location))
		{
			double diff = point.subtract((((CondLocation)cond).point)).norm();
			if(player.equals(((CondLocation)cond).player) && diff < PlanningAI.epsCor)
				return true;
		}
		return false;
	}
	@Override
	public boolean negates(Condition cond) {
		if(cond.name.equals(Condition.Name.Location))
		{
			double diff = point.subtract((((CondLocation)cond).point)).norm();
			if(((CondLocation)cond).player == this.player && diff >= PlanningAI.epsCor)
				return true;
		}
		return false;
	}
}
