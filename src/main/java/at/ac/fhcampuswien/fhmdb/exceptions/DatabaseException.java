package at.ac.fhcampuswien.fhmdb.exceptions;

import javafx.scene.chart.PieChart;

public class DatabaseException extends Exception {
    // construct with msg
    public DatabaseException(String message) {
        super(message);
    }

    // construct with msh and cause
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}