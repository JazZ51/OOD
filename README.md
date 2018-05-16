# OOD
Object Oriented Database Using HSQLDB

# Changes

[See historical of changes](./versions.md)

# How to use it ?
## With Gradle :

Adapt into your build.gradle file, the next code :

	...
	repositories {
		...
		maven {
	       		url "https://artifactory.distri-mind.fr/artifactory/gradle-release"
	   	} 
		...
	}
	...
	dependencies {
		...
		compile(group:'com.distrimind.ood', name: 'OOD', version: '2.0.0-Beta82')
		//choose one of these optional drivers
			//optional and under JDK8 or newer
			compile(group:'org.hsqldb', name: 'hsqldb', version: '2.4.0')
			//optional and under JDK7 or newer
			compile(group:'org.hsqldb', name: 'hsqldb', version: '2.3.4')
		//choose one of these optional drivers
			//optional and under JDK7 or newer
			compile(group:'org.apache.derby', name: 'derby', version: '10.11.1.1')
			//optional and under JDK8 or newer
			compile(group:'org.apache.derby', name: 'derby', version: '10.13.1.1')
		...
	}
	...

To know what last version has been uploaded, please refer to versions availables into [this repository](https://artifactory.distri-mind.fr/artifactory/DistriMind-Public/com/distrimind/ood/OOD/)
## With Maven :
Adapt into your pom.xml file, the next code :

	<project>
		...
		<dependencies>
			...
			<dependency>
				<groupId>com.distrimind.ood</groupId>
				<artifactId>OOD</artifactId>
				<version>2.0.0-Beta82</version>
			</dependency>
			<!-- choose one of these optional drivers -->
				<dependency>
					<groupId>org.hsqldb</groupId>
					<artifactId>hsqldb</artifactId>
	
					<!-- under JDK 8 or newer -->
					<version>2.4.0</version>

					<!-- under JDK 7 or newer -->
					<version>2.3.4</version>
				</dependency>
				<dependency>
					<groupId>org.apache.derby</groupId>
					<artifactId>derby</artifactId>
	
					<!-- under JDK 8 or newer -->
					<version>10.13.1.1</version>

					<!-- under JDK 7 or newer -->
					<version>10.11.1.1</version>
				</dependency>
			...
		</dependencies>
		...
		<repositories>
			...
			<repository>
				<id>DistriMind-Public</id>
				<url>https://artifactory.distri-mind.fr/artifactory/gradle-release</url>
			</repository>
			...
		</repositories>
	</project>

To know what last version has been uploaded, please refer to versions availables into [this repository](https://artifactory.distri-mind.fr/artifactory/DistriMind-Public/com/distrimind/ood/OOD/)
