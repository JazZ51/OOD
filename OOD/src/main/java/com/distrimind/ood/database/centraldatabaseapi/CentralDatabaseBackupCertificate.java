package com.distrimind.ood.database.centraldatabaseapi;
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

import com.distrimind.util.DecentralizedIDGenerator;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.SecuredDecentralizedID;
import com.distrimind.util.crypto.EncryptionProfileProvider;
import com.distrimind.util.crypto.IASymmetricPublicKey;
import com.distrimind.util.crypto.MessageDigestType;
import com.distrimind.util.crypto.SecureRandomType;
import com.distrimind.util.data_buffers.WrappedSecretData;
import com.distrimind.util.io.Integrity;
import com.distrimind.util.io.SecureExternalizable;
import com.distrimind.util.properties.MultiFormatProperties;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 3.0.0
 */
public abstract class CentralDatabaseBackupCertificate extends MultiFormatProperties implements SecureExternalizable {
	public static final int MAX_SIZE_IN_BYTES_OF_CERTIFICATE_IDENTIFIER =32;

	public static byte[] generateCertificateIdentifier()
	{
		try {
			WrappedSecretData t=new SecuredDecentralizedID(MessageDigestType.SHA3_256, new DecentralizedIDGenerator(), SecureRandomType.DEFAULT.getInstance(null)).encode();
			return Arrays.copyOfRange(t.getBytes(), 1, Math.max(t.getBytes().length-1, MAX_SIZE_IN_BYTES_OF_CERTIFICATE_IDENTIFIER));
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new IllegalAccessError();
		}
	}

	protected CentralDatabaseBackupCertificate() {
		super(null);
	}

	public abstract IASymmetricPublicKey getCertifiedAccountPublicKey();
	public abstract byte[] getCertificateIdentifier();
	public abstract long getCertificateExpirationTimeUTCInMs();

	public abstract Integrity isValidCertificate(long accountID, IASymmetricPublicKey externalAccountID, DecentralizedValue hostID, DecentralizedValue centralID);


}
