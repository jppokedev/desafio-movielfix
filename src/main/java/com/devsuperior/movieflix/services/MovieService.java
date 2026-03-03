package com.devsuperior.movieflix.services;


import com.devsuperior.movieflix.dto.MovieCardDTO;
import com.devsuperior.movieflix.dto.MovieDetailsDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.projections.MovieProjection;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.services.exceptions.ResourceNotFoundException;
import com.devsuperior.movieflix.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository repository;

    @Transactional(readOnly = true)
    public MovieDetailsDTO findById(Long id){
        Optional<Movie> obj = repository.findById(id);
        Movie entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new MovieDetailsDTO(entity, entity.getGenre());
    }

    @Transactional(readOnly = true)
    public Page<MovieCardDTO> findAllPaged(String name, Long genreId, Pageable pageable){

        Long searchGenreId = (genreId == null) ? 0L : genreId;
        String searchName = (name == null) ? "" : name.trim();

        assert name != null;
        Page<MovieProjection> page = repository.searchMovie(genreId, name.trim(), pageable);

        if (page.isEmpty()){
            return Page.empty(pageable);
        }

        List<Long> movieIds = page.map(MovieProjection::getId).toList();
        List<Movie> entities = repository.searchMoviesWithGenre(movieIds);

        List<Movie> sortedEntities = (List<Movie>) Utils.replace(page.getContent(), entities)
                .stream()
                .map(x -> (Movie) x)
                .toList();

        return new PageImpl<>(sortedEntities, pageable, page.getTotalElements()).map(MovieCardDTO::new);
    }
}
