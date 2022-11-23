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

package io.stargate.sdk.test.rest.domain;

import io.stargate.sdk.rest.domain.Row;
import io.stargate.sdk.rest.domain.RowMapper;

/**
 * Test row mapper.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class VideoRowMapper implements RowMapper<Video> {

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
