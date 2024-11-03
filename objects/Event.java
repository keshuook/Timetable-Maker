package objects;

import ui.Timetable.Events;

import java.util.ArrayList;
import java.util.List;

public class Event {
    public List<Teacher> teachers;
    public List<Venue> venues;
    public String eventName;
    public String displayName;
    public String className;

    public String getEventName() {
        return eventName;
    }

    public Event(String eventName) {
        this.eventName = eventName;
        teachers = new ArrayList<Teacher>();
        venues = new ArrayList<Venue>();
    }
    public Event(String eventName, Event event) {
        this.eventName = eventName;
        teachers = new ArrayList<Teacher>();
        venues = new ArrayList<Venue>();
        displayName = event.displayName;
        className = event.className;
        for(Teacher teacher : event.teachers) {
            teachers.add(teacher);
        }
        for(Venue venue : event.venues) {
            venues.add(venue);
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setClassName(String name) {
        this.className = name;
    }

    public String getClassName() {
        return className;
    }

    public void updateTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }
    public void updateVenues(List<Venue> venues) {
        this.venues = venues;
    }
    public EventAdditionReturn canBeAddedTo(TimeRange range, int day) {
        for(Teacher teacher : teachers) {
            if(!teacher.getAllowedRangeAtDay(day).isClashingWith(range)) return EventAdditionReturn.TEACHER_NOT_IN_SCHOOL;
            for(TimeRange teacherRange : teacher.getClassTimings(day)) {
                if(range.isClashingWith(teacherRange)) {
                    return EventAdditionReturn.TEACHER_BUSY;
                }
            }
        }
        for(Venue venue : venues) {
            for (TimeRange venueRange : venue.getTimingsAt(day)) {
                if(range.isClashingWith(venueRange)) {
                    return EventAdditionReturn.VENUE_BUSY;
                }
            }
        }
        return EventAdditionReturn.OK;
    }
    public void addEvent(TimeRange range, int day) {
        for(Teacher teacher : teachers) {
            teacher.getClassTimings(day).add(range);
        }
        for(Venue venue : venues) {
            venue.getTimingsAt(day).add(range);
        }
    }
    public void removeEvent(TimeRange range, int day) {
        for(Teacher teacher : teachers) {
            teacher.getClassTimings(day).remove(range);
        }
        for(Venue venue : venues) {
            venue.getTimingsAt(day).remove(range);
        }
    }
}
