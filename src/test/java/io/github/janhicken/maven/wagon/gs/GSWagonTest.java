package io.github.janhicken.maven.wagon.gs;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.testing.FakeStorageRpc2;
import org.apache.maven.wagon.StreamingWagonTestCase;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.resource.Resource;

import java.time.Instant;

public class GSWagonTest extends StreamingWagonTestCase {

    private static final Instant UPDATE_TIME = Instant.now();
    private static final Storage LOCAL_STORAGE = StorageOptions.newBuilder()
            .setProjectId("dummy-project-id-for-testing")
            .setServiceRpcFactory(options -> new FakeStorageRpc2(true, UPDATE_TIME))
            .build()
            .getService();

    @Override
    protected String getTestRepositoryUrl() {
        return "gs://test-bucket/repo";
    }

    @Override
    protected String getProtocol() {
        return "gs";
    }

    @Override
    protected void setupWagonTestingFixtures() {
        GSWagon.setStorage(LOCAL_STORAGE);
    }

    @Override
    protected long getExpectedLastModifiedOnGet(final Repository repository, final Resource resource) {
        return UPDATE_TIME.toEpochMilli();
    }
}