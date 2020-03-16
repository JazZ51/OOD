package com.distrimind.ood.database;

import android.annotation.SuppressLint;

import com.distrimind.ood.database.exceptions.DatabaseException;

import org.sqldroid.SQLDroidBlob;

import java.io.File;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;

public class AndroidSQLiteDatabaseWrapper extends DatabaseWrapper {

    private String url;
    private static volatile boolean driverLoaded=false;

    @SuppressLint("SdCardPath")
    private static String getPath(String _package, String _database_name, boolean externalStorage)
    {
        return (externalStorage?"/sdcard/":"/data/data/") + _package + "/"+_database_name+".db";
    }


    AndroidSQLiteDatabaseWrapper(String _package, String _database_name, boolean externalStorage) throws DatabaseException {
        super(_database_name, new File(getPath(_package, _database_name, externalStorage)), false,false);
        url = "jdbc:sqldroid:" + getPath(_package, _database_name, externalStorage);
        checkDriverLoading();
    }

    public static boolean deleteDatabaseFiles(String _package, String _database_name, boolean externalStorage) {
        File f=new File(getPath(_package, _database_name, externalStorage));
        if (f.exists())
            return f.delete();
        return false;
    }

    public static boolean deleteDatabaseFiles(AndroidSQLiteDatabaseFactory factory) {
        return deleteDatabaseFiles(factory.getPackageName(), factory.getDatabaseName(), factory.isUseExternalCard());
    }

