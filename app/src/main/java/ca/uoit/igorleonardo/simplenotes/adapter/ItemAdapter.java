package ca.uoit.igorleonardo.simplenotes.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.uoit.igorleonardo.simplenotes.R;
import ca.uoit.igorleonardo.simplenotes.model.Note;
import ca.uoit.igorleonardo.simplenotes.utils.Resources;

public class ItemAdapter extends ArrayAdapter<Note> implements Filterable {

    private final static int [] NOTE_BG_TITLE_RESOURCES = new int [] {
        R.color.default_background_title_addnote,
        R.color.blue_background_title_addnote,
        R.color.white_background_title_addnote,
        R.color.green_background_title_addnote,
        R.color.red_background_title_addnote
    };

    private Context c;
    private ArrayList<Note> notes;
    private ArrayList<Note> filteredNotes;
    private Note note;
    private Filter noteFilter;
    private Resources resources = new Resources();

    public ItemAdapter(Context c, ArrayList<Note> notes) {
        super(c, 0, notes);
        this.c = c;
        this.notes = notes;
        this.filteredNotes = notes;

        getFilter();
    }

    @Override
    public int getCount() {
        return filteredNotes.size();
    }

    @Override
    public Note getItem(int position) {
        return filteredNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, null);
            holder.color = (TextView) v.findViewById(R.id.priority);
            holder.title = (TextView) v.findViewById(R.id.title);
            holder.dateTime = (TextView) v.findViewById(R.id.dateTime);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        note = getItem(position);

        holder.color.setBackgroundResource(resources.getBgColor(note.getBgColor(), resources.TITLE));
        holder.title.setText(note.getTitle());

        holder.dateTime.setText(resources.formatDate(note.getDatetime()));

        return v;
    }

    static class ViewHolder {
        TextView color;
        TextView title;
        TextView dateTime;
    }


    // Filter
    @Override
    public Filter getFilter() {
        if (noteFilter == null)
            noteFilter = new NoteFilter();

        return noteFilter;
    }

    private class NoteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                final ArrayList<Note> nNotes = new ArrayList<Note>();

                for (final Note g : notes) {
                    if (g.getTitle().toLowerCase().contains(constraint)
                            || String.valueOf(g.getDatetime()).toLowerCase().contains(constraint)
                            || g.getNote().toLowerCase().contains(constraint))
                        nNotes.add(g);
                }

                results.values = nNotes;
                results.count = nNotes.size();
            } else {
                results.values = notes;
                results.count = notes.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredNotes = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }
    }
}
