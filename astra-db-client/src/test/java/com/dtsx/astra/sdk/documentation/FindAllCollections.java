package com.dtsx.astra.sdk.documentation;
import com.dtsx.astra.sdk.AstraDB;
import io.stargate.sdk.json.domain.CollectionDefinition;

public class FindAllCollections {
  public static void main(String[] args) {

    // Given an active db
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Iterate over all collections
    db.findAllCollections().forEach(col -> {
      System.out.print("\nname=" + col.getName());
      if (col.getOptions() != null && col.getOptions().getVector() != null) {
        CollectionDefinition.Options.Vector vector = col.getOptions().getVector();
        System.out.print(",dim=" + vector.getDimension());
        System.out.print(",metric=" + vector.getMetric());
      }
    });
  }
}
