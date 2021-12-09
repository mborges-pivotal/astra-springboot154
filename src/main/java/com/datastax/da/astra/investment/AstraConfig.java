package com.datastax.da.astra.investment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.datastax.driver.core.Cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.ResourceLoader;
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
// import org.springframework.util.ResourceUtils;

/**
 * Taking over the Cassandra Configuration from the Spring Boot Data Cassandra
 * starter so we can use a more recent latest driver with the properties to
 * connect to
 * Astra. The driver that comes with Spring Boot 1.5.4 (circa 2017) doesn't
 * support Astra.
 * 
 * This effective turns off the CassandraAutoConfiguration. The bean "Cluster"
 * allows us to take control over how the Cassandra driver is configure so we
 * can connect to Astra using the secure bundled and token credentials.
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

    // @Autowired
    // private ResourceLoader resourceLoader;

    @Bean
    public Cluster cluster() {

        // Check the cloud zip file

        File cloudSecureConnectBundleFile = null;
        Cluster cluster = null;

        if (props.getBundle() == null) {
            LOGGER.info("Loading bundle zip file from 'classpath:secure-connect-bundle.zip'");
            try {
                // cloudSecureConnectBundleFile =
                // ResourceUtils.getFile("classpath:secure-connect-bundle.zip");

                // Resource resource = resourceLoader.getResource("classpath:secure-connect-bundle.zip");
                // InputStream inputStream = resource.getInputStream();

                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("secure-connect-bundle.zip");
                if (stream == null) {
                    new Error("Bundle inputstream is null - Maybe it was not found in the 'resources' folder");
                }

                // Connect
                cluster = Cluster.builder().withCloudSecureConnectBundle(stream)
                        .withCredentials(props.getUsername(), props.getPassword()).build();

            } catch (Exception e) {
                throw new Error("Bundle not found in the 'resources' folder", e);
            }
        } else {
            LOGGER.info("Loading bundle zip file from {}", props.getBundle());
            cloudSecureConnectBundleFile = new File(props.getBundle());
            if (!cloudSecureConnectBundleFile.exists()) {
                throw new IllegalStateException("File '" + props.getBundle() + "' has not been found\n"
                        + "To run this sample you need to download the secure bundle file from ASTRA WebPage\n"
                        + "More info here:");
            }

            // Connect
            cluster = Cluster.builder().withCloudSecureConnectBundle(cloudSecureConnectBundleFile)
                    .withCredentials(props.getUsername(), props.getPassword()).build();

        }

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
