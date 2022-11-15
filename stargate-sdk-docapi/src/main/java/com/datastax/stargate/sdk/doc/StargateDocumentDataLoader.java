package com.datastax.stargate.sdk.doc;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Allow to load beans.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateDocumentDataLoader<DOC> implements Closeable {
    
   /** With Envoy configuration in Astra. */
   private static final int DEFAULT_POOL_SIZE = 6;
   
   /** Limit number of thread in parallel. */
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
  
   /** Executor too run thread. */
   private final ExecutorService executor;
     
   /**
    * Default Constructor.
    */
   public StargateDocumentDataLoader() {
       this(DEFAULT_POOL_SIZE);
   }
           
   /**
    * Set up thread in parallel.
    *
    * @param poolSize
    *       poolse
    */
   public StargateDocumentDataLoader(int poolSize) {
       executor= Executors.newFixedThreadPool(poolSize);
   }
   
   /**
    * Push thread in the queue for the document to be loaded.
    *
    * @param ccc
    *       collection client parent
    * @param doc
    *       document to be saved
    * @return
    *       noothing to return 
    */
   public CompletableFuture<Void> submitCreateDoc(CollectionClient ccc, DOC doc) {
       return CompletableFuture.runAsync(() -> { 
           lock.writeLock().lock();
           ccc.create(doc);
           lock.writeLock().unlock();
       }, executor);
   }
   
   /**
    * Push thread in the queue for the document to be loaded.
    *
    * @param ccc
    *       collection client parent
    * @param doc
    *       document to be saved
    * @return
    *       noothing to return 
    */
   public CompletableFuture<Void> submitCreateDoc(CollectionClient ccc, String doc) {
       return CompletableFuture.runAsync(() -> { 
           lock.writeLock().lock();
           ccc.create(doc);
           lock.writeLock().unlock();
       }, executor);
   }

   /** {@inheritDoc} */
   @Override
   public void close()  {
       executor.shutdown();
   };
    
}
