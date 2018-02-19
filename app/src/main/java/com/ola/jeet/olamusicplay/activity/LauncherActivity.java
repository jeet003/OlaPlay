package com.ola.jeet.olamusicplay.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ola.jeet.olamusicplay.backgroundServices.GetSongs;
import com.ola.jeet.olamusicplay.interfaces.Volley;
import com.ola.jeet.olamusicplay.models.SongItem;

import java.util.ArrayList;

/**
 * Created by jeet on 12/18/2017.
 */

public class LauncherActivity extends AppCompatActivity implements Volley.GetSong {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchContacts();
    }
    @Override
    public void onGetSong(ArrayList<SongItem> songItems, String rtrnValue) {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        if(rtrnValue.equals("SUCCESS")){
            Intent intent=new Intent(this,MainActivity.class);
            intent.putParcelableArrayListExtra("list",songItems);
            intent.putExtra("toolbar","Songs");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);



        }else if(rtrnValue.equals("NO DATA")){

            Intent intent=new Intent(this,MainActivity.class);
            intent.putParcelableArrayListExtra("list",null);
            intent.putExtra("toolbar","Songs");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }else{
            Intent intent=new Intent(this,MainActivity.class);
            intent.putParcelableArrayListExtra("list",null);
            intent.putExtra("toolbar","Songs");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    private void fetchContacts() {
        progressDialog= ProgressDialog.show(this,"","Please wait.");
        GetSongs getSongs=new GetSongs(this);
        getSongs.getSongDetails();
    }
}
