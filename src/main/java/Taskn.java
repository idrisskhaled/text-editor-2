
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Taskn extends JFrame implements ActionListener {
    int n ;

    JTextArea textAreas[];
    JScrollPane scrollPanes[];
    JLabel labels[];
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem exitItem;

    Taskn(String arg) {
        n =Integer.parseInt(arg);
        textAreas=new JTextArea[n];
        scrollPanes=new JScrollPane[n];
        labels=new JLabel[n];
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Task "+n);
        this.setSize(500, 550);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);
        for(int i=0;i<n;i++){
            textAreas[i] = new JTextArea();
            textAreas[i].setLineWrap(true);
            textAreas[i].setWrapStyleWord(true);
            textAreas[i].setFont(new Font("Arial", Font.PLAIN, 20));
            scrollPanes[i] = new JScrollPane(textAreas[i]);
            scrollPanes[i].setPreferredSize(new Dimension(450, 200));
            scrollPanes[i].setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            labels[i] = new JLabel("Task "+i+" Text : ");
        }


        // ------ menubar ------

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(this);

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // ------ /menubar ------

        this.setJMenuBar(menuBar);
        for(int i=0;i<n;i++){
            this.add(labels[i]);
            this.add(scrollPanes[i]);
        }




        this.setVisible(true);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Taskn t = new Taskn(args[0]);
        t.startConnection();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == exitItem) {
            System.exit(0);
        }
    }

    public void startConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchange", "direct", true);
        String queueNames[]=new String[n];
        String formats[]=new String[n];
        for(int i=0;i<n;i++){
            queueNames[i] = channel.queueDeclare().getQueue();
            channel.queueBind(queueNames[i], "exchange", "text"+i);
             formats[i] = channel.queueDeclare().getQueue();
            channel.queueBind( formats[i] , "exchange", "format"+i);
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
                textAreas[finalI].setText(message);
            };
            channel.basicConsume(queueNames[i], true, deliverCallback, consumerTag -> {});
            int finalI1 = i;
            DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

                if (message.split(";").length == 1) {
                    textAreas[finalI1].setForeground(new Color(Integer.parseInt(message.split(";")[0].split(",")[0]), Integer.parseInt(message.split(";")[0].split(",")[1]), Integer.parseInt(message.split(";")[0].split(",")[2])));
                }
                if (message.split(";").length == 2) {
                    textAreas[finalI1].setFont(new Font(message.split(";")[1], Font.PLAIN, textAreas[finalI1].getFont().getSize()));
                }
                if (message.split(";").length == 3) {
                    textAreas[finalI1].setFont(new Font(textAreas[finalI1].getFont().getFamily(), Font.PLAIN, Integer.parseInt(message.split(";")[2])));
                }
                if (message.split(";").length == 4) {
                    if (message.split(";")[3].equals("B")) {
                        textAreas[finalI1].setFont(new Font(textAreas[finalI1].getFont().getFamily(), textAreas[finalI1].getFont().getStyle() +1, textAreas[finalI1].getFont().getSize()));
                    } else  if (message.split(";")[3].equals("N")){
                        textAreas[finalI1].setFont(new Font(textAreas[finalI1].getFont().getFamily(), textAreas[finalI1].getFont().getStyle()-1, textAreas[finalI1].getFont().getSize()));
                    }}
                if (message.split(";").length == 5) {
                    if (message.split(";")[4].equals("I")){
                        textAreas[finalI1].setFont(new Font(textAreas[finalI1].getFont().getFamily(), textAreas[finalI1].getFont().getStyle() +2, textAreas[finalI1].getFont().getSize()));

                    }
                    else  if (message.split(";")[4].equals("N")) {
                        textAreas[finalI1].setFont(new Font(textAreas[finalI1].getFont().getFamily(), textAreas[finalI1].getFont().getStyle() - 2, textAreas[finalI1].getFont().getSize()));

                    }}
            };
            channel.basicConsume(formats[i], true, deliverCallback2, consumerTag -> {
            });
        }


    }
}
