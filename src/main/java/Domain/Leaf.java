package Domain;

public class Leaf {
    private String endPoint;
    private HttpMethod httpMethod;
    private String body;
    private String response;
    private boolean functional;

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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    @Override
    public String toString() {
        return "endpoint: " + this.endPoint + " --> " + this.isFunctional();
    }
}
