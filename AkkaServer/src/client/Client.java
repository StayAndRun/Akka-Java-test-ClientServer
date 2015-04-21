/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {

    public static void main(String[] arg) {
        ActorSystem system = ActorSystem.create("TCPClient");
        final ActorRef manager = system.actorOf(Props.create(Manager.class), "listener");
        //System.out.println("Enter the InetSocket adress.");    
        final ActorRef client = system.actorOf(Props.create(TCPClient.class,
                new InetSocketAddress("localhost", 90), manager), "client");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String f = sc.nextLine();
            if (f.equals("exit")) break;
            manager.tell(f, ActorRef.noSender());
        }
        system.shutdown();
    }
}
