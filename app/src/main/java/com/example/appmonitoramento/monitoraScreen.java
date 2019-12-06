package com.example.appmonitoramento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;

public class monitoraScreen extends AppCompatActivity {
    RespostaServidor resposta = new RespostaServidor();
    ProgressDialog progress;

    private static String token;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);

            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void clique(View view) throws JSONException {
        // Pegar o valor atual do gás de cozinha
        //chama o retrofit para fazer a requisição no webservice
        retrofitConsultar(1);

//      Notificação Local
        /*
        Intent intent = new Intent(monitoraScreen.this,LocalNotify.class);
        intent.putExtra("mensagem",resposta.getData());
        int id = (int)(Math.random()*1000);
        PendingIntent pi = PendingIntent.getActivity(getBaseContext(), id,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder (getBaseContext())
                .setContentTitle("De: Douglas Amorim")
                .setContentText(resposta.getData()).setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pi).build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id,notification);
        */

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pega a intent de outra activity
        Intent monitoraScreen = getIntent();

        //Recuperei a string da outra activity
        token = monitoraScreen.getStringExtra("token");
    }

    public void setaValores(){
        TextView consumoGas = (TextView) findViewById(R.id.textViewvalorGas);
        consumoGas.setText(resposta.getValor());

        TextView idBotijao = (TextView) findViewById(R.id.textViewidBotijao);
        idBotijao.setText(resposta.getIdBotijao());

        TextView data = (TextView) findViewById(R.id.textViewDataMonitoramento);
        data.setText(resposta.getData());

    }

    public void retrofitConsultar(int idUsuario) throws JSONException {

        RetrofitService service = ServiceGenerator.createService(RetrofitService.class,token);

        final String json  =  "{\"idUsuario\": \"1\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);

        //Call<User> userCall = apiInterface.getUser(paramObject.toString());

        Call<RespostaServidor> call = service.consultarConsumo(body);

        call.enqueue(new Callback<RespostaServidor>() {
            @Override
            public void onResponse(Call<RespostaServidor> call, Response<RespostaServidor> response) {

                if (response.isSuccessful()) {

                    RespostaServidor respostaServidor = response.body();

                    //verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        resposta.setData(respostaServidor.getData());
                        resposta.setIdBotijao(respostaServidor.getIdBotijao());
                        resposta.setIdMonitoramento(respostaServidor.getIdMonitoramento());
                        resposta.setIdSensor(respostaServidor.getIdSensor());
                        resposta.setIdUsuario(respostaServidor.getIdUsuario());
                        resposta.setValor(respostaServidor.getValor());

                        setaValores();
                        //progress.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(),"Resposta nula do servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Resposta não foi sucesso", Toast.LENGTH_SHORT).show();
                    // segura os erros de requisição
                    ResponseBody errorBody = response.errorBody();
                }
                //progress.dismiss();
            }

            @Override
            public void onFailure(Call<RespostaServidor> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
            }
        });

    }
}

