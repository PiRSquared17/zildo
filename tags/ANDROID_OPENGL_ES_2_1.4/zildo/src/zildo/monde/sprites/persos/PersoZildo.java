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

package zildo.monde.sprites.persos;

import java.util.ArrayList;
import java.util.List;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.sound.BankSound;
import zildo.client.stage.SinglePlayer;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.ui.UIText;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.items.Item;
import zildo.monde.items.ItemCircle;
import zildo.monde.items.ItemKind;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoDescription;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementArrow;
import zildo.monde.sprites.elements.ElementBomb;
import zildo.monde.sprites.elements.ElementBoomerang;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.persos.action.HealAction;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.sprites.utils.ShieldEffect;
import zildo.monde.sprites.utils.ShieldEffect.ShieldType;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.MultiplayerManagement;
import zildo.server.Server;

public class PersoZildo extends Perso {

	private SpriteEntity pushingSprite;
	private int acceleration; // from 0 to 10

	private Angle sightAngle; // For boomerang

	private int touch; // number of frames zildo is touching something without
						// moving

	private boolean inventoring = false;
	private boolean buying = false;
	public ItemCircle guiCircle;
	private List<Item> inventory;
	private ShieldEffect shieldEffect;

	private ZildoOutfit outfit;

	private int heartQuarter;

	// Linked elements
	Element shield;
	Element feet;

	private SpriteEntity boomerang;
	private int quadDuration;

