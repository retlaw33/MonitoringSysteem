package Service;

import Domain.Leaf;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.apache.http.protocol.HTTP.USER_AGENT;

public class HttpClient {
    public Leaf getRequest(Leaf leaf) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet getRequest = new HttpGet(leaf.getEndPoint());

        // add leaf header
        getRequest.addHeader("User-Agent", USER_AGENT);

        CloseableHttpResponse response = null;
        try{
            response = client.execute(getRequest);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            leaf = checkResponse(leaf, response);
            client.close();
        }
        catch (IOException ex){
            //ex.printStackTrace();
        }
        return leaf;
    }

    public Leaf postRequest(Leaf leaf) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build(); // HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(leaf.getEndPoint());


        StringEntity entity = new StringEntity(leaf.getBody());
        postRequest.setEntity(entity);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-type", "application/json");

        postRequest.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse response = client.execute(postRequest);

        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

        leaf = checkResponse(leaf, response);

        client.close();
        return leaf;
    }

    private Leaf checkResponse(Leaf leaf, CloseableHttpResponse response) throws IOException {
        int statuscode = response.getStatusLine().getStatusCode();
        if (statuscode == 200) {
            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println("result = " + result);
            leaf.setResponse(String.valueOf(result));
            leaf.setFunctional(true);
        }
        else{
            leaf.setFunctional(false);
        }
        return leaf;
    }
}
