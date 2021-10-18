/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.PriorityQueue;
import java.util.Random;
import javax.swing.JOptionPane;




/**
 *
 * @author pmateo
 */
public class generadorDePacientesDistribEmpirica {

  
    
    private Random rnd = null;
    
    private PriorityQueue<Usuario> listaPacientesSim;
    private PriorityQueue<Usuario> listaPacientesReal;
    private Usuario usuario = null;
    private LocalDate from;
    private LocalDate until;
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private boolean verbose = false;
    private CargaPacientesYDistribuciones datos;
    
    generadorDePacientesDistribEmpirica( LocalDate fr, LocalDate unt,Random rrnndd,CargaPacientesYDistribuciones d) {

       from=fr;
       until=unt;
       listaPacientesSim = new PriorityQueue<>();
       rnd=rrnndd;
       ;
       datos=d;
        
       
    }
    
    /**
     * Esta función se encarga de leer los ficheros que me ha pasado Javier
     * y a partir de dicha información y las fórmulas correspondientes 
     * generar los pacientes con toda su información, para posteriormente
     * generar los eventos y generar la salida correspondiente.
     */
    void genera_pacientes() {
        int diasAux,d;
        double cuantil=0.9;
     
        listaPacientesSim=datos.getPacientes();

        for (Usuario u : listaPacientesSim) {
                 // Generando la historia de los pacientes nuevos
                 if (u.getTipo_de_paciente() == 99) {
                    if (rnd.nextDouble() < datos.getP_H_G2()[u.getGrupo_G2()]) u.setIngreso_hospital(true); else u.setIngreso_hospital(false); 
                    if (u.isIngreso_hospital() && rnd.nextDouble() < datos.getP_UCIh_G2()[u.getGrupo_G2()]) u.setIngreso_UCI(true); else u.setIngreso_UCI(false);   
                    if(u.isIngreso_hospital()){
                        //Ignoramos la fecha de referencia, no la cargamos ni la procesamos posterioremente.
                        //Sorteamos fecha de entrada al hospital
                        diasAux = sorteoRuletaSupervivencia(datos.getD_ant_hos_G2()[u.getGrupo_G2()]);
                        u.setFecha_ingreso_hospital(u.getFecha_referencia().plusDays(diasAux));                        
                        if(u.isIngreso_UCI()){
                            //Si entra en la UCI sorteamos fecha de entrada a UCI
                            diasAux= sorteoRuletaSupervivencia(datos.getD_hos_ant_uci_G2()[u.getGrupo_G2()]);
                            u.setFecha_ingreso_UCI(u.getFecha_ingreso_hospital().plusDays(diasAux));
                            //Si entra a UCI sorteamos fecha de salida de UCI
                            diasAux= sorteoRuletaSupervivencia(datos.getD_uci_G2()[u.getGrupo_G2()]);
                            u.setFecha_alta_UCI(u.getFecha_ingreso_UCI().plusDays(diasAux));
                            //Tras salir de UCI sorteamos los días hasta salir de hospital
                            diasAux= sorteoRuletaSupervivencia(datos.getD_hos_tras_uci_G2()[u.getGrupo_G2()]);
                            u.setFecha_de_alta_hospital(u.getFecha_alta_UCI().plusDays(diasAux));
                        }else{
                            //Si no entra en UCI sorteamos fecha de salida de hospital
                            diasAux= sorteoRuletaSupervivencia(datos.getD_hos_sin_uci_G2()[u.getGrupo_G2()]);
                            u.setFecha_de_alta_hospital(u.getFecha_ingreso_hospital().plusDays(diasAux));
                        }                        
                    }
                    u.setResultado(Usuario.recuperado);//NO HAY FALLECIMIENTOS (EXPLICITOS)
                }
                else{ //Estos son los pacientes del día de arranque, clasificados en los tipos que sean
                     //paciente_positivo=1; paciente_hospitalizado=2;
                     //paciente_en_UCI=3;paciente_tras_UCI=4;
                    int diasDesdeEvento = u.getEdad(); //lo guardábamos temporalmente ahí
                    double pTx;
                    double PCond;
                    switch (u.getTipo_de_paciente()) {
                        case 1://Paciente que ha dado positivo previamente (0,1,2,3, 0 indica que ha dado positivo ese dia), 'x' dias antes.
                            //Dada esa información se calcula la probabilidad de que entre al hospital, P_{H|x}, almacenado en PCond                            
                            //HE CAMBIADO LAS LINEAS DEL IF
                            if (diasDesdeEvento >= datos.getD_ant_hos_G1()[u.getGrupo_G1()].length) {
                                PCond=0;
                            }else{
                                //diasDesdeEvento=calculaPercentil(cuantil, datos.getD_ant_hos_G1()[u.getGrupo_G1()]);
                                pTx = datos.getD_ant_hos_G1()[u.getGrupo_G1()][diasDesdeEvento];
                                PCond = (datos.getP_H_G1()[u.getGrupo_G1()] * pTx) / (datos.getP_H_G1()[u.getGrupo_G1()] * pTx + (1 - datos.getP_H_G1()[u.getGrupo_G1()]));                            
                            }
                            //Tras actualizar la probabilidad se sortea si entra al hospital
                            if (rnd.nextDouble() < PCond) {
                                u.setIngreso_hospital(true);
                            } else {
                                u.setIngreso_hospital(false);
                            }

                            if (u.isIngreso_hospital()) {
                                //Si va a entrar al hospital hay que sortear cuando, para ello nuevamente se tendrá
                                //en cuenta los días que lleva desde que dio positivo.
                                d = sorteoRuletaSupervivencia(datos.getD_ant_hos_G1()[u.getGrupo_G1()], diasDesdeEvento);
                                //Entrara al hospita a los 'd' dias del evento, pero ya han pasado diasDesdeEvento días así
                                //que a partir de ahora la entrda será en d-diasDesdeEvento a partir de from.
                                u.setFecha_ingreso_hospital(from.plusDays(d - diasDesdeEvento)); 
                                //Ahora sorteamos si entra en la uci (a partir de aquí como si fuera nuevo)
                                if (rnd.nextDouble() < datos.getP_UCIh_G1()[u.getGrupo_G1()]) {
                                    u.setIngreso_UCI(true);
                                } else {
                                    u.setIngreso_UCI(false);
                                }
                                if (u.isIngreso_UCI()) {
                                    //Si entra en la UCI sorteamos fecha de entrada a UCI
                                    diasAux = sorteoRuletaSupervivencia(datos.getD_hos_ant_uci_G1()[u.getGrupo_G1()]);
                                    u.setFecha_ingreso_UCI(u.getFecha_ingreso_hospital().plusDays(diasAux));
                                    //Si entra a UCI sorteamos fecha de salida de UCI
                                    diasAux = sorteoRuletaSupervivencia(datos.getD_uci_G1()[u.getGrupo_G1()]);
                                    u.setFecha_alta_UCI(u.getFecha_ingreso_UCI().plusDays(diasAux));
                                    //Tras salir de UCI sorteamos los días hasta salir de hospital
                                    diasAux = sorteoRuletaSupervivencia(datos.getD_hos_tras_uci_G1()[u.getGrupo_G1()]);
                                    u.setFecha_de_alta_hospital(u.getFecha_alta_UCI().plusDays(diasAux));
                                } else {
                                    //Si no entra en UCI sorteamos fecha de salida de hospital
                                    diasAux = sorteoRuletaSupervivencia(datos.getD_hos_sin_uci_G1()[u.getGrupo_G1()]);
                                    u.setFecha_de_alta_hospital(u.getFecha_ingreso_hospital().plusDays(diasAux));
                                }
                            }
                            u.setResultado(Usuario.recuperado);//NO HAY FALLECIMIENTOS (EXPLICITOS)
                            break;                                   
                         case 2://Paciente que lleva 'x' días hospitalizado y que no ha entrado en UCI, calculamos la probabilidad de entrar
                             //en uCI de acuerdo a los 'x' días que lleva y a su grupo de edad.
                             
                            //Lo registramos como dentro del hospital
                            u.setIngreso_hospital(true);
                            u.setResultado(Usuario.recuperado); //Nadie fallece en este modelo
                            if (diasDesdeEvento >= datos.getD_hos_ant_uci_G1()[u.getGrupo_G1()].length) { //diasDesdeEvento mayor que vector de permanencia antes de uci (T2)
                                 PCond = 0;
                                 if (diasDesdeEvento >= datos.getD_hos_sin_uci_G1()[u.getGrupo_G1()].length) {
                                     diasDesdeEvento = calculaPercentil(cuantil, datos.getD_hos_sin_uci_G1()[u.getGrupo_G1()]);
                                 }
                             } else {
                                 pTx = datos.getD_hos_ant_uci_G1()[u.getGrupo_G1()][diasDesdeEvento];
                                 PCond = (datos.getP_UCIh_G1()[u.getGrupo_G1()] * pTx)
                                         / (datos.getP_UCIh_G1()[u.getGrupo_G1()] * pTx
                                         + datos.getD_hos_sin_uci_G1()[u.getGrupo_G1()][diasDesdeEvento] * (1 - datos.getP_UCIh_G1()[u.getGrupo_G1()]));

                             }
                            //Tras actualizar la probabilidad se sortea si entra a la UCI
                            if (rnd.nextDouble() < PCond) {
                                u.setIngreso_UCI(true);
                            } else {
                                u.setIngreso_UCI(false);
                            }
                            if (u.isIngreso_UCI()) {
                            //Sorteamos el tiempo hasta entrar en uci
                                d = sorteoRuletaSupervivencia(datos.getD_hos_ant_uci_G1()[u.getGrupo_G1()], diasDesdeEvento);
                                //Entrara la UCI a los 'd' dias del evento, pero ya han pasado diasDesdeEvento días así
                                //que a partir de ahora la entrda será en d-diasDesdeEvento a partir de from.
                                u.setFecha_ingreso_UCI(from.plusDays(d - diasDesdeEvento));
                                //sorteamos fecha de salida de UCI
                                diasAux = sorteoRuletaSupervivencia(datos.getD_uci_G1()[u.getGrupo_G1()]);
                                u.setFecha_alta_UCI(u.getFecha_ingreso_UCI().plusDays(diasAux));
                                //Tras salir de UCI sorteamos los días hasta salir de hospital
                                diasAux = sorteoRuletaSupervivencia(datos.getD_hos_tras_uci_G1()[u.getGrupo_G1()]);
                                u.setFecha_de_alta_hospital(u.getFecha_alta_UCI().plusDays(diasAux));
                            }
                            else{
                                //Sorteamos el tiempo restante en el hospital
                                if(diasDesdeEvento>=datos.getD_hos_sin_uci_G1()[u.getGrupo_G1()].length) 
                                    diasDesdeEvento=calculaPercentil(cuantil,datos.getD_hos_sin_uci_G1()[u.getGrupo_G1()]);                                    
                                d = sorteoRuletaSupervivencia(datos.getD_hos_sin_uci_G1()[u.getGrupo_G1()], diasDesdeEvento);
                                //Añadimos a los diasDesdeEvento los dias retantes
                                u.setFecha_de_alta_hospital(from.plusDays(d - diasDesdeEvento));
                            }
                            break;
                         case 3: //Paciente que lleva 'x' días en UCI le generamos el tiempo restante de UCI y de hospital después
                                 u.setIngreso_hospital(true);
                                 u.setIngreso_UCI(true);
                                 u.setResultado(Usuario.recuperado);
                                 if(diasDesdeEvento>=datos.getD_uci_G1()[u.getGrupo_G1()].length) 
                                     diasDesdeEvento=calculaPercentil(cuantil,datos.getD_uci_G1()[u.getGrupo_G1()]);  
                                d = sorteoRuletaSupervivencia(datos.getD_uci_G1()[u.getGrupo_G1()], diasDesdeEvento);
                                u.setFecha_alta_UCI(from.plusDays(d - diasDesdeEvento));
                                //Tras salir de UCI sorteamos los días hasta salir de hospital
                                diasAux = sorteoRuletaSupervivencia(datos.getD_hos_tras_uci_G1()[u.getGrupo_G1()]);
                                u.setFecha_de_alta_hospital(u.getFecha_alta_UCI().plusDays(diasAux));
                                break;
                         case 4:
                                u.setIngreso_hospital(true);
                                u.setIngreso_UCI(true);
                                u.setResultado(Usuario.recuperado);
                                //Ha salido de UCI y lleva diasdDesdeEvento en hospital, sorteamos los días restantes
                                //de hospital
                                if(diasDesdeEvento>=datos.getD_hos_tras_uci_G1()[u.getGrupo_G1()].length) 
                                     diasDesdeEvento=calculaPercentil(cuantil,datos.getD_hos_tras_uci_G1()[u.getGrupo_G1()]);  
                                d = sorteoRuletaSupervivencia(datos.getD_hos_tras_uci_G1()[u.getGrupo_G1()],diasDesdeEvento);
                                u.setFecha_de_alta_hospital(from.plusDays(d-diasDesdeEvento));
                                break;
                         default:
                            if(verbose) JOptionPane.showMessageDialog(null,"Paciente tipo "+u.getTipo_de_paciente()+ " desconocido.");
                     }
                }
            }
      
        
        //Ya tenemos los usuarios generados y los ficheros de probabilidades 
        //y supervivencias cargados, ahora dependiendo de su tipo generamos todas sus
        //trazas con los tiempos de permanencia correspondientes a cada fase
        
    }
    /**
     * función para generar variables aleatorias discretas con valores
     * no equiprobables, a partir de función de distribución y supervivencia.
     * 
     * @return 
     */
    int sorteoRuletaSupervivencia(double sup[]) {
        
        double r = rnd.nextDouble();
        for (int i = 0; i < sup.length; i++) {
            if (r > sup[i]) {
                return i;                
            }
            continue;
        }
        throw new IllegalStateException();
    }

