package univ.bigdata.course;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;
import univ.bigdata.course.providers.MoviesProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Main class which capable to keep all information regarding movies review.
 * Has to implements all methods from @{@link IMoviesStorage} interface.
 * Also presents functionality to answer different user queries, such as:
 * <p>
 * 1. Total number of distinct movies reviewed.
 * 2. Total number of distinct users that produces the review.
 * 3. Average review score for all movies.
 * 4. Average review score per single movie.
 * 5. Most popular movie reviewed by at least "K" unique users
 * 6. Word count for movie review, select top "K" words
 * 7. K most helpful users
 */
public class MoviesStorage implements IMoviesStorage {
	
	Map<String, List<MovieReview>> moviereviews = new HashMap<String, List<MovieReview>>();
	private int numberOfMovieReviews = 0;
	Map <String, List<String>> helpfulnessUsers = new HashMap <String, List<String>>();

    public MoviesStorage(final MoviesProvider provider) {
        //TODO: read movies using provider interface
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    	MovieReview review;
    	List<MovieReview> listOfMovie;
    	List <String> helpfulness;
    	while(provider.hasMovie())
    	{
    		review = provider.getMovie();
    		listOfMovie = moviereviews.get(review.getMovie().getProductId());
    		helpfulness = helpfulnessUsers.get(review.getUserId());
    		if(listOfMovie == null)
    		{
    			listOfMovie = new ArrayList<MovieReview>();
    			listOfMovie.add(review);
    			moviereviews.put(review.getMovie().getProductId(), listOfMovie);
    		}
    		else
    			listOfMovie.add(review);
    		if(helpfulness == null)
    		{
    			helpfulness = new ArrayList<String>();
    			helpfulness.add(review.getHelpfulness());
    			helpfulnessUsers.put(review.getUserId(), helpfulness);
    		}
    		else
    			helpfulness.add(review.getHelpfulness());
    		numberOfMovieReviews++;
    	}
    }

