package engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import javax.swing.JPanel;

/**
 * 
 * @author Christoph
 *
 */
public class Country extends JPanel{
	//country data
	private ArrayList<Polygon> territories = new ArrayList<>();
	private Point capital;
	private String name;
	private boolean isActive = false;
	private ArrayList<Country> neighbours = new ArrayList<Country>();
	private Player owner = null;
	
	//army and reenforcement data
	private int reenforcements =0;
	private HashMap<Country, Integer> gotArmiesFrom = new HashMap<>();
	private int armies =0;
	private boolean isTakenOver = false;
	
	/**
	 * Creates a new Country object
	 * @param x : list of x-Coordinates
	 * @param y : list of y-Coordinates
	 * @param name : name of this country
	 */
	public Country(Queue<Integer> x, Queue<Integer> y, String name){
		this.name = name;
		
		//check if size of coordinate lists are equal
		if(x.size() != y.size()){
			return;
		}
		//create new polygon, shape of the first territory of this country
        Polygon p = new Polygon();
		do{
			p.addPoint(x.poll(), y.poll());
		}while (x.size() >0);
		
		this.territories.add(p);
	}
	
	/**
	 * adds an additional territory to this country
	 * @param x : list of x-Coordinates
	 * @param y : list of y-Coordinates
	 */
	public void add(Queue<Integer> x, Queue<Integer> y){
		//check if size of x and y coordinate lists are equal
		if(x.size() != y.size()){
			return;
		}else{
			//create new territory polygon, shape of the territory
			Polygon p = new Polygon();
			do{
				p.addPoint(x.poll(), y.poll());
			}while (x.size() >0);
			//add the new territory to list
			this.territories.add(p);
			//this.setSize(this.getPreferredSize());
		}
	}
	
	/**
	 * sets the owner of this country
	 * @param owner
	 */
	public void setOwner(Player owner){
		//check if player object exists
		if(this.owner != null){
			this.owner.removeTerritory(this);
		}
		//set owner
		this.owner = owner;
		//add territory to new owner
		this.owner.addTerritory(this);
		//set country to taken over, only relevant for army movement
		this.isTakenOver = true;
	}
	
	/**
	 * returns the current owner of the country
	 * @return
	 */
	public Player getOwner(){
		return this.owner;
	}
	
	/**
	 * paints this territory
	 */
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
	    //cast Graphics object of Graphics2D object, used for drawing different lines 
	    Graphics2D g2d = (Graphics2D) g;
	    
	    Color countryColor;
	    
	    //set country color
	    if(this.isActive){
	    	countryColor = Map.activeTerritoryColor;
	    }else{
	    	if(this.owner != null && this.owner.getPlayerColor()!=null){
	    		countryColor = this.owner.getPlayerColor();
	    	}else{
	    		countryColor = Map.defaultColor;
	    	}
	    }
	    
	    //draw the single territories
	    for(Polygon p : this.territories){
	    	g2d.setColor(Color.BLACK);
	    	g2d.setStroke(new BasicStroke(5));
			g2d.drawPolygon(p);
			g2d.setColor(countryColor);
			g2d.fillPolygon(p);
	    }
	    
