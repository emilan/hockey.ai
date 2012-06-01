package agent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import AI.FakeAI;

import core.*;

/**
 * The class that plans and execute actions for the hockey game.
 * @author Björn Berntsson
 */
public class PlanningAI extends AIBase{
	
	public static final int CLUBREACH = 46; // TODO: set it
	public static final int PUCKRADIUS = 12; // TODO: set it
	private static final int epsPath = 5; // TODO: set it
	public static final int epsCor = 5;  // TODO: set it
	private static final int epsRot = 10;  // TODO: set it
	private static final int farFromLine = 10;  // TODO: set it
	private ShotData  shotData = new ShotData();
	private int moveDest = 0; // saves the destination of move orders
	private int angleDest = 0; // saves the angleDestination of rotation orders
	private int phaseStuckCount = 0;
	
	private Agent agent;
	Date d=new Date();
	/**
	 * A Planning AI that plans and execute actions for the hockey game.
	 * @param gameAddress
	 * @param port 
	 * @param localPort
	 * @param gameAddress
	 * @throws IOException
	 */
	public PlanningAI(int localPort, SocketAddress gameAddress) throws IOException{
		super(localPort, gameAddress);
		System.out.println("out of Super");
		agent = new Agent(puck, friendlyPlayers, opposingPlayers);
		//reciever.start();
	}
	public Puck getPuck(){
		return puck;
	}
	public Team getTeam(){
		return team;
	}
	public List<ControllablePlayer> getFriendlyPlayers(){
		return friendlyPlayers;
	}
	public List<Player> getOpposingPlayers(){
		return opposingPlayers;
	}
	
	private ArrayList<Condition> getCurrentEffects()
	{
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for(Condition cond : agent.current.effects)
		{
			if(cond.name.equals(Condition.Name.PathClear))
			{
				//if path is still clear add
				//if(agent.PathClear(((CondPathClear)cond).from, ((CondPathClear)cond).to))
					conditions.add(cond);
				//else // debug
					//System.out.println("Path was not clear From: " + ((CondPathClear)cond).from +" To: " + ((CondPathClear)cond).to);
			}
			/*if(cond.name.equals(Condition.Name.HasPuck))
			{
				// if still has add
				conditions.add(cond);
			}*/
			/*if(cond.name.equals(Condition.Name.CanGetPuck))
			{
				if(((CondCanGetPuck)cond).player.canReachVector(getPuck()))
					conditions.add(cond);
			}*/
		}
		boolean defend = true;
		for(Player player : friendlyPlayers)
		{
			conditions.add(new CondLocation(player, player.getLocation(),agent.current));
			
			if((int)player.getLocation().shortestVectorDistance(getPuck()) < CLUBREACH) 
			{
				conditions.add(new CondHasPuck(player,agent.current));
			}
			if(player.canReachVector(getPuck()))
			{
				conditions.add(new CondCanGetPuck(player, agent.current));
				defend = false;
			}
		}
		if(defend && (!state.equals(State.PASSING) || phase != 7))
		{
			mainState = MainState.DEFENDING;
		}
		return conditions;
	}
	private Action pickShot()
	{
		Action act = null;
		Random rand = new Random();
		/*if(puck.getY() >= 0)
		{
			
			if(rand.nextInt(2) == 1)
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(4), friendlyPlayers.get(4).path[rand.nextInt(256)], friendlyPlayers.get(5).path[180]);
				return act;
			}
			else
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(3), friendlyPlayers.get(3).path[rand.nextInt(150)], friendlyPlayers.get(5).path[255]);
				return act;
			}
				
			//if path is clear player 4 or 3 shoot
		}
		else
		{
			if(rand.nextInt(2) == 1)
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(4), friendlyPlayers.get(4).path[rand.nextInt(150)], friendlyPlayers.get(5).path[180]);
				return act;
			}
			else
			{
				act = new Action(Action.Act.Shoot, friendlyPlayers.get(5), friendlyPlayers.get(5).path[30], friendlyPlayers.get(3).path[255]);
				return act;
			}
			//if path is clear player 4 or 5 shoot
		}*/
		Vector b = team.equals(Team.HOME) ? new Vector(285,25): new Vector(-285,29);
		Vector t = team.equals(Team.HOME) ? new Vector(285, -37): new Vector(-285, -29);
		act = new Action(Action.Act.Shoot, friendlyPlayers.get(4), friendlyPlayers.get(4).getLocation(170), b);
		return act;//.preconditions;
	}
	
