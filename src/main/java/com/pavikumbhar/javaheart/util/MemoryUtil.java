package com.pavikumbhar.javaheart.util;

import org.slf4j.Logger;

/**
 * 
 * @author pavikumbhar
 *
 */
public class MemoryUtil {
    
	 public static  void printMemoryUsage(String s){
    	Runtime runtime = Runtime.getRuntime();
        
    	int mb = 1024*1024;
    	long totoalMemory=runtime.totalMemory();
    	long freeMemory=runtime.freeMemory();
    
        System.out.println("##Heap statistics->["+s+"][Used Memory:"+ (totoalMemory - freeMemory) / mb+"][Free Memory:"+ freeMemory / mb+"][Total Memory:" + totoalMemory / mb+"][Max Memory:" + runtime.maxMemory() / mb+"] in MB");
      
    }
        
        
    public static  void printMemoryUsage(Logger  classLogger){
    	Runtime runtime = Runtime.getRuntime();
        
    	int mb = 1024*1024;
    	long totoalMemory=runtime.totalMemory();
    	long freeMemory=runtime.freeMemory();
   
       
        classLogger.debug("##Heap statistics->[Used Memory:"+ (totoalMemory - freeMemory) / mb+"][Free Memory:"+ freeMemory / mb+"][Total Memory:" + totoalMemory / mb+"][Max Memory:" + runtime.maxMemory() / mb+"] in MB");
       
    }
    
}
