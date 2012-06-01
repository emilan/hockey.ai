package AI;

import java.io.IOException;
import java.net.SocketAddress;

import agent.Agent;
import core.AIBase;
import core.ControllablePlayer;
import core.Player;
import core.Puck;
import core.Team;

public class FakeAI extends AIBase{
	public FakeAI(int localPort, SocketAddress gameAddress)  throws IOException{
		super(localPort, gameAddress);
	}
	@Override
	public void onNewState() {

	}
}
