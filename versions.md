Object Oriented Database
========================
3.1.0 BETA 2 (Build: 4505) (from 01/04/2013 to 07/07/2021)

# Creator(s):
Jason MAHDJOUB

# Developer(s):
Jason MAHDJOUB (Entered in the team at 01/04/2013)

# Modifications:


### 3.1.0 BETA 2 (07/07/2021)
* Remove concatenated sql queries


### 3.1.0 BETA 1 (07/07/2021)
* Update Utils to 5.18.5
* Make OOD compatible with MadKitLanEdition
* Fix NullPointerException into DatabaseConfiguration class
* Add function void DatabaseFactory.setEncryptionProfileProviders(EncryptionProfileProviderFactory, SecureRandomType randomType, byte[], byte[])
* Add function void DatabaseFactory.setEncryptionProfileProviders(EncryptionProfileProviderFactory, SecureRandomType randomType)
* Fix bad using of central database backup when it is not used
* Fix issue with notification of local host initialization
* Fix issue : update internal table state when database becomes decentralized
* Fix issue with DatabaseConfigurationsBuilder
* Fix issue when removing peer whereas OOD does not use CentralDatabaseBackup
* Fix issue with function DatabaseConfigurationBuilder.resetSynchronizerAndRemoveAllHosts() : distant peers and local host id were not removed
* Add class CentralDatabaseBackupReceiverFactory
* Fix null pointer exceptions
* Fix class cast exceptions
* Add function CentralDatabaseBackupReceiver.sendMessageFromThisCentralDatabaseBackup(MessageComingFromCentralDatabaseBackup)
* Add function BackupRestoreManager.hasNonFinalFiles()
* Add function DatabaseWrapper.getNextPossibleEventTimeUTC()


### 3.0.0 STABLE (24/05/2021)
* Update Utils to 5.17.6
* Update HSQLDB to 2.5.1
* Implementation of data synchronization with central database backup
* Each pear can transfer its encrypted backup into the central database that do not permit backup reversion in a short time.
* Removed data into central database backup are removed only after a delay has occurred. Same thing with removed accounts.
* When a peer is added into the network, it is not necessary to add it into each peach. The adding is broadcast into the network.
* Primary keys can be now decentralized
* Do not synchronize database that do not share the same version
* Make OOD compatible with Android
* Implementation of driver for MySQL
* Implementation of driver for Android H2 Database
* Implementation of driver for PostgreSQL
* Compatible with Java 8 and newer
* Calendar is better serialized into database (better performances, and lower space)
* Calendar is now comparable
* Implementation of DatabaseConfigurationBuilder that centralize database loading, database restoration, and database synchronization with decentralized peers and with central backup database. The profile can be saved into the disk.
* Add function Table.removeAllRecordsWithCascade()
* Fix issue when changing database's version and refresh tables state when BackupRestoreManager was loaded
* Add function Table.hasRecords(String, Map)
* Add function Table.hasRecords(Filter, String, Map)
* Add function DatabaseLifeCycles.peersAdded(Set<DecentralizedValue>) that is triggered when a set of peers was added
* Fix join issue when table A has two foreign keys that reference the same table B
* Optimize queries by using junctions
* Add possibility to personalize the moment when OOD will create a new database backup
* Add possibility to notify user that new database backup files were created
* Extends authenticated messages for critical queries, by using protected keys
* When local host identifier is changed or removed, distant hosts are notified


### 2.4.0 STABLE (11/02/2020)
* Update Utils to 4.9.0
* Update database factories
* Database wrappers are now instantiable only through factories


### 2.3.21 STABLE (24/01/2020)
* Update Utils to 4.8.6


### 2.3.14 STABLE (07/01/2020)
* Rename RandomPrivateKey.byteNumber but do not change its behavior


### 2.3.13 STABLE (16/12/2019)
* Update Utils to 4.7.1 STABLE


### 2.3.12 STABLE (22/11/2019)
* Update Utils to 4.7.0 STABLE


