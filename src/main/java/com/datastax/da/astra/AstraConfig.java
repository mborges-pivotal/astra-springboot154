package com.datastax.da.astra;

import java.io.File;

import com.datastax.driver.core.Cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Taking over the Cassandra Configuration from the Spring Boot Data Cassandra
 * started so we can use the latest driver with the properties to connect to
 * Astra
 * 
 * @see https://docs.spring.io/spring-data/cassandra/docs/1.5.4.RELEASE/reference/html/
 */
@Configuration
@EnableCassandraRepositories(basePackages = { "com.datastax.da.astra.repository" })
public class AstraConfig {

    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraConfig.class);

    /*
     * We externalize the configuration and manage the loading so they can be used
     * by the Spring Beans below
     */
    @Autowired
    private AstraProperties props;

    @Bean
    public Cluster cluster() {

        // Check the cloud zip file
        File cloudSecureConnectBundleFile = new File(props.getBundle());
        if (!cloudSecureConnectBundleFile.exists()) {
            throw new IllegalStateException("File '" + props.getBundle() + "' has not been found\n"
                    + "To run this sample you need to download the secure bundle file from ASTRA WebPage\n"
                    + "More info here:");
        }

        // Connect
        Cluster cluster = Cluster.builder().withCloudSecureConnectBundle(cloudSecureConnectBundleFile)
                .withCredentials(props.getUsername(), props.getPassword()).build();
        LOGGER.info("[OK] cluster object created");

        return cluster;
    }

    /*
     * Use the standard Cassandra driver API to create a
     * com.datastax.driver.core.Session instance.
     */
    @Bean
    public CassandraSessionFactoryBean session() {
        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(cluster());
        session.setKeyspaceName(props.getKeyspace());
        session.setConverter(converter());
        session.setSchemaAction(SchemaAction.NONE);
        LOGGER.info("[OK] session object created");
        return session;
    }

    @Bean
    public CassandraOperations cassandraTemplate() throws Exception {
        return new CassandraTemplate(session().getObject());
    }

    @Bean
    public CassandraMappingContext mappingContext() {
        BasicCassandraMappingContext mappingContext = new BasicCassandraMappingContext();
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster(), props.getKeyspace()));
        return mappingContext;
    }

    @Bean
    public CassandraConverter converter() {
        return new MappingCassandraConverter(mappingContext());
    }

}
