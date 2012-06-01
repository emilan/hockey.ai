package core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class Reciever extends Thread{
	ArrayList<Updateable> toUpdate=new ArrayList<Updateable>();
	DatagramSocket socket;
	DatagramPacket rcv;
	/**
	 * Constructs and initializes a reciever that listens on the given socket and updates the updateable when a message is recieved.
	 * @param socket
	 * @param updateable
	 */
	Reciever(DatagramSocket socket,Updateable u){
		addUpdateable(u);
		this.socket=socket;
		
		rcv = new DatagramPacket(new byte[1000], 1000);
		
		start(); // TODO: was started last in PlanningAI constructor
		System.out.println("Listening");
	}
	@Override
	/**
	 * Method that is run internally for listening on the selected port
	 */
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				socket.receive(rcv);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] pos = rcv.getData();
			int length = rcv.getLength();

			if(length == 1 && pos[0] == 'D')// Servern frågar om du är död!
			{
				byte no = (byte)'N';
				for(Updateable u:toUpdate){
					
					u.answer(no);//
				}
			}
			else if (length == 29 * 4)
			{
			//
				int[] a = bytesToInts(pos);
				for(Updateable u:toUpdate){
					u.update(a);
				}
			}
			else if (length % (5 * 4) == 0) {
				int[] a = bytesToInts(pos);
				for(Updateable u : toUpdate){
					u.failed(a);
				}
			}
		}
	}
	private int[] bytesToInts(byte[] pos) {
		int[] a = new int[rcv.getLength() / 4];
		for (int i = 0; i < rcv.getLength() / 4; i++) {
			for (int j = 0; j < 4; j++) {
				a[i] += (pos[4 * i + j] & 0xff) << (8 * j);
			}
		}
		return a;
	}
	void addUpdateable(Updateable u){
		toUpdate.add(u);
	}
}
