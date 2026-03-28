package ch.uzh.ifi.hase.soprafs26.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;

@Repository("friendRequestRepository")
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List <FriendRequest> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
    List <FriendRequest> findByReceiverIdAndStatus(Long receiverId, FriendRequestStatus status);
    FriendRequest findBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
