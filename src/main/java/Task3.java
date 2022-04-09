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

public class Task3 extends JFrame implements ActionListener {
    JTextArea textArea;
    JScrollPane scrollPane;

    JTextArea textArea2;
    JScrollPane scrollPane2;
    JLabel label;
    JLabel label2;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;

    Task3() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Task 3");
        this.setSize(500, 550);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        label = new JLabel("Task 1 Text : ");
        // ------ text 2 ------
        textArea2 = new JTextArea();
        textArea2.setLineWrap(true);
        textArea2.setWrapStyleWord(true);
        textArea2.setFont(new Font("Arial", Font.PLAIN, 20));
        scrollPane2 = new JScrollPane(textArea2);
        scrollPane2.setPreferredSize(new Dimension(450, 200));
        scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        label2 = new JLabel("Task 2 Text : ");

        // ------ menubar ------

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // ------ /menubar ------

        this.setJMenuBar(menuBar);
        this.add(label);
        this.add(scrollPane);
        this.add(label2);
        this.add(scrollPane2);


        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Task3 t = new Task3();
        t.startConnection();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            fileChooser.setFileFilter(filter);

            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                Scanner fileIn = null;

                try {
                    fileIn = new Scanner(file);
                    if (file.isFile()) {
                        while (fileIn.hasNextLine()) {
                            String line = fileIn.nextLine() + "\n";
                            textArea.append(line);
                        }
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } finally {
                    fileIn.close();
                }
            }
        }
        if (e.getSource() == saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int response = fileChooser.showSaveDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File file;
                PrintWriter fileOut = null;

                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    fileOut = new PrintWriter(file);
                    fileOut.println(textArea.getText());
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } finally {
                    fileOut.close();
                }
            }
        }
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
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "exchange", "text2");
        String queueName2 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName2, "exchange", "format2");
        String queueName3 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName3, "exchange", "text1");
        String queueName4 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName4, "exchange", "format1");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            textArea2.setText(message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

            if (message.split(";").length == 1) {
                textArea2.setForeground(new Color(Integer.parseInt(message.split(";")[0].split(",")[0]), Integer.parseInt(message.split(";")[0].split(",")[1]), Integer.parseInt(message.split(";")[0].split(",")[2])));
            }
            if (message.split(";").length == 2) {
                textArea2.setFont(new Font(message.split(";")[1], Font.PLAIN, textArea2.getFont().getSize()));
            }
            if (message.split(";").length == 3) {
                textArea2.setFont(new Font(textArea2.getFont().getFamily(), Font.PLAIN, Integer.parseInt(message.split(";")[2])));
            }
            if (message.split(";").length == 4) {
                if (message.split(";")[3].equals("B")) {
                    textArea2.setFont(new Font(textArea2.getFont().getFamily(), textArea2.getFont().getStyle() +1, textArea2.getFont().getSize()));
                } else  if (message.split(";")[3].equals("N")){
                    textArea2.setFont(new Font(textArea2.getFont().getFamily(), textArea2.getFont().getStyle()-1, textArea2.getFont().getSize()));
                }}
            if (message.split(";").length == 5) {
                if (message.split(";")[4].equals("I")){
                    textArea2.setFont(new Font(textArea2.getFont().getFamily(), textArea2.getFont().getStyle() +2, textArea2.getFont().getSize()));

                }
                else  if (message.split(";")[4].equals("N")) {
                    textArea2.setFont(new Font(textArea2.getFont().getFamily(), textArea2.getFont().getStyle() - 2, textArea2.getFont().getSize()));

                }}
        };
        channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
        });
        DeliverCallback deliverCallback3 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            textArea.setText(message);
        };
        channel.basicConsume(queueName3, true, deliverCallback3, consumerTag -> {
        });

        DeliverCallback deliverCallback4 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            if (message.split(";").length == 1) {
                textArea.setForeground(new Color(Integer.parseInt(message.split(";")[0].split(",")[0]), Integer.parseInt(message.split(";")[0].split(",")[1]), Integer.parseInt(message.split(";")[0].split(",")[2])));
            }
            if (message.split(";").length == 2) {
                textArea.setFont(new Font(message.split(";")[1], Font.PLAIN, textArea.getFont().getSize()));
            }
            if (message.split(";").length == 3) {
                textArea.setFont(new Font(textArea.getFont().getFamily(), Font.PLAIN, Integer.parseInt(message.split(";")[2])));
            }
            if (message.split(";").length == 4) {
                if (message.split(";")[3].equals("B")) {
                    textArea.setFont(new Font(textArea.getFont().getFamily(), textArea.getFont().getStyle() +1, textArea.getFont().getSize()));
                } else  if (message.split(";")[3].equals("N")){
                    textArea.setFont(new Font(textArea.getFont().getFamily(), textArea.getFont().getStyle()-1, textArea.getFont().getSize()));
            }}
            if (message.split(";").length == 5) {
                if (message.split(";")[4].equals("I")){
                    textArea.setFont(new Font(textArea.getFont().getFamily(), textArea.getFont().getStyle() +2, textArea.getFont().getSize()));

                }
             else  if (message.split(";")[4].equals("N")) {
                    textArea.setFont(new Font(textArea.getFont().getFamily(), textArea.getFont().getStyle() - 2, textArea.getFont().getSize()));

                }}
        };
        channel.basicConsume(queueName4, true, deliverCallback4, consumerTag -> {
        });
    }
}
