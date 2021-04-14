package org.datastax.astra.dto;

/**
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Video {
    
    private String genre;
    
    private String title;
    
    private int year;

    /**
     * Getter accessor for attribute 'genre'.
     *
     * @return
     *       current value of 'genre'
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Setter accessor for attribute 'genre'.
     * @param genre
     * 		new value for 'genre '
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Getter accessor for attribute 'year'.
     *
     * @return
     *       current value of 'year'
     */
    public int getYear() {
        return year;
    }

    /**
     * Setter accessor for attribute 'year'.
     * @param year
     * 		new value for 'year '
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Getter accessor for attribute 'title'.
     *
     * @return
     *       current value of 'title'
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter accessor for attribute 'title'.
     * @param title
     * 		new value for 'title '
     */
    public void setTitle(String title) {
        this.title = title;
    }

}
