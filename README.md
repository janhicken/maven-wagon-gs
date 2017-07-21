# Use Google Cloud Storage as a Maven Repo

This Maven [wagon](http://maven.apache.org/wagon/) extension allows you to resolve and deploy artifacts using Google Cloud Storage:

    gs://mybucket/com/synack/myartifact/0.1/...

This was inspired by the [Spring S3 Wagon](https://github.com/spring-projects/aws-maven)

## Adding to a project 

You need to do the following:

 - Add the `extension` tag to your pom if you want to use the plugin
 - Add a `repository` tag to your pom if you want to resolve dependencies
 - Add a `distributionManagement` tag if you want to deploy

You can use the following as a template:

    <build>
        <extensions>
            <extension>
                <groupId>com.synack</groupId>
                <artifactId>maven.wagon-gs</artifactId>
                <version>0.1-SNAPSHOT</version>
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

## Enhancements?

This was created just for Synack's use case. Here are some things it could do with a bit of effort:

 - Allow public repos
 - Allow paths, for example `gs://mybucket/release` and `gs://mybucket/snapshot`
 - Don't always use the default gcloud project
 - Load credentials/tokens from `~/.m2/settings.xml`

 Enjoy!