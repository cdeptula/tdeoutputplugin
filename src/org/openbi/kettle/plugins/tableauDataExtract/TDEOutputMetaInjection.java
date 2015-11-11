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

package org.openbi.kettle.plugins.tableauDataExtract;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.step.StepInjectionUtil;
import org.pentaho.di.trans.step.StepMetaInjectionEntryInterface;
import org.pentaho.di.trans.step.StepMetaInjectionInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This takes care of the external metadata injection into the TDEOutputMeta class
 *
 * @author Chris
 */
public class TDEOutputMetaInjection implements StepMetaInjectionInterface {

  private enum Entry implements StepMetaInjectionEntryInterface {

    EXTRACT_NAME( ValueMetaInterface.TYPE_STRING, "The name of the extract file" ), APPEND(
      ValueMetaInterface.TYPE_STRING, "Append to the existing file? (Y/N)" ), CREATE_PARENT_FOLDER(
      ValueMetaInterface.TYPE_STRING, "Create parent folder? (Y/N)" ), INCLUDE_STEPNR(
      ValueMetaInterface.TYPE_STRING, "Include Step nr in filename? (Y/N)" ),
      INCLUDE_PARTITION_NR( ValueMetaInterface.TYPE_STRING, "Include partition number in filename? (Y/N)" ), 
      INCLUDE_DATE( ValueMetaInterface.TYPE_STRING, "Include date in filename? (Y/N)" ), 
      INCLUDE_TIME( ValueMetaInterface.TYPE_STRING, "Include time in filename? (Y/N)" ),
      SPECIFY_DATE_FORMAT( ValueMetaInterface.TYPE_STRING, "Specify date time format? (Y/N)" ), 
      DATE_TIME_FORMAT(ValueMetaInterface.TYPE_STRING, "Date time format" ), ADD_FILENAME_TO_RESULT(
      ValueMetaInterface.TYPE_STRING, "Add filenames to result? (Y/N)" ),
      
      EXTRACT_FIELDS( ValueMetaInterface.TYPE_NONE, "The fields to add to the extract" ), EXTRACT_FIELD(
        ValueMetaInterface.TYPE_NONE, "One field to add to the extract" ), EXTRACT_FIELDNAME(
        ValueMetaInterface.TYPE_STRING, "Field to add to the extract" ), EXTRACT_RENAME_TO(
        ValueMetaInterface.TYPE_STRING, "Rename field to" ),
        EXTRACT_TYPE( ValueMetaInterface.TYPE_STRING,"Tableau type for the field (Boolean, Character String, Date, " +
        "Date Time, Double, Duration, Integer, Unicode String)" );

    private int valueType;
    private String description;

    private Entry( int valueType, String description ) {
      this.valueType = valueType;
      this.description = description;
    }

    /**
     * @return the valueType
     */
    public int getValueType() {
      return valueType;
    }

    /**
     * @return the description
     */
    public String getDescription() {
      return description;
    }

    public static Entry findEntry( String key ) {
      return Entry.valueOf( key );
    }
  }

  private TDEOutputMeta meta;

  public TDEOutputMetaInjection( TDEOutputMeta meta ) {
    this.meta = meta;
  }

  @Override
  public List<StepInjectionMetaEntry> getStepInjectionMetadataEntries() throws KettleException {
    List<StepInjectionMetaEntry> all = new ArrayList<StepInjectionMetaEntry>();

    Entry[] topEntries =
      new Entry[] {
        Entry.EXTRACT_NAME, Entry.APPEND, Entry.CREATE_PARENT_FOLDER, Entry.INCLUDE_STEPNR,
        Entry.INCLUDE_PARTITION_NR, Entry.INCLUDE_DATE, Entry.INCLUDE_TIME, 
        Entry.SPECIFY_DATE_FORMAT, Entry.DATE_TIME_FORMAT, Entry.ADD_FILENAME_TO_RESULT, };
    for ( Entry topEntry : topEntries ) {
      all.add( new StepInjectionMetaEntry( topEntry.name(), topEntry.getValueType(), topEntry.getDescription() ) );
    }

    // The fields
    //
    StepInjectionMetaEntry fieldsEntry =
      new StepInjectionMetaEntry(
        Entry.EXTRACT_FIELDS.name(), ValueMetaInterface.TYPE_NONE, Entry.EXTRACT_FIELDS.description );
    all.add( fieldsEntry );
    StepInjectionMetaEntry fieldEntry =
      new StepInjectionMetaEntry(
        Entry.EXTRACT_FIELD.name(), ValueMetaInterface.TYPE_NONE, Entry.EXTRACT_FIELD.description );
    fieldsEntry.getDetails().add( fieldEntry );

    Entry[] fieldsEntries = new Entry[] { Entry.EXTRACT_FIELDNAME, Entry.EXTRACT_RENAME_TO, Entry.EXTRACT_TYPE, };
    for ( Entry entry : fieldsEntries ) {
      StepInjectionMetaEntry metaEntry =
        new StepInjectionMetaEntry( entry.name(), entry.getValueType(), entry.getDescription() );
      fieldEntry.getDetails().add( metaEntry );
    }

    return all;
  }

