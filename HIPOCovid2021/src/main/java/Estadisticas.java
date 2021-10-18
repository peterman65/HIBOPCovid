/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.JTextArea;
import org.apache.commons.math3.stat.StatUtils;


/**
 *
 * @author pmateo
 */
public class Estadisticas {

    /**
     * @return the indicesJavier
     */
    public double[][] getIndicesJavier() {
        return indicesJavier;
    }

    protected final double uci[][][];
    private final double hospital[][][];
    protected final int nuevosPositivosTotales[][];
    protected final LocalDate from;
    protected final LocalDate until;
    private final JTextArea resText;
    protected final double percents[]={5,10,50,90,95};
    protected int numGrupos;
    protected double statsUH[][][][];
//   private double realDataUH[][][];
    //protected final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    protected final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("uuuu/MM/dd");
    protected String realDataName=null;
    protected String realDataNameGroups=null;
    protected final String dataDirectory;
    protected double indicesJavier[][];
    private  final int horizonte;
    
    
    Estadisticas(double u[][][], double h[][][], int npt[][],LocalDate fr, LocalDate un, JTextArea jta,String realD,String dadi,String realDG) {
        uci = u;
        hospital = h;
        nuevosPositivosTotales=npt;
        from = fr;
        until=un;
        resText=jta;
        realDataName=realD;
        realDataNameGroups=realDG;
        dataDirectory=dadi;
        horizonte = uci[0].length;
        numGrupos= uci.length;
    }
    
    
    //[gdp.getNumero_grupos_edad()][horizonte_][numero_de_replicas];
     void calculaStadisticas() {
        int nGruposG2 = uci.length;
        
        int numReplicas = uci[0][0].length;
//        grupos = new String[nGruposG2];
     //   for(int i=0;i<nGruposG2;i++)grupos[i]=bundle.getString("estadistica.grupo")+(i+1);
        statsUH = new double[2][percents.length + 3][nGruposG2 + 1][horizonte];  //0=UCI, 1=HOSPITAL, grupos de edad + total+ real
        for (int nS = 0; nS < percents.length + 2; nS++) {
            for (int nge = 0; nge < nGruposG2; nge++) {
                for (int h = 0; h < horizonte; h++) {
                    switch (nS) {
                        case 0://media
                            statsUH[0][nS][nge][h] = StatUtils.mean(uci[nge][h]);
                            statsUH[1][nS][nge][h] = StatUtils.mean(hospital[nge][h]);
                            break;
                        case 1://desviacion tipica
                            statsUH[0][nS][nge][h] = Math.sqrt(StatUtils.variance(uci[nge][h]));
                            statsUH[1][nS][nge][h] = Math.sqrt(StatUtils.variance(hospital[nge][h]));
                            break;
                        default: //percentiles
                            statsUH[0][nS][nge][h] = StatUtils.percentile(uci[nge][h], percents[nS - 2]);
                            statsUH[1][nS][nge][h] = StatUtils.percentile(hospital[nge][h], percents[nS - 2]);
                    }
                }
            }

        }
        
        
         double total[][][] = new double[2][horizonte][numReplicas];
         for (int h = 0; h < horizonte; h++) {
             for (int r = 0; r < numReplicas; r++) {
                 for (int ng = 0; ng < nGruposG2; ng++) {
                     total[0][h][r] += uci[ng][h][r];
                     total[1][h][r] += hospital[ng][h][r];
                 }
             }
         }
        
        for (int nS = 0; nS < percents.length + 2; nS++) {
                for (int h = 0; h < horizonte; h++) {
                    switch (nS) {
                        case 0://media
                            statsUH[0][nS][nGruposG2][h] = StatUtils.mean(total[0][h]);
                            statsUH[1][nS][nGruposG2][h] = StatUtils.mean(total[1][h]);
                            break;
                        case 1://desviacion tipica
                            statsUH[0][nS][nGruposG2][h] = Math.sqrt(StatUtils.variance(total[0][h]));
                            statsUH[1][nS][nGruposG2][h] = Math.sqrt(StatUtils.variance(total[1][h]));
                            break;
                        default: //percentiles
                            statsUH[0][nS][nGruposG2][h] = StatUtils.percentile(total[0][h], percents[nS - 2]);
                            statsUH[1][nS][nGruposG2][h] = StatUtils.percentile(total[1][h], percents[nS - 2]);
                    }
                }
        }
        //Ahora cargamos los datos reales
        lee_ficheros_evolucion_real();
        calculaIndices();

    }

