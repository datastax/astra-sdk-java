package com.datastax.astra.shell.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.shell.cmd.BaseCommand;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Download and start cqlShell is needed.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class CqlShellUtils {
    
    /** Environment variable coding user home. */
    public static final String ENV_USER_HOME = "user.home";

    /** where to Download CqlSH. */
    public static final String CQLSH_URL = "https://downloads.datastax.com/enterprise/cqlsh-astra.tar.gz";
    
    /** Path to save third-parties. */
    public static final String CQLSH_HOME = System.getProperty(ENV_USER_HOME) + File.separator + ".astra";
    
    /** Folder name of Cqlsh once untar. */
    public static final String CQLSH_FOLDER = "cqlsh-astra";
    
    /** Folder name of Cqlsh once untar. */
    public static final String CQLSH_TARBALL = "cqlsh-astra.tar.gz";
    
    /**
     * Hide default construtor
     */
    private CqlShellUtils() {}
    
    /**
     * Check if cqlshel has been installed.
     *
     * @return
     *      if the folder exist
     */
    private static boolean isCqlShellInstalled() {
       File cqlshAstra = new File(CQLSH_HOME + File.separator + CQLSH_FOLDER);
       return cqlshAstra.exists() && cqlshAstra.isDirectory();
    }
    
    /**
     * Un Tar file.
     *
     * @param tarFile
     *      source file
     * @param destFile
     *      destination folder
     * @throws IOException
     *      error during opening archive
     */
    private static void unTarFile(File tarFile, File destFile) throws IOException{
      FileInputStream       fis      = new FileInputStream(tarFile);
      TarArchiveInputStream tis      = new TarArchiveInputStream(fis);
      TarArchiveEntry       tarEntry = null;
      while ((tarEntry = tis.getNextTarEntry()) != null) {
        File outputFile = new File(destFile + File.separator + tarEntry.getName());
        if (tarEntry.isDirectory()) {
            if (!outputFile.exists()) {
                outputFile.mkdirs();
            }
        } else {
            outputFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(outputFile);
            IOUtils.copy(tis, fos);
            fos.close();
        }
      }
      tis.close();
    }
    
    /**
     * Download targz and unzip.
     *
     * @param cmd
     *      current command with option to format 
     */
    public static void installCqlShellAstra(BaseCommand cmd) {
        if (!isCqlShellInstalled()) {
            LoggerShell.info("CqlSh has not been found, downloading...");
            Utils.downloadFile(CQLSH_URL, CQLSH_HOME + File.separator + CQLSH_TARBALL);
            File cqlshtarball = new File (CQLSH_HOME + File.separator + CQLSH_TARBALL);
            if (cqlshtarball.exists()) {
                LoggerShell.info("File Downloaded. Unzipping...");
                try {
                    unTarFile(cqlshtarball, new File (CQLSH_HOME));
                    if (!isCqlShellInstalled()) {
                        LoggerShell.success("Cqlsh installed");
                    }
                } catch (IOException e) {
                    LoggerShell.error("Cannot extract tar archive:" + e.getMessage());
                }
            }
        } else {
            LoggerShell.success("Cqlsh is installed.");
        }
    }
   
    /**
     * Install CqlShell if needed and start the program.
     * 
     * @param cmd
     *      current command with option to format 
     * @param token
     *      authentication token
     * @param dbId
     *      database id
     * @param dbRegion
     *      database region
     * @return
     *      linux process
     * @throws IOException
     *      errors occured
     */
    public static Process runCqlShellAstra(BaseCommand cmd, String token, String dbId, String dbRegion) 
    throws IOException {
        installCqlShellAstra(cmd);
        
        StringBuilder startCqlsh = new StringBuilder()
                .append(System.getProperty(ENV_USER_HOME) + File.separator)
                .append(".astra" + File.separator + CQLSH_FOLDER)
                .append(File.separator + "bin")
                .append(File.separator + "cqlsh");
        startCqlsh.append(" -u token");
        startCqlsh.append(" -p " + token);
        startCqlsh.append(" -b " + System.getProperty(ENV_USER_HOME) + 
                File.separator + ".astra" + 
                File.separator + AstraClientConfig.buildScbFileName(dbId, dbRegion));
        System.out.println(startCqlsh.toString());
        return Runtime.getRuntime().exec(startCqlsh.toString());
    }
   
}
