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

import com.distrimind.ood.database.exceptions.DatabaseException;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 2.5.0
 */
public class AndroidSQLiteDatabaseFactory extends DatabaseFactory<AndroidSQLiteDatabaseWrapper> {

    private boolean useExternalCard;
    private String databaseName;
    private String packageName;
    public AndroidSQLiteDatabaseFactory(Package packageName, String databaseName, boolean useExternalCard) throws DatabaseException {
        this(packageName.getName(), databaseName, useExternalCard);
    }
    public AndroidSQLiteDatabaseFactory(DatabaseConfigurations databaseConfigurations, Package packageName, String databaseName, boolean useExternalCard) throws DatabaseException {
        this(databaseConfigurations, packageName.getName(), databaseName, useExternalCard);
    }
    public AndroidSQLiteDatabaseFactory(String packageName, String databaseName, boolean useExternalCard) throws DatabaseException {
        this(null, packageName, databaseName, useExternalCard);
    }
    public AndroidSQLiteDatabaseFactory(DatabaseConfigurations databaseConfigurations, String packageName, String databaseName, boolean useExternalCard) throws DatabaseException {
        super(databaseConfigurations);
        if (packageName==null)
            throw new NullPointerException();
        if (databaseName==null)
            throw new NullPointerException();
        this.useExternalCard = useExternalCard;
        this.databaseName = databaseName;
        this.packageName = packageName;
    }


    @Override
    protected AndroidSQLiteDatabaseWrapper newWrapperInstance(DatabaseLifeCycles databaseLifeCycles, boolean createDatabasesIfNecessaryAndCheckIt) throws DatabaseException {
        return new AndroidSQLiteDatabaseWrapper(packageName, databaseName, databaseConfigurations, databaseLifeCycles,
                signatureProfileFactoryForAuthenticatedMessagesDestinedToCentralDatabaseBackup.getEncryptionProfileProviderSingleton(),
                encryptionProfileFactoryForE2EDataDestinedCentralDatabaseBackup.getEncryptionProfileProviderSingleton(),
                protectedEncryptionProfileFactoryProviderForAuthenticatedP2PMessages.getEncryptionProfileProviderSingleton(),
                getSecureRandom(), createDatabasesIfNecessaryAndCheckIt, useExternalCard);
    }

    @Override
    public void deleteDatabase() {
        AndroidSQLiteDatabaseWrapper.deleteDatabaseFiles(this);
    }

    public boolean isUseExternalCard() {
        return useExternalCard;
    }

    public void setUseExternalCard(boolean useExternalCard) {
        this.useExternalCard = useExternalCard;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        if (databaseName==null)
            throw new NullPointerException();
        this.databaseName = databaseName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        if (packageName==null)
            throw new NullPointerException();
        this.packageName = packageName;
    }
    public void setPackageName(Package packageName) {
        if (packageName==null)
            throw new NullPointerException();
        this.packageName = packageName.getName();
    }
}
