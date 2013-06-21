package com.zerofull800.parsexmlexample;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Nestor_Velasquez on 6/21/13.
 */
public class Parseador {
    private static final String URL = "http://sports.yahoo.com/mlb/rss.xml";

    /**
     *
     * @return Retorna un objeto InputStream descargado de la conexion.
     * @throws IOException Error de conexion.
     */
    private static InputStream connect() throws IOException {
        //Se hace la conexion y se solicita el request.
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(URL);
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        InputStream stream;
        //Se solicita el contenido de la conexion.
        InputSource source = new InputSource(entity.getContent());
        //Se coloca la codificacion de caracteres.
        source.setEncoding("utf-8");
        //Se retorna el contenido codificado.
        stream = source.getByteStream();
        return stream;
    }

    /**
     *
     * @return Retorna una lista de objetos parseados para procesar.
     * @throws IOException Error de conectividad o redes.
     * @throws SAXException Error del parseador.
     * @throws ParserConfigurationException Error de la estructura al parsearlo.
     */
    public static List<Object> parse() throws IOException, SAXException, ParserConfigurationException {

        //Inicializar componentes.
        InputStream stream = connect();
        List<Object> objects = new ArrayList<Object>();

        //Se crea un documento para almacenar el XML.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(stream);

        //Se extrae una lista de todos los elementos que vamos a parsear.
        NodeList elements = document.getElementsByTagName("item");

        //Se parsea cada elemento y se agrega a la lista.
        for (int i = 0; i < elements.getLength(); i++){
            Element el = (Element)elements.item(i);
            Object object = new Object();

            object.title = getNodeValue(el, "title");
            object.content = getNodeValue(el, "description");

            objects.add(object);
        }

        return objects;
    }

    /**
     *
     * @param el Objeto Element con la informacion del Item.
     * @param tag El atributo que deseamos parsear del elemento.
     * @return Retorna un String con el valor del atributo
     */
    private static String getNodeValue(Element el, String tag) {
        try {
            return el.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {
            return "";
        }
    }
}
