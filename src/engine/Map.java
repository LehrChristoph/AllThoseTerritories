/**
 * 
 */
package engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.omg.CORBA.INTERNAL;

import game.AllThoseTerritories;

/**
 * @author christoph
 *
 */
public class Map extends JPanel implements MouseMotionListener, MouseListener, ActionListener{
	
	private HashMap<String, Territory> territories = new HashMap<String, Territory>();
	private HashMap<String, Territory> territoriesToPick = new HashMap<String, Territory>();
	private HashMap<String, Continent> continents = new HashMap<>();
	private Territory activeTerritories;
	private Territory coloredTerritories;
	
	private AllThoseTerritories game;
	
	public static final Color activeTerritoryColor = Color.RED;
	public static final Color defaultColor = Color.lightGray;
	public static final Color enteredColor = Color.GRAY;
	
	private JButton nextRoundBtn = new JButton("next Round");
	
	public Map(){
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		
		this.nextRoundBtn.addActionListener(this);
		this.setLocation(1100, 550);
		this.add(this.nextRoundBtn);
	}
	
	public void setSuperVisor(AllThoseTerritories game){
		if(game != null){
			this.game = game;
		}
	}
	
	public void add(Queue<Integer> x, Queue<Integer> y, String name){
		if(x.size() != y.size()){
			return;
		}else{		
			if(this.territories.containsKey(name)){
				this.territories.get(name).add(x,y);
				
			}else{
				this.territories.put(name, new Territory(x, y, name));
			}
		}
	}
	
	public void setCapital(String name, int x, int y){
		if(this.territories.containsKey(name)){
			this.territories.get(name).setCapital(new Point(x, y));
		}
	}
	
	public void setNeighbours(String country, String[] neighbours){
		if(this.territories.containsKey(country)){
			for(String neighbour : neighbours){
				neighbour = neighbour.trim();
				if(this.territories.containsKey(neighbour)){
					this.territories.get(country).addNeighbour(this.territories.get(neighbour));
					this.territories.get(neighbour).addNeighbour(this.territories.get(country));
				}
			}
		}
	}
	
	public void addContinent(String name, int bonus, String[] territories){
		if(!this.continents.containsKey(name)){
			this.continents.put(name, new Continent(name, bonus));
		}
		
		for(String territory : territories){
			territory = territory.trim();
			if(this.territories.containsKey(territory)){
				this.continents.get(name).addTerritory(this.territories.get(territory));
			}
		}
	}
	
	public int getSumOfTerritories(){
		return this.territories.size();
	}
	
	public void start(){
		this.territoriesToPick = (HashMap<String, Territory>) this.territories.clone();
	}
	
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	        
	    g.setColor(Color.BLACK);
	    
	    for(String territory : this.territories.keySet()){
	    	Point currentTerritory = this.territories.get(territory).getCapital();
	    	
	    	g.drawString(String.valueOf(this.territories.get(territory).getArmies()), (int)currentTerritory.getX(), (int)currentTerritory.getY());
	    	
	    	for(Point captial : this.territories.get(territory).getNeighbourCaptials()){
	    		g.drawLine((int)currentTerritory.getX(), (int)currentTerritory.getY(), (int)captial.getX(), (int)captial.getY());
	    	}
	    		
	    }
	    
	    for(String name : this.territories.keySet()){
	    	this.territories.get(name).paintComponent(g, defaultColor);
	    }
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Territory enteredTerritory = null;
		
		for(String name : this.territories.keySet()){
			for(Polygon p: this.territories.get(name).getLandslides()){
				if(p.contains(e.getPoint())){
					enteredTerritory = this.territories.get(name);
					break;
				}
			}
		}
		
