package server;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.util.ByteString;
import akka.util.ByteStringBuilder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class SimpleHandler extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    ByteString buffer = ByteString.empty();

    @Override
    public void onReceive(Object msg) throws Exception {
        log.info("Opened SimpleHandler");
        if (msg instanceof Received) {
            final ByteString data = ((Received) msg).data();
            log.info("Received. Incoming data:");
            if (data.decodeString("UTF-8").equals("history")) {
                log.info("History request...");                
                getSender().tell(TcpMessage.write(buffer), getSelf());
                log.info("History sent to TCP client.");
            } else {
                log.info(data.decodeString("UTF-8"));               
                buffer = buffer.concat(data)
                        .concat(ByteString.fromString("\n"));
                getSender().tell(TcpMessage.write(data), getSelf());
            }
            //System.out.println(data.decodeString("UTF-8"));
            //System.out.println(buffer.decodeString("UTF-8"));
        } else if (msg instanceof ConnectionClosed) {
            log.info("message - ConnectionFailed");
            getContext().stop(getSelf());
        }
    }
}
