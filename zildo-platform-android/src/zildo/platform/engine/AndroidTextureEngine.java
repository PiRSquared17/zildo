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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.util.Log;

import zildo.fwk.gfx.GraphicStuff;
import zildo.fwk.gfx.engine.TextureEngine;
import zildo.platform.opengl.AndroidOpenGLGestion;

/**
 * Abstract class which provides management of a texture set.
 * 
 * The first phase is the texture creation. It must be done like this:
 * -call prepareSurfaceForTexture()
 * -draw things on the GFXBasics returned
 * -call generateTexture() for adding texture to the texture set
 * 
 * To tell the openGL engine that it must draw with the right texture, just call:
 *  GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureTab[i]);
 * where 'i' is the number of the generated texture
 *                   
 * @author tchegito
 *
 */
public class AndroidTextureEngine extends TextureEngine {
    
	GL10 gl10;
	
	public AndroidTextureEngine(GraphicStuff graphicStuff) {
		super(graphicStuff);
    	gl10 = AndroidOpenGLGestion.gl10;
	}
	
    private int getTextureFormat() {
    	if (alphaChannel) {
    		return GL11.GL_RGBA;
    	} else {
    		return GL11.GL_RGB;
    	}
    }
    @Override
	public int doGenerateTexture() {

        // Create A IntBuffer For Image Address In Memory
        IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
        gl10.glGenTextures(1, buf); // Create Texture In OpenGL

        Log.d("texture", "generate texture "+buf.get(0));
        gl10.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(0));
        // Typical Texture Generation Using Data From The Image

        int wrapping=GL11.GL_REPEAT;	// Wrap texture (useful for cloud)
        gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapping);
        gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapping);
        
        int filtering=GL11.GL_NEAREST;
        // Linear Filtering
        gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filtering);
        gl10.glTexParameterx(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filtering);
        // Generate The Texture
        gl10.glTexImage2D(GL11.GL_TEXTURE_2D, 0, getTextureFormat(), 256, 256, 0, getTextureFormat(), 
        		GL11.GL_UNSIGNED_BYTE, scratch);
        
        return buf.get(0);
    }
    
    @Override
	public void getTextureImage(int p_texId) {
    	gl10.glBindTexture(GL11.GL_TEXTURE_2D, p_texId);
    	// FIXME: this method doesn't exist in OpenGL ES, so we have to find another solution
    	// This method is only called for Zildo outfits : it's not urgent for now
    	//gL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, getTextureFormat(), GL11.GL_UNSIGNED_BYTE, scratch);
    }
    
    /**
    public void saveScreen(int p_texId) {

		// Draw texture with depth
    	GLUtils.copyScreenToTexture(p_texId, 1024, 512);
    }
    */
}
