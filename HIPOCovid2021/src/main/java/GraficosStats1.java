/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
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
 * Gráfico por grupos G2 para Forecast y validation
 * 
 * @author pmateo
 */
public class GraficosStats1 extends javax.swing.JFrame implements ActionListener{
    private double stats[][][][];
    private final int numPositivosTotales[][];
    private double percents[];
    private LocalDate from;
    private LocalDate until;
    private javax.swing.JRadioButton grEdad_RB[];
    private javax.swing.JRadioButton percen_RB[];
    private TimeSeriesCollection dataset= null;
    private TimeSeries series[][][];
    private String names[][][];
    private boolean pintado[][][];
    private boolean pintadoPrevio[][][];
    private JFreeChart chart;
    private XYPlot plot =null;
    private Day dias[]=null;
    private int numDias=-1;
    private int numGruEdad;
    private XYLineAndShapeRenderer renderer;
    private Estadisticas e;
    private ResourceBundle bundle;
    private boolean isForecast;
    private String tituloGrafico;
    /**
     * Creates new form GraficosStats
     */
    
    public GraficosStats1(String titulo, Estadisticas ee,ResourceBundle bndl,boolean isf ) {     
        e=ee;
        stats=e.statsUH;
        numPositivosTotales=e.nuevosPositivosTotales;
        percents=e.percents;
        numGruEdad=e.numGrupos;
        from=e.from;
        until=e.until;
        bundle=bndl;
        isForecast=isf;
        tituloGrafico=titulo;
         initComponents();
       
       grEdad_RB = new javax.swing.JRadioButton[numGruEdad+1];
       for(int i=0;i<numGruEdad;i++){
         grEdad_RB[i] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.grupo")+" "+(i+1));        
         grupoEdadSelecc.add(grEdad_RB[i]);
         grEdad_RB[i].setFont(new java.awt.Font("Courier", 0, 12));
         grEdad_RB[i].setSelected(false);       
         grEdad_RB[i].setActionCommand("GE_"+i);
         grEdad_RB[i].addActionListener(this);
       }
       //Totales
         grEdad_RB[numGruEdad] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.totales"));
         grupoEdadSelecc.add(grEdad_RB[numGruEdad]);
         grEdad_RB[numGruEdad].setFont(new java.awt.Font("Courier", 0, 12));
         grEdad_RB[numGruEdad].setSelected(true);
         grEdad_RB[numGruEdad].setActionCommand("GE_"+numGruEdad);
         grEdad_RB[numGruEdad].addActionListener(this);
         
         percen_RB = new javax.swing.JRadioButton[percents.length+3];
       //Media
         percen_RB[0] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.media"));        
         PercentSelec.add(percen_RB[0]);
         percen_RB[0].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[0].setSelected(true);
         percen_RB[0].setActionCommand("P_0");
         percen_RB[0].addActionListener(this);
       //sd
         percen_RB[1] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.desviacionTipica"));        
         PercentSelec.add(percen_RB[1]);
         percen_RB[1].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[1].setSelected(false);
         percen_RB[1].setActionCommand("P_1");
         percen_RB[1].addActionListener(this);
         percen_RB[1].setVisible(false);
       for(int i=0;i<percents.length;i++){           
         percen_RB[i+2] = new javax.swing.JRadioButton(String.format(bundle.getString("graficos1.grafico.rbutton.percentil"),percents[i]) );        
         PercentSelec.add(percen_RB[i+2]);
         percen_RB[i+2].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[i+2].setSelected(false);
         percen_RB[i+2].setActionCommand("P_"+(i+2));
         percen_RB[i+2].addActionListener(this);
       }
       
        //Historico o positivos
        if(this.isForecast)
         percen_RB[percents.length+2] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.positive"));        
        else
         percen_RB[percents.length+2] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.historico"));           
         PosObs.add(percen_RB[percents.length+2]);
         percen_RB[percents.length+2].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[percents.length+2].setSelected(false);
         percen_RB[percents.length+2].setActionCommand("P_"+(percents.length+2));
         percen_RB[percents.length+2].addActionListener(this);
         //percen_RB[percents.length+2].setVisible(true);
       //Creamos las series de datos, todas, y después ya apañaremos y pintaremos
       //las que interesen
       dataset= new TimeSeriesCollection();
       series = new TimeSeries[2][numGruEdad+1][percents.length+3];
       names = new String[2][numGruEdad+1][percents.length+3];
       //guardamos las series que tendran  que estar pintadas en este momento
       pintado = new boolean[2][numGruEdad+1][percents.length+3];
       pintadoPrevio = new boolean[2][numGruEdad+1][percents.length+3];
       actualizaPintado(pintado);
       actualizaPintado(pintadoPrevio);
              
       //Añadimos escuchadores para los radio buttons
       UCI_RB.setActionCommand(bundle.getString("graficos1.grafico.rbutton.uci"));
       UCI_RB.addActionListener(this);
       hospital_RB.setActionCommand(bundle.getString("graficos1.grafico.rbutton.hospital"));
       hospital_RB.addActionListener(this);
       //Creamos los días a poner en eje X
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
       for (int p = 0; p < percents.length + 3; p++) {
                for (int ge = 0; ge < numGruEdad + 1; ge++) {
                    for(int t=0;t<2;t++){
                        if(p!=percents.length+2){
                        if(t==0)names[t][ge][p]=bundle.getString("graficos1.grafico.rbutton.uci")+"_";
                        else names[t][ge][p]=bundle.getString("graficos1.grafico.rbutton.hospital")+"_";  
                        }
                        else
                            names[t][ge][p]="";
                        if(ge==numGruEdad)names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.totales")+"_";
                        else
                            names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.grupo")+(ge+1)+"_";
                        
                        if(p==0)names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.media");
                        else if(p==1)names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.desviacionTipica");
                        else if(p==percents.length+2){
                            if(isForecast)   names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.positive");
                            else names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.historico");
                        }
                        else names[t][ge][p]+=(int)percents[p-2]+bundle.getString("graficos1.grafico.rbutton.th");
                        setSerie(t,ge,p);
                        if(pintado[t][ge][p]){
                        //    dataset.getSeries(posicionSerie(t,p,ge));
                            renderer.setSeriesLinesVisible(posicionSerie(t,p,ge), true);
                            renderer.setSeriesVisibleInLegend(posicionSerie(t,p,ge), true);
                            renderer.setSeriesShapesVisible(posicionSerie(t,p,ge), true);
                            
                        }else{
                         //   dataset.getSeries(posicionSerie(t,p,ge));
                            renderer.setSeriesLinesVisible(posicionSerie(t,p,ge), false);
                            renderer.setSeriesVisibleInLegend(posicionSerie(t,p,ge), false);
                             renderer.setSeriesShapesVisible(posicionSerie(t,p,ge), false);
                         
                        }
                        
                        
                    }
                }
        } 
        
    }   
        /*
    public GraficosStats1(double Sta[][][][],int ngrEd,double per[],LocalDate fro,LocalDate unt) {        
       stats=Sta;
       percents=per;
       from=fro;
       until=unt;
       numGruEdad=ngrEd;       
       initComponents();
       
       grEdad_RB = new javax.swing.JRadioButton[numGruEdad+1];
       for(int i=0;i<numGruEdad;i++){
         grEdad_RB[i] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.grupo")+" "+i);        
         grupoEdadSelecc.add(grEdad_RB[i]);
         grEdad_RB[i].setFont(new java.awt.Font("Courier", 0, 12));
         grEdad_RB[i].setSelected(false);       
         grEdad_RB[i].setActionCommand("GE_"+i);
         grEdad_RB[i].addActionListener(this);
       }
       //Totales
         grEdad_RB[numGruEdad] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.totales"));                
         grupoEdadSelecc.add(grEdad_RB[numGruEdad]);
         grEdad_RB[numGruEdad].setFont(new java.awt.Font("Courier", 0, 12));
         grEdad_RB[numGruEdad].setSelected(true);
         grEdad_RB[numGruEdad].setActionCommand("GE_"+numGruEdad);
         grEdad_RB[numGruEdad].addActionListener(this);
         
         percen_RB = new javax.swing.JRadioButton[percents.length+3];
       //Media
         percen_RB[0] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.media"));        
         PercentSelec.add(percen_RB[0]);
         percen_RB[0].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[0].setSelected(true);
         percen_RB[0].setActionCommand("P_0");
         percen_RB[0].addActionListener(this);
       //sd
         percen_RB[1] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.desviacionTipica"));        
         PercentSelec.add(percen_RB[1]);
         percen_RB[1].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[1].setSelected(false);
         percen_RB[1].setActionCommand("P_1");
         percen_RB[1].addActionListener(this);
         
       for(int i=0;i<percents.length;i++){           
         percen_RB[i+2] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.percentil")+" "+percents[i]+"th");        
         PercentSelec.add(percen_RB[i+2]);
         percen_RB[i+2].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[i+2].setSelected(false);
         percen_RB[i+2].setActionCommand("P_"+(i+2));
         percen_RB[i+2].addActionListener(this);
       }
       
        //Historico
         percen_RB[percents.length+2] = new javax.swing.JRadioButton(bundle.getString("graficos1.grafico.rbutton.historico"));        
         PercentSelec.add(percen_RB[percents.length+2]);
         percen_RB[percents.length+2].setFont(new java.awt.Font("Courier", 0, 12));
         percen_RB[percents.length+2].setSelected(false);
         percen_RB[percents.length+2].setActionCommand("P_"+(percents.length+2));
         percen_RB[percents.length+2].addActionListener(this);
       //Creamos las series de datos, todas, y después ya apañaremos y pintaremos
       //las que interesen
       dataset= new TimeSeriesCollection();
       series = new TimeSeries[2][numGruEdad+1][percents.length+3];
       names = new String[2][numGruEdad+1][percents.length+3];
       //guardamos las series que tendran  que estar pintadas en este momento
       pintado = new boolean[2][numGruEdad+1][percents.length+3];
       pintadoPrevio = new boolean[2][numGruEdad+1][percents.length+3];
       actualizaPintado(pintado);
       actualizaPintado(pintadoPrevio);
              
       //Añadimos escuchadores para los radio buttons
       UCI_RB.setActionCommand("UCI");
       UCI_RB.addActionListener(this);
       hospital_RB.setActionCommand("hospital");
       hospital_RB.addActionListener(this);
       //Creamos los días a poner en eje X
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
       for (int p = 0; p < percents.length + 3; p++) {
                for (int ge = 0; ge < numGruEdad + 1; ge++) {
                    for(int t=0;t<2;t++){
                        if(t==0)names[t][ge][p]=bundle.getString("graficos1.grafico.rbutton.uci")+"_";else names[t][ge][p]=bundle.getString("graficos1.grafico.rbutton.uci")+"_";    
                        if(ge==numGruEdad)names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.totales")+"_";
                        else
                            names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.grupo")+ge+"_";
                        if(p==0)names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.media");
                        else if(p==1)names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.desviacionTipica");
                        else if(p==percents.length+2)names[t][ge][p]+=bundle.getString("graficos1.grafico.rbutton.historico")+"_";
                        else names[t][ge][p]+=percents[p-2]+bundle.getString("graficos1.grafico.rbutton.th");
                        setSerie(t,ge,p);
                        if(pintado[t][ge][p]){
                        //    dataset.getSeries(posicionSerie(t,p,ge));
                            renderer.setSeriesLinesVisible(posicionSerie(t,p,ge), true);
                            renderer.setSeriesVisibleInLegend(posicionSerie(t,p,ge), true);
                            renderer.setSeriesShapesVisible(posicionSerie(t,p,ge), true);
                            System.out.println("Posicion serie ="+posicionSerie(t,p,ge)+" SI");
                        }else{
                         //   dataset.getSeries(posicionSerie(t,p,ge));
                            renderer.setSeriesLinesVisible(posicionSerie(t,p,ge), false);
                            renderer.setSeriesVisibleInLegend(posicionSerie(t,p,ge), false);
                             renderer.setSeriesShapesVisible(posicionSerie(t,p,ge), false);
                         System.out.println("Posicion serie ="+posicionSerie(t,p,ge)+" NO");
                        }
                        
                        
                    }
                }
        } 
       
    }
*/
    int posicionSerie(int tipo,int p, int ge ){
        return((p*(numGruEdad+1)*2)+(ge*2)+tipo);
    }
    
    
void setSerie(int sitio,int grupEd, int perc){
    
    if (series[sitio][grupEd][perc] == null) {
       
            
        series[sitio][grupEd][perc] = new TimeSeries(names[sitio][grupEd][perc]);
        if (perc < percents.length + 2) {
            for (int d = 0; d < numDias; d++) {
                series[sitio][grupEd][perc].add(dias[d], stats[sitio][perc][grupEd][d]);
            }
        } else {//perc=percents.length+2
            for (int d = 1; d < numDias; d++) {
                series[sitio][grupEd][perc].add(dias[d], numPositivosTotales[grupEd][d]);
            }
        }
        dataset.addSeries(series[sitio][grupEd][perc]);
    }
}
    
void creaGrafica(){

        chart = ChartFactory.createTimeSeriesChart(
            tituloGrafico,  // title
            bundle.getString("graficos1.grafico.xlabel"),             // x-axis label
            bundle.getString("graficos1.grafico.ylabel"),   // y-axis label
            dataset,            // data
            true,               // create legend?
            false,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);

        plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);    
        renderer = (XYLineAndShapeRenderer) plot.getRenderer();//new XYLineAndShapeRenderer();
        //plot.setRenderer(renderer);
    //renderer.setSeriesLinesVisible(0, false);
    //renderer.setSeriesShapesVisible(1, false);
    //Shape shape = new Rectangle2D.Double(-1, -1, 2, 2);
    //renderer.setSeriesShape(0, shape);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        TickUnits units = new TickUnits();
         units.add(new DateTickUnit(DateTickUnitType.DAY, 1));
         units.add(new DateTickUnit(DateTickUnitType.DAY, 2));
         units.add(new DateTickUnit(DateTickUnitType.DAY, 3));
         units.add(new DateTickUnit(DateTickUnitType.DAY, 4));
         units.add(new DateTickUnit(DateTickUnitType.DAY, 5));  
         units.add(new DateTickUnit(DateTickUnitType.DAY, 10));  
         axis.setStandardTickUnits(units);
    //    axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
        //axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
        axis.setVerticalTickLabels(true);
        LegendTitle legend = chart.getLegend();        
        legend.setPosition(RectangleEdge.RIGHT);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        this.add(chartPanel,BorderLayout.CENTER);
        

}    
    

    void actualizaPintado(boolean pin[][][]){
    
        for (int p = 0; p < percents.length + 3; p++) 
             for (int ge = 0; ge < numGruEdad + 1; ge++) 
                 for(int s=0;s<2;s++)
                         pin[s][ge][p]=false;
        
        for (int p = 0; p < percents.length + 3; p++) {
            if (percen_RB[p].isSelected()) {
                for (int ge = 0; ge < numGruEdad + 1; ge++) {
                    if (grEdad_RB[ge].isSelected()) {
                        if(p<percents.length + 2){
                        pin[0][ge][p] = UCI_RB.isSelected();
                        pin[1][ge][p] = hospital_RB.isSelected();
                        }
                        else{
                        pin[0][ge][p] = true;//pin[1][ge][p]  = true;
                        }
                    }
                }
            }
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
        UciHosSelecc = new javax.swing.JPanel();
        hospital_RB = new javax.swing.JRadioButton();
        UCI_RB = new javax.swing.JRadioButton();
        grupoEdadSelecc = new javax.swing.JPanel();
        PercentSelec = new javax.swing.JPanel();
        PosObs = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("graficos1.ventana.titulo")
        );
        setMinimumSize(new java.awt.Dimension(600, 450));
        setPreferredSize(new java.awt.Dimension(600, 600));

        botoneraGraficos.setLayout(new javax.swing.BoxLayout(botoneraGraficos, javax.swing.BoxLayout.Y_AXIS));

        UciHosSelecc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos1.titPanel.opcion"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
        UciHosSelecc.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        UciHosSelecc.setMaximumSize(new java.awt.Dimension(150, 80));
        UciHosSelecc.setMinimumSize(new java.awt.Dimension(100, 70));
        UciHosSelecc.setPreferredSize(new java.awt.Dimension(150, 80));
        UciHosSelecc.setLayout(new javax.swing.BoxLayout(UciHosSelecc, javax.swing.BoxLayout.Y_AXIS));

        hospital_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        hospital_RB.setText(bundle.getString("graficos1.grafico.rbutton.hospital"));
        hospital_RB.setName("1"); // NOI18N
        UciHosSelecc.add(hospital_RB);

        UCI_RB.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        UCI_RB.setSelected(true);
        UCI_RB.setText(bundle.getString("graficos1.grafico.rbutton.uci"));
        UCI_RB.setName("0"); // NOI18N
        UciHosSelecc.add(UCI_RB);

        botoneraGraficos.add(UciHosSelecc);

        grupoEdadSelecc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos1.titPanel.grupos"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
        grupoEdadSelecc.setMaximumSize(new java.awt.Dimension(150, 200));
        grupoEdadSelecc.setMinimumSize(new java.awt.Dimension(100, 190));
        grupoEdadSelecc.setPreferredSize(new java.awt.Dimension(150, 190));
        grupoEdadSelecc.setLayout(new javax.swing.BoxLayout(grupoEdadSelecc, javax.swing.BoxLayout.Y_AXIS));
        botoneraGraficos.add(grupoEdadSelecc);

        PercentSelec.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos1.titPanel.estadisticas"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
        PercentSelec.setMaximumSize(new java.awt.Dimension(150, 200));
        PercentSelec.setMinimumSize(new java.awt.Dimension(100, 190));
        PercentSelec.setName(""); // NOI18N
        PercentSelec.setPreferredSize(new java.awt.Dimension(150, 190));
        PercentSelec.setLayout(new javax.swing.BoxLayout(PercentSelec, javax.swing.BoxLayout.Y_AXIS));
        botoneraGraficos.add(PercentSelec);

        PosObs.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("graficos1.titPanel.positivos")
            , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Courier", 0, 12))); // NOI18N
    PosObs.setAlignmentX(0.0F);
    PosObs.setMaximumSize(new java.awt.Dimension(150, 60));
    PosObs.setMinimumSize(new java.awt.Dimension(100, 41));
    PosObs.setPreferredSize(new java.awt.Dimension(150, 50));
    PosObs.setLayout(new javax.swing.BoxLayout(PosObs, javax.swing.BoxLayout.Y_AXIS));
    botoneraGraficos.add(PosObs);

    getContentPane().add(botoneraGraficos, java.awt.BorderLayout.LINE_START);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PercentSelec;
    private javax.swing.JPanel PosObs;
    private javax.swing.JRadioButton UCI_RB;
    private javax.swing.JPanel UciHosSelecc;
    private javax.swing.JPanel botoneraGraficos;
    private javax.swing.JPanel grupoEdadSelecc;
    private javax.swing.JRadioButton hospital_RB;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent ae) {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        actualizaPintado(pintado);
        for (int p = 0; p < percents.length + 3; p++) {
                for (int ge = 0; ge < numGruEdad + 1; ge++) {
                        for (int s = 0; s < 2; s++) {
                            if (pintado[s][ge][p] != pintadoPrevio[s][ge][p]) {
                                
                                //Se ha activado o desactivado
                                if (pintado[s][ge][p]) {
                                //Pasa de inactivo a activo                                 
                                renderer.setSeriesLinesVisible(posicionSerie(s,p,ge), true);
                                renderer.setSeriesShapesVisible(posicionSerie(s,p,ge), true);
                                renderer.setSeriesVisibleInLegend(posicionSerie(s,p,ge), true);
                                    //setSerie(s,ge,p);
                                }else{
                                //Pasa de activo a inactivo
                                //Lo quitamos de la lista timeseriescollection
                                //dataset.removeSeries(series[s][ge][p]);
                                renderer.setSeriesLinesVisible(posicionSerie(s,p,ge), false);                                                            
                                renderer.setSeriesShapesVisible(posicionSerie(s,p,ge), false);
                                renderer.setSeriesVisibleInLegend(posicionSerie(s,p,ge), false);
                                }
                                pintadoPrevio[s][ge][p]=pintado[s][ge][p];
                            }
                        }
                }
            
        }
    }

   

  
}
