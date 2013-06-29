package hudson.views;

public class PluginHelperUtils {

	public static interface PluginHelperTestable {
		@SuppressWarnings("unchecked")
		Class getPluginTesterClass();
	}
	
	public static <T extends PluginHelperTestable> T validateAndThrow(T testable) {
		if (testable == null) {
			// this condition is fine - it means the calling code tried to construct but got an exception
			return null;
		}
		// this line should throw an exception if the test class doesn't exist
		testable.getPluginTesterClass();
		
		// if we get here, it means the plugin is installed, and the helper is good to go
		return testable;
	}
	
}