    private static void checkDriverLoading()
    {
        if (!driverLoaded) {
            driverLoaded=true;
            try {
                DriverManager.registerDriver((Driver) Class.forName("org.sqldroid.SQLDroidDriver").newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Failed to register SQLDroidDriver");
            }
        }

    }
    @Override
    protected Connection reopenConnectionImpl() throws DatabaseLoadingException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new DatabaseLoadingException("Impossible to load database", e);
        }
    }

    @Override
    protected String getCachedKeyword() {
        return "";
    }

    @Override
    protected String getNotCachedKeyword() {
        return "";
    }

    @Override
    public boolean supportCache() {
        return false;
    }

    @Override
    public boolean supportNoCacheParam() {
        return false;
    }

    @Override
    protected void closeConnection(Connection c, boolean deepClosing) throws SQLException {
        c.setAutoCommit(true);
        c.close();
    }

    @Override
    protected void startTransaction(Session _openedConnection, TransactionIsolation transactionIsolation, boolean write) {

    }

    @Override
    protected void rollback(Connection openedConnection) throws SQLException {
        openedConnection.rollback();
    }

    @Override
    protected void commit(Connection openedConnection) throws SQLException {
        openedConnection.commit();
    }

    @Override
    protected boolean supportSavePoint(Connection openedConnection) {
        return false;
    }

    @Override
    protected void rollback(Connection openedConnection, String savePointName, Savepoint savePoint) throws SQLException {
        openedConnection.rollback(savePoint);
    }

    @Override
    protected void disableAutoCommit(Connection openedConnection) throws SQLException {
        openedConnection.setAutoCommit(false);
    }

    @Override
    protected Savepoint savePoint(Connection openedConnection, String savePoint) throws SQLException {
        return openedConnection.setSavepoint(savePoint);
    }

    @Override
    protected void releasePoint(Connection openedConnection, String _savePointName, Savepoint savepoint) throws SQLException {
        openedConnection.releaseSavepoint(savepoint);
    }

    @Override
    protected boolean isSerializationException(SQLException e) {
        return false;
    }

    @Override
    protected boolean isTransactionDeadLockException(SQLException e) {
        return false;
    }

    @Override
    protected boolean isDisconnectionException(SQLException e) {
        return false;
    }

    @Override
    protected boolean isDuplicateKeyException(SQLException e) {
        return false;
    }

    @Override
    public String getAutoIncrementPart(long startWith) {
        return "";
    }

    @Override
    protected boolean doesTableExists(String tableName) throws Exception {
        try(ResultSet rs=getConnectionAssociatedWithCurrentThread().getConnection().getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    @Override
    protected Table.ColumnsReadQuerry getColumnMetaData(String tableName, String columnName) throws Exception {
        Connection c;
        ResultSet rs=(c=getConnectionAssociatedWithCurrentThread().getConnection()).getMetaData().getColumns(database_name, null, tableName, columnName);
        return new CReadQuerry(c, rs);
    }

    static class CReadQuerry extends Table.ColumnsReadQuerry {

        CReadQuerry(Connection _sql_connection, ResultSet resultSet) {
            super(_sql_connection, resultSet);
            setTableColumnsResultSet(new TCResultSet(resultSet));
        }
    }

    static class TCResultSet extends DatabaseWrapper.TableColumnsResultSet {

        TCResultSet(ResultSet _rs) {
            super(_rs);
        }

        @Override
        public String getColumnName() throws SQLException {
            return resultSet.getString(4);
        }

        @Override
        public String getTypeName() throws SQLException {
            return resultSet.getString(6);
        }

        @Override
        public int getColumnSize() throws SQLException {
            return resultSet.getInt(7);
        }

        @Override
        public boolean isNullable() throws SQLException {
            return resultSet.getInt(11)==1;
        }

        @Override
        public boolean isAutoIncrement() {
            throw new InternalError();
        }

        @Override
        public int getOrdinalPosition() {
            throw new InternalError();
        }

    }


    @Override
    protected void checkConstraints(Table<?> table) {
        //TODO complete
    }

    @Override
    protected String getSqlComma() {
        return "";
    }

    @Override
    protected int getVarCharLimit() {
        return 2147483647;
    }

    @Override
    protected boolean isVarBinarySupported() {
        return true;
    }

    @Override
    protected boolean isLongVarBinarySupported() {
        return false;
    }

    @Override
    protected String getSqlNULL() {
        return "";
    }

    @Override
    protected String getSqlNotNULL() {
        return "NOT NULL";
    }

    @Override
    protected String getByteType() {
        return "INTEGER";
    }

    @Override
    protected String getIntType() {
        return "INTEGER";
    }

    @Override
    protected String getBlobType(long limit) {
        return "BLOB";
    }

    @Override
    protected String getTextType(long limit) {
        return "TEXT";
    }

    @Override
    protected String getFloatType() {
        return "REAL";
    }

    @Override
    protected String getDoubleType() {
        return "REAL";
    }

    @Override
    protected String getShortType() {
        return "INTEGER";
    }

    @Override
    protected String getLongType() {
        return "INTEGER";
    }

    @Override
    protected String getBigDecimalType(long limit) {
        return "BLOB";
    }

    @Override
    protected String getBigIntegerType(long limit) {
        return "BLOB";
    }

    @Override
    protected String getDateTimeType() {
        return "INTEGER";
    }

    @Override
    protected String getDropTableCascadeQuery(Table<?> table) {
        return "DROP TABLE IF EXISTS " + table.getSqlTableName()
                + " CASCADE";
    }

    @Override
    protected boolean supportSingleAutoPrimaryKeys() {
        return false;
    }

    @Override
    protected boolean supportMultipleAutoPrimaryKeys() {
        return false;
    }

    @Override
    protected String getOnUpdateCascadeSqlQuery() {
        return "";
    }

    @Override
    protected String getOnDeleteCascadeSqlQuery() {
        return "";
    }

    @Override
    protected Blob getBlob(byte[] bytes) {
        return new SQLDroidBlob(bytes);
    }

    @Override
    public void nativeBackup(File path) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean isThreadSafe() {
        return false;
    }

    @Override
    protected boolean supportFullSqlFieldName() {
        return true;
    }

    @Override
    protected String getLimitSqlPart(long startPosition, long rowLimit) {
        StringBuilder limit=new StringBuilder();
        if (rowLimit>=0)
        {
            limit.append(" LIMIT ");
            limit.append(rowLimit);
            if (startPosition>0)
            {
                limit.append(" OFFSET ");
                limit.append(startPosition);
            }
        }
        return limit.toString();
    }
}