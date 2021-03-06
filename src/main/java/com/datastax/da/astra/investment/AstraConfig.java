package com.datastax.da.astra.investment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.datastax.driver.core.Cluster;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;

import com.google.common.io.ByteStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
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

    @Value("classpath:secure-connect-bundle.zip")
    Resource bundleFile;

    @Value("classpath:application.properties")
    Resource propertiesFile;

    @Bean
    public Cluster cluster() {

        // Check the cloud zip file

        Cluster cluster = null;

        if (props.getBundle() == null) {
            LOGGER.info("[2] Loading bundle zip file from 'classpath:secure-connect-bundle.zip'");
            try {

                InputStream testStream = propertiesFile.getInputStream();
                String result = new BufferedReader(new InputStreamReader(testStream)).lines().parallel()
                        .collect(Collectors.joining("\n"));
                LOGGER.info("application.properties {}", result);

                InputStream stream = bundleFile.getInputStream();
                createCloudConfig(stream);
                stream = bundleFile.getInputStream();

                // Connect
                cluster = Cluster.builder().withCloudSecureConnectBundle(stream)
                        .withCredentials(props.getUsername(), props.getPassword()).build();

            } catch (Exception e) {
                throw new Error("Problem connecting by loading Bundle from the 'resources' folder", e);
            }
        } else {
            LOGGER.info("Loading bundle zip file from {}", props.getBundle());
            File cloudSecureConnectBundleFile = new File(props.getBundle());
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


    ///////////////////
    
    // The purpose of is test the driver piece of code that is failing
    private void createCloudConfig(InputStream cloudConfig)
            throws IOException, GeneralSecurityException {
                
        JsonNode configJson = null;
        ByteArrayOutputStream keyStoreOutputStream = null;
        ByteArrayOutputStream trustStoreOutputStream = null;
        ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream = new ZipInputStream(cloudConfig);
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.equals("config.json")) {
                    configJson = mapper.readTree(zipInputStream);
                    LOGGER.info("found config.json");
                } else if (fileName.equals("identity.jks")) {
                    keyStoreOutputStream = new ByteArrayOutputStream();
                    ByteStreams.copy(zipInputStream, keyStoreOutputStream);
                    LOGGER.info("found identity.jks");
                } else if (fileName.equals("trustStore.jks")) {
                    trustStoreOutputStream = new ByteArrayOutputStream();
                    ByteStreams.copy(zipInputStream, trustStoreOutputStream);
                    LOGGER.info("found trustStore.jks");
                }
            }
        } finally {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        }
    }

}
