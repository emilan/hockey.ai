package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
/**
 * Class
 * @author Anders Ryndel
 *
 */
public abstract class AIBase implements Updateable{
	private int gamePort;
	private int localPort;
	protected Reciever reciever;
	private Sender sender;
	protected Team team;
	protected LinkedList<ControllablePlayer> friendlyPlayers=new LinkedList<ControllablePlayer>();
	protected LinkedList<Player> opposingPlayers=new LinkedList<Player>();
	protected Puck puck;
	protected int gameTime=0;
	int myGoals = 0;
	int enemyGoals = 0;
	Date d=new Date();
	/**
	 * Creates and initaializes basic AI functionality and connects it to the game at the selected adress
	 * @param localPort
	 * @param gameAddress
	 * @throws IOException
	 */
	public  AIBase(int localPort,SocketAddress gameAddress) throws IOException{
		
		puck=new Puck();
		
		
		
		DatagramSocket socket=doHandshake(localPort, gameAddress);
		System.out.println("Handshake Done");
		Team otherTeam;
		System.out.println("Team: " + team);
		if(team.equals(Team.HOME)){
			otherTeam=Team.AWAY;
		}else{
			otherTeam=Team.HOME;
		}
		for(int i=0;i<6;i++){
			friendlyPlayers.add(new ControllablePlayer(i, team, puck));
			opposingPlayers.add(new Player(i, otherTeam, puck));
		}
		System.out.println("handshakedone");
		sender=new Sender(socket,gameAddress);
		reciever=new Reciever(socket,this);
		
	}
	@Override
	public void answer(byte n)
	{
		try {
			sender.answer(n);
		}
		catch(IOException e){}
	}
	@Override
	public void failed(int[] a) {
		// Do nothing. 
		// TODO: Do something...
	}
	@Override
	public void update(int[] a) {	
		int i=0;
		myGoals = a[i++];
		enemyGoals = a[i++];
		puck.setState(a[i++], a[i++]);
		gameTime=a[i++];
	
		//System.out.println(puck);
//		for(int q=0;q<a.length;q++){
//			System.out.print(a[q]);
//			System.out.print(" ");
//		}
//		System.out.println();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		for(Player p:friendlyPlayers){
			p.setState(a[i++], a[i++]);
		}
		for(Player p:opposingPlayers){
			p.setState(a[i++], a[i++]);
		}
		onNewState();
	}
	private DatagramSocket doHandshake(int localPort,SocketAddress gameAddress) throws IOException{
		DatagramSocket socket = null;
		System.out.println("Shaking hands");
		if(localPort==0){
			socket=new DatagramSocket();
		}else{
			socket=new DatagramSocket(localPort);
		}
		
		//socket.connect(gameAddress);
		byte[] message="a".getBytes();
		
		DatagramPacket data = new DatagramPacket(message, message.length,gameAddress);
		socket.send(data);
		DatagramPacket rcv = new DatagramPacket(new byte[1000], 1000);
		socket.receive(rcv);
		byte[] pos = rcv.getData();
		int a=0;
		
			for (int j = 0; j < 4; j++) {
				a += (pos[j] & 0xff) << (8 * j);
			}
		
		if(a==1){
			team=Team.HOME;
		}else if(a==2){
			team=Team.AWAY;
			System.out.println("AWAYTEAM");
		}else{
			System.out.println("handshake not recieved");
			System.exit(1);
		}
		return socket; 
	}
	public void send() throws IOException{
		sender.send();
	}
	private String getString(DatagramPacket data){
		return new String(data.getData(),0,data.getLength());
	}
	public void addOrder(PrimitiveOrder order){
		sender.add(order);
	}
	public boolean hasOrder(){
		return sender.size() > 0;
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
	public abstract void onNewState();
	
	
}
