
import com.ibm.icu.util.Calendar;
import com.toedter.calendar.JDateChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.renjin.sexp.Vector;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pmateo
 */
public class leeTesteaHistoricalRecordsPrediccion extends SwingWorker<Integer,Integer>{

    
    
   //private showProgress barra;
   private ResourceBundle bundle;
   private ScriptEngine engine;     
   private String nombreFicheroPrevio; 
   private showProgress  barra;    
   private  JTextArea salidaTexto;
   private  StringWriter outputWriter;
   private javax.swing.JTextField fechaFin;
   private javax.swing.JTextField fechaIni; 
   private Date tMin;
   private Date tMax;
   private JTextField nombreFicheroJTF;
   private JDateChooser jDateChooser_t_0;
    private JDateChooser jDateChooser_t_1;
    private JDateChooser jDateChooser_t_F;
    private JDateChooser jDateChooser_t_I;
    private String dataDirectory; 
    private JButton procesar2;
    private boolean ficheroDatosDisponible;
    private int[] SnG2;
   public leeTesteaHistoricalRecordsPrediccion(ResourceBundle bndl,ScriptEngine eng,String nfp,JTextArea st,  JTextField fechaIni,
    JTextField fechaFin,JTextField nfjtf, JDateChooser jdt0,JDateChooser jdt1,JDateChooser jdtf,JDateChooser jdtI,String dd,JButton ps2,boolean fdd, StringWriter oW,
   int[] SnumG2){
       bundle = bndl;
       engine=eng;
       nombreFicheroPrevio=nfp;
       salidaTexto=st;
       this.fechaIni=fechaIni;
       this.fechaFin=fechaFin;
       nombreFicheroJTF=nfjtf;
       jDateChooser_t_0=jdt0;
       jDateChooser_t_1=jdt1;
       jDateChooser_t_F=jdtf;
       jDateChooser_t_I=jdtI;
       dataDirectory = dd;
       procesar2= ps2;
       ficheroDatosDisponible=fdd;
       outputWriter=oW;
       SnG2=SnumG2;
   } 
    @Override
    protected Integer doInBackground() {       
       try {
           barra = new showProgress(bundle,bundle.getString("showprogress.readingdata"));
           barra.setVisible(true);
           barra.getBarraProgreso().setIndeterminate(true);
           File ff = new File(nombreFicheroJTF.getText());
                if (!ff.exists()) {
                    JOptionPane.showMessageDialog(null, String.format(bundle.getString("error.ficheronoexiste"),nombreFicheroJTF.getText()));
                    return -1;
                }
                
           
           engine.put("nombreFichero", nombreFicheroPrevio);
           engine.eval(new java.io.FileReader("leeFicheroRaw.R"));
           
         
           return 0;
       } catch (FileNotFoundException ex) {
           Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ScriptException ex) {                      
       }
       return 0;
    }
    
