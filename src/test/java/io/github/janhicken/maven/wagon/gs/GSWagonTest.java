package io.github.janhicken.maven.wagon.gs;

import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.testing.FakeStorageRpc2;
import java.time.Instant;
import org.apache.maven.wagon.StreamingWagonTestCase;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.resource.Resource;

public class GSWagonTest extends StreamingWagonTestCase {

  final Instant updateTime = Instant.now();
  final StorageOptions storageOptions =
      StorageOptions.newBuilder()
          .setProjectId("dummy-project-id-for-testing")
          .setServiceRpcFactory(options -> new FakeStorageRpc2(true, updateTime))
          .build();
  final GSWagon wagon = new GSWagon(storageOptions);

  @Override
  protected String getTestRepositoryUrl() {
    return "gs://test-bucket/repo";
  }

  @Override
  protected String getProtocol() {
    return "gs";
  }

  @Override
  protected long getExpectedLastModifiedOnGet(
      final Repository repository, final Resource resource) {
    return updateTime.toEpochMilli();
  }

  @Override
  protected Wagon getWagon() {
    return wagon;
  }

  public void testLookupByProtocol() throws Exception {
    final var result = lookup(Wagon.ROLE, getProtocol());
    assertTrue(
        String.format("Expected an instance of GSWagon, got: %s", result.getClass().getName()),
        result instanceof GSWagon);
  }
}
