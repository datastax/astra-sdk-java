/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.dto;

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
