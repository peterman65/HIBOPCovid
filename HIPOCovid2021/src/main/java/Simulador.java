/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;



/**
 *
 * @author pmateo
 */
public class Simulador extends SwingWorker<Integer,Integer> { 
   
    /**
     * @return the usaPositivosUsuario
     */
    public boolean isUsaPositivosUsuario() {
        return usaPositivosUsuario;
    }

    /**
     * @param usaPositivosUsuario the usaPositivosUsuario to set
     */
    public void setUsaPositivosUsuario(boolean usaPositivosUsuario) {
        this.usaPositivosUsuario = usaPositivosUsuario;
    }

    /**
     * @return the from
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * @return the until
     */
    public LocalDate getUntil() {
        return until;
    }

    /**
     * @return the data_UCI
     */
    public double[][][] getData_UCI() {
        return data_UCI;
    }

    /**
     * @return the data_Hospital
     */
    public double[][][] getData_Hospital() {
        return data_Hospital;
    }
//El string lo usamos para escribir en un label el día, o la hora o lo que sea, 
                                                             //que marque la evolución 
    private LocalDate from;
    private LocalDate until;
    private String ficheros[];
    private int numero_de_replicas=1;
    private double data_UCI[][][]=null;
    private double data_Hospital[][][]=null;
    private int nuevosPostivosTotales[][]=null;
    private int horizonte;
    private long semilla;
    private JTextArea logTextArea;
    private JButton resSimulacionText;
    private JButton resSimulacionGrf;
    private JButton resSimulacionText2;
    private JButton resSimulacionGrf2;
    
