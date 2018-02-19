package com.ola.jeet.olamusicplay.backgroundServices;

        import android.content.Context;
        import android.util.Log;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.NetworkError;
        import com.android.volley.NetworkResponse;
        import com.android.volley.NoConnectionError;
        import com.android.volley.ParseError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.ServerError;
        import com.android.volley.TimeoutError;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.ola.jeet.olamusicplay.interfaces.Volley;
        import com.ola.jeet.olamusicplay.models.SongItem;
        import com.ola.jeet.olamusicplay.util.VolleySingleton;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

/**
 * Created by jeet on 18/12/17.
 *
 * The songs and other details are being fetched using this volley class
 */

public class GetSongs {

    private Context context;
    private String rtrnValue,url;
    private String Tag="GetSongs";
    private Volley.GetSong getSongList;

    public GetSongs(Context context){
        this.context=context;
        url="http://starlord.hackerearth.com/studio";
        try {
            getSongList=(Volley.GetSong) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException();
        }
    }


    //fetches the list of stories
    public void getSongDetails()
    {
        final Map<String,String> params=new HashMap<String, String>();

        StringRequest storyRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null){
                    try {
                        Log.d("Title",response);

                        JSONArray songArray=new JSONArray(response);
                        rtrnValue="SUCCESS";

                        ArrayList<SongItem> songItemArrayList=new ArrayList<>();

                        if(songArray.length()==0){
                            rtrnValue="NO DATA";
                        }
                        else{
                            for(int i=0;i<songArray.length();i++){
                                JSONObject jsonObject=songArray.getJSONObject(i);
                                String image=jsonObject.getString("cover_image");
                                String name=jsonObject.getString("song");
                                String artist=jsonObject.getString("artists");
                                String url=jsonObject.getString("url");

                                SongItem songItem=new SongItem();
                                songItem.setSongArtist(artist);
                                songItem.setSongName(name);
                                songItem.setSongImage(image);
                                songItem.setUrl(url);
                                songItem.setIndex(i);

                                songItemArrayList.add(songItem);

                            }

                        }
                        getSongList.onGetSong(songItemArrayList,rtrnValue);

                    } catch (Exception e) {
                        e.printStackTrace();
                        rtrnValue="ERROR";
                        getSongList.onGetSong(null,rtrnValue);
                    }
                }
                else{
                    // response is null
                    rtrnValue="ERROR";
                    getSongList.onGetSong(null,rtrnValue);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                }
                if (error instanceof TimeoutError) {
                    //AlertDialogs.show((Activity) context,"","Please try after some time. Slow net may be the problem");
                    rtrnValue="HANDLED";
                    getSongList.onGetSong(null,rtrnValue);
                    //Toast.makeText(context, "Please try after some time. Slow net may be the problem", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    //AlertDialogs.show((Activity) context,"",context.getString(R.string.no_internet_connection_text));
                    rtrnValue="HANDLED";
                    getSongList.onGetSong(null,rtrnValue);
                    //Toast.makeText(context, "Check your network connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.e("Volley", "AuthFailureError");
                    rtrnValue="ERROR";
                    getSongList.onGetSong(null,rtrnValue);
                } else if (error instanceof ServerError) {
                    Log.e("Volley", "ServerError");
                    rtrnValue="ERROR";
                    getSongList.onGetSong(null,rtrnValue);
                } else if (error instanceof NetworkError) {
                    Log.e("Volley", "NetworkError");
                    rtrnValue="ERROR";
                    getSongList.onGetSong(null,rtrnValue);
                } else if (error instanceof ParseError) {
                    Log.e("Volley", "Parse Error: " + error.getMessage());
                    rtrnValue="ERROR";
                    getSongList.onGetSong(null,rtrnValue);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header=new HashMap<String, String>();
                header.put("accept","application/json");
                return header;
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(storyRequest);
    }


}

