package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Teacher {
    protected List<TimeRange>[] classTimings;
    protected List<TimeRange> allowedTimings;
    protected String name;
    public Teacher(String name, List<TimeRange> allowedTimings) {
        this.name = name;
        this.allowedTimings = allowedTimings;
        this.classTimings = new List[5];
        for(int i = 0;i < 5;i++) {
            classTimings[i] = new ArrayList<>();
        }
    }
    public Teacher(Teacher teacher) {
        this.name = teacher.name;
        this.allowedTimings = new ArrayList<>(teacher.allowedTimings);
        this.classTimings = new List[5];
        for(int i = 0;i < 5;i++) classTimings[i] = new ArrayList<>();
    }
    public List<TimeRange> getAllowedTimings() {
        return allowedTimings;
    }
    public String getTeachersName() {
        return this.name;
    }
    public void rename(String name) {
        this.name = name;
    }
    public TimeRange getAllowedRangeAtDay(int day) {
        return allowedTimings.get(day);
    }
    public List<TimeRange> getClassTimings(int day) {
        return classTimings[day];
    }
}
