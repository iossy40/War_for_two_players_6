package war_for_two_players;

import java.awt.*;
import javax.swing.*;

public class Card {
		
	//logical data
	final private byte value;
		
	//visual data(card images)
	final private ImageIcon frontImage;  //hold the front image of the card  
	final private ImageIcon backImage; //hold the back image of the card
	private ImageIcon currentImage; //current image of the card(front or back)
			
	//constructor
	public Card(final ImageIcon frontImage,final ImageIcon backImage,final byte value) {
		this.frontImage=frontImage;
		this.backImage=backImage;
		this.currentImage=backImage;
		this.value=value;
	}
	
	//drawing image card
	public void draw(Graphics graphics,Component component,int x,int y) {
		currentImage.paintIcon(component,graphics,x,y);
	}
		
	//hide the card
	public void hide() {
		currentImage=backImage;
	}
	
	//compare two cards
	public int compareTo(Card card) {
		return this.value-card.value;
	}
	
	//reveal the card
	public void reveal() {
		currentImage=frontImage;
	}
	
	//return the value of the card
	public byte getValue() {
		return value;
	}
	
}