    /*void dibujaEstadísticas() {
        int nGruposEdad = uci.length;

        int horizonte = uci[0].length;
        int numReplicas = uci[0][0].length;

        GraficosStats1 grfS = new GraficosStats1(statsUH, grupos, percents, from, until);
        grfS.setVisible(true);
    }
*/
   // void dibujaEstadísticasGenerales() {
    
  //      GraficosStats1 grfS = new GraficosStats1(statsUH, grupos, percents, from, until);
 //       grfS.setVisible(true);
  //  }    
    /*void dibujaEstadísticas2() {
        int nGruposEdad = uci.length;

        int horizonte = uci[0].length;
        int numReplicas = uci[0][0].length;

        GraficosStats2 grfS = new GraficosStats2(statsUH, nuevosPositivosTotales, grupos, percents, from, until);
        grfS.setVisible(true);
    }
    */
    
    //void dibujaEstadísticasComparacion() {
        
    //    GraficosStats3 grfS = new GraficosStats3(statsUH, nuevosPositivosTotales, grupos, percents, from, until,dataDirectory,realDataName);
     //   grfS.setVisible(true);
   // }
  
  //  void dibujaEstadísticasComparacion(Estadisticas e) {
        
  //     GraficosStats4 grfS = new GraficosStats4(statsUH, e.statsUH, nuevosPositivosTotales, grupos, percents, from, until,dataDirectory,realDataName);
  //      grfS.setVisible(true);
  //  }
    
    /**
     * Estadísticas generales, media, sd y percentiles pero treas a tres, mostrando
     * lo obtenido con las predicciones de los nuevos, con los nuevos reales y el histórico.
     * Es necesario que el argumento e, contenga las estadísticas ya calculadas.
     * 
     * @param e 
     */
    //void escribeResumenEstadisticasGenerales(Estadisticas e) {
    /*    int nGrupos = uci.length;
        int horizonte_ = uci[0].length;
        int numStats = 2+percents.length;
        String formato1=" %15.0f%% ";
        String formato2="(%7.2f/%7.2f) ";
        String txt[]={"UCI     ",
                      "HOSPITAL"};
        resText.append("\n Resumen de estadísticas:\n La tabla siguiente contiene la media, desviación típica y los percentiles");
        for (int i = 0; i < percents.length; i++) {
            resText.append(" " + percents[i]);
        }
        resText.append(" por grupos, para la UCI y para el Hospital. \n"
                + "Entre paréntesis primero el valor correspondiente al resultado utilizando la predicción de nuevos infectados y el"
                + " segundo valor utilizando el valor observado de los nuevos infectados.");
        
        
        for (int t = 0; t < 2; t++) {
            for (int nge = 0; nge < nGrupos+1; nge++) {
                if(nge<nGrupos)
                    resText.append(txt[t] + ": grupo nº: " + nge + 
                                            "\n=====================\n\n");
                else
                                   resText.append(txt[t] + ": Total \n=====================\n\n");
                resText.append("                         mean                sd ");
                for (int p = 0; p < percents.length; p++) {
                    resText.append(String.format(formato1, percents[p]));
                }
                resText.append("\n");
                resText.append("Fecha        (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs) (Pred   /    Obs)\n"+
                               "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
                for (int h = 0; h < horizonte_; h++) {
                    resText.append(from.plusDays(h).format(formatoFecha) + "   ");
                    for (int ns = 0; ns < numStats; ns++) {
                        resText.append(String.format(formato2, statsUH[t][ns][nge][h],e.statsUH[t][ns][nge][h]));
                    }
                    resText.append("\n");
                }
                resText.append("\n\n");
            }
        }
        //resText.append("\nPredicción\n----------");
        //escribeIndices();
        //resText.append("\n\nObservado\n----------");
       // e.escribeIndices();
        
       e.escribeIndices(getIndicesJavier(),"Sim real","Sim HW");
       */
  //     TextStats1 tst = new TextStats1("titulito2",this,e);
  //     tst.rellena();
   //    tst.setVisible(true);
       
       
       
