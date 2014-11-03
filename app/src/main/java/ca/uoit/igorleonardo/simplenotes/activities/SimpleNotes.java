package ca.uoit.igorleonardo.simplenotes.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import ca.uoit.igorleonardo.simplenotes.adapter.ItemAdapter;
import ca.uoit.igorleonardo.simplenotes.model.Note;
import ca.uoit.igorleonardo.simplenotes.R;
import ca.uoit.igorleonardo.simplenotes.database.DAO;


public class SimpleNotes extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    private DAO db;
    private Menu mainMenu;
    private ListView mainListView;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private Button btnNewNote;
    private LinearLayout emptyList;
    private ItemAdapter adapter;
    private ArrayList<Note> notes = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_notes);

        initResources();
    }

    private void initResources() {

        // Setting up listview, dataset and adapter

        // Create a new DAO object
        db = new DAO(this);
        // Get all notes from DB
        notes = db.getAllNotes();

        // Find the listview
        mainListView = (ListView) findViewById( R.id.notes_list );
        // Create the new adapter with all notes
        adapter = new ItemAdapter(this, notes);
        // Set the listview adapter
        mainListView.setAdapter(adapter);
        // Enable search listview items search function
        mainListView.setTextFilterEnabled(true);
        // Find layout used in case of an empty list
        emptyList = (LinearLayout) findViewById(R.id.empty_list);
        // Set the empty view in case of empty list
        mainListView.setEmptyView(emptyList);
        // Find new note button
        btnNewNote = (Button) findViewById(R.id.btn_new_note);
        // Set visibility of the new note button according to the list
        // hiding it in case of and empty list was given
        btnNewNote.setVisibility(mainListView.getCount()>0 ? View.VISIBLE : View.GONE);

        // Activate the click listeners
        mainListView.setOnItemClickListener(this);
        mainListView.setOnItemLongClickListener(this);
        emptyList.setOnClickListener(this);
        btnNewNote.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple_notes, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.toLowerCase());
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        SearchView.OnCloseListener onCloseSearchListener = new SearchView.OnCloseListener(){
            @Override
            public boolean onClose() {
                mainListView.setEmptyView(emptyList);
                btnNewNote.setVisibility(mainListView.getCount()>0 ? View.VISIBLE : View.GONE);
                return false;
            }
        };
        searchView.setOnCloseListener(onCloseSearchListener);
        searchView.setOnSearchClickListener(this);

        MenuItemCompat.collapseActionView(mainMenu.findItem(R.id.action_search));
        (mainMenu.findItem(R.id.action_search)).collapseActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.btn_new_note :
            case R.id.empty_list :
                // If any new note option was clicked
                // start the new activity and wait for results
                Intent intent = new Intent(this, NoteEdit.class);
                startActivityForResult(intent, 1);

                /*adapter.sort(new Comparator<Note>() {
                    public int compare(Note object1, Note object2) {
                        return object2.getBgColor().compareTo(object1.getBgColor()); //decrescente
                        //return object1.getBgColor().compareTo(object2.getBgColor()); //crescente
                        adapter.notifyDataSetChanged();
                    };
                });*/
                break;
            case R.id.action_search:
                mainListView.setEmptyView(null);
                btnNewNote.setVisibility(View.GONE);
                break;
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1) {

            if (searchView.isShown()) {
                searchMenuItem.collapseActionView();
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchMenuItem.setVisible(false);
            }

            // Get all notes
            notes = db.getAllNotes();

            // Clear adapter
            adapter.clear();
            // Add all notes received from database
            adapter.addAll(notes);
            // Apply those changes
            adapter.notifyDataSetChanged();

            // Hide the new note button in case of an empty list
            btnNewNote.setVisibility(mainListView.getCount() > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int i, long l) {

        // close search view if its visible
        if (searchView.isShown()) {
            searchMenuItem.collapseActionView();
            searchView.setQuery("", false);
        }

        // In case of the user click on a item of the list
        // Set up a new intent
        Intent intent = new Intent(this, NoteEdit.class);
        // Prepare to send the row id to the new activity
        intent.putExtra("rowId", adapter.getItem(i).getId());
        // Start the new activity and wait for results
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onItemLongClick(AdapterView parent, View view, int i, long l) {
        return false;
    }
}
