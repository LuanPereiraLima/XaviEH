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


public class ShuffleHandler extends org.apache.hadoop.yarn.server.api.AuxiliaryService {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(org.apache.hadoop.mapred.ShuffleHandler.class);

    private static final org.slf4j.Logger AUDITLOG = org.slf4j.LoggerFactory.getLogger(((org.apache.hadoop.mapred.ShuffleHandler.class.getName()) + ".audit"));

    public static final java.lang.String SHUFFLE_MANAGE_OS_CACHE = "mapreduce.shuffle.manage.os.cache";

    public static final boolean DEFAULT_SHUFFLE_MANAGE_OS_CACHE = true;

    public static final java.lang.String SHUFFLE_READAHEAD_BYTES = "mapreduce.shuffle.readahead.bytes";

    public static final int DEFAULT_SHUFFLE_READAHEAD_BYTES = (4 * 1024) * 1024;

    // pattern to identify errors related to the client closing the socket early
    // idea borrowed from Netty SslHandler
    private static final java.util.regex.Pattern IGNORABLE_ERROR_MESSAGE = java.util.regex.Pattern.compile("^.*(?:connection.*reset|connection.*closed|broken.*pipe).*$", java.util.regex.Pattern.CASE_INSENSITIVE);

    private static final java.lang.String STATE_DB_NAME = "mapreduce_shuffle_state";

    private static final java.lang.String STATE_DB_SCHEMA_VERSION_KEY = "shuffle-schema-version";

    protected static final org.apache.hadoop.yarn.server.records.Version CURRENT_VERSION_INFO = org.apache.hadoop.yarn.server.records.Version.newInstance(1, 0);

    private static final java.lang.String DATA_FILE_NAME = "file.out";

    private static final java.lang.String INDEX_FILE_NAME = "file.out.index";

    public static final org.jboss.netty.handler.codec.http.HttpResponseStatus TOO_MANY_REQ_STATUS = new org.jboss.netty.handler.codec.http.HttpResponseStatus(429, "TOO MANY REQUESTS");

    // This should kept in sync with Fetcher.FETCH_RETRY_DELAY_DEFAULT
    public static final long FETCH_RETRY_DELAY = 1000L;

    public static final java.lang.String RETRY_AFTER_HEADER = "Retry-After";

    private int port;

    private org.jboss.netty.channel.ChannelFactory selector;

    private final org.jboss.netty.channel.group.ChannelGroup accepted = new org.jboss.netty.channel.group.DefaultChannelGroup();

    protected org.apache.hadoop.mapred.ShuffleHandler.HttpPipelineFactory pipelineFact;

    private int sslFileBufferSize;

    /**
     * Should the shuffle use posix_fadvise calls to manage the OS cache during
     * sendfile
     */
    private boolean manageOsCache;

    private int readaheadLength;

    private int maxShuffleConnections;

    private int shuffleBufferSize;

    private boolean shuffleTransferToAllowed;

    private int maxSessionOpenFiles;

    private org.apache.hadoop.io.ReadaheadPool readaheadPool = org.apache.hadoop.io.ReadaheadPool.getInstance();

    private java.util.Map<java.lang.String, java.lang.String> userRsrc;

    private org.apache.hadoop.mapreduce.security.token.JobTokenSecretManager secretManager;

    private org.iq80.leveldb.DB stateDb = null;

    public static final java.lang.String MAPREDUCE_SHUFFLE_SERVICEID = "mapreduce_shuffle";

    public static final java.lang.String SHUFFLE_PORT_CONFIG_KEY = "mapreduce.shuffle.port";

    public static final int DEFAULT_SHUFFLE_PORT = 13562;

    public static final java.lang.String SHUFFLE_LISTEN_QUEUE_SIZE = "mapreduce.shuffle.listen.queue.size";

    public static final int DEFAULT_SHUFFLE_LISTEN_QUEUE_SIZE = 128;

    public static final java.lang.String SHUFFLE_CONNECTION_KEEP_ALIVE_ENABLED = "mapreduce.shuffle.connection-keep-alive.enable";

    public static final boolean DEFAULT_SHUFFLE_CONNECTION_KEEP_ALIVE_ENABLED = false;

    public static final java.lang.String SHUFFLE_CONNECTION_KEEP_ALIVE_TIME_OUT = "mapreduce.shuffle.connection-keep-alive.timeout";

    public static final int DEFAULT_SHUFFLE_CONNECTION_KEEP_ALIVE_TIME_OUT = 5;// seconds


    public static final java.lang.String SHUFFLE_MAPOUTPUT_META_INFO_CACHE_SIZE = "mapreduce.shuffle.mapoutput-info.meta.cache.size";

    public static final int DEFAULT_SHUFFLE_MAPOUTPUT_META_INFO_CACHE_SIZE = 1000;

    public static final java.lang.String CONNECTION_CLOSE = "close";

    public static final java.lang.String SUFFLE_SSL_FILE_BUFFER_SIZE_KEY = "mapreduce.shuffle.ssl.file.buffer.size";

    public static final int DEFAULT_SUFFLE_SSL_FILE_BUFFER_SIZE = 60 * 1024;

    public static final java.lang.String MAX_SHUFFLE_CONNECTIONS = "mapreduce.shuffle.max.connections";

    public static final int DEFAULT_MAX_SHUFFLE_CONNECTIONS = 0;// 0 implies no limit


    public static final java.lang.String MAX_SHUFFLE_THREADS = "mapreduce.shuffle.max.threads";

    // 0 implies Netty default of 2 * number of available processors
    public static final int DEFAULT_MAX_SHUFFLE_THREADS = 0;

    public static final java.lang.String SHUFFLE_BUFFER_SIZE = "mapreduce.shuffle.transfer.buffer.size";

    public static final int DEFAULT_SHUFFLE_BUFFER_SIZE = 128 * 1024;

    public static final java.lang.String SHUFFLE_TRANSFERTO_ALLOWED = "mapreduce.shuffle.transferTo.allowed";

    public static final boolean DEFAULT_SHUFFLE_TRANSFERTO_ALLOWED = true;

    public static final boolean WINDOWS_DEFAULT_SHUFFLE_TRANSFERTO_ALLOWED = false;

    private static final java.lang.String TIMEOUT_HANDLER = "timeout";

    /* the maximum number of files a single GET request can
    open simultaneously during shuffle
     */
    public static final java.lang.String SHUFFLE_MAX_SESSION_OPEN_FILES = "mapreduce.shuffle.max.session-open-files";

    public static final int DEFAULT_SHUFFLE_MAX_SESSION_OPEN_FILES = 3;

    boolean connectionKeepAliveEnabled = false;

    private int connectionKeepAliveTimeOut;

    private int mapOutputMetaInfoCacheSize;

    private org.jboss.netty.util.Timer timer;

    @org.apache.hadoop.metrics2.annotation.Metrics(about = "Shuffle output metrics", context = "mapred")
    static class ShuffleMetrics implements org.jboss.netty.channel.ChannelFutureListener {
        @org.apache.hadoop.metrics2.annotation.Metric("Shuffle output in bytes")
        org.apache.hadoop.metrics2.lib.MutableCounterLong shuffleOutputBytes;

        @org.apache.hadoop.metrics2.annotation.Metric("# of failed shuffle outputs")
        org.apache.hadoop.metrics2.lib.MutableCounterInt shuffleOutputsFailed;

        @org.apache.hadoop.metrics2.annotation.Metric("# of succeeeded shuffle outputs")
        org.apache.hadoop.metrics2.lib.MutableCounterInt shuffleOutputsOK;

