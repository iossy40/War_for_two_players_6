package war_for_two_players;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

class Main extends JFrame {
		
	private static final long serialVersionUID = 1L;
	
	//steps type
	private enum StepStatus {FIGHT,COMPARE};
	
	//the inner surface of the game
	private MainPanel mainPanel;
			
	public static void main(String[] args) {
		   Main mainWindow=new Main();   
		   mainWindow.go();
	}
	
	//game initialization
	private void go() {
		//creating game surface
		mainPanel=new MainPanel();
		//setting the main window
		this.setTitle("war game for two players");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(900,700);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
		//connecting the inner surface to the main window
		this.setContentPane(mainPanel);
		//setting the default button in the panel
		mainPanel.setDefaultButton();
	}
	
	//inner class for the game surface
	private class MainPanel extends JPanel implements ActionListener {
		
		private static final long serialVersionUID = 1L;
		
		//game objects
		private Deck deck;
		private Player[] players;
				
		//game status
		private StepStatus nextStepStatus;
		
		//buttons
		private JButton newGameButton;
		private JButton nextStepButton;
		private JCheckBox missingCardLose;
		private JRadioButton twoPlayersButton;
		private JRadioButton threePlayersButton;
		private JRadioButton fourPlayersButton;
		private ButtonGroup numOfPlayersGroup;
		
		//finals
		final short TOP_OFFSET_FOR_BUTTONS=630;
		
		MainPanel() {
			//build gui objects
			buildGuiObjects();
			//we take number of players from the radio button
			int numOfPlayers=Integer.parseInt(numOfPlayersGroup.getSelection().getActionCommand());
			//deck creation
			deck=new Deck(numOfPlayers);
			deck.shuffle();
			//players creation
			createPlayers(numOfPlayers);
			//every player get half deck to play with (by default we get two players)
			createSubDecks(numOfPlayers);
			//initialization of game status
			nextStepStatus=StepStatus.FIGHT;
		}
		
		//creating players
		private void createPlayers(int numOfPlayers) {
			players=new Player[numOfPlayers];
			for (int i=0;i<=numOfPlayers-1;i++) {
				players[i]=new Player();
			}
		}
		
		//creating sub decks for the players
		private void createSubDecks(int numOfPlayers) {
			for (int i=0;i<=numOfPlayers-1;i++) {
				deck.createSubDeck(players[i]);
			}
		}
		
		//build gui objects
		private void buildGuiObjects() {
			//the gui elements are located by precise locations
			this.setLayout(null);
			//buttons creation
			newGameButton=new JButton("new game");
			newGameButton.addActionListener(this);
			this.add(newGameButton);
			newGameButton.setBounds(10,TOP_OFFSET_FOR_BUTTONS,100,30);
			nextStepButton=new JButton("next step");
			nextStepButton.addActionListener(this);
			this.add(nextStepButton);
			nextStepButton.setBounds(120,TOP_OFFSET_FOR_BUTTONS,100,30);
			missingCardLose=new JCheckBox("missing card lose");
			missingCardLose.setSelected(true);
			this.add(missingCardLose);
			missingCardLose.setBounds(230,TOP_OFFSET_FOR_BUTTONS,130,30);
			//label creation for number of players
			JLabel numOfPlayersLbl=new JLabel("number of players:");
			this.add(numOfPlayersLbl);
			numOfPlayersLbl.setBounds(370,TOP_OFFSET_FOR_BUTTONS,110,30);
		    //creating radio button group for number of players
			numberOfPlayersGuiCreation();
		}
		
