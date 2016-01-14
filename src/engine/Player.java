package engine;

import java.awt.Color;
import java.util.ArrayList;

public class Player {
	
	private Color playerColor;
	private String name = new String();
	private int armiesToPlace =0;
	
	private ArrayList<Territory> ownedTerritories = new ArrayList<>();
	private ArrayList<Continent> ownedContinents = new ArrayList<>();
	
	public Player(String name){
		this.name =name;
	}
	
	public Player(String name, Color playerColor){
		this.name = name;
		this.playerColor = playerColor;
	}
	
	public void setPlayerColor(Color playerColor){
		this.playerColor = playerColor;
	}
	
	public Color getPlayerColor(){
		return this.playerColor;
	}
	
	public void addTerritory(Territory territory){
		this.ownedTerritories.add(territory);
	}
	
	public void removeTerritory(Territory territory){
		for(Territory owned : this.ownedTerritories){
			if(owned.equals(territory)){
				this.ownedTerritories.remove(owned);
				return;
			}
		}
	}
	
	public ArrayList<Territory> getTerritories(){
		return this.ownedTerritories;
	}
	
	public boolean equals(Player player){
		return (this.name.equalsIgnoreCase(player.name));
	}
	
	public void newRound(){
		int armies =0;
		
		for(Continent cont : this.ownedContinents){
			armies+=cont.getContinentBonus();
		}
		
		armies+=(this.ownedTerritories.size()/3);
		System.out.println("Player " + this.name +" has " +armies + " reenforcements");
		this.armiesToPlace = armies;
	}
	
	public boolean armyPlaced(){
		if(this.armiesToPlace <=0){
			return false;
		}
		
		this.armiesToPlace--;
		
		if(this.armiesToPlace <=0){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean armiesLeftToPlace(){
		return (this.armiesToPlace>0);
	}
	
	public int getArmiesLeftToPlace(){
		return this.armiesToPlace;
	}
	
	public String toString(){
		return new String(this.name);
	}
	
	public void setContinents(ArrayList<Continent> continents){
		this.ownedContinents = continents;
	}
}