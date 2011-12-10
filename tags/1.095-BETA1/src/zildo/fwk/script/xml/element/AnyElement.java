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

package zildo.fwk.script.xml.element;

import org.w3c.dom.Element;

public abstract class AnyElement {

    public boolean waiting = false;
    public boolean done = false;

    public abstract void parse(Element p_elem);
    
    // Useful operations
    public boolean isTrue(Element p_elem, String p_attrName) {
    	String str=p_elem.getAttribute(p_attrName);
    	return str.equalsIgnoreCase("true");
    }
    
    /**
     * Read an attribute's value, and return NULL if it isn't set.
     * @param p_elem
     * @param p_attrName
     * @return String
     */
    protected String readAttribute(Element p_elem, String p_attrName) {
    	String value = p_elem.getAttribute(p_attrName);
    	return "".equals(value) ? null : value;
    }
    
    /**
     * Read an int value. Returns 0 if null.
     * @param p_elem
     * @param p_attrName
     * @return int
     */
    protected int readInt(Element p_elem, String p_attrName) {
		String strValue = readAttribute(p_elem, p_attrName);
		if (strValue == null) {
			return 0;
		} else {
			return Integer.valueOf(strValue);
		}
    }
}