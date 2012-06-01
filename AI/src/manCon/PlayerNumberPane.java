package manCon;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.ControllablePlayer;

public class PlayerNumberPane extends JPanel implements ActionListener{
	String[] alternatives={"0","1","2","3","4","5"};
	JComboBox playerSelect;
	int currentPlayer=0;
	List<ControllablePlayer> playerList ;
	ArrayList<PlayerPanel> playerPanels= new ArrayList<PlayerPanel>();
	PlayerNumberPane(List<ControllablePlayer> list){
		//setSize(100,50);
		playerList=list;
		playerSelect=new JComboBox(alternatives);
		playerSelect.setSelectedIndex(currentPlayer);
		add(new JLabel("Player"));
		playerSelect.addActionListener(this);
		add(playerSelect);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
        int val = Integer.valueOf((String)cb.getSelectedItem());
		if(val>=0&&val<6){
			currentPlayer=val;
		}
		for(PlayerPanel p : playerPanels){
			p.setCurrentPlayer(playerList.get(currentPlayer));
		}
	}
	
	public void addPlayerPanel(PlayerPanel p){
		playerPanels.add(p);
	}
	
	
}
