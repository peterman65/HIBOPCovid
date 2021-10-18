/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author pmateo
 */
public class PantallaPrincipalA extends javax.swing.JFrame {

    /**
     * @param sim1 the sim1 to set
     */
    public void setSim1(Simulador sim1) {
        this.sim1 = sim1;
    }

    /**
     * @param sim2 the sim1 to set
     */
    public void setSim2(Simulador sim2) {
        this.sim2 = sim2;
    }
    /**
     * @param from the from to set
     */
    public void setFrom(LocalDate from) {
        this.from = from;
    }

    /**
     * @param until the until to set
     */
    public void setUntil(LocalDate until) {
        this.until = until;
    }

    /**
     * @return the lastFileUsed
     */
    public String getLastFileUsed() {
        return lastFileUsed;
    }

    /**
     * @param lastFileUsed the lastFileUsed to set
     */
    public void setLastFileUsed(String lastFileUsed) {
        this.lastFileUsed = lastFileUsed;
    }

    /**
     * @return the lastDirectoryVisited
     */
    public String getLastDirectoryVisited() {
        return lastDirectoryVisited;
    }

    /**
     * @param lastDirectoryVisited the lastDirectoryVisited to set
     */
    public void setLastDirectoryVisited(String lastDirectoryVisited) {
        this.lastDirectoryVisited = lastDirectoryVisited;
    }

    /**
     * @return the dataDirectory
     */
    public String getDataDirectory() {
        return dataDirectory;
    }

