package server;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public class Server {

    public static void main(String[] arg) {
        ActorSystem system = ActorSystem.create("TCPServer");
        final ActorRef server = system.actorOf(Props.create(TCPServer.class), "server");
    }
}
