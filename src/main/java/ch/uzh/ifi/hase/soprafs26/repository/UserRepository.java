package ch.uzh.ifi.hase.soprafs26.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs26.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	User findByToken(String token);

	@Query("SELECT u FROM User u WHERE u.reactionHighScore IS NOT NULL ORDER BY u.reactionHighScore ASC")
    List<User> findTopReactionTimeScores(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.typingHighScore IS NOT NULL ORDER BY u.typingHighScore DESC")
    List<User> findTopTypingSpeedScores(Pageable pageable);
}
