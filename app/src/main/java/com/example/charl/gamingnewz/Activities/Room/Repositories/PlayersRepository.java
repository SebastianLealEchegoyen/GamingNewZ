package com.example.charl.gamingnewz.Activities.Room.Repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.charl.gamingnewz.Activities.Activities.Login;
import com.example.charl.gamingnewz.Activities.Interfaces.GamingNewZAPI;
import com.example.charl.gamingnewz.Activities.POJO.Players;
import com.example.charl.gamingnewz.Activities.Room.DAOs.PlayersDAO;
import com.example.charl.gamingnewz.Activities.Room.Room_Entities_and_Database.GamingNewZDatatbase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlayersRepository {

    private PlayersDAO playerDao;  //El DAO
    private LiveData<List<Players>> LolPlayers;   // La lista donde recibiremos los datos
    private LiveData<List<Players>> OverPlayers;
    private LiveData<List<Players>> CsgoPlayers;
    private String UsrToken;
    public Context ctx;

    public PlayersRepository(Application application) { //Application es para Viewmodel.

        GamingNewZDatatbase db = GamingNewZDatatbase.getAppDataBase(application);
        playerDao = db.playersDAO();

        //Recuperando el Token
        SharedPreferences sharedPref = application.getSharedPreferences("LToken", Context.MODE_PRIVATE);
        UsrToken = sharedPref.getString("Token", "");

        GetPlayers();
        LolPlayers = playerDao.getLolPlayers();
        OverPlayers = playerDao.getOverPlayers();
        CsgoPlayers = playerDao.getCsgoPlayers();
    }

    public LiveData<List<Players>> getLolPlayers() {
        return LolPlayers;
    }

    public LiveData<List<Players>> getOverPlayers() {
        return OverPlayers;
    }

    public LiveData<List<Players>> getCsgoPlayers() {
        return CsgoPlayers;
    }

    public void GetPlayers() {
        new GtPlayers(UsrToken, playerDao).execute();
    }


    private static class AsyncTaskI extends AsyncTask<ArrayList<Players>, Void, Void> {

        private PlayersDAO playerDAO;

        AsyncTaskI(PlayersDAO PlayerDao) {
            this.playerDAO = PlayerDao;
        }

        @Override
        protected Void doInBackground(ArrayList<Players>... arrayLists) {
            ArrayList<Players> Not = arrayLists[0]; //Nos vamos a la posicion 0
            for (int e = 0; e < Not.size(); e++) {
                Players players = Not.get(e); //Vamos una por una

                playerDAO.insertPlayer(players);
            }
            return null;
        }


    }

    //Subclase para Consumir la API.
    private static class GtPlayers extends AsyncTask<Void, Void, Void> {

        private String UsrToken;
        private PlayersDAO playerDao;
        public static String code;

        public GtPlayers(String UsrToken, PlayersDAO playerDao) {
            this.UsrToken = UsrToken;
            this.playerDao = playerDao;
        }


        public static String  GetCode(){
            return code;
        }



        @Override
        protected Void doInBackground(Void... voids) {

            Retrofit retrofit = new Retrofit.Builder().baseUrl(GamingNewZAPI.FINISH).addConverterFactory(GsonConverterFactory.create(new Gson())).build();
            GamingNewZAPI GNZAPI = retrofit.create(GamingNewZAPI.class);


            Call<ArrayList<Players>> players = GNZAPI.getPlayers("Beared " + UsrToken);
            players.enqueue(new Callback<ArrayList<Players>>() {


                @Override
                public void onResponse(Call<ArrayList<Players>> call, Response<ArrayList<Players>> response) {
                    if (response.isSuccessful()) {
                        code= response.code()+"";
                        System.out.println(response.code()+"");
                        ArrayList<Players> newz = (ArrayList<Players>) response.body();
                        //Collections.reverse(newz); // Por este medio le damos vuelta a la lista de mas nuevo a mas viejo.
                        new AsyncTaskI(playerDao).execute(newz);

                    } else {
                        code= response.code()+"";
                        System.out.println(response.code()+"");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Players>> call, Throwable t) {
                    code= t.getMessage()+"";
                    System.out.println(t.getMessage()+"");
                }
            });
            return null;
        }

    }
}
