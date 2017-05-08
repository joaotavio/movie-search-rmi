package moviesearch.server;


import moviesearch.service.MovieSearchServiceImpl;
import moviesearch.service.MovieSearchService;

import java.rmi.Naming;

public class MovieSearchServer {

    public MovieSearchServer() {
        try {
            MovieSearchService movieSearchService = new MovieSearchServiceImpl();
            Naming.rebind("//localhost/MovieSearchService", movieSearchService);

            System.out.println("Server started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MovieSearchServer();
    }
}
