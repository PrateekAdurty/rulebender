package rulebender;


import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import rulebender.core.workspace.PickWorkspaceDialog;
import rulebender.preferences.PreferencesClerk;
import rulebender.prereq.PreReqChecker;

/**
 * This class was generated by Eclipse when the rulebender plugin was created
 *  and it controls all aspects of the application's execution.  The only thing that
 *  has been altered here is how the workspace is managed.  
 *  
 *  See /rulebender/src/rulebender/core/workspace/PickWorkspaceDialog.java
 *  
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) 
	{
		// This is how to change the name in the title bar of the application.  
		// There may be a better way to do it, but this worked.  
		Display.setAppName("RuleBender");
	 	
	    // the old Eclipse generated code 
        Display display = PlatformUI.createDisplay(); 

        
        // Get the workspace and only continue if it is set correctly.
	    if(!selectWorkspace(display))
	    {
	    	System.exit(0); 
            return IApplication.EXIT_OK; 
	    }
	    
	    // Default.  Do not touch.
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			// This seems like the best place to copy the name of the BioNetGen directory
			// from the PreferencesClerk database into the _preferences database.
			String mm = PickWorkspaceDialog.setLastSetBioNetGenDirectory(PreferencesClerk.getUserBNGPath());
			display.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
	
	private boolean selectWorkspace(Display display)
	{		
		 // fetch the Location that we will be modifying 
	    Location instanceLoc = Platform.getInstanceLocation(); 
	    
	    	
	    try { 
			 
	        // get what the user last said about remembering the workspace location 
	        boolean remember = PickWorkspaceDialog.isRememberWorkspace(); 
	 
	        // get the last used workspace location 
	        String lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory(); 
	 
	        // Are we restarting from a switched workspace?
	        if(PickWorkspaceDialog.didSwitchRestart())
		    {
	        	// Set remember to true no matter what. 
	        	remember = true;
		    }
	        
	        // if we have a "remember" but no last used workspace, it's not much to remember 
	        if (remember && (lastUsedWs == null || lastUsedWs.length() == 0)) 
	        { 
	            remember = false; 
	        } 
	 
	        // check to ensure the workspace location is still OK 
	        if (remember) 
	        { 
	            // if there's any problem whatsoever with the workspace, force a dialog which in its turn will tell them what's bad 
	            String ret = PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(), lastUsedWs, false, false); 
	            if (ret != null) 
	            { 
	            	remember = false; 
	            } 
	 
	        } 
	 
	        // if we don't remember the workspace, show the dialog 
	        if (!remember) { 
	            PickWorkspaceDialog pwd = new PickWorkspaceDialog(false, null); 
	            int pick = pwd.open(); 
	 
	            // if the user cancelled, we can't do anything as we need a workspace, so in this case, we tell them and exit 
	            if (pick == Window.CANCEL) { 
	            if (pwd.getSelectedWorkspaceLocation()  == null) { 
	                MessageDialog.openError(display.getActiveShell(), "Error", 
	                    "The application can not start without a workspace root and will now exit."); 
	               return false;
	            } 
	            } 
	            else { 
	            // tell Eclipse what the selected location was and continue 
	            instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()), false); 
	            //  The dialog for picking the workspace will not be shown, so copy the path to BioNetGen.
				Activator.getDefault().getPreferenceStore().setDefault("SIM_PATH",PickWorkspaceDialog.getLastSetBioNetGenDirectory());
	            } 
	        } 
	        else { 
	            // set the last used location and continue 
	            instanceLoc.set(new URL("file", null, lastUsedWs), false);
	            //  The dialog for picking the workspace will not be shown, so copy the path to BioNetGen.
				Activator.getDefault().getPreferenceStore().setDefault("SIM_PATH",PickWorkspaceDialog.getLastSetBioNetGenDirectory());
	        }   
	    } 
	    catch (Exception err) 
	    { 
	       
	    }
	    
	    
	    /*
        String     bngPath   = PreferencesClerk.getFullDefaultBNGPath();
        String     bngPath2  = PreferencesClerk.getFullUserBNGPath();
       // String bngPath = bng.toString();

//      System.out.println(" bngPath " + bngPath);
//      System.out.println(" bngPath2 " + bngPath2);

       boolean prereq = BioNetGenUtility.checkPreReq();
       boolean bng  = validateBNGPath(bngPath);
       boolean bng2 = validateBNGPath(bngPath2);

	    
       if (bng || bng2) {
//         System.out.println("\nBioNetGen has been located on your system. ");
             TableItem item = new TableItem(table, SWT.NONE);
       //      item.setText(0, "BioNetGen Found");
       //      item.setText(1, "Level 2.2.6");
         } else {
         TableItem item = new TableItem(table, SWT.NONE);
       //          item.setText(0, "BioNetGen Not Found");
       //          item.setText(1, "Please Click OK and Follow Instructions");
         }

          */
		
		return true; 
	}
}
