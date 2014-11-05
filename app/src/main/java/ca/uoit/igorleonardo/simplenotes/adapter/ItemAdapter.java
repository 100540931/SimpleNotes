package ca.uoit.igorleonardo.simplenotes.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uoit.igorleonardo.simplenotes.R;
import ca.uoit.igorleonardo.simplenotes.model.Note;
import ca.uoit.igorleonardo.simplenotes.utils.AppResources;

public class ItemAdapter extends ArrayAdapter<Note> implements Filterable {
    private ArrayList<Note> notes;
    private ArrayList<Note> filteredNotes;
    private Note note;
    private Filter noteFilter;
    private AppResources appResources = new AppResources();

    public ItemAdapter(Context c, ArrayList<Note> notes) {
        super(c, 0, notes);
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

        holder.color.setBackgroundResource(appResources.getBgColor(note.getBgColor(), appResources.TITLE));
        holder.title.setText(note.getTitle());

        holder.dateTime.setText(appResources.formatDate(note.getDatetime()));

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
