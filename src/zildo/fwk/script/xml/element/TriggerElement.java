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

import zildo.fwk.script.model.ZSSwitch;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Point;
import zildo.monde.map.Zone;
import zildo.monde.quest.QuestEvent;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;

public class TriggerElement extends AnyElement {

	public final QuestEvent kind;
	String name; // dialog, map, item and questDone
	int numSentence;
	Point location;
	int radius; // For location
	boolean not;	// Use for inversion of inventory posess
	Zone region; // Unimplemented yet

	ZSSwitch questSwitch;

	public TriggerElement(QuestEvent p_kind) {
		kind = p_kind;
	}

	//
	@Override
	public void parse(Element p_elem) {
		if (kind == null) {
			throw new RuntimeException("Trigger kind is unknown !");
		}
		switch (kind) {
		case DIALOG:
			numSentence = Integer.valueOf(p_elem.getAttribute("num"));
		case LOCATION:
			name = readAttribute(p_elem, "name");
			radius = readInt(p_elem, "radius");
			String strPos = readAttribute(p_elem, "pos");
			if (strPos != null) {
				location = Point.fromString(strPos);
			}
			break;
		case QUESTDONE:
			name = readAttribute(p_elem, "name");
			questSwitch = new ZSSwitch(name + ":1,0");
			break;
		case INVENTORY:
			// TODO: replace this by a switch => most complete
			name = p_elem.getAttribute("item");
			not = name.indexOf("!") != -1;
			name = name.replaceAll("!", "");
			break;
		}

	}

	/**
	 * Returns TRUE if the given trigger matches the current one.
	 * <p/>
	 * We assume they are same kind, and that given one is undone.
	 * 
	 * @param p_another
	 * @return boolean
	 */
	public boolean match(TriggerElement p_another) {
		if (kind != p_another.kind) {
			return false; // Different kinds
		}
		switch (kind) {
		case DIALOG:
			if (p_another.name.equals(name)
					&& p_another.numSentence == numSentence) {
				return true;
			}
			break;
		case INVENTORY:
			if (p_another.name.equals(name)) {
				return !not;
			}
			break;
		case LOCATION:
			if (p_another.name.equals(name)) {
				if (p_another.location == null && location == null) {
					return true;
				} else if (p_another.location != null && location != null) {
					float dist = p_another.location.distance(location);
					// System.out.println(dist + "   <   "+(8f + 16*radius));
					return dist < (8f + 16 * radius);
				}
			}
			break;
		case QUESTDONE:
			if (questSwitch.contains(p_another.name)) {
				return questSwitch.evaluate() == 1;
			}
		}
		return false;
	}

	public boolean isLocationSpecific() {
		return kind == QuestEvent.LOCATION && name != null && location != null;
	}

	/**
	 * The 'done' member isn't reliable, because of the QUESTDONE kind of
	 * triggers.
	 * 
	 * @return boolean
	 */
	public boolean isDone() {
		switch (kind) {
		case QUESTDONE:
			return questSwitch.evaluate() == 1;
		case INVENTORY:
			PersoZildo zildo = EngineZildo.persoManagement.getZildo();
			return zildo != null && zildo.hasItem(ItemKind.fromString(name)) == !not;
		default:
			return done;
		}
	}

	/**
	 * Ingame method to check a dialog trigger.
	 * 
	 * @param p_name
	 * @param p_num
	 * @return TriggerElement for Dialog
	 */
	public static TriggerElement createDialogTrigger(String p_name, int p_num) {
		TriggerElement elem = new TriggerElement(QuestEvent.DIALOG);
		elem.name = p_name;
		elem.numSentence = p_num;
		return elem;
	}

	/**
	 * Ingame method to check a inventory trigger.
	 * 
	 * @param p_name
	 * @return TriggerElement for Inventory
	 */
	public static TriggerElement createInventoryTrigger(ItemKind p_item) {
		TriggerElement elem = new TriggerElement(QuestEvent.INVENTORY);
		elem.name = p_item.toString();
		return elem;
	}

	/**
	 * Ingame method to check a location trigger.
	 * 
	 * @param p_mapName
	 * @return TriggerElement
	 */
	public static TriggerElement createLocationTrigger(String p_mapName,
			Point p_location) {
		TriggerElement elem = new TriggerElement(QuestEvent.LOCATION);
		elem.name = p_mapName;
		if (p_location != null) {
			elem.location = p_location;
		}
		return elem;
	}

	/**
	 * Ingame method to check a 'quest done' trigger.
	 * 
	 * @param p_quest
	 * @return TriggerElement
	 */
	public static TriggerElement createQuestDoneTrigger(String p_quest) {
		TriggerElement elem = new TriggerElement(QuestEvent.QUESTDONE);
		elem.name = p_quest;
		return elem;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(kind.toString());
		sb.append(" ");
		if (name != null) {
			sb.append(name).append(" ");
		}
		if (location != null) {
			sb.append(location).append(" ");
		}
		if (kind == QuestEvent.DIALOG) {
			sb.append(numSentence);
		}
		return sb.toString();
	}
}