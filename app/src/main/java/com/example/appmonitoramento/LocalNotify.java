package com.example.appmonitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class LocalNotify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_notify);

        TextView msg = (TextView)findViewById(R.id.txtMsg);
        String mensagem = getIntent().getStringExtra("mensagem");
        msg.setText(mensagem);
    }
}
