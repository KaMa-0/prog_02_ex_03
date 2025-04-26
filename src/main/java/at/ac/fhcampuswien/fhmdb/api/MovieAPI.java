package at.ac.fhcampuswien.fhmdb.api;

import at.ac.fhcampuswien.fhmdb.exceptions.MovieAPIException;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import okhttp3.*;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MovieAPI {
    public static final String DELIMITER = "&";
    private static final String URL = "http://prog2.fh-campuswien.ac.at/movies"; // https if certificates work
    private static final OkHttpClient client = new OkHttpClient();

    private static String buildUrl(UUID id) {
        StringBuilder url = new StringBuilder(URL);
        if (id != null) {
            url.append("/").append(id);
        }
        return url.toString();
    }

    private static String buildUrl(String query, Genre genre, String releaseYear, String ratingFrom) {
        StringBuilder url = new StringBuilder(URL);

        if ((query != null && !query.isEmpty()) ||
                genre != null || releaseYear != null || ratingFrom != null) {

            url.append("?");

            // Parameter zur URL hinzufügen
            if (query != null && !query.isEmpty()) {
                url.append("query=").append(query).append(DELIMITER);
            }
            if (genre != null) {
                url.append("genre=").append(genre).append(DELIMITER);
            }
            if (releaseYear != null) {
                url.append("releaseYear=").append(releaseYear).append(DELIMITER);
            }
            if (ratingFrom != null) {
                url.append("ratingFrom=").append(ratingFrom).append(DELIMITER);
            }
        }

        return url.toString();
    }

    public static List<Movie> getAllMovies() throws MovieAPIException {
        return getAllMovies(null, null, null, null);
    }

    public static List<Movie> getAllMovies(String query, Genre genre, String releaseYear, String ratingFrom) throws MovieAPIException {
        String url = buildUrl(query, genre, releaseYear, ratingFrom);
        Request request = new Request.Builder()
                .url(url)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "http.agent")  // benötigt für Server-Akzeptanz
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MovieAPIException("API-Antwort nicht erfolgreich. Status-Code: " + response.code());
            }

            String responseBody = response.body().string();
            Gson gson = new Gson();
            Movie[] movies = gson.fromJson(responseBody, Movie[].class);

            return Arrays.asList(movies);
        } catch (Exception e) {
            throw new MovieAPIException("Fehler beim Abrufen der Filme von der API", e);
        }
    }

    public static Movie requestMovieById(UUID id) throws MovieAPIException {
        String url = buildUrl(id);
        Request request = new Request.Builder()
                .url(url)
                .removeHeader("User-Agent")
                .addHeader("User-Agent", "http.agent")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new MovieAPIException("API-Antwort nicht erfolgreich. Status-Code: " + response.code());
            }

            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), Movie.class);
        } catch (Exception e) {
            throw new MovieAPIException("Fehler beim Abrufen des Films mit ID " + id, e);
        }
    }
}