  @Override
  public void injectStepMetadataEntries( List<StepInjectionMetaEntry> all ) throws KettleException {

    List<String> extractFields = new ArrayList<String>();
    List<String> extractRenameTos = new ArrayList<String>();
    List<String> extractTypes = new ArrayList<String>();

    // Parse the fields, inject into the meta class..
    //
    for ( StepInjectionMetaEntry lookFields : all ) {
      Entry fieldsEntry = Entry.findEntry( lookFields.getKey() );
      if ( fieldsEntry == null ) {
        continue;
      }

      String lookValue = (String) lookFields.getValue();
      switch ( fieldsEntry ) {
        case EXTRACT_FIELDS:
          for ( StepInjectionMetaEntry lookField : lookFields.getDetails() ) {
            Entry fieldEntry = Entry.findEntry( lookField.getKey() );
            if ( fieldEntry == Entry.EXTRACT_FIELD ) {

              String extractField = null;
              String extractRenameTo = null;
              String extractType = null;
                            
              List<StepInjectionMetaEntry> entries = lookField.getDetails();
              for ( StepInjectionMetaEntry entry : entries ) {
                Entry metaEntry = Entry.findEntry( entry.getKey() );
                if ( metaEntry != null ) {
                  String value = (String) entry.getValue();
                  switch ( metaEntry ) {
                    case EXTRACT_FIELDNAME:
                      extractField = value;
                      break;
                    case EXTRACT_RENAME_TO:
                      extractRenameTo = value;
                      break;
                    case EXTRACT_TYPE:
                      extractType = value;
                      break;
                   default:
                      break;
                  }
                }
              }
              extractFields.add( extractField );
              extractRenameTos.add( extractRenameTo );
              extractTypes.add( extractType );
            }
          }
          break;

        case EXTRACT_NAME:
          meta.setFileName( lookValue );
          break;
        case APPEND:
          meta.setFileAppended( "Y".equalsIgnoreCase( lookValue ) );
          break;
        case CREATE_PARENT_FOLDER:
          meta.setCreateParentFolder( "Y".equalsIgnoreCase( lookValue ) );
          break;
        case INCLUDE_STEPNR:
          meta.setStepNrInFilename( "Y".equalsIgnoreCase( lookValue ) );
          break;
        case INCLUDE_PARTITION_NR:
          meta.setPartNrInFilename( "Y".equalsIgnoreCase( lookValue ) );
          break;
        case INCLUDE_DATE:
          meta.setDateInFilename( "Y".equalsIgnoreCase( lookValue ) );
          break;
        case INCLUDE_TIME:
          meta.setTimeInFilename( "Y".equalsIgnoreCase( lookValue ) );
          break;
        case SPECIFY_DATE_FORMAT:
          meta.setSpecifyingFormat( "Y".equalsIgnoreCase( lookValue ) );
          break;
        case DATE_TIME_FORMAT:
          meta.setDateTimeFormat( lookValue );
          break;
        case ADD_FILENAME_TO_RESULT:
        	meta.setAddToResultFiles( "Y".equalsIgnoreCase( lookValue ) );
          break;
        default:
          break;
      }
    }

    // Pass the grid to the step metadata
    //
    if ( extractFields.size() > 0 ) {
      TDEField[] tf = new TDEField[extractFields.size()];

      Iterator<String> itExtractFields = extractFields.iterator();
      Iterator<String> itExtractRenameTos = extractRenameTos.iterator();
      Iterator<String> itExtractTypes = extractTypes.iterator();
      int i=0;
      while(itExtractFields.hasNext())
      {
    	  tf[i] = new TDEField(itExtractFields.next(), itExtractRenameTos.next(), itExtractTypes.next());
    	  i++;
      }
      meta.setOutputFields(tf);
    }
  }

  public List<StepInjectionMetaEntry> extractStepMetadataEntries() {
    List<StepInjectionMetaEntry> list = new ArrayList<StepInjectionMetaEntry>();

    list.add( StepInjectionUtil.getEntry( Entry.EXTRACT_NAME, meta.getFileName() ) );
    list.add( StepInjectionUtil.getEntry( Entry.APPEND, meta.isFileAppended() ) );
    list.add( StepInjectionUtil.getEntry( Entry.CREATE_PARENT_FOLDER, meta.isCreateParentFolder() ) );
    list.add( StepInjectionUtil.getEntry( Entry.INCLUDE_STEPNR, meta.isStepNrInFilename() ) );
    list.add( StepInjectionUtil.getEntry( Entry.INCLUDE_PARTITION_NR, meta.isPartNrInFilename() ) );
    list.add( StepInjectionUtil.getEntry( Entry.INCLUDE_DATE, meta.isDateInFilename() ) );
    list.add( StepInjectionUtil.getEntry( Entry.INCLUDE_TIME, meta.isTimeInFilename() ) );
    list.add( StepInjectionUtil.getEntry( Entry.SPECIFY_DATE_FORMAT, meta.isSpecifyingFormat() ) );
    list.add( StepInjectionUtil.getEntry( Entry.DATE_TIME_FORMAT, meta.getDateTimeFormat() ) );
    list.add( StepInjectionUtil.getEntry( Entry.ADD_FILENAME_TO_RESULT, meta.isAddToResultFiles() ) );

    StepInjectionMetaEntry fieldsEntry = StepInjectionUtil.getEntry( Entry.EXTRACT_FIELDS );
    list.add( fieldsEntry );
    for( int i = 0; i < meta.getOutputFields().length; i++ ) {
      StepInjectionMetaEntry fieldEntry = StepInjectionUtil.getEntry( Entry.EXTRACT_FIELD );
      List<StepInjectionMetaEntry> details = fieldEntry.getDetails();
      details.add( StepInjectionUtil.getEntry( Entry.EXTRACT_FIELDNAME, meta.getOutputFields()[i].getName() ) );
      details.add( StepInjectionUtil.getEntry( Entry.EXTRACT_RENAME_TO, meta.getOutputFields()[i].getOutputName() ) );
      details.add( StepInjectionUtil.getEntry( Entry.EXTRACT_TYPE, meta.getOutputFields()[i].getTdeType() ) );
    }

    return list;
  }

  public TDEOutputMeta getMeta() {
    return meta;
  }
}
