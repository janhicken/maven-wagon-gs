package io.github.janhicken.maven.wagon.gs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.maven.wagon.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Maven wagon with Google Cloud Storage as backend.
 *
 * Authentication is done using <a href="https://cloud.google.com/docs/authentication/production">Google Cloud application default credentials</a>.
 */
public class GSWagon extends StreamWagon {
    private Storage storage;

    /**
     * @return an exception saying that the specified bucket cannot be found
     */
    protected TransferFailedException createBucketNotFoundException() {
        return new TransferFailedException(String.format("Cannot find bucket '%s", getBucketName()));
    }

    /**
     * @return the bucket name for artifact storage, e. g. {@code "my-bucket"}
     */
    public String getBucketName() {
        return getRepository().getHost();
    }

    /**
     * @return the bucket for artifact storage, if it exists
     */
    public Optional<Bucket> getBucket() {
        return Optional.ofNullable(storage.get(getBucketName()));
    }

    @Override
    public void fillInputData(final InputData inputData) throws TransferFailedException, ResourceDoesNotExistException {
        final Bucket bucket = getBucket()
            .orElseThrow(this::createBucketNotFoundException);

        final Blob blob = bucket.get(inputData.getResource().getName());
        if (blob == null) {
            throw new ResourceDoesNotExistException(String.format("File not found: gs://%s/%s",
                getBucketName(), inputData.getResource().getName()));
        }

        final InputStream inputStream = Channels.newInputStream(blob.reader());
        inputData.setInputStream(inputStream);
        inputData.getResource().setContentLength(blob.getSize());
        inputData.getResource().setLastModified(blob.getUpdateTime());
    }

    @Override
    public boolean resourceExists(final String resourceName) throws TransferFailedException {
        final Bucket bucket = getBucket()
            .orElseThrow(this::createBucketNotFoundException);

        return bucket.get(resourceName) != null;
    }

    @Override
    public List<String> getFileList(final String destinationDirectory) throws TransferFailedException {
        final Bucket bucket = getBucket()
            .orElseThrow(this::createBucketNotFoundException);

        return StreamSupport.stream(bucket.list().iterateAll().spliterator(), false)
            .map(Blob::getName)
            .filter(name -> name.startsWith(destinationDirectory))
            .limit(DEFAULT_BUFFER_SIZE)
            .collect(Collectors.toList());
    }

    @Override
    public void fillOutputData(final OutputData outputData) throws TransferFailedException {
        final Bucket bucket = getBucket()
            .orElseThrow(this::createBucketNotFoundException);
        final Blob blob = bucket.create(outputData.getResource().getName(), new byte[0]);

        final OutputStream outputStream = Channels.newOutputStream(blob.writer());
        outputData.setOutputStream(outputStream);
    }

    @Override
    protected void openConnectionInternal() throws ConnectionException {
        if (!"/".equals(getRepository().getBasedir())) {
            throw new ConnectionException("Not supported: Url contains path");
        }
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    @Override
    public void closeConnection() {
    }
}