    private Random rnd;    
    private DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu");     
    private generadorDePacientesDistribEmpirica gdp = null;
    private PriorityQueue<Evento> listaEventos = new PriorityQueue(); //Contiene la cola de eventos, 
    private CargaPacientesYDistribuciones datos;
    private String dataDirectory;
    private boolean usarReales;
    private String nombreConjunto;
    private ResourceBundle bundle;
    private boolean flagPrint;
    private boolean usaPositivosUsuario;
    private showProgress barra;

    
    Simulador(String fich[], LocalDate f, LocalDate u, int nrep, JTextArea resTA, JButton jBResText, JButton jBResGrf,JButton jBResText2, 
                JButton jBResGrf2, long seed,String dataDir, boolean usarRealess,String nCon,ResourceBundle bndl,boolean fpr,boolean uPu) {
        from = f;//LocalDate.of(2020, 11,15);
        until = u;//LocalDate.of(2020, 12,15);       
        horizonte = (int) ChronoUnit.DAYS.between(from, until) + 1;
        ficheros = fich;
        dataDirectory=dataDir;
        numero_de_replicas = nrep;
        semilla = seed;
        logTextArea = resTA;
        resSimulacionText = jBResText;
        resSimulacionGrf = jBResGrf;
        resSimulacionText2 = jBResText2;
        resSimulacionGrf2 = jBResGrf2;
        usarReales=usarRealess;
        bundle=bndl;
        nombreConjunto=nCon;
        flagPrint=fpr;
        usaPositivosUsuario=uPu;
        if (semilla != -1) {
            rnd = new Random(seed);
        } else {
            rnd = new Random();
        }
       
    }

  
           
           
    @Override
    protected Integer doInBackground() {
        
      
     if(flagPrint){
      barra = new showProgress(bundle,bundle.getString("showprogress.simulacion"));
        barra.setVisible(true);
     }
        //General
        datos = new CargaPacientesYDistribuciones(ficheros, getFrom(),dataDirectory,usarReales);        
        datos.cargaInfo_G1();
        datos.cargaInfo_G2();
        datos.leePacientesNuevos();
        datos.leePacientesActuales();
        nuevosPostivosTotales=datos.getNuevosPositivosTotales();
        
        int numGruposG1=datos.getNumGrupos_G1();
        int numGruposG2=datos.getNumGrupos_G2();
        gdp = new generadorDePacientesDistribEmpirica(getFrom(), getUntil(),rnd,datos);
       
        //Para cada réplica
        //Usuario usuario=null;
        Evento ev=null;
        int dia;
        for (int r = 0; r < numero_de_replicas; r++) {
            //Generamos pacientes
            //publish("Procesando réplica "+r+" generando pacientes.");
            if(flagPrint)// && Math.IEEEremainder(r, numero_de_replicas/10.0)==0)
                publish(r);

                //publish(String.format(bundle.getString("mensaje.procesandoreplica"),r,numero_de_replicas));
          //  datos.setPacientes(null); 
           //datos.leePacientesNuevos();//
          // datos.leePacientesActuales();
           
            gdp.genera_pacientes();
            
            if(data_UCI==null)
                data_UCI = new double[numGruposG2][horizonte][numero_de_replicas];
            if(data_Hospital==null)
                data_Hospital= new double[numGruposG2][horizonte][numero_de_replicas];
            //Vaciamos lista de eventos
            if(listaEventos!=null)
                listaEventos.clear();
            //Generamos eventos, repasamos uno a uno los pacientes e introducimos los eventos que generan
            
           for(Usuario usuario: gdp.getListaPacientes()){
                switch(usuario.getTipo_de_paciente()){
                    case Usuario.paciente_nuevo: //Nuevo
                    case Usuario.paciente_positivo:  //paciente que lleva x dias tras positivo y no ha entrado al sistema
                        if(usuario.getFecha_ingreso_hospital()!=null && !usuario.getFecha_ingreso_hospital().isAfter(until))
                            listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.EntradaHospital,usuario.getFecha_ingreso_hospital(),usuario.getGrupo_G2()));
                    case Usuario.paciente_hospitalizado: //Paceinte que lleva x días hospitalizado
                        if(usuario.getFecha_ingreso_UCI()!=null && !usuario.getFecha_ingreso_UCI().isAfter(until))
                            listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.EntradaUci,usuario.getFecha_ingreso_UCI(),usuario.getGrupo_G2()));
                        //Nuevo
                        if(usuario.getTipo_de_paciente()==Usuario.paciente_hospitalizado)
                            listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.EntradaHospital, getFrom(),usuario.getGrupo_G2()));                        
                        
                    case Usuario.paciente_en_UCI: //paciente con x dias en uci    
                        if(usuario.getFecha_alta_UCI()!=null && !usuario.getFecha_alta_UCI().isAfter(until)) {
                            listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.SalidaUci,usuario.getFecha_alta_UCI(),usuario.getGrupo_G2()));                            
                        }
                        //Nuevo
                        if(usuario.getTipo_de_paciente()==Usuario.paciente_en_UCI){
                            listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.EntradaHospital, getFrom(),usuario.getGrupo_G2()));
                            listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.EntradaUci, getFrom(),usuario.getGrupo_G2()));
                        }
                        
                    case Usuario.paciente_tras_UCI: //paciente salido de uci sigue en hospital hasta alta   
                        
                        if(usuario.getFecha_de_alta_hospital()!=null && !usuario.getFecha_de_alta_hospital().isAfter(until)){
                          if(usuario.getResultado()==Usuario.recuperado){
                              if(usuario.getFecha_ingreso_UCI()!=null)
                              listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.SalidaRecuperadoTrasHospitalConUCI,usuario.getFecha_de_alta_hospital(),usuario.getGrupo_G2()));                              
                              else
                              listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.SalidaRecuperadoTrasHospitalSinUCI,usuario.getFecha_de_alta_hospital(),usuario.getGrupo_G2()));                                  
                          }else{//Muerto
                              if(usuario.getFecha_fallecimiento()!=null){
                                  if(!usuario.getFecha_de_alta_hospital().isAfter(usuario.getFecha_fallecimiento()))
                                    listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.SalidaMuertoTrasHospital,usuario.getFecha_fallecimiento(),usuario.getGrupo_G2()));
                                  else
                                    listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.SalidaMuertoEnHospital,usuario.getFecha_fallecimiento(),usuario.getGrupo_G2()));
                              }
                          }
                        
                        }
                        //Nuevo
                        if(usuario.getTipo_de_paciente()==Usuario.paciente_tras_UCI)
                            listaEventos.add(new Evento(usuario.getCodigoUsuario(),usuario.getTipo_de_paciente(),Evento.EntradaHospital, getFrom(),usuario.getGrupo_G2()));                        
                        break;
                    default: JOptionPane.showMessageDialog(null,String.format(bundle.getString("mensaje.tipopacientedesconocido"),usuario.getTipo_de_paciente()));
                }//Fswitch
                usuario.resetSimulData();
            }
            //Tenemos todos los eventos cargados, ahora los procesamos
             try{
            while (listaEventos.size() > 0) {
                ev = listaEventos.poll();           
                dia=(int)ChronoUnit.DAYS.between(getFrom(), ev.getInstante());  //posicion en Data_UCI y Data_Hospital 
                    switch (ev.getTipoEvento()) {
                       case Evento.EntradaHospital://Evento entra al hospital, incrementamos en uno el número de pacientes en hospital
                            data_Hospital[ev.getGrupo()][dia][r]++;
                            break;
                        case Evento.EntradaUci:
                            data_UCI[ev.getGrupo()][dia][r]++;
                            break;
                        case Evento.SalidaUci:
                            data_UCI[ev.getGrupo()][dia][r]--;
                            break;
                        case Evento.SalidaRecuperadoTrasHospitalConUCI:
                        case Evento.SalidaRecuperadoTrasHospitalSinUCI:    
                            data_Hospital[ev.getGrupo()][dia][r]--;
                            break;
                        case Evento.SalidaMuertoEnHospital:
                            data_Hospital[ev.getGrupo()][dia][r]--;
                            break;
                        case Evento.Entrada://Evento da positivo
                        case Evento.SalidaMuertoTrasHospital:
                        case Evento.SalidaMuertoSinHospital:
                        case Evento.SalidaRecuperadoSinHospital:
                    }
            }
             }catch(Exception e){

             
             }        
        }
        //Ahora repasamos las matrices data_UCI y data_Hospital y acumulamos y arrastramos los eventos
        for (int ge = 0; ge < numGruposG2; ge++) {
            for (int h = 1; h < horizonte; h++) {
                for (int r = 0; r < numero_de_replicas; r++) {
                    data_UCI[ge][h][r]+=data_UCI[ge][h-1][r];
                    data_Hospital[ge][h][r]+=data_Hospital[ge][h-1][r];
                }
            }
        }
        //Ahora repetimos el proceso, cargamos los datos reales registrando solo lo que ocurre entre from y until, para posteriormente pintarlo
      
    return 0;
    }

 
    void analiza_lista_eventos() {
        int contadorEventos[];
        int numDias=(int) ChronoUnit.DAYS.between(getFrom(), getUntil())+1;
        contadorEventos = new int[numDias];
        
        int dia;
        for(Evento ev: listaEventos)
        {
            dia = (int) ChronoUnit.DAYS.between(getFrom(), ev.getInstante());  //posicion en Data_UCI y Data_Hospital 
            contadorEventos[dia]++;
            ev=listaEventos.peek();
        }

    }
 @Override
   protected void done() {
      // Mostramos el nombre del hilo para ver que efectivamente esto
      // se ejecuta en el hilo de eventos.
      if(flagPrint)logTextArea.append(bundle.getString("mensaje.simulacionfinalizada"));
      resSimulacionText.setEnabled(true);
      resSimulacionGrf.setEnabled(true);
      resSimulacionText2.setEnabled(true);
      resSimulacionGrf2.setEnabled(true);      
 
      if(flagPrint)barra.dispose();
   }
    
   
   protected void process(List<Integer> l){
      for (final Integer st : l) {      
      if(Math.IEEEremainder(st, numero_de_replicas/10.0)==0) 
          logTextArea.append(( String.format(bundle.getString("mensaje.procesandoreplica"),st,numero_de_replicas))  ) ;
      barra.getBarraProgreso().setValue((int)(100*((float)st/(float)numero_de_replicas)));
      
    }
   }

    /**
     * @return the nuevosPostivosTotales
     */
    public int[][] getNuevosPostivosTotales() {
        return nuevosPostivosTotales;
    }
   
   
    
}
