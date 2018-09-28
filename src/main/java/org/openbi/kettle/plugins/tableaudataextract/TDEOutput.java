/*******************************************************************************
 *
 * Tableau Data Extract Plugin for Pentaho Data Integration
 *
 * Author: Chris Deptula
 * https://github.com/cdeptula/tdeoutputplugin
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import com.tableausoftware.common.Collation;
import com.tableausoftware.extract.Extract;
import com.tableausoftware.extract.TableDefinition;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import java.util.Calendar;
import java.util.Date;


/**
 * Converts input rows to text and then writes this text to one or more files.
 * 
 * @author Chris
 * @since 2012-12-09
 */
public class TDEOutput extends BaseStep implements StepInterface {
  private static Class<?> PKG = TDEOutputMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

  public TDEOutputMeta meta;
  public TDEOutputData data;

  public TDEOutput( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  public synchronized boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (TDEOutputMeta) smi;
    data = (TDEOutputData) sdi;

    boolean result = true;
    Object[] r = getRow(); // This also waits for a row to be finished.

    if ( r != null && first ) {
      first = false;
      data.outputRowMeta = getInputRowMeta().clone();
      meta.getFields( data.outputRowMeta, getStepname(), null, null, this );

      // if file name in field is enabled then set field name and open file
      //

      data.fieldnrs = new int[meta.getOutputFields().length];
      for ( int i = 0; i < meta.getOutputFields().length; i++ ) {
        data.fieldnrs[i] = data.outputRowMeta.indexOfValue( meta.getOutputFields()[i].getName() );
        if ( data.fieldnrs[i] < 0 ) {
          throw new KettleStepException( "Field [" + meta.getOutputFields()[i].getName() + "] couldn't be found in the input stream!" );
        }
      }
    }

    // no more input to be expected...
    if ( r == null ) {
      closeFile();
      setOutputDone();
      return false;
    }

    // First handle the file name in field
    // Write a header line as well if needed
    //

    try {
      RowMetaInterface inputRowMeta = getInputRowMeta();
      data.row = new com.tableausoftware.extract.Row( data.tableDef );

      for ( int i = 0; i < meta.getOutputFields().length; i++ ) {
        String name = meta.getOutputFields()[i].getName();
        int fieldIndex = inputRowMeta.indexOfValue( name );

        if ( inputRowMeta.isNull( r, fieldIndex ) ) {
          data.row.setNull( i );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.INTEGER ) {
          data.row.setInteger( i, inputRowMeta.getInteger( r, fieldIndex ).intValue() );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.DOUBLE ) {
          data.row.setDouble( i, inputRowMeta.getNumber( r, fieldIndex ) );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.DATE_TIME ) {
          Date date = inputRowMeta.getDate( r, fieldIndex );
          Calendar calendar = Calendar.getInstance();
          calendar.setTime( date );
          data.row.setDateTime( i, calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ) + 1, calendar.get( Calendar.DAY_OF_MONTH ), calendar.get( Calendar.HOUR_OF_DAY ), calendar.get( Calendar.MINUTE ), calendar.get( Calendar.SECOND ), calendar.get( Calendar.MILLISECOND ) );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.DATE ) {
          Date date = inputRowMeta.getDate( r, fieldIndex );
          Calendar calendar = Calendar.getInstance();
          calendar.setTime( date );
          data.row.setDate( i, calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ) + 1, calendar.get( Calendar.DAY_OF_MONTH ) );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.BOOLEAN ) {
          data.row.setBoolean( i, inputRowMeta.getBoolean( r, fieldIndex ) );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.UNICODE_STRING ) {
          data.row.setString( i, inputRowMeta.getString( r, fieldIndex ) );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.CHAR_STRING ) {
          data.row.setCharString( i, inputRowMeta.getString( r, fieldIndex ) );
        } else if ( meta.getOutputFields()[i].getTdeType() == TDEField.DURATION ) {
          Long totalmilliseconds = inputRowMeta.getInteger( r, fieldIndex );
          int[] dateparts = { 1000 * 60 * 60 * 24, 1000 * 60 * 60, 1000 * 60, 1000, 1 };
          Long[] durationParts = new Long[5];
          for ( int e = 0; e < dateparts.length; e++ ) {
            durationParts[e] = totalmilliseconds / (long) dateparts[e];
            Long subtract = durationParts[e] * (long) dateparts[e];
            totalmilliseconds = totalmilliseconds - subtract;
          }

          if ( totalmilliseconds != 0 ) {
            throw new KettleException( "Failed to parse duration" );
          }
          data.row.setDuration( i, durationParts[0].intValue(), durationParts[1].intValue(), durationParts[2].intValue(), durationParts[3].intValue(), durationParts[4].intValue() );
        }
      }
      data.table.insert( data.row );
      incrementLinesOutput();

