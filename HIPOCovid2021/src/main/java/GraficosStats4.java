/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import javax.swing.JRadioButton;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.util.ShapeUtilities;

/**
 *
 * @author pmateo
 */
public class GraficosStats4 extends javax.swing.JFrame implements ActionListener{
    private final double[][][][] stats1;
    private final double[][][][] stats2;
    private final int numPositivosTotales[][];
    private double percents[];
    private LocalDate from;
    private LocalDate until;
  //  private javax.swing.JRadioButton grEdad_RB[];
  //  private javax.swing.JRadioButton percen_RB[];
    private TimeSeriesCollection dataset= null;
    private TimeSeries series[];
    private String names[];
    private JFreeChart chart;
    private XYPlot plot =null;
    private Day dias[]=null;
    private Day observedDays[]=null;
    private int numDias=-1;
    private int numGruEdad;
    private double observedData[][];    
    private String realDataName;
    private String dataDirectory;
    private int posFrom=-1;
    private int posUntil=-1;
    private int lagFrom=0;
    private int lagUntil=0;
    private XYLineAndShapeRenderer renderer;
    private ResourceBundle bundle;
    /**
     * Creates new form GraficosStats
     */
        
    public GraficosStats4(String titulo, Estadisticas e1, Estadisticas e2,ResourceBundle bndl) {
    stats1=e1.statsUH;
    stats2=e2.statsUH;
    numPositivosTotales=e1.nuevosPositivosTotales;
       percents=e1.percents;
       from=e1.from;
       until=e1.until;
       numGruEdad=e1.numGrupos;
       realDataName = e1.realDataName;
       dataDirectory=e1.dataDirectory;
       bundle=bndl;
     initComponents();
       //Creamos las series de datos, todas, y después ya apañaremos y pintaremos
       //las que interesen
       dataset= new TimeSeriesCollection();
       series = new TimeSeries[17];
       names = new String[17];
              
       //Añadimos escuchadores para los radio buttons
       
       UCI_Pre_RB.addActionListener(this);       
       hospital_Pre_RB.addActionListener(this);       
       positivos_Pre_RB.addActionListener(this);
       UCIObs_RB.addActionListener(this);
       hospitalObs_RB.addActionListener(this);
       positivosObs_RB.addActionListener(this);
       UCI_Real_RB.addActionListener(this);       
       hospital_Real_RB.addActionListener(this);       
       positivos_Real_RB.addActionListener(this);
       
       
   
       numDias = (int)from.until(until,ChronoUnit.DAYS)+1;
       LocalDate aux=from;
       dias = new Day[numDias];
       for(int i=0;i<numDias;i++){
           aux=from.plusDays(i);
           dias[i] = new Day(aux.getDayOfMonth(),aux.getMonthValue(),aux.getYear());
       }
       //Creamos el grafico
       creaGrafica();
       //Ponemos nombre a las series de datos
       //e inicializamos a -1 su contenido
       names[0]=bundle.getString("graficos4.grafico.leyenda.prediccion.proyectados");//"FP proyected";
       names[1]=bundle.getString("graficos4.grafico.leyenda.prediccion.media.hospital");//"FP Mean hospitalized";       
       names[2]=bundle.getString("graficos4.grafico.leyenda.prediccion.p5.hospital");//"FP p. 5th hosptalized";
       names[3]=bundle.getString("graficos4.grafico.leyenda.prediccion.p95.hospital");//"FP p. 95th hosptalized";
       names[4]=bundle.getString("graficos4.grafico.leyenda.prediccion.media.uci");//"FP Mean UCI";
       names[5]=bundle.getString("graficos4.grafico.leyenda.prediccion.p5.uci");//"FP p. 5th ICU";
       names[6]=bundle.getString("graficos4.grafico.leyenda.prediccion.p95.uci");//"FP p. 95th ICU";       
       names[7]=bundle.getString("graficos4.grafico.leyenda.observados.proyectados");//"FO proyected";
       names[8]=bundle.getString("graficos4.grafico.leyenda.observados.media.hospital");//"FO Mean hospitalized";       
       names[9]=bundle.getString("graficos4.grafico.leyenda.observados.p5.hospital");//"FO p. 5th hosptalized";
       names[10]=bundle.getString("graficos4.grafico.leyenda.observados.p95.hospital");//="FO p. 95th hosptalized";
       names[11]=bundle.getString("graficos4.grafico.leyenda.observados.media.uci");//="FO Mean UCI ";
       names[12]=bundle.getString("graficos4.grafico.leyenda.observados.p5.uci");//"FO p. 5th ICU";
       names[13]=bundle.getString("graficos4.grafico.leyenda.observados.p95.uci");//"FO p. 95th ICU";       
       names[14]=bundle.getString("graficos4.grafico.leyenda.positivos.observados");//"Positive observed";
       names[15]=bundle.getString("graficos4.grafico.leyenda.hospitalizados.observados");//"Hospitalized observed";
       names[16]=bundle.getString("graficos4.grafico.leyenda.uci.observados");//"ICU observed";
       
       
       
       lee_fichero_evolucion_real();      
       
       for(int i=0;i<17;i++) setSerie(i);
       
        if (posFrom == -1 && posUntil==-1){
          //Periodo de predicción sin intersección con historico (queda a su derecha)
          posFrom=0;
          posUntil=observedDays.length-1;
          JSliderPos.setEnabled(false);        
        }else
        if(posFrom!=-1 && posUntil==-1){
        //Periodo que arranca dentro del historico y acaba fuera (parcialmente a su derecha)
        posUntil=this.observedDays.length-1;
         JSliderPos.setEnabled(false);
        }else
        if(posFrom!=-1 && posUntil!=-1){
        //Periodo totalmente comprendido en el historico
          JSliderPos.setMaximum(observedDays.length - posUntil - 1);
        }
        JSliderPre.setMaximum(posFrom);
       
    }
    
