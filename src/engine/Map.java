/**
 * 
 */
package engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.omg.CORBA.INTERNAL;

import game.AllThoseTerritories;

/**
 * @author christoph
 *
 */
public class Map extends JPanel implements MouseMotionListener, MouseListener, ActionListener{
	
	private HashMap<String, Country> countries = new HashMap<String, Country>();
	private HashMap<String, Country> countriesLeftToPick = new HashMap<String, Country>();
	private HashMap<String, Continent> continents = new HashMap<>();
	private Country clickedCountry;
	private Country coloredCountry;
	
	private AllThoseTerritories game;
	
	public static final Color activeTerritoryColor = Color.RED;
	public static final Color defaultColor = Color.lightGray;
	public static final Color enteredColor = Color.GRAY;
	
	private JButton nextRoundBtn = new JButton("next Round");
	/**
	 * Constructor of Maps Class
	 */
	public Map(){
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		
		this.nextRoundBtn.addActionListener(this);
		this.setLocation(1100, 550);
		this.add(this.nextRoundBtn);
	}
	
	/**
	 * Sets the interface between Map Object and the game object
	 * @param game
	 */
	public void setSuperVisor(AllThoseTerritories game){
		if(game != null){
			this.game = game;
		}
	}
	
	/**
	 * Creates a new Country or adds an additional territory to an existing Country
	 * @param x : x-Coordinates for the shape of the territory
	 * @param y : y-Coordinates for the shape of the territory
	 * @param countryName : the name of the new Country respectively an existing country
	 */
	public void addTerritory(Queue<Integer> x, Queue<Integer> y, String countryName){
		//check if the count of x and y coordinates are equal
		if(x.size() != y.size()){
			return;
		}else{		
			//check if the country already exists
			if(this.countries.containsKey(countryName)){
				this.countries.get(countryName).add(x,y);
			//else create a new one	
			}else{
				this.countries.put(countryName, new Country(x, y, countryName));
			}
		}
	}
	
	/**
	 * Sets the Capital of the country with the given name
	 * @param countryName : Name of the country to set the Capital
	 * @param x : x-coordinate of the countries capital
	 * @param y : y-coordinate of the countries capital
	 */
	public void setCapital(String countryName, int x, int y){
		if(this.countries.containsKey(countryName)){
			this.countries.get(countryName).setCapital(new Point(x, y));
		}
	}
	
	/**
	 * Sets the neighbours of a given country:
	 * @param countryName : the name of the country
	 * @param neighbours : list of countries which should be set as neighbour
	 */
	public void setNeighboursOfCountry(String countryName, String[] neighbours){
		//check if current country(countryName) exists
		if(this.countries.containsKey(countryName)){
			//iterate through all neighbours and check if they exist
			for(String neighbour : neighbours){
				neighbour = neighbour.trim();
				if(this.countries.containsKey(neighbour)){
					//add neighbour to current country (countryName) as neighbour
					this.countries.get(countryName).addNeighbour(this.countries.get(neighbour));
					//add current country (countryName) to the neighbour as neighbour
					this.countries.get(neighbour).addNeighbour(this.countries.get(countryName));
				}
			}
		}
	}
	
	/**
	 * Adds new Continent and set the containing countries
	 * @param continentName : The name of the new continent
	 * @param continentBonus : bonus of owning the whole continent
	 * @param containingCountries : list of countries which are part of the continent
	 */
	public void addContinent(String continentName, int continentBonus, String[] containingCountries){
		//check if continent already exists
		if(!this.continents.containsKey(continentName)){
			this.continents.put(continentName, new Continent(continentName, continentBonus));
		}
		
		//iterate through the list of given countries
		for(String country : containingCountries){
			country = country.trim();
			//check if country exists
			if(this.countries.containsKey(country)){
				this.continents.get(continentName).addCountry(this.countries.get(country));
			}
		}
	}
	/**
	 * returns the count of countries
	 * @return count of countries
	 */
	public int getSumOfTerritories(){
		return this.countries.size();
	}
	/**
	 * sets the map to starting conditions
	 */
	public void start(){
		this.countriesLeftToPick = (HashMap<String, Country>) this.countries.clone();
	}
	
	/**
	 * sets the map to new round conditions
	 */
	public void newRound(){
		for(Country territory : this.countries.values()){
			territory.newRound();
		}
	}
	