    //Para sorteo de probabilidad condicional del tiempo de resto de estado
    int sorteoRuletaSupervivencia(double sup[],int indice_x) {        
        
        if(indice_x>= sup.length){
            System.exit(1);
        }
        double r = rnd.nextDouble()*sup[indice_x]; 
        if(r==0)return indice_x;
        for (int i = indice_x+1; i < sup.length; i++) {
            if (r > sup[i]) {
                return i;                
            }
            continue;
        }
        throw new IllegalStateException();
    }
        
    int sorteoRuletaDistribucion(double cdf[]) {
        
        double r = rnd.nextDouble();
        for (int i = 0; i < cdf.length; i++) {
            if (r > cdf[i]) {
                continue;
            }
            return i;
        }
        throw new IllegalStateException();
    }
    
    int[] sorteoRuletaDistribucion(double cdf[], int n) {
        
        int resultado[]= new int[n];
        double r;
        while(n>0){
        r = rnd.nextDouble();        
        for (int i = 0; i < cdf.length; i++) {
            if (r > cdf[i]) {
                continue;
            }
            resultado[--n]=i;
            break;
        }
        }
        return resultado;
    }
    
    
 
    
  
      /**
     * Primer valor k, tal que 1-p >= P(T>k)
     *
     * @param p
     * @param prob
     * @return
     */
    int calculaPercentil(double p, double probSup[]) {
        for (int i = 0; i < probSup.length; i++) {
            if (probSup[i] <= 1 - p) {
                return i;
            }
        }
        return 1;
    }
    
     /**
     * @return the numero_grupos_edad
     */
   // public int getNumero_grupos_edad() {
  //      return numero_grupos_edad;
   // }
        /**
     * @return the listaPacientesSim
     */
    public PriorityQueue<Usuario> getListaPacientes() {
        return listaPacientesSim;
    }
}
