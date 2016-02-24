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
	public static final String PHASE_ATTACK_MOVE = "attack_move";
	
	private Queue<Player> players = new LinkedList<Player>();
	
	public AllThoseTerritories(String mapFile) {
		
		if(mapFile.isEmpty()){
			JFileChooser chooser = new JFileChooser();
	        // Dialog zum Oeffnen von Dateien anzeigen
	        chooser.showOpenDialog(this);
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
		
		Player one = new Player("horst", Color.BLUE, false);
		Player two = new Player("AI", Color.CYAN, true);
		this.players.offer(one);
		this.players.offer(two);
		
		this.phases.offer(this.PHASE_PICK);
		this.phases.offer(this.PHASE_PLACE);
		this.phases.offer(this.PHASE_ATTACK_MOVE);
		
		this.terrietories.setSuperVisor(this);
		
		this.terrietories.start();
	}
	
	public String getPhase(){
		return this.phases.peek();
	}
	
	public String nextPhase(){
		String current = this.phases.poll();
		
		if(current.equalsIgnoreCase(PHASE_PICK)){
			getCurrentPlayer().setContinents(this.terrietories.checkContinents(getCurrentPlayer().getTerritories()));
		}else{
			Queue<Player> tmpPlayerQueue = this.players;
			for(Player tmpPlayer : tmpPlayerQueue){
				if(tmpPlayer.getTerritories().size() <=0) {
					System.out.println("Player " + this.players.peek() + " defeated");
					this.players.remove(tmpPlayer);
				}
			}
			if(this.players.size() <= 1 ){
				System.out.println("Player " + this.players.poll() +" won the game");
				System.exit(NORMAL);
			}
		}
		
		if (current.equalsIgnoreCase(PHASE_PLACE)){
			this.phases.offer(current);
		}else if(current.equalsIgnoreCase(PHASE_ATTACK_MOVE)){
			this.nextPlayer();
			this.phases.offer(current);
		}

		/*if(!this.getCurrentPlayer().armiesLeftToPlace()){
			current = this.phases.poll();
			//this.phases.offer(current);
		}*/

		System.out.println("New Phase: " + this.phases.peek());
		return this.phases.peek();
	}

	public Player getCurrentPlayer(){
		return this.players.peek();
	}
	
	public Player nextPlayer(){
		this.players.offer(this.players.poll());

		if(!this.phases.peek().equalsIgnoreCase(this.PHASE_PICK)){
			Queue<Player> tmpPlayerQueue = this.players;
			for(Player tmpPlayer : tmpPlayerQueue){
				if(tmpPlayer.getTerritories().size() <=0) {
					System.out.println("Player " + this.players.peek() + " defeated");
					this.players.remove(tmpPlayer);
				}
			}
			if(this.players.size() <= 1 ){
				System.out.println("Player " + this.players.poll() +" won the game");
				System.exit(NORMAL);
			}
		}
		
		System.out.println("Current Player: " + this.players.peek());
		this.players.peek().setContinents(this.terrietories.checkContinents(getCurrentPlayer().getTerritories()));

		getCurrentPlayer().newRound();

		if (getCurrentPlayer().isAI()) {
			if (getPhase().equals(this.PHASE_PICK)) {
				terrietories.kipick();
			}
			if (getPhase().equals(this.PHASE_PLACE)) {
				terrietories.kiplace(getCurrentPlayer());
				terrietories.kiattack(getCurrentPlayer());
				this.nextPlayer();
			}
		}
		return this.players.peek();
	}
}