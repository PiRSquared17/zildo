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

package zildo.monde.sprites.elements;

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;


public class ElementGoodies extends Element {

	// Coeur : nSpr=40
	// Diamant : nSpr=10
	
	private int timeToAcquire;	// Untakeable. Zildo has just to wait to have it (for chest)
	protected boolean volatil=true;	// TRUE=goodies disappear after a delay
	
	/**
	 * Common goodies : volatil
	 */
	public ElementGoodies() {
		super();
		spe=540;	// Dur�e de vie du goodies, en frames (on tourne en g�n�ral � 60FPS : 540==9sec)
	}
	
	/**
	 * Constructor for object coming from a chest. He's designed for Zildo.
	 * @param p_zildo
	 */
	public ElementGoodies(Perso p_zildo) {
		linkedPerso=p_zildo;
		timeToAcquire=60;
	}
	
	@Override
	public void animate() {
		
		super.animate();
		
		if (volatil) {
			spe--;
		}
		
		ElementDescription spr=ElementDescription.fromInt(nSpr);
		if (spr == ElementDescription.HEART_LEFT) {
			// Coeur voletant vers le sol
			if (vx<=-0.15) {
				ax=0.01f;
				addSpr=0;	// Coeur tourn� vers la gauche
			} else if (vx>=0.15) {
				ax=-0.01f;
				addSpr=1;	// Coeur tourn� vers la droite
			}
			if (z<=4) {
				nSpr=10;
				addSpr=0;
				vx=0;
				ax=0;
				y=y+3;
			}
		}
		
		
		if (spr==ElementDescription.HEART || spr.isMoney()) {
			// Il s'agit d'un diamant ou du coeur (10)
			int eff=EngineZildo.compteur_animation % 100;
			// 1) brillance
			if (eff<33 && spr!=ElementDescription.HEART) {		// Les diamants brillent
				addSpr=(eff / 10) % 3;
			}
			// 2) s'arr�te sur le sol
			if (z<4) {
				z=4;
			}
		}
		
		if (timeToAcquire > 0) {
			timeToAcquire--;
			if (timeToAcquire == 0) {
				// Zildo will now have the goodies
				((PersoZildo)linkedPerso).pickGoodies(this);
				dying=true;
			}
		}
		
		if (spe==0) {
			// Le sprite doit mourir
			dying=true;
		} else if (spe<120) {
			visible=(spe%4>1);
		} else if (spe<60) {
			visible=(spe%2==0);
		}
		
		setAjustedX((int) x);
		setAjustedY((int) y);
	}
	
	@Override
	public boolean isGoodies() {
		return true;
	}
	
	@Override
	public boolean beingCollided(Perso p_perso) {
		return true;
	}

}
