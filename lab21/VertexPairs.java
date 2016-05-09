
public class VertexPairs implements Comparable<VertexPairs> {
	public int startVertex;
	private double minSoFar;
	
	public VertexPairs(int vertex, double min) {
		startVertex = vertex;
		minSoFar = min;
	}

	@Override
	public int compareTo(VertexPairs o) {
		if (minSoFar == o.minSoFar) {
			return 0;
		} else if (minSoFar < o.minSoFar) {
			return -1;
		}
		return 1;
	}
}


