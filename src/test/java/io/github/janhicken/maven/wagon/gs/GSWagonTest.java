package io.github.janhicken.maven.wagon.gs;

import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.testing.FakeStorageRpc2;
import java.time.Instant;
import org.apache.maven.wagon.StreamingWagonTestCase;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.resource.Resource;

public class GSWagonTest extends StreamingWagonTestCase {

  private static final Instant UPDATE_TIME = Instant.now();
  private static final StorageOptions LOCAL_STORAGE_OPTIONS =
      StorageOptions.newBuilder()
          .setProjectId("dummy-project-id-for-testing")
          .setServiceRpcFactory(options -> new FakeStorageRpc2(true, UPDATE_TIME))
          .build();

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
    GSWagon.options = LOCAL_STORAGE_OPTIONS;
  }

  @Override
  protected long getExpectedLastModifiedOnGet(
      final Repository repository, final Resource resource) {
    return UPDATE_TIME.toEpochMilli();
  }
}