	    //draw containing army count
	    g2d.setColor(Color.black);
	    g2d.drawString(String.valueOf(this.armies), (int)this.capital.getX(), (int) this.capital.getY());
	}
	
	/**
	 * paint the country with a specific color
	 * @param g
	 * @param territoryColor
	 */
	public void paintComponent(Graphics g, Color territoryColor) {
	    super.paintComponent(g);
	    
	    //cast Graphics object of Graphics2D object, used for drawing different lines 
	    Graphics2D g2d = (Graphics2D) g;
	    
	    //check if country is active, no need to repaint
	    if(this.isActive){
	    	return;
	    }
	    
	    //draw single territories of country
	    for(Polygon p : this.territories){
	    	g2d.setColor(Color.BLACK);
	    	g2d.setStroke(new BasicStroke(5));
			g2d.drawPolygon(p);
			g2d.setColor(territoryColor);
			g2d.fillPolygon(p);
	    }
	    
	    //draw containing army count
	    g2d.setColor(Color.black);
	    g2d.drawString(String.valueOf(this.armies), (int)this.capital.getX(), (int) this.capital.getY());
	}
	
	/**
	 * sets the capital of this country
	 * @param pos : x and y coordinate of the countrie's capital
	 */
	public void setCapital(Point pos){
		this.capital = pos;
	}
	
	/**
	 * returns the position of the current capital
	 * @return
	 */
	public Point getCapital(){
		return this.capital;
	}
	
	/**
	 * returns the name of the country
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * returns the list of containing territories
	 * @return
	 */
	public ArrayList<Polygon> getTerritories(){
		return this.territories;
	}
	
	/**
	 * marks this country as active or non-active
	 * @param isActive
	 */
	public void setActive(boolean isActive){
		this.isActive = isActive;
	}
	
	/**
	 * adds a given country as neighbour and this country as a neighbour of the given country
	 * @param neighbour : country object to set as neighbour
	 */
	public void addNeighbour(Country neighbour){
		this.neighbours.add(neighbour);
	}
	
	/**
	 * returns the current list of neighbours
	 * @return
	 */
	public ArrayList<Country> getNeighbours(){
		return this.neighbours;
	}
	
	/**
	 * returns a positions of the neighbour capitals as list
	 * @return
	 */
	public ArrayList<Point> getNeighbourCaptials(){
		ArrayList<Point> captitals = new ArrayList<>();
		//iterate over neighbours
		for(Country neighbour : this.neighbours){
			//get the capital positions
			captitals.add(neighbour.getCapital());
		}
		
		return captitals;
	}
	
	/**
	 * returns the armies which are in this country
	 * @return
	 */
	public int getArmies(){
		return this.armies;
	}
	
	/**
	 * increments the containing armies by 1
	 */
	public void incrementArmies(){
		this.armies++;
	}
	
	/**
	 * increments the armies which armies by i
	 * @param i : increment of the armies
	 */
	public void incrementArmies(int i){
		this.armies+=i;
	}
	
	/**
	 * reduces the armies by n
	 * @param n : decrement of armies
	 */
	public void decrementArmies(int n){
		this.armies-=n;
	}
	
	/**
	 * sets the armies
	 * @param armies : new count of armies of this country 
	 */
	public void setArmies(int armies){
		this.armies=armies;
	}
	
	/**
	 * checks if given country is neighbour
	 * @param country : country tho check
	 * @return
	 */
	public boolean isNeighbourOf(Country country){
		return (this.neighbours.contains(country));
	}
	
	/**
	 * returns String object of this country
	 */
	public String toString(){
		return new String(this.name);
	}
	
	/**
	 * moves one army to destination country
	 * @param destination : country where the army will be sent
	 */
	public void moveArmies(Country destination){
		moveArmies(destination, 1);
	}
	
	/**
	 * moves a count of armies to the destination
	 * @param destination : country where the armies should be sent
	 * @param armies : count of armies the move
	 */
	public void moveArmies(Country destination, int armies){
		//check if count of armies to move is too big
		if(this.armies -this.reenforcements < armies || this.armies - armies <1 || this.isTakenOver){
			return;
		}
		
		// TODO: needs a redo, per round army only are allowed to move between 2 countries at all, except countries is newly takten over
		if(this.gotArmiesFrom.containsKey(destination)){
			if(armies > this.gotArmiesFrom.get(destination)){
				return;
			}
			
			this.reenforcements(destination, -armies);
		}
		
		this.armies-=armies;
		destination.reenforcements(this, armies);
		destination.armies+=armies;
	}
	
	//TODO: redo, look method abover
	public void reenforcements(Country source, int armies){
		if(this.gotArmiesFrom.containsKey(source.name)){
			int reenforcemt = this.gotArmiesFrom.get(source)+armies;
			if(reenforcemt <=0){
				this.gotArmiesFrom.remove(source);
			}else{
				this.gotArmiesFrom.put(source, reenforcemt);
			}
		}else{
			this.gotArmiesFrom.put(source, armies);
		}
		this.reenforcements+=armies;
	}
	
	/**
	 * sets country to new round values
	 */
	public void newRound(){
		this.reenforcements =0;
		this.gotArmiesFrom = new HashMap<>();
		this.isTakenOver = false;
	}
}
