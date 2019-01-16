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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since MaDKitLanEdition 2.0
 */
public class EmbeddedH2DatabaseWrapper extends CommonHSQLH2DatabaseWrapper{

	private static boolean hsql_loaded = false;
	private final File file_name;
	private static Constructor<? extends Blob> H2BlobConstructor=null;
	private static Method H2ValueMethod=null;

	/**
	 * Constructor
	 *
	 * @param _file_name
	 *            The file which contains the database. If this file does not
	 *            exists, it will be automatically created with the correspondent
	 *            database.
	 *
	 * @throws NullPointerException
	 *             if parameters are null pointers.
	 * @throws IllegalArgumentException
	 *             If the given file is a directory.
	 * @throws DatabaseException if a problem occurs
	 */
	public EmbeddedH2DatabaseWrapper(File _file_name) throws DatabaseException {
		this(_file_name, false);
	}
	/**
	 * Constructor
	 *
	 * @param _file_name
	 *            The file which contains the database. If this file does not
	 *            exists, it will be automatically created with the correspondent
	 *            database.
	 * @param alwaysDeconectAfterOnTransaction true if the database must always be connected and detected during one transaction
	 * @throws NullPointerException
	 *             if parameters are null pointers.
	 * @throws IllegalArgumentException
	 *             If the given file is a directory.
	 * @throws DatabaseException if a problem occurs
	 */
	public EmbeddedH2DatabaseWrapper(File _file_name, boolean alwaysDeconectAfterOnTransaction) throws DatabaseException {
		super("Database from file : " + getH2DataFileName(_file_name) + ".data", alwaysDeconectAfterOnTransaction);
		this.file_name = _file_name;
	}



	private static void ensureH2Loading() throws DatabaseLoadingException {
		synchronized (EmbeddedHSQLDBWrapper.class) {
			if (!hsql_loaded) {
				try {
					Class.forName("org.h2.Driver");

					//noinspection SingleStatementInBlock,unchecked
					H2BlobConstructor=(Constructor<? extends Blob>)Class.forName("org.h2.jdbc.JdbcBlob").getDeclaredConstructor(Class.forName("org.h2.jdbc.JdbcConnection"), Class.forName("org.h2.value.Value"), int.class);
					H2ValueMethod=Class.forName("org.h2.value.ValueBytes").getDeclaredMethod("get", byte[].class);
					//DbBackupMain=Class.forName("org.hsqldb.lib.tar.DbBackupMain").getDeclaredMethod("main", (new String[0]).getClass());
					hsql_loaded = true;
				} catch (Exception e) {
					throw new DatabaseLoadingException("Impossible to load H2 ", e);
				}
			}
		}
	}
	private static Connection getConnection(File _file_name)
			throws DatabaseLoadingException {
		if (_file_name == null)
			throw new NullPointerException("The parameter _file_name is a null pointer !");
		if (_file_name.isDirectory())
			throw new IllegalArgumentException("The given file name is a directory !");
		ensureH2Loading();
		try {
			Connection c = DriverManager
					.getConnection("jdbc:h2:file:" + getH2DataFileName(_file_name), "SA", "");
			databaseShutdown.set(false);

			return c;
		} catch (Exception e) {
			throw new DatabaseLoadingException("Impossible to create the database into the file " + _file_name, e);
		}
	}

	private static String getH2DataFileName(File _file_name) {
		if (_file_name.isDirectory())
			throw new IllegalArgumentException();

		String s = _file_name.getAbsolutePath();
		if (s.toLowerCase().endsWith(".data"))
			return s.substring(0, s.length() - 5);
		else
			return s;
	}
	private final static AtomicBoolean databaseShutdown = new AtomicBoolean(false);

	@Override
	protected void closeConnection(Connection connection, boolean deepClose) throws SQLException {
		if (!deepClose || databaseShutdown.getAndSet(true)) {
			connection.close();
		} else {
			try (Statement s = connection.createStatement()) {
				s.executeQuery("SHUTDOWN" + getSqlComma());
			} finally {
				connection.close();
			}
		}

	}

