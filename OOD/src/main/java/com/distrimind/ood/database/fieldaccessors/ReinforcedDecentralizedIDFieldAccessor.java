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
import com.distrimind.ood.database.exceptions.FieldDatabaseException;
import com.distrimind.util.RenforcedDecentralizedIDGenerator;
import com.distrimind.util.crypto.AbstractSecureRandom;
import com.distrimind.util.io.RandomInputStream;
import com.distrimind.util.io.RandomOutputStream;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author Jason Mahdjoub
 * @version 2.0
 * @since OOD 1.7.0
 * 
 */
public class ReinforcedDecentralizedIDFieldAccessor extends FieldAccessor {
	protected final SqlField[] sql_fields;

	protected ReinforcedDecentralizedIDFieldAccessor(Table<?> table,
													 DatabaseWrapper _sql_connection, Field _field, String parentFieldName, boolean severalPrimaryKeysPresentIntoTable) throws DatabaseException {
		super(_sql_connection, _field, parentFieldName, compatibleClasses, table, severalPrimaryKeysPresentIntoTable);
		sql_fields = new SqlField[2];
		sql_fields[0] = new SqlField(supportQuotes, table_name + "." + this.getSqlFieldName() + "_ts",
				Objects.requireNonNull(DatabaseWrapperAccessor.getLongType(sql_connection)), isNotNull());
		sql_fields[1] = new SqlField(supportQuotes, table_name + "." + this.getSqlFieldName() + "_widseq",
				Objects.requireNonNull(DatabaseWrapperAccessor.getLongType(sql_connection)), isNotNull());
	}

	private static final Class<?>[] compatibleClasses;
	static {
		compatibleClasses = new Class<?>[1];
		compatibleClasses[0] = RenforcedDecentralizedIDGenerator.class;
	}

	@Override
	public void setValue(Object _class_instance, Object _field_instance) throws DatabaseException {

		if (_field_instance == null) {
			if (isNotNull())
				throw new FieldDatabaseException("The given _field_instance, used to store the field " + field.getName()
						+ " (type=" + field.getType().getName() + ", declaring_class="
						+ field.getDeclaringClass().getName() + ") into the DatabaseField class "
						+ field.getDeclaringClass().getName() + ", is null and should not be.");
		} else if (!(_field_instance instanceof RenforcedDecentralizedIDGenerator))
			throw new FieldDatabaseException("The given _field_instance parameter, destinated to the field "
					+ field.getName() + " of the class " + field.getDeclaringClass().getName()
					+ ", should be an Long and not a " + _field_instance.getClass().getName());
		try {
			field.set(_class_instance, _field_instance);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new DatabaseException("Unexpected exception.", e);
		}
	}

