package moviesearch.service;


import com.google.gson.*;

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

public class MovieSearchService extends UnicastRemoteObject implements MovieSearch {

    private static final String API_URL_FORMAT = "http://www.omdbapi.com/?s=%s";

    protected MovieSearchService() throws RemoteException {
        super();
    }

    @Override
    public List<Movie> search(String name) throws RemoteException {
        List<Movie> movies;

        try {
            URL url = new URL(String.format(API_URL_FORMAT, name));

            String jsonString = getJsonFromURL(url);

            movies = jsonStringToMovies(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return result.toString();
    }

    private List<Movie> jsonStringToMovies(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonString).getAsJsonObject();

        JsonArray moviesArray = jsonObject.get("Search").getAsJsonArray();

        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingStrategy(field -> {
            if (field.getName().equals("imdbID")) {
                return field.getName();
            }

            return capitalize(field.getName());
        });

        Gson gson = builder.create();

        List<Movie> movies = new ArrayList<>();
        moviesArray.forEach(jsonElement -> {
            Movie movie = gson.fromJson(jsonElement, Movie.class);
            movies.add(movie);
        });

        return movies;
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
