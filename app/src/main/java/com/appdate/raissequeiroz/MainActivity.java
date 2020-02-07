package com.appdate.raissequeiroz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    public static final String AGENDAMENTOS_CHILD = "agendamentos";

    private NetworkReceiver networkReceiver;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = findViewById(R.id.cordinatorLayout);
        networkReceiver = new NetworkReceiver();

    }

    class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
                executeAsync();
               /* if (!hasConnection())
                    connectionAlert("Sem conexão");*/
            }
        }
    }

    private void connectionAlert(String mensagem){
        snackbar =  Snackbar.make(coordinatorLayout, mensagem, Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.RED);
        View sbview = snackbar.getView();
        TextView textView = sbview.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        //snackbar.setAction(getString(R.string.conectar), view -> executeAsync()).show();
        snackbar.show();
    }

    private boolean hasConnection(){
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }

    private void  executeAsync(){
        ConexaoAsync conexaoAsync = new ConexaoAsync();
        conexaoAsync.execute(hasConnection());
    }

    class ConexaoAsync extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            if (booleans[0]) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204")
                                    .openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    boolean conectado = (urlc.getResponseCode() == 204 &&
                            urlc.getContentLength() == 0);
                    if (conectado) {
                        publishProgress(true);
                    } else {
                        publishProgress(false);
                    }
                    Log.i("CONEXAO", "Abrindo conexão");
                } catch (IOException e) {
                    Log.i("CONEXAO", "Error checking internet connection", e.getCause());
                    publishProgress(false);
                }
            } else {
                publishProgress(false);
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            if (values[0]) {
                if (snackbar != null) {
                    snackbar.dismiss();
                }
            }else {
                connectionAlert(getString(R.string.sem_conexao));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }
}