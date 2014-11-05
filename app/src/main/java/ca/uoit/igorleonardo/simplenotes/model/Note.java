package ca.uoit.igorleonardo.simplenotes.model;

public class Note {
    private long id;
    private String title;
    private String note;
    private Long date_time;
    private Integer color;

    public Note(long id, String title, String note, long date_time, int color) {
        super();
        this.id = id;
        this.title = title;
        this.note = note;
        this.date_time = date_time;
        this.color = color;
    }

    public Note() { }

    public String getTitle() { return title; }

    public String getNote() { return note; }

    public Long getDatetime() { return date_time; }

    public long getId() { return id; }

    public Integer getBgColor() { return color; }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDatetime(long date_time) {
        this.date_time = date_time;
    }

    public void setBgColor(int color) { this.color = color; }
}
