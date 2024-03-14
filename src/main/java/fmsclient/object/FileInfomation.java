/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fmsclient.object;

/**
 *
 * @author GiaTK
 */
public class FileInfomation {
    private String name;
    private byte[] data;
    private String format;

    public FileInfomation() {
    }   
    
    public FileInfomation(String name, byte[] data, String format) {
        this.name = name;
        this.data = data;
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }        
}
