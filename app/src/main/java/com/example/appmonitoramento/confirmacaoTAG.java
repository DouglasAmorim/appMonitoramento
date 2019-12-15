package com.example.appmonitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class confirmacaoTAG extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_tag);

        //Pega a intent de outra activity
        Intent confirmacaoTAG = getIntent();

        //Recuperei a string da outra activity
       String peso = confirmacaoTAG.getStringExtra("peso");
       String tag = confirmacaoTAG.getStringExtra("tag");
        System.out.println(peso);

        TextView tag_t = (TextView) findViewById(R.id.textViewTag);
        tag_t.setText(tag);

        TextView peso_t = (TextView) findViewById(R.id.textViewPeso);
        peso_t.setText(peso);
    }
}
