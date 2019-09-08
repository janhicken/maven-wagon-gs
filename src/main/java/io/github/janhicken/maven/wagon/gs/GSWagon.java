package io.github.janhicken.maven.wagon.gs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.apache.maven.wagon.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Maven wagon with Google Cloud Storage as backend.
 * <p>
 * Authentication is done using <a href="https://cloud.google.com/docs/authentication/production">Google Cloud application default credentials</a>.
 */
public class GSWagon extends StreamWagon {
    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    @VisibleForTesting
    static void setStorage(final Storage storage) {
        GSWagon.storage = storage;
    }

    /**
     * @return the bucket name for artifact storage, e. g. {@code "my-bucket"}
     */
    public String getBucketName() {
        return getRepository().getHost();
    }

    @Override
    public void fillInputData(final InputData inputData) throws ResourceDoesNotExistException {
        final Blob blob = storage.get(getBucketName(), inputData.getResource().getName());
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
    public boolean resourceExists(final String resourceName) {
        return storage.get(getBucketName(), resourceName) != null;
    }

    @Override
    public List<String> getFileList(final String destinationDirectory) throws ResourceDoesNotExistException {
        final int offset = destinationDirectory.isEmpty() ? 0 : 1;
        final Set<String> files = StreamSupport.stream(storage.list(getBucketName()).iterateAll().spliterator(), false)
                .limit(DEFAULT_BUFFER_SIZE)
                .map(Blob::getName)
                .filter(name -> name.startsWith(destinationDirectory))
                .map(name -> name.substring(destinationDirectory.length() + offset))
                .collect(Collectors.toSet());

        final Set<String> directories = StreamSupport.stream(storage.list(getBucketName()).iterateAll().spliterator(), false)
                .limit(DEFAULT_BUFFER_SIZE)
                .map(Blob::getName)
                .filter(name -> name.startsWith(destinationDirectory))
                .filter(name -> name.indexOf('/') > 0)
                .flatMap(name -> {
                    final Set<String> d = new HashSet<>();
                    int i = 0;
                    while ((i = name.indexOf('/', i + 1)) > 0) {
                        d.add(name.substring(0, i + 1));
                    }
                    return d.stream();
                })
                .collect(Collectors.toSet());

        if (files.isEmpty())
            throw new ResourceDoesNotExistException(String.format("Directory %s does not exist", destinationDirectory));
        else
            return ImmutableList.<String>builder()
                    .addAll(files)
                    .addAll(directories)
                    .build();
    }

    @Override
    public void fillOutputData(final OutputData outputData) {
        final Blob blob = storage.create(
                BlobInfo.newBuilder(getBucketName(), outputData.getResource().getName())
                        .build(),
                new byte[0]);

        final OutputStream outputStream = Channels.newOutputStream(blob.writer());
        outputData.setOutputStream(outputStream);
    }

    @Override
    protected void openConnectionInternal() throws ConnectionException {
        if (!"/".equals(getRepository().getBasedir())) {
            throw new ConnectionException("Not supported: Url contains path");
        }
    }

    @Override
    public void closeConnection() {
    }
}