	@Override
	public boolean equals(Object _class_instance, Object _field_instance) throws DatabaseException {
		try {
			Object o = field.get(_class_instance);
			if (o == _field_instance)
				return true;
			if (o != null)
				return o.equals(_field_instance);
			return false;
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	/*@Override
	protected boolean equals(Object _field_instance, ResultSet _result_set, SqlFieldTranslation _sft)
			throws DatabaseException {
		try {
			RenforcedDecentralizedIDGenerator val1 = null;
			if (_field_instance instanceof RenforcedDecentralizedIDGenerator)
				val1 = (RenforcedDecentralizedIDGenerator) _field_instance;
			long ts = _result_set.getLong(_sft.translateField(sql_fields[0]));
			long wsseq = _result_set.getLong(_sft.translateField(sql_fields[1]));

			return val1 != null && val1.getTimeStamp() == ts && val1.getWorkerIDAndSequence() == wsseq;
		} catch (SQLException e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}*/

	@Override
	public Object getValue(Object _class_instance) throws DatabaseException {
		try {
			return field.get(_class_instance);
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
		SqlFieldInstance[] res = new SqlFieldInstance[2];
		RenforcedDecentralizedIDGenerator did = (RenforcedDecentralizedIDGenerator) getValue(_instance);
		res[0] = new SqlFieldInstance(supportQuotes, sqlTableName, sql_fields[0], did.getTimeStamp());
		res[1] = new SqlFieldInstance(supportQuotes, sqlTableName, sql_fields[1], did.getWorkerIDAndSequence());
		return res;
	}

	@Override
	public boolean isAlwaysNotNull() {
		return true;
	}

	@Override
	public boolean isComparable() {
		return false;
	}

	@Override
	public int compare(Object _r1, Object _r2) throws DatabaseException {
		throw new DatabaseException("Unexpected exception");
	}

	@Override
	public void setValue(String sqlTableName, Object _class_instance, ResultSet _result_set, ArrayList<DatabaseRecord> _pointing_records)
			throws DatabaseException {
		try {
			long ts = _result_set.getLong(getColumnIndex(_result_set, getSqlFieldName(sqlTableName, sql_fields[0])));
			long wsseq = _result_set.getLong(getColumnIndex(_result_set, getSqlFieldName(sqlTableName, sql_fields[1])));

			field.set(_class_instance, DatabaseWrapperAccessor.getReinforcedDecentralizedIDGeneratorInstance(ts, wsseq));
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
			RenforcedDecentralizedIDGenerator did = (RenforcedDecentralizedIDGenerator) o;
			_prepared_statement.setObject(_field_start, did.getTimeStamp());
			_prepared_statement.setObject(++_field_start, did.getWorkerIDAndSequence());
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}

	}

	/*@Override
	public void updateValue(Object _class_instance, Object _field_instance, ResultSet _result_set)
			throws DatabaseException {
		setValue(_class_instance, _field_instance);
		try {
			RenforcedDecentralizedIDGenerator did = (RenforcedDecentralizedIDGenerator) field.get(_class_instance);
			_result_set.updateObject(sql_fields[0].short_field_without_quote, did.getTimeStamp());
			_result_set.updateObject(sql_fields[1].short_field_without_quote, did.getWorkerIDAndSequence());
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	protected void updateResultSetValue(Object _class_instance, ResultSet _result_set, SqlFieldTranslation _sft)
			throws DatabaseException {
		try {
			RenforcedDecentralizedIDGenerator did = (RenforcedDecentralizedIDGenerator) field.get(_class_instance);
			_result_set.updateObject(_sft.translateField(sql_fields[0]), did.getTimeStamp());
			_result_set.updateObject(_sft.translateField(sql_fields[1]), did.getWorkerIDAndSequence());
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}*/

	@Override
	public boolean canBePrimaryOrUniqueKey() {
		return true;
	}

	@Override
	public boolean canAutoGenerateValues() {
		return true;
	}

	@Override
	public boolean needToCheckUniquenessOfAutoGeneratedValues() {
		return false;
	}

	@Override
	public Object autoGenerateValue(AbstractSecureRandom _random) throws DatabaseException {
		try {
			return new RenforcedDecentralizedIDGenerator();
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public void serialize(RandomOutputStream _oos, Object _class_instance) throws DatabaseException {
		try {
			RenforcedDecentralizedIDGenerator a = (RenforcedDecentralizedIDGenerator) getValue(_class_instance);
			if (a == null)
				throw new FieldDatabaseException("The given _field_instance, used to store the field " + field.getName()
						+ " (type=" + field.getType().getName() + ", declaring_class="
						+ field.getDeclaringClass().getName() + ") into the DatabaseField class "
						+ field.getDeclaringClass().getName() + ", is null and should not be.");

			_oos.writeLong(a.getTimeStamp());
			_oos.writeLong(a.getWorkerIDAndSequence());
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public void deserialize(RandomInputStream _ois, Map<String, Object> _map) throws DatabaseException {
		try {
			long ts = _ois.readLong();
			long wsseq = _ois.readLong();
			_map.put(getFieldName(), DatabaseWrapperAccessor.getReinforcedDecentralizedIDGeneratorInstance(ts, wsseq));
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	@Override
	public Object deserialize(RandomInputStream _ois, Object _classInstance) throws DatabaseException {
		try {
			long ts = _ois.readLong();
			long wsseq = _ois.readLong();

			RenforcedDecentralizedIDGenerator a = DatabaseWrapperAccessor
					.getReinforcedDecentralizedIDGeneratorInstance(ts, wsseq);
			setValue(_classInstance, a);
			return a;
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

}
