/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
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

package zeditor.core.tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zeditor.core.selection.PersoSelection;
import zeditor.core.selection.SpriteSelection;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.GFXBasics;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZPersoLibrary;
import zildo.monde.sprites.desc.ZSpriteLibrary;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.util.Angle;
import zildo.monde.util.Vector4f;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
@SuppressWarnings("serial")
public class SpriteSet extends ImageSet {

	public final static int width = 320;
	public final static int height = 350;

	protected Map<Zone, SpriteDescription> objectsFromZone;

	boolean perso;

	final static ZPersoLibrary persoLibrary = new ZPersoLibrary();
	final static List<SpriteDescription> spriteLibrary = ZSpriteLibrary
			.getList();

	/**
	 * Initialize the object
	 * 
	 * @param p_perso
	 *            TRUE=Perso / FALSE=Element
	 * @param p_manager
	 */
	public SpriteSet(boolean p_perso, MasterFrameManager p_manager) {
		super(null, p_manager);

		perso = p_perso;

		objectsFromZone = new HashMap<Zone, SpriteDescription>();
		initImage(p_perso ? persoLibrary : spriteLibrary);
	}

	public void initImage(List<SpriteDescription> p_bankDesc) {
		currentTile = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		currentTile.getGraphics();

		if (false) { // for test, now
			p_bankDesc = prepareListFromBank(4);
		}

		displayListSprites(p_bankDesc);
	}

	/**
	 * Return a virtual list for displaying an entire bank
	 * 
	 * @param p_numBank
	 * @return List<SpriteDescription>
	 */
	private List<SpriteDescription> prepareListFromBank(final int p_numBank) {
		// Display an entire bank
		List<SpriteDescription> list = new ArrayList<SpriteDescription>();
		SpriteBank bank = EngineZildo.spriteManagement.getSpriteBank(p_numBank);
		for (int i = 0; i < bank.getNSprite(); i++) {
			final int n = i;
			list.add(new SpriteDescription() {
				@Override
				public int getBank() {
					return p_numBank;
				}

				@Override
				public int getNSpr() {
					return n;
				}

				@Override
				public boolean isBlocking() {
					return false;
				}

				@Override
				public int ordinal() {
					return n;
				}

				@Override
				public boolean isDamageable() {
					return false;
				}
				
				@Override
				public boolean isPushable() {
					return false;
				}

				@Override
				public boolean isNotFixe() {
					return false;
				}

				@Override
				public boolean isSliping() {
					return false;
				}

				@Override
				public boolean isWeapon() {
					return false;
				}

				@Override
				public int getRadius() {
					// TODO Auto-generated method stub
					return 0;
				}

			});
		}
		return list;
	}

	private void displayListSprites(List<SpriteDescription> p_list) {

		int posX = 0;
		int posY = 0;
		int maxY = 0;
		for (SpriteDescription sprite : p_list) {

			SpriteBank bank = EngineZildo.spriteManagement.getSpriteBank(sprite
					.getBank());
			int nSpr = sprite.getNSpr();
			if (bank.getName().equals("pnj2.spr")) {
				//nSpr = nSpr - 128;
			}
			SpriteModel model = bank.get_sprite(nSpr);

			int sizeX = model.getTaille_x();
			if (posX + sizeX > width) {
				posX = 0;
				posY += maxY;
				maxY = 0;
			}
			if (model.getTaille_y() > maxY) {
				maxY = model.getTaille_y();
			}

			drawPerso(posX, posY, bank, nSpr, false);

			// Store this zone into the list
			Zone z = new Zone(posX, posY, model.getTaille_x(),
					model.getTaille_y());
			selectables.add(z);
			objectsFromZone.put(z, sprite);
			posX += sizeX;
		}
	}

	final Vector4f colMasque = new Vector4f(192, 128, 240, 1);

	/**
	 * Display sprite, with or without mask
	 * 
	 * @param i
	 * @param j
	 * @param nBank
	 * @param nMotif
	 * @param masque
	 */
	private void drawPerso(int i, int j, SpriteBank pnjBank, int nSpr,
			boolean masque) {

		SpriteModel model = pnjBank.get_sprite(nSpr);
		short[] data = pnjBank.getSpriteGfx(nSpr);

		Graphics2D gfx2d = (Graphics2D) currentTile.getGraphics();

		for (int y = 0; y < model.getTaille_y(); y++) {
			for (int x = 0; x < model.getTaille_x(); x++) {
				int offset = y * model.getTaille_x() + x;
				int a = data[offset];
				if (a != 255) {
					Vector4f col = GFXBasics.getColor(a);
					gfx2d.setColor(new Color(col.x / 256, col.y / 256,
							col.z / 256));
					gfx2d.drawLine(x + i, y + j, x + i, y + j);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildSelection() {
		Zone z = getObjectOnClick(startPoint.x + 1, startPoint.y + 1);
		if (z != null) {
			SpriteDescription desc = objectsFromZone.get(z);
			if (desc != null) {
				if (perso) {
					PersoDescription persoDesc = (PersoDescription) desc;
					// Initialize a virtual character
					Perso temp = EngineZildo.persoManagement.createPerso(
							persoDesc, 0, 0, 0, "new", Angle.NORD.value);
					temp.setInfo(PersoInfo.NEUTRAL);
					temp.initPersoFX();
					switch (persoDesc) {
						case PANNEAU:
							temp.setName("pano");
							break;
						case PAPER_NOTE:
							temp.setName("note");
							break;
						case GARDE_BOUCLIER:
							temp.setName("garde");
							break;
					}
					persoLibrary.initialize(temp);

					currentSelection = new PersoSelection(temp);
					manager.setPersoSelection((PersoSelection) currentSelection);
				} else {
					SpriteEntity temp;
					if (desc instanceof ElementDescription) {
						temp = ((ElementDescription) desc).createElement();
					} else {
						temp = new Element();
					}
					temp.setDesc(desc);
					currentSelection = buildSelection(temp);
					manager.setSpriteSelection((SpriteSelection<SpriteEntity>) currentSelection);
				}
			}
		}
	}

	@Override
	protected void specificPaint(Graphics2D p_g2d) {

	}

	/**
	 * Build a selection with a given element. Useful for composite sprites only
	 * (for example: doors)
	 * 
	 * @param p_elem
	 * @return SpriteSelection
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends SpriteEntity> SpriteSelection<T> buildSelection(T p_elem) {
		SpriteDescription desc = p_elem.getDesc();
		if (desc instanceof ElementDescription) {
			switch ((ElementDescription) desc) {
			case DOOR_OPEN1:
				// Create linked elements
				Element el = new Element();
				el.setDesc(ElementDescription.DOOR_OPEN1);
				el.reverse = Reverse.HORIZONTAL;
				el.x += el.getSprModel().getTaille_x();

				Element up1 = new Element();
				up1.setDesc(ElementDescription.DOOR_OPEN2);
				up1.y -= el.getSprModel().getTaille_y();
				up1.reverse = Reverse.VERTICAL;

				Element up2 = new Element();
				up2.setDesc(ElementDescription.DOOR_OPEN2);
				up2.x += up2.getSprModel().getTaille_x();
				up2.y -= el.getSprModel().getTaille_y();
				up2.reverse = Reverse.ALL;
				return new SpriteSelection(Arrays.asList(p_elem, el, up1, up2));
			case CARPET:
				p_elem=(T) new SpriteEntity();
				p_elem.setDesc(desc);
				p_elem.repeatX=5;
			default:
			}
		}
		return new SpriteSelection<T>(p_elem);
	}

}