     @Override
   protected void done() {
       
       try {
          barra.dispose();
           
           String outPut = outputWriter.toString();
           Vector gammaVector = (Vector) engine.eval("as.character(min(data_form$positive))");
           String min = gammaVector.getElementAsString(0);
           gammaVector = (Vector) engine.eval("as.character(max(data_form$positive))");
           String max = gammaVector.getElementAsString(0);
           gammaVector = (Vector) engine.eval("numClasesG2");
           Integer numClasesG2 = gammaVector.getElementAsInt(0);
           
           if(min==null || max==null|| numClasesG2==null) throw  new Exception("min,max o numClasesG2 son null");
           
           
           
           
           
           SnG2[0]=numClasesG2;
           Calendar cc = Calendar.getInstance();
           salidaTexto.append( String.format(bundle.getString("mensaje.ficherocargado"),nombreFicheroJTF.getText()));
           salidaTexto.append( String.format(bundle.getString("mensaje.registrosdisponibles"),min,max));
           fechaIni.setText(min);
           fechaFin.setText(max);

           // convertir fecha a string
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");           
           //Fijamos los máximos y mínimos de cada fecha y después pongo el valor
           tMin = sdf.parse(min);
           tMax = sdf.parse(max);
           //Fijamos los valores y luego las cots
           
           jDateChooser_t_I.setDate(tMin);
           jDateChooser_t_F.setDate(tMax);    //No cambiar el orden de estas dos lineas, priero tmax luego tmin       
           cc.setTime(tMax);
           cc.add(Calendar.DATE, 1);
           jDateChooser_t_0.setDate(cc.getTime());
           cc.add(Calendar.DATE, 13);
           jDateChooser_t_1.setDate(cc.getTime()); 

           //Ahora fijamos los minimos y máximos
           //t_I
           jDateChooser_t_I.setMinSelectableDate(tMin); 
           cc.setTime(tMax);
           cc.add(Calendar.DATE, -1);
           jDateChooser_t_I.setMaxSelectableDate(cc.getTime());
           

           //t_F
           cc.setTime(tMin);
           cc.add(Calendar.DATE, 1);
           jDateChooser_t_F.setMinSelectableDate(cc.getTime());
           jDateChooser_t_F.setMaxSelectableDate(tMax);
           //t_0
           cc.setTime(tMax);
           cc.add(Calendar.DATE, 1);
           jDateChooser_t_0.setMinSelectableDate(cc.getTime());
           //cc.add(Calendar.DATE, 12);
           jDateChooser_t_0.setMaxSelectableDate(cc.getTime());
           //t_1
           cc.setTime(tMax);
           cc.add(Calendar.DATE, 2);
           jDateChooser_t_1.setMinSelectableDate(cc.getTime());
           cc.add(Calendar.DATE, 365);
           jDateChooser_t_1.setMaxSelectableDate(cc.getTime());
           
           dataDirectory=System.getProperty("java.io.tmpdir")+ File.separator;           
           procesar2.setEnabled(true);
           ficheroDatosDisponible = true;
          
       } catch (ScriptException ex) {
           Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ParseException ex) {
  //         Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex);
           JOptionPane.showMessageDialog(null,bundle.getString("error.formatofechanovalido"));
           return ;
       } catch (Exception ex) {
//           Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex); 
           JOptionPane.showMessageDialog(null,bundle.getString("error.formatofechanovalido"));
           return ;
       }
      
       
      // Mostramos el nombre del hilo para ver que efectivamente esto
      // se ejecuta en el hilo de eventos.
      
       
      // Mostramos el nombre del hilo para ver que efectivamente esto
      // se ejecuta en el hilo de eventos.
      
   }
    
   
      protected void doneseg() {
       
       try {
          barra.dispose();
           
           String outPut = outputWriter.toString();
           Vector gammaVector = (Vector) engine.eval("as.character(min(data_form$positive))");
           String min = gammaVector.getElementAsString(0);
           gammaVector = (Vector) engine.eval("as.character(max(data_form$positive))");
           String max = gammaVector.getElementAsString(0);
           gammaVector = (Vector) engine.eval("numClasesG2");
           Integer numClasesG2 = gammaVector.getElementAsInt(0);
           
           if(min==null || max==null|| numClasesG2==null) throw  new Exception("min,max o numClasesG2 son null");
           
           
           
           
           
           Calendar cc = Calendar.getInstance();
           salidaTexto.append( String.format(bundle.getString("mensaje.ficherocargado"),nombreFicheroJTF.getText()));
           salidaTexto.append( String.format(bundle.getString("mensaje.registrosdisponibles"),min,max));
           // salidaTexto.append("\n\nRegistros disponibles desde el **" + min + "** hasta el **" + max + "**\n");
           fechaIni.setText(min);
           fechaFin.setText(max);
           
           // convertir fecha a string
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           tMin = sdf.parse(min);
           tMax = sdf.parse(max);
           //Fijamos los valores y luego las cots
           jDateChooser_t_F.setDate(tMax);    //No cambiar el orden de estas dos lineas, priero tmax luego tmin       
           jDateChooser_t_I.setDate(tMin);
           
       
           cc.setTime(tMax);
           cc.add(Calendar.DATE, 1);
           jDateChooser_t_0.setDate(cc.getTime());
      
           cc.add(Calendar.DATE, 14);
           jDateChooser_t_1.setDate(cc.getTime()); 

           //t_I
           jDateChooser_t_I.setMinSelectableDate(tMin); 

           cc.setTime(tMax);
           cc.add(Calendar.DATE, -1);
           jDateChooser_t_I.setMaxSelectableDate(cc.getTime());

           //t_F
           cc.setTime(tMin);
           cc.add(Calendar.DATE, 1);
           jDateChooser_t_F.setMinSelectableDate(cc.getTime());
           jDateChooser_t_F.setMaxSelectableDate(tMax);
           //t_0
           cc.setTime(tMax);
           cc.add(Calendar.DATE, 1);
           jDateChooser_t_0.setMinSelectableDate(cc.getTime());
           jDateChooser_t_0.setMaxSelectableDate(cc.getTime());
           //t_1
           cc.setTime(tMax);
           cc.add(Calendar.DATE, 2);
           jDateChooser_t_1.setMinSelectableDate(cc.getTime());
           dataDirectory=System.getProperty("java.io.tmpdir")+ File.separator;
           procesar2.setEnabled(true);
           ficheroDatosDisponible = true;
          
       } catch (ScriptException ex) {
           Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ParseException ex) {
  //         Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex);
           JOptionPane.showMessageDialog(null,bundle.getString("error.formatofechanovalido"));
           return ;
       } catch (Exception ex) {
//           Logger.getLogger(leeTesteaHistoricalRecordsPrediccion.class.getName()).log(Level.SEVERE, null, ex); 
           JOptionPane.showMessageDialog(null,bundle.getString("error.formatofechanovalido"));
           return ;
       }
      
       
      // Mostramos el nombre del hilo para ver que efectivamente esto
      // se ejecuta en el hilo de eventos.
      
       
      // Mostramos el nombre del hilo para ver que efectivamente esto
      // se ejecuta en el hilo de eventos.
      
   }
   

}

    

