package univ.bigdata.course.movie;

import java.util.Comparator;

public class Movie implements Comparator<Movie>{

    private String productId;

    private double score;

    public Movie() {
    }

    public Movie(String productId, double score) {
        this.productId = productId;
        this.score = score;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "productId='" + productId + '\'' +
                ", score=" + score +
                '}';
    }

	@Override
	public int compare(Movie o1, Movie o2) {
		if(o1.score == o2.score)
		{
			return o1.productId.compareTo(o2.productId);
		}
		if(o1.score>o2.score)
			return -1;
		else
			return 1;
	}

}