	// Sequence for sprite animation
	static int seq_1[] = { 0, 1, 2, 1 };
	static int seq_2[] = { 0, 1, 2, 1, 0, 3, 4, 3 };

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	public PersoZildo(int p_id) { // Only used to create Zildo on a client
		id = p_id;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// PersoZildo
	// /////////////////////////////////////////////////////////////////////////////////////
	// Return a perso named Zildo : this game's hero !
	// with a given location.
	// /////////////////////////////////////////////////////////////////////////////////////
	public PersoZildo(int p_posX, int p_posY, ZildoOutfit p_outfit) {
		super();
		this.setName("Zildo");

		// We could maybe put that somewhere else
		outfit = p_outfit;
		this.setNBank(SpriteBank.BANK_ZILDO);
		setNSpr(0);

		setX(p_posX); // 805); //601-32;//-500);
		setY(p_posY); // 973); //684+220;//-110);
		setAngle(Angle.NORD);
		setPos_seqsprite(-1);
		setMouvement(MouvementZildo.VIDE);
		setInfo(PersoInfo.ZILDO);
		setMaxpv(6);
		setPv(6);
		setAlerte(false);
		setCompte_dialogue(0);
		setMoney(0);
		setCountKey(0);
		pushingSprite = null;

		shield = new Element(this);
		shield.setX(getX());
		shield.setY(getY());
		shield.setNBank(SpriteBank.BANK_ZILDO);
		shield.setNSpr(103); // Assign initial nSpr to avoid 'isNotFixe'
								// returning TRUE)

		shadow = new Element(this);
		shadow.setNBank(SpriteBank.BANK_ZILDO);
		shadow.setNSpr(103);

		feet = new Element(this);
		feet.setNBank(SpriteBank.BANK_ZILDO);
		feet.setNSpr(ZildoDescription.WATFEET1.getNSpr());

		shieldEffect = null;

		addPersoSprites(shield);
		addPersoSprites(shadow);
		addPersoSprites(feet);

		// weapon=new Item(ItemKind.SWORD);
		inventory = new ArrayList<Item>();
		// inventory.add(weapon);

		setSpeed(Constantes.ZILDO_SPEED);
	}

	/**
	 * Reset any effects zildo could have before he dies.
	 */
	public void resetForMultiplayer() {
		weapon = new Item(ItemKind.SWORD);
		inventory = new ArrayList<Item>();
		inventory.add(weapon);

		MultiplayerManagement.setUpZildo(this);

		if (shieldEffect != null) {
			shieldEffect.kill();
			shieldEffect = null;
		}
		quadDuration = 0;

		inWater = false;
		inDirt = false;
	}

	@Override
	public boolean isZildo() {
		return true;
	}

	@Override
	public void initPersoFX() {
		setSpecialEffect(EngineFX.NO_EFFECT);
	}

	public SpriteEntity getPushingSprite() {
		return pushingSprite;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// attack
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void attack() {
		boolean outOfOrder = false;
		if (weapon == null) {
			return; // No weapon ? No attack
		}
		switch (weapon.kind) {
		case SWORD:
			EngineZildo.soundManagement.broadcastSound(BankSound.ZildoAttaque, this);
			setMouvement(MouvementZildo.ATTAQUE_EPEE);
			setAttente(6 * 2);
			break;
		case BOW:
			if (attente == 0) {
				if (countArrow > 0) {
					setMouvement(MouvementZildo.ATTAQUE_ARC);
					setAttente(4 * 8);
				} else {
					outOfOrder = true;
				}
			}
			break;
		case BOOMERANG:
			if (attente == 0 && (boomerang == null || !boomerang.isVisible())) {
				setMouvement(MouvementZildo.ATTAQUE_BOOMERANG);
				// Sightangle should not be null, but I got it once in
				// multiplayer test
				boomerang = new ElementBoomerang(sightAngle == null ? Angle.NORD : sightAngle, (int) x, (int) y,
						(int) z, this);
				EngineZildo.spriteManagement.spawnSprite(boomerang);
				setAttente(16);
			}
			break;
		case BOMB:
			if (attente == 0) {
				if (countBomb > 0) {
					Element bomb = new ElementBomb((int) x, (int) y, 0, this);
					EngineZildo.spriteManagement.spawnSprite(bomb);
					countBomb--;
					setAttente(1);
				} else {
					outOfOrder = true;
				}
			}
			break;
		case FLASK_RED:
			if (getPv() == getMaxpv()) { // If Zildo already has full life, do
											// nothing
				outOfOrder = true;
			} else {
				action = new HealAction(this, 6); // Give back 6 half-hearts
				removeItem(ItemKind.FLASK_RED);
				weapon = null;
			}
			break;
		case MILK:
			String sentence = UIText.getGameText("milk.action");
			EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new ScriptAction(sentence));
			break;
		case FLUT:
			EngineZildo.soundManagement.playSound(BankSound.Sort, this);
			break;
		}
		if (outOfOrder) {
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
		}
		if (weapon == null) {
			weapon = inventory.get(0);
		}

	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// manageCollision
	// /////////////////////////////////////////////////////////////////////////////////////
	// -create collision zone for Zildo
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void manageCollision() {
		if (getMouvement() == MouvementZildo.ATTAQUE_EPEE) {
			// La collision avec l'�p�e de Zildo}
			double cx, cy, beta;
			int rayon;
			cx = getX();
			cy = getY();
			rayon = 4;
			beta = (2.0f * Math.PI * this.getAttente()) / (7 * Constantes.speed);

			switch (this.getAngle()) {
			case NORD:
				beta = beta + Math.PI;
				cy = cy - 16;
				break;
			case EST:
				beta = -beta + Math.PI / 2;
				cy = cy - 4;
				break;
			case SUD:
				cy = cy - 4;
				break;
			case OUEST:
				beta = beta + Math.PI / 2;
				cy = cy - 4;
				cx = cx - 4;
				break;
			}
			if (angle.isHorizontal()) {
				cx = cx + 16 * Math.cos(beta);
				cy = cy + 16 * Math.sin(beta);
			} else {
				cx = cx + 12 * Math.cos(beta);
				cy = cy + 12 * Math.sin(beta);
			}

			// Add this collision record to collision engine
			// Damage type: blunt at start, and then : cutting front
			DamageType dmgType = DamageType.BLUNT;
			if (attente < 6) {
				dmgType = DamageType.CUTTING_FRONT;
			}
			Collision c = new Collision((int) cx, (int) cy, rayon, Angle.NORD, this, dmgType, null);
			EngineZildo.collideManagement.addCollision(c);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// beingWounded
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN : cx,cy : enemy's position
	// /////////////////////////////////////////////////////////////////////////////////////
	// Invoked when Zildo got wounded by any enemy.
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void beingWounded(float cx, float cy, Perso p_shooter, int p_damage) {

		if (mouvement == MouvementZildo.SAUTE || inventoring) {
			return;
		}
		// Project Zildo away from the enemy
		float diffx = getX() - cx;
		float diffy = getY() - cy;
		float norme = (float) Math.sqrt((diffx * diffx) + (diffy * diffy));
		if (norme == 0.0f) {
			norme = 1.0f; // Pour �viter le 'divide by zero'
		}
		// Et on l'envoie !
		setPx(8 * (diffx / norme));
		setPy(8 * (diffy / norme));

		if (p_shooter != null && p_shooter.getQuel_deplacement().isAlertable()) {
			p_shooter.setAlerte(true);
		}
		
		beingWounded(p_shooter, p_damage);
	}

	public void beingWounded(Perso p_shooter, int p_damage) {
		// Si Zildo a quelque chose dans les mains, on doit le laisser tomber
		if (getEn_bras() != null) {
			getEn_bras().az = -0.07f;
			if (getMouvement() == MouvementZildo.FIERTEOBJET) {
				getEn_bras().dying = true;
			}
			setEn_bras(null);
		}
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoTouche, this);

		setMouvement(MouvementZildo.TOUCHE);
		setWounded(true);
		this.setPv(getPv() - p_damage);

		if (guiCircle != null) {
			guiCircle.kill();
			inventoring = false;
			guiCircle = null;
		}

		if (getDialoguingWith() != null) {
			getDialoguingWith().setDialoguingWith(null);
			setDialoguingWith(null); // End dialog
			EngineZildo.dialogManagement.stopDialog(Server.getClientFromZildo(this), true);
		}

		boolean die = getPv() <= 0;
		if (die) {
			die(false, p_shooter);
		}
	}

	/**
	 * Zildo is dead ! Send messages and respawn (in multiplayer deathmatch)
	 */
	@Override
	public void die(boolean p_link, Perso p_shooter) {
		if (EngineZildo.game.multiPlayer) {
			super.die(p_link, p_shooter);
			EngineZildo.multiplayerManagement.kill(this, p_shooter);
		} else {
			// Game over
			pos_seqsprite = 0;
			EngineZildo.scriptManagement.execute("death");
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// stopBeingWounded
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void stopBeingWounded()
	{
		setMouvement(MouvementZildo.VIDE);
		setCompte_dialogue(64); // Temps d'invulnerabilit� de Zildo
		setPx(0.0f);
		setPy(0.0f);
		setSpecialEffect(EngineFX.NO_EFFECT);
	}


	final int decalxSword[][] = {
			{ 1, -1, -1, -5, -10, -13 }, { 0, 2, 3, 2, 1, 1 },
			{ -5, -6, -4, -4, -4, -4 }, { -2, -12, -18, -14, -13, -8 } };
	final int decalySword[][] = {
			{ 2, -6, -11, -6, -3, 1 }, { 1, 0, 3, 3, 2, 2 },
			{ 1, 3, 3, 6, 3, 3 }, { 1, 0, 3, 3, 2, 2 } };

	final int decalxBow[][] = {
			{ -2, -5, -5 }, { 0, 0, 0 }, { 0, 0, 0 }, { -1, -3, -4 }
	};
	final int decalyBow[][] = {
			{ 2, 3, 2 }, { 1, 2, 1 }, { 3, 2, 2 }, { 1, 2, 1 }
	};
	final int decalboucliery[] = { 0, 0, 0, -1, -2, -1 };
	final int decalbouclier2y[] = { 0, -1, -1, 0, -1, -1, 0, 0 };
	final int decalbouclier3y[] = { 0, 0, -1, -2, 0, -1, 0, 0 };

	
	// /////////////////////////////////////////////////////////////////////////////////////
	// animate
	// /////////////////////////////////////////////////////////////////////////////////////
	// Manage all things related to Zildo display : shield, shadow, feets, and
	// object taken.
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void animate(int compteur_animation)
	{
		super.animate(compteur_animation);

		// If zildo's dead, don't display him
		if (getPv() <= 0) {
			if (EngineZildo.game.multiPlayer) {
				setVisible(false);
				return;
			} else {
				Point zPos = getCenteredScreenPosition();
				Zildo.pdPlugin.getFilter(CircleFilter.class).setCenter(zPos.x, zPos.y);
			}
		}

		if (getEn_bras() != null && getEn_bras().dying) {
			setEn_bras(null);
		}

		// Get zildo
		float xx = x;
		float yy = y;

		if (compte_dialogue != 0) {
			compte_dialogue--;
			if (compte_dialogue == 0) {
				setWounded(false);
			}
		}

		if (mouvement == MouvementZildo.SAUTE) {
			moveJump();
		}
		SpriteEntity pushedEntity = getPushingSprite();

		if (px != 0.0f || py != 0.0f) {
			// Zildo being hurt !
			xx += px;
			yy += py;
			px *= 0.8f;
			py *= 0.8f;
			if (pv > 0 && Math.abs(px) + Math.abs(py) < 0.2f) {
				stopBeingWounded();
			}
			Pointf p = tryMove(xx, yy);
			x = p.x;
			y = p.y;
		} else if (getMouvement() == MouvementZildo.POUSSE && pushedEntity != null) {
			// Zildo est en train de pousser : obstacle bidon ou bloc ?

			if (pushedEntity.getEntityType().isElement()) {
				Element pushedElement = (Element) pushedEntity;
				if (pushedElement.isPushable()) {
					pushedElement.moveOnPush(getAngle());
					// Break link between Zildo and pushed object
					pushSomething(null);
					// Trigger an object being pushed
					TriggerElement trig = TriggerElement.createPushTrigger(pushedElement.getName(), getAngle());
					EngineZildo.scriptManagement.trigger(trig);
				}
			}
		}

		// Get variables to reduce code amount
		Element en_bras = getEn_bras();

		// Default : invisible
		shadow.setVisible(false);
		feet.setVisible(inWater || inDirt);
		shield.setVisible(false);

		if (isQuadDamaging()) {
			if (shieldEffect == null) {
				shieldEffect = new ShieldEffect(this, ShieldType.REDBALL);
			}
			setSpecialEffect(EngineFX.QUAD);
		} else if (shieldEffect != null) {
			setSpecialEffect(EngineFX.NO_EFFECT);
			shieldEffect.kill();
			shieldEffect = null;
		}
		// Shield effect animation
		if (shieldEffect != null) {
			shieldEffect.animate();
		}

		// Corrections , d�calages du sprite
		if (angle == Angle.EST) {
			xx -= 2;
		} else if (angle == Angle.OUEST) {
			xx += 2;
		}

		switch (mouvement) {
		// Bouclier
		case VIDE:
			if (hasItem(ItemKind.SHIELD)) {
				shield.setForeground(false);
				switch (angle) {
				case NORD:
					shield.setX(xx + 8);
					shield.setY(yy + 2);
					shield.setZ(5 - 1 - decalbouclier3y[nSpr % 8]);
					shield.setNSpr(103);
					shield.setNBank(SpriteBank.BANK_ZILDO);
					break;
				case EST:
					shield.setX(xx + 9); // PASCAL : +10
					shield.setY(yy - 2 + decalbouclier2y[(nSpr - ZildoDescription.RIGHT_FIXED.ordinal()) % 8]);
					shield.setZ(0.0f);
					shield.setNSpr(104);
					shield.setNBank(SpriteBank.BANK_ZILDO);
					break;
				case SUD:
					shield.setX(xx - 4); // PASCAL : -3)
					shield.setY(yy + 4);
					shield.setZ(1 + 1 - decalboucliery[(nSpr - ZildoDescription.DOWN_FIXED.ordinal()) % 6]);
					shield.setNSpr(105);
					shield.setNBank(SpriteBank.BANK_ZILDO);
					break;
				case OUEST:
					shield.setX(xx - 8);
					shield.setY(yy - 2 + decalbouclier2y[(nSpr - ZildoDescription.LEFT_FIXED.ordinal()) % 8]);
					shield.setZ(0.0f);
					shield.setNSpr(106);
					shield.setNBank(SpriteBank.BANK_ZILDO);
					break;
				}
				shield.setVisible(true);
			}
			break;

		case BRAS_LEVES:
			yy++;
			if (angle.isVertical()) {
				yy++;
			}
			if (en_bras != null) {
				en_bras.setX(xx + 1);
				en_bras.setY(yy + 3);
				en_bras.setZ(17);
			}
			break;
		case SOULEVE:
			yy += 3;
			break;
		case TIRE:
			if (angle.isHorizontal()) {
				yy += 1;
			} else {
				if (angle == Angle.NORD) {
					yy += 3;
				} else {
					yy += 4;
				}
			}
			if (nSpr == 47) {
				xx -= 3;
			}
			break;

		case POUSSE:
			yy += 1;
			if (angle == Angle.NORD) {
				yy += 1;
			} else if (angle == Angle.SUD) {
				yy += 3;
			}
			break;

		case ATTAQUE_EPEE:
			if (!angle.isDiagonal()) {
				xx += decalxSword[angle.value][nSpr - (54 + 6 * angle.value)];
				yy += decalySword[angle.value][nSpr - (54 + 6 * angle.value)];
			}
			shield.setVisible(false);
			break;

		case ATTAQUE_ARC:
			if (!angle.isDiagonal()) {
				xx += decalxBow[angle.value][nSpr - (108 + 3 * angle.value)];
				yy += decalyBow[angle.value][nSpr - (108 + 3 * angle.value)];
			}
			shield.setVisible(false);
			break;
		case TOUCHE:
			nSpr = 78 + angle.value;
			break;

		case SAUTE:
			// Zildo est en train de sauter, on affiche l'ombre � son arriv�e

			shadow.setX(posShadowJump.x); // (float) (xx-ax)); //-6;)
			shadow.setY(posShadowJump.y); // (float) (yy-ay)-3);
			shadow.setNSpr(2);
			shadow.setNBank(SpriteBank.BANK_ELEMENTS);
			shadow.setZ(0);
			shadow.setVisible(true);
			shield.setVisible(false);

			break;

		case FIERTEOBJET:
			nSpr = ZildoDescription.ARMSRAISED.ordinal();
			yy++;
			break;
		case MORT:
			yy+=9;
			xx-=2;
			break;
		}

		// On affiche Zildo
		feet.setX(x + (angle.isVertical() || angle == Angle.OUEST ? 1 : 0));
		feet.setY(y + 9 + 1);
		feet.setZ(3);
		feet.setAddSpr((compteur_animation / 6) % 3);
		if (inWater) {
			feet.setNSpr(ZildoDescription.WATFEET1.getNSpr());
		} else if (inDirt) {
			feet.setNSpr(ZildoDescription.DIRT1.getNSpr());
			feet.setY(feet.getY() - 3);
		}
		feet.setForeground(false);

		if (pv > 0) {
			boolean touche = (mouvement == MouvementZildo.TOUCHE || getCompte_dialogue() != 0);
			// Zildo blink
			touche = (touche && ((compteur_animation >> 1) % 2) == 0);
			visible = !touche;
			for (Element elem : persoSprites) { // Blink linked elements too
				if (elem.isVisible()) {
					elem.setVisible(visible);
				}
			}
		} else {
			// Zildo should stay focused at die scene
			setSpecialEffect(EngineFX.FOCUSED);
		}

		// Ajustemenent
		xx -= 7;
		yy -= 21;

		if (isAlerte()) {
			// Zildo a les pieds dans l'eau
			// spriteManagement.aff_spriteplace(BANK_ZILDO,100+(compteur_animation
			// / 20),xx+1,yy+1);
		}

		if (mouvement == MouvementZildo.BRAS_LEVES)
		{
			// On affiche ce que Zildo a dans les mains

			// Corrections...
			if (en_bras != null) {
				int objX = (int) en_bras.getX();
				int objY = (int) en_bras.getY();
				int objZ = (int) en_bras.getZ();
				if (angle == Angle.EST) {
					objX++;
				} else if (angle == Angle.OUEST) {
					objX--;
				}
				int variation = seq_1[((getPos_seqsprite() % (4 * Constantes.speed)) / Constantes.speed)];

				en_bras.setX(objX);
				en_bras.setY(objY);
				en_bras.setZ(objZ - variation);
			}
		} else if (mouvement == MouvementZildo.SOULEVE)
		{
			// Si Zildo est en train de soulever un objet, on l'affiche
			// xx-=8;yy-=11;
			// if (angle==1) xx+=6;
			// else if (angle==3) xx-=6;
			// spriteManagement.aff_sprite(BANK_ELEMENTS,en_bras,xx,yy-8);
		}

		// GUI circle
		if (guiCircle != null) {
			guiCircle.animate();
			if (guiCircle.isReduced()) {
				inventoring = false;
				guiCircle = null;
			}
		}

		// Quad damage
		if (quadDuration > 0) {
			quadDuration--;
			if (quadDuration == 160) {
				EngineZildo.soundManagement.playSound(BankSound.QuadDamageLeaving, this);
			}
		}
		setAjustedX((int) xx);
		setAjustedY((int) yy);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// finaliseComportementPnj
	// /////////////////////////////////////////////////////////////////////////////////////
	// Manage character's graphic side, depending on the position in the
	// animated sequence.
	// /////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void finaliseComportement(int compteur_animation) {

		final int[] seq_zildoBow = { 0, 1, 2, 1 };
		reverse = Reverse.NOTHING;
		switch (getMouvement())
		{
		case VIDE:
			setSpr(ZildoDescription.getMoving(angle, ((pos_seqsprite + 1) % (8 * Constantes.speed)) / Constantes.speed));
			// setNSpr(angle.value*7 +
			// seq_zildoDeplacement[angle.value][((pos_seqsprite+1) %
			// (8*Constantes.speed)) / Constantes.speed]);
			break;
		case SAUTE:
			setNSpr(angle.value + 96);
			break;
		case BRAS_LEVES:
			if (angle.isVertical()) {
				setNSpr(angle.value * 4 + seq_2[(pos_seqsprite % (8 * Constantes.speed)) / Constantes.speed] + 28);
			} else {
				setNSpr(((angle.value - 1) / 2) * 8
						+ seq_1[(pos_seqsprite % (4 * Constantes.speed)) / Constantes.speed] + 33);
			}
			break;
		case SOULEVE:
			switch (angle) {
			case NORD:
				setNSpr(44);
				break;
			case EST:
				setNSpr(52);
				break;
			case SUD:
				setNSpr(48);
				break;
			case OUEST:
				setNSpr(53);
				break;
			}
			break;
		case TIRE:
			setNSpr(44 + 2 * angle.value + pos_seqsprite);
			break;
		case TOUCHE:
			setNSpr(78 + angle.value);
			break;
		case POUSSE:
			if (angle == Angle.NORD) {
				setNSpr(seq_2[(pos_seqsprite / 2 % (8 * Constantes.speed)) / Constantes.speed] + 82);
			} else {
				setNSpr(angle.value * 3 + seq_1[(pos_seqsprite / 2 % (4 * Constantes.speed)) / Constantes.speed] + 84);
			}
			break;
		case ATTAQUE_EPEE:
			setNSpr(angle.value * 6 + (((6 * 2 - getAttente() - 1) % (6 * 2)) / 2) + 54);
			break;
		case ATTAQUE_ARC:

			setNSpr(angle.value * 3 + seq_zildoBow[(((4 * 8 - getAttente() - 1) % (4 * 8)) / 8)] + 108);
			if (attente == 2 * 8) {
				EngineZildo.soundManagement.broadcastSound(BankSound.FlecheTir, this);
				Element arrow = new ElementArrow(angle, (int) x, (int) y, 0, this);
				EngineZildo.spriteManagement.spawnSprite(arrow);
				countArrow--;
			}
			break;
		case MORT:
			setNSpr(ZildoDescription.LAYDOWN.getNSpr());
			break;
		}

		if (outfit != null && nBank == SpriteBank.BANK_ZILDO) {
			setNBank(outfit.getNBank());
		}
	}

	/**
	 * Zildo take some goodies. It could be a heart, an arrow, or a weapon...
	 * 
	 * @param p_element
	 *            (can be null, if p_money is filled)
	 * @param p_money
	 *            >0 ==> Zildo gets some money
	 * @return boolean : TRUE=element should disappear / FALSE=element stays
	 */
	public boolean pickGoodies(Element p_element, int p_money) {
		// Effect on perso
		int money = this.getMoney();
		if (p_money != 0) { // Zildo gets some money
			setMoney(money + p_money);
			if (p_money > 0) {
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRecupArgent, this);
			} else {
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoGagneArgent, this);
			}
		} else {
			int elemNSpr=p_element.getNSpr();
			ElementDescription d = ElementDescription.fromInt(elemNSpr);
			if (d.isWeapon()) {
				pickItem(d.getItem(), p_element);
				return false;
			} else {
				// Automatic behavior (presentation text, ammos adjustments)
				EngineZildo.scriptManagement.automaticBehavior(this, null, d);
				switch (d) {
				case GREENMONEY1:
					setMoney(money + 1);
					break;
				case BLUEMONEY1:
					setMoney(money + 5);
					break;
				case REDMONEY1:
					setMoney(money + 20);
					break;
				case HEART:
				case HEART_LEFT:
					if (pv < maxpv) {
						pv = Math.max(pv+2, maxpv);
					}
					break;
				case ARROW_UP:
					countArrow += 5;
					break;
				case QUAD1:
					quadDuration = MultiplayerManagement.QUAD_TIME_DURATION;
					EngineZildo.multiplayerManagement.pickUpQuad();
					break;
				case BOMBS3:
					countBomb += 3;
					break;
				case KEY:
					countKey++;
					break;
				}
				// Sound
				BankSound toPlay = null;
				switch (d) {
				case GREENMONEY1:
				case BLUEMONEY1:
				case REDMONEY1:
					toPlay = BankSound.ZildoRecupArgent;
					break;
				case QUAD1:
					toPlay = BankSound.QuadDamage;
					break;
				case KEY:
					toPlay = BankSound.ZildoKey;
					break;
				case HEART_FRAGMENT:
					toPlay = BankSound.ZildoCoeur;
					heartQuarter++;
					break;
				case HEART:
				case HEART_LEFT:
				default:
					toPlay = BankSound.ZildoRecupCoeur;
					break;
				}
				if (toPlay != null) {	// Isn't it obvious ?
					EngineZildo.soundManagement.broadcastSound(toPlay, this);
				}
			}
		}
		return true;
	}

	/**
	 * Zildo picks something up (bushes, hen...) Object can be already on the
	 * map (hen), or we can spawn it there (bushes, jar).
	 * 
	 * @param objX
	 * @param objY
	 * @param d
	 *            sprite's description, in case no object is supplied
	 * @param object
	 *            the taken element
	 */
	@Override
	public void takeSomething(int objX, int objY, SpriteDescription d, Element object) {
		EngineZildo.soundManagement.broadcastSound(BankSound.ZildoRamasse, this);

		Element elem = object;
		if (object == null) {
			elem = new Element();
			elem.setNBank(d.getBank());
			elem.setNSpr(d.getNSpr());
		}
		elem.setScrX(objX);
		elem.setScrY(objY);
		elem.setX(objX);
		elem.setY(objY);
		elem.setZ(4);
		elem.setVisible(true);
		elem.flying = false;

		elem.setLinkedPerso(this); // Link to Zildo

		if (object == null) {
			EngineZildo.spriteManagement.spawnSprite(elem);
		}

		// On passe en position "soul�ve", et on attend 20 frames
		setMouvement(MouvementZildo.SOULEVE);
		setAttente(20);
		setEn_bras(elem);

	}

	/**
	 * Zildo throws what he got in his raised arms. (enBras)
	 */
	public void throwSomething() {
		// On jette un objet
		Element element = getEn_bras();
		setMouvement(MouvementZildo.VIDE);
		if (element != null) {
			// Element shouldn't be null, but it happened !
			setEn_bras(null);
			element.setLinkedPerso(null);
			element.setX(getX() + 1);
			element.setY(getY());
			element.setZ(21.0f + 1.0f);
			element.setVx(0.0f);
			element.setVy(0.0f);
			element.setVz(0.0f);
			element.setAx(0.0f);
			element.setAy(0.0f);
			element.setAz(-0.07f);
			element.setLinkedPerso(this); // Declare this element thrown by Zildo
											// (so it can't collide with him)
			element.setAngle(angle);
			element.flying = true;
			element.relativeZ = EngineZildo.mapManagement.getCurrentMap().readAltitude((int) x / 16, (int) y / 16);
	
			switch (getAngle()) {
			case NORD:
				element.setVy(-4.0f);
				element.setFy(0.04f);
				break;
			case EST:
				element.setVx(4.0f);
				element.setFx(0.04f);
				break;
			case SUD:
				element.setVy(4.0f);
				element.setFy(0.04f);
				break;
			case OUEST:
				element.setVx(-4.0f);
				element.setFx(0.04f);
				break;
			}
			EngineZildo.soundManagement.broadcastSound(BankSound.ZildoLance, this);
		}
	}

	/**
	 * Display Zildo's inventory around him
	 */
	public void lookInventory() {
		if (inventory.size() == 0) {
			// no inventory !
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
			return;
		}
		int sel = inventory.indexOf(weapon);
		if (sel == -1) {
			sel = 0;
		}
		lookItems(inventory, sel, this, false);
	}

	public int getIndexSelection() {
		return inventory.indexOf(getWeapon());
	}
	
	public void lookItems(List<Item> p_items, int p_sel, Perso p_involved, boolean p_buying) {
		inventoring = true;
		guiCircle = new ItemCircle(this);
		guiCircle.create(p_items, p_sel, p_involved, p_buying);
		buying = p_buying;
	}

	/**
	 * Zildo buy an item at a store. Check his money, and add item to his
	 * inventory if he has enough.
	 */
	public void buyItem() {
		Item item = guiCircle.getItemSelected();
		int remains = getMoney() - item.getPrice();
		if (remains < 0) {
			// Not enough money
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
		} else if (inventory.size() == 8) {
			// Too much items
			EngineZildo.soundManagement.playSound(BankSound.MenuOutOfOrder, this);
		} else {
			setMoney(getMoney() - item.getPrice());
			inventory.add(item);
			EngineZildo.soundManagement.playSound(BankSound.ZildoGagneArgent, this);
		}
	}

	public void closeInventory() {
		EngineZildo.soundManagement.playSound(BankSound.MenuIn, this);
		guiCircle.close(); // Ask for the circle to close
		if (!buying) {
			weapon = guiCircle.getItemSelected();
			Perso perso = getDialoguingWith();
			if (perso != null) {
				perso.setDialoguingWith(null);
				setDialoguingWith(null);
			}
		}
	}

	/**
	 * Directly add an item to the inventory
	 * 
	 * @param p_item
	 */
	public void addInventory(Item p_item) {
		inventory.add(p_item);
	}

	public boolean isInventoring() {
		return inventoring;
	}

	/**
	 * Zildo takes an item.
	 * 
	 * @param p_kind
	 * @param p_element
	 *            NULL if we have to spawn the element / otherwise, element
	 *            already is on the map.
	 */
	public void pickItem(ItemKind p_kind, Element p_element) {
		if (getEn_bras() == null) { // Doesn't take 2 items at 1 time
			addInventory(new Item(p_kind));
			attente = 40;
			mouvement = MouvementZildo.FIERTEOBJET;
			Element elem = p_element;
			if (elem == null) {
				elem = EngineZildo.spriteManagement.spawnElement(p_kind.representation,
						(int) x,
						(int) y, 0, Reverse.NOTHING, Rotation.NOTHING);
			}
			// Place item right above Zildo
			elem.x = x + 5;
			elem.y = y + 1;
			elem.z = 20f;
			setEn_bras(elem);
			EngineZildo.soundManagement.playSound(BankSound.ZildoTrouve, this);

			// Automatic behavior (presentation text, ammos adjustments)
			EngineZildo.scriptManagement.automaticBehavior(this, p_kind, null);

			// Adventure trigger
			if (!EngineZildo.game.multiPlayer) {
				TriggerElement trig = TriggerElement.createInventoryTrigger(p_kind);
				EngineZildo.scriptManagement.trigger(trig);
			}
		}
	}

	/**
	 * Zildo loose an item from his inventory.
	 * 
	 * @param p_kind
	 */
	public void removeItem(ItemKind p_kind) {
		int index = 0;
		for (Item i : inventory) {
			if (i.kind == p_kind) {
				inventory.remove(index);
				return;
			}
			index++;
		}
	}

	/**
	 * Return TRUE if Zildo has an item from given kind.
	 * 
	 * @param p_kind
	 * @return boolean
	 */
	public boolean hasItem(ItemKind p_kind) {
		for (Item i : inventory) {
			if (i.kind == p_kind) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Item getWeapon() {
		Item item = super.getWeapon();
		if (item == null && !inventory.isEmpty()) {
			// No weapon is selected => take the first one, if it exists
			// This case occurs only after game has just been loaded
			weapon = inventory.get(0);
			item = weapon;
		}
		return item;
	}
	/**
	 * Return all Zildo's inventory. Useful for saving a game.
	 * 
	 * @return List<Item>
	 */
	public List<Item> getInventory() {
		return inventory;
	}

	/**
	 * Zildo avance contre un SpriteEntity
	 * 
	 * @param object
	 */
	public void pushSomething(Element object) {
		if (object == null || object.isPushable()) {
			pushingSprite = object;
		}
		if (object != null && object.getDesc().getBank() == SpriteBank.BANK_GEAR) {
			((ElementGear) object).push(this);
		}
	}

	public int getTouch() {
		return touch;
	}

	public void setTouch(int touch) {
		this.touch = touch;
	}

	public boolean isAlive() {
		return getPv() > 0;
	}

	public void setSightAngle(Angle sightAngle) {
		this.sightAngle = sightAngle;
	}

	public boolean isQuadDamaging() {
		return quadDuration > 0;
	}
	
	public int getHeartQuarter() {
		return heartQuarter;
	}

	public void setHeartQuarter(int heartQuarter) {
		this.heartQuarter = heartQuarter;
	}
	
	int[] accels = new int[] { 0, 1, 1, 1, 2, 2, 3, 6, 8, 10, 10 };

	public float getAcceleration() {
		return accels[acceleration];
	}

	public void increaseAcceleration() {
		if (acceleration != 10) {
			acceleration += 1;
		}
	}

	public void decreaseAcceleration() {
		if (acceleration > 1) {
			acceleration -= 1;
		}
	}
}