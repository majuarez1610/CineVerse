package com.platform.cineverse.dto;

import com.platform.cineverse.model.Category;

public record SerieDTO(Long id,
         String titulo,
         Integer totalTemporadas,
         Double evaluacion,
         String poster,
         Category genero,
         String actores,
         String sinopsis){
}
