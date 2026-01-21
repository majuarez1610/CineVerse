package com.platform.cineverse.service;

public interface IDataMapper {
    <T> T obtenerDatos(String json, Class<T> clase);
}
