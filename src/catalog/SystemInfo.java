/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package catalog;

/**
 *
 * @author jimakos
 */
public class SystemInfo {
    int numOfBuffers = -1;
    int sizeOfBuffer = -1;
    double latency = 0.0;
    double transferTime = 0.0;
    double timeForWritingPages = 0.0;
    
    public SystemInfo(){
    
    }

    public int getNumOfBuffers() {
        return numOfBuffers;
    }

    public int getSizeOfBuffer() {
        return sizeOfBuffer;
    }

    public double getLatency() {
        return latency;
    }

    public double getTransferTime() {
        return transferTime;
    }

    public double getTimeForWritingPages() {
        return timeForWritingPages;
    }

    public void setNumOfBuffers(int numOfBuffers) {
        this.numOfBuffers = numOfBuffers;
    }

    public void setSizeOfBuffer(int sizeOfBuffer) {
        this.sizeOfBuffer = sizeOfBuffer;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public void setTransferTime(double transferTime) {
        this.transferTime = transferTime;
    }

    public void setTimeForWritingPages(double timeForWritingPages) {
        this.timeForWritingPages = timeForWritingPages;
    }
    
    
    
    
}