	@Override
	protected Connection reopenConnectionImpl() throws DatabaseLoadingException {
		return getConnection(file_name);

	}

	@Override
	protected Blob getBlob(byte[] bytes) throws SQLException {
		try {
			return H2BlobConstructor.newInstance(getConnectionAssociatedWithCurrentThread().getConnection(), H2ValueMethod.invoke(null, (Object) bytes), -1);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | DatabaseException e) {
			throw new SQLException(e);
		}
		catch(InvocationTargetException e)
		{
			Throwable t=e.getCause();
			if (t instanceof SQLException)
				throw (SQLException)t;
			else
				throw new SQLException(t);
		}
	}

	/**
	 * Backup the database into the given directory.
	 *
	 * @param path
	 *            the path where to save the database. If <code>saveAsFiles</code>
	 *            is set to false, it must be a directory, else it must be a file.
	 * @throws DatabaseException
	 *             if a problem occurs
	 */
	public void backup(File path)
			throws DatabaseException {
		if (path == null)
			throw new NullPointerException("file");

		if (path.exists()) {
			if (!path.isFile())
				throw new IllegalArgumentException("The given path (" + path.getAbsolutePath() + ") must be a file !");
		}
		String f = path.getAbsolutePath();
		if (!f.toLowerCase().endsWith(".zip"))
			f+=".zip";

		final String querry = "BACKUP TO '" + f;
		this.runTransaction(new Transaction() {

			@Override
			public TransactionIsolation getTransactionIsolation() {
				return TransactionIsolation.TRANSACTION_SERIALIZABLE;
			}

			@Override
			public Object run(DatabaseWrapper _sql_connection) throws DatabaseException {
				try {

					lockWrite();
					Connection sql_connection = getConnectionAssociatedWithCurrentThread().getConnection();
					try (PreparedStatement preparedStatement = sql_connection.prepareStatement(querry)) {
						preparedStatement.execute();
					}
					return null;
				} catch (Exception e) {
					throw new DatabaseException("", e);
				} finally {

					unlockWrite();
				}
			}

			@Override
			public boolean doesWriteData() {
				return false;
			}

			@Override
			public void initOrReset() {

			}
		}, true);

	}

	@Override
	protected void startTransaction(Session _openedConnection, TransactionIsolation transactionIsolation, boolean write)
			throws SQLException {
		String isoLevel;
		switch (transactionIsolation) {
			case TRANSACTION_NONE:
			case TRANSACTION_READ_COMMITTED:
				isoLevel = "3";
				break;
			case TRANSACTION_READ_UNCOMMITTED:
				isoLevel = "0";
				break;
			case TRANSACTION_REPEATABLE_READ:
				isoLevel = "1 READ";
				break;
			case TRANSACTION_SERIALIZABLE:
				isoLevel = "1";
				break;
			default:
				throw new IllegalAccessError();

		}

		try (Statement s = _openedConnection.getConnection().createStatement()) {
			s.executeQuery("SET LOCK_MODE " + isoLevel + getSqlComma());
		}

	}

	@Override
	protected boolean isDisconnetionException(SQLException e) {
		return e.getErrorCode()==90067;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void deleteDatabaseFiles(File _file_name) {
		String file_base = getH2DataFileName(_file_name);
		File f = new File(file_base + ".data");
		if (f.exists())
			f.delete();
		f = new File(file_base + ".log");
		if (f.exists())
			f.delete();
		f = new File(file_base + ".properties");
		if (f.exists())
			f.delete();
		f = new File(file_base + ".script");
		if (f.exists())
			f.delete();
		f = new File(file_base + ".tmp");
		if (f.exists())
			f.delete();
		f = new File(file_base + ".lobs");
		if (f.exists())
			f.delete();
	}


}
