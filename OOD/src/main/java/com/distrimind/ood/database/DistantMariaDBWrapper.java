package com.distrimind.ood.database;
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

import com.distrimind.ood.database.exceptions.DatabaseException;

import java.io.File;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since OOD 2.5.0
 */
public class DistantMariaDBWrapper extends CommonMySQLWrapper{
	protected DistantMariaDBWrapper(String urlLocation, int port, String _database_name, String user, String password,
									int connectTimeInMillis,
									int socketTimeOutMillis,
									boolean useCompression,
									String characterEncoding,
									boolean useSSL,
									boolean trustServerCertificate,
									String enabledSslProtocolSuites,
									String enabledSslCipherSuites,
									File serverSslCert,
									boolean autoReconnect,
									String additionalParams) throws DatabaseException {
		super(urlLocation, port, _database_name, user, password,
				getURL(urlLocation, port, _database_name, connectTimeInMillis, socketTimeOutMillis, useCompression, characterEncoding, useSSL, trustServerCertificate, enabledSslProtocolSuites, enabledSslCipherSuites, serverSslCert, autoReconnect, additionalParams), characterEncoding);
		if (additionalParams==null)
			throw new NullPointerException();
	}

	protected DistantMariaDBWrapper(String urlLocation, int port, String _database_name, String user, String password,
									int connectTimeInMillis,
									int socketTimeOutMillis,
									boolean useCompression,
									String characterEncoding,
									boolean useSSL,
									boolean trustServerCertificate,
									String enabledSslProtocolSuites,
									String enabledSslCipherSuites,
									File serverSslCert,
									boolean autoReconnect) throws DatabaseException {
		super(urlLocation, port, _database_name, user, password,
				getURL(urlLocation, port, _database_name, connectTimeInMillis, socketTimeOutMillis, useCompression, characterEncoding, useSSL, trustServerCertificate, enabledSslProtocolSuites, enabledSslCipherSuites, serverSslCert, autoReconnect, null), characterEncoding);
	}

	private static String getURL(String urlLocation, int port,
								 String _database_name,
								 int connectTimeInMillis,
								 int socketTimeOutMillis,
								 boolean useCompression,
								 String characterEncoding,
								 boolean useSSL,
								 boolean trustServerCertificate,
								 String enabledSslProtocolSuites,
								 String enabledSslCipherSuites,
								 File serverSslCert,
								 boolean autoReconnect,
								 String additionalParams
								 )
	{

		return "jdbc:mariadb://"+urlLocation+":"+port+"/"+_database_name+"?"+"connectTimeout="+connectTimeInMillis+"&socketTimeout="+socketTimeOutMillis+
				"&useCompression="+useCompression+"&passwordCharacterEncoding="+characterEncoding+
				"&useSSL="+useSSL+
				"&trustServerCertificate="+trustServerCertificate+
				"&enabledSslProtocolSuites="+enabledSslProtocolSuites+
				"&enabledSslCipherSuites="+enabledSslCipherSuites+
				(serverSslCert!=null?"&serverSslCert="+serverSslCert.toURI().toString():"")+
				"&autoReconnect="+autoReconnect+
				(additionalParams==null?"":additionalParams);
	}
}
