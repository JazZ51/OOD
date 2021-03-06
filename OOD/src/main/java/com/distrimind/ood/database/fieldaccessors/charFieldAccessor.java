
/*
Copyright or © or Copr. Jason Mahdjoub (01/04/2013)

jason.mahdjoub@distri-mind.fr

This software (Object Oriented Database (OOD)) is a computer program 
whose purpose is to manage a local database with the object paradigm 
and the java language

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

import com.distrimind.ood.database.*;
import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.ood.database.exceptions.DatabaseIntegrityException;
import com.distrimind.ood.database.exceptions.FieldDatabaseException;
import com.distrimind.util.io.RandomInputStream;
import com.distrimind.util.io.RandomOutputStream;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * @author Jason Mahdjoub
 * @version 1.2
 * @since OOD 1.0
 */
public class charFieldAccessor extends FieldAccessor {
	protected final SqlField[] sql_fields;

	protected charFieldAccessor(Table<?> table, DatabaseWrapper _sql_connection, Field _field,
			String parentFieldName, boolean severalPrimaryKeysPresentIntoTable) throws DatabaseException {
		super(_sql_connection, _field, parentFieldName, compatible_classes, table, severalPrimaryKeysPresentIntoTable);
		sql_fields = new SqlField[1];
		sql_fields[0] = new SqlField(supportQuotes, table_name + "." + this.getSqlFieldName(), "CHARACTER", isNotNull());
	}

	@Override
	public void setValue(Object _class_instance, Object _field_instance) throws DatabaseException {
		if (_field_instance == null) {
			throw new FieldDatabaseException("The given _field_instance, used to store the field " + field.getName()
					+ " (type=" + field.getType().getName() + ", declaring_class=" + field.getDeclaringClass().getName()
					+ ") into the DatabaseField class " + field.getDeclaringClass().getName()
					+ ", is null and should not be (property NotNull present).");
		}
		try {
			if (_field_instance instanceof Character)
				field.setChar(_class_instance, (Character) _field_instance);
			else
				throw new FieldDatabaseException("The given _field_instance parameter, destined to the field "
						+ field.getName() + " of the class " + field.getDeclaringClass().getName()
						+ ", should be a Character and not a " + _field_instance.getClass().getName());

		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public boolean equals(Object _class_instance, Object _field_instance) throws DatabaseException {
		if (_field_instance == null) {
			return false;
		}
		try {
			char val = field.getChar(_class_instance);
			if (_field_instance instanceof Character)
				return val == (Character) _field_instance;
			return false;
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	private static final Class<?>[] compatible_classes = { char.class, Character.class };

	@Override
	public Object getValue(Object _class_instance) throws DatabaseException {
		try {
			return field.getChar(_class_instance);
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public SqlField[] getDeclaredSqlFields() {
		return sql_fields;
	}

	@Override
	public SqlFieldInstance[] getSqlFieldsInstances(String sqlTableName, Object _instance) throws DatabaseException {
		SqlFieldInstance[] res = new SqlFieldInstance[1];
		res[0] = new SqlFieldInstance(supportQuotes,sqlTableName,  sql_fields[0], getValue(_instance));
		return res;
	}

	@Override
	public boolean isAlwaysNotNull() {
		return true;
	}

	@Override
	public boolean isComparable() {
		return true;
	}

	@Override
	public int compare(Object _r1, Object _r2) throws DatabaseException {
		try {
			char val1 = field.getChar(_r1);
			char val2 = field.getChar(_r2);
			return Character.compare(val1, val2);
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public void setValue(String sqlTableName, Object _class_instance, ResultSet _result_set, ArrayList<DatabaseRecord> _pointing_records)
			throws DatabaseException {
		try {
			String s = _result_set.getString(getColumnIndex(_result_set, getSqlFieldName(sqlTableName, sql_fields[0])));
			Character c = null;
			if (s != null)
				c = s.charAt(0);
			if (c == null && isNotNull())
				throw new DatabaseIntegrityException("Unexpected exception.");
			//noinspection ConstantConditions
			field.setChar(_class_instance, c);
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}

	}

	@Override
	public void getValue(Object _class_instance, PreparedStatement _prepared_statement, int _field_start)
			throws DatabaseException {
		try {
			_prepared_statement.setString(_field_start, String.valueOf(field.getChar(_class_instance)));
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}

	}

	@Override
	public void getValue(PreparedStatement _prepared_statement, int _field_start, Object o) throws DatabaseException {
		try {
			_prepared_statement.setString(_field_start, o == null ? null : o.toString());
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public boolean canBePrimaryOrUniqueKey() {
		return true;
	}

	@Override
	public void serialize(RandomOutputStream _oos, Object _class_instance) throws DatabaseException {
		try {
			_oos.writeChar(field.getChar(_class_instance));

		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public void deserialize(RandomInputStream _ois, Map<String, Object> _map) throws DatabaseException {
		try {
			_map.put(getFieldName(), _ois.readChar());
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}

	}

	@Override
	public Object deserialize(RandomInputStream _ois, Object _classInstance) throws DatabaseException {
		try {
			char v = _ois.readChar();
			field.setChar(_classInstance, v);
			return v;
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

}
