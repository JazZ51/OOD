
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

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.distrimind.ood.database.DatabaseRecord;
import com.distrimind.ood.database.DatabaseWrapper;
import com.distrimind.ood.database.SqlField;
import com.distrimind.ood.database.SqlFieldInstance;
import com.distrimind.ood.database.Table;
import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.ood.database.exceptions.DatabaseIntegrityException;
import com.distrimind.ood.database.exceptions.FieldDatabaseException;
import com.distrimind.util.io.RandomByteArrayInputStream;
import com.distrimind.util.io.RandomByteArrayOutputStream;
import com.distrimind.util.io.RandomInputStream;
import com.distrimind.util.io.RandomOutputStream;

/**
 * 
 * @author Jason Mahdjoub
 * @version 1.2
 * @since OOD 1.0
 */
public class SecureExternalizableFieldAccessor extends FieldAccessor {
	protected final SqlField[] sql_fields;
	protected Method compareTo_method;

	protected SecureExternalizableFieldAccessor(Table<?> table, DatabaseWrapper _sql_connection,
												Field _field, String parentFieldName) throws DatabaseException {
		super(_sql_connection, _field, parentFieldName, getCompatibleClasses(_field), table);
		if (!Serializable.class.isAssignableFrom(field.getType()))
			throw new FieldDatabaseException("The given field " + field.getName() + " of type "
					+ field.getType().getName() + " must be a serializable field.");
		sql_fields = new SqlField[1];

		sql_fields[0] = new SqlField(table_name + "." + this.getSqlFieldName(),
				Objects.requireNonNull(DatabaseWrapperAccessor.getSerializableType(sql_connection)), null, null, isNotNull());

		if (Comparable.class.isAssignableFrom(field.getType())) {
			try {
				Method m = field.getType().getDeclaredMethod("compareTo", field.getType());
				if (Modifier.isPublic(m.getModifiers()))
					compareTo_method = m;
				else
					compareTo_method = null;
			} catch (Exception e) {
				compareTo_method = null;
			}
		} else
			compareTo_method = null;

	}

	private static Class<?>[] getCompatibleClasses(Field field) {
		Class<?>[] res = new Class<?>[1];
		res[0] = field.getType();
		return res;
	}

