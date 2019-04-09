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


public class FadvisedChunkedFile extends org.jboss.netty.handler.stream.ChunkedFile {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(org.apache.hadoop.mapred.FadvisedChunkedFile.class);

    private final boolean manageOsCache;

    private final int readaheadLength;

    private final org.apache.hadoop.io.ReadaheadPool readaheadPool;

    private final java.io.FileDescriptor fd;

    private final java.lang.String identifier;

    private org.apache.hadoop.io.ReadaheadPool.ReadaheadRequest readaheadRequest;

    public FadvisedChunkedFile(java.io.RandomAccessFile file, long position, long count, int chunkSize, boolean manageOsCache, int readaheadLength, org.apache.hadoop.io.ReadaheadPool readaheadPool, java.lang.String identifier) throws java.io.IOException {
        super(file, position, count, chunkSize);
        this.manageOsCache = manageOsCache;
        this.readaheadLength = readaheadLength;
        this.readaheadPool = readaheadPool;
        this.fd = file.getFD();
        this.identifier = identifier;
    }

    @java.lang.Override
    public java.lang.Object nextChunk() throws java.lang.Exception {
        if ((manageOsCache) && ((readaheadPool) != null)) {
            readaheadRequest = readaheadPool.readaheadStream(identifier, fd, getCurrentOffset(), readaheadLength, getEndOffset(), readaheadRequest);
        }
        return nextChunk();
    }

    @java.lang.Override
    public void close() throws java.lang.Exception {
        if ((readaheadRequest) != null) {
            readaheadRequest.cancel();
        }
        if ((manageOsCache) && (((getEndOffset()) - (getStartOffset())) > 0)) {
            try {
                NativeIO.POSIX.getCacheManipulator().posixFadviseIfPossible(identifier, fd, getStartOffset(), ((getEndOffset()) - (getStartOffset())), org.apache.hadoop.mapred.POSIX_FADV_DONTNEED);
            }
        }
        close();
    }
}

