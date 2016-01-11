package game;

import java.awt.EventQueue;

public class GameStarter {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            public void run() {
            	
            	AllThoseTerritories window;
            	
            	if(args.length ==0){
            		window = new AllThoseTerritories(new String());
            	}else{
            		window = new AllThoseTerritories(args[0]);
            	}
                // Set window visible
                window.setVisible(true);
            }
        });
	}
}