	@Override
	public void setValue(Object _class_instance, Object _field_instance) throws DatabaseException {
		if (_field_instance == null) {
			if (isNotNull())
				throw new FieldDatabaseException(
						"The given _field_instance, used to store the field " + field.getName() + " (type="
								+ field.getType().getName() + ", declaring_class=" + field.getDeclaringClass().getName()
								+ ") into the DatabaseField class " + field.getDeclaringClass().getName()
								+ ", is null and should not be (property NotNull present).");
		} else if (!(field.getType().isAssignableFrom(_field_instance.getClass())))
			throw new FieldDatabaseException("The given _field_instance parameter, destinated to the field "
					+ field.getName() + " of the class " + field.getDeclaringClass().getName() + ", should be a "
					+ field.getType().getName() + " and not a " + _field_instance.getClass().getName());
		try {
			field.set(_class_instance, _field_instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new DatabaseException("Unexpected exception.", e);
		}
	}

	@Override
	public void setValue(Object _class_instance, ResultSet _result_set, ArrayList<DatabaseRecord> _pointing_records)
			throws DatabaseException {
		try {
			Blob b = _result_set.getBlob(getColmunIndex(_result_set, sql_fields[0].field));
			if (b == null && isNotNull())
				throw new DatabaseIntegrityException("Unexpected exception.");

			if (b == null)
				field.set(_class_instance, null);
			else {
				try (RandomByteArrayInputStream bais = new RandomByteArrayInputStream(b.getBytes(1, (int) b.length()))) {
					Object res = deserialize(bais);
					if (res == null && isNotNull())
						throw new DatabaseIntegrityException("Unexpected exception.");
					field.set(_class_instance, res);
				}
			}
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public void updateValue(Object _class_instance, Object _field_instance, ResultSet _result_set)
			throws DatabaseException {
		setValue(_class_instance, _field_instance);
		try {
			if (Objects.equals(DatabaseWrapperAccessor.getSerializableType(sql_connection), "BLOB")) {
				try (RandomByteArrayOutputStream os = new RandomByteArrayOutputStream()) {
					serialize(os, _class_instance);
					// os.writeObject(field.get(_class_instance));

					try (ByteArrayInputStream bais = new ByteArrayInputStream(os.getBytes())) {
						_result_set.updateBlob(sql_fields[0].short_field, bais);
					}

				}
			} else
				_result_set.updateObject(sql_fields[0].short_field, field.get(_class_instance));
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}

	}

	@Override
	protected void updateResultSetValue(Object _class_instance, ResultSet _result_set, SqlFieldTranslation _sft)
			throws DatabaseException {
		try {
			if (Objects.equals(DatabaseWrapperAccessor.getSerializableType(sql_connection), "BLOB")) {
				try (RandomByteArrayOutputStream os = new RandomByteArrayOutputStream()) {
					serialize(os, _class_instance);
					// os.writeObject(field.get(_class_instance));

					try (ByteArrayInputStream bais = new ByteArrayInputStream(os.getBytes())) {
						_result_set.updateBlob(_sft.translateField(sql_fields[0]), bais);
					}
				}
			} else
				_result_set.updateObject(_sft.translateField(sql_fields[0]), field.get(_class_instance));
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public boolean equals(Object _class_instance, Object _field_instance) throws DatabaseException {
		try {
			Object val = field.get(_class_instance);
			if (val == null || _field_instance == null)
				return _field_instance == val;
			if ((!(field.getType().isAssignableFrom(_field_instance.getClass()))))
				return false;
			return val.equals(_field_instance);
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	protected boolean equals(Object _field_instance, ResultSet _result_set, SqlFieldTranslation _sft)
			throws DatabaseException {
		try {
			Blob b = _result_set.getBlob(sql_fields[0].short_field);
			try (RandomByteArrayInputStream bais = new RandomByteArrayInputStream(b.getBytes(1, (int) b.length()))) {

				Object val1 = deserialize(bais);
				if (val1 == null || _field_instance == null)
					return _field_instance == val1;
				if ((!(field.getType().isAssignableFrom(_field_instance.getClass()))))
					return false;
				return val1.equals(_field_instance);
			}
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public Object getValue(Object _class_instance) throws DatabaseException {
		try {
			return field.get(_class_instance);
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public void getValue(Object _class_instance, PreparedStatement _prepared_statement, int _field_start)
			throws DatabaseException {
		try {
			getValue(_prepared_statement, _field_start, field.get(_class_instance));
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public void getValue(PreparedStatement _prepared_statement, int _field_start, Object o) throws DatabaseException {
		try {
			if (Objects.equals(DatabaseWrapperAccessor.getSerializableType(sql_connection), "BLOB")) {
				try (RandomByteArrayOutputStream baos = new RandomByteArrayOutputStream()) {
					serializeObject(baos, o);
					// os.writeObject(o);

					try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.getBytes())) {
						_prepared_statement.setBlob(_field_start, bais);
					}
				}
			} else
				_prepared_statement.setObject(_field_start, o);
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public SqlField[] getDeclaredSqlFields() {
		return sql_fields;
	}

	@Override
	public SqlFieldInstance[] getSqlFieldsInstances(Object _instance) throws DatabaseException {
		SqlFieldInstance[] res = new SqlFieldInstance[1];
		if (Objects.equals(DatabaseWrapperAccessor.getSerializableType(sql_connection), "BLOB")) {
			try (RandomByteArrayOutputStream baos = new RandomByteArrayOutputStream()) {
				this.serialize(baos, _instance);
				res[0] = new SqlFieldInstance(sql_fields[0], baos.getBytes());

			} catch (Exception e) {
				throw DatabaseException.getDatabaseException(e);
			}
		} else {
			res[0] = new SqlFieldInstance(sql_fields[0], getValue(_instance));
		}
		return res;

	}

	@Override
	public boolean isAlwaysNotNull() {
		return false;
	}

	@Override
	public boolean isComparable() {
		return compareTo_method != null;
	}

	@Override
	public int compare(Object _r1, Object _r2) throws DatabaseException {
		if (!isComparable())
			throw new DatabaseException(
					"The field " + field.getName() + " of the class " + field.getDeclaringClass().getName()
							+ " of type " + field.getType().getName() + " is not a comparable field.");
		else {
			try {
				Object obj1 = field.get(_r1);
				Object obj2 = field.get(_r2);
				if (obj1 == null && obj2 != null)
					return -1;
				else if (obj1 != null && obj2 == null)
					return 1;
				else if (obj1 == obj2)
					return 0;

				return (Integer) compareTo_method.invoke(obj1, obj2);
			} catch (Exception e) {
				throw new DatabaseException("", e);
			}
		}
	}

	@Override
	public boolean canBePrimaryOrUniqueKey() {
		return false;
	}

	@Override
	public void serialize(RandomOutputStream _oos, Object _class_instance) throws DatabaseException {
		try {
			serializeObject(_oos, getValue(_class_instance));
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}



	public void serializeObject(RandomOutputStream dos, Object object) throws DatabaseException {
		try {
			long limit=getLimit();
			if (limit==0 || limit>Integer.MAX_VALUE)
				limit=Integer.MAX_VALUE;
			if (object == null && isNotNull())
				throw new DatabaseException("The field should not be null");

			dos.writeObject(object, true, (int)limit);
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}
	private Object deserialize(RandomInputStream ois) throws DatabaseException {
		try  {
			long limit=getLimit();
			if (limit==0 || limit>Integer.MAX_VALUE)
				limit=Integer.MAX_VALUE;
			Object o=ois.readObject(true, (int)limit);
			if (o != null && !field.getType().isAssignableFrom(o.getClass()))
				throw new DatabaseException(
						"Incompatible class : " + o.getClass() + " (expected=" + field.getType() + ")");
			if (o == null && isNotNull())
				throw new DatabaseException("The field should not be null");
			return o;
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}

	}


	@Override
	public void deserialize(RandomInputStream dis, Map<String, Object> _map) throws DatabaseException {
		_map.put(getFieldName(), deserialize(dis));
	}

	@Override
	public Object deserialize(RandomInputStream dis, Object _classInstance) throws DatabaseException {
		Object o=deserialize(dis);
		setValue(_classInstance, o);
		return o;
	}


}