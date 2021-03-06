
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

package com.distrimind.ood.database.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation must be declared before a DatabaseRecord field. It is useless
 * to use this annotation with the next annotations :
 * {@link com.distrimind.ood.database.annotations.AutoPrimaryKey},
 * {@link com.distrimind.ood.database.annotations.RandomPrimaryKey},
 * {@link com.distrimind.ood.database.annotations.PrimaryKey},
 * {@link com.distrimind.ood.database.annotations.ForeignKey}. The accepted
 * field types are all native java types, there correspondent class (i.e.
 * 'Float' for 'float'), array of bytes, String, BigInteger, BigDecimal, and
 * DatabaseRecord for foreign keys.
 * 
 * @author Jason Mahdjoub
 * @version 1.2
 * @since OOD 1.0
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

	/**
	 * 
	 * @return The value limit in elements. This parameter concerns only the String
	 *         type, the native byte array type, and the Object array type.
	 */
	long limit() default 0;

	/**
	 * 
	 * @return true if the field has to be indexed into the database in order to
	 *         increase the database parse when this field is concerned.
	 */
	boolean index() default false;

	/**
	 * If true, use descending order for the column to create the index. Else, use
	 * ascending order. Using the descending order for a column can help improve the
	 * performance of queries that require the results in mixed sort order or
	 * descending order and for queries that select the minimum or maximum value of
	 * an indexed column.
	 * 
	 * @return true if use descending order for the column to create the index
	 */
	boolean descendingIndex() default false;


	/**
	 * Define the SQL field name.
	 * By default, the SQL field takes the name of the class field name.
	 * @return the SQL field name.
	 */
	String sqlFieldName() default "";

	/**
	 * Tels if the field can be cached or not.
	 * In most time, it's the all table containing this field that is not cached.
	 * @return true if the cache for the concerned field must be disabled
	 */
	boolean disableCache() default false;

	/**
	 *
	 * @return true if the key expiration UTC must be included into the field. Concerns only {@link com.distrimind.util.crypto.ASymmetricPublicKey} and {@link com.distrimind.util.crypto.ASymmetricKeyPair}.
	 */
	boolean includeKeyExpiration() default true;

	/**
	 *
	 * @return true if the field must use blob type, even if the size limit is lower than the database threshold
	 */
	boolean forceUsingBlobOrClob() default false;
}
