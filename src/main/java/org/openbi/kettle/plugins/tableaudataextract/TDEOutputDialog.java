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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.dialog.EnterSelectionDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.ui.trans.step.TableItemInsertListener;



public class TDEOutputDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = TDEOutputMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

  private CTabFolder   wTabFolder;
  private FormData     fdTabFolder;

  private CTabItem     wFileTab, wFieldsTab;

  private FormData     fdFileComp, fdFieldsComp;

  private Label        wlFilename;
  private Button       wbFilename;
  private TextVar     wFilename;
  private FormData     fdlFilename, fdbFilename, fdFilename;


  //private Label        wlExtension;
  //private TextVar         wExtension;
  //private FormData     fdlExtension, fdExtension;

  private Label        wlAddStepnr;
  private Button       wAddStepnr;
  private FormData     fdlAddStepnr, fdAddStepnr;

  private Label        wlAddPartnr;
  private Button       wAddPartnr;
  private FormData     fdlAddPartnr, fdAddPartnr;

  private Label        wlAddDate;
  private Button       wAddDate;
  private FormData     fdlAddDate, fdAddDate;

  private Label        wlAddTime;
  private Button       wAddTime;
  private FormData     fdlAddTime, fdAddTime;

  private Button       wbShowFiles;
  private FormData     fdbShowFiles;

  private Label        wlAppend;
  private Button       wAppend;
  private FormData     fdlAppend, fdAppend;

  private TDEOutputMeta input;


  private Label        wlAddToResult;
  private Button       wAddToResult;
  private FormData     fdlAddToResult, fdAddToResult;

  private Label        wlDateTimeFormat;
  private CCombo       wDateTimeFormat;
  private FormData     fdlDateTimeFormat, fdDateTimeFormat;

  private Label        wlSpecifyFormat;
  private Button       wSpecifyFormat;
  private FormData     fdlSpecifyFormat, fdSpecifyFormat;

  private Label        wlCreateParentFolder;
  private Button       wCreateParentFolder;
  private FormData     fdlCreateParentFolder, fdCreateParentFolder;

  private TableView    wFields;
  private FormData     fdFields;

  private ColumnInfo[] colinf;

  private Map<String, Integer> inputFields;

  public TDEOutputDialog( Shell parent, Object in, TransMeta transMeta, String sname ) {
    super( parent, (BaseStepMeta) in, transMeta, sname );
    input = (TDEOutputMeta) in;
    inputFields = new HashMap<String, Integer>();
  }

  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };
    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth  = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "TDEOutputDialog.DialogTitle" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Stepname line
    wlStepname = new Label( shell, SWT.RIGHT );
    wlStepname.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
    props.setLook( wlStepname );
    fdlStepname = new FormData();
    fdlStepname.left  = new FormAttachment( 0, 0 );
    fdlStepname.top   = new FormAttachment( 0, margin );
    fdlStepname.right = new FormAttachment( middle, -margin );
    wlStepname.setLayoutData( fdlStepname );
    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepname.setText( stepname );
    props.setLook( wStepname );
    wStepname.addModifyListener( lsMod );
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment( middle, 0 );
    fdStepname.top  = new FormAttachment( 0, margin );
    fdStepname.right = new FormAttachment( 100, 0 );
    wStepname.setLayoutData( fdStepname );

    wTabFolder = new CTabFolder( shell, SWT.BORDER );
    props.setLook( wTabFolder, Props.WIDGET_STYLE_TAB );
    wTabFolder.setSimple( false );

    //////////////////////////
    // START OF FILE TAB///
    ///
    wFileTab = new CTabItem( wTabFolder, SWT.NONE );
    wFileTab.setText( BaseMessages.getString( PKG, "TDEOutputDialog.FileTab.TabTitle" ) );

    Composite wFileComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wFileComp );

    FormLayout fileLayout = new FormLayout();
    fileLayout.marginWidth  = 3;
    fileLayout.marginHeight = 3;
    wFileComp.setLayout( fileLayout );

    // Filename line
    wlFilename = new Label( wFileComp, SWT.RIGHT );
    wlFilename.setText( BaseMessages.getString( PKG, "TDEOutputDialog.Filename.Label" ) );
    props.setLook( wlFilename );
    fdlFilename = new FormData();
    fdlFilename.left = new FormAttachment( 0, 0 );
    fdlFilename.top  = new FormAttachment( 0, margin );
    fdlFilename.right = new FormAttachment( middle, -margin );
    wlFilename.setLayoutData( fdlFilename );

    wbFilename = new Button( wFileComp, SWT.PUSH | SWT.CENTER );
    props.setLook( wbFilename );
    wbFilename.setText( BaseMessages.getString( PKG, "System.Button.Browse" ) );
    fdbFilename = new FormData();
    fdbFilename.right = new FormAttachment( 100, 0 );
    fdbFilename.top  = new FormAttachment( 0, 0 );
    wbFilename.setLayoutData( fdbFilename );

    wFilename = new TextVar( transMeta, wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wFilename );
    wFilename.addModifyListener( lsMod );
    fdFilename = new FormData();
    fdFilename.left = new FormAttachment( middle, 0 );
    fdFilename.top  = new FormAttachment( 0, margin );
    fdFilename.right = new FormAttachment( wbFilename, -margin );
    wFilename.setLayoutData( fdFilename );

    /*// Extension line
    wlExtension= new Label( wFileComp, SWT.RIGHT );
    wlExtension.setText( BaseMessages.getString( PKG, "System.Label.Extension" ) );
     props.setLook( wlExtension );
    fdlExtension= new FormData();
    fdlExtension.left = new FormAttachment( 0, 0 );
    fdlExtension.top  = new FormAttachment( wFilename, margin );
    fdlExtension.right = new FormAttachment( middle, -margin );
    wlExtension.setLayoutData( fdlExtension );
    wExtension= new TextVar(transMeta, wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wExtension.setText("" );
     props.setLook( wExtension );
    wExtension.addModifyListener( lsMod );
    fdExtension= new FormData();
    fdExtension.left = new FormAttachment( middle, 0 );
    fdExtension.top  = new FormAttachment( wFilename, margin );
    fdExtension.right = new FormAttachment( 100, 0 );
    wExtension.setLayoutData( fdExtension );
    */
    // Append to end of file?
    wlAppend = new Label( wFileComp, SWT.RIGHT );
    wlAppend.setText( BaseMessages.getString( PKG, "TDEOutputDialog.Append.Label" ) );
    props.setLook( wlAppend );
    fdlAppend = new FormData();
    fdlAppend.left = new FormAttachment( 0, 0 );
    fdlAppend.top  = new FormAttachment( wFilename, margin );
    fdlAppend.right = new FormAttachment( middle, -margin );
    wlAppend.setLayoutData( fdlAppend );
    wAppend = new Button( wFileComp, SWT.CHECK );
    wAppend.setToolTipText( BaseMessages.getString( PKG, "TDEOutputDialog.Append.Tooltip" ) );
    props.setLook( wAppend );
    fdAppend = new FormData();
    fdAppend.left = new FormAttachment( middle, 0 );
    fdAppend.top  = new FormAttachment( wFilename, margin );
    fdAppend.right = new FormAttachment( 100, 0 );
    wAppend.setLayoutData( fdAppend );
    wAppend.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( SelectionEvent e ) {
          input.setChanged();
        }
      }
    );

    // Create Parent Folder
    wlCreateParentFolder = new Label( wFileComp, SWT.RIGHT );
    wlCreateParentFolder.setText( BaseMessages.getString( PKG, "TDEOutputDialog.CreateParentFolder.Label" ) );
    props.setLook( wlCreateParentFolder );
    fdlCreateParentFolder = new FormData();
    fdlCreateParentFolder.left = new FormAttachment( 0, 0 );
    fdlCreateParentFolder.top  = new FormAttachment( wAppend, margin );
    fdlCreateParentFolder.right = new FormAttachment( middle, -margin );
    wlCreateParentFolder.setLayoutData( fdlCreateParentFolder );
    wCreateParentFolder = new Button( wFileComp, SWT.CHECK );
    wCreateParentFolder.setToolTipText( BaseMessages.getString( PKG, "TDEOutputDialog.CreateParentFolder.Tooltip" ) );
    props.setLook( wCreateParentFolder );
    fdCreateParentFolder = new FormData();
    fdCreateParentFolder.left = new FormAttachment( middle, 0 );
    fdCreateParentFolder.top  = new FormAttachment( wAppend, margin );
    fdCreateParentFolder.right = new FormAttachment( 100, 0 );
    wCreateParentFolder.setLayoutData( fdCreateParentFolder );
    wCreateParentFolder.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
      }
    }
    );

    // Create multi-part file?
    wlAddStepnr = new Label( wFileComp, SWT.RIGHT );
    wlAddStepnr.setText( BaseMessages.getString( PKG, "TDEOutputDialog.AddStepnr.Label" ) );
    props.setLook( wlAddStepnr );
    fdlAddStepnr = new FormData();
    fdlAddStepnr.left = new FormAttachment( 0, 0 );
    fdlAddStepnr.top  = new FormAttachment( wCreateParentFolder, margin );
    fdlAddStepnr.right = new FormAttachment( middle, -margin );
    wlAddStepnr.setLayoutData( fdlAddStepnr );
    wAddStepnr = new Button( wFileComp, SWT.CHECK );
    props.setLook( wAddStepnr );
    fdAddStepnr = new FormData();
    fdAddStepnr.left = new FormAttachment( middle, 0 );
    fdAddStepnr.top  = new FormAttachment( wCreateParentFolder, margin );
    fdAddStepnr.right = new FormAttachment( 100, 0 );
    wAddStepnr.setLayoutData( fdAddStepnr );
    wAddStepnr.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
      }
    }
    );

    // Create multi-part file?
    wlAddPartnr = new Label( wFileComp, SWT.RIGHT );
    wlAddPartnr.setText( BaseMessages.getString( PKG, "TDEOutputDialog.AddPartnr.Label" ) );
    props.setLook( wlAddPartnr );
    fdlAddPartnr = new FormData();
    fdlAddPartnr.left = new FormAttachment( 0, 0 );
    fdlAddPartnr.top  = new FormAttachment( wAddStepnr, margin );
    fdlAddPartnr.right = new FormAttachment( middle, -margin );
    wlAddPartnr.setLayoutData( fdlAddPartnr );
    wAddPartnr = new Button( wFileComp, SWT.CHECK );
    props.setLook( wAddPartnr );
    fdAddPartnr = new FormData();
    fdAddPartnr.left = new FormAttachment( middle, 0 );
    fdAddPartnr.top  = new FormAttachment( wAddStepnr, margin );
    fdAddPartnr.right = new FormAttachment( 100, 0 );
    wAddPartnr.setLayoutData( fdAddPartnr );
    wAddPartnr.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
      }
    }
    );

    // Create multi-part file?
    wlAddDate = new Label( wFileComp, SWT.RIGHT );
    wlAddDate.setText( BaseMessages.getString( PKG, "TDEOutputDialog.AddDate.Label" ) );
    props.setLook( wlAddDate );
    fdlAddDate = new FormData();
    fdlAddDate.left = new FormAttachment( 0, 0 );
    fdlAddDate.top  = new FormAttachment( wAddPartnr, margin );
    fdlAddDate.right = new FormAttachment( middle, -margin );
    wlAddDate.setLayoutData( fdlAddDate );
    wAddDate = new Button( wFileComp, SWT.CHECK );
    props.setLook( wAddDate );
    fdAddDate = new FormData();
    fdAddDate.left = new FormAttachment( middle, 0 );
    fdAddDate.top  = new FormAttachment( wAddPartnr, margin );
    fdAddDate.right = new FormAttachment( 100, 0 );
    wAddDate.setLayoutData( fdAddDate );
    wAddDate.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
        // System.out.println("wAddDate.getSelection()="+wAddDate.getSelection() );
      }
    }
    );
    // Create multi-part file?
    wlAddTime = new Label( wFileComp, SWT.RIGHT );
    wlAddTime.setText( BaseMessages.getString( PKG, "TDEOutputDialog.AddTime.Label" ) );
    props.setLook( wlAddTime );
    fdlAddTime = new FormData();
    fdlAddTime.left = new FormAttachment( 0, 0 );
    fdlAddTime.top  = new FormAttachment( wAddDate, margin );
    fdlAddTime.right = new FormAttachment( middle, -margin );
    wlAddTime.setLayoutData( fdlAddTime );
    wAddTime = new Button( wFileComp, SWT.CHECK );
    props.setLook( wAddTime );
    fdAddTime = new FormData();
    fdAddTime.left = new FormAttachment( middle, 0 );
    fdAddTime.top  = new FormAttachment( wAddDate, margin );
    fdAddTime.right = new FormAttachment( 100, 0 );
    wAddTime.setLayoutData( fdAddTime );
    wAddTime.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
      }
    }
    );

    // Specify date time format?
    wlSpecifyFormat = new Label( wFileComp, SWT.RIGHT );
    wlSpecifyFormat.setText( BaseMessages.getString( PKG, "TDEOutputDialog.SpecifyFormat.Label" ) );
    props.setLook( wlSpecifyFormat );
    fdlSpecifyFormat = new FormData();
    fdlSpecifyFormat.left = new FormAttachment( 0, 0 );
    fdlSpecifyFormat.top  = new FormAttachment( wAddTime, margin );
    fdlSpecifyFormat.right = new FormAttachment( middle, -margin );
    wlSpecifyFormat.setLayoutData( fdlSpecifyFormat );
    wSpecifyFormat = new Button( wFileComp, SWT.CHECK );
    props.setLook( wSpecifyFormat );
    wSpecifyFormat.setToolTipText( BaseMessages.getString( PKG, "TDEOutputDialog.SpecifyFormat.Tooltip" ) );
    fdSpecifyFormat = new FormData();
    fdSpecifyFormat.left = new FormAttachment( middle, 0 );
    fdSpecifyFormat.top  = new FormAttachment( wAddTime, margin );
    fdSpecifyFormat.right = new FormAttachment( 100, 0 );
    wSpecifyFormat.setLayoutData( fdSpecifyFormat );
    wSpecifyFormat.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        input.setChanged();
        setDateTimeFormat();
      }
    }
    );

     // DateTimeFormat
    wlDateTimeFormat = new Label( wFileComp, SWT.RIGHT );
    wlDateTimeFormat.setText( BaseMessages.getString( PKG, "TDEOutputDialog.DateTimeFormat.Label" ) );
    props.setLook( wlDateTimeFormat );
    fdlDateTimeFormat = new FormData();
    fdlDateTimeFormat.left = new FormAttachment( 0, 0 );
    fdlDateTimeFormat.top  = new FormAttachment( wSpecifyFormat, margin );
    fdlDateTimeFormat.right = new FormAttachment( middle, -margin );
    wlDateTimeFormat.setLayoutData( fdlDateTimeFormat );
    wDateTimeFormat = new CCombo( wFileComp, SWT.BORDER | SWT.READ_ONLY );
    wDateTimeFormat.setEditable( true );
    props.setLook( wDateTimeFormat );
    wDateTimeFormat.addModifyListener( lsMod );
    fdDateTimeFormat = new FormData();
    fdDateTimeFormat.left = new FormAttachment( middle, 0 );
    fdDateTimeFormat.top  = new FormAttachment( wSpecifyFormat, margin );
    fdDateTimeFormat.right = new FormAttachment( 100, 0 );
    wDateTimeFormat.setLayoutData( fdDateTimeFormat );
    String[] dats = Const.getDateFormats();
    for ( int x = 0; x < dats.length; x++ ) {
      wDateTimeFormat.add( dats[x] );
    }

    wbShowFiles = new Button( wFileComp, SWT.PUSH | SWT.CENTER );
    props.setLook( wbShowFiles );
    wbShowFiles.setText( BaseMessages.getString( PKG, "TDEOutputDialog.ShowFiles.Button" ) );
    fdbShowFiles = new FormData();
    fdbShowFiles.left = new FormAttachment( middle, 0 );
    fdbShowFiles.top  = new FormAttachment( wDateTimeFormat, margin * 2 );
    wbShowFiles.setLayoutData( fdbShowFiles );
    wbShowFiles.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        TDEOutputMeta tfoi = new TDEOutputMeta();
        getInfo( tfoi );
        String[] files = tfoi.getFiles( transMeta );
        if ( files != null && files.length > 0 ) {
          EnterSelectionDialog esd = new EnterSelectionDialog( shell, files, BaseMessages.getString( PKG, "TDEOutputDialog.SelectOutputFiles.DialogTitle" ), BaseMessages.getString( PKG, "TDEOutputDialog.SelectOutputFiles.DialogMessage" ) );
          esd.setViewOnly();
          esd.open();
        } else {
          MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
          mb.setMessage( BaseMessages.getString( PKG, "TDEOutputDialog.NoFilesFound.DialogMessage" ) );
          mb.setText( BaseMessages.getString( PKG, "System.Dialog.Error.Title" ) );
          mb.open();
        }
      }
    }
    );


    // Add File to the result files name
    wlAddToResult = new Label( wFileComp, SWT.RIGHT );
    wlAddToResult.setText( BaseMessages.getString( PKG, "TDEOutputDialog.AddFileToResult.Label" ) );
    props.setLook( wlAddToResult );
    fdlAddToResult = new FormData();
    fdlAddToResult.left  = new FormAttachment( 0, 0 );
    fdlAddToResult.top   = new FormAttachment( wbShowFiles, 2 * margin );
    fdlAddToResult.right = new FormAttachment( middle, -margin );
    wlAddToResult.setLayoutData( fdlAddToResult );
    wAddToResult = new Button( wFileComp, SWT.CHECK );
    wAddToResult.setToolTipText( BaseMessages.getString( PKG, "TDEOutputDialog.AddFileToResult.Tooltip" ) );
    props.setLook( wAddToResult );
    fdAddToResult = new FormData();
    fdAddToResult.left  = new FormAttachment( middle, 0 );
    fdAddToResult.top   = new FormAttachment( wbShowFiles, 2 * margin );
    fdAddToResult.right = new FormAttachment( 100, 0 );
    wAddToResult.setLayoutData( fdAddToResult );
    SelectionAdapter lsSelR = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        input.setChanged();
      }
    };
    wAddToResult.addSelectionListener( lsSelR );

    fdFileComp = new FormData();
    fdFileComp.left  = new FormAttachment( 0, 0 );
    fdFileComp.top   = new FormAttachment( 0, 0 );
    fdFileComp.right = new FormAttachment( 100, 0 );
    fdFileComp.bottom = new FormAttachment( 100, 0 );
    wFileComp.setLayoutData( fdFileComp );

    wFileComp.layout();
    wFileTab.setControl( wFileComp );

    /////////////////////////////////////////////////////////////
    /// END OF FILE TAB
    /////////////////////////////////////////////////////////////


    // Fields tab...
    //
    wFieldsTab = new CTabItem( wTabFolder, SWT.NONE );
    wFieldsTab.setText( BaseMessages.getString( PKG, "TDEOutputDialog.FieldsTab.TabTitle" ) );

    FormLayout fieldsLayout = new FormLayout();
    fieldsLayout.marginWidth  = Const.FORM_MARGIN;
    fieldsLayout.marginHeight = Const.FORM_MARGIN;

    Composite wFieldsComp = new Composite( wTabFolder, SWT.NONE );
    wFieldsComp.setLayout( fieldsLayout );
    props.setLook( wFieldsComp );

    wGet = new Button( wFieldsComp, SWT.PUSH );
    wGet.setText( BaseMessages.getString( PKG, "System.Button.GetFields" ) );
    wGet.setToolTipText( BaseMessages.getString( PKG, "System.Tooltip.GetFields" ) );

    setButtonPositions( new Button[] { wGet}, margin, null );

    final int FieldsCols = 3;
    final int FieldsRows = input.getOutputFields().length;

    // Prepare a list of possible formats...
    String[] nums = Const.getNumberFormats();
    int totsize = dats.length + nums.length;
    String[] formats = new String[totsize];
    for ( int x = 0; x < dats.length; x++ ) {
      formats[x] = dats[x];
    }
    for ( int x = 0; x < nums.length; x++ ) {
      formats[dats.length + x] = nums[x];
    }

    colinf = new ColumnInfo[FieldsCols];
    colinf[0] = new ColumnInfo( BaseMessages.getString( PKG, "TDEOutputDialog.NameColumn.Column" ),       ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { "" }, false );
    colinf[1] = new ColumnInfo( BaseMessages.getString( PKG, "TDEOutputDialog.TDENewNameColumn.Column" ),       ColumnInfo.COLUMN_TYPE_TEXT, false );
    colinf[2] = new ColumnInfo( BaseMessages.getString( PKG, "TDEOutputDialog.TDETypeColumn.Column" ),       ColumnInfo.COLUMN_TYPE_CCOMBO, TDEField.getTypes(), false );

    wFields = new TableView( transMeta, wFieldsComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, lsMod, props );

    fdFields = new FormData();
    fdFields.left  = new FormAttachment( 0, 0 );
    fdFields.top   = new FormAttachment( 0, 0 );
    fdFields.right = new FormAttachment( 100, 0 );
    fdFields.bottom = new FormAttachment( wGet, -margin );
    wFields.setLayoutData( fdFields );

    //
    // Search the fields in the background
    final Runnable runnable = new Runnable() {
      public void run() {
        StepMeta stepMeta = transMeta.findStep( stepname );
        if ( stepMeta != null ) {
          try {
            RowMetaInterface row = transMeta.getPrevStepFields( stepMeta );
            // Remember these fields...
            for ( int i = 0; i < row.size(); i++ ) {
                inputFields.put( row.getValueMeta( i ).getName(), Integer.valueOf( i ) );
            }
            setComboBoxes();
          } catch ( KettleException e ) {
            logError( BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ) );
          }
        }
      }
    };
    new Thread( runnable ).start();

    fdFieldsComp = new FormData();
    fdFieldsComp.left  = new FormAttachment( 0, 0 );
    fdFieldsComp.top   = new FormAttachment( 0, 0 );
    fdFieldsComp.right = new FormAttachment( 100, 0 );
    fdFieldsComp.bottom = new FormAttachment( 100, 0 );
    wFieldsComp.setLayoutData( fdFieldsComp );

    wFieldsComp.layout();
    wFieldsTab.setControl( wFieldsComp );

    fdTabFolder = new FormData();
    fdTabFolder.left  = new FormAttachment( 0, 0 );
    fdTabFolder.top   = new FormAttachment( wStepname, margin );
    fdTabFolder.right = new FormAttachment( 100, 0 );
    fdTabFolder.bottom = new FormAttachment( 100, -50 );
    wTabFolder.setLayoutData( fdTabFolder );

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );

    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOK, wCancel }, margin, wTabFolder );

    // Add listeners
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };
    lsGet = new Listener() {
      public void handleEvent( Event e ) {
        get();
      }
    };
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };

    wOK.addListener( SWT.Selection, lsOK );
    wGet.addListener( SWT.Selection, lsGet );
    wCancel.addListener( SWT.Selection, lsCancel );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wStepname.addSelectionListener( lsDef );
    wFilename.addSelectionListener( lsDef );

    // Whenever something changes, set the tooltip to the expanded version:
    wFilename.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        wFilename.setToolTipText( transMeta.environmentSubstitute( wFilename.getText() ) );
      }
    }
    );

    wbFilename.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        FileDialog dialog = new FileDialog( shell, SWT.SAVE );
        dialog.setFilterExtensions( new String[] { "*.tde", "*" } );
        if ( wFilename.getText() != null ) {
          dialog.setFileName( transMeta.environmentSubstitute( wFilename.getText() ) );
        }
        dialog.setFilterNames( new String[] { BaseMessages.getString( PKG, "TDEOutput.FileType.TDEFiles" ), BaseMessages.getString( PKG, "System.FileType.AllFiles" ) } );
        if ( dialog.open() != null ) {
          String extension = "tde";
          if ( extension != null && dialog.getFileName() != null && dialog.getFileName().endsWith( "." + extension ) ) {
            // The extension is filled in and matches the end 
            // of the selected file => Strip off the extension.
            String fileName = dialog.getFileName();
            wFilename.setText( dialog.getFilterPath() + System.getProperty( "file.separator" ) + fileName.substring( 0, fileName.length() - ( extension.length() + 1 ) ) );
          } else {
            wFilename.setText( dialog.getFilterPath() + System.getProperty( "file.separator" ) + dialog.getFileName() );
          }
        }
      }
    }
    );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener(  new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    lsResize = new Listener() {
      public void handleEvent( Event event ) {
        Point size = shell.getSize();
        wFields.setSize( size.x - 10, size.y - 50 );
        wFields.table.setSize( size.x - 10, size.y - 50 );
        wFields.redraw();
      }
    };
    shell.addListener( SWT.Resize, lsResize );

    wTabFolder.setSelection( 0 );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  protected void setComboBoxes() {
    // Something was changed in the row.
    //
    final Map<String, Integer> fields = new HashMap<String, Integer>();

    // Add the currentMeta fields...
    fields.putAll( inputFields );

    Set<String> keySet = fields.keySet();
    List<String> entries = new ArrayList<String>( keySet );

    String[] fieldNames = (String[]) entries.toArray( new String[entries.size()] );

    Const.sortStrings( fieldNames );
    colinf[0].setComboValues( fieldNames );
  }

  private void setDateTimeFormat() {
    if ( wSpecifyFormat.getSelection() ) {
      wAddDate.setSelection( false );
      wAddTime.setSelection( false );
    }

    wDateTimeFormat.setEnabled( wSpecifyFormat.getSelection() );
    wlDateTimeFormat.setEnabled( wSpecifyFormat.getSelection() );
    wlAddDate.setEnabled( !( wSpecifyFormat.getSelection() ) );
    wAddTime.setEnabled( !( wSpecifyFormat.getSelection() ) );
    wlAddTime.setEnabled( !( wSpecifyFormat.getSelection() ) );
  }

 /*   private void getFields()
   {
    if(!gotPreviousFields)
    {
     try{
       String field =wFileNameField.getText();
       RowMetaInterface r = transMeta.getPrevStepFields(stepname );
       if(r!=null)
        {
         wFileNameField.setItems(r.getFieldNames() );
        }
       if( field!=null) wFileNameField.setText( field);
       }catch(KettleException ke ){
        new ErrorDialog( shell, BaseMessages.getString( PKG, "TDEOutputDialog.FailedToGetFields.DialogTitle" ), BaseMessages.getString( PKG, "TDEOutputDialog.FailedToGetFields.DialogMessage" ), ke );
      }
       gotPreviousFields=true;
    }
   } */

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    if ( input.getFileName()  != null ) {
      wFilename.setText( input.getFileName() );
    }
    wCreateParentFolder.setSelection( input.isCreateParentFolder() );
    //.setText(Const.NVL( input.getExtension(), "" ) );

    wAddDate.setSelection( input.isDateInFilename() );
    wAddTime.setSelection( input.isTimeInFilename() );
    wDateTimeFormat.setText( Const.NVL( input.getDateTimeFormat(), "" ) );
    wSpecifyFormat.setSelection( input.isSpecifyingFormat() );

    wAppend.setSelection( input.isFileAppended() );
    wAddStepnr.setSelection( input.isStepNrInFilename() );
    wAddPartnr.setSelection( input.isPartNrInFilename() );
    wAddToResult.setSelection( input.isAddToResultFiles() );

    logDebug( "getting fields info..." );

    for ( int i = 0; i < input.getOutputFields().length; i++ ) {
      TDEField field = input.getOutputFields()[i];
      TableItem item = wFields.table.getItem( i );
      if ( field.getName() != null ) {
        item.setText( 1, field.getName() );
      }
      if ( field.getNewName() != null ) {
        item.setText( 2, field.getNewName() );
      }
      item.setText( 3, Const.NVL( field.getTdeTypeDesc(), "-" ) );
    }

    wFields.optWidth( true );
    wStepname.selectAll();
  }

  private void cancel() {
    stepname = null;

    input.setChanged( backupChanged );

    dispose();
  }

  private void getInfo( TDEOutputMeta tfoi ) {
    tfoi.setFileName(   wFilename.getText() );
    tfoi.setCreateParentFolder( wCreateParentFolder.getSelection() );
    //tfoi.setExtension(  wExtension.getText() );
    tfoi.setFileAppended( wAppend.getSelection() );
    tfoi.setStepNrInFilename( wAddStepnr.getSelection() );
    tfoi.setPartNrInFilename( wAddPartnr.getSelection() );
    tfoi.setDateInFilename( wAddDate.getSelection() );
    tfoi.setTimeInFilename( wAddTime.getSelection() );
    tfoi.setDateTimeFormat( wDateTimeFormat.getText() );
    tfoi.setSpecifyingFormat( wSpecifyFormat.getSelection() );
    tfoi.setAddToResultFiles( wAddToResult.getSelection() );

    int i;
    //Table table = wFields.table;

    int nrfields = wFields.nrNonEmpty();

    tfoi.allocate( nrfields );

    for ( i = 0; i < nrfields; i++ ) {
      TDEField field = new TDEField();

      TableItem item = wFields.getNonEmpty( i );
      field.setName( item.getText( 1 ) );
      field.setNewName( item.getText( 2 ) );
      field.setTdeType( item.getText( 3 ) );
      tfoi.getOutputFields()[i]  = field;
    }
  }

  private void ok() {
    if ( Utils.isEmpty( wStepname.getText() ) ) {
      return;
    }

    stepname = wStepname.getText(); // return value

    getInfo( input );

    if ( "Y".equalsIgnoreCase( props.getCustomParameter( "TABLEAU_OUTPUT_WINDOWS_WARNING", "Y" ) ) ) {
      MessageDialogWithToggle md = new MessageDialogWithToggle( shell, BaseMessages.getString( PKG,
          "TDEOutput.TDEWindowsWarning.DialogTitle" ),
          null, BaseMessages.getString( PKG, "TDEOutput.TDEWindowsWarning.DialogMessage", Const.CR ) + Const.CR,
          MessageDialog.WARNING, new String[] { BaseMessages.getString( PKG,
              "TDEOutput.TDEWindowsWarning.Option1" ) },
          0, BaseMessages.getString( PKG, "TDEOutput.TDEWindowsWarning.Option2" ),
          "N".equalsIgnoreCase( props.getCustomParameter( "TABLEAU_OUTPUT_WINDOWS_WARNING", "Y" ) )
      );
      MessageDialogWithToggle.setDefaultImage( GUIResource.getInstance().getImageSpoon() );
      md.open();
      props.setCustomParameter( "TABLEAU_OUTPUT_WINDOWS_WARNING", md.getToggleState() ? "N" : "Y" );
      props.saveProps();
    }

    dispose();
  }

  private void get() {
    try {
      RowMetaInterface r = transMeta.getPrevStepFields( stepname );
      if ( r != null ) {
        TableItemInsertListener listener = new TableItemInsertListener() {
          public boolean tableItemInserted( TableItem tableItem, ValueMetaInterface v ) {
            TDEField f = new TDEField();

            tableItem.setText( 3, Const.NVL( f.getTdeTypeDesc( TDEField.getDefaultTdeType( v.getType() ) ), "" ) ); //$NON-NLS-1$
            return true;
          }
        };
        BaseStepDialog.getFieldsFromPrevious( r, wFields, 1, new int[] { 1 }, null, -1, -1, listener );
      }
    } catch ( KettleException ke ) {
      new ErrorDialog( shell, BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Title" ), BaseMessages.getString( PKG, "System.Dialog.GetFieldsFailed.Message" ), ke );
    }

  }

  /**
   * Sets the output width to minimal width...
   *
   */

}
