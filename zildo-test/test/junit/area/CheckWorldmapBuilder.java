/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package junit.area;

import junit.perso.EngineUT;

import org.junit.Test;

import zeditor.tools.builder.WorldmapBuilder;

/**
 * @author Tchegito
 *
 */
public class CheckWorldmapBuilder extends EngineUT {

	@Test
	public void basic() {
		String firstMap = "coucou";
		new WorldmapBuilder(firstMap, null);
	}
	
	@Test
	public void assembleImages() {
		String firstMap = "coucou";
		WorldmapBuilder wmb = new WorldmapBuilder(firstMap, null);
		wmb.savePng();
	}
}
