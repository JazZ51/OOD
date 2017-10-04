
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

package com.distrimind.ood.database;

/**
 * This class represent a field description with its instance into the SqlJet
 * database. The user should not use this class.
 * 
 * @author Jason Mahdjoub
 * @version 1.1
 */

public class SqlFieldInstance extends SqlField {
	/**
	 * the field instance
	 */
	public final Object instance;

	/**
	 * Constructor
	 * 
	 * @param _field
	 *            the name of the SqlJet field.
	 * @param _type
	 *            the type of the SqlJet field.
	 * @param _pointed_field
	 *            The name of the pointed Sql field, if this field is a foreign key
	 * @param _pointed_table
	 *            The name of the pointed SqlJet table, if this field is a foreign
	 *            key
	 * @param _not_null
	 *            tells if the field is not null
	 * @param _instance
	 *            The field instance
	 */
	public SqlFieldInstance(String _field, String _type, String _pointed_table, String _pointed_field,
			boolean _not_null, Object _instance) {
		super(_field, _type, _pointed_table, _pointed_field, _not_null);
		instance = _instance;
	}

	/**
	 * Constructor
	 * 
	 * @param _sql_field
	 *            The description of the SqlJet field.
	 * @param _instance
	 *            The field instance.
	 */
	public SqlFieldInstance(SqlField _sql_field, Object _instance) {
		super(_sql_field.field, _sql_field.type, _sql_field.pointed_table, _sql_field.pointed_field,
				_sql_field.not_null);
		instance = _instance;
	}

}