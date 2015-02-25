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

import com.tableausoftware.DataExtract.Extract;
import com.tableausoftware.DataExtract.Table;
import com.tableausoftware.DataExtract.TableDefinition;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris
 * @since 2013-12-09
 */
public class TDEOutputData extends BaseStepData implements StepDataInterface
{
	public int splitnr;

	public int fieldnrs[];

	public NumberFormat nf;
	public DecimalFormat df;
	public DecimalFormatSymbols dfs;
	
	public SimpleDateFormat daf;
	public DateFormatSymbols dafs;

	public SimpleDateFormat  defaultDateFormat;
    public DateFormatSymbols defaultDateFormatSymbols;

    public Process cmdProc;

    public RowMetaInterface outputRowMeta;

	public boolean oneFileOpened;

	public List<String> previouslyOpenedFiles;
	
	public String fileName;
	
	public Boolean extractOpened;
	
	public Extract extract;
	
	public Table table;
	
	public TableDefinition tableDef;
	
	public com.tableausoftware.DataExtract.Row row;

    /**
	 * 
	 */
	public TDEOutputData()
	{
		super();
		
		nf = NumberFormat.getInstance();
		df = (DecimalFormat)nf;
		dfs=new DecimalFormatSymbols();

		daf = new SimpleDateFormat();
		dafs= new DateFormatSymbols();
        
        defaultDateFormat = new SimpleDateFormat();
        defaultDateFormatSymbols = new DateFormatSymbols();
        
        previouslyOpenedFiles = new ArrayList<String>();
       
        cmdProc = null;
        oneFileOpened=false;

        extractOpened = false;
        
       
	}
}
