package com.devsuperior.movieflix.repositories;

import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.projections.MovieProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>{

    @Query(nativeQuery = true, value = """
        SELECT DISTINCT tb_movie.id, tb_movie.title
        FROM tb_movie
        WHERE (:genreId = 0 OR :genreId IS NULL OR tb_movie.genre_id = :genreId)
        AND (LOWER(tb_movie.title) LIKE LOWER(CONCAT('%',:name,'%')))
        ORDER BY tb_movie.title
        """,
        countQuery = """
        SELECT COUNT(DISTINCT tb_movie.id)
        FROM tb_movie
        WHERE (:genreId = 0 OR :genreId IS NULL OR tb_movie.genre_id = :genreId)
        AND (LOWER(tb_movie.title) LIKE LOWER(CONCAT('%',:name,'%')))
        """)
    Page<MovieProjection> searchMovie(@Param("genreId")Long genreId, @Param("name") String name, Pageable pageable);

    @Query("SELECT obj FROM Movie obj JOIN FETCH obj.genre WHERE obj.id IN :movieIds")
    List<Movie> searchMoviesWithGenre(List<Long> movieIds);

}
