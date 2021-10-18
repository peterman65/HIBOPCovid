/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase se encarga de cargar y procesar todas las estimaciones de probabilidades 
 * y supervivencias de los ficheros de Javier/Miguel.
 * @author pmateo
 */


public class CargaPacientesYDistribuciones {
    static final int DatosIniciales =0;
    static final int NuevosPositivosPrediccion =1;    
    static final int ProbabilidadH_UCI_G1 =2;
    static final int Dias_antes_hospital_G1 = 3;
    static final int Dias_hospital_antes_uci_G1=4;
    static final int Dias_hospital_sin_uci_G1=5;
    static final int Dias_hospital_tras_uci_G1=6;
    static final int Dias_uci_G1=7;
    static final int ProbabilidadH_UCI_G2 =8;
    static final int Dias_antes_hospital_G2 = 9;
    static final int Dias_hospital_antes_uci_G2=10;
    static final int Dias_hospital_sin_uci_G2=11;
    static final int Dias_hospital_tras_uci_G2=12;
    static final int Dias_uci_G2=13;
    static final int situation_counts=14;
    static final int NuevosPositivosReales =15;        
    private String nombre_de_fichero[];
    private ArrayList<Double> aux_al[]=null;
    
    
    private double P_H_G1[]=null;
    private double P_UCIh_G1[]=null;    
    private double d_ant_hos_G1[][]=null;
    private double d_hos_ant_uci_G1[][]=null;
    private double d_hos_sin_uci_G1[][]=null;
    private double d_hos_tras_uci_G1[][]=null;
    private double d_uci_G1[][]=null;    
    private int numGrupos_G1=0;
    
    
    private double P_H_G2[]=null;
    private double P_UCIh_G2[]=null;    
    private double d_ant_hos_G2[][]=null;
    private double d_hos_ant_uci_G2[][]=null;
    private double d_hos_sin_uci_G2[][]=null;
    private double d_hos_tras_uci_G2[][]=null;
    private double d_uci_G2[][]=null;    
    
    private int nuevosPositivosTotales[][];
    private int numGrupos_G2=0;
    private String dataDirectory;
    private LocalDate from;
    private PriorityQueue<Usuario> pacientes;
    private boolean usarReales;
    
    public CargaPacientesYDistribuciones(String nf[],LocalDate f,String dd,boolean usarRealess){
      nombre_de_fichero = nf;        
      from =f;
      pacientes = new PriorityQueue<>();
      dataDirectory=dd;
      usarReales=usarRealess;
    }
    
    void cargaInfo_G1(){ 
        BufferedReader buf;
        String words[];
        String lineJustFetched;        
       //Fichero de probabilidad de entrar en hospital y UCI (dado que ha ido a hospital) Pacientes actuales
            //###########################
            try{
            buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[ProbabilidadH_UCI_G1]));                        
            lineJustFetched = buf.readLine();
            words = lineJustFetched.split(",");
            numGrupos_G1=words.length;
            P_H_G1 = new double[numGrupos_G1];
            for(int i=0;i<numGrupos_G1;i++){
                  P_H_G1[i]=Double.parseDouble(words[i]);
            }
            lineJustFetched = buf.readLine();
            words = lineJustFetched.split(",");
            P_UCIh_G1 = new double[numGrupos_G1];
            for(int i=0;i<numGrupos_G1;i++){
                  P_UCIh_G1[i]=Double.parseDouble(words[i]);
            }   
            buf.close();         
            //Ficheros de supervivencia G1
            //###########################
            aux_al = new ArrayList[numGrupos_G1];
            for(int i=0;i<numGrupos_G1;i++) aux_al[i]= new ArrayList<>();
            
            //Fichero supervivencia dias antes de hospital.             
            lee_fichero_supervivencia(aux_al,Dias_antes_hospital_G1, numGrupos_G1);
            d_ant_hos_G1= new double[numGrupos_G1][];
            carga_matriz_supervivencia(getD_ant_hos_G1(),aux_al, numGrupos_G1);
            for(int i=0;i<numGrupos_G1;i++) aux_al[i].clear();
               
            //Fichero supervivencia de dias hospital antes uci. 
            lee_fichero_supervivencia(aux_al,Dias_hospital_antes_uci_G1, numGrupos_G1);            
            d_hos_ant_uci_G1 = new double[numGrupos_G1][];
            carga_matriz_supervivencia(getD_hos_ant_uci_G1(),aux_al, numGrupos_G1);
            for(int i=0;i<numGrupos_G1;i++) aux_al[i].clear();
              
