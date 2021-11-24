package com.datastax.da.astra;

import java.io.File;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

@SpringBootApplication
// It was not taking the secure bundle contact points
@EnableAutoConfiguration(exclude = { CassandraDataAutoConfiguration.class })
public class AstraApplication implements CommandLineRunner {

	/** Logger for the class. */
	private static Logger LOGGER = LoggerFactory.getLogger(AstraApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AstraApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {

		// Those are mandatory to connect to ASTRA
		final String ASTRA_ZIP_FILE = "LOCATION_OF_SECURE_BUNDLE_ZIP";
		final String ASTRA_USERNAME = "TOKEN_CLIENT_ID";
		final String ASTRA_PASSWORD = "TOKEN_CLIENT_SECRET";
		final String ASTRA_KEYSPACE = "YOUR_KEYSPACE";

		// Check the cloud zip file
		File cloudSecureConnectBundleFile = new File(ASTRA_ZIP_FILE);
		if (!cloudSecureConnectBundleFile.exists()) {
			throw new IllegalStateException("File '" + ASTRA_ZIP_FILE + "' has not been found\n"
					+ "To run this sample you need to download the secure bundle file from ASTRA WebPage\n"
					+ "More info here:");
		}

		// Connect
		try (Cluster cluster = Cluster.builder().withCloudSecureConnectBundle(cloudSecureConnectBundleFile)
				.withCredentials(ASTRA_USERNAME, ASTRA_PASSWORD).build()) {
			Session session = cluster.connect(ASTRA_KEYSPACE);
			LOGGER.info("[OK] Welcome to ASTRA. Connected to Keyspace {}", session.getLoggedKeyspace());
		}
		LOGGER.info("[OK] Success");
	}

}
