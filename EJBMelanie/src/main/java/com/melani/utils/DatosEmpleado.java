package com.melani.utils;

public class DatosEmpleado {
    private Integer id;
    private String apellido;
    private String nombre;
    private String nombreUsuario;
    private String password;
    private String passwordre;
    private String email;
    private Short idTipoDocumento;
    private Integer numeroDocumento;
    private String salario;
    private String salarioxhora;
    private Short idGenero;
    private String observaciones;
    private String tipoEmpleado;

    public String getPasswordre() {
        return passwordre;
    }

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(String tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public void setPasswordre(String passwordre) {
        this.passwordre = passwordre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Short getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Short idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public Integer getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Integer numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public Short getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Short idGenero) {
        this.idGenero = idGenero;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getSalarioxhora() {
        return salarioxhora;
    }

    public void setSalarioxhora(String salarioxhora) {
        this.salarioxhora = salarioxhora;
    }
}