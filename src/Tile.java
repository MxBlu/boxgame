
enum Tile {
	// representative integers of a specific tile
	WALL(0),
	WALKABLE(1),
	BOX(2),
	GOAL(3),
	PLAYER(4),
	TEMP_WALKABLE(5),
	ANY(6),
	BORDER(7);
	
	private final int intRep;
	
	/**
	 * Represents a coordinate on the playable level
	 */
	Tile(int intRep) {
		this.intRep = intRep;
	}
	
	/**
	 * @return the tile representative number
	 */
	public int getIntRep() {
		return this.intRep;
	}
	
	/**
	 * Checks if the given number is representative of this tile
	 * @param intRep
	 * @return the tile t if it is representative of intRep
	 * Otherwise, returns false
	 */
	public static Tile getTile(int intRep) {
		for (Tile t : values())
			if (t.intRep == intRep)
				return t;
		
		return null;
	}	
}