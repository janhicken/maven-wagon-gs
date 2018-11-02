package com.synack.maven.wagon.gs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.maven.wagon.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

/**
 * Created by jdavey on 7/20/17.
 *
 * @plexus.component role="org.apache.maven.wagon.Wagon" role-hint="gs" instantiation-strategy="per-lookup"
 */
public class GSWagon extends StreamWagon {
    private Storage storage;

    private String getBucketName() {
        return getRepository().getHost();
    }

    @Override
    public void fillInputData(final InputData inputData) throws TransferFailedException, ResourceDoesNotExistException {
        final Bucket bucket = storage.get(getBucketName());
        if (bucket == null) {
            throw new TransferFailedException(String.format("Cannot find bucket '%s'", getBucketName()));
        }

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
        final Bucket bucket = storage.get(getBucketName());

        if (bucket == null) {
            throw new TransferFailedException(String.format("Cannot find bucket '%s'", getBucketName()));
        } else {
            return bucket.get(resourceName) != null;
        }
    }

    /**
     * Upload a file to gs
     */
    @Override
    public void fillOutputData(final OutputData outputData) throws TransferFailedException {
        final Bucket bucket = storage.get(getBucketName());
        if (bucket == null) {
            throw new TransferFailedException(String.format("Cannot find bucket '%s'", getBucketName()));
        }

        final Blob blob = bucket.create(outputData.getResource().getName(), new byte[]{});

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
