package game;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import engine.Map;
import engine.Player;

public class AllThoseTerritories extends JFrame{
	
	private Map terrietories = new Map();
	
	private Queue<String> phases = new LinkedList();
	public static final String PHASE_PICK = "pick";
	public static final String PHASE_PLACE = "place";
	public static final String PHASE_ATTACK_MOVE = "move";
	
	private Queue<Player> players = new LinkedList<Player>();
	
	public AllThoseTerritories(String mapFile) {
		
		if(mapFile.isEmpty()){
			JFileChooser chooser = new JFileChooser();
	        // Dialog zum Oeffnen von Dateien anzeigen
	        chooser.showOpenDialog(null);
	        File test = chooser.getSelectedFile();
	        if(chooser.getSelectedFile() != null){
	        	mapFile = chooser.getSelectedFile().toString();
	        }else{
	        	return;
	        }
	        
		}
		
		this.setSize(1250, 650);
		this.setResizable(false);
        this.setTitle("Command and Conquer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//readMapFile(mapFile);
		WorldGenerator wg = new WorldGenerator();
		this.terrietories = wg.generateMap(mapFile);
		this.add(this.terrietories);
		
		Player one = new Player("horst", Color.BLUE);
		Player two = new Player("asdf", Color.CYAN);
		this.players.offer(one);
		this.players.offer(two);
		
		this.phases.offer(this.PHASE_PICK);
		this.phases.offer(this.PHASE_PLACE);
		this.phases.offer(this.PHASE_ATTACK_MOVE);
		
		this.terrietories.start();
		
		this.terrietories.setSuperVisor(this);
		
	}
	
	public String getPhase(){
		return this.phases.peek();
	}
	
	public String nextPhase(){
		String current = this.phases.poll();
		if(current.equalsIgnoreCase(PHASE_PICK)){
			getCurrentPlayer().newRound();
		}else if(current.equalsIgnoreCase(PHASE_PLACE)){
			getCurrentPlayer().newRound();
			this.phases.offer(current);
		}else{
			this.phases.offer(current);
			this.nextPlayer();
		}
		System.out.println("New Phase: " + this.phases.peek());
		return this.phases.peek();
	}
	
	public Player getCurrentPlayer(){
		return this.players.peek();
	}
	
	public Player nextPlayer(){
		this.players.offer(this.players.poll());
		System.out.println("Current Player: " + this.players.peek());
		return this.players.peek();
	}
}