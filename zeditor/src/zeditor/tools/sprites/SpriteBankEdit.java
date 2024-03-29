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

package zeditor.tools.sprites;

import java.util.Iterator;

import zeditor.tools.builder.Modifier;
import zeditor.tools.tiles.Banque;
import zeditor.tools.tiles.GraphChange;
import zildo.client.gui.GUIDisplay;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.db.Identified;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.gfx.GFXBasics;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.util.Zone;

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
        
        Identified.resetCounter(SpriteModel.class);
    }
    
    public void addSpr(int p_position, int p_tailleX, int p_tailleY, short[] p_gfx) {
        SpriteModel model=new SpriteModel(p_tailleX, p_tailleY, 0);    // don't care about 'offset'
        bankEdit.gfxs.add(p_position, p_gfx);
        models.add(p_position, model);
        nSprite++;
    }
    
    public void setSpr(int p_position, int p_tailleX, int p_tailleY, short[] p_gfx) {
        SpriteModel model=new SpriteModel(p_tailleX, p_tailleY, 0);    // don't care about 'offset'
        bankEdit.gfxs.set(p_position, p_gfx);
        models.set(p_position, model);
        nSprite++;
    }
    
    public void removeSpr(int p_position) {
        models.remove(p_position);
        bankEdit.gfxs.remove(p_position);
        nSprite--;
    }
   
    public void clear() {
    	while (nSprite != 0) {
    		removeSpr(0);
    	}
    }
    
    public void fillNSprite(int number) {
    	for (int i=0;i<number;i++) {
	    	bankEdit.gfxs.add(new short[] {});
	    	models.add(new SpriteModel());
    	}
    }
    
    public void addSprFromImage(int p_position, int p_startX, int p_startY,
			int p_tailleX, int p_tailleY) {
		// Extract sprite from image
		short[] sprite = bankEdit.getRectFromImage(p_startX, p_startY, p_tailleX, p_tailleY);
		addSpr(p_position, p_tailleX, p_tailleY, sprite);
	}
   
    public void setSprFromImage(int p_position, int p_startX, int p_startY,
			int p_tailleX, int p_tailleY) {
		// Extract sprite from image
		short[] sprite = bankEdit.getRectFromImage(p_startX, p_startY, p_tailleX, p_tailleY);
		setSpr(p_position, p_tailleX, p_tailleY, sprite);
	}
    
    public void loadImage(String p_filename, int p_transparentColor) {
		String imageName=Banque.PKM_PATH;
    	// New engine with free tiles
    	// 1) Try with folder containing free tiles
    	String completeName = imageName + "../FreeGraph/" + p_filename + ".png";
    	try {
    		bankEdit.loadImage(completeName, p_transparentColor);
    	} catch (Exception e) {
        	completeName = imageName + p_filename + ".png";
    		bankEdit.loadImage(completeName, p_transparentColor);
    	}
	}
    
    public void saveBank() {
        EasyBuffering buffer=new EasyBuffering(80000);
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
    
    /**
     * Returns the width of an element starting at given coordinates.<br/>
     * We assume that element is ending when there's an entire line made with transparent color at his right.
     * @param p_startX
     * @param p_startY
     * @param p_height
     * @return int
     */
	public int getWidth(int p_startX, int p_startY, int p_height) {
		int width = 0;
		while (p_startX < bankEdit.getImageWidth() && bankEdit.isLineFilled(p_startX + width, p_startY, p_height)) {
			width++;
		}
		return width;
	}
	
	public void addSpritesFromBank(SpriteBanque p_bank) {
   	 Zone[] elements=p_bank.getZones();
   	 Iterator<GraphChange> itChanges = p_bank.getPkmChanges().iterator();
   	 GraphChange current = null;
	 int startSpr=getNSprite();
	 int i=0;
     for (Zone z : elements) {
    	 if (current == null && itChanges.hasNext()) {
    		 current = itChanges.next();
    	 }
    	 if (current != null) {
    		 if (current.nTile == i) {
    			 if (current.decrodedPalette) {
    				 GFXBasics.switchPalette(2);
    			 }
    			 loadImage(current.imageName, Modifier.COLOR_BLUE);
    			 current = null;
    		 }
    	 }
      	addSprFromImage(startSpr + i, z.x1, z.y1, z.x2, z.y2);
      	i++;
      }
	 GFXBasics.switchPalette(1);
	}
	
	public void captureFonts(int posY, int fontHeight, String chars, int constantWidth, int heightSpace) {
		// Capture the fonts
		int startX = 0;
		int startY = posY;
		int nTentativ = 0;
		final int imgWidth = bankEdit.getImageWidth();
		final int offsetnSprite = nSprite;
		int width;
		for (int i = 0; i < GUIDisplay.transcoChar.length(); i++) {
			// Get size
			if (constantWidth != 0) {
				width = constantWidth;
			} else {
				width = getWidth(startX, startY, fontHeight);
			}
			int offsetFont = i;	// Default i-nth font
			char c = GUIDisplay.transcoChar.charAt(i);
			if (chars != null) {
				c = chars.charAt(i);
				offsetFont = GUIDisplay.transcoChar.indexOf(chars.charAt(i));
			}
			if (width > 1) {
				//System.out.println(c);
				if (chars == null) {
					addSprFromImage(offsetnSprite + offsetFont, startX, startY, width, fontHeight);
				} else {
					setSprFromImage(offsetnSprite + offsetFont, startX, startY, width, fontHeight);
				}

				//System.out.println(startX + " , " + startY + " size=" + width);

				startX += width + 1;
				if (constantWidth > 0) {
					startX--;	// No need to space fonts, if width is constant
				}
				nTentativ = 0;
			} else {
				if (nTentativ == 5 || startX >= imgWidth) {
					startX = 0;
					startY += fontHeight + heightSpace;
					i--;
					nTentativ = 0;
				} else {
					nTentativ++;
					startX++;
					i--;
				}
			}
		}
	}
}
