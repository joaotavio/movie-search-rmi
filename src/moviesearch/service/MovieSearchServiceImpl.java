package moviesearch.service;


import com.google.gson.*;
import moviesearch.service.Movie;
import moviesearch.service.MovieSearchService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class MovieSearchServiceImpl extends UnicastRemoteObject implements MovieSearchService {

    private static final String API_URL_FORMAT = "http://www.omdbapi.com/?s=%s";

    public MovieSearchServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public List<Movie> search(String name) throws RemoteException {
        List<Movie> movies;

        try {
            URL url = new URL(String.format(API_URL_FORMAT, name.replace(' ', '+')));

            String jsonString = getJsonFromURL(url);

            movies = jsonStringToMovies(jsonString);
        } catch (IOException e) {
            throw new RemoteException(String.format("Error trying to search for \"%s\"", name));
        }

        return movies;
    }

    private String getJsonFromURL(URL url) throws IOException {
        StringBuilder result = new StringBuilder();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString();
    }

    private List<Movie> jsonStringToMovies(String jsonString) throws IOException {
        List<Movie> movies = new ArrayList<>();

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

        boolean response = jsonObject.get("Response").getAsBoolean();

        if (!response) {
            throw new IOException("Can't find movies or tv shows with this name.");
        }

        JsonArray moviesArray = jsonObject.get("Search").getAsJsonArray();

        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingStrategy(field -> {
            if (field.getName().equals("imdbID")) {
                return field.getName();
            }

            return capitalize(field.getName());
        });

        Gson gson = builder.create();


        moviesArray.forEach(jsonElement -> {
            Movie movie = gson.fromJson(jsonElement, Movie.class);
            if (movie.getPoster() == null || movie.getPoster().equals("N/A")) {
                movie.setPoster("https://filmesonlinegratis.club/uploads/posts/2017-01/1484566660_1483537137_1463864319_no_poster.png");
            }
            movies.add(movie);
        });

        return movies;
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
