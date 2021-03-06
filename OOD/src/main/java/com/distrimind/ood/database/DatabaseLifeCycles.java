
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
package com.distrimind.ood.database;

/**
 * Interface whose functions are called after the database's creation
 * 
 * @author Jason Mahdjoub
 * @version 2.0.0
 * @since OOD 2.0
 */
public interface DatabaseLifeCycles {

	/**
	 * This function is called when the database was created and need to transfer
	 * data from old database. If old database does not exists, this function will
	 * not be called.
	 *
	 *
	 *
	 * @param wrapper the database wrapper
	 * @param newDatabaseConfiguration
	 *            the new database configuration
	 *
	 * @throws Exception
	 *             if a problem occurs
	 */
	void transferDatabaseFromOldVersion(DatabaseWrapper wrapper, DatabaseConfiguration newDatabaseConfiguration) throws Exception;

	/**
	 * This function is called after the database was created and after the eventual
	 * call of the function
	 * {@link #transferDatabaseFromOldVersion(DatabaseWrapper, DatabaseConfiguration)}
	 * 
	 * @param wrapper the database wrapper
	 * @param newDatabaseConfiguration
	 *            the new database configuration
	 * @throws Exception
	 *             if a problem occurs
	 */
	void afterDatabaseCreation(DatabaseWrapper wrapper, DatabaseConfiguration newDatabaseConfiguration) throws Exception;

	/**
	 * Tells if the old database must be removed after having created the new
	 * database
	 * 
	 * @return true if the old database must be removed after having created the new
	 *         database
	 * @throws Exception
	 *             if a problem occurs
	 */
	boolean hasToRemoveOldDatabase(DatabaseConfiguration databaseConfiguration) throws Exception;

	/**
	 * When a new version of the database is created, the synchronizer is reset.
	 * Then, the new database version must be synchronized with the new distant database version.
	 * During the synchronization process, if there are records that are conflictual between different peers,
	 * this function tells if the distant record can be ignored
	 * @return true if the distant conflictual record can be ignored
	 */
	boolean replaceDistantConflictualRecordsWhenDistributedDatabaseIsResynchronized(DatabaseConfiguration databaseConfiguration);

	/**
	 * This function is called when the database configurations are altered.
	 * The aim of this function is to save the database configurations and to make it persistent.
	 *
	 * @param databaseConfigurations the altered database configuration
	 */
	void saveDatabaseConfigurations(DatabaseConfigurations databaseConfigurations);


	/**
	 * This function is call to know if OOD must create a new backup reference which can take lot of time
	 * according the quantity of data into the database
	 * @param backupConfiguration the current backup configuration
	 * @param lastBackupReferenceTimeUTC the last backup reference time UTC
	 * @return true if OOD must create a new backup reference which can take lot of time
	 */
	default boolean mustCreateNewBackupReference(BackupConfiguration backupConfiguration, long lastBackupReferenceTimeUTC)
	{
		return backupConfiguration.mustCreateNewBackupReference(lastBackupReferenceTimeUTC);
	}

	/**
	 * This function is called when a new backup file was added concerning the given backup restore manager
	 * @param backupRestoreManager the backup restore manager
	 */
	default void newDatabaseBackupFileCreated(BackupRestoreManager backupRestoreManager)
	{

	}

}
