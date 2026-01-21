package com.platform.cineverse.main;

import com.platform.cineverse.model.TvShowData;
import com.platform.cineverse.model.SeasonData;
import com.platform.cineverse.model.Episode;
import com.platform.cineverse.model.Serie;
import com.platform.cineverse.repository.TvShowRepository;
import com.platform.cineverse.service.APIClient;
import com.platform.cineverse.service.DataMapper;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private Scanner teclado = new Scanner(System.in);
    private APIClient consumoApi = new APIClient();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=56c3b32a";
    private DataMapper conversor = new DataMapper();
    private List<TvShowData> datosSeries = new ArrayList<>();
    private TvShowRepository repositorio;
    private List<Serie> series;

    public Main(TvShowRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    
                    ╔════════════════════════════════════════╗
                    ║        CINEVERSE - MENÚ PRINCIPAL      ║
                    ╠════════════════════════════════════════╣
                    ║  1 - Registrar Series                  ║
                    ║  2 - Registar Episodios                ║
                    ║  3 - Mostrar series buscadas           ║
                    ║                                        ║
                    ║  0 - Salir                             ║
                    ╚════════════════════════════════════════╝
                    
                    Selecciona una opción: """;
            System.out.print(menu);

            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        buscarSerieWeb();
                        break;
                    case 2:
                        buscarEpisodioPorSerie();
                        break;
                    case 3:
                        mostrarSeriesBuscadas();
                        break;
                    case 0:
                        System.out.println("\n🎬 Cerrando la aplicación... ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("\n❌ Opción inválida. Por favor, selecciona una opción del menú.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n❌ Error: Debes ingresar un número.");
                teclado.nextLine(); // Limpiar el buffer
                opcion = -1; // Mantener el bucle activo
            } catch (Exception e) {
                System.out.println("\n❌ Ocurrió un error inesperado: " + e.getMessage());
                opcion = -1; // Mantener el bucle activo
            }
        }
    }

    private TvShowData getDatosSerie() {
        System.out.println("\n🔍 BÚSQUEDA DE SERIES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.print("Escribe el nombre de la serie: ");
        var nombreSerie = teclado.nextLine();

        if (nombreSerie == null || nombreSerie.trim().isEmpty()) {
            System.out.println("\n❌ El nombre de la serie no puede estar vacío.");
            return null;
        }

        try {
            System.out.println("\n⏳ Buscando '" + nombreSerie + "'...");
            var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);

            if (json == null || json.trim().isEmpty()) {
                System.out.println("\n❌ No se recibió respuesta de la API.");
                return null;
            }

            System.out.println("\n📡 Respuesta de la API:");
            System.out.println(json);

            TvShowData datos = conversor.obtenerDatos(json, TvShowData.class);

            // Verificar si la API devolvió un error (por ejemplo, serie no encontrada)
            if (datos == null || datos.titulo() == null || datos.titulo().equalsIgnoreCase("N/A")) {
                System.out.println("\n❌ No se encontró la serie '" + nombreSerie + "'. Verifica el nombre e intenta de nuevo.");
                return null;
            }

            return datos;
        } catch (Exception e) {
            System.out.println("\n❌ Error al buscar la serie: " + e.getMessage());
            return null;
        }
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();

        if (series == null || series.isEmpty()) {
            System.out.println("\n⚠️ Primero debes registrar al menos una serie.");
            return;
        }

        System.out.println("\n📺 BÚSQUEDA DE EPISODIOS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.print("Escribe el nombre de la serie: ");
        var nombreSerie = teclado.nextLine();

        if (nombreSerie == null || nombreSerie.trim().isEmpty()) {
            System.out.println("\n❌ El nombre de la serie no puede estar vacío.");
            return;
        }

        try {
            Optional<Serie> serie = series.stream()
                    .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                    .findFirst();

            if(serie.isPresent()){
                var serieEncontrada = serie.get();
                System.out.println("\n✅ Serie encontrada: " + serieEncontrada.getTitulo());
                System.out.println("⏳ Obteniendo información de " + serieEncontrada.getTotalTemporadas() + " temporadas...\n");

                List<SeasonData> temporadas = new ArrayList<>();

                for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                    try {
                        System.out.println("📥 Descargando temporada " + i + "...");
                        var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);

                        if (json != null && !json.trim().isEmpty()) {
                            SeasonData datosTemporada = conversor.obtenerDatos(json, SeasonData.class);
                            if (datosTemporada != null) {
                                temporadas.add(datosTemporada);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Error al obtener la temporada " + i + ": " + e.getMessage());
                    }
                }

                if (temporadas.isEmpty()) {
                    System.out.println("\n⚠️ No se pudieron obtener episodios para esta serie.");
                    return;
                }

                System.out.println("\n📋 TEMPORADAS OBTENIDAS:");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                temporadas.forEach(System.out::println);

                List<Episode> episodios = temporadas.stream()
                        .flatMap(d -> d.episodios().stream()
                                .map(e -> new Episode(d.numero(), e)))
                        .collect(Collectors.toList());

                serieEncontrada.setEpisodios(episodios);
                repositorio.save(serieEncontrada);

                System.out.println("\n💾 Episodios guardados exitosamente en la base de datos.");
            } else {
                System.out.println("\n❌ No se encontró ninguna serie con ese nombre.");
            }
        } catch (Exception e) {
            System.out.println("\n❌ Error al buscar episodios: " + e.getMessage());
        }
    }

    private void buscarSerieWeb() {
        try {
            TvShowData datos = getDatosSerie();

            if (datos == null) {
                System.out.println("\n⚠️ No se pudo obtener la información de la serie. Intenta de nuevo.");
                return;
            }

            Serie serie = new Serie(datos);
            repositorio.save(serie);

            System.out.println("\n✅ SERIE GUARDADA:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println(datos);
            System.out.println("\n💾 La serie ha sido guardada en la base de datos.");
        } catch (Exception e) {
            System.out.println("\n❌ Error al guardar la serie: " + e.getMessage());
            System.out.println("Por favor, intenta de nuevo.");
        }
    }

    private void mostrarSeriesBuscadas() {
        try {
            series = repositorio.findAll();

            if(series == null || series.isEmpty()) {
                System.out.println("\n📭 No hay series guardadas en la base de datos.");
                return;
            }

            System.out.println("\n📚 SERIES GUARDADAS:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            series.stream()
                    .sorted(Comparator.comparing(Serie::getGenero))
                    .forEach(System.out::println);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Total de series: " + series.size());
        } catch (Exception e) {
            System.out.println("\n❌ Error al mostrar las series: " + e.getMessage());
        }
    }
}