package uq.deco2800.singularity.server;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

/**
 * An extension of the {@link Configuration} class provided by Dropwizard. It
 * adds extra configuration options needed for the server.
 * 
 * @author dion-loetscher
 *
 */
public class ServerConfiguration extends Configuration {

	/**
	 * Used to store the configuration for the database and creating new
	 * datasources.
	 */
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	/**
	 * Stores the configuration for the messaging service
	 */
	@JsonProperty
	private RealTimeSessionConfiguration messagingConfiguration;
	
	@JsonProperty
	private boolean shouldStartLoggingService = true;

	/**
	 * Sets the data source factory which is used to store the database
	 * configuration
	 * 
	 * @param factory
	 *            the DataSourceFactory to be stored.
	 */
	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory factory) {
		this.database = factory;
	}

	/**
	 * Retrieves the currently stored data source factory.
	 * 
	 * @return The data source factory stored in the configuration. Should not
	 *         be null.
	 */
	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

	/**
	 * Retrieves the messaging configuration stored.
	 * 
	 * @return The messaging service's configuration stored in this
	 *         configuration. Should not be null.
	 */
	public RealTimeSessionConfiguration getMessagingConfiguration() {
		return messagingConfiguration;
	}
	
	public boolean loggingServiceShouldStart() {
		return shouldStartLoggingService;
	}

}
