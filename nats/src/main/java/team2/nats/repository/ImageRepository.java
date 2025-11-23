package team2.nats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import team2.nats.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
