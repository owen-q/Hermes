package org.lunker.new_proxy.model;

/**
 * Created by dongqlee on 2018. 5. 31..
 */
public class Constants {
    public static class Options{
        public static class UDP{

        }

        /**
         * Constants for TCP Server
         */
        public static class TCP{
            public static final String SO_BACKLOG="so_bakclog";
            public static final String SO_LINGER="so_linger";
            public static final String TCP_NODELAY="tcp_nodelay";
            public static final String SO_RCVBUF="so_rcvbuf";
            public static final String SO_SNDBUF="so_sndbuf";
        }

        /**
         * Constants for TLS Server
         */
        public static class TLS {
            public static final String SSL_CERT = "ssl_cert";
            public static final String SSL_KEY = "ssl_key";
        }

        public static class WS{

        }

        /**
         * Constants for wss server
         */
        public static class WSS{
            public static final String SSL_CERT="ssl_cert";
            public static final String SSL_KEY="ssl_key";
        }
    }
}
