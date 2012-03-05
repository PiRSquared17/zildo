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

package zildo.platform.opengl.compatibility;

import javax.microedition.khronos.opengles.GL11;

import zildo.fwk.opengl.compatibility.VBO;
import zildo.fwk.opengl.compatibility.VBOBuffers;

public class VBOSoftware implements VBO {

	GL11 gl11;
	
	@Override
	public VBOBuffers create(int p_numPoints) {
		return new VBOBuffers(p_numPoints);
	}

	protected void preDraw() {
		gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		gl11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		gl11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}
	
	@Override
	public void draw(VBOBuffers p_bufs) {
		preDraw();
		gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, p_bufs.vertices);
		gl11.glNormalPointer(0, 0, p_bufs.normals);
		gl11.glTexCoordPointer(2, 0, 0, p_bufs.textures);
		
		// TODO: check this ! it hasn't be tested yet
		int count = p_bufs.indices.limit() - p_bufs.indices.position();
		count = count / 3;
		gl11.glDrawElements(GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_SHORT, count, p_bufs.indices);

	}

	@Override
	public void cleanUp(VBOBuffers p_bufs) {
		p_bufs.vertices.clear();
		p_bufs.textures.clear();
	}

	@Override
	public void endInitialization(VBOBuffers p_bufs) {
		if (p_bufs.vertices.position() != 0) {
			// On se repositionne � z�ro uniquement si on y est pas d�j�
			p_bufs.vertices.flip();
		}
		if (p_bufs.normals.position() != 0) {
			p_bufs.normals.flip();
		}
		if (p_bufs.textures.position() != 0) {
			p_bufs.textures.flip();
		}
		if (p_bufs.indices.position() != 0) {
			p_bufs.indices.flip();
		}
	}
}
