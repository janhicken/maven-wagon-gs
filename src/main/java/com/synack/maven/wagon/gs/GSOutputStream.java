package com.synack.maven.wagon.gs;

import com.google.cloud.storage.Bucket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Buffers output and writes it to GS on close
 *
 * Created by jdavey on 7/20/17.
 */
public class GSOutputStream extends OutputStream {

    private Bucket bucket;
    private String name;
    private ByteArrayOutputStream buffer;

    GSOutputStream(Bucket bucket, String name) {
        this.bucket = bucket;
        this.name = name;
        this.buffer = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
    }

    public void close() throws IOException {
        bucket.create(name, buffer.toByteArray());
        buffer.close();
        super.close();
    }

}