   /* public GraficosStats4(double Sta1[][][][],double Sta2[][][][],int npt[],int grEd,double per[],LocalDate fro,LocalDate unt,String dadi,String realD) {        
       stats1=Sta1;
       stats2=Sta2;
       numPositivosTotales=npt;
       percents=per;
       from=fro;
       until=unt;
       numGruEdad=grEd;       
       realDataName = realD;
       dataDirectory=dadi;
       initComponents();
       //Creamos las series de datos, todas, y después ya apañaremos y pintaremos
       //las que interesen
       dataset= new TimeSeriesCollection();
       series = new TimeSeries[17];
       names = new String[17];
              
       //Añadimos escuchadores para los radio buttons
       
       UCI_Pre_RB.addActionListener(this);       
       hospital_Pre_RB.addActionListener(this);       
       positivos_Pre_RB.addActionListener(this);
       UCIObs_RB.addActionListener(this);
       hospitalObs_RB.addActionListener(this);
       positivosObs_RB.addActionListener(this);
       UCI_Real_RB.addActionListener(this);       
       hospital_Real_RB.addActionListener(this);       
       positivos_Real_RB.addActionListener(this);
       
       
   
       numDias = (int)from.until(until,ChronoUnit.DAYS)+1;
       LocalDate aux=from;
       dias = new Day[numDias];
       for(int i=0;i<numDias;i++){
           aux=from.plusDays(i);
           dias[i] = new Day(aux.getDayOfMonth(),aux.getMonthValue(),aux.getYear());
       }
       //Creamos el grafico
       creaGrafica();
       //Ponemos nombre a las series de datos
       //e inicializamos a -1 su contenido
       names[0]="FP proyected";
       names[1]="FP Mean hospitalized";       
       names[2]="FP p. 5th hosptalized";
       names[3]="FP p. 95th hosptalized";
       names[4]="FP Mean UCI ";
       names[5]="FP p. 5th ICU";
       names[6]="FP p. 95th ICU";       
       names[7]="FO proyected";
       names[8]="FO Mean hospitalized";       
       names[9]="FO p. 5th hosptalized";
       names[10]="FO p. 95th hosptalized";
       names[11]="FO Mean UCI ";
       names[12]="FO p. 5th ICU";
       names[13]="FO p. 95th ICU";       
       names[14]="Positive observed";
       names[15]="Hospitalized observed";
       names[16]="ICU observed";
       
       
       lee_fichero_evolucion_real();      
       
       for(int i=0;i<17;i++) setSerie(i);
       
        if (posFrom == -1 && posUntil==-1){
          //Periodo de predicción sin intersección con historico (queda a su derecha)
          posFrom=0;
          posUntil=observedDays.length-1;
          JSliderPos.setEnabled(false);        
        }
        if(posFrom!=-1 && posUntil==-1){
        //Periodo que arranca dentro del historico y acaba fuera (parcialmente a su derecha)
        posUntil=this.observedDays.length-1;
         JSliderPos.setEnabled(false);
        }
        if(posFrom!=-1 && posUntil!=-1){
        //Periodo totalmente comprendido en el historico
          JSliderPos.setMaximum(observedDays.length - posUntil - 1);
        }
        JSliderPre.setMaximum(posFrom);
    }
*/
void setSerie(int sitio){
        series[sitio] = new TimeSeries(names[sitio]);
        
            switch(sitio){
                case 0: //Positivos predicción/pred AZULES
                    for (int d = 1; d < numDias; d++) {series[sitio].add(dias[d],numPositivosTotales[numGruEdad][d]);}
                    series[sitio].add(dias[0],observedData[0][posFrom]);
                    renderer.setSeriesPaint( sitio, new Color(0,0,255));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Ellipse2D.Double(-2d, -2d, 4d, 4d));
                    break;
                case 1: //Hospital predicción/total/media/pred verdes
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats1[1][0][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(34, 139, 34));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Ellipse2D.Double(-2d, -2d, 4d, 4d));
                break;
                case 2: //Hospital prediccion /total/5% /pred verdes
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats1[1][2][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(34, 139, 34));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                    break;
                case 3: //Hospital prediccion /total/95% /pred verdes
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats1[1][6][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(34, 139, 34));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                    break;
                case 4: //UCI prediccion/total/media /pred
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats1[0][0][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(148, 0, 211));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Ellipse2D.Double(-2d, -2d, 4d, 4d));
                    break;
                case 5: //UCI prediccion/total/5% /pred
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats1[0][2][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(148, 0, 211));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                break;
                case 6: //UCI prediccion/total/95% /pred
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats1[0][6][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(148, 0, 211));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                break;                
                    
                case 7: //Positivos predicción/real AZULES
                    //  REVISA REVISA REVISA REVISA, QUE PINTO QUE PINTO QUE PINTO
                    for (int d = posFrom-lagFrom; d <= posUntil+lagUntil; d++) {series[sitio].add(observedDays[d], observedData[0][d]);}
                    //for (int d = 1; d < numDias; d++) {series[sitio].add(dias[d],numPositivosTotales[d]);}
