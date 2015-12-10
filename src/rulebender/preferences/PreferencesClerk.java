/**
 * This class is a static way to reference the preferences and other
 */
package rulebender.preferences;

import java.io.File;
import java.util.prefs.*;

import rulebender.Activator;
import rulebender.core.workspace.PickWorkspaceDialog;
import rulebender.preferences.views.MyFieldEditorPreferencePage;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import rulebender.preferences.PreferencesClerk;
import rulebender.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import rulebender.preferences.PreferencesClerk;
import rulebender.simulate.BioNetGenUtility;
import rulebender.Activator;

/*  This seems like a good time and place to document the datastructures that keep track of
   the location of the workspace and the location of BioNetGen.  
   
   The location of the workspace is stored in the native java preference mechanism.  Look 
   for the _preferences tag in the PickWorkspaceDialog().    You will see that certain fields
   of this tag will store the location of the current workspace and you will also see that
   a number of previously used workspaces is also stored in _preferences.
   
   For most purposeses, you can assume that the location of the workspace is stored in
   _preferences  You can make changes to it, and I believe that at the end of the run,
   the locations will be written to instanceLoc, to make the locations available for the next
   run of RuleBender.  The existence of the second database, instanceLoc, creates a certain
   amout of confusion,  but for most debugging tasks, you need only use  _preferences.
   
   The situation is somewhat more complicated with the location of BioNetGen.  This is 
   because two very different dialog boxes are used to update the directory information.
   The Preferences dialog box captures new settings that are typed by the user, and it
   updates a variable that is pointed to by  SIM_PATH.  Access to that internal database is
   provided only through the Preferences dialog box itself, and through methods.
   
   The decision was made to also permit the user to change the location of BioNetGen 
   in the Workspace selection dialog box.  This dialog box works with ordinary scalar
   variables, and there is now way to have it invoke a method when a variable needs to get
   changed.
   
   So we have two dialog boxes that somehow need to modify the same data, but the two of
   them have incompatible mechanisms for fetching and storing data.
   
   The resolution to this problem is to assume that the preference variable pointed to by
   SIM_PATH always has the correct and up-to-date information.  If that information is 
   needed to populate the PickWorkspace dialog box, then the scalars in the PickWorkspace
   class are first updated by calling the methods of PreferenceClerk class to get 
   current information.  After the PickWorkspace dialog box has been submitted by the
   user, methods are then called to push that data back to the PreferencesClerk class.
   This way the two databases are always kept in synch with one another.  It's crazy, but
   it works.
   
*/

public class PreferencesClerk 
{
  // This is a more "native" type of preference store, and values seem to be stored immediately
  // after a .put() call.  It seems like the PreferenceStore() package saves the values only
  // at shutdown, and not at all if the application restarts itself.	
  private static Preferences  _preferences   = Preferences.userNodeForPackage(PreferencesClerk.class);
  private static final String _upgrade_check = "upgradecheck";	
	
	// The name of the main BNG file.
	private static String BNGName = "BNG2.pl";

	// The path from the root directory to the main BNG file.
	private static String BNGPathFromRoot = "BioNetGen-2.2.6";

	// In an effort to keep version-specific data in one place, the RuleBender version number appears
	// here, along with the BioNetGen version number above.  It's worth pointing out, that the RuleBender
	// version number also appears in the plugins.xml file, the s_installation_packages.sh file, and 
	// the rulebender.product file, in two places.  At the moment, there doesn't seem to be a way to 
	// merge these things, so each of those files will need to be updated when preparing a new release 
	// of RuleBender.
	private static String RuleBenderVersionNumber = "2.1.0";	
	private static String RuleBenderVersion = "RuleBender-" + RuleBenderVersionNumber;	
	
	// Private constructor for static access only.
	private PreferencesClerk() {
		throw new AssertionError();
	}

	/**
	 * This method returns the path to the 'BNGName' file, but not including that
	 * file.
	 * 
	 * @return String path to 'BNGName'
	 */

	public static String getDefaultBNGPath() 
	{
//           return Activator.getDefault().getPreferenceStore().getString("SIM_PATH")
//	   + System.getProperty("file.separator") + BNGPathFromRoot;
		return  System.getProperty("user.dir")
		    + System.getProperty("file.separator")
                    + BNGPathFromRoot;
	}
	/**
	 * Returns the name of the main BNG file in 'BNGName'.
	 * 
	 * @return String name of main bng file.
	 */
	public static String getBNGName() {
		return BNGName;
	}

	/**
	 * Returns the user supplied root path to the BioNetGen directory. This does
	 * not include the path from the root to 'BNGName' or 'BNGName' itself.
	 * 
	 * @return String root path to BNG directory.
	 */
	public static String getBNGRoot() {
      return  System.getProperty("user.dir");
	}

