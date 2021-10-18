
import com.toedter.calendar.JDateChooser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pmateo
 */
public class estimaParametrosYSimulaPrediccion extends SwingWorker<Integer,Integer>{

    
    
   //private showProgress barra;
   private ResourceBundle bundle;
   private ScriptEngine engine;     

   private showProgress  barra;    
   private  JTextArea salidaTexto;
   private  StringWriter outputWriter;
   private JDateChooser jDateChooser_t_0;
    private JDateChooser jDateChooser_t_1;
    private JDateChooser jDateChooser_t_F;
    private JDateChooser jDateChooser_t_I;
    private String dataDirectory; 
    private String nombreFicheroPrevio; 
    private String lastDirectoryVisited;
    private JButton procesar2;
    private boolean usarFicheroNewPosPropio;
    private boolean custom_scenario_simple;
    
    private String newOwnPositivesFile;
    private int numClasesG2;
    private int numero;
    private PantallaPrincipalA ppa;
    private String[] ficheros;
    private JTextField semillaJTF;
    private JTextField numRepJTF;
    private int numReplicas;
    private int semilla;
    private JButton resTextoButon;
    private JButton resGraphicButon;
    private JButton resTextoButon2;
    private JButton resGraphicButon2;
   public estimaParametrosYSimulaPrediccion(ResourceBundle bndl,ScriptEngine eng,JTextArea st,  JDateChooser jdt0,JDateChooser jdt1,JDateChooser jdtf,JDateChooser jdtI,String dd,JButton ps2,
     StringWriter oW,boolean ufnpp,boolean css,String nopf, int ncG2,int num,PantallaPrincipalA ppaa,String nfp,String ldv,String[] fichs,JTextField sem,JTextField nr,
     JButton resTextoButonn, JButton resGraphicButonn, JButton resTextoButon2n, JButton resGraphicButon2n){
       bundle = bndl;
       engine=eng;
  
       salidaTexto=st;
 
       jDateChooser_t_0=jdt0;
       jDateChooser_t_1=jdt1;
       jDateChooser_t_F=jdtf;
       jDateChooser_t_I=jdtI;
       dataDirectory = dd;
       procesar2= ps2;
       outputWriter=oW;
       usarFicheroNewPosPropio=ufnpp;
       custom_scenario_simple=css;
       newOwnPositivesFile=nopf;
       numClasesG2=ncG2;
       numero=num;
       ppa=ppaa;
       nombreFicheroPrevio=nfp;
       lastDirectoryVisited=ldv;
       ficheros=fichs;
       semillaJTF=sem;
       numRepJTF=nr;
       resTextoButon=resTextoButonn;
    resGraphicButon=resGraphicButonn;
    resTextoButon2=resTextoButon2n;
    resGraphicButon2= resGraphicButon2n;
       
   } 
    @Override
    protected Integer doInBackground() {  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");    
       try {
           barra = new showProgress(bundle,bundle.getString("showprogress.estimationofparameters"));
           barra.setVisible(true);
           barra.getBarraProgreso().setIndeterminate(true);
           
           String outPut = outputWriter.toString();
           
           
         
           // this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            engine.put("carpetaResultados", dataDirectory);
            
            engine.put("t_I",sdf.format(jDateChooser_t_I.getDate()));
            engine.put("t_F",sdf.format(jDateChooser_t_F.getDate()));
            engine.put("t_0",sdf.format(jDateChooser_t_0.getDate()));
            engine.put("t_1",sdf.format(jDateChooser_t_1.getDate()));
            if(!usarFicheroNewPosPropio){
                engine.put("automatic_scenario",true);
                engine.put("custom_scenario",false);
                engine.put("frequency_series",numero);
            }else{
              if(custom_scenario_simple){
                  engine.put("automatic_scenario",false);
                  //engine.put("custom_scenario",leeFicheroNewPositivosSimple());
                  engine.eval("custom_scenario ="+leeFicheroNewPositivosSimple());
                  engine.put("threshold_proportion_groups_custom_scenario",30);
              }else{
                  engine.put("automatic_scenario",false);
                  engine.put("custom_scenario",false);
                  engine.put("numClasesG2",numClasesG2);
              }
            }
            
            //engine.put("t_1",sdf.format(jDateChooser_t_1.getDate()));
            engine.eval(new java.io.FileReader("genera_tablas_formateado_sin_simulador_20abr.R"));
            outPut = outputWriter.toString();
            salidaTexto.append(outPut+"\n");                                 
     //       this.setCursor(c);
         
           
           
     //      return 0;
       } catch (FileNotFoundException ex) {
           Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ScriptException ex) {
           Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       ppa.setDataDirectory(dataDirectory);
       ppa.setLastDirectoryVisited(lastDirectoryVisited);
       ppa.setLastFileUsed(nombreFicheroPrevio);
       
         if (this.usarFicheroNewPosPropio && !custom_scenario_simple) {
            if (newOwnPositivesFile != null) {
                //Chequea y comprueba el fichero, si tiene tantas columnas como debe, o en el caso de no tener
                //que proporcione las proporciones correspondientes para generar los datos adecuadamente.
                //ESTO ESTÁ PENDIENTE., PERO SERÍA INTERESANTE HACERLO PARA SABER QUE SE LEE ALGO CON SENTIDO.
                Path sourceFile = Paths.get(newOwnPositivesFile);
                Path targetFile = Paths.get(dataDirectory + ficheros[1]);
                try {
                    Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    this.usarFicheroNewPosPropio = true;
                    procesar2.setEnabled(true);
                } catch (IOException ex) {
                    this.usarFicheroNewPosPropio = false;
                    procesar2.setEnabled(false);
                    JOptionPane.showMessageDialog(null, bundle.getString("error.cargaficheropropio"));
                    return -1;
                }
            } else {
                JOptionPane.showMessageDialog(null, bundle.getString("mensaje.ficheropropionocargado"));
                return -1;
            }
        }

        try{
        numReplicas =Integer.parseInt(numRepJTF.getText());
        semilla=Integer.parseInt(semillaJTF.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, String.format(bundle.getString("mensaje.replicasOsemillaerroneas"),
                    numRepJTF.getText(),semillaJTF.getText()));
            return -1;
        }
        barra.dispose();
        resTextoButon.setEnabled(false);
        resGraphicButon.setEnabled(false);
        resTextoButon2.setEnabled(false);
        resGraphicButon2.setEnabled(false);
        
        //Lanzamos la simulación     

        String trozos[] = (sdf.format(jDateChooser_t_0.getDate())).split("-");
        LocalDate from = LocalDate.of(Integer.parseInt(trozos[0]), Integer.parseInt(trozos[1]), Integer.parseInt(trozos[2]));
        trozos = (sdf.format(jDateChooser_t_1.getDate())).split("-");
        LocalDate until = LocalDate.of(Integer.parseInt(trozos[0]), Integer.parseInt(trozos[1]), Integer.parseInt(trozos[2]));
        
        Simulador sim = new Simulador(ficheros, from.minusDays(1), until, numReplicas, salidaTexto, resTextoButon,
                this.resGraphicButon, resTextoButon2, this.resGraphicButon2, semilla, dataDirectory, false, "(evolución predicha)", bundle, true, usarFicheroNewPosPropio);
        ppa.setSim1(sim);
        sim.execute();    
        
       return 0;
    }
    
    
      String leeFicheroNewPositivosSimple(){
        String lineJustFetched;
        StringBuilder resultado = new StringBuilder("c(");
        //Fichero de nuevos positivos de usuario, calculando tamaño
        //###########################
        try ( 
            BufferedReader buf = new BufferedReader(new FileReader(this.newOwnPositivesFile))) {            
            
            while (true) {
                lineJustFetched = buf.readLine();                
                if (lineJustFetched == null)   break;
                resultado.append(lineJustFetched+",");
            }
            buf.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaPacientesYDistribuciones.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CargaPacientesYDistribuciones.class.getName()).log(Level.SEVERE, null, ex);
        }    
        resultado.setCharAt(resultado.length()-1, ')');
        
    return resultado.toString();
    }
     @Override
   protected void done() {
       
     
          
           
           
     
      
       
      // Mostramos el nombre del hilo para ver que efectivamente esto
      // se ejecuta en el hilo de eventos.
      
   }
    
   

}

    

