
/*
Copyright or © or Copr. Jason Mahdjoub (01/04/2013)

jason.mahdjoub@distri-mind.fr

This software (Object Oriented Database (OOD)) is a computer program 
whose purpose is to manage a local database with the object paradigm 
and the java langage 

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 */


package com.distrimind.ood.database.fieldaccessors;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import com.distrimind.ood.database.DatabaseRecord;
import com.distrimind.ood.database.DatabaseWrapper;
import com.distrimind.ood.database.SqlField;
import com.distrimind.ood.database.SqlFieldInstance;
import com.distrimind.ood.database.Table;
import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.ood.database.exceptions.FieldDatabaseException;

/**
 * 
 * @author Jason Mahdjoub
 * @version 1.2
 * @since OOD 1.0
 */
public class ForeignKeyFieldAccessor extends FieldAccessor
{
    protected SqlField sql_fields[]=null;
    protected ArrayList<FieldAccessor> linked_primary_keys=null; 
    protected String linked_table_name=null;
    protected Table<? extends DatabaseRecord> pointed_table=null;
    //private final Class<?>[] compatible_classes;
    
    private static Method get_record_method;
    static
    {
	try
	{
	    get_record_method=Table.class.getDeclaredMethod("getRecordFromPointingRecord", (new SqlFieldInstance[1]).getClass(), (new ArrayList<DatabaseRecord>()).getClass());
	    get_record_method.setAccessible(true);
	}
	catch (SecurityException e)
	{
	    System.err.println("Impossible to access to the function getRecordFromPointingRecord of the class Table. This is an inner bug of MadKitGroupExtension. Please contact the developers. Impossible to continue. See the next error :");
	    e.printStackTrace();
	    System.exit(-1);
	}
	catch (NoSuchMethodException e)
	{
	    System.err.println("Impossible to found to the function getRecordFromPointingRecord of the class Table. This is an inner bug of MadKitGroupExtension. Please contact the developers. Impossible to continue. See the next error :");
	    e.printStackTrace();
	    System.exit(-1);
	}
	
    }
    
    
    protected ForeignKeyFieldAccessor(Class<? extends Table<?>> table_class, DatabaseWrapper _sql_connection, Field _field, String parentFieldName) throws DatabaseException
    {
	super(_sql_connection, _field, parentFieldName, getCompatibleClasses(_field), table_class);
	if (!DatabaseRecord.class.isAssignableFrom(_field.getType()))
	    throw new DatabaseException("The field "+_field.getName()+" of the class "+_field.getDeclaringClass().getName()+" is not a DatabaseRecord.");
	if (!field.getType().getPackage().equals(field.getDeclaringClass().getPackage()))
	    throw new DatabaseException("The package of the pointed DatabaseRecord class "+field.getType().getName()+", is not the same then the package of the containing class "+field.getDeclaringClass().getName()+" (Foregin key="+field.getName()+").");
	
    }
    public void initialize() throws DatabaseException
    {
	if (sql_fields==null)
	{
	    @SuppressWarnings("unchecked")
	    Class<? extends DatabaseRecord> c=(Class<? extends DatabaseRecord>)field.getType();
	    pointed_table=sql_connection.getTableInstance(Table.getTableClass(c));
	    linked_primary_keys=pointed_table.getPrimaryKeysFieldAccessors();
	    linked_table_name=pointed_table.getName();
	    
	    ArrayList<SqlField> sql_fields=new ArrayList<SqlField>();
	    for (FieldAccessor fa : linked_primary_keys)
	    {
		if (fa.isForeignKey())
		{
		    ((ForeignKeyFieldAccessor)fa).initialize();
		}
		for (SqlField sf : fa.getDeclaredSqlFields())
		{
		    sql_fields.add(new SqlField(table_name+"."+this.getFieldName()+"__"+pointed_table.getName()+"_"+sf.short_field, sf.type, pointed_table.getName(), sf.field));
		    
		}
	    }
	    this.sql_fields=new SqlField[sql_fields.size()];
	    for (int i=0;i<sql_fields.size();i++)
		this.sql_fields[i]=sql_fields.get(i);	    
	}
    }
    
    
    public Table<? extends DatabaseRecord> getPointedTable()
    {
	return pointed_table;
    }
    @Override
    public void setValue(Object _class_instance, Object _field_instance) throws DatabaseException
    {
	try
	{
	    if (_field_instance==null)
	    {
		if (isNotNull())
		    throw new FieldDatabaseException("The given _field_instance, used to store the field "+field.getName()+" (type="+field.getType().getName()+", declaring_class="+field.getDeclaringClass().getName()+") into the DatabaseRecord class "+field.getDeclaringClass().getName()+", is null and should not be (property NotNull present).");
	    }
	    else if (!(_field_instance.getClass().equals(field.getType())))
		throw new FieldDatabaseException("The given _field_instance parameter, destinated to the field "+field.getName()+" of the class "+field.getDeclaringClass().getName()+", should be a "+field.getType().getName()+" and not a "+_field_instance.getClass().getName());
	    if (_field_instance==_class_instance)
		throw new FieldDatabaseException("The given _field_instance parameter, destinated to the field "+field.getName()+" of the class "+field.getDeclaringClass().getName()+", is the same reference than the correspondant table (autoreference).");
	    field.set(_class_instance, _field_instance);
	}
	catch(IllegalArgumentException e)
	{
	    throw new DatabaseException("Unexpected exception.",e);
	}
	catch(IllegalAccessException e)
	{
	    throw new DatabaseException("Unexpected exception.",e);
	}
    }

