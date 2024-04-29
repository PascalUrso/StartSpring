package demo.model;

import java.util.List;

// Hotel rooms POJO
public class Room {
    private int id;
    private int occupancy;
    private List<Calendar> calendar;

    public Room() {
    }

    public Room(int id, int occupancy, List<Calendar> calendar) {
        this.id = id;
        this.occupancy = occupancy;
        this.calendar = calendar;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getOccupancy() {
        return occupancy;
    }
    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }
    public List<Calendar> getCalendar() {
        return calendar;
    }
    public void setCalendar(List<Calendar> calendar) {
        this.calendar = calendar;
    }
}