        @org.apache.hadoop.metrics2.annotation.Metric("# of current shuffle connections")
        org.apache.hadoop.metrics2.lib.MutableGaugeInt shuffleConnections;

        @java.lang.Override
        public void operationComplete(org.jboss.netty.channel.ChannelFuture future) throws java.lang.Exception {
            if (future.isSuccess()) {
                shuffleOutputsOK.incr();
            } else {
                shuffleOutputsFailed.incr();
            }
            shuffleConnections.decr();
        }
    }

    final org.apache.hadoop.mapred.ShuffleHandler.ShuffleMetrics metrics;

    class ReduceMapFileCount implements org.jboss.netty.channel.ChannelFutureListener {
        private org.apache.hadoop.mapred.ShuffleHandler.ReduceContext reduceContext;

        public ReduceMapFileCount(org.apache.hadoop.mapred.ShuffleHandler.ReduceContext rc) {
            this.reduceContext = rc;
        }

        @java.lang.Override
        public void operationComplete(org.jboss.netty.channel.ChannelFuture future) throws java.lang.Exception {
            if (!(future.isSuccess())) {
                future.getChannel().close();
                return;
            }
            int waitCount = this.reduceContext.getMapsToWait().decrementAndGet();
            if (waitCount == 0) {
                metrics.operationComplete(future);
                // Let the idle timer handler close keep-alive connections
                if (reduceContext.getKeepAlive()) {
                    org.jboss.netty.channel.ChannelPipeline pipeline = future.getChannel().getPipeline();
                    org.apache.hadoop.mapred.ShuffleHandler.TimeoutHandler timeoutHandler = ((org.apache.hadoop.mapred.ShuffleHandler.TimeoutHandler) (pipeline.get(org.apache.hadoop.mapred.ShuffleHandler.TIMEOUT_HANDLER)));
                    timeoutHandler.setEnabledTimeout(true);
                } else {
                    future.getChannel().close();
                }
            } else {
                pipelineFact.getSHUFFLE().sendMap(reduceContext);
            }
        }
    }

    /**
     * Maintain parameters per messageReceived() Netty context.
     * Allows sendMapOutput calls from operationComplete()
     */
    private static class ReduceContext {
        private java.util.List<java.lang.String> mapIds;

        private java.util.concurrent.atomic.AtomicInteger mapsToWait;

        private java.util.concurrent.atomic.AtomicInteger mapsToSend;

        private int reduceId;

        private org.jboss.netty.channel.ChannelHandlerContext ctx;

        private java.lang.String user;

        private java.util.Map<java.lang.String, org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo> infoMap;

        private java.lang.String jobId;

        private final boolean keepAlive;

        public ReduceContext(java.util.List<java.lang.String> mapIds, int rId, org.jboss.netty.channel.ChannelHandlerContext context, java.lang.String usr, java.util.Map<java.lang.String, org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo> mapOutputInfoMap, java.lang.String jobId, boolean keepAlive) {
            this.mapIds = mapIds;
            this.reduceId = rId;
            /**
             * Atomic count for tracking the no. of map outputs that are yet to
             * complete. Multiple futureListeners' operationComplete() can decrement
             * this value asynchronously. It is used to decide when the channel should
             * be closed.
             */
            this.mapsToWait = new java.util.concurrent.atomic.AtomicInteger(mapIds.size());
            /**
             * Atomic count for tracking the no. of map outputs that have been sent.
             * Multiple sendMap() calls can increment this value
             * asynchronously. Used to decide which mapId should be sent next.
             */
            this.mapsToSend = new java.util.concurrent.atomic.AtomicInteger(0);
            this.ctx = context;
            this.user = usr;
            this.infoMap = mapOutputInfoMap;
            this.jobId = jobId;
            this.keepAlive = keepAlive;
        }

        public int getReduceId() {
            return reduceId;
        }

        public org.jboss.netty.channel.ChannelHandlerContext getCtx() {
            return ctx;
        }

        public java.lang.String getUser() {
            return user;
        }

        public java.util.Map<java.lang.String, org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo> getInfoMap() {
            return infoMap;
        }

        public java.lang.String getJobId() {
            return jobId;
        }

        public java.util.List<java.lang.String> getMapIds() {
            return mapIds;
        }

        public java.util.concurrent.atomic.AtomicInteger getMapsToSend() {
            return mapsToSend;
        }

        public java.util.concurrent.atomic.AtomicInteger getMapsToWait() {
            return mapsToWait;
        }

        public boolean getKeepAlive() {
            return keepAlive;
        }
    }

    ShuffleHandler(org.apache.hadoop.metrics2.MetricsSystem ms) {
        super(org.apache.hadoop.mapred.ShuffleHandler.MAPREDUCE_SHUFFLE_SERVICEID);
        metrics = ms.register(new org.apache.hadoop.mapred.ShuffleHandler.ShuffleMetrics());
    }

    public ShuffleHandler() {
        this(org.apache.hadoop.metrics2.lib.DefaultMetricsSystem.instance());
    }

    /**
     * Serialize the shuffle port into a ByteBuffer for use later on.
     *
     * @param port
     * 		the port to be sent to the ApplciationMaster
     * @return the serialized form of the port.
     */
    public static java.nio.ByteBuffer serializeMetaData(int port) throws java.io.IOException {
        // TODO these bytes should be versioned
        org.apache.hadoop.io.DataOutputBuffer port_dob = new org.apache.hadoop.io.DataOutputBuffer();
        port_dob.writeInt(port);
        return java.nio.ByteBuffer.wrap(port_dob.getData(), 0, port_dob.getLength());
    }

    /**
     * A helper function to deserialize the metadata returned by ShuffleHandler.
     *
     * @param meta
     * 		the metadata returned by the ShuffleHandler
     * @return the port the Shuffle Handler is listening on to serve shuffle data.
     */
    public static int deserializeMetaData(java.nio.ByteBuffer meta) throws java.io.IOException {
        // TODO this should be returning a class not just an int
        org.apache.hadoop.io.DataInputByteBuffer in = new org.apache.hadoop.io.DataInputByteBuffer();
        in.reset(meta);
        int port = in.readInt();
        return port;
    }

    /**
     * A helper function to serialize the JobTokenIdentifier to be sent to the
     * ShuffleHandler as ServiceData.
     *
     * @param jobToken
     * 		the job token to be used for authentication of
     * 		shuffle data requests.
     * @return the serialized version of the jobToken.
     */
    public static java.nio.ByteBuffer serializeServiceData(org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier> jobToken) throws java.io.IOException {
        // TODO these bytes should be versioned
        org.apache.hadoop.io.DataOutputBuffer jobToken_dob = new org.apache.hadoop.io.DataOutputBuffer();
        jobToken.write(jobToken_dob);
        return java.nio.ByteBuffer.wrap(jobToken_dob.getData(), 0, jobToken_dob.getLength());
    }

    static org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier> deserializeServiceData(java.nio.ByteBuffer secret) throws java.io.IOException {
        org.apache.hadoop.io.DataInputByteBuffer in = new org.apache.hadoop.io.DataInputByteBuffer();
        in.reset(secret);
        org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier> jt = new org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier>();
        jt.readFields(in);
        return jt;
    }

