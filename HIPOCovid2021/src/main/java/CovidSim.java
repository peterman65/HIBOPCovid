/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pmateo
 */
public class CovidSim {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        //String fich[]={"./datosReducido.csv"};
        //String fich[]={"./Data/datos_iniciales_noviembre.txt",
        //               "./Data/nuevos_positivos_noviembre.txt",
        //               "./Data/probabilidades.txt",
        //               "./Data/dias_antes_hospital.txt",
        //               "./Data/dias_hospital_antes_uci.txt",
        //               "./Data/dias_hospital_sin_uci.txt",
        //               "./Data/dias_hospital_tras_uci.txt",
        //               "./Data/dias_uci.txt"
        //};
        
        PantallaPrincipalA pp = new PantallaPrincipalA();
        pp.setVisible(true);
        
       // Simulador  sim = new Simulador(fich,generadorDePacientesDistribEmpirica.JavierMiguel,1);
      //  sim.execute();
    }
    
}
