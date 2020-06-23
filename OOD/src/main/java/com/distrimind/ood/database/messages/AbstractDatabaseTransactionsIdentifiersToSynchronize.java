package com.distrimind.ood.database.messages;
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

import com.distrimind.ood.database.DatabaseEvent;
import com.distrimind.ood.database.DatabaseWrapper;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.io.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 3.0.0
 */
public class AbstractDatabaseTransactionsIdentifiersToSynchronize extends DatabaseEvent implements SecureExternalizable {
	protected DecentralizedValue hostIDSource;
	Map<DecentralizedValue, Long> lastTransactionFieldsBetweenDistantHosts;

	AbstractDatabaseTransactionsIdentifiersToSynchronize()
	{

	}

	@Override
	public int getInternalSerializedSize() {
		int res=4+lastTransactionFieldsBetweenDistantHosts.size()*8+ SerializationTools.getInternalSize(hostIDSource,0);
		for (DecentralizedValue dv: lastTransactionFieldsBetweenDistantHosts.keySet())
			res+=SerializationTools.getInternalSize(dv, 0);
		return res;
	}

	@Override
	public void writeExternal(SecuredObjectOutputStream out) throws IOException {
		out.writeObject(hostIDSource, false);
		out.writeInt(lastTransactionFieldsBetweenDistantHosts.size());
		for (Map.Entry<DecentralizedValue, Long> e : lastTransactionFieldsBetweenDistantHosts.entrySet())
		{
			out.writeObject(e.getKey(), false);
			out.writeLong(e.getValue());
		}
	}

	@Override
	public void readExternal(SecuredObjectInputStream in) throws IOException, ClassNotFoundException {
		hostIDSource=in.readObject(false, DecentralizedValue.class);
		int s=in.readInt();
		if (s<0 || s> DatabaseWrapper.MAX_DISTANT_PEERS)
			throw new MessageExternalizationException(Integrity.FAIL_AND_CANDIDATE_TO_BAN, ""+s);
		lastTransactionFieldsBetweenDistantHosts=new HashMap<>();
		for (int i=0;i<s;i++)
		{
			DecentralizedValue dv=in.readObject(false, DecentralizedValue.class);
			lastTransactionFieldsBetweenDistantHosts.put(dv, in.readLong());
		}
	}


	AbstractDatabaseTransactionsIdentifiersToSynchronize(DecentralizedValue hostIDSource,
														 Map<DecentralizedValue, Long> lastTransactionFieldsBetweenDistantHosts) {
		this.hostIDSource = hostIDSource;
		this.lastTransactionFieldsBetweenDistantHosts = lastTransactionFieldsBetweenDistantHosts;
	}



	public DecentralizedValue getHostSource() {
		return hostIDSource;
	}

	public Map<DecentralizedValue, Long> getLastDistantTransactionIdentifiers() {
		return lastTransactionFieldsBetweenDistantHosts;
	}

}