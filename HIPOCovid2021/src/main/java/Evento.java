/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.time.LocalDate;

/**
 *
 * @author pmateo
 */
public class Evento implements Comparable<Evento>{

    /**
     * @return the grupo
     */
    public int getGrupo() {
        return grupo;
    }

   
    static final int Entrada = 1;
    static final int EntradaHospital = 2;
    static final int EntradaUci =3 ;
    static final int SalidaUci =4;
    static final int SalidaRecuperadoTrasHospitalSinUCI=5;
    static final int SalidaRecuperadoTrasHospitalConUCI=6;
    static final int SalidaMuertoEnHospital=7;
    static final int SalidaMuertoTrasHospital=8;
    static final int SalidaMuertoSinHospital=9;
    static final int SalidaRecuperadoSinHospital=10;
    static String[] texto = {
    "Posicion ignorada",
    "Paciente da positivo",
    "Paciente ingresa en hospital",
    "Paciente ingresa en UCI",
    "Paciente sale de la UCI",
    "Paciente sale del hospital recuperado sin pasar por la UCI",
    "Paciente sale del hospital recuperado habiendo pasado por la  UCI",
    "Paciente fallece en el hospital",
    "Paciente fallece después de abandonar el hospital",
    "Paciente que no ha llegado a ingresar fallece"
    };

    private String codigoUsuario;
    private int tipo_de_usuario;
    private int tipoEvento;
    private int grupo;
    private LocalDate instante;
    
    
    Evento(String cod,int tipo_usuario,int tipo, LocalDate inst,int ge){
        tipo_de_usuario=tipo_usuario;
        codigoUsuario=cod;
        tipoEvento=tipo;
        instante=inst;
        grupo=ge;
    }
    
    
     /**
     * @return the tipo_de_usuario
     */
    public int getTipo_de_usuario() {
        return tipo_de_usuario;
    }

    /**
     * @return the codigoUsuario
     */
    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    /**
     * @return the tipoEvento
     */
    public int getTipoEvento() {
        return tipoEvento;
    }

    /**
     * @return the instante
     */
    public LocalDate getInstante() {
        return instante;
    }
    
    
    @Override
    public int compareTo(Evento t) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.        
        return this.getInstante().compareTo(t.getInstante());
    }
        
}
