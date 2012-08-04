package rulebender.simulate.parameterscan;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

import rulebender.core.utility.Console;
import rulebender.simulate.CommandRunner;
import rulebender.simulate.SimulationErrorException;

public class ParameterScanJob extends Job
{

  private String m_absoluteFilePath;
  private String m_relativeFilePath;
  private String m_bngPath;
  private String m_scriptFullPath;
  private ParameterScanData m_data;
  private String m_resultsPath;

  public ParameterScanJob(String name, IFile iFile, String bngPath,
      String scriptFullPath, ParameterScanData data, String resultsPath)
  {
    super(name);

    setAbsoluteFilePath(iFile.getLocation().makeAbsolute().toOSString());
    setRelativeFilePath(iFile.getFullPath().toOSString());

    setBNGPath(bngPath);
    setScriptFullPath(scriptFullPath);
    setData(data);
    setResultsPath(resultsPath);

    setProperty(IProgressConstants.KEEP_PROPERTY, true);
  }

  @Override
  protected IStatus run(IProgressMonitor monitor)
  {
    // Tell the monitor
    monitor.beginTask("Validation Files...", 4);

    if (!validateBNGLFile(m_absoluteFilePath))
    {
      Console.displayOutput(m_absoluteFilePath, "Error in file path.");
      return Status.CANCEL_STATUS;
    }
    if (!validateBNGPath(m_bngPath))
    {
      Console.displayOutput(m_bngPath, "Error bng path.");
      return Status.CANCEL_STATUS;
    }
    if (!validateScriptPath(m_scriptFullPath))
    {
      Console.displayOutput(m_scriptFullPath, "Error in script path.");
      return Status.CANCEL_STATUS;
    }

    // MONITOR
    monitor.setTaskName("Setting up the results directory");
    monitor.worked(1);

    // Set up the results directory

    // Make the directory if necessary
    (new File(m_resultsPath)).mkdirs();

    // MONITOR
    monitor.setTaskName("Generating Commands...");
    monitor.worked(1);

    // Get a parameterscan command
    ParameterScanCommand command = new ParameterScanCommand(m_absoluteFilePath,
        m_bngPath, m_scriptFullPath, m_resultsPath, m_data);

    // MONITOR
    monitor.setTaskName("Running Parameter Scan...");
    monitor.worked(1);

    // Run it in the commandRunner
    CommandRunner<ParameterScanCommand> runner = 
        new CommandRunner<ParameterScanCommand>(
        command, new File(m_resultsPath), m_relativeFilePath, monitor);

    try
    {
      runner.run();
    }
    catch (SimulationErrorException e)
    {
      return Status.CANCEL_STATUS;
    }

    if (monitor.isCanceled())
    {
      undoSimulation();
      updateTrees();
      return Status.CANCEL_STATUS;
    }
    else
    {
      // MONITOR
      monitor.setTaskName("Done.");
      monitor.worked(1);
    }

    updateTrees();
    return Status.OK_STATUS;
  }

  private void undoSimulation()
  {
    try
    {
      deleteRecursive(new File(m_resultsPath));
    }
    catch (FileNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    Console.displayOutput(m_relativeFilePath, "Parameter Scan Cancelled!\n\n");
  }

  private boolean deleteRecursive(File path) throws FileNotFoundException
  {
    if (!path.exists())
      throw new FileNotFoundException(path.getAbsolutePath());
    boolean ret = true;
    if (path.isDirectory())
    {
      for (File f : path.listFiles())
      {
        ret = ret && deleteRecursive(f);
      }
    }
    return ret && path.delete();
  }

  private void updateTrees()
  {
    // Update the resource tree.
    try
    {
      ResourcesPlugin.getWorkspace().getRoot()
          .refreshLocal(IResource.DEPTH_INFINITE, null);
    }
    catch (CoreException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static boolean validateBNGLFile(String path)
  {
    if ((new File(path)).exists())
      return true;

    return false;
  }

  private static boolean validateBNGPath(String path)
  {
    if ((new File(path + "BNG2.pl")).exists())
      return true;

    return false;
  }

  private static boolean validateScriptPath(String path)
  {
    if ((new File(path)).exists())
      return true;

    return false;
  }

  public void setBNGPath(String path)
  {
    m_bngPath = path;
  }

  public void setScriptFullPath(String path)
  {
    m_scriptFullPath = path;
  }

  public void setData(ParameterScanData data)
  {
    m_data = data;
  }

  private void setResultsPath(String resultsPath)
  {
    m_resultsPath = resultsPath;
  }

  public void setAbsoluteFilePath(String path)
  {
    m_absoluteFilePath = path;
  }

  public void setRelativeFilePath(String path)
  {
    m_relativeFilePath = path;
  }

}
