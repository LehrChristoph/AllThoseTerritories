package engine;

import java.awt.Color;
import java.util.ArrayList;

/**
 * 
 * @author Christoph
 *
 */
public class Player {
	//player data
	private Color playerColor;
	private String name = new String();
	private int armiesToPlace =0;
	
	//owner countries and continents
	private ArrayList<Country> ownedCountries = new ArrayList<>();
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
	
	public void addTerritory(Country territory){
		this.ownedCountries.add(territory);
	}
	
	public void removeTerritory(Country territory){
		for(Country owned : this.ownedCountries){
			if(owned.equals(territory)){
				this.ownedCountries.remove(owned);
				return;
			}
		}
	}
	
	public ArrayList<Country> getTerritories(){
		return this.ownedCountries;
	}
	
	public boolean equals(Player player){
		return (this.name.equalsIgnoreCase(player.name));
	}
	
	public void newRound(){
		int armies =0;
		
		for(Continent cont : this.ownedContinents){
			armies+=cont.getContinentBonus();
		}
		
		armies+=(this.ownedCountries.size()/3);
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