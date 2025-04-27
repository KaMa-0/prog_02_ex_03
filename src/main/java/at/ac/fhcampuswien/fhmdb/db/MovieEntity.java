package at.ac.fhcampuswien.fhmdb.db;

import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;
@DatabaseTable(tableName = "movies") //Annotation zeigt an, dass diese Klasse einer Datenbanktabelle namens "movies" entspricht.
public class MovieEntity {
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField //eine Spalte in der Datenbanktabelle
    private String apiId;
    @DatabaseField
    private String title;
    @DatabaseField
    private String description;
    @DatabaseField
    private String genres;
    @DatabaseField
    private int releaseYear;
    @DatabaseField
    private String imgUrl;
    @DatabaseField
    private int lengthInMinutes;
    @DatabaseField
    private double rating;

    public MovieEntity() {} //parameterlose Konstruktor ist für ORMLite erforderlich, damit die Bibliothek Objekte aus der Datenbank erstellen kann.
    public MovieEntity(Movie movie){ //nimmt ein Movie-Objekt und konvertiert es in ein MovieEntity-Objekt.
        this.apiId = movie.getId();
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.genres = genresToString(movie.getGenres()); //Die genres-Liste wird mit der Methode genresToString in eine kommagetrennte Zeichenkette umgewandelt, da wir in der Datenbank keine direkten Listen speichern können.
        this.releaseYear = movie.getReleaseYear();
        this.imgUrl = movie.getImgUrl();
        this.lengthInMinutes = movie.getLengthInMinutes();
        this.rating = movie.getRating();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getApiId() {
        return apiId;
    }

    public Movie toMovie(){ //Diese Methode konvertiert ein MovieEntity-Objekt zurück in ein Movie-Objekt, nimmt die Werte aus dem Entity-Objekt und erstellt ein neues Movie-Objekt.
                            //Diese Methode ist wichtig, um Daten aus der Datenbank in ein Format zu bringen, das in der Anwendungsschicht verwendet werden kann.
        return new Movie(this.apiId, this.title, this.description, stringToGenres(this.genres), this.releaseYear, this.imgUrl, this.lengthInMinutes, this.rating);
    }

    public String genresToString(List<Genre> genres) {
        // Null- oder Leerprüfung
        if (genres == null || genres.isEmpty()) return "";

        StringBuilder genreString = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            genreString.append(genres.get(i));
            // Kein Komma nach dem letzten Element hinzufügen
            if (i < genres.size() - 1) {
                genreString.append(",");
            }
        }
        return genreString.toString();
    }

    private List<Genre> stringToGenres(String genres) {
        // Null- oder Leerprüfung
        if (genres == null || genres.isEmpty()) {
            return new ArrayList<>();
        }

        List<Genre> genreList = new ArrayList<>();

        // Teilt die kommagetrennte Zeichenkette
        String[] genreArray = genres.split(",");

        // Konvertiert jedes Element
        for (String genreStr : genreArray) {
            // Überprüfung auf leere Zeichenkette (könnte nach dem letzten Komma vorkommen)
            if (!genreStr.isEmpty()) {
                try {
                    Genre genre = Genre.valueOf(genreStr.trim());
                    genreList.add(genre);
                } catch (IllegalArgumentException e) {
                    // Überspringt ungültige Genre-Werte
                    System.err.println("Ungültiger Genre-Wert: " + genreStr);
                }
            }
        }

        return genreList;
    }

    public static List<MovieEntity> fromMovies(List<Movie> movies) {
        // Null-Prüfung
        if (movies == null || movies.isEmpty()) {
            return new ArrayList<>();
        }
        List<MovieEntity> movieEntities = new ArrayList<>();
        // Jedes Movie-Objekt in ein MovieEntity-Objekt umwandeln
        for (Movie movie : movies) {
            if (movie != null) {
                MovieEntity entity = new MovieEntity(movie);
                movieEntities.add(entity);
            }
        }
        return movieEntities;
    }

    public static List<Movie> toMovies(List<MovieEntity> movieEntities) {
        // Null-Prüfung
        if (movieEntities == null || movieEntities.isEmpty()) {
            return new ArrayList<>();
        }
        List<Movie> movies = new ArrayList<>();

        // Jedes MovieEntity-Objekt in ein Movie-Objekt umwandeln
        for (MovieEntity entity : movieEntities) {
            if (entity != null) {
                Movie movie = entity.toMovie();
                movies.add(movie);
            }
        }
        return movies;
    }
}
