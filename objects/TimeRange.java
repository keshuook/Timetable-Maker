package objects;

public class TimeRange {
    private int minHour, minMinute, maxHour, maxMinute;
    public TimeRange(int mh, int mm, int xh, int xm) {
        this.minHour = mh;
        this.minMinute = mm;
        this.maxHour = xh;
        this.maxMinute = xm;
    }
    public TimeRange(TimeRange range) {
        this.minHour = range.minHour;
        this.maxHour = range.maxHour;
        this.minMinute = range.minMinute;
        this.maxMinute = range.maxMinute;
    }
    public boolean isClashingWith(TimeRange time) {
        int thisMinTime = (this.minHour*60)+this.minMinute;
        int thisMaxTime = (this.maxHour*60)+this.maxMinute;
        int objMinTime = (time.minHour*60)+time.minMinute;
        int objMaxTime = (time.maxHour*60)+time.maxMinute;
        return (objMinTime >= thisMinTime && objMinTime < thisMaxTime) || (objMaxTime > thisMinTime && objMinTime <= thisMinTime);
    }
    public String[] getAsStrings() {
        String[] strs = new String[4];
        strs[0] = this.minHour+"";
        strs[1] = this.minMinute+"";
        strs[2] = this.maxHour+"";
        strs[3] = this.maxMinute+"";
        for (int i = 0;i < strs.length;i++) {
            if(strs[i].length() == 1) strs[i] = "0".concat(strs[i]);
        }
        return strs;
    }
    public String getRangeAsString() {
        String[] strs = getAsStrings();
        return strs[0]+":"+strs[1]+" - "+strs[2]+":"+strs[3];
    }
    public static boolean IsRangeBad(int mh, int mm, int xh, int xm) {
        return xh < mh || (mh == xh && xm < mm) || (xh > 23 || xm > 59 || mm > 59);
    }
    public boolean IsRangeBad() {
        return (this.maxHour < this.minHour || (this.minHour == this.maxHour && this.maxMinute < this.minMinute)) || (this.maxHour > 23 || this.maxMinute > 59 || this.minMinute > 59);
    }
    public static TimeRange fromString(String min, String max) {
//         Input format mh:mm, xh:xm
        return new TimeRange(Integer.parseInt(min.split(":")[0]), Integer.parseInt(min.split(":")[1]), Integer.parseInt(max.split(":")[0]), Integer.parseInt(max.split(":")[1]));
    }
}
