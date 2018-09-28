/*******************************************************************************
 *
 * Tableau Data Extract Plugin for Pentaho Data Integration
 *
 * Author: Chris Deptula
 * https://github.com/cdeptula/tdeoutputplugin
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License" );
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.openbi.kettle.plugins.tableaudataextract;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInjectionInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * Created on 2013-12-09
 *
 */
@Step( id = "TDEOutputPlugin", image = "tde.png", name = "Step.Name", description = "Step.Description",
  categoryDescription = "Category.Description", i18nPackageName = "org.openbi.kettle.plugins.tableaudataextract",
  documentationUrl = "https://github.com/cdeptula/tdeoutputplugin/wiki",
  casesUrl = "https://github.com/cdeptula/tdeoutputplugin/issues",
  isSeparateClassLoaderNeeded = false )
public class TDEOutputMeta extends BaseStepMeta  implements StepMetaInterface {
  private static Class<?> PKG = TDEOutputMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$


  /** The base name of the output file */
  private  String fileName;

  /** Flag: create parent folder, default to true */
  private boolean createparentfolder = true;

  /** The file extention in case of a generated filename */
  private  String  extension = "tde";

  /** Flag to indicate the we want to append an existing file or overwrite */
  private  boolean fileAppended;

  /** Flag: add the stepnr in the filename */
  private  boolean stepNrInFilename;

  /** Flag: add the partition number in the filename */
  private  boolean partNrInFilename;

  /** Flag: add the date in the filename */
  private  boolean dateInFilename;

  /** Flag: add the time in the filename */
  private  boolean timeInFilename;

  /* THE FIELD SPECIFICATIONS ... */

  /** The output fields */
  private  TDEField[] outputFields;

  /** Flag: add the filenames to result filenames */
  private boolean addToResultFilenames;

  private boolean specifyingFormat;

  private String dateTimeFormat;

  public TDEOutputMeta() {
    super(); // allocate BaseStepMeta
  }

  /**
   * @return FileAsCommand
   */
  /**
   * @param createparentfolder The createparentfolder to set.
   */
  public void setCreateParentFolder( boolean createparentfolder ) {
    this.createparentfolder = createparentfolder;
  }

  /**
   * @return Returns the createparentfolder.
   */
  public boolean isCreateParentFolder() {
    return createparentfolder;
  }
  /**
   * @return Returns the dateInFilename.
   */
  public boolean isDateInFilename() {
    return dateInFilename;
  }

  /**
   * @param dateInFilename The dateInFilename to set.
   */
  public void setDateInFilename( boolean dateInFilename ) {
    this.dateInFilename = dateInFilename;
  }

  /**
   * @return Returns the extension.
   */
  public String getExtension() {
    return extension;
  }

  /**
   * @param extension The extension to set.
   */
  public void setExtension( String extension ) {
    this.extension = extension;
  }

  /**
   * @return Returns the add to result filesname.
   */
  public boolean isAddToResultFiles() {
    return addToResultFilenames;
  }

  /**
   * @param addtoresultfilenamesin The addtoresultfilenames to set.
   */
  public void setAddToResultFiles( boolean addtoresultfilenamesin ) {
    this.addToResultFilenames = addtoresultfilenamesin;
  }

  /**
   * @return Returns the fileAppended.
   */
  public boolean isFileAppended() {
    return fileAppended;
  }

  /**
   * @param fileAppended The fileAppended to set.
   */
  public void setFileAppended( boolean fileAppended ) {
    this.fileAppended = fileAppended;
  }

  /**
   * @return Returns the fileName.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName The fileName to set.
   */
  public void setFileName( String fileName ) {
    this.fileName = fileName;
  }
  /**
   * @return Returns the stepNrInFilename.
   */
  public boolean isStepNrInFilename() {
    return stepNrInFilename;
  }

  /**
   * @param stepNrInFilename The stepNrInFilename to set.
   */
  public void setStepNrInFilename( boolean stepNrInFilename ) {
    this.stepNrInFilename = stepNrInFilename;
  }

  /**
   * @return Returns the partNrInFilename.
   */
  public boolean isPartNrInFilename() {
    return partNrInFilename;
  }

  /**
   * @param partNrInFilename The partNrInFilename to set.
   */
  public void setPartNrInFilename( boolean partNrInFilename ) {
    this.partNrInFilename = partNrInFilename;
  }

  /**
   * @return Returns the timeInFilename.
   */
  public boolean isTimeInFilename() {
    return timeInFilename;
  }

  /**
   * @param timeInFilename The timeInFilename to set.
   */
  public void setTimeInFilename( boolean timeInFilename ) {
    this.timeInFilename = timeInFilename;
  }