		//creating radio button group for number of players
		private void numberOfPlayersGuiCreation() {
			twoPlayersButton = new JRadioButton("2",true);
			twoPlayersButton.setActionCommand("2");
			twoPlayersButton.setBounds(490,TOP_OFFSET_FOR_BUTTONS,30,30);
			threePlayersButton = new JRadioButton("3");
			threePlayersButton.setActionCommand("3");
			threePlayersButton.setBounds(520,TOP_OFFSET_FOR_BUTTONS,30,30);
			fourPlayersButton = new JRadioButton("4");
			fourPlayersButton.setActionCommand("4");
			fourPlayersButton.setBounds(550,TOP_OFFSET_FOR_BUTTONS,30,30);
			numOfPlayersGroup=new ButtonGroup();
			numOfPlayersGroup.add(twoPlayersButton);
			numOfPlayersGroup.add(threePlayersButton);
			numOfPlayersGroup.add(fourPlayersButton);
			this.add(twoPlayersButton);
			this.add(threePlayersButton);
			this.add(fourPlayersButton);
		}
		
		//"enter" key will perform the next step button 
		private void setDefaultButton() {
			this.getRootPane().setDefaultButton(nextStepButton);
		}
		
		//drawing the cards
		public void paintComponent(Graphics graphics) {
			//erasing cards
			graphics.setColor(Color.GREEN);
			graphics.fillRect(0, 0, 900, 670);
			//drawing cards
			int y=10;
			for (int i=0;i<=players.length-1;i++) {
				players[i].drawSubDeck(graphics,this,10,y);
				players[i].drawDeclaration(graphics,this,144,y);
				y+=158;
			}
		}
		
		//button was pressed
		public void actionPerformed(ActionEvent evt) {
			//next step button was pressed
			if (evt.getSource()==nextStepButton) {
				//declarations step
				if (nextStepStatus==StepStatus.FIGHT) {
					makeFight();
				} else {
					//checking the result of the fight
					Player theWinner=Player.findWinner(players);
					//there is a winner (current fight come to end)
					if (theWinner!=null) {
						applyResult(theWinner);
					} 
					//the game has not been finished yet
					if (atleast2ActivePlayers()) {
						nextStepStatus=StepStatus.FIGHT;
					} else {
						//game is over
						nextStepButton.setEnabled(false);
					}
					if (theWinner!=null) {
						//show the result
						this.repaint();
					} else {
						//continue current fight (update current declarations)
						nextStepButton.doClick();
					}
				} // close of compare step
			} else {
				//the new game button was pressed
				applyNewGame();
			}
		}
		
		//updating current declarations or open new ones
		private void makeFight() {
			for (int i=0;i<=players.length-1;i++) {
				players[i].shot(missingCardLose);
			}
			nextStepStatus=StepStatus.COMPARE;
			this.repaint();
		}
		
		//if there is a winner we apply the result
		private void applyResult(Player theWinner) {
			//we move all the declaration cards to the winner bottom sub deck 
			theWinner.takeDeclarationCardsFromLosers(players);	
			theWinner.hideDeclarationCards();
			theWinner.shuffleDeclarationCards();
			theWinner.moveDeclarationCardsToSubDeck();
			missingCardLose.setEnabled(true);
		}
		
		//check if the game has been finished
		private  boolean atleast2ActivePlayers() {
			byte numOfActivePlayers=0;
			for (int i=0;i<=players.length-1 && numOfActivePlayers<2;i++) {
				numOfActivePlayers=(players[i].canWin() ? (byte)(numOfActivePlayers+1):numOfActivePlayers);
			}
			return (numOfActivePlayers==2);
		}
		
		//new game button was pressed
		private void applyNewGame() {
			//in a new game all cards are turned down
			for (int i=0;i<=players.length-1;i++) {
				players[i].hideDeclarationCards();
			}
			//we take number of players from the radio button
			int numOfPlayers=Integer.parseInt(numOfPlayersGroup.getSelection().getActionCommand());
			//deck initialization
			deck.init(numOfPlayers);
			deck.shuffle();
			//players creation
			createPlayers(numOfPlayers);
			//every player get sub deck
			createSubDecks(numOfPlayers);
			nextStepButton.setEnabled(true);
			nextStepStatus=StepStatus.FIGHT;
			this.repaint();
		}
		
	} //end of internal class (game surface)
		
} 
