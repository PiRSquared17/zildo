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

package zildo.fwk.script.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Zildo Script Switch.
 * <p/>
 * 
 * Simple "switch/case/default" clause. Used especially with dialogs.
 * <p/>
 * 
 * Example : "new ZSSwitch("flut&!ferme:8,enlevement:3,0")
 * 
 * @author Tchegito
 * 
 */
public class ZSSwitch {

	List<ZSCondition> conditions;
	final int defaultValue;
	String toString = null;

	public ZSSwitch(int p_defaultValue) {
		if (p_defaultValue == ZSCondition.FALSE) {
			throw new RuntimeException("Unable to set the reserved value "
					+ ZSCondition.FALSE);
		}
		defaultValue = p_defaultValue;
		conditions = new ArrayList<ZSCondition>();
	}

	public ZSSwitch(List<ZSCondition> p_conditions, int p_defaultValue) {
		this(p_defaultValue);
		conditions.addAll(p_conditions);
	}

	public ZSSwitch(String p_parseableString) {
		String[] strConds = p_parseableString.replaceAll("-", "&").split(",");
		int def = 0;
		boolean defaultSet = false;
		conditions = new ArrayList<ZSCondition>();
		for (String s : strConds) {
			if (s.indexOf(":") == -1) {
				def = Integer.valueOf(s);
				defaultSet = true;
			} else {
				conditions.add(new ZSCondition(s));
			}
		}
		if (!defaultSet) {
			throw new RuntimeException("Default value must be set");
		}
		defaultValue = def;
	}

	/**
	 * Fluent method, designed for fast construction, with built condition.
	 * 
	 * @param p_condition
	 * @return ZSSwitch
	 */
	public ZSSwitch add(ZSCondition p_condition) {
		conditions.add(p_condition);
		toString = null;
		return this;
	}

	/**
	 * Fluent method, designed for fast construction, with a parseable string.
	 * 
	 * @param p_value
	 * @param p_expression
	 * @return ZSSwitch
	 */
	public ZSSwitch addCondition(String p_expression, int p_value) {
		add(new ZSCondition(p_value, p_expression));
		return this;
	}

	/**
	 * Evalute ordered conditions, and return corresponding value.
	 * 
	 * @return int
	 */
	public int evaluate() {
		int result;
		for (ZSCondition cond : conditions) {
			result = cond.evaluate();
			if (result != ZSCondition.FALSE) {
				return result;
			}
		}
		return defaultValue;
	}

	@Override
	public String toString() {
		// Memorize the toString value
		if (toString == null) {
			StringBuilder sb = new StringBuilder();
			for (ZSCondition e : conditions) {
				sb.append(e.toString());
				sb.append(",");
			}
			sb.append(defaultValue);
			toString = sb.toString();
		}
		return toString;
	}

	/**
	 * Returns TRUE if this switch references the given quest name
	 * 
	 * @param p_questName
	 * @return boolean
	 */
	public boolean contains(String p_questName) {
		return toString().contains(p_questName);
	}
}
