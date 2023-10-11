package com.dtsx.astra.sdk.huggingface;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor
public class HFDatasetPage {

    private List<Feature> features;

    private List<RowData> rows;

    @JsonProperty("num_rows_total")
    private int numRowsTotal;

    @JsonProperty("num_rows_per_page")
    private int numRowsPerPage;

    // Getters, setters, and default constructor

    @Data @NoArgsConstructor
    public static class Feature {

        @JsonProperty("feature_idx")
        private int featureIdx;

        private String name;

        private Type type;

        @Data @NoArgsConstructor
        public static class Type {

            private String dtype;

            @JsonProperty("_type")
            private String type;
        }
    }

    @Data @NoArgsConstructor
    public static class RowData {

        @JsonProperty("row_idx")
        private int rowIdx;
        private Map<String, String> row;

        @JsonProperty("truncated_cells")
        private List<String> truncatedCells;
    }
}