    /**
     * @param dataDirectory the dataDirectory to set
     */
    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }
    private Simulador  sim1=null;
    private Simulador  sim2=null;
    private Estadisticas estadisticas=null;
    private Estadisticas estadisticas2=null;
    private LocalDate from=null;
    private LocalDate until=null;
    private String lastDirectoryVisited=null;
    private String dataDirectory="./Data";
    private String lastFileUsed="";
    String fich[] = {
            "datos_iniciales.csv", //0
            "nuevos_positivos_escenario.csv", //1
            "probabilidades_hospital_uci_1.csv", //2
            "dias_antes_hospital_1.csv", //3
            "dias_hospital_antes_uci_1.csv", //4 
            "dias_hospital_sin_uci_1.csv", //5
            "dias_hospital_tras_uci_1.csv", //6  
            "dias_uci_1.csv", //7
            "probabilidades_hospital_uci_2.csv", //8
            "dias_antes_hospital_2.csv", //9
            "dias_hospital_antes_uci_2.csv", //10
            "dias_hospital_sin_uci_2.csv", //11
            "dias_hospital_tras_uci_2.csv", //12
            "dias_uci_2.csv", //13
            "situation_counts.csv", //14
            "nuevos_positivos_reales.csv", //15
            "situation_counts_groups.csv" //16
        };
    private ResourceBundle bundle = null;
    private Locale locale = null;
    
    /**
     * Creates new form PantallaPrincipal
     */
    public PantallaPrincipalA() {
        Properties prop = new Properties();
        try{
            BufferedInputStream bis;
            File f= new File("config.properties");
            if(f.exists()){
                bis = new BufferedInputStream(new FileInputStream(f));
                  prop.load(bis);
                  if(prop.containsKey("lastDirectoryVisited"))                     
                      lastDirectoryVisited=(String) prop.get("lastDirectoryVisited");
                  else
                      lastDirectoryVisited=".";
                  if(prop.containsKey("dataDirectory"))                     
                      dataDirectory=(String) prop.get("dataDirectory");
                  else
                      dataDirectory=".";
                  if(prop.containsKey("lastFileUsed"))                     
                      lastFileUsed=(String) prop.get("lastFileUsed");
                  else
                      lastFileUsed="noFile";
                  
                
            }else{
                   lastDirectoryVisited=".";
                   dataDirectory=".";
                   lastFileUsed="noFile";
            }
           
            
        } catch (FileNotFoundException ex) {
          //  Logger.getLogger(CovidSim.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
           // Logger.getLogger(CovidSim.class.getName()).log(Level.SEVERE, null, ex);
        }
        locale = Locale.getDefault();
        bundle = ResourceBundle.getBundle("literales", locale);
        initComponents();
    }

   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        opcionARealizar = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelFondo = new javax.swing.JPanel();
        resultadoSimulacionTexto = new javax.swing.JButton();
        Acercade = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        salidaTexto = new javax.swing.JTextArea();
        resultadosGraficos = new javax.swing.JButton();
        procesaDatos = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        prediccion = new javax.swing.JRadioButton();
        validacion = new javax.swing.JRadioButton();
        resultadoSimulacionTextoNew = new javax.swing.JButton();
        resultadosGraficosNew = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(bundle.getString("principal.pantalla.title")
        );
        setIconImage( Toolkit.getDefaultToolkit().getImage(getClass().getResource("pngwing.png")));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        resultadoSimulacionTexto.setText(bundle.getString("principal.pantalla.button.textoGrupos")
        );
        resultadoSimulacionTexto.setEnabled(false);
        resultadoSimulacionTexto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultadoSimulacionTextoActionPerformed(evt);
            }
        });

        Acercade.setText(bundle.getString("principal.pantalla.button.acercade"));
        Acercade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcercadeActionPerformed(evt);
            }
        });

        salidaTexto.setColumns(20);
        salidaTexto.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        salidaTexto.setRows(5);
        jScrollPane2.setViewportView(salidaTexto);

        resultadosGraficos.setText(bundle.getString("principal.pantalla.button.graficoGrupos"));
        resultadosGraficos.setEnabled(false);
        resultadosGraficos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultadosGraficosActionPerformed(evt);
            }
        });

        procesaDatos.setText(bundle.getString("principal.pantalla.button.procesa"));
        procesaDatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procesaDatosActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("principal.pantalla.label.opcion")
        ));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        opcionARealizar.add(prediccion);
        prediccion.setSelected(true);
        prediccion.setText(bundle.getString("principal.pantalla.rbutton.prediccion")
        );
        jPanel1.add(prediccion);

        opcionARealizar.add(validacion);
        validacion.setText(bundle.getString("principal.pantalla.rbutton.validacion"));
        jPanel1.add(validacion);

        resultadoSimulacionTextoNew.setText(bundle.getString("principal.pantalla.button.textoGlobal"));
        resultadoSimulacionTextoNew.setEnabled(false);
        resultadoSimulacionTextoNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultadoSimulacionTextoNewActionPerformed(evt);
            }
        });

        resultadosGraficosNew.setText(bundle.getString("principal.pantalla.button.graficoGlobal"));
        resultadosGraficosNew.setEnabled(false);
        resultadosGraficosNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultadosGraficosNewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultadosGraficos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultadoSimulacionTexto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(procesaDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Acercade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultadoSimulacionTextoNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultadosGraficosNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(47, 47, 47)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE))
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jScrollPane2))
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(procesaDatos)
                        .addGap(18, 18, 18)
                        .addComponent(resultadoSimulacionTexto)
                        .addGap(18, 18, 18)
                        .addComponent(resultadosGraficos)
                        .addGap(18, 18, 18)
                        .addComponent(resultadoSimulacionTextoNew)
                        .addGap(18, 18, 18)
                        .addComponent(resultadosGraficosNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 115, Short.MAX_VALUE)
                        .addComponent(Acercade)))
                .addGap(25, 25, 25))
        );

        jScrollPane1.setViewportView(panelFondo);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Properties prop = new Properties();
        try(OutputStream output = new FileOutputStream("config.properties")){
            
            prop.setProperty("lastDirectoryVisited", getLastDirectoryVisited());
            prop.setProperty("dataDirectory", getDataDirectory());        
            prop.setProperty("lastFileUsed", getLastFileUsed());        
            prop.store(output, "Datos actualizados");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CovidSim.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CovidSim.class.getName()).log(Level.SEVERE, null, ex);
        }    
        System.out.println("Saliendo del programa");
        
    }//GEN-LAST:event_formWindowClosing

    private void procesaDatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procesaDatosActionPerformed
        
        if(prediccion.isSelected()){
        InputsPreprocesoPrediccion ipp = new InputsPreprocesoPrediccion(this,true,dataDirectory,lastDirectoryVisited, getLastFileUsed(),
                salidaTexto,fich,resultadoSimulacionTexto,resultadosGraficos,resultadoSimulacionTextoNew,resultadosGraficosNew,bundle);
        ipp.setVisible(true);}
        else
        {
        InputsPreprocesoValidacion ipv = new InputsPreprocesoValidacion(this,true,dataDirectory,lastDirectoryVisited, getLastFileUsed(),
                salidaTexto,fich,resultadoSimulacionTexto,resultadosGraficos,resultadoSimulacionTextoNew,resultadosGraficosNew,bundle);
        ipv.setVisible(true);
        }

       
    }//GEN-LAST:event_procesaDatosActionPerformed

    private void resultadosGraficosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultadosGraficosActionPerformed
        if (sim1 == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("error.estadisticassinsimulacion"));
            return;
        }
        if(validacion.isSelected()){
            estadisticas = new Estadisticas(sim1.getData_UCI(),sim1.getData_Hospital(),sim1.getNuevosPostivosTotales(),sim1.getFrom(),sim1.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();
            //estadisticas.dibujaEstadísticasGenerales();
            estadisticas2 = new Estadisticas(sim2.getData_UCI(),sim2.getData_Hospital(),sim2.getNuevosPostivosTotales(),sim2.getFrom(),sim2.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas2.calculaStadisticas();
            //estadisticas2.dibujaEstadísticasGenerales();
            GraficosStats1 grfS = new GraficosStats1(bundle.getString("graficos1.grafico.titlePPC"),estadisticas,bundle,true);
            grfS.setVisible(true);
            GraficosStats1 grfS2 = new GraficosStats1(bundle.getString("graficos1.grafico.titleOPC"),estadisticas2,bundle,true);
            grfS2.setVisible(true);
            
        }else{
            estadisticas = new Estadisticas(sim1.getData_UCI(),sim1.getData_Hospital(),sim1.getNuevosPostivosTotales(),sim1.getFrom(),sim1.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();
           // estadisticas.dibujaEstadísticasGenerales();
            GraficosStats1 grfS = new GraficosStats1(bundle.getString("graficos1.grafico.titleF"),estadisticas,bundle,true);
            grfS.setVisible(true);

        }

    }//GEN-LAST:event_resultadosGraficosActionPerformed

    private void AcercadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcercadeActionPerformed
        // TODO add your handling code here:
      
        salidaTexto.append("                                      HIBOP-COVID\n");
        salidaTexto.append("===================================================================================================\n\n");
        salidaTexto.append("                  Hospital and ICU Bed Occupancy Prediction in COVID-19\n\n");
        salidaTexto.append("Authors by alphabetical order:\n");
        salidaTexto.append("------------------------------\n");
        
        salidaTexto.append("Jesús Asín        jasin@unizar.es     Dept. of Statistical Methods, University of Zaragoza, Spain\n");               
        salidaTexto.append("Angel Borque      aborque@comz.org    Dept. of Urology, Miguel Servet University Hospital  and IIS, Zaragoza, Spain\n");
        salidaTexto.append("Ana C. Cebrián    acebrian@unizar.es  Dept. of Statistical Methods, University of Zaragoza, Spain\n");
        salidaTexto.append("Luis M. Esteban   lmeste@unizar.es    Escuela Universitaria Politécnica de la Almunia, University of Zaragoza, Spain\n");
        salidaTexto.append("Miguel Lafuente   miguelllb@unizar.es Dept. of Statistical Methods, University of Zaragoza, Spain\n");
        salidaTexto.append("Javier López      javierl@unizar.es   Dept. of Statistical Methods, University of Zaragoza, Spain\n");
        salidaTexto.append("Pedro Mateo       mateo@unizar.es     Dept. of Statistical Methods, University of Zaragoza, Spain\n");
        salidaTexto.append("José A. Moler     jmoler@unavarra.es  Dept. of Statistics and Operational Research, Public University of Navarra, Spain\n");
        salidaTexto.append("Ana Pérez         anapp@unizar.es     Dept. of Statistical Methods, University of Zaragoza, Spain\n");
        salidaTexto.append("Gerardo Sanz      gerardo@unizar.es   Dept. of Statistical Methods, University of Zaragoza, Spain\n");
        
        
        
        
        
        
        
        salidaTexto.append("\n\nThis software has been developed using the libraries:\n \n");
        salidaTexto.append("   - Renjin    http://www.renjin.org  it is available under the GPLv2 (or higher) license.\n     See https://www.gnu.org/licenses/old-licenses/gpl-2.0.html\n");
        salidaTexto.append("   - jfreechart https://www.jfree.org/jfreechart/ it is available under the GNU Lesser General Public Licence (LGPL).\n     See https://www.gnu.org/licenses/lgpl-3.0.html\n"); 
        salidaTexto.append("   - JCalendar https://toedter.com/jcalendar/ it is available under the GNU Lesser General Public Licence (LGPL)\n     See https://www.gnu.org/licenses/lgpl-3.0.html\n"); 
        salidaTexto.append("   - Commons Math: The Apache Commons Mathematics Library https://commons.apache.org/proper/commons-math/ it is available under the Apache License, V2.0.\n     See https://www.apache.org/licenses/LICENSE-2.0\n");
        salidaTexto.append("\n\nThis software is available under GPLv3 license https://www.gnu.org/licenses/gpl-3.0.html.\n\n");
        
    }//GEN-LAST:event_AcercadeActionPerformed

    private void resultadoSimulacionTextoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultadoSimulacionTextoActionPerformed
        // TODO add your handling code here:

        if (sim1 == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("error.estadisticassinsimulacion"));
            return;
        }
        if(validacion.isSelected()){
            estadisticas = new Estadisticas(sim1.getData_UCI(),sim1.getData_Hospital(),sim1.getNuevosPostivosTotales(),sim1.getFrom(),sim1.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();
            estadisticas2 = new Estadisticas(sim2.getData_UCI(),sim2.getData_Hospital(),sim2.getNuevosPostivosTotales(),sim2.getFrom(),sim2.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas2.calculaStadisticas();
          //  estadisticas.escribeResumenEstadisticasGenerales(estadisticas2);
            TextStats1 tst = new TextStats1(estadisticas,estadisticas2,bundle);
            tst.rellena();
            tst.setVisible(true);
        }else{
            estadisticas = new Estadisticas(sim1.getData_UCI(),sim1.getData_Hospital(),sim1.getNuevosPostivosTotales(),sim1.getFrom(),sim1.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();
          //  estadisticas.escribeResumenEstadisticasGenerales();
            
            TextStats1 ts1 = new TextStats1(estadisticas,bundle,true);
            ts1.rellena();
            ts1.setVisible(true);
        }
        
            
    }//GEN-LAST:event_resultadoSimulacionTextoActionPerformed

    private void resultadoSimulacionTextoNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultadoSimulacionTextoNewActionPerformed
        // TODO add your handling code here:
        if (sim1 == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("error.estadisticassinsimulacion"));
            return;
        } 
        if(validacion.isSelected()){
            estadisticas = new Estadisticas(sim1.getData_UCI(),sim1.getData_Hospital(),sim1.getNuevosPostivosTotales(),sim1.getFrom(),sim1.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();
            estadisticas2 = new Estadisticas(sim2.getData_UCI(),sim2.getData_Hospital(),sim2.getNuevosPostivosTotales(),sim2.getFrom(),sim2.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas2.calculaStadisticas();
           // estadisticas.escribeResumenEstadisticasComparacion(estadisticas2);
            
            TextStats2 tst = new TextStats2(estadisticas,estadisticas2,bundle,false);
            tst.rellena();
            tst.setVisible(true);
       
            
        }else{
            estadisticas = new Estadisticas(sim1.getData_UCI(),sim1.getData_Hospital(),sim1.getNuevosPostivosTotales(),sim1.getFrom(),sim1.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();
            //estadisticas.escribeResumenEstadisticasComparacion();
            TextStats2 ts2 = new TextStats2( estadisticas,bundle,sim1.isUsaPositivosUsuario());
            ts2.rellena();
            ts2.setVisible(true);
                     
        } 
        
    }//GEN-LAST:event_resultadoSimulacionTextoNewActionPerformed

    private void resultadosGraficosNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultadosGraficosNewActionPerformed
        if (sim1 == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("error.estadisticassinsimulacion"));
            return;
        }
        if (validacion.isSelected()) {
            estadisticas = new Estadisticas(sim1.getData_UCI(), sim1.getData_Hospital(), sim1.getNuevosPostivosTotales(), sim1.getFrom(), sim1.getUntil(), salidaTexto, fich[14], dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();estadisticas2 = new Estadisticas(sim2.getData_UCI(),sim2.getData_Hospital(),sim2.getNuevosPostivosTotales(),sim2.getFrom(),sim2.getUntil(),salidaTexto,fich[14],dataDirectory,fich[16]);
            estadisticas2.calculaStadisticas();
           // estadisticas.dibujaEstadísticasComparacion(estadisticas2);
            
            
            GraficosStats4 grfS = new GraficosStats4("titulo",estadisticas,estadisticas2,bundle);
            grfS.setVisible(true);
            
        } else {
            estadisticas = new Estadisticas(sim1.getData_UCI(), sim1.getData_Hospital(), sim1.getNuevosPostivosTotales(), sim1.getFrom(), sim1.getUntil(), salidaTexto, fich[14], dataDirectory,fich[16]);
            estadisticas.calculaStadisticas();
            //estadisticas.dibujaEstadísticasComparacion();
            
            GraficosStats3 grfS = new GraficosStats3("titulo",estadisticas,bundle);
            grfS.setVisible(true);
        }

    }//GEN-LAST:event_resultadosGraficosNewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Acercade;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.ButtonGroup opcionARealizar;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JRadioButton prediccion;
    private javax.swing.JButton procesaDatos;
    private javax.swing.JButton resultadoSimulacionTexto;
    private javax.swing.JButton resultadoSimulacionTextoNew;
    private javax.swing.JButton resultadosGraficos;
    private javax.swing.JButton resultadosGraficosNew;
    private javax.swing.JTextArea salidaTexto;
    private javax.swing.JRadioButton validacion;
    // End of variables declaration//GEN-END:variables
}

