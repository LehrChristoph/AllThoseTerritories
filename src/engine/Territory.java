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

public class Territory extends JPanel{

	private ArrayList<Polygon> landslides = new ArrayList<>();
	private Point capital;
	private String name;
	private boolean isActive = false;
	private ArrayList<Territory> neighbours = new ArrayList<Territory>();
	
	private int reenforcements =0;
	private HashMap<Territory, Integer> gotArmiesFrom = new HashMap<>();
	
	private Player owner = null;
	private boolean isTakenOver = false;
	
	private int armies =0;
	
	public Territory(Queue<Integer> x, Queue<Integer> y, String name){        
        Polygon p = new Polygon();
		do{
			p.addPoint(x.poll(), y.poll());
		}while (x.size() >0);
		this.landslides.add(p);
        this.name = name;
	}
	
	public void add(Queue<Integer> x, Queue<Integer> y){
		if(x.size() != y.size()){
			return;
		}else{
			Polygon p = new Polygon();
			do{
				p.addPoint(x.poll(), y.poll());
			}while (x.size() >0);
			this.landslides.add(p);
			this.setSize(this.getPreferredSize());
		}
	}
	
	public void setOwner(Player owner){
		if(this.owner != null){
			this.owner.removeTerritory(this);
		}
		this.owner = owner;
		this.owner.addTerritory(this);
		this.isTakenOver = true;
	}
	
	public Player getOwner(){
		return this.owner;
	}
	
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
	    Graphics2D g2d = (Graphics2D) g;
	    
	    Color territoryColor;
	    
	    if(this.isActive){
	    	territoryColor = Map.activeTerritoryColor;
	    }else{
	    	if(this.owner != null && this.owner.getPlayerColor()!=null){
	    		territoryColor = this.owner.getPlayerColor();
	    	}else{
	    		territoryColor = Map.defaultColor;
	    	}
	    }
	    
	    for(Polygon p : this.landslides){
	    	g2d.setColor(Color.BLACK);
	    	g2d.setStroke(new BasicStroke(5));
			g2d.drawPolygon(p);
			g2d.setColor(territoryColor);
			g2d.fillPolygon(p);
	    }
	    
	    g2d.setColor(Color.black);
	    g2d.drawString(String.valueOf(this.armies), (int)this.capital.getX(), (int) this.capital.getY());
	}
	
	public void paintComponent(Graphics g, Color territoryColor) {
	    super.paintComponent(g);
	    
	    Graphics2D g2d = (Graphics2D) g;
	    
	    if(this.isActive){
	    	return;
	    }

	    for(Polygon p : this.landslides){
	    	g2d.setColor(Color.BLACK);
	    	g2d.setStroke(new BasicStroke(5));
			g2d.drawPolygon(p);
			g2d.setColor(territoryColor);
			g2d.fillPolygon(p);
	    }
	    
	    g2d.setColor(Color.black);
	    g2d.drawString(String.valueOf(this.armies), (int)this.capital.getX(), (int) this.capital.getY());

	}
	
	public void setCapital(Point pos){
		this.capital = pos;
	}
	
	public Point getCapital(){
		return this.capital;
	}
	
	public String getName(){
		return this.name;
	}
	
	public ArrayList<Polygon> getLandslides(){
		return this.landslides;
	}
	
	public void setActive(boolean isActive){
		this.isActive = isActive;
	}
	
	public void addNeighbour(Territory neighbour){
		this.neighbours.add(neighbour);
	}
	
	public ArrayList<Territory> getNeighbours(){
		return this.neighbours;
	}
	
	public ArrayList<Point> getNeighbourCaptials(){
		ArrayList<Point> captitals = new ArrayList<>();
		
		for(Territory neighbour : this.neighbours){
			captitals.add(neighbour.getCapital());
		}
		
		return captitals;
	}
	
	public int getArmies(){
		return this.armies;
	}
	
	public void incrementArmies(){
		this.armies++;
	}
	
	public void incrementArmies(int i){
		this.armies+=i;
	}
	
	public void decrementArmies(int n){
		this.armies-=n;
	}
	
	public void setArmies(int armies){
		this.armies=armies;
	}
	
	public boolean isNeighbourOf(Territory territory){
		return (this.neighbours.contains(territory));
	}
	
	public String toString(){
		return new String(this.name);
	}
	
	public void moveArmies(Territory destination){
		moveArmies(destination, 1);
	}
	
	public void moveArmies(Territory destination, int armies){
		if(this.armies -this.reenforcements < armies || this.armies - armies <1 || this.isTakenOver){
			return;
		}
		
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
	
	public void reenforcements(Territory source, int armies){
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
	
	public void newRound(){
		this.reenforcements =0;
		this.gotArmiesFrom = new HashMap<>();
		this.isTakenOver = false;
	}
}
