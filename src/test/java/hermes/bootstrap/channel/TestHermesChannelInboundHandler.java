package hermes.bootstrap.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.owen.hermes.bootstrap.channel.HermesChannelInboundHandler;
import org.owen.hermes.core.ConnectionManager;

import java.net.InetSocketAddress;

/**
 * Created by owen_q on 2018. 6. 20..
 */
public class TestHermesChannelInboundHandler {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // Mock
    @Mock
    private HermesChannelInboundHandler mockHermesChannelInboundHandler;

    @Mock
    private ChannelHandlerContext mockChannelHandlerContext;

    @Mock
    private NioSocketChannel mockNioSocketChannel;

    private HermesChannelInboundHandler hermesChannelInboundHandler = null;
    private InetSocketAddress givenInetSocketAddress;
    private String givenRemoteHost = "10.0.1.202";
    private int givenRemotePort = 12222;
    private String givenTransport = "test";

    @Before
    public void beforeTest() {
        MockitoAnnotations.initMocks(this);

        hermesChannelInboundHandler = new HermesChannelInboundHandler();
    }

    @Test
    public void addConnectionWhenFireChannelRegistered() throws Exception {
        // Given:
        InetSocketAddress givenInetSocketAddress = new InetSocketAddress(givenRemoteHost, givenRemotePort);

        // When:
        whenChannelEvent();
        // Then:
        hermesChannelInboundHandler.channelRegistered(mockChannelHandlerContext);

        Mockito.verify(mockNioSocketChannel).remoteAddress();
        assertMockConnectionRegistered();
    }

    private void whenChannelEvent(){
        Mockito.when(mockChannelHandlerContext.name()).thenReturn(givenTransport);
        Mockito.when(mockChannelHandlerContext.channel()).thenReturn(mockNioSocketChannel);
        Mockito.when(mockNioSocketChannel.remoteAddress()).thenReturn(givenInetSocketAddress);
    }

    private void assertMockConnectionRegistered(){
        Assert.assertTrue(ConnectionManager.getInstance().isConnectionExist(givenRemoteHost, givenRemotePort, givenTransport));
    }

    @Test
    public void deleteConnectionWhenFireChannelUnregistered() throws Exception{
        // Given:
        InetSocketAddress givenInetSocketAddress = new InetSocketAddress("10.0.1.202", 12222);

        // When:
//        Mockito.when();




        // Then:
        hermesChannelInboundHandler.channelUnregistered(mockChannelHandlerContext);

        assertMockConnectionUnRegistered();
    }

    private void assertMockConnectionUnRegistered(){
        Assert.assertFalse(ConnectionManager.getInstance().isConnectionExist(givenRemoteHost, givenRemotePort, givenTransport));
    }


    @Test
    public void testCreateConnectionKey() {
        // Given:
        String givenHost = "1.1.1.1";
        int givenPort = 11111;
        String givenTransport = "test";

        InetSocketAddress givenRemoteAddress = new InetSocketAddress(givenHost, givenPort);

        Mockito.when(mockChannelHandlerContext.channel().remoteAddress()).thenReturn(givenRemoteAddress);

        // When:
//        ConnectionManager.getInstance().createConnectionKey()

        // Then:


    }
}
