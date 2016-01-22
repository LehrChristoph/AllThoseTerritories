package game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import engine.Map;
import engine.Country;

public class WorldGenerator {
	
	private Map worldMap = new Map();
	
	public Map generateMap(String mapFile){
		this.worldMap = new Map();
		readMapFile(mapFile);
		return this.worldMap;
	}
	
	private void readMapFile(String mapFilePath){
		BufferedReader br = null;
		
		Queue<String> territories = new LinkedList<>();
		Queue<String> capitals = new LinkedList<>();
		Queue<String> neighbours = new LinkedList<>();
		Queue<String> continents = new LinkedList<>();
		
		try {

			String currentLine;

			br = new BufferedReader(new FileReader(mapFilePath));

			while ((currentLine = br.readLine()) != null) {
				if(currentLine.startsWith("patch-of")){
					territories.offer(currentLine.substring("patch-of".length()+1));
				}else if(currentLine.startsWith("capital-of")){
					capitals.offer(currentLine.substring("capital-of".length()+1));
				}else if(currentLine.startsWith("neighbors-of")){
					neighbours.offer(currentLine.substring("neighbors-of".length()+1));
				}else if(currentLine.startsWith("continent")){
					continents.offer(currentLine.substring("continent".length()+1));
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		createTerritories(territories);
		setCapitals(capitals);
		setNeighbours(neighbours);
		setContinents(continents);
	}
	
	private void createTerritories(Queue<String> territories){
		for(String line : territories){
			Matcher matcher = Pattern.compile("\\d+").matcher(line);
			matcher.find();
			int startCoordinates = line.indexOf(matcher.group());
			
			Queue<Integer> xCoordinates = new LinkedList<>();
			Queue<Integer> yCoordinates = new LinkedList<>();
			
			String countryName = line.substring(0,startCoordinates-1).trim();
			
			String[] unformated = line.substring(countryName.length()+1).split(" ");
			
			for(int i=0; i< unformated.length; i++){
				if(i%2==0){
					xCoordinates.offer(Integer.valueOf(unformated[i]));
				}else{
					yCoordinates.offer(Integer.valueOf(unformated[i]));
				}
			}
			
			worldMap.addTerritory(xCoordinates, yCoordinates, countryName);
			
		}
		
	}
	
	private void setCapitals(Queue<String> capitals){
		for(String line : capitals){
			Matcher matcher = Pattern.compile("\\d+").matcher(line);
			matcher.find();
			int startCoordinates = line.indexOf(matcher.group());
			
			String countryName = line.substring(0,startCoordinates-1).trim();
			
			String[] unformated = line.substring(countryName.length()+1).split(" ");
			
			this.worldMap.setCapital(countryName, Integer.valueOf(unformated[0]), Integer.valueOf(unformated[1]));			
		}
	}
	
	private void setNeighbours(Queue<String> neighbours){
		for(String line : neighbours){
			int split = line.indexOf(":");
			String countryname = line.substring(0, split).trim();
			String[] neighbourlist =  line.substring(split+1).split("-");
			
			this.worldMap.setNeighboursOfCountry(countryname, neighbourlist);
		}
	}
	
	private void setContinents(Queue<String> continents){
		for(String line : continents){
			Matcher matcher = Pattern.compile("\\d+").matcher(line);
			matcher.find();
			int bonus = line.indexOf(matcher.group());
			
			String countryName = line.substring(0,bonus-1).trim();
			
			int startTerritories = line.indexOf(":");
			
			bonus = Integer.valueOf(line.substring(bonus, startTerritories).trim());
			
			String[] unformated = line.substring(startTerritories+1).split("-");
			
			this.worldMap.addContinent(countryName, bonus, unformated);			
		}
	}
}


