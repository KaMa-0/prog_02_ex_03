package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.db.DatabaseManager;
import at.ac.fhcampuswien.fhmdb.db.MovieRepository;
import at.ac.fhcampuswien.fhmdb.db.WatchlistRepository;
import at.ac.fhcampuswien.fhmdb.exceptions.DatabaseException;
import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.ui.WatchlistMovieCell;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class WatchlistController implements Initializable {

    @FXML
    private JFXButton homeBtn;

    @FXML
    private JFXButton watchlistBtn;

    @FXML
    private JFXButton aboutBtn;

    @FXML
    private VBox navPane;

    @FXML
    private VBox sidebarTrigger;

    @FXML
    private JFXListView<Movie> watchlistView;

    // Animationen für das Ein- und Ausblenden der Sidebar
    private Timeline showSidebarAnimation;
    private Timeline hideSidebarAnimation;

    // Beobachtbare Liste für die in der Watchlist angezeigten Filme
    private final ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    private final DatabaseManager databaseManager;
    private final MovieRepository movieRepository;
    private final WatchlistRepository watchlistRepository;

    // Konstruktor: Initialisiert den DatabaseManager und die Repository-Instanzen
    public WatchlistController() {
        this.databaseManager = DatabaseManager.getInstance();
        this.movieRepository = new MovieRepository(databaseManager);
        this.watchlistRepository = new WatchlistRepository(databaseManager, movieRepository);
    }

    // Initialisierung der Benutzeroberfläche
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Lade die Filme aus der Watchlist und zeige sie an
        loadWatchlistMovies();

        // Konfiguriere die ListView mit der beobachtbaren Liste und benutzerdefinierter Zellenanzeige
        watchlistView.setItems(observableMovies);
        watchlistView.setCellFactory(movieListView -> new WatchlistMovieCell(this::removeFromWatchlist));

        // Richte die Animationen für die Sidebar ein
        setupSidebarAnimations();
    }

    // Lädt Filme aus der Watchlist und fügt sie in die observableMovies-Liste ein
    private void loadWatchlistMovies() {
        try {
            List<Movie> watchlistMovies = watchlistRepository.getWatchlistMovies();
            observableMovies.clear();
            observableMovies.addAll(watchlistMovies);
        } catch (DatabaseException e) {
            showAlert("Error", "Failed to load watchlist: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Konfiguriert die Ein- und Ausblendanimation für die Navigationsleiste
    private void setupSidebarAnimations() {
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

        // Aktion, wenn die Einblendanimation abgeschlossen ist
        showSidebarAnimation.setOnFinished(e -> {
            navPane.setVisible(true);
            navPane.setManaged(true);
        });

        // Aktion, wenn die Ausblendanimation abgeschlossen ist
        hideSidebarAnimation.setOnFinished(e -> {
            navPane.setVisible(false);
            navPane.setManaged(false);
        });
    }

    // Zeigt die Sidebar an, wenn sie durch einen Mausklick aktiviert wird
    @FXML
    private void showSidebar(MouseEvent event) {
        navPane.setVisible(true);
        navPane.setManaged(true);
        showSidebarAnimation.play();
    }

    // Blendet die Sidebar aus, wenn der Ausblend-Trigger aktiviert wird
    @FXML
    private void hideSidebar(MouseEvent event) {
        hideSidebarAnimation.play();
    }

    // Entfernt einen Film aus der Watchlist sowohl in der Datenbank als auch in der UI
    private void removeFromWatchlist(Movie movie) {
        try {
            watchlistRepository.removeFromWatchlist(movie.getId());
            observableMovies.remove(movie);
            showAlert("Success", "Movie removed from watchlist: " + movie.getTitle(), Alert.AlertType.INFORMATION);
        } catch (DatabaseException e) {
            showAlert("Error", "Failed to remove movie from watchlist: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Zeigt einen Alert an, um den Benutzer über den Status einer Aktion zu informieren
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Navigiert zur Home-Ansicht, indem die entsprechende FXML-Datei geladen wird
    @FXML
    private void navigateToHome(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FhmdbApplication.class.getResource("home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 890, 620);
            scene.getStylesheets().add(Objects.requireNonNull(FhmdbApplication.class.getResource("styles.css")).toExternalForm());

            Stage stage = (Stage) homeBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Error navigating to home page: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Da diese Ansicht bereits angezeigt wird, macht eine Navigation in die Watchlist nichts
    @FXML
    private void navigateToWatchlist(ActionEvent event) {
        // Bereits in der Watchlist-Ansicht, daher keine Aktion
    }

    // Navigiert zur About-Ansicht, indem die entsprechende FXML-Datei geladen wird
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