package io.github.janhicken.maven.wagon.gs;

import com.google.cloud.Tuple;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.wagon.InputData;
import org.apache.maven.wagon.OutputData;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.StreamWagon;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Maven wagon with Google Cloud Storage as backend.
 * <p>
 * Authentication is done using <a href="https://cloud.google.com/docs/authentication/production">Google Cloud application default credentials</a>.
 */
public class GSWagon extends StreamWagon {
    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    protected String prefix;

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
        final Blob blob = storage.get(getBucketName(), prefix + inputData.getResource().getName());
        if (blob == null) {
            throw new ResourceDoesNotExistException(String.format("File not found: gs://%s/%s",
                getBucketName(), prefix + inputData.getResource().getName()));
        }

        final InputStream inputStream = Channels.newInputStream(blob.reader());
        inputData.setInputStream(inputStream);
        inputData.getResource().setContentLength(blob.getSize());
        inputData.getResource().setLastModified(blob.getUpdateTime());
    }

    @Override
    public boolean resourceExists(final String resourceName) {
        return storage.get(getBucketName(), prefix + resourceName) != null;
    }

    @Override
    public List<String> getFileList(final String destinationDirectory) throws ResourceDoesNotExistException {
        final int offset = destinationDirectory.isEmpty() ? 0 : 1;
        final Set<String> blobs = StreamSupport.stream(storage.list(getBucketName()).iterateAll().spliterator(), false)
                .limit(DEFAULT_BUFFER_SIZE)
                .map(Blob::getName)
                .filter(name -> name.startsWith(prefix))
                .map(name -> name.substring(prefix.length()))
                .filter(name -> name.startsWith(destinationDirectory))
                .collect(Collectors.toSet());

        final Set<String> files = blobs.stream()
                .map(name -> name.substring(destinationDirectory.length() + offset))
                .collect(Collectors.toSet());
        final Stream<String> directories = blobs.stream()
                .filter(name -> name.indexOf('/') > 0)
                .flatMap(name -> IntStream.range(0, name.length())
                        .mapToObj(i -> Tuple.of(name.charAt(i), i))
                        .filter(t -> t.x() == '/')
                        .map(t -> name.substring(0, t.y() + 1)));

        if (files.isEmpty())
            throw new ResourceDoesNotExistException(String.format("Directory %s does not exist", destinationDirectory));
        else
            return Stream.concat(files.stream(), directories).collect(Collectors.toList());
    }

    @Override
    public void fillOutputData(final OutputData outputData) {
        final Blob blob = storage.create(BlobInfo.newBuilder(getBucketName(),
                prefix + outputData.getResource().getName()).build());

        final OutputStream outputStream = Channels.newOutputStream(blob.writer());
        outputData.setOutputStream(outputStream);
    }

    @Override
    protected void openConnectionInternal() {
        prefix = getRepository().getBasedir().substring(1);
        if (!prefix.isEmpty() && !prefix.endsWith("/"))
            prefix += '/';
    }

    @Override
    public void closeConnection() {
    }
}
