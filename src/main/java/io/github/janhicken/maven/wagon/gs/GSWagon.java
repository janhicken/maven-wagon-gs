package io.github.janhicken.maven.wagon.gs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageOptions;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.InputData;
import org.apache.maven.wagon.OutputData;
import org.apache.maven.wagon.PathUtils;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.StreamWagon;
import org.apache.maven.wagon.resource.Resource;

/**
 * Maven wagon with Google Cloud Storage as backend.
 *
 * <p>Authentication is done using <a
 * href="https://cloud.google.com/docs/authentication/production">Google Cloud application default
 * credentials</a>.
 */
public class GSWagon extends StreamWagon {

  final MimeMapper mimeMapper = new MimeMapper();
  final Storage storage;
  String prefix;

  public GSWagon() {
    this(StorageOptions.getDefaultInstance());
  }

  public GSWagon(final StorageOptions options) {
    this.storage = options.getService();
  }

  /**
   * Returns the bucket name used for artifact storage, e.g. {@code "my-bucket"}.
   *
   * @return the bucket name
   */
  public String getBucketName() {
    return getRepository().getHost();
  }

  /**
   * Resolves a blob against the configured bucket name and prefix.
   *
   * @param resourceName the resource name to resolve
   * @return the resource's corresponding blob
   */
  BlobId resolve(final String resourceName) {
    return BlobId.of(getBucketName(), prefix + resourceName);
  }

  @Override
  public void fillInputData(final InputData inputData) throws ResourceDoesNotExistException {
    final Resource resource = inputData.getResource();
    final BlobId blobId = resolve(resource.getName());
    final Blob blob = storage.get(blobId);
    if (blob == null) {
      throw new ResourceDoesNotExistException("File not found: " + blobId.toGsUtilUri());
    }

    final InputStream inputStream = Channels.newInputStream(blob.reader());
    inputData.setInputStream(inputStream);
    resource.setContentLength(blob.getSize());
    resource.setLastModified(blob.getUpdateTimeOffsetDateTime().toInstant().toEpochMilli());
  }

  @Override
  public boolean resourceExists(final String resourceName) {
    return storage.get(resolve(resourceName)) != null;
  }

  @Override
  public List<String> getFileList(final String destinationDirectory)
      throws ResourceDoesNotExistException {
    final var listPrefix = prefix + ensureTrailingSlash(destinationDirectory);
    final var list = storage.list(getBucketName(), BlobListOption.prefix(listPrefix));
    final Set<String> blobs =
        StreamSupport.stream(list.iterateAll().spliterator(), false)
            .map(Blob::getName)
            .map(s -> s.substring(listPrefix.length()))
            .collect(Collectors.toUnmodifiableSet());
    if (blobs.isEmpty())
      throw new ResourceDoesNotExistException(
          String.format("Directory %s does not exist", destinationDirectory));

    final Stream<String> directories =
        blobs.stream()
            .flatMap(pathStr -> Arrays.stream(PathUtils.dirnames(pathStr)))
            .map(p -> p + '/');
    return Stream.concat(blobs.stream(), directories).collect(Collectors.toUnmodifiableList());
  }

  @Override
  public void fillOutputData(final OutputData outputData) {
    final BlobId blobId = resolve(outputData.getResource().getName());
    final BlobInfo.Builder builder = BlobInfo.newBuilder(blobId);
    mimeMapper
        .getMimeTypeForFileName(outputData.getResource().getName())
        .ifPresent(builder::setContentType);
    final Blob blob = storage.create(builder.build());

    final OutputStream outputStream = Channels.newOutputStream(blob.writer());
    outputData.setOutputStream(outputStream);
  }

  static String ensureTrailingSlash(final String s) {
    if (!s.isEmpty() && s.charAt(s.length() - 1) != '/') {
      return s + '/';
    } else {
      return s;
    }
  }

  @Override
  protected void openConnectionInternal() {
    prefix = ensureTrailingSlash(getRepository().getBasedir().substring(1));
  }

  @Override
  public void closeConnection() throws ConnectionException {
    try {
      storage.close();
    } catch (final Exception e) {
      throw new ConnectionException("Error closing connection", e);
    }
  }
}
