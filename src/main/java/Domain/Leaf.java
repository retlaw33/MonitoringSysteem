package Domain;

import org.apache.http.client.methods.CloseableHttpResponse;

public class Leaf {
    private String endPoint;
    private HttpMethod httpMethod;
    private String body;
    private int statuscode;
    private String result;
    private boolean functional;
    private int upCount;
    private int downCount;

    public Leaf(String endPoint, HttpMethod httpMethod, String body) {
        this.endPoint = endPoint;
        this.httpMethod = httpMethod;
        this.body = body;
        this.functional = false;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getBody() {
        return body;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getUpCount() {
        return upCount;
    }

    public void addToUpCount() {
        this.upCount++;
    }

    public int getDownCount() {
        return downCount;
    }

    public void addToDownCount() {
        this.downCount++;
    }

    @Override
    public String toString() {
        return "endpoint: " + this.endPoint + " --> " + this.isFunctional();
    }
}
