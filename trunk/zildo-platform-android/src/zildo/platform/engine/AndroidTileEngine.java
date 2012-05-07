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

package zildo.platform.engine;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.gfx.primitive.TileGroupPrimitive.ActionNthRunner;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.platform.opengl.AndroidOpenGLGestion;

// V1.0
// --------------------------------------------
// 4 vertices ---> 2 triangles ---> 1 tile
// 6 vertices ---> 4 triangles ---> 2 tiles
// 8 vertices ---> 6 triangles ---> 3 tiles
// (...)
// 42 vertices --> 40 triangles ---> 20 tiles

// x----x----x----x ... x----x				a=TILEENGINE_WIDTH
// |0   |1   |2   | ... |a-1 |a
// |    |    |    | ... |    |
// |    |    |    | ... |    |
// x----x----x----x ... x----x
// |a+1 |a+2 |a+3 | ... |2a  |2a+1

// Indices : (0,a+2,a+1) - (0,1,a+2)
//			 (1,a+3,a+2) - (1,2,a+3)
//                (...)
//           (a-1,2a+1,2a) - (a-1,a,2a+1)

// V2.0
// --------------------------------------------
// 4 vertices ---> 2 triangles ---> 1 tile
// 8 vertices ---> 4 triangles ---> 2 tiles
// 12 vertices --> 6 triangles ---> 3 tiles
// (...)
// 80 vertices --> 40 triangles --> 20 tiles

// x----x x----x x----x ... x----x				a=TILEENGINE_WIDTH
// |0  1| |2  3| |4  5| ... |2a-2|2a-1
// |    | |    | |    | ... |    |
// |2a  | |2a+2| |2a+4| ... |4a-2|
// x----x x----x x----x ... x----x
//   2a+1   2a+3   2a+5       4a-1
// x----x x----x x----x ... x----x
// |4a  | |4a+2| |4a+4| ... |6a-2|6a-1

// Indices : (0,2a+1,2a)   - (0,1,2a+1)
//			 (2,2a+3,2a+2) - (2,3,2a+3)

public class AndroidTileEngine extends TileEngine {

	GL10 gl10;
	
	public AndroidTileEngine(TextureEngine texEngine) {
		super(texEngine);
    	gl10 = AndroidOpenGLGestion.gl10;
	}
	
	private TextureBinder texBinder = new TextureBinder();
	
	@Override
	public void render(boolean backGround) {

		if (initialized) {
			Vector3f ambient = ClientEngineZildo.ortho.getAmbientColor();
			if (ambient != null) {
            	// FIXME: previously color3f
				gl10.glColor4f(ambient.x, ambient.y, ambient.z, 1f);
			}

			Point p = ClientEngineZildo.mapDisplay.getCamera();
			gl10.glPushMatrix();
			gl10.glTranslatef(-p.x, -p.y, 0f);
			
			if (backGround) {
				// Display BACKGROUND
				gl10.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				gl10.glEnable(GL11.GL_BLEND);
				meshBACK.render(texBinder);
				meshBACK2.render(texBinder);

				gl10.glDisable(GL11.GL_BLEND);
			}
			else {
				// Display FOREGROUND
				gl10.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				gl10.glEnable(GL11.GL_BLEND);

				meshFORE.render(texBinder);

				gl10.glDisable(GL11.GL_BLEND);
			}
			gl10.glPopMatrix();
		}

	}
	
	private class TextureBinder implements ActionNthRunner {
		public void execute(final int i) {
			gl10.glBindTexture(GL11.GL_TEXTURE_2D, textureEngine.getNthTexture(i)); 
		}
	}
}
