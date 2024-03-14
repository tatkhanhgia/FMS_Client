/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fmsclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import fmsclient.HTTPUtils.ContentType;
import fmsclient.object.Constant;
import fmsclient.object.FileInfomation;
import fmsclient.object.Response;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 *
 * @author Admin
 */
public class FMSClient {

    private FileInfomation file;
    private String UUID;
    private String url;
    private boolean isGTKDev = false;
    private HttpResponse response = null;

    public void setFormat(String format) {
        file.setFormat(format);
    }

    public void setData(byte[] data) {
        file.setData(data);
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
    
    public void setGTK_Dev(){
        this.isGTKDev = true;
    }

    public FMSClient() {
        file = new FileInfomation();
        file.setFormat("tmp");
        url = "https://dev-fms.mobile-id.vn/rssp.FMS";
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void uploadFile() throws Exception {
        if (file == null) {
            throw new Exception("Missing FileInformation");
        }
        if(file.getData() == null){
            throw new Exception("Binary data is null!");
        }
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-file-name", file.getName());
        headers.put("Content-Length", String.valueOf(file.getData().length));
        if (file.getFormat() != null) {
            headers.put("x-mime-type", file.getFormat());
        }
        headers.put("x-gtk-dev", String.valueOf(isGTKDev));
        
        response = HTTPUtils.invokeHttpRequestAsStream(
                url + Constant.uploadFile,
                "POST",
                ContentType.JSON,
                50000,
                headers,
                file.getData());
    }

    public void downloadFile() throws Exception {
        if (UUID == null) {
            throw new Exception("Missing UUID");
        }
        HashMap<String, String> request = new HashMap<>();
        request.put("uuid", UUID);
        
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-gtk-dev", String.valueOf(isGTKDev));
        

        response = HTTPUtils.invokeHttpGET_returnInputStream(
                url + Constant.downloadFile,
                "GET",
                ContentType.JSON,
                50000,
                headers,
                request);
    }

    public void deleteFile() throws Exception {
        response = HTTPUtils.invokeHttpRequest(
                null,
                url + Constant.deleteFile + "/" + UUID,
                "DELETE",
                ContentType.JSON,
                50000,
                new HashMap<>(),
                null);
    }

    public byte[] getData() throws Exception {
        if (response != null) {
            if (response.getData() != null) {
                return response.getData();
            }
            throw new Exception("Response does not contain BinaryData");
        }
        throw new Exception("Please call Download function first");
    }

    public int getHttpCode() throws Exception {
        if (response != null) {
            return response.getHttpCode();
        }
        throw new Exception("Please call Upload/Download/Delete first");
    }

    public String getMessage_Error() throws Exception {
        if (response != null) {
            if (response.getMsg().contains("Message")) {
                return response.getMsg().substring(12, response.getMsg().length() - 3);
            }
            return response.getMsg();
        }
        throw new Exception("Please call Upload/Download/Delete first");
    }

    public String getUUID() throws Exception {
        if (response != null && (response.getHttpCode() == 201 || response.getHttpCode() == 200)) {
            Response temp = new ObjectMapper().readValue(response.getMsg(), Response.class);
            if(temp.getUUID() == null || temp.getUUID().isEmpty()){
                return temp.getMessage();
            }
            return temp.getUUID();
        } else if (response == null) {
            throw new Exception("Please call Upload function first");
        }
        throw new Exception("Response does not contain UUID");
    }

    public static void main(String[] args) throws IOException, Exception {
        byte[] data = Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\response.pdf"));
        FMSClient client = new FMSClient();
        client.setURL("https://dev-fms.mobile-id.vn/rssp.FMS");
        client.setData(data);
        client.setFormat("pdf");

        //Upload file
        client.uploadFile();
        System.out.println("HttpCode:" + client.getHttpCode());
        System.out.println("Message:" + client.getMessage_Error());
        System.out.println("UUID:" + client.getUUID());
        
        //Download file
        FMSClient client2 = new FMSClient();
        client2.setURL("https://dev-fms.mobile-id.vn/rssp.FMS");
        client2.setUUID("9F0AD27AB37F390576F77B71C0BC8EA4");
        
        client2.downloadFile();
        
        System.out.println("HttpCode:" + client2.getHttpCode());
        System.out.println("Message:" + client2.getMessage_Error());
        System.out.println("Data:"+client2.getData());
        if (client2.getData() != null) {
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Admin\\Downloads\\test.txt");
            outputStream.write(client2.getData());
            outputStream.close();
        }
        
        //Delete file
        FMSClient client3 = new FMSClient();
        client3.setURL("https://dev-fms.mobile-id.vn/rssp.FMS");
        client3.setUUID("9F0AD27AB37F390576F77B71C0BC8EA4");
        
        client3.deleteFile();
        
        System.out.println("HttpCode:" + client3.getHttpCode());
        System.out.println("Message:" + client3.getMessage_Error());
    }
}
