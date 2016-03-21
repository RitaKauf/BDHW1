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
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
    	boolean rtn;
    	rtn = scanner.hasNext();
    	if(!rtn)
    		scanner.close();
    	return rtn;
    }

    @Override
    public MovieReview getMovie() {
        //throw new UnsupportedOperationException("You have to implement this method on your own.");
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
    	
    	review = scanner.nextLine();
    	if(review==null){
		throw new UnsupportedOperationException("Null review");
	}
    	String[] parts =review.split("\\t+");
    	 for (int i=0;i< parts.length ; i++){
    		 parts[i] = parts[i].split("(product/productId: )|(review/userId: )|(review/profileName: )|(review/helpfulness: )|(review/score: )|(review/time: )|(review/text: )|(review/summary: )")[1];
    	 }
    	

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
    	time = new Date(Integer.parseInt(strTime));
    	summary = parts[6];
    	if(summary==null){
    		throw new UnsupportedOperationException("Null summary");
    	}
    	text = parts[7];
    	movie = new Movie(productId,score);
    	moviereview = new MovieReview(movie,userId,profileName,helpfulness,time,summary,text);
    	return moviereview;
    }
}
