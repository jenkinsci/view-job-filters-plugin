package hudson.views;

public abstract class AbstractScmValuesProvider implements ScmValuesProvider {

	private boolean checked = false;
	private boolean loaded;
	
	@Override
	public boolean checkLoaded() {
		if (!checked) {
			try {
				getPluginTesterClass();
				loaded = true;
			} catch (Throwable t) {
				loaded = false;
			}
			checked = true;
			return loaded;
		} else {
			return loaded;
		}
	}

}
