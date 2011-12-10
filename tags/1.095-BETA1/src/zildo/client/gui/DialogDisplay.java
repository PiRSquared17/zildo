/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.client.gui;

import java.util.List;

import zildo.client.ClientEngineZildo;
import zildo.client.ClientEventNature;
import zildo.client.sound.BankSound;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.dialog.WaitingDialog.CommandDialog;

public class DialogDisplay {
	
	public boolean dialoguing;
	
	private boolean fullSentenceDisplayed;
	
	private String currentSentence;
	private int positionInSentence;
	private int numToScroll;

	public DialogDisplay() {
		dialoguing=false;
	}
	
	public boolean isDialoguing() {
		return dialoguing;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// Call the right method to manage interaction between Zildo and his human relative
	///////////////////////////////////////////////////////////////////////////////////////
	public void manageDialog() {
		if (dialoguing) {
			manageConversation();
		}
	}
	
	/**
	 * Act on a dialog. Launch, continue, or quit the dialog.
	 * @param p_queue
	 * @return boolean (TRUE if dialog ends)
	 */
    public boolean launchDialog(List<WaitingDialog> p_queue) {
    	boolean result=false;
        for (WaitingDialog dial : p_queue) {
            if (dial.client == null) {
            	if (dial.sentence != null && dial.action != CommandDialog.CONTINUE) {
            		if (dial.console) {
            			ClientEngineZildo.guiDisplay.displayMessage(dial.sentence);
            		} else {
            			launchDialog(dial.sentence, dial.action);
            		}
            	} else {
            		result=actOnDialog(dial.sentence, dial.action);
            	}
            }
        }
        return result;
    }
	
	/**
	 * Ask GUI to display the current sentence.
	 * @param p_sentence
	 * @param p_dialAction optional
	 */
	public void launchDialog(String p_sentence, CommandDialog p_dialAction) {
		
		currentSentence=p_sentence.replaceAll("[@|$]", "");

		fullSentenceDisplayed = false;

		int displayMode = GUIDisplay.DIALOGMODE_CLASSIC;
		if (p_dialAction == CommandDialog.BUYING) {
			// Hero is looking items in a store : so display sentence centered and directly
			displayMode = GUIDisplay.DIALOGMODE_MENU;
		}

		positionInSentence=0;
		ClientEngineZildo.guiDisplay.setText(currentSentence, displayMode);
		ClientEngineZildo.guiDisplay.setToDisplay_dialoguing(true);
		dialoguing=true;
	}
	
	public void clearDialogs() {
		positionInSentence=-1;
		numToScroll=0;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// manageConversation
	///////////////////////////////////////////////////////////////////////////////////////
	// Here, Zildo is talking with someone.We can :
	// -go forward into conversation
	// -select a sentence into multiple ones
	// -quit dialog
	///////////////////////////////////////////////////////////////////////////////////////
	void manageConversation() {
		GUIDisplay guiDisplay=ClientEngineZildo.guiDisplay;
	
		boolean entireMessageDisplay=guiDisplay.isEntireMessageDisplay();
		boolean visibleMessageDisplay=guiDisplay.isVisibleMessageDisplay();
	
		if (entireMessageDisplay || visibleMessageDisplay) {
			if (numToScroll!=0) {
				numToScroll--;
				if (!entireMessageDisplay) {
					guiDisplay.scrollAndDisplayTextParts(positionInSentence,currentSentence);
				}
			} else if (entireMessageDisplay && !fullSentenceDisplayed) {
			    // Tell server that sentence is full displayed
			    ClientEngineZildo.askEvent(ClientEventNature.DIALOG_FULLDISPLAY);
			    fullSentenceDisplayed = true;
			}
		} else if (!visibleMessageDisplay ) {
			// Draw sentences slowly (word are appearing one after another)
			positionInSentence++;
			if (positionInSentence % 3 ==0 && (Math.random()*10)>7) {
				ClientEngineZildo.soundPlay.playSoundFX(BankSound.AfficheTexte);
			}
			guiDisplay.displayTextParts(positionInSentence,currentSentence,(numToScroll!=0));
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// actOnDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// -We came here when player clicks ACTION, UP or DOWN
	// .Quit dialog
	// .Move on dialog
	// .Choose topic
	// -Returns TRUE if dialog is finished
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean actOnDialog(String p_sentence, CommandDialog actionDialog) {
		GUIDisplay guiDisplay = ClientEngineZildo.guiDisplay;
		boolean entireMessageDisplay=guiDisplay.isEntireMessageDisplay();
		boolean visibleMessageDisplay=guiDisplay.isVisibleMessageDisplay();
	
		boolean result=false;
		
		if (dialoguing) {
			// Conversation
			switch (actionDialog) {
				case ACTION:
				case CONTINUE:
					if (entireMessageDisplay || visibleMessageDisplay) {
						// Two cases : continue or quit
						if (!entireMessageDisplay) {
							numToScroll=3;
						} else {
						    if (actionDialog == CommandDialog.CONTINUE) {
								launchDialog(p_sentence, actionDialog);
								return false;
						    } else {
								// Quit dialog
								guiDisplay.setToRemove_dialoguing(true);
								dialoguing=false;
								result=true;
						    }
						}
					}
					break;
				case STOP:
					guiDisplay.setToRemove_dialoguing(true);
					dialoguing=false;
					break;
			}
		}
		return result;
	}

}
