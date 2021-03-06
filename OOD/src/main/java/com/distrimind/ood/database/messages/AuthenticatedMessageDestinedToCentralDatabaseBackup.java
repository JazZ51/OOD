package com.distrimind.ood.database.messages;
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

import com.distrimind.ood.database.DatabaseEvent;
import com.distrimind.ood.database.centraldatabaseapi.CentralDatabaseBackupCertificate;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.crypto.ASymmetricAuthenticatedSignatureType;
import com.distrimind.util.crypto.IASymmetricPublicKey;
import com.distrimind.util.io.SecuredObjectInputStream;
import com.distrimind.util.io.SecuredObjectOutputStream;
import com.distrimind.util.io.SerializationTools;

import java.io.IOException;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 3.0.0
 */
public abstract class AuthenticatedMessageDestinedToCentralDatabaseBackup extends DatabaseEvent implements MessageDestinedToCentralDatabaseBackup, AuthenticatedCentralDatabaseBackupMessage {


	private DecentralizedValue hostSource;

	private byte[] asymmetricSignature;
	private CentralDatabaseBackupCertificate certificate;

	protected AuthenticatedMessageDestinedToCentralDatabaseBackup() {
	}

	protected AuthenticatedMessageDestinedToCentralDatabaseBackup(DecentralizedValue hostSource, CentralDatabaseBackupCertificate certificate) {
		if (hostSource==null)
			throw new NullPointerException();
		if (certificate==null)
			throw new NullPointerException();
		this.hostSource = hostSource;
		this.certificate = certificate;

	}

	@Override
	public IASymmetricPublicKey getCertificateASymmetricPublicKey()
	{
		return certificate.getCertifiedAccountPublicKey();
	}

	@Override
	public byte[] getSignatures() {
		return asymmetricSignature;
	}

	@Override
	public void setSignatures(byte[] signatures) {
		if (signatures==null)
			throw new NullPointerException();
		this.asymmetricSignature=signatures;
	}

	@Override
	public DecentralizedValue getHostSource() {
		return hostSource;
	}


	@Override
	public void writeExternalWithoutSignatures(SecuredObjectOutputStream out) throws IOException {
		out.writeObject(hostSource, false);
		out.writeObject(certificate, false);
	}

	@Override
	public void readExternalWithoutSignatures(SecuredObjectInputStream in) throws IOException, ClassNotFoundException {
		hostSource=in.readObject(false, DecentralizedValue.class );
		certificate=in.readObject(false);
	}



	@Override
	public int getInternalSerializedSizeWithoutSignatures() {
		int res= SerializationTools.getInternalSize(hostSource, 0)+2+SerializationTools.getInternalSize(certificate);
		res+=SerializationTools.getInternalSize(asymmetricSignature, ASymmetricAuthenticatedSignatureType.MAX_ASYMMETRIC_SIGNATURE_SIZE);
		return res;
	}



	public CentralDatabaseBackupCertificate getCertificate() {
		return certificate;
	}

	@Override
	public String toString() {
		return "AuthenticatedMessageDestinedToCentralDatabaseBackup{" +
				"hostSource=" + hostSource+
				'}';
	}

	public abstract boolean equals(Object o);
}
