package hermes.bootstrap.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.owen.hermes.bootstrap.channel.HermesChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by owen_q on 2018. 6. 20..
 */
public class TestHermesChannelInboundHandler {
    private Logger logger = LoggerFactory.getLogger(TestHermesChannelInboundHandler.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HermesChannelInboundHandler hermesChannelInboundHandler = null;

    // Mock
    @Mock
    private HermesChannelInboundHandler mockHermesChannelInboundHandler;

    @Mock
    private ChannelHandlerContext mockChannelHandlerContext;

    @Mock
    private NioSocketChannel mockNioSocketChannel;

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);

        hermesChannelInboundHandler = new HermesChannelInboundHandler();
    }

    @Test
    public void addConnectionWhenFireChannelRegistered() throws Exception {
        // Given:
        InetSocketAddress givenInetSocketAddress = new InetSocketAddress("10.0.1.202", 12222);

        // When:
        Mockito.when(mockChannelHandlerContext.name()).thenReturn("test");
        Mockito.when(mockChannelHandlerContext.channel()).thenReturn(mockNioSocketChannel);
        Mockito.when(mockNioSocketChannel.remoteAddress()).thenReturn(givenInetSocketAddress);

        // Then:
        hermesChannelInboundHandler.channelRegistered(mockChannelHandlerContext);


        Mockito.verify(mockNioSocketChannel).remoteAddress();
//        Mockito.verify(ConnectionManager.getInstance()).addConnection(givenInetSocketAddress.getHostString(), givenInetSocketAddress.getPort(), "test", mockChannelHandlerContext);
    }
}
