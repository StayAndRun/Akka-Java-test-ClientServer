/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Tcp.Connected;
import akka.util.ByteString;

public class Manager extends UntypedActor {

    private boolean connected = false;
    private ActorRef client = null;

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof Connected) {
            connected = true;
            client = getSender();
        } else if (msg instanceof String) {
            ByteString string = ByteString.fromString((String) msg);
            client.tell(string, getSelf());
        } else if (msg instanceof ByteString) {           
            System.out.println("Server response: ");            
            System.out.println(((ByteString)msg).decodeString("UTF-8"));
        }
    }
}
