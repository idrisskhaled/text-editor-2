package Part2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Config extends JFrame implements ActionListener {
    int nbUsers;
    JLabel label;
    JTextField field;
    JButton submit;
    JLabel result;
    Config(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("App Config");
        this.setSize(500, 100);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout());
        field=new JTextField();
        result=new JLabel();
        label=new JLabel("Set nb users : ");
        field.setPreferredSize(new Dimension(100,30));
        submit = new JButton("submit");
        submit.addActionListener(this);
        this.add(label);
        this.add(field);
        this.add(submit);
        this.add(result);
        result.setVisible(false);
        this.setVisible(true);
    }



    public void EmitLog(String message)  {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("exchange","direct",true);
            channel.basicPublish("exchange", "nbUsers", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==submit){
            try {
                nbUsers = Integer.parseInt(field.getText());
                result.setText("Users number set to "+nbUsers);
                result.setForeground(Color.green);
                result.setVisible(true);
                this.EmitLog(field.getText());
            }
        catch (Exception x){
            result.setText("Error : Invalid input");
            result.setForeground(Color.red);

            result.setVisible(true);
            x.printStackTrace();
        }
        }

    }

    public static void main(String[] args) {

        Config conf=new Config();

    }
}
