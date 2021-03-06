
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

import com.distrimind.ood.database.*;
import com.distrimind.ood.database.annotations.*;
import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.util.AbstractDecentralizedID;
import com.distrimind.util.DecentralizedIDGenerator;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.RenforcedDecentralizedIDGenerator;
import com.distrimind.util.crypto.ASymmetricPublicKey;
import com.distrimind.util.crypto.AbstractSecureRandom;
import com.distrimind.util.io.RandomInputStream;
import com.distrimind.util.io.RandomOutputStream;
import com.distrimind.util.io.SerializationTools;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * 
 * @author Jason Mahdjoub
 * @version 3.0
 * @since OOD 1.0
 */
public abstract class FieldAccessor {
	protected final DatabaseWrapper sql_connection;
	protected final Field field;
	protected final String parentFieldName;
	protected final String fieldName, sqlFieldName;
	protected String table_name;
	protected final boolean auto_primary_key;
	protected final boolean random_primary_key;
	protected final boolean primary_key;
	protected final boolean foreign_key;
	protected final boolean unique;
	protected final boolean not_null;
	protected long limit;
	protected final long start_value;
	protected final int bits_number;
	protected final boolean hasToCreateIndex;
	protected final boolean descendantIndex;
	private final Class<?>[] compatible_classes;
	private final String indexName;
	private final Class<? extends Table<?>> table_class;
	private final boolean cacheDisabled;
	protected final boolean useBlob;
	private final boolean manual_auto_primary_key;
	protected final boolean supportQuotes;

	private static final Class<?> databaseEventsRecordTableClass;

