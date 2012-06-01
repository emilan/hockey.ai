package agent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Main {

public static void main(String[] arg) throws IOException{
		
		SocketAddress gameAddress = new InetSocketAddress(java.net.InetAddress.getLocalHost() ,60040);//"S2007", 60040);
		PlanningAI d = new PlanningAI(60090, gameAddress);

	}
}