	/**
	 * is used for painting the containing countries at startup
	 */
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    //casts the Graphics object to a Graphics2D Object
	    Graphics2D g2d = (Graphics2D) g;
	    //sets the stroke of the lines and the color
	    g2d.setStroke(new BasicStroke(2));
	    g2d.setColor(Color.BLACK);
	    //iterates through all countries and draws lines between neighbours
	    for(Country territory : this.countries.values()){
	    	Point currentTerritory = territory.getCapital();
	    	
	    	g2d.drawString(String.valueOf(territory.getArmies()), (int)currentTerritory.getX(), (int)currentTerritory.getY());
	    	
	    	for(Point captial : territory.getNeighbourCaptials()){
	    		g2d.drawLine((int)currentTerritory.getX(), (int)currentTerritory.getY(), (int)captial.getX(), (int)captial.getY());
	    	}
	    		
	    }
	    //draw the single countries
	    for(Country territory : this.countries.values()){
	    	territory.paintComponent(g, defaultColor);
	    }
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}

	/**
	 * is used for mouse hover effects
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		Country enteredCountry = null;
		//check if mouse is in any country
		for(Country territory : this.countries.values()){
			for(Polygon p: territory.getTerritories()){
				if(p.contains(e.getPoint())){
					enteredCountry = territory;
					break;
				}
			}
		}
		
		//check if any country is active
		if(this.clickedCountry != null ){
			//check if the entered country equals the clicked country
			if(enteredCountry == this.clickedCountry){
				//return, no need to do something
				return;
			}
			//check if the entered country is neighbour of the clicked(active country)
			else if(enteredCountry !=null && enteredCountry.isNeighbourOf(this.clickedCountry)){
				//check if a country, except the clicked country, is different painted
				if(this.coloredCountry != null){
					//repaint this country
					this.coloredCountry.paintComponent(this.getGraphics());
				}
				//set the current country as colored country
				this.coloredCountry = enteredCountry;
				//repaint this country
				enteredCountry.paintComponent(this.getGraphics(), enteredColor);
				return;
			}
			else{
				//check if any country is colored and repaint it
				if(this.coloredCountry != null){
					this.coloredCountry.paintComponent(this.getGraphics());
					this.coloredCountry =null;
				}
				return;
			}
		}else{
			//check if entered country equals currently colored country
			if(enteredCountry == this.coloredCountry){
				//returns, no need to do something
				return;
			}
			//check if any country was entered and if it is already painted
			if(enteredCountry != null && enteredCountry != this.coloredCountry){
				//repaint colored country
				if(this.coloredCountry != null){
					this.coloredCountry.paintComponent(this.getGraphics());
				}
				//set current country as colored country
				this.coloredCountry = enteredCountry;
				enteredCountry.paintComponent(this.getGraphics(), enteredColor);
			//check if no country was entered and a country is colored
			}else if(this.coloredCountry !=null && enteredCountry == null){
				//repaint an set colored country to null
				this.coloredCountry.paintComponent(this.getGraphics());
				this.coloredCountry = null;
			}
		}
	}
	
	/**
	 * used for mouse clicked effects
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		Country enteredCountry = null;
		//check if any county was clicked
		for(Country territory : this.countries.values()){
			for(Polygon p: territory.getTerritories()){
				if(p.contains(e.getPoint())){
					enteredCountry = territory;
					break;
				}
			}
		}
		//check if any county was clicked
		if(enteredCountry != null){
			//if game phase is pick -> execute pick country
			if(this.game.getPhase().equalsIgnoreCase(AllThoseTerritories.PHASE_PICK)){
				pickTerritory(enteredCountry);
			//check if country belongs to current player and if game phase is place -> execute place armies,
			}else if(enteredCountry.getOwner().equals(this.game.getCurrentPlayer()) && this.game.getPhase().equalsIgnoreCase(AllThoseTerritories.PHASE_PLACE)){
				placeArmies(enteredCountry);
			//if phase equals attack_move
			}else if(this.game.getPhase().equalsIgnoreCase(AllThoseTerritories.PHASE_ATTACK_MOVE)){
				//if no country was clicked, set clicked country
				if(this.clickedCountry != null && this.clickedCountry.equals(enteredCountry)){
					this.clickedCountry.setActive(false);
					this.clickedCountry.paintComponent(this.getGraphics());
					this.clickedCountry = null;
				}
				//if entered country equals clicked country deselect clicked country
				else if(enteredCountry.getOwner().equals(this.game.getCurrentPlayer()) && this.clickedCountry == null){
					this.clickedCountry = enteredCountry;
					this.clickedCountry.setActive(true);
					this.clickedCountry.paintComponent(this.getGraphics());
				}
				//check if click was rightclick and country belongs to current player and is neighbour of clicked country -> execute move armies
				else if(SwingUtilities.isRightMouseButton(e) && enteredCountry.getOwner().equals(this.game.getCurrentPlayer()) && this.clickedCountry != null && this.clickedCountry.isNeighbourOf(enteredCountry) && enteredCountry.getOwner().equals(this.game.getCurrentPlayer())){
					moveArmies(enteredCountry);
				}
				//check if click is left click and country does not belong to current player and is neighbour of clicked country -> execute attack
				else if(SwingUtilities.isLeftMouseButton(e) && !enteredCountry.getOwner().equals(this.game.getCurrentPlayer()) && this.clickedCountry != null && this.clickedCountry.isNeighbourOf(enteredCountry)){
					attack(enteredCountry);
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
	/**
	 * Adds the picked country to the current player
	 * @param pickedCountry : country which will be added to current player
	 */
	private void pickTerritory(Country pickedCountry){
		//check if country is left to pick
		if(!this.countriesLeftToPick.containsKey(pickedCountry.getName())){
			return;
		}
		
		//get current player
		Player currentPlayer = this.game.getCurrentPlayer();
		//set ownership
		pickedCountry.setOwner(currentPlayer);
		//set armies to 1
		pickedCountry.setArmies(1);
		//repaint the picked country
		pickedCountry.paintComponent(this.getGraphics());
		//add picked country to current player
		currentPlayer.addTerritory(pickedCountry);
		//remove the picked territories from list of pickable countries
		this.countriesLeftToPick.remove(pickedCountry.getName());
		
		System.out.println(this.game.getCurrentPlayer() + " picked " + pickedCountry);
		//change player
		this.game.nextPlayer();
		//check if any country is left to pick
		if(this.countriesLeftToPick.size() <=0){
			this.game.nextPhase();
		}
	}
	
	/**
	 * Places one army in the picked country
	 * @param pickedCountry : country in which an army should be placed
	 */
	private void placeArmies(Country pickedCountry){
		//get current player
		Player currentPlayer = this.game.getCurrentPlayer();
		//check if player has armies left to place
		if(currentPlayer.armiesLeftToPlace()){
			//increment armies by 1
			pickedCountry.incrementArmies();
			//decrement armies of player
			if(! currentPlayer.armyPlaced()){
				//start next phase if no army is left to place
				this.game.nextPhase();
			}
			//repaint country
			pickedCountry.paintComponent(this.getGraphics());
			System.out.println("Player " + currentPlayer + " has " + currentPlayer.getArmiesLeftToPlace() + " reenforcements left to place");
		}
	}
	
	/**
	 * moves one army from the active country to picked country
	 * @param pickedCountry : country in which an army should be moved
	 */
	private void moveArmies(Country pickedCountry){
		//ToDO: need to change, look at doc
		this.clickedCountry.moveArmies(pickedCountry);
		this.clickedCountry.paintComponent(this.getGraphics());
		pickedCountry.paintComponent(this.getGraphics());
	}
	
	/**
	 * attacks the picked country from the clicked country
	 * @param pickedCountry : country which should be attacked
	 */
	private void attack(Country pickedCountry){
		//get the armies which are able to attack, max 3
		int invadingkArmies = this.clickedCountry.getArmies();
		if(invadingkArmies >=4){
			invadingkArmies =3;
		}else if (invadingkArmies > 1){
			invadingkArmies-=1;
		}else{
			return;
		}
		
		//get armies to defen, max 2
		int defendArmies = pickedCountry.getArmies();
		if(defendArmies >2){
			defendArmies =2;
		}
		
		System.out.println("Player " + this.game.getCurrentPlayer() + " attacks " + pickedCountry + "("+ defendArmies +" defenders)" + " from " + this.clickedCountry + "(" + invadingkArmies + " invaders)");
		
		ArrayList<Integer> invaderDices = new ArrayList<>();
		ArrayList<Integer> defendDices = new ArrayList<>();
		
		//"throw the dices" invader
		for(int i=0; i < invadingkArmies; i++){
			invaderDices.add(1+(int)Math.floor(6*Math.random()));
		}
		//sort dices and reverse it
		Collections.sort(invaderDices);
		Collections.reverse(invaderDices);

		//"throw the dices" defender
		for(int i=0; i < defendArmies; i++){
			defendDices.add(1+(int)Math.floor(6*Math.random()));
		}
		//sort dices and reverse it
		Collections.sort(defendDices);
		Collections.reverse(defendDices);
		
		//check how often the defender wins
		int defenderWins =0;
		if(invaderDices.get(0)<=defendDices.get(0)){
			defenderWins++;
		}
		if(invaderDices.size()>1 && defendDices.size()>1 && invaderDices.get(1)<=defendDices.get(1)){
			defenderWins++;
		}
		
		System.out.println("Invader: " + invaderDices + " Defender: " + defendDices);
		
		//check if the defender lost this country
		if(defenderWins ==0){
			System.out.println("Invader wins");
			pickedCountry.setOwner(this.game.getCurrentPlayer(), true);
			pickedCountry.setArmies(invadingkArmies);
			this.clickedCountry.setArmies(this.clickedCountry.getArmies() - invadingkArmies);
		}else{
			System.out.println("Defender wins");
			this.clickedCountry.decrementArmies(defenderWins);
			pickedCountry.decrementArmies(defendArmies-defenderWins);
		}
		
		//repaint involved countries
		this.clickedCountry.paintComponent(this.getGraphics());
		pickedCountry.paintComponent(this.getGraphics());
	}

	/**
	 * handles the end of the attack and move phase
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//check current phase
		if(this.game.getPhase().equalsIgnoreCase(AllThoseTerritories.PHASE_ATTACK_MOVE)){
			//check if a country is active (clicked) and repaint it
			if(this.clickedCountry != null){
				this.clickedCountry.setActive(false);
				this.clickedCountry.paintComponent(this.getGraphics());
				this.clickedCountry=null;
			}
			//end this phase
			this.game.nextPhase();
		}
	}
	/**
	 * Checks if the given list of countries contains whole continents
	 * @param countries : a list of countries
	 * @return
	 */
	public ArrayList<Continent> checkContinents(ArrayList<Country> countries){
		ArrayList<Continent> userOwnedContinents = new ArrayList<>();
		//iterate over the continent and check the containing countries
		for(Continent continent : this.continents.values()){
			if(continent.checkCountries(countries)){
				userOwnedContinents.add(continent);
			}
		}
		//return a list the continents 
		return userOwnedContinents;
	}

	public void kipick() {
		if (countriesLeftToPick.size()>0) {
			Random rn = new Random();
			String[] leftToPick = countriesLeftToPick.keySet().toArray(new String[countriesLeftToPick.size()]);
			pickTerritory(countriesLeftToPick.get(leftToPick[rn.nextInt(leftToPick.length)]));
		}
	}

	public void kiplace(Player player){
		if (player.toString().equals("Fetti Fett Fett")) {
			Random rn = new Random();
			while (player.getArmiesLeftToPlace() > 0) {
				placeArmies(player.getTerritories().get(rn.nextInt(player.getTerritories().size())));
				player.armyPlaced();
			}
		}
	}



}

