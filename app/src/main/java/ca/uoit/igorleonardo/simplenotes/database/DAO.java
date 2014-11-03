package ca.uoit.igorleonardo.simplenotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ca.uoit.igorleonardo.simplenotes.model.Note;

public class DAO extends SQLiteOpenHelper {

    private SQLiteDatabase database;

    // Database informations
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "simplenotes";
    public static final String DATABASE_TABLE = "simplenotes";

    // Application table columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_DATETIME = "date_time";
    public static final String COLUMN_COLOR = "color";

    private String[] allColumns = { COLUMN_ID, COLUMN_TITLE, COLUMN_NOTE
            , COLUMN_DATETIME, COLUMN_COLOR };

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + DATABASE_TABLE + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_NOTE + " TEXT, "
            + COLUMN_DATETIME + " INTEGER, "
            + COLUMN_COLOR + " INTEGER"
            + ")";

    public DAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DAO.class.getName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public void openWrite() throws SQLException {
        database = this.getWritableDatabase();
    }

    public void openRead() throws SQLException { database = this.getReadableDatabase(); }

    public void close() {
        database.close();
    }

    public void addNote(Note note) {
        openWrite();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_NOTE, note.getNote());
        values.put(COLUMN_DATETIME, note.getDatetime());
        values.put(COLUMN_COLOR, note.getBgColor());

        database.insert(DATABASE_TABLE, null, values);

        close();
    }

    public Note getNote(long id){
        openRead();

        Cursor cursor =
                database.query(DATABASE_TABLE, allColumns
                        , COLUMN_ID + " = ?", new String[] { String.valueOf(id) }
                        , null, null, null, null);

        Note note = new Note();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            note = cursorToNote(cursor);
        }

        close();
        return note;
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<Note>();

        openRead();
        Cursor cursor = database.query(DATABASE_TABLE,
                allColumns, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
            cursor.moveToNext();
        }

        cursor.close();
        close();

        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        return new Note(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getInt(4));
    }

    public boolean updateNote(Note note) {
        openWrite();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_NOTE, note.getNote());
        values.put(COLUMN_DATETIME, note.getDatetime());
        values.put(COLUMN_COLOR, note.getBgColor());

        boolean i = database.update(DATABASE_TABLE, values, COLUMN_ID
                + " = ?", new String[]{ String.valueOf(note.getId()) }) > 0;
        close();

        return i;
    }

    public boolean deleteNote(Note note) {
        openWrite();
        boolean i = database.delete(DATABASE_TABLE, COLUMN_ID
                + " = ?", new String[]{ String.valueOf(note.getId()) }) > 0;
        close();

        return i;
    }
}