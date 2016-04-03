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
    		//get Movie review from input 
    		review = provider.getMovie();
    		//Get movie review list for this movie by product id 
    		listOfMovie = moviereviews.get(review.getMovie().getProductId());
    		helpfulness = helpfulnessUsers.get(review.getUserId());
    		//Add this review to the list , if the list is empty creat it and add this review
    		if(listOfMovie == null)
    		{
    			listOfMovie = new ArrayList<MovieReview>();
    			listOfMovie.add(review);
    			moviereviews.put(review.getMovie().getProductId(), listOfMovie);
    		}
    		else
    			listOfMovie.add(review);
    		//Add the helpfulness to the list , if the list is empty create it and add this review
    		if(helpfulness == null)
    		{
    			helpfulness = new ArrayList<String>();
    			helpfulness.add(review.getHelpfulness());
    			helpfulnessUsers.put(review.getUserId(), helpfulness);
    		}
    		else
    			helpfulness.add(review.getHelpfulness());
    		// increase review number by 1
    		numberOfMovieReviews++;
    	}
    }

    @Override
    public double totalMoviesAverageScore() {
    	//initiate score to 0;
    	double score = 0; 
    	//for each movie , get review list , and sum all reviews 
    	for (Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) {
            List<MovieReview> reviews = entry.getValue();
            for(MovieReview review : reviews)
            	score+=review.getMovie().getScore();
        }
    	if(numberOfMovieReviews==0)
    		throw new UnsupportedOperationException("The movie reviews = null");
    	//retrun score/ numberOfMovieReviews which holds how many reviews there are
        return round(score/numberOfMovieReviews,5);
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public double totalMovieAverage(String productId) {
    	double totalAverage = 0;
    	int count = 0;
    	//for each review , add review score to totalAverage and count how many there are in count
    	List<MovieReview> reviews = moviereviews.get(productId);
        for(MovieReview review : reviews)
        {
        	totalAverage+=review.getMovie().getScore();
        	count++;
        }
        //return average
        return round(totalAverage/count,5);
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

	@Override
    public List<Movie> getTopKMoviesAverage(long topK) {
		// create one list for all movies and another list for topK which called return list
    	List<Movie> movieList = new ArrayList<Movie>();
    	List<Movie>returnList = new ArrayList<Movie>();
    	//For each movie , add name and average score to movieList
        for (Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) 
        {
        	double averageScore = 0;
        	String key = entry.getKey();
        	averageScore = totalMovieAverage(key);
        	movieList.add(new Movie(key,averageScore));
        }
        //Sort this list  and add topK to  returnList and return it
       Collections.sort(movieList, new Movie());
        for(int i=0;i<topK;i++)
        	
        	returnList.add(movieList.get(i));
    	return returnList;
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public Movie movieWithHighestAverage() {
    	//Call getTopKMoviesAverage with 1 
    	return getTopKMoviesAverage(1).get(0);
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public List<Movie> getMoviesPercentile(double percentile) {
    	long moviesInTotal = moviesCount();
    	//total number of movies
    	int moviesToPresent = (int)Math.ceil((double)((double)moviesInTotal/100) * (100- percentile)); 
    	//one percent of movies(rounded up) multiplied by the percentile required which is the reduction of percentile from 100%
		List<Movie>returnList = getTopKMoviesAverage(moviesToPresent);
		//return list creation and assigned with the movies in the percentile required. 
		return returnList;
        //throw new UnsupportedOperationException("You have to implement this method on your own.");		       
    }

    @Override
    public String mostReviewedProduct() {
    	Map<String, Long> mostPopularMovieReviewed;
    	// Get Top reviewd movie by calling reviewCountPerMovieTopKMovies with K=1
		mostPopularMovieReviewed =  reviewCountPerMovieTopKMovies(1);
		Map.Entry<String,Long> entry = null;
		
		//get the movie form the map returned and return it
		entry=mostPopularMovieReviewed.entrySet().iterator().next();
		if(entry==null){
		throw new UnsupportedOperationException("No movie with Review ");
		}
		
		return entry.getKey();
    
    }

    @Override
    public Map<String, Long> reviewCountPerMovieTopKMovies(int topK) {
    	//List for all movies which we can sort
    	List<Movie> movieList = new ArrayList<Movie>();
    	// Linked Hash Map that holds the Top K Movies
    	Map<String, Long>returnMap = new LinkedHashMap<String, Long>();
    	//for each movie, get reviews as list and count them , then add 
    	// movie name and reviews size to movieList
        for (Map.Entry<String, List<MovieReview>> entry : moviereviews.entrySet()) 
        {
        	double countReview = 0;
        	String key = entry.getKey();
        	List<MovieReview> reviews = moviereviews.get(key);
        	countReview = reviews.size();
        	movieList.add(new Movie(key,countReview));
        }
        //Sort movie list 
        Collections.sort(movieList, new Movie());
        //check if there are enough movies as K asked for , if not change K to be all movies rated
        if(movieList.size()< topK)
        	topK = movieList.size();
        //add topK movies to Linked Hash Map , and return it
        for(int i=0;i<topK;i++)
        returnMap.put(movieList.get(i).getProductId(),(long) movieList.get(i).getScore());
        
    	return returnMap;
//        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

   

	@Override
    public String mostPopularMovieReviewedByKUsers(int numOfUsers) {
		// Create a list for all movies sorted by average
		List <Movie> moviesHighScore = getTopKMoviesAverage(moviesCount());
		// then start form top and search for the movie reviewd by numOfUsers
		//and then return it
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
    	// for each movie review check  the list of users reviewd 
    	for (Map.Entry<String, List<String>> entry : helpfulnessUsers.entrySet()) 
        {
    		count = 0;
    		score = 0;
    		// for each review count score out of total for this review
    		for(String helpfulness : entry.getValue())
    		{
    			String[] parts = helpfulness.split("/");
    			score+= Integer.parseInt(parts[0]);
    			count+= Integer.parseInt(parts[1]);
    		}
    		// add user name and average score given
    		if(count!=0)
    			list.put(entry.getKey(),Double.valueOf(round((double)score/count, 5)));
    		
        }
    	// sort list by score and then by name
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
    //create new LinkedHashMap and add top K users to it 
    	Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
    	count = 0;
    	//Get top K users from sortedMap
    	for(Map.Entry<String, Double> entry : sortList)
    	{
    		if(count>=k)
    			break;
    		sortedMap.put(entry.getKey(),entry.getValue());
    		count++;
    	}
    	//return sortedMap with top K helpfull users
    	 if(sortedMap!=null)
    		 return sortedMap;
    	 else 
    		 throw new UnsupportedOperationException("List topKHelpfullUsers is null.");
    	 }

    @Override
    public long moviesCount() {
    	long Count = 0;
    	// Return movies count after counting them
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
