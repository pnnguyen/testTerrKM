package com.example.terrorist;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;

/**
 *
 * @author Phuc Nguyen
 *
 * Change History
 *
 * Date     Who  Ver  What
 * -------- --- ----- ----------------------------------------------------------
 * 06/08/19  PN  X0.1 Initial wrapper class to ingest an OWL model via Jena 2.11.
 */

public class OwlKB
{
  private OntModel _curDataModel = null;
  private OntDocumentManager _ontManager = null;

  public boolean loadModelFile(String modelUri)
  {
    String fileUri = "";

    try
    {
      _curDataModel = ModelFactory.createOntologyModel();
    }
    catch (Exception cce)
    {
      System.out.println("\n!!! OwlKB.loadModelFile(modelUri) caught Exception (look for ClassCastException in ClassLoader).\n"
                         + cce.getMessage());
    }
    if (_curDataModel == null)
    {
      System.out.println("\n!!! OwlKB.loadModelFile(modelUri):_curDataModel = " + _curDataModel);
      return false;
    }

    try
    {
      if (_curDataModel != null)
      {
        _ontManager = _curDataModel.getDocumentManager();
        if ( modelUri.startsWith("C:")||modelUri.startsWith("D:") )
        {
          fileUri = new String("file:///" + modelUri.trim());
          _curDataModel = (OntModel)_ontManager.getOntology(fileUri,
                               OntModelSpec.OWL_MEM);
          OntModelSpec curSpec = _curDataModel.getSpecification();
          _curDataModel = ModelFactory.createOntologyModel(curSpec, _curDataModel);
          long numTriples = _curDataModel.size();
          if(numTriples==0)
          {
            throw new Exception("!!! No triples!!!");
          }
        }
        return true;
      }
    }
    catch (Exception riotEx)
    {
      System.out.println("\n!!! OwlKB.loadModelFile(modelUri) error "
          + "(look for RiotException in Jena stack trace due to invalid attr "
          + "\n" );
      riotEx.printStackTrace();
    }

    return false;
  }
//
  public OntModel getJenaModel()
  {
    return _curDataModel;
  }

  public void closeEngine()
  {
    if (_curDataModel != null)
    {
      _curDataModel = null;
      System.out.println("\n!!! OwlKB.closeEngine() ...");
    }
  }

}