package at.ac.fhcampuswien.fhmdb.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "watchlist")
public class WatchlistEntity {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private MovieEntity movie;

    public WatchlistEntity() {}

    public WatchlistEntity(MovieEntity movie) {
        this.movie = movie;
    }

    public long getId() {
        return id;
    }

    public MovieEntity getMovie() {
        return movie;
    }
}