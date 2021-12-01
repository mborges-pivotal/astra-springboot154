# astra-springboot154
How to use a new driver that support Astra (3.9.0+) than what Spring Data Cassandra brings in Spring Boot 1.5.4 

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

Everything else should behave the exact same way as before with the older driver. 

