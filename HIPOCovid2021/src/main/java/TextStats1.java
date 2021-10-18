
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pmateo
 */
public class TextStats1 extends javax.swing.JFrame {

    //private String titleWindow;
    private Estadisticas e1=null;
    private Estadisticas e2=null;
    private int tipo;
    private ResourceBundle bundle; 
    private boolean isForecast;
   
    /**
     * Creates new form TextStats1
     */
    public TextStats1() {
        initComponents();
    }

    public TextStats1(Estadisticas ee1,ResourceBundle bndl,boolean isf) {
        super();
        e1=ee1;
    //    titleWindow = tW;
        tipo =1;
        bundle=bndl;
        isForecast=isf;
        initComponents();
    }
    public TextStats1(Estadisticas ee1,Estadisticas ee2,ResourceBundle bndl) {
        super();
        e1=ee1;
        e2=ee2;
      //  titleWindow = tW;
        tipo=2;
        bundle=bndl;
        initComponents();
    }
    
    void rellena() {
        int nGrupos = e1.uci.length;
        int horizonte_ = e1.uci[0].length;
        int numStats = e1.percents.length + 2;
        int numPercents = e1.percents.length;
        if (tipo == 1) {
            String formato = "%8.2f";
            String formatoP = "%7.0f";
            String formatoI = "%9d";
            String txt[] = {"ICU", "HOSPITAL"};
            cuadroTexto.append(bundle.getString("estadisticas1.resumen"));
            for (int t = 0; t < 2; t++) {
                for (int nge = 0; nge < nGrupos + 1; nge++) {
                    if (nge < nGrupos) {
                        cuadroTexto.append(String.format(bundle.getString("estadisticas1.encabezado1"), txt[t], (nge + 1)));
                    } else {
                        cuadroTexto.append(String.format(bundle.getString("estadisticas1.encabezado2"), txt[t]));
                    }
                    if(isForecast)
                        cuadroTexto.append(bundle.getString("estadisticas1.cabeceratrozo1Forecast"));
                    else
                        cuadroTexto.append(bundle.getString("estadisticas1.cabeceratrozo1Observed"));
                    for (int p = 0; p < e1.percents.length; p++) {
                        cuadroTexto.append(String.format(bundle.getString("estadisticas1.cabeceratrozo2"), e1.percents[p]));
                    }
                    cuadroTexto.append("\n");
                    cuadroTexto.append("-----------------------------------------------------------------------\n");
                    for (int h = 1; h < horizonte_; h++) {
                        cuadroTexto.append(e1.from.plusDays(h).format(e1.formatoFecha) + " ");
                        cuadroTexto.append(String.format(formatoI, e1.nuevosPositivosTotales[nge][h]));
                        for (int ns = 0; ns < numStats; ns++) {
                            //if(ns!=1)
                            {
                                if (ns == 0) {
                                    cuadroTexto.append(String.format(formato, e1.statsUH[t][ns][nge][h]));
                                } else if (ns == 1) {
                                    cuadroTexto.append(String.format(formato, e1.statsUH[t][ns][nge][h]));
                                } else {
                                    cuadroTexto.append(String.format(formatoP, e1.statsUH[t][ns][nge][h]));
                                }
                            }
                        }
                        cuadroTexto.append("\n");
                    }
                    cuadroTexto.append("\n\n");
                }
            }
            // cuadroTexto.append(String.format("\nUCI      Índice 1: %6.2f%% índice 2: %6.2f%%",e1.indicesJavier[0][0],e1.indicesJavier[0][1]));
            //   cuadroTexto.append(String.format("\nHospital Índice 1: %6.2f%% índice 2: %6.2f%%",e1.indicesJavier[1][0],e1.indicesJavier[1][1])); 
        } else if (tipo == 2) {
            String formato1 = " %15.0f%% ";
            String formato2 = "(%7.2f/%7.2f) ";
            String formato2I = "(%7d/%7d) ";
            String formato2_0 = "(%7.0f/%7.0f) ";
            String formato3 = " %6.0f      ";
            String txt[] = {bundle.getString("estadisticas3.uci"), bundle.getString("estadisticas3.hospital")};
            cuadroTexto.append(bundle.getString("estadisticas3.resumen"));

            for (int t = 0; t < 2; t++) {
                for (int nge = 0; nge < nGrupos + 1; nge++) {
                    if (nge < nGrupos) {
                        cuadroTexto.append(String.format(bundle.getString("estadisticas3.encabezado1"), txt[t], (nge + 1)));
                    } else {
                        cuadroTexto.append(String.format(bundle.getString("estadisticas3.encabezado2"), txt[t], (nge + 1)));
                    }
                    cuadroTexto.append(String.format(bundle.getString("estadisticas3.cabecera1trozo0_5"), " "));
                    for(int i=0;i<e1.percents.length;i++) cuadroTexto.append(bundle.getString("estadisticas3.cabecera1trozo0_5_2"));
                    cuadroTexto.append(String.format(bundle.getString("estadisticas3.cabecera1trozo1"), " "));
                    for (int p = 0; p < e1.percents.length; p++) {
                        cuadroTexto.append(String.format(bundle.getString("estadisticas3.cabecera1trozo2"), e1.percents[p]));
                    }
                    cuadroTexto.append("\n");
                    cuadroTexto.append(String.format(bundle.getString("estadisticas3.cabecera2trozo1"), " "));                    
                    for (int p = 0; p < e1.percents.length + 2; p++) {
                        cuadroTexto.append(bundle.getString("estadisticas3.cabecera2trozo2"));
                    }
                    cuadroTexto.append("\n");
                    //cuadroTexto.append("Fecha        (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs)\n"+
                  //  cuadroTexto.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
                  
                    for (int h = 1; h < horizonte_; h++) {
                        cuadroTexto.append(e1.from.plusDays(h).format(e1.formatoFecha) + "  ");
                        cuadroTexto.append(String.format(formato2I, e2.nuevosPositivosTotales[nge][h], e1.nuevosPositivosTotales[nge][h]));
                        //aqui tiene que ir el observed que toque percentil/total..

                        cuadroTexto.append(String.format(formato3, e1.statsUH[t][e1.percents.length + 2][nge][h]));
                        cuadroTexto.append(String.format(formato2, e2.statsUH[t][0][nge][h], e1.statsUH[t][0][nge][h]));
                        cuadroTexto.append(String.format(formato2, e2.statsUH[t][1][nge][h], e1.statsUH[t][1][nge][h]));
                        for (int ns = 2; ns < numStats; ns++) {
                            cuadroTexto.append(String.format(formato2_0, e2.statsUH[t][ns][nge][h], e1.statsUH[t][ns][nge][h]));
                        }
                        cuadroTexto.append("\n");
                    }
                    cuadroTexto.append("\n\n");
                }
            }
      //      cuadroTexto.append(String.format("\n\n          Hospital       Uci     "));
        //    cuadroTexto.append(String.format("\n%-10s%8.3f%% %8.3f%%", "Sim real", e2.indicesJavier[1][0], e2.indicesJavier[0][0]));
        //    cuadroTexto.append(String.format("\n%-10s%8.3f%% %8.3f%%", "Sim HW", e1.indicesJavier[1][0], e1.indicesJavier[0][0]));
         //   cuadroTexto.append(String.format("\nReal      %8.3f%% %8.3f%%", e2.indicesJavier[1][1], e2.indicesJavier[0][1]));
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelSuperior = new javax.swing.JScrollPane();
        cuadroTexto = new javax.swing.JTextArea();
        panelInferior = new javax.swing.JPanel();
        botonExportar = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        botonSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("estadisticas1.ventana.titulo")
        );
        setMinimumSize(new java.awt.Dimension(550, 530));

        cuadroTexto.setColumns(20);
        cuadroTexto.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        cuadroTexto.setRows(5);
        cuadroTexto.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelSuperior.setViewportView(cuadroTexto);

        getContentPane().add(panelSuperior, java.awt.BorderLayout.CENTER);

        panelInferior.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        panelInferior.setAlignmentX(0.0F);
        panelInferior.setAlignmentY(0.0F);
        panelInferior.setLayout(new javax.swing.BoxLayout(panelInferior, javax.swing.BoxLayout.X_AXIS));

        botonExportar.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        botonExportar.setText(bundle.getString("estadisticas1.button.exportar")
        );
        botonExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonExportarActionPerformed(evt);
            }
        });
        panelInferior.add(botonExportar);
        panelInferior.add(filler1);

        botonSalir.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        botonSalir.setText(bundle.getString("estadisticas1.button.cerrar")
        );
        botonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSalirActionPerformed(evt);
            }
        });
        panelInferior.add(botonSalir);

        getContentPane().add(panelInferior, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSalirActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_botonSalirActionPerformed

    private void botonExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonExportarActionPerformed
        // TODO add your handling code here:  System.getProperty("user.home")

        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileFilter filter = new FileNameExtensionFilter(bundle.getString("exportar.textarea.nombreExtension"),"txt");
        
        jfc.addChoosableFileFilter(filter);
        jfc.showSaveDialog(this);        
        File f = jfc.getSelectedFile();
        if(f==null)return;
        if(f.exists() && JOptionPane.showConfirmDialog( this,                        
                        String.format(bundle.getString("exportar.textarea.ficheroExistente"),f.getName())) !=JOptionPane.OK_OPTION) return;
        String nombre = f.getAbsolutePath();
        if(!nombre.toUpperCase().endsWith(".TXT")) nombre += ".txt";
            
        JOptionPane.showMessageDialog(this, String.format(bundle.getString("exportar.textarea.guardadoennombrefichero"),f.getName()));
        try (BufferedWriter fileOut = new BufferedWriter(new FileWriter(new File(nombre)))) {
            cuadroTexto.write(fileOut);
        } catch (IOException ex) {
            Logger.getLogger(TextStats1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_botonExportarActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonExportar;
    private javax.swing.JButton botonSalir;
    private javax.swing.JTextArea cuadroTexto;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JScrollPane panelSuperior;
    // End of variables declaration//GEN-END:variables
}
