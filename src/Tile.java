
enum Tile {
	WALL(0),
	WALKABLE(1),
	BOX(2),
	GOAL(3),
	TEMP_WALKABLE(5);
	
	private final int intRep;
	Tile(int intRep) {
		this.intRep = intRep;
	}
	
	public int getIntRep() {
		return this.intRep;
	}
}