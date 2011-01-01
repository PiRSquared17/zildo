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

package zeditor.tools.sprites;

import zildo.fwk.bank.SpriteBank;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.monde.sprites.SpriteModel;

/**
 * Designed for modifying sprite bank.<br/>
 * 
 * Features:<ul>
 * <li>uses an external image to pick sprites (see {@link #loadImage} and {@link #addSprFromImage})</li>
 * <li>remove sprite</li>
 * <li>add new one</li>
 * </ul>
 * @author Tchegito
 *
 */
public class SpriteBankEdit extends SpriteBank {

	BankEdit bankEdit;
	
    public SpriteBankEdit(SpriteBank p_bank) {
        models=p_bank.getModels();
        name=p_bank.getName();
        nSprite=p_bank.getNSprite();
        sprites_buf=p_bank.getSprites_buf();
        
        bankEdit = new BankEdit();

        // Build all graphics into a single list
        for (int i=0;i<nSprite;i++) {
            short[] gfx=p_bank.getSpriteGfx(i);
            bankEdit.gfxs.add(gfx);
        }
    }
    
    public void addSpr(int p_position, int p_tailleX, int p_tailleY, short[] p_gfx) {
        SpriteModel model=new SpriteModel(p_tailleX, p_tailleY, 0);    // don't care about 'offset'
        bankEdit.gfxs.add(p_position, p_gfx);
        models.add(p_position, model);
        nSprite++;
    }
   
    public void removeSpr(int p_position) {
        models.remove(p_position);
        bankEdit.gfxs.remove(p_position);
        nSprite--;
    }
   
    public void addSprFromImage(int p_position, int p_startX, int p_startY,
			int p_tailleX, int p_tailleY) {
		// Extract sprite from image
		short[] sprite = bankEdit.getRectFromImage(p_startX, p_startY, p_tailleX, p_tailleY);
		addSpr(p_position, p_tailleX, p_tailleY, sprite);
	}
   
    public void loadImage(String p_filename, int p_transparentColor) {
		bankEdit.loadImage(p_filename, p_transparentColor);
	}
    
    public void saveBank() {
        EasyBuffering buffer=new EasyBuffering();
        for (int i=0;i<nSprite;i++) {
            SpriteModel model=models.get(i);
            buffer.put((byte) model.getTaille_x());
            buffer.put((byte) model.getTaille_y());
            for (short s : bankEdit.gfxs.get(i)) {
                buffer.put((byte) s);
            }
        }
        EasyWritingFile file=new EasyWritingFile(buffer);
        file.saveFile(getName());
    }
}