    @Override
    public boolean equals(Object _class_instance, Object _field_instance) throws DatabaseException
    {
	if (_field_instance!=null && !(_field_instance.getClass().equals(field.getType())))
	    return false;
	try
	{
	    
	    if (_field_instance==null && isNotNull())
		return false;
	    Object val1=(Object)field.get(_class_instance);
	    Object val2=(Object)_field_instance;
	    if (val1==val2)
		return true;
	    if (val1==null || val2==null)
		return false;
	    for (FieldAccessor fa : linked_primary_keys)
	    {
		if (!fa.equals(val1, fa.getValue(val2)))
		{
		    return false;
		}
	    }
	    return true;
	}
	catch(Exception e)
	{
	    throw new DatabaseException("",e);
	}
	
    }

    @Override
    protected boolean equals(Object _field_instance, ResultSet _result_set, SqlFieldTranslation _sft) throws DatabaseException
    {
	if (_field_instance!=null && !(_field_instance.getClass().equals(field.getType())))
	    return false;
	    
	Object val1=(Object)_field_instance;
	    
	for (FieldAccessor fa : linked_primary_keys)
	{
	    if (!fa.equals(val1, _result_set, new SqlFieldTranslation(fa, _sft)))
		return false;
	}
	return true;
    }
    
    
    private static Class<?>[] getCompatibleClasses(Field field)
    {
	Class<?> compatible_classes[]=new Class<?>[1];
	compatible_classes[0]=field.getType();

	return compatible_classes;
    }

    
    

    @Override
    public Object getValue(Object _class_instance) throws DatabaseException
    {
	try
	{
	    return field.get(_class_instance);
	}
	catch(Exception e)
	{
	    throw new DatabaseException("",e);
	}
    }

    @Override
    public SqlField[] getDeclaredSqlFields()
    {
	return sql_fields;
    }
    
    @Override
    public SqlFieldInstance[] getSqlFieldsInstances(Object _instance) throws DatabaseException
    {
	Object val=this.getValue(_instance);
	SqlFieldInstance res[]=new SqlFieldInstance[sql_fields.length];
	if (val==null)
	{
	    for (int i=0;i<sql_fields.length;i++)
		res[i]=new SqlFieldInstance(sql_fields[i], null);
	}
	else
	{
	    int i=0;
	    for (FieldAccessor fa : linked_primary_keys)
	    {
		SqlFieldInstance linked_sql_field_instances[]=fa.getSqlFieldsInstances((Object)val);
		for (SqlFieldInstance sfi : linked_sql_field_instances)
		{
		    res[i++]=new SqlFieldInstance(table_name+"."+this.getFieldName()+"__"+pointed_table.getName()+"_"+sfi.short_field, sfi.type, linked_table_name, sfi.field, sfi.instance);
		}
	    }
	}
	return res;
	
    }

    @Override
    public boolean isAlwaysNotNull()
    {
	return false;
    }

    @Override
    public boolean isComparable()
    {
	return false;
    }