//                    series[sitio].add(dias[0],observedData[0][posFrom]);
                    renderer.setSeriesPaint( sitio, new Color(0,0,139));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, ShapeUtilities.createDownTriangle(2f));
                    break;
                case 8: //Hospital predicción/total/media/real verdes
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats2[1][0][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(152, 251, 152));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, ShapeUtilities.createDownTriangle(2f));
                break;
                case 9: //Hospital prediccion /total/5% /real verdes
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats2[1][2][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(152, 251, 152));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                    break;
                case 10: //Hospital prediccion /total/95% /real verdes
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats2[1][6][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(152, 251, 152));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                    break;
                case 11: //UCI prediccion/total/media/real
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats2[0][0][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(221, 160, 221));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, ShapeUtilities.createDownTriangle(2f));
                    break;
                case 12: //UCI prediccion/total/5%/real
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats2[0][2][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(221, 160, 221));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                break;
                case 13: //UCI prediccion/total/95%/real
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats2[0][6][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(221, 160, 221));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                break;
                case 14: //Positivos observados AZULES
                    for (int d = posFrom-lagFrom; d <= posUntil+lagUntil; d++) {series[sitio].add(observedDays[d], observedData[0][d]);}
                    renderer.setSeriesPaint( sitio, new Color(0,191,255));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0));
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {2.0f, 2.0f}, 0.0f));
                    break;
                case 15: //Hospital obesrvados Verdes
                    for (int d = posFrom-lagFrom; d <= posUntil+lagUntil; d++) {series[sitio].add(observedDays[d], observedData[1][d]);}
                    renderer.setSeriesPaint( sitio, new Color(0,80,0));
                    renderer.setSeriesShape(sitio, new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {2.0f, 2.0f}, 0.0f));
                    break;
                case 16: //UCI observados
                    for (int d = posFrom-lagFrom; d <= posUntil+lagUntil; d++) {series[sitio].add(observedDays[d], observedData[2][d]);}
                    renderer.setSeriesPaint( sitio, new Color(75, 0, 130));
                    renderer.setSeriesShape(sitio, new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {2.0f, 2.0f}, 0.0f));
                    break;
            }
        dataset.addSeries(series[sitio]);         
        renderer.setSeriesLinesVisible(sitio, true);
        //renderer.setSeriesShapesVisible(sitio, true);
        if(sitio!=3 && sitio!=6 && sitio!=10 && sitio!=13)
             renderer.setSeriesVisibleInLegend(sitio, true);
        else
             renderer.setSeriesVisibleInLegend(sitio, false);
        
}
    
