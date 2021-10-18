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

/**
 *
 * @author pmateo
 */
public class GraficosStats3 extends javax.swing.JFrame implements ActionListener{
    private final double stats[][][][];
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
    private Estadisticas e;
    private ResourceBundle bundle;
    /**
     * Creates new form GraficosStats
     */
    
    public GraficosStats3(String titulo, Estadisticas ee,ResourceBundle bndl){
    e=ee;
    stats=e.statsUH;
    numPositivosTotales=e.nuevosPositivosTotales;
    percents=e.percents;
    from=e.from;
    until=e.until;
    numGruEdad=e.numGrupos;
    realDataName=e.realDataName;
    dataDirectory=e.dataDirectory;
    bundle=bndl;
    
     initComponents();
       //Creamos las series de datos, todas, y después ya apañaremos y pintaremos
       //las que interesen
       dataset= new TimeSeriesCollection();
       series = new TimeSeries[10];
       names = new String[10];
              
       //Añadimos escuchadores para los radio buttons
       
       UCI_RB.addActionListener(this);       
       hospital_RB.addActionListener(this);       
       positivos_RB.addActionListener(this);
       UCIObs_RB.addActionListener(this);
       hospitalObs_RB.addActionListener(this);
       positivosObs_RB.addActionListener(this);
       
   
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
       names[0]=bundle.getString("graficos2.grafico.leyenda.positivosprediccion");//"Positivos predicción";
       names[1]=bundle.getString("graficos2.grafico.leyenda.hospitalizadosprediccion");//"Hospitalizados predicción";       
       names[2]=bundle.getString("graficos2.grafico.leyenda.hospitalizadosp5th");//"Hospitalizados 5%";
       names[3]=bundle.getString("graficos2.grafico.leyenda.hospitalizadosp95th");//"Hospitalizados 95%";
       names[4]=bundle.getString("graficos2.grafico.leyenda.uciprediccion");//"UCI predicción";
       names[5]=bundle.getString("graficos2.grafico.leyenda.ucip5th");//"UCI 5%";
       names[6]=bundle.getString("graficos2.grafico.leyenda.ucip95th");//"UCI 95%";
       names[7]=bundle.getString("graficos2.grafico.leyenda.positivosobservados");//"Positivos observados";
       names[8]=bundle.getString("graficos2.grafico.leyenda.hospitalizadosobservados");//"Hospitalizados observados";
       names[9]=bundle.getString("graficos2.grafico.leyenda.uciobservados");//"UCI observados";
       lee_fichero_evolucion_real();      
       
       for(int i=0;i<10;i++) setSerie(i);
      
       
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
        /*
    public GraficosStats3(double Sta[][][][],int npt[],int grEd,double per[],LocalDate fro,LocalDate unt,String dadi,String realD) {        
       stats=Sta;
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
       series = new TimeSeries[10];
       names = new String[10];
              
       //Añadimos escuchadores para los radio buttons
       
       UCI_RB.addActionListener(this);       
       hospital_RB.addActionListener(this);       
       positivos_RB.addActionListener(this);
       UCIObs_RB.addActionListener(this);
       hospitalObs_RB.addActionListener(this);
       positivosObs_RB.addActionListener(this);
       
   
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
       names[0]="Positivos predicción";
       names[1]="Hospitalizados predicción";       
       names[2]="Hospitalizados 5%";
       names[3]="Hospitalizados 95%";
       names[4]="UCI predicción";
       names[5]="UCI 5%";
       names[6]="UCI 95%";
       names[7]="Positivos observados";
       names[8]="Hospitalizados observados";
       names[9]="UCI observados";
       lee_fichero_evolucion_real();      
       
       for(int i=0;i<10;i++) setSerie(i);
       
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
    }*/

void setSerie(int sitio){
        series[sitio] = new TimeSeries(names[sitio]);
        
            switch(sitio){
                case 0: //Positivos predicción
                    for (int d = 1; d < numDias; d++) {series[sitio].add(dias[d],numPositivosTotales[numGruEdad][d]);}
                   if(posFrom!=-1) series[sitio].add(dias[0],observedData[0][posFrom]);
                    renderer.setSeriesPaint( sitio, new Color(74,35,90));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Ellipse2D.Double(-2d, -2d, 4d, 4d));
                    break;
                case 1: //Hospital predicción/total/media
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats[1][0][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(21,67,96));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Ellipse2D.Double(-2d, -2d, 4d, 4d));
                break;
                case 2: //Hospital prediccion /total/5% 
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats[1][2][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(21,67,96));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                    break;
                case 3: //Hospital prediccion /total/95% 
                    for (int d = 0; d < numDias; d++) {
                        series[sitio].add(dias[d], stats[1][6][numGruEdad][d]);
                    }
                    renderer.setSeriesPaint( sitio, new Color(21,67,96));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                    break;
                case 4: //UCI prediccion/total/media
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats[0][0][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(20,90,50));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Ellipse2D.Double(-2d, -2d, 4d, 4d));
                    break;
                case 5: //UCI prediccion/total/5%
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats[0][2][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(20,90,50));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                break;
                case 6: //UCI prediccion/total/95%
                    for (int d = 0; d < numDias; d++) {series[sitio].add(dias[d], stats[0][6][numGruEdad][d]);}
                    renderer.setSeriesPaint( sitio, new Color(20,90,50));
                    renderer.setSeriesShapesVisible(sitio, false);
                    renderer.setSeriesStroke(sitio,   new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,1.0f, new float[] {6.0f, 6.0f}, 0.0f));
                break;
                case 7: //Positivos observados
                    
                    for (int d = posFrom-lagFrom; d <= posUntil+lagUntil; d++) {series[sitio].add(observedDays[d], observedData[0][d]);}
                    renderer.setSeriesPaint( sitio, new Color(187,143,206));
                    renderer.setSeriesShapesVisible(sitio, true);
                    renderer.setSeriesShape(sitio, new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0));
                    
                    break;
                case 8: //Hospital obesrvados
                   
                    for (int d = posFrom-lagFrom; d <= posUntil+lagUntil; d++) {series[sitio].add(observedDays[d], observedData[1][d]);}
                    renderer.setSeriesPaint( sitio, new Color(127,179,213));
                    renderer.setSeriesShape(sitio, new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0));
                    renderer.setSeriesShapesVisible(sitio, true);
                    break;
                case 9: //UCI observados
                    
                    for (int d = posFrom-lagFrom; d <= posUntil+lagUntil; d++) {series[sitio].add(observedDays[d], observedData[2][d]);}
                    renderer.setSeriesPaint( sitio, new Color(39,174,96));
                    renderer.setSeriesShape(sitio, new Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0));
                    renderer.setSeriesShapesVisible(sitio, true);
                    break;
            }
        dataset.addSeries(series[sitio]);         
        renderer.setSeriesLinesVisible(sitio, true);
        //renderer.setSeriesShapesVisible(sitio, true);
        if(sitio!=3 && sitio!=6)
        renderer.setSeriesVisibleInLegend(sitio, true);
        else
            renderer.setSeriesVisibleInLegend(sitio, false);
        
}
    
