/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.LocalDate;

/**
 *
 * @author pmateo
 * Esta clase contendrá la información completa de cada paciente, 
 * Se genera con la información 
 */
public class Usuario implements Comparable<Usuario>{

    /**
     * @return the grupo_G1
     */
    public int getGrupo_G1() {
        return grupo_G1;
    }

    /**
     * @param grupo_G1 the grupo_G1 to set
     */
    public void setGrupo_G1(int grupo_G1) {
        this.grupo_G1 = grupo_G1;
    }

    static final int fallecido=0;
    static final int recuperado=1;
    static final int masculino=0;
    static final int femenino=1;
    static final int paciente_nuevo=99;
    static final int paciente_positivo=1;
    static final int paciente_hospitalizado=2;
    static final int paciente_en_UCI=3;
    static final int paciente_tras_UCI=4;
    
    
    
    //Fechas y recorrido
    private int tipo_de_paciente=0;
    private String codigoUsuario=null;
    private LocalDate fecha_referencia=null;
    private boolean ingreso_hospital=false;
    private boolean ingreso_UCI=false;
    private LocalDate fecha_ingreso_hospital=null;
    private LocalDate fecha_de_alta_hospital=null;
    private LocalDate fecha_ingreso_UCI=null;
    private LocalDate fecha_alta_UCI=null;
    private LocalDate fecha_fallecimiento=null;
    private int  dias_hospital=-1;
    private int dias_UCI=-1;
    
    private int resultado=-1; //recuperado, muerto
    //Caracteristicas (aquí todo lo que proporcione información para o de la 
    //simulacion    
    private LocalDate fecha_de_nacimiento=null;
    private int edad=-1;
    private int grupo_G1=-1;
    private int grupo_G2=-1;
    private int sexo=-1;
    
    void clear() {
        tipo_de_paciente = 0;
        codigoUsuario = null;
        fecha_referencia = null;
        setIngreso_hospital(false);
        setIngreso_UCI(false);
        fecha_ingreso_hospital = null;
        fecha_de_alta_hospital = null;
        fecha_ingreso_UCI = null;
        fecha_alta_UCI = null;
        fecha_fallecimiento = null;
        dias_hospital = -1;
        dias_UCI = -1;
        resultado = -1;
        fecha_de_nacimiento = null;
        edad = -1;
        grupo_G1=-1;
        grupo_G2 = -1;        
        sexo = -1;
    }

    void resetSimulData(){
        setIngreso_hospital(false);
        setIngreso_UCI(false);
        fecha_ingreso_hospital = null;
        fecha_de_alta_hospital = null;
        fecha_ingreso_UCI = null;
        fecha_alta_UCI = null;
        fecha_fallecimiento = null;
        dias_hospital = -1;
        dias_UCI = -1;
        resultado = -1;
        fecha_de_nacimiento = null;
        sexo = -1;    
    }
    
        /**
     * @return the tipo_de_paciente
     */
    public int getTipo_de_paciente() {
        return tipo_de_paciente;
    }

    /**
     * @param tipo_de_paciente the tipo_de_paciente to set
     */
    public void setTipo_de_paciente(int tipo_de_paciente) {
        this.tipo_de_paciente = tipo_de_paciente;
    }

      /**
     * @return the fecha_alta_UCI
     */
    public LocalDate getFecha_alta_UCI() {
        return fecha_alta_UCI;
    }

    /**
     * @param fecha_alta_UCI the fecha_alta_UCI to set
     */
    public void setFecha_alta_UCI(LocalDate fecha_alta_UCI) {
        this.fecha_alta_UCI = fecha_alta_UCI;
    }

    /**
     * @return the codigoUsuario
     */
    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    /**
     * @param codigoUsuario the codigoUsuario to set
     */
    public void setCodigoUsuario(String codigoUsuario) {
        this.codigoUsuario = codigoUsuario;
    }

    /**
     * @return the fecha_referencia
     */
    public LocalDate getFecha_referencia() {
        return fecha_referencia;
    }

    /**
     * @param fecha_referencia the fecha_referencia to set
     */
    public void setFecha_referencia(LocalDate fecha_referencia) {
        this.fecha_referencia = fecha_referencia;
    }

    /**
     * @return the ingreso_hospital
     */
    public boolean isIngreso_hospital() {
        return ingreso_hospital;
    }