      putRow( data.outputRowMeta, r ); // in case we want it to go further...
    } catch ( Exception ex ) {
      result = false;
      throw new KettleValueException( ex.getLocalizedMessage() );
    }

    if ( checkFeedback( getLinesOutput() ) ) {
      logBasic( "linenr " + getLinesOutput() );
    }

    return result;
  }

  public String buildFilename( String filename ) {
    return meta.buildFilename( filename, meta.getExtension(), this, getCopy(), getPartitionID(), data.splitnr, false, meta );
  }

  public void configureTableDef() throws KettleException {
    try {
      data.tableDef = new TableDefinition();
      data.tableDef.setDefaultCollation( Collation.EN_GB );
      for ( int i = 0; i < meta.getOutputFields().length; i++ ) {
        data.tableDef.addColumn( meta.getOutputFields()[i].getOutputName(), meta.getOutputFields()[i].getTdeTypeValue( meta.getOutputFields()[i].getTdeType() ) );
      }
    } catch ( UnsatisfiedLinkError ex ) {
      throw new KettleException( "Error configuring extract.  This usually means an error in the Tableau API installation.  Please consult the readme." );
    } catch ( Exception ex ) {
      throw new KettleException( ex );
    }
  }

  public void openNewFile( String baseFilename ) throws KettleException {
    if ( baseFilename == null ) {
      throw new KettleFileException( BaseMessages.getString( PKG, "TDEOutput.Exception.FileNameNotSet" ) ); //$NON-NLS-1$
    }

    String filename = buildFilename( environmentSubstitute( baseFilename ) );

    //Tableau is very picky about file names.  They may not start with file://
    if ( !Const.isWindows() ) {
      if ( filename.startsWith( "file://" ) ) {
        filename = filename.substring( 7 );
      }
    }

    //For Windows Tableau requires filenames to use \ instead of /
    if ( Const.isWindows() ) {
      if ( filename.startsWith( "file:///" ) ) {
        filename = filename.substring( 8 );
      }
      filename = filename.replace( "/", "\\" );
    }

    try {
      // Check for parent folder creation only if the user asks for it
      //
      if ( meta.isCreateParentFolder() ) {
        createParentFolder( filename );
      }

      checkFileExists( filename );

      if ( log.isDetailed() ) {
        logDetailed( "Opening extract " + filename );
      }
      if ( !checkPreviouslyOpened( filename ) ) {
        data.extract = new Extract( filename );
        if ( meta.isFileAppended() && data.extract.hasTable( "Extract" ) ) {
          data.table = data.extract.openTable( "Extract" );
        } else if ( data.extract.hasTable( "Extract" ) ) {
          throw new KettleException( "Extract already exists and append is set to false" );
        } else {
          data.table = data.extract.addTable( "Extract", data.tableDef );
        }

        data.tableDef = data.table.getTableDefinition();
        data.extractOpened = true;
      }

      if ( log.isDetailed() ) {
        logDetailed( "Opened new extract with name [" + filename + "]" );
      }

    } catch ( Exception e ) {
      throw new KettleException( "Error opening extract : " + e.toString() + "\n"
        + "This usually means that the extract was not installed correctly.  Please consult the readme.", e );
    }


    if ( meta.isAddToResultFiles() ) {
      // Add this to the result file names...
      ResultFile resultFile = new ResultFile( ResultFile.FILE_TYPE_GENERAL, KettleVFS.getFileObject( filename, getTransMeta() ), getTransMeta().getName(), getStepname() );
      if ( resultFile != null ) {
        resultFile.setComment( BaseMessages.getString( PKG, "TDEOutput.AddResultFile" ) );
        addResultFile( resultFile );
      }
    }
  }

  private boolean closeFile() {
    boolean retval = false;

    try {
      if ( data.tableDef != null ) {
        data.tableDef.close();
      }
      if ( data.extractOpened && data.extract != null ) {
        data.extract.close();
      }
      data.tableDef = null;
      data.extract = null;
      data.extractOpened = false;
      retval = true;
    } catch ( Exception e ) {
      logError( "Exception trying to close file: " + e.toString(), e );
      setErrors( 1 );
      retval = false;
    }

    return retval;
  }

  public boolean checkPreviouslyOpened( String filename ) {
    return data.previouslyOpenedFiles.contains( filename );
  }

  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (TDEOutputMeta) smi;
    data = (TDEOutputData) sdi;

    if ( super.init( smi, sdi ) ) {
      data.splitnr = 0;
      // In case user want to create file at first row
      // In that case, DO NOT create file at Init
      try {
        configureTableDef();
        openNewFile( meta.getFileName() );
      } catch ( Exception ex ) {
        closeFile();
        logError( ex.getLocalizedMessage() );
        return false;
      }

      return true;
    }

    return false;
  }


  public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
    meta = (TDEOutputMeta) smi;
    data = (TDEOutputData) sdi;
    try {
      closeFile();
    } catch ( Exception ex ) {
      // IGNORE
    }
    super.dispose( smi, sdi );
  }

  private void createParentFolder( String filename ) throws Exception {
    // Check for parent folder
    FileObject parentfolder = null;
    try {
      // Get parent folder
      parentfolder = KettleVFS.getFileObject( filename ).getParent();
      if ( parentfolder.exists() ) {
        if ( isDetailed() ) {
          logDetailed( BaseMessages.getString( PKG, "TDEOutput.Log.ParentFolderExist", parentfolder.getName() ) );
        }
      } else {
        if ( isDetailed() ) {
          logDetailed( BaseMessages.getString( PKG, "TDEOutput.Log.ParentFolderNotExist", parentfolder.getName() ) );
        }
        if ( meta.isCreateParentFolder() ) {
          parentfolder.createFolder();
          if ( isDetailed() ) {
            logDetailed( BaseMessages.getString( PKG, "TDEOutput.Log.ParentFolderCreated", parentfolder.getName() ) );
          }
        } else {
          throw new KettleException( BaseMessages.getString( PKG, "TDEOutput.Log.ParentFolderNotExistCreateIt", parentfolder.getName(), filename ) );
        }
      }
    } finally {
      if ( parentfolder != null ) {
        try {
          parentfolder.close();
        } catch ( Exception ex ) {
          //IGNORE
        }
      }
    }
  }

  private void checkFileExists( String filename ) throws Exception {
    FileObject file = null;
    FileObject parentfolder = null;

    if ( !meta.isCreateParentFolder() ) {
      parentfolder = KettleVFS.getFileObject( filename ).getParent();
      if ( !parentfolder.exists() ) {
        throw new KettleException( BaseMessages.getString( PKG, "TDEOutput.Error.ParentFolderDoesNotExist" ) );
      }
    }

    file = KettleVFS.getFileObject( filename );
    if ( !meta.isFileAppended() && file.exists() ) {
      file.delete();
      if ( isDetailed() ) {
        logDetailed( BaseMessages.getString( PKG, "TDEOutput.Log.FileDeleted", filename ) );
      }
    } else {
      if ( isDetailed() ) {
        logDetailed( BaseMessages.getString( PKG, "TDEOutput.Log.FileDoesNotExist" ) );
      }
    }
  }
}
