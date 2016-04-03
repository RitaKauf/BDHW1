package univ.bigdata.course.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;

public class FileIOMoviesProvider implements MoviesProvider {
	
	private String inputFileName = null;
	private Scanner scanner = null;
	
	public FileIOMoviesProvider(final String inputfilename)
	{
		inputFileName = inputfilename;
		
		File file = new File(inputFileName);
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public boolean hasMovie() {
       // While has movie is true , get next movie review 
    	boolean rtn;
    	rtn = scanner.hasNext();
    	if(!rtn)
    		//If no movie revies left , close scanner
    		scanner.close();
    	return rtn;
    }

    @Override
    public MovieReview getMovie() {
        //initialise all arguments with the starting string , so we can search for them in the review
    	MovieReview moviereview = null;
    	Movie movie;
    	double score;
    	Date time;
    	String review = null;
    	String productId = "product/productId: ";
    	String userId = "	review/userId: ";
    	String profileName = "	review/profileName: ";
    	String helpfulness = "	review/helpfulness: ";
    	String strScore = "6";
    	String strTime = "1042588800";
    	String summary = "	review/summary: ";
    	String text = "	review/text: ";
    	
    	//Get next line from input
    	review = scanner.nextLine();
    	if(review==null){
		throw new UnsupportedOperationException("Null review");
	}
    	//split review line by tabs and save them into parts 
    	String[] parts =review.split("\\t+");
    	//for each part split with the relevant agruments start and take second part
    	 for (int i=0;i< parts.length ; i++){
    		 parts[i] = parts[i].split("(product/productId: )|(review/userId: )|(review/profileName: )|(review/helpfulness: )|(review/score: )|(review/time: )|(review/text: )|(review/summary: )")[1];
    	 }
    	
    	 //Check that there are no null or empty parts
    	productId = parts[0];
    	if(productId==null){
    		throw new UnsupportedOperationException("Null productId");
    	}
    	userId = parts[1];
    	if(userId==null){
    		throw new UnsupportedOperationException("Null userId");
    	}
    	profileName = parts[2];
    	if(profileName==null){
    		throw new UnsupportedOperationException("Null profileName");
    	}
    	helpfulness = parts[3];
    	if(helpfulness==null){
    		throw new UnsupportedOperationException("Null helpfulness");
    	}
    	
    	strScore = parts[4];
    	if(strScore==null){
    		throw new UnsupportedOperationException("Null strScore");
    	}
    	score = Double.parseDouble(strScore);
    	strTime = parts[5];
    	if(strTime==null){
    		throw new UnsupportedOperationException("Null strTime");
    	}
    	//Parse time 
    	time = new Date(Integer.parseInt(strTime));
    	summary = parts[6];
    	if(summary==null){
    		throw new UnsupportedOperationException("Null summary");
    	}
    	text = parts[7];
    	//Create a movie 
    	movie = new Movie(productId,score);
    	// Create a movie review and return it.
    	moviereview = new MovieReview(movie,userId,profileName,helpfulness,time,summary,text);
    	return moviereview;
    }
}
