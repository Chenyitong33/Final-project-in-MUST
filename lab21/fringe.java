
public class fringe implements Comparable<fringe> {
	public int startVertex;
	private double minSoFar;
	
	public fringe(int vertex, double min) {
		startVertex = vertex;
		minSoFar = min;
	}

	@Override
	public int compareTo(fringe o) {
		if (minSoFar == o.minSoFar) {
			return 0;
		} else if (minSoFar < o.minSoFar) {
			return -1;
		}
		return 1;
	}
}

