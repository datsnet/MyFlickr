import android.app.Application;
import android.os.StrictMode;

/**
 * 
 */

/**
 * @author atsumi
 *
 */
public class StrictModeTestApplication extends Application {
	private static final boolean DEVELOPER_MODE = true;
	
	@Override
	public void onCreate() {
		if (DEVELOPER_MODE) {
			StrictMode.enableDefaults();
		}
		super.onCreate();
	}
}
