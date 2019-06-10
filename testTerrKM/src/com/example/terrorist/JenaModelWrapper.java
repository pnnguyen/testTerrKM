package com.example.terrorist;

import java.util.HashMap;
import java.util.Vector;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 *
 * @author Phuc Nguyen
 *
 * Date     Who  Ver  What
 * -------- --- ----- ----------------------------------------------------------
 * 06/08/19  PN  X0.1 Initial wrapper class to inspect an OWL model via Jena 2.11.
 */

public class JenaModelWrapper
{
  private String _currentNode = "";

  private OwlKB _owlModel = null;
  private Vector<String> _allClasses = new Vector<String>(1000);
  private HashMap<String, String> _allClassesLowerCase = new HashMap(1000);

  public JenaModelWrapper(OwlKB owlKB)
  {
    _owlModel = owlKB;
  }

  private void setCurrentNode(String nodeName)
  {
    _currentNode = nodeName;
  }

  public String getCurrentNode()
  {
    return _currentNode;
  }

  public Vector getAllClassesSorted()
  {
    if (_allClasses.size() > 0)
    {
      return _allClasses;
    }
    else
    {
      buildSortedClassList();
      return _allClasses;
    }
  }

  public void buildSortedClassList()
  {
    ExtendedIterator classIter = null;
    try
    {
      if (_owlModel.getJenaModel() != null)
      {
        System.out.println("getAllClassesSorted(): curDataModel size = " +
                           _owlModel.getJenaModel().size() );
        classIter = _owlModel.getJenaModel().listClasses();
        while (classIter.hasNext())
        {
          Object aClass = classIter.next();
          String className = aClass.toString().trim();
          _allClasses.add(className);
        }
      }
    }
    catch (Exception e)
    {
      System.out.println("!@#$ ERROR: " + e.getMessage() );
      classIter.close();
    }
    java.util.Collections.sort(_allClasses);
  }

  public Vector getSuperClassForNode(String nodeName)
  {
    Vector<String> allSupers = new Vector<String>();
    Model newModel = null;
    OntClass node = _owlModel.getJenaModel().getOntClass(nodeName);
    if (node == null)
    {
      return allSupers;
    }
    SimpleSelector select1 =
      new SimpleSelector(node, (Property)RDFS.subClassOf, (RDFNode)null );
    try
    {
      if (_owlModel.getJenaModel() != null)
      {
        newModel = _owlModel.getJenaModel().query(select1);
      }
    }
    catch (Exception e)
    {
      newModel.close();
    }
    StmtIterator stmtIter = newModel.listStatements();
    int stmtCount = 0;
    while (stmtIter.hasNext())
    {
      Statement stmt = stmtIter.nextStatement();
      String obj = stmt.getObject().toString().trim();
      OntClass parentNode = _owlModel.getJenaModel().getOntClass(obj);
      if (parentNode != null)
      {
        allSupers.add(obj);
        stmtCount++;
      }
    }
    return allSupers;
  }

  public Vector getSubClassesForNode(String nodeName)
  {
    Vector<String> allSubclasses = new Vector<String>();
    Model newModel = null;
    OntClass node = _owlModel.getJenaModel().getOntClass(nodeName);
    if (node == null)
    {
      return allSubclasses;
    }
    SimpleSelector select1 =
      new SimpleSelector((Resource)null, (Property)RDFS.subClassOf,	node);
    try
    {
      if (_owlModel.getJenaModel() != null)
      {
        newModel = _owlModel.getJenaModel().query(select1);
      }
    }
    catch (Exception e)
    {
      newModel.close();
    }
    StmtIterator stmtIter = newModel.listStatements();
    int stmtCount = 0;
    while (stmtIter.hasNext())
    {
      Statement stmt = stmtIter.nextStatement();
      String subj = stmt.getSubject().toString().trim();
      allSubclasses.add(subj);
      stmtCount++;
    }
    return allSubclasses;
  }

  // Only knows OntClass.
  public Vector getOutboundStmtsForNode(String nodeName)
  {
    Vector<Vector> allStmts = new Vector<Vector>();
    Model newModel = null;
    OntClass node = _owlModel.getJenaModel().getOntClass(nodeName);
    if (node == null)
    {
      return allStmts;
    }
    SimpleSelector select1 = new SimpleSelector((Resource)node, (Property)null,
                             (RDFNode)null);
    try
    {
      if (_owlModel.getJenaModel() != null)
      {
        newModel = _owlModel.getJenaModel().query(select1);
      }
      else
      {
        Exception ioe = new Exception(" getOutboundStmtsForNode(String nodeName)!@#");
        throw ioe;
      }
    }
    catch (Exception e)
    {
      newModel.close();
    }
    StmtIterator stmtIter = newModel.listStatements();
    int stmtCount = 0;
    while (stmtIter.hasNext())
    {
      Statement stmt = stmtIter.nextStatement();
      String predicate = stmt.getPredicate().toString().trim();
      String object = stmt.getObject().toString().trim();
      System.out.println("  <" + predicate + ", " + object + ">");
      Vector<String> stmtVector = new Vector<String>(2);
      stmtVector.add(0, predicate);
      stmtVector.add(1, object);
      allStmts.add(stmtVector);
      stmtCount++;
    }
    return allStmts;
  }

  public Vector getTopLevelClasses()
  {
    Vector<String> rootClasses = new Vector<String>();
    Vector<String> allClasses = this.getAllClassesSorted();
    int count = allClasses.size();

    try
    {
      for (int i=0; i<count; i++)
      {
        String className = allClasses.get(i);
        Vector superClasses = this.getSuperClassForNode(className);
        if (superClasses.size() == 0)
        {
          rootClasses.add(className);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println(" getTopLevelClasses() //TODO add some notes !@#");
    }
    return rootClasses;
  }

  public String getBaseNamespace()
  {
    OntModel jenaModel = _owlModel.getJenaModel();
    String baseNS = jenaModel.getNsPrefixURI("");
    return baseNS;
  }
}
