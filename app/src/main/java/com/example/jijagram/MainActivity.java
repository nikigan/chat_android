package com.example.jijagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
        server.sendName(myName);
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
                if (server.getAvailable()) {

                    String userMessage = messageField.getText().toString();
                    mc.addMessage(
                            new MessageController.Message(userMessage,
                                    myName,
                                    true)
                    );
                    server.sendMessage(userMessage);
                    messageField.getText().clear();
                }
                else {
                    String errorText = "Проблема с подключением к серверу! Попробуйте отправить сообщение позже!";
                    Toast.makeText(MainActivity.this, errorText, Toast.LENGTH_LONG).show();
                    Button testBtn = new Button(MainActivity.this);
                    testBtn.setText("Тест сообщений");

                    LinearLayout view = findViewById(R.id.linearLayout2);
                    Button sendButton = findViewById(R.id.sendButton);
                    sendButton.setVisibility(View.GONE);
                    view.addView(testBtn);

                    testBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messageField.getText().clear();
                            hideKeyboard();
                            mc.addMessage(
                                    new MessageController.Message("Hello world!",
                                            myName,
                                            true)
                            );
                            mc.addMessage(
                                    new MessageController.Message("Hi there!",
                                            "Anonymous",
                                            false)
                            );
                            mc.addMessage(
                                    new MessageController.Message("What's UP?",
                                            "Stranger",
                                            false)
                            );
                            mc.addMessage(
                                    new MessageController.Message("It is JijaGram, not WhatsApp",
                                            myName,
                                            true)
                            );
                        }
                    });
                }
            }
        });

        myName = getIntent().getStringExtra("userName");
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
