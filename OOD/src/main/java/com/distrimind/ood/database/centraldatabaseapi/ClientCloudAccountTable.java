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

import com.distrimind.ood.database.DatabaseRecord;
import com.distrimind.ood.database.DatabaseWrapper;
import com.distrimind.ood.database.Table;
import com.distrimind.ood.database.annotations.*;
import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.crypto.IASymmetricPublicKey;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 3.0.0
 */
public class ClientCloudAccountTable extends Table<ClientCloudAccountTable.Record> {
	public static final int MAX_EXTERNAL_ACCOUNT_ID_SIZE_IN_BYTES= DatabaseWrapper.MAX_ACCEPTED_SIZE_IN_BYTES_OF_DECENTRALIZED_VALUE+4;
	protected ClientCloudAccountTable() throws DatabaseException {
	}

	public static class Record extends DatabaseRecord
	{


		@SuppressWarnings("unused")
		@AutoPrimaryKey
		private long accountID;

		@Field
		private short maxClients;


		@Field(limit = IASymmetricPublicKey.MAX_SIZE_IN_BYTES_OF_KEY)
		@Unique
		private IASymmetricPublicKey externalAccountID;


		@SuppressWarnings("unused")
		private Record() {
		}
		public Record(short maxClients) {
			this(maxClients, null);
		}
		public Record(short maxClients, IASymmetricPublicKey externalAccountID) {
			if (maxClients<1)
				throw new IllegalArgumentException();
			this.externalAccountID = externalAccountID;
			this.maxClients = maxClients;
		}

		public long getAccountID() {
			return accountID;
		}

		public IASymmetricPublicKey getExternalAccountID() {
			return externalAccountID;
		}

		public void setExternalAccountID(IASymmetricPublicKey externalAccountID) {
			this.externalAccountID = externalAccountID;
		}

		public short getMaxClients() {
			return maxClients;
		}

	}
}
