package com.distrimind.ood.database;

import com.distrimind.ood.database.exceptions.DatabaseException;
import com.distrimind.util.DecentralizedValue;
import com.distrimind.util.crypto.AbstractSecureRandom;
import com.distrimind.util.crypto.EncryptionProfileProvider;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since MaDKitLanEdition 3.0.0
 */
public class DatabaseConfigurationsBuilder {
	private final DatabaseConfigurations configurations;
	private final DatabaseWrapper wrapper;
	private final DatabaseLifeCycles lifeCycles;
	private final EncryptionProfileProvider encryptionProfileProvider;
	private final AbstractSecureRandom secureRandom;


	DatabaseConfigurationsBuilder(DatabaseConfigurations configurations,
								  DatabaseWrapper wrapper,
								  DatabaseLifeCycles lifeCycles,
								  EncryptionProfileProvider encryptionProfileProvider,
								  AbstractSecureRandom secureRandom) throws DatabaseException {
		if (configurations==null)
			throw new NullPointerException();
		if (wrapper==null)
			throw new NullPointerException();
		if (encryptionProfileProvider==null)
			throw new NullPointerException();
		if (secureRandom==null)
			throw new NullPointerException();

		this.configurations = configurations;
		this.wrapper = wrapper;
		this.lifeCycles = lifeCycles;
		this.encryptionProfileProvider=encryptionProfileProvider;
		this.secureRandom=secureRandom;
		boolean save=false;
		for (DatabaseConfiguration dc : configurations.getConfigurations())
		{
			for (DecentralizedValue dv : dc.getDistantPeersThatCanBeSynchronizedWithThisDatabase())
			{
				if (configurations.getDistantPeers() == null)
					configurations.distantPeers = new HashSet<>();

				if (configurations.distantPeers.add(dv))
					save=true;
			}
		}
		if (save && lifeCycles!=null)
		{
			lifeCycles.saveDatabaseConfigurations(configurations);
		}
		checkInitLocalPeer();
	}
	private static class Transaction {
		final ArrayList<ConfigurationQuery> queries =new ArrayList<>();
		private boolean updateConfigurationPersistence=false;
		void updateConfigurationPersistence() {
			updateConfigurationPersistence = true;
		}
	}
	private interface ConfigurationQuery
	{
		void execute(Transaction transaction) throws DatabaseException;
	}

	private Transaction currentTransaction=null;

	private void pushQuery(ConfigurationQuery query) {
		synchronized (this) {
			if (currentTransaction==null)
				currentTransaction=new Transaction();
			currentTransaction.queries.add(query);
		}
	}


	public void commit() throws DatabaseException {
		synchronized (this) {
			if (currentTransaction==null)
				throw new DatabaseException("No query was added ! Nothing to commit !");
			wrapper.lockWrite();
			try {
				for (ConfigurationQuery q : currentTransaction.queries)
					q.execute(currentTransaction);
				if (currentTransaction.updateConfigurationPersistence)
					lifeCycles.saveDatabaseConfigurations(configurations);
				currentTransaction = null;
			}
			finally {
				wrapper.unlockWrite();
			}
		}
	}


	public EncryptionProfileProvider getEncryptionProfileProvider() {
		return encryptionProfileProvider;
	}

	AbstractSecureRandom getSecureRandom() {
		return secureRandom;
	}

	DatabaseConfigurationsBuilder addConfiguration(DatabaseConfiguration configuration, boolean makeConfigurationLoadingPersistent )
	{
		pushQuery((t) -> {
			//TODO complete
		});
		return this;
	}

	private void checkInitLocalPeer() throws DatabaseException {

		if (configurations.getLocalPeer() != null) {
			if (!wrapper.getSynchronizer().isInitialized(configurations.getLocalPeer()))
				wrapper.getSynchronizer().initLocalHostID(configurations.getLocalPeer(), configurations.isPermitIndirectSynchronizationBetweenPeers());
		}
	}

	private void checkNewConnexions() throws DatabaseException {
		checkInitLocalPeer();
	}

	public void setLocalPeerIdentifier(DecentralizedValue localPeerId, boolean permitIndirectSynchronizationBetweenPeers, boolean replace) throws DatabaseException {
		if (localPeerId==null)
			throw new NullPointerException();
		pushQuery((t)-> {
			if (configurations.getLocalPeer() != null) {
				if (configurations.getLocalPeer().equals(localPeerId)) {
					if (configurations.isPermitIndirectSynchronizationBetweenPeers() != permitIndirectSynchronizationBetweenPeers) {
						if (replace) {
							wrapper.getSynchronizer().disconnectAll();
							configurations.setPermitIndirectSynchronizationBetweenPeers(permitIndirectSynchronizationBetweenPeers);
							t.updateConfigurationPersistence();
							checkNewConnexions();
						} else
							throw new DatabaseException("Local peer identifier is already configured !");
					}
				} else {
					if (replace) {
						if (wrapper.getSynchronizer().isInitialized(configurations.getLocalPeer())) {
							wrapper.getSynchronizer().disconnectAll();
							wrapper.getSynchronizer().removeHook(wrapper.getSynchronizer().getLocalHostID());
						}
					} else
						throw new DatabaseException("Local peer identifier is already configured !");
					configurations.setPermitIndirectSynchronizationBetweenPeers(permitIndirectSynchronizationBetweenPeers);
					configurations.setLocalPeer(localPeerId);
					t.updateConfigurationPersistence();
					checkNewConnexions();
				}

			}
		});

	}

	DatabaseConfigurationsBuilder addDistantPeer(DecentralizedValue distantPeer, Package ... packages)
	{
		String[] packagesString=new String[packages.length];
		int i=0;
		for (Package p : packages)
		{
			if (p==null)
				throw new NullPointerException();
			packagesString[i++]=p.getName();
		}
		return addDistantPeer(distantPeer, packagesString);
	}

	DatabaseConfigurationsBuilder addDistantPeer(DecentralizedValue distantPeer, String ... packagesString)
	{
		pushQuery((t) -> {
			//TODO complete
		});
		return this;
	}

}