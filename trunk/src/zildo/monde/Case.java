package zildo.monde;
//////////////////////////////////////////////////////////////////////
// Case
//////////////////////////////////////////////////////////////
// Class defining a map element : 16x16 sized position.
//
//
//////////////////////////////////////////////////////////////////////


public class Case {

	private int n_motif;
	private int n_banque;
	private int n_motif_masque;
	private int n_banque_masque;
	private int n_tile;	

	public int getN_motif() {
		return n_motif;
	}

	public void setN_motif(int n_motif) {
		this.n_motif = n_motif;
	}

	public int getN_banque() {
		return n_banque;
	}

	public void setN_banque(int n_banque) {
		this.n_banque = n_banque;
	}

	public int getN_motif_masque() {
		return n_motif_masque;
	}

	public void setN_motif_masque(int n_motif_masque) {
		this.n_motif_masque = n_motif_masque;
	}

	public int getN_banque_masque() {
		return n_banque_masque;
	}

	public void setN_banque_masque(int n_banque_masque) {
		this.n_banque_masque = n_banque_masque;
	}

	public int getN_tile() {
		return n_tile;
	}

	public void setN_tile(int n_tile) {
		this.n_tile = n_tile;
	}

	public Case() {
	}

	public int getAnimatedMotif(int compteur_animation)
	{
		int motif=this.n_motif;
		switch (this.n_banque)
		{
		// On g�re les sprites anim�s
			case 0:
			// L'eau
				if (motif>=108 && motif<=130) {
					if (compteur_animation > 40)
						motif+=100;
					else if (compteur_animation > 20)
						motif+=100+23;
				} else if (motif==52 || motif==53) {
					//Les fleurs
					if (compteur_animation > 40)
						motif+=3;
					else if (compteur_animation > 20)
						motif+=111;
				}
				break;
	
	
			case 1:
				if (motif>=235 && motif<=237)
					motif+=(compteur_animation / 20)*3;
				break;
	
			case 2:
				switch (motif) 
				{
					case 174:    
						if (compteur_animation>=20)
							motif=175+(compteur_animation / 20);
						break;
					case 142:
						if (compteur_animation>=40)
							motif=178;
						else if (compteur_animation>=20)
							motif=194;
						break;
					case 144:
						if (compteur_animation>=40)
							motif=179;
						else if (compteur_animation>=20)
							motif=195;
						break;
				    case 235:
						motif=235+(compteur_animation / 20);
				}
				break;
			case 3:
			// L'eau dans les grottes/palais
				if (motif==78)
					motif=78+(compteur_animation / 20);
				break;
	
			case 5:
				// FORET3.DEC animation d'eau suppl�mentaire
				if (motif>=59 && motif<=61)
					motif+=(compteur_animation / 20)*3;
				break;
		}
	
		// Return computed motif
		return motif;
	}
}