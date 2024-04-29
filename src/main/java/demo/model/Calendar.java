package demo.model;

public class Calendar {
    private int day;
    private int rate;
    private boolean isAvailable;

    public Calendar() {
    }

    public Calendar(int day, int rate, boolean isAvailable) {
        this.day = day;
        this.rate = rate;
        this.isAvailable = isAvailable;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}