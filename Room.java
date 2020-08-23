import java.util.*;

/**
 * The Room class represents the various Rooms on the Cluedo
 * board.
 *
 * @author Jared Boult
 */
public class Room {
    private String name;
    private Set<Location> locations = new HashSet<>();
    private Set<Location> entrances = new HashSet<>();
    private Set<Location> exits = new HashSet<>();

    /**
     * Constructor for the Room class
     *
     * @param name The name of the Room
     */
    Room(String name){
        this.name = name;
    }

    /**
     * Getter for the name of the Room
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public Set<Location> getEntrances() {
        return entrances;
    }

    public Set<Location> getUnoccupiedExits() {
        Set<Location> unoccupiedExits = new HashSet<>();
        for(Location loc : exits){
            if(!loc.occupied){
                unoccupiedExits.add(loc);
            }
        }
        return unoccupiedExits;
    }

    public boolean playerCanRoll(){
        // Can always roll when in the hallway
        if(name.equals("Passageway")){
            return true;
        }
        // Cannot roll if all exits out of the room are blocked
        for(Location loc : exits){
            if(!loc.occupied){
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a Location in the Set of Locations that
     * belong to this Room.
     *
     * @param location A Location that exists within this Room
     */
    public void addLocation(Location location){
        locations.add(location);
    }

    /**
     * Adds a Location in the Set of Entrances that
     * belong to this Room.
     *
     * @param location A Location in the Room that is has a neighbouring square in
     *                 the Passageway it can access.
     */
    public void addEntrance(Location location){
        entrances.add(location);
    }

    /**
     * Adds a Location in the Set of Exits that
     * belong to this Room.
     *
     * @param location A Location in the Passageway that has a neighbouring
     *                 square it can access inside this Room.
     */
    public void addExit(Location location){
        exits.add(location);
    }

    /**
     * Get a random unoccupied Location within this Room, usually
     * this is called when a Piece is suggested and needs to move
     * to this Room.
     *
     * @return - A random unoccupied Location
     */
    public Location getRandomRoomLocation(){
        List<Location> allLocations = new ArrayList<>(locations);
        Collections.shuffle(allLocations);
        for (Location loc : allLocations) {
            if (!loc.occupied && !entrances.contains(loc)) {
                return loc;
            }
        }
        return null;
    }
}