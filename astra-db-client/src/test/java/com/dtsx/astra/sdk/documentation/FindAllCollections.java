package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.data.domain.CollectionDefinition;

public class FindAllCollections {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Get Collection Names
    db.findAllCollectionsNames().forEach(System.out::println);

    // Iterate over all collections and print each vector definition
    db.findAllCollections().forEach(col -> {
      System.out.print("\nname=" + col.getName());
      if (col.getOptions() != null && col.getOptions().getVector() != null) {
        CollectionDefinition.Options.Vector vector = col.getOptions().getVector();
        System.out.print(", dim=" + vector.getDimension());
        System.out.print(", metric=" + vector.getMetric());
      }
    });
  }
}
