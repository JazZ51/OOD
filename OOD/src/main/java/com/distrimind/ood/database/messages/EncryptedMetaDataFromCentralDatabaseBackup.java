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
package com.distrimind.ood.database.messages;

import com.distrimind.ood.database.DatabaseEvent;
import com.distrimind.ood.database.EncryptedDatabaseBackupMetaDataPerFile;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.io.SecureExternalizable;
import com.distrimind.util.io.SecuredObjectInputStream;
import com.distrimind.util.io.SecuredObjectOutputStream;
import com.distrimind.util.io.SerializationTools;

import java.io.IOException;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 3.0.0
 */
public class EncryptedMetaDataFromCentralDatabaseBackup extends DatabaseEvent implements MessageComingFromCentralDatabaseBackup, SecureExternalizable {

	private DecentralizedValue hostDestination, hostSource;
	private EncryptedDatabaseBackupMetaDataPerFile metaData;

	@SuppressWarnings("unused")
	private EncryptedMetaDataFromCentralDatabaseBackup() {
	}

	public EncryptedMetaDataFromCentralDatabaseBackup(DecentralizedValue hostDestination, DecentralizedValue hostSource, EncryptedDatabaseBackupMetaDataPerFile metaData) {
		if (hostDestination==null)
			throw new NullPointerException();
		if (hostSource==null)
			throw new NullPointerException();
		if(metaData==null)
			throw new  NullPointerException();
		this.hostDestination = hostDestination;
		this.hostSource = hostSource;
		this.metaData = metaData;
	}

	@Override
	public DecentralizedValue getHostDestination() {
		return hostDestination;
	}

	public DecentralizedValue getHostSource() {
		return hostSource;
	}

	public EncryptedDatabaseBackupMetaDataPerFile getMetaData() {
		return metaData;
	}

	@Override
	public int getInternalSerializedSize() {
		return SerializationTools.getInternalSize(hostSource)
				+SerializationTools.getInternalSize(hostDestination)
				+SerializationTools.getInternalSize(metaData);
	}

	@Override
	public void writeExternal(SecuredObjectOutputStream out) throws IOException {
		out.writeObject(hostSource, false);
		out.writeObject(hostDestination, false);
		out.writeObject(metaData, false);
	}

	@Override
	public void readExternal(SecuredObjectInputStream in) throws IOException, ClassNotFoundException {
		hostSource=in.readObject(false, DecentralizedValue.class);
		hostDestination=in.readObject(false, DecentralizedValue.class);
		metaData=in.readObject(false, EncryptedDatabaseBackupMetaDataPerFile.class);
	}

	@Override
	public boolean cannotBeMerged() {
		return true;
	}

	@Override
	public String toString() {
		return "EncryptedMetaDataFromCentralDatabaseBackup{" +
				"hostDestination=" + hostDestination +
				", hostSource=" + hostSource +
				'}';
	}
}
