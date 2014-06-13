package war_for_two_players;

import java.util.List;
import java.util.*;
import java.awt.*;
import javax.swing.JCheckBox;

public class Player {
	
	//private members
	private Queue<Card> subDeck=new LinkedList<Card>();
	private ArrayList<Card> declaration=new ArrayList<Card>();
	private boolean lastHiddenCardWasTurnedUp;
		
	//set player subDeck
	public void setSubDeck(final List<Card> subDeck) {
		this.subDeck.clear();
		this.subDeck.addAll(subDeck);
	}
	
	//drawing sub deck
	public void drawSubDeck(Graphics graphics,Component component,int x,int y) {
		ArrayList<Card> listCards=new ArrayList<Card>(subDeck);
		for (int i=listCards.size()-1;i>=0;i--) {
			listCards.get(i).draw(graphics,component,x++,y++);
		}
	}
	
	//drawing declaration
	public void drawDeclaration(Graphics graphics,Component component,int x,int y) {
		for (int i=0;i<=declaration.size()-1;i++) {
			declaration.get(i).draw(graphics,component,x,y);
			x+=83;
		}
	}
		
	//check whether the player can win the game
	public boolean canWin() {
		return (holdAcard() || lastHiddenCardWasTurnedUp);
	}
	
	//check whether there is at list one card in the sub deck
	public boolean holdAcard() {
		return !subDeck.isEmpty(); 
	}
	
	//update the war declaration of the player
	public void shot(final JCheckBox missingCardLose) {
		if (holdAcard()) {
			Card card; 
			//the player has a card to shot
			if (declaration.size()==0) {
				//new fight (one reveals card)
				card=subDeck.remove();
				card.reveal();
				declaration.add(card);
			} else {
				//continues fight (the first card is hidden)
				card=subDeck.remove();
				declaration.add(card);
				if (holdAcard()) {
					//continues fight (the second card reveals)
					card=subDeck.remove();
					card.reveal();
					declaration.add(card);
				} else if (!missingCardLose.isSelected()) {
					//continues fight but there is no more card for the second revealed car.
					//in this case the first hidden card play the role of the second one
					card.reveal();
					lastHiddenCardWasTurnedUp=true;
					missingCardLose.setEnabled(false);
				}
			} //end of continues fight
		} //end of the player has a card to shot
	}
		
	//return the winner player or null in case of draw 
	public static Player findWinner(Player[] players) {
		ArrayList<Player> longestDeclarationPlayers=findLongestDeclarationPlayers(players);
		Player theWinner=null;
		//clear winner (all players lost all there cards except the winner)
		if (longestDeclarationPlayers.size()==1 && !caseOfLastHiddenCardWasTurnedUp(players)) {
			theWinner=longestDeclarationPlayers.get(0);
		} else if (longestDeclarationPlayers.size()>1 || caseOfLastHiddenCardWasTurnedUp(players)) {
			//the more powerful last card is the winner in this fight
			theWinner=findPlayerWithWinnerDeclaration(players);
		} 
		return theWinner;
	}
	
	//return the players with the longest declaration (can have more then one player)
	private static ArrayList<Player> findLongestDeclarationPlayers(Player[] players) {
		ArrayList<Player> playersWithTheLongestDeclaration=new ArrayList<Player>();
		//find longest declaration player
		Player playerOfLongestDeclaration=null;
		byte sizeOfLongestDeclaration=0;
		for (byte i=0;i<=players.length-1;i++) {
			if (players[i].declaration.size()>sizeOfLongestDeclaration) {
				playerOfLongestDeclaration=players[i];
				sizeOfLongestDeclaration=(byte)players[i].declaration.size();
			}
		}
		//add the player that holds the longest declaration
		playersWithTheLongestDeclaration.add(playerOfLongestDeclaration);
		//add the other players holding the same size except the original player
		for (byte i=0;i<=players.length-1;i++) {
			if (players[i].declaration.size()==sizeOfLongestDeclaration && players[i]!=playerOfLongestDeclaration) {
				playersWithTheLongestDeclaration.add(players[i]);
			}
		}
		return playersWithTheLongestDeclaration;
	}
	
	//check whether there is a case of last hidden card that was turned up
	private static boolean caseOfLastHiddenCardWasTurnedUp(Player[] players) {
		boolean caseWasFound=false;
		for (byte i=0;i<=players.length-1 && !caseWasFound;i++) {
			caseWasFound=(players[i].lastHiddenCardWasTurnedUp);
		}
		return caseWasFound;
	}
	
	//find player with a winner declaration (comparing last card in all declarations)
	private static Player findPlayerWithWinnerDeclaration(Player[] players) {
		Player bestPlayer=null;
		byte highestCardValue=0;
		//scanning the players
		for (byte i=0;i<=players.length-1;i++) {
			byte lastCardIndexInDeclaration=(byte)(players[i].declaration.size()-1);
			if (lastCardIndexInDeclaration>=0) {
				//the player is still active (didn't lost all his cards)
				Card lastCardInDeclaration=players[i].declaration.get(lastCardIndexInDeclaration);
				if (lastCardInDeclaration.getValue()>highestCardValue) {
					//new best player declaration
					bestPlayer=players[i];
					highestCardValue=lastCardInDeclaration.getValue();
				} else if (lastCardInDeclaration.getValue()==highestCardValue) {
					//more then one best declaration leads to draw
					bestPlayer=null;
				}
			} //end of active player 
		} //end of scanning the players
		return bestPlayer;
	}
			
	//the losers declaration card/s are taken from them and united with the declaration card/s of the winner
	public void takeDeclarationCardsFromLosers(Player[] players) {
		for (byte i=0;i<=players.length-1;i++) {
			if (this!=players[i]) {
				this.declaration.addAll(players[i].declaration);
				players[i].declaration.clear();
				players[i].lastHiddenCardWasTurnedUp=false;
			}
		}
	}
	
	//hide all the declaration cards
	public void hideDeclarationCards() {
		for (byte i=0;i<=declaration.size()-1;i++)
			declaration.get(i).hide();
	}
	
	//shuffle the declaration cards
	public void shuffleDeclarationCards() {
		Collections.shuffle(declaration);
	}
	
	//the declaration cards are taken to the bottom of the sub deck
	public void moveDeclarationCardsToSubDeck() {
		subDeck.addAll(declaration);
		declaration.clear();
		lastHiddenCardWasTurnedUp=false;
	}
					
}