  // }
    

    
    
   // void escribeResumenEstadisticasGenerales() {
  /*     int nGrupos = uci.length;
        int horizonte_ = uci[0].length;
        int numStats = 2+percents.length;
        String formato="%7.2f ";
        String txt[]={"UCI","HOSPITAL"};
        resText.append("\n Resumen de estadísticas:\n La tabla siguiente contiene la media, desviación típica y los percentiles");
        for (int i = 0; i < percents.length; i++) {
            resText.append(" " + percents[i]);
        }
        resText.append(" por grupos, para la UCI y para el Hospital.\n\n");
        
        
        for (int t = 0; t < 2; t++) {
            for (int nge = 0; nge < nGrupos+1; nge++) {
                if(nge<nGrupos)
                    resText.append(txt[t] + ": grupo nº: " + nge + "\n-----------------------------------------------------\n\n");
                else
                    resText.append(txt[t] + ": Total \n-----------------------------------------------------\n\n");
                resText.append("dd/mm/aaaa   mean     sd ");
                for (int p = 0; p < percents.length; p++) {
                    resText.append(String.format(formato, percents[p]));
                }
                resText.append("\n");
                for (int h = 0; h < horizonte_; h++) {
                    resText.append(from.plusDays(h).format(formatoFecha) + " ");
                    for (int ns = 0; ns < numStats; ns++) {
                        resText.append(String.format(formato, statsUH[t][ns][nge][h]));
                    }
                    resText.append("\n");
                }
                resText.append("\n\n");
            }
        }
        escribeIndices();
        
        */
  //      TextStats1 ts1 = new TextStats1("titulito",this);
  ///      ts1.rellena();
   //     ts1.setVisible(true);
   // }

    //void escribeResumenEstadisticasComparacion(Estadisticas e) {
//        int nGrupos = uci.length;
//        int horizonte_ = uci[0].length;
//        int numStats = 2+percents.length;
//        String formato="%6.1f";
//        String txt[]={"UCI     ",
//                      "HOSPITAL"};
//        resText.append("\n\n\n Resumen de estadísticas:\n La tabla siguiente contiene para cada día:\n\n   -Número de nuevos positivos.\n   -Hospitalizados (media, 5% inferior, 5% superior).\n   -UCI la media(media, 5% inferior, 5% superior)\n"
//                +" para predicción y observados (Pred/Obs).\n\n");
//        resText.append("            Positivos     Hospital                                        UCI\n");
//        resText.append("                          Media          5% Inf.        5% Sup.           Media          5% Inf.        5% Sup.\n");
//        resText.append("            Pred/Obs      Pred  /Obs     Pred  /Obs     Pred  /Obs        Pred  /Obs     Pred  /Obs     Pred  /Obs\n");
//        resText.append("---------------------------------------------------------------------------------------------------------------------\n");
//        for (int h = 0; h < horizonte_; h++) {
//            /*Fecha*/       resText.append(from.plusDays(h).format(formatoFecha) + "  ");
//            /*Positivos*/   resText.append(String.format("%4d/%4d   ", nuevosPositivosTotales[h],e.nuevosPositivosTotales[h]));
//            /*Hosp, media*/ resText.append("  "+String.format(formato, statsUH[1][0][nGrupos][h])+"/");
//                            resText.append(String.format(formato, e.statsUH[1][0][nGrupos][h]));
//            /*Hosp, 5%inf*/ resText.append("  "+String.format(formato, statsUH[1][2][nGrupos][h])+"/");
//                            resText.append(String.format(formato, e.statsUH[1][2][nGrupos][h]));
//            /*Hosp, 5%sup*/ resText.append("  "+String.format(formato, statsUH[1][numStats-1][nGrupos][h])+"/");
//                            resText.append(String.format(formato, e.statsUH[1][numStats-1][nGrupos][h]));
//            resText.append("   ");
//            /*UCI , media*/ resText.append("  "+String.format(formato, statsUH[0][0][nGrupos][h])+"/");
//                            resText.append(String.format(formato, e.statsUH[0][0][nGrupos][h]));
//            /*UCI , 5%inf*/ resText.append("  "+String.format(formato, statsUH[0][2][nGrupos][h])+"/");
//                            resText.append(String.format(formato, e.statsUH[0][2][nGrupos][h]));
//            /*UCI , 5%sup*/ resText.append("  "+String.format(formato, statsUH[0][numStats-1][nGrupos][h])+"/");
//                            resText.append(String.format(formato, e.statsUH[0][numStats-1][nGrupos][h]));
//            resText.append("\n");
//        }
//        resText.append("\n");
//        resText.append("\nPredicción\n----------");
//        escribeIndices();
//        resText.append("\n\nObservado\n----------");
//        e.escribeIndices();
//        
//        e.escribeIndices(getIndicesJavier(),"Sim real","Sim HW");
//        
    //    TextStats2 tst = new TextStats2("titulito2",this,e);
    //   tst.rellena();
    //   tst.setVisible(true);
       
        
        