	/**
	 * Returns the RuleBender version.
	 * 
	 * @return String RuleBender version.
	 */
	public static String getRuleBenderVersionNumber() {
	      return  RuleBenderVersionNumber;
		}
	public static String getRuleBenderVersion() {
      return  RuleBenderVersion;
	}



	
	
	public static String getUpgradeCheck() {
	  return _preferences.get(_upgrade_check, null);
	}
	
	public static void setUpgradeCheck(String ssss) {
      _preferences.put(_upgrade_check, ssss);
      return;
	}
	

	
	
	public static String getOutputSetting() {
      return Activator.getDefault().getPreferenceStore().getString("OUTPUT_SETTING");
	}
	
	public static String setOutputSetting(String ssss) {
      Activator.getDefault().getPreferenceStore().setValue("OUTPUT_SETTING",ssss);
      return "Ret_String";
	}
	
	
	public static String getMaxGraphDensity() {
	  return Activator.getDefault().getPreferenceStore().getString("MAX_GRAPH_COLUMNS");
	}
		
    public static String setMaxGraphDensity(String ssss) {
	  Activator.getDefault().getPreferenceStore().setValue("MAX_GRAPH_COLUMNS",ssss);
	  return "Ret_String";
    }
		
		
	
	/**
	 * Returns the entire path from the root to the 'BNGName' file.
	 * 
	 * @return String path to main bng file.
	 */
	
	public static String getUserBNGPath() {
		return Activator.getDefault().getPreferenceStore().getString("SIM_PATH");
	}
	public static String getFullUserBNGPath() {
		return getUserBNGPath()  + System.getProperty("file.separator") + BNGName;
	}
	
	public static String getFullDefaultBNGPath() {
		return getDefaultBNGPath() + System.getProperty("file.separator") + BNGName;
	}
	
	
	/*  This returns either a valid directory name with no file.separator, or No_Valid_Path_. */
	public static String getBNGPath() {
		boolean prereq = BioNetGenUtility.checkPreReq();
		if (prereq) {		  
          // Check to see that RuleBender uses the latest BioNetGen for the first run.  The effect of 
          // this check is to make sure that the PreferenceStore does not cause RuleBender to use an 
          // older version. After this, the user can reset the location of BioNetGen. 
          if (getUpgradeCheck() != null) { 
          if (getUpgradeCheck().equals(BNGPathFromRoot)) { 
  		    String     bngPath2  = PreferencesClerk.getFullUserBNGPath();		 
		    boolean bng2 = validateBNGPath(bngPath2);
		    if (bng2) { 
  		      bngPath2  = PreferencesClerk.getUserBNGPath();		 
			  String mm = PickWorkspaceDialog.setLastSetBioNetGenDirectory(PreferencesClerk.getUserBNGPath());
	          Activator.getDefault().getPreferenceStore().setValue("SIM_PATH",PreferencesClerk.getUserBNGPath());
              setUpgradeCheck(BNGPathFromRoot);
	          return bngPath2; 
	        }
          }
          }
	      
	  	  String     bngPath   = PreferencesClerk.getFullDefaultBNGPath();
	      boolean bng  = validateBNGPath(bngPath);
	      if (bng) { 
  	  	    bngPath = PreferencesClerk.getDefaultBNGPath();
			String mm = PickWorkspaceDialog.setLastSetBioNetGenDirectory(PreferencesClerk.getDefaultBNGPath());
            Activator.getDefault().getPreferenceStore().setValue("SIM_PATH",PreferencesClerk.getDefaultBNGPath());
            setUpgradeCheck(BNGPathFromRoot);
            return bngPath; 
          }
		}

		return "No_Valid_Path_";  //  This is not a good way to handle the situation, but it's
		               //  better than what we had before.
	}
	public static String getFullBNGPath() {
		return  getBNGPath() + System.getProperty("file.separator") + BNGName;
	}

	
	private static boolean validateBNGPath(String path) {
		if ((new File(path)).exists()) {
			return true;
		}
		return false;
	}

	
	
	public static OS getOS() {
		String stemp = System.getProperty("os.name");

		if (stemp.contains("Windows") || stemp.contains("WINDOWS")
		    || stemp.contains("windows")) {
			return OS.WINDOWS;
		} else if (stemp.contains("Mac") || stemp.contains("MAC")
		    || stemp.contains("mac")) {
			return OS.OSX;
		} else {
			return OS.LINUX;
		}

	}

	public static String getWorkspace() {
		return PickWorkspaceDialog.getLastSetWorkspaceDirectory();
	}
	public static String getBioNetGen() {
		return PickWorkspaceDialog.getLastSetBioNetGenDirectory();
	}
}
