package com.ola.jeet.olamusicplay.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ola.jeet.olamusicplay.R;
import com.ola.jeet.olamusicplay.adapter.SongAdapter;
import com.ola.jeet.olamusicplay.backgroundServices.GetSongs;
import com.ola.jeet.olamusicplay.interfaces.Volley;
import com.ola.jeet.olamusicplay.models.SongItem;
import com.ola.jeet.olamusicplay.util.MyDividerItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SongAdapter.SongsAdapterListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private ArrayList<SongItem> songList;
    private SongAdapter mAdapter;
    private SearchView searchView;
    private ProgressDialog progressDialog;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("toolbar"));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        empty=(TextView) findViewById(R.id.empty);
        Intent i=getIntent();

        songList=i.getParcelableArrayListExtra("list");

        if(songList!=null) {
            mAdapter = new SongAdapter(songList, this, this);

            // white background notification bar
            whiteNotificationBar(recyclerView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 18));
            recyclerView.setAdapter(mAdapter);
        }
        else{
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            Toast.makeText(this,"An error has occured. Please try loading again",Toast.LENGTH_LONG).show();
        }

        //fetchContacts();
    }

    /**
     * fetches json by making http calls
     */
    private void fetchContacts() {
        progressDialog=ProgressDialog.show(this,"","Please wait.");
        GetSongs getSongs=new GetSongs(this);
        getSongs.getSongDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
// with MenuItemCompat instead of your MenuItem
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //searchView = (SearchView) menu.findItem(R.id.action_search)
          //      .getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(new ComponentName(getApplicationContext(), MainActivity.class)));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }



    @Override
    public void onSongSelected(SongItem songItem) {
        Toast.makeText(getApplicationContext(), "Selected: " + songItem.getSongName(), Toast.LENGTH_LONG).show();
        Intent intent=new Intent(this,PlayActivity.class);
        intent.putExtra("url",songItem.getUrl());
        intent.putExtra("name",songItem.getSongName());
        intent.putExtra("artist",songItem.getSongArtist());
        intent.putExtra("list",songList);
        intent.putExtra("current_song",songItem);
        startActivity(intent);
    }
}
