package com.tomi.Kandle;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.tomi.Kandle.MainActivity.table;


public class ConcreteNameThread extends Thread {



    MyParser myParser;

    BufferedReader bufferedReader;

    public ConcreteNameThread(MyParser parser){
        this.myParser = parser;
    }



    private String urlString;

    private HttpsURLConnection http = null;
    private InputStream inputStream = null;


    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getStingsFromBuffer(BufferedReader bufferedReader) throws IOException {
        ArrayList<String> htmlString  = new ArrayList<>();
        String ln;

        while ((ln=bufferedReader.readLine()) != null) {
            htmlString.add(ln);
        }
        return htmlString;
    }




    @Override
    public void run() {

        try {



            urlString = myParser.getConcreteUrl();
            URL urlString = new URL(this.urlString);

            trustAllHosts();

            http = (HttpsURLConnection) urlString
                    .openConnection();
            http.setHostnameVerifier(DO_NOT_VERIFY);
            http.connect();

            inputStream = http.getInputStream();


            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            ArrayList<String> htmlString =  getStingsFromBuffer(bufferedReader);

            //myParser.setData();
            myParser.parsetxt(htmlString);
            http.disconnect();

                table.hideShow();

                //myParser.hideButtons();

        } catch (Throwable  e) {
            e.printStackTrace();
        }
    }

}
