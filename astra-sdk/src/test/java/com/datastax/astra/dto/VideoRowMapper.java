package com.datastax.astra.dto;

import com.datastax.stargate.sdk.rest.domain.Row;
import com.datastax.stargate.sdk.rest.domain.RowMapper;

public class VideoRowMapper implements RowMapper<Video>{

    /** {@inheritDoc} */
    @Override
    public Video map(Row row) {
        Video video = new Video();
        video.setGenre(row.getString("genre"));
        video.setTitle(row.getString("title"));
        video.setYear(row.getInt("year"));
        return video;
    }

}
