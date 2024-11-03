package objects;

import java.util.ArrayList;
import java.util.List;

public class Venue {
    protected List<TimeRange>[] timings;
    protected String name;
    public Venue(String name) {
        this.name = name;
        this.timings = new List[5];
        for(int i = 0;i < 5;i++) timings[i] = new ArrayList<>();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TimeRange> getTimingsAt(int day) {
        return timings[day];
    }
}
