
import com.toedter.calendar.JDateChooser;
import java.io.FileNotFoundException;
import java.io.StringWriter;
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
public class estimaParametrosYSimulaValidacion extends SwingWorker<Integer,Integer>{

    
    
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
   public estimaParametrosYSimulaValidacion(ResourceBundle bndl,ScriptEngine eng,JTextArea st,  JDateChooser jdt0,JDateChooser jdt1,JDateChooser jdtf,JDateChooser jdtI,String dd,JButton ps2,
     StringWriter oW,int num,PantallaPrincipalA ppaa,String nfp,String ldv,String[] fichs,JTextField sem,JTextField nr,
     JButton resTextoButonn, JButton resGraphicButonn, JButton resTextoButon2n, JButton resGraphicButon2n){
       bundle = bndl;
       engine=eng;
  
       salidaTexto=st;
 
       jDateChooser_t_0=jdt0;
       jDateChooser_t_1=jdt1;
       jDateChooser_t_F=jdtf;
       jDateChooser_t_I=jdtI;
       dataDirectory = dd;
       outputWriter=oW;
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
        String outPut;
        int numReplicas;
        int semilla;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
            try { 
                barra = new showProgress(bundle,bundle.getString("showprogress.estimationofparameters"));
           barra.setVisible(true);
           barra.getBarraProgreso().setIndeterminate(true);
            engine.put("carpetaResultados", dataDirectory);

            engine.put("t_I", sdf.format(jDateChooser_t_I.getDate()));
            engine.put("t_F", sdf.format(jDateChooser_t_F.getDate()));
            engine.put("t_0", sdf.format(jDateChooser_t_0.getDate()));
            engine.put("t_1", sdf.format(jDateChooser_t_1.getDate()));
            engine.put("automatic_scenario",true);
            engine.put("custom_scenario",false);
            
            engine.put("frequency_series",numero);
            
            engine.eval(new java.io.FileReader("genera_tablas_formateado_sin_simulador_20abr.R"));
            outPut = outputWriter.toString();
            salidaTexto.append(outPut + "\n");    

        } catch (ScriptException | FileNotFoundException ex) {
            Logger.getLogger(PantallaPrincipalA.class.getName()).log(Level.SEVERE, null, ex);
        }
        ppa.setDataDirectory(dataDirectory);
        ppa.setLastDirectoryVisited(lastDirectoryVisited);
        ppa.setLastFileUsed(nombreFicheroPrevio);
        /*Properties prop = new Properties();
        try {
            BufferedOutputStream bis;
            bis = new BufferedOutputStream(new FileOutputStream(new File(dataDirectory + "simul.properties")));
            prop.setProperty("t_0", sdf.format(jDateChooser_t_0.getDate()));
            prop.setProperty("t_1", sdf.format(jDateChooser_t_1.getDate()));
            prop.store(bis, "Fechas de predicción");
        } catch (FileNotFoundException ex) {
            //  Logger.getLogger(CovidSim.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            // Logger.getLogger(CovidSim.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        try {
            numReplicas = Integer.parseInt(numRepJTF.getText());
            semilla = Integer.parseInt(semillaJTF.getText());
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

        //Ya tenemos los ficheros, ahora las simulaciones, creamos dos simuladores sim1 y sim 2        
        String trozos[] = (sdf.format(jDateChooser_t_0.getDate())).split("-");
        LocalDate from = LocalDate.of(Integer.parseInt(trozos[0]), Integer.parseInt(trozos[1]), Integer.parseInt(trozos[2]));
        trozos = (sdf.format(jDateChooser_t_1.getDate())).split("-");
        LocalDate until = LocalDate.of(Integer.parseInt(trozos[0]), Integer.parseInt(trozos[1]), Integer.parseInt(trozos[2]));
        
        Simulador sim1 = new Simulador(ficheros, from.minusDays(1), until, numReplicas, salidaTexto, resTextoButon, this.resGraphicButon, resTextoButon2, this.resGraphicButon2, semilla, dataDirectory, false,"(evolución predicha)",bundle,true,false);
        ppa.setSim1(sim1);
        sim1.execute();

        Simulador sim2 = new Simulador(ficheros, from.minusDays(1), until, numReplicas, salidaTexto, resTextoButon, this.resGraphicButon, resTextoButon2, this.resGraphicButon2, semilla, dataDirectory, true,"(datos observados)",bundle,false,false);
        ppa.setSim2(sim2);
        sim2.execute();
       return 0;
    }
    
    
     @Override
   protected void done() {
    
      
   }
    
   

}

    

