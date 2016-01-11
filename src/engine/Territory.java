package engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Queue;

import javax.swing.JPanel;

public class Territory extends JPanel{

	private ArrayList<Polygon> landslides = new ArrayList<>();
	private Point capital;
	private String name;
	private boolean isActive = false;
	private ArrayList<Territory> neighbours = new ArrayList<Territory>();
	
	private Player owner = null;
	
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
		this.owner = owner;
	}
	
	public Player getOwner(){
		return this.owner;
	}
	
	public void paintComponent(){
		this.paintComponent(this.getGraphics());
	}
	
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
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
			g.drawPolygon(p);
			g.setColor(territoryColor);
			g.fillPolygon(p);
	    }
	    
	    g.setColor(Color.black);
	    g.drawString(String.valueOf(this.armies), (int)this.capital.getX(), (int) this.capital.getY());
	}
	
	public void paintComponent(Graphics g, Color territoryColor) {
	    super.paintComponent(g);
	    
	    if(this.isActive){
	    	return;
	    }
	    
	    for(Polygon p : this.landslides){
			g.drawPolygon(p);
			g.setColor(territoryColor);
			g.fillPolygon(p);
	    }
	    
	    g.setColor(Color.black);
	    g.drawString(String.valueOf(this.armies), (int)this.capital.getX(), (int) this.capital.getY());

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
}
