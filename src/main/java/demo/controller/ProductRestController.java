package demo.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import demo.model.Calendar;
import demo.model.Room;

@RestController
@RequestMapping("/rooms")
public class ProductRestController {
    private static List<Calendar> generateCalendar(int baseRate) {
        List<Calendar> calendar = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Calendar day = new Calendar(i, (int) (baseRate * (i>15 ? 1.25 : 1)), true);
            calendar.add(day);
        }
        return calendar;
    }

    private static Map<Integer, Room> roomsRepo = new HashMap<>();
    static {
        Room room1 = new Room();
        room1.setId(101);
        room1.setOccupancy(2);
        roomsRepo.put(room1.getId(), room1);
        room1.setCalendar(generateCalendar(100));

        Room room2 = new Room();
        room2.setId(102);
        room2.setOccupancy(2);
        roomsRepo.put(room2.getId(), room2);
        room2.setCalendar(generateCalendar(100));

        Room room3 = new Room();
        room3.setId(200);
        room3.setOccupancy(4);
        roomsRepo.put(room3.getId(), room3);
        room3.setCalendar(generateCalendar(180));
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Object> getRooms(
            @RequestParam(value = "persons", defaultValue = "0") int persons,
            @RequestParam(value = "from") Optional<Integer> from,
            @RequestParam(value = "to") Optional<Integer> to) {
        Stream<Room> s = roomsRepo.values().stream().filter(p -> p.getOccupancy() >= persons);
        if (from.isPresent() && to.isPresent()) {
            if (from.get() >= to.get()) {
                return new ResponseEntity<>("Invalid date range", HttpStatus.BAD_REQUEST);
            }
            s = s.map(r -> new Room(r.getId(), r.getOccupancy(), r.getCalendar().stream()
                    .filter(d -> d.getDay() >= from.get() && d.getDay() < to.get()).toList()));
            s = s.filter(r -> r.getCalendar().stream().allMatch(d -> d.isAvailable()));
        } else if (from.isPresent() || to.isPresent()) {
            return new ResponseEntity<>("Invalid date range", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(s.toList(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Room> get(@PathVariable("id") int id) {
        if (roomsRepo.containsKey(id)) {
            return new ResponseEntity<>(roomsRepo.get(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}/calendar/{day}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDay(@PathVariable("id") int id, @PathVariable("day") int day) {
        if (!roomsRepo.containsKey(id) ||
            roomsRepo.get(id).getCalendar() == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Optional<Calendar> oc = roomsRepo.get(id).getCalendar().stream().filter(p -> p.getDay() == day).findFirst();
        return oc.map(c -> new ResponseEntity<Object>(c, HttpStatus.OK)).
                orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/{id}/calendar/{day}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateDay(@PathVariable("id") int id, @PathVariable("day") int day, @RequestBody Calendar c) {
        Room room = roomsRepo.get(id);
        if (room == null) {
            return new ResponseEntity<>("Room not found", HttpStatus.NOT_FOUND);
        }
        List<Calendar> calendar = room.getCalendar();
        if (calendar == null) {
            return new ResponseEntity<>("Calendar not found", HttpStatus.NOT_FOUND);
        }
        Calendar dayToUpdate = calendar.stream().filter(p -> p.getDay() == day).findFirst().orElse(null);
        if (dayToUpdate == null) {
            return new ResponseEntity<>("Day not found", HttpStatus.NOT_FOUND);
        }            
        if (dayToUpdate.getDay() != c.getDay()) {
            return new ResponseEntity<>("Day mismatch", HttpStatus.BAD_REQUEST);
        } 
        dayToUpdate.setRate(c.getRate());
        dayToUpdate.setAvailable(c.isAvailable());
        return new ResponseEntity<>("Day is updated successsfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/calendar/{day}", method = RequestMethod.POST)
    public ResponseEntity<Object> createDay(@PathVariable("id") int id, @PathVariable("day") int day, @RequestBody Calendar c) {
        Room room = roomsRepo.get(id);
        if (room == null) {
            return new ResponseEntity<>("Room not found", HttpStatus.NOT_FOUND);
        }
        List<Calendar> calendar = room.getCalendar();
        if (calendar == null) {
            return new ResponseEntity<>("Calendar not found", HttpStatus.NOT_FOUND);
        }
        Calendar dayToUpdate = calendar.stream().filter(p -> p.getDay() == day).findFirst().orElse(null);
        if (dayToUpdate != null) {
            return new ResponseEntity<>("Day already exists", HttpStatus.BAD_REQUEST);
        }
        calendar.add(c);
        return new ResponseEntity<>("Day is created successsfully", HttpStatus.OK);
    }
}
