/* ********************************************************** /*
 * javaAVMTR064 - open source Java TR-064 API                 *
 * JFritz - pc call management
 *                                                            *
 * Copyright (C) 2015 JFritz contributors                     *
 * Copyright (C) 2015 Marin Pollmann <pollmann.m@gmail.com>   *
 *                                                            *
/* ********************************************************** */
package de.linusgke.fritzdialer.fritz.tr064;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class TR064Connection {

    private static final int DEFAULT_HTTPS_PORT = 49443;
    private static final String SCHEME_HTTPS = "https";
    private static final String FRITZ_IGD_DESC_FILE = "igddesc.xml";
    private static final String FRITZ_TR64_DESC_FILE = "tr64desc.xml";

    private final HttpHost targetHost;
    @Getter
    private Map<String, Service> services;
    private String user = null;
    private String pwd = null;
    private CloseableHttpClient httpClient;
    private HttpClientContext context;

    private String name;

    public TR064Connection(String scheme, String address, int port) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        targetHost = new HttpHost(address, port, scheme);
        httpClient = newClient();
        context = HttpClientContext.create();
        services = new HashMap<>();
    }

    public TR064Connection(String address) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(SCHEME_HTTPS, address, DEFAULT_HTTPS_PORT);
    }

    public TR064Connection(String scheme, String address, int port, String user, String pwd) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(scheme, address, port);
        this.user = user;
        this.pwd = pwd;
    }

    public TR064Connection(String address, String user, String pwd) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this(address);
        this.user = user;
        this.pwd = pwd;
    }

    private CloseableHttpClient newClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        SSLContext context = SSLContexts.custom().loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE).build();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE)).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    public void init(String scpdUrl) throws IOException, ParseException, UnauthorizedException {
        if (pwd != null) {
            log.debug("try to connect to " + this.targetHost.getAddress() + " with credentials " + this.user + "/" + this.pwd);
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pwd));
            AuthCache authCache = new BasicAuthCache();
            DigestScheme digestScheme = new DigestScheme();
            digestScheme.overrideParamter("realm", "F!Box SOAP-Auth");
            digestScheme.overrideParamter("nonce", Long.toString(new Random().nextLong(), 36));
            digestScheme.overrideParamter("qop", "auth");
            digestScheme.overrideParamter("nc", "0");
            digestScheme.overrideParamter("cnonce", DigestScheme.createCnonce());
            authCache.put(targetHost, digestScheme);
            context.setCredentialsProvider(credsProvider);
            context.setAuthCache(authCache);
            readTR64(scpdUrl);
        } else {
            log.debug("read igddesc, because only user is set to " + this.user);
            readIGDDESC();
        }
    }

    private void readTR64(String scpdUrl) throws IOException, ParseException, UnauthorizedException {
        scpdUrl = scpdUrl == null ? FRITZ_TR64_DESC_FILE : scpdUrl;
        InputStream xml = getXMLIS("/" + scpdUrl);

        RootType root = (RootType) JAXBUtilities.unmarshalInput(xml);
        log.debug(root.toString());
        DeviceDesc device = root.getDevice();
        name = device.getFriendlyName();
        getServicesFromDevice(device);
    }

    private void readIGDDESC() throws IOException, UnauthorizedException {
        InputStream xml = getXMLIS("/" + FRITZ_IGD_DESC_FILE);
        try {
            RootType root = (RootType) JAXBUtilities.unmarshalInput(xml);
            log.debug(root.toString());
            DeviceDesc device = root.getDevice();
            name = device.getFriendlyName();
            getServicesFromDevice(device);
        } catch (ParseException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new IOException(e);
        }
    }

    private void getServicesFromDevice(DeviceDesc device) throws IOException, ParseException, UnauthorizedException {
        for (Object sT : device.getServiceList()) {
            log.debug("Service {} {}", sT, sT.getClass().getName());
        }

        for (ServiceDesc sT : device.getServiceList()) {
            String[] tmp = sT.getServiceType().split(":");
            String key = tmp[tmp.length - 2] + ":" + tmp[tmp.length - 1];
            log.debug("adding service " + key + " to inventory");
            services.put(key, new Service(sT, this));
        }

        if (device.getDeviceList() != null) {
            for (DeviceDesc d : device.getDeviceList()) {
                getServicesFromDevice(d);
            }
        }
    }

    synchronized private InputStream httpRequest(HttpHost target, HttpRequest request, HttpContext context) throws IOException, UnauthorizedException {
        byte[] content;
        log.debug("try to request " + request.getRequestLine() + " from " + target.toURI());
        try (CloseableHttpResponse response = httpClient.execute(target, request, context)) {
            log.debug("got response " + response.getStatusLine());
            content = EntityUtils.toByteArray(response.getEntity());
            EntityUtils.consume(response.getEntity());
            log.debug("got content: " + new String(content));
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException(response.getStatusLine().toString());
            }

            if (content != null) {
                return new ByteArrayInputStream(content);
            } else {
                return new ByteArrayInputStream(new byte[0]);
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            if (isUnauthorizedException(e)) {
                throw new UnauthorizedException(e);
            } else {
                throw e;
            }
        }
    }

    private boolean isUnauthorizedException(IOException e) {
        return e.getMessage().contains("401");
    }

    public InputStream getXMLIS(String fileName) throws IOException, UnauthorizedException {
        HttpGet httpget = new HttpGet(fileName);
        return httpRequest(targetHost, httpget, context);
    }

    protected InputStream getSOAPXMLIS(String fileName, String urn, HttpEntity entity) throws IOException, UnauthorizedException {
        HttpPost httppost = new HttpPost(fileName);
        httppost.addHeader("soapaction", urn);
        httppost.addHeader("charset", "utf-8");
        httppost.addHeader("content-type", "text/xml");
        httppost.setEntity(entity);
        return httpRequest(targetHost, httppost, context);
    }

    public Service getService(String name) {
        return getServices().get(name);
    }

    public void printInfo() {
        System.out.println(name);
        System.out.println("----------------------------------");
        for (String a : services.keySet()) {
            System.out.println(a);
            Service s = services.get(a);
            for (String b : s.getActions().keySet()) {
                System.out.print("    ");
                System.out.println(b);
                System.out.print("       ");
                System.out.println(s.getActions().get(b).getArguments());
            }
        }
    }
}