void creaGrafica(){

        chart = ChartFactory.createTimeSeriesChart(
            bundle.getString("graficos4.grafico.title"),  // title
            bundle.getString("graficos4.grafico.xlabel"),             // x-axis label
            bundle.getString("graficos4.grafico.ylabel"),   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        plot = (XYPlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundPaint(new Color(251,251,252));
//        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinePaint(new Color(207,207,210));
//        plot.setRangeGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(new Color(207,207,210));
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        //plot.setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        TickUnits units = new TickUnits();
        units.add(new DateTickUnit(DateTickUnitType.DAY, 1));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 2));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 3));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 4));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 5));     
        units.add(new DateTickUnit(DateTickUnitType.DAY, 10));     
        axis.setStandardTickUnits(units);
        
        //axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));        
        //axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
        axis.setVerticalTickLabels(true);
        LegendTitle legend = chart.getLegend();        
        legend.setPosition(RectangleEdge.RIGHT);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        this.add(chartPanel,BorderLayout.CENTER);
        

}    
    
void lee_fichero_evolucion_real(){

        Day ldFrom,ldUntil;
        int d;
        String words[] = null;
        String lineJustFetched = null;
        ldFrom = new Day(from.getDayOfMonth(),from.getMonthValue(),from.getYear());
        ldUntil = new Day(until.getDayOfMonth(),until.getMonthValue(),until.getYear());
        BufferedReader buf=null;
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+realDataName));
                lineJustFetched = buf.readLine(); //Salto la cabecera
                d=0;
                while (true) {
                    lineJustFetched = buf.readLine();
                    if (lineJustFetched == null) break; else d++;
                }
                buf.close();
                observedData = new double[3][d];
                observedDays = new Day[d];
                
                buf = new BufferedReader(new FileReader(dataDirectory+File.separator+realDataName));
                lineJustFetched = buf.readLine(); //Salto la cabecera
                d=0;
                while (true) {
                    lineJustFetched = buf.readLine();
                    if (lineJustFetched == null) {
                        break;
                    } else {
                        words = lineJustFetched.split(",");
                     //   ld = LocalDate.parse(words[0], formato);
                    //    d = (int) ChronoUnit.DAYS.between(from, ld);
                        observedData[0][d]=Double.parseDouble(words[1]);//positivos
                        observedData[1][d]=Double.parseDouble(words[2]);//hospital
                        observedData[2][d]=Double.parseDouble(words[3]);//UCI
                        words=words[0].split("-");
                        observedDays[d]=new Day(Integer.parseInt(words[2]),Integer.parseInt(words[1]),Integer.parseInt(words[0]));
                        if(observedDays[d].equals(ldFrom)){
                            posFrom=d;
                        }
                        else 
                            if(observedDays[d].equals(ldUntil)){
                                posUntil=d;
                            }
                        d++;
                       //statsUH[1][percents.length + 2][nGruposEdad][d] = Double.parseDouble(words[2]);
                       //statsUH[0][percents.length + 2][nGruposEdad][d] = Double.parseDouble(words[3]);
                    }
                }
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
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

        botoneraGraficos = new javax.swing.JPanel();
        UciHosPosPred = new javax.swing.JPanel();
        positivos_Pre_RB = new javax.swing.JRadioButton();
        hospital_Pre_RB = new javax.swing.JRadioButton();
        UCI_Pre_RB = new javax.swing.JRadioButton();
        UciHosPosReal = new javax.swing.JPanel();
        positivos_Real_RB = new javax.swing.JRadioButton();
        hospital_Real_RB = new javax.swing.JRadioButton();
        UCI_Real_RB = new javax.swing.JRadioButton();
        observDataPanel = new javax.swing.JPanel();
        positivosObs_RB = new javax.swing.JRadioButton();
        hospitalObs_RB = new javax.swing.JRadioButton();
        UCIObs_RB = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        JSliderPre = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        JSliderPos = new javax.swing.JSlider();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("graficos4.ventana.titulo"));
        setMinimumSize(new java.awt.Dimension(600, 450));

        botoneraGraficos.setMaximumSize(new java.awt.Dimension(220, 370));
        botoneraGraficos.setPreferredSize(new java.awt.Dimension(220, 370));
        botoneraGraficos.setLayout(new javax.swing.BoxLayout(botoneraGraficos, javax.swing.BoxLayout.Y_AXIS));

        UciHosPosPred.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos4.titPanel.prediccionHW"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
        UciHosPosPred.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        UciHosPosPred.setMaximumSize(new java.awt.Dimension(220, 100));
        UciHosPosPred.setMinimumSize(new java.awt.Dimension(100, 70));
        UciHosPosPred.setPreferredSize(new java.awt.Dimension(220, 100));
        UciHosPosPred.setLayout(new javax.swing.BoxLayout(UciHosPosPred, javax.swing.BoxLayout.Y_AXIS));

        positivos_Pre_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        positivos_Pre_RB.setSelected(true);
        positivos_Pre_RB.setText(bundle.getString("graficos4.positivos"));
        positivos_Pre_RB.setActionCommand("Positivos_RB");
        UciHosPosPred.add(positivos_Pre_RB);

        hospital_Pre_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        hospital_Pre_RB.setSelected(true);
        hospital_Pre_RB.setText(bundle.getString("graficos4.hospital"));
        hospital_Pre_RB.setActionCommand("Hospital_RB");
        hospital_Pre_RB.setName("1"); // NOI18N
        UciHosPosPred.add(hospital_Pre_RB);

        UCI_Pre_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        UCI_Pre_RB.setSelected(true);
        UCI_Pre_RB.setText(bundle.getString("graficos4.uci"));
        UCI_Pre_RB.setActionCommand("UCI_RB");
        UCI_Pre_RB.setName("0"); // NOI18N
        UciHosPosPred.add(UCI_Pre_RB);

        botoneraGraficos.add(UciHosPosPred);

        UciHosPosReal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos4.titPanel.prediccionOBS"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
        UciHosPosReal.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        UciHosPosReal.setMaximumSize(new java.awt.Dimension(220, 100));
        UciHosPosReal.setMinimumSize(new java.awt.Dimension(100, 70));
        UciHosPosReal.setPreferredSize(new java.awt.Dimension(220, 100));
        UciHosPosReal.setLayout(new javax.swing.BoxLayout(UciHosPosReal, javax.swing.BoxLayout.Y_AXIS));

        positivos_Real_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        positivos_Real_RB.setSelected(true);
        positivos_Real_RB.setText(bundle.getString("graficos4.positivos"));
        positivos_Real_RB.setActionCommand("Positivos_Real_RB");
        UciHosPosReal.add(positivos_Real_RB);

        hospital_Real_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        hospital_Real_RB.setSelected(true);
        hospital_Real_RB.setText(bundle.getString("graficos4.hospital"));
        hospital_Real_RB.setActionCommand("Hospital_Real_RB");
        UciHosPosReal.add(hospital_Real_RB);

        UCI_Real_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        UCI_Real_RB.setSelected(true);
        UCI_Real_RB.setText(bundle.getString("graficos4.uci"));
        UCI_Real_RB.setActionCommand("UCI_Real_RB");
        UciHosPosReal.add(UCI_Real_RB);

        botoneraGraficos.add(UciHosPosReal);

        observDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos4.titPanel.observados"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
        observDataPanel.setAlignmentX(0.0F);
        observDataPanel.setMaximumSize(new java.awt.Dimension(220, 170));
        observDataPanel.setMinimumSize(new java.awt.Dimension(100, 70));
        observDataPanel.setPreferredSize(new java.awt.Dimension(220, 170));
        observDataPanel.setLayout(new javax.swing.BoxLayout(observDataPanel, javax.swing.BoxLayout.Y_AXIS));

        positivosObs_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        positivosObs_RB.setSelected(true);
        positivosObs_RB.setText(bundle.getString("graficos4.positivos"));
        positivosObs_RB.setActionCommand("PositivosObs_RB");
        observDataPanel.add(positivosObs_RB);

        hospitalObs_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        hospitalObs_RB.setSelected(true);
        hospitalObs_RB.setText(bundle.getString("graficos4.hospital"));
        hospitalObs_RB.setActionCommand("HospitalObs_RB");
        observDataPanel.add(hospitalObs_RB);

        UCIObs_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        UCIObs_RB.setSelected(true);
        UCIObs_RB.setText(bundle.getString("graficos4.uci"));
        UCIObs_RB.setActionCommand("UCIObs_RB");
        observDataPanel.add(UCIObs_RB);

        jLabel1.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        jLabel1.setText(bundle.getString("graficos4.slider.periodoprevio"));
        observDataPanel.add(jLabel1);

        JSliderPre.setValue(0);
        JSliderPre.setAlignmentX(0.0F);
        JSliderPre.setMaximumSize(new java.awt.Dimension(180, 16));
        JSliderPre.setPreferredSize(new java.awt.Dimension(180, 16));
        JSliderPre.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSliderPreStateChanged(evt);
            }
        });
        observDataPanel.add(JSliderPre);

        jLabel2.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        jLabel2.setText(bundle.getString("graficos4.slider.periodoposterior"));
        observDataPanel.add(jLabel2);

        JSliderPos.setValue(0);
        JSliderPos.setAlignmentX(0.0F);
        JSliderPos.setMaximumSize(new java.awt.Dimension(180, 16));
        JSliderPos.setPreferredSize(new java.awt.Dimension(180, 16));
        JSliderPos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                JSliderPosStateChanged(evt);
            }
        });
        observDataPanel.add(JSliderPos);
        observDataPanel.add(filler1);

        botoneraGraficos.add(observDataPanel);

        getContentPane().add(botoneraGraficos, java.awt.BorderLayout.LINE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void JSliderPreStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSliderPreStateChanged
        // TODO add your handling code here:
        lagFrom = JSliderPre.getValue();
        if (dataset != null) {
            for (int i = 14; i < 17; i++) {
                dataset.getSeries(i).clear();
                for (int d = posFrom - lagFrom; d <= posUntil + lagUntil; d++) {
                    series[i].add(observedDays[d], observedData[i-14][d]);
                }
            }
            if (positivosObs_RB.isSelected()) {
               renderer.setSeriesPaint( 14, new Color(0,191,255));
                renderer.setSeriesShapesVisible(14, true);
                renderer.setSeriesLinesVisible(14, true);
                renderer.setSeriesVisibleInLegend(14, true);
            }
            if (hospitalObs_RB.isSelected()) {
               renderer.setSeriesPaint( 15, new Color(0,80,0));
                renderer.setSeriesShapesVisible(15, true);
                renderer.setSeriesLinesVisible(15, true);
                renderer.setSeriesVisibleInLegend(15, true);
            }
            if (UCIObs_RB.isSelected()) {
               renderer.setSeriesPaint( 16, new Color(75, 0, 130));
                renderer.setSeriesShapesVisible(16, true);
                renderer.setSeriesLinesVisible(16, true);
                renderer.setSeriesVisibleInLegend(16, true);
            }
        }
    }//GEN-LAST:event_JSliderPreStateChanged

    private void JSliderPosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSliderPosStateChanged
        // TODO add your handling code here:
            lagUntil = JSliderPos.getValue();
        if (dataset != null) {
            for (int i = 14; i < 17; i++) {
                dataset.getSeries(i).clear();
                for (int d = posFrom - lagFrom; d <= posUntil + lagUntil; d++) {
                    series[i].add(observedDays[d], observedData[i-14][d]);
                }
            }
            if (positivosObs_RB.isSelected()) {
               renderer.setSeriesPaint( 14, new Color(0,191,255));
                renderer.setSeriesShapesVisible(14, true);
                renderer.setSeriesLinesVisible(14, true);
                renderer.setSeriesVisibleInLegend(14, true);
            }
            if (hospitalObs_RB.isSelected()) {
               renderer.setSeriesPaint( 15, new Color(0,80,0));
                renderer.setSeriesShapesVisible(15, true);
                renderer.setSeriesLinesVisible(15, true);
                renderer.setSeriesVisibleInLegend(15, true);
            }
            if (UCIObs_RB.isSelected()) {
               renderer.setSeriesPaint( 16, new Color(75, 0, 130));
                renderer.setSeriesShapesVisible(16, true);
                renderer.setSeriesLinesVisible(16, true);
                renderer.setSeriesVisibleInLegend(16, true);
            }
        }
    }//GEN-LAST:event_JSliderPosStateChanged

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider JSliderPos;
    private javax.swing.JSlider JSliderPre;
    private javax.swing.JRadioButton UCIObs_RB;
    private javax.swing.JRadioButton UCI_Pre_RB;
    private javax.swing.JRadioButton UCI_Real_RB;
    private javax.swing.JPanel UciHosPosPred;
    private javax.swing.JPanel UciHosPosReal;
    private javax.swing.JPanel botoneraGraficos;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JRadioButton hospitalObs_RB;
    private javax.swing.JRadioButton hospital_Pre_RB;
    private javax.swing.JRadioButton hospital_Real_RB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel observDataPanel;
    private javax.swing.JRadioButton positivosObs_RB;
    private javax.swing.JRadioButton positivos_Pre_RB;
    private javax.swing.JRadioButton positivos_Real_RB;
    // End of variables declaration//GEN-END:variables

                                
                                
    @Override
    public void actionPerformed(ActionEvent ae) {
        
        JRadioButton jrb =(JRadioButton) ae.getSource();
        
        switch(jrb.getActionCommand()){
            case "Positivos_RB": 
                if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(0, false);
                    renderer.setSeriesShapesVisible(0, false);
                    renderer.setSeriesVisibleInLegend(0, false);
                    
                } else {
                    renderer.setSeriesLinesVisible(0, true);
                    renderer.setSeriesShapesVisible(0, true);
                    renderer.setSeriesVisibleInLegend(0, true);
                }
                break;
            case "Hospital_RB": 
                   if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(1, false);
                    renderer.setSeriesLinesVisible(2, false);
                    renderer.setSeriesLinesVisible(3, false);
                    renderer.setSeriesShapesVisible(1, false);
                    //renderer.setSeriesShapesVisible(2, false);
                    //renderer.setSeriesShapesVisible(3, false);
                    renderer.setSeriesVisibleInLegend(1, false);
                    renderer.setSeriesVisibleInLegend(2, false);
                    renderer.setSeriesVisibleInLegend(3, false);
                } else {
                    renderer.setSeriesLinesVisible(1, true);
                    renderer.setSeriesLinesVisible(2, true);
                    renderer.setSeriesLinesVisible(3, true);
                    renderer.setSeriesShapesVisible(1, true);
                    //renderer.setSeriesShapesVisible(2, true);
                    //renderer.setSeriesShapesVisible(3, true);
                    renderer.setSeriesVisibleInLegend(1, true);
                    renderer.setSeriesVisibleInLegend(2, true);
                    //renderer.setSeriesVisibleInLegend(3, true);
                    renderer.setSeriesVisibleInLegend(3, false);
                }
                
                break;
            case "UCI_RB": 
                 if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(4, false);
                    renderer.setSeriesLinesVisible(5, false);
                    renderer.setSeriesLinesVisible(6, false);
                    renderer.setSeriesShapesVisible(4, false);
                    //renderer.setSeriesShapesVisible(5, false);
                    //renderer.setSeriesShapesVisible(6, false);
                    renderer.setSeriesVisibleInLegend(4, false);
                    renderer.setSeriesVisibleInLegend(5, false);
                    renderer.setSeriesVisibleInLegend(6, false);
                 }
                    else {
                    renderer.setSeriesLinesVisible(4, true);
                    renderer.setSeriesLinesVisible(5, true);
                    renderer.setSeriesLinesVisible(6, true);
                    renderer.setSeriesShapesVisible(4, true);
                    //renderer.setSeriesShapesVisible(5, true);
                    //renderer.setSeriesShapesVisible(6, true);
                    renderer.setSeriesVisibleInLegend(4,true);
                    renderer.setSeriesVisibleInLegend(5,true);
                    //renderer.setSeriesVisibleInLegend(6,true);
                    renderer.setSeriesVisibleInLegend(6,false);
                    
                            }

                break;
            
            
            
            
            
            case "Positivos_Real_RB": 
                if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(7, false);
                    renderer.setSeriesShapesVisible(7, false);
                    renderer.setSeriesVisibleInLegend(7, false);
                    
                } else {
                    renderer.setSeriesLinesVisible(7, true);
                    renderer.setSeriesShapesVisible(7, true);
                    renderer.setSeriesVisibleInLegend(7, true);
                }
                break;
            case "Hospital_Real_RB": 
                   if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(8, false);
                    renderer.setSeriesLinesVisible(9, false);
                    renderer.setSeriesLinesVisible(10, false);
                    renderer.setSeriesShapesVisible(8, false);
                    //renderer.setSeriesShapesVisible(2, false);
                    //renderer.setSeriesShapesVisible(3, false);
                    renderer.setSeriesVisibleInLegend(8, false);
                    renderer.setSeriesVisibleInLegend(9, false);
                    renderer.setSeriesVisibleInLegend(10, false);
                } else {
                    renderer.setSeriesLinesVisible(8, true);
                    renderer.setSeriesLinesVisible(9, true);
                    renderer.setSeriesLinesVisible(10, true);
                    renderer.setSeriesShapesVisible(8, true);
                    //renderer.setSeriesShapesVisible(2, true);
                    //renderer.setSeriesShapesVisible(3, true);
                    renderer.setSeriesVisibleInLegend(8, true);
                    renderer.setSeriesVisibleInLegend(9, true);
                    //renderer.setSeriesVisibleInLegend(10, true);
                    renderer.setSeriesVisibleInLegend(10, false);
                    
                }
                
                break;
            case "UCI_Real_RB": 
                 if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(11, false);
                    renderer.setSeriesLinesVisible(12, false);
                    renderer.setSeriesLinesVisible(13, false);
                    renderer.setSeriesShapesVisible(11, false);
                    //renderer.setSeriesShapesVisible(5, false);
                    //renderer.setSeriesShapesVisible(6, false);
                    renderer.setSeriesVisibleInLegend(11, false);
                    renderer.setSeriesVisibleInLegend(12, false);
                    renderer.setSeriesVisibleInLegend(13, false);
                 }
                    else {
                    renderer.setSeriesLinesVisible(11, true);
                    renderer.setSeriesLinesVisible(12, true);
                    renderer.setSeriesLinesVisible(13, true);
                    renderer.setSeriesShapesVisible(11, true);
                    //renderer.setSeriesShapesVisible(5, true);
                    //renderer.setSeriesShapesVisible(6, true);
                    renderer.setSeriesVisibleInLegend(11,true);
                    renderer.setSeriesVisibleInLegend(12,true);
                    //renderer.setSeriesVisibleInLegend(13,true);
                    renderer.setSeriesVisibleInLegend(13,false);
                            }

                break;
                
                case "PositivosObs_RB": 
                 if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(14, false);
                    renderer.setSeriesShapesVisible(14, false);
                    renderer.setSeriesVisibleInLegend(14, false);
                } else {
                    renderer.setSeriesLinesVisible(14, true);
                    renderer.setSeriesShapesVisible(14, true);
                    renderer.setSeriesVisibleInLegend(14, true);
                }break;
            case "HospitalObs_RB": 
                if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(15, false);
                    renderer.setSeriesVisibleInLegend(15, false);
                    renderer.setSeriesShapesVisible(15, false);
                } else {
                    renderer.setSeriesLinesVisible(15, true);
                    renderer.setSeriesShapesVisible(15, true);
                    renderer.setSeriesVisibleInLegend(15, true);
                }break;
            case "UCIObs_RB": 
                if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(16, false);
                    renderer.setSeriesShapesVisible(16, false);
                    renderer.setSeriesVisibleInLegend(16, false);
                } else {
                    renderer.setSeriesLinesVisible(16, true);
                    renderer.setSeriesShapesVisible(16, true);
                    renderer.setSeriesVisibleInLegend(16, true);
                }break;
            
        
        }
    }

    //@Override
   // public void actionPerformed(ActionEvent ae) {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    /*    System.out.println("Pulsado " + ae.getActionCommand());
        actualizaPintado(pintado);
        for (int p = 0; p < percents.length + 3; p++) {
                for (int ge = 0; ge < gruEdadS.length + 1; ge++) {
                        for (int s = 0; s < 2; s++) {
                            if (pintado[s][ge][p] != pintadoPrevio[s][ge][p]) {
                                //Se ha activado o desactivado
                                if (pintado[s][ge][p]) {
                                //Pasa de inactivo a activo
                                    setSerie(s,ge,p);
                                }else{
                                //Pasa de activo a inactivo
                                //Lo quitamos de la lista timeseriescollection
                                dataset.removeSeries(series[s][ge][p]);
                                }
                                pintadoPrevio[s][ge][p]=pintado[s][ge][p];
                            }
                        }
                }
            
        }*/
    //}

   

  
}
