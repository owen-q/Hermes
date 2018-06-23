# What is Hermes?
Hermes is modern SIP server framework.

Hermes will substitute old legacy SipServlet.  
Hermes is based on reactive manifesto.

Hermes makes you better reactive programming without consider complicated NIO, reactive backgrounds.

Just create some methods for handling SIP Messages and Start Hermes! 


*Hermes is detach from [new-proxy](https://github.com/lunker/new-proxy) by main contributor owen-q(a.k.a [lunker](https://github.com/lunker))*


## Dependency 
- Java 8 or + 
- Reactor-Netty
- Reactor-Core


## How to Run
1. Download jar from release 
2. Add dependencies
3. Create your SIP Handlers & Consumers 
```java
SipHandler simpleHandlerJustForward200OK = new SipHandler() {
            @Override
            DefaultSipMessage apply(DefaultSipMessage defaultSipMessage) {
                if(defaultSipMessage instanceof DefaultSipRequest){
                    DefaultSipRequest defaultSipRequest = (DefaultSipRequest) defaultSipMessage

                    DefaultSipResponse response200OK = defaultSipRequest.createResponse(SIPResponse.OK)

                    return response200OK
                }
            }
        }

``` 

```java
SipConsumer sipConsumer = new SipConsumer() {
            @Override
            void send(DefaultSipMessage defaultSipMessage) {
                ConnectionManager connectionManager = ConnectionManager.getInstance()

                String remoteHost = "";
                int remotePort = 0;
                String remoteTransport = "";
                ChannelHandlerContext targetCtx = null;

                SIPMessage message = defaultSipMessage.getRawSipMessage()

                /**
                 * Request:
                 *
                 * // Client -> proxy (Direct Connection)
                 *  1) - request uri 정보를 읽어와서 client connection을 뒤지고, 해당 socket에 전송한다
                 *
                 * // Client -> LB -> Proxy
                 *  2) - request uri의 정보를 읽어와서 client connection을 못찾으면,
                 *  top via의 정보를 읽어서 해당 노드의 정보가 LB인지 확인한다.
                 *  LB와 일치하면, 해당 LB에게 전송한다.
                 *
                 * Response:
                 *  -> Via를 뒤져서, 해당 connection을 직접 가지고 있으면 전송, 아니면 lb에게 전송
                 *  -> Via를 뒤져서,
                 */
                if(defaultSipMessage.getRawSipMessage() instanceof SIPRequest){
                    RouteList routeList = defaultSipMessage.getRawSipMessage().getRouteHeaders();

                    if(routeList != null && routeList.size() !=0){
                        // Contains Route header
                        // TODO: Find connection using 'Route' Header
                        RouteHeader routeHeader=(RouteHeader) defaultSipMessage.getRouteHeaders().getFirst();
                        SipUri routeUri=(SipUri) routeHeader.getAddress().getURI();

                        remoteHost=routeUri.getHost();
                        remotePort=routeUri.getPort();
                        remoteTransport=routeUri.getTransportParam();
                    }
                    else{
                        // No Route header
                        // Using request-uri to find target connection
                        SIPRequest sipRequest=(SIPRequest) message;
                        SipUri requestUri=(SipUri) sipRequest.getRequestURI();

                        remoteHost=requestUri.getHost();
                        remotePort=requestUri.getPort();
                        remoteTransport=requestUri.getTransportParam();
                    }
                }
                else{
                    // Response

                    SIPResponse sipResponse=(SIPResponse) message;

                    Via topVia = sipResponse.getTopmostVia();
                    remoteHost = topVia.getReceived();
                    remotePort = topVia.getRPort();

                    remoteTransport = topVia.getTransport().toLowerCase();
                }

                targetCtx = connectionManager.getConnection(remoteHost, remotePort, remoteTransport);

                if(targetCtx != null){
                    ChannelFuture cf;
                    if(Transport.TCP.getValue().equals(remoteTransport)){
                        cf = targetCtx.writeAndFlush((Unpooled.copiedBuffer(message.toString(), CharsetUtil.UTF_8)));
                    }
                    else if(Transport.UDP.getValue().equals(remoteTransport)){
                        cf = targetCtx.writeAndFlush(new DatagramPacket(
                                Unpooled.copiedBuffer(message.toString(), CharsetUtil.UTF_8),
                                new InetSocketAddress(remoteHost, remotePort)));
                    }
                    else if(Transport.WSS.getValue().equals(remoteTransport)){
                        cf = targetCtx.writeAndFlush(new TextWebSocketFrame(message.toString()));
                    }
                }
                else {
                }
            }

            @Override
            void accept(DefaultSipMessage defaultSipMessage) {

                // Do Some stuffs here

                // Send Message to target!
                send(defaultSipMessage)
            }
        }
```
4. Create SipServer using ServerFactory & Run!


```java
ServerFactory serverFactory = new ServerFactory()
        SipServer sipServer = serverFactory
                .host(givenServerListenHost)
                .port(givenServerListenPort)
                .transport(givenServerTransport)
                .sipMessageHandler(simpleHandlerJustForward200OK)
                .sipMessageConsumer(sipConsumer)
                .build()

        sipServer.runSync()
```



## License
Licensed Under GNU Lesser General Public License v3.0





