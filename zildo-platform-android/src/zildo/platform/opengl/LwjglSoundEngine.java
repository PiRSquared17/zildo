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

package zildo.platform.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.SoundStore;

import zildo.Zildo;
import zildo.fwk.opengl.Sound;
import zildo.fwk.opengl.SoundEngine;

/**
 * @author Tchegito
 *
 */
public class LwjglSoundEngine extends SoundEngine {

	@Override
	public Sound createSound(String path) {
		return new LwjglSound(path);
	}
	
	@Override
	public void detectAndInitSoundEngine() {
		if (failed || initialized || !Zildo.soundEnabled) {
			return;	// No need to try once more
		}
		try {
			AL.create();
			AL.destroy();
			SoundStore.get().init();
			initialized = true;
		} catch (LWJGLException e) {
			Zildo.soundEnabled = false;
			failed = true;	// Avoid to reinit another time
		}
	}
	

	@Override
	public void pollMusic(int delta) {
		try {
			SoundStore.get().poll(delta);
		} catch (Throwable t) {
			// Not a big deal : music is wrapping
			System.out.println("coucou");
		}
	}
	

	@Override
	public void cleanUp() {
		AL.destroy();
	}
}