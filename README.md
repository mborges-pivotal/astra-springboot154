# astra-springboot154
How to use a new driver that support Astra (3.9.0+) than what Spring Data Cassandra brings in Spring Boot 1.5.4.

Although there is some code around the DataStax Model by Example for the Investment Portfolio sample, the intent is to simply help understand how to bring a newer DataStax driver that supports Astra and properly replace the Spring Data Cassandra Starter so we 
can configure the Astra connectivity with the secure connect bundle and token. We'll update with the link of the project that implements
the complete application for the Investment Portfolio data model. 

The assumption is that you have basic understanding of DataStax Astra and Spring Framework, including Spring Boot. We are using Maven but the changes would be equivalent for Gradle. 

## Application Changes

First we exclude the driver that is included with Spring Data Cassandra and bring a driver that support Astra

Look at the POM file where we overwrite the driver version shown below:
```

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-cassandra</artifactId>
			<exclusions>
				<exclusion>
					<artifactId>cassandra-driver-core</artifactId>
					<groupId>com.datastax.cassandra</groupId>
				</exclusion>
			</exclusions>
		</dependency>

		<!-- OSS CASSANDRA DRIVER -->
		<dependency>
			<groupId>com.datastax.cassandra</groupId>
			<artifactId>cassandra-driver-core</artifactId>
			<version>3.11.0</version>
		</dependency>
```        

Then we create our own spring configuration class for Cassandra to work with the new driver. We don't need to extend the helper class AbstractCassandraConfiguration. See [AstraConfig class](/src/main/java/com/datastax/da/astra/AstraConfig.java). 

We also created a @ConfigurationProperties to externalize the properties used by the AstraConfig class and help load the properties in the order required to create the spring beans. 

Everything else should behave the exact same way as before with the older driver. You don't need to change anything in your code. ***You just bring these 2 classes (AstraConfig and AstraProperties) to your project and off you go!*** 

