package moviesearch.client.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import moviesearch.service.Movie;
import moviesearch.service.MovieSearchService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainController {

    @FXML
    private FlowPane contentPane;
    @FXML
    private TextField searchField;
    @FXML
    private Label labelMessage;


    private ProgressIndicator progressIndicator;

    private MovieSearchService movieSearchService;

    @FXML
    private void initialize() {
        try {
            movieSearchService = (MovieSearchService) Naming.lookup( "//localhost/MovieSearchService");
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.out.println(e);
            labelMessage.setText("ERROR: " + e.getMessage());
        }

        searchField.setOnAction(event -> onSearchAction());

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(25, 25);
        progressIndicator.setVisible(false);

        labelMessage.setGraphic(progressIndicator);
    }

    private void onSearchAction() {
        String searchStr = searchField.getText();

        if (searchStr.trim().isEmpty()) {
            return;
        }

        progressIndicator.setVisible(true);
        contentPane.getChildren().clear();

        labelMessage.setText("");

        Thread thread = new Thread(new Task<List<Movie>>() {
            @Override
            protected List<Movie> call() throws Exception {
                List<Movie> movies = new ArrayList<>();
                try {
                    movies = movieSearchService.search(searchStr);
                } catch (RemoteException e) {
                    Platform.runLater(() -> labelMessage.setText(e.getMessage()));
                }
                return movies;
            }

            @Override
            protected void succeeded() {
                super.succeeded();

                progressIndicator.setVisible(false);

                try {
                    List<Movie> movies = this.get();

                    for (Movie movie : movies) {
                        addMoviePane(movie);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void addMoviePane(Movie movie) {
        HBox hbox = new HBox();
        hbox.getStyleClass().add("movie-pane");

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));

        Image image = new Image(movie.getPoster(), 200, 200, true, true, true);
        ImageView poster = new ImageView(image);

        Label labelTitle = new Label("Title");
        labelTitle.getStyleClass().add("content-title");
        VBox.setMargin(labelTitle, new Insets(10, 0, 0, 0));
        Label labelTitle2 = new Label(movie.getTitle());

        Label labelYear = new Label("Year");
        labelYear.getStyleClass().add("content-title");
        VBox.setMargin(labelYear, new Insets(10, 0, 0, 0));
        Label labelYear2 = new Label(movie.getYear());

        Label labelType = new Label("Type");
        labelType.getStyleClass().add("content-title");
        VBox.setMargin(labelType, new Insets(10, 0, 0, 0));
        Label labelType2 = new Label(movie.getType());

        Label labelImdb = new Label("IMDB");
        labelImdb.getStyleClass().add("content-title");
        VBox.setMargin(labelImdb, new Insets(10, 0, 0, 0));
        Label labelImdb2 = new Label(movie.getImdbID());

        vbox.getChildren().addAll(labelTitle, labelTitle2, labelYear, labelYear2, labelType, labelType2, labelImdb, labelImdb2);

        hbox.getChildren().addAll(poster, vbox);
        contentPane.getChildren().add(hbox);
    }
}

