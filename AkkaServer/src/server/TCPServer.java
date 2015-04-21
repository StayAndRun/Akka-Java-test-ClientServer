package server;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.io.Tcp;
import akka.io.Tcp.Connected;
import akka.io.TcpMessage;
import akka.io.Udp.Bound;
import akka.io.Udp.CommandFailed;
import java.net.InetSocketAddress;
import akka.event.LoggingAdapter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class TCPServer extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef handler = null;
    private int clientCount = 0;

    @Override
    public void preStart() throws Exception {
        log.info("TCPServer starting...");
        final ActorRef tcp = Tcp.get(getContext().system()).manager();
        tcp.tell(TcpMessage.bind(getSelf(),
                new InetSocketAddress("localhost", 90), 100), getSelf());
        log.info("OK. TCPServer IP: LocalHost, Port: 90.");
        handler = getContext().actorOf(Props.create(SimpleHandler.class));
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof Bound) {
            log.info("Message - Bound");
        } else if (msg instanceof CommandFailed) {
            log.info("Message - CommandFailed");
            getContext().stop(getSelf());
        } else if (msg instanceof Connected) {
            clientCount++;
            log.info("Client #" + clientCount + " connected to TCPServer.");
            final Connected conn = (Connected) msg;
            getSender().tell(TcpMessage.register(handler), getSelf());
        }
    }
}
