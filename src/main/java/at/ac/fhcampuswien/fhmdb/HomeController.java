package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.api.MovieAPI;
import at.ac.fhcampuswien.fhmdb.db.DatabaseManager;
import at.ac.fhcampuswien.fhmdb.db.MovieRepository;
import at.ac.fhcampuswien.fhmdb.db.WatchlistRepository;
import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.exceptions.MovieAPIException;
import at.ac.fhcampuswien.fhmdb.models.Genre;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.models.SortedState;
import at.ac.fhcampuswien.fhmdb.ui.MovieCell;
import at.ac.fhcampuswien.fhmdb.ui.ClickEventHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomeController implements Initializable {
    @FXML
    public JFXButton searchBtn;

    @FXML
    public TextField searchField;

    @FXML
    public JFXListView<Movie> movieListView;

    @FXML
    public JFXComboBox<Object> genreComboBox;

    @FXML
    public JFXComboBox<Object> releaseYearComboBox;

    @FXML
    public JFXComboBox<Object> ratingFromComboBox;

    @FXML
    public JFXButton sortBtn;

    @FXML
    public JFXButton homeBtn;

    @FXML
    public JFXButton watchlistBtn;

    @FXML
    public JFXButton aboutBtn;

    @FXML
    public VBox navPane;

    @FXML
    public VBox sidebarTrigger;

    private Timeline showSidebarAnimation;
    private Timeline hideSidebarAnimation;

    public List<Movie> allMovies;

    protected ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    protected SortedState sortedState;

    private final DatabaseManager databaseManager;
    private final MovieRepository movieRepository;
    private final WatchlistRepository watchlistRepository;
    private final ClickEventHandler<Movie> onAddToWatchlistClicked;

    public HomeController() throws DatabaseException {
        this.databaseManager = DatabaseManager.getInstance();
        this.movieRepository = new MovieRepository(databaseManager);
        this.watchlistRepository = new WatchlistRepository(databaseManager, movieRepository);
        onAddToWatchlistClicked = (clickedItem) -> {
            try {
                watchlistRepository.addToWatchlist(clickedItem);
                showAlert("Success", "Movie added to watchlist: " + clickedItem.getTitle(), Alert.AlertType.INFORMATION);
            } catch (DatabaseException e) {
                showAlert("Error", "Failed to add movie to watchlist: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        };
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeState();
        initializeLayout();
        setupSidebarAnimations();
    }
    public void initializeState() {
        try {
            List<Movie> result = getMoviesFromAPIorDatabase();
            setMovies(result);
            setMovieList(result);
            sortedState = SortedState.NONE;
        } catch (MovieAPIException | DatabaseException e) {
            showAlert("Error", "Failed to load movies: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private List<Movie> getMoviesFromAPIorDatabase() throws MovieAPIException, DatabaseException {
        try {
            // Versuche, Filme von der API zu holen
            List<Movie> apiMovies = MovieAPI.getAllMovies();

            if (!apiMovies.isEmpty()) {
                // Filme von der API erhalten, in der Datenbank cachen
                try {
                    movieRepository.addAllMovies(apiMovies);
                } catch (DatabaseException e) {
                    // Fehler beim Cachen, aber wir haben trotzdem die API-Filme
                    System.err.println("Error caching movies: " + e.getMessage());
                }
                return apiMovies;
            } else {
                throw new MovieAPIException("No movies returned from API");
            }
        } catch (Exception e) {
            // API-Fehler, versuche Filme aus der Datenbank zu laden
            System.err.println("API error: " + e.getMessage() + ". Trying to load from database...");

            try {
                return movieRepository.getAllMovies().stream()
                        .map(movieEntity -> movieEntity.toMovie())
                        .collect(Collectors.toList());
            } catch (DatabaseException dbEx) {
                throw new DatabaseException("Failed to load movies from database", dbEx);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void initializeLayout() {
        movieListView.setItems(observableMovies);   // set the items of the listview to the observable list
        movieListView.setCellFactory(movieListView -> new MovieCell(onAddToWatchlistClicked)); // apply custom cells to the listview

        // genre combobox
        Object[] genres = Genre.values();   // get all genres
        genreComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        genreComboBox.getItems().addAll(genres);    // add all genres to the combobox
        genreComboBox.setPromptText("Filter by Genre");

        // year combobox
        releaseYearComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        // fill array with numbers from 1900 to 2023
        Integer[] years = new Integer[124];
        for (int i = 0; i < years.length; i++) {
            years[i] = 1900 + i;
        }
        releaseYearComboBox.getItems().addAll(years);    // add all years to the combobox
        releaseYearComboBox.setPromptText("Filter by Release Year");

        // rating combobox
        ratingFromComboBox.getItems().add("No filter");  // add "no filter" to the combobox
        // fill array with numbers from 0 to 10
        Integer[] ratings = new Integer[11];
        for (int i = 0; i < ratings.length; i++) {
            ratings[i] = i;
        }
        ratingFromComboBox.getItems().addAll(ratings);    // add all ratings to the combobox
        ratingFromComboBox.setPromptText("Filter by Rating");
    }

    private void setupSidebarAnimations() {
        // Initialize animations
        showSidebarAnimation = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(navPane.prefWidthProperty(), 150)
                )
        );

        hideSidebarAnimation = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(navPane.prefWidthProperty(), 0)
                )
        );

        // Set on finished actions
        showSidebarAnimation.setOnFinished(e -> {
            navPane.setVisible(true);
            navPane.setManaged(true);
        });

        hideSidebarAnimation.setOnFinished(e -> {
            navPane.setVisible(false);
            navPane.setManaged(false);
        });
    }

    @FXML
    private void showSidebar(MouseEvent event) {
        navPane.setVisible(true);
        navPane.setManaged(true);
        showSidebarAnimation.play();
    }

    @FXML
    private void hideSidebar(MouseEvent event) {
        hideSidebarAnimation.play();
    }

    public void setMovies(List<Movie> movies) {
        allMovies = movies;
    }

    public void setMovieList(List<Movie> movies) {
        observableMovies.clear();
        observableMovies.addAll(movies);
    }

    public void sortMovies(){
        if (sortedState == SortedState.NONE || sortedState == SortedState.DESCENDING) {
            sortMovies(SortedState.ASCENDING);
        } else if (sortedState == SortedState.ASCENDING) {
            sortMovies(SortedState.DESCENDING);
        }
    }

    public void sortMovies(SortedState sortDirection) {
        if (sortDirection == SortedState.ASCENDING) {
            observableMovies.sort(Comparator.comparing(Movie::getTitle));
            sortedState = SortedState.ASCENDING;
        } else {
            observableMovies.sort(Comparator.comparing(Movie::getTitle).reversed());
            sortedState = SortedState.DESCENDING;
        }
    }

    public List<Movie> filterByQuery(List<Movie> movies, String query){
        if(query == null || query.isEmpty()) return movies;

        if(movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream().filter(movie ->
                        movie.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                movie.getDescription().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<Movie> filterByGenre(List<Movie> movies, Genre genre){
        if(genre == null) return movies;

        if(movies == null) {
            throw new IllegalArgumentException("movies must not be null");
        }

        return movies.stream().filter(movie -> movie.getGenres().contains(genre)).toList();
    }

    public void applyAllFilters(String searchQuery, Object genre) {
        List<Movie> filteredMovies = allMovies;

        if (!searchQuery.isEmpty()) {
            filteredMovies = filterByQuery(filteredMovies, searchQuery);
        }

        if (genre != null && !genre.toString().equals("No filter")) {
            filteredMovies = filterByGenre(filteredMovies, Genre.valueOf(genre.toString()));
        }

        observableMovies.clear();
        observableMovies.addAll(filteredMovies);
    }

    public void searchBtnClicked(ActionEvent actionEvent) {
        try {
            String searchQuery = searchField.getText().trim().toLowerCase();
            String releaseYear = validateComboboxValue(releaseYearComboBox.getSelectionModel().getSelectedItem());
            String ratingFrom = validateComboboxValue(ratingFromComboBox.getSelectionModel().getSelectedItem());
            String genreValue = validateComboboxValue(genreComboBox.getSelectionModel().getSelectedItem());

            Genre genre = null;
            if(genreValue != null) {
                genre = Genre.valueOf(genreValue);
            }

            List<Movie> movies = getMovies(searchQuery, genre, releaseYear, ratingFrom);
            setMovies(movies);
            setMovieList(movies);

            sortMovies(sortedState);
        } catch (Exception e) {
            showAlert("Error", "Error filtering movies: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public String validateComboboxValue(Object value) {
        if(value != null && !value.toString().equals("No filter")) {
            return value.toString();
        }
        return null;
    }

    public List<Movie> getMovies(String searchQuery, Genre genre, String releaseYear, String ratingFrom) throws MovieAPIException{
        try {
            return MovieAPI.getAllMovies(searchQuery, genre, releaseYear, ratingFrom);
        } catch (MovieAPIException e) {
            try {
                // Wenn API-Aufruf fehlschlägt, versuche Filme aus der Datenbank zu laden
                return movieRepository.getAllMovies().stream()
                        .map(movieEntity -> movieEntity.toMovie())
                        .collect(Collectors.toList());
            } catch (DatabaseException dbEx) {
                showAlert("Database Error", "Could not load movies from database: " + dbEx.getMessage(), Alert.AlertType.ERROR);
                return new ArrayList<>();
            }
        }
    }

    public void sortBtnClicked(ActionEvent actionEvent) {
        sortMovies();
    }

    // Navigation Methods
    @FXML
    private void navigateToHome(ActionEvent event) {
        // Already on home, do nothing
    }

    @FXML
    private void navigateToWatchlist(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FhmdbApplication.class.getResource("watchlist-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 890, 620);
            scene.getStylesheets().add(Objects.requireNonNull(FhmdbApplication.class.getResource("styles.css")).toExternalForm());

            Stage stage = (Stage) watchlistBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Error navigating to watchlist: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void navigateToAbout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FhmdbApplication.class.getResource("about-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 890, 620);
            scene.getStylesheets().add(Objects.requireNonNull(FhmdbApplication.class.getResource("styles.css")).toExternalForm());

            Stage stage = (Stage) aboutBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Error navigating to about page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}