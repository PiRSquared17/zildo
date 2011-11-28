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

package zeditor.windows.subpanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zeditor.core.selection.SpriteSelection;
import zeditor.core.tiles.SpriteSet;
import zeditor.tools.ui.SizedGridPanel;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.ZUtils;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZSpriteLibrary;

/**
 * @author Tchegito
 * 
 */
public class SpritePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -591070226658678002L;

	MasterFrameManager manager;

	JCheckBox reverseHorizontal;
	JCheckBox reverseVertical;
	JCheckBox foreground;
	JComboBox spriteType;
	JSpinner spinX;
	JSpinner spinY;
	JSpinner repeatX;
	JSpinner repeatY;
	JLabel entityType;

	boolean updatingUI;

	SpriteSelection<SpriteEntity> sel;

	SpriteEntity entity;
	List<SpriteEntity> entities; // We can modify a global list of entities

	final List<SpriteDescription> spriteLib = ZSpriteLibrary.getList();

	public SpritePanel(MasterFrameManager p_manager) {
		setLayout(new BorderLayout());
		add(new SpriteSet(false, p_manager), BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);

		manager = p_manager;
	}

	@SuppressWarnings("serial")
	private JPanel getSouthPanel() {
		SizedGridPanel panel = new SizedGridPanel(9, 0);

		foreground = new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (sel != null) {
					sel.toggleForeground();
				}
			}
		});

		entityType = new JLabel();
		panel.addComp(new JLabel("Kind"), entityType);

		spriteType = new JComboBox(spriteLib.toArray());
		panel.addComp(new JLabel("Type"), spriteType);

		panel.addComp(new JLabel("Foreground"), foreground);

		spinX = new JSpinner(new SpinnerNumberModel(0, -1, 16, -1));
		spinY = new JSpinner(new SpinnerNumberModel(0, -1, 16, -1));

		panel.addComp(new JLabel("Add-x"), spinX);

		panel.addComp(new JLabel("Add-y"), spinY);

		reverseHorizontal = new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (sel != null) {
					toggleReverse(true);
				}
			}

		});
		panel.addComp(new JLabel("Reverse H"), reverseHorizontal);

		reverseVertical = new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (sel != null) {
					toggleReverse(false);
				}
			}

		});
		panel.addComp(new JLabel("Reverse V"), reverseVertical);

		SpriteFieldsListener listener = new SpriteFieldsListener();
		spriteType.addActionListener(listener);
		spinX.addChangeListener(listener);
		spinY.addChangeListener(listener);

		// Repeat fields
		repeatX = new JSpinner(new SpinnerNumberModel(1, 1, 127, -1));
		repeatY = new JSpinner(new SpinnerNumberModel(1, 1, 127, -1));
		repeatX.addChangeListener(listener);
		repeatY.addChangeListener(listener);

		panel.addComp(new JLabel("Repeat-x"), repeatX);

		panel.addComp(new JLabel("Repeat-y"), repeatY);
		return panel;
	}

	private void toggleReverse(boolean p_horizontal) {
		sel.reverse(p_horizontal);
		// Ask a sprite visual update
		manager.getZildoCanvas().setChangeSprites(true);
	}

	/**
	 * Update fields with the given entity's infos.
	 * 
	 * @param p_entity
	 */
	private void focusSprite(SpriteEntity p_entity) {
		updatingUI = true;
		if (p_entity == null) {
			// Reset fields
			entityType.setText("");
			spinX.setValue(0);
			spinY.setValue(0);
			spriteType.setSelectedIndex(0);
			reverseHorizontal.setSelected(false);
			reverseVertical.setSelected(false);
			foreground.setSelected(false);
			repeatX.setValue(1);
			repeatY.setValue(1);
		} else {
			entityType.setText(p_entity.getEntityType().toString());
			spriteType.setSelectedIndex(spriteLib.indexOf(p_entity.getDesc()));
			spinX.setValue((int) p_entity.x % 16);
			spinY.setValue((int) p_entity.y % 16);
			reverseHorizontal.setSelected(p_entity.reverse.isHorizontal());
			reverseVertical.setSelected(p_entity.reverse.isVertical());
			foreground.setSelected(p_entity.isForeground());
			repeatX.setValue(p_entity.repeatX);
			repeatY.setValue(p_entity.repeatY);
		}
		updatingUI = false;
		entity = p_entity;
	}

	public void focusSprites(SpriteSelection<SpriteEntity> p_sel) {
		entities = null;
		sel = p_sel;
		if (p_sel == null) {
			focusSprite(null);
		} else {
			List<SpriteEntity> ent = p_sel.getElement();
			if (ent.size() == 1) {
				focusSprite(ent.get(0));
			} else {
				// We're dealing with a list
				entities = ent;
				focusSprite(null);
			}
		}
	}

	class SpriteFieldsListener implements ChangeListener, ActionListener {

		@Override
		public void stateChanged(ChangeEvent changeevent) {

			// If we are focusing on an existing sprite, then update his
			// attributes
			if (!updatingUI && sel != null) {
				Component comp = (Component) changeevent.getSource();
				if (comp instanceof JSpinner) {
					JSpinner spinner = (JSpinner) comp;
					int val = (Integer) spinner.getValue();
					if (comp == spinX) {
						sel.addX(val);
					} else if (comp == spinY) {
						sel.addY(val);
					} else if (comp == repeatX){
						sel.setRepeatX(val);
					} else if (comp == repeatY) {
						sel.setRepeatY(val);
					}
					// Wrap values (only for addX, addY)
					if (comp == spinX || comp == spinY) {
						if (val == 16) {
							spinner.setValue(0);
						} else if (val == -1) {
							spinner.setValue(15);
						}
					}
					manager.getZildoCanvas().setChangeSprites(true);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent actionevent) {
			if (!updatingUI && sel != null) {
				Component comp = (Component) actionevent.getSource();
				if (comp == spriteType) {
					String val = (String) ((JComboBox) comp).getSelectedItem();
					SpriteDescription desc = ZUtils.getField(val, spriteLib);
					entity.setDesc(desc);
				}
				manager.getZildoCanvas().setChangeSprites(true);
			}
		}
	}

}
