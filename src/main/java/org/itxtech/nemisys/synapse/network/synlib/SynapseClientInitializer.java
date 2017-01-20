/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.itxtech.nemisys.synapse.network.synlib;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.itxtech.nemisys.network.synlib.SessionManager;
import org.itxtech.nemisys.network.synlib.SynapsePacketDecoder;
import org.itxtech.nemisys.network.synlib.SynapsePacketEncoder;
import org.itxtech.nemisys.network.synlib.SynapseServerHandler;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SynapseClientInitializer extends ChannelInitializer<SocketChannel> {

    private SynapseClient synapseClient;

    public SynapseClientInitializer(SynapseClient synapseClient) {
        this.synapseClient = synapseClient;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new SynapsePacketDecoder());
        pipeline.addLast(new SynapsePacketEncoder());
        pipeline.addLast(new SynapseClientHandler(this.synapseClient));
    }
}
