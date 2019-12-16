package com.example.appmonitoramento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.ResponseBody;
import retrofit2.*;

public class monitoraScreen extends AppCompatActivity {
    RespostaServidor resposta = new RespostaServidor();
    ProgressDialog progress;
    SwipeRefreshLayout swipeLayout;

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
        //retrofitConsultar(1);

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
        // Getting SwipeContainerLayout
        swipeLayout = findViewById(R.id.swipe_container);
        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                final SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(monitoraScreen.this);

                String user_salvo = myPreferences.getString("USUARIO", "unknown");
                String senha_salvo = myPreferences.getString("SENHA", "unknown");
                String token = myPreferences.getString("TOKEN", "unknown");

                try {
                    retrofitConsultar(user_salvo,senha_salvo);
                    //alert("TOKEN" + tokenApp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Pega a intent de outra activity
                //Intent monitoraScreen = getIntent();

                //Recuperei a string da outra activity
                //token = monitoraScreen.getStringExtra("token");

                Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeLayout.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });

        // Scheme colors for animation
        swipeLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );


        final SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(monitoraScreen.this);

        String user_salvo = myPreferences.getString("USUARIO", "unknown");
        String senha_salvo = myPreferences.getString("SENHA", "unknown");
        String token = myPreferences.getString("TOKEN", "unknown");

        try {
            retrofitConsultar(user_salvo,senha_salvo);
            //alert("TOKEN" + tokenApp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Pega a intent de outra activity
        //Intent monitoraScreen = getIntent();

        //Recuperei a string da outra activity
        //token = monitoraScreen.getStringExtra("token");
    }

    public void setaValores(){
        TextView consumoGas = (TextView) findViewById(R.id.textViewValor__t);
        consumoGas.setText(resposta.getWeight());

        TextView data_t = (TextView) findViewById(R.id.textViewData__t);
        data_t.setText(resposta.getDate());

        TextView score_t = (TextView) findViewById(R.id.textViewPorcentagem__t);
        score_t.setText(resposta.getScore());
    }

    public void retrofitConsultar(String usuario, String senha) throws JSONException {

        RetrofitService service = ServiceGenerator.createService(RetrofitService.class,usuario,senha);

        //Call<User> userCall = apiInterface.getUser(paramObject.toString());

        Call<RespostaServidor> call = service.consultarConsumo();

        call.enqueue(new Callback<RespostaServidor>() {
            @Override
            public void onResponse(Call<RespostaServidor> call, Response<RespostaServidor> response) {

                if (response.isSuccessful()) {

                    RespostaServidor respostaServidor = response.body();

                    //verifica aqui se o corpo da resposta não é nulo
                    if (respostaServidor != null) {
                        resposta.setDate(respostaServidor.getDate());
                        resposta.setWeight(respostaServidor.getWeight());
                        resposta.setScore(respostaServidor.getScore());

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

