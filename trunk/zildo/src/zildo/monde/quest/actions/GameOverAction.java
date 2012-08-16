/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.monde.quest.actions;

import zildo.client.ClientEngineZildo;
import zildo.client.stage.SinglePlayer;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.ui.UIText;
import zildo.monde.dialog.ActionDialog;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

/**
 * When Zildo dies in single player.
 * <p/>
 * Displays a message and shut down the single player.<br/>
 * Back to the start menu.
 * 
 * @author tchegito
 *
 */
public class GameOverAction extends ActionDialog {

	public GameOverAction() {
		super(UIText.getGameText("game.over"));
		ClientEngineZildo.filterCommand.fadeOut(FilterEffect.SEMIFADE);
	}
	
	@Override
	public void launchAction(ClientState p_clientState) {
		// Reset map / GUI
		EngineZildo.mapManagement.deleteCurrentMap();
		ClientEngineZildo.tileEngine.cleanUp();
		ClientEngineZildo.guiDisplay.setToDisplay_generalGui(false);
		ClientEngineZildo.soundPlay.stopMusic();
		ClientEngineZildo.filterCommand.fadeIn(FilterEffect.SEMIFADE);
		// Stop this game
		SinglePlayer.getClientState().gameOver=true;
		// Return on the start menu
		ClientEngineZildo.getClientForMenu().quitGame();
	}

}
