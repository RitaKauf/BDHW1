package univ.bigdata.course;

import univ.bigdata.course.providers.FileIOMoviesProvider;
import univ.bigdata.course.providers.MoviesProvider;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class MoviesReviewsQueryRunner {

    public static void main(String[] args) {
   
        //TODO: Here you need to add the part of reading input parameters
        // opening stream for writing the output and validating.
    	String inputFileName = null,outputFileName = null;
    	//Find the names of input and output files from String[] args
    	
    
    	inputFileName = args[0].split("(-inputFile=)")[1];
    	inputFileName = "src/main/resources/" + inputFileName;
    	outputFileName = args[1].split("(-outputFile=)")[1];
    	
       	 
    	
    	//Check if all parameters is insert
    	if(outputFileName == null || inputFileName == null)
    	{
    		throw new UnsupportedOperationException("Error: no output files or input files");
    	}
    	
        PrintStream printer = new PrintStream(System.out);;
		/*try {
			printer = new PrintStream(outputFileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}*/
        try{
            final MoviesProvider provider = new FileIOMoviesProvider(inputFileName);
            final IMoviesStorage storage = new MoviesStorage(provider);

            printer.println("Getting list of total movies average.");
            // 1.
            printer.println();
            printer.println("TOP2.");
            storage.getTopKMoviesAverage(2).stream().forEach(printer::println);
            printer.println();
            printer.println("TOP4.");
            storage.getTopKMoviesAverage(4).stream().forEach(printer::println);
		
            // 2.
            printer.println("Total average: " + storage.totalMoviesAverageScore());

            // 3.`
            printer.println();
            printer.println("The movie with highest average:  " + storage.movieWithHighestAverage());

            // 4.
            printer.println();
            storage.reviewCountPerMovieTopKMovies(4)
                    .entrySet()
                    .stream()
                    .forEach(pair -> printer.println("Movie product id = [" + pair.getKey() + "], reviews count [" + pair.getValue() + "]."));

            // 5.
           printer.println();
            printer.println("The most reviewed movie product id is " + storage.mostReviewedProduct());

            // 6.
                        printer.println();
//            printer.println("Computing 90th percentile of all movies average.");
//            storage.getMoviesPercentile(90).stream().forEach(printer::println);
//             
//            printer.println();
//            printer.println("Computing 50th percentile of all movies average.");
//            storage.getMoviesPercentile(50).stream().forEach(printer::println);

            // 7.
            printer.println();
            printer.println("Computing TOP100 words count");
            storage.moviesReviewWordsCount(100)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));
			
            /*     // 8.
            printer.println();
            printer.println("Computing TOP100 words count for TOP100 movies");
            storage.topYMoviewsReviewTopXWordsCount(100, 100)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));

            printer.println("Computing TOP100 words count for TOP10 movies");
            storage.topYMoviewsReviewTopXWordsCount(100, 10)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));

            // 9.
            printer.println();
            printer.println("Most popular movie with highest average score, reviewed by at least 20 users " + storage.mostPopularMovieReviewedByKUsers(20));
            printer.println("Most popular movie with highest average score, reviewed by at least 15 users " + storage.mostPopularMovieReviewedByKUsers(15));
            printer.println("Most popular movie with highest average score, reviewed by at least 10 users " + storage.mostPopularMovieReviewedByKUsers(10));
            printer.println("Most popular movie with highest average score, reviewed by at least 5 users " + storage.mostPopularMovieReviewedByKUsers(5));

            // 10.
            printer.println();
            printer.println("Compute top 10 most helpful users.");
            storage.topKHelpfullUsers(10)
                    .entrySet()
                    .forEach(pair -> printer.println("User id = [" + pair.getKey() + "], helpfulness [" + pair.getValue() + "]."));

            printer.println();
            printer.println("Compute top 100 most helpful users.");
            storage.topKHelpfullUsers(100)
                    .entrySet()
                    .forEach(pair -> printer.println("User id = [" + pair.getKey() + "], helpfulness [" + pair.getValue() + "]."));
                    */
            // 11.
            printer.println();
            printer.println("Total number of distinct movies reviewed [" +storage.moviesCount() + "].");
            printer.println("THE END.");
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }
}