    /**
     * @param ingreso_hospital the ingreso_hospital to set
     */
    public void setIngreso_hospital(boolean ingreso_hospital) {
        this.ingreso_hospital = ingreso_hospital;
    }

    /**
     * @return the fecha_ingreso_hospital
     */
    public LocalDate getFecha_ingreso_hospital() {
        return fecha_ingreso_hospital;
    }

    /**
     * @param fecha_ingreso_hospital the fecha_ingreso_hospital to set
     */
    public void setFecha_ingreso_hospital(LocalDate fecha_ingreso_hospital) {
        this.fecha_ingreso_hospital = fecha_ingreso_hospital;
    }

    /**
     * @return the dias_hospital
     */
    public int getDias_hospital() {
        return dias_hospital;
    }

    /**
     * @param dias_hospital the dias_hospital to set
     */
    public void setDias_hospital(int dias_hospital) {
        this.dias_hospital = dias_hospital;
    }

    /**
     * @return the ingreso_UCI
     */
    public boolean isIngreso_UCI() {
        return ingreso_UCI;
    }

    /**
     * @param ingreso_UCI the ingreso_UCI to set
     */
    public void setIngreso_UCI(boolean ingreso_UCI) {
        this.ingreso_UCI = ingreso_UCI;
    }

    /**
     * @return the fecha_ingreso_UCI
     */
    public LocalDate getFecha_ingreso_UCI() {
        return fecha_ingreso_UCI;
    }

    /**
     * @param fecha_ingreso_UCI the fecha_ingreso_UCI to set
     */
    public void setFecha_ingreso_UCI(LocalDate fecha_ingreso_UCI) {
        this.fecha_ingreso_UCI = fecha_ingreso_UCI;
    }

    /**
     * @return the dias_UCI
     */
    public int getDias_UCI() {
        return dias_UCI;
    }

    /**
     * @param dias_UCI the dias_UCI to set
     */
    public void setDias_UCI(int dias_UCI) {
        this.dias_UCI = dias_UCI;
    }

    /**
     * @return the fecha_de_alta
     */
    public LocalDate getFecha_de_alta_hospital() {
        return fecha_de_alta_hospital;
    }

    /**
     * @param fecha_de_alta the fecha_de_alta to set
     */
    public void setFecha_de_alta_hospital(LocalDate fecha_de_alta) {
        this.fecha_de_alta_hospital = fecha_de_alta;
    }

    /**
     * @return the resultado
     */
    public int getResultado() {
        return resultado;
    }

    /**
     * @param resultado the resultado to set
     */
    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    /**
     * @return the fecha_de_nacimiento
     */
    public LocalDate getFecha_de_nacimiento() {
        return fecha_de_nacimiento;
    }

    /**
     * @param fecha_de_nacimiento the fecha_de_nacimiento to set
     */
    public void setFecha_de_nacimiento(LocalDate fecha_de_nacimiento) {
        this.fecha_de_nacimiento = fecha_de_nacimiento;
    }

    /**
     * @return the edad
     */
    public int getEdad() {
        return edad;
    }

    /**
     * @param edad the edad to set
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * @return the sexo
     */
    public int getSexo() {
        return sexo;
    }

    /**
     * @param sexo the sexo to set
     */
    public void setSexo(int sexo) {
        this.sexo = sexo;
    }

    /**
     * @return the fecha_fallecimiento
     */
    public LocalDate getFecha_fallecimiento() {
        return fecha_fallecimiento;
    }

    /**
     * @param fecha_fallecimiento the fecha_fallecimiento to set
     */
    public void setFecha_fallecimiento(LocalDate fecha_fallecimiento) {
        this.fecha_fallecimiento = fecha_fallecimiento;
    }
    
    int generaSexo() {
        if (Math.random() < 0.5) {
            return this.masculino;
        } else {
            return this.femenino;
        }
    }


 /**
     * @return the grupo_G2
     */
    public int getGrupo_G2() {
        return grupo_G2;
    }

    /**
     * @param grupo_G2 the grupo_G2 to set
     */
    public void setGrupo_G2(int grupo_G2) {
        this.grupo_G2 = grupo_G2;
    }


    
    @Override
    public int compareTo(Usuario t) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        
        return this.getFecha_referencia().compareTo(t.getFecha_referencia());
    }
    
}
