package io.github.janhicken.maven.wagon.gs;

import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;
import com.google.cloud.storage.spi.v1.StorageRpc;
import java.util.Map;
import org.apache.maven.wagon.StreamingWagonTestCase;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.repository.Repository;
import org.apache.maven.wagon.resource.Resource;

public class GSWagonTest extends StreamingWagonTestCase {

  protected static final String BUCKET_NAME = "test-bucket";
  protected static final String PREFIX = "repo";

  final StorageOptions storageOptions = LocalStorageHelper.getOptions();
  final GSWagon wagon = new GSWagon(storageOptions);

  @Override
  protected String getTestRepositoryUrl() {
    return BlobId.of(BUCKET_NAME, PREFIX).toGsUtilUri();
  }

  @Override
  protected String getProtocol() {
    return "gs";
  }

  @Override
  protected long getExpectedLastModifiedOnGet(
      final Repository repository, final Resource resource) {
    final var storageObject = new StorageObject();
    storageObject.setBucket(repository.getHost());
    storageObject.setName(repository.getBasedir().substring(1) + '/' + resource.getName());

    final var rpc = (StorageRpc) storageOptions.getRpc();
    final var ret = rpc.get(storageObject, Map.of());
    if (ret == null) return 0;
    else return ret.getUpdated().getValue();
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

  public void testMimeType() throws Exception {
    final var rpc = (StorageRpc) storageOptions.getRpc();
    setupWagonTestingFixtures();
    setupRepositories();

    final var expectedMimeTypes =
        Map.of(
            "jar", "application/java-archive",
            "pom", "application/xml",
            "asc", "text/plain",
            "sha1", "text/plain");
    for (final var expectedMimeType : expectedMimeTypes.entrySet()) {
      final var resourceName = "mime_test." + expectedMimeType.getKey();
      putFile(resourceName, resourceName, "foo");

      final var storageObject = new StorageObject();
      storageObject.setBucket(BUCKET_NAME);
      storageObject.setName(PREFIX + '/' + resourceName);
      final var ret = rpc.get(storageObject, Map.of());
      assertEquals(
          "Unexpected content type for " + resourceName,
          expectedMimeType.getValue(),
          ret.getContentType());
    }
  }
}
