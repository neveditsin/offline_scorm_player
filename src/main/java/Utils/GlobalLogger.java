package Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.naming.ConfigurationException;

public class GlobalLogger {
	private final static Logger GENERAL = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private final static String DEFAULT_LOG_FILE = "scorm";
	private final static AtomicBoolean isInitialized = new AtomicBoolean(false);
	

	
	public static synchronized void init(String path) throws SecurityException, IOException, ConfigurationException{		
		if (isInitialized.get()) {
			return;
		}
		GENERAL.setLevel(Level.INFO);
		
		String logdir = null;
		
		Path tmp = Paths.get(DEFAULT_LOG_FILE + ".tmp");
		if (path == null || path.length() < 1) {
			System.out.print("Log file path is not set. ");
			
			try{				
				if(Files.createFile(tmp) != null){
					Files.delete(tmp);
					System.out
							.println("Use default directory: "
									+ tmp.toAbsolutePath().getParent()
									+ File.separator);
					logdir = DEFAULT_LOG_FILE;
				}
			} catch(Exception e){
				System.out.println("Unable to create a file in the default directory. Use stderr for logging ");			
			}			
		} else{
			//path is set
			if (Files.isWritable(Paths.get(path))) {
				logdir = path + File.separator + DEFAULT_LOG_FILE;
			} else{
				throw new ConfigurationException(
						"The provided log file path is invalid or not writable. Please check write permissions");
			}
			
		}		
		 
		GENERAL.addHandler(logdir == null? newStderrHandler() : newFileHandler(logdir + ".log"));
		isInitialized.set(true);
		
	}
	

	
	private static Handler newFileHandler(String path) throws SecurityException, IOException{
		Handler h = new FileHandler(path, true);
        h.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] %2$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getMessage()
                );
            }
        });
        return h;
	}
	
	private static Handler newStderrHandler() {
		Handler h = new StreamHandler();
        h.setFormatter(new SimpleFormatter());
        return h;
	}

	public static void error(String message, Throwable t){
		if(isInitialized.get()) {
			GENERAL.log(Level.SEVERE, message, t);
		}
		else
		{
			System.err.println(message);
		}
	}
	
	public static void error(String message){
		if(isInitialized.get()) {
			GENERAL.log(Level.SEVERE, message);
		}
		else
		{
			System.err.println(message);
		}
	}
	
	public static void info(String message){
		if(isInitialized.get()) {
			GENERAL.log(Level.INFO, message);
		}
		else
		{
			System.err.println(message);
		}
	}



}