	static
	{
		Class<?> c=null;
		try {
			c=Class.forName("com.distrimind.ood.database.DatabaseEventsTable$AbstractRecord");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		databaseEventsRecordTableClass=c;
	}

	@SuppressWarnings("unchecked")
	protected FieldAccessor(DatabaseWrapper _sql_connection, Field _field, String parentFieldName,
							Class<?>[] compatible_classes, Table<?> table, boolean severalPrimaryKeysPresentIntoTable) throws DatabaseException {
		if (compatible_classes == null)
			throw new NullPointerException("compatible_classes");
		this.supportQuotes=DatabaseWrapperAccessor.supportsItalicQuotesWithTableAndFieldNames(_sql_connection);
		this.compatible_classes = compatible_classes;
		sql_connection = _sql_connection;
		field = _field;
		this.parentFieldName = parentFieldName;
		com.distrimind.ood.database.annotations.Field af=field.getAnnotation(com.distrimind.ood.database.annotations.Field.class);
		String fName;
		if (af == null || af.sqlFieldName().trim().isEmpty())
			fName=field.getName();
		else
			fName=af.sqlFieldName();
		if (fName.endsWith("__"))
			throw new DatabaseException("Field "+fName+" cannot ends with '__'");

		this.fieldName = ((this.parentFieldName == null || this.parentFieldName.isEmpty()) ? ""
				: (this.parentFieldName + ".")) + field.getName();
		this.sqlFieldName = ((this.parentFieldName == null || this.parentFieldName.isEmpty()) ? ""
				: (this.parentFieldName.replace(".", "_") + "_")) + fName.toUpperCase();
		this.table_class = table==null?null:(Class<? extends Table<?>>)table.getClass();
		table_name = table_class == null ? (DatabaseRecord.class.isAssignableFrom(field.getDeclaringClass())
				? (_sql_connection.getInternalTableName(Table.getTableClass((Class<? extends DatabaseRecord>) field.getDeclaringClass()), table==null?-1:table.getDatabaseVersion()))
				: null) : _sql_connection.getInternalTableName(table_class, table.getDatabaseVersion());
		assert table != null;
		useBlob= _field.isAnnotationPresent(com.distrimind.ood.database.annotations.Field.class) && _field.getAnnotation(com.distrimind.ood.database.annotations.Field.class).forceUsingBlobOrClob();
		auto_primary_key = _field.isAnnotationPresent(AutoPrimaryKey.class);
		random_primary_key = field.isAnnotationPresent(RandomPrimaryKey.class);

		manual_auto_primary_key = auto_primary_key && (!DatabaseWrapperAccessor.supportSingleAutoPrimaryKeys(table.getDatabaseWrapper()) || (severalPrimaryKeysPresentIntoTable && !DatabaseWrapperAccessor.supportMultipleAutoPrimaryKeys(table.getDatabaseWrapper())));
		if (auto_primary_key && random_primary_key)
			throw new DatabaseException(
					"The field " + field.getName() + " of the DatabaseRecord " + field.getDeclaringClass().getName()
							+ " cannot be an auto primary key and a random primary key at the same time.");
		if (auto_primary_key && !canAutoGenerateValues())
			throw new DatabaseException(
					"The field " + field.getName() + " of the DatabaseRecord " + field.getDeclaringClass().getName()
							+ ", which is an auto primary key, must be a byte, a short, an int, a long, or a decentralized value.");
		primary_key = field.isAnnotationPresent(PrimaryKey.class) || auto_primary_key || random_primary_key;
		foreign_key = field.isAnnotationPresent(ForeignKey.class);
		if (foreign_key && (auto_primary_key || random_primary_key))
			throw new DatabaseException("The field " + field.getName() + " of the DatabaseRecord "
					+ field.getDeclaringClass().getName()
					+ " cannot be a foreign key and an auto primary key (or a random primary key) at the same time.");
		if (random_primary_key && !canAutoGenerateValues())
			throw new DatabaseException(
					"The field " + field.getName() + " of the DatabaseRecord " + field.getDeclaringClass().getName()
							+ " is declared as a random primary key. However this type cannot be randomly generated.");
		unique = field.isAnnotationPresent(Unique.class) && !auto_primary_key && !random_primary_key;
		not_null = field.isAnnotationPresent(NotNull.class) || primary_key || isAlwaysNotNull();
		if (foreign_key && not_null && field.getType().equals(field.getDeclaringClass()))
			throw new DatabaseException(
					"The field " + field.getName() + " of the class " + field.getDeclaringClass().getName()
							+ " points to same class. So this field cannot have the annotation NotNull.");

		if (primary_key && !canBePrimaryOrUniqueKey())
			throw new DatabaseException(
					"The field " + field.getName() + " of the class " + field.getDeclaringClass().getName()
							+ " of type " + field.getType().getName() + " can't be a primary key.");
		if (unique && !canBePrimaryOrUniqueKey())
			throw new DatabaseException(
					"The field " + field.getName() + " of the class " + field.getDeclaringClass().getName()
							+ " of type " + field.getType().getName() + " can't be a unique key.");

		if ((field.getName().equals("concernedSerializedPrimaryKey") || field.getName().equals("concernedSerializedNewForeignKey")) && databaseEventsRecordTableClass.isAssignableFrom(field.getDeclaringClass()))
			limit=DatabaseWrapperAccessor.getMaxKeySize(sql_connection);
		else if (field.isAnnotationPresent(com.distrimind.ood.database.annotations.Field.class))
			limit = _field.getAnnotation(com.distrimind.ood.database.annotations.Field.class).limit();
		else
			limit=-1;

		if (auto_primary_key) {
			start_value = _field.getAnnotation(AutoPrimaryKey.class).startValue();
			bits_number = -1;

		} else if (random_primary_key) {
			start_value = -1;
			int nb = _field.isAnnotationPresent(RandomPrimaryKey.class)?_field.getAnnotation(RandomPrimaryKey.class).bitsNumber():-1;
			if (nb == -1) {
				nb = getDefaultBitsNumberForAutoGeneratedValues();
			}
			bits_number = nb;
			if (bits_number < 1 || bits_number > getMaximumBitsNumberForAutoGeneratedValues())
				throw new DatabaseException("The field " + field.getName() + " of the DatabaseRecord "
						+ field.getDeclaringClass().getName()
						+ " is a random primary key whose type enables at maximum "
						+ getMaximumBitsNumberForAutoGeneratedValues()
						+ " random bits and at minimum 1 random bit. Currently, the number of bits is " + bits_number);

		} else if (primary_key) {
			start_value = -1;
			bits_number = -1;

		} else if (foreign_key) {
			start_value = -1;
			bits_number = -1;
			limit = -1;
		} else {
			start_value = -1;
			bits_number = -1;
		}
		if (field.isAnnotationPresent(com.distrimind.ood.database.annotations.Field.class)
				&& field.getAnnotation(com.distrimind.ood.database.annotations.Field.class).index() && !primary_key
				&& !foreign_key) {
			hasToCreateIndex = true;
			descendantIndex = field.getAnnotation(com.distrimind.ood.database.annotations.Field.class)
					.descendingIndex();
		} else {
			hasToCreateIndex = false;
			descendantIndex = true;
		}
		this.indexName = (this.table_name + "_" + this.getField().getName()).replace(".", "_").toUpperCase();
		this.cacheDisabled=isCacheAlwaysDisabled()
				|| (field.getAnnotation(com.distrimind.ood.database.annotations.Field.class)!=null
				&& field.getAnnotation(com.distrimind.ood.database.annotations.Field.class).disableCache());
	}

	public boolean isManualAutoPrimaryKey() {
		return manual_auto_primary_key;
	}

	public void changeInternalTableName(String oldInternalTableName, String internalTableName, int newTableVersion) throws DatabaseException {
		table_name=table_name.replace(oldInternalTableName, internalTableName);
		for (SqlField sf : getDeclaredSqlFields())
		{
			sf.field=sf.field.replace(oldInternalTableName, internalTableName);
			sf.fieldWithoutQuote =sf.fieldWithoutQuote.replace(oldInternalTableName, internalTableName);
		}
	}


	public final boolean isCacheDisabled()
	{
		return cacheDisabled;
	}

	public boolean isCacheAlwaysDisabled()
	{
		return !ASymmetricPublicKey.class.isAssignableFrom(field.getType()) && DecentralizedValue.class.isAssignableFrom(field.getType());
	}

	public String getSqlFieldName()
	{
		return sqlFieldName;
	}

	public boolean isDecentralizablePrimaryKey()
	{
		return !isAutoPrimaryKey() && !isRandomPrimaryKey();
	}

	public Class<? extends Table<?>> getTableClass() {
		return table_class;
	}

	public boolean hasToCreateIndex() {
		return hasToCreateIndex;
	}

	public boolean isDescendentIndex() {
		return descendantIndex;
	}

	public String getIndexName() {
		return indexName;
	}

	public Class<?> getFieldClassType() {
		return field.getType();
	}

	public boolean isTypeCompatible(Class<?> _cls) {
		for (Class<?> c : getCompatibleClasses())
			if (c.equals(_cls))
				return true;
		return false;
	}

	public boolean isAssignableTo(Class<?> _cls) {
		for (Class<?> c : getCompatibleClasses())
			if (c.isAssignableFrom(_cls) /* || _cls.equals(c) */)
				return true;
		return false;
	}

	public boolean isPrimaryKey() {
		return primary_key;
	}

	public boolean isAutoPrimaryKey() {
		return auto_primary_key;
	}

	public boolean isRandomPrimaryKey() {
		return random_primary_key;
	}

	public boolean isUnique() {
		return unique;
	}

	public boolean isForeignKey() {
		return foreign_key;
	}

	public boolean isNotNull() {
		return not_null;
	}

	public String getFieldName() {
		return fieldName;
	}

	public long getLimit() {
		return limit;
	}

	public final long getStartValue() {
		return start_value;
	}

	public final int getBitsNumber() {
		return bits_number;
	}

	public abstract void setValue(Object _class_instance, Object _field_instance) throws DatabaseException;

	public final void setValue(String sqlTableName, Object _class_instance, ResultSet _result_set) throws DatabaseException {
		setValue(sqlTableName, _class_instance, _result_set, null);
	}

	public abstract void setValue(String sqlTableName, Object _class_instance, ResultSet _result_set,
			ArrayList<DatabaseRecord> _pointing_records) throws DatabaseException;

	/*public abstract void updateValue(String sqlTableName, Object _class_instance, Object _field_instance, ResultSet _result_set)
			throws DatabaseException;

	protected abstract void updateResultSetValue(String sqlTableName, Object _class_instance, ResultSet _result_set,
			SqlFieldTranslation _sft) throws DatabaseException;*/

	public abstract boolean equals(Object _class_instance, Object _field_instance) throws DatabaseException;

	/*protected abstract boolean equals(Object _field_instance, ResultSet _result_set, SqlFieldTranslation _sft)
			throws DatabaseException;

	public final boolean equals(Object _field_instance, ResultSet _result_set) throws DatabaseException {
		return equals(_field_instance, _result_set, new SqlFieldTranslation(this));
	}*/

	public final Class<?>[] getCompatibleClasses() {
		return compatible_classes;
	}

	public abstract Object getValue(Object _class_instance) throws DatabaseException;

	public abstract void getValue(Object _class_instance, PreparedStatement _prepared_statement, int _field_start)
			throws DatabaseException;

	public abstract void getValue(PreparedStatement _prepared_statement, int _field_start, Object field_content)
			throws DatabaseException;

	public abstract SqlField[] getDeclaredSqlFields();

	public abstract SqlFieldInstance[] getSqlFieldsInstances(String sqlTableName, Object _instance) throws DatabaseException;

	public abstract boolean isAlwaysNotNull();

	public abstract boolean isComparable();

	public abstract boolean canBePrimaryOrUniqueKey();

	public abstract int compare(Object r1, Object _r2) throws DatabaseException;

	public abstract void serialize(RandomOutputStream oos, Object classInstance) throws DatabaseException;

	public abstract void deserialize(RandomInputStream ois, Map<String, Object> map) throws DatabaseException;

	public abstract Object deserialize(RandomInputStream ois, Object classInstance) throws DatabaseException;

	public boolean canAutoGenerateValues() {
		return false;
	}

	public Object autoGenerateValue(AbstractSecureRandom random) throws DatabaseException {
		return null;
	}

	public void autoGenerateValue(DatabaseRecord _class_instance, AbstractSecureRandom random) throws DatabaseException {
		try {
			field.set(_class_instance, autoGenerateValue(random));
		} catch (Exception e) {
			throw DatabaseException.getDatabaseException(e);
		}
	}

	public boolean needToCheckUniquenessOfAutoGeneratedValues() {
		return true;
	}

	public int getDefaultBitsNumberForAutoGeneratedValues() {
		return 7;
	}

	public int getMaximumBitsNumberForAutoGeneratedValues() {
		return 7;
	}

	public Field getField() {
		return field;
	}



	public static ArrayList<FieldAccessor> getFields(DatabaseWrapper _sql_connection,
													 Table<?> _table, Class<?> database_record_class, String parentFieldName,
			List<Class<?>> parentFields) throws DatabaseException {

		ArrayList<FieldAccessor> res = new ArrayList<>();
		try {
			FieldAccessPrivilegedAction fapa = new FieldAccessPrivilegedAction(database_record_class);
			int pkNumber=0;
			for (java.lang.reflect.Field f : AccessController.doPrivileged(fapa)) {
				if (f.isAnnotationPresent(PrimaryKey.class)
						|| f.isAnnotationPresent(AutoPrimaryKey.class)
						|| f.isAnnotationPresent(RandomPrimaryKey.class)) {

					++pkNumber;
				}
			}
			final boolean severalPrimaryKeysPresentIntoTable=pkNumber>1;
			for (java.lang.reflect.Field f : AccessController.doPrivileged(fapa)) {
				if (f.isAnnotationPresent(com.distrimind.ood.database.annotations.Field.class)
						|| (parentFieldName == null
								&& (f.isAnnotationPresent(PrimaryKey.class) || f.isAnnotationPresent(ForeignKey.class)
										|| f.isAnnotationPresent(AutoPrimaryKey.class)
										|| f.isAnnotationPresent(RandomPrimaryKey.class)))) {
					Class<?> type = f.getType();
					if (parentFieldName == null && f.isAnnotationPresent(ForeignKey.class)) {

						if (!DatabaseRecord.class.isAssignableFrom(type))
							throw new IllegalAccessError(
									"The class " + database_record_class.getSimpleName() + " contains a foreign key ("
											+ type.getName() + ") which does not point to a DatabaseRecord class");
						if (!type.getSimpleName().equals("Record"))
							throw new IllegalAccessError("The class " + database_record_class.getSimpleName()
									+ " contains a foreign key which points to a DatabaseRecord class whose name ("
									+ type.getName() + ") is not equal to 'Record'.");
						@SuppressWarnings("unchecked")
						Class<? extends DatabaseRecord> type2 = (Class<? extends DatabaseRecord>) type;

						Class<? extends Table<?>> t = Table.getTableClass(type2);
						if (!t.getPackage().equals(_table.getClass().getPackage()))
							throw new DatabaseException("The class " + database_record_class.getName()
									+ " contains a foreign key which points to a DatabaseRecord (" + type.getName()
									+ ") which have not the same package of the considered table.");
						if (!t.isAnnotationPresent(LoadToMemory.class)
								&& _table.getClass().isAnnotationPresent(LoadToMemory.class))
							throw new IllegalAccessError("The Table " + t.getSimpleName()
									+ " is not loaded into memory whereas the table " + _table.getClass().getSimpleName()
									+ " is ! It is a problem since the table " + _table.getClass().getSimpleName()
									+ " has a foreign key which points to " + t.getSimpleName());
						res.add(new ForeignKeyFieldAccessor(_table, _sql_connection, f, null, severalPrimaryKeysPresentIntoTable));
					} else {
						ByteTabObjectConverter converter;
						if (type.equals(boolean.class))
							res.add(new booleanFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(byte.class))
							res.add(new byteFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(short.class))
							res.add(new shortFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(char.class))
							res.add(new charFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(int.class))
							res.add(new intFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(long.class))
							res.add(new longFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(float.class))
							res.add(new floatFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(double.class))
							res.add(new doubleFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(String.class))
							res.add(new StringFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						/*else if (type.equals(ASymmetricPublicKey.class) || type.equals(ASymmetricPrivateKey.class) || type.equals(SymmetricSecretKey.class))
						{
							res.add(new KeyFieldAccessor(_table_class, _sql_connection, f, parentFieldName));
						}*/

						else if (type.equals(class_array_byte))
							res.add(new ByteTabFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Boolean.class))
							res.add(new BooleanNumberFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Byte.class))
							res.add(new ByteNumberFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Short.class))
							res.add(new ShortNumberFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Character.class))
							res.add(new CharacterNumberFieldAccessor(_table, _sql_connection, f,
									parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Integer.class))
							res.add(new IntegerNumberFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Long.class))
							res.add(new LongNumberFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Float.class))
							res.add(new FloatNumberFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(Double.class))
							res.add(new DoubleNumberFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(BigDecimal.class))
							res.add(new BigDecimalFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(BigInteger.class))
							res.add(new BigIntegerFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (Date.class.isAssignableFrom(type))
							res.add(new DateFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(DecentralizedIDGenerator.class))
							res.add(new DecentralizedIDFieldAccessor(_table, _sql_connection, f,
									parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(RenforcedDecentralizedIDGenerator.class))
							res.add(new ReinforcedDecentralizedIDFieldAccessor(_table, _sql_connection, f,
									parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (AbstractDecentralizedID.class.isAssignableFrom(type))
							res.add(new AbstractDecentralizedIDFieldAccessor(_table, _sql_connection, f,
									parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (Calendar.class.isAssignableFrom(type))
							res.add(new CalendarFieldAccessor(_table, _sql_connection, f,
									parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (type.equals(UUID.class))
							res.add(new UUIDFieldAccessor(_table, _sql_connection, f,
									parentFieldName, severalPrimaryKeysPresentIntoTable));
						else if (DecentralizedValue.class.isAssignableFrom(type) )
						{
							res.add(new DecentralizedValueFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						}
						else if ((converter = _sql_connection.getByteTabObjectConverter(type)) != null)
							res.add(new ByteTabConvertibleFieldAccessor(_table, _sql_connection, f,
									parentFieldName, converter, severalPrimaryKeysPresentIntoTable));
						else if (isComposedField(type)) {
							for (Class<?> cpf : parentFields)
								if (cpf.isAssignableFrom(type))
									throw new DatabaseException("The class/table " + _table.getClass().getSimpleName()
											+ " has a problem of circularity with its fields.");

							ArrayList<Class<?>> pf = new ArrayList<>(parentFields);
							pf.add(type);

							res.add(new ComposedFieldAccessor(_sql_connection, _table, f, parentFieldName, pf, severalPrimaryKeysPresentIntoTable));
						} else if (SerializationTools.isSerializableType(type))
							res.add(new SecureExternalizableFieldAccessor(_table, _sql_connection, f, parentFieldName, severalPrimaryKeysPresentIntoTable));
						else
							throw new DatabaseException(
									"The field " + f.getName() + " of the class " + database_record_class.getName()
											+ " have a type which can't be used on the SqlJet database ");
					}
				}
			}
		} catch (PrivilegedActionException e) {
			throw new DatabaseException(
					"Impossible to access to fields of the class " + database_record_class.getName(), e);
		}

		res.sort(Comparator.comparing(FieldAccessor::getFieldName));
		return res;
	}

	public static ArrayList<FieldAccessor> getFields(DatabaseWrapper _sql_connection,
			Table<?> _table) throws DatabaseException {
		@SuppressWarnings("unchecked")
		Class<? extends DatabaseRecord> database_record_class = Table.getDatabaseRecord((Class<? extends Table<?>>)_table.getClass());

		try {
			if (!checkCircularityWithPrimaryForeignKeys(database_record_class))
				throw new DatabaseException("The class/table " + _table.getClass().getSimpleName()
						+ " has a problem of circularity with other tables, through primary foreign keys !");
			if (!checkCircularityWithNotNullForeignKeys(database_record_class))
				throw new DatabaseException("The class/table " + _table.getClass().getSimpleName()
						+ " has a problem of circularity with other tables, through not null foreign keys !");
			ArrayList<Class<?>> parentFields = new ArrayList<>();
			parentFields.add(database_record_class);
			return getFields(_sql_connection, _table, database_record_class, null, parentFields);
		} catch (PrivilegedActionException e) {
			throw new DatabaseException(
					"Impossible to access to fields of the class " + database_record_class.getName(), e);
		}

	}

	private static boolean isComposedField(Class<?> type) {
		return type.isAnnotationPresent(com.distrimind.ood.database.annotations.Field.class);
	}

	private static boolean checkCircularityWithNotNullForeignKeys(Class<?> _original_class)
			throws PrivilegedActionException {
		ArrayList<Class<?>> list_classes = new ArrayList<>();

		FieldAccessPrivilegedAction fapa = new FieldAccessPrivilegedAction(_original_class);
		ArrayList<Field> fields = AccessController.doPrivileged(fapa);

		for (java.lang.reflect.Field f : fields) {
			if (f.isAnnotationPresent(ForeignKey.class)) {
				if (f.isAnnotationPresent(NotNull.class)) {
					Class<?> new_class = f.getType();
					if (DatabaseRecord.class.isAssignableFrom(new_class)) {
						if (!checkCircularityWithNotNullForeignKeys(_original_class, list_classes, new_class))
							return false;
					}
				}
			}
		}
		return true;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean checkCircularityWithNotNullForeignKeys(Class<?> _original_class,
																  List<Class<?>> _list_classes, Class<?> _new_class) throws PrivilegedActionException {
		if (_original_class.equals(_new_class))
			return false;
		if (_list_classes.contains(_new_class))
			return true;
		_list_classes.add(_new_class);

		FieldAccessPrivilegedAction fapa = new FieldAccessPrivilegedAction(_new_class);
		ArrayList<Field> fields = AccessController.doPrivileged(fapa);

		for (java.lang.reflect.Field f : fields) {
			if (f.isAnnotationPresent(ForeignKey.class)) {
				if (f.isAnnotationPresent(NotNull.class)) {
					Class<?> new_class = f.getType();
					if (DatabaseRecord.class.isAssignableFrom(new_class)) {
						if (!checkCircularityWithNotNullForeignKeys(_original_class, _list_classes, new_class))
							return false;
					}
				}
			}
		}

		return true;
	}

	private static boolean checkCircularityWithPrimaryForeignKeys(Class<?> _original_class)
			throws PrivilegedActionException {
		ArrayList<Class<?>> list_classes = new ArrayList<>();

		FieldAccessPrivilegedAction fapa = new FieldAccessPrivilegedAction(_original_class);
		ArrayList<Field> fields = AccessController.doPrivileged(fapa);

		for (java.lang.reflect.Field f : fields) {
			if (f.isAnnotationPresent(ForeignKey.class) && f.isAnnotationPresent(PrimaryKey.class)) {
				Class<?> new_class = f.getType();
				if (DatabaseRecord.class.isAssignableFrom(new_class)) {
					if (!checkCircularityWithPrimaryForeignKeys(_original_class, list_classes, new_class))
						return false;
				}
			}
		}
		return true;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean checkCircularityWithPrimaryForeignKeys(Class<?> _original_class,
																  List<Class<?>> _list_classes, Class<?> _new_class) throws PrivilegedActionException {
		if (_original_class.equals(_new_class))
			return false;
		if (_list_classes.contains(_new_class))
			return true;
		_list_classes.add(_new_class);

		FieldAccessPrivilegedAction fapa = new FieldAccessPrivilegedAction(_new_class);
		ArrayList<Field> fields = AccessController.doPrivileged(fapa);

		for (java.lang.reflect.Field f : fields) {
			if (f.isAnnotationPresent(ForeignKey.class) && f.isAnnotationPresent(PrimaryKey.class)) {
				Class<?> new_class = f.getType();
				if (DatabaseRecord.class.isAssignableFrom(new_class)) {
					if (!checkCircularityWithPrimaryForeignKeys(_original_class, _list_classes, new_class))
						return false;
				}
			}
		}

		return true;
	}

	private static final Class<?> class_array_byte = byte[].class;

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean equalsBetween(Object val1, Object val2) throws DatabaseException {
		if (val1 == val2)
			return true;
		if (val1 == null)
			return false;
		if (val2 == null)
			return false;
		if (val1.getClass().equals(String.class)) {
			if (val2.getClass().equals(String.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(BigDecimal.class))
				return val2.equals(new BigDecimal((String) val1));
			else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(class_array_byte)) {
			if (val2.getClass().equals(class_array_byte))
				return tabEquals((byte[]) val1, (byte[]) val2);
			else if (val2.getClass().equals(BigInteger.class))
				return val2.equals(new BigInteger((byte[]) val1));
			else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(Boolean.class)) {
			if (val2.getClass().equals(Boolean.class)) {
				return val1.equals(val2);
			} else if (val2.getClass().equals(Long.class)) {
				return val1.equals((Long) val2 != 0);
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(Byte.class)) {
			if (val2.getClass().equals(Byte.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(Long.class))
				return val1.equals(((Long) val2).byteValue());
			else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(Short.class)) {
			if (val2.getClass().equals(Short.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(Long.class)) {
				return val1.equals(((Long) val2).shortValue());
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(Character.class)) {
			if (val2.getClass().equals(Character.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(Long.class)) {
				return val1.equals((char) ((Long) val2).longValue());
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(Integer.class)) {
			if (val2.getClass().equals(Integer.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(Long.class)) {
				return val1.equals(((Long) val2).intValue());
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(Long.class)) {
			if (val2.getClass().equals(Long.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(Boolean.class))
				return val2.equals((Long) val1 != 0);
			else if (val2.getClass().equals(Integer.class)) {
				return val1.equals(((Integer) val2).longValue());
			} else if (val2.getClass().equals(Byte.class))
				return val1.equals((long) (Byte) val2);
			else if (val2.getClass().equals(Short.class))
				return val1.equals((long) (Short) val2);
			else if (val2.getClass().equals(Character.class))
				return val1.equals((long) (Character) val2);
			else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(Float.class)) {
			if (val2.getClass().equals(Float.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(Double.class))
				return val1.equals(((Double) val2).floatValue());
			else
				throw new DatabaseException("Unexpected exception.");

		} else if (val1.getClass().equals(Double.class)) {
			if (val2.getClass().equals(Double.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(Float.class)) {
				return ((Double) val1).floatValue() == (Float) val2;
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(BigDecimal.class)) {
			if (val2.getClass().equals(String.class))
				return val1.equals(new BigDecimal((String) val2));
			else if (val2.getClass().equals(BigDecimal.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(BigInteger.class))
				return val1.equals(new BigDecimal((BigInteger) val2));
			else
				throw new DatabaseException("Unexpected exception.");
		} else if (val1.getClass().equals(BigInteger.class)) {
			if (val2.getClass().equals(String.class))
				return val1.equals(new BigInteger((String) val2));
			else if (val2.getClass().equals(BigInteger.class))
				return val1.equals(val2);
			else if (val2.getClass().equals(BigDecimal.class))
				return val2.equals(new BigDecimal((BigInteger) val1));
			else
				throw new DatabaseException("Unexpected exception.");
		} else if (Calendar.class.isAssignableFrom(val1.getClass())) {
			if (Calendar.class.isAssignableFrom(val2.getClass())) {
				return val1.equals(val2);
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (Timestamp.class.isAssignableFrom(val1.getClass())) {
			if (Timestamp.class.isAssignableFrom(val2.getClass())) {
				return val2.equals(val1);
			} else if (Date.class.isAssignableFrom(val2.getClass())) {
				return val1.equals(new Timestamp(((Date) val2).getTime()));
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (Date.class.isAssignableFrom(val1.getClass())) {
			if (Timestamp.class.isAssignableFrom(val2.getClass())) {
				return val2.equals(new Timestamp(((Date) val1).getTime()));
			} else if (Date.class.isAssignableFrom(val2.getClass())) {
				return val1.equals(val2);
			} else
				throw new DatabaseException("Unexpected exception.");
		} else if (Serializable.class.isAssignableFrom(val1.getClass())) {
			if (Serializable.class.isAssignableFrom(val2.getClass())) {
				return val1.equals(val2);
			} else
				throw new DatabaseException("Unexpected exception.");
		} else
			throw new DatabaseException("Unexpected exception.");
	}

	protected int getColumnIndex(ResultSet _result_set, String fieldName) throws SQLException {

		if (DatabaseWrapperAccessor.supportFullSqlFieldName(sql_connection)) {
			return _result_set.findColumn(fieldName);
		}
		else {
			ResultSetMetaData rsmd = _result_set.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (rsmd.getColumnName(i).equalsIgnoreCase(fieldName))
					return i;
			}
			throw new SQLException("colmun " + fieldName + " not found !");
		}

	}

	private static boolean tabEquals(byte[] tab1, byte[] tab2) {
		if (tab1.length != tab2.length)
			return false;
		for (int i = 0; i < tab1.length; i++)
			if (tab1[i] != tab2[i])
				return false;
		return true;
	}

	@SuppressWarnings("ConstantConditions")
	public static void setValue(DatabaseWrapper sql_connection, PreparedStatement st, int index, Object p)
			throws SQLException {
		if (p instanceof byte[] || p instanceof Number) {

			if (p instanceof Byte)
				st.setByte(index, (Byte) p);
			else if (p instanceof Integer)
				st.setInt(index, (Integer) p);
			else if (p instanceof Long)
				st.setLong(index, (Long) p);
			else if (p instanceof byte[])
				st.setBytes(index, (byte[]) p);
			else if (p instanceof CharSequence)
				st.setString(index, p.toString());
			else if (p instanceof Short)
				st.setShort(index, (Short) p);
			else if (p instanceof Boolean)
				st.setBoolean(index, (Boolean)p);
			else if (p instanceof Float)
				st.setFloat(index, (Float)p);
			else if (p instanceof Double)
				st.setDouble(index, (Double)p);
			else if (p instanceof BigDecimal) {
				String t = DatabaseWrapperAccessor.getBigDecimalType(sql_connection, 128);
				if (p instanceof ByteTabFieldAccessor.BD)
				{
					if (t.contains(DatabaseWrapperAccessor.getBinaryBaseWord(sql_connection)))
						st.setBytes(index, ByteTabFieldAccessor.getByteTab((BigDecimal) p));
					else if (t.contains("CHAR"))
						st.setString(index, p.toString());
					else
						st.setBigDecimal(index, (BigDecimal) p);
				}
				else {
					if (t.contains(DatabaseWrapperAccessor.getBinaryBaseWord(sql_connection)))
						st.setBytes(index, BigDecimalFieldAccessor.bigDecimalToBytes((BigDecimal) p));
					else if (t.contains("CHAR"))
						st.setString(index, p.toString());
					else
						st.setBigDecimal(index, (BigDecimal) p);
				}
			}
			else if (p instanceof BigInteger) {
				String t=DatabaseWrapperAccessor.getBigIntegerType(sql_connection, 128);
				if (t.contains(DatabaseWrapperAccessor.getBinaryBaseWord(sql_connection)) || t.contains(DatabaseWrapperAccessor.getBlobBaseWord(sql_connection)))
					st.setBytes(index, ((BigInteger)p).toByteArray());
				else if (t.contains("CHAR"))
					st.setString(index, p.toString());
				else
					st.setBigDecimal(index, new BigDecimal((BigInteger)p));
			}

		}
		else
			st.setObject(index, p);
	}

	/*protected static class SqlFieldTranslation {
		private final HashMap<String, String> sql_fields = new HashMap<>();
		private final FieldAccessor field_accessor;

		public SqlFieldTranslation(FieldAccessor fa, SqlField[] _sql_field) {
			field_accessor = fa;
			for (SqlField sf : fa.getDeclaredSqlFields()) {
				if (sf.pointed_field != null)
					sql_fields.put(sf.pointed_field, sf.short_field);
				else
					sql_fields.put(sf.field, sf.short_field);
			}
		}

		public SqlFieldTranslation(ForeignKeyFieldAccessor fa, SqlFieldTranslation _sft) {
			field_accessor = fa;
			for (SqlField sf : fa.getDeclaredSqlFields()) {
				SqlField sf_pointing_founded = null;
				assert sf.pointed_field != null;
				for (SqlField sf_pointing : _sft.field_accessor.getDeclaredSqlFields()) {
					if (sf_pointing.pointed_field_without_quote.equals(sf.field_without_quote)) {
						sf_pointing_founded = sf_pointing;
						break;
					}
				}
				String t;

				if (sf_pointing_founded == null || (t = _sft.translateField(sf_pointing_founded)) == null) {
					sql_fields.put(sf.pointed_field_without_quote, sf.short_field_without_quote);
				} else {
					sql_fields.put(sf.pointed_field_without_quote, t);
				}
			}
		}

		public SqlFieldTranslation(FieldAccessor fa, String sqlTable) {
			field_accessor = fa;
			for (SqlField sf : fa.getDeclaredSqlFields()) {
				if (sf.pointed_field != null)
					sql_fields.put(sf.pointed_field_without_quote, sf.short_field_without_quote);
				else
					sql_fields.put(sqlTable+"."+sf.short_field_without_quote, sf.short_field_without_quote);
			}

		}

		public String translateField(String sqlTable, SqlField sf) {
			return sql_fields.get(sqlTable+"."+sf.short_field_without_quote);
		}

	}*/

	final String getSqlFieldName(String sqlTableName, SqlField sqlField)
	{
		//return sqlField.field_without_quote;
		return sqlTableName+"__"+sqlField.sqlFieldAliasName;
	}

	protected static final class FieldAccessPrivilegedAction implements PrivilegedExceptionAction<ArrayList<Field>> {
		private final Class<?> m_cls;

		public FieldAccessPrivilegedAction(Class<?> _cls) {
			m_cls = _cls;
		}

		@Override
		public ArrayList<Field> run() {
			ArrayList<Field> fields = new ArrayList<>();
			Class<?> sup = m_cls.getSuperclass();
			if (sup != Object.class && sup != DatabaseRecord.class) {
				FieldAccessPrivilegedAction fapa = new FieldAccessPrivilegedAction(sup);
				fields.addAll(fapa.run());
			}

			Field[] fs = m_cls.getDeclaredFields();
			for (java.lang.reflect.Field f : fs) {
				f.setAccessible(true);
				fields.add(f);
			}

			return fields;
		}

	}

}
