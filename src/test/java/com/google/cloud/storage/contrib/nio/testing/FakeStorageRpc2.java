package com.google.cloud.storage.contrib.nio.testing;

import com.google.api.client.util.DateTime;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.storage.StorageException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;

public class FakeStorageRpc2 extends FakeStorageRpc {

  private final DateTime updateTime;

  /**
   * @param throwIfOption if true, we throw when given any option
   * @param updateTime the update time to set for created blobs
   */
  public FakeStorageRpc2(final boolean throwIfOption, final Instant updateTime) {
    super(throwIfOption);
    this.updateTime = new DateTime(updateTime.toEpochMilli());
  }

  @Override
  public StorageObject create(
      final StorageObject object, final InputStream content, final Map<Option, ?> options)
      throws StorageException {
    final StorageObject storageObject = super.create(object, content, options);
    storageObject.setUpdated(updateTime);
    return storageObject;
  }

  @Override
  public StorageObject writeWithResponse(
      final String uploadId,
      final byte[] toWrite,
      final int toWriteOffset,
      final long destOffset,
      final int length,
      final boolean last) {
    final StorageObject storageObject =
        super.writeWithResponse(uploadId, toWrite, toWriteOffset, destOffset, length, last);
    storageObject.setUpdated(updateTime);
    return storageObject;
  }
}
