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

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.xml.ScriptReader;

public class AdventureElement extends AnyElement {

	List<SceneElement> scenes;
	List<QuestElement> quests;
	List<MapscriptElement> mapScripts;
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		scenes = (List<SceneElement>) ScriptReader.parseNodes(p_elem, "scene");
		quests = (List<QuestElement>) ScriptReader.parseNodes(p_elem, "quest");
		mapScripts=(List<MapscriptElement>) ScriptReader.parseNodes(p_elem, "mapScript");
	}

	/**
	 * Get the named scene, if it exists.
	 * @param p_name
	 * @return SceneElement
	 */
	public SceneElement getSceneNamed(String p_name) {
		for (SceneElement scene : scenes) {
			if (scene.id.equalsIgnoreCase(p_name)) {
				return scene;
			}
		}
		return null;
	}
	
	public List<QuestElement> getQuests() {
		return quests;
	}
	
	public List<SceneElement> getScenes() {
	    return scenes;
	}
	
	public List<MapscriptElement> getMapScripts() {
	    return mapScripts;
	}
	
	/**
	 * Manually add a quest to the adventure. (only for automatic behaviors like chest and doors)
	 * @param p_quest
	 */
	public void addQuest(QuestElement p_quest) {
		quests.add(p_quest);
	}
}
