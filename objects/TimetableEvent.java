package objects;

public class TimetableEvent {
    private Teacher[] teachers;
    private Venue[] venues;
    public static String ERROR_MESSAGE;
    private String name;
    public TimetableEvent(Teacher[] teachers, Venue[] venues) {
        this.teachers = teachers;
        this.venues = venues;
    }
}
