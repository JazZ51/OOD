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

import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.ood.database.messages.AuthenticatedP2PMessage;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.crypto.EncryptionProfileProvider;
import com.distrimind.util.crypto.SymmetricAuthenticatedSignatureType;
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
class HookRemoveRequest extends DatabaseEvent implements AuthenticatedP2PMessage {
	private DecentralizedValue removedHookID, hostDestination, hostSource;
	private short encryptionProfileIdentifier;
	private byte[] symmetricSignature;

	@SuppressWarnings("unused")
	private HookRemoveRequest() {
	}

	HookRemoveRequest(DecentralizedValue hostSource, DecentralizedValue hostDestination, DecentralizedValue removedHookID, EncryptionProfileProvider encryptionProfileProvider) throws DatabaseException {
		if (hostSource==null)
			throw new NullPointerException();
		if (hostDestination==null)
			throw new NullPointerException();
		if (removedHookID==null)
			throw new NullPointerException();
		if (encryptionProfileProvider==null)
			throw new NullPointerException();
		if (hostDestination.equals(removedHookID))
			throw new IllegalArgumentException();
		this.hostSource=hostSource;
		this.hostDestination=hostDestination;
		this.removedHookID = removedHookID;
		this.encryptionProfileIdentifier = encryptionProfileProvider.getDefaultKeyID();
		this.symmetricSignature=sign(encryptionProfileProvider);

	}

	@Override
	public void writeExternalWithoutSignature(SecuredObjectOutputStream out) throws IOException {
		out.writeObject(hostSource, false);
		out.writeObject(hostDestination, false);
		out.writeObject(removedHookID, false);
		out.writeShort(encryptionProfileIdentifier);
	}



	@Override
	public DecentralizedValue getHostDestination() {
		return hostDestination;
	}

	@Override
	public DecentralizedValue getHostSource() {
		return hostSource;
	}

	@Override
	public int getInternalSerializedSize() {
		return 2+ SerializationTools.getInternalSize((SecureExternalizable)hostSource)
				+SerializationTools.getInternalSize((SecureExternalizable)hostDestination)
				+SerializationTools.getInternalSize((SecureExternalizable)removedHookID);
	}

	@Override
	public void writeExternal(SecuredObjectOutputStream out) throws IOException {
		writeExternalWithoutSignature(out);
		out.writeBytesArray(symmetricSignature, false, SymmetricAuthenticatedSignatureType.MAX_SYMMETRIC_SIGNATURE_SIZE);
	}

	@Override
	public void readExternal(SecuredObjectInputStream in) throws IOException, ClassNotFoundException {
		hostSource=in.readObject(false, DecentralizedValue.class);
		hostDestination=in.readObject(false, DecentralizedValue.class);
		removedHookID=in.readObject(false, DecentralizedValue.class);
		encryptionProfileIdentifier=in.readShort();
		symmetricSignature=in.readBytesArray(false, SymmetricAuthenticatedSignatureType.MAX_SYMMETRIC_SIGNATURE_SIZE);
	}

	@Override
	public byte[] getSymmetricSignature() {
		return symmetricSignature;
	}

	@Override
	public short getEncryptionProfileIdentifier() {
		return encryptionProfileIdentifier;
	}


}