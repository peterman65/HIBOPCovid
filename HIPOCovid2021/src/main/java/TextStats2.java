
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
public class TextStats2 extends javax.swing.JFrame {

    //private String titleWindow;
    private Estadisticas e1=null;
    private Estadisticas e2=null;
    private int tipo;
    private ResourceBundle bundle;
    private boolean usoFichDatosUsuario;
   
    /**
     * Creates new form TextStats1
     */
    public TextStats2() {
        initComponents();
    }

    public TextStats2(Estadisticas ee1,ResourceBundle bndl,boolean UPU) {
        super();
        e1=ee1;
     //   titleWindow = tW;
        tipo =1;
        bundle=bndl;
        usoFichDatosUsuario=UPU;
        initComponents();
    }
    public TextStats2(Estadisticas ee1,Estadisticas ee2,ResourceBundle bndl,boolean UPU) {
        super();
        e1=ee1;
        e2=ee2;
       // titleWindow = tW;
        tipo=2;
        bundle=bndl;
        usoFichDatosUsuario=UPU;
        initComponents();
    }
   
    void rellena() {
        int nGrupos = e1.uci.length;
        int horizonte_ = e1.uci[0].length;
        int numStats = e1.percents.length + 2;
        int numPercents = e1.percents.length;
        if (tipo == 1) {
            String formato = "%7.0f  ";
            String formato2 = "%7.2f  ";
            String txt[] = {bundle.getString("estadisticas2.uci"), bundle.getString("estadisticas2.hospital")};
            if(usoFichDatosUsuario)
            cuadroTexto.append(bundle.getString("estadisticas2.resumen.usuario"));
            else
                cuadroTexto.append(bundle.getString("estadisticas2.resumen.general"));
            cuadroTexto.append(String.format(bundle.getString("estadisticas2.cabeceratrozo1"), ""));
            //cuadroTexto.append("            Positivos     Hospital                      UCI\n");
            //cuadroTexto.append("                          Media    5% Inf.  5% Sup.     Media    5% Inf.  5% Sup.\n");
            cuadroTexto.append(bundle.getString("estadisticas2.cabeceratrozo2"));
            // cuadroTexto.append("---------------------------------------------------------------------------------\n");
            for (int h = 1; h < horizonte_; h++) {
                /*Fecha*/ cuadroTexto.append(e1.from.plusDays(h).format(e1.formatoFecha) + "  ");
                /*Positivos*/ cuadroTexto.append(String.format("%9d     ", e1.nuevosPositivosTotales[nGrupos][h]));
                /*UCI , media*/ cuadroTexto.append(String.format(formato2, e1.statsUH[0][0][nGrupos][h]));
                /*UCI , 5%inf*/ cuadroTexto.append(String.format(formato, e1.statsUH[0][2][nGrupos][h]));
                /*UCI , 5%sup*/ cuadroTexto.append(String.format(formato, e1.statsUH[0][numStats - 1][nGrupos][h]));

                cuadroTexto.append("   ");
                /*Hosp, media*/ cuadroTexto.append(String.format(formato2, e1.statsUH[1][0][nGrupos][h]));
                /*Hosp, 5%inf*/ cuadroTexto.append(String.format(formato, e1.statsUH[1][2][nGrupos][h]));
                /*Hosp, 5%sup*/ cuadroTexto.append(String.format(formato, e1.statsUH[1][numStats - 1][nGrupos][h]));
                cuadroTexto.append("\n");
            }
            cuadroTexto.append("\n");
            //   cuadroTexto.append(String.format("\nUCI      Índice 1: %6.2f%% índice 2: %6.2f%%",e1.indicesJavier[0][0],e1.indicesJavier[0][1]));
            //   cuadroTexto.append(String.format("\nHospital Índice 1: %6.2f%% índice 2: %6.2f%%",e1.indicesJavier[1][0],e1.indicesJavier[1][1])); 
        }else if (tipo == 2) {

            String formato = "%6.1f";
            String formato3 = "%-6.1f";
            String formato2 = "%6.0f";
            String formato4 = "%-6.0f";
            
            String txt[] = {bundle.getString("estadisticas4.uci"),
                bundle.getString("estadisticas4.hospital")};
            cuadroTexto.append(bundle.getString("estadisticas4.resumen"));            
            cuadroTexto.append(String.format(bundle.getString("estadisticas4.cabecera1"),""));            
            cuadroTexto.append(String.format(bundle.getString("estadisticas4.cabecera1_5"),""));
            cuadroTexto.append(String.format(bundle.getString("estadisticas4.cabecera2"),""));
            cuadroTexto.append(String.format(bundle.getString("estadisticas4.cabecera3"),""));
            
            //cuadroTexto.append("            Positivos     Hospital                                        UCI\n");
            //cuadroTexto.append("                          Media          5% Inf.        5% Sup.           Media          5% Inf.        5% Sup.\n");
            //cuadroTexto.append("            Pred/Obs      Pred  /Obs     Pred  /Obs     Pred  /Obs        Pred  /Obs     Pred  /Obs     Pred  /Obs\n");
           // cuadroTexto.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
            for (int h = 1; h < horizonte_; h++) {
                /*Fecha*/ cuadroTexto.append(e1.from.plusDays(h).format(e1.formatoFecha) + "  ");
                /*Positivos*/ cuadroTexto.append(String.format("%4d/%-4d   ", e2.nuevosPositivosTotales[nGrupos][h],e1.nuevosPositivosTotales[nGrupos][h] ));
                cuadroTexto.append("   ");
                /*UCI Observados*/cuadroTexto.append(String.format("%5.0f   ", e2.statsUH[0][numStats][nGrupos][h] ));  
                
                /*UCI , media*/ cuadroTexto.append("   " + String.format(formato, e2.statsUH[0][0][nGrupos][h]) + "/");
                cuadroTexto.append(String.format(formato3, e1.statsUH[0][0][nGrupos][h]));
                /*UCI , 5%inf*/ cuadroTexto.append("  " + String.format(formato2, e2.statsUH[0][2][nGrupos][h]) + "/");
                cuadroTexto.append(String.format(formato4, e1.statsUH[0][2][nGrupos][h]));
                /*UCI , 5%sup*/ cuadroTexto.append("  " + String.format(formato2, e2.statsUH[0][numStats - 1][nGrupos][h]) + "/");
                cuadroTexto.append(String.format(formato4, e1.statsUH[0][numStats - 1][nGrupos][h]));
                cuadroTexto.append("      ");
                /*hospital Observados*/cuadroTexto.append(String.format("%5.0f   ", e2.statsUH[1][numStats][nGrupos][h] ));  
                /*Hosp, media*/ cuadroTexto.append("  " + String.format(formato, e2.statsUH[1][0][nGrupos][h]) + "/");
                cuadroTexto.append(String.format(formato3, e1.statsUH[1][0][nGrupos][h]));                
                /*Hosp, 5%inf*/ cuadroTexto.append("  " + String.format(formato2, e2.statsUH[1][2][nGrupos][h]) + "/");
                cuadroTexto.append(String.format(formato4, e1.statsUH[1][2][nGrupos][h]));                
                /*Hosp, 5%sup*/ cuadroTexto.append("  " + String.format(formato2, e2.statsUH[1][numStats - 1][nGrupos][h]) + "/");
                cuadroTexto.append(String.format(formato4, e1.statsUH[1][numStats - 1][nGrupos][h]));
                cuadroTexto.append("\n");
            }
            cuadroTexto.append("\n");
            cuadroTexto.append(bundle.getString("estadisticas4.mape"));
            cuadroTexto.append("          "+bundle.getString("estadisticas4.uci")+"       "+bundle.getString("estadisticas4.hospital"));
            cuadroTexto.append(String.format("\n%-10s%8.3f%% %8.3f%%", bundle.getString("estadisticas4.observado"), e2.indicesJavier[0][0], e2.indicesJavier[1][0]));
            cuadroTexto.append(String.format("\n%-10s%8.3f%% %8.3f%%\n", bundle.getString("estadisticas4.proyectado"), e1.indicesJavier[0][0], e1.indicesJavier[1][0]));
            //cuadroTexto.append(String.format("\nReal      %8.3f%% %8.3f%%", e2.indicesJavier[1][1], e2.indicesJavier[0][1]));
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
        setTitle(bundle.getString("estadisticas2.ventana.titulo"));
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
        botonExportar.setText(bundle.getString("estadisticas4.button.exportar")
        );
        botonExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonExportarActionPerformed(evt);
            }
        });
        panelInferior.add(botonExportar);
        panelInferior.add(filler1);

        botonSalir.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        botonSalir.setText(bundle.getString("estadisticas4.button.cerrar")
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

    private void botonExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonExportarActionPerformed
        // TODO add your handling code here:
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

    private void botonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSalirActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_botonSalirActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TextStats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TextStats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TextStats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TextStats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TextStats1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonExportar;
    private javax.swing.JButton botonSalir;
    private javax.swing.JTextArea cuadroTexto;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel panelInferior;
    private javax.swing.JScrollPane panelSuperior;
    // End of variables declaration//GEN-END:variables
}