### 2.3.10 STABLE (15/11/2019)
* Update Utils to 4.6.5 STABLE
* Compile with openjdk 13 (compatibility set to Java 7


### 2.3.8 STABLE (13/11/2019)
* Update Utils to 4.6.3 STABLE
* Fix backup cache upgrade issue when canceling transaction


### 2.3.7 STABLE (31/10/2019)
* Update Utils to 4.6.2 STABLE


### 2.3.6 STABLE (20/10/2019)
* Update dependencies


### 2.3.4 STABLE (17/10/2019)
* Update Utils to 4.6.0 STABLE
* Fix NullPointerException into DatabaseWrapper$DatabaseSynchronizer.getLocalHostID()
* Fix HookAddRequest externalisation
* Fix issue when loading the same database two times
* Better reset synchronization
* Better disconnect all hosts


### 2.2.1 STABLE (02/09/2019)
* Add function DatabaseWrapper.Synchronizer.getDistantHostsIds


### 2.2.0 STABLE (16/07/2019)
* Update Utils to 4.4.3 STABLE
* Add backup/restore manager, with historical management
* Better manage database versions
* Optimizations of several queries
* Add function Table.removeAllRecordsWithCascade()
* Use long values for queries limited by a number of rows


### 2.0.0 BETA 107 (06/05/2019)
* Update Utils to 3.26.0 STABLE
* Add Field.includeKeyExpiration()


### 2.0.0 BETA 106 (23/04/2019)
* Update Utils to 3.25.6 STABLE
* Fix problem with getRecordsNumber function using where condition


### 2.0.0 BETA 104 (21/03/2019)
* Update Utils to 3.25.5 STABLE


### 2.0.0 BETA 103 (13/03/2019)
* Update Utils to 3.25.4 STABLE


### 2.0.0 BETA 100 (01/03/2019)
* Add function DatabaseCollisionsNotifier.areDuplicatedEventsNotConsideredAsCollisions()
* Add function DatabaseCollisionsNotifier.startNewSynchronizationTransaction()
* Add function DatabaseCollisionsNotifier.endSynchronizationTransaction()


### 2.0.0 BETA 99 (08/02/2019)
* Better support of sub fields into queries


### 2.0.0 BETA 98 (06/02/2019)
* Update Utils to 3.25.1


### 2.0.0 BETA 97 (05/02/2019)
* Security fix : disable cache for tables that use secret ou private keys
* Security improvement : add Field.disableCache property
* Add function Table.isCached()


### 2.0.0 BETA 96 (25/01/2019)
* Add function Table.removeRecord(Map keys)
* Add function Table.removeRecord(Object...keys)
* Add function Table.removeRecordWithCascade(Map keys)
* Add function Table.removeRecordWithCascade(Object...keys)
* Do not generate conflicts if events are the same


### 2.0.0 BETA 95 (18/01/2019)
* Add H2 database driver


### 2.0.0 BETA 94 (15/01/2019)
* Use shortest table's name
* Add possibility to personalize SQL table name (see annotation TableName)
* Add possibility to personalize SQL field name (see annotation Field)


### 2.0.0 BETA 93 (17/12/2018)
* Updating utils to 3.24.0


### 2.0.0 BETA 90 (13/11/2018)
* Updating utils to 3.22.0


### 2.0.0 BETA 89 (07/11/2018)
* Updating utils to 3.21.1
* Manage Keys used for encryption as decentralized keys
* Add DatabaseAnomaliesNotifier interface
* Add DatabaseCollisionsNotifier interface


### 2.0.0 BETA 85 (27/07/2018)
* Updating utils to 3.18.0


### 2.0.0 BETA 84 (17/07/2018)
* Updating utils to 3.17.0
* Do not loop infinitely if disconnection exception is generated infinitely


### 2.0.0 BETA 83 (12/07/2018)
* Updating utils to 3.16.1
* Clean code
* Add auto disconnection option with database wrappers


### 2.0.0 BETA 82 (16/05/2018)
* Updating utils to 3.15.0


### 2.0.0 BETA 71 (30/03/2018)
* Optimization of CachedInputStream
* Optimization of CachedOutputStream
* Updating utils to 3.13.1


### 2.0.0 BETA 66 (10/02/2018)
* Updating utils to 3.10.5
* Correcting a bug with the nativeBackup of enum variables


### 2.0.0 BETA 59 (31/01/2018)
* Updating utils to 3.9.0


### 2.0.0 BETA 57 (13/12/2017)
* Updating utils to 3.7.1
* Managing disconnection database exception/reconnection


### 2.0.0 BETA 50 (16/10/2017)
* Adding secure random functions into database wrapper
* public/private/secret keys can now be primary keys


### 2.0.0 BETA 49 (13/10/2017)
* Update Utils to 3.2.4


### 2.0.0 BETA 44 (06/10/2017)
* Update Utils to 3.1.1
* Correcting a bug with key pair size limit


### 2.0.0 BETA 35 (04/10/2017)
* Update Utils to 3.0.0


### 2.0.0 BETA 34 (08/09/2017)
* Adding lock HSQLDB file possibility


### 2.0.0 BETA 33 (08/09/2017)
* Correcting a problem with internal tables creation


### 2.0.0 BETA 32 (07/09/2017)
* Correcting a problem of loop into Symbol.getFieldAccessor(Table<T>, Object)


### 2.0.0 BETA 30 (07/09/2017)
* Changing table lock policy


### 2.0.0 BETA 28 (05/09/2017)
* Changing transaction policy
* Changing Calendar serialization method
* Changing table locking method
* Updating Utils to 2.16.0
* Optimizing memory use during big data synchronisations between peers
* Adding UUID field management
* Possibility of use of NULL parameters into SQL Where commands
* Now, JDBC drivers must be explicitly specified into dependencies of projects using OOD
* Correcting rule instance locking problem


### 2.0.0 BETA 22 (29/08/2017)
* Possibility to insert null values for parameters
* Changing locker


### 2.0.0 BETA 21 (29/08/2017)
* Correcting DateFieldAccessor when using null values


### 2.0.0 BETA 20 (21/08/2017)
* Updating Utils to 2.15.1
* Minimal corrections
* Corrections into the documentation


### 2.0.0 BETA 19 (15/08/2017)
* Optimizing database loading


### 2.0.0 BETA 18 (15/08/2017)
* Updating OOD to 2.15.0


### 2.0.0 BETA 17 (13/08/2017)
* Updating OOD to 2.14.0


### 2.0.0 BETA 16 (10/08/2017)
* Updating Utils to 2.12.0


### 2.0.0 BETA 15 (09/08/2017)
* Making HSQLDB not supporting 'LongVarBinary' type.


### 2.0.0 BETA 14 (05/08/2017)
* Little byte tab primary keys are know possible for DerbyDB.
* All decentralized database synchronization tests are OK with DerbyDB.


### 2.0.0 BETA 11 (05/08/2017)
* Convert project to gradle project.


### 2.0.0 BETA 8 (27/07/2017)
* All decentralized database synchronization tests are OK with HSQLDB (but not with Derby DB).


### 2.0.0 BETA 7 (05/07/2017)
* Tests synchro between two direct peers OK.
* Correcting a problem of transaction lock.


### 2.0.0 BETA 6 (02/07/2017)
* Adding not null possibility for each sub field.
* Begin debug decentralized database.
* Optimizing tables junction.


### 2.0.0 BETA 5 (23/06/2017)
* Adding not null possibility for each sub field.


### 2.0.0 BETA 4 (14/06/2017)
* Debugging SQL interpreter.
* Adding not null possibility for each sub field.


### 2.0.0 BETA 3 (01/06/2017)
* Adding database factory.
* Correcting a bug in database connections/disconnections.
* Updating Utils to 2.8.0.


### 2.0.0 BETA 2 (26/05/2017)
* Debugging transaction's use.
* Adding paginated queries.
* Adding ordered queries.
* Adding records numbers queries.
* Correcting a bug with multi fields match queries.


### 2.0.0 BETA 1 (24/05/2017)
* Adding database synchronisation possibility between different peers (unstable).
* Cleaning DatabaseWrapper functions.
* Correcting a lock problem into DatabaseWrapper.runSynchronizedTransaction(SynchronizedTransaction) function.
* Optimizing Decentralized IDs.
* Adding SQL where treatments (unstable).
* Add indexation field possibility (see annotation Field).
* Updating to Utils 2.7.1.
* Downgrading to Derby 10.11.1.1 (JDK 7 Compatible).
* Adding DatabaseConfiguration class.
* Adding DatabaseLifeCycles class.
* Changing the database loading policy : enabling transfer data from old database to new database.
* Minimizing code duplication of tests.
* Adding remove database possibility.
* Use of savepoint.


### 1.9.7 STABLE (07/03/2017)
* Updating Utils to 2.5.0.


### 1.9.6 STABLE (04/03/2017)
* Updating Utils to 2.4.0.


### 1.9.5 STABLE (07/02/2017)
* Updating Utils to 2.3.0.


### 1.9.4 STABLE (05/01/2017)
* Updating Utils to 2.2.0.


### 1.9.3 STABLE (31/12/2016)
* Updating Utils to 2.1.0.


### 1.9.2 STABLE (26/12/2016)
* Correcting a problem of data integrity check.
* Updating Derby DB to 10.13.1.1.
* Solving a problem of multithreading execution into windows (SQLNonTransientConnectionException).


### 1.9.1 STABLE (24/12/2016)
* Updating Utils to version 2.0.1.


### 1.9.0 STABLE (19/12/2016)
* Updating Utils to version 2.0.0.


### 1.8.1 STABLE (06/12/2016)
* Updating Utils to version 1.9.0.


### 1.8.0 STABLE (30/11/2016)
* Using ACID transactions instead of semaphores.
* Adding HSQLDBConcurrencyControl class.
* Adding memory refresh interval (see LoadToMemory annotation).
* Adding the possibility to set composed fields by adding @Field to a class.


### 1.7.1 STABLE (13/10/2016)
* Updating Utils to 1.8.0.


### 1.7.0 STABLE (19/09/2016)
* Correcting bug into FieldAccessor (null pointer exception).
* Adding enum support.
* Adding Decentralized ID support.


### 1.6.4 STABLE (29/08/2016)
* Updating Utils to 1.7.2.


### 1.6.3 STABLE (23/08/2016)
* Updating Utils to 1.7.1.


### 1.6.2 STABLE (04/07/2016)
* Updating Utils to 1.7.
* Updating to Common-Net 3.5.
* Updating to HSQLDB 3.3.4.


### 1.6.1 STABLE (10/06/2016)
* Correction a bug into the constructor of ByteTabConvertibleFieldAccessor.
* Adding version tests.
* Changing license to CECILL-C.


### 1.6.0 STABLE (11/03/2016)
* Updating with Utils 1.6.
* Adding database nativeBackup tools.
* renaming alterRecord functions to updateRecord.
* Adding functions Table.addRecord(record), Table.updateRecord(record).


### 1.5.2 STABLE (04/03/2016)
* Updating with Utils 1.5
* Adding encryption keys encoding/decoding.
* Correcting bugs with ByteTabConvertibleFieldAccessor class.


### 1.5.1 STABLE (01/03/2016)
* Updating with Utils 1.4.
* Adding AllTestsNG.xml file.


### 1.5.0 STABLE (15/02/2016)
* Adding iterator functionality in class Table.
* Adding ByteTabObjectConverter class.
* Adding DefaultByteTabObjectConverter class.
* Adding ByteTabConvertibleFieldAccessor class.
* Adding function addByteTabObjectConverter in DatabaseWrapper class.
* Adding possibility to use Object tabs as an alternative of use of maps when referring to fields.
* Optimizing use of SQL database.
* Linking with Utils 1.3.


### 1.4.1 STABLE (14/02/2016)
* Adding some close statements corrections.
* Adding some multi-thread optimisations.


### 1.4.0 STABLE (08/02/2016)
* One database is associated to one package. Now, its is possible to load several database/packages into the same file.
* OOD works now with HSQLDB or Apache Derby.


### 1.3.0 STABLE (05/02/2016)
* Adding dependency with Utils and updating OOD consequently.


### 1.2.0 STABLE (01/02/2016)
* Correcting some bugs into the documentation.
* Upgrading to HSQLDB 2.3.3 and Commons-Net 3.4.


### 1.1.0 STABLE (18/11/2013)
* Improving function Table.alterRecords(...) and class AlterRecordFilter (see documentation).


### 1.0.0 STABLE (24/04/2013)
* Releasing Oriented Object Database as a stable version.

