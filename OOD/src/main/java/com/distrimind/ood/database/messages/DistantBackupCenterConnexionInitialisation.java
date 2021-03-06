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

import com.distrimind.ood.database.DatabaseWrapper;
import com.distrimind.ood.database.EncryptionTools;
import com.distrimind.ood.database.centraldatabaseapi.CentralDatabaseBackupCertificate;
import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.crypto.AbstractSecureRandom;
import com.distrimind.util.crypto.EncryptionProfileProvider;
import com.distrimind.util.io.SecuredObjectInputStream;
import com.distrimind.util.io.SecuredObjectOutputStream;
import com.distrimind.util.io.SerializationTools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 3.0.0
 */
public class DistantBackupCenterConnexionInitialisation extends AuthenticatedMessageDestinedToCentralDatabaseBackup {
	private Map<DecentralizedValue, byte[]> encryptedDistantLastValidatedIDs;

	private static final int MAX_DISTANT_VALIDATED_IDS_SIZE_IN_BYTES=4+DatabaseWrapper.MAX_DISTANT_PEERS*(DatabaseWrapper.MAX_ACCEPTED_SIZE_IN_BYTES_OF_DECENTRALIZED_VALUE+8+EncryptionTools.MAX_ENCRYPTED_ID_SIZE);

	@SuppressWarnings("unused")
	private DistantBackupCenterConnexionInitialisation() {
	}

	public DistantBackupCenterConnexionInitialisation(DecentralizedValue hostSource, Map<DecentralizedValue, Long> distantLastValidatedIDs, AbstractSecureRandom random, EncryptionProfileProvider encryptionProfileProvider, CentralDatabaseBackupCertificate certificate) throws DatabaseException {
		super(hostSource, certificate);
		if (distantLastValidatedIDs==null)
			throw new NullPointerException();
		if (distantLastValidatedIDs.containsKey(hostSource))
			throw new IllegalArgumentException();
		if (distantLastValidatedIDs.size()>DatabaseWrapper.MAX_DISTANT_PEERS)
			throw new IllegalArgumentException();
		this.encryptedDistantLastValidatedIDs =new HashMap<>();
		for (Map.Entry<DecentralizedValue, Long> e : distantLastValidatedIDs.entrySet())
		{
			if (e.getKey()==null)
				throw new NullPointerException();
			if (e.getValue()==null)
				throw new NullPointerException();
			try {
				assert e.getValue()!=Long.MIN_VALUE;
				this.encryptedDistantLastValidatedIDs.put(e.getKey(), EncryptionTools.encryptID(e.getValue(), random, encryptionProfileProvider));
			} catch (IOException ioException) {
				throw DatabaseException.getDatabaseException(ioException);
			}
		}
	}

	public Map<DecentralizedValue, byte[]> getEncryptedDistantLastValidatedIDs() {
		return encryptedDistantLastValidatedIDs;
	}

	@Override
	public boolean cannotBeMerged() {
		return true;
	}

	@Override
	public int getInternalSerializedSizeWithoutSignatures() {
		return super.getInternalSerializedSizeWithoutSignatures()
				+SerializationTools.getInternalSize(encryptedDistantLastValidatedIDs, MAX_DISTANT_VALIDATED_IDS_SIZE_IN_BYTES);
	}

	@Override
	public void writeExternalWithoutSignatures(SecuredObjectOutputStream out) throws IOException {
		super.writeExternalWithoutSignatures(out);
		out.writeMap(encryptedDistantLastValidatedIDs, false, MAX_DISTANT_VALIDATED_IDS_SIZE_IN_BYTES, false, false );
	}

	@Override
	public void readExternalWithoutSignatures(SecuredObjectInputStream in) throws IOException, ClassNotFoundException {
		super.readExternalWithoutSignatures(in);
		encryptedDistantLastValidatedIDs=in.readMap(false, MAX_DISTANT_VALIDATED_IDS_SIZE_IN_BYTES, false, false, DecentralizedValue.class, byte[].class);
	}

	@Override
	public String toString() {
		return "DistantBackupCenterConnexionInitialisation{}";
	}

	@Override
	public boolean equals(Object o) {
		return o == this;
	}
}
