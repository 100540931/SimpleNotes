package ca.uoit.igorleonardo.simplenotes.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ca.uoit.igorleonardo.simplenotes.model.Note;
import ca.uoit.igorleonardo.simplenotes.R;
import ca.uoit.igorleonardo.simplenotes.database.DAO;
import ca.uoit.igorleonardo.simplenotes.utils.AppResources;


public class NoteEdit extends Activity implements View.OnClickListener {
    private DAO db;
    private View layoutAll;
    private View layoutTitle;
    private EditText title;
    private View noteBgColorSelector;
    private ImageView btnSetBgColor;
    private TextView info;
    private TextView datetime;
    private TextView bodyView;
    private EditText body;
    private View actions;
    private Button cancelDiscardButton;
    private Button saveUpdateButton;
    private AppResources appResources = new AppResources();



    private Note note;
    private long id;
    private long time;
    private boolean firstTouch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force fullscreen mode
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.activity_note_edit);

        initResources();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_edit, menu);
        return true;
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

    private void initResources() {
        // Setting up DAO
        db = new DAO(this);
        note = new Note();

        // Finding up all used views
        layoutAll = findViewById(R.id.layoutAll);
        layoutTitle = findViewById(R.id.layoutTitle);
        title =  (EditText) findViewById(R.id.title);
        noteBgColorSelector = findViewById(R.id.note_bg_color_selector);
        btnSetBgColor = (ImageView) findViewById(R.id.btn_set_bg_color);
        info = (TextView) findViewById(R.id.info);
        datetime = (TextView) findViewById(R.id.datetime);
        bodyView = (TextView) findViewById(R.id.bodyView);
        body = (EditText) findViewById(R.id.body);
        actions = findViewById(R.id.actions);
        cancelDiscardButton = (Button) findViewById(R.id.btn_cancel_discard);
        saveUpdateButton = (Button) findViewById(R.id.btn_save);

        // Get message from parent activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){ // if there is any message
            id = extras.getLong("rowId");
            DAO db = new DAO(this);
            note = db.getNote(id);

            layoutAll.setBackgroundResource(appResources.getBgColor(note.getBgColor(), appResources.BODY));
            layoutTitle.setBackgroundResource(appResources.getBgColor(note.getBgColor(), appResources.TITLE));

            title.setText(note.getTitle());
            datetime.setText(appResources.formatDate(note.getDatetime()));
            bodyView.setText(note.getNote());
            body.setText(note.getNote());
            body.setVisibility(View.GONE);

        } else { // if there is no message
            bodyView.setVisibility(View.GONE);
            body.setVisibility(View.VISIBLE);
            saveUpdateButton.setText("Save");
            cancelDiscardButton.setText("Cancel");

            // Setting note's default values
            long millis = System.currentTimeMillis();
            note.setBgColor(appResources.YELLOW);

            note.setDatetime(millis);
            info.setText("New note");
            datetime.setText(appResources.formatDate(millis));
        }

        // Setting up all click listeners
        btnSetBgColor.setOnClickListener(this);
        for (int id : appResources.sBgSelectorBtnsMap.keySet()) {
            ImageView iv = (ImageView) findViewById(id);
            iv.setOnClickListener(this);
        }
        cancelDiscardButton.setOnClickListener(this);
        saveUpdateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_set_bg_color) {
            noteBgColorSelector.setVisibility(noteBgColorSelector.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else if (appResources.sBgSelectorBtnsMap.containsKey(id)) {
            layoutAll.setBackgroundResource(appResources.getBgColor(appResources.sBgSelectorBtnsMap.get(id), appResources.BODY));
            layoutTitle.setBackgroundResource(appResources.getBgColor(appResources.sBgSelectorBtnsMap.get(id), appResources.TITLE));
            noteBgColorSelector.setVisibility(View.GONE);
            note.setBgColor(appResources.sBgSelectorBtnsMap.get(id));
        } else if(id == R.id.btn_cancel_discard){
            if(cancelDiscardButton.getText().equals("Delete")) { // delete
                db.deleteNote(note);
            }
            setResult(1, getIntent());
            finish();
        } else if(id == R.id.btn_save){
            if(saveUpdateButton.getText().equals("Save")){
                note.setTitle(title.getText().toString());
                note.setNote(body.getText().toString());
                db.addNote(note);

            } else {
                note.setTitle(title.getText().toString());
                note.setNote(body.getText().toString());
                note.setDatetime(System.currentTimeMillis());
                db.updateNote(note);
            }
            setResult(1, getIntent());
            finish();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (noteBgColorSelector.getVisibility() == View.VISIBLE
                && !inRangeOfView(noteBgColorSelector, ev)) {
            noteBgColorSelector.setVisibility(View.GONE);
            return true;
        }

        if(ev.getAction() == ev.ACTION_UP
                && bodyView.getVisibility() == View.VISIBLE
                && !inRangeOfView(actions, ev)){
            if(!firstTouch){
                if((System.currentTimeMillis() - time) <= 300) {
                    firstTouch = true;
                    bodyView.setVisibility(View.GONE);
                    body.setVisibility(View.VISIBLE);
                    info.setText("Editing");
                } else {
                    time = System.currentTimeMillis();

                    Toast singleTap = Toast.makeText(this, "Double tap to edit", Toast.LENGTH_SHORT);
                    singleTap.show();
                }
            }
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        int []location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x
                || ev.getX() > (x + view.getWidth())
                || ev.getY() < y
                || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }
}
