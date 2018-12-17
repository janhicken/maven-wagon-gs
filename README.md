# Use Google Cloud Storage as a Maven Repo

This Maven [wagon](http://maven.apache.org/wagon/) extension allows you to resolve and deploy artifacts using Google Cloud Storage:

    gs://mybucket/com/mygroup/myartifact/0.1/...

## Adding to a project 

You need to do the following:

 - Add the `extension` tag to your pom if you want to use the plugin
 - Add a `repository` tag to your pom if you want to resolve dependencies
 - Add a `distributionManagement` tag if you want to deploy

Here is an example configuration of the above:

```xml
<build>
    <extensions>
        <extension>
            <groupId>io.github.janhicken</groupId>
            <artifactId>maven.wagon-gs</artifactId>
            <version>1.0</version>
        </extension>
    </extensions>
</build>

<distributionManagement>
    <repository>
        <id>synack-gs</id>
        <url>gs://my-mvn-repo</url>
    </repository>
</distributionManagement>

<repositories>
    <repository>
        <id>synack-gs</id>
        <url>gs://my-mvn-repo</url>
    </repository>
</repositories> 
```