/**
 * Continent Class: holds a list of countries which are part of the continent, the continent bonus and the name
 * @author Christoph
 * 
 */
class Continent{
	
	private int continentBonus =0;
	private ArrayList<Country> territories = new ArrayList<>();
	private String name = new String();
	
	/**
	 * Creates an empty continent object
	 * @param name : name of thei continent
	 * @param continentBonus : bonus of owning this continent
	 */
	public Continent(String name, int continentBonus){
		this.continentBonus = continentBonus;
		this.name = name;

	}
	
	/**
	 * Creates a new continent with the given list of countries
	 * @param name : name of the continent
	 * @param continentBonus : bonus of owning this continent
	 * @param countries : list of countries, which are part of the continent
	 */
	public Continent(String name, int continentBonus, ArrayList<Country> countries){
		this.continentBonus = continentBonus;
		this.name = name;
		this.territories = countries;
	}
	
	/**
	 * Compares a given list of countries with the containing countries
	 * @param countriesToCheck : list of countries to check if part of the continent
	 * @return 
	 */
	public boolean checkCountries(ArrayList<Country> countriesToCheck){
		//check if continent contains countries
		if(this.territories.size()==0){
			return false;
		}
		
		int count =0;
		//iterate over containing countries and check if the list contains them
		for(Country territory : countriesToCheck){
			if(this.territories.contains(territory)){
				count++;
			}
		}
		
		//check if all countries occur in the list
		if(count == this.territories.size()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * appends a country to the continent
	 * @param country : to add to the continent
	 */
	public void addCountry(Country country){
		this.territories.add(country);
	}
	
	/**
	 * returns the list of containing countries
	 * @return
	 */
	public ArrayList<Country> getCountries(){
		return this.territories;
	}
	
	/**
	 * returns the continentbonus
	 * @return
	 */
	public int getContinentBonus(){
		return this.continentBonus;
	}
	
	/**
	 * returns the name of the continent
	 * @return
	 */
	public String getName(){
		return this.name;
	}
}
