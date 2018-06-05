package com.example.charl.gamingnewz.Activities.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.charl.gamingnewz.Activities.Adapters.NewsAdapter;
import com.example.charl.gamingnewz.Activities.Interfaces.GamingNewZAPI;
import com.example.charl.gamingnewz.Activities.POJO.News;
import com.example.charl.gamingnewz.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainNews.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainNews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainNews extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView rv;
    NewsAdapter adapter;
    ArrayList<News> newz;
    private String token;
    GridLayoutManager gManager;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainNews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainNews.
     */
    // TODO: Rename and change types and number of parameters
    public static MainNews newInstance(String param1, String param2) {
        MainNews fragment = new MainNews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_main_news, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LToken", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("Token","");



        rv = vista.findViewById(R.id.recycler);


       newz = new ArrayList<>();


        gManager = new GridLayoutManager(getActivity(), 2);
        gManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position % 3 == 0) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });


        rv.setLayoutManager(gManager);
        prepareNews();




        adapter = new NewsAdapter(newz);





        rv.setAdapter(adapter);


        return vista;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
        public void prepareNews(){
            Retrofit retrofit = new Retrofit.Builder().baseUrl(GamingNewZAPI.FINISH).addConverterFactory(GsonConverterFactory.create(new Gson())).build();
            GamingNewZAPI GNZAPI = retrofit.create(GamingNewZAPI.class);
            Call<ArrayList<News>> news = GNZAPI.getNews("Beared " + token);
            news.enqueue(new Callback<ArrayList<News>>() {


                @Override
                public void onResponse(Call<ArrayList<News>> call, Response<ArrayList<News>> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getContext(),"Loading Data....",Toast.LENGTH_SHORT).show();

                        newz = (ArrayList<News>) response.body();
                        Toast.makeText(getContext(),newz.get(0).getGame(),Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getContext(),"Error al cargar noticias",Toast.LENGTH_LONG).show();
                        Toast.makeText(getContext(),token,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<News>> call, Throwable t) {
                    Toast.makeText(getContext(),"Error de coneccion",Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(),token,Toast.LENGTH_LONG).show();
                }
            });



        }
}
