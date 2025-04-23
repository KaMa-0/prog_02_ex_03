package at.ac.fhcampuswien.fhmdb;

import at.ac.fhcampuswien.fhmdb.models.Movie;
import at.ac.fhcampuswien.fhmdb.ui.ClickEventHandler;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

    private Timeline showSidebarAnimation;
    private Timeline hideSidebarAnimation;

    private final ObservableList<Movie> observableMovies = FXCollections.observableArrayList();

    // For demonstration only - in the actual implementation, this would come from the database
    private List<Movie> getWatchlistMovies() {
        // TODO: Replace with actual data from the database
        // This is a placeholder
        return new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load watchlist movies
        List<Movie> watchlistMovies = getWatchlistMovies();
        observableMovies.addAll(watchlistMovies);

        // Set up the watchlist view
        watchlistView.setItems(observableMovies);
        watchlistView.setCellFactory(movieListView -> new WatchlistMovieCell(this::removeFromWatchlist));

        // Setup sidebar animations
        setupSidebarAnimations();
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

    private void removeFromWatchlist(Movie movie) {
        // TODO: Implement the code to remove movie from watchlist in DB
        System.out.println("Removing movie from watchlist: " + movie.getTitle());
        observableMovies.remove(movie);
    }

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
            e.printStackTrace();
            // TODO: Proper error handling
        }
    }

    @FXML
    private void navigateToWatchlist(ActionEvent event) {
        // Already on watchlist, do nothing
    }
}