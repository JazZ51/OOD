Object Oriented Database
========================
2.0.0 Beta 86 (Build: 313) (from 01/04/2013 to 01/08/2018)

# Creator(s):
Jason MAHDJOUB

# Developer(s):
Jason MAHDJOUB (Entred in the team at 01/04/2013)

# Modifications:


### 2.0.0 Beta 86 (01/08/2018)
* Updating utils to 3.19.0


### 2.0.0 Beta 85 (27/07/2018)
* Updating utils to 3.18.0


### 2.0.0 Beta 84 (17/07/2018)
* Updating utils to 3.17.0
* Do not loop infinitely if deconnection exception is generated infinitely


### 2.0.0 Beta 83 (12/07/2018)
* Updating utils to 3.16.1
* Clean code
* Add autodeconnection option with database wrappers


### 2.0.0 Beta 82 (16/05/2018)
* Updating utils to 3.15.0


### 2.0.0 Beta 71 (30/03/2018)
* Optimization of CachedInputStream
* Optimization of CachedOutoutStream
* Updating utils to 3.13.1


### 2.0.0 Beta 66 (10/02/2018)
* Updating utils to 3.10.5
* Correcting a bug with the backup of enum variables


### 2.0.0 Beta 59 (31/01/2018)
* Updating utils to 3.9.0


### 2.0.0 Beta 57 (13/12/2017)
* Updating utils to 3.7.1
* Managing deconnection database exception/reconnection


### 2.0.0 Beta 50 (16/10/2017)
* Adding secure random functions into database wrapper
* public/private/secret keys can now be primary keys


### 2.0.0 Beta 50 (16/10/2017)
* Manage File fields
* Add ExcludeFromDecentralization annotation !
* Updating Utils to 3.3.0


### 2.0.0 Beta 49 (13/10/2017)
* Update Utils to 3.2.4


### 2.0.0 Beta 44 (06/10/2017)
* Update Utils to 3.1.1
* Correcting a bug with key pair size limit


### 2.0.0 Beta 35 (04/10/2017)
* Update Utils to 3.0.0


### 2.0.0 Beta 34 (08/09/2017)
* Adding lock HSQLDB file possibility


### 2.0.0 Beta 33 (08/09/2017)
* Correcting a problem with internal tables creation


### 2.0.0 Beta 32 (07/09/2017)
* Correcting a problem of loop into Symbol.getFieldAccessor(Table<T>, Object)


### 2.0.0 Beta 30 (07/09/2017)
* Changing table lock policy


### 2.0.0 Beta 28 (05/09/2017)
* Changing transaction policy
* Changing Calendar serialization method
* Changing table locking method
* Updating Utils to 2.16.0
* Optimizing memory use during big data synchronisations between peers
* Adding UUID field management
* Possibility of use of NULL parameters into SQL Where commands
* Now, JDBC drivers must be explicitly specified into dependencies of projects using OOD
* Correcting rule instance locking problem


### 2.0.0 Beta 22 (29/08/2017)
* Possibility to insert null values for parameters
* Changing locker


### 2.0.0 Beta 21 (29/08/2017)
* Correcting DateFieldAccessor when using null values


### 2.0.0 Beta 20 (21/08/2017)
* Updating Utils to 2.15.1
* Minimal corrections
* Corrections into the documentation


### 2.0.0 Beta 19 (15/08/2017)
* Optimizing database loading


### 2.0.0 Beta 18 (15/08/2017)
* Updating OOD to 2.15.0


### 2.0.0 Beta 17 (13/08/2017)
* Updating OOD to 2.14.0


### 2.0.0 Beta 16 (10/08/2017)
* Updating Utils to 2.12.0


### 2.0.0 Beta 15 (09/08/2017)
* Making HSQLDB not supporting 'LongVarBinary' type.


### 2.0.0 Beta 14 (05/08/2017)
* Little byte tab primary keys are know possible for DerbyDB.
* All decentralized database synchronization tests are OK with DerbyDB.


### 2.0.0 Beta 11 (05/08/2017)
* Convert project to gradle project.


### 2.0.0 Beta 8 (27/07/2017)
* All decentralized database synchronization tests are OK with HSQLDB (but not with Derby DB).


### 2.0.0 Beta 7 (05/07/2017)
* Tests synchro between two direct peers OK.
* Correcting a problem of transaction lock.


### 2.0.0 Beta 6 (02/07/2017)
* Adding not null possibility for each sub field.
* Begin debug decentralized database.
* Optimizing tables junction.


### 2.0.0 Beta 5 (23/06/2017)
* Adding not null possibility for each sub field.


### 2.0.0 Beta 4 (14/06/2017)
* Debugging SQL interpreter.
* Adding not null possibility for each sub field.


### 2.0.0 Beta 3 (01/06/2017)
* Adding database factory.
* Correcting a bug in database connections/deconnections.
* Updating Utils to 2.8.0.


