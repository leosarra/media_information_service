package core;

import com.rabbitmq.client.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitReceive implements Runnable{
    private final static String QUEUE_NAME = "MSI_Info";
    private static Channel channel;

    public RabbitReceive(){
        System.out.println("[RabbitMQ] Setup flow has started...");
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //channel.queuePurge("MSI_Info"); //Remove unread messages from a previous run of the server
            System.out.println("[RabbitMQ] Waiting for messages...");
        } catch (TimeoutException e) {
            e.printStackTrace();
            Application.config="NORABBIT";
        } catch (IOException e) {
            e.printStackTrace();
            Application.config="NORABBIT";
        }


    }

    @Override
    public void run() {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [RabbitMQ] Received '" + message + "'");
                fileOP(message);
            }
        };
        try {
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void fileOP(String text){
        File fl=new File("media_request.txt");
        if(!fl.exists()) try {
            fl.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long sizeInBytes=fl.length();
        long sizeInMb = sizeInBytes/ (1024 * 1024);

        if (sizeInMb> 20){
            fl.delete();
            try {
                fl.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw=new FileWriter(fl,true);
            fw.append(text);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
