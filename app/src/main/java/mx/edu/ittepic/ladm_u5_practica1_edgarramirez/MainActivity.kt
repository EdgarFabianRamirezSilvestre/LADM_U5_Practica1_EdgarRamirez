package mx.edu.ittepic.ladm_u5_practica1_edgarramirez

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    var REQUEST_PERMISOS = 111
    lateinit var locacion : LocationManager
    var pos1 : Location = Location("")
    var pos2 : Location = Location("")


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solicitarPermisos()

        baseRemota.collection("tecnologico")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        lblUbicaciones.setText("ERROR: "+firebaseFirestoreException.message)
                        return@addSnapshotListener
                    }

                    var resultado = ""
                    posicion.clear()
                    for(document in querySnapshot!!){
                        var data = Data()
                        data.nombre = document.getString("nombre").toString()
                        data.posicion1 = document.getGeoPoint("posicion1")!!
                        data.posicion2 = document.getGeoPoint("posicion2")!!
                        data.contenido = document.getString("contenido").toString()

                        resultado += data.toString()+"\n\n"
                        posicion.add(data)
                    }

                    lblUbicaciones.setText(resultado)
                }

        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, oyente)

        btnBuscarUbicacion.setOnClickListener {
            baseRemota.collection("tecnologico")
                    .whereEqualTo("nombre", txtBuscarNombre.getText().toString())
                    .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                        if(firebaseFirestoreException != null){
                            lblMostrar.setText("ERROR, NO EXISTE CONEXIÓN CON LA BD")
                            return@addSnapshotListener
                        }

                        var contenido = ""

                        for(document in  querySnapshot!!){
                            pos1.latitude = document.getGeoPoint("posicion1")!!.latitude
                            pos1.longitude = document.getGeoPoint("posicion1")!!.longitude

                            pos2.latitude = document.getGeoPoint("posicion2")!!.latitude
                            pos2.longitude = document.getGeoPoint("posicion2")!!.longitude
                            contenido = document.getString("contenido")!!
                        }

                        var r = "COORDENADAS:\n(${(pos1.latitude)}, ${pos1.longitude}),(${pos2.latitude}, ${pos2.longitude}) \nContenido: ${contenido}"
                        lblMostrar.setText(r)
                    }
        }//btnBuscarUbicacion

    }//onCreate

    private fun solicitarPermisos() {
        var permisoAccessFind = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(permisoAccessFind != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_PERMISOS)
        }
    }//solicitarPermisos

}//main

class Oyente(puntero:MainActivity) : LocationListener {

    var p = puntero

    override fun onLocationChanged(location: Location) {
        p.lblActual.setText("Ubicacion actual:\n${location.latitude}, ${location.longitude}")
        p.lblEncuentras.setText("")
        var geoPosicionGPS = GeoPoint(location.latitude, location.longitude)

        for (item in p.posicion) {
            if (item.estoyEn(geoPosicionGPS)) {
                p.lblEncuentras.setText("Te encuentras en: ${item.nombre}")
            }else{
                p.lblEncuentras.setText("No te encuentras en alguna ubicacion registrada")
            }
        }
    }//onLocationChanged

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }
    override fun onProviderEnabled(provider: String?) {

    }
    override fun onProviderDisabled(provider: String?) {

    }
}//Oyente
