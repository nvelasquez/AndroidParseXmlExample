package com.zerofull800.parsexmlexample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends Activity {

    List<Object> mObjectList = new ArrayList<Object>();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se carga el listview del layout.
        lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(new ListAdapter());

        //Esto es para correrlo en el Hilo de la UI ya que el Task modifica el ListView
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Se ejecuta la Class privada Task, la cual baja y parsea la informacion y alimenta al listview.
                new Task().execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Esta calase es un adaptador para presentar los objetos en el ListView
     */
    private class ListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mObjectList.size();
        }

        @Override
        public Object getItem(int i) {
            return mObjectList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LinearLayout linearLayout;
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            //Se elige si la vista ya esta cargada no cargarla de nuevo.
            if (view == null){
                linearLayout = (LinearLayout)inflater.inflate(R.layout.item_layout, null);
            }else {
                linearLayout = (LinearLayout)view;
            }

            //Objeto customizado para agregar a la vista.
            Object object = mObjectList.get(i);

            WebView webView = (WebView)linearLayout.findViewById(R.id.webView);
            TextView textView = (TextView)linearLayout.findViewById(R.id.textView);

            //Se carga el contenido en un minibrowser, el atributo content del Objeto object es un Html.
            webView.loadDataWithBaseURL(null, object.content, "text/html", "utf-8", null);
            textView.setText(object.title);

            //Se retorna un layout con varios componentes agregados.
            //Esto se hace si se quiere presentar mas de un componente en el ListView.
            return linearLayout;
        }
    }


    /**
     * Clase con la tarea para bajar la informacion y parsearla.
     */
    private class Task extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Se llena la lista de objetos con lo que se retorne de la conexion y se parsee.
                mObjectList = Parseador.parse();
            } catch (IOException e) {
                showMessage("Error bajando el archivo");
                e.printStackTrace();
            } catch (SAXException e) {
                showMessage("Error parseando el XML");
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                showMessage("Error parseando el XML");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Se refresca el listview con la nueva informacion.
            //Ese meto manda a redibujar todos los objetos.
            lv.invalidateViews();
            super.onPostExecute(aVoid);
        }
    }

    //Mostrar mensaje en pantalla.
    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();;
    }
}
