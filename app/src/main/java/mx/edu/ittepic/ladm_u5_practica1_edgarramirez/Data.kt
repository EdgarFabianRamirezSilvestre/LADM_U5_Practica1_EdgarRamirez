package mx.edu.ittepic.ladm_u5_practica1_edgarramirez

import com.google.firebase.firestore.GeoPoint

class Data{
    var nombre : String = ""
    var posicion1 : GeoPoint = GeoPoint(0.0, 0.0)
    var posicion2 : GeoPoint = GeoPoint(0.0, 0.0)
    var contenido : String = ""

    override fun toString(): String {
        return nombre+"\n"+posicion1.latitude+","+posicion1.longitude+"\n"+
                posicion2.latitude+","+posicion2.longitude+"\n"+contenido
    }//toString

    fun estoyEn(posicionActual:GeoPoint) : Boolean {
        if (posicionActual.latitude >= posicion1.latitude &&
            posicionActual.latitude <= posicion2.latitude){
            if (invertir(posicionActual.longitude) >= invertir(posicion1.longitude) &&
                invertir(posicionActual.longitude) <= invertir(posicion2.longitude)){
                return true
            }
        }
        return false
    }//estoyEn

    private fun invertir(valor:Double):Double{
        return valor*-1
    }//invertir

}//data