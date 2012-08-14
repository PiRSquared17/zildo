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

package zildo.fwk.gfx.filter;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.ZUtils;
import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.primitive.QuadPrimitive;
import zildo.monde.util.Vector3f;


/**
 * Defines a screen filter.
 * 
 * Provides basically:<ul>
 * <li>a FBO</li>
 * <li>a texture</li>
 * <li>a depth rendered buffer</li>
 * <li>a screen sized tile</li> 
 * </ul>
 * A simple <code>super.render()</code> from a derived class draw binded texture at screen.
 * 
 * @author tchegito
 *
 */
public abstract class ScreenFilter extends QuadPrimitive {

	// Screen size
	protected static final int sizeX=Zildo.viewPortX;
	protected static final int sizeY=Zildo.viewPortY;
	// Resizing for OpenGL storage
	protected static final int realX=ZUtils.adjustTexSize(sizeX);
	protected static final int realY=ZUtils.adjustTexSize(sizeY);
	
	// common members
	protected int textureID;
	protected int depthTextureID;
	protected int fboId=-1;
	protected boolean active=true;
	
	protected GraphicStuff graphicStuff;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	public ScreenFilter(GraphicStuff graphicStuff)
	{
		super(4,ZUtils.adjustTexSize(sizeX), ZUtils.adjustTexSize(sizeY));
		
		this.graphicStuff = graphicStuff;
		
		// Create a screen sized quad
		super.startInitialization();
		this.addQuadSized(0,0,0.0f,0.0f,sizeX, sizeY);
		this.endInitialization();
	
		// Create texture for alpha blending
		this.createBlankTexture(true);
		
		setActive(false, null);
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// renderFilter
	///////////////////////////////////////////////////////////////////////////////////////
	// Render filter on screen, after GUI done.
	///////////////////////////////////////////////////////////////////////////////////////
	public abstract boolean renderFilter();
	
	///////////////////////////////////////////////////////////////////////////////////////
	// createBlankTexture
	///////////////////////////////////////////////////////////////////////////////////////
	// Create a OpenGL texture object, and attach a FBO to it.
	///////////////////////////////////////////////////////////////////////////////////////
	private void createBlankTexture(boolean p_withDepth) {
		
		textureID=graphicStuff.generateTexture(sizeX, sizeY);
		if (p_withDepth) {
			depthTextureID=graphicStuff.generateDepthBuffer();
		}
		
        attachTextureToFBO(textureID, depthTextureID);
	}
	
	private void attachTextureToFBO(int texId, int texDepthId) {
		if (fboId == -1) {
			fboId=graphicStuff.fbo.create();
		}
		graphicStuff.fbo.bindToTextureAndDepth(texId, texDepthId, fboId);
	}

	/**
	 * Default preFilter : full color
	 */
	public void preFilter() {
		ClientEngineZildo.ortho.setAmbientColor(new Vector3f(1, 1, 1));
	}

	public void postFilter() {
		
	}
	
	public void doOnInactive(FilterEffect effect) {
		
	}
	
	public void doOnActive(FilterEffect effect) {
		
	}
	
	@Override
	final public void cleanUp() {
		if (fboId > 0) {
			graphicStuff.cleanTexture(textureID);
			graphicStuff.cleanDepthBuffer(depthTextureID);
			graphicStuff.cleanFBO(fboId);
		}
	}
	
	final public void setActive(boolean activ, FilterEffect effect) {
		active=activ;
		if (!activ) {
			doOnInactive(effect);
		} else {
			doOnActive(effect);
		}
	}
	
	final public boolean isActive() {
		return active;
	}
}