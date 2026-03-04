package com.devsuperior.movieflix.services;

import com.devsuperior.movieflix.dto.ReviewDTO;
import com.devsuperior.movieflix.entities.Movie;
import com.devsuperior.movieflix.entities.Review;
import com.devsuperior.movieflix.repositories.MovieRepository;
import com.devsuperior.movieflix.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository repository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public List<ReviewDTO> findByMovie(Long movieId){
        List<Review> list = repository.findReviewsByMovieId(movieId);
        return list.stream().map(ReviewDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO insert(ReviewDTO dto){
        Review entity = new Review();
        entity.setText(dto.getText());
        entity.setMovie(movieRepository.getReferenceById(dto.getMovieId()));
        entity.setUser(authService.authenticated());
        entity = repository.save(entity);
        return new ReviewDTO(entity);
    }

}
