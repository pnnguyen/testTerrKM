package com.example.terrorist;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import com.example.terrorist.OwlKB;
import com.example.terrorist.JenaModelWrapper;


/**
 * @author Phuc Nguyen
 *
 * Change History
 *
 * Date     Who  Ver  What
 * -------- --- ----- ----------------------------------------------------------
 * 06/08/19  PN  X0.1 Initial main class to navigate an OWL taxonomy model.
 */

public class TaxoNav
{
  private static final String TAXONAV_PROPERTIES_FILE = "TaxoNav.properties";
  private static final String TAXONOMY_FULL_FILENAME = "com.example.terrorist.fullFilename";

  private JenaModelWrapper _jenaModel;
  private OwlKB _qe;
  private String _modelFileStr = "";

  public static void main(String[] args)
  {
    System.out.println("!!! Start TaxoNav ... \n");
    long startTime = System.currentTimeMillis();
    TaxoNav taxoNav = new TaxoNav();
    String baseNS = taxoNav.getBaseNamespace();
    System.out.println("$$$ baseNS = " + baseNS);
    Vector<String> rootClasses = taxoNav.getTopLevelClasses();
    int count = rootClasses.size();
    System.out.println("!!! rootClasses Count = " + count );

    String className = "";
    if (args.length == 0)
    {
      for (int i=0; i<count; i++)
      {
        className = rootClasses.get(i).trim();
        System.out.println("\nLevel1-- " + className );
        Vector directSupers = taxoNav.getSuperClasses(className);
        Vector<Vector> allPropsObjs = taxoNav.getPropertiesAndObjectsForNode(className);
        Vector<String> directSubs = taxoNav.getSubClasses(className);
        int countSubsL2 = directSubs.size();
        for (int j=0; j<countSubsL2; j++)
        {
          className = (String)directSubs.get(j);
          System.out.println("  Level2-- " + className );
        }
      }
    }
    if (args.length > 0)
    {
      className = args[0];
      System.out.println("\nTriples for Subject Class: " + className );
      taxoNav.getPropertiesAndObjectsForNode(className);
    }

    System.out.println("\n!!! Stop TaxoNav. \n");
    long stopTime = System.currentTimeMillis();
    System.out.println("***** total RunTime (ms) = " + (stopTime - startTime) );
    taxoNav.quit();
  }
  
  public TaxoNav(String fullFileName)
  {
    setModelFileStr(fullFileName);
    _qe = new OwlKB();
    loadModelFile(fullFileName);
    _jenaModel = new JenaModelWrapper(_qe);
    _jenaModel.buildSortedClassList();
  }

  public TaxoNav()
  {
    init();
    _qe = new OwlKB();
    loadModelFile(_modelFileStr);
    _jenaModel = new JenaModelWrapper(_qe);
    _jenaModel.buildSortedClassList();
  }

  void init()
  {
    String startServerDir;
    String pathSep;
    String propFile = "";
    try
    {
      //TODO:
      startServerDir = System.getProperty("user.dir");
      pathSep = System.getProperty("file.separator");
      propFile = startServerDir + pathSep + TAXONAV_PROPERTIES_FILE;
    }
    catch (Exception exc)
    {
      System.out.println("ERROR: TaxoNav.init(): " + exc.getMessage());
    }
    java.util.Properties props = new java.util.Properties();
    System.out.println("\n==> propFile:" + propFile);
    try
    {
      props.load(new BufferedInputStream(new FileInputStream(propFile)));
      _modelFileStr = props.getProperty(TAXONOMY_FULL_FILENAME).trim();
      System.out.println(TAXONOMY_FULL_FILENAME + ": " + _modelFileStr);
    }
    catch (IOException ioe)
    {
      System.out.println("ERROR: Property file not found.");
    }
    catch (NullPointerException npe)
    {
      System.out.println("ERROR: Property not found.");
      npe.printStackTrace();
    }
    finally
    {
      System.out.println("==> java.class.path=" + System.getProperty("java.class.path") );
    }

  }

  boolean loadModelFile(String filePath)
  {
    boolean loadOK = false;
    if(!loadOK && _qe != null)
    {
      loadOK = _qe.loadModelFile(filePath);
    }
    return loadOK;

  }

  public Vector getSuperClasses(String nodeName)
  {
    Vector<String> superClasses = _jenaModel.getSuperClassForNode(nodeName);
    return superClasses;
  }

  public Vector getSubClasses(String nodeName)
  {
    Vector<String> subClasses = _jenaModel.getSubClassesForNode(nodeName);
    return subClasses;
  }

  public Vector getPropertiesAndObjectsForNode(String nodeName)
  {
    Vector<Vector> allPropsObjs = _jenaModel.getOutboundStmtsForNode(nodeName);
    return allPropsObjs;
  }

  public Vector getTopLevelClasses()
  {
    Vector<String> rootClasses = _jenaModel.getTopLevelClasses();
    return rootClasses;
  }


  void setModelFileStr(String filePath)
  {
    _modelFileStr = new String(filePath);
    System.out.println(TAXONOMY_FULL_FILENAME + ": " + _modelFileStr);
  }

  public String getBaseNamespace()
  {
    String baseNS = _jenaModel.getBaseNamespace();
    return baseNS;
  }

  void quit()
  {
    if (_qe != null)
    {
      _qe.closeEngine();
    }
    System.exit(0);
  }

}