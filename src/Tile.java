
enum Tile {
	WALL(0),
	WALKABLE(1),
	BOX(2),
	GOAL(3),
	PLAYER(4),
	TEMP_WALKABLE(5),
	ANY(6);
	
	private final int intRep;
	Tile(int intRep) {
		this.intRep = intRep;
	}
	
	public int getIntRep() {
		return this.intRep;
	}
	
	public static Tile getTile(int intRep) {
		for (Tile t : values())
			if (t.intRep == intRep)
				return t;
		
		return null;
	}	
}