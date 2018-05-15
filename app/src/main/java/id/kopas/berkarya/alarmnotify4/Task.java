package id.kopas.berkarya.alarmnotify4;

class Task {
    private int day;
    private String time,title,desc;
    public Task(int day, String time, String title, String desc) {
        this.day = day;
        this.time = time;
        this.title = title;
        this.desc = desc;
    }

    public int getDay(){return day;}
    public String getTime(){return time;}
    public String getTitle(){return title;}
    public String getDesc(){return desc;}
}
