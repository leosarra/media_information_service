package mediacontent;

public class FilmInfo extends MediaInfo {
    public String overview= "Information not avaiable";
    public String releaseDate= "Information not avaiable";
    public String vote= "Information not avaiable";


    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    @Override
    public String toString() {
        return "FilmInfo{" +
                "overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", vote='" + vote + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
