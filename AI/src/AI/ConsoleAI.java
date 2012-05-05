package AI;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.NoSuchElementException;
import java.util.Scanner;

import core.AIBase;
import core.PrimitiveOrder;

public class ConsoleAI extends AIBase {

	public ConsoleAI(int localPort, SocketAddress gameAddress)
			throws IOException {
		super(localPort, gameAddress);
	}

	@Override
	public void onNewState() {
	}
	
	public static void main(String[] args) throws IOException {
		SocketAddress gameAddress = new InetSocketAddress("S2007", 60040);
		ConsoleAI m = new ConsoleAI(60090, gameAddress);
		ConsoleAI d = new ConsoleAI(60089, gameAddress);
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Format: team(0-1) id(0-5) transSpeed(0-255) transDest(0-255) rotSpeed(-128-127) rotDest(0-255)");
		
		while (true) {
			String s = sc.nextLine();
			Scanner sc2 = new Scanner(s);
			try {
				while (sc2.hasNextInt()) {
					int a = sc2.nextInt();
					int id = sc2.nextInt();
					int transSpeed = sc2.nextInt();
					int transDest = sc2.nextInt();
					int rotSpeed = sc2.nextInt();
					int rotDest = sc2.nextInt();
					
					ConsoleAI ai = a == 1 ? d : m;
					ai.addOrder(new PrimitiveOrder(id, transSpeed, transDest, rotSpeed, rotDest));
				}
				
				d.send();
				m.send();
			}
			catch (NoSuchElementException e) {
				System.out.println("Invalid input");
			}
		}
	}	
}
