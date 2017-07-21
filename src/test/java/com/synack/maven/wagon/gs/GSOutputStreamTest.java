package com.synack.maven.wagon.gs;

import com.google.cloud.storage.Bucket;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class GSOutputStreamTest extends EasyMockSupport {

    @Test
    public void writeEmptyFile() throws Exception {
        Bucket bucket = mock(Bucket.class);
        expect(bucket.create(eq("my-empty-file.txt"), aryEq(new byte[]{}))).andReturn(null);
        replayAll();

        GSOutputStream os = new GSOutputStream(bucket, "my-empty-file.txt");
        os.write(new byte[]{});
        os.close();
        verifyAll();
    }

    @Test
    public void writeNonEmptyFile() throws Exception {
        Bucket bucket = mock(Bucket.class);
        expect(bucket.create(eq("my-other-file.txt"), aryEq("abcdefg".getBytes()))).andReturn(null);
        replayAll();

        GSOutputStream os = new GSOutputStream(bucket, "my-other-file.txt");
        os.write("abcdefg".getBytes());
        os.close();
        verifyAll();
    }
}
