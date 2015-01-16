cloudhub-maven-plugin
====================

Maven plugin for deploying Mule applications to CloudHub.


### Deploy an application at the end of the build lifecycle

```xml
<plugin>
    <groupId>com.mulesoft.muleesb</groupId>
    <artifactId>cloudhub-maven-plugin</artifactId>
    <version>1.0</version>
    <configuration>
        <muleVersion>3.6.0</muleVersion>
        <application>/path/to/app1.zip</application>
    </configuration>
    <executions>
        <execution>
            <id>deploy</id>
            <phase>deploy</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Deploying the project generated artifact

For this to work your project must generate a valid Mule application. You can do this using `mule-artifact-archiver as explained [here](https://github.com/mulesoft/mule-artifact-archiver/)

```xml
<plugin>
    <groupId>com.mulesoft.muleesb</groupId>
    <artifactId>cloudhub-maven-plugin</artifactId>
    <version>1.0</version>
    <configuration>
        <muleVersion>3.6.0</muleVersion>
    </configuration>
    <executions>
        <execution>
            <id>deploy</id>
            <phase>deploy</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Running integration tests in CloudHub

This configuration will deploy your application to CloudHub, run your integration tests and the undeploy the application.

```xml
<plugin>
	<groupId>@project.groupId@</groupId>
	<artifactId>@project.artifactId@</artifactId>
	<version>@project.version@</version>
    <configuration>
		<muleVersion>3.5.0</muleVersion>
    </configuration>
	<executions>
        <execution>
            <id>deploy</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
        </execution>
		<execution>
			<id>undeploy</id>
			<phase>post-integration-test</phase>
			<goals>
				<goal>undeploy</goal>
			</goals>
		</execution>
	</executions>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>2.18.1</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
In this case you will configure the plugin to deploy and to undeploy, and additionally configure `maven-failsafe-plugin` to run your integration tests (*ITCase.java).

### Timeouts

You can specify a timeout for the plugin using the `<timeout>` tag inside `<configuration> or using cloudhub.timeout system property.
The plugin waits for the desired timeout or the default one for the operation to complete (deploy or undeploy). If the operation doesn't complete within that time the plugin execution fails. There is an exception to this, when timeout is zero, the plugin triggers the operation and inmediately returns the the control. In this last scenario you don't get an error if the deployment fails.

### Without a project

You can use the plugin to deploy or undeploy from command line without needing a project.

Deploy:

```
$mvn cloudhub:deploy -Dcloudhub.username=username -Dcloudhub.password=password -Dcloudhub.domain=testdomain -Dapplication=/path/to/app.zip -Dcloudhub.muleVersion=3.6.0
```

Undeploy:

```
mvn cloudhub:undeploy -Dcloudhub.username=username -Dcloudhub.password=password -Dcloudhub.domain=testdomain
```

Note: pay attention to the domain name, CloudHub doesn't allow you to use an existing domain.