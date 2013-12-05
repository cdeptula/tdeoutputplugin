/*******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2012 by Pentaho : http://www.pentaho.com
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

import java.util.Arrays;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMeta;

import com.tableausoftware.DataExtract.Type;

/**
 * Describes a single field in a text file
 * 
 * @author Matt
 * @since 11-05-2005
 * 
 */
public class TDEField implements Cloneable
{

	private String name; 
	
	private String newName;

	private int tdeType;
	
	private static List<String> tdeTypeNames = Arrays.asList("Boolean",
				"Character String",
				"Date",
				"Date Time",
				"Double",
				"Duration",
				"Integer",
				"Unicode String",
				"-");
	/* private static List<String> tdeTypeMessages = Arrays.asList("tdeOutput.type.boolean",
			"tdeOutput.type.char_string",
			"tdeOutput.type.date",
			"tdeOutput.type.date_time",
			"tdeOutput.type.double",
			"tdeOutput.type.duration",
			"tdeOutput.type.integer",
			"tdOutput.type.unicode_string");
	*/
	
	public static int BOOLEAN=0;
	public static int CHAR_STRING=1;
	public static int DATE=2;
	public static int DATE_TIME=3;
	public static int DOUBLE=4;
	public static int DURATION=5;
	public static int INTEGER=6;
	public static int UNICODE_STRING=7;
	public static int NONE=8;
	
	/*public static Type BOOLEAN_TYPE=Type.BOOLEAN;
	public static Type CHAR_STRING_TYPE=Type.CHAR_STRING;
	public static Type DATE_TYPE=Type.DATE;
	public static Type DATE_TIME_TYPE=Type.DATETIME;
	public static Type DOUBLE_TYPE=Type.DOUBLE;
	public static Type DURATION_TYPE=Type.DURATION;
	public static Type INTEGER_TYPE=Type.INTEGER;
	public static Type UNICODE_STRING_TYPE=Type.UNICODE_STRING;
	*/
	

	public TDEField(String name, int tdeType)
	{
		this.name = name;
		this.tdeType = tdeType;
	}

	public TDEField()
	{
	}

	public int compare(Object obj)
	{
		TDEField field = (TDEField) obj;

		return name.compareTo(field.getName());
	}

	public boolean equal(Object obj)
	{
		TDEField field = (TDEField) obj;

		return name.equals(field.getName());
	}

	public Object clone()
	{
		try
		{
			Object retval = super.clone();
			return retval;
		} catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

	
	public String getName()
	{
		return name;
	}

	public void setName(String fieldname)
	{
		this.name = fieldname;
	}
	
	public String getNewName()
	{
		return newName;
	}
	
	public void setNewName(String fieldNewName)
	{
		this.newName = fieldNewName;
	}
	
	public String getOutputName()
	{
		if(newName != null) return newName;
		return name;
	}
	
	public int getTdeType()
	{
		return tdeType;
	}

	public String getTdeTypeDesc()
	{
		return tdeTypeNames.get(tdeType);
	}
	
	public String getTdeTypeDesc(int tdeType)
	{
		return tdeTypeNames.get(tdeType);
	}
	
	public Type getTdeTypeValue(int tdeType) throws KettleException
	{
		if(tdeType==BOOLEAN)
			return Type.BOOLEAN;
		else if(tdeType==CHAR_STRING)
			return Type.CHAR_STRING;
		else if(tdeType==DATE)
			return Type.DATE;
		else if(tdeType==DATE_TIME)
			return Type.DATETIME;
		else if(tdeType==DOUBLE)
			return Type.DOUBLE;
		else if(tdeType==DURATION)
			return Type.DURATION;
		else if(tdeType==INTEGER)
			return Type.INTEGER;
		else if(tdeType==UNICODE_STRING)
			return Type.UNICODE_STRING;
		
		throw new KettleException("Field does not have a type defined.");
		
		
	} 
	
	public static int getDefaultTdeType(int type)
	{
		if(type == ValueMeta.TYPE_BIGNUMBER || type == ValueMeta.TYPE_NUMBER)
		{
			return DOUBLE;
		} else if (type==ValueMeta.TYPE_BOOLEAN)
		{
			return BOOLEAN;
		} else if (type==ValueMeta.TYPE_INTEGER)
		{
			return INTEGER;
		} else if (type==ValueMeta.TYPE_DATE || type==ValueMeta.TYPE_TIMESTAMP)
		{
			return DATE_TIME;
		} else {
			return CHAR_STRING;
		}
	}

	public void setTdeType(int tdeType)
	{
		this.tdeType = tdeType;
	}

	public void setTdeType(String tdeTypeDesc)
	{
		if(tdeTypeNames.indexOf(tdeTypeDesc)!=-1)
		{
			this.tdeType = tdeTypeNames.indexOf(tdeTypeDesc);
		} else {
			this.tdeType = NONE;
		}
				
	}

	
	public String toString()
	{
		return name + ":" + getTdeTypeDesc();
	}
	
	public static String[] getTypes()
	{
		String[] types= new String[tdeTypeNames.size()-1];
		int arrayIndex=0;
		for (int i=0; i<tdeTypeNames.size();i++)
		{
			if(i!=NONE)
			{
				types[arrayIndex]=tdeTypeNames.get(i);
				arrayIndex++;
			}
		}
		
		return types;
	}

	
}
