package client;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.io.Udp.CommandFailed;
import akka.japi.Procedure;
import akka.util.ByteString;
import java.net.InetSocketAddress;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class TCPClient extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    final InetSocketAddress socket;
    final ActorRef manager;

    public TCPClient(InetSocketAddress socket, ActorRef manager) {
        this.socket = socket;
        this.manager = manager;
        final ActorRef tcp = Tcp.get(getContext().system()).manager();
        tcp.tell(TcpMessage.connect(socket), getSelf());
        log.info(socket.toString());
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof CommandFailed) {
            log.info("Command failed.");
            manager.tell("failed", getSelf());
            getContext().stop(getSelf());
        } else if (msg instanceof Connected) {
            log.info("Connected to TCPServer. OK.");
            manager.tell(msg, getSelf());
            getSender().tell(TcpMessage.register(getSelf()), getSelf());
            getContext().become(connected(getSender()));
            log.info("Client waiting to data message...");
        } else {
            log.info("unknown message/data...");
        }
    }

    private Procedure<Object> connected(final ActorRef connection) {
        return new Procedure<Object>() {
            @Override
            public void apply(Object msg) throws Exception {
                log.info("open connected procedure");
                if (msg instanceof ByteString) {
                    log.info("message - ByteString");
                    connection.tell(TcpMessage.write((ByteString) msg), getSelf());
                } else if (msg instanceof CommandFailed) {
                    log.info("message - Command failed");
                } else if (msg instanceof Received) {
                    log.info("message - received");
                    manager.tell(((Received) msg).data(), getSelf());
                } else if (msg.equals("close")) {
                    log.info("message - close");
                    connection.tell(TcpMessage.close(), getSelf());
                } 
                else if (msg instanceof ConnectionClosed) {
                    log.info("message - connection closed");
                    getContext().stop(getSelf());
                }
            }
        };
    }
}