    @Override
    public double totalMoviesAverageScore() {
    	double score = 0; 
    	for (Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) {
            List<MovieReview> reviews = entry.getValue();
            for(MovieReview review : reviews)
            	score+=review.getMovie().getScore();
        }
    	if(numberOfMovieReviews==0)
    		throw new UnsupportedOperationException("The movie reviews = null");
        return round(score/numberOfMovieReviews,5);
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public double totalMovieAverage(String productId) {
    	double totalAverage = 0;
    	int count = 0;
    	List<MovieReview> reviews = moviereviews.get(productId);
        for(MovieReview review : reviews)
        {
        	totalAverage+=review.getMovie().getScore();
        	count++;
        }
        return round(totalAverage/count,5);
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

	@Override
    public List<Movie> getTopKMoviesAverage(long topK) {
    	List<Movie> movieList = new ArrayList<Movie>();
    	List<Movie>returnList = new ArrayList<Movie>();
        for (Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) 
        {
        	double averageScore = 0;
        	String key = entry.getKey();
        	averageScore = totalMovieAverage(key);
        	movieList.add(new Movie(key,averageScore));
        }
       Collections.sort(movieList, new Movie());
        for(int i=0;i<topK;i++)
        	returnList.add(movieList.get(i));
    	return returnList;
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public Movie movieWithHighestAverage() {
    	
    	return getTopKMoviesAverage(1).get(0);
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public List<Movie> getMoviesPercentile(double percentile) {
    	long moviesInTotal = moviesCount();
    	int moviesToPresent = (int)Math.ceil((double)((double)moviesInTotal/100) * (100- percentile));   	
		List<Movie>returnList = getTopKMoviesAverage(moviesToPresent);
		return returnList;
        //throw new UnsupportedOperationException("You have to implement this method on your own.");		       
    }

    @Override
    public String mostReviewedProduct() {
    	Map<String, Long> mostPopularMovieReviewed;
		mostPopularMovieReviewed =  reviewCountPerMovieTopKMovies(1);
		Map.Entry<String,Long> entry = null;
	
		entry=mostPopularMovieReviewed.entrySet().iterator().next();
		if(entry==null){
		throw new UnsupportedOperationException("No movie with Review ");
		}
		
		return entry.getKey();
    
    }

    @Override
    public Map<String, Long> reviewCountPerMovieTopKMovies(int topK) {
    	List<Movie> movieList = new ArrayList<Movie>();

    	Map<String, Long>returnMap = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) 
        {
        	double countReview = 0;
        	String key = entry.getKey();
        	List<MovieReview> reviews = moviereviews.get(key);
        	countReview = reviews.size();
        	movieList.add(new Movie(key,countReview));
        }
        Collections.sort(movieList, new Movie());
        
        if(movieList.size()< topK)
        	topK = movieList.size();
        for(int i=0;i<topK;i++)
        returnMap.put(movieList.get(i).getProductId(),(long) movieList.get(i).getScore());
        
    	return returnMap;
//        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

   

	@Override
    public String mostPopularMovieReviewedByKUsers(int numOfUsers) {

		List <Movie> moviesHighScore = getTopKMoviesAverage(moviesCount());
		
		for(Movie tempMovie: moviesHighScore)
		{
			int numOfRev = moviereviews.get(tempMovie.getProductId()).size();
			if(numOfRev >= numOfUsers)
				return tempMovie.getProductId();
		}

		return null;

    }

    @Override
    public Map<String, Long> moviesReviewWordsCount(int topK) {
    	 Map<String, Long> map = new LinkedHashMap<>();
    	 
    	 //go over all each movie review 
    	 for (Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) {
             List<MovieReview> reviews = entry.getValue();
             
             //for each review - get all of its words
             for(MovieReview review : reviews){
            	 String[] words= review.getReview().split("\\s");   	    
				for (String w : words) {
	    	        Long n = map.get(w);
	    	        //increase the word counter if this word has already appeared before
	    	        n = (n == null) ? 1 : ++n;
	    	        map.put(w, n);
	    	    }
			
             }
    	 }
    	 
    	 //sort the map of words by their number of appearances. 
    	 List<Map.Entry<String, Long>> wordsCount = new ArrayList<Map.Entry<String, Long>>(map.entrySet());
    	 Collections.sort(wordsCount, new Comparator<Map.Entry<String, Long>>() {
    		  public int compare(Map.Entry<String, Long> a, Map.Entry<String, Long> b){
    			 if(a.getValue().equals(b.getValue()))
    				{
    				 return a.getKey().compareTo(b.getKey());
    				}
    			 if (a.getValue().longValue() > b.getValue().longValue())
   					return -1;
    			 else 
    			 	return 1;
    				 
    		 }
    		});
    	 
    	 //limit the map to top k words.
    	 Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
         for(int i=0;i<topK;i++){
           	sortedMap.put(wordsCount.get(i).getKey(), wordsCount.get(i).getValue());
         }
    	 if(sortedMap!=null)
    		 return sortedMap;
    	 
    	 else 
    		 throw new UnsupportedOperationException("You have to implement this method on your own.");
    	 
    }

       @Override
    public Map<String, Long> topYMoviewsReviewTopXWordsCount(int topMovies, int topWords) {
    	//mapToReturn = the final map that will be returned
    	Map<String,Long> mapToReturn= new LinkedHashMap<String,Long>();
    	// mReviewedMovies = top topWords movies and their number of reviews
    	Map<String, Long> mReviewedMovies = reviewCountPerMovieTopKMovies(topMovies);// mReviewedMovies = top topWords movies and their number of reviews
    	
    	Map<String,Long> mapToReturnPreSorting= new LinkedHashMap<String,Long>();
    	
    	//goes over the reviews and adds each word to mapToReturnPreSorting
    	for(Map.Entry<String, Long> entry : mReviewedMovies.entrySet()) {
    		
    		List<MovieReview> reviewsForMovie = moviereviews.get(entry.getKey());
    		
            for(MovieReview review : reviewsForMovie){
           	 String[] words= review.getReview().split("\\s");   	    
				for (String w : words) {//goes over the words in the review
	    	        Long n = mapToReturnPreSorting.get(w);
	    	        n = (n == null) ? 1 : ++n;//adding to the appearances number of that word
	    	        mapToReturnPreSorting.put(w, n);
	    	    }
			
            }

    	}
    	
		//sorting the words by appearances number of each word in mapToReturnPreSorting
		List<Map.Entry<String, Long>> wordsCount = new ArrayList<Map.Entry<String, Long>>(mapToReturnPreSorting.entrySet());
   	 	Collections.sort(wordsCount, new Comparator<Map.Entry<String, Long>>() {
   		  public int compare(Map.Entry<String, Long> a, Map.Entry<String, Long> b){
   			 if(a.getValue().equals(b.getValue()))
   				{
   				 return a.getKey().compareTo(b.getKey());
   				}
   			 if (a.getValue().longValue() > b.getValue().longValue())
  					return -1;
   			 else 
   			 	return 1;
   				 
   		 }
   		});
   	 	int tWordsTemp = 0;
   	 	if(wordsCount.size() < topWords)
   	 		tWordsTemp = wordsCount.size();
   	 	else 
   	 		tWordsTemp = topWords;
   	 	
   	 	//resizing wordsCount so that it'll contain only 'topWords' words
        for(int i=0;i<tWordsTemp;i++){
        	
        	mapToReturn.put(wordsCount.get(i).getKey(), wordsCount.get(i).getValue());
        }
    	
    	return mapToReturn;
    }

    @SuppressWarnings("unused")
	@Override
    public Map<String, Double> topKHelpfullUsers(int k) {
    	//return null;
    	Map<String, Double> list = new HashMap<>();
    	int count = 0;
    	int score = 0;
    	for (Map.Entry<String, List<String>> entry : helpfulnessUsers.entrySet()) 
        {
    		count = 0;
    		score = 0;
    		for(String helpfulness : entry.getValue())
    		{
    			String[] parts = helpfulness.split("/");
    			score+= Integer.parseInt(parts[0]);
    			count+= Integer.parseInt(parts[1]);
    		}
    		if(count!=0)
    			list.put(entry.getKey(),Double.valueOf(round((double)score/count, 5)));
    		
        }
    	List<Map.Entry<String, Double>> sortList=new ArrayList<Map.Entry<String, Double>>(list.entrySet());
    	Collections.sort(sortList, new Comparator<Map.Entry<String, Double>>() {
  		  public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b){
  			 if(a.getValue().doubleValue() == b.getValue().doubleValue())
  			{
  				 return a.getKey().compareTo(b.getKey());
  			}
  			if (a.getValue().doubleValue() > b.getValue().doubleValue())
 				return -1;
  			else 
  			 	return 1;
  				 
  		 }
  		});
    	Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
    	count = 0;
    	for(Map.Entry<String, Double> entry : sortList)
    	{
    		if(count>=k)
    			break;
    		sortedMap.put(entry.getKey(),entry.getValue());
    		count++;
    	}
    	 if(sortedMap!=null)
    		 return sortedMap;
    	 else 
    		 throw new UnsupportedOperationException("List topKHelpfullUsers is null.");
    	 }

    @Override
    public long moviesCount() {
    	long Count = 0;
   	 	for (@SuppressWarnings("unused") Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) {
    		Count++;
        }
    	return Count;
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
