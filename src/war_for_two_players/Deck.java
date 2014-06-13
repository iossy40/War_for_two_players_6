package war_for_two_players;

import java.net.URL;
import java.util.*;
import javax.swing.ImageIcon;

public class Deck {
	
	//constant members
	private static final String SUITS="cdhs";
	private static final String RANKS="23456789tjqka";
	private static final byte DECK_SIZE=(byte)(SUITS.length()*RANKS.length());
		
	//private members
	private ArrayList<Card> cards=new ArrayList<Card>();
	//we cut the deck to sub decks (every player gets one sub deck)
	private int numOfSubDecks;
	private byte subDecksCounter; 
			
	//constructor for the deck we worked with
	public Deck(int numOfSubDecks) {
		this.numOfSubDecks=numOfSubDecks;
		//setting the image of the back card side
		ClassLoader cldrForCardsBackImage=this.getClass().getClassLoader();
		String backImagePath="war_for_two_players/images/b.gif";
		URL backImageURL = cldrForCardsBackImage.getResource(backImagePath);
		ImageIcon backImg = new ImageIcon(backImageURL);
		//class loader for the front images
		ClassLoader cldrForCardsFrontImages=this.getClass().getClassLoader();
		//creating cards and adding them to the deck
		for(byte i=0;i<=SUITS.length()-1;i++) {
			for (byte j=0;j<=RANKS.length()-1;j++) {
				char suit=SUITS.charAt(i);
				char rank=RANKS.charAt(j);
			    String frontImagePath="war_for_two_players/images/"+rank+suit+".gif";
	            URL frontImageURL = cldrForCardsFrontImages.getResource(frontImagePath);
	            ImageIcon frontImg = new ImageIcon(frontImageURL);
				cards.add(new Card(frontImg,backImg,j));
			}
		}	
	}
	
	//initialize the deck 
	public void init(int numOfSubDecks) {
		this.numOfSubDecks=numOfSubDecks;
		this.subDecksCounter=0;
	}
		
	//shuffling the deck
	public void shuffle() {
		Collections.shuffle(cards);
	}
		
	//creating sub deck for a player
	public void createSubDeck(Player p) {
		final byte SUB_DECK_SIZE=(byte)(DECK_SIZE/numOfSubDecks);
		final byte startLocation=(byte)(subDecksCounter*SUB_DECK_SIZE);
		List<Card> subDeck=cards.subList(startLocation, startLocation+SUB_DECK_SIZE);
		Collections.reverse(subDeck);
		p.setSubDeck(subDeck);
		subDecksCounter=(byte)(subDecksCounter==numOfSubDecks-1 ? 0 : subDecksCounter+1);
	}
				
}