void creaGrafica(){

        chart = ChartFactory.createTimeSeriesChart(
            bundle.getString("graficos2.grafico.title"),                
            bundle.getString("graficos2.grafico.xlabel"),             // x-axis label
            bundle.getString("graficos2.grafico.ylabel"),   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        plot = (XYPlot) chart.getPlot();
//        plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundPaint(new Color(242,244,244));
//        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinePaint(new Color(207,207,210));
//        plot.setRangeGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(new Color(207,207,210));
//        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
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
        UciHosPosSelecc = new javax.swing.JPanel();
        positivos_RB = new javax.swing.JRadioButton();
        hospital_RB = new javax.swing.JRadioButton();
        UCI_RB = new javax.swing.JRadioButton();
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
        setTitle(bundle.getString("graficos2.ventana.titulo"));
        setMinimumSize(new java.awt.Dimension(600, 450));

        botoneraGraficos.setLayout(new javax.swing.BoxLayout(botoneraGraficos, javax.swing.BoxLayout.Y_AXIS));

        UciHosPosSelecc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos2.titPanel.prediccion")
            , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
    UciHosPosSelecc.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    UciHosPosSelecc.setMaximumSize(new java.awt.Dimension(165, 100));
    UciHosPosSelecc.setMinimumSize(new java.awt.Dimension(100, 70));
    UciHosPosSelecc.setPreferredSize(new java.awt.Dimension(165, 100));
    UciHosPosSelecc.setLayout(new javax.swing.BoxLayout(UciHosPosSelecc, javax.swing.BoxLayout.Y_AXIS));

    positivos_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    positivos_RB.setSelected(true);
    positivos_RB.setText(bundle.getString("graficos2.grafico.rbutton.positivos"));
    positivos_RB.setActionCommand("Positivos_RB");
    UciHosPosSelecc.add(positivos_RB);

    hospital_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    hospital_RB.setSelected(true);
    hospital_RB.setText(bundle.getString("graficos2.grafico.rbutton.hospital"));
    hospital_RB.setActionCommand("Hospital_RB");
    hospital_RB.setName("1"); // NOI18N
    UciHosPosSelecc.add(hospital_RB);

    UCI_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    UCI_RB.setSelected(true);
    UCI_RB.setText(bundle.getString("graficos2.grafico.rbutton.uci"));
    UCI_RB.setActionCommand("UCI_RB");
    UCI_RB.setName("0"); // NOI18N
    UciHosPosSelecc.add(UCI_RB);

    botoneraGraficos.add(UciHosPosSelecc);

    observDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos2.titPanel.observados"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
    observDataPanel.setAlignmentX(0.0F);
    observDataPanel.setMaximumSize(new java.awt.Dimension(165, 170));
    observDataPanel.setMinimumSize(new java.awt.Dimension(100, 70));
    observDataPanel.setPreferredSize(new java.awt.Dimension(165, 170));
    observDataPanel.setLayout(new javax.swing.BoxLayout(observDataPanel, javax.swing.BoxLayout.Y_AXIS));

    positivosObs_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    positivosObs_RB.setSelected(true);
    positivosObs_RB.setText(bundle.getString("graficos2.grafico.rbutton.positivos"));
    positivosObs_RB.setActionCommand("PositivosObs_RB");
    observDataPanel.add(positivosObs_RB);

    hospitalObs_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    hospitalObs_RB.setSelected(true);
    hospitalObs_RB.setText(bundle.getString("graficos2.grafico.rbutton.hospital"));
    hospitalObs_RB.setActionCommand("HospitalObs_RB");
    observDataPanel.add(hospitalObs_RB);

    UCIObs_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    UCIObs_RB.setSelected(true);
    UCIObs_RB.setText(bundle.getString("graficos2.grafico.rbutton.uci"));
    UCIObs_RB.setActionCommand("UCIObs_RB");
    observDataPanel.add(UCIObs_RB);

    jLabel1.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    jLabel1.setText(bundle.getString("graficos2.slider.periodoprevio"));
    observDataPanel.add(jLabel1);

    JSliderPre.setValue(0);
    JSliderPre.setAlignmentX(0.0F);
    JSliderPre.setMaximumSize(new java.awt.Dimension(160, 16));
    JSliderPre.setPreferredSize(new java.awt.Dimension(160, 16));
    JSliderPre.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(javax.swing.event.ChangeEvent evt) {
            JSliderPreStateChanged(evt);
        }
    });
    observDataPanel.add(JSliderPre);

    jLabel2.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
    jLabel2.setText(bundle.getString("graficos2.slider.periodoposterior"));
    observDataPanel.add(jLabel2);

    JSliderPos.setValue(0);
    JSliderPos.setAlignmentX(0.0F);
    JSliderPos.setMaximumSize(new java.awt.Dimension(160, 16));
    JSliderPos.setPreferredSize(new java.awt.Dimension(160, 16));
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
            for (int i = 7; i < 10; i++) {
                dataset.getSeries(i).clear();
                for (int d = posFrom - lagFrom; d <= posUntil + lagUntil; d++) {
                    series[i].add(observedDays[d], observedData[i-7][d]);
                }
            }
            if (positivosObs_RB.isSelected()) {
               renderer.setSeriesPaint( 7, new Color(187,143,206));
                renderer.setSeriesShapesVisible(7, true);
                renderer.setSeriesLinesVisible(7, true);
                renderer.setSeriesVisibleInLegend(7, true);
            }
            if (hospitalObs_RB.isSelected()) {
               renderer.setSeriesPaint( 8, new Color(84,153,199));
                renderer.setSeriesShapesVisible(8, true);
                renderer.setSeriesLinesVisible(8, true);
                renderer.setSeriesVisibleInLegend(8, true);
            }
            if (UCIObs_RB.isSelected()) {
               renderer.setSeriesPaint( 9, new Color(39,174,96));
                renderer.setSeriesShapesVisible(9, true);
                renderer.setSeriesLinesVisible(9, true);
                renderer.setSeriesVisibleInLegend(9, true);
            }
        }
    }//GEN-LAST:event_JSliderPreStateChanged

    private void JSliderPosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_JSliderPosStateChanged
        // TODO add your handling code here:
            lagUntil = JSliderPos.getValue();
        if (dataset != null) {
            for (int i = 7; i < 10; i++) {
                dataset.getSeries(i).clear();
                for (int d = posFrom - lagFrom; d <= posUntil + lagUntil; d++) {
                    series[i].add(observedDays[d], observedData[i-7][d]);
                }
            }
            if (positivosObs_RB.isSelected()) {
               renderer.setSeriesPaint( 7, new Color(187,143,206));
                renderer.setSeriesShapesVisible(7, true);
                renderer.setSeriesLinesVisible(7, true);
                renderer.setSeriesVisibleInLegend(7, true);
            }
            if (hospitalObs_RB.isSelected()) {
               renderer.setSeriesPaint( 8, new Color(127,179,213));
                renderer.setSeriesShapesVisible(8, true);
                renderer.setSeriesLinesVisible(8, true);
                renderer.setSeriesVisibleInLegend(8, true);
            }
            if (UCIObs_RB.isSelected()) {
               renderer.setSeriesPaint( 9, new Color(39,174,96));
                renderer.setSeriesShapesVisible(9, true);
                renderer.setSeriesLinesVisible(9, true);
                renderer.setSeriesVisibleInLegend(9, true);
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
    private javax.swing.JRadioButton UCI_RB;
    private javax.swing.JPanel UciHosPosSelecc;
    private javax.swing.JPanel botoneraGraficos;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JRadioButton hospitalObs_RB;
    private javax.swing.JRadioButton hospital_RB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel observDataPanel;
    private javax.swing.JRadioButton positivosObs_RB;
    private javax.swing.JRadioButton positivos_RB;
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
            case "PositivosObs_RB": 
                 if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(7, false);
                    renderer.setSeriesShapesVisible(7, false);
                    renderer.setSeriesVisibleInLegend(7, false);
                } else {
                    renderer.setSeriesLinesVisible(7, true);
                    renderer.setSeriesShapesVisible(7, true);
                    renderer.setSeriesVisibleInLegend(7, true);
                }break;
            case "HospitalObs_RB": 
                if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(8, false);
                    renderer.setSeriesVisibleInLegend(8, false);
                    renderer.setSeriesShapesVisible(8, false);
                } else {
                    renderer.setSeriesLinesVisible(8, true);
                    renderer.setSeriesShapesVisible(8, true);
                    renderer.setSeriesVisibleInLegend(8, true);
                }break;
            case "UCIObs_RB": 
                if (!jrb.isSelected()) {
                    renderer.setSeriesLinesVisible(9, false);
                    renderer.setSeriesShapesVisible(9, false);
                    renderer.setSeriesVisibleInLegend(9, false);
                } else {
                    renderer.setSeriesLinesVisible(9, true);
                    renderer.setSeriesShapesVisible(9, true);
                    renderer.setSeriesVisibleInLegend(9, true);
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
