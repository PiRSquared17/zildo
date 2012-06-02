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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import zildo.Zildo;
import zildo.fwk.ZUtils;
import zildo.fwk.opengl.compatibility.FBO;
import android.opengl.GLES20;

/**
 * @author tchegito
 */
public class FBOHardware implements FBO {

	//TODO: not implemented yet, but check later the NDK (native development kit)
	//Apparently, we could use native code for opengl es
	
	@Override
	public int create() {
		IntBuffer buffer = ByteBuffer.allocateDirect(1 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glGenFramebuffers(1, buffer); // generate
		int fboId = buffer.get();

		checkCompleteness(fboId);

		return fboId;
	}

	@Override
	public void bindToTextureAndDepth(int myTextureId, int myDepthId,
			int myFBOId) {
		// On bind le FBO � la texture
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, myFBOId);

		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, 
				GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES,
				GLES20.GL_TEXTURE_2D, myTextureId, 0);

		// Puis on d�tache la texture de la vue
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}

	@Override
	public int generateDepthBuffer() {/*
		IntBuffer buf = ByteBuffer.allocateDirect(4)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		EXTFramebufferObject.glGenRenderbuffersEXT(buf); // Create Texture In
															// OpenGL
		int depthID = buf.get(0);

		EXTFramebufferObject.glBindRenderbufferEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthID);
		EXTFramebufferObject.glRenderbufferStorageEXT(
				EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				GL11.GL_DEPTH_COMPONENT, ZUtils.adjustTexSize(Zildo.viewPortX),
				ZUtils.adjustTexSize(Zildo.viewPortY));

		return depthID;*/
		return 0;
	}

	@Override
	public void startRendering(int myFBOId, int sizeX, int sizeY) {

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, myFBOId);
		
		GLES20.glViewport(0, 0, Zildo.viewPortX, Zildo.viewPortY);
		GLES20.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void endRendering() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		// Restore viewport
		GLES20.glViewport(0, 0, Zildo.screenX, Zildo.screenY);

	}

	@Override
	public void cleanUp(int id) { 
		GLES20.glDeleteFramebuffers(1, ZUtils.getBufferWithId(id));
	}

	@Override
	public void cleanDepthBuffer(int id) { /*
		EXTFramebufferObject
				.glDeleteRenderbuffersEXT(ZUtils.getBufferWithId(id));
	*/}

	public void checkCompleteness(int myFBOId) {
		/*
		int framebuffer = EXTFramebufferObject
				.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
		switch (framebuffer) {

		case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
			break;
		case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
			throw new RuntimeException(
					"FrameBuffer: "
							+ myFBOId
							+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
		case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			throw new RuntimeException(
					"FrameBuffer: "
							+ myFBOId
							+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
		case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
			throw new RuntimeException(
					"FrameBuffer: "
							+ myFBOId
							+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
		case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
			throw new RuntimeException(
					"FrameBuffer: "
							+ myFBOId
							+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
		case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
			throw new RuntimeException(
					"FrameBuffer: "
							+ myFBOId
							+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
		case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
			throw new RuntimeException(
					"FrameBuffer: "
							+ myFBOId
							+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
		default:
			throw new RuntimeException(
					"Unexpected reply from glCheckFramebufferStatusEXT: "
							+ framebuffer);
		}
		*/
	}
}