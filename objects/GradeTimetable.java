package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradeTimetable {
    private List<TimeRange> timings;
    private Event[][] eventsList;
    private String name;
    public GradeTimetable(String name, List<TimeRange> timings) {
        this.timings = timings;
        this.eventsList = new Event[5][this.timings.size()];
        this.name = name;
    }
    public void setEvent(Event event, int day, int period) {
        this.eventsList[day][period] = event;
    }
    public void removeEvent(Event event, int day, int period) {
        this.eventsList[day][period].removeEvent(timings.get(period), day);
        this.eventsList[day][period] = null;
    }
    public Event[][] getEventsList() {
        return eventsList;
    }

    public String getName() {
        return name;
    }

    public List<TimeRange> getTimings() {
        return timings;
    }
    public void update() {
        if(timings.size() > eventsList[0].length) {
            Event[][] newEvents = new Event[5][timings.size()];
            for (int i = 0; i < eventsList.length; i++) {
                for (int j = 0; j < eventsList[i].length; j++) {
                    newEvents[i][j] = eventsList[i][j];
                }
            }
            this.eventsList = newEvents;
        }
    }
    public void shiftEvents(boolean addOne, int index) {
        // If addOne = true, another element is added at index, if addOne = false, the element at index is removed
        for(int i = 0;i < eventsList.length;i++) {
            List<Event> listOfEvents = new ArrayList<>(Arrays.asList(eventsList[i])); // A list with null values that is mutable
            if(addOne) {
                listOfEvents.add(index, null);
            }else{
                listOfEvents.remove(index);
            }
            eventsList[i] = new Event[listOfEvents.size()];
            listOfEvents.toArray(eventsList[i]);
        }
    }
}