### 2.0.0 Beta 2 (26/05/2017)
* Debuging transaction's use.
* Adding pagined queries.
* Adding ordered queries.
* Adding records numbers queries.
* Correcting a bug with multi fields match querries.


### 2.0.0 Beta 1 (24/05/2017)
* Adding database synchronisation possibility between different peers (unstable).
* Cleaning DatabaseWrapper functions.
* Correcting a lock problem into DatabaseWrapper.runSynchronizedTransaction(SynchronizedTransaction) function.
* Optimizing Decentralized IDs.
* Adding SQL where treatments (unstable).
* Add indexation field possibility (see annotation Field).
* Updating to Utils 2.7.1.
* Downgrading to Derby 10.11.1.1 (JDK 7 Compatible).
* Adding DatabaseConfiguration class.
* Adding DatabaseCreationCallable class.
* Changing the database loading policy : enabling transfer data from old database to new database.
* Minimizing code duplication of tests.
* Adding remove database possibility.
* Use of savepoints.


### 1.9.7 Stable (07/03/2017)
* Updating Utils to 2.5.0.


### 1.9.6 Stable (04/03/2017)
* Updating Utils to 2.4.0.


### 1.9.5 Stable (07/02/2017)
* Updating Utils to 2.3.0.


### 1.9.4 Stable (05/01/2017)
* Updating Utils to 2.2.0.


### 1.9.3 Stable (31/12/2016)
* Updating Utils to 2.1.0.


### 1.9.2 Stable (26/12/2016)
* Correcting a problem of data integrity check.
* Updating Derby DB to 10.13.1.1.
* Solving a problem of multithread execution into windows (SQLNonTransientConnectionException).


### 1.9.1 Stable (24/12/2016)
* Updating Utils to version 2.0.1.


### 1.9.0 Stable (19/12/2016)
* Updating Utils to version 2.0.0.


### 1.8.1 Stable (06/12/2016)
* Updating Utils to version 1.9.0.


### 1.8.0 Stable (30/11/2016)
* Using ACID transactions instead of semaphores.
* Adding HSQLDBConcurrencyControl class.
* Adding memory refresh interval (see LoadToMemory annotation).
* Adding the possibility to set composed fields by adding @Field to a class.


### 1.7.1 Stable (13/10/2016)
* Updating Utils to 1.8.0.


### 1.7.0 Stable (19/09/2016)
* Correcting bug into FieldAccessor (null pointer exception).
* Adding enum support.
* Adding Decentralized ID support.


### 1.6.4 Stable (29/08/2016)
* Updating Utils to 1.7.2.


### 1.6.3 Stable (23/08/2016)
* Updating Utils to 1.7.1.


### 1.6.2 Stable (04/07/2016)
* Updating Utils to 1.7.
* Updating to Common-Net 3.5.
* Updating to HSDQLDB 3.3.4.


### 1.6.1 Stable (10/06/2016)
* Correction a bug into the constructor of ByteTabConvertibleFieldAccessor.
* Adding version tests.
* Changing license to CECILL-C.


### 1.6.0 Stable (11/03/2016)
* Updating with Utils 1.6.
* Adding database backup tools.
* renaming alterRecord functions to updateRecord.
* Adding functions Table.addRecord(record), Table.updateRecord(record).


### 1.5.2 Stable (04/03/2016)
* Updating with Utils 1.5
* Adding encryption keys encoding/decoding.
* Correcting bugs with ByteTabConvertibleFieldAccessor class.


### 1.5.1 Stable (01/03/2016)
* Updating with Utils 1.4.
* Adding AllTestsNG.xml file.


### 1.5.0 Stable (15/02/2016)
* Adding iterator functionality in class Table.
* Adding ByteTabObjectConverter class.
* Adding DefaultByteTabObjectConverter class.
* Adding ByteTabConvertibleFieldAccessor class.
* Adding function addByteTabObjectConverter in DatabaseWrapper class.
* Adding possibility to use Object tabs as an alternative of use of maps when reffering to fields.
* Optimizing use of SQL database.
* Linking with Utils 1.3.


### 1.4.1 Stable (14/02/2016)
* Adding some close statements corrections.
* Adding some multi-thread optimisations.


### 1.4.0 Stable (08/02/2016)
* One databse is associated to one package. Now, its is possible to load several database/packages into the same file.
* OOD works now with HSQLDB or Apache Derby.


### 1.3.0 Stable (05/02/2016)
* Adding dependency with Utils and updating OOD consequently.


### 1.2.0 Stable (01/02/2016)
* Correcting some bugs into the documentation.
* Upgrading to HSQLDB 2.3.3 and Commons-Net 3.4.


### 1.1.0 Stable (18/11/2013)
* Improving function Table.alterRecords(...) and class AlterRecordFilter (see documentation).


### 1.0.0 Stable (24/04/2013)
* Releasing Oriented Object Database as a stable version.
