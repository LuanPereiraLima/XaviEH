/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred;


public class FadvisedFileRegion extends org.jboss.netty.channel.DefaultFileRegion {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(org.apache.hadoop.mapred.FadvisedFileRegion.class);

    private final boolean manageOsCache;

    private final int readaheadLength;

    private final org.apache.hadoop.io.ReadaheadPool readaheadPool;

    private final java.io.FileDescriptor fd;

    private final java.lang.String identifier;

    private final long count;

    private final long position;

    private final int shuffleBufferSize;

    private final boolean shuffleTransferToAllowed;

    private final java.nio.channels.FileChannel fileChannel;

    private org.apache.hadoop.io.ReadaheadPool.ReadaheadRequest readaheadRequest;

    public FadvisedFileRegion(java.io.RandomAccessFile file, long position, long count, boolean manageOsCache, int readaheadLength, org.apache.hadoop.io.ReadaheadPool readaheadPool, java.lang.String identifier, int shuffleBufferSize, boolean shuffleTransferToAllowed) throws java.io.IOException {
        super(file.getChannel(), position, count);
        this.manageOsCache = manageOsCache;
        this.readaheadLength = readaheadLength;
        this.readaheadPool = readaheadPool;
        this.fd = file.getFD();
        this.identifier = identifier;
        this.fileChannel = file.getChannel();
        this.count = count;
        this.position = position;
        this.shuffleBufferSize = shuffleBufferSize;
        this.shuffleTransferToAllowed = shuffleTransferToAllowed;
    }

    @java.lang.Override
    public long transferTo(java.nio.channels.WritableByteChannel target, long position) throws java.io.IOException {
        if (((readaheadPool) != null) && ((readaheadLength) > 0)) {
            readaheadRequest = readaheadPool.readaheadStream(identifier, fd, ((getPosition()) + position), readaheadLength, ((getPosition()) + (getCount())), readaheadRequest);
        }
        if (this.shuffleTransferToAllowed) {
            return transferTo(target, position);
        } else {
            return customShuffleTransfer(target, position);
        }
    }

    /**
     * This method transfers data using local buffer. It transfers data from
     * a disk to a local buffer in memory, and then it transfers data from the
     * buffer to the target. This is used only if transferTo is disallowed in
     * the configuration file. super.TransferTo does not perform well on Windows
     * due to a small IO request generated. customShuffleTransfer can control
     * the size of the IO requests by changing the size of the intermediate
     * buffer.
     */
    @com.google.common.annotations.VisibleForTesting
    long customShuffleTransfer(java.nio.channels.WritableByteChannel target, long position) throws java.io.IOException {
        long actualCount = (this.count) - position;
        if ((actualCount < 0) || (position < 0)) {
            throw new java.lang.IllegalArgumentException((((("position out of range: " + position) + " (expected: 0 - ") + ((this.count) - 1)) + ')'));
        }
        if (actualCount == 0) {
            return 0L;
        }
        long trans = actualCount;
        int readSize;
        java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocate(java.lang.Math.min(this.shuffleBufferSize, (trans > (java.lang.Integer.MAX_VALUE) ? java.lang.Integer.MAX_VALUE : ((int) (trans)))));
        while ((trans > 0L) && ((readSize = fileChannel.read(byteBuffer, ((this.position) + position))) > 0)) {
            // adjust counters and buffer limit
            if (readSize < trans) {
                trans -= readSize;
                position += readSize;
                byteBuffer.flip();
            } else {
                // We can read more than we need if the actualCount is not multiple
                // of the byteBuffer size and file is big enough. In that case we cannot
                // use flip method but we need to set buffer limit manually to trans.
                byteBuffer.limit(((int) (trans)));
                byteBuffer.position(0);
                position += trans;
                trans = 0;
            }
            // write data to the target
            while (byteBuffer.hasRemaining()) {
                target.write(byteBuffer);
            } 
            byteBuffer.clear();
        } 
        return actualCount - trans;
    }

    @java.lang.Override
    public void releaseExternalResources() {
        if ((readaheadRequest) != null) {
            readaheadRequest.cancel();
        }
        releaseExternalResources();
    }

    /**
     * Call when the transfer completes successfully so we can advise the OS that
     * we don't need the region to be cached anymore.
     */
    public void transferSuccessful() {
        if ((manageOsCache) && ((getCount()) > 0)) {
            try {
                NativeIO.POSIX.getCacheManipulator().posixFadviseIfPossible(identifier, fd, getPosition(), getCount(), org.apache.hadoop.mapred.POSIX_FADV_DONTNEED);
            }
        }
    }
}

