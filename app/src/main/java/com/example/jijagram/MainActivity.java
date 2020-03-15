package com.example.jijagram;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Server server;
    String myName;
    MessageController mc = new MessageController();

    @Override
    protected void onStart() {
        super.onStart();
        server = new Server(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mc.addMessage(
                                new MessageController.Message(
                                        pair.first,
                                        pair.second,
                                        false)
                        );
                    }
                });

            }
        }, new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = String.format("From %s: %s", pair.second, pair.first);
                        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        server.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText messageField = findViewById(R.id.messageField);
        Button sendButton = findViewById(R.id.sendButton);

        RecyclerView messageWindow = findViewById(R.id.recyclerView);


        mc.setIncomingLayout(R.layout.message_in)
                .setOutgoingLayout(R.layout.message)
                .setMessageTextId(R.id.messageText)
                .setUserNameId(R.id.userName)
                .setMessageTimeId(R.id.messageTime)
                .appendTo(messageWindow, this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageField.getText().toString();
                mc.addMessage(
                        new MessageController.Message(userMessage,
                                myName,
                                true)
                );
                server.sendMessage(userMessage);
                messageField.getText().clear();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name: ");

        final EditText nameInput = new EditText(this);
        builder.setView(nameInput);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myName = nameInput.getText().toString();
                server.sendName(myName);
            }
        });

        builder.show();
    }
}
