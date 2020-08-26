/**
 * Represents a Piece on the board.
 * 
 * @author Elijah Guarina
 */
public class Piece {
	private String displayIcon;
	private Location location;
	
	public Piece(String icon, Location loc) {
		this.displayIcon = icon;
		this.location = loc;
		this.location.occupied = true;
		loc.piece = this;
	}
	
	public String icon() { return displayIcon; }
	
	public Location location() { return location; }

	public void setLocation(Location loc){
		this.location.occupied = false;
		this.location.piece = null;
		this.location = loc;
		loc.occupied = true;
		loc.piece = this;
	}
}