    @java.lang.Override
    public void initializeApplication(org.apache.hadoop.yarn.server.api.ApplicationInitializationContext context) {
        java.lang.String user = context.getUser();
        org.apache.hadoop.yarn.api.records.ApplicationId appId = context.getApplicationId();
        java.nio.ByteBuffer secret = context.getApplicationDataForService();
        // TODO these bytes should be versioned
        try {
            org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier> jt = org.apache.hadoop.mapred.ShuffleHandler.deserializeServiceData(secret);
            // TODO: Once SHuffle is out of NM, this can use MR APIs
            org.apache.hadoop.mapred.JobID jobId = new org.apache.hadoop.mapred.JobID(java.lang.Long.toString(appId.getClusterTimestamp()), appId.getId());
            recordJobShuffleInfo(jobId, user, jt);
        }
    }

    @java.lang.Override
    public void stopApplication(org.apache.hadoop.yarn.server.api.ApplicationTerminationContext context) {
        org.apache.hadoop.yarn.api.records.ApplicationId appId = context.getApplicationId();
        org.apache.hadoop.mapred.JobID jobId = new org.apache.hadoop.mapred.JobID(java.lang.Long.toString(appId.getClusterTimestamp()), appId.getId());
        try {
            removeJobShuffleInfo(jobId);
        }
    }

