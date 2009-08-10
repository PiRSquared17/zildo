package zildo.fwk;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.filter.BilinearFilter;
import zildo.fwk.filter.BlendFilter;
import zildo.fwk.filter.ScreenFilter;
import zildo.prefs.Constantes;

/**
 * Command class for screen filters:
 * -can combine several filters
 * -can active/disactive filters (all, or by class specifically)
 * 
 * -manage fade level (go from 0 to 255, and from 255 to 0)
 * 
 * @author Tchegito
 *
 */
public class FilterCommand {

	private List<ScreenFilter> filters;
	protected int fadeLevel;		// Integer between 0 and 255
	protected boolean asked_FadeIn;
	protected boolean asked_FadeOut;

	public FilterCommand() {
		filters=new ArrayList<ScreenFilter>();

		fadeLevel=0;
		asked_FadeIn = false;
		asked_FadeOut =false;
	}
	
    public void addFilter(ScreenFilter filter) {
        if (filter.isFBOSupported()) {
            filters.add(filter);
        }
    }
	
	public void doPreFilter() {
		for (ScreenFilter filter : filters) {
			if (filter.isActive()) {
				filter.preFilter();
			}
		}
	}

	public void doPostFilter() {
		for (ScreenFilter filter : filters) {
			if (filter.isActive()) {
				filter.postFilter();
			}
		}
	}

	/**
	 * Calculate fade level, and render active filters.
	 */
	public void doFilter() {
		boolean progressFadeLevel=true;
		for (ScreenFilter filter : filters) {
			if (filter.isActive()) {
				progressFadeLevel&=filter.renderFilter();
			}
		}

		if (progressFadeLevel) {
			// Evaluate fade level
			if (asked_FadeOut && fadeLevel < 255) {
				fadeLevel+=Constantes.FADE_SPEED;
			}
			if (asked_FadeIn && fadeLevel > 0) {
				fadeLevel-=Constantes.FADE_SPEED;
			}
		}

	}

	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeIn
	///////////////////////////////////////////////////////////////////////////////////////
	// When lights comes back
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeIn()
	{
		asked_FadeIn  = true;
		asked_FadeOut = false;
		active(null, false);
		//active(FadeFilter.class,true);
		active(BlendFilter.class,true);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeOut
	///////////////////////////////////////////////////////////////////////////////////////
	// When lights goes out
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeOut()
	{
		asked_FadeIn  = false;
		asked_FadeOut = true;
		active(null, false);
		//active(FadeFilter.class,true);
		//active(BlurFilter.class,true);
		active(BlendFilter.class,true);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeEnd
	///////////////////////////////////////////////////////////////////////////////////////
	// Fade is over, so put back in default position
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeEnd() {
		active(null, false);
		active(BilinearFilter.class, true);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isFadeOver
	///////////////////////////////////////////////////////////////////////////////////////
	// Returns TRUE wether fade operation is over, and set the 'ask' boolean to FALSE when
	// it's done.
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isFadeOver() {
		if (asked_FadeOut && fadeLevel >= 255) {
			fadeLevel=255;
			asked_FadeIn=false;
			return true;
		} else if (asked_FadeIn && fadeLevel <= 0) {
			fadeLevel=0;
			asked_FadeOut=false;
			fadeEnd();
			return true;
		}
		return false;
	}

	public int getFadeLevel() {
		return fadeLevel;
	}
	
	/**
	 * Active/Inactive all filters from given class.
	 * If 'clazz' is null, so all filters will be targeted.
	 * @param clazz
	 * @param activ
	 */
	public void active(Class<? extends ScreenFilter> clazz, boolean activ) {
		for (ScreenFilter filter : filters) {
			if (clazz == null || filter.getClass().equals(clazz)) {
				filter.setActive(activ);
			}
		}
	}
	
	public void cleanUp() {
		for (ScreenFilter filter : filters) {
			filter.cleanUp();
		}
	}
}
