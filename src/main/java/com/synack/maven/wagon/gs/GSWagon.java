package com.synack.maven.wagon.gs;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.maven.wagon.*;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * Created by jdavey on 7/20/17.
 *
 * @plexus.component role="org.apache.maven.wagon.Wagon" role-hint="gs" instantiation-strategy="per-lookup"
 */
public class GSWagon extends StreamWagon {

    private static Logger log = LoggerFactory.getLogger(GSWagon.class);
    private Storage storage;

    private String getBucketName() {
        return getRepository().getHost();
    }

    @Override
    public void fillInputData(InputData inputData) throws ResourceDoesNotExistException {
        String bucketName = getBucketName();
        String resourceName = inputData.getResource().getName();
        log.info("Downloading: gs://" + bucketName + "/" + resourceName);

        Bucket bucket = storage.get(bucketName);

        Blob blob = bucket.get(resourceName);

        if (blob == null) {
            throw new ResourceDoesNotExistException("File: gs://" + bucketName + "/" + resourceName + " does not exist");
        }
        byte[] content = blob.getContent();

        inputData.setInputStream(new ByteArrayInputStream(content));
        inputData.getResource().setContentLength(content.length);
        inputData.getResource().setLastModified(blob.getUpdateTime());
    }

    @Override
    public boolean resourceExists(String resourceName) {
        return storage.get(getBucketName()).get(resourceName) != null;
    }

    /**
     * Upload a file to gs
     */
    @Override
    public void fillOutputData(OutputData outputData) {
        String bucketName = getBucketName();
        String resourceName = outputData.getResource().getName();
        log.info("Uploading: gs://" + bucketName + "/" + resourceName);

        Storage storage = StorageOptions.getDefaultInstance().getService();
        Bucket bucket = storage.get(bucketName);
        outputData.setOutputStream(new GSOutputStream(bucket, resourceName));
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