	enum MainState{ATTACKING, DEFENDING;}
	enum State {PLANNING, SHOOTING, PASSING, COLLECTINGPUCK, MOVING, MOVINGWITHPUCK;}
	MainState mainState = MainState.ATTACKING;
	State state = State.PLANNING;
	int phase = 0;
	
	MainState tempMainState = MainState.ATTACKING;
	State tempState = State.PLANNING;
	int tempPhase = 0;
	int repetedStates = 0;
	Action act;
	
	@Override
	public void onNewState()  {
		if(team.equals(Team.HOME)) // only away team plays for now!
			return;
		
		//getPassOrShotPos(new Action(Act.Shoot, friendlyPlayers.get(4), puck, new Vector(150, 150)));

		// set current.effects
		agent.current.effects = getCurrentEffects();
		// start defending if none of your players can get the puck
		/*if(act != null)
		{
			boolean stop = true;
			for(Iterator<Condition> iter = agent.current.effects.iterator(); iter.hasNext();)
			{
				Condition cond = iter.next();
				if(cond.name.equals(Condition.Name.CanGetPuck) && ((CondCanGetPuck)cond).player.equals(act.player1))
				{						
					stop = false;
					break;
				}
			}
			if (stop)
			{
				mainState = MainState.DEFENDING;
			}
		}*/
		//fulfix
		if(tempMainState.equals(mainState) && tempState.equals(state) && tempPhase == phase)
			repetedStates++;
		else
			repetedStates = 0;
		if(repetedStates > 70)
		{
			agent.reset();
			mainState = MainState.ATTACKING;
			state = State.PLANNING;
			phase = 0;
			phaseStuckCount = 0;
		}
			
		tempMainState = mainState;
		tempState = state;
		tempPhase = phase;
		System.out.println("2: Team: " + team + " State: " + state + " MainState: " + mainState + " Phase: " + phase);
		// make sure we have a goal
		if(mainState.equals(MainState.ATTACKING))
		{
			if(state.equals(State.PLANNING))
			{
				phase = 0;
				act = new Action(Action.Act.NoOp);
				if(agent.shoot.from == null)
				{
					agent.setNewGoal(pickShot());
					agent.current.effects = getCurrentEffects();
				}
				// find a action
				while (act.act.equals(Action.Act.NoOp))
				{
					try{
						act = agent.Continuous_Pop_Agent();
						
					}
					catch(Exception e){e.printStackTrace();}
					
					if (act == null) // if null is returned go back to defending
					{
						agent.reset();
						mainState = MainState.DEFENDING;
						state = State.PLANNING;
						return;
					}
					else if(act.act.equals(Action.Act.NewGoal))// try a new goal.
					{
						agent.reset();
						state = State.PLANNING;
						return;
					}
				}
				
				if(act.act.equals(Action.Act.Move))
					state = State.MOVING;
				else if(act.act.equals(Action.Act.MoveWithPuck))
					state = State.MOVINGWITHPUCK;
				else if(act.act.equals(Action.Act.CollectPuck))
					state = State.COLLECTINGPUCK;
				else if(act.act.equals(Action.Act.Pass))
					state = State.PASSING;
				else if(act.act.equals(Action.Act.Shoot))
					state = State.SHOOTING;
			}
			else if(state.equals(State.MOVING))
			{				
				switch(phase)
				{
					case 0:
						int i = 0;
						for(; i < 255; i++)
						{
							if(act.player1.getLocation(i).equals(act.to))
								break;
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),30,i,0,0));
						phase++;
						break;
					case 1:
						Vector dist = act.player1.getLocation().subtract(act.to);
						if(dist.norm() < epsCor)
							state = State.PLANNING;
						break;
					default:
						System.out.println("Bad phase while moving!");
						break;
				}
			}
			else if(state.equals(State.MOVINGWITHPUCK))
			{
				if(!playerStillHasPuck(act.player1) && phase == 7 && phase == 0 && phase == 1)
					mainState = MainState.DEFENDING;
				
				switch(phase)
				{
					case 0:
						int dir = team.equals(Team.HOME) ? 1 : -1;
						if(puck.getY() < act.player1.getLocation().getY())
							dir = team.equals(Team.HOME) ? -1 : 1;
						//moving left, puck on the right
						if (act.to.getX() < act.player1.getLocation().getX() && act.player1.getLocation().getX() < puck.getX())
						{
							if (act.player1.getLocation().getY() > puck.getY())
								angleDest = team.equals(Team.HOME) ? 192 : 64;
							else
								angleDest = team.equals(Team.HOME) ? 64 : 192;
							this.addOrder(new PrimitiveOrder(act.player1.getId(),0,0,dir*15,angleDest));
							phase++;
							/*if(dir == -1)
								phase++;*/
						}
						//moving right, puck on the left
						else if(act.to.getX() > act.player1.getLocation().getX() && act.player1.getLocation().getX() > puck.getX())
						{
							angleDest = team.equals(Team.HOME) ? 64 : 192;
							this.addOrder(new PrimitiveOrder(act.player1.getId(),0,0,dir*15,angleDest));
							phase++;
							/*if(dir == -1)
								phase++;*/
						}
						else // we are allready "behind" the puck
						{
							phase+=4;
						}
						break;
					case 1:
						System.out.println("MovePuck phase 1. angle Error: " + rotError(act.player1.getCurrentRot(), angleDest));
						if(rotError(act.player1.getCurrentRot(), angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;
					case 2:
						int dir2 = team.equals(Team.HOME) ? 1 : -1;
						if(puck.getY() < act.player1.getLocation().getY())
							dir2 = team.equals(Team.HOME) ? -1 : 1;
						
						boolean left = false; // moving "right" : towards away team
						if (act.to.getX() < act.player1.getLocation().getX()) // moving "left"
						{
							left = true;
						}
						int i = 1;
						double xDiff = Math.abs((act.player1.getLocation(0).getX()) - puck.getX());
						double yDiff = Math.abs((act.player1.getLocation(0).getY()) - puck.getY());
						double oldDiff = yDiff+xDiff;
						double diff = 0;
						for(; i < 255; i++)
						{	
							xDiff = Math.abs((act.player1.getLocation(i).getX()) - puck.getX());
							yDiff = Math.abs((act.player1.getLocation(i).getY()) - puck.getY());
							diff = yDiff+xDiff;
							if(diff >= oldDiff)
							{
								i--;
								break;
							}
							
							oldDiff = diff;
						}
						if(team.equals(Team.HOME))
						{
							i = left ? i+30: i-30;
						}
						else
							i = left ? i-30: i+30;
						i = i<0 ? 0:i;
						i = i>255 ? 255:i;
						moveDest = i;
						this.addOrder(new PrimitiveOrder(act.player1.getId(),15,i,0,0));
						//dir2*25,angleDest));// move behind
						phase++;
						break;
					case 3:
						System.out.println("MovePuck phase 3. Error: " + Math.abs(act.player1.getCurrentPos()-moveDest));
						if(Math.abs(act.player1.getCurrentPos()-moveDest) < epsPath)
							phase++;
						break;
					case 4: //TODO: may want to and or remove a few degrees based on left or right traveling to get an inward angle
						boolean leftTravel = false;
						if(puck.getX() < act.player1.getLocation().getX())
							leftTravel = true;
						angleDest = !leftTravel ? 244 : 11;
						int direction = 0;
						if(act.player1.getLocation().getY() < puck.getY())// puck below player
						{
							direction = team.equals(Team.HOME) ? 1 : -1;
							if(team.equals(Team.HOME))
								angleDest = leftTravel ? 117 : 130;
						}
						if(act.player1.getLocation().getY() >= puck.getY())// puck above player
						{
							direction = team.equals(Team.HOME) ? -1 : 1;
							if(team.equals(Team.AWAY))
							{
								if(act.player1.getId() == 4)
									angleDest = leftTravel ? 165 : 117; // 165 för att spelet är konstigt =(
								else
									angleDest = leftTravel ? 130 : 117;
							}
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0,0,direction*25,angleDest));// rotate into pos
						System.out.println("MovePuck phase 4, turning to:" +  angleDest);
						phase++;
						break;
					case 5:
						System.out.println("MovePuck phase 5. angle Error: " + rotError(act.player1.getCurrentRot(), angleDest));
						if(rotError(act.player1.getCurrentRot(), angleDest) < epsRot)
							phase++;
						break;
					case 6:
						int j = 0;
						for(; j < 255; j++)
						{
							if(act.player1.getLocation(j).equals(act.to))
								break;
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),7,j,0,0));
						phase++;
						break;
					case 7:
						Vector dist = act.player1.getLocation().subtract(act.to);
						if(dist.norm() < epsCor)
							phase++;
						break;
					case 8: //give server time to send new data
						phaseStuckCount++;
						if(phaseStuckCount > 20)
						{
							state = State.PLANNING;
							phaseStuckCount = 0;
						}
						break;
					default:
						System.out.println("Bad phase while moving!");
						break;
				}
			}
			else if(state.equals(State.COLLECTINGPUCK))
			{
				// TODO: Om det finns tid, dra in pucken närmare vår egen linje så ingen motståndare når den.
				switch(phase)
				{
					case 0: //TODO: may want to and or remove a few degrees based on left or right traveling
						System.out.println("collect phase 0");
						phase++;
						angleDest = 0;
						if(act.player1.getLocation().getY() < puck.getY())// puck below player
						{
							if(team.equals(Team.HOME))
								angleDest = 128;
						}
						if(act.player1.getLocation().getY() >= puck.getY())// puck above player
						{
							if(team.equals(Team.AWAY))
								angleDest = 128;
						}
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0,0,20,angleDest));// rotate into pos
						
						break;
					case 1:
						System.out.println("collect phase 1, Error: " + (rotError(act.player1.getCurrentRot(), angleDest)));
						if(rotError(act.player1.getCurrentRot(), angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;
					case 2:
						System.out.println("collect phase 2");
						int i = 1;
						double xDiff = Math.abs((act.player1.getLocation(0).getX()) - puck.getX());
						double yDiff = Math.abs((act.player1.getLocation(0).getY()) - puck.getY());
						double oldDiff = yDiff+xDiff;
						double diff = 0;
						for(; i < 255; i++)
						{	
							xDiff = Math.abs((act.player1.getLocation(i).getX()) - puck.getX());
							yDiff = Math.abs((act.player1.getLocation(i).getY()) - puck.getY());
							diff = yDiff+xDiff;
							if(diff >= oldDiff)
							{
								i--;
								break;
							}
							
							oldDiff = diff;
						}
						if(puck.getX() > act.player1.getLocation().getX())
							i = team.equals(Team.HOME) ? i-40 : i+40;
						else
							i = team.equals(Team.HOME) ? i+40 : i-40;
						i = i>255 ? 255 : i;
						i = i<0 ? 0 : i; 
						this.addOrder(new PrimitiveOrder(act.player1.getId(),10,i,0,0));
						phase++;
						break;
					case 3:
						System.out.println("collect phase 3. Error: " + act.player1.getLocation().shortestVectorDistance(puck));
						if(act.player1.getLocation().shortestVectorDistance(puck) < CLUBREACH)
							state = State.PLANNING;
						break;
					default:
						System.out.println("Bad phase while CollectingPuck!");
						break;
				}
			}
			else if(state.equals(State.PASSING))
			{
				switch(phase)
				{
					case 0: // check if the puck has a good position, send receiver to his position
						shotData.calculateData(act, puck, team);
						int i = 0;
						for(; i < 256; i++)
						{	
							if(act.player2.getLocation(i).equals(act.to))
							{
								break;
							}
						}
						Vector a = act.player2.getLocation().subtract(act.player1.getLocation());
						if(a.getX() < -20 && a.getY() < -20) // player is to the right of puck;
						{
							angleDest = 160;
						}
						else if(a.getX() < -20 && a.getY() > 20)
						{
							angleDest = 224;
						}
						else if(a.getX() > 20 && a.getY() > 20)
						{
							angleDest = 32;
						}
						else if(a.getX() > 20 && a.getY() < -20)
						{
							angleDest = 96;
						}
						else if(a.getX() < 0 && a.getY() > -20 && a.getY() < 20)
						{
							angleDest = 128;
						}
						else if(a.getX() > 0 && a.getY() > -20 && a.getY() < 20)
						{
							angleDest = 0;
						}
						else if(a.getY() < 0 && a.getX() > -20 && a.getX() < 20)
						{
							angleDest = 64;
						}
						else if(a.getY() > 0 && a.getX() > -20 && a.getX() < 20)
						{
							angleDest = 192;
						}
						angleDest = team.equals(Team.HOME) ? (angleDest+128) % 256 : angleDest;
						//obs the movement should never really be needed, he should stand here already
						this.addOrder(new PrimitiveOrder(act.player2.getId(),20, i, 20, angleDest));
						if(shotData.possible)
							phase+=2;
						else // need to move the puck.
						{
							// TODO: check this part, maybe also use player.y < 0 to set dir ??
							// move puck away from line, closer to final destination
							if(act.player1.getLocation(shotData.closestPos).subtract(puck).norm() < farFromLine)
							{
								
								int dir = 0;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									dir = -1;
									angleDest = team.equals(Team.HOME) ? 59 : 186;
								}
								else
								{
									dir = 1;
									angleDest = team.equals(Team.HOME) ? 197 : 69;
								}
								// TODO: may need to increase speed for short rotations
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*20, angleDest));
							}
							else
							{
								// TODO: move puck closer to the line
								// turn clockwise
								int dir = 1;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									if(act.player1.getLocation().getY() > puck.getY())//puck above player
									{
										dir = 1;
										angleDest = team.equals(Team.HOME) ? 32 : 160;
									}
									else
									{
										dir = -1;
										angleDest = team.equals(Team.HOME) ? 96 : 224;
									}
								}
								else
								{
									if(act.player1.getLocation().getY() > puck.getY())
									{
										dir = -1;
										angleDest = team.equals(Team.HOME) ? 224 : 96;
									}
									else
									{
										dir = 1;
										angleDest = team.equals(Team.HOME) ? 160 : 32;
									}
								}
								
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*30, angleDest));
							}
							phase++;
						}
						break;
					/*WE MAY NEED THIS case 1: 
						if(Math.abs(act.player1.getCurrentRot()-angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;*/
					case 1:
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase++;
						else
						{
							phaseStuckCount++;
							if (phaseStuckCount > 50)
							{
								phaseStuckCount = 0;
								state = State.PLANNING; // TODO: plan or go to phase1 ???
							}
						}
						break;
					case 2: //skall även rotera ifrån pucken till en bra vinkel att passa ifrån
						//shotData.calculateData(act, puck, team);
						int dir = 0;
						if(act.player1.getCurrentRot() > shotData.fromAngle)
						{
							if(act.player1.getCurrentRot()-shotData.fromAngle > 128)
								dir = -1; //anti-clock
							else
								dir = 1; // clock
						}
						else
						{
							if(shotData.fromAngle - act.player1.getCurrentRot() > 128)
								dir = 1; //anti-clock
							else
								dir = -1; // clock
						}
						System.out.println("Players current pos:" + act.player1.getCurrentPos());
						// this if and phase 3 and 4 are only here because we can not move small distances
						if(Math.abs(shotData.pos -act.player1.getCurrentPos()) < epsPath +10 && Math.abs(shotData.pos -act.player1.getCurrentPos()) > 3)
						{
							moveDest = shotData.pos-50 > 0 ? shotData.pos-50: 0;
							this.addOrder(new PrimitiveOrder(act.player1.getId(),20, moveDest, dir*10, shotData.fromAngle));
							phase++;
						}
						else
						{
							this.addOrder(new PrimitiveOrder(act.player1.getId(),140, shotData.pos, dir*10, shotData.fromAngle));
							phase+=3;
						}
						break;
					case 3:
						if(Math.abs(act.player1.getCurrentPos() - moveDest) < epsPath && rotError(act.player1.getCurrentRot(), shotData.fromAngle) < epsRot)
							phase++;
						break;
					case 4:
						this.addOrder(new PrimitiveOrder(act.player1.getId(),20, shotData.pos, 0, 0));
						phase++;
						break;
					case 5:
						if(Math.abs(act.player1.getCurrentPos() - shotData.pos) < epsPath)
							phase++;
						break;
					case 6:
						System.out.println("Players pos when passing:" + act.player1.getCurrentPos());
						int rotDest = shotData.fromAngle + shotData.clockwise*32; // almost one complete turn
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, shotData.clockwise*70, rotDest));
						phase++;
						break;
					case 7: // wait a few turns before trying to plan more
						phaseStuckCount++;
						if(phaseStuckCount > 20) // TODO: Tune this constant or use some time constant instead
						{
							state = State.PLANNING;
							phaseStuckCount = 0;
						}
						break;
					default:
						System.out.println("BAD!");
						break;
				}
			}
			else if(state.equals(State.SHOOTING))
			{
				switch(phase)
				{
					case 0: // check if the puck has a good position
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase+=2;
						else // need to move the puck.
						{
							// TODO: check this part, maybe also use player.y < 0 to set dir ??
							// move puck away from line, closer to final destination
							if(act.player1.getLocation(shotData.closestPos).subtract(puck).norm() < farFromLine)
							{
								
								int dir = 0;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									dir = team.equals(Team.HOME) ? 1 : -1;
									angleDest = team.equals(Team.HOME) ? 59 : 186;
								}
								else
								{
									dir = team.equals(Team.HOME) ? -1 : 1;
									angleDest = team.equals(Team.HOME) ? 197 : 69;
								}
								// TODO: may need to increase speed for short rotations
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*20, angleDest));
							}
							else
							{
								// TODO: move puck closer to the line
								// turn clockwise
								int dir = team.equals(Team.HOME) ? -1 : 1;
								if(act.player1.getLocation().getX() > puck.getX()) // player is to the right of puck;
								{
									if(act.player1.getLocation().getY() > puck.getY())//puck above player
									{
										dir = team.equals(Team.HOME) ? -1 : 1;
										angleDest = team.equals(Team.HOME) ? 32 : 160;
									}
									else
									{
										dir = team.equals(Team.HOME) ? 1 : -1;
										angleDest = team.equals(Team.HOME) ? 96 : 224;
									}
								}
								else
								{
									if(act.player1.getLocation().getY() > puck.getY())
									{
										dir = team.equals(Team.HOME) ? 1 : -1;
										angleDest = team.equals(Team.HOME) ? 224 : 96;
									}
									else
									{
										dir = team.equals(Team.HOME) ? -1 : 1;
										angleDest = team.equals(Team.HOME) ? 160 : 32;
									}
								}
								
								this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, dir*20, angleDest));
							}
							phase++;
						}
						break;
					/*WE MAY NEED THIS case 1: 
						if(Math.abs(act.player1.getCurrentRot()-angleDest) < epsRot) // dålig runt nollan?
							phase++;
						break;*/
					case 1:
						shotData.calculateData(act, puck, team);
						if(shotData.possible)
							phase++;
						else
						{
							phaseStuckCount++;
							if (phaseStuckCount > 50)
							{
								phaseStuckCount = 0;
								state = State.PLANNING; // TODO: plan or go to phase1 ???
							}
						}
						break;
					case 2: //backa och rotera ifrån pucken till en bra vinkel att skjuta ifrån
						//shotData.calculateData(act, puck, team);
						int dir = 0;
						if(act.player1.getCurrentRot() > shotData.fromAngle)
						{
							if(act.player1.getCurrentRot()-shotData.fromAngle > 128)
								dir = -1; //anti-clock
							else
								dir = 1; // clock
						}
						else
						{
							if(shotData.fromAngle - act.player1.getCurrentRot() > 128)
								dir = 1; //anti-clock
							else
								dir = -1; // clock
						}
						
						//dir = team.equals(Team.HOME) ? dir*-1 : dir;
						
						System.out.println("Players current pos:" + act.player1.getCurrentPos());
						// this if and phase 3 and 4 are only here because we can not move small distances
						if(Math.abs(shotData.pos -act.player1.getCurrentPos()) < epsPath +10 && Math.abs(shotData.pos -act.player1.getCurrentPos()) > 3)
						{
							moveDest = shotData.pos-50 > 0 ? shotData.pos-50: 0;
							this.addOrder(new PrimitiveOrder(act.player1.getId(),20, moveDest, dir*10, shotData.fromAngle));
							phase++;
						}
						else
						{
							this.addOrder(new PrimitiveOrder(act.player1.getId(),30, shotData.pos, dir*10, shotData.fromAngle));
							phase+=3;
						}
						break;
					case 3:
						if(Math.abs(act.player1.getCurrentPos() - moveDest) < epsPath && rotError(act.player1.getCurrentRot(), shotData.fromAngle) < epsRot)
							phase++;
						break;
					case 4:
						this.addOrder(new PrimitiveOrder(act.player1.getId(),20, shotData.pos, 0, 0));
						phase++;
						break;
					case 5:
						if(Math.abs(act.player1.getCurrentPos() - shotData.pos) < epsPath)
							phase++;
						break;
					case 6:
						int rotDest = shotData.fromAngle + shotData.clockwise*32; // almost one complete turn
						this.addOrder(new PrimitiveOrder(act.player1.getId(),0, 0, shotData.clockwise*127, rotDest));
						phase++;
						break;
					case 7: // wait a few turns before trying to plan more
						phaseStuckCount++;
						if(phaseStuckCount > 20) // TODO: Tune this constant or use some time constant instead
						{
							agent.current.from = null; // ugly way of detecting when we have a goal(for the plan) or not
							state = State.PLANNING;
							phaseStuckCount = 0;
						}
						break;
					default:
						System.out.println("BAD!");
						break;
				}
			}
		}
		else if(mainState.equals(MainState.DEFENDING))
		{
			for(Iterator<Condition> iter = agent.current.effects.iterator(); iter.hasNext();)
			{
				Condition cond = iter.next();
				if(cond.name.equals(Condition.Name.CanGetPuck))
				{
					Player player = ((CondCanGetPuck)cond).player;
					if(friendlyPlayers.contains(player)) // test
					{
						agent.reset();
						//act = new Action(Action.Act.CollectPuck, player);
						mainState = MainState.ATTACKING;
						state = State.PLANNING;
						phase = 0;
						phaseStuckCount = 0;
						return;
					}
				}
			}
			boolean faceFront = false;
			if(puck.getY() <= 0) // 
				faceFront = true;
			
			friendlyPlayers.get(0);
		}
		//ACT and update frame with action
		/*if(act.act == Action.Act.Move)
		{
			int i = 0;
			for(; i < 255; i++)
			{
				if(act.player1.path[i].equals(act.to))
					break;
			}
			act.player1.setState(i, 0);
		}
		// more complex comand. Move player behind puck whitout disturbing puck
		// then with a slight "invard" angle move to the puck
		else if(act.act == Action.Act.CollectPuck)
		{
			int i = 1;
			double xDiff = Math.abs((act.player1.path[0].getX()) - puck.getX());
			double yDiff = Math.abs((act.player1.path[0].getY()) - puck.getY());
			double oldDiff = yDiff+xDiff;
			double diff = 0;
			for(; i < 255; i++)
			{	
				xDiff = Math.abs((act.player1.path[i].getX()) - puck.getX());
				yDiff = Math.abs((act.player1.path[i].getY()) - puck.getY());
				diff = yDiff+xDiff;
				if(diff >= oldDiff)
				{
					i--;
					break;
				}
				oldDiff = diff;
			}
			act.player1.setState(i, 0);
		}
		else if(act.act == Action.Act.Shoot)
		{
			puck.setState((int)act.to.getX(), (int)act.to.getY()); // in goal
			act.from = null;
		}
		else if(act.act == Action.Act.Pass)
		{
			puck.setState((int)act.player2.getLocation().getX(), (int)act.player2.getLocation().getY());
		}
		else if(act.act == Action.Act.MoveWithPuck)
		{	
			int i = 0;
			for(; i < 256; i++)
			{
				if(act.player1.path[i].equals(act.to))
					break;
			}
			act.player1.setState(i, 0);
			puck.setState((int)act.player1.getLocation().getX(), (int)act.player1.getLocation().getY());
		}*/
		//frame.update(frame.getGraphics());
		if(this.hasOrder())
		{
			try {
				this.send();
				System.out.println(" worked!");
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	int rotError(int rotCurrent, int rotDestination)
	{
		int posError = 0, negError = 0;
		if(rotDestination < rotCurrent) {
			
			posError = rotCurrent - rotDestination;
			
			
		} else if (rotDestination > rotCurrent) {
		
			posError =  rotDestination - rotCurrent;
		}
		posError = posError % (256-epsRot);
		return posError;
	}
	boolean playerStillHasPuck(Player player){
		for(Iterator<Condition> iter = agent.current.effects.iterator(); iter.hasNext();)
		{
			Condition cond = iter.next();
			if(cond.name.equals(Condition.Name.HasPuck) && ((CondHasPuck)cond).player.equals(player))
				return true;
		}
		return false;
	}
	
	public static void main(String[] arg) throws IOException{
		
		SocketAddress gameAddress = new InetSocketAddress(java.net.InetAddress.getLocalHost() ,60040);//"S2007", 60040);

		PlanningAI h = new PlanningAI(60090, gameAddress);
		PlanningAI b = new PlanningAI(60089, gameAddress);

	}
}