    @Override
    public int compare(Object _r1, Object _r2) throws DatabaseException
    {
	throw new DatabaseException("Unexpected exception");
    }
    @Override
    public void setValue(Object _class_instance, ResultSet _result_set, ArrayList<DatabaseRecord> _pointing_records) throws DatabaseException
    {
	try
	{
	    ArrayList<DatabaseRecord> list=_pointing_records==null?new ArrayList<DatabaseRecord>():_pointing_records;
	    list.add((DatabaseRecord)_class_instance);
	    SqlField sfs[]=getDeclaredSqlFields();
	    SqlFieldInstance sfis[]=new SqlFieldInstance[sfs.length];
	    for (int i=0;i<sfs.length;i++)
	    {
		sfis[i]=new SqlFieldInstance(sfs[i], _result_set.getObject(sfs[i].short_field));
	    }
	    field.set(_class_instance, get_record_method.invoke(getPointedTable(), sfis, list));
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
	
    }
    @Override
    public void getValue(Object _class_instance, PreparedStatement _prepared_statement, int _field_start) throws DatabaseException
    {
	try
	{
	    getValue(_prepared_statement, _field_start, field.get(_class_instance));
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
    }
    
    @Override
    public void getValue(PreparedStatement _prepared_statement, int _field_start, Object o) throws DatabaseException
    {
	try
	{
	    DatabaseRecord dr=(DatabaseRecord)o;

	    for (FieldAccessor fa : linked_primary_keys)
	    {
		if (dr==null)
		{
		    for (int i=0;i<this.sql_fields.length;i++)
		    {
			_prepared_statement.setObject(_field_start++, null);
		    }
		}
		else
		{
		    fa.getValue(dr, _prepared_statement, _field_start);
		    _field_start+=fa.getDeclaredSqlFields().length;
		}
		
	    }
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
    }
        
    @Override
    public void updateValue(Object _class_instance, Object _field_instance, ResultSet _result_set) throws DatabaseException
    {
	setValue(_class_instance, _field_instance);
	try
	{
	    DatabaseRecord dr=(DatabaseRecord)field.get(_class_instance);
	    for (FieldAccessor fa : linked_primary_keys)
	    {
		
		if (dr==null)
		{
		    for (SqlField sf : sql_fields)
		    {
			_result_set.updateObject(sf.short_field, null);
		    }
		}
		else
		{
		    fa.updateResultSetValue(dr, _result_set, new SqlFieldTranslation(this));
		}
		
	    }
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
	
    }
    
    @Override
    protected void updateResultSetValue(Object _class_instance, ResultSet _result_set, SqlFieldTranslation _sft) throws DatabaseException
    {
	try
	{
	    DatabaseRecord dr=(DatabaseRecord)field.get(_class_instance);
	    for (FieldAccessor fa : linked_primary_keys)
	    {
		
		if (dr==null)
		{
		    for (SqlField sf : sql_fields)
		    {
			_result_set.updateObject(_sft.translateField(sf), null);
		    }
		}
		else
		{
		    fa.updateResultSetValue(dr, _result_set, new SqlFieldTranslation(this, _sft));
		}
		
	    }
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
    }
        
    @Override
    public boolean canBePrimaryOrUniqueKey()
    {
	return true;
    }
    
    @Override
    public void serialize(ObjectOutputStream _oos, Object _class_instance) throws DatabaseException
    {
	try
	{
	    DatabaseRecord dr=(DatabaseRecord)getValue(_class_instance);
	    if (dr!=null)
	    {
		_oos.writeBoolean(true);
		for (FieldAccessor fa : pointed_table.getFieldAccessors())
		    fa.serialize(_oos, dr);
	    }
	    else 
		_oos.writeBoolean(false);
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
    }



    @Override
    public void unserialize(ObjectInputStream _ois, HashMap<String, Object> _map) throws DatabaseException
    {
	try
	{
	    boolean isNotNull=_ois.readBoolean();
	    if (isNotNull)
	    {
		DatabaseRecord dr=pointed_table.getDefaultRecordConstructor().newInstance();
		for (FieldAccessor fa : pointed_table.getFieldAccessors())
		    fa.unserialize(_ois, dr);
		_map.put(getFieldName(), dr);
	    }
	    else if (isNotNull())
		throw new DatabaseException("field should not be null");
	    else
		_map.put(getFieldName(), null);
	    
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
    }
    @Override
    public Object unserialize(ObjectInputStream _ois, Object _classInstance) throws DatabaseException
    {
	try
	{
	    boolean isNotNull=_ois.readBoolean();
	    if (isNotNull)
	    {
		DatabaseRecord dr=pointed_table.getDefaultRecordConstructor().newInstance();
		for (FieldAccessor fa : pointed_table.getFieldAccessors())
		    fa.unserialize(_ois, dr);
		setValue(_classInstance, dr);
		return dr;
	    }
	    else if (isNotNull())
		throw new DatabaseException("field should not be null");
	    else
		return null;
	    
	}
	catch(Exception e)
	{
	    throw DatabaseException.getDatabaseException(e);
	}
    }      
    

}