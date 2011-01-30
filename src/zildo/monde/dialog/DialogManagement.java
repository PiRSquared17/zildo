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

package zildo.monde.dialog;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.net.TransferObject;
import zildo.fwk.script.xml.TriggerElement;
import zildo.monde.items.Item;
import zildo.monde.items.ItemCircle;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

// DialogManagement.cpp: implementation of the DialogManagement class.
//
//////////////////////////////////////////////////////////////////////


public class DialogManagement {
	
	private boolean dialoguing;
	private boolean topicChoosing;
	
	List<WaitingDialog> dialogQueue;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public DialogManagement() {
		clearDialogs();
		
		dialogQueue=new ArrayList<WaitingDialog>();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : p_zildo : 
	//      persoToTalk : 
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Launch an interaction between Zildo and a character, or ingame event: <ul>
	 * <li>if <code>p_persoToTalk</code> is not null, starts a dialog with him.</li>
	 * <li>if <code>p_actionDialog</code> is not null, displays text and launch delegate action.</li>
	 * </ul>
	 * @param p_client Zildo who's talking
	 * @param p_persoToTalk perso to talk with
	 * @param p_actionDialog
	 */
	public void launchDialog(ClientState p_client, Perso persoToTalk, ActionDialog p_actionDialog ) {
		MapDialog dialogs=EngineZildo.mapManagement.getCurrentMap().getMapDialog();
		String sentence;
		
		if (persoToTalk != null) {
			// Dialog with character
			Behavior behav=dialogs.getBehaviors().get(persoToTalk.getNom());
			if (behav == null) {
				// This perso can't talk
				return;
			}
			int compteDial=persoToTalk.getCompte_dialogue();
			
	        sentence = dialogs.getSentence(behav, compteDial);
			
	        // Update perso about next sentence he(she) will say
	        String sharp = "#";
	        int posSharp = sentence.indexOf(sharp);
	        if (posSharp != -1) {
	            // La phrase demande explicitement de rediriger vers une autre
	            persoToTalk.setCompte_dialogue(sentence.charAt(posSharp + 1) - 48);
	            sentence = sentence.substring(0, posSharp);

	        } else if (behav.replique[compteDial + 1] != 0) {
	            // On passe � la suivante, puisqu'elle existe
	            persoToTalk.setCompte_dialogue(compteDial + 1);
	        }

	        // Adventure trigger
	        TriggerElement trig=TriggerElement.createDialogTrigger(persoToTalk.getNom(), compteDial);
	        EngineZildo.scriptManagement.trigger(trig);

	        // Set the dialoguing states for each Perso
	        persoToTalk.setDialoguingWith(p_client.zildo);

		} else {
			// Ingame event
			sentence=p_actionDialog.text;
	        p_client.dialogState.actionDialog = p_actionDialog;
		}

        sentence = sentence.trim();
        dialogQueue.add(new WaitingDialog(sentence, -1, false, p_client == null ? null : p_client.location));
        
        p_client.dialogState.dialoguing = true;
        p_client.zildo.setDialoguingWith(persoToTalk);
    }
	
	public void stopDialog(ClientState p_client) {
		//p_client.dialogState.dialoguing=false;
		PersoZildo zildo=p_client.zildo;
		Perso perso=p_client.zildo.getDialoguingWith();
		//zildo.setDialoguingWith(null);
		if (perso != null) {
			//perso.setDialoguingWith(null);
			
			zildo.guiCircle=new ItemCircle(zildo);
			List<Item> items=new ArrayList<Item>();
			items.addAll(zildo.getInventory());
			items.addAll(zildo.getInventory());
			zildo.guiCircle.create(items, 0, perso);
		} else {
			ActionDialog actionDialog=p_client.dialogState.actionDialog;
			if (actionDialog != null) {
				actionDialog.launchAction();
			}
		}
	}
	public void actOnDialog(TransferObject p_location, int p_actionDialog) {
		dialogQueue.add(new WaitingDialog(null, p_actionDialog, false, p_location));
	}
	
	public void writeConsole(String p_sentence) {
		dialogQueue.add(new WaitingDialog(p_sentence, 0, true, null));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchTopicSelection
	///////////////////////////////////////////////////////////////////////////////////////
	public void launchTopicSelection() {
		//TODO : topic
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clearDialogs
	///////////////////////////////////////////////////////////////////////////////////////
	public void clearDialogs() {
		dialoguing=false;
		topicChoosing=false;
	}

	public boolean isDialoguing() {
		return dialoguing;
	}

	public void setDialoguing(boolean dialoguing) {
		this.dialoguing = dialoguing;
	}

	public boolean isTopicChoosing() {
		return topicChoosing;
	}

	public void setTopicChoosing(boolean topicChoosing) {
		this.topicChoosing = topicChoosing;
	}
	
	public List<WaitingDialog> getQueue() {
		return dialogQueue;
	}
	
	public void resetQueue() {
		dialogQueue.clear();
	}

}