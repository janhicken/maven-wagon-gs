package com.google.cloud.storage.contrib.nio.testing;

import com.google.api.client.util.DateTime;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.StorageException;

import java.io.InputStream;
import java.time.Instant;
import java.util.Map;

public class FakeStorageRpc2 extends FakeStorageRpc {

    private final Instant updateTime;

    /**
     * @param throwIfOption if true, we throw when given any option
     * @param updateTime    the update time to set for created blobs
     */
    public FakeStorageRpc2(final boolean throwIfOption, final Instant updateTime) {
        super(throwIfOption);
        this.updateTime = updateTime;
    }

    @Override
    public StorageObject create(final StorageObject object, final InputStream content, final Map<Option, ?> options)
            throws StorageException {
        final StorageObject storageObject = super.create(object, content, options);
        storageObject.setUpdated(new DateTime(updateTime.toEpochMilli()));
        return storageObject;
    }
}
