package com.example.todo.backend;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
    static Logger Log = Logger.getLogger("com.example.[USERNAME].myapplication.backend.MyServlet");
    private String url = "https://todo-eb54e.firebaseio.com/todoItems";
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        System.out.println("=============Get");
        // Log.info("Got cron message, constructing email.");
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello World!!");

        //Create a new Firebase instance and subscribe on child events.
        Firebase firebase = new Firebase(url);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Build the email message contents using every field from Firebase.
                System.out.println("=============onDataChange");
                final StringBuilder newItemMessage = new StringBuilder();
                newItemMessage.append("Good Morning!  You have the following todo items:\n");
                for (DataSnapshot todoItem : dataSnapshot.getChildren()) {
                    for (DataSnapshot field : todoItem.getChildren()) {
                        newItemMessage.append(field.getKey())
                                .append(":")
                                .append(field.getValue().toString())
                                .append("\n");
                    }
                }

                //Now Send the email
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                try {
                    Message msg = new MimeMessage(session);
                    //Make sure you substitute your project-id in the email From field
                    msg.setFrom(new InternetAddress("ToDo@todo-eb54e.appspotmail.com",
                            "Todo Nagger"));
                    msg.addRecipient(Message.RecipientType.TO,
                            new InternetAddress("shekarrex@gmail.com", "Recipient"));
                    msg.setSubject("Good Morning!");
                    msg.setText(newItemMessage.toString());
                    Transport.send(msg);
                } catch (javax.mail.MessagingException | UnsupportedEncodingException e) {
                    //  Log.warning(e.getMessage());
                    e.printStackTrace();
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}