		if(this.activeTerritories != null ){
			if(enteredTerritory == this.activeTerritories){
				return;
			}else if(enteredTerritory !=null && enteredTerritory.isNeighbourOf(this.activeTerritories)){
				if(this.coloredTerritories != null){
					this.coloredTerritories.paintComponent(this.getGraphics());
				}
				this.coloredTerritories = enteredTerritory;
				enteredTerritory.paintComponent(this.getGraphics(), enteredColor);
				return;
			}else{
				if(this.coloredTerritories != null){
					this.coloredTerritories.paintComponent(this.getGraphics());
					this.coloredTerritories =null;
				}
				return;
			}
		}else{
			if(enteredTerritory != null && enteredTerritory != this.coloredTerritories){
				if(this.coloredTerritories != null){
					this.coloredTerritories.paintComponent(this.getGraphics());
				}
				this.coloredTerritories = enteredTerritory;
				enteredTerritory.paintComponent(this.getGraphics(), enteredColor);
			}else if(this.coloredTerritories !=null && enteredTerritory == null){
				this.coloredTerritories.paintComponent(this.getGraphics());
				this.coloredTerritories = null;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		Territory enteredTerritory = null;
		
		for(String name : this.territories.keySet()){
			for(Polygon p: this.territories.get(name).getLandslides()){
				if(p.contains(e.getPoint())){
					enteredTerritory = this.territories.get(name);
					break;
				}
			}
		}
		
		if(enteredTerritory != null){

			if(this.game.getPhase().equalsIgnoreCase(AllThoseTerritories.PHASE_PICK)){
				pickTerritory(enteredTerritory);
			}else if(enteredTerritory.getOwner().equals(this.game.getCurrentPlayer()) && this.game.getPhase().equalsIgnoreCase(AllThoseTerritories.PHASE_PLACE)){
				placeArmies(enteredTerritory);
			}else if(this.game.getPhase().equalsIgnoreCase(AllThoseTerritories.PHASE_ATTACK_MOVE)){
				if(enteredTerritory.getOwner().equals(this.game.getCurrentPlayer()) && this.activeTerritories == null){
					this.activeTerritories = enteredTerritory;
					this.activeTerritories.setActive(true);
				}else if(enteredTerritory.getOwner().equals(this.game.getCurrentPlayer()) && this.activeTerritories.isNeighbourOf(enteredTerritory) && enteredTerritory.getOwner().equals(this.game.getCurrentPlayer())){
					moveArmie(enteredTerritory);
				}else if(!enteredTerritory.getOwner().equals(this.game.getCurrentPlayer()) && this.activeTerritories.isNeighbourOf(enteredTerritory)){
					attack(enteredTerritory);
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void pickTerritory(Territory picked){
		if(!this.territoriesToPick.containsKey(picked.getName())){
			return;
		}
		
		Player currentPlayer = this.game.getCurrentPlayer();
		picked.setOwner(currentPlayer);
		picked.incrementArmies();
		picked.paintComponent(this.getGraphics());
		currentPlayer.addTerritory(picked);
		this.territoriesToPick.remove(picked.getName());
		
		this.game.nextPlayer();
		if(this.territoriesToPick.size() <=0){
			this.game.nextPhase();
		}
	}
	
	private void placeArmies(Territory picked){
		
		Player currentPlayer = this.game.getCurrentPlayer();
		if(currentPlayer.armiesLeftToPlace()){
			picked.incrementArmies();
			if(! currentPlayer.armyPlaced()){
				this.game.nextPhase();
			}
			picked.paintComponent(this.getGraphics());
		}
	}
	
	private void moveArmie(Territory picked){
		
		
	}
	
	private void attack(Territory picked){
		int attackArmies = this.activeTerritories.getArmies();
		if(attackArmies >=4){
			attackArmies =3;
		}else if (attackArmies > 1){
			attackArmies-=1;
		}else{
			return;
		}
		
		int defendArmies = picked.getArmies();
		if(defendArmies >2){
			defendArmies =2;
		}
		
		ArrayList<Integer> attackDices = new ArrayList<>();
		ArrayList<Integer> defendDices = new ArrayList<>();

		for(int i=0; i < attackArmies; i++){
			attackDices.add((int)Math.floor(6*Math.random()));
		}
		
		Collections.sort(attackDices);
		Collections.reverse(attackDices);

		for(int i=0; i < defendArmies; i++){
			defendDices.add((int)Math.floor(6*Math.random()));
		}
		
		Collections.sort(defendDices);
		Collections.reverse(defendDices);
		
		int defenderWins =0;
		
		if(attackDices.get(0)<defendDices.get(0)){
			defenderWins++;
		}
		if(attackDices.size()>1 && defendDices.size()>1 && attackDices.get(1)<defendDices.get(1)){
			defenderWins++;
		}
		
		if(defenderWins ==0){
			picked.setOwner(this.game.getCurrentPlayer());
			picked.setArmies(attackArmies);
		}else{
			this.activeTerritories.decrementArmies(defenderWins);
			picked.decrementArmies(defendArmies-defenderWins);
		}
		
		this.activeTerritories.paintComponent(this.getGraphics());
		picked.paintComponent(this.getGraphics());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		this.activeTerritories.setActive(false);
		this.activeTerritories.paintComponent(this.getGraphics());
		this.activeTerritories=null;
		this.game.nextPhase();
	}
}

class Continent{
	
	private int continentBonus =0;
	private ArrayList<Territory> territories = new ArrayList<>();
	private String name = new String();
	
	public Continent(String name, int continentBonus){
		this.continentBonus = continentBonus;
		this.name = name;

	}
	public Continent(String name, int continentBonus, ArrayList<Territory> territories){
		this.continentBonus = continentBonus;
		this.name = name;
		this.territories = territories;
	}
	
	public void addTerritory(Territory territory){
		this.territories.add(territory);
	}
	
	public ArrayList<Territory> getTerritories(){
		return this.territories;
	}
	
	public int getContinentBonus(){
		return this.continentBonus;
	}
	
	public String getName(){
		return this.name;
	}
}