  //  }
    
 //   void escribeResumenEstadisticasComparacion() {
//                int nGrupos = uci.length;
//        int horizonte_ = uci[0].length;
//        int numStats = 2+percents.length;
//        String formato="%07.2f  ";
//        String txt[]={"UCI","HOSPITAL"};
//        resText.append("\n\n\n Resumen de estadísticas:\n La tabla siguiente contiene para cada día:\n\n   -Número de nuevos positivos.\n   -Hospitalizados (media, 5% inferior, 5% superior).\n   -UCI la media(media, 5% inferior, 5% superior).\n\n");
//        resText.append("            Positivos     Hospital                      UCI\n");
//        resText.append("                          Media    5% Inf.  5% Sup.     Media    5% Inf.  5% Sup.\n");
//        resText.append("---------------------------------------------------------------------------------\n");
//        for (int h = 0; h < horizonte_; h++) {
//            /*Fecha*/       resText.append(from.plusDays(h).format(formatoFecha) + "  ");
//            /*Positivos*/   resText.append(String.format("%09d     ", nuevosPositivosTotales[h]));
//            /*Hosp, media*/ resText.append(String.format(formato, statsUH[1][0][nGrupos][h]));
//            /*Hosp, 5%inf*/ resText.append(String.format(formato, statsUH[1][2][nGrupos][h]));
//            /*Hosp, 5%sup*/ resText.append(String.format(formato, statsUH[1][numStats-1][nGrupos][h]));
//            resText.append("   ");
//            /*UCI , media*/ resText.append(String.format(formato, statsUH[0][0][nGrupos][h]));
//            /*UCI , 5%inf*/ resText.append(String.format(formato, statsUH[0][2][nGrupos][h]));
//            /*UCI , 5%sup*/ resText.append(String.format(formato, statsUH[0][numStats-1][nGrupos][h]));
//            resText.append("\n");
//        }
//        resText.append("\n");
//        escribeIndices();
//        TextStats2 ts2 = new TextStats2("Titulito", this);
//        ts2.rellena();
 //       ts2.setVisible(true);
  //  }

     
    void lee_ficheros_evolucion_(){
        
        int nGruposEdad = uci.length;        
        LocalDate ld;
        int d;
     //   realDataUH = new double[2][nGruposEdad + 1][horizonte_];  //0=UCI, 1=HOSPITAL, grupos de edad + total, dias
        BufferedReader buf=null;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+realDataName));
                
                String words[] = null;
                String lineJustFetched = null;
                lineJustFetched = buf.readLine(); //Salto la cabecera
                while (true) {
                    lineJustFetched = buf.readLine();
                    if (lineJustFetched == null) {
                        break;
                    } else {
                        words = lineJustFetched.split(",");
                        ld = LocalDate.parse(words[0], formato);
                        if (ld.isBefore(from) || ld.isAfter(until)) {
                            continue;
                        }
                        d = (int) ChronoUnit.DAYS.between(from, ld);
                       statsUH[1][percents.length + 2][nGruposEdad][d] = Double.parseDouble(words[2]);
                       statsUH[0][percents.length + 2][nGruposEdad][d] = Double.parseDouble(words[3]);
                    }
                }
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+realDataNameGroups));
                
                String words[] = null;
                String lineJustFetched = null;
                lineJustFetched = buf.readLine(); //Salto la cabecera
                while (true) {
                    lineJustFetched = buf.readLine();
                    if (lineJustFetched == null) {
                        break;
                    } else {
                        words = lineJustFetched.split(",");
                        ld = LocalDate.parse(words[0], formato);
                        if (ld.isBefore(from) || ld.isAfter(until)) {
                            continue;
                        }
                        d = (int) ChronoUnit.DAYS.between(from, ld);
                        for(int i=0;i<nGruposEdad;i++){
                           statsUH[1][percents.length + 2][i][d] = Double.parseDouble(words[1+3*i+1]); //hospital
                           statsUH[0][percents.length + 2][i][d] = Double.parseDouble(words[1+3*i+2]); //UCI             
                        
                        }
                    }
                }
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    }     
     
      /**
     * Esta función lee los ficheros de evolución de los hospitalizados y de los 
     * que van a UCI, carga las estructuras adecuadas para su posterior visualización
     * 
     * @param fichero 
     */
    void lee_ficheros_evolucion_real(){
        
        int nGruposEdad = uci.length;        
        LocalDate ld;
        int d;
     //   realDataUH = new double[2][nGruposEdad + 1][horizonte_];  //0=UCI, 1=HOSPITAL, grupos de edad + total, dias
        BufferedReader buf=null;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+realDataName));
                
                String words[] = null;
                String lineJustFetched = null;
                lineJustFetched = buf.readLine(); //Salto la cabecera
                while (true) {
                    lineJustFetched = buf.readLine();
                    if (lineJustFetched == null) {
                        break;
                    } else {
                        words = lineJustFetched.split(",");
                        ld = LocalDate.parse(words[0], formato);
                        if (ld.isBefore(from) || ld.isAfter(until)) {
                            continue;
                        }
                        d = (int) ChronoUnit.DAYS.between(from, ld);
                       statsUH[1][percents.length + 2][nGruposEdad][d] = Double.parseDouble(words[2]);
                       statsUH[0][percents.length + 2][nGruposEdad][d] = Double.parseDouble(words[3]);
                    }
                }
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+realDataNameGroups));
                
                String words[] = null;
                String lineJustFetched = null;
                lineJustFetched = buf.readLine(); //Salto la cabecera
                while (true) {
                    lineJustFetched = buf.readLine();
                    if (lineJustFetched == null) {
                        break;
                    } else {
                        words = lineJustFetched.split(",");
                        ld = LocalDate.parse(words[0], formato);
                        if (ld.isBefore(from) || ld.isAfter(until)) {
                            continue;
                        }
                        d = (int) ChronoUnit.DAYS.between(from, ld);
                        for(int i=0;i<nGruposEdad;i++){
                           statsUH[1][percents.length + 2][i][d] = Double.parseDouble(words[1+3*i+1]); //hospital
                           statsUH[0][percents.length + 2][i][d] = Double.parseDouble(words[1+3*i+2]); //UCI             
                        
                        }
                    }
                }
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    }
    
    
    void calculaIndices(){
    
       //  statsUH = new double[2][percents.length + 3][nGruposG2 + 1][horizonte_];  //0=UCI, 1=HOSPITAL, grupos de edad + total+ real
       int nGruposG2 = uci.length;
        indicesJavier = new double[2][2];
        //percents.length+1 totales simulados
        //percents.length+2 reales

        for (int t = 0; t < 2; t++) { //Hospital/UCI
            
            
            for (int h = 1; h < horizonte; h++) {
               indicesJavier[t][0]+=(
                       Math.abs(statsUH[t][0][nGruposG2][h] -statsUH[t][percents.length + 2][nGruposG2][h])
                       /
                       statsUH[t][percents.length + 2][nGruposG2][h]
                       );
            }
            
            for (int h = 1; h < horizonte; h++) {
               indicesJavier[t][1]+=(Math.abs(statsUH[t][percents.length + 2][nGruposG2][h]-statsUH[t][percents.length + 2][nGruposG2][h-1])/statsUH[t][percents.length + 2][nGruposG2][h] );
            }
            
            indicesJavier[t][0]*=(100./(horizonte-1.0));
            indicesJavier[t][1]*=(100./(horizonte-1.0));
        }
        
        
//        for (int t = 0; t < 2; t++) {
//        System.out.println();
//        for (int h = 0; h < horizonte_; h++) {
//               System.out.print(" "+statsUH[t][0][nGruposG2][h]);
//            }        
//        System.out.println();
//        for (int h = 0; h < horizonte_; h++) {
//               System.out.print(" "+statsUH[t][percents.length + 2][nGruposG2][h]);
//            }
//        }
    
    }