  public boolean  isSpecifyingFormat() {
    return specifyingFormat;
  }

  public void setSpecifyingFormat( boolean specifyingFormat ) {
    this.specifyingFormat = specifyingFormat;
  }

  public String getDateTimeFormat() {
    return dateTimeFormat;
  }

  public void setDateTimeFormat( String dateTimeFormat ) {
    this.dateTimeFormat = dateTimeFormat;
  }


  /**
   * @return Returns the outputFields.
   */
  public TDEField[] getOutputFields() {
    return outputFields;
  }

  /**
   * @param outputFields The outputFields to set.
   */
  public void setOutputFields( TDEField[] outputFields ) {
    this.outputFields = outputFields;
  }


  public void loadXML( Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters ) throws KettleXMLException {
    readData( stepnode );
  }

  public void allocate( int nrfields ) {
    outputFields = new TDEField[nrfields];
  }

  public Object clone() {
    TDEOutputMeta retval = (TDEOutputMeta) super.clone();
    int nrfields = outputFields.length;

    retval.allocate( nrfields );

    for ( int i = 0; i < nrfields; i++ ) {
      retval.outputFields[i] = (TDEField) outputFields[i].clone();
    }

    return retval;
  }

  public void readData( Node stepnode ) throws KettleXMLException {
    try {
            // Default createparentfolder to true if the tag is missing
      String createParentFolderTagValue = XMLHandler.getTagValue( stepnode, "create_parent_folder" );
      createparentfolder = ( createParentFolderTagValue == null ) ? true : "Y".equalsIgnoreCase( createParentFolderTagValue );

      fileName              = XMLHandler.getTagValue( stepnode, "file", "name" );
      extension             = XMLHandler.getTagValue( stepnode, "file", "extention" );
      fileAppended           = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "file", "append" ) );
      stepNrInFilename      = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "file", "split" ) );
      partNrInFilename      = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "file", "haspartno" ) );
      dateInFilename        = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "file", "add_date" ) );
      timeInFilename        = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "file", "add_time" ) );
      specifyingFormat        = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "file", "SpecifyFormat" ) );
      dateTimeFormat             = XMLHandler.getTagValue( stepnode, "file", "date_time_format" );

      String AddToResultFiles = XMLHandler.getTagValue( stepnode, "file", "add_to_result_filenames" );
      if ( Const.isEmpty( AddToResultFiles ) ) {
        addToResultFilenames = true;
      } else {
        addToResultFilenames = "Y".equalsIgnoreCase( AddToResultFiles );
      }

      Node fields  = XMLHandler.getSubNode( stepnode, "fields" );
      int nrfields = XMLHandler.countNodes( fields, "field" );

      allocate( nrfields );

      for ( int i = 0; i < nrfields; i++ ) {
        Node fnode = XMLHandler.getSubNodeByNr( fields, "field", i );

        outputFields[i] = new TDEField();
        outputFields[i].setName( XMLHandler.getTagValue( fnode, "name" ) );
        outputFields[i].setNewName( XMLHandler.getTagValue( fnode, "new_name" ) );
        outputFields[i].setTdeType( XMLHandler.getTagValue( fnode, "type" ) );
      }
    } catch ( Exception e ) {
      throw new KettleXMLException( "Unable to load step info from XML", e );
    }
  }


  public void setDefault() {
    createparentfolder = true;  // Default createparentfolder to true
    specifyingFormat = false;
    dateTimeFormat = null;
    fileName         = "extract";
    extension        = "tde";
    stepNrInFilename = false;
    partNrInFilename = false;
    dateInFilename   = false;
    timeInFilename   = false;
    addToResultFilenames = true;

    int i, nrfields = 0;

    allocate( nrfields );

    for ( i = 0; i < nrfields; i++ ) {
      outputFields[i] = new TDEField();

      outputFields[i].setName( "field" + i );
      outputFields[i].setTdeType( "Number" );

    }
    fileAppended = false;
  }

  public String[] getFiles( VariableSpace space ) {
    int copies = 1;
    int splits = 1;
    int parts = 1;

    if ( stepNrInFilename ) {
      copies = 3;
    }

    if ( partNrInFilename ) {
      parts = 3;
    }

    int nr = copies * parts * splits;
    if ( nr > 1 ) {
      nr++;
    }

    String[] retval = new String[nr];

    int i = 0;
    for ( int copy = 0; copy < copies; copy++ ) {
      for ( int part = 0; part < parts; part++ ) {
        for ( int split = 0; split < splits; split++ ) {
          retval[i] = buildFilename( space, copy, "P" + part, split, false );
          i++;
        }
      }
    }
    if ( i < nr ) {
      retval[i] = "...";
    }

    return retval;
  }

  public String buildFilename( VariableSpace space, int stepnr, String partnr, int splitnr, boolean ziparchive ) {
    return buildFilename( fileName, extension, space, stepnr, partnr, splitnr, ziparchive, this );
  }

  public String buildFilename( String filename, String extension, VariableSpace space, int stepnr, String partnr, int splitnr, boolean ziparchive, TDEOutputMeta meta ) {
    SimpleDateFormat daf = new SimpleDateFormat();

    // Replace possible environment variables...
    String retval = space.environmentSubstitute( filename );
    String realextension = space.environmentSubstitute( extension );


    Date now = new Date();

    if ( meta.isSpecifyingFormat() && !Const.isEmpty( meta.getDateTimeFormat() ) ) {
      daf.applyPattern( meta.getDateTimeFormat() );
      String dt = daf.format( now );
      retval += dt;
    } else {
      if ( meta.isDateInFilename() ) {
        daf.applyPattern( "yyyMMdd" );
        String d = daf.format( now );
        retval += "_" + d;
      }
      if ( meta.isTimeInFilename() ) {
        daf.applyPattern( "HHmmss" );
        String t = daf.format( now );
        retval += "_" + t;
      }
    }
    if ( meta.isStepNrInFilename() ) {
      retval += "_" + stepnr;
    }
    if ( meta.isPartNrInFilename() ) {
      retval += "_" + partnr;
    }
    retval += "." + realextension;
    return retval;
  }


  public void getFields( RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space ) throws KettleStepException {
    // No values are added to the row in this type of step
    // However, in case of Fixed length records, 
    // the field precisions and lengths are altered!

    for ( int i = 0; i < outputFields.length; i++ ) {
      TDEField field = outputFields[i];
      ValueMetaInterface v = row.searchValueMeta( field.getName() );
    }
  }

  public String getXML() {
    StringBuffer retval = new StringBuffer( 800 );

    retval.append( "    " + XMLHandler.addTagValue( "create_parent_folder", createparentfolder ) );
    retval.append( "    <file>" ).append( Const.CR );
    retval.append( "      " ).append( XMLHandler.addTagValue( "name",       fileName ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "extention",  extension ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "append",     fileAppended ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "split",      stepNrInFilename ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "haspartno",  partNrInFilename ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "add_date",   dateInFilename ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "add_time",   timeInFilename ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "SpecifyFormat",   specifyingFormat ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "date_time_format",  dateTimeFormat ) );


    retval.append( "      " ).append( XMLHandler.addTagValue( "add_to_result_filenames",   addToResultFilenames ) );
    retval.append( "    </file>" ).append( Const.CR );

    retval.append( "    <fields>" ).append( Const.CR );
    for ( int i = 0; i < outputFields.length; i++ ) {
      TDEField field = outputFields[i];

      if ( field.getName() != null && field.getName().length() != 0 ) {
        retval.append( "      <field>" ).append( Const.CR );
        retval.append( "        " ).append( XMLHandler.addTagValue( "name",      field.getName() ) );
        retval.append( "        " ).append( XMLHandler.addTagValue( "new_name",      field.getNewName() ) );
        retval.append( "        " ).append( XMLHandler.addTagValue( "type",      field.getTdeTypeDesc() ) );
        retval.append( "      </field>" ).append( Const.CR );
      }
    }
    retval.append( "    </fields>" ).append( Const.CR );

    return retval.toString();
  }

  public void readRep( Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters ) throws KettleException {
    try {
      createparentfolder =      rep.getStepAttributeBoolean( id_step, "create_parent_folder" );
      fileName        =      rep.getStepAttributeString( id_step, "file_name" );
      extension       =      rep.getStepAttributeString( id_step, "file_extention" );
      fileAppended          =      rep.getStepAttributeBoolean( id_step, "file_append" );
      stepNrInFilename      =      rep.getStepAttributeBoolean( id_step, "file_add_stepnr" );
      partNrInFilename      =      rep.getStepAttributeBoolean( id_step, "file_add_partnr" );
      dateInFilename        =      rep.getStepAttributeBoolean( id_step, "file_add_date" );
      timeInFilename        =      rep.getStepAttributeBoolean( id_step, "file_add_time" );
      specifyingFormat        =      rep.getStepAttributeBoolean( id_step, "SpecifyFormat" );
      dateTimeFormat       =      rep.getStepAttributeString( id_step, "date_time_format" );


      String AddToResultFiles = rep.getStepAttributeString( id_step, "add_to_result_filenames" );
      if ( Const.isEmpty( AddToResultFiles ) ) {
        addToResultFilenames = true;
      } else {
        addToResultFilenames = rep.getStepAttributeBoolean( id_step, "add_to_result_filenames" );
      }

      int nrfields = rep.countNrStepAttributes( id_step, "field_name" );


      allocate( nrfields );

      for ( int i = 0; i < nrfields; i++ ) {
        outputFields[i] = new TDEField();
        outputFields[i].setName(        rep.getStepAttributeString( id_step, i, "field_name" ) );
        outputFields[i].setNewName(        rep.getStepAttributeString( id_step, i, "field_new_name" ) );
        outputFields[i].setTdeType(       rep.getStepAttributeString( id_step, i, "field_type" ) );
      }

    } catch ( Exception e ) {
      throw new KettleException( "Unexpected error reading step information from the repository", e );
    }
  }

  public void saveRep( Repository rep, ObjectId id_transformation, ObjectId id_step ) throws KettleException {
    try {
      rep.saveStepAttribute( id_transformation, id_step, "file_name",        fileName );
      rep.saveStepAttribute( id_transformation, id_step, "file_extention",   extension );
      rep.saveStepAttribute( id_transformation, id_step, "file_append",      fileAppended );
      rep.saveStepAttribute( id_transformation, id_step, "file_add_stepnr",  stepNrInFilename );
      rep.saveStepAttribute( id_transformation, id_step, "file_add_partnr",  partNrInFilename );
      rep.saveStepAttribute( id_transformation, id_step, "file_add_date",    dateInFilename );
      rep.saveStepAttribute( id_transformation, id_step, "date_time_format",   dateTimeFormat );
      rep.saveStepAttribute( id_transformation, id_step, "create_parent_folder", createparentfolder );
      rep.saveStepAttribute( id_transformation, id_step, "SpecifyFormat",    specifyingFormat );

      rep.saveStepAttribute( id_transformation, id_step, "add_to_result_filenames",    addToResultFilenames );
      rep.saveStepAttribute( id_transformation, id_step, "file_add_time",    timeInFilename );

      for ( int i = 0; i < outputFields.length; i++ ) {
        TDEField field = outputFields[i];

        rep.saveStepAttribute( id_transformation, id_step, i, "field_name",      field.getName() );
        rep.saveStepAttribute( id_transformation, id_step, i, "field_new_name",      field.getNewName() );
        rep.saveStepAttribute( id_transformation, id_step, i, "field_type",      field.getTdeTypeDesc() );
      }
    } catch ( Exception e ) {
      throw new KettleException( "Unable to save step information to the repository for id_step= " + id_step, e );
    }
  }


  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepinfo, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info ) {
    CheckResult cr;

    // Check output fields
    if ( prev != null && prev.size() > 0 ) {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "TDEOutputMeta.CheckResult.FieldsReceived", "" + prev.size() ), stepinfo );
      remarks.add( cr );

      String  error_message =  "";
      boolean error_found = false;

      // Starting from selected fields in ...
      for ( int i = 0; i < outputFields.length; i++ ) {
        int idx = prev.indexOfValue( outputFields[i].getName() );
        if ( idx < 0 ) {
          error_message += "\t\t" + outputFields[i].getName() + Const.CR;
          error_found = true;
        }
      }
      if ( error_found ) {
        error_message = BaseMessages.getString( PKG, "TDEOutputMeta.CheckResult.FieldsNotFound", error_message );
        cr = new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepinfo );
        remarks.add( cr );
      } else {
        cr = new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "TDEOutputMeta.CheckResult.AllFieldsFound" ), stepinfo );
        remarks.add( cr );
      }
    }

    // See if we have input streams leading to this step!
    if ( input.length > 0 ) {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "TDEMeta.CheckResult.ExpectedInputOk" ), stepinfo );
      remarks.add( cr );
    } else {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString( PKG, "TDEOutputMeta.CheckResult.ExpectedInputError" ), stepinfo );
      remarks.add( cr );
    }

    cr = new CheckResult( CheckResultInterface.TYPE_RESULT_COMMENT, BaseMessages.getString( PKG, "TDEOutputMeta.CheckResult.FilesNotChecked" ), stepinfo );
    remarks.add( cr );
  }

  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans trans ) {
    return new TDEOutput( stepMeta, stepDataInterface, cnr, transMeta, trans );
  }

  public StepDataInterface getStepData() {
    return new TDEOutputData();
  }

  /**
   * Since the exported transformation that runs this will reside in a ZIP file, we can't reference files relatively.
   * So what this does is turn the name of the base path into an absolute path.
   */
  public void setFilename( String fileName ) {
    this.fileName = fileName;
  }

  @Override
  public StepMetaInjectionInterface getStepMetaInjectionInterface() {
    return new TDEOutputMetaInjection( this );
  }
}
