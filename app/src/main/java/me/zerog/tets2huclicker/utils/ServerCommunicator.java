package me.zerog.tets2huclicker.utils;

import static java.net.HttpURLConnection.HTTP_OK;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerCommunicator <Progress, Result>{

    private Result result;
    private ReqParams params;
    private boolean updated = false;

    private final Class<Result> typeResponceClass;
    private Executable<Result> postExecuteSuccess;
    private Executable<Exception> postExecuteFail;

    public ServerCommunicator(ReqParams params, Class<Result> typeResponceClass, @Nullable Executable<Result> postExecuteSuccess, @Nullable Executable<Exception> postExecuteFail){
        this.params = params;
        this.typeResponceClass = typeResponceClass;

        this.postExecuteSuccess = postExecuteSuccess;
        this.postExecuteFail = postExecuteFail;
    }

    public void run(){
        new InnerCommunicator().execute();
    }

    public void run(ReqParams params){
        this.params = params;
        new InnerCommunicator().execute();
    }

    private class InnerCommunicator extends AsyncTask<Void, Progress, Result>{
        @Override
        protected Result doInBackground(Void ... parameters) {
            try{
                return new Gson().fromJson(sendGET(), typeResponceClass);
            }catch (Exception exception){
                if(postExecuteFail != null){
                    postExecuteFail.execute(exception);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Result res) {
            updated = true;
            result = res;
            if(postExecuteSuccess != null && res != null){
                postExecuteSuccess.execute(res);
            }
        }

        private String sendGET() throws Exception{
            return sendGET(params);
        }

        private String sendGET(ReqParams params) throws Exception {
            URL obj = new URL(params.urlPostfix == null ? params.url : params.url + params.urlPostfix);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod(params.method.toString());
            con.setDoInput(params.doInput);
            con.setDoOutput(params.doOutput);
            con.setDefaultUseCaches(params.defaultUseCaches);

            //Add headers to a request
            params.headers.forEach(con::setRequestProperty);

            //Add body to a request
            if(params.getJSONBodyString() != null){
                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = params.getJSONBodyString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            int responseCode = con.getResponseCode();
            //TODO Fix "Message : {"mesage":"Error: ..."}"
            //TODO При повторной попытке залогиниться показывает сообщение с предыдущего раза
            String response = appendResponse(con, true);

            if(responseCode == HTTP_OK){
                return response;
            }else{
                throw new ConnectException("Request did not work. Response code: "+responseCode+". \nMessage: "+ ((Map<String, String>) new Gson().fromJson(response, Map.class)).get("message"));
            }
        }
    }

    /**
     * Converts response input stream into a String
     * @return Returns response String, "" otherwise
     * @throws Exception on incorrect input stream
     * */
    private static String appendResponse(HttpURLConnection connection, boolean ignoreException) throws Exception{
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getResponseCode() == HTTP_OK ? connection.getInputStream() : connection.getErrorStream()))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }catch (Exception ignored){
            if(ignoreException)
                return "";
            throw new ConnectException("Response building failed!");
        }
    }

    public interface Executable<Data>{
        void execute(Data response);
    }

    public static class ReqParams{
        private ReqMethod method = ReqMethod.GET;
        //private String userAgent = "Application/1.0";
        //private String userAgent = "Mozilla/5.0";
        private String url = "http://10.0.2.2:8080/";
        @Nullable
        private String urlPostfix;
        private Map<String, String> headers = new HashMap<>(
                Map.of(
                        "Accept", "*/*",
                        "Content-Type", "application/json",
                        "User-Agent", "2HUApp/1.0"
                )
        );
        private boolean doInput = true;
        private boolean doOutput = true;
        private boolean defaultUseCaches = true;
        private Map<String, String> jsonBody = new HashMap<>();

        /**Gets default parameters for HTTP request. Highly unrecommended to use*/
        public ReqParams getDefault(){
            return new ReqParams();
        }

        /**Gets default parameters for HTTP request. Highly unrecommended to use*/
        public ReqParams getDefault(String url){
            return new ReqParams(url);
        }

        public ReqParams getDefault(String url, ReqMethod reqMethod){
            return new ReqParams(url, reqMethod);
        }

        /**Adds property to the request Header
         * @return Previous associated value or 'null'*/
        public String addHeader(String key, String value){
            return headers.put(key, value);
        }

        /**Clears request Headers*/
        public void clearHeaders(){
            headers.clear();
        }

        @Nullable
        public String getJSONBodyValue(String key){
            return jsonBody.get(key);
        }

        /**Puts value to a request Body (To be converted to a json)
         * @return Previous associated value or 'null'*/
        public String addJSONBodyValue(String key, String value){
            return jsonBody.put(key, value);
        }

        public void setJSONBody(String jsonString){
            if(jsonString == null || jsonString.isEmpty()){
                jsonBody = new HashMap<>();
                return;
            }
            jsonBody = new Gson().fromJson(jsonString, new TypeToken<Map<String, String>>(){}.getType());
        }

        @Nullable
        public String getJSONBodyString(){
            if(jsonBody.isEmpty()){
                return null;
            }
            return new Gson().toJson(jsonBody);
        }

        /**Clears request Body*/
        public void clearBody(){
            jsonBody.clear();
        }

        public void setUrl(String url){
            this.url = url;
            urlPostfix = null;
        }

        public void clearPostfix(){
            this.urlPostfix = null;
        }

        public ReqParams setUrlPostfix(String postfix){
            this.urlPostfix = postfix;
            return this;
        }

        /**@return Returns copy of original request with additional postfix*/
        public ReqParams withPostfix(String postfix){
            return copy().setUrlPostfix(postfix);
        }

        /**@return Returns copy of original request with additional postfix and request method*/
        public ReqParams withPostfix(String postfix, ReqMethod reqMethod){
            return copy().setUrlPostfix(postfix).setMethod(reqMethod);
        }

        public String setUserAgent(String userAgent){
            return headers.put("User-Agent", userAgent);
        }

        public String getUserAgent(){
            return headers.get("User-Agent");
        }

        public ReqParams setMethod(ReqMethod method){
            this.method = method;
            return this;
        }

        public void setHeaders(HashMap<String, String> headers){
            this.headers = headers;
        }

        public void setDoInput(boolean doInput){
            this.doInput = doInput;
        }

        public void setDoOutput(boolean doOutput){
            this.doOutput = doOutput;
        }

        public void setDefaultUseCaches(boolean defaultUseCaches){
            this.defaultUseCaches = defaultUseCaches;
        }

        public ReqParams copy(){
            ReqParams copy = new ReqParams();
            copy.setMethod(method);
            copy.setUserAgent(getUserAgent());
            copy.setUrl(url);
            copy.setUrlPostfix(urlPostfix);
            copy.setHeaders(new HashMap<>(headers));
            copy.setDoInput(doInput);
            copy.setDoOutput(doOutput);
            copy.setDefaultUseCaches(defaultUseCaches);
            copy.setJSONBody(getJSONBodyString());
            return copy;
        }

        public ReqParams(){
            //reqProperties.put("Accept", "application/json");
        }
        public ReqParams(String url){
            this.url = url;
        }
        public ReqParams(String url, ReqMethod reqMethod){
            this.url = url; this.method = reqMethod;
        }
    }

    public enum ReqMethod{
        GET,
        POST,
        PUT,
        DELETE;
    }

    public Result getResult(){
        return result;
    }

    public ReqParams getParams(){
        return params;
    }

    public void setParams(ReqParams params){
        this.params = params;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setPostExecuteSuccess(Executable<Result> executable){
        postExecuteSuccess = executable;
    }

    public void setPostExecuteFail(Executable<Exception> executable){
        postExecuteFail = executable;
    }
}