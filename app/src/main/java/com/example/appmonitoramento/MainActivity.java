package com.example.appmonitoramento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("asdasdasdas");
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean enviado = sharedPreferences.getBoolean("enviado", false);
                if (enviado) {
                    System.out.println("O token foi gerado e enviado ao servidor.");
                } else {
                    System.out.println("Um erro aconteceu ao gerar o token.");
                }
            }
        };

        if(verificarPlayServices()) {
            Intent intent = new Intent(this, RegistoService.class);
            startService(intent);
        }

        /*
        Button btLogin = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tLogin = (TextView) findViewById(R.id.tLogin);
                TextView tSenha = (TextView) findViewById(R.id.tSenha);

                String login = tLogin.getText().toString();
                String senha = tSenha.getText().toString();

                if(login.equals("amorimdos") && senha.equals("90909090")){
                    alert("Login Realizado com Sucesso");
                    Intent monitoraScreen = new Intent(MainActivity.this, monitoraScreen.class);
                    startActivity(monitoraScreen);
                }else {
                    alert("LOGIN OU SENHA INCORRETOS");
                };

            }
        });
        */
    }
   /* private void alert(String s){
        Toast.makeText(this,s, Toast.LENGTH_LONG).show();
    }*/
    private boolean verificarPlayServices() {
        int codigo = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (codigo != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(codigo)) {
                GooglePlayServicesUtil.getErrorDialog(codigo, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                Log.i(TAG, "BANANA.");
            } else {
                Log.i(TAG, "Este dispositivo não permite usar o recurso.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("registrationComplete"));
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }
}