            //Fichero supervivencia de dias hospital sin uci. 
            lee_fichero_supervivencia(aux_al,Dias_hospital_sin_uci_G1, numGrupos_G1);
            d_hos_sin_uci_G1 =new double[numGrupos_G1][];
            carga_matriz_supervivencia(getD_hos_sin_uci_G1(),aux_al, numGrupos_G1);
            for(int i=0;i<numGrupos_G1;i++) aux_al[i].clear();

            //Fichero supervivencia de dias hospital tras uci. 
            lee_fichero_supervivencia(aux_al,Dias_hospital_tras_uci_G1, numGrupos_G1);
            d_hos_tras_uci_G1 =new double[numGrupos_G1][];
            carga_matriz_supervivencia(getD_hos_tras_uci_G1(),aux_al, numGrupos_G1);
            for(int i=0;i<numGrupos_G1;i++) aux_al[i].clear();
          
            //Fichero supervivencia de dias en uci. 
            lee_fichero_supervivencia(aux_al, Dias_uci_G1, numGrupos_G1);
            d_uci_G1 =new double[numGrupos_G1][];
            carga_matriz_supervivencia(getD_uci_G1(),aux_al, numGrupos_G1);
            for(int i=0;i<numGrupos_G1;i++) aux_al[i].clear();
            //Fin lectura ficheros supervivencia}
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
    }
 
 
    void cargaInfo_G2(){ 
        BufferedReader buf;
        String words[];
        String lineJustFetched;
        
       //Fichero de probabilidad de entrar en hospital y UCI (dado que ha ido a hospital) Pacientes actuales
            //###########################
            try{
            buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[ProbabilidadH_UCI_G2]));                        
            lineJustFetched = buf.readLine();
            words = lineJustFetched.split(",");
            numGrupos_G2=words.length;
            P_H_G2 = new double[numGrupos_G2];
            for(int i=0;i<numGrupos_G2;i++){
                  P_H_G2[i]=Double.parseDouble(words[i]);
            }
            lineJustFetched = buf.readLine();
            words = lineJustFetched.split(",");
            P_UCIh_G2 = new double[numGrupos_G2];
            for(int i=0;i<numGrupos_G2;i++){
                  P_UCIh_G2[i]=Double.parseDouble(words[i]);
            }   
            buf.close();         
            //Ficheros de supervivencia G2
            //###########################
            aux_al = new ArrayList[numGrupos_G2];
            for(int i=0;i<numGrupos_G2;i++) aux_al[i]= new ArrayList<>();
            
            //Fichero supervivencia dias antes de hospital.             
            lee_fichero_supervivencia(aux_al,Dias_antes_hospital_G2, numGrupos_G2);
            setD_ant_hos_G2(new double[numGrupos_G2][]);
            carga_matriz_supervivencia(getD_ant_hos_G2(),aux_al, numGrupos_G2);
            for(int i=0;i<numGrupos_G2;i++) aux_al[i].clear();
               
            //Fichero supervivencia de dias hospital antes uci. 
            lee_fichero_supervivencia(aux_al,Dias_hospital_antes_uci_G2, numGrupos_G2);            
            d_hos_ant_uci_G2 = new double[numGrupos_G2][];
            carga_matriz_supervivencia(getD_hos_ant_uci_G2(),aux_al, numGrupos_G2);
            for(int i=0;i<numGrupos_G2;i++) aux_al[i].clear();
              
            //Fichero supervivencia de dias hospital sin uci. 
            lee_fichero_supervivencia(aux_al,Dias_hospital_sin_uci_G2, numGrupos_G2);
            d_hos_sin_uci_G2 =new double[numGrupos_G2][];
            carga_matriz_supervivencia(getD_hos_sin_uci_G2(),aux_al, numGrupos_G2);
            for(int i=0;i<numGrupos_G2;i++) aux_al[i].clear();

            //Fichero supervivencia de dias hospital tras uci. 
            lee_fichero_supervivencia(aux_al,Dias_hospital_tras_uci_G2, numGrupos_G2);
            d_hos_tras_uci_G2 =new double[numGrupos_G2][];
            carga_matriz_supervivencia(getD_hos_tras_uci_G2(),aux_al, numGrupos_G2);
            for(int i=0;i<numGrupos_G2;i++) aux_al[i].clear();
          
            //Fichero supervivencia de dias en uci. 
            lee_fichero_supervivencia(aux_al, Dias_uci_G2, numGrupos_G2);
            d_uci_G2 =new double[numGrupos_G2][];
            carga_matriz_supervivencia(getD_uci_G2(),aux_al, numGrupos_G2);
            for(int i=0;i<numGrupos_G2;i++) aux_al[i].clear();
            //Fin lectura ficheros supervivencia}
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
    }
 
    
       
    /**
     * Traspasa la información de probabilidades de supervivencia almacenadas en
     * un arrayList a una matriz
     * @param matriz
     * @param al 
     */
    void carga_matriz_supervivencia(double matriz[][],ArrayList<Double> al[], int nge){
                for(int i=0;i<nge;i++) {
                matriz[i]= new double[al[i].size()];
                for(int j=0;j<matriz[i].length;j++){
                  matriz[i][j]=al[i].get(j);
                }
            }
    }
    
    
    
    void lee_fichero_supervivencia(ArrayList<Double> al[], int indiceFichero,int nge) {

        String lineJustFetched;
        String words[];
        boolean activo[]= new boolean[nge];
        int numActivos=nge;
        for(int i=0;i<nge;i++) activo[i]=true;
        try {
            BufferedReader buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[indiceFichero]));
            while (true) {
                lineJustFetched = buf.readLine();
                if (lineJustFetched == null) {
                    break;
                } else {
                    words = lineJustFetched.split(",");
                }
                for (int i = 0; i < nge; i++) {
                    if (activo[i] && Double.parseDouble(words[i]) >= 0) {
                        al[i].add(Double.parseDouble(words[i]));
                        if(Double.parseDouble(words[i])==0){
                            activo[i]=false;
                            numActivos--;
                        }
                    }
                    if(numActivos==0){
                        buf.close();
                        return;
                    }
                }
                
            }
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void leePacientesActuales(){ 
        LocalDate auxLD;
        int aux1,aux2; 
        String lineJustFetched;
        String words[];
        Usuario usuario;
        int codigoUsuario=(getPacientes()!=null)?getPacientes().size():0; 
      try {
            //Fichero de datos de estado inicial del sistema
            //###########################
            BufferedReader buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[DatosIniciales]));
            while (true) {
                lineJustFetched = buf.readLine();
                if (lineJustFetched == null)   break;
                else {
                    words = lineJustFetched.split(",");
                }
                usuario = new Usuario();
                usuario.setFecha_referencia(from);
                usuario.setGrupo_G1(Integer.parseInt(words[0])-1); //numero de 0 a 5 (comodidad)  
                usuario.setGrupo_G2(Integer.parseInt(words[1])-1); //numero de 0 a 5 (comodidad)  
                usuario.setTipo_de_paciente(Integer.parseInt(words[2]));
                usuario.setCodigoUsuario(""+codigoUsuario);
                //Utilizo el campo edad para almacenar los días desde el evento
                usuario.setEdad(Integer.parseInt(words[3]));
                pacientes.add(usuario);
                codigoUsuario++;
            }
            buf.close();          
            } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaPacientesYDistribuciones.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CargaPacientesYDistribuciones.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    void  leePacientesNuevos(){
        LocalDate auxLD;
        int aux1,aux2; 
        String lineJustFetched;
        String words[];
        Usuario usuario;
        int codigoUsuario=(getPacientes()!=null)?getPacientes().size():0;  
        
        //Fichero de nuevos positivos
        //###########################
        try { BufferedReader buf;
            if(usarReales)
             buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[NuevosPositivosReales])); 
            else
              buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[NuevosPositivosPrediccion]));   
            aux1=0;
            while (true) {
                lineJustFetched = buf.readLine();
                if (lineJustFetched == null)   break;
                aux1++;
            }
            buf.close(); 
            if(usarReales)
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[NuevosPositivosReales])); 
            else
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[NuevosPositivosPrediccion]));
            //buf = new BufferedReader(new FileReader(dataDirectory+File.separator+nombre_de_fichero[NuevosPositivosPrediccion])); 
            nuevosPositivosTotales= new int[getNumGrupos_G2()+1][aux1+1];
            aux1=0;
            auxLD = LocalDate.of(from.getYear(),from.getMonth(),from.getDayOfMonth());
            aux2=1;
            while (true) {
                lineJustFetched = buf.readLine();
                if (lineJustFetched == null)   break;
                else {
                    words = lineJustFetched.split(",");
                }
                
                for(int i = 0; i<getNumGrupos_G2();i++){
                    aux1= Integer.parseInt(words[i]);
                    nuevosPositivosTotales[getNumGrupos_G2()][aux2]+=aux1;
                    nuevosPositivosTotales[i][aux2]=aux1;
                    for(int j=0;j<aux1;j++){
                        usuario = new Usuario();
                        usuario.setFecha_referencia(auxLD.plusDays(aux2));
                        usuario.setGrupo_G1(Usuario.paciente_nuevo);
                        usuario.setGrupo_G2(i);
                        usuario.setTipo_de_paciente(Usuario.paciente_nuevo);
                        usuario.setCodigoUsuario(""+codigoUsuario);
                        getPacientes().add(usuario);
                        codigoUsuario++;
                    }
                }
                aux2++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaPacientesYDistribuciones.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CargaPacientesYDistribuciones.class.getName()).log(Level.SEVERE, null, ex);
        }    

    }
    
    
    /**
     * @return the P_H_G1
     */
    public double[] getP_H_G1() {
        return P_H_G1;
    }

    /**
     * @return the P_UCIh_G1
     */
    public double[] getP_UCIh_G1() {
        return P_UCIh_G1;
    }

    /**
     * @param P_UCIh_G1 the P_UCIh_G1 to set
     */
    public void setP_UCIh_G1(double[] P_UCIh_G1) {
        this.P_UCIh_G1 = P_UCIh_G1;
    }

    /**
     * @return the d_ant_hos_G1
     */
    public double[][] getD_ant_hos_G1() {
        return d_ant_hos_G1;
    }

    /**
     * @return the d_hos_ant_uci_G1
     */
    public double[][] getD_hos_ant_uci_G1() {
        return d_hos_ant_uci_G1;
    }

    /**
     * @return the d_hos_sin_uci_G1
     */
    public double[][] getD_hos_sin_uci_G1() {
        return d_hos_sin_uci_G1;
    }

    /**
     * @return the d_hos_tras_uci_G1
     */
    public double[][] getD_hos_tras_uci_G1() {
        return d_hos_tras_uci_G1;
    }

    /**
     * @return the d_uci_G1
     */
    public double[][] getD_uci_G1() {
        return d_uci_G1;
    }

    /**
     * @return the numGrupos_G1
     */
    public int getNumGrupos_G1() {
        return numGrupos_G1;
    }

    /**
     * @return the P_H_G2
     */
    public double[] getP_H_G2() {
        return P_H_G2;
    }

    /**
     * @return the P_UCIh_G2
     */
    public double[] getP_UCIh_G2() {
        return P_UCIh_G2;
    }

    /**
     * @return the d_ant_hos_G2
     */
    public double[][] getD_ant_hos_G2() {
        return d_ant_hos_G2;
    }

    /**
     * @param d_ant_hos_G2 the d_ant_hos_G2 to set
     */
    public void setD_ant_hos_G2(double[][] d_ant_hos_G2) {
        this.d_ant_hos_G2 = d_ant_hos_G2;
    }

    /**
     * @return the d_hos_ant_uci_G2
     */
    public double[][] getD_hos_ant_uci_G2() {
        return d_hos_ant_uci_G2;
    }

    /**
     * @return the d_hos_sin_uci_G2
     */
    public double[][] getD_hos_sin_uci_G2() {
        return d_hos_sin_uci_G2;
    }

    /**
     * @return the d_hos_tras_uci_G2
     */
    public double[][] getD_hos_tras_uci_G2() {
        return d_hos_tras_uci_G2;
    }

    /**
     * @return the d_uci_G2
     */
    public double[][] getD_uci_G2() {
        return d_uci_G2;
    }

    /**
     * @return the numGrupos_G2
     */
    public int getNumGrupos_G2() {
        return numGrupos_G2;
    }

    /**
     * @return the pacientes
     */
    public PriorityQueue<Usuario> getPacientes() {
        return pacientes;
    }
    public void setPacientes(PriorityQueue<Usuario> p) {
        pacientes=p;
    }

    /**
     * matriz: filas = grupos de edad +1, componente 0 contiene los totales
     *         columnas = dias
     * @return the nuevosPositivosTotales
     * 
     */
    public int[][] getNuevosPositivosTotales() {
        return nuevosPositivosTotales;
    }
    
}