    @java.lang.Override
    protected void serviceInit(org.apache.hadoop.conf.Configuration conf) throws java.lang.Exception {
        manageOsCache = conf.getBoolean(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_MANAGE_OS_CACHE, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_MANAGE_OS_CACHE);
        readaheadLength = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_READAHEAD_BYTES, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_READAHEAD_BYTES);
        maxShuffleConnections = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.MAX_SHUFFLE_CONNECTIONS, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_MAX_SHUFFLE_CONNECTIONS);
        int maxShuffleThreads = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.MAX_SHUFFLE_THREADS, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_MAX_SHUFFLE_THREADS);
        if (maxShuffleThreads == 0) {
            maxShuffleThreads = 2 * (java.lang.Runtime.getRuntime().availableProcessors());
        }
        shuffleBufferSize = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_BUFFER_SIZE, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_BUFFER_SIZE);
        shuffleTransferToAllowed = conf.getBoolean(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_TRANSFERTO_ALLOWED, (org.apache.hadoop.util.Shell.WINDOWS ? org.apache.hadoop.mapred.ShuffleHandler.WINDOWS_DEFAULT_SHUFFLE_TRANSFERTO_ALLOWED : org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_TRANSFERTO_ALLOWED));
        maxSessionOpenFiles = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_MAX_SESSION_OPEN_FILES, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_MAX_SESSION_OPEN_FILES);
        java.util.concurrent.ThreadFactory bossFactory = new com.google.common.util.concurrent.ThreadFactoryBuilder().setNameFormat("ShuffleHandler Netty Boss #%d").build();
        java.util.concurrent.ThreadFactory workerFactory = new com.google.common.util.concurrent.ThreadFactoryBuilder().setNameFormat("ShuffleHandler Netty Worker #%d").build();
        selector = new org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory(org.apache.hadoop.util.concurrent.HadoopExecutors.newCachedThreadPool(bossFactory), org.apache.hadoop.util.concurrent.HadoopExecutors.newCachedThreadPool(workerFactory), maxShuffleThreads);
        serviceInit(new org.apache.hadoop.conf.Configuration(conf));
    }

    // TODO change AbstractService to throw InterruptedException
    @java.lang.Override
    protected void serviceStart() throws java.lang.Exception {
        org.apache.hadoop.conf.Configuration conf = getConfig();
        userRsrc = new java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.String>();
        secretManager = new org.apache.hadoop.mapreduce.security.token.JobTokenSecretManager();
        recoverState(conf);
        org.jboss.netty.bootstrap.ServerBootstrap bootstrap = new org.jboss.netty.bootstrap.ServerBootstrap(selector);
        // Timer is shared across entire factory and must be released separately
        timer = new org.jboss.netty.util.HashedWheelTimer();
        try {
            pipelineFact = new org.apache.hadoop.mapred.ShuffleHandler.HttpPipelineFactory(conf, timer);
        }
        bootstrap.setOption("backlog", conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_LISTEN_QUEUE_SIZE, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_LISTEN_QUEUE_SIZE));
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.setPipelineFactory(pipelineFact);
        port = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_PORT_CONFIG_KEY, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_PORT);
        org.jboss.netty.channel.Channel ch = bootstrap.bind(new java.net.InetSocketAddress(port));
        accepted.add(ch);
        port = ((java.net.InetSocketAddress) (ch.getLocalAddress())).getPort();
        conf.set(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_PORT_CONFIG_KEY, java.lang.Integer.toString(port));
        pipelineFact.SHUFFLE.setPort(port);
        org.apache.hadoop.mapred.ShuffleHandler.LOG.info((((getName()) + " listening on port ") + (port)));
        serviceStart();
        sslFileBufferSize = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SUFFLE_SSL_FILE_BUFFER_SIZE_KEY, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SUFFLE_SSL_FILE_BUFFER_SIZE);
        connectionKeepAliveEnabled = conf.getBoolean(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_CONNECTION_KEEP_ALIVE_ENABLED, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_CONNECTION_KEEP_ALIVE_ENABLED);
        connectionKeepAliveTimeOut = java.lang.Math.max(1, conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_CONNECTION_KEEP_ALIVE_TIME_OUT, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_CONNECTION_KEEP_ALIVE_TIME_OUT));
        mapOutputMetaInfoCacheSize = java.lang.Math.max(1, conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_MAPOUTPUT_META_INFO_CACHE_SIZE, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_MAPOUTPUT_META_INFO_CACHE_SIZE));
    }

    @java.lang.Override
    protected void serviceStop() throws java.lang.Exception {
        accepted.close().awaitUninterruptibly(10, java.util.concurrent.TimeUnit.SECONDS);
        if ((selector) != null) {
            org.jboss.netty.bootstrap.ServerBootstrap bootstrap = new org.jboss.netty.bootstrap.ServerBootstrap(selector);
            bootstrap.releaseExternalResources();
        }
        if ((pipelineFact) != null) {
            pipelineFact.destroy();
        }
        if ((timer) != null) {
            // Release this shared timer resource
            timer.stop();
        }
        if ((stateDb) != null) {
            stateDb.close();
        }
        serviceStop();
    }

    @java.lang.Override
    public synchronized java.nio.ByteBuffer getMetaData() {
        try {
            return org.apache.hadoop.mapred.ShuffleHandler.serializeMetaData(port);
        }
    }

    protected org.apache.hadoop.mapred.ShuffleHandler.Shuffle getShuffle(org.apache.hadoop.conf.Configuration conf) {
        return new org.apache.hadoop.mapred.ShuffleHandler.Shuffle(conf);
    }

    private void recoverState(org.apache.hadoop.conf.Configuration conf) throws java.io.IOException {
        org.apache.hadoop.fs.Path recoveryRoot = getRecoveryPath();
        if (recoveryRoot != null) {
            startStore(recoveryRoot);
            java.util.regex.Pattern jobPattern = java.util.regex.Pattern.compile(JobID.JOBID_REGEX);
            org.apache.hadoop.yarn.server.utils.LeveldbIterator iter = null;
            try {
                iter = new org.apache.hadoop.yarn.server.utils.LeveldbIterator(stateDb);
                iter.seek(bytes(JobID.JOB));
                while (iter.hasNext()) {
                    java.util.Map.Entry<byte[], byte[]> entry = iter.next();
                    java.lang.String key = asString(entry.getKey());
                    if (!(jobPattern.matcher(key).matches())) {
                        break;
                    }
                    recoverJobShuffleInfo(key, entry.getValue());
                } 
            } finally {
                if (iter != null) {
                    iter.close();
                }
            }
        }
    }

    private void startStore(org.apache.hadoop.fs.Path recoveryRoot) throws java.io.IOException {
        org.iq80.leveldb.Options options = new org.iq80.leveldb.Options();
        options.createIfMissing(false);
        org.apache.hadoop.fs.Path dbPath = new org.apache.hadoop.fs.Path(recoveryRoot, org.apache.hadoop.mapred.ShuffleHandler.STATE_DB_NAME);
        org.apache.hadoop.mapred.ShuffleHandler.LOG.info((("Using state database at " + dbPath) + " for recovery"));
        java.io.File dbfile = new java.io.File(dbPath.toString());
        try {
            stateDb = JniDBFactory.factory.open(dbfile, options);
        }
        checkVersion();
    }

    @com.google.common.annotations.VisibleForTesting
    org.apache.hadoop.yarn.server.records.Version loadVersion() throws java.io.IOException {
        byte[] data = stateDb.get(bytes(org.apache.hadoop.mapred.ShuffleHandler.STATE_DB_SCHEMA_VERSION_KEY));
        // if version is not stored previously, treat it as CURRENT_VERSION_INFO.
        if ((data == null) || ((data.length) == 0)) {
            return getCurrentVersion();
        }
        org.apache.hadoop.yarn.server.records.Version version = new org.apache.hadoop.yarn.server.records.impl.pb.VersionPBImpl(org.apache.hadoop.yarn.proto.YarnServerCommonProtos.VersionProto.parseFrom(data));
        return version;
    }

    private void storeSchemaVersion(org.apache.hadoop.yarn.server.records.Version version) throws java.io.IOException {
        java.lang.String key = org.apache.hadoop.mapred.ShuffleHandler.STATE_DB_SCHEMA_VERSION_KEY;
        byte[] data = ((org.apache.hadoop.yarn.server.records.impl.pb.VersionPBImpl) (version)).getProto().toByteArray();
        try {
            stateDb.put(bytes(key), data);
        }
    }

    private void storeVersion() throws java.io.IOException {
        storeSchemaVersion(org.apache.hadoop.mapred.ShuffleHandler.CURRENT_VERSION_INFO);
    }

    // Only used for test
    @com.google.common.annotations.VisibleForTesting
    void storeVersion(org.apache.hadoop.yarn.server.records.Version version) throws java.io.IOException {
        storeSchemaVersion(version);
    }

    protected org.apache.hadoop.yarn.server.records.Version getCurrentVersion() {
        return org.apache.hadoop.mapred.ShuffleHandler.CURRENT_VERSION_INFO;
    }

    /**
     * 1) Versioning scheme: major.minor. For e.g. 1.0, 1.1, 1.2...1.25, 2.0 etc.
     * 2) Any incompatible change of DB schema is a major upgrade, and any
     *    compatible change of DB schema is a minor upgrade.
     * 3) Within a minor upgrade, say 1.1 to 1.2:
     *    overwrite the version info and proceed as normal.
     * 4) Within a major upgrade, say 1.2 to 2.0:
     *    throw exception and indicate user to use a separate upgrade tool to
     *    upgrade shuffle info or remove incompatible old state.
     */
    private void checkVersion() throws java.io.IOException {
        org.apache.hadoop.yarn.server.records.Version loadedVersion = loadVersion();
        org.apache.hadoop.mapred.ShuffleHandler.LOG.info(("Loaded state DB schema version info " + loadedVersion));
        if (loadedVersion.equals(getCurrentVersion())) {
            return;
        }
        if (loadedVersion.isCompatibleTo(getCurrentVersion())) {
            org.apache.hadoop.mapred.ShuffleHandler.LOG.info(("Storing state DB schema version info " + (getCurrentVersion())));
            storeVersion();
        } else {
            throw new java.io.IOException(((("Incompatible version for state DB schema: expecting DB schema version " + (getCurrentVersion())) + ", but loading version ") + loadedVersion));
        }
    }

    private void addJobToken(org.apache.hadoop.mapred.JobID jobId, java.lang.String user, org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier> jobToken) {
        userRsrc.put(jobId.toString(), user);
        secretManager.addTokenForJob(jobId.toString(), jobToken);
        org.apache.hadoop.mapred.ShuffleHandler.LOG.info(("Added token for " + (jobId.toString())));
    }

    private void recoverJobShuffleInfo(java.lang.String jobIdStr, byte[] data) throws java.io.IOException {
        org.apache.hadoop.mapred.JobID jobId;
        try {
            jobId = org.apache.hadoop.mapred.JobID.forName(jobIdStr);
        }
        org.apache.hadoop.mapred.proto.ShuffleHandlerRecoveryProtos.JobShuffleInfoProto proto = org.apache.hadoop.mapred.proto.ShuffleHandlerRecoveryProtos.JobShuffleInfoProto.parseFrom(data);
        java.lang.String user = proto.getUser();
        org.apache.hadoop.security.proto.SecurityProtos.TokenProto tokenProto = proto.getJobToken();
        org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier> jobToken = new org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier>(tokenProto.getIdentifier().toByteArray(), tokenProto.getPassword().toByteArray(), new org.apache.hadoop.io.Text(tokenProto.getKind()), new org.apache.hadoop.io.Text(tokenProto.getService()));
        addJobToken(jobId, user, jobToken);
    }

    private void recordJobShuffleInfo(org.apache.hadoop.mapred.JobID jobId, java.lang.String user, org.apache.hadoop.security.token.Token<org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier> jobToken) throws java.io.IOException {
        if ((stateDb) != null) {
            org.apache.hadoop.security.proto.SecurityProtos.TokenProto tokenProto = org.apache.hadoop.security.proto.SecurityProtos.TokenProto.newBuilder().setIdentifier(com.google.protobuf.ByteString.copyFrom(jobToken.getIdentifier())).setPassword(com.google.protobuf.ByteString.copyFrom(jobToken.getPassword())).setKind(jobToken.getKind().toString()).setService(jobToken.getService().toString()).build();
            org.apache.hadoop.mapred.proto.ShuffleHandlerRecoveryProtos.JobShuffleInfoProto proto = org.apache.hadoop.mapred.proto.ShuffleHandlerRecoveryProtos.JobShuffleInfoProto.newBuilder().setUser(user).setJobToken(tokenProto).build();
            try {
                stateDb.put(bytes(jobId.toString()), proto.toByteArray());
            }
        }
        addJobToken(jobId, user, jobToken);
    }

    private void removeJobShuffleInfo(org.apache.hadoop.mapred.JobID jobId) throws java.io.IOException {
        java.lang.String jobIdStr = jobId.toString();
        secretManager.removeTokenForJob(jobIdStr);
        userRsrc.remove(jobIdStr);
        if ((stateDb) != null) {
            try {
                stateDb.delete(bytes(jobIdStr));
            }
        }
    }

    static class TimeoutHandler extends org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler {
        private boolean enabledTimeout;

        void setEnabledTimeout(boolean enabledTimeout) {
            this.enabledTimeout = enabledTimeout;
        }

        @java.lang.Override
        public void channelIdle(org.jboss.netty.channel.ChannelHandlerContext ctx, org.jboss.netty.handler.timeout.IdleStateEvent e) {
            if (((e.getState()) == (org.jboss.netty.handler.timeout.IdleState.WRITER_IDLE)) && (enabledTimeout)) {
                e.getChannel().close();
            }
        }
    }

    class HttpPipelineFactory implements org.jboss.netty.channel.ChannelPipelineFactory {
        final org.apache.hadoop.mapred.ShuffleHandler.Shuffle SHUFFLE;

        private org.apache.hadoop.security.ssl.SSLFactory sslFactory;

        private final org.jboss.netty.channel.ChannelHandler idleStateHandler;

        public HttpPipelineFactory(org.apache.hadoop.conf.Configuration conf, org.jboss.netty.util.Timer timer) throws java.lang.Exception {
            SHUFFLE = getShuffle(conf);
            if (conf.getBoolean(MRConfig.SHUFFLE_SSL_ENABLED_KEY, MRConfig.SHUFFLE_SSL_ENABLED_DEFAULT)) {
                org.apache.hadoop.mapred.ShuffleHandler.LOG.info("Encrypted shuffle is enabled.");
                sslFactory = new org.apache.hadoop.security.ssl.SSLFactory(SSLFactory.Mode.SERVER, conf);
                sslFactory.init();
            }
            this.idleStateHandler = new org.jboss.netty.handler.timeout.IdleStateHandler(timer, 0, connectionKeepAliveTimeOut, 0);
        }

        public org.apache.hadoop.mapred.ShuffleHandler.Shuffle getSHUFFLE() {
            return SHUFFLE;
        }

        public void destroy() {
            if ((sslFactory) != null) {
                sslFactory.destroy();
            }
        }

        @java.lang.Override
        public org.jboss.netty.channel.ChannelPipeline getPipeline() throws java.lang.Exception {
            org.jboss.netty.channel.ChannelPipeline pipeline = org.jboss.netty.channel.Channels.pipeline();
            if ((sslFactory) != null) {
                pipeline.addLast("ssl", new org.jboss.netty.handler.ssl.SslHandler(sslFactory.createSSLEngine()));
            }
            pipeline.addLast("decoder", new org.jboss.netty.handler.codec.http.HttpRequestDecoder());
            pipeline.addLast("aggregator", new org.jboss.netty.handler.codec.http.HttpChunkAggregator((1 << 16)));
            pipeline.addLast("encoder", new org.jboss.netty.handler.codec.http.HttpResponseEncoder());
            pipeline.addLast("chunking", new org.jboss.netty.handler.stream.ChunkedWriteHandler());
            pipeline.addLast("shuffle", SHUFFLE);
            pipeline.addLast("idle", idleStateHandler);
            pipeline.addLast(org.apache.hadoop.mapred.ShuffleHandler.TIMEOUT_HANDLER, new org.apache.hadoop.mapred.ShuffleHandler.TimeoutHandler());
            return pipeline;
            // TODO factor security manager into pipeline
            // TODO factor out encode/decode to permit binary shuffle
            // TODO factor out decode of index to permit alt. models
        }
    }

    class Shuffle extends org.jboss.netty.channel.SimpleChannelUpstreamHandler {
        private static final int MAX_WEIGHT = (10 * 1024) * 1024;

        private static final int EXPIRE_AFTER_ACCESS_MINUTES = 5;

        private static final int ALLOWED_CONCURRENCY = 16;

        private final org.apache.hadoop.conf.Configuration conf;

        private final org.apache.hadoop.mapred.IndexCache indexCache;

        private int port;

        private final com.google.common.cache.LoadingCache<org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier, org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo> pathCache = com.google.common.cache.CacheBuilder.newBuilder().expireAfterAccess(org.apache.hadoop.mapred.ShuffleHandler.Shuffle.EXPIRE_AFTER_ACCESS_MINUTES, java.util.concurrent.TimeUnit.MINUTES).softValues().concurrencyLevel(org.apache.hadoop.mapred.ShuffleHandler.Shuffle.ALLOWED_CONCURRENCY).removalListener(new com.google.common.cache.RemovalListener<org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier, org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo>() {
            @java.lang.Override
            public void onRemoval(com.google.common.cache.RemovalNotification<org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier, org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo> notification) {
                if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                    org.apache.hadoop.mapred.ShuffleHandler.LOG.debug(((("PathCache Eviction: " + (notification.getKey())) + ", Reason=") + (notification.getCause())));
                }
            }
        }).maximumWeight(org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MAX_WEIGHT).weigher(new com.google.common.cache.Weigher<org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier, org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo>() {
            @java.lang.Override
            public int weigh(org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier key, org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo value) {
                return ((((key.jobId.length()) + (key.user.length())) + (key.attemptId.length())) + (value.indexPath.toString().length())) + (value.dataPath.toString().length());
            }
        }).build(new com.google.common.cache.CacheLoader<org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier, org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo>() {
            @java.lang.Override
            public org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo load(org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier key) throws java.lang.Exception {
                java.lang.String base = getBaseLocation(key.jobId, key.user);
                java.lang.String attemptBase = base + (key.attemptId);
                org.apache.hadoop.fs.Path indexFileName = getAuxiliaryLocalPathHandler().getLocalPathForRead(((attemptBase + "/") + (org.apache.hadoop.mapred.ShuffleHandler.INDEX_FILE_NAME)));
                org.apache.hadoop.fs.Path mapOutputFileName = getAuxiliaryLocalPathHandler().getLocalPathForRead(((attemptBase + "/") + (org.apache.hadoop.mapred.ShuffleHandler.DATA_FILE_NAME)));
                if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                    org.apache.hadoop.mapred.ShuffleHandler.LOG.debug((("Loaded : " + key) + " via loader"));
                }
                return new org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo(indexFileName, mapOutputFileName);
            }
        });

        public Shuffle(org.apache.hadoop.conf.Configuration conf) {
            this.conf = conf;
            indexCache = new org.apache.hadoop.mapred.IndexCache(new org.apache.hadoop.mapred.JobConf(conf));
            this.port = conf.getInt(org.apache.hadoop.mapred.ShuffleHandler.SHUFFLE_PORT_CONFIG_KEY, org.apache.hadoop.mapred.ShuffleHandler.DEFAULT_SHUFFLE_PORT);
        }

        public void setPort(int port) {
            this.port = port;
        }

        private java.util.List<java.lang.String> splitMaps(java.util.List<java.lang.String> mapq) {
            if (null == mapq) {
                return null;
            }
            final java.util.List<java.lang.String> ret = new java.util.ArrayList<java.lang.String>();
            for (java.lang.String s : mapq) {
                java.util.Collections.addAll(ret, s.split(","));
            }
            return ret;
        }

        @java.lang.Override
        public void channelOpen(org.jboss.netty.channel.ChannelHandlerContext ctx, org.jboss.netty.channel.ChannelStateEvent evt) throws java.lang.Exception {
            super.channelOpen(ctx, evt);
            if (((maxShuffleConnections) > 0) && ((accepted.size()) >= (maxShuffleConnections))) {
                org.apache.hadoop.mapred.ShuffleHandler.LOG.info(java.lang.String.format(("Current number of shuffle connections (%d) is " + "greater than or equal to the max allowed shuffle connections (%d)"), accepted.size(), maxShuffleConnections));
                java.util.Map<java.lang.String, java.lang.String> headers = new java.util.HashMap<java.lang.String, java.lang.String>(1);
                // notify fetchers to backoff for a while before closing the connection
                // if the shuffle connection limit is hit. Fetchers are expected to
                // handle this notification gracefully, that is, not treating this as a
                // fetch failure.
                headers.put(org.apache.hadoop.mapred.ShuffleHandler.RETRY_AFTER_HEADER, java.lang.String.valueOf(org.apache.hadoop.mapred.ShuffleHandler.FETCH_RETRY_DELAY));
                sendError(ctx, "", org.apache.hadoop.mapred.ShuffleHandler.TOO_MANY_REQ_STATUS, headers);
                return;
            }
            accepted.add(evt.getChannel());
        }

        @java.lang.Override
        public void messageReceived(org.jboss.netty.channel.ChannelHandlerContext ctx, org.jboss.netty.channel.MessageEvent evt) throws java.lang.Exception {
            org.jboss.netty.handler.codec.http.HttpRequest request = ((org.jboss.netty.handler.codec.http.HttpRequest) (evt.getMessage()));
            if ((request.getMethod()) != (GET)) {
                sendError(ctx, org.apache.hadoop.mapred.METHOD_NOT_ALLOWED);
                return;
            }
            // Check whether the shuffle version is compatible
            if ((!(ShuffleHeader.DEFAULT_HTTP_HEADER_NAME.equals(((request.headers()) != null ? request.headers().get(ShuffleHeader.HTTP_HEADER_NAME) : null)))) || (!(ShuffleHeader.DEFAULT_HTTP_HEADER_VERSION.equals(((request.headers()) != null ? request.headers().get(ShuffleHeader.HTTP_HEADER_VERSION) : null))))) {
                sendError(ctx, "Incompatible shuffle request version", org.apache.hadoop.mapred.BAD_REQUEST);
            }
            final java.util.Map<java.lang.String, java.util.List<java.lang.String>> q = new org.jboss.netty.handler.codec.http.QueryStringDecoder(request.getUri()).getParameters();
            final java.util.List<java.lang.String> keepAliveList = q.get("keepAlive");
            boolean keepAliveParam = false;
            if ((keepAliveList != null) && ((keepAliveList.size()) == 1)) {
                keepAliveParam = java.lang.Boolean.valueOf(keepAliveList.get(0));
                if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                    org.apache.hadoop.mapred.ShuffleHandler.LOG.debug(((("KeepAliveParam : " + keepAliveList) + " : ") + keepAliveParam));
                }
            }
            final java.util.List<java.lang.String> mapIds = splitMaps(q.get("map"));
            final java.util.List<java.lang.String> reduceQ = q.get("reduce");
            final java.util.List<java.lang.String> jobQ = q.get("job");
            if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                org.apache.hadoop.mapred.ShuffleHandler.LOG.debug(((((((((("RECV: " + (request.getUri())) + "\n  mapId: ") + mapIds) + "\n  reduceId: ") + reduceQ) + "\n  jobId: ") + jobQ) + "\n  keepAlive: ") + keepAliveParam));
            }
            if (((mapIds == null) || (reduceQ == null)) || (jobQ == null)) {
                sendError(ctx, "Required param job, map and reduce", org.apache.hadoop.mapred.BAD_REQUEST);
                return;
            }
            if (((reduceQ.size()) != 1) || ((jobQ.size()) != 1)) {
                sendError(ctx, "Too many job/reduce parameters", org.apache.hadoop.mapred.BAD_REQUEST);
                return;
            }
            int reduceId;
            java.lang.String jobId;
            try {
                reduceId = java.lang.Integer.parseInt(reduceQ.get(0));
                jobId = jobQ.get(0);
            }
            final java.lang.String reqUri = request.getUri();
            if (null == reqUri) {
                // TODO? add upstream?
                sendError(ctx, org.apache.hadoop.mapred.FORBIDDEN);
                return;
            }
            org.jboss.netty.handler.codec.http.HttpResponse response = new org.jboss.netty.handler.codec.http.DefaultHttpResponse(HTTP_1_1, OK);
            try {
                verifyRequest(jobId, ctx, request, response, new java.net.URL("http", "", this.port, reqUri));
            }
            java.util.Map<java.lang.String, org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo> mapOutputInfoMap = new java.util.HashMap<java.lang.String, org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo>();
            org.jboss.netty.channel.Channel ch = evt.getChannel();
            org.jboss.netty.channel.ChannelPipeline pipeline = ch.getPipeline();
            org.apache.hadoop.mapred.ShuffleHandler.TimeoutHandler timeoutHandler = ((org.apache.hadoop.mapred.ShuffleHandler.TimeoutHandler) (pipeline.get(org.apache.hadoop.mapred.ShuffleHandler.TIMEOUT_HANDLER)));
            timeoutHandler.setEnabledTimeout(false);
            java.lang.String user = userRsrc.get(jobId);
            try {
                populateHeaders(mapIds, jobId, user, reduceId, request, response, keepAliveParam, mapOutputInfoMap);
            }
            ch.write(response);
            // Initialize one ReduceContext object per messageReceived call
            boolean keepAlive = keepAliveParam || (connectionKeepAliveEnabled);
            org.apache.hadoop.mapred.ShuffleHandler.ReduceContext reduceContext = new org.apache.hadoop.mapred.ShuffleHandler.ReduceContext(mapIds, reduceId, ctx, user, mapOutputInfoMap, jobId, keepAlive);
            for (int i = 0; i < (java.lang.Math.min(maxSessionOpenFiles, mapIds.size())); i++) {
                org.jboss.netty.channel.ChannelFuture nextMap = sendMap(reduceContext);
                if (nextMap == null) {
                    return;
                }
            }
        }

        /**
         * Calls sendMapOutput for the mapId pointed by ReduceContext.mapsToSend
         * and increments it. This method is first called by messageReceived()
         * maxSessionOpenFiles times and then on the completion of every
         * sendMapOutput operation. This limits the number of open files on a node,
         * which can get really large(exhausting file descriptors on the NM) if all
         * sendMapOutputs are called in one go, as was done previous to this change.
         *
         * @param reduceContext
         * 		used to call sendMapOutput with correct params.
         * @return the ChannelFuture of the sendMapOutput, can be null.
         */
        public org.jboss.netty.channel.ChannelFuture sendMap(org.apache.hadoop.mapred.ShuffleHandler.ReduceContext reduceContext) throws java.lang.Exception {
            org.jboss.netty.channel.ChannelFuture nextMap = null;
            if ((reduceContext.getMapsToSend().get()) < (reduceContext.getMapIds().size())) {
                int nextIndex = reduceContext.getMapsToSend().getAndIncrement();
                java.lang.String mapId = reduceContext.getMapIds().get(nextIndex);
                try {
                    org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo info = reduceContext.getInfoMap().get(mapId);
                    if (info == null) {
                        info = getMapOutputInfo(mapId, reduceContext.getReduceId(), reduceContext.getJobId(), reduceContext.getUser());
                    }
                    nextMap = sendMapOutput(reduceContext.getCtx(), reduceContext.getCtx().getChannel(), reduceContext.getUser(), mapId, reduceContext.getReduceId(), info);
                    if (null == nextMap) {
                        sendError(reduceContext.getCtx(), org.apache.hadoop.mapred.NOT_FOUND);
                        return null;
                    }
                    nextMap.addListener(new org.apache.hadoop.mapred.ShuffleHandler.ReduceMapFileCount(reduceContext));
                }
            }
            return nextMap;
        }

        private java.lang.String getErrorMessage(java.lang.Throwable t) {
            java.lang.StringBuffer sb = new java.lang.StringBuffer(t.getMessage());
            while ((t.getCause()) != null) {
                sb.append(t.getCause().getMessage());
                t = t.getCause();
            } 
            return sb.toString();
        }

        private java.lang.String getBaseLocation(java.lang.String jobId, java.lang.String user) {
            final org.apache.hadoop.mapred.JobID jobID = org.apache.hadoop.mapred.JobID.forName(jobId);
            final org.apache.hadoop.yarn.api.records.ApplicationId appID = org.apache.hadoop.yarn.api.records.ApplicationId.newInstance(java.lang.Long.parseLong(jobID.getJtIdentifier()), jobID.getId());
            final java.lang.String baseStr = ((((((((org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ContainerLocalizer.USERCACHE) + "/") + user) + "/") + (org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ContainerLocalizer.APPCACHE)) + "/") + (appID.toString())) + "/output") + "/";
            return baseStr;
        }

        protected org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo getMapOutputInfo(java.lang.String mapId, int reduce, java.lang.String jobId, java.lang.String user) throws java.io.IOException {
            org.apache.hadoop.mapred.ShuffleHandler.AttemptPathInfo pathInfo;
            try {
                org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier identifier = new org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier(jobId, user, mapId);
                pathInfo = pathCache.get(identifier);
                if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                    org.apache.hadoop.mapred.ShuffleHandler.LOG.debug(((("Retrieved pathInfo for " + identifier) + " check for corresponding loaded messages to determine whether") + " it was loaded or cached"));
                }
            }
            org.apache.hadoop.mapred.IndexRecord info = indexCache.getIndexInformation(mapId, reduce, pathInfo.indexPath, user);
            if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                org.apache.hadoop.mapred.ShuffleHandler.LOG.debug(((((((("getMapOutputInfo: jobId=" + jobId) + ", mapId=") + mapId) + ",dataFile=") + (pathInfo.dataPath)) + ", indexFile=") + (pathInfo.indexPath)));
            }
            org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo outputInfo = new org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo(pathInfo.dataPath, info);
            return outputInfo;
        }

        protected void populateHeaders(java.util.List<java.lang.String> mapIds, java.lang.String jobId, java.lang.String user, int reduce, org.jboss.netty.handler.codec.http.HttpRequest request, org.jboss.netty.handler.codec.http.HttpResponse response, boolean keepAliveParam, java.util.Map<java.lang.String, org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo> mapOutputInfoMap) throws java.io.IOException {
            long contentLength = 0;
            for (java.lang.String mapId : mapIds) {
                org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo outputInfo = getMapOutputInfo(mapId, reduce, jobId, user);
                if ((mapOutputInfoMap.size()) < (mapOutputMetaInfoCacheSize)) {
                    mapOutputInfoMap.put(mapId, outputInfo);
                }
                org.apache.hadoop.mapreduce.task.reduce.ShuffleHeader header = new org.apache.hadoop.mapreduce.task.reduce.ShuffleHeader(mapId, outputInfo.indexRecord.partLength, outputInfo.indexRecord.rawLength, reduce);
                org.apache.hadoop.io.DataOutputBuffer dob = new org.apache.hadoop.io.DataOutputBuffer();
                header.write(dob);
                contentLength += outputInfo.indexRecord.partLength;
                contentLength += dob.getLength();
            }
            // Now set the response headers.
            setResponseHeaders(response, keepAliveParam, contentLength);
            // this audit log is disabled by default,
            // to turn it on please enable this audit log
            // on log4j.properties by uncommenting the setting
            if (org.apache.hadoop.mapred.ShuffleHandler.AUDITLOG.isDebugEnabled()) {
                java.lang.StringBuilder sb = new java.lang.StringBuilder("shuffle for ");
                sb.append(jobId).append(" reducer ").append(reduce);
                sb.append(" length ").append(contentLength);
                sb.append(" mappers: ").append(mapIds);
                org.apache.hadoop.mapred.ShuffleHandler.AUDITLOG.debug(sb.toString());
            }
        }

        protected void setResponseHeaders(org.jboss.netty.handler.codec.http.HttpResponse response, boolean keepAliveParam, long contentLength) {
            if ((!(connectionKeepAliveEnabled)) && (!keepAliveParam)) {
                if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                    org.apache.hadoop.mapred.ShuffleHandler.LOG.debug("Setting connection close header...");
                }
                response.headers().set(HttpHeader.CONNECTION.asString(), org.apache.hadoop.mapred.ShuffleHandler.CONNECTION_CLOSE);
            } else {
                response.headers().set(HttpHeader.CONTENT_LENGTH.asString(), java.lang.String.valueOf(contentLength));
                response.headers().set(HttpHeader.CONNECTION.asString(), HttpHeader.KEEP_ALIVE.asString());
                response.headers().set(HttpHeader.KEEP_ALIVE.asString(), ("timeout=" + (connectionKeepAliveTimeOut)));
                org.apache.hadoop.mapred.ShuffleHandler.LOG.info(("Content Length in shuffle : " + contentLength));
            }
        }

        class MapOutputInfo {
            final org.apache.hadoop.fs.Path mapOutputFileName;

            final org.apache.hadoop.mapred.IndexRecord indexRecord;

            MapOutputInfo(org.apache.hadoop.fs.Path mapOutputFileName, org.apache.hadoop.mapred.IndexRecord indexRecord) {
                this.mapOutputFileName = mapOutputFileName;
                this.indexRecord = indexRecord;
            }
        }

        protected void verifyRequest(java.lang.String appid, org.jboss.netty.channel.ChannelHandlerContext ctx, org.jboss.netty.handler.codec.http.HttpRequest request, org.jboss.netty.handler.codec.http.HttpResponse response, java.net.URL requestUri) throws java.io.IOException {
            javax.crypto.SecretKey tokenSecret = secretManager.retrieveTokenSecret(appid);
            if (null == tokenSecret) {
                org.apache.hadoop.mapred.ShuffleHandler.LOG.info(("Request for unknown token " + appid));
                throw new java.io.IOException("could not find jobid");
            }
            // string to encrypt
            java.lang.String enc_str = org.apache.hadoop.mapreduce.security.SecureShuffleUtils.buildMsgFrom(requestUri);
            // hash from the fetcher
            java.lang.String urlHashStr = request.headers().get(SecureShuffleUtils.HTTP_HEADER_URL_HASH);
            if (urlHashStr == null) {
                org.apache.hadoop.mapred.ShuffleHandler.LOG.info(("Missing header hash for " + appid));
                throw new java.io.IOException("fetcher cannot be authenticated");
            }
            if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                int len = urlHashStr.length();
                org.apache.hadoop.mapred.ShuffleHandler.LOG.debug(((("verifying request. enc_str=" + enc_str) + "; hash=...") + (urlHashStr.substring((len - (len / 2)), (len - 1)))));
            }
            // verify - throws exception
            org.apache.hadoop.mapreduce.security.SecureShuffleUtils.verifyReply(urlHashStr, enc_str, tokenSecret);
            // verification passed - encode the reply
            java.lang.String reply = org.apache.hadoop.mapreduce.security.SecureShuffleUtils.generateHash(urlHashStr.getBytes(Charsets.UTF_8), tokenSecret);
            response.headers().set(SecureShuffleUtils.HTTP_HEADER_REPLY_URL_HASH, reply);
            // Put shuffle version into http header
            response.headers().set(ShuffleHeader.HTTP_HEADER_NAME, ShuffleHeader.DEFAULT_HTTP_HEADER_NAME);
            response.headers().set(ShuffleHeader.HTTP_HEADER_VERSION, ShuffleHeader.DEFAULT_HTTP_HEADER_VERSION);
            if (org.apache.hadoop.mapred.ShuffleHandler.LOG.isDebugEnabled()) {
                int len = reply.length();
                org.apache.hadoop.mapred.ShuffleHandler.LOG.debug(((("Fetcher request verfied. enc_str=" + enc_str) + ";reply=") + (reply.substring((len - (len / 2)), (len - 1)))));
            }
        }

        protected org.jboss.netty.channel.ChannelFuture sendMapOutput(org.jboss.netty.channel.ChannelHandlerContext ctx, org.jboss.netty.channel.Channel ch, java.lang.String user, java.lang.String mapId, int reduce, org.apache.hadoop.mapred.ShuffleHandler.Shuffle.MapOutputInfo mapOutputInfo) throws java.io.IOException {
            final org.apache.hadoop.mapred.IndexRecord info = mapOutputInfo.indexRecord;
            final org.apache.hadoop.mapreduce.task.reduce.ShuffleHeader header = new org.apache.hadoop.mapreduce.task.reduce.ShuffleHeader(mapId, info.partLength, info.rawLength, reduce);
            final org.apache.hadoop.io.DataOutputBuffer dob = new org.apache.hadoop.io.DataOutputBuffer();
            header.write(dob);
            ch.write(wrappedBuffer(dob.getData(), 0, dob.getLength()));
            final java.io.File spillfile = new java.io.File(mapOutputInfo.mapOutputFileName.toString());
            java.io.RandomAccessFile spill;
            try {
                spill = org.apache.hadoop.io.SecureIOUtils.openForRandomRead(spillfile, "r", user, null);
            }
            org.jboss.netty.channel.ChannelFuture writeFuture;
            if ((ch.getPipeline().get(org.jboss.netty.handler.ssl.SslHandler.class)) == null) {
                final org.apache.hadoop.mapred.FadvisedFileRegion partition = new org.apache.hadoop.mapred.FadvisedFileRegion(spill, info.startOffset, info.partLength, manageOsCache, readaheadLength, readaheadPool, spillfile.getAbsolutePath(), shuffleBufferSize, shuffleTransferToAllowed);
                writeFuture = ch.write(partition);
                writeFuture.addListener(new org.jboss.netty.channel.ChannelFutureListener() {
                    // TODO error handling; distinguish IO/connection failures,
                    // attribute to appropriate spill output
                    @java.lang.Override
                    public void operationComplete(org.jboss.netty.channel.ChannelFuture future) {
                        if (future.isSuccess()) {
                            partition.transferSuccessful();
                        }
                        partition.releaseExternalResources();
                    }
                });
            } else {
                // HTTPS cannot be done with zero copy.
                final org.apache.hadoop.mapred.FadvisedChunkedFile chunk = new org.apache.hadoop.mapred.FadvisedChunkedFile(spill, info.startOffset, info.partLength, sslFileBufferSize, manageOsCache, readaheadLength, readaheadPool, spillfile.getAbsolutePath());
                writeFuture = ch.write(chunk);
            }
            metrics.shuffleConnections.incr();
            metrics.shuffleOutputBytes.incr(info.partLength);// optimistic

            return writeFuture;
        }

        protected void sendError(org.jboss.netty.channel.ChannelHandlerContext ctx, org.jboss.netty.handler.codec.http.HttpResponseStatus status) {
            sendError(ctx, "", status);
        }

        protected void sendError(org.jboss.netty.channel.ChannelHandlerContext ctx, java.lang.String message, org.jboss.netty.handler.codec.http.HttpResponseStatus status) {
            sendError(ctx, message, status, java.util.Collections.<java.lang.String, java.lang.String>emptyMap());
        }

        protected void sendError(org.jboss.netty.channel.ChannelHandlerContext ctx, java.lang.String msg, org.jboss.netty.handler.codec.http.HttpResponseStatus status, java.util.Map<java.lang.String, java.lang.String> headers) {
            org.jboss.netty.handler.codec.http.HttpResponse response = new org.jboss.netty.handler.codec.http.DefaultHttpResponse(HTTP_1_1, status);
            response.headers().set(org.apache.hadoop.mapred.CONTENT_TYPE, "text/plain; charset=UTF-8");
            // Put shuffle version into http header
            response.headers().set(ShuffleHeader.HTTP_HEADER_NAME, ShuffleHeader.DEFAULT_HTTP_HEADER_NAME);
            response.headers().set(ShuffleHeader.HTTP_HEADER_VERSION, ShuffleHeader.DEFAULT_HTTP_HEADER_VERSION);
            for (java.util.Map.Entry<java.lang.String, java.lang.String> header : headers.entrySet()) {
                response.headers().set(header.getKey(), header.getValue());
            }
            response.setContent(org.jboss.netty.buffer.ChannelBuffers.copiedBuffer(msg, CharsetUtil.UTF_8));
            // Close the connection as soon as the error message is sent.
            ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
        }

        @java.lang.Override
        public void exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext ctx, org.jboss.netty.channel.ExceptionEvent e) throws java.lang.Exception {
            org.jboss.netty.channel.Channel ch = e.getChannel();
            java.lang.Throwable cause = e.getCause();
            if (cause instanceof org.jboss.netty.handler.codec.frame.TooLongFrameException) {
                sendError(ctx, org.apache.hadoop.mapred.BAD_REQUEST);
                return;
            } else
                if (cause instanceof java.io.IOException) {
                    if (cause instanceof java.nio.channels.ClosedChannelException) {
                        org.apache.hadoop.mapred.ShuffleHandler.LOG.debug("Ignoring closed channel error", cause);
                        return;
                    }
                    java.lang.String message = java.lang.String.valueOf(cause.getMessage());
                    if (org.apache.hadoop.mapred.ShuffleHandler.IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
                        org.apache.hadoop.mapred.ShuffleHandler.LOG.debug("Ignoring client socket close", cause);
                        return;
                    }
                }

            org.apache.hadoop.mapred.ShuffleHandler.LOG.error("Shuffle error: ", cause);
            if (ch.isConnected()) {
                org.apache.hadoop.mapred.ShuffleHandler.LOG.error(("Shuffle error " + e));
                sendError(ctx, org.apache.hadoop.mapred.INTERNAL_SERVER_ERROR);
            }
        }
    }

    static class AttemptPathInfo {
        // TODO Change this over to just store local dir indices, instead of the
        // entire path. Far more efficient.
        private final org.apache.hadoop.fs.Path indexPath;

        private final org.apache.hadoop.fs.Path dataPath;

        public AttemptPathInfo(org.apache.hadoop.fs.Path indexPath, org.apache.hadoop.fs.Path dataPath) {
            this.indexPath = indexPath;
            this.dataPath = dataPath;
        }
    }

    static class AttemptPathIdentifier {
        private final java.lang.String jobId;

        private final java.lang.String user;

        private final java.lang.String attemptId;

        public AttemptPathIdentifier(java.lang.String jobId, java.lang.String user, java.lang.String attemptId) {
            this.jobId = jobId;
            this.user = user;
            this.attemptId = attemptId;
        }

        @java.lang.Override
        public boolean equals(java.lang.Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier that = ((org.apache.hadoop.mapred.ShuffleHandler.AttemptPathIdentifier) (o));
            if (!(attemptId.equals(that.attemptId))) {
                return false;
            }
            if (!(jobId.equals(that.jobId))) {
                return false;
            }
            return true;
        }

        @java.lang.Override
        public int hashCode() {
            int result = jobId.hashCode();
            result = (31 * result) + (attemptId.hashCode());
            return result;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return (((((("AttemptPathIdentifier{" + "attemptId='") + (attemptId)) + '\'') + ", jobId='") + (jobId)) + '\'') + '}';
        }
    }
}

