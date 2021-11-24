# astra-springboot154
How to use a new driver that support Astra (3.9.0+) than what Spring Data Cassandra brings in Spring Boot 1.5.4 

Look at the POM file where we overwrite the driver version shown below:
```
		<!-- OSS CASSANDRA DRIVER -->
		<dependency>
			<groupId>com.datastax.cassandra</groupId>
			<artifactId>cassandra-driver-core</artifactId>
			<version>${cassandra.driver.oss.version}</version>
		</dependency>
```        

