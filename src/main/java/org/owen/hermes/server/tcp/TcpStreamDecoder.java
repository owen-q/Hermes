package org.owen.hermes.server.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.header.ContentLengthHeader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dongqlee on 2018. 3. 22..
 */
public class TcpStreamDecoder extends ByteToMessageDecoder{

    private static AtomicInteger num=new AtomicInteger(0);

    private Logger logger= LoggerFactory.getLogger(TcpStreamDecoder.class);

    private final int DEFAULT_HEADER_SIZE=3000;
    private final int DEFAULT_HEADER_LINE_SIZE=512;
    private final int DEFAULT_BODY_SIZE=6000;

    private ByteBuf headerBuffer=null;
    private ByteBuf headerLineBuffer=null;
    private ByteBuf bodyBuffer=null;

    private final int CR=13;
    private final int LF=10;
    private PooledByteBufAllocator pooledByteBufAllocator=null;
    private UnpooledByteBufAllocator unpooledByteBufAllocator=null;

    private boolean isHeaderState=true;
    private boolean isBodyState=false;
    private byte lastByte;
    private boolean hasCRLF=false;
    private int contentLength=-1;
    private int readBodyLength=0; // TODO: change to Atomic
    private byte[] contentByte="Content-Length:".getBytes();

    public TcpStreamDecoder() {
//        logger.info("%d",num.incrementAndGet());

        pooledByteBufAllocator=new PooledByteBufAllocator(true);
        unpooledByteBufAllocator=new UnpooledByteBufAllocator(false);

        headerBuffer=allocate(DEFAULT_HEADER_SIZE);
        headerLineBuffer=allocate(DEFAULT_HEADER_LINE_SIZE);
        bodyBuffer=allocate(DEFAULT_BODY_SIZE);
    }

    /**
     * Allocate buffer
     * @param size Buffer size
     * @return
     */
    public ByteBuf allocate(int size){
        return unpooledByteBufAllocator.buffer(size);
    }

    public boolean contains(ByteBuf srcBuf, byte[] targetBuf){
        int srcSize=srcBuf.readableBytes();
        byte[] srcTmp=new byte[srcSize];
        byte[] small;
        byte[] large;

        srcBuf.getBytes(0, srcTmp);

        if(srcTmp.length > targetBuf.length){
            small=targetBuf;
            large=srcTmp;
        }
        else{
            small=srcTmp;
            large=targetBuf;
        }

        // TODO: Change to Stream API
        for(int idx=0; idx<small.length; idx++){
            if(small[idx] != large[idx])
                return false;
        }

        return true;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte currentByte;

        while(in.isReadable()){
            currentByte=in.readByte();

            if(isHeaderState){
                // read Header
                if(currentByte != CR && currentByte != LF){
                    headerLineBuffer.writeByte(currentByte);
                    this.hasCRLF=false;
                }
                else if(currentByte == CR){
                    // ignore

                    if(!this.hasCRLF && contains(headerLineBuffer, contentByte)){

                        try{
                            contentLength=Integer.parseInt(headerLineBuffer.toString(CharsetUtil.UTF_8).substring(
                                    ContentLengthHeader.NAME.length()+1).trim());

                            if(contentLength > DEFAULT_BODY_SIZE) {
                                bodyBuffer.clear();
                                bodyBuffer.release();
                                bodyBuffer=allocate(contentLength);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                else if(currentByte == LF){
                    if (!this.hasCRLF && this.lastByte == CR){
                        this.hasCRLF=true;

                        headerLineBuffer.writeByte(CR);
                        headerLineBuffer.writeByte(LF);


                        headerBuffer.writeBytes(headerLineBuffer);
                        headerLineBuffer.clear();
                    }
                    else if(this.hasCRLF==true && this.lastByte == CR){
                        // end of header! need to parsing Content-Length Value
                        headerBuffer.writeByte(CR);
                        headerBuffer.writeByte(LF);

                        this.hasCRLF=false;

                        // change state
                        this.isHeaderState=false;
                        this.isBodyState=true;
                    }

                }// end handling
            }

            if(isBodyState){
                // read content body

                if(this.contentLength > this.readBodyLength){
                    this.bodyBuffer.writeByte(currentByte);
                    readBodyLength++;
                }
                else{

                    // create entire Sip wrapper
                    try{
                        String strSipMessage=headerBuffer.toString(0, headerBuffer.writerIndex(), CharsetUtil.UTF_8) + bodyBuffer.toString(0, bodyBuffer.writerIndex(), CharsetUtil.UTF_8);

                        if(logger.isDebugEnabled())
                            logger.debug("Parsed sip wrapper:\n{}", strSipMessage);

                        // reset used buffer
                        headerBuffer.clear();
                        headerLineBuffer.clear();
                        bodyBuffer.clear();

                        // change state
                        this.isHeaderState=true;
                        this.isBodyState=false;
                        this.readBodyLength=0;
                        this.contentLength=0;

                        ctx.fireChannelRead(strSipMessage);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            this.lastByte=currentByte;
        }
    }

    /**
     * Detect isDoubleCRLF
     * @return
     */
    public boolean isDoubleCRLF(){
        return false;
    }
}