//    void escribeIndices(){
//        resText.append(String.format("\nUCI      Índice 1: %6.2f%% índice 2: %6.2f%%",indicesJavier[0][0],indicesJavier[0][1]));
//        resText.append(String.format("\nHospital Índice 1: %6.2f%% índice 2: %6.2f%%",indicesJavier[1][0],indicesJavier[1][1]));   
//    }
//    
//    void escribeIndices(double indices[][], String labelFirst,String labelSecond){//el argumento son los segundos y 
//        resText.append(String.format("\n\n          Hospital       Uci     "));
//        resText.append(String.format("\n%-10s%8.3f%% %8.3f%%",labelFirst,indicesJavier[1][0],indicesJavier[0][0]));
//        resText.append(String.format("\n%-10s%8.3f%% %8.3f%%",labelSecond,indices[1][0],indices[0][0]));
//        resText.append(String.format("\nReal      %8.3f%% %8.3f%%",indicesJavier[1][1],indicesJavier[0][1]));        
//    }
    
    /*
      void lee_fichero_evolucion_realSEG(){
        
        int nGrupos = uci.length;
        int horizonte_ = uci[0].length;
        LocalDate ld;
        int d;
        realDataUH = new double[2][nGrupos + 1][horizonte_];  //0=UCI, 1=HOSPITAL, grupos de edad + total, dias
        BufferedReader buf=null;
        DateTimeFormatter formato2 = DateTimeFormatter.ofPattern("dd/MM/uu");
        for (int v = 0; v < 2; v++) {
            try {
                if(v==0)
                 buf = new BufferedReader(new FileReader(realDataUName));
                else
                    buf = new BufferedReader(new FileReader(realDataHName));
                String words[] = null;
                String lineJustFetched = null;
                lineJustFetched = buf.readLine(); //Salto la cabecera
                while (true) {
                    lineJustFetched = buf.readLine();
                    if (lineJustFetched == null) {
                        break;
                    } else {
                        words = lineJustFetched.split(",");
                        ld = LocalDate.parse(words[0], formato2);
                        if (ld.isBefore(from) || ld.isAfter(until)) {
                            continue;
                        }
                        d = (int) ChronoUnit.DAYS.between(from, ld);
                      /*  for (int ge = 0; ge < nGruposG2 + 1; ge++) {
                            statsUH[v][percents.length + 2][ge][d] = Double.parseDouble(words[ge + 1]);
                        }*/
                      /* statsUH[v][percents.length + 2][nGrupos][d] = Double.parseDouble(words[1]);
                    }
                